package com.lakeel.device.management.presentation.view;

import com.google.android.gms.common.api.Status;

import android.support.annotation.StringRes;

public interface NearbyView extends BaseView {

    void showTitle();

    void showResolutionDialog(Status status);

    void showSnackBar(@StringRes int resId);
}
