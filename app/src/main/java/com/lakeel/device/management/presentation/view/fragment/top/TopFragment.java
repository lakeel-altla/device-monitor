package com.lakeel.device.management.presentation.view.fragment.top;

import com.lakeel.device.management.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public final class TopFragment extends Fragment {

    //
    // Fragment 再生成時、リフレクションでデフォルトコンストラクタが
    // 呼ばれるため、デフォルトコンストラクタは public で残しておかなければならない。
    //
    public static TopFragment newInstance() {
        return new TopFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_top, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
