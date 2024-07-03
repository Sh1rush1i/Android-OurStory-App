package com.submis.ourstory.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.submis.ourstory.R

class CarouselAdapter(var context: Context, private var arrayList: ArrayList<String>) :
    RecyclerView.Adapter<CarouselAdapter.ViewHolder>() {

    private var onItemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.item_carousel, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(context).load(arrayList[position]).into(holder.imageView)
        holder.itemView.setOnClickListener { _: View? ->
            onItemClickListener?.onClick(holder.imageView, arrayList[position])
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: ImageView = itemView.findViewById(R.id.list_item_image)
    }

    fun setItemClickListener(listener: OnItemClickListener?) {
        this.onItemClickListener = listener
    }

    interface OnItemClickListener {
        fun onClick(imageView: ImageView?, path: String?)
    }
}
