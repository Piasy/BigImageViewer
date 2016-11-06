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

package com.github.piasy.fresco.bigimageviewer;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.facebook.binaryresource.FileBinaryResource;
import com.facebook.cache.common.CacheKey;
import com.facebook.cache.disk.FileCache;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.cache.DefaultCacheKeyFactory;
import com.facebook.imagepipeline.core.DefaultExecutorSupplier;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.core.ImagePipelineFactory;
import com.facebook.imagepipeline.memory.PooledByteBuffer;
import com.facebook.imagepipeline.request.ImageRequest;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Piasy{github.com/Piasy} on 06/11/2016.
 */

public class BigImageView extends FrameLayout {
    private final SubsamplingScaleImageView mImageView;

    private final DefaultExecutorSupplier mExecutorSupplier;

    private final List<DataSource<CloseableReference<PooledByteBuffer>>> mDownloadingImages;
    private final List<File> mTempImages;

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

        mExecutorSupplier = new DefaultExecutorSupplier(Runtime.getRuntime().availableProcessors());
        mDownloadingImages = new ArrayList<>();
        mTempImages = new ArrayList<>();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        for (int i = 0, size = mDownloadingImages.size(); i < size; i++) {
            mDownloadingImages.get(i).close();
        }
        mDownloadingImages.clear();
        for (int i = 0, size = mTempImages.size(); i < size; i++) {
            mTempImages.get(i).delete();
        }
        mTempImages.clear();
    }

    public void showImage(Uri uri) {
        Log.d("FrescoBigImageViewer", "showImage " + uri);

        ImageRequest request = ImageRequest.fromUri(uri);

        File localCache = getCacheFile(request);
        if (localCache.exists()) {
            showCachedImage(localCache);
        } else {
            ImagePipeline pipeline = Fresco.getImagePipeline();
            DataSource<CloseableReference<PooledByteBuffer>> source =
                    pipeline.fetchEncodedImage(request, true);
            mDownloadingImages.add(source);
            source.subscribe(new ImageDownloadSubscriber(getContext()) {
                @Override
                protected void onSuccess(File image) {
                    mTempImages.add(image);
                    showDownloadedImage(image);
                }

                @Override
                protected void onFail(Throwable t) {
                    t.printStackTrace();
                }
            }, mExecutorSupplier.forBackgroundTasks());
        }
    }

    private File getCacheFile(final ImageRequest request) {
        FileCache mainFileCache = ImagePipelineFactory
                .getInstance()
                .getMainFileCache();
        final CacheKey cacheKey = DefaultCacheKeyFactory
                .getInstance()
                .getEncodedCacheKey(request, false); // we don't need context, but avoid null
        File cacheFile = request.getSourceFile();
        if (mainFileCache.hasKey(cacheKey)) {
            cacheFile = ((FileBinaryResource) mainFileCache.getResource(cacheKey)).getFile();
        }
        return cacheFile;
    }

    @WorkerThread
    private void showDownloadedImage(final File image) {
        Log.d("FrescoBigImageViewer", "showDownloadedImage " + image);
        post(new Runnable() {
            @Override
            public void run() {
                doShowImage(image);
            }
        });
    }

    @UiThread
    private void showCachedImage(File image) {
        Log.d("FrescoBigImageViewer", "showCachedImage " + image);
        doShowImage(image);
    }

    @UiThread
    private void doShowImage(File image) {
        mImageView.setImage(ImageSource.uri(Uri.fromFile(image)));
    }
}
