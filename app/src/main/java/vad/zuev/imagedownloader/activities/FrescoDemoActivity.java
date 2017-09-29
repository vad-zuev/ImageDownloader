package vad.zuev.imagedownloader.activities;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.so.example.R;

public class FrescoDemoActivity extends AbsLibDemoActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Override
    protected void init() {
        draweeView = findViewById(R.id.drawee);
        draweeView.setVisibility(View.VISIBLE);
    }
}
