package com.meibeike.meiphoto.common.protocol.bmodel;

import android.os.Handler;


public abstract class EntityImpl implements IEntity {
  protected int function;
  protected int subfunction;
  protected int subversion;
  protected Handler handler = null;
  private Handler handler2 = null;
  protected byte fileType = 0;
  protected int downloadType = 0; // 0为文件下载 1位缩略图下载

  protected int resType = 0x401;// 优先网络的

  public EntityImpl() {
    super();

  }

  public EntityImpl(int function, int subfunction, int subversion) {
    super();
    this.function = function;
    this.subfunction = subfunction;
    this.subversion = subversion;
  }

  public int getFunction() {
    return this.function;
  }

  public void setFunction(int function) {
    this.function = function;
  }

  public int getSubfunction() {
    return this.subfunction;
  }

  public void setSubfunction(int subfunction) {
    this.subfunction = subfunction;
  }

  public int getSubversion() {
    return this.subversion;
  }

  public void setSubversion(int subversion) {
    this.subversion = subversion;
  }

  public Handler getHandler() {
    return this.handler;
  }

  public void setHandler(Handler handler) {
    this.handler = handler;
  }

  public byte getFileType() {
    return this.fileType;
  }

  public void setFileType(byte fileType) {
    this.fileType = fileType;
  }

  @Override
  public int getCode() {
    return 0;
  }

  public String getMessage() {
    return null;
  }

  public int getResType() {
    return this.resType;

  }

  public void setResType(int resType) {
    this.resType = resType;
  }

  public int getDownloadType() {
    return this.downloadType;
  }

  public void setDownloadType(int downloadType) {
    this.downloadType = downloadType;
  }

  public Handler getHandler2() {
    return this.handler2;
  }

  public void setHandler2(Handler handler2) {
    this.handler2 = handler2;
  }

}
