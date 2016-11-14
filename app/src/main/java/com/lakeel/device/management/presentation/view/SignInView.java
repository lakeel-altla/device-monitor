package com.lakeel.device.management.presentation.view;

import android.content.Intent;

public interface SignInView extends BaseView {

    void showSignInActivity(Intent intent);

    void showTitle(String title);

    void showTopFragment();
}
