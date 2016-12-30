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
import android.content.res.TypedArray;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.RequiresPermission;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.widget.FrameLayout;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.github.piasy.biv.BigImageViewer;
import com.github.piasy.biv.R;
import com.github.piasy.biv.indicator.ProgressIndicator;
import com.github.piasy.biv.loader.ImageLoader;
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
    public static final int INIT_SCALE_TYPE_CENTER_INSIDE = 1;
    public static final int INIT_SCALE_TYPE_CENTER_CROP = 2;
    public static final int INIT_SCALE_TYPE_AUTO = 3;

    private final SubsamplingScaleImageView mImageView;

    private final ImageLoader mImageLoader;
    private final List<File> mTempImages;

    private View mProgressIndicatorView;
    private View mThumbnailView;

    private ImageSaveCallback mImageSaveCallback;
    private File mCurrentImageFile;
    private Uri mThumbnail;

    private ProgressIndicator mProgressIndicator;
    private final ProgressNotifyRunnable mProgressNotifyRunnable
            = new ProgressNotifyRunnable() {
        @Override
        public void run() {
            if (mProgressIndicator != null) {
                mProgressIndicator.onProgress(mProgress);
                notified();
            }
        }
    };
    private DisplayOptimizeListener mDisplayOptimizeListener;
    private int mInitScaleType;

    public BigImageView(Context context) {
        this(context, null);
    }

    public BigImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BigImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray array = context.getTheme()
                .obtainStyledAttributes(attrs, R.styleable.BigImageView, defStyleAttr, 0);
        mInitScaleType = array.getInteger(R.styleable.BigImageView_initScaleType,
                INIT_SCALE_TYPE_CENTER_INSIDE);
        array.recycle();

        mImageView = new SubsamplingScaleImageView(context, attrs);
        addView(mImageView);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        mImageView.setLayoutParams(params);
        mImageView.setMinimumTileDpi(160);

        mDisplayOptimizeListener = new DisplayOptimizeListener(mImageView);
        mImageView.setOnImageEventListener(mDisplayOptimizeListener);

        setInitScaleType(mInitScaleType);

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

    public void setInitScaleType(int initScaleType) {
        mInitScaleType = initScaleType;
        switch (initScaleType) {
            case INIT_SCALE_TYPE_CENTER_CROP:
                mImageView.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CENTER_CROP);
                break;
            case INIT_SCALE_TYPE_AUTO:
                mImageView.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CUSTOM);
                break;
            case INIT_SCALE_TYPE_CENTER_INSIDE:
            default:
                mImageView.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CENTER_INSIDE);
                break;
        }
        mDisplayOptimizeListener.setInitScaleType(initScaleType);
    }

    public void setImageSaveCallback(ImageSaveCallback imageSaveCallback) {
        mImageSaveCallback = imageSaveCallback;
    }

    public void setProgressIndicator(ProgressIndicator progressIndicator) {
        mProgressIndicator = progressIndicator;
    }

    public String currentImageFile() {
        return mCurrentImageFile == null ? "" : mCurrentImageFile.getAbsolutePath();
    }

    @RequiresPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public void saveImageIntoGallery() {
        if (mCurrentImageFile == null) {
            if (mImageSaveCallback != null) {
                mImageSaveCallback.onFail(new IllegalStateException("image not downloaded yet"));
            }

            return;
        }

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

        mThumbnail = Uri.EMPTY;
        mImageLoader.loadImage(uri, this);
    }

    public void showImage(Uri thumbnail, Uri uri) {
        Log.d("BigImageView", "showImage with thumbnail " + thumbnail + ", " + uri);

        mThumbnail = thumbnail;
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

    @WorkerThread
    @Override
    public void onStart() {
        post(new Runnable() {
            @Override
            public void run() {
                // why show thumbnail in onStart? because we may not need download it from internet
                if (mThumbnail != Uri.EMPTY) {
                    mThumbnailView = mImageLoader.showThumbnail(BigImageView.this, mThumbnail,
                            mInitScaleType);
                    addView(mThumbnailView);
                }

                if (mProgressIndicator != null) {
                    mProgressIndicatorView = mProgressIndicator.getView(BigImageView.this);
                    addView(mProgressIndicatorView);
                    mProgressIndicator.onStart();
                }
            }
        });
    }

    @WorkerThread
    @Override
    public void onProgress(final int progress) {
        if (mProgressIndicator != null && mProgressNotifyRunnable.update(progress)) {
            post(mProgressNotifyRunnable);
        }
    }

    @WorkerThread
    @Override
    public void onFinish() {
        post(new Runnable() {
            @Override
            public void run() {
                AnimationSet set = new AnimationSet(true);
                AlphaAnimation animation = new AlphaAnimation(1, 0);
                animation.setDuration(500);
                animation.setFillAfter(true);
                set.addAnimation(animation);
                if (mThumbnailView != null) {
                    mThumbnailView.setAnimation(set);
                }

                if (mProgressIndicator != null) {
                    mProgressIndicator.onFinish();
                }

                if (mProgressIndicatorView != null) {
                    mProgressIndicatorView.setAnimation(set);
                }

                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        if (mThumbnailView != null) {
                            mThumbnailView.setVisibility(GONE);
                        }
                        if (mProgressIndicatorView != null) {
                            mProgressIndicatorView.setVisibility(GONE);
                        }
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });
            }
        });
    }

    @UiThread
    private void doShowImage(File image) {
        mImageView.setImage(ImageSource.uri(Uri.fromFile(image)));
    }
}
