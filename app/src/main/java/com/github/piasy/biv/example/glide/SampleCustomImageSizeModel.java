package com.github.piasy.biv.example.glide;

import android.net.Uri;

public class SampleCustomImageSizeModel implements CustomImageSizeModel {

  private Uri baseImageUrl;

  public void setBaseImageUrl(Uri baseImageUrl) {
    this.baseImageUrl = baseImageUrl;
  }

  @Override
  public String requestCustomSizeUrl(Integer width, Integer height) {
    if (baseImageUrl != null) {
      return baseImageUrl
          .buildUpon()
          .appendQueryParameter("width", width.toString())
          .appendQueryParameter("height", height.toString())
          .build()
          .toString();
    }
    throw new RuntimeException("You have to set the base image url first");
  }
}
