package com.lakeel.device.management.presentation.receiver;


import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.BleSignal;
import com.google.android.gms.nearby.messages.Distance;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.lakeel.device.management.data.exception.DataStoreException;
import com.lakeel.device.management.data.repository.EncryptedPreferences;
import com.lakeel.device.management.data.entity.PreferenceResult;
import com.lakeel.device.management.R;
import com.lakeel.device.management.data.entity.BeaconFoundEntity;
import com.lakeel.device.management.presentation.intent.BundleKey;
import com.lakeel.device.management.presentation.intent.IntentKey;
import com.lakeel.device.management.presentation.intent.data.BeaconDistanceChangedIntentData;
import com.lakeel.device.management.presentation.intent.data.BeaconFoundIntentData;
import com.lakeel.device.management.data.entity.mapper.BeaconFoundDataMapper;
import com.lakeel.device.management.presentation.view.activity.MainActivity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import java.util.UUID;

import rx.Completable;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public final class BeaconMessageReceiver extends BroadcastReceiver {

    private static final Logger LOGGER = LoggerFactory.getLogger(BeaconMessageReceiver.class);

    private static final String BEACONS_REFERENCE = "beacons";

    private BeaconFoundDataMapper mBeaconFoundDataMapper = new BeaconFoundDataMapper();

    private EncryptedPreferences mEncryptedPreferences;

    @Override
    public void onReceive(Context context, Intent intent) {
        LOGGER.debug("onReceive");

        mEncryptedPreferences = new EncryptedPreferences(context);

        // ここでそのままサーバに送信するか、ローカル通知を出すか、
        // アプリの利用方法により異なる。

        Nearby.Messages.handleIntent(intent, new MessageListener() {

            @Override
            public void onFound(Message message) {
                LOGGER.debug("Beacon found");

                // 一度、onFound() が呼ばれると、onLost() が呼ばれない限り、
                // また呼ばれない。

                BeaconFoundIntentData beaconFoundIntentData = new BeaconFoundIntentData(message);

                Observable<String> o1 = findDeviceId();
                Observable<String> o2 = findRegistrationId();

                Observable.zip(o1, o2, PreferenceResult::new)
                        .flatMap(preferenceResult -> saveBeaconFound(beaconFoundIntentData, preferenceResult.mDeviceId, preferenceResult.mRegistrationId).toObservable())
                        .subscribeOn(Schedulers.io())
                        .toCompletable()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(e -> LOGGER.error("Failed to save beacon event", e),
                                () -> LOGGER.info("Succeeded to save beacon event"));

                Bundle bundle = new Bundle();
                bundle.putSerializable(BundleKey.BEACON_FOUND.name(), beaconFoundIntentData);

                Intent intent = getIntent(context);
                intent.putExtra(IntentKey.BEACON_FOUND.name(), bundle);

                String notificationMessage = context.getResources().getString(R.string.notification_message_beacon_found);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

                notifyNotification(context, pendingIntent, notificationMessage);
            }

            @Override
            public void onLost(Message message) {
                LOGGER.debug("Beacon lost");

                Bundle bundle = new Bundle();

                Intent intent = getIntent(context);
                intent.putExtra(IntentKey.BEACON_LOST.name(), bundle);

                String notificationMessage = context.getResources().getString(R.string.notification_message_beacon_lost);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

                notifyNotification(context, pendingIntent, notificationMessage);
            }

            @Override
            public void onDistanceChanged(Message message, Distance distance) {
                LOGGER.debug("Beacon distance changed");

                BeaconDistanceChangedIntentData beaconDistanceChangedIntentData = new BeaconDistanceChangedIntentData(message, distance);

                Bundle bundle = new Bundle();
                bundle.putSerializable(BundleKey.BEACON_DISTANCE.name(), beaconDistanceChangedIntentData);

                Intent intent = getIntent(context);
                intent.putExtra(IntentKey.BEACON_DISTANCE.name(), bundle);

                String notificationMessage = context.getResources().getString(R.string.notification_message_beacon_distance_changed);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

                notifyNotification(context, pendingIntent, notificationMessage);
            }

            @Override
            public void onBleSignalChanged(Message message, BleSignal bleSignal) {
                LOGGER.debug("Ble signal changed");

                Bundle bundle = new Bundle();

                Intent intent = getIntent(context);
                intent.putExtra(IntentKey.BLE_SIGNAL.name(), bundle);


                String notificationMessage = context.getResources().getString(R.string.notification_message_ble_signal_changed);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

                notifyNotification(context, pendingIntent, notificationMessage);
            }
        });
    }

    private Intent getIntent(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return intent;
    }

    private Notification createNotification(Context context, PendingIntent pendingIntent, String message) {
        String title = context.getResources().getString(R.string.app_name);

        // setTicker を設定しないと通知が表示されない模様。
        Notification notification = new NotificationCompat.Builder(context)
                .setContentTitle(title)
                .setTicker(message)
                .setContentText(message)
                .setSmallIcon(R.mipmap.ic_nearby_white)
                .setVibrate(new long[]{0, 200, 100, 200, 100, 200})
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .build();

        return notification;
    }

    private void notifyNotification(Context context, PendingIntent pendingIntent, String message) {
        int uuid = UUID.randomUUID().hashCode();

        Notification notification = createNotification(context, pendingIntent, message);
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        manager.notify(uuid, notification);
    }

    public Observable<String> findDeviceId() {
        return Observable.create((Observable.OnSubscribe<String>) subscriber -> {
            String deviceId = mEncryptedPreferences.getDeviceId();
            subscriber.onNext(deviceId);
            subscriber.onCompleted();
        });
    }

    public Observable<String> findRegistrationId() {
        return Observable.create(subscriber -> {
            String registrationId = mEncryptedPreferences.getRegistrationId();
            subscriber.onNext(registrationId);
            subscriber.onCompleted();
        });
    }

    Completable saveBeaconFound(BeaconFoundIntentData beaconFoundIntentData, String deviceId, String registrationId) {
        return Completable.create(subscriber -> {
            BeaconFoundEntity beaconFoundEntity = mBeaconFoundDataMapper.map(beaconFoundIntentData, deviceId, registrationId);

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(BEACONS_REFERENCE);
            Task task = databaseReference.push().setValue(beaconFoundEntity)
                    .addOnSuccessListener(aVoid -> subscriber.onCompleted())
                    .addOnFailureListener(subscriber::onError);

            Exception exception = task.getException();
            if (exception != null) {
                throw new DataStoreException(exception);
            }
        });
    }
}
