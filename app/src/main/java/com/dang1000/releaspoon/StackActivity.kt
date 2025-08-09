package com.dang1000.releaspoon

import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.core.view.isVisible
import com.dang1000.releaspoon.base.BaseActivity
import com.dang1000.releaspoon.databinding.ActivityStackBinding

class StackActivity : BaseActivity<ActivityStackBinding>() {
    override val layoutResID: Int
        get() = R.layout.activity_stack

    private val indigo300 by lazy { ContextCompat.getColor(this, R.color.indigo_500) }
    private val indigo100 by lazy { ContextCompat.getColor(this, R.color.indigo_100) }
    private val mint300 by lazy { ContextCompat.getColor(this, R.color.emerald_500) }
    private val mint100 by lazy { ContextCompat.getColor(this, R.color.emerald_100) }

    enum class FileKind { BUILD_GRADLE, PACKAGE_SWIFT, PUBSPEC_YAML, PACKAGE_JSON }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onClick()
    }

    private fun onClick() {
        viewDataBinding.run {
            tvNext.setOnClickListener {
                startActivity(Intent(this@StackActivity, MainActivity::class.java))
            }

            listOf(viewDataBinding.cvAuto, viewDataBinding.cvManual).forEach { c ->
                c.setOnClickListener { buttonSelect(c.id == cvAuto.id) }
            }

            viewAuto.chipGroupKind.setOnCheckedStateChangeListener { group, checkedIds ->
                val id = checkedIds.firstOrNull() ?: viewAuto.chipGradle.id
                group.children.forEach { view ->
                    val chip = view as com.google.android.material.chip.Chip
                    chip.isChipIconVisible = (chip.id == id)
                }
                viewAuto.etUrl.hint = hintFor(kindFromChipId(id))
            }
        }

        buttonSelect(true)
    }

    fun buttonSelect(auto: Boolean) {
        viewDataBinding.run {
            cvAuto.isChecked = auto
            cvAuto.strokeColor = if (auto) indigo300 else indigo100
            cvManual.isChecked = !auto
            cvManual.strokeColor = if (!auto) mint300 else mint100
            viewAuto.root.isVisible = auto
            viewManual.root.isVisible = !auto
        }
    }

    private fun kindFromChipId(id: Int): FileKind = when (id) {
        viewDataBinding.viewAuto.chipGradle.id -> FileKind.BUILD_GRADLE
        viewDataBinding.viewAuto.chipSwift.id -> FileKind.PACKAGE_SWIFT
        viewDataBinding.viewAuto.chipPubspec.id -> FileKind.PUBSPEC_YAML
        viewDataBinding.viewAuto.chipPackageJson.id -> FileKind.PACKAGE_JSON
        else -> FileKind.BUILD_GRADLE
    }

    private fun hintFor(kind: FileKind): String = when (kind) {
        FileKind.BUILD_GRADLE -> "build.gradle"
        FileKind.PACKAGE_SWIFT -> "Package.swift"
        FileKind.PUBSPEC_YAML -> "pubspec.yaml"
        FileKind.PACKAGE_JSON -> "package.json"
    }
}