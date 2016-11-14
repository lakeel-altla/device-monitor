package com.lakeel.device.management.presentation.di.module;

import com.lakeel.altla.nearby.NearbyWrapper;
import com.lakeel.device.management.presentation.di.ActivityScope;

import android.app.Activity;
import android.support.annotation.NonNull;

import dagger.Module;
import dagger.Provides;

@Module
public class ActivityModule {

    private final Activity mActivity;

    public ActivityModule(@NonNull Activity activity) {
        mActivity = activity;
    }

    @ActivityScope
    @Provides
    public NearbyWrapper provideNearbyWrapper() {
        return new NearbyWrapper(mActivity);
    }
}
