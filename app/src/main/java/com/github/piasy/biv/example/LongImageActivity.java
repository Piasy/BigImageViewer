package com.github.piasy.biv.example;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import com.github.piasy.biv.BigImageViewer;
import com.github.piasy.biv.loader.fresco.FrescoImageLoader;
import com.github.piasy.biv.view.BigImageView;

public class LongImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        BigImageViewer.initialize(FrescoImageLoader.with(getApplicationContext()));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_big_image);

        findViewById(R.id.mBtnLoad).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BigImageView bigImageView = (BigImageView) findViewById(R.id.mBigImage);
                bigImageView.showImage(
                        Uri.parse("http://ww3.sinaimg.cn/mw690/005Fj2RDgw1f9lkbljz9nj30c840mx35.jpg"));
            }
        });
    }
}
