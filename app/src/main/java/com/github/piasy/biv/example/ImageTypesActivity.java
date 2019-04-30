/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2018 Piasy
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.piasy.biv.example;

import android.net.Uri;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;
import com.github.piasy.biv.BigImageViewer;
import com.github.piasy.biv.indicator.progresspie.ProgressPieIndicator;
import com.github.piasy.biv.loader.fresco.FrescoImageLoader;
import com.github.piasy.biv.loader.glide.GlideImageLoader;
import com.github.piasy.biv.metadata.ImageInfoExtractor;
import com.github.piasy.biv.view.BigImageView;
import com.github.piasy.biv.view.FrescoImageViewFactory;
import com.github.piasy.biv.view.GlideImageViewFactory;
import com.github.piasy.biv.view.ImageViewFactory;
import java.util.Arrays;
import java.util.List;

public class ImageTypesActivity extends AppCompatActivity {
    private static final int IMAGE_LOADER_FRESCO = 1;
    private static final int IMAGE_LOADER_GLIDE = 2;

    private static final List<NamedValue> IMAGE_LOADERS = Arrays.asList(
            new NamedValue("Fresco", IMAGE_LOADER_FRESCO),
            new NamedValue("Glide", IMAGE_LOADER_GLIDE)
    );
    private static final List<NamedValue> IMAGE_TYPES = Arrays.asList(
            new NamedValue("Normal", ImageInfoExtractor.TYPE_STILL_IMAGE),
            new NamedValue("Gif", ImageInfoExtractor.TYPE_GIF),
            new NamedValue("Animated WebP", ImageInfoExtractor.TYPE_ANIMATED_WEBP),
            new NamedValue("Still WebP", ImageInfoExtractor.TYPE_STILL_WEBP)
    );
    private static final SparseArray<String> IMAGE_URLS = new SparseArray<>(4);

    static {
        IMAGE_URLS.put(ImageInfoExtractor.TYPE_STILL_IMAGE,
                "https://upload.wikimedia.org/wikipedia/commons/9/99/Las_Meninas_01.jpg");
        IMAGE_URLS.put(ImageInfoExtractor.TYPE_GIF,
                "https://upload.wikimedia.org/wikipedia/commons/a/aa/SmallFullColourGIF.gif");
        IMAGE_URLS.put(ImageInfoExtractor.TYPE_ANIMATED_WEBP,
                "https://mathiasbynens.be/demo/animated-webp-supported.webp");
        IMAGE_URLS.put(ImageInfoExtractor.TYPE_STILL_WEBP,
                "http://www.gstatic.com/webp/gallery/1.webp");
    }

    private int mImageLoader = IMAGE_LOADER_FRESCO;
    private int mImageType = ImageInfoExtractor.TYPE_STILL_IMAGE;

    private LinearLayout mRootLayout;
    private BigImageView mBiv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_type);

        mRootLayout = findViewById(R.id.rootLayout);

        Spinner spLoader = findViewById(R.id.spLoader);
        spLoader.setAdapter(getArrayAdapter(IMAGE_LOADERS));
        spLoader.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> parent, final View view,
                    final int position, final long id) {
                mImageLoader = IMAGE_LOADERS.get(position).value;
            }

            @Override
            public void onNothingSelected(final AdapterView<?> parent) {
            }
        });

        Spinner spType = findViewById(R.id.spType);
        spType.setAdapter(getArrayAdapter(IMAGE_TYPES));
        spType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> parent, final View view,
                    final int position, final long id) {
                mImageType = IMAGE_TYPES.get(position).value;
            }

            @Override
            public void onNothingSelected(final AdapterView<?> parent) {
            }
        });

        findViewById(R.id.load).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (mBiv != null) {
                    mRootLayout.removeView(mBiv);
                }
                ImageViewFactory imageViewFactory;
                switch (mImageLoader) {
                    case IMAGE_LOADER_FRESCO:
                        BigImageViewer.initialize(FrescoImageLoader.with(getApplicationContext()));
                        imageViewFactory = new FrescoImageViewFactory();
                        break;
                    case IMAGE_LOADER_GLIDE:
                        BigImageViewer.initialize(GlideImageLoader.with(getApplicationContext()));
                        imageViewFactory = new GlideImageViewFactory();
                        break;
                    default:
                        return;
                }
                mBiv = new BigImageView(ImageTypesActivity.this);
                mBiv.setImageViewFactory(imageViewFactory);
                mBiv.setProgressIndicator(new ProgressPieIndicator());
                mRootLayout.addView(mBiv,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
                mBiv.showImage(Uri.parse(IMAGE_URLS.get(mImageType)));
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        BigImageViewer.imageLoader().cancelAll();
    }

    private <T> ArrayAdapter getArrayAdapter(final List<T> list) {
        ArrayAdapter<T> spinnerAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, list);
        spinnerAdapter.setDropDownViewResource(
                android.R.layout.simple_list_item_single_choice);
        return spinnerAdapter;
    }

    static class NamedValue {
        final String name;
        final int value;

        NamedValue(final String name, final int value) {
            this.name = name;
            this.value = value;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
