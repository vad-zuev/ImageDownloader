package vad.zuev.imagedownloader.activities;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.so.example.R;

import vad.zuev.imagedownloader.tools.VolleyManager;

/**
 * This Activity demonstrates how to download an image using Volley
 */
public class VolleyDemoActivity extends AbsLibDemoActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

         // An example that uses a "normal" ImageView and Volley's ImageLoader - this gives you access to the Bitmap

   /*     VolleyManager.getInstance(this).getImageLoader().get(imageUrl, new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                imgResult.setImageBitmap(response.getBitmap());
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                imgResult.setImageResource(PLACEHOLDER_RED);
                Utils.toastLong(VolleyDemoActivity.this, VolleyManager.getInstance(VolleyDemoActivity.this).checkError(error));
            }
        }); */

        // using the NetworkImageView makes loading and displaying the image a one-liner:
        networkImageView.setImageUrl(imageUrl, VolleyManager.getInstance(this).getImageLoader());

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        VolleyManager.getInstance(this).cancelAll();
    }

    @Override
    protected void init() {
        networkImageView = findViewById(R.id.network_image_view);
        networkImageView.setDefaultImageResId(R.drawable.placeholder_grey);
        networkImageView.setVisibility(View.VISIBLE);
        defaultSetup();
    }
}
