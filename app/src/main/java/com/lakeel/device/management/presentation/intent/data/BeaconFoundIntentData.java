package com.lakeel.device.management.presentation.intent.data;

import com.google.android.gms.nearby.messages.Message;

import java.io.Serializable;

public class BeaconFoundIntentData implements Serializable {

    public final String mBeaconId;

    public final long mTimestamp;

    public BeaconFoundIntentData(Message message) {
        mBeaconId = new String(message.getContent());
        mTimestamp = System.currentTimeMillis();
    }
}
