package com.lakeel.device.management.data.repository;

import javax.inject.Inject;

import rx.Completable;
import rx.Observable;

public class DeviceRepositoryImpl implements DeviceRepository {

    private EncryptedPreferences mEncryptedPreferences;

    @Inject
    public DeviceRepositoryImpl(EncryptedPreferences encryptedPreferences) {
        mEncryptedPreferences = encryptedPreferences;
    }

    @Override
    public Observable<String> findDeviceId() {
        return Observable.create((Observable.OnSubscribe<String>) subscriber -> {
            String deviceId = mEncryptedPreferences.getDeviceId();
            subscriber.onNext(deviceId);
            subscriber.onCompleted();
        });
    }

    @Override
    public Completable saveDeviceId(String deviceId) {
        return Completable.create(subscriber -> {
            mEncryptedPreferences.saveDeviceId(deviceId);
            subscriber.onCompleted();
        });
    }
}
