package com.github.piasy.biv.example.glide;

import androidx.annotation.NonNull;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.ModelCache;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;
import java.io.InputStream;

public class CustomImageSizeUrlLoaderFactory implements ModelLoaderFactory<CustomImageSizeModel, InputStream> {

  private ModelCache modelCache = new ModelCache<CustomImageSizeModel, GlideUrl>(500);

  @NonNull
  @Override
  public ModelLoader<CustomImageSizeModel, InputStream> build(@NonNull MultiModelLoaderFactory multiFactory) {
    ModelLoader<GlideUrl, InputStream> modelLoader = multiFactory.build(GlideUrl.class, InputStream.class);
    return new CustomImageSizeUrlLoader(modelLoader, modelCache);
  }

  @Override
  public void teardown() {

  }
}
