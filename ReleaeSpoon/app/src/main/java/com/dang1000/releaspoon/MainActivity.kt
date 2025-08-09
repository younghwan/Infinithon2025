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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewDataBinding.rvFeed.layoutManager = LinearLayoutManager(this)

        // 서버 요청
        requestAndShow(
            fileUrl = "https://github.com/younghwan/android-calculator-mvvm/blob/main/counter/build.gradle",
            fileTypeHint = "build.gradle"
        )
    }

    private fun requestAndShow(fileUrl: String, fileTypeHint: String) {
        lifecycleScope.launch {
            val result = repo.fetchArtifacts(fileUrl, fileTypeHint)
            result.onSuccess { res ->
                if (res.artifacts.isNotEmpty()) {
                    val artifact = res.artifacts.first() // 일단 첫 번째만 표시
                    viewDataBinding.rvFeed.adapter = FeedAdapter(
                        name = artifact.name,
                        items = artifact.versions
                    )
                    Log.d("younghwan", "success $res")
                } else {
                    Log.d("younghwan", "표시할 아티팩트가 없습니다.")
                }
            }.onFailure {
                Log.d("younghwan", "요청 실패: ${it.message}")
            }
        }
    }
}
