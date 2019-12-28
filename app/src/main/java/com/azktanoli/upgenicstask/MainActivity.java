package com.azktanoli.upgenicstask;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.azktanoli.upgenicstask.utils.Constants;
import com.azktanoli.upgenicstask.views.HorizontalProgressBar;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements Handler.Callback {

    private int totalItems = 10;
    private LinearLayout linearLayout;
    private static final int REQ_WRITE = 120;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(contentView());

        if (!writePermissionGranted()) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQ_WRITE);
        } else {
            useThreadPool();
        }

    }

    private LinearLayout contentView() {

        // Parent View
        linearLayout = new LinearLayout(this);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        for (int i = 0; i < totalItems; i++) {

            HorizontalProgressBar horizontalProgressBar = new HorizontalProgressBar(new ContextThemeWrapper(this, android.R.style.Widget_DeviceDefault_Light_ProgressBar_Horizontal));
            horizontalProgressBar.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            horizontalProgressBar.setProgress(0);
            linearLayout.addView(horizontalProgressBar);

        }

        return linearLayout;

    }

    @Override
    public boolean handleMessage(@NonNull final Message msg) {
        Log.d(MainActivity.class.getSimpleName(), "handleMessage: " + msg.what + " " + (int) (long) msg.obj);
        ProgressBar progressBar = (ProgressBar) linearLayout.getChildAt(msg.what);
        if (progressBar != null) {
            progressBar.setProgress((int) (long) msg.obj);
        }
        return true;
    }

    private boolean writePermissionGranted() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQ_WRITE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            useThreadPool();
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void useThreadPool() {

        ExecutorService executorService = Executors.newFixedThreadPool(5);

        for (int i = 0; i < totalItems; i++) {

            executorService.execute(new DownloadThread(i, Constants.mediumImageUrl, new Handler(this)));

        }

        executorService.shutdown();
    }
}

