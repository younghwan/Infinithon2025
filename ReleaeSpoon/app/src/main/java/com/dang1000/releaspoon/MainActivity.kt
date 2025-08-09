package com.dang1000.releaspoon

import android.os.Bundle
import androidx.activity.ComponentActivity
import com.dang1000.releaspoon.base.BaseActivity
import com.dang1000.releaspoon.databinding.ActivityMainBinding

class MainActivity : BaseActivity<ActivityMainBinding>() {
    override val layoutResID: Int
        get() = R.layout.activity_main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
}
