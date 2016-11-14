package com.lakeel.device.management.data.repository;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.lakeel.device.management.data.exception.DataStoreException;
import com.lakeel.device.management.data.entity.BeaconDistanceEntity;
import com.lakeel.device.management.data.entity.BeaconFoundEntity;
import com.lakeel.device.management.presentation.intent.data.BeaconDistanceChangedIntentData;
import com.lakeel.device.management.presentation.intent.data.BeaconFoundIntentData;
import com.lakeel.device.management.data.entity.mapper.BeaconDistanceChangedDataMapper;
import com.lakeel.device.management.data.entity.mapper.BeaconFoundDataMapper;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import rx.Completable;

public final class BeaconRepositoryImpl implements BeaconRepository {

    private static final String BEACON_REFERENCE = "beacon";

    private DatabaseReference mReference;

    private BeaconFoundDataMapper mBeaconFoundDataMapper = new BeaconFoundDataMapper();

    private BeaconDistanceChangedDataMapper mBeaconDistanceChangedDataMapper = new BeaconDistanceChangedDataMapper();

    @Inject
    public BeaconRepositoryImpl() {
        mReference = FirebaseDatabase.getInstance().getReference(BEACON_REFERENCE);
    }

    @Override
    public Completable saveBeaconFound(@NonNull BeaconFoundIntentData beaconFoundIntentData, @NonNull String deviceId, @NonNull String registrationId) {

        // 注意:
        //
        // Firebase の Task のコールバックはメインスレッドで呼ばれる。
        // 後続の Rx の処理がメインスレッドで動作してしまうため、要注意。
        //
        return Completable.create(subscriber -> {
            BeaconFoundEntity beaconFoundEntity = mBeaconFoundDataMapper.map(beaconFoundIntentData, deviceId, registrationId);

            Task task = mReference.push().setValue(beaconFoundEntity)
                    .addOnSuccessListener(aVoid -> subscriber.onCompleted())
                    .addOnFailureListener(subscriber::onError);

            Exception exception = task.getException();
            if (exception != null) {
                throw new DataStoreException(exception);
            }
        });
    }


    @Override
    public Completable saveBeaconDistanceChanged(@NonNull BeaconDistanceChangedIntentData beaconDistanceChangedIntentData, @NonNull String deviceId, @NonNull String registrationId) {
        return Completable.create(subscriber -> {
            BeaconDistanceEntity beaconDistanceEntity = mBeaconDistanceChangedDataMapper.map(beaconDistanceChangedIntentData, deviceId, registrationId);

            Task task = mReference.push().setValue(beaconDistanceEntity)
                    .addOnSuccessListener(aVoid -> subscriber.onCompleted())
                    .addOnFailureListener(subscriber::onError);

            Exception exception = task.getException();
            if (exception != null) {
                throw new DataStoreException(exception);
            }
        });
    }
}
