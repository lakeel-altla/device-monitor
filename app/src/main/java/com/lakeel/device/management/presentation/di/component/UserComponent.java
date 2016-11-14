package com.lakeel.device.management.presentation.di.component;

import com.lakeel.device.management.presentation.di.ActivityScope;
import com.lakeel.device.management.presentation.di.module.ActivityModule;
import com.lakeel.device.management.presentation.di.module.PresenterModule;
import com.lakeel.device.management.presentation.di.module.RepositoryModule;
import com.lakeel.device.management.presentation.view.activity.MainActivity;
import com.lakeel.device.management.presentation.view.fragment.nearby.NearbyFragment;
import com.lakeel.device.management.presentation.view.fragment.signin.SignInFragment;

import dagger.Subcomponent;

@ActivityScope
@Subcomponent(modules = {ActivityModule.class, PresenterModule.class, RepositoryModule.class})
public interface UserComponent {

    void inject(MainActivity activity);

    void inject(NearbyFragment fragment);

    void inject(SignInFragment fragment);
}
