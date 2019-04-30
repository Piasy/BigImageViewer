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
import android.text.TextUtils;
import android.view.View;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;
import com.github.piasy.biv.BigImageViewer;
import com.github.piasy.biv.loader.fresco.FrescoImageLoader;
import com.github.piasy.biv.view.BigImageView;

public class ScaleTypeActivity extends AppCompatActivity {

    private Uri mSmallImage = Uri.parse(
            "http://img0.imgtn.bdimg.com/it/u=1404334591,1869466979&fm=21&gp=0.jpg");
    private Uri mMediumImage = Uri.parse(
            "http://img4.duitang.com/uploads/item/201507/12/20150712205801_UGTdu.png");
    private Uri mBigImage = Uri.parse(
            "http://h.hiphotos.baidu.com/zhidao/pic/item/1b4c510fd9f9d72afff98f28d22a2834349bbb62.jpg");

    private Spinner mScaleType;
    private Spinner mImageSize;
    private BigImageView mBigImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BigImageViewer.initialize(FrescoImageLoader.with(getApplicationContext()));

        setContentView(R.layout.activity_scale_type);

        mScaleType = findViewById(R.id.mScaleType);
        mImageSize = findViewById(R.id.mImageSize);
        mBigImageView = findViewById(R.id.mBigImage);

        findViewById(R.id.mBtnLoad).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setScaleType();
                showImage();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        BigImageViewer.imageLoader().cancelAll();
    }

    private void setScaleType() {
        String scaleType = (String) mScaleType.getSelectedItem();
        if (TextUtils.equals(scaleType, getString(R.string.scale_center_crop))) {
            mBigImageView.setInitScaleType(BigImageView.INIT_SCALE_TYPE_CENTER_CROP);
        } else if (TextUtils.equals(scaleType, getString(R.string.scale_center_inside))) {
            mBigImageView.setInitScaleType(BigImageView.INIT_SCALE_TYPE_CENTER_INSIDE);
        } else if (TextUtils.equals(scaleType, getString(R.string.scale_custom))) {
            mBigImageView.setInitScaleType(BigImageView.INIT_SCALE_TYPE_CUSTOM);
        } else if (TextUtils.equals(scaleType, getString(R.string.scale_start))) {
            mBigImageView.setInitScaleType(BigImageView.INIT_SCALE_TYPE_START);
        }
    }

    private void showImage() {
        String imageSize = (String) mImageSize.getSelectedItem();
        if (TextUtils.equals(imageSize, getString(R.string.size_small))) {
            mBigImageView.showImage(mSmallImage);
        } else if (TextUtils.equals(imageSize, getString(R.string.size_medium))) {
            mBigImageView.showImage(mMediumImage);
        } else if (TextUtils.equals(imageSize, getString(R.string.size_big))) {
            mBigImageView.showImage(mBigImage);
        }
    }
}
