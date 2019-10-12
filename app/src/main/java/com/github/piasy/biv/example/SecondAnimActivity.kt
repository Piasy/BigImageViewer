package com.github.piasy.biv.example

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.transition.Transition
import android.util.Log
import android.view.MenuItem
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.net.toUri
import com.github.piasy.biv.BigImageViewer
import com.github.piasy.biv.loader.glide.GlideImageLoader
import com.github.piasy.biv.view.BigImageView
import com.github.piasy.biv.view.ImageCycleCallback

class SecondAnimActivity : AppCompatActivity() {

    companion object {

        private const val THUMB_PARAM = "intent_param_thumbnail"
        private const val SOURCE_PARAM = "intent_param_source"

        fun start(activity: Activity, imageView: ImageView, thumbUrl: String, sourceUrl: String) {

            val intent = Intent(activity, SecondAnimActivity::class.java)
            intent.putExtra(THUMB_PARAM, thumbUrl)
            intent.putExtra(SOURCE_PARAM, sourceUrl)

            val transitionName = activity.resources.getString(R.string.transition_name)
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, imageView, transitionName)
            val bundle = options.toBundle()

            if (Build.VERSION.SDK_INT >= 16) {
                activity.startActivity(intent, bundle)
            } else {
                Log.i("SecondAnimActivity", "Animation not available for this SDK Versions.")
            }
        }
    }

    private val thumbUrl by lazy { intent.getStringExtra(THUMB_PARAM)!! }
    private val sourceUrl by lazy { intent.getStringExtra(SOURCE_PARAM)!! }

    private val biv by lazy { findViewById<BigImageView>(R.id.sourceView) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        BigImageViewer.initialize(GlideImageLoader.with(applicationContext))

        setContentView(R.layout.activity_anim_second)

        if (Build.VERSION.SDK_INT >= 21) {
            window.sharedElementEnterTransition.addListener(object : Transition.TransitionListener {

                override fun onTransitionStart(transition: Transition?) {}
                override fun onTransitionCancel(transition: Transition?) {}
                override fun onTransitionPause(transition: Transition?) {}
                override fun onTransitionResume(transition: Transition?) {}

                override fun onTransitionEnd(transition: Transition?) {

                    biv.triggerImageSwap()
                }
            })
        }

        biv.setImageCycleCallback(object : ImageCycleCallback {

            override fun onThumbnailShown() {
                showToast("onThumbnailShown triggered!")
            }

            override fun onImageShown() {
                showToast("onImageShown triggered!")
            }
        })

        biv.showImage(thumbUrl.toUri(), sourceUrl.toUri(), true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId) {
            android.R.id.home -> {
                supportFinishAfterTransition()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun showToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }
}