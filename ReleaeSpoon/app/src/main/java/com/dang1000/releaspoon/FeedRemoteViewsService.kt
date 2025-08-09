package com.dang1000.releaspoon

import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.RemoteViewsService

class FeedRemoteViewsService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory =
        FeedFactory(applicationContext)
}

private class FeedFactory(private val ctx: Context) : RemoteViewsService.RemoteViewsFactory {

    private val items = mutableListOf<Pair<String, String>>() // name to version

    override fun onCreate() {}

    override fun onDataSetChanged() {
        // 여기서 동기적으로 데이터 준비 (네트워크 길면 캐시 사용 권장)
        items.clear()
        // 예: 로컬 캐시/DB에서 읽거나, 마지막 MainActivity 결과 저장본을 사용
        items += listOf(
            "androidx.core:core-ktx" to "1.12.0",
            "com.google.android.material:material" to "1.11.0"
        )
    }

    override fun getCount() = items.size

    override fun getViewAt(position: Int): RemoteViews {
        val (name, ver) = items[position]
        return RemoteViews(ctx.packageName, R.layout.widget_feed_item).apply {
            setTextViewText(R.id.tvName, name)
            setTextViewText(R.id.tvVersion, ver)
            // 항목 클릭 시 열릴 인텐트(템플릿에 들어갈 fill-in)
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
    override fun onDestroy() {
        items.clear()
    }
}
