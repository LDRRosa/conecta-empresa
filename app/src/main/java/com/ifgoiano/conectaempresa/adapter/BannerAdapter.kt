package com.ifgoiano.conectaempresa.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ifgoiano.conectaempresa.R

class BannerAdapter(
    private val banners: List<String>  // <-- AGORA É LISTA DE URL (String)
) : RecyclerView.Adapter<BannerAdapter.BannerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_banner, parent, false)

        return BannerViewHolder(view)
    }

    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
        val url = banners[position]

        Glide.with(holder.itemView.context)
            .load(url)
            .into(holder.imageBanner)
    }

    override fun getItemCount(): Int = banners.size

    class BannerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageBanner: ImageView = itemView.findViewById(R.id.imgBanner)
    }
}
