package vad.zuev.imagedownloader.activities;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.so.example.R;

/**
 * This Activity demonstrates how to download an image using Glide
 */
public class FrescoDemoActivity extends AbsLibDemoActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void init() {
        draweeView = findViewById(R.id.drawee);
        draweeView.setVisibility(View.VISIBLE);
    }
}
