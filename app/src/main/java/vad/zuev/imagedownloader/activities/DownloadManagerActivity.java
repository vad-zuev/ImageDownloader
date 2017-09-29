package vad.zuev.imagedownloader.activities;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.ImageView;
import android.widget.TextView;

import com.so.example.R;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

/**
 * This Activity uses Android's {@link DownloadManager} for downloading an image. The
 * image is set into an ImageView after the download.<br>
 * <b>Note: </b> using the DownloadManager for downloading AND displaying images is not an
 * efficient way to manage images. This is for demo purposes only.
 */
public class DownloadManagerActivity extends AbsActivity {

    // this receiver will be notified of download completion
    private BroadcastReceiver downloadCompleteReceiver;
    private DownloadManager dm;
    private long downloadId;
    private boolean completed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_manager);

        TextView tvStatus = findViewById(R.id.tv_info);

        DownloadManager.Request request;

        // setting up the request can fail
        try {
            request = new DownloadManager.Request(Uri.parse(imageUrl));
        } catch (IllegalArgumentException e) {
            tvStatus.setText(String.format("Error: %s", e.getMessage()));
            return;
        }
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        // title and description will be visible to the user
        request.setTitle("Download Manager Example");
        request.setDescription("Downloading image");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        // DownloadManager can be used to download any kind of file - but we will only allow images
        String[] allowedTypes = {"png", "jpg", "jpeg", "gif", "webp"};
        String suffix = imageUrl.substring(imageUrl.lastIndexOf(".") + 1).toLowerCase();
        if (!Arrays.asList(allowedTypes).contains(suffix)) {
            tvStatus.setText("Invalid file extension. Allowed types: \n");
            for (String s : allowedTypes)
                tvStatus.append("\n" + "." + s);
            return;
        }
        // this is where you specify the destination file
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS +
                File.separator + "image_test", fileName + "." + suffix);
        // we allow the system MediaScanner to scan the downloaded file
        request.allowScanningByMediaScanner();
        dm = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        assert dm != null;

        // create a receiver that will listen for download complete events
        downloadCompleteReceiver = new BroadcastReceiver() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onReceive(Context context, Intent intent) {
                if (downloadId == intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1L)) {
                    DownloadManager.Query query = new DownloadManager.Query();
                    query.setFilterById(downloadId);
                    Cursor cursor = dm.query(query);
                    if (!cursor.moveToFirst()) {
                        tvStatus.setText("Error: cursor is empty");
                        return;
                    }
                    if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) != DownloadManager.STATUS_SUCCESSFUL) {
                        tvStatus.setText("Download failed: no success status");
                        return;
                    }
                    try {
                        // get the actual file path
                        String path = new File(new URI(cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)))).getAbsolutePath();
                        tvStatus.setText("File download complete. Location: \n" + path);
                        ((ImageView) findViewById(R.id.img_result)).setImageBitmap(BitmapFactory.decodeFile(path));
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                    completed = true;
                }
            }
        };
        // do not forget to register the receiver
        registerReceiver(downloadCompleteReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        // unique ID of this download
        downloadId = dm.enqueue(request);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(downloadCompleteReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // cancel the download if it is still pending/running
        if (!completed)
            dm.remove(downloadId);
    }
}
