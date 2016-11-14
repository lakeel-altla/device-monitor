package com.lakeel.device.management.domain.usecase;

import com.lakeel.device.management.data.entity.PreferenceResult;
import com.lakeel.device.management.presentation.intent.data.BeaconDistanceChangedIntentData;
import com.lakeel.device.management.data.repository.BeaconRepository;
import com.lakeel.device.management.data.repository.DeviceRepository;
import com.lakeel.device.management.data.repository.NotificationRepository;

import javax.inject.Inject;

import rx.Completable;
import rx.Observable;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public final class SaveBeaconDistanceChangedUseCase {

    @Inject
    BeaconRepository mBeaconRepository;

    @Inject
    DeviceRepository mDeviceRepository;

    @Inject
    NotificationRepository mNotificationRepository;

    @Inject
    public SaveBeaconDistanceChangedUseCase() {
    }

    public Completable execute(BeaconDistanceChangedIntentData beaconDistanceChangedIntentData) {
        Observable<String> o1 = mDeviceRepository.findDeviceId();
        Observable<String> o2 = mNotificationRepository.findRegistrationId();

        return Observable.zip(o1, o2, PreferenceResult::new)
                .flatMap((Func1<PreferenceResult, Observable<?>>) preferenceResult -> saveBeaconDistanceChanged(beaconDistanceChangedIntentData, preferenceResult.mDeviceId, preferenceResult.mRegistrationId))
                .subscribeOn(Schedulers.io())
                .toCompletable();
    }

    Observable saveBeaconDistanceChanged(BeaconDistanceChangedIntentData beaconDistanceChangedIntentData, String deviceId, String registrationId) {
        return mBeaconRepository.saveBeaconDistanceChanged(beaconDistanceChangedIntentData, deviceId, registrationId).toObservable();
    }
}
