package top.gushenge.testdemo.EmulatorCheck;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import top.gushenge.testdemo.R;

public class EmulatorActivity extends AppCompatActivity {

    @BindView(R.id.emulator)
    TextView emulator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emulator);readSysProperty();
        ButterKnife.bind(this);
        if (readSysProperty()) {
            emulator.setText(R.string.phone);
        } else {
            emulator.setText(R.string.emulator);
        }
    }
    /**
     * 判断处理器基带等信息,超过两项及以上通过即为真机
     * 如此可以排除大部分模拟器
     * return false 为模拟器 */
    public boolean readSysProperty() {
        int suspectCount = 0;
        //判断是否存在光传感器来判断是否为模拟器
        SensorManager sensorManager = (SensorManager)this.getSystemService(SENSOR_SERVICE);
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        if (null == sensor) ++suspectCount;
        //读基带信息
        String basebandVersion = CommandUtil.getSingleInstance().getProperty("gsm.version.baseband");
        if (basebandVersion == null | "".equals(basebandVersion)) ++suspectCount;
        //读渠道信息，针对一些基于vbox的模拟器
        String buildFlavor = CommandUtil.getSingleInstance().getProperty("ro.build.flavor");
        if (buildFlavor == null | "".equals(buildFlavor) | (buildFlavor != null && buildFlavor.contains("vbox")))
            ++suspectCount;
        //读处理器信息，这里经常会被处理
        String productBoard = CommandUtil.getSingleInstance().getProperty("ro.product.board");
        if (productBoard == null | "".equals(productBoard)) ++suspectCount;
        //读处理器平台，这里不常会处理
        String boardPlatform = CommandUtil.getSingleInstance().getProperty("ro.board.platform");
        if (boardPlatform == null | "".equals(boardPlatform)) ++suspectCount;
        //高通的cpu两者信息一般是一致的
        if (productBoard != null && boardPlatform != null && !productBoard.equals(boardPlatform))
            ++suspectCount;
        //一些模拟器读取不到进程租信息
        String filter = CommandUtil.getSingleInstance().exec("cat /proc/self/cgroup");
        if (filter == null || filter.length() == 0) ++suspectCount;
        return suspectCount < 2;
    }
}
