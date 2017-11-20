package com.github.piasy.biv.loader.fresco;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.github.piasy.biv.view.BigImageView;
import com.github.piasy.biv.view.GifImageView;

/**
 * Created by classTC on 20/11/2017. FrescoGifImageView
 */

public class FrescoGifImageView implements GifImageView {

    private SimpleDraweeView mGifImageView;

    FrescoGifImageView(BigImageView parent, int scaleType) {
        mGifImageView = (SimpleDraweeView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ui_fresco_image, parent, false);

        mGifImageView.setClickable(false);
        mGifImageView.setFocusable(false);

        switch (scaleType) {
            case BigImageView.INIT_SCALE_TYPE_CENTER_CROP:
                mGifImageView.getHierarchy()
                        .setActualImageScaleType(ScalingUtils.ScaleType.CENTER_CROP);
                break;
            case BigImageView.INIT_SCALE_TYPE_CENTER_INSIDE:
                mGifImageView.getHierarchy()
                        .setActualImageScaleType(ScalingUtils.ScaleType.CENTER_INSIDE);
            default:
                break;
        }

        parent.addView(mGifImageView);
    }

    @Override
    public void showGifImageView(Uri gifImageUri) {
        if (mGifImageView == null) {
            throw new RuntimeException("You must initialize FrescoGifImageView before show it!");
        }

        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setOldController(mGifImageView.getController())
                .setAutoPlayAnimations(true)
                .setUri(gifImageUri)
                .build();
        mGifImageView.setController(controller);
    }

    @Override
    public void setGifImageViewVisibility(int visibility) {
        if (mGifImageView != null) {
            mGifImageView.setVisibility(visibility);
        }
    }

    @Override
    public void setOnClickListener(View.OnClickListener listener) {
        if (mGifImageView != null) {
            mGifImageView.setOnClickListener(listener);
        }
    }

    @Override
    public void setOnLongClickListener(View.OnLongClickListener listener) {
        if (mGifImageView != null) {
            mGifImageView.setOnLongClickListener(listener);
        }
    }

}
