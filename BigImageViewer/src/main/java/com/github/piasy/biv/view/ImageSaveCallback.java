package com.github.piasy.biv.view;

/**
 * Created by Piasy{github.com/Piasy} on 10/11/2016.
 */

public interface ImageSaveCallback {
    void onSuccess(String uri);

    void onFail(Throwable t);
}
