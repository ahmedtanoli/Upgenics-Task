package com.azktanoli.upgenicstask.views;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ProgressBar;

import androidx.annotation.RequiresApi;

public class HorizontalProgressBar extends ProgressBar {

    public HorizontalProgressBar(Context context) {
        super(context, null, android.R.attr.progressBarStyleHorizontal);
        init();
    }

    public HorizontalProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs, android.R.attr.progressBarStyleHorizontal);
        init();
    }

    public HorizontalProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setIndeterminate(false);
        setMax(100);
    }

}
