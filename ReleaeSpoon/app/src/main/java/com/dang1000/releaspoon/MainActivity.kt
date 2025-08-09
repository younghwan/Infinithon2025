package com.dang1000.releaspoon

import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.lifecycle.lifecycleScope
import com.dang1000.releaspoon.adapter.FeedAdapter
import com.dang1000.releaspoon.base.BaseActivity
import com.dang1000.releaspoon.databinding.ActivityMainBinding
import com.dang1000.releaspoon.network.ApiProvider
import com.dang1000.releaspoon.network.ChangelogRepository
import kotlinx.coroutines.launch

class MainActivity : BaseActivity<ActivityMainBinding>() {
    override val layoutResID: Int get() = R.layout.activity_main

    private val api by lazy { ApiProvider.create("http://10.99.10.153:8000") } // 서버 주소
    private val repo by lazy { ChangelogRepository(api) }

    private lateinit var adapter: FeedAdapter
    private var showOnlyBookmarks = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1) RecyclerView/Adapter 초기화
        adapter = FeedAdapter()
        viewDataBinding.rvFeed.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }

        // 2) 북마크 필터 토글
        viewDataBinding.ivSortBookmark.setOnClickListener { v ->
            showOnlyBookmarks = !showOnlyBookmarks
            v.isSelected = showOnlyBookmarks
            adapter.filterBookmarks(showOnlyBookmarks)
        }

        // 3) 서버 요청
        requestAndShow(
            fileUrl = SpoonApplication.prefManager.packageUrl,
            fileTypeHint = SpoonApplication.prefManager.packageType
        )
    }

    private fun requestAndShow(fileUrl: String, fileTypeHint: String) {
        lifecycleScope.launch {
            val result = repo.fetchArtifacts(fileUrl, fileTypeHint)
            result
                .onSuccess { res ->
                    val pairs = res.artifacts.flatMap { artifact ->
                        artifact.versions.map { feed -> artifact.name to feed }
                    }
                    if (pairs.isNotEmpty()) {
                        adapter.addItems(pairs)
                        Log.d("younghwan", "success $res")
                    } else {
                        Log.d("younghwan", "표시할 아티팩트가 없습니다.")
                    }
                }
                .onFailure { e ->
                    Log.d("younghwan", "요청 실패: ${e.message}")
                }
        }
    }
}
