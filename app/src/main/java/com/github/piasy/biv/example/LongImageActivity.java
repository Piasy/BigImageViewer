/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Piasy
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
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.piasy.biv.BigImageViewer;
import com.github.piasy.biv.indicator.progresspie.ProgressPieIndicator;
import com.github.piasy.biv.loader.fresco.FrescoImageLoader;
import com.github.piasy.biv.view.BigImageView;
import com.github.piasy.biv.view.ImageSaveCallback;

public class LongImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        BigImageViewer.initialize(FrescoImageLoader.with(getApplicationContext()));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_big_image);

        final BigImageView bigImageView = (BigImageView) findViewById(R.id.mBigImage);
        bigImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        bigImageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new MaterialDialog.Builder(LongImageActivity.this)
                        .items(R.array.big_image_ops)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View itemView,
                                    int position, CharSequence text) {
                                if (TextUtils.equals(text, getString(R.string.save_image))) {
                                    bigImageView.saveImageIntoGallery();
                                } else if (TextUtils.equals(text,
                                        getString(R.string.scan_qr_code))) {
                                    Toast.makeText(LongImageActivity.this,
                                            "TODO: 10/11/2016 open source RxQrCode "
                                            + bigImageView.currentImageFile(),
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .show();
                return true;
            }
        });

        bigImageView.setImageSaveCallback(new ImageSaveCallback() {
            @Override
            public void onSuccess(String uri) {
                Toast.makeText(LongImageActivity.this,
                        "Success",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFail(Throwable t) {
                t.printStackTrace();
                Toast.makeText(LongImageActivity.this,
                        "Fail",
                        Toast.LENGTH_SHORT).show();
            }
        });

        bigImageView.setProgressIndicator(new ProgressPieIndicator());

        findViewById(R.id.mBtnLoad).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bigImageView.showImage(Uri.parse(
                        "http://ww1.sinaimg.cn/mw690/005Fj2RDgw1f9mvl4pivvj30c82ougw3.jpg"));
            }
        });
    }
}
