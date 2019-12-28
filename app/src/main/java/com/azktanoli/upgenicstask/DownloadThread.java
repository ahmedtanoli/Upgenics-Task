package com.azktanoli.upgenicstask;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ProgressBar;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DownloadThread implements Runnable {

    private int threadNum;
    private String url;
    private Handler handler;
    private static final String TAG = DownloadThread.class.getSimpleName();

    public DownloadThread() {
    }

    public DownloadThread(int threadNum, String url, Handler handler) {
        this.threadNum = threadNum;
        this.url = url;
        this.handler = handler;
    }

    @Override
    public void run() {

        Log.d(TAG, "run: " + "Start Thread " + threadNum);
        if (downloadFile()) {
            Log.d(TAG, "run: " + "Download successful " + threadNum);
        } else {
            Log.d(TAG, "run: " + "Download failed " + threadNum);
        }
        Log.d(TAG, "run: " + "End Thread " + threadNum);
    }

    private void sendMessage(int what, long percentage) {
        Message message = handler.obtainMessage(what, percentage);
        message.sendToTarget();
    }


    private boolean downloadFile() {

        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(url).build();

            Response response = client.newCall(request).execute();

            InputStream in = response.body().byteStream();
            BufferedInputStream input = new BufferedInputStream(in);

            File downloadDir = new File(Environment.getExternalStorageDirectory() + "/Upgenics Task");
            if (!downloadDir.exists()) {
                downloadDir.mkdirs();
            }

            String fileName = System.currentTimeMillis() + url.substring(url.lastIndexOf('/') + 1, url.length());

            File file = new File(downloadDir, fileName);
            if (!file.exists()) {
                file.createNewFile();
            }

            FileOutputStream fileOutput =
                    new FileOutputStream(file);

            byte[] buffer = new byte[1024];

            int downloadedSize = 0;
            final Long totalSize = response.body().contentLength();

            int bufferLength = 0;
            while ((bufferLength = input.read(buffer)) > 0) {
                downloadedSize += bufferLength;
                fileOutput.write(buffer, 0, bufferLength);
                Log.d(TAG, "downloadFile: " + threadNum + " " + (downloadedSize*100)/totalSize);
                sendMessage(threadNum, (downloadedSize*100)/totalSize);
            }
            fileOutput.flush();
            fileOutput.close();
            input.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;

    }


}
