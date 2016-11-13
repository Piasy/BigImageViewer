/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Piasy
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

package com.github.piasy.biv.indicator.progresspie;

import android.view.LayoutInflater;
import android.view.View;
import com.filippudak.ProgressPieView.ProgressPieView;
import com.github.piasy.biv.indicator.ProgressIndicator;
import com.github.piasy.biv.view.BigImageView;
import java.util.Locale;

/**
 * Created by Piasy{github.com/Piasy} on 12/11/2016.
 */

public class ProgressPieIndicator implements ProgressIndicator {
    private ProgressPieView mProgressPieView;

    @Override
    public View getView(BigImageView parent) {
        mProgressPieView = (ProgressPieView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ui_progress_pie_indicator, parent, false);
        return mProgressPieView;
    }

    @Override
    public void onStart() {
        // not interested
    }

    @Override
    public void onProgress(int progress) {
        if (progress < 0 || progress > 100) {
            return;
        }
        mProgressPieView.setProgress(progress);
        mProgressPieView.setText(String.format(Locale.getDefault(), "%d%%", progress));
    }

    @Override
    public void onFinish() {
        // not interested
    }
}
