/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2019 Piasy
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

package com.github.piasy.biv.example

import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.SharedElementCallback
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.drawable.ScalingUtils
import com.facebook.drawee.view.SimpleDraweeView

class FirstAnimFrescoActivity : AppCompatActivity() {

    companion object {
        private const val THUMB_URL =
            "https://images.unsplash.com/photo-1497240299146-17ff4089466a?dpr=2&auto=compress,format&fit=crop&w=376"
        private const val SOURCE_URL =
            "https://images.unsplash.com/photo-1497240299146-17ff4089466a"
    }

    private val thumb by lazy { findViewById<SimpleDraweeView>(R.id.thumbView) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Fresco.initialize(this)
        setContentView(R.layout.activity_anim_first_fresco)

        ActivityCompat.setExitSharedElementCallback(this, object : SharedElementCallback() {
            override fun onSharedElementEnd(
                sharedElementNames: MutableList<String>?,
                sharedElements: MutableList<View>?,
                sharedElementSnapshots: MutableList<View>?
            ) {
                super.onSharedElementEnd(sharedElementNames, sharedElements, sharedElementSnapshots)
                thumb.visibility = View.VISIBLE
                thumb.drawable.setVisible(true, true)
            }
        })

        val useGlide = findViewById<RadioButton>(R.id.use_glide)
        val useFresco = findViewById<RadioButton>(R.id.use_fresco)
        val useViewFactory = findViewById<CheckBox>(R.id.check_use_view_factory)

        thumb.setOnClickListener {
            SecondAnimActivity.start(
                this, thumb,
                THUMB_URL, SOURCE_URL,
                useGlide.isChecked, useFresco.isChecked, useViewFactory.isChecked
            )
        }

        thumb.setLegacyVisibilityHandlingEnabled(true)
        val controller = Fresco.newDraweeControllerBuilder()
            .setUri(THUMB_URL)
            .build()

        thumb.hierarchy.actualImageScaleType = ScalingUtils.ScaleType.FIT_START
        thumb.controller = controller
    }
}