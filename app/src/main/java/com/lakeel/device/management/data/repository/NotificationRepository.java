package com.lakeel.device.management.data.repository;

import android.support.annotation.NonNull;

import rx.Observable;

public interface NotificationRepository {

    Observable<String> findRegistrationId();

    Observable saveRegistrationId(@NonNull String registrationId);
}
