package com.github.piasy.biv.example.glide;

import com.github.piasy.biv.loader.glide.GlideModel;

public interface CustomImageSizeModel extends GlideModel {
  String requestCustomSizeUrl(Integer width, Integer height);
}
