package com.gushenge.testdemo;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.gushenge.testdemo.EmulatorCheck.EmulatorActivity;
import com.gushenge.testdemo.SwipeLeftOrRight.SwipeActivity;
import com.gushenge.testdemo.TaobaoNews.NewsActivity;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.maintoswipe)
    Button mainToSwipe;
    @BindView(R.id.emulator_check)
    Button emulatorCheck;
    @BindView(R.id.taobao_news)
    Button news;
    @BindView(R.id.github)
    TextView github;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initUi();
    }

    private void initUi() {
        mainToSwipe.setOnClickListener(v->startActivity(new Intent(this, SwipeActivity.class)));
        emulatorCheck.setOnClickListener(v->startActivity(new Intent(this, EmulatorActivity.class)));
        news.setOnClickListener(v->startActivity(new Intent(this, NewsActivity.class)));
        github.setOnClickListener(V->openBrowser(this));
    }
    public static  void openBrowser(Context context){
        final Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://github.com/Gushenge/LittleTools"));
        // 注意此处的判断intent.resolveActivity()可以返回显示该Intent的Activity对应的组件名
        // 官方解释 : Name of the component implementing an activity that can display the intent
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            final ComponentName componentName = intent.resolveActivity(context.getPackageManager());
            context.startActivity(Intent.createChooser(intent, context.getString(R.string.chooseBrowser)));
        } else {
            Toast.makeText(context.getApplicationContext(), context.getString(R.string.downBrowser), Toast.LENGTH_SHORT).show();
        }
    }
}
