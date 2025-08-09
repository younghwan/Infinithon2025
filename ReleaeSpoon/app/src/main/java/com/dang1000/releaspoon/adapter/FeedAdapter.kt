package com.dang1000.releaspoon.adapter

import ArtifactFeed
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dang1000.releaspoon.databinding.ItemFeedBinding

class FeedAdapter(
    private val name: String,
    private val items: List<ArtifactFeed>
) : RecyclerView.Adapter<FeedAdapter.FeedViewHolder>() {

    inner class FeedViewHolder(val binding: ItemFeedBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FeedViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemFeedBinding.inflate(inflater, parent, false)
        return FeedViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
        val item = items[position]
        holder.binding.let {
            it.tvDoc.text = item.text
            it.tvVersion.text = item.version
            it.tvLibraryName.text = name
        }
    }

    override fun getItemCount(): Int = items.size
}
