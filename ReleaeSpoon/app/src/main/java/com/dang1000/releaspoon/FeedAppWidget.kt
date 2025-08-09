package com.dang1000.releaspoon

// widget/FeedAppWidget.kt

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.RemoteViews

class FeedAppWidget : AppWidgetProvider() {

    override fun onUpdate(ctx: Context, mgr: AppWidgetManager, ids: IntArray) {
        ids.forEach { id -> updateOne(ctx, mgr, id) }
    }

    private fun updateOne(ctx: Context, mgr: AppWidgetManager, id: Int) {
        val views = RemoteViews(ctx.packageName, R.layout.widget_feed).apply {
            // List 어댑터 연결
            val svcIntent = Intent(ctx, FeedRemoteViewsService::class.java).apply {
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, id)
                data = Uri.parse(toUri(Intent.URI_INTENT_SCHEME)) // 인스턴스 구분
            }
            setRemoteAdapter(R.id.lvFeed, svcIntent)

            // 항목 클릭 시 앱 열기(메인)
            val openMain = PendingIntent.getActivity(
                ctx, 0, Intent(ctx, MainActivity::class.java),
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
            setPendingIntentTemplate(R.id.lvFeed, openMain)

            // + 버튼 → InputActivity
            val plusPI = PendingIntent.getActivity(
                ctx, 1, Intent(ctx, InputActivity::class.java),
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
            setOnClickPendingIntent(R.id.ivPlus, plusPI)

            // 북마크 버튼 동작은 단순 새로고침으로 처리(필요 시 확장)
            val refreshIntent = Intent(ctx, FeedAppWidget::class.java).apply {
                action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, intArrayOf(id))
            }
            val refreshPI = PendingIntent.getBroadcast(
                ctx, id, refreshIntent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
            setOnClickPendingIntent(R.id.ivSortBookmark, refreshPI)
        }
        mgr.updateAppWidget(id, views)
    }

    companion object {
        fun notifyAll(ctx: Context) {
            val mgr = AppWidgetManager.getInstance(ctx)
            val ids = mgr.getAppWidgetIds(ComponentName(ctx, FeedAppWidget::class.java))
            mgr.notifyAppWidgetViewDataChanged(ids, R.id.lvFeed)
        }
    }
}
