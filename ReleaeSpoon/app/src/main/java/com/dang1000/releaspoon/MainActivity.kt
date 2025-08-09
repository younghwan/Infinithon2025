package com.dang1000.releaspoon

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.lifecycle.lifecycleScope
import com.dang1000.releaspoon.adapter.FeedAdapter
import com.dang1000.releaspoon.base.BaseActivity
import com.dang1000.releaspoon.databinding.ActivityMainBinding
import com.dang1000.releaspoon.network.ApiProvider
import com.dang1000.releaspoon.network.ChangelogRepository
import kotlinx.coroutines.launch

import android.view.animation.RotateAnimation
import android.widget.ProgressBar
import androidx.activity.result.contract.ActivityResultContracts
import kotlinx.coroutines.delay

class MainActivity : BaseActivity<ActivityMainBinding>() {
    override val layoutResID: Int get() = R.layout.activity_main

    private val api by lazy { ApiProvider.create("http://10.99.10.153:8000") } // 서버 주소
    private val repo by lazy { ChangelogRepository(api) }

    private lateinit var adapter: FeedAdapter
    private var showOnlyBookmarks = false

    private val inputLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data
            val fileUrl = data?.getStringExtra(EXTRA_FILE_URL).orEmpty()
            val fileTypeHint = data?.getStringExtra(EXTRA_FILE_TYPE_HINT).orEmpty()

            if (fileUrl.isNotBlank() && fileTypeHint.isNotBlank()) {
                requestAndShow(fileUrl, fileTypeHint)
            } else {
                Log.d("younghwan", "입력 값이 비어있습니다.")
            }
        }
    }


    private fun doProgressBar() {
        val progressBar: ProgressBar = findViewById(R.id.progressBar)

        // 회전 애니메이션 설정
        val rotate = RotateAnimation(
            0f, 360f, // 시작 각도와 종료 각도
            RotateAnimation.RELATIVE_TO_SELF, 0.5f, // 회전 기준점 (자기 자신을 기준으로 회전)
            RotateAnimation.RELATIVE_TO_SELF, 0.5f
        )
        rotate.duration = 1000 // 1초 동안 360도 회전
        rotate.repeatCount = RotateAnimation.INFINITE // 무한 반복

        // 애니메이션을 ProgressBar에 적용
        progressBar.startAnimation(rotate)

        // Coroutine을 사용하여 2초 후에 ProgressBar 숨기기
        lifecycleScope.launch {
            delay(2000) // 2초 대기
            progressBar.clearAnimation()
            viewDataBinding.clProgressBar.visibility = View.GONE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        doProgressBar()

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

        viewDataBinding.ivPlus.setOnClickListener {
            val intent = Intent(this, InputActivity::class.java).apply {
                putExtra(EXTRA_FILE_URL, SpoonApplication.prefManager.packageUrl)
                putExtra(EXTRA_FILE_TYPE_HINT, SpoonApplication.prefManager.packageType)
            }
            inputLauncher.launch(intent)
        }
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
