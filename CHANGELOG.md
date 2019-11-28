# Change log

+ v1.6.2
  - Update dependencies, fix #184
+ v1.6.1
  - Update click handling, fix #180
+ v1.6.0
  - Experimental support for shared element transition
+ v1.5.7
  - Add `ImageLoader.cancelAll()` to allow APP cancel all flying requests, avoiding memory leak;
+ v1.5.6
  - Replace deprecated Glide SimpleTarget, #131
  - Fix NPE when thumbnail scale type not specified, #130
+ v1.5.5
  - Migrate to Android X
+ v1.5.4
  - Add thumbnail scale type attribute
+ v1.5.3
  - Determine still/animated webp, show still webp with SSIV
  - Use ImageView to display animated webp rather than leave it blank when using GlideImageLoader
+ v1.5.2
  - Support custom Glide components
+ v1.5.1
  - Fix #110, click listener bug
+ v1.5.0
  - Gif support, cheers~ 🍻
  - Move thumbnail view creation from `ImageLoader` into `ImageViewFactory`
+ v1.4.7
  - Fix #91
+ v1.4.6
  - Remove commons-io dependency
+ v1.4.5
  - Add `cancel` method
  - Auto cancel when detach from window to fix memory leak
+ v1.4.4
  - Add `INIT_SCALE_TYPE_START` scale type
  - Rename `INIT_SCALE_TYPE_AUTO` to `INIT_SCALE_TYPE_CUSTOM`
+ v1.4.3
  - Fix #72: allow disable tap to retry fail image
+ v1.4.2
  - Fix crash when pass attr to SSIV in some case
+ v1.4.1
  - Remove logging
  - Update dependencies
+ v1.4.0
  - Upgrade to Glide 4.x
  - Update min sdk version to 14 according to Glide 4.x
+ v1.3.2
  - Fix Android Studio preview issue
+ v1.3.1
  - Fix NPE when no failure image specified
  - Support custom SSIV
+ v1.3.0
  - Add events callback for library user
  - Support failure image, and tap-to-reload
  - `currentImageFile` -> `getCurrentImageFile`
+ v1.2.9
  - Add access to the internal SSIV, #42
+ v1.2.8
  - Update fresco to v1.2.0, #41
+ v1.2.7
  - Allow disable display optimization, #35, #38
+ v1.2.6
  - Fix #37
+ v1.2.5
  - let users handle permission check
  - animate to hide thumbnail image
+ v1.2.4
  - fix crash, thanks YOLO
+ v1.2.3
  - add initScaleType
+ v1.2.2
  - Fix #16
+ v1.2.1
  - Change minSdkVersion to 10
+ v1.2.0
  - Add progress and thumbnail support
+ v1.1.3
  - Play auto scale animation only when showing long image
+ v1.1.2
  - Optimize image display effect, including long image support
  - Save image file to gallery
