package com.lakeel.device.management.presentation.presenter.nearby;

import com.google.android.gms.common.api.Status;

import com.lakeel.altla.nearby.NearbyWrapper;
import com.lakeel.device.management.R;
import com.lakeel.device.management.presentation.presenter.BasePresenter;
import com.lakeel.device.management.presentation.view.NearbyView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.PendingIntent;

import javax.inject.Inject;

import rx.subscriptions.CompositeSubscription;

public final class NearbyPresenter extends BasePresenter<NearbyView> implements NearbyWrapper.SubscribedResultListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(NearbyPresenter.class);

    private final CompositeSubscription subscriptions = new CompositeSubscription();

    @Inject
    NearbyWrapper mNearbyWrapper;

    @Inject
    public NearbyPresenter() {
    }

    @Override
    public void onResume() {
        mNearbyWrapper.addSubscribedResultListener(this);

        getView().showTitle();
    }

    @Override
    public void onStop() {
        mNearbyWrapper.removeSubscribedResultListener(this);

        // Subscription を解放。
        subscriptions.unsubscribe();
    }

    @Override
    public void onSubscribedResult(Status status) {
        getView().showResolutionDialog(status);
    }

    public void subscribeInBackground(PendingIntent pendingIntent) {
        if (mNearbyWrapper.isConnected()) {
            mNearbyWrapper.subscribeInBackground(pendingIntent);
        } else {
            LOGGER.warn("Not connected to google api client");
            getView().showSnackBar(R.string.error_not_connected);
        }
    }
}
