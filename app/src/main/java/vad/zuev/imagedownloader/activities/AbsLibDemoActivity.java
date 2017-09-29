package vad.zuev.imagedownloader.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.so.example.R;

/**
 * Parent class for all Activities that demonstrate how to use an image
 * download library.
 */
public abstract class AbsLibDemoActivity extends AbsActivity {

    protected ImageView imgResult;
    protected SimpleDraweeView draweeView;
    protected NetworkImageView networkImageView;
    protected ProgressBar pb;
    protected TextView tvPercent;

    /**
     * This is where each subclass should perform its own initialization stuff
     */
    protected abstract void init();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library_demo);
        init();
    }

    /**
     * Initializes the ImageView and sets it visible
     */
    protected void standardSetup() {
        imgResult = findViewById(R.id.img_result);
        imgResult.setVisibility(View.VISIBLE);
    }
}
