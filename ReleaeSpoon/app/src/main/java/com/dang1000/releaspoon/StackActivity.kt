package com.dang1000.releaspoon

import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
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
                c.setOnClickListener { select(c.id == cvAuto.id) }
            }
        }

        select(true)
    }

    fun select(auto: Boolean) {
        viewDataBinding.run {
            cvAuto.isChecked = auto
            cvAuto.strokeColor = if (auto) indigo300 else indigo100
            cvManual.isChecked = !auto
            cvManual.strokeColor = if (!auto) mint300 else mint100
            viewAuto.root.isVisible = auto
            viewManual.root.isVisible = !auto
        }
    }
}