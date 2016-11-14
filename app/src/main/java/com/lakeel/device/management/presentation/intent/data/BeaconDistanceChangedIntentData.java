package com.lakeel.device.management.presentation.intent.data;

import com.google.android.gms.nearby.messages.Distance;
import com.google.android.gms.nearby.messages.Message;

import java.io.Serializable;

public final class BeaconDistanceChangedIntentData implements Serializable {

    public final String mBeaconId;

    public final long mTimestamp;

    public final double mMeters;

    public final int mAccuracy;

    public BeaconDistanceChangedIntentData(Message message, Distance distance) {
        mBeaconId = new String(message.getContent());
        mTimestamp = System.currentTimeMillis();
        mMeters = distance.getMeters();
        mAccuracy = distance.getAccuracy();
    }
}
