package com.lakeel.device.management.data.repository;

import com.lakeel.device.management.presentation.intent.data.BeaconDistanceChangedIntentData;
import com.lakeel.device.management.presentation.intent.data.BeaconFoundIntentData;

import android.support.annotation.NonNull;

import rx.Completable;

public interface BeaconRepository {

    Completable saveBeaconFound(@NonNull BeaconFoundIntentData beaconFoundIntentData, @NonNull String deviceId, @NonNull String registrationId);

    Completable saveBeaconDistanceChanged(@NonNull BeaconDistanceChangedIntentData beaconDistanceChangedIntentData, @NonNull String deviceId, @NonNull String registrationId);
}
