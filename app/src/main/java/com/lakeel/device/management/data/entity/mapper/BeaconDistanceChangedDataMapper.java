package com.lakeel.device.management.data.entity.mapper;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.lakeel.device.management.data.State;
import com.lakeel.device.management.data.Type;
import com.lakeel.device.management.data.entity.BeaconDistanceEntity;
import com.lakeel.device.management.presentation.intent.data.BeaconDistanceChangedIntentData;

public final class BeaconDistanceChangedDataMapper {

    private FirebaseUser myUser = FirebaseAuth.getInstance().getCurrentUser();

    public BeaconDistanceEntity map(BeaconDistanceChangedIntentData beaconDistanceChangedIntentData, String deviceId, String registrationId) {
        BeaconDistanceEntity beaconDistanceEntity = new BeaconDistanceEntity();
        beaconDistanceEntity.uid = myUser.getUid();
        beaconDistanceEntity.beaconId = beaconDistanceChangedIntentData.mBeaconId;
        beaconDistanceEntity.deviceId = deviceId;
        beaconDistanceEntity.registrationId = registrationId;
        beaconDistanceEntity.type = Type.DISTANCE.getValue();
        beaconDistanceEntity.status = State.WAIT.getValue();
        beaconDistanceEntity.meters = beaconDistanceChangedIntentData.mMeters;
        beaconDistanceEntity.accuracy = beaconDistanceChangedIntentData.mAccuracy;
        beaconDistanceEntity.timestamp = beaconDistanceChangedIntentData.mTimestamp;

        return beaconDistanceEntity;
    }
}
