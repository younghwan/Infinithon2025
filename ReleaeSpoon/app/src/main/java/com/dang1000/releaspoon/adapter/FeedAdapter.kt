package com.dang1000.releaspoon.adapter

import ArtifactFeed
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.dang1000.releaspoon.databinding.ItemFeedBinding

class FeedAdapter : RecyclerView.Adapter<FeedAdapter.FeedViewHolder>() {

    /** 원본 데이터 */
    private val items: MutableList<Pair<String, ArtifactFeed>> = mutableListOf()

    /** 화면에 보여줄 데이터 */
    private var filteredItems: List<Pair<String, ArtifactFeed>> = items

    inner class FeedViewHolder(val binding: ItemFeedBinding) :
        RecyclerView.ViewHolder(binding.root)

    /** 외부에서 데이터 추가 */
    fun addItems(newItems: List<Pair<String, ArtifactFeed>>) {
        items.addAll(newItems)
        filteredItems = items
        notifyDataSetChanged()
    }

    /** 북마크 필터 */
    fun filterBookmarks(showOnlyBookmarks: Boolean) {
        filteredItems = if (showOnlyBookmarks) {
            items.filter { it.second.isBookmarked }
        } else {
            items
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder {
        val binding = ItemFeedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FeedViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
        val (name, feed) = filteredItems[position]

        with(holder.binding) {
            tvDoc.text = feed.text
            tvVersion.text = feed.version
            tvLibraryName.text = name

            btnLink.setOnClickListener {
                val firstLink = feed.links.firstOrNull() ?: return@setOnClickListener
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(firstLink))
                it.context.startActivity(intent)
            }

            ivBookmark.isSelected = feed.isBookmarked
            ivBookmark.setOnClickListener { v ->
                v.isSelected = !v.isSelected
                feed.isBookmarked = v.isSelected
            }
            when (feed.impact) {
                "high" -> {
                    cpImpact.isVisible = true
                    cpMed.isVisible = false
                    cpLow.isVisible = false
                }
                "medium" -> {
                    cpImpact.isVisible = false
                    cpMed.isVisible = true
                    cpLow.isVisible = false
                }
                "low" -> {
                    cpImpact.isVisible = false
                    cpMed.isVisible = false
                    cpLow.isVisible = true
                }
            }
        }
    }

    override fun getItemCount(): Int = filteredItems.size
}
