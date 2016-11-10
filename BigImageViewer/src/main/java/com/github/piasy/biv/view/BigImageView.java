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

package com.github.piasy.biv.view;

import android.Manifest;
import android.content.Context;
import android.graphics.PointF;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.github.piasy.biv.BigImageViewer;
import com.github.piasy.biv.loader.ImageLoader;
import com.tbruyelle.rxpermissions2.RxPermissions;
import io.reactivex.functions.Consumer;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Piasy{github.com/Piasy} on 06/11/2016.
 *
 * Use FrameLayout for extensibility.
 */

public class BigImageView extends FrameLayout implements ImageLoader.Callback {
    private final SubsamplingScaleImageView mImageView;

    private final ImageLoader mImageLoader;

    private final List<File> mTempImages;
    // credit: https://github.com/Piasy/BigImageViewer/issues/2
    private final SubsamplingScaleImageView.OnImageEventListener mEventListener
            = new SubsamplingScaleImageView.OnImageEventListener() {
        @Override
        public void onReady() {
            float result = 0.5f;
            int imageWidth = mImageView.getSWidth();
            int imageHeight = mImageView.getSHeight();
            int viewWidth = mImageView.getWidth();
            int viewHeight = mImageView.getHeight();

            boolean hasZeroValue = false;
            if (imageWidth == 0 || imageHeight == 0 || viewWidth == 0 || viewHeight == 0) {
                result = 0.5f;
                hasZeroValue = true;
            }

            if (!hasZeroValue) {
                if (imageWidth <= imageHeight) {
                    result = (float) viewWidth / imageWidth;
                } else {
                    result = (float) viewHeight / imageHeight;
                }
            }

            if (!hasZeroValue) {
                if ((float) imageHeight / imageWidth > 1.5f) {
                    // scale at top
                    mImageView
                            .animateScaleAndCenter(result, new PointF(imageWidth / 2, 0))
                            .withEasing(SubsamplingScaleImageView.EASE_OUT_QUAD)
                            .start();
                } else {
                    // scale at center
                    mImageView
                            .animateScaleAndCenter(result,
                                    new PointF(viewWidth / 2, viewHeight / 2))
                            .withEasing(SubsamplingScaleImageView.EASE_OUT_QUAD)
                            .start();
                }
            }

            // `对结果进行放大裁定，防止计算结果跟双击放大结果过于相近`
            if (Math.abs(result - 0.1) < 0.2f) {
                result += 0.2f;
            }

            mImageView.setDoubleTapZoomScale(result);
        }

        @Override
        public void onImageLoaded() {

        }

        @Override
        public void onPreviewLoadError(Exception e) {

        }

        @Override
        public void onImageLoadError(Exception e) {

        }

        @Override
        public void onTileLoadError(Exception e) {

        }

        @Override
        public void onPreviewReleased() {

        }
    };

    private ImageSaveCallback mImageSaveCallback;
    private File mCurrentImageFile;

    public BigImageView(Context context) {
        this(context, null);
    }

    public BigImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BigImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mImageView = new SubsamplingScaleImageView(context, attrs);
        addView(mImageView);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        mImageView.setLayoutParams(params);
        mImageView.setMinimumTileDpi(160);
        mImageView.setOnImageEventListener(mEventListener);

        mImageLoader = BigImageViewer.imageLoader();

        mTempImages = new ArrayList<>();
    }

    @Override
    public void setOnClickListener(OnClickListener listener) {
        mImageView.setOnClickListener(listener);
    }

    @Override
    public void setOnLongClickListener(OnLongClickListener listener) {
        mImageView.setOnLongClickListener(listener);
    }

    public void setImageSaveCallback(ImageSaveCallback imageSaveCallback) {
        mImageSaveCallback = imageSaveCallback;
    }

    public void saveImageIntoGallery() {
        if (mCurrentImageFile == null) {
            if (mImageSaveCallback != null) {
                mImageSaveCallback.onFail(new IllegalStateException("image not downloaded yet"));
            }

            return;
        }

        RxPermissions.getInstance(getContext())
                .request(Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean grant) throws Exception {
                        if (grant) {
                            doSaveImageIntoGallery();
                        } else {
                            if (mImageSaveCallback != null) {
                                mImageSaveCallback.onFail(
                                        new RuntimeException("Permission denied"));
                            }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (mImageSaveCallback != null) {
                            mImageSaveCallback.onFail(throwable);
                        }
                    }
                });
    }

    private void doSaveImageIntoGallery() {
        try {
            String result = MediaStore.Images.Media.insertImage(getContext().getContentResolver(),
                    mCurrentImageFile.getAbsolutePath(), mCurrentImageFile.getName(), "");
            if (mImageSaveCallback != null) {
                if (!TextUtils.isEmpty(result)) {
                    mImageSaveCallback.onSuccess(result);
                } else {
                    mImageSaveCallback.onFail(new RuntimeException("saveImageIntoGallery fail"));
                }
            }
        } catch (FileNotFoundException e) {
            if (mImageSaveCallback != null) {
                mImageSaveCallback.onFail(e);
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        for (int i = 0, size = mTempImages.size(); i < size; i++) {
            mTempImages.get(i).delete();
        }
        mTempImages.clear();
    }

    public void showImage(Uri uri) {
        Log.d("BigImageView", "showImage " + uri);

        mImageLoader.loadImage(uri, this);
    }

    @UiThread
    @Override
    public void onCacheHit(File image) {
        Log.d("BigImageView", "onCacheHit " + image);

        mCurrentImageFile = image;
        doShowImage(image);
    }

    @WorkerThread
    @Override
    public void onCacheMiss(final File image) {
        Log.d("BigImageView", "onCacheMiss " + image);

        mCurrentImageFile = image;
        mTempImages.add(image);
        post(new Runnable() {
            @Override
            public void run() {
                doShowImage(image);
            }
        });
    }

    @UiThread
    private void doShowImage(File image) {
        mImageView.setImage(ImageSource.uri(Uri.fromFile(image)));
    }
}
