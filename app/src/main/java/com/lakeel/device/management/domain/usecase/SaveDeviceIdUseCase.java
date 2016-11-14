package com.lakeel.device.management.domain.usecase;

import com.lakeel.device.management.core.StringUtils;
import com.lakeel.device.management.data.repository.DeviceRepository;

import java.util.UUID;

import javax.inject.Inject;

import rx.Completable;
import rx.Observable;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public final class SaveDeviceIdUseCase {

    @Inject
    DeviceRepository mDeviceRepository;

    @Inject
    public SaveDeviceIdUseCase() {
    }

    public Completable execute() {
        return mDeviceRepository.findDeviceId()
                .filter(StringUtils::isEmpty)
                .flatMap((Func1<String, Observable<?>>) s -> saveDeviceId())
                .toCompletable()
                .subscribeOn(Schedulers.io());
    }

    Observable<Void> saveDeviceId() {
        return mDeviceRepository.saveDeviceId(UUID.randomUUID().toString()).toObservable();
    }
}
