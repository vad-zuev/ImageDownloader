package vad.zuev.imagedownloader.activities;


import android.os.Bundle;
import android.support.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class GlideDemoActivity extends AbsLibDemoActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // the simplest way to display an image is very similar to Picasso. Also suits very well for a ListView/RecyclerView
        // Important: use an Activity or Fragment context where possible since Glide will also handle the lifecycle callbacks (e.g. it will pause
        // GIF animations when your Activity/Fragment goes into background)

        Glide.with(this)
                .load(imageUrl)
                .transition(withCrossFade())
                .apply(new RequestOptions()
                        .placeholder(PLACEHOLDER_RED)
                        .error(ERROR_RES))
                .into(imgResult);






    }

    @Override
    protected void init() {
        this.standardSetup();
    }
}
