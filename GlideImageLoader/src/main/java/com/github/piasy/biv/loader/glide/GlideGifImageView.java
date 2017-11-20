package com.github.piasy.biv.loader.glide;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.RequestManager;
import com.github.piasy.biv.view.BigImageView;
import com.github.piasy.biv.view.GifImageView;

/**
 * Created by classTC on 20/11/2017. GlideGifImageView
 */

public class GlideGifImageView implements GifImageView {

    private RequestManager mRequestManager;
    private ImageView mGifImageView;

    public GlideGifImageView(BigImageView parent, int scaleType, RequestManager requestManager) {
        mGifImageView = (ImageView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ui_glide_image, parent, false);
        mGifImageView.setClickable(false);
        mGifImageView.setFocusable(false);
        switch (scaleType) {
            case BigImageView.INIT_SCALE_TYPE_CENTER_CROP:
                mGifImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                break;
            case BigImageView.INIT_SCALE_TYPE_CENTER_INSIDE:
                mGifImageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            default:
                break;
        }
        parent.addView(mGifImageView);
        mRequestManager = requestManager;
    }

    @Override
    public void showGifImageView(Uri gifImageUri) {
        if (mRequestManager == null || mGifImageView == null) {
            throw new RuntimeException("You must initialize GlideGifImageView before show it!");
        }
        mRequestManager.load(gifImageUri)
                .into(mGifImageView);
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
