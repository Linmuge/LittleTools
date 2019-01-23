package com.gushenge.testdemo.TaobaoNews;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.Random;

import com.gushenge.testdemo.R;

public class NewsAdapter extends BaseAdapter implements AutoScrollView.AutoScroll {
    private Random random = new Random();
    private LayoutInflater mLayoutInflater;
    @Override
    public int getCount() {
        return 10;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            if (mLayoutInflater == null) {
                mLayoutInflater = LayoutInflater.from(parent.getContext());
            }
            convertView = mLayoutInflater.inflate(R.layout.news_item, parent,false);
            viewHolder = new ViewHolder();
            viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        convertView.setBackgroundColor(Color.argb(100, random.nextInt(255), random.nextInt(255), random.nextInt(255)));
        viewHolder.tvTitle.setText("第"+position + "个item");
        return convertView;
    }

    /*传入item高度*/
    @Override
    public int getListItemHeight(Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, context.getResources().getDisplayMetrics());
    }
    /*传入展示数量*/
    @Override
    public int getImmovableCount() {
        return 2;
    }

    class ViewHolder{
        public TextView tvTitle;
    }
}
