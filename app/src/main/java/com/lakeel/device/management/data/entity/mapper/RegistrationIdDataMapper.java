package com.lakeel.device.management.data.entity.mapper;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.lakeel.device.management.data.entity.RegistrationIdEntity;

public final class RegistrationIdDataMapper {

    private FirebaseUser myUser = FirebaseAuth.getInstance().getCurrentUser();

    public RegistrationIdEntity map(String registrationId, String deviceId) {
        RegistrationIdEntity registrationIdEntity = new RegistrationIdEntity();
        registrationIdEntity.registrationId = registrationId;
        registrationIdEntity.deviceId = deviceId;
        registrationIdEntity.userId = myUser.getUid();
        return registrationIdEntity;
    }
}
