package vad.zuev.imagedownloader.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.so.example.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import static vad.zuev.imagedownloader.tools.Utils.toastShort;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final int REQ_STORAGE_PERM = Byte.MAX_VALUE;
    private EditText etUrl, etName;
    private int clickedBtnId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etUrl = findViewById(R.id.et_image_url);
        etName = findViewById(R.id.et_file_name);
        JSONObject versions = null;
        try {
            versions = new JSONObject(readVersions());
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        findViewById(R.id.btn_download_dm).setOnClickListener(this);
        findViewById(R.id.btn_download_basic).setOnClickListener(this);
        Button btnFresco = findViewById(R.id.btn_download_fresco);
        btnFresco.setOnClickListener(this);
        Button btnPicasso = findViewById(R.id.btn_download_picasso);
        btnPicasso.setOnClickListener(this);
        Button btnGlide = findViewById(R.id.btn_download_glide);
        btnGlide.setOnClickListener(this);
        Button btnUIL = findViewById(R.id.btn_download_uil);
        btnUIL.setOnClickListener(this);
        Button btnVolley = findViewById(R.id.btn_download_volley);
        btnVolley.setOnClickListener(this);
        if (versions != null) {
            appendVersion(btnFresco, versions.optString("fresco"));
            appendVersion(btnGlide, versions.optString("glide"));
            appendVersion(btnPicasso, versions.optString("picasso"));
            appendVersion(btnUIL, versions.optString("uil"));
            appendVersion(btnVolley, versions.optString("volley"));
        }
    }

    private void appendVersion(Button btn, String v) {
        btn.setText(String.format(Locale.getDefault(), "%1s %2s %3s", btn.getText(), "v.", v));
    }

    private String readVersions() throws IOException {
        int character;
        InputStream is = getAssets().open("versions.json");
        if (is == null)
            return null;
        StringBuilder sb = new StringBuilder();
        while ((character = is.read()) != -1)
            sb.append((char) character);
        return sb.toString();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_clear:
                etUrl.setText("");
                break;
            case R.id.action_paste:
                ClipboardManager manager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData data = manager != null ? manager.getPrimaryClip() : null;
                if (data == null) {
                    toastShort(this, "nothing found to paste");
                    return false;
                }
                ClipData.Item toPaste = data.getItemAt(0);
                etUrl.setText(toPaste.getText().toString());
                etUrl.setSelection(etUrl.getText().length());
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            clickedBtnId = v.getId();
            checkStoragePermissionBeforeProceed();
        } else {
            proceedWithDownload(v.getId());
        }
    }

    private void proceedWithDownload(int btnId) {
        String name = etName.getText().toString();
        if (name.isEmpty())
            name = "imgtest_" + Math.abs(new Random().nextInt());
        String url = etUrl.getText().toString().replaceAll("\\s+", "");
        try {
            new URL(url);
        } catch (MalformedURLException e) {
            url = getRandomPic();
            toastShort(this, "invalid URL, picking randomly");
        }
        Class clazz;
        switch (btnId) {
            case R.id.btn_download_basic:
                clazz = BasicDemoActivity.class;
                break;
            case R.id.btn_download_dm:
                clazz = DownloadManagerActivity.class;
                break;
            case R.id.btn_download_fresco:
                clazz = FrescoDemoActivity.class;
                break;
            case R.id.btn_download_glide:
                clazz = GlideDemoActivity.class;
                break;
            case R.id.btn_download_picasso:
                clazz = PicassoDemoActivity.class;
                break;
            case R.id.btn_download_uil:
                clazz = UILDemoActivity.class;
                break;
            case R.id.btn_download_volley:
                clazz = VolleyDemoActivity.class;
                break;
            default:
                return;
        }
        Intent i = new Intent(this, clazz);
        i.putExtra(AbsActivity.KEY_NAME, name);
        i.putExtra(AbsActivity.KEY_URL, url);
        startActivity(i);
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void checkStoragePermissionBeforeProceed() {
        List<String> needPermissions = new ArrayList<>();
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            needPermissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            needPermissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (!needPermissions.isEmpty())
            requestPermissions(needPermissions.toArray(new String[needPermissions.size()]), REQ_STORAGE_PERM);
        else
            proceedWithDownload(clickedBtnId);
    }

    private String getRandomPic() {
        /*--- random wallpaper images from the web -- for test/demo purposes only -- images may be subject to copyright --
         the author of this software does not claim to be the copyright holder of any of the images linked in the code below ---*/
        //TODO add a better solution
        String[] urls = new String[]{
                "http://www.technocrazed.com/wp-content/uploads/2015/12/Landscape-wallpaper-16.jpg",
                "https://i.pinimg.com/736x/04/56/b3/0456b3f17c5d5a82a09c97c9e46401cd--perspective-art-beach-sunsets.jpg",
                "https://static.pexels.com/photos/36762/scarlet-honeyeater-bird-red-feathers.jpg",
                "https://static.pexels.com/photos/132037/pexels-photo-132037.jpeg",
                "http://www.funmag.org/wp-content/uploads/2012/03/beautiful-nature-wallpapers-4.jpg",
                "http://wallpapers-library.com/images/nature-wallpapers-hd-for-desktop/nature-wallpapers-hd-for-desktop-1.jpg",
                "https://s-media-cache-ak0.pinimg.com/originals/36/6d/c7/366dc7004b1724dca09115766200080f.jpg",
                "https://wallpapersfun.files.wordpress.com/2011/07/island-and-moon-3d-landscape-wallpapers.jpg",
                "http://7-themes.com/data_images/out/39/6904304-japan-landscape-wallpaper.jpg"};
        return urls[new Random().nextInt(urls.length - 1)];
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_STORAGE_PERM) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED)
                toastShort(this, "missing permission: reading files will fail");
            if (grantResults[1] != PackageManager.PERMISSION_GRANTED)
                toastShort(this, "missing permission: writing files will fail");
            proceedWithDownload(clickedBtnId);
        }
    }
}
