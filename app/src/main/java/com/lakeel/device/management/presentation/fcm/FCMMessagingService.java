package com.lakeel.device.management.presentation.fcm;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import com.lakeel.device.management.R;
import com.lakeel.device.management.presentation.view.activity.MainActivity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import java.util.UUID;


public class FCMMessagingService extends FirebaseMessagingService {

    private static final Logger LOG = LoggerFactory.getLogger(FCMMessagingService.class);

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        // Notification ペイロードを取得。
        RemoteMessage.Notification notification = remoteMessage.getNotification();

        String title = notification.getTitle();
        String message = notification.getBody();

        Intent intent = getIntent(this);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        notifyNotification(this, pendingIntent, title, message);
    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
        LOG.debug("This message has been deleted");
    }

    @Override
    public void onSendError(String msgId, Exception e) {
        super.onSendError(msgId, e);
        LOG.error("GCM has failed to send a push notification", e);
    }

    private Intent getIntent(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return intent;
    }

    private void notifyNotification(Context context, PendingIntent pendingIntent, String title, String message) {
        int uuid = UUID.randomUUID().hashCode();

        Notification notification = createNotification(context, pendingIntent, title, message);
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        manager.notify(uuid, notification);
    }

    private Notification createNotification(Context context, PendingIntent pendingIntent, String title, String message) {
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
}
