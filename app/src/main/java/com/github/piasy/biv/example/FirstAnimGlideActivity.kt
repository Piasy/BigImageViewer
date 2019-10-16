package com.github.piasy.biv.example

import android.os.Bundle
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class FirstAnimGlideActivity : AppCompatActivity() {

    companion object {
        private const val THUMB_URL =
            "https://images.unsplash.com/photo-1497240299146-17ff4089466a?dpr=2&auto=compress,format&fit=crop&w=376"
        private const val SOURCE_URL =
            "https://images.unsplash.com/photo-1497240299146-17ff4089466a"
    }

    private val thumb by lazy { findViewById<ImageView>(R.id.thumbView) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_anim_first_glide)

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

        val glide = Glide.with(this)
        glide.asBitmap()
            .load(THUMB_URL)
            .into(thumb)
    }
}