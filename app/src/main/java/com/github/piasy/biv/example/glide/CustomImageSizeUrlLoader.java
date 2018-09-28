package com.github.piasy.biv.example.glide;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.ModelCache;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.stream.BaseGlideUrlLoader;
import java.io.InputStream;

public class CustomImageSizeUrlLoader extends BaseGlideUrlLoader<CustomImageSizeModel> {

  public CustomImageSizeUrlLoader(ModelLoader<GlideUrl, InputStream> concreteLoader,
      @Nullable ModelCache<CustomImageSizeModel, GlideUrl> modelCache) {
    super(concreteLoader, modelCache);
  }

  @Override
  protected String getUrl(CustomImageSizeModel model, int width, int height,
      Options options) {
    return model.requestCustomSizeUrl(width, height);
  }

  @Override
  public boolean handles(@NonNull CustomImageSizeModel customImageSizeModel) {
    return true;
  }
}
