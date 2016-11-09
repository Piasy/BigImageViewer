package com.github.piasy.biv.loader.glide;

import android.content.Context;
import android.net.Uri;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.github.piasy.biv.loader.ImageLoader;
import java.io.File;

/**
 * Created by Piasy{github.com/Piasy} on 09/11/2016.
 */

public final class GlideImageLoader implements ImageLoader {
    private final RequestManager mRequestManager;

    private GlideImageLoader(Context context) {
        mRequestManager = Glide.with(context);
    }

    public static GlideImageLoader with(Context context) {
        return new GlideImageLoader(context);
    }

    @Override
    public void loadImage(Uri uri, final Callback callback) {
        mRequestManager
                .load(uri)
                .downloadOnly(new SimpleTarget<File>() {
                    @Override
                    public void onResourceReady(File image,
                            GlideAnimation<? super File> glideAnimation) {
                        // we don't need delete this image file, so it behaves live cache hit
                        callback.onCacheHit(image);
                    }
                });
    }

    @Override
    public void prefetch(Uri uri) {
        mRequestManager
                .load(uri)
                .downloadOnly(new SimpleTarget<File>() {
                    @Override
                    public void onResourceReady(File resource,
                            GlideAnimation<? super File> glideAnimation) {
                        // not interested in result
                    }
                });
    }
}
