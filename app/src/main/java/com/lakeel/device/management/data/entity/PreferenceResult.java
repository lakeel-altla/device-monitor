package com.lakeel.device.management.data.entity;

public final class PreferenceResult {

    public String mDeviceId;

    public String mRegistrationId;

    public PreferenceResult(String deviceId, String registrationId) {
        mDeviceId = deviceId;
        mRegistrationId = registrationId;
    }
}
