package com.orhanobut.sample;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

public class ApkUtils {
  private static String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};


  /**
   * 请求权限
   *
   * @param context
   */
  public static boolean requestStorePermission(Context context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      int i = context.checkSelfPermission(permissions[0]);
      int l = context.checkSelfPermission(permissions[1]);
      if (i != PackageManager.PERMISSION_GRANTED || l != PackageManager.PERMISSION_GRANTED) {
        return false;
      } else {
        return true;
      }
    }
    return true;
  }
}
