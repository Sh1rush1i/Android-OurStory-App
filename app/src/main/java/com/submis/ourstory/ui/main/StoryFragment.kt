package com.submis.ourstory.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.submis.ourstory.R
import com.submis.ourstory.databinding.FragmentStoryBinding
import com.submis.ourstory.dom.story.model.Story
import com.submis.ourstory.ui.adapter.LoadingAdapter
import com.submis.ourstory.ui.adapter.StoryAdapter
import com.submis.ourstory.ui.main.viewmodel.StoryViewModel
import com.submis.ourstory.utils.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StoryFragment : Fragment() {

    private var _binding: FragmentStoryBinding? = null
    private val binding get() = _binding
    private val viewModel by activityViewModels<StoryViewModel>()
    private lateinit var storyAdapter: StoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStoryBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Create an instance of StoryAdapter
        storyAdapter = StoryAdapter { id, ivStory, tvName ->
            navigateToDetail(id, ivStory, tvName)
        }

        // Create an instance of LoadingAdapter
        val loadAdapter = LoadingAdapter {
            storyAdapter.retry()
        }

        // Add a LoadStateListener to the StoryAdapter to handle different load states.
        storyAdapter.addLoadStateListener {
            if (it.source.refresh is LoadState.NotLoading && it.append.endOfPaginationReached && storyAdapter.itemCount < 1) {
                showMessage(getString(R.string.txt_empty))
            }
        }

        binding?.apply {

            // Set up a listener for the swipe-to-refresh layout
            updateNewStory.setOnRefreshListener {
                storyAdapter.refresh()
                updateNewStory.isRefreshing = false // Stop the refreshing animation
            }

            // Configure RecyclerView for displaying the stories.
            rvStory.apply {
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
                adapter = storyAdapter.withLoadStateFooter(loadAdapter)
            }

            // Set up a click listener to back on top
            btnBackToTop.setOnClickListener {
                rvStory.smoothScrollToPosition(0)
            }

            // Listener to the RecyclerView to handle the visibility back top button
            rvStory.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    btnBackToTop.isVisible = recyclerView.isNotAtTop()
                }
            })
        }

        // Observe the stories LiveData from the ViewModel
        viewModel.stories.observe(viewLifecycleOwner, storyObserver)
    }

    // Check if the RecyclerView is not at the top
    private fun RecyclerView.isNotAtTop(): Boolean {
        return canScrollVertically(-1)
    }

    // Handle navigation to the detail story page with shared element transition
    private fun navigateToDetail(id: String?, ivStory: ImageView?, tvName: TextView?) {
        if (id == null || ivStory == null || tvName == null) {
            return
        }

        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
            requireActivity(),
            Pair(ivStory, getString(R.string.txt_story_image)),
            Pair(tvName, getString(R.string.txt_story_name))
        ).toBundle()

        val intent = Intent(requireActivity(), DetailActivity::class.java).apply {
            putExtra(DetailActivity.EXTRA_ID, id)
        }

        requireActivity().startActivity(intent, options)
    }

    // Observer for the LiveData containing PagingData of Story objects
    private val storyObserver = Observer<PagingData<Story>> { result ->
        storyAdapter.submitData(lifecycle, result)
        hideRefresh()
    }

    // Hide the swipe-to-refresh
    private fun hideRefresh() {
        binding?.updateNewStory?.isRefreshing = false
    }

    // Show a custom dialog with the provided message and alignment
    private fun showMessage(message: String?) {
        showDialogInfo(
            context = requireActivity(),
            message = message.toString(),
            alignment = Gravity.CENTER
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}