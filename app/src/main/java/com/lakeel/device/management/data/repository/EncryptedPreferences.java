package com.lakeel.device.management.data.repository;

import com.orhanobut.hawk.Hawk;
import com.orhanobut.hawk.HawkBuilder;
import com.orhanobut.hawk.LogLevel;

import android.content.Context;

public final class EncryptedPreferences {
    
    private static final String DEVICE_ID_KEY = "deviceId";

    private static final String REGISTRATION_ID_KEY = "registrationId";

    public EncryptedPreferences(Context context) {
        Hawk.init(context)
                .setEncryptionMethod(HawkBuilder.EncryptionMethod.MEDIUM)
                .setStorage(HawkBuilder.newSharedPrefStorage(context))
                .setLogLevel(LogLevel.FULL)
                .build();
    }

    public void saveDeviceId(String deviceId) {
        setString(DEVICE_ID_KEY, deviceId);
    }

    public String getDeviceId() {
        return getString(DEVICE_ID_KEY);
    }

    public void saveRegistrationId(String registrationId) {
        setString(REGISTRATION_ID_KEY, registrationId);
    }

    public String getRegistrationId() {
        return getString(REGISTRATION_ID_KEY);
    }

    private void setString(String key, String value) {
        // AES で暗号化。
        Hawk.put(key, value);
    }

    private String getString(String key) {
        return Hawk.get(key);
    }
}
