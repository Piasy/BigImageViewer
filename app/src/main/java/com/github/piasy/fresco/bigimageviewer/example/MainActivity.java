package com.github.piasy.fresco.bigimageviewer.example;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import com.github.piasy.fresco.bigimageviewer.BigImageView;
import com.github.piasy.fresco.bigimageviewer.FrescoBigImageViewer;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FrescoBigImageViewer.initialize(getApplicationContext());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
