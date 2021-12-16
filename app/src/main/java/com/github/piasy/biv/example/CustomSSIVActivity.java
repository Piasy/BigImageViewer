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

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.github.piasy.biv.BigImageViewer;
import com.github.piasy.biv.indicator.progresspie.ProgressPieIndicator;
import com.github.piasy.biv.loader.fresco.FrescoImageLoader;
import com.github.piasy.biv.view.BigImageView;
import com.github.piasy.biv.view.ImageViewFactory;

public class CustomSSIVActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BigImageViewer.initialize(FrescoImageLoader.with(getApplicationContext()));

        setContentView(R.layout.activity_custom_ssiv);

        // more image resize, use https://docs.imgix.com/apis/rendering api
        Button btn1 = findViewById(R.id.mBtnLoad1);
        btn1.setText(String.format("%s default[3520x2347]", getText(R.string.bttn_load)));
        btn1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        BigImageView bigImageView = findViewById(R.id.mBigImage);
                        bigImageView.setProgressIndicator(new ProgressPieIndicator());
                        bigImageView.setImageViewFactory(new ImageViewFactory() {
                            @Override
                            protected SubsamplingScaleImageView createStillImageView(final Context context) {
                                return new MySSIV(context);
                            }
                        });

                        bigImageView.showImage(
                                // 3520x2347
                                Uri.parse("https://images.unsplash.com/photo-1497240299146-17ff4089466a?dpr=2&auto=compress,format&fit=crop&w=4376"),
                                Uri.parse("https://images.unsplash.com/photo-1497240299146-17ff4089466a?dpr=2&auto=compress,format&w=4376")
                        );
                    }
                });

        Button btn2 = findViewById(R.id.mBtnLoad2);
        btn2.setText(String.format("%s custom[4096 x 2731]", getText(R.string.bttn_load)));
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BigImageView bigImageView = findViewById(R.id.mBigImage);
                bigImageView.setProgressIndicator(new ProgressPieIndicator());
                // custom INIT_SCALE_TYPE_CUSTOM|OptimizeDisplay
                bigImageView.setInitScaleType(BigImageView.INIT_SCALE_TYPE_CUSTOM);
                bigImageView.setOptimizeDisplay(true);
                bigImageView.setImageViewFactory(new ImageViewFactory() {
                    @Override
                    protected SubsamplingScaleImageView createStillImageView(final Context context) {
                        return new MySSIV(context);
                    }
                });

                bigImageView.showImage(
                        // 4096 x 2731
                        Uri.parse("https://images.unsplash.com/photo-1497240299146-17ff4089466a?dpr=2&auto=compress,format&fit=crop&w=4096"),
                        Uri.parse("https://images.unsplash.com/photo-1497240299146-17ff4089466a?dpr=2&auto=compress,format&fit=crop&w=4096")
                );
            }
        });

        Button btn3 = findViewById(R.id.mBtnLoad3);
        btn3.setText(String.format("%s custom[1512x2016]", getText(R.string.bttn_load)));
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BigImageView bigImageView = findViewById(R.id.mBigImage);
                bigImageView.setProgressIndicator(new ProgressPieIndicator());
                // custom INIT_SCALE_TYPE_CUSTOM|OptimizeDisplay
                bigImageView.setOptimizeDisplay(true);
                bigImageView.setInitScaleType(BigImageView.INIT_SCALE_TYPE_CUSTOM);
                bigImageView.setImageViewFactory(new ImageViewFactory() {
                    @Override
                    protected SubsamplingScaleImageView createStillImageView(final Context context) {
                        return new MySSIV(context);
                    }
                });

                bigImageView.showImage(
                        // 1512x2016
                        Uri.parse("https://images.unsplash.com/photo-1497240299146-17ff4089466a?auto=compress,format&fit=crop&w=1512&h=2016"),
                        Uri.parse("https://images.unsplash.com/photo-1497240299146-17ff4089466a?auto=compress,format&fit=crop&w=1512&h=2016")
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
