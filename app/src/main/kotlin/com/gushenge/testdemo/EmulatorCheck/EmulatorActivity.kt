package com.gushenge.testdemo.EmulatorCheck

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

import butterknife.BindView
import butterknife.ButterKnife
import com.gushenge.testdemo.BaseActivity
import com.gushenge.testdemo.R
import kotlinx.android.synthetic.main.activity_emulator.*

class EmulatorActivity : BaseActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emulator)
        if (readSysProperty()) {
            emulator.setText(R.string.phone)
        } else {
            emulator.setText(R.string.emulator)
        }
    }

    /**
     * 判断处理器基带等信息,超过两项及以上通过即为真机
     * 如此可以排除大部分模拟器
     * return false 为模拟器  */
    private fun readSysProperty(): Boolean {
        var suspectCount = 0
        //判断是否存在光传感器来判断是否为模拟器
        val sensorManager = this.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
        if (null == sensor) ++suspectCount
        //读基带信息
        val basebandVersion = CommandUtil().getProperty("gsm.version.baseband")
        if ((basebandVersion == null) or ("" == basebandVersion)) ++suspectCount
        //读渠道信息，针对一些基于vbox的模拟器
        val buildFlavor = CommandUtil().getProperty("ro.build.flavor")
        if ((buildFlavor == null) or ("" == buildFlavor) or (buildFlavor != null && buildFlavor!!.contains("vbox")))
            ++suspectCount
        //读处理器信息，这里经常会被处理
        val productBoard = CommandUtil().getProperty("ro.product.board")
        if ((productBoard == null) or ("" == productBoard)) ++suspectCount
        //读处理器平台，这里不常会处理
        val boardPlatform = CommandUtil().getProperty("ro.board.platform")
        if ((boardPlatform == null) or ("" == boardPlatform)) ++suspectCount
        //高通的cpu两者信息一般是一致的
        if (productBoard != null && boardPlatform != null && productBoard != boardPlatform)
            ++suspectCount
        //一些模拟器读取不到进程租信息
        val filter = CommandUtil().exec("cat /proc/self/cgroup")
        if (filter == null || filter.length == 0) ++suspectCount
        return suspectCount < 2
    }
}
