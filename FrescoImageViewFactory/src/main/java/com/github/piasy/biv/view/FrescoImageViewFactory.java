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

package com.github.piasy.biv.view;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import java.io.File;

/**
 * Created by Piasy{github.com/Piasy} on 2018/8/12.
 */
public class FrescoImageViewFactory extends ImageViewFactory {
    @Override
    protected View createAnimatedImageView(final Context context, final int imageType,
            final File imageFile, int initScaleType) {
        SimpleDraweeView view = new SimpleDraweeView(context);
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setUri(Uri.parse("file://" + imageFile.getAbsolutePath()))
                .setAutoPlayAnimations(true)
                .build();
        view.setController(controller);
        view.getHierarchy().setActualImageScaleType(scaleType(initScaleType));
        return view;
    }

    @Override
    public View createThumbnailView(final Context context, final Uri thumbnail,
            final ImageView.ScaleType scaleType) {
        SimpleDraweeView thumbnailView = new SimpleDraweeView(context);
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setUri(thumbnail)
                .build();
        if (scaleType != null) {
            thumbnailView.getHierarchy().setActualImageScaleType(scaleType(scaleType));
        }
        thumbnailView.setController(controller);
        return thumbnailView;
    }

    private ScalingUtils.ScaleType scaleType(int value) {
        switch (value) {
            case BigImageView.INIT_SCALE_TYPE_CENTER:
                return ScalingUtils.ScaleType.CENTER;
            case BigImageView.INIT_SCALE_TYPE_CENTER_CROP:
                return ScalingUtils.ScaleType.CENTER_CROP;
            case BigImageView.INIT_SCALE_TYPE_CENTER_INSIDE:
                return ScalingUtils.ScaleType.CENTER_INSIDE;
            case BigImageView.INIT_SCALE_TYPE_FIT_END:
                return ScalingUtils.ScaleType.FIT_END;
            case BigImageView.INIT_SCALE_TYPE_FIT_START:
                return ScalingUtils.ScaleType.FIT_START;
            case BigImageView.INIT_SCALE_TYPE_FIT_XY:
                return ScalingUtils.ScaleType.FIT_XY;
            case BigImageView.INIT_SCALE_TYPE_FIT_CENTER:
            default:
                return ScalingUtils.ScaleType.FIT_CENTER;
        }
    }

    private ScalingUtils.ScaleType scaleType(ImageView.ScaleType scaleType) {
        switch (scaleType) {
            case CENTER:
                return ScalingUtils.ScaleType.CENTER;
            case CENTER_CROP:
                return ScalingUtils.ScaleType.CENTER_CROP;
            case CENTER_INSIDE:
                return ScalingUtils.ScaleType.CENTER_INSIDE;
            case FIT_END:
                return ScalingUtils.ScaleType.FIT_END;
            case FIT_START:
                return ScalingUtils.ScaleType.FIT_START;
            case FIT_XY:
                return ScalingUtils.ScaleType.FIT_XY;
            case FIT_CENTER:
            default:
                return ScalingUtils.ScaleType.FIT_CENTER;
        }
    }
}
