package com.lakeel.device.management.presentation;

import com.lakeel.device.management.presentation.di.component.ApplicationComponent;
import com.lakeel.device.management.presentation.di.component.DaggerApplicationComponent;
import com.lakeel.device.management.presentation.di.module.ApplicationModule;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.squareup.leakcanary.LeakCanary;

import android.app.Activity;
import android.app.Application;
import android.support.annotation.NonNull;

public final class App extends Application {

    private ApplicationComponent mApplicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        LeakCanary.install(this);
        initImageLoaderInstance();

        mApplicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build();
    }

    public static ApplicationComponent getApplicationComponent(@NonNull Activity activity) {
        return ((App) activity.getApplication()).mApplicationComponent;
    }

    private void initImageLoaderInstance() {
        // ImageLoader の初期化。

        // キャッシュの制限サイズを超えた時に、使用頻度が低い画像を削除する。
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024))
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
                .build();
        ImageLoader.getInstance().init(config);
    }
}
