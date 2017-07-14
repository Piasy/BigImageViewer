package com.github.piasy.biv.view;

import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

import com.github.piasy.biv.loader.ImageLoader;

class DummyImageLoader implements ImageLoader {
    @Override
    public void loadImage(Uri uri, Callback callback) {
         callback.onFail(new RuntimeException("Dummy"));
    }

    @Override
    public View showThumbnail(BigImageView parent, Uri thumbnail, int scaleType) {
        return new ImageView(parent.getContext());
    }

    @Override
    public void prefetch(Uri uri) {

    }
}
