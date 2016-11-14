package com.lakeel.device.management.data.entity.mapper;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.lakeel.device.management.data.State;
import com.lakeel.device.management.data.Type;
import com.lakeel.device.management.data.entity.BeaconFoundEntity;
import com.lakeel.device.management.presentation.intent.data.BeaconFoundIntentData;

public final class BeaconFoundDataMapper {

    private FirebaseUser myUser = FirebaseAuth.getInstance().getCurrentUser();

    public BeaconFoundEntity map(BeaconFoundIntentData beaconFoundIntentData, String deviceId, String registrationId) {
        BeaconFoundEntity beaconFoundEntity = new BeaconFoundEntity();
        beaconFoundEntity.beaconId = beaconFoundIntentData.mBeaconId;
        beaconFoundEntity.deviceId = deviceId;
        beaconFoundEntity.registrationId = registrationId;
        beaconFoundEntity.status =State.WAIT.getValue();
        beaconFoundEntity.timestamp = beaconFoundIntentData.mTimestamp;
        beaconFoundEntity.type =  Type.FOUND.getValue();
        beaconFoundEntity.uid = myUser.getUid();

        return beaconFoundEntity;
    }
}
