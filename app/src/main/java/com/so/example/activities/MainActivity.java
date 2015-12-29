package com.so.example.activities;

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

import java.util.Random;

/**
 * @author Vadim Zuev
 * @version 1.0
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etUrl = (EditText) findViewById(R.id.etImageUrl);
        findViewById(R.id.btnDownloadBasic).setOnClickListener(this);
        findViewById(R.id.btnDownloadFresco).setOnClickListener(this);
        findViewById(R.id.btnDownloadPicasso).setOnClickListener(this);
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
                ClipData data = ((ClipboardManager) getSystemService(CLIPBOARD_SERVICE)).getPrimaryClip();
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
        String url = etUrl.getText().toString().replaceAll("\\s+","");
        if(url.length() < 6){
            Toast.makeText(this, "url too short", Toast.LENGTH_SHORT).show();
            etUrl.setText("");
            return;
        }
        Intent i = new Intent(this, ImageActivity.class);
        i.putExtra(ImageActivity.KEY_NAME, name);
        i.putExtra(ImageActivity.KEY_URL, url);
        i.putExtra(ImageActivity.KEY_SELECTED_BTN, v.getId());
        startActivity(i);
    }
}
