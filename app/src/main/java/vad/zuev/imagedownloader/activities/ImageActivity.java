package vad.zuev.imagedownloader.activities;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageRequest;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.so.example.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.util.Arrays;

import vad.zuev.imagedownloader.tools.BasicImageDownloader;
import vad.zuev.imagedownloader.tools.VolleyManager;


public class ImageActivity extends AppCompatActivity {

    public static final String KEY_NAME = "name";
    public static final String KEY_SELECTED_BTN = "selected_button";
    public static final String KEY_URL = "url";
    private String name;
    private ImageView imgDisplay;
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

        switch (getIntent().getIntExtra(KEY_SELECTED_BTN, 0)) {

            case R.id.btnDownloadBasic:

                setContentView(R.layout.activity_image_basic_dm);
                getSupportActionBar().setTitle("BasicImageDownloader demo");
                imgDisplay = findViewById(R.id.imgResult);
                imgDisplay.setImageResource(RES_PLACEHOLDER);
                final TextView tvPercent = findViewById(R.id.tvPercent);
                final ProgressBar pbLoading = findViewById(R.id.pbImageLoading);
                final BasicImageDownloader downloader = new BasicImageDownloader(new BasicImageDownloader.OnImageLoaderListener() {
                    @Override
                    public void onError(BasicImageDownloader.ImageError error) {
                        Toast.makeText(ImageActivity.this, "Error code " + error.getErrorCode() + ": " +
                                error.getMessage(), Toast.LENGTH_LONG).show();
                        error.printStackTrace();
                        imgDisplay.setImageResource(RES_ERROR);
                        tvPercent.setVisibility(View.GONE);
                        pbLoading.setVisibility(View.GONE);
                    }

                    @Override
                    public void onProgressChange(int percent) {
                        pbLoading.setProgress(percent);
                        tvPercent.setText(percent + "%");
                    }

                    @Override
                    public void onComplete(Bitmap result) {
                        /* save the image - I'm gonna use JPEG */
                        final Bitmap.CompressFormat mFormat = Bitmap.CompressFormat.JPEG;
                        /* don't forget to include the extension into the file name */
                        final File myImageFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +
                                File.separator + "image_test" + File.separator + name + "." + mFormat.name().toLowerCase());
                        BasicImageDownloader.writeToDisk(myImageFile, result, new BasicImageDownloader.OnBitmapSaveListener() {
                            @Override
                            public void onBitmapSaved() {
                                Toast.makeText(ImageActivity.this, "Image saved as: " + myImageFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onBitmapSaveError(BasicImageDownloader.ImageError error) {
                                Toast.makeText(ImageActivity.this, "Error code " + error.getErrorCode() + ": " +
                                        error.getMessage(), Toast.LENGTH_LONG).show();
                                error.printStackTrace();
                            }


                        }, mFormat, false);

                        tvPercent.setVisibility(View.GONE);
                        pbLoading.setVisibility(View.GONE);
                        imgDisplay.setImageBitmap(result);
                        imgDisplay.startAnimation(AnimationUtils.loadAnimation(ImageActivity.this, android.R.anim.fade_in));
                    }
                });
                downloader.download(url, true);

                break;

            case R.id.btnDownloadDM:

                setContentView(R.layout.activity_image_basic_dm);
                getSupportActionBar().setTitle("DownloadManager demo");
                findViewById(R.id.tvDMWorking).setVisibility(View.VISIBLE);
                findViewById(R.id.pbImageLoading).setVisibility(View.GONE);
                findViewById(R.id.tvPercent).setVisibility(View.GONE);
                final TextView tvStatus = findViewById(R.id.tvDMWorking);
                Animation anim = AnimationUtils.loadAnimation(ImageActivity.this, android.R.anim.fade_out);
                anim.setRepeatCount(Animation.INFINITE);
                anim.setRepeatMode(Animation.REVERSE);
                anim.setDuration(700L);
                tvStatus.startAnimation(anim);

                DownloadManager.Request request;

                try {
                    request = new DownloadManager.Request(Uri.parse(url));
                } catch (IllegalArgumentException e) {
                    tvStatus.setText("Error: " + e.getMessage());
                    tvStatus.clearAnimation();
                    break;
                }
                /* allow mobile and WiFi downloads */
                request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
                request.setTitle("DM Example");
                request.setDescription("Downloading file");

                /* we let the user see the download in a notification */
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
                /* Try to determine the file extension from the url. Only allow image types. You
                 * can skip this check if you only plan to handle the downloaded file manually and
                 * don't care about file managers not recognizing the file as a known type */
                String[] allowedTypes = {"png", "jpg", "jpeg", "gif", "webp"};
                String suffix = url.substring(url.lastIndexOf(".") + 1).toLowerCase();
                if (!Arrays.asList(allowedTypes).contains(suffix)) {
                    tvStatus.clearAnimation();
                    tvStatus.setText("Invalid file extension. Allowed types: \n");
                    for (String s : allowedTypes) {
                        tvStatus.append("\n" + "." + s);
                    }
                    break;
                }

                /* set the destination path for this download */
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS +
                        File.separator + "image_test", name + "." + suffix);
                /* allow the MediaScanner to scan the downloaded file */
                request.allowScanningByMediaScanner();
                final DownloadManager dm = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                /* this is our unique download id */
                final long DL_ID = dm.enqueue(request);

                /* get notified when the download is complete */
                downloadCompleteReceiver = new BroadcastReceiver() {

                    @Override
                    public void onReceive(Context context, Intent intent) {
                        /* our download */
                        if (DL_ID == intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1L)) {

                            tvStatus.clearAnimation();
                            /* get the path of the downloaded file */
                            DownloadManager.Query query = new DownloadManager.Query();
                            query.setFilterById(DL_ID);
                            Cursor cursor = dm.query(query);
                            if (!cursor.moveToFirst()) {
                                tvStatus.setText("Download error: cursor is empty");
                                return;
                            }

                            if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                                    != DownloadManager.STATUS_SUCCESSFUL) {
                                tvStatus.setText("Download failed: no success status");
                                return;
                            }

                            String path = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                            tvStatus.setText("File download complete. Location: \n" + path);
                        }
                    }
                };
                /* register receiver to listen for ACTION_DOWNLOAD_COMPLETE action */
                registerReceiver(downloadCompleteReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

                break;
            case R.id.btnDownloadPicasso:

                setContentView(R.layout.activity_image_libs);
                getSupportActionBar().setTitle("Picasso demo");
                final ImageView imgResult = findViewById(R.id.imgLibResult);

                // A basic example that just loads the image into the ImageView
                /* NOTE: by default, Picasso will cache the downloaded image unless you explicitly disable
                    this behavior by using .memoryPolicy(MemoryPolicy.NO_CACHE) */

                /*
                Picasso.with(this)
                        .load(url)
                        .placeholder(RES_PLACEHOLDER)
                        .error(RES_ERROR)
                        .into(imgResult);
                */


                // implementing the Target interface gives you access to the Bitmap as well as some additional information

                Target target = new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        imgResult.setImageBitmap(bitmap);
                        doFoo();
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {
                        imgResult.setImageDrawable(errorDrawable);
                        Toast.makeText(ImageActivity.this, "download failed", Toast.LENGTH_SHORT).show();
                        doBar();
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                        imgResult.setImageDrawable(placeHolderDrawable);
                    }
                };

                //this will help us to avoid the Target being gc'd
                imgResult.setTag(target);

                Picasso.with(this)
                        .load(url)
                        .placeholder(RES_PLACEHOLDER)
                        .error(RES_ERROR)
                        .into(target);

                /*
                    Refer to the official Picasso website for more options and examples
                                        http://square.github.io/picasso
                                                                                          */

                break;

            case R.id.btnDownloadGlide:









                break;


            case R.id.btnDownloadVolley:

                setContentView(R.layout.activity_image_libs);
                getSupportActionBar().setTitle("Volley demo");
                final ImageView imgRes = findViewById(R.id.imgLibResult);

                /* here we are implementing an ImageRequest that provides callbacks for success and error cases */
                ImageRequest imgRequest = new ImageRequest(url, response -> {
                    imgRes.setImageBitmap(response);
                    imgRes.startAnimation(AnimationUtils.loadAnimation(ImageActivity.this, android.R.anim.fade_in));
                }, 0, 0, ImageView.ScaleType.CENTER_CROP, Bitmap.Config.ARGB_8888, error -> {
                    imgRes.setImageResource(RES_ERROR);

                    /*
                    Volley provides quite a detailed error description on request failure.
                       This is a sample implementation that simply creates an error message according to
                       the occurred error. You can define error codes based on the type of the error and
                       evaluate them to perform some actions. For possible VolleyError subclasses, see
                       the VolleyManager class constructor.
                    */

                    Toast.makeText(ImageActivity.this, VolleyManager.getInstance().checkError(error), Toast.LENGTH_LONG).show();
                });

                /*
                Volley allows us to tag requests which makes it possible to cancel specific
                    groups of requests by calling cancelAll(Tag) on the RequestQueue. This can be useful
                    when we no longer care for the result (Activity/Fragment was destroyed, operation
                    cancelled by user etc.). To cancel just one request, simply call cancel() on
                    the request object.
                 */
                imgRequest.setTag(CANCELABLE_REQUEST_TAG);

                /* start the download */
                VolleyManager.getInstance().getRequestQ(this).add(imgRequest);

                /* or simply use the NetworkImageView that is a part of Volley */
                //  ((NetworkImageView) findViewById(R.id.netImgResult)).setImageUrl(url, VolleyManager.getInstance().getImageLoader());

                /*
                    For more examples and details, check out the official docs at
                                        http://developer.android.com/training/volley/index.html
                    To grab Volley via Maven/Gradle visit the unofficial mirror at
                                        https://github.com/mcxiaoke/android-volley
                                                                                                  */

                break;
            case R.id.btnDownloadFresco:
                setContentView(R.layout.activity_image_libs);
                getSupportActionBar().setTitle("Fresco demo");
                final SimpleDraweeView drawee = findViewById(R.id.drawee);
                drawee.setVisibility(View.VISIBLE);

                /* the easiest way to load an image with Fresco - you simply set the Uri */
                //  drawee.setImageURI(Uri.parse(url));

                /* Let's do it with an animated image - download some .gif or .webp image to try*/
                /*
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
                */

                /* Progressive JPEG streaming - download a large JPEG to try. NOTE, from the official docs:
                    "keep in mind that not all JPEG images are encoded in progressive format, and for those that are not,
                     it is not possible to display them progressively." */
                //here I use the fully qualified class name 'cause we also have an ImageRequest defined by Volley
                com.facebook.imagepipeline.request.ImageRequest imgReq = ImageRequestBuilder.newBuilderWithSource(Uri.parse(url))
                        .setProgressiveRenderingEnabled(true)
                        .build();
                DraweeController controller = Fresco.newDraweeControllerBuilder()
                        .setImageRequest(imgReq)
                        .setOldController(drawee.getController())
                        .build();
                drawee.setController(controller);


                /*
                    To get some background and interesting details about Fresco,
                    visit https://code.facebook.com/posts/366199913563917/introducing-fresco-a-new-image-library-for-android/
                    To learn how to use it, check out the official docs at http://frescolib.org/docs/
                    To view the sources, visit the GitHub repository https://github.com/facebook/fresco
                                                                                                                    */

                break;
            case R.id.btnDownloadUIL:
                setContentView(R.layout.activity_image_libs);
                getSupportActionBar().setTitle("Universal Image Loader demo");
                final ProgressBar pbPercent = findViewById(R.id.pbImageLoading);
                pbPercent.setVisibility(View.VISIBLE);
                final TextView tvPerc = findViewById(R.id.tvPercent);
                tvPerc.setVisibility(View.VISIBLE);
                final ImageView imageView = findViewById(R.id.imgLibResult);

                /* initialize the image loader with some basic config */

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

                /* Load the image and get status callbacks. Loading image this way also gives you quite an extended error information
                  as well as allows you to update the loading progress */

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
                        /* we don't need to set the Bitmap into the ImageView since UIL will do this for us */
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

                /*
                 * To learn more about the Universal Image Loader, visit the official GitHub page:
                         https://github.com/nostra13/Android-Universal-Image-Loader
                 */

                break;
        }
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
