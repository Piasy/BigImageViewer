/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2018 Piasy
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.piasy.biv.example;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.github.piasy.biv.BigImageViewer;
import com.github.piasy.biv.indicator.progresspie.ProgressPieIndicator;
import com.github.piasy.biv.loader.ImageLoader;
import com.github.piasy.biv.loader.fresco.FrescoImageLoader;
import com.github.piasy.biv.view.BigImageView;

import java.io.File;

public class ImageLoaderCallbackActivity extends AppCompatActivity {

    private void showToastOnUiThread(final String message) {
        runOnUiThread(new Runnable() {
            public void run() {
                showToast(message);
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(ImageLoaderCallbackActivity.this,
                message,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BigImageViewer.initialize(FrescoImageLoader.with(getApplicationContext()));

        setContentView(R.layout.activity_big_image);

        findViewById(R.id.mBtnLoad).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BigImageView bigImageView = findViewById(R.id.mBigImage);
                bigImageView.setProgressIndicator(new ProgressPieIndicator());
                bigImageView.setImageLoaderCallback(new ImageLoader.Callback() {
                    @Override
                    public void onCacheHit(int imageType, File image) {
                        final String message = "onCacheHit callback called, cached image " + image.getName();
                        Log.i("onCacheHit", message);
                        showToast(message);
                    }

                    @Override
                    public void onCacheMiss(int imageType, File image) {
                        final String message = "onCacheMiss callback called, fetching image " + image.getName();
                        Log.i("onCacheMiss", message);
                        showToastOnUiThread(message);
                    }

                    @Override
                    public void onBeforeSetImage(int imageType, File image, SubsamplingScaleImageView ssv) {
                        final String message = "onBeforeSetImage callback called, fetching image " + image.getName();
                        Log.i("onBeforeSetImage", message);
                        showToastOnUiThread(message);
                    }

                    @Override
                    public void onStart() {
                        final String message = "onStart callback called";
                        Log.i("onStart", message);
                        showToastOnUiThread(message);

                    }

                    @Override
                    public void onProgress(int progress) {
                        final String message = "onProgress callback called, progress is " + progress;
                        Log.i("onProgress", message);
                    }

                    @Override
                    public void onFinish() {
                        final String message = "onFinish callback called";
                        Log.i("onFinish", message);
                        showToastOnUiThread(message);
                    }

                    @Override
                    public void onSuccess(File image) {
                        final String message = "onSuccess callback called";
                        Log.i("onSuccess", message);
                        showToast(message);
                    }

                    @Override
                    public void onFail(Exception e) {
                        final String message = "onFail callback called";
                        Log.i("onFail", message);
                        showToast(message);
                    }
                });

                bigImageView.showImage(
                        Uri.parse("https://images.unsplash.com/photo-1497613913923-e07e0f465b12?dpr=2&auto=compress,format&fit=crop&w=376"),
                        Uri.parse("https://images.unsplash.com/photo-1497613913923-e07e0f465b12")
                );
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        BigImageViewer.imageLoader().cancelAll();
    }
}
