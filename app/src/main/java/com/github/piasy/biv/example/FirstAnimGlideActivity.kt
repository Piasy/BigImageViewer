package com.github.piasy.biv.example

import android.os.Bundle
import android.widget.CheckBox
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class FirstAnimGlideActivity : AppCompatActivity() {

    companion object {
        private const val THUMB_URL =
                "https://images.unsplash.com/photo-1497240299146-17ff4089466a?dpr=2&auto=compress,format&fit=crop&w=376"
        private const val SOURCE_URL =
                "https://images.unsplash.com/photo-1497240299146-17ff4089466a"

        private const val THUMB_URL2 =
                "https://preview.redd.it/k18ckrkhrzt31.jpg?width=640&crop=smart&auto=webp&s=32ee988aaf9dd0af480cc2df104e38fd8ce73b03"
        private const val SOURCE_URL2 =
                "https://i.redd.it/k18ckrkhrzt31.jpg"
    }

    private val thumb by lazy { findViewById<ImageView>(R.id.thumbView) }
    private val glide by lazy { Glide.with(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_anim_first_glide)

        val useViewFactory = findViewById<CheckBox>(R.id.check_use_view_factory)
        val useTallImage = findViewById<CheckBox>(R.id.check_use_tall_image)

        thumb.setOnClickListener {
            SecondAnimActivity.start(this, thumb,
                    thumbUrl = if (!useTallImage.isChecked) THUMB_URL else THUMB_URL2,
                    sourceUrl = if (!useTallImage.isChecked) SOURCE_URL else SOURCE_URL2,
                    useGlide = true, useFresco = false, useViewFactory = useViewFactory.isChecked
            )
        }

        useTallImage.setOnCheckedChangeListener { _, isChecked ->

            loadThumb(isChecked)
        }

        loadThumb(useTallImage.isChecked)
    }

    private fun loadThumb(isChecked: Boolean) {

        if (isChecked) {

            glide.asBitmap()
                    .load(THUMB_URL2)
                    .into(thumb)
        } else {

            glide.asBitmap()
                    .load(THUMB_URL)
                    .into(thumb)
        }
    }
}