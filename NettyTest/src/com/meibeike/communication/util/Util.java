package com.meibeike.communication.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.util.Log;

/**
 * 工具类
 * com.meibeike.mbk_communicationframework.util.Util
 * @author 张涢 <br/>
 * create at 2015-9-21 上午11:54:36
 */
public class Util {
  private static final String TAG = "Util";
  
  private static final boolean DEBUG = true;//为true打印log,false不打印log
  
  /**
   * 打印log日志，用于发布版本限制打印log
   * 
   * @param TAG
   *          标签
   * @param message
   *          信息
   */
  public static void LogUntils(String TAG, String message) {
    if (DEBUG) {
      Log.i(TAG, "" + message);
    }
  }
  /**
   * 获取当前时间 返回String 类型
   * 
   * @return
   */
  public static String getNow() {
    Date now = new Date();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");// 可以方便地修改日期格式
    String hehe = dateFormat.format(now);
    return hehe + " - ";

  }
}
