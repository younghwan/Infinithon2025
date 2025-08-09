package com.dang1000.releaspoon

// FeedCache.kt (간단 SharedPreferences JSON 캐시)

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

object FeedCache {
    private const val PREF = "feed_cache"
    private const val KEY = "items" // JSON Array: [{name:"a", version:"1.0"}]

    fun save(context: Context, pairs: List<Pair<String, String>>) {
        val arr = JSONArray().apply {
            pairs.forEach { (name, ver) ->
                put(JSONObject().apply {
                    put("name", name); put("version", ver)
                })
            }
        }
        context.getSharedPreferences(PREF, 0).edit().putString(KEY, arr.toString()).apply()
    }

    fun load(context: Context): List<Pair<String, String>> {
        val s = context.getSharedPreferences(PREF, 0).getString(KEY, null) ?: return emptyList()
        return runCatching {
            val arr = JSONArray(s)
            (0 until arr.length()).map {
                val o = arr.getJSONObject(it)
                o.getString("name") to o.getString("version")
            }
        }.getOrElse { emptyList() }
    }
}
