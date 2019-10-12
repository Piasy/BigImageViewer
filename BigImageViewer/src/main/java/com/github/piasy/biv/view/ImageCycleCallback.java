package com.github.piasy.biv.view;

import androidx.annotation.UiThread;

@UiThread
public interface ImageCycleCallback {

    void onThumbnailShown();
    void onImageShown();
}
