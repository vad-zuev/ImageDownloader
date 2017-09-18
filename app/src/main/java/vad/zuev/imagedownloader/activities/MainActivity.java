package vad.zuev.imagedownloader.activities;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.so.example.R;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etUrl = findViewById(R.id.etImageUrl);
        findViewById(R.id.btnDownloadBasic).setOnClickListener(this);
        findViewById(R.id.btnDownloadFresco).setOnClickListener(this);
        findViewById(R.id.btnDownloadPicasso).setOnClickListener(this);
        findViewById(R.id.btnDownloadGlide).setOnClickListener(this);
        findViewById(R.id.btnDownloadUIL).setOnClickListener(this);
        findViewById(R.id.btnDownloadVolley).setOnClickListener(this);
        findViewById(R.id.btnDownloadDM).setOnClickListener(this);
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
                    Toast.makeText(this, "nothing found to paste", Toast.LENGTH_SHORT).show();
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
        String name = ((EditText) findViewById(R.id.etImageName)).getText().toString();
        if (name.isEmpty()) name = "imgtest_" + Math.abs(new Random().nextInt());
        String url = etUrl.getText().toString().replaceAll("\\s+", "");
        try {
            new URL(url);
        } catch (MalformedURLException e) {
            url = getRandomPic();
            Toast.makeText(this, "invalid URL, picking randomly", Toast.LENGTH_SHORT).show();
        }
        Intent i = new Intent(this, ImageActivity.class);
        i.putExtra(ImageActivity.KEY_NAME, name);
        i.putExtra(ImageActivity.KEY_URL, url);
        i.putExtra(ImageActivity.KEY_SELECTED_BTN, v.getId());
        startActivity(i);
    }

    private String getRandomPic() {
        /*--- random wallpaper images from the web -- for test/demo purposes only -- images may be subject to copyright --
         the author of this software does not claim to be the copyright holder of any of the images linked in the code below ---*/
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
}
