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

package com.github.piasy.biv.utils;

import android.graphics.PointF;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.github.piasy.biv.view.BigImageView;

/**
 * credit: https://github.com/Piasy/BigImageViewer/issues/2
 */

public class DisplayOptimizeListener implements SubsamplingScaleImageView.OnImageEventListener {
    private static final int LONG_IMAGE_SIZE_RATIO = 2;

    private final SubsamplingScaleImageView mImageView;

    private int mInitScaleType;

    public DisplayOptimizeListener(SubsamplingScaleImageView imageView) {
        mImageView = imageView;
    }

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

        if (!hasZeroValue && (float) imageHeight / imageWidth > LONG_IMAGE_SIZE_RATIO) {
            // scale at top
            mImageView
                    .animateScaleAndCenter(result, new PointF(imageWidth / 2, 0))
                    .withEasing(SubsamplingScaleImageView.EASE_OUT_QUAD)
                    .start();
        }

        // `对结果进行放大裁定，防止计算结果跟双击放大结果过于相近`
        if (Math.abs(result - 0.1) < 0.2f) {
            result += 0.2f;
        }

        if (mInitScaleType == BigImageView.INIT_SCALE_TYPE_CUSTOM) {
            float maxScale = Math.max((float) viewWidth / imageWidth,
                    (float) viewHeight / imageHeight);
            if (maxScale > 1) {
                // image is smaller than screen, it should be zoomed out to its origin size.
                // image is bigger than screen, it should be scale to the result.
                if (imageWidth > viewWidth) {
                    mImageView.setMinScale(result);
                } else {
                    mImageView.setMinScale(1.0f);
                }
                // and it should be zoomed in to fill the screen
                float defaultMaxScale = mImageView.getMaxScale();
                mImageView.setMaxScale(Math.max(defaultMaxScale, maxScale * 1.2F));
            } else {
                // image is bigger than screen, it should be zoomed out to fit the screen
                float minScale = Math.min((float) viewWidth / imageWidth,
                        (float) viewHeight / imageHeight);
                mImageView.setMinScale(minScale);
                // but no need to set max scale
            }

            // scale to fit screen, and center
            if (mImageView.getMaxScale() < mImageView.getMinScale()){
                mImageView.setScaleAndCenter(maxScale, new PointF(imageWidth / 2.0f, imageHeight / 2.0f));
                // reset max scale for the better animation.
                mImageView.setMaxScale(mImageView.getMinScale() * 1.5f);
            }
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

    public void setInitScaleType(int initScaleType) {
        mInitScaleType = initScaleType;
    }
}
