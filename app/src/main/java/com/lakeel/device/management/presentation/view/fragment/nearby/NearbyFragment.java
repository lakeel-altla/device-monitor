package com.lakeel.device.management.presentation.view.fragment.nearby;

import com.google.android.gms.common.api.Status;

import com.lakeel.device.management.R;
import com.lakeel.device.management.android.SnackBarUtils;
import com.lakeel.device.management.presentation.presenter.nearby.NearbyPresenter;
import com.lakeel.device.management.presentation.receiver.BeaconMessageReceiver;
import com.lakeel.device.management.presentation.view.activity.MainActivity;
import com.lakeel.device.management.presentation.view.NearbyView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public final class NearbyFragment extends Fragment implements NearbyView {

    private static final Logger LOG = LoggerFactory.getLogger(NearbyFragment.class);

    private static int RESOLVE_SUBSCRIBED_REQUEST_CODE = 1;

    @Inject
    NearbyPresenter mNearbyPresenter;

    @BindView(R.id.layout_beacon)
    LinearLayout mLinearLayout;

    public static NearbyFragment newInstance() {
        return new NearbyFragment();
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
        View view = inflater.inflate(R.layout.fragment_beacon, container, false);
        ButterKnife.bind(this, view);

        mNearbyPresenter.onCreateView(this);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mNearbyPresenter.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
        mNearbyPresenter.onStop();
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (RESOLVE_SUBSCRIBED_REQUEST_CODE == requestCode && resultCode == Activity.RESULT_OK) {
            mNearbyPresenter.subscribeInBackground(getPendingIntent());
        }
    }

    @OnClick(R.id.button_subscribe)
    public void onSubscribeButtonClicked() {
        mNearbyPresenter.subscribeInBackground(getPendingIntent());
    }

    @Override
    public void showTitle() {
        getActivity().setTitle(R.string.title_nearby);
    }

    @Override
    public void showResolutionDialog(Status status) {
        try {
            status.startResolutionForResult(getActivity(), RESOLVE_SUBSCRIBED_REQUEST_CODE);
        } catch (IntentSender.SendIntentException e) {
            LOG.error("Failed to subscribe");
            SnackBarUtils.showShort(mLinearLayout, R.string.error_unknown);
        }
    }

    @Override
    public void showSnackBar(int resId) {
        SnackBarUtils.showShort(mLinearLayout, resId);
    }

    private PendingIntent getPendingIntent() {
        return PendingIntent.getBroadcast(getContext(), 0, new Intent(getContext(), BeaconMessageReceiver.class),
                PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
