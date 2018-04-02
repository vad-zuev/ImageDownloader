package vad.zuev.imagedownloader.activities;


import android.os.Bundle;
import android.support.annotation.Nullable;

import com.so.example.R;

public class UILDemoActivity extends AbsLibDemoActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void init() {
        this.defaultSetup();
        tvPercent = findViewById(R.id.tv_percent);
        progressBar = findViewById(R.id.pb);
    }
}

