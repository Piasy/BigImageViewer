package com.github.piasy.biv.loader.glide;

import android.content.Context;
import android.net.Uri;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import java.io.File;
import okhttp3.OkHttpClient;

public class GlideCustomImageLoader extends GlideImageLoader {
  private final Class<? extends GlideModel> mModel;

  private GlideCustomImageLoader(Context context, OkHttpClient okHttpClient,
      Class<? extends GlideModel> model) {
    super(context, okHttpClient);
    mModel = model;
  }

  public static GlideCustomImageLoader with(Context context, Class<? extends GlideModel> glideModel) {
    return with(context, null, glideModel);
  }

  public static GlideCustomImageLoader with(Context context, OkHttpClient okHttpClient, Class<? extends GlideModel> glideModel) {
    return new GlideCustomImageLoader(context, okHttpClient, glideModel);
  }

  @Override
  protected void downloadImageInto(Uri uri, SimpleTarget<File> target) {
    if(mModel != null) {
      try {
        GlideModel glideModel = mModel.newInstance();
        glideModel.setBaseImageUrl(uri);
        mRequestManager
            .downloadOnly()
            .load(glideModel)
            .into(target);
      } catch (InstantiationException e) {
        super.downloadImageInto(uri, target);
      } catch (IllegalAccessException e) {
        super.downloadImageInto(uri, target);
      }
    } else {
      super.downloadImageInto(uri, target);
    }
  }

  @Override
  public void prefetch(Uri uri) {
    if(mModel != null) {
      try {
        GlideModel glideModel = mModel.newInstance();
        glideModel.setBaseImageUrl(uri);
        mRequestManager
            .downloadOnly()
            .load(glideModel)
            .into(new SimpleTarget<File>() {
              @Override
              public void onResourceReady(File resource,
                  Transition<? super File> transition) {
                // not interested in result
              }
            });
      } catch (InstantiationException e) {
        super.prefetch(uri);
      } catch (IllegalAccessException e) {
        super.prefetch(uri);
      }
    } else {
      super.prefetch(uri);
    }
  }
}
