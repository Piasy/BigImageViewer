package com.github.piasy.biv.example;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import com.github.piasy.biv.BigImageViewer;
import com.github.piasy.biv.loader.glide.GlideImageLoader;
import com.github.piasy.biv.view.BigImageView;

public class GlideLoaderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        BigImageViewer.initialize(GlideImageLoader.with(getApplicationContext()));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_big_image);

        findViewById(R.id.mBtnLoad).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BigImageView bigImageView = (BigImageView) findViewById(R.id.mBigImage);
                bigImageView.showImage(
                        Uri.parse("http://code2png.babits.top/images/code_1477885912.cpp.png"));
            }
        });
    }
}
