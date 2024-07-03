package com.submis.ourstory.ui.main

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.submis.ourstory.R
import com.submis.ourstory.databinding.ActivityDetailBinding
import com.submis.ourstory.dom.Result
import com.submis.ourstory.dom.story.model.Story
import com.submis.ourstory.ui.main.viewmodel.DetailViewModel
import com.submis.ourstory.utils.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private val viewModel by viewModels<DetailViewModel>()

    @SuppressLint("ObsoleteSdkInt")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupWindow()

        val storyIdentity = intent.getStringExtra(EXTRA_ID)
        storyIdentity?.run(viewModel::setStoryId)

        // Initiate toolbar
        setSupportActionBar(binding.toolbarDetails)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Observe changes detailStory LiveData and handling
        viewModel.detailStory.observe(this) { result ->
            when (result) {
                is Result.Loading -> showLoading(true)
                is Result.Success -> {
                    showLoading(false)
                    val detailStory = result.data
                    detailStory.populateDetail()
                }
                is Result.Error -> {
                    showLoading(false)
                    val message = result.message ?: ""
                    if (message.contains("401")) {
                        showMessage(getString(R.string.txt_auth_command))
                        navigateToAuth()
                        return@observe
                    }
                    showMessage(result.message)
                }
            }
        }
    }

    // Set up the window to handle status bar transparency and fullscreen layout
    @SuppressLint("ObsoleteSdkInt")
    private fun setupWindow() {
        if (Build.VERSION.SDK_INT in 19..20) {
            @Suppress("DEPRECATION")
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        }
        if (Build.VERSION.SDK_INT >= 19) {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
        if (Build.VERSION.SDK_INT >= 21) {
            @Suppress("DEPRECATION")
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = Color.TRANSPARENT
        }
        if (Build.VERSION.SDK_INT >= 30) {
            window?.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }
    }

    private fun Story.populateDetail() {
        with(binding) {

            // Set toolbar sub into uploader name
            toolbarDetails.subtitle = name

            // Set textview to uploader name
            tvDetailName.text = name

            // Set textview to desc
            tvDetailDescription.text = description

            // Set upload time
            storyUploadTime.text = createdAt.toRelativeTime()

            // Set detail image with transition
            Glide.with(this@DetailActivity)
                .load(photoUrl)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(ivDetailPhoto)
        }
    }

    // Navigate to auth func
    private fun navigateToAuth() {
        val intent = Intent(this, AuthActivity::class.java)
        startActivity(intent)
        finish()
    }

    // Show loading lottie
    private fun showLoading(isLoading: Boolean) {
        binding.apply {
            loading.apply {
                isVisible = isLoading
                if (isLoading) {
                    playAnimation()
                } else {
                    cancelAnimation()
                }
            }
        }
    }
    // Display a message in custom dialog
    private fun showMessage(message: String?) {
        showDialogInfo(
            context = this,
            message = message.toString(),
            alignment = Gravity.CENTER
        )
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    companion object {
        const val EXTRA_ID = "story_id"
    }
}