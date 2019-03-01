package com.example.srct.aiscreen_part2;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.samsung.strdivider.DividerEngine;
import com.samsung.strdivider.DividerResultCallBack;
import com.samsung.strdivider.strrecognizer.BitmapUtils;
import com.samsung.strdivider.strrecognizer.Util;

import java.util.ArrayList;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    private static final int REQUEST_PERMISSION = 1;
    private static final int REQUEST_SELECT_IMAGE = 2;
    private ProgressBar mProgressBar= null;
    private int mResultCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        byte [] bis=getIntent().getByteArrayExtra("bitmap");
//        Bitmap bitmap= BitmapFactory.decodeByteArray(bis, 0, bis.length);
//        ImageView image = (ImageView)findViewById(R.id.image);
        mProgressBar = (ProgressBar) findViewById(R.id.progressbar);
        mProgressBar.setVisibility(View.INVISIBLE);
       // image.setImageBitmap(bitmap);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // If you do not have permission, request it
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSION);
            Log.d(TAG, "lzh request permission");
        }else {
            DividerEngine.getInstance().initialize(this);
            process();
        }

    }

    private void process() {

            mProgressBar.setVisibility(View.VISIBLE);
            byte[] bis = getIntent().getByteArrayExtra("bitmap");
            if (bis != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bis, 0, bis.length);
                DividerEngine.getInstance().startRecognize(this, bitmap, new DividerResultCallBack() {
                    @Override
                    public void onSuccess(ArrayList<String> listWords) {
                        Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_LONG).show();
                        String result = "";
                        for (String word : listWords) {
                            result = result + word + "| ";
                        }
                        Log.d(TAG, "lzh result = " + result);
                        DividerEngine.getInstance().showCards(MainActivity.this);
                        mProgressBar.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onFailure(String errorMsg) {
                        Toast.makeText(MainActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                    }
                });
            }
        }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        process();
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        process();
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults.length == 3 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                //only can initialize the engine after get WRITE_EXTERNAL_STORAGE and READ_PHONE_STATE permission
                DividerEngine.getInstance().initialize(this);
                process();
            }

        } else {
            Toast.makeText(this.getApplicationContext(), "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }
}
