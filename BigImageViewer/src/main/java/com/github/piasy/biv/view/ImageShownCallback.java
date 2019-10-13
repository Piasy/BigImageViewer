package com.github.piasy.biv.view;

import androidx.annotation.UiThread;

@UiThread
public interface ImageShownCallback {

    void onThumbnailShown();
    void onMainImageShown();
}
