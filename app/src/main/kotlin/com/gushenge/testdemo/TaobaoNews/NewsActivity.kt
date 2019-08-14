package com.gushenge.testdemo.TaobaoNews

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import butterknife.BindView
import butterknife.ButterKnife
import com.gushenge.testdemo.BaseActivity
import com.gushenge.testdemo.R
import kotlinx.android.synthetic.main.activity_news.*

class NewsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)
        val adapter = NewsAdapter(this)
        news_list.adapter = adapter
    }
}
