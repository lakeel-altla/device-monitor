package com.lakeel.device.management.presentation.presenter.activity;

import com.google.android.gms.common.ConnectionResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;

import com.lakeel.altla.nearby.NearbyWrapper;
import com.lakeel.device.management.domain.usecase.SaveDeviceIdUseCase;
import com.lakeel.device.management.domain.usecase.SaveRegistrationIdUseCase;
import com.lakeel.device.management.presentation.presenter.BasePresenter;
import com.lakeel.device.management.presentation.view.ActivityView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

public class ActivityPresenter extends BasePresenter<ActivityView> implements NearbyWrapper.ConnectionListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActivityPresenter.class);

    @Inject
    NearbyWrapper mNearbyWrapper;

    @Inject
    SaveDeviceIdUseCase mSaveDeviceIdUseCase;

    @Inject
    SaveRegistrationIdUseCase mSaveRegistrationIdUseCase;

    private FirebaseInstanceId mInstanceId = FirebaseInstanceId.getInstance();

    private CompositeSubscription subscriptions = new CompositeSubscription();

    @Inject
    public ActivityPresenter() {
    }

    @Override
    public void onResume() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
            getView().setUpDrawer(false);
            getView().showSignInFragment();
        } else {
            // サインイン済み
            mNearbyWrapper.addConnectionListener(this);
            if (!mNearbyWrapper.isConnected()) {
                mNearbyWrapper.connect();
            }

            saveRegistrationId();

            getView().showProfile();
            getView().setUpDrawer(true);
            getView().showTopFragment();
        }
    }

    @Override
    public void onStop() {
        mNearbyWrapper.removeConnectionListener(this);

        // subscription の解放。
        subscriptions.unsubscribe();
    }

    @Override
    public void onConnected() {
        LOGGER.info("Connected to nearby");
    }

    @Override
    public void onConnectionSuspended(int i) {
        LOGGER.debug("Nearby connection suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        getView().showConnectionResolutionDialog(connectionResult);
    }

    public void saveDeviceId() {
        Subscription subscription = mSaveDeviceIdUseCase.execute()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(throwable -> {
                    LOGGER.error("Could not save device id");
                }, () -> {
                });
        subscriptions.add(subscription);
    }

    public void saveRegistrationId() {
        String token = mInstanceId.getToken();
        Subscription subscription = mSaveRegistrationIdUseCase
                .execute(token)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(e -> LOGGER.error("Failed to save registration id"),
                        () -> {
                        });
        subscriptions.add(subscription);
    }
}
