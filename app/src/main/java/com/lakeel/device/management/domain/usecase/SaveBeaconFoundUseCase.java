package com.lakeel.device.management.domain.usecase;

import com.lakeel.device.management.data.entity.PreferenceResult;
import com.lakeel.device.management.presentation.intent.data.BeaconFoundIntentData;
import com.lakeel.device.management.data.repository.BeaconRepository;
import com.lakeel.device.management.data.repository.DeviceRepository;
import com.lakeel.device.management.data.repository.NotificationRepository;

import javax.inject.Inject;

import rx.Completable;
import rx.Observable;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public final class SaveBeaconFoundUseCase {

    @Inject
    BeaconRepository mBeaconRepository;

    @Inject
    DeviceRepository mDeviceRepository;

    @Inject
    NotificationRepository mNotificationRepository;

    @Inject
    public SaveBeaconFoundUseCase() {
    }

    public Completable execute(BeaconFoundIntentData beaconFoundIntentData) {
        Observable<String> o1 = mDeviceRepository.findDeviceId();
        Observable<String> o2 = mNotificationRepository.findRegistrationId();

        return Observable.zip(o1, o2, PreferenceResult::new)
                .flatMap((Func1<PreferenceResult, Observable<?>>) preferenceResult ->
                        saveBeaconFound(beaconFoundIntentData, preferenceResult.mDeviceId, preferenceResult.mRegistrationId))
                .subscribeOn(Schedulers.io())
                .toCompletable();
    }

    Observable saveBeaconFound(BeaconFoundIntentData beaconFoundIntentData, String deviceId, String registrationId) {
        return mBeaconRepository.saveBeaconFound(beaconFoundIntentData, deviceId, registrationId).toObservable();
    }
}
