package com.lakeel.device.management.data.repository;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.lakeel.device.management.data.exception.DataStoreException;
import com.lakeel.device.management.data.entity.RegistrationIdEntity;
import com.lakeel.device.management.data.entity.mapper.RegistrationIdDataMapper;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import rx.Observable;
import rx.Single;

public class NotificationRepositoryImpl implements NotificationRepository {

    private static final String NOTIFICATION_REFERENCE = "notification";

    private EncryptedPreferences mEncryptedPreferences;

    private DatabaseReference mDatabaseReference;

    private RegistrationIdDataMapper mRegistrationIdDataMapper = new RegistrationIdDataMapper();

    @Inject
    public NotificationRepositoryImpl(EncryptedPreferences encryptedPreferences) {
        mEncryptedPreferences = encryptedPreferences;
        mDatabaseReference = FirebaseDatabase.getInstance().getReference(NOTIFICATION_REFERENCE);
    }

    @Override
    public Observable<String> findRegistrationId() {
        return Observable.create(subscriber -> {
            String registrationId = mEncryptedPreferences.getRegistrationId();
            subscriber.onNext(registrationId);
            subscriber.onCompleted();
        });
    }

    @Override
    public Observable saveRegistrationId(@NonNull String registrationId) {
        return saveRegistrationIdLocal(registrationId)
                .flatMap(aVoid -> getDeviceId())
                .map(deviceId -> mRegistrationIdDataMapper.map(registrationId, deviceId))
                .flatMap(this::saveRegistrationData)
                .toObservable();
    }

    Single<String> saveRegistrationIdLocal(String registrationId) {
        mEncryptedPreferences.saveRegistrationId(registrationId);
        return Single.just(registrationId);
    }

    Single<String> getDeviceId() {
        String deviceId = mEncryptedPreferences.getDeviceId();
        return Single.just(deviceId);
    }

    Single saveRegistrationData(RegistrationIdEntity registrationIdEntity) {
        return Observable.create(subscriber -> {
            Task task = mDatabaseReference.push().setValue(registrationIdEntity)
                    .addOnSuccessListener(aVoid -> subscriber.onCompleted())
                    .addOnFailureListener(subscriber::onError);

            Exception exception = task.getException();
            if (exception != null) {
                throw new DataStoreException(exception);
            }
        }).toSingle();
    }
}
