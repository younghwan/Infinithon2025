package com.dang1000.releaspoon

import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import android.util.Log

class FeedRemoteViewsService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory =
        FeedFactory(applicationContext)
}

private class FeedFactory(private val ctx: Context) : RemoteViewsService.RemoteViewsFactory {

    private val items = mutableListOf<Pair<String, String>>() // (name, version)

    override fun onCreate() {}

    override fun onDataSetChanged() {
        items.clear()
        val loaded = FeedCache.load(ctx)
        items += loaded
        Log.d("Widget", "onDataSetChanged size=${items.size}")
    }

    override fun getCount() = items.size.also {
        Log.d("Widget", "getCount=$it")
    }

    override fun getViewAt(position: Int): RemoteViews {
        val (name, ver) = items[position]
        return RemoteViews(ctx.packageName, R.layout.widget_feed_item).apply {
            setTextViewText(R.id.tvName, name)
            setTextViewText(R.id.tvVersion, ver)

            // 항목 클릭 시 열릴 인텐트(Provider에서 setPendingIntentTemplate와 매칭)
            val fillIn = Intent().apply {
                putExtra("name", name)
                putExtra("version", ver)
            }
            setOnClickFillInIntent(R.id.tvName, fillIn)
        }
    }

    override fun getLoadingView(): RemoteViews? = null
    override fun getViewTypeCount() = 1
    override fun getItemId(position: Int) = position.toLong()
    override fun hasStableIds() = true
    override fun onDestroy() { items.clear() }
}
