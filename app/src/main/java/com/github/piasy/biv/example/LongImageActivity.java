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

import android.Manifest;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.piasy.biv.BigImageViewer;
import com.github.piasy.biv.indicator.progresspie.ProgressPieIndicator;
import com.github.piasy.biv.loader.fresco.FrescoImageLoader;
import com.github.piasy.biv.view.BigImageView;
import com.github.piasy.biv.view.ImageSaveCallback;
import com.github.piasy.rxqrcode.RxQrCode;
import com.google.zxing.Result;
import com.tbruyelle.rxpermissions2.RxPermissions;
import hu.akarnokd.rxjava.interop.RxJavaInterop;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class LongImageActivity extends AppCompatActivity {

    private BigImageView mBigImageView;
    private Disposable mPermissionRequest;
    private Disposable mQrCodeDecode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BigImageViewer.initialize(FrescoImageLoader.with(getApplicationContext()));

        setContentView(R.layout.activity_big_image);

        mBigImageView = findViewById(R.id.mBigImage);
        mBigImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mBigImageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new MaterialDialog.Builder(LongImageActivity.this)
                        .items(R.array.big_image_ops)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View itemView,
                                    int position, CharSequence text) {
                                if (TextUtils.equals(text, getString(R.string.save_image))) {
                                    saveImage();
                                } else if (TextUtils.equals(text,
                                        getString(R.string.scan_qr_code))) {
                                    decodeQrCode();
                                }
                            }
                        })
                        .show();
                return true;
            }
        });

        mBigImageView.setImageSaveCallback(new ImageSaveCallback() {
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

        mBigImageView.setProgressIndicator(new ProgressPieIndicator());

        findViewById(R.id.mBtnLoad).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBigImageView.showImage(Uri.parse(
                        "http://ww1.sinaimg.cn/mw690/005Fj2RDgw1f9mvl4pivvj30c82ougw3.jpg"));
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        disposePermissionRequest();
        disposeQrCodeDecode();

        BigImageViewer.imageLoader().cancelAll();
    }

    private void decodeQrCode() {
        if (mBigImageView.getCurrentImageFile() == null) {
            return;
        }
        disposeQrCodeDecode();
        mQrCodeDecode = RxJavaInterop
                .toV2Observable(RxQrCode.scanFromPicture(
                        mBigImageView.getCurrentImageFile().getAbsolutePath()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Result>() {
                    @Override
                    public void accept(Result result) throws Exception {
                        Toast.makeText(LongImageActivity.this,
                                "Found " + result.getText(),
                                Toast.LENGTH_SHORT).show();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(LongImageActivity.this,
                                "Not found",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @SuppressWarnings("MissingPermission")
    private void saveImage() {
        disposePermissionRequest();
        mPermissionRequest = new RxPermissions(this)
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean granted) throws Exception {
                        if (granted) {
                            mBigImageView.saveImageIntoGallery();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }

    private void disposePermissionRequest() {
        if (mPermissionRequest != null) {
            mPermissionRequest.dispose();
        }
    }

    private void disposeQrCodeDecode() {
        if (mQrCodeDecode != null) {
            mQrCodeDecode.dispose();
        }
    }
}
