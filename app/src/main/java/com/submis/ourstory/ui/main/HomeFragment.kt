package com.submis.ourstory.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.submis.ourstory.databinding.FragmentHomeBinding
import com.submis.ourstory.ui.adapter.CarouselAdapter


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        @Suppress("DEPRECATION")
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        // Use the binding to get the RecyclerView reference
        val recyclerView: RecyclerView = binding.recyclerCarousel
        val arrayList = ArrayList<String>()

        // Add multiple images to the ArrayList
        arrayList.add("https://asset.brandfetch.io/idK1RDMSRY/idzryC-3Zi.jpeg")
        arrayList.add("https://i.ibb.co.com/n8qgPvb/apa-itu-google.png")
        arrayList.add("https://i.ibb.co.com/Cmk9cHH/1667205415-goto-fp.png")
        arrayList.add("https://asset.kompas.com/crops/s4jtWQy8J3bf1zUn5FXCl-YOKCY=/9x0:685x451/1200x800/data/photo/2020/05/03/5eae34a7dd090.jpg")
        arrayList.add("https://www.pranataprinting.com/wp-content/uploads/2021/02/Sejarah-Singkat-Perusahaan-Traveloka-dan-Perkembangannya.jpg")

        // Initialize the adapter with the current context and ArrayList
        val adapter = CarouselAdapter(requireContext(), arrayList)
        recyclerView.adapter = adapter

        // Set up the item click listener for the adapter
        adapter.setItemClickListener(object : CarouselAdapter.OnItemClickListener {
            override fun onClick(imageView: ImageView?, path: String?) {
                // Handle the click event, e.g., open the image in a new activity or show it in full screen
            }
        })

        // Return the root view from the binding
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {

            btnSettings.setOnClickListener{
                navigateToSettings()
            }
        }
    }

    private fun navigateToSettings() {
        val intent = Intent(requireActivity(), SettingsActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }
}