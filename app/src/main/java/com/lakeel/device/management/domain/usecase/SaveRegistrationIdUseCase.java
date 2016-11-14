package com.lakeel.device.management.domain.usecase;

import com.lakeel.device.management.core.StringUtils;
import com.lakeel.device.management.data.repository.NotificationRepository;

import javax.inject.Inject;

import rx.Completable;
import rx.Observable;
import rx.schedulers.Schedulers;

public final class SaveRegistrationIdUseCase {

    @Inject
    NotificationRepository mNotificationRepository;

    @Inject
    public SaveRegistrationIdUseCase() {
    }

    public Completable execute(String token) {
        return mNotificationRepository.findRegistrationId()
                .filter(registrationId -> isRegistrationIdSaveNeeded(token, registrationId))
                .flatMap(s -> saveRegistrationId(token))
                .subscribeOn(Schedulers.io())
                .toCompletable();
    }

    Observable saveRegistrationId(String token) {
        return mNotificationRepository.saveRegistrationId(token);
    }

    boolean isRegistrationIdSaveNeeded(String token, String registrationId) {
        if (StringUtils.isEmpty(registrationId)) {
            return true;
        } else {
            // token が更新された場合。
            if (!registrationId.equals(token)) {
                return true;
            }
        }
        return false;
    }
}
