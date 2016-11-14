package com.lakeel.device.management.presentation.presenter.signin;

import com.google.firebase.iid.FirebaseInstanceId;

import com.firebase.ui.auth.AuthUI;
import com.lakeel.altla.nearby.NearbyWrapper;
import com.lakeel.device.management.R;
import com.lakeel.device.management.domain.usecase.SaveRegistrationIdUseCase;
import com.lakeel.device.management.presentation.presenter.BasePresenter;
import com.lakeel.device.management.presentation.view.SignInView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Intent;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

public final class SignInPresenter extends BasePresenter<SignInView> {

    @Inject
    NearbyWrapper mNearbyWrapper;

    @Inject
    SaveRegistrationIdUseCase mSaveRegistrationIdUseCase;

    private static final Logger LOGGER = LoggerFactory.getLogger(SignInPresenter.class);

    private CompositeSubscription subscriptions = new CompositeSubscription();

    private FirebaseInstanceId mInstanceId = FirebaseInstanceId.getInstance();

    @Inject
    public SignInPresenter() {
    }

    private static final String TITLE = "SignInFragment";

    // サービス利用規約の URL
    private static final String GOOGLE_TOS_URL =
            "https://www.google.com/policies/terms/";

    private static final String FIREBASE_TOS_URL =
            "https://www.firebase.com/terms/terms-of-service.html";

    @Override
    public void onResume() {
        getView().showTitle(TITLE);
    }

    public void signIn() {
        // FirebaseUI を利用。各種プロバイダを設定。
        Intent intent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setProviders(
                        AuthUI.EMAIL_PROVIDER,
                        AuthUI.FACEBOOK_PROVIDER,
                        AuthUI.GOOGLE_PROVIDER)
                .setTosUrl(GOOGLE_TOS_URL)
                .setTheme(R.style.FirebaseUI)
                .build();

        getView().showSignInActivity(intent);
    }

    public void onSignedIn() {
        // Activity 側でリスナーを受ける。
        mNearbyWrapper.connect();

        String token = mInstanceId.getToken();
        Subscription subscription = mSaveRegistrationIdUseCase.execute(token)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(e -> LOGGER.error("Failed to save registration id"),
                        () -> getView().showTopFragment());
        subscriptions.add(subscription);
    }
}
