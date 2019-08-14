package com.gushenge.testdemo

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.gushenge.atools.util.AView

open class BaseActivity:AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AView.setStatusBar(this,true,resources.getColor(R.color.colorPrimary))

    }
}