package com.lakeel.device.management.presentation.view.activity;

import com.google.android.gms.common.ConnectionResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import com.lakeel.device.management.R;
import com.lakeel.device.management.android.PermissionUtils;
import com.lakeel.device.management.presentation.App;
import com.lakeel.device.management.presentation.di.component.UserComponent;
import com.lakeel.device.management.presentation.di.module.ActivityModule;
import com.lakeel.device.management.presentation.di.module.PresenterModule;
import com.lakeel.device.management.presentation.di.module.RepositoryModule;
import com.lakeel.device.management.presentation.presenter.activity.ActivityPresenter;
import com.lakeel.device.management.presentation.view.ActivityView;
import com.lakeel.device.management.presentation.view.fragment.nearby.NearbyFragment;
import com.lakeel.device.management.presentation.view.fragment.signin.SignInFragment;
import com.lakeel.device.management.presentation.view.fragment.top.TopFragment;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public final class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ActivityView {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainActivity.class);

    private static final int REQUEST_CODE_ACCESS_FINE_LOCATION = 1;

    private static final int REQUEST_CODE_NEARBY_CONNECTION = 2;

    private UserComponent mUserComponent;

    private ActionBarDrawerToggle toggle;

    private DrawerLayout drawerLayout;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Inject
    ActivityPresenter mActivityPresenter;

    @BindView(R.id.imageView_profile)
    ImageView profile;

    @BindView(R.id.textView_userName)
    TextView userName;

    @BindView(R.id.textView_address)
    TextView userAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        // 注意:
        //
        // メモリの管理によって、Activity が OS によって破棄された状態でアプリを最前面化した場合、
        // Fragment の再構築が自動で行われる。
        // しかし、onCreate() の後で Dagger のインスタンスを生成してしまうと、
        // onCreate() の処理の中で Fragment の再構築が行われてしまい、NullPointerException が発生してしまうため、
        // onCreate() の前で Dagger インスタンスを生成する。
        //

        // Dagger。
        mUserComponent = App.getApplicationComponent(this)
                .userComponent(new ActivityModule(this), new PresenterModule(), new RepositoryModule());
        mUserComponent.inject(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mActivityPresenter.onCreateView(this);
        mActivityPresenter.saveDeviceId();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.syncState();
        drawerLayout.addDrawerListener(toggle);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.inflateHeaderView(R.layout.navigation_header_main);
        profile = ButterKnife.findById(headerView, R.id.imageView_profile);
        userName = ButterKnife.findById(headerView, R.id.textView_userName);
        userAddress = ButterKnife.findById(headerView, R.id.textView_address);

        //
        // Android 5 系までは、Beacon のみ利用する場合に android.permission.ACCESS_FINE_LOCATION が許可されていれば
        // オプトインのダイアログは表示されない。
        // Android 6 系では、android.permission.ACCESS_FINE_LOCATION の許可を得る必要がある。
        //
        if (PermissionUtils.checkFineLocation(this) != PackageManager.PERMISSION_GRANTED) {
            PermissionUtils.requestAccessFineLocationPermissions(this, REQUEST_CODE_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mActivityPresenter.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
        mActivityPresenter.onStop();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        // Intent を伝搬しなければ、onResume() で Intent データを取得できない。
        setIntent(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        // TODO:Android 6 系の Permission 対応
        if (REQUEST_CODE_ACCESS_FINE_LOCATION != requestCode) {
            // 拒否。
            LOGGER.warn("Permission denied");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.navigation_beacon:
                FragmentController fragmentController = new FragmentController(getSupportFragmentManager());
                fragmentController.showBeaconFragment();
                break;
            default:
                LOGGER.debug("Unknown navigation item tapped");
        }

        drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    public void showConnectionResolutionDialog(ConnectionResult connectionResult) {
        try {
            connectionResult.startResolutionForResult(this, REQUEST_CODE_NEARBY_CONNECTION);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showSignInFragment() {
        FragmentController fragmentController = new FragmentController(getSupportFragmentManager());
        fragmentController.showSignInFragment();
    }

    @Override
    public void showTopFragment() {
        FragmentController fragmentController = new FragmentController(getSupportFragmentManager());
        fragmentController.showTopFragment();
    }

    @Override
    public void showProfile() {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        if (firebaseUser != null) {
            Uri uri = firebaseUser.getPhotoUrl();
            if (uri != null) {
                String imageUri = uri.toString();

                ImageLoader imageLoader = ImageLoader.getInstance();
                imageLoader.displayImage(imageUri, profile);
            }

            userName.setText(firebaseUser.getDisplayName());
            userAddress.setText(firebaseUser.getEmail());
        }
    }

    @Override
    public void setUpDrawer(boolean isSignedIn) {
        ActionBar actionBar = getSupportActionBar();

        if (isSignedIn) {
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
            }

            toggle.setDrawerIndicatorEnabled(true);
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        } else {
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(false);
            }

            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
    }

    public static UserComponent getUserComponent(@NonNull Fragment fragment) {
        return ((MainActivity) fragment.getActivity()).mUserComponent;
    }

    private static class FragmentController {

        private static final String SIGN_IN_FRAGMENT_TAG = SignInFragment.class.getSimpleName();

        private static final String TOP_FRAGMENT_TAG = TopFragment.class.getSimpleName();

        private static final String BEACON_FRAGMENT_TAG = NearbyFragment.class.getSimpleName();

        private FragmentManager mFragmentManager;

        public FragmentController(FragmentManager fragmentManager) {
            mFragmentManager = fragmentManager;
        }

        public void showSignInFragment() {
            SignInFragment signInFragment = SignInFragment.newInstance();
            replaceFragment(R.id.fragmentPlaceholder, signInFragment, SIGN_IN_FRAGMENT_TAG);
        }

        public void showTopFragment() {
            TopFragment topFragment = TopFragment.newInstance();
            replaceFragment(R.id.fragmentPlaceholder, topFragment, TOP_FRAGMENT_TAG);
        }

        public void showBeaconFragment() {
            NearbyFragment nearbyFragment = NearbyFragment.newInstance();
            replaceFragment(R.id.fragmentPlaceholder, nearbyFragment, BEACON_FRAGMENT_TAG);
        }

        private void replaceFragment(@IdRes int containerViewId, Fragment fragment, String tag) {
            FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
            fragmentTransaction.replace(containerViewId, fragment, tag);
            fragmentTransaction.commit();
        }
    }
}
