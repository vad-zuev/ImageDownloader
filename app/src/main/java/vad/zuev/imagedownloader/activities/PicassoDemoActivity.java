package vad.zuev.imagedownloader.activities;


import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import vad.zuev.imagedownloader.tools.Utils;

/**
 * This Activity demonstrates how to download an image using Picasso
 */
public class PicassoDemoActivity extends AbsLibDemoActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*  The simplest way of displaying an image is basically a one-liner. Also works very well in a ListView/RecyclerView */
       // Picasso.get().load(imageUrl).error(ERROR_RES).placeholder(PLACEHOLDER_RED).into(imgResult);

       // implement the Target interface to access the downloaded Bitmap

        Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                imgResult.setImageBitmap(bitmap);
                Utils.toastLong(PicassoDemoActivity.this, "from: " + from.name());
                // do anything you need with the Bitmap
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                Utils.toastLong(PicassoDemoActivity.this, "Could not load image: ");
                e.printStackTrace();
                imgResult.setImageDrawable(errorDrawable);
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                imgResult.setImageDrawable(placeHolderDrawable);
            }
        };
        // this is needed to prevent your Target from being garbage collected while the image is still loading
        imgResult.setTag(target);

        Picasso.get()
                .load(imageUrl)
                .error(ERROR_RES)
                .placeholder(PLACEHOLDER_RED)
                .into(target);

        /*  visit
                     http://square.github.io/picasso
                                                       for docs and more examples */
    }

    @Override
    protected void init() {
        this.defaultSetup();
    }
}
