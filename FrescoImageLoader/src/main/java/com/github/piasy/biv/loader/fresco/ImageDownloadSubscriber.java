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

package com.github.piasy.biv.loader.fresco;

import android.content.Context;
import androidx.annotation.WorkerThread;
import com.facebook.common.memory.PooledByteBuffer;
import com.facebook.common.memory.PooledByteBufferInputStream;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.BaseDataSubscriber;
import com.facebook.datasource.DataSource;
import com.github.piasy.biv.utils.IOUtils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Piasy{github.com/Piasy} on 06/11/2016.
 */

public abstract class ImageDownloadSubscriber
        extends BaseDataSubscriber<CloseableReference<PooledByteBuffer>> {
    private final File mTempFile;

    private volatile boolean mFinished;

    public ImageDownloadSubscriber(Context context) {
        mTempFile = new File(context.getCacheDir(), "" + System.currentTimeMillis() + ".png");
    }

    @Override
    public void onProgressUpdate(DataSource<CloseableReference<PooledByteBuffer>> dataSource) {
        if (!mFinished) {
            onProgress((int) (dataSource.getProgress() * 100));
        }
    }

    @Override
    protected void onNewResultImpl(DataSource<CloseableReference<PooledByteBuffer>> dataSource) {
        if (!dataSource.isFinished() || dataSource.getResult() == null) {
            return;
        }

        // if we try to retrieve image file by cache key, it will return null
        // so we need to create a temp file, little bit hack :(
        PooledByteBufferInputStream inputStream = null;
        FileOutputStream outputStream = null;
        try {
            inputStream = new PooledByteBufferInputStream(dataSource.getResult().get());
            outputStream = new FileOutputStream(mTempFile);
            IOUtils.copy(inputStream, outputStream);

            mFinished = true;
            onSuccess(mTempFile);
        } catch (IOException e) {
            onFail(e);
        } finally {
            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(outputStream);
        }
    }

    @Override
    protected void onFailureImpl(DataSource<CloseableReference<PooledByteBuffer>> dataSource) {
        mFinished = true;
        onFail(new RuntimeException("onFailureImpl"));
    }

    @WorkerThread
    protected abstract void onProgress(int progress);

    @WorkerThread
    protected abstract void onSuccess(File image);

    @WorkerThread
    protected abstract void onFail(Throwable t);
}
