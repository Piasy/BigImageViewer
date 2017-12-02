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
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.RequiresPermission;
import android.support.annotation.UiThread;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
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
 * <p>
 * Use FrameLayout for extensibility.
 */

public class BigImageView extends FrameLayout implements ImageLoader.Callback {
    public static final int INIT_SCALE_TYPE_CENTER_INSIDE = 1;
    public static final int INIT_SCALE_TYPE_CENTER_CROP = 2;
    public static final int INIT_SCALE_TYPE_CUSTOM = 3;
    public static final int INIT_SCALE_TYPE_START = 4;

    public static final int IMAGE_SCALE_TYPE_FIT_CENTER_INDEX = 3;
    public static final ImageView.ScaleType[] IMAGE_SCALE_TYPES = {
            ImageView.ScaleType.CENTER,
            ImageView.ScaleType.CENTER_CROP,
            ImageView.ScaleType.CENTER_INSIDE,
            ImageView.ScaleType.FIT_CENTER,
            ImageView.ScaleType.FIT_END,
            ImageView.ScaleType.FIT_START,
            ImageView.ScaleType.FIT_XY,
    };

    private final ImageLoader mImageLoader;
    private final List<File> mTempImages;
    private final ImageLoader.Callback mInternalCallback;

    private SubsamplingScaleImageView mImageView;

    private View mProgressIndicatorView;
    private View mThumbnailView;
    private ImageView mFailureImageView;

    private ImageSaveCallback mImageSaveCallback;
    private ImageLoader.Callback mUserCallback;
    private File mCurrentImageFile;
    private Uri mUri;
    private Uri mThumbnail;

    private ProgressIndicator mProgressIndicator;
    private DisplayOptimizeListener mDisplayOptimizeListener;
    private int mInitScaleType;
    private ImageView.ScaleType mFailureImageScaleType;
    private boolean mOptimizeDisplay;
    private int mCustomSsivId;
    private boolean mTapToRetry;

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

        if (array.hasValue(R.styleable.BigImageView_failureImage)) {
            int scaleTypeIndex = array.getInteger(
                    R.styleable.BigImageView_failureImageInitScaleType,
                    IMAGE_SCALE_TYPE_FIT_CENTER_INDEX);
            if (scaleTypeIndex < 0 || IMAGE_SCALE_TYPES.length <= scaleTypeIndex) {
                throw new IllegalArgumentException("Bad failureImageInitScaleType value: "
                                                   + scaleTypeIndex);
            }
            mFailureImageScaleType = IMAGE_SCALE_TYPES[scaleTypeIndex];
            Drawable mFailureImageDrawable = array.getDrawable(
                    R.styleable.BigImageView_failureImage);
            setFailureImage(mFailureImageDrawable);
        }

        mOptimizeDisplay = array.getBoolean(R.styleable.BigImageView_optimizeDisplay, true);
        mCustomSsivId = array.getResourceId(R.styleable.BigImageView_customSsivId, 0);
        mTapToRetry = array.getBoolean(R.styleable.BigImageView_tapToRetry, true);

        array.recycle();

        if (mCustomSsivId == 0) {
            mImageView = new SubsamplingScaleImageView(context);
            addView(mImageView);
        }

        if (isInEditMode()) {
            mImageLoader = null;
        } else {
            mImageLoader = BigImageViewer.imageLoader();
        }
        mInternalCallback = ThreadedCallbacks.create(ImageLoader.Callback.class, this);

