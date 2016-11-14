package com.lakeel.device.management.data.repository;

import rx.Completable;
import rx.Observable;

public interface DeviceRepository {

    Observable<String> findDeviceId();

    Completable saveDeviceId(String deviceId);
}
