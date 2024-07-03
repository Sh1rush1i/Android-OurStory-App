package com.submis.ourstory.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.submis.ourstory.databinding.LayoutLoadingBinding

class LoadingAdapter(private val try_again: () -> Unit) : LoadStateAdapter<LoadingAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): ViewHolder {
        val binding = LayoutLoadingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    inner class ViewHolder(private val binding: LayoutLoadingBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            // Invoke try again via button
            binding.btnTryAgain.setOnClickListener { try_again.invoke() }
        }

        fun bind(loadState: LoadState) {
            with(binding) {

                // Display error message if the load state is Error
                if (loadState is LoadState.Error) {
                    tvErrorMessage.text = loadState.error.localizedMessage
                }

                // Show or hide the Lottie animation on the load state
                if (loadState is LoadState.Loading) {
                    loading.isVisible = true
                    loading.playAnimation()
                } else {
                    loading.isVisible = false
                    loading.cancelAnimation()
                }

                // Show retry button if load state Error
                btnTryAgain.isVisible = loadState is LoadState.Error

                // Show error message if load state Error
                tvErrorMessage.isVisible = loadState is LoadState.Error
            }
        }
    }
}