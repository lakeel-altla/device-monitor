package com.lakeel.device.management.presentation.view.fragment.signin;

import com.lakeel.device.management.R;
import com.lakeel.device.management.presentation.presenter.signin.SignInPresenter;
import com.lakeel.device.management.presentation.view.activity.MainActivity;
import com.lakeel.device.management.presentation.view.SignInView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.OnClick;

public final class SignInFragment extends Fragment implements SignInView {

    //
    // Fragment 再生成時、リフレクションでデフォルトコンストラクタが
    // 呼ばれるため、デフォルトコンストラクタは public で残しておかなければならない。
    //

    @Inject
    SignInPresenter mSignInPresenter;

    private static final Logger LOGGER = LoggerFactory.getLogger(SignInFragment.class);

    private static final int SIGN_IN_REQUEST_CODE = 1;

    public static SignInFragment newInstance() {
        return new SignInFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Dagger
        MainActivity.getUserComponent(this).inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);
        ButterKnife.bind(this, view);

        mSignInPresenter.onCreateView(this);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @OnClick(R.id.button_signIn)
    public void onSignInButtonClicked(View view) {
        mSignInPresenter.signIn();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (SIGN_IN_REQUEST_CODE == requestCode) {
            if (Activity.RESULT_OK == resultCode) {
                mSignInPresenter.onSignedIn();
            } else {
                LOGGER.debug("Sign in failed:ResultCode=" + resultCode);
            }
        }
    }

    @Override
    public void showSignInActivity(Intent intent) {
        startActivityForResult(intent, SIGN_IN_REQUEST_CODE);
    }

    @Override
    public void showTitle(String title) {
        getActivity().setTitle(title);
    }

    @Override
    public void showTopFragment() {
        ((MainActivity) getActivity()).showTopFragment();
    }
}
