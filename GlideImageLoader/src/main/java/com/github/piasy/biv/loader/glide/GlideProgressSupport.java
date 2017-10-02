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
 *
 * credit: https://gist.github.com/TWiStErRob/08d5807e396740e52c90
 */

package com.github.piasy.biv.loader.glide;

import com.bumptech.glide.Glide;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.model.GlideUrl;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 * Created by Piasy{github.com/Piasy} on 12/11/2016.
 */

public class GlideProgressSupport {
    private static Interceptor createInterceptor(final ResponseProgressListener listener) {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                Response response = chain.proceed(request);
                return response.newBuilder()
                        .body(new OkHttpProgressResponseBody(request.url(), response.body(),
                                listener))
                        .build();
            }
        };
    }

    public static void init(Glide glide, OkHttpClient okHttpClient) {
        OkHttpClient.Builder builder;
        if (okHttpClient != null) {
            builder = okHttpClient.newBuilder();
        } else {
            builder = new OkHttpClient.Builder();
        }
        builder.addNetworkInterceptor(createInterceptor(new DispatchingProgressListener()));
        glide.getRegistry().replace(GlideUrl.class, InputStream.class,
                new OkHttpUrlLoader.Factory(builder.build()));
    }

    public static void forget(String url) {
        DispatchingProgressListener.forget(url);
    }

    public static void expect(String url, ProgressListener listener) {
        DispatchingProgressListener.expect(url, listener);
    }

    public interface ProgressListener {
        void onDownloadStart();

        void onProgress(int progress);

        void onDownloadFinish();
    }

    private interface ResponseProgressListener {
        void update(HttpUrl url, long bytesRead, long contentLength);
    }

    private static class DispatchingProgressListener implements ResponseProgressListener {
        private static final Map<String, ProgressListener> LISTENERS = new HashMap<>();
        private static final Map<String, Integer> PROGRESSES = new HashMap<>();

        static void forget(String url) {
            LISTENERS.remove(url);
            PROGRESSES.remove(url);
        }

        static void expect(String url, ProgressListener listener) {
            LISTENERS.put(url, listener);
        }

        @Override
        public void update(HttpUrl url, final long bytesRead, final long contentLength) {
            String key = url.toString();
            final ProgressListener listener = LISTENERS.get(key);
            if (listener == null) {
                return;
            }

            Integer lastProgress = PROGRESSES.get(key);
            if (lastProgress == null) {
                // ensure `onStart` is called before `onProgress` and `onFinish`
                listener.onDownloadStart();
            }
            if (contentLength <= bytesRead) {
                listener.onDownloadFinish();
                forget(key);
                return;
            }
            int progress = (int) ((float) bytesRead / contentLength * 100);
            if (lastProgress == null || progress != lastProgress) {
                PROGRESSES.put(key, progress);
                listener.onProgress(progress);
            }
        }
    }

    private static class OkHttpProgressResponseBody extends ResponseBody {
        private final HttpUrl mUrl;
        private final ResponseBody mResponseBody;
        private final ResponseProgressListener mProgressListener;
        private BufferedSource mBufferedSource;

        OkHttpProgressResponseBody(HttpUrl url, ResponseBody responseBody,
                ResponseProgressListener progressListener) {
            this.mUrl = url;
            this.mResponseBody = responseBody;
            this.mProgressListener = progressListener;
        }

        @Override
        public MediaType contentType() {
            return mResponseBody.contentType();
        }

        @Override
        public long contentLength() {
            return mResponseBody.contentLength();
        }

        @Override
        public BufferedSource source() {
            if (mBufferedSource == null) {
                mBufferedSource = Okio.buffer(source(mResponseBody.source()));
            }
            return mBufferedSource;
        }

        private Source source(Source source) {
            return new ForwardingSource(source) {
                private long mTotalBytesRead = 0L;

                @Override
                public long read(Buffer sink, long byteCount) throws IOException {
                    long bytesRead = super.read(sink, byteCount);
                    long fullLength = mResponseBody.contentLength();
                    if (bytesRead == -1) { // this source is exhausted
                        mTotalBytesRead = fullLength;
                    } else {
                        mTotalBytesRead += bytesRead;
                    }
                    mProgressListener.update(mUrl, mTotalBytesRead, fullLength);
                    return bytesRead;
                }
            };
        }
    }
}
