package com.lakeel.device.management.presentation.di.module;

import com.lakeel.device.management.data.repository.EncryptedPreferences;
import com.lakeel.device.management.presentation.di.ActivityScope;

import android.content.Context;

import dagger.Module;
import dagger.Provides;

@Module
public final class PresenterModule {

    @ActivityScope
    @Provides
    public EncryptedPreferences provideEncryptedPreferences(Context context) {
        return new EncryptedPreferences(context);
    }
}
