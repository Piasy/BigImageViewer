# BigImageViewer

[ ![Download](https://api.bintray.com/packages/piasy/maven/BigImageViewer/images/download.svg) ](https://bintray.com/piasy/maven/BigImageViewer/_latestVersion)

Big image viewer supporting pan and zoom, with very little memory usage and full featured image loading choices. Powered by [Subsampling Scale Image View](https://github.com/davemorrissey/subsampling-scale-image-view), [Fresco](https://github.com/facebook/fresco), [Glide](https://github.com/bumptech/glide), and [Picasso](https://github.com/square/picasso).

## Demo

![memory usage](art/android_studio_memory_monitor.png)

![demo](art/fresco_big_image_viewer_demo.gif)

## Usage

### Dependency

``` gradle
allprojects {
    repositories {
        jcenter()
    }
}

// NOTE that the artifact id has been changed!
compile 'com.github.piasy:BigImageViewer:1.2.0'

// load with fresco
compile 'com.github.piasy:FrescoImageLoader:1.2.0'

// load with glide
compile 'com.github.piasy:GlideImageLoader:1.2.0'

// progress pie indicator
compile 'com.github.piasy:ProgressPieIndicator:1.2.0'
```

### initialize

``` java
// MUST use app context to avoid memory leak!
// load with fresco
BigImageViewer.initialize(FrescoImageLoader.with(appContext));

// or load with glide
BigImageViewer.initialize(GlideImageLoader.with(appContext));
```

**Note that** if you've already used Fresco in your project, please change `Fresco.initialize` into `BigImageViewer.initialize`.

### Layout

``` xml
<com.github.piasy.biv.BigImageView
        android:id="@+id/mBigImage"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        />
```

### Java

``` java
BigImageView bigImageView = (BigImageView) findViewById(R.id.mBigImage);
bigImageView.showImage(Uri.parse(url));

// you can show thumbnail before the big image is loaded
bigImageView.showImage(Uri.parse(thumbnail), Uri.parse(url));
```

### Downloading progress

``` java
bigImageView.setProgressIndicator(new ProgressPieIndicator());
```

There is one built-in indicator, `ProgressPieIndicator`, you can implement your own indicator easily, [learn by example](/ProgressPieIndicator).

### Prefetch

You can prefetch images in advance, so it could be shown immediately when user want to see it.

``` java
BigImageViewer.prefetch(uris);
```

### Save image into gallery

``` java
bigImageView.setImageSaveCallback(new ImageSaveCallback() {
    @Override
    public void onSuccess(String uri) {
        Toast.makeText(LongImageActivity.this,
                "Success",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFail(Throwable t) {
        t.printStackTrace();
        Toast.makeText(LongImageActivity.this,
                "Fail",
                Toast.LENGTH_SHORT).show();
    }
});

bigImageView.saveImageIntoGallery();
```

## Why another big image viewer?

There are several big image viewer libraries, [PhotoDraweeView](https://github.com/ongakuer/PhotoDraweeView), [FrescoImageViewer](https://github.com/stfalcon-studio/FrescoImageViewer), and [Subsampling Scale Image View](https://github.com/davemorrissey/subsampling-scale-image-view).

They both support pan and zoom. PhotoDraweeView and FrescoImageViewer both use Fresco to load image, which will cause extremely large memory usage when showing big images. Subsampling Scale Image View uses very little memory, but it can only show local image file.

This library show big image with Subsampling Scale Image View, so it only uses very little memory. And this library support using different image load libraries, so it's full featured!

## Performance

Memory usage of different libraries:

| \- | PhotoDraweeView | FrescoImageViewer | BigImageViewer |
| ------| ------ | ------ | ------ |
| 4135\*5134 | 80MB | 80MB | 2~20 MB |

## Todo

+ [x] GlideImageLoader
+ [x] Save image file to gallery
+ [x] Optimize long image showing effect, thanks for [razerdp](https://github.com/razerdp)
+ [x] Optimize "double tap to zoom" effect, thanks for [razerdp](https://github.com/razerdp)
+ [x] Loading animation
+ [x] Downloading progress
+ [x] Thumbnail support
+ [ ] Fail image
+ [ ] Retry when fail
+ [ ] PicassoImageLoader, track [this issue](https://github.com/square/picasso/issues/506)

Those features are offered by image load libraries, and they should be easy to implement, but I don't have enough time currently. So your contributions are welcome!
