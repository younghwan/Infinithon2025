package com.dang1000.releaspoon

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.animation.RotateAnimation
import android.widget.ProgressBar
import androidx.core.view.children
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import com.dang1000.releaspoon.StackActivity.FileKind
import com.dang1000.releaspoon.base.BaseActivity
import com.dang1000.releaspoon.databinding.ActivityInputBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.sequences.forEach

class InputActivity : BaseActivity<ActivityInputBinding>() {
    override val layoutResID: Int get() = R.layout.activity_input

    private var fileUrl: String = ""
    private var fileTypeHint: String = ""

    private val viewAuto by lazy { viewDataBinding.viewAuto }
    private var typeChipId: Int = 0
    private val defaultChipId by lazy { viewDataBinding.viewAuto.chipGradle.id }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 완료 버튼
        viewDataBinding.btnDone.setOnClickListener {

            doProgressBar()
        }

        // 취소 버튼(선택)
        viewDataBinding.btnCancel.setOnClickListener { finish() }

        viewAuto.chipGroupKind.setOnCheckedStateChangeListener { group, checkedIds ->
            typeChipId = checkedIds.firstOrNull() ?: defaultChipId
            group.children.forEach { view ->
                val chip = view as com.google.android.material.chip.Chip
                chip.isChipIconVisible = (chip.id == typeChipId)
            }
            viewAuto.etUrl.text = null
            viewAuto.etUrl.hint = packageType(typeChipId)
            fileTypeHint = packageType(typeChipId)
        }
        viewAuto.chipGroupKind.check(defaultChipId)
        viewAuto.etUrl.hint = packageType(defaultChipId)

        viewAuto.etUrl.doAfterTextChanged {
            fileUrl = it?.toString().orEmpty()
        }

    }

    private fun doProgressBar() {
        viewDataBinding.clProgressBar.visibility = View.VISIBLE
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

            val data = intent.apply {
                putExtra(EXTRA_FILE_URL, fileUrl)
                putExtra(EXTRA_FILE_TYPE_HINT, fileTypeHint)
            }
            setResult(RESULT_OK, data)
            finish()
        }
    }

    private fun packageChipId(id: Int): FileKind = when (id) {
        viewAuto.chipGradle.id -> FileKind.BUILD_GRADLE
        viewAuto.chipSwift.id -> FileKind.PACKAGE_SWIFT
        viewAuto.chipPubspec.id -> FileKind.PUBSPEC_YAML
        viewAuto.chipPackageJson.id -> FileKind.PACKAGE_JSON
        else -> FileKind.BUILD_GRADLE
    }

    private fun packageType(id: Int): String = when (packageChipId(id)) {
        FileKind.BUILD_GRADLE -> "build.gradle"
        FileKind.PACKAGE_SWIFT -> "Package.swift"
        FileKind.PUBSPEC_YAML -> "pubspec.yaml"
        FileKind.PACKAGE_JSON -> "package.json"
    }
}
