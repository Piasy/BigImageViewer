package com.github.piasy.biv.view;

import android.net.Uri;
import android.view.View;

/**
 * Created by classTC on 20/11/2017. GifImageView
 */

public interface GifImageView {
    void showGifImageView(Uri gifImageUri);

    void setGifImageViewVisibility(int visibility);

    void setOnClickListener(View.OnClickListener listener);

    void setOnLongClickListener(View.OnLongClickListener listener);
}
