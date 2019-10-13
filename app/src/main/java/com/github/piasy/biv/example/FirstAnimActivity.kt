package com.github.piasy.biv.example

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

class FirstAnimActivity : AppCompatActivity() {

    companion object {
        private const val THUMB_URL = "https://preview.redd.it/nahhvcadsbo21.jpg?width=216&crop=smart&auto=webp&s=c560e5774d7f43e178c1f0faad09315cdb86c203"
        private const val SOURCE_URL = "https://i.redd.it/nahhvcadsbo21.jpg"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_anim_first)

        val thumb = findViewById<ImageView>(R.id.thumbView)
        thumb.setOnClickListener {

            SecondAnimActivity.start(this, thumb, THUMB_URL, SOURCE_URL)
        }

        val glide = Glide.with(this)
        glide.asBitmap()
                .load(THUMB_URL)
                .into(thumb)
    }
}