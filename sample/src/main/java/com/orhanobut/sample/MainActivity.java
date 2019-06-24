package com.orhanobut.sample;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.orhanobut.logger.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends Activity {
  private static String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
  public static final int READ_AND_WRITE = 1001;

  /**
   * 用户权限申请的回调方法
   *
   * @param requestCode  申请权限方传入的请求代码
   * @param permissions  请求的权限
   * @param grantResults 相应权限的授予结果
   */
  @Override
  public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if (requestCode == READ_AND_WRITE) {
      if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
        //如果没有获取权限，那么可以提示用户去设置界面--->应用权限开启权限
        Toast.makeText(this, "获取读取权限失败，无法查看U盘", Toast.LENGTH_LONG).show();
      } else {
        Toast.makeText(this, "有存储权限", Toast.LENGTH_LONG).show();
      }
    }
  }


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    if (ApkUtils.requestStorePermission(this)) {
    } else {
      Toast.makeText(this, "没有存储权限", Toast.LENGTH_LONG).show();
      requestPermissions(permissions, 2);
    }
    Log.d("hsw Tag", "I'm a log which you don't see easily, hehe");
    Log.d("hsw json content", "{ \"key\": 3, \n \"value\": something}");
    Log.d("hsw error", "There is a crash somewhere or any warning");

    Logger.addLogAdapter(new AndroidLogAdapter());
    Logger.d("hsw message");

    Logger.clearLogAdapters();


    FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
        .showThreadInfo(false)  // (Optional) Whether to show thread info or not. Default true
        .methodCount(0)         // (Optional) How many method line to show. Default 2
        .methodOffset(3)        // (Optional) Skips some method invokes in stack trace. Default 5
//        .logStrategy(customLog) // (Optional) Changes the log strategy to print out. Default LogCat
        .tag("hsw My custom tag")   // (Optional) Custom tag for each log. Default PRETTY_LOGGER
        .build();

    Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy));

    Logger.addLogAdapter(new AndroidLogAdapter() {
      @Override public boolean isLoggable(int priority, String tag) {
        return BuildConfig.DEBUG;
      }
    });

    Logger.addLogAdapter(new DiskLogAdapter());


    Logger.w("hsw 今天你进步了吗");

    Logger.clearLogAdapters();

    Logger.addLogAdapter(new ExpDiskLogAdapter());


    Logger.w("hsw 今天你吃饭了吗");

    //会把当前注册的所有适配器全部删掉
    Logger.clearLogAdapters();
    formatStrategy = PrettyFormatStrategy.newBuilder()
        .showThreadInfo(false)
        .methodCount(0)
        .build();

    Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy));
    Logger.i("hsw 没吃");

    Logger.t("hsw tag").e("Custom tag for only one use");

    Logger.json("{ \"key\": 3, \"value\": something}");

    Logger.d(Arrays.asList("foo", "bar"));

    Map<String, String> map = new HashMap<>();
    map.put("key", "value");
    map.put("key1", "value2");

    Logger.d(map);

    Logger.clearLogAdapters();
    formatStrategy = PrettyFormatStrategy.newBuilder()
        .showThreadInfo(false)
        .methodCount(0)
        .tag("hsw MyTag")
        .build();
    Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy));

    Logger.w("hsw my log message with my tag");
  }
}
