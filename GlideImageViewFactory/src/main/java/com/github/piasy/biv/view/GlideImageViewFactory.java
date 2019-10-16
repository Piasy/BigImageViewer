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
import com.bumptech.glide.Glide;
import com.github.piasy.biv.metadata.ImageInfoExtractor;
import java.io.File;
import pl.droidsonroids.gif.GifImageView;

/**
 * Created by Piasy{github.com/Piasy} on 2018/8/12.
 */
public class GlideImageViewFactory extends ImageViewFactory {
    @Override
    protected final View createAnimatedImageView(final Context context, final int imageType,
            final int initScaleType) {
        switch (imageType) {
            case ImageInfoExtractor.TYPE_GIF: {
                final GifImageView gifImageView = new GifImageView(context);
                gifImageView.setScaleType(BigImageView.scaleType(initScaleType));
                return gifImageView;
            }
            case ImageInfoExtractor.TYPE_ANIMATED_WEBP: {
                final ImageView imageView = new ImageView(context);
                imageView.setScaleType(BigImageView.scaleType(initScaleType));
                return imageView;
            }
            default:
                return super.createAnimatedImageView(context, imageType, initScaleType);
        }
    }

    @Override
    public final void loadAnimatedContent(final View view, final int imageType,
            final File imageFile) {
        switch (imageType) {
            case ImageInfoExtractor.TYPE_GIF: {
                if (view instanceof GifImageView) {
                    ((GifImageView) view).setImageURI(
                            Uri.parse("file://" + imageFile.getAbsolutePath()));
                }
                break;
            }
            case ImageInfoExtractor.TYPE_ANIMATED_WEBP: {
                if (view instanceof ImageView) {
                    Glide.with(view.getContext())
                            .load(imageFile)
                            .into((ImageView) view);
                }
                break;
            }

            default:
                super.loadAnimatedContent(view, imageType, imageFile);
        }
    }

    @Override
    public void loadThumbnailContent(final View view, final Uri thumbnail) {
        if (view instanceof ImageView) {
            Glide.with(view.getContext())
                    .load(thumbnail)
                    .into((ImageView) view);
        }
    }
}
