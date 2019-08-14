package com.gushenge.testdemo

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity

import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

import butterknife.BindView
import butterknife.ButterKnife
import com.gushenge.atools.util.AView
import com.gushenge.testdemo.EmulatorCheck.EmulatorActivity
import com.gushenge.testdemo.SwipeLeftOrRight.SwipeActivity
import com.gushenge.testdemo.TaobaoNews.NewsActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initUi()
    }

    private fun initUi() {
        maintoswipe.setOnClickListener { v -> startActivity(Intent(this, SwipeActivity::class.java)) }
        emulator_check!!.setOnClickListener { v -> startActivity(Intent(this, EmulatorActivity::class.java)) }
        taobao_news!!.setOnClickListener { v -> startActivity(Intent(this, NewsActivity::class.java)) }
        github!!.setOnClickListener { V -> openBrowser(this) }
    }

    companion object {
        fun openBrowser(context: Context) {
            val intent = Intent()
            intent.action = Intent.ACTION_VIEW
            intent.data = Uri.parse("https://github.com/Gushenge/LittleTools")
            // 注意此处的判断intent.resolveActivity()可以返回显示该Intent的Activity对应的组件名
            // 官方解释 : Name of the component implementing an activity that can display the intent
            if (intent.resolveActivity(context.packageManager) != null) {
                val componentName = intent.resolveActivity(context.packageManager)
                context.startActivity(Intent.createChooser(intent, context.getString(R.string.chooseBrowser)))
            } else {
                Toast.makeText(context.applicationContext, context.getString(R.string.downBrowser), Toast.LENGTH_SHORT).show()
            }
        }
    }
}
