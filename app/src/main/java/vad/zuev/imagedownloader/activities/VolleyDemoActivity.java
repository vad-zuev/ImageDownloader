package vad.zuev.imagedownloader.activities;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.so.example.R;

public class VolleyDemoActivity extends AbsLibDemoActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Override
    protected void init() {
        networkImageView = findViewById(R.id.network_image_view);
        networkImageView.setVisibility(View.VISIBLE);
    }
}
