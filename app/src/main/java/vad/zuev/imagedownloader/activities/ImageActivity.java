package vad.zuev.imagedownloader.activities;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.so.example.R;

import vad.zuev.imagedownloader.tools.VolleyManager;


public class ImageActivity extends AppCompatActivity {

    public static final String KEY_NAME = "name";
    public static final String KEY_SELECTED_BTN = "selected_button";
    public static final String KEY_URL = "url";
    private String name;
    private ImageView imgDisplay, imgResult;
    private BroadcastReceiver downloadCompleteReceiver;
    /* we will use this tag for Volley image requests */
    private final String CANCELABLE_REQUEST_TAG = "volleyImageRequest";
    private final int RES_ERROR = R.drawable.error_orange;
    private final int RES_PLACEHOLDER = R.drawable.placeholder_grey;


    @SuppressLint({"SetTextI18n", "CutPasteId"})
    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        name = getIntent().getStringExtra(KEY_NAME);
        final String url = getIntent().getStringExtra(KEY_URL);
        /* NOTE: normally, you would call this in your Application subclass or some base Activity class.
             Do NOT call Fresco.initialize(Context) for each Activity.
           Note #2: you have to initialize fresco BEFORE calling setContentView() (another good reason to
                    do it in some base class) */
        Fresco.initialize(this);

      /*  switch (getIntent().getIntExtra(KEY_SELECTED_BTN, 0)) {



            case R.id.btnDownloadGlide:

                setContentView(R.layout.activity_library_demo);
                getSupportActionBar().setTitle("Glide demo");
                imgResult = findViewById(R.id.imgLibResult);

              //  GlideExamples.loadIntoImageView(this, url, RES_PLACEHOLDER, RES_ERROR, imgResult);
                //TODO add SVG example

                GlideExamples.loadIntoTarget(this, url, RES_PLACEHOLDER, RES_ERROR, new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                        imgResult.setImageDrawable(resource);
                    }
                });

                break;


            case R.id.btnDownloadVolley:

                setContentView(R.layout.activity_library_demo);
                getSupportActionBar().setTitle("Volley demo");
                final ImageView imgRes = findViewById(R.id.imgLibResult);


                ImageRequest imgRequest = new ImageRequest(url, response -> {
                    imgRes.setImageBitmap(response);
                    imgRes.startAnimation(AnimationUtils.loadAnimation(ImageActivity.this, android.R.anim.fade_in));
                }, 0, 0, ImageView.ScaleType.CENTER_CROP, Bitmap.Config.ARGB_8888, error -> {
                    imgRes.setImageResource(RES_ERROR);



                    Toast.makeText(ImageActivity.this, VolleyManager.getInstance().checkError(error), Toast.LENGTH_LONG).show();
                });


                imgRequest.setTag(CANCELABLE_REQUEST_TAG);


                VolleyManager.getInstance().getRequestQ(this).add(imgRequest);


                //  ((NetworkImageView) findViewById(R.id.netImgResult)).setImageUrl(url, VolleyManager.getInstance().getImageLoader());


                    For more examples and details, check out the official docs at
                                        http://developer.android.com/training/volley/index.html
                    To grab Volley via Maven/Gradle visit the unofficial mirror at
                                        https://github.com/mcxiaoke/android-volley


                break;
            case R.id.btnDownloadFresco:
                setContentView(R.layout.activity_library_demo);
                getSupportActionBar().setTitle("Fresco demo");
                final SimpleDraweeView drawee = findViewById(R.id.drawee);
                drawee.setVisibility(View.VISIBLE);


                //  drawee.setImageURI(Uri.parse(url));


                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(Resources.getSystem().getDisplayMetrics().widthPixels/2,
                        Resources.getSystem().getDisplayMetrics().heightPixels/2);
                params.addRule(RelativeLayout.CENTER_IN_PARENT);
                // since animated images are usually of smaller resolution, we can reduce the Drawee's size to 1/2 of device screen size
                drawee.setLayoutParams(params);
                DraweeController controller = Fresco.newDraweeControllerBuilder()
                        .setUri(Uri.parse(url))
                        .setAutoPlayAnimations(true)
                .build();
                drawee.setController(controller);



                //here I use the fully qualified class name 'cause we also have an ImageRequest defined by Volley
                com.facebook.imagepipeline.request.ImageRequest imgReq = ImageRequestBuilder.newBuilderWithSource(Uri.parse(url))
                        .setProgressiveRenderingEnabled(true)
                        .build();
                DraweeController controller = Fresco.newDraweeControllerBuilder()
                        .setImageRequest(imgReq)
                        .setOldController(drawee.getController())
                        .build();
                drawee.setController(controller);




                break;
            case R.id.btnDownloadUIL:
                setContentView(R.layout.activity_library_demo);
                getSupportActionBar().setTitle("Universal Image Loader demo");
                final ProgressBar pbPercent = findViewById(R.id.pbImageLoading);
                pbPercent.setVisibility(View.VISIBLE);
                final TextView tvPerc = findViewById(R.id.tvPercent);
                tvPerc.setVisibility(View.VISIBLE);
                final ImageView imageView = findViewById(R.id.imgLibResult);



                DisplayImageOptions opts = new DisplayImageOptions.Builder()
                        .cacheInMemory(true)
                        .cacheOnDisk(true)
                        .imageScaleType(ImageScaleType.EXACTLY)
                        .displayer(new FadeInBitmapDisplayer(500))
                        .build();

                ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                        getApplicationContext())
                        .defaultDisplayImageOptions(opts)
                        .memoryCache(new WeakMemoryCache())
                        .build();

                if (!ImageLoader.getInstance().isInited())
                    ImageLoader.getInstance().init(config);



                ImageLoader.getInstance().displayImage(url, imageView, opts, new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                        doFoo();
                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                        @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
                        Throwable cause = failReason.getCause();
                        String message = cause != null ? cause.getMessage() : failReason.getType().name();
                        Toast.makeText(ImageActivity.this, "Error: " + message, Toast.LENGTH_LONG).show();
                        pbPercent.setVisibility(View.GONE);
                        tvPerc.setVisibility(View.GONE);
                        imageView.setImageResource(RES_ERROR);
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        pbPercent.setVisibility(View.GONE);
                        tvPerc.setVisibility(View.GONE);
                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {
                        Toast.makeText(ImageActivity.this, this.getClass().getSimpleName() + " : OnLoadingCancelled", Toast.LENGTH_LONG).show();
                        doBar();
                    }
                    // NOTE: .cacheOnDisk(true) must be set in the options or this Listener won't work
                }, (imageUri, view, current, total) -> {
                    int percent = (current * 100) / total;
                    pbPercent.setProgress(percent);
                    tvPerc.setText(percent + "%");
                });


                break;
        } */
    }

    /**
     * does foo
     */
    private void doFoo() {
        //dummy
    }

    /**
     * does bar
     */
    private void doBar() {
        //dummy
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // we are no longer interested in download complete events
        if (downloadCompleteReceiver != null)
            unregisterReceiver(downloadCompleteReceiver);
        // cancel all tagged Volley requests
        VolleyManager.getInstance().getRequestQ(this).cancelAll(CANCELABLE_REQUEST_TAG);
    }
}
