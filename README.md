# FrescoBigImageViewer

[ ![Download](https://api.bintray.com/packages/piasy/maven/FrescoBigImageViewer/images/download.svg) ](https://bintray.com/piasy/maven/FrescoBigImageViewer/_latestVersion)

Big image viewer using [Fresco](https://github.com/facebook/fresco) and [Subsampling Scale Image View](https://github.com/davemorrissey/subsampling-scale-image-view), support pan and zoom, with very little memory usage.

## Demo

![demo](art/shaped_drawee_view_demo.png)

## Usage

### Dependency

``` gradle
allprojects {
    repositories {
        jcenter()
    }
}

compile 'com.github.piasy:FrescoBigImageViewer:1.0.0'
```

### initialize

``` java
FrescoBigImageViewer.initialize(context);
```

**Note that** if you've already used Fresco in your project, please change `Fresco.initialize` into `FrescoBigImageViewer.initialize`.

### Layout

``` xml
<com.github.piasy.fresco.bigimageviewer.BigImageView
        android:id="@+id/mBigImage"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        />
```

### Java

``` java
BigImageView bigImageView = (BigImageView) findViewById(R.id.mBigImage);
bigImageView.showImage(Uri.parse("http://code2png.babits.top/images/code_1477885912.cpp.png"));
```

### Prefetch

You can prefetch images in advance, so it could be shown immediately when user want to see it.

``` java
FrescoBigImageViewer.prefetch(uris);
```

## Why another big image viewer?

There are several big image viewer libraries, [PhotoDraweeView](https://github.com/ongakuer/PhotoDraweeView), [FrescoImageViewer](https://github.com/stfalcon-studio/FrescoImageViewer), and [Subsampling Scale Image View](https://github.com/davemorrissey/subsampling-scale-image-view).

They both support pan and zoom. PhotoDraweeView and FrescoImageViewer both use Fresco to load image, which will cause extremely large memory usage when showing big images. Subsampling Scale Image View uses very little memory, but it can only show local image file.

This library use Fresco to load image, and show them with Subsampling Scale Image View, so it keeps both features of easy to use and low memory usage.

## Performance

\- | PhotoDraweeView | FrescoImageViewer | FrescoBigImageViewer
4135*5134 | 80MB | 80MB | 2~14 MB

## Todo

+ [ ] Loading animation
+ [ ] Fail image
+ [ ] Retry when fail

Those features are offered by Fresco, and they should be easy to implement, but I don't have enough time currently. So your contributions are welcome!
