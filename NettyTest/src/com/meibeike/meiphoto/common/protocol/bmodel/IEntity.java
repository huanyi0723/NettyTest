package com.meibeike.meiphoto.common.protocol.bmodel;

import android.os.Handler;

public interface IEntity {

  public String onEncode();

  public void onDecode(String temp);

  public Handler getHandler();

  public int getFunction();

  public int getSubfunction();

  public int getSubversion();

  public byte getFileType();
  
  public int  getResType() ; //请求类型

  public int  getDownloadType() ; //请求类型

  public int getCode();
  
  public String getMessage();

}
