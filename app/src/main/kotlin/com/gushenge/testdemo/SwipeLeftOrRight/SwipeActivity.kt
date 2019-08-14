package com.gushenge.testdemo.SwipeLeftOrRight

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.RelativeLayout
import android.widget.Toast

import butterknife.BindView
import butterknife.ButterKnife
import com.gushenge.testdemo.BaseActivity
import com.gushenge.testdemo.R
import kotlinx.android.synthetic.main.activity_swipe.*

class SwipeActivity : BaseActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_swipe)
        initUi()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initUi() {
        root_layout.isLongClickable = true
        root_layout.setOnTouchListener(TouchListener(this))
    }

    private inner class TouchListener(context: Context) : GestureListener(context) {

        override fun left(): Boolean {
            Toast.makeText(this@SwipeActivity, "左滑", Toast.LENGTH_SHORT).show()
            return super.left()
        }

        override fun right(): Boolean {
            Toast.makeText(this@SwipeActivity, "右滑", Toast.LENGTH_SHORT).show()
            return super.right()
        }
    }
}
