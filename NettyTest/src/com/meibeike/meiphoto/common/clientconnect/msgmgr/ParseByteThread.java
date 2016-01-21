package com.meibeike.meiphoto.common.clientconnect.msgmgr;


import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.meibeike.meiphoto.common.clientconnect.PackageHeader;
import com.meibeike.meiphoto.common.clientconnect.impl.IClientConnect;

/**
 * com.meibeike.meiphoto.common.clientconnect.baseconnect.parseByteArray<br/>
 * 消息解析线程
 * 
 * @author 缪运锦 <br/>
 *         create at 2015年6月9日 上午11:53:13
 */
class ParseByteThread implements Runnable {

  private byte[] bufHeader = null;
  private byte[] readData = null;
  private PackageHeader header = null;
  private int headerLenth = PackageHeader.headerLenth;
  private int readDataLenth = 0;
  private int sLength = 0;// 添加到数组的长度
  private Handler fileParseHandler = null;
  private IClientConnect connect;
  
  public static final int RESPONSE_SUCCESS = 0x401;
  public static final int RESPONSE_FAIL = 0x402;
  public static final int RESPONSE_TIMEOUT = 0x403;
  /** 心跳超时  */
  public static final int REQUEST_HEARTBEAT_TIMEOUT = 0x410;
  /** 用户未登录  */
  public static final int  NOT_LOGIN= 0x411;

  public Handler getFileParseHandler() {
    return this.fileParseHandler;
  }

  public void sendParseByte(byte[] msg) {
    if (fileParseHandler != null) {
      Message msgData = Message.obtain();
      msgData.obj = msg;
      fileParseHandler.sendMessage(msgData);
    }
  }
  
  public ParseByteThread(IClientConnect connect) {
    readDataLenth = 0;
    sLength = 0;
    headerLenth = PackageHeader.headerLenth;
    bufHeader = new byte[PackageHeader.headerLenth];
    readData = null;
    header = new PackageHeader();
    this.connect = connect;
  }

  public void setFileParseHandler(Handler fileParseHandler) {
    this.fileParseHandler = fileParseHandler;
  }
  
  public void closeThread(){
    readDataLenth = 0;
    sLength = 0;
    headerLenth = PackageHeader.headerLenth;
    bufHeader = new byte[PackageHeader.headerLenth];
    readData = null;
    header = new PackageHeader();
  }
  @Override
  public void run() {

    Looper.prepare();
    fileParseHandler = new Handler() {
      public void handleMessage(Message data) {
        synchronized (data) {
          byte[] msg = (byte[]) data.obj;
          if (msg == null) {
            return;
          }
          int msgLength = msg.length;
          int useLength = 0;// 已经使用的长度
          while (msgLength - useLength > 0) {
            // 读取包头
            if (readDataLenth == 0) {
              if (msgLength - useLength >= headerLenth - sLength) {
                // 读取了一个完整的包头
                System.arraycopy(msg, useLength, bufHeader, sLength, headerLenth - sLength);
                useLength += (headerLenth - sLength);
                sLength = 0;
                header.setPackageHeader(bufHeader);
                if (header.getFunction() > 10000 || header.getFunction() < 999) {
                  // 包头不符合，跳出循环 放弃整包
                  connect.callBack(null, null, "包头不符合",  RESPONSE_FAIL);
                  break;
                }
                if (header.getFunction() != 9999 && header.getFunction() != 9998) {
                  readDataLenth = (int) header.getInclusionLenth();
                  readData = null;
                  readData = new byte[readDataLenth];
                } else if (header.getFunction() == 9999) {
                  // 发送心跳包
                  connect.callBack(header, readData, "",  RESPONSE_SUCCESS);
                } else if (header.getFunction() == 9998) {
                  msgLength = 0;
                  useLength = 0;
                  connect.callBack(header, readData, "",  RESPONSE_SUCCESS);
                }
              } else {

                System.arraycopy(msg, useLength, bufHeader, sLength, msgLength - useLength);
                sLength += (msgLength - useLength);
                break;
              }
            }
            // 读取包体
            else {
              if (msgLength - useLength >= readDataLenth - sLength) {
                // 读取了一个完整的包体
                System.arraycopy(msg, useLength, readData, sLength, readDataLenth - sLength);
                useLength += (readDataLenth - sLength);
                sLength = 0;
                readDataLenth = 0;
                bufHeader = null;
                bufHeader = new byte[PackageHeader.headerLenth];
                // 解析成功 返回数据
                try {
                  connect.callBack(header, readData, "",  RESPONSE_SUCCESS);
                } catch (Exception e) {
                  e.printStackTrace();
                }
              } else {
                System.arraycopy(msg, useLength, readData, sLength, msgLength - useLength);
                sLength += (msgLength - useLength);
                break;
              }
            }
          }
        }
      }
    };
    Looper.loop();
  }
}