        mTempImages = new ArrayList<>();
    }

    @Override
    public void setOnClickListener(final OnClickListener listener) {
        mImageView.setOnClickListener(listener);
        if (mFailureImageView != null) {
            mFailureImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    // Retry loading when failure image is clicked
                    if (mTapToRetry) {
                        showImage(mThumbnail, mUri);
                    }
                    listener.onClick(v);
                }
            });
        }
    }

    @Override
    public void setOnLongClickListener(OnLongClickListener listener) {
        mImageView.setOnLongClickListener(listener);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        if (mImageView == null) {
            mImageView = findViewById(mCustomSsivId);
        }
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        mImageView.setLayoutParams(params);
        mImageView.setMinimumTileDpi(160);

        setOptimizeDisplay(mOptimizeDisplay);
        setInitScaleType(mInitScaleType);
    }

    public void setFailureImageInitScaleType(ImageView.ScaleType scaleType) {
        mFailureImageScaleType = scaleType;
    }

    public void setFailureImage(Drawable failureImage) {
        // Failure image is not set
        if (failureImage == null) {
            return;
        }

        if (mFailureImageView == null) {
            // Init failure image
            mFailureImageView = new ImageView(getContext());
            mFailureImageView.setVisibility(GONE);

            if (mFailureImageScaleType != null) {
                mFailureImageView.setScaleType(mFailureImageScaleType);
            }

            addView(mFailureImageView);
        }

        mFailureImageView.setImageDrawable(failureImage);
    }

    public void setInitScaleType(int initScaleType) {
        mInitScaleType = initScaleType;
        switch (initScaleType) {
            case INIT_SCALE_TYPE_CENTER_CROP:
                mImageView.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CENTER_CROP);
                break;
            case INIT_SCALE_TYPE_CUSTOM:
                mImageView.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CUSTOM);
                break;
            case INIT_SCALE_TYPE_START:
                mImageView.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_START);
                break;
            case INIT_SCALE_TYPE_CENTER_INSIDE:
            default:
                mImageView.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CENTER_INSIDE);
                break;
        }
        if (mDisplayOptimizeListener != null) {
            mDisplayOptimizeListener.setInitScaleType(initScaleType);
        }
    }

    public void setOptimizeDisplay(boolean optimizeDisplay) {
        mOptimizeDisplay = optimizeDisplay;
        if (mOptimizeDisplay) {
            mDisplayOptimizeListener = new DisplayOptimizeListener(mImageView);
            mImageView.setOnImageEventListener(mDisplayOptimizeListener);
        } else {
            mDisplayOptimizeListener = null;
            mImageView.setOnImageEventListener(null);
        }
    }

    public void setTapToRetry(boolean tapToRetry) {
        mTapToRetry = tapToRetry;
    }

    public void setImageSaveCallback(ImageSaveCallback imageSaveCallback) {
        mImageSaveCallback = imageSaveCallback;
    }

    public void setProgressIndicator(ProgressIndicator progressIndicator) {
        mProgressIndicator = progressIndicator;
    }

    public void setImageLoaderCallback(ImageLoader.Callback imageLoaderCallback) {
        mUserCallback = imageLoaderCallback;
    }

    public File getCurrentImageFile() {
        return mCurrentImageFile;
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

        mImageLoader.cancel(hashCode());

        for (int i = 0, size = mTempImages.size(); i < size; i++) {
            mTempImages.get(i).delete();
        }
        mTempImages.clear();
    }

    public void showImage(Uri uri) {
        showImage(Uri.EMPTY, uri);
    }

    public void showImage(final Uri thumbnail, final Uri uri) {
        mThumbnail = thumbnail;
        mUri = uri;
        mImageLoader.loadImage(hashCode(), uri, mInternalCallback);

        if (mFailureImageView != null) {
            mFailureImageView.setVisibility(GONE);
        }
    }

    public SubsamplingScaleImageView getSSIV() {
        return mImageView;
    }

    @Override
    public void onCacheHit(File image) {
        mCurrentImageFile = image;
        doShowImage(image);

        if (mUserCallback != null) {
            mUserCallback.onCacheHit(image);
        }
    }

    @Override
    public void onCacheMiss(final File image) {
        mCurrentImageFile = image;
        mTempImages.add(image);
        doShowImage(image);

        if (mUserCallback != null) {
            mUserCallback.onCacheMiss(image);
        }
    }

    @Override
    public void onStart() {
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

        if (mUserCallback != null) {
            mUserCallback.onStart();
        }
    }

    @Override
    public void onProgress(final int progress) {
        if (mProgressIndicator != null) {
            mProgressIndicator.onProgress(progress);
        }
        if (mUserCallback != null) {
            mUserCallback.onProgress(progress);
        }
    }

    @Override
    public void onFinish() {
        doOnFinish();
        if (mUserCallback != null) {
            mUserCallback.onFinish();
        }
    }

    @Override
    public void onSuccess(final File image) {
        if (mUserCallback != null) {
            mUserCallback.onSuccess(image);
        }
    }

    @Override
    public void onFail(Exception error) {
        showFailImage();

        if (mUserCallback != null) {
            mUserCallback.onFail(error);
        }
    }

    @UiThread
    private void doOnFinish() {
        if (mOptimizeDisplay) {
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
        } else {
            if (mProgressIndicator != null) {
                mProgressIndicator.onFinish();
            }
            if (mThumbnailView != null) {
                mThumbnailView.setVisibility(GONE);
            }
            if (mProgressIndicatorView != null) {
                mProgressIndicatorView.setVisibility(GONE);
            }
        }
    }

    @UiThread
    private void doShowImage(File image) {
        mImageView.setImage(ImageSource.uri(Uri.fromFile(image)));
        if (mFailureImageView != null) {
            mFailureImageView.setVisibility(GONE);
        }
        mImageView.setVisibility(VISIBLE);
    }

    @UiThread
    private void showFailImage() {
        // Failure image is not set
        if (mFailureImageView == null) {
            return;
        }

        mFailureImageView.setVisibility(VISIBLE);
        mImageView.setVisibility(GONE);
        if (mProgressIndicatorView != null) {
            mProgressIndicatorView.setVisibility(GONE);
        }
    }
}
