package vad.zuev.imagedownloader.activities;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.so.example.R;

import java.io.File;
import java.util.Locale;

import vad.zuev.imagedownloader.tools.BasicImageDownloader;
import vad.zuev.imagedownloader.tools.Utils;

/**
 * This Activity demonstrates how to download and save a single image using the {@link BasicImageDownloader}
 */
public class BasicDemoActivity extends AbsActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_download);
        ImageView img = findViewById(R.id.img_result);
        TextView tvPercent = findViewById(R.id.tv_percent);
        ProgressBar pbLoading = findViewById(R.id.pb);
        /* download the image  */
        BasicImageDownloader.download(this.imageUrl, true, new BasicImageDownloader.ImageDownloadListener() {
            @Override
            public void onError(BasicImageDownloader.ImageError error) {
                Utils.toastLong(BasicDemoActivity.this, "Error code " + error.getErrorCode() + ": " + error.getMessage());
                error.printStackTrace();
                img.setImageResource(ERROR_RES);
                tvPercent.setVisibility(View.GONE);
                pbLoading.setVisibility(View.GONE);
            }

            @Override
            public void onProgressChange(int percent) {
                /* display the download progress - this will run on the UI thread */
                pbLoading.setProgress(percent);
                tvPercent.setText(String.format(Locale.getDefault(), "%d%%", percent));
            }

            @Override
            public void onComplete(Bitmap result) {
                /* save the image - I'm gonna use JPEG */
                 Bitmap.CompressFormat format = Bitmap.CompressFormat.JPEG;
                  /* don't forget to include the extension into the file name */
                 File myImageFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +
                        File.separator + "image_test" + File.separator + BasicDemoActivity.this.fileName + "." + format.name().toLowerCase());
                // save the image
                BasicImageDownloader.writeToDisk(myImageFile, result, new BasicImageDownloader.OnBitmapSaveListener() {
                    @Override
                    public void onBitmapSaved() {
                        Utils.toastLong(BasicDemoActivity.this, "Image saved as: " + myImageFile.getAbsolutePath());
                    }

                    @Override
                    public void onBitmapSaveError(BasicImageDownloader.ImageError error) {
                        Utils.toastLong(BasicDemoActivity.this, "Error code " + error.getErrorCode() + ": " + error.getMessage());
                        error.printStackTrace();
                    }
                }, format, false);

                tvPercent.setVisibility(View.GONE);
                pbLoading.setVisibility(View.GONE);
                img.setImageBitmap(result);
                img.startAnimation(AnimationUtils.loadAnimation(BasicDemoActivity.this, android.R.anim.fade_in));
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // cancel any running download
        BasicImageDownloader.cancel();
    }
}
