package com.lakeel.device.management.presentation.di.component;

import com.lakeel.device.management.presentation.di.module.ActivityModule;
import com.lakeel.device.management.presentation.di.module.ApplicationModule;
import com.lakeel.device.management.presentation.di.module.PresenterModule;
import com.lakeel.device.management.presentation.di.module.RepositoryModule;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {

    UserComponent userComponent(ActivityModule module, PresenterModule presenterModule, RepositoryModule repositoryModule);

    Context context();
}
