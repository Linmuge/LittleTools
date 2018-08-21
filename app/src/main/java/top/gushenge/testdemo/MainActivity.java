package top.gushenge.testdemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import top.gushenge.testdemo.EmulatorCheck.EmulatorActivity;
import top.gushenge.testdemo.SwipeLeftOrRight.SwipeActivity;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.maintoswipe)
    Button mainToSwipe;
    @BindView(R.id.emulator_check)
    Button emulatorCheck;
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

    }
}
