package com.lakeel.device.management.presentation.view;

import com.google.android.gms.common.ConnectionResult;

public interface ActivityView extends BaseView {

    void showConnectionResolutionDialog(ConnectionResult connectionResult);

    void showSignInFragment();

    void showTopFragment();

    void showProfile();

    void setUpDrawer(boolean isSignedIn);
}
