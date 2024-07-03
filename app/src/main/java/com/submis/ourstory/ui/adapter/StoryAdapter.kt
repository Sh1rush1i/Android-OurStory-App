package com.submis.ourstory.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.submis.ourstory.R
import com.submis.ourstory.databinding.AdapterStoryBinding
import com.submis.ourstory.dom.story.model.Story
import com.submis.ourstory.utils.*

class StoryAdapter(
    private val onItemClicked: (id: String?, ivStory: ImageView?, tvName: TextView?) -> Unit
) : PagingDataAdapter<Story, StoryAdapter.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = AdapterStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val story = getItem(position)
        holder.bind(story)
    }

    inner class ViewHolder(private val binding: AdapterStoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(story: Story?) {
            with(binding) {

                // Load story Image
                Glide.with(itemView.context)
                    .load(story?.photoUrl)
                    .placeholder(R.drawable.custom_background_loading)
                    .into(ivItemPhoto)

                // Set the story name
                tvItemName.text = story?.name

                // Convert the story upload time to a relative time
                storyUploadTime.text = story?.createdAt.toRelativeTime()

                // Set a click listener on the item view
                itemView.setOnClickListener {
                    onItemClicked(story?.id, ivItemPhoto, tvItemName)
                }
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Story>() {
            override fun areItemsTheSame(oldItem: Story, newItem: Story) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Story, newItem: Story) =
                oldItem == newItem
        }
    }
}