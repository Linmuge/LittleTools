package com.gushenge.testdemo.TaobaoNews

import android.content.Context
import android.graphics.Color
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

import java.util.Random

import com.gushenge.testdemo.R
 class NewsAdapter(val context:Context) : BaseAdapter(), AutoScrollView.AutoScroll {
     override val immovableCount: Int
         get() = 2
     private val random = Random()
    private var mLayoutInflater: LayoutInflater? = null
    override fun getCount(): Int {
        return 10
    }

    override fun getItem(position: Int): Any? {
        return null
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view:View
        val viewHolder: ViewHolder
        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.news_item, parent, false)
            viewHolder = ViewHolder()
            viewHolder.tvTitle = view.findViewById<TextView>(R.id.tv_title)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = convertView.tag as ViewHolder
        }
        viewHolder.tvTitle!!.text = "第" + position + "个item"
        return view
    }

    /*传入item高度*/
    override fun getListItemHeight(context: Context): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50f, context.resources.displayMetrics).toInt()
    }


    internal inner class ViewHolder {
        var tvTitle: TextView? = null
    }
}
