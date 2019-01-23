package com.gushenge.testdemo.SwipeLeftOrRight;

import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.gushenge.testdemo.R;

public class SwipeActivity extends AppCompatActivity {

    @BindView(R.id.root_layout)
    RelativeLayout rootLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe);
        ButterKnife.bind(this);
        initUi();
    }

    private void initUi() {
        rootLayout.setLongClickable(true);
        rootLayout.setOnTouchListener(new TouchListener(this));
    }

    private class TouchListener extends GestureListener {
        public TouchListener(Context context) {
            super(context);
        }

        @Override
        public boolean left() {
            Toast.makeText(SwipeActivity.this, "左滑", Toast.LENGTH_SHORT).show();
            return super.left();
        }

        @Override
        public boolean right() {
            Toast.makeText(SwipeActivity.this, "右滑", Toast.LENGTH_SHORT).show();
            return super.right();
        }
    }
}
