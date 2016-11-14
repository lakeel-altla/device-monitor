package com.lakeel.device.management.presentation.di.module;

import com.lakeel.device.management.presentation.di.ActivityScope;
import com.lakeel.device.management.data.repository.BeaconRepository;
import com.lakeel.device.management.data.repository.BeaconRepositoryImpl;
import com.lakeel.device.management.data.repository.DeviceRepository;
import com.lakeel.device.management.data.repository.DeviceRepositoryImpl;
import com.lakeel.device.management.data.repository.NotificationRepository;
import com.lakeel.device.management.data.repository.NotificationRepositoryImpl;

import dagger.Module;
import dagger.Provides;

@Module
public class RepositoryModule {

    @ActivityScope
    @Provides
    public DeviceRepository provideDeviceRepository(DeviceRepositoryImpl repository) {
        return repository;
    }

    @ActivityScope
    @Provides
    public BeaconRepository provideBeaconRepository(BeaconRepositoryImpl repository) {
        return repository;
    }

    @ActivityScope
    @Provides
    public NotificationRepository provideNotificationRepository(NotificationRepositoryImpl repository) {
        return repository;
    }
}
