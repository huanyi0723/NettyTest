package com.example.nettytest;

import com.meibeike.meiphoto.common.clientconnect.ClientConnectFactory;

import android.app.Application;
import android.content.Context;

public class MeiApp extends Application{
  
  public static Context mContext;
  
  @Override
  public void onCreate() {
    super.onCreate();
    mContext = this;
    
    ClientConnectFactory.getInstance().init(mContext);
  }
  
  
}
