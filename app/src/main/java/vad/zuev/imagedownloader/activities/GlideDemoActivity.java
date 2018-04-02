package vad.zuev.imagedownloader.activities;


import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BaseTarget;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.transition.Transition;

/**
 * This Activity demonstrates how to download an image using Glide
 */
public class GlideDemoActivity extends AbsLibDemoActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // the simplest way to display an image is very similar to Picasso. Also suits very well for a ListView/RecyclerView
        // Important: use an Activity or Fragment context where possible since Glide will also handle the lifecycle callbacks (e.g. it will pause
        // GIF animations when your Activity/Fragment goes into background)

    /*
           Glide.with(this)
                .load(imageUrl)
                .transition(withCrossFade())
                .apply(new RequestOptions()
                        .placeholder(PLACEHOLDER_RED)
                        .error(ERROR_RES))
                .into(imgResult);

                */


        BaseTarget target = new BaseTarget<BitmapDrawable>() {
            @Override
            public void onResourceReady(BitmapDrawable bitmapDrawable, Transition<? super BitmapDrawable> transition) {
                imgResult.setImageDrawable(bitmapDrawable);
                // if you need the Bitmap itself - e.g. to save it to device storage:
                // Bitmap bm = bitmapDrawable.getBitmap();
            }

            @Override
            public void getSize(SizeReadyCallback cb) {
                // you can set whatever width/height you need - Glide will resize the image for you
                cb.onSizeReady(SIZE_ORIGINAL, SIZE_ORIGINAL);
            }

            @Override
            public void removeCallback(SizeReadyCallback cb) {}
        };

        //noinspection unchecked
        Glide.with(this)
                .load(imageUrl)
                .into(target);

                /*  visit
                            https://bumptech.github.io/glide/
                                                                for docs and more examples */
    }

    @Override
    protected void init() {
        this.defaultSetup();
    }
}
