package com.github.piasy.fresco.bigimageviewer;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.Nullable;
import com.facebook.drawee.backends.pipeline.DraweeConfig;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.request.ImageRequest;

/**
 * Created by Piasy{github.com/Piasy} on 06/11/2016.
 */

public final class FrescoBigImageViewer {
    private FrescoBigImageViewer() {
        // no instance
    }

    public static void initialize(Context context) {
        initialize(context, null, null);
    }

    public static void initialize(Context context,
            @Nullable ImagePipelineConfig imagePipelineConfig) {
        initialize(context, imagePipelineConfig, null);
    }

    public static void initialize(Context context,
            @Nullable ImagePipelineConfig imagePipelineConfig,
            @Nullable DraweeConfig draweeConfig) {
        Fresco.initialize(context, imagePipelineConfig, draweeConfig);
    }

    public static void prefetch(Uri... uris) {
        if (uris == null) {
            return;
        }

        ImagePipeline pipeline = Fresco.getImagePipeline();
        for (Uri uri : uris) {
            pipeline.prefetchToDiskCache(ImageRequest.fromUri(uri),
                    false); // we don't need context, but avoid null
        }
    }
}
