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

package com.github.piasy.biv.loader.glide;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.github.piasy.biv.loader.ImageLoader;
import com.github.piasy.biv.metadata.ImageInfoExtractor;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;

import androidx.annotation.NonNull;
import okhttp3.OkHttpClient;

/**
 * Created by Piasy{github.com/Piasy} on 09/11/2016.
 */

public class GlideImageLoader implements ImageLoader {
    protected final RequestManager mRequestManager;

    private final ConcurrentHashMap<Integer, ImageDownloadTarget> mRequestTargetMap
            = new ConcurrentHashMap<>();

    protected GlideImageLoader(Context context, OkHttpClient okHttpClient) {
        GlideProgressSupport.init(Glide.get(context), okHttpClient);
        mRequestManager = Glide.with(context);
    }

    public static GlideImageLoader with(Context context) {
        return with(context, null);
    }

    public static GlideImageLoader with(Context context, OkHttpClient okHttpClient) {
        return new GlideImageLoader(context, okHttpClient);
    }

    @Override
    public void loadImage(final int requestId, final Uri uri, final Callback callback) {
        ImageDownloadTarget target = new ImageDownloadTarget(uri.toString()) {
            @Override
            public void onResourceReady(@NonNull File resource,
                    Transition<? super File> transition) {
                super.onResourceReady(resource, transition);
                // we don't need delete this image file, so it behaves like cache hit
                callback.onCacheHit(ImageInfoExtractor.getImageType(resource), resource);
                callback.onSuccess(resource);
            }

            @Override
            public void onLoadFailed(final Drawable errorDrawable) {
                super.onLoadFailed(errorDrawable);
                callback.onFail(new GlideLoaderException(errorDrawable));
            }

            @Override
            public void onDownloadStart() {
                callback.onStart();
            }

            @Override
            public void onProgress(int progress) {
                callback.onProgress(progress);
            }

            @Override
            public void onDownloadFinish() {
                callback.onFinish();
            }
        };
        clearTarget(requestId);
        saveTarget(requestId, target);

        downloadImageInto(uri, target);
    }

    protected void downloadImageInto(Uri uri, Target<File> target) {
        mRequestManager
                .downloadOnly()
                .load(uri)
                .into(target);
    }

    @Override
    public void prefetch(Uri uri) {
        downloadImageInto(uri, new PrefetchTarget());
    }

    @Override
    public void cancel(int requestId) {
        clearTarget(requestId);
    }

    @Override
    public void cancelAll() {
        for (Integer key : mRequestTargetMap.keySet()) {
            cancel(key);
        }
    }

    private void saveTarget(int requestId, ImageDownloadTarget target) {
        mRequestTargetMap.put(requestId, target);
    }

    private void clearTarget(int requestId) {
        ImageDownloadTarget target = mRequestTargetMap.remove(requestId);
        if (target != null) {
            mRequestManager.clear(target);
        }
    }
}
