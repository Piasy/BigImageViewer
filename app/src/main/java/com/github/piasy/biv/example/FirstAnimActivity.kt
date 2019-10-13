package com.github.piasy.biv.example

import android.os.Bundle
import android.widget.CheckBox
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.drawable.ScalingUtils
import com.facebook.drawee.view.SimpleDraweeView

class FirstAnimActivity : AppCompatActivity() {

    companion object {
        private const val THUMB_URL = "https://preview.redd.it/nahhvcadsbo21.jpg?width=216&crop=smart&auto=webp&s=c560e5774d7f43e178c1f0faad09315cdb86c203"
        private const val SOURCE_URL = "https://i.redd.it/nahhvcadsbo21.jpg"
    }

    private val thumb by lazy { findViewById<SimpleDraweeView>(R.id.thumbView) }

    override fun onCreate(savedInstanceState: Bundle?) {
        Fresco.initialize(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_anim_first)

        val useGlide = findViewById<RadioButton>(R.id.use_glide)
        val useFresco = findViewById<RadioButton>(R.id.use_fresco)
        val useViewFactory = findViewById<CheckBox>(R.id.check_use_view_factory)

        thumb.setOnClickListener {

            SecondAnimActivity.start(this, thumb,
                    THUMB_URL, SOURCE_URL,
                    useGlide.isChecked, useFresco.isChecked, useViewFactory.isChecked)
        }

        if (useGlide.isChecked) {
            loadGlide()
        }

        if (useFresco.isChecked) {
            loadFresco()
        }

        useGlide.setOnCheckedChangeListener { _, isChecked ->

            if (isChecked) {
                loadGlide()
            }
        }

        useFresco.setOnCheckedChangeListener { _, isChecked ->

            if (isChecked) {
                loadFresco()
            }
        }
    }

    private fun loadGlide() {
        val glide = Glide.with(this)
        glide.asBitmap()
                .load(THUMB_URL)
                .into(thumb)

        showToast("Loaded with Glide!")
    }

    private fun loadFresco() {

        val controller = Fresco.newDraweeControllerBuilder()
                .setUri(THUMB_URL)
                .build()

        thumb.hierarchy.actualImageScaleType = ScalingUtils.ScaleType.FIT_START
        thumb.controller = controller

        showToast("Loaded with Fresco!")
    }

    private fun showToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }
}