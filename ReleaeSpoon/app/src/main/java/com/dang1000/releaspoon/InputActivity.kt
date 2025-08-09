package com.dang1000.releaspoon

import android.app.Activity
import android.os.Bundle
import androidx.core.view.children
import androidx.core.widget.doAfterTextChanged
import com.dang1000.releaspoon.StackActivity.FileKind
import com.dang1000.releaspoon.base.BaseActivity
import com.dang1000.releaspoon.databinding.ActivityInputBinding
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

        // 초기값 세팅(있으면)
        fileUrl = intent.getStringExtra(EXTRA_FILE_URL).orEmpty()
        fileTypeHint = intent.getStringExtra(EXTRA_FILE_TYPE_HINT).orEmpty()

//        viewDataBinding.etFileUrl.setText(fileUrl)
//        viewDataBinding.etFileTypeHint.setText(fileTypeHint)
//
//        // 텍스트 변경 감지
//        viewDataBinding.etFileUrl.doAfterTextChanged { fileUrl = it?.toString().orEmpty() }
//        viewDataBinding.etFileTypeHint.doAfterTextChanged { fileTypeHint = it?.toString().orEmpty() }

        // 완료 버튼
        viewDataBinding.btnDone.setOnClickListener {
            val data = intent.apply {
                putExtra(EXTRA_FILE_URL, viewAuto.etUrl.text.toString())
                putExtra(EXTRA_FILE_TYPE_HINT, typeChipId)
            }
            setResult(RESULT_OK, data)
            finish()
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
        }
        viewAuto.chipGroupKind.check(defaultChipId)
        viewAuto.etUrl.hint = packageType(defaultChipId)

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
