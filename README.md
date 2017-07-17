# BigImageViewer

[![Download](https://api.bintray.com/packages/piasy/maven/BigImageViewer/images/download.svg)](https://bintray.com/piasy/maven/BigImageViewer/_latestVersion)

Big image viewer supporting pan and zoom, with very little memory usage and full
featured image loading choices. Powered by [Subsampling Scale Image
View](https://github.com/davemorrissey/subsampling-scale-image-view),
[Fresco](https://github.com/facebook/fresco),
[Glide](https://github.com/bumptech/glide), and
[Picasso](https://github.com/square/picasso).


## Demo

![memory usage](art/android_studio_memory_monitor.png)

![demo](art/fresco_big_image_viewer_demo.gif)


## Getting started

### Add the dependencies

``` gradle
allprojects {
    repositories {
        maven {
            url  "http://dl.bintray.com/piasy/maven"
        }
    }
}

compile 'com.github.piasy:BigImageViewer:1.3.2'

// load with fresco
compile 'com.github.piasy:FrescoImageLoader:1.3.2'

// load with glide
compile 'com.github.piasy:GlideImageLoader:1.3.2'

// progress pie indicator
compile 'com.github.piasy:ProgressPieIndicator:1.3.2'
```

### Initialize

``` java
// MUST use app context to avoid memory leak!
// load with fresco
BigImageViewer.initialize(FrescoImageLoader.with(appContext));

// or load with glide
BigImageViewer.initialize(GlideImageLoader.with(appContext));
```

**Note that** if you've already used Fresco in your project, please change
`Fresco.initialize` into `BigImageViewer.initialize`.

### Add the BigImageView to your layout

``` xml
<com.github.piasy.biv.view.BigImageView
        android:id="@+id/mBigImage"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:failureImage="@drawable/failure_image"
        app:failureImageInitScaleType="center"
        app:optimizeDisplay="false"
        />
```

You can disable display optimization using `optimizeDisplay` attribute, or
`BigImageView.setOptimizeDisplay(false)`. Which will disable animation for long
image, and the switch between thumbnail and origin image.

### Show the image

``` java
BigImageView bigImageView = (BigImageView) findViewById(R.id.mBigImage);
bigImageView.showImage(Uri.parse(url));

// Or show a thumbnail before the big image is loaded
bigImageView.showImage(Uri.parse(thumbnail), Uri.parse(url));
```


## Usage

### Download progress indicator

``` java
bigImageView.setProgressIndicator(new ProgressPieIndicator());
```

There is one built-in indicator, `ProgressPieIndicator`, you can implement your
own indicator easily, [learn by example](/ProgressPieIndicator).

### Prefetch

You can prefetch images in advance, so it could be shown immediately when user
want to see it.

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

### Get current image file

``` java
// only valid when image file is downloaded.
File path = bigImageView.getCurrentImageFile();
```

### Image init scale type

``` xml
<com.github.piasy.biv.view.BigImageView
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:initScaleType="centerInside"
        />
```

``` java
mBigImageView.setInitScaleType(BigImageView.INIT_SCALE_TYPE_CENTER_CROP);
```

| value | effect |
| ------| ------ |
| centerInside | default, Scale the image so that both dimensions of the image will be equal to or less than the corresponding dimension of the view. The image is then centered in the view. This is the default behaviour and best for galleries. |
| centerCrop | Scale the image uniformly so that both dimensions of the image will be equal to or larger than the corresponding dimension of the view. The image is then centered in the view. |
| auto | determine the max scale and min scale by image size and view size, fit the image to screen and centered when loaded. |

### Failure image

``` xml
<com.github.piasy.biv.view.BigImageView
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:failureImage="@drawable/failure_image"
        />
```

``` java
mBigImageView.setFailureImage(failureImageDrawable);
```

Displayed using an `ImageView` when the image network request fails. If not specified, nothing is displayed when the request fails.

#### Failure image init scale type

``` xml
<com.github.piasy.biv.view.BigImageView
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:failureImageInitScaleType="center"
        />
```

``` java
mBigImageView.setFailureImageInitScaleType(ImageView.ScaleType.CENTER);
```

Any valid [ImageView.ScaleType](https://developer.android.com/reference/android/widget/ImageView.ScaleType.html).
Set to `ImageView.ScaleType.FIT_CENTER` by default. Ignored if there is no
failure image set.

### Image load callback

You can handle the image load response by creating a new `ImageLoader.Callback`
and overriding the key callbacks

```java
ImageLoader.Callback myImageLoaderCallback = new ImageLoader.Callback() {
    @Override
    public void onCacheHit(File image) {
      // Image was found in the cache
    }

    @Override
    public void onCacheMiss(File image) {
      // Image was downloaded from the network
    }

    @Override
    public void onStart() {
      // Image download has started
    }

    @Override
    public void onProgress(int progress) {
      // Image download progress has changed
    }

    @Override
    public void onFinish() {
      // Image download has finished
    }

    @Override
    public void onSuccess(File image) {
      // Image was retrieved successfully (either from cache or network)
    }

    @Override
    public void onFail(Exception error) {
      // Image download failed
    }
}
```

Then setting it as the image load callback

```java
mBigImageView.setImageLoaderCallback(myImageLoaderCallback);
```

The `onSuccess(File image)` is always called after the image was  retrieved
successfully whether from the cache or the network.

**Note that** only `onSuccess(File image)` and `onFail(Exception error)` as well
as `onCacheHit(File image)` callbacks run on the UI thread. All other callbacks
run on a background (worker) thread.

For an example see ImageLoaderCallbackActivity.java

### Full customization

You can get the SSIV instance through the method below:

``` java
public SubsamplingScaleImageView getSSIV() {
    return mImageView;
}
```

Then you can do anything you can imagine about SSIV :)

### Custom SSIV support

You can even use your own custom SSIV, by declaring it in layout file:

``` xml
<com.github.piasy.biv.view.BigImageView
        android:id="@+id/mBigImage"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:customSsivId="@+id/mSSIV"
        >
    <com.github.piasy.biv.example.MySSIV
            android:id="@+id/mSSIV"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            />
</com.github.piasy.biv.view.BigImageView>
```

BIV look up custom SSIV through `customSsivId`, don't forget that.

### Runnable demo

You can try the example to checkout the differences! https://fir.im/BIV . Thanks
for fir.im!


## Caveats

+ Handle permission when you want to save image into gallery.
+ When you want load local image file, you can create the Uri via
`Uri.fromFile`, but the path will be url encoded, and may cause the image loader
fail to load it, consider using `Uri.parse("file://" + file.getAbsolutePath())`.


## Why another big image viewer?

There are several big image viewer libraries,
[PhotoDraweeView](https://github.com/ongakuer/PhotoDraweeView),
[FrescoImageViewer](https://github.com/stfalcon-studio/FrescoImageViewer), and
[Subsampling Scale Image
View](https://github.com/davemorrissey/subsampling-scale-image-view).

They both support pan and zoom. PhotoDraweeView and FrescoImageViewer both use
Fresco to load image, which will cause extremely large memory usage when showing
big images. Subsampling Scale Image View uses very little memory, but it can
only show local image file.

This library show big image with Subsampling Scale Image View, so it only uses
very little memory. And this library support using different image load
libraries, so it's full featured!

If you are interested in how does this library work, you can refer to [this
issue](https://github.com/Piasy/BigImageViewer/issues/8), and [Subsampling Scale
Image View](https://github.com/davemorrissey/subsampling-scale-image-view).


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
+ [ ] Component to display image list, with memory optimization
+ [x] Fail image
+ [x] Retry when fail
+ [ ] PicassoImageLoader, track [this issue](https://github.com/square/picasso/issues/506)

Those features are offered by image load libraries, and they should be easy to
implement, but I don't have enough time currently. So your contributions are
welcome!
