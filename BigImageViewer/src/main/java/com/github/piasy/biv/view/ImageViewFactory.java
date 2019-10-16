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
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.github.piasy.biv.metadata.ImageInfoExtractor;
import java.io.File;

/**
 * Created by Piasy{github.com/Piasy} on 2018/8/12.
 */
public class ImageViewFactory {

    public final View createMainView(final Context context, final int imageType,
            final int initScaleType) {
        switch (imageType) {
            case ImageInfoExtractor.TYPE_GIF:
            case ImageInfoExtractor.TYPE_ANIMATED_WEBP:
                return createAnimatedImageView(context, imageType, initScaleType);
            case ImageInfoExtractor.TYPE_STILL_WEBP:
            case ImageInfoExtractor.TYPE_STILL_IMAGE:
            default:
                return createStillImageView(context);
        }
    }

    public boolean isAnimatedContent(final int imageType) {
        switch (imageType) {
            case ImageInfoExtractor.TYPE_GIF:
            case ImageInfoExtractor.TYPE_ANIMATED_WEBP:
                return true;
            default:
                return false;
        }
    }

    protected SubsamplingScaleImageView createStillImageView(final Context context) {
        return new SubsamplingScaleImageView(context);
    }

    public void loadSillContent(final View view, final Uri uri) {
        if (view instanceof SubsamplingScaleImageView) {
            ((SubsamplingScaleImageView) view).setImage(ImageSource.uri(uri));
        }
    }

    protected View createAnimatedImageView(final Context context, final int imageType,
            final int initScaleType) {
        return null;
    }

    public void loadAnimatedContent(final View view, final int imageType, final File imageFile) {
    }

    public View createThumbnailView(final Context context, final ImageView.ScaleType scaleType,
            final boolean willLoadFromNetwork) {
        final ImageView thumbnailView = new ImageView(context);
        if (scaleType != null) {
            thumbnailView.setScaleType(scaleType);
        }
        return thumbnailView;
    }

    public void loadThumbnailContent(final View view, final File thumbnail) {
        if (view instanceof ImageView) {
            ((ImageView) view).setImageURI(Uri.fromFile(thumbnail));
        }
    }

    public void loadThumbnailContent(final View view, final Uri thumbnail) {
    }
}
