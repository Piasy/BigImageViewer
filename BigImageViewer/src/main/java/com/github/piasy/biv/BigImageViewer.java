package com.github.piasy.biv;

import android.net.Uri;
import com.github.piasy.biv.loader.ImageLoader;

/**
 * Created by Piasy{github.com/Piasy} on 06/11/2016.
 *
 * This is not a singleton, you can initialize it multiple times, but before you initialize it
 * again, it will use the same {@link ImageLoader} globally.
 */

public final class BigImageViewer {
    private static volatile BigImageViewer sInstance;

    private final ImageLoader mImageLoader;

    private BigImageViewer(ImageLoader imageLoader) {
        mImageLoader = imageLoader;
    }

    public static void initialize(ImageLoader imageLoader) {
        sInstance = new BigImageViewer(imageLoader);
    }

    public static ImageLoader imageLoader() {
        if (sInstance == null) {
            throw new IllegalStateException("You must initialize BigImageViewer before use it!");
        }
        return sInstance.mImageLoader;
    }

    public static void prefetch(Uri... uris) {
        if (uris == null) {
            return;
        }

        ImageLoader imageLoader = imageLoader();
        for (Uri uri : uris) {
            imageLoader.prefetch(uri);
        }
    }
}
