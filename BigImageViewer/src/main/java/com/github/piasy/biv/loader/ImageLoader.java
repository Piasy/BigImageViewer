package com.github.piasy.biv.loader;

import android.net.Uri;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;
import java.io.File;

/**
 * Created by Piasy{github.com/Piasy} on 08/11/2016.
 */

public interface ImageLoader {

    void loadImage(Uri uri, Callback callback);

    void prefetch(Uri uri);

    interface Callback {
        @UiThread
        void onCacheHit(File image);

        @WorkerThread
        void onCacheMiss(File image);
    }
}
