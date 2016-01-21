package com.meibeike.meiphoto.common.clientconnect;

import java.io.UnsupportedEncodingException;

import com.meibeike.meiphoto.common.protocol.bmodel.IEntity;

public class ClientSocketUtils {

  /**
   * 拼装数据 ，包括包头 和包头内容
   * 
   * @return
   */
  public static byte[] sendDatas(IEntity entity) {
    byte[] data = null;
    PackageHeader ph = null;
    try {
      data = entity.onEncode().getBytes("utf-8");
      ph = new PackageHeader(data.length, 0, 1, 0, entity.getFunction(), (short) 0, (short) entity.getSubfunction(),
          (short) entity.getSubversion(), 0);
    } catch (UnsupportedEncodingException e) {
    }
    return PackageHeader.byteMerger(ph.getPackageHeader(), data);
  }

  /**
   * 拼装数据 ，包括包头 和包头内容
   * 
   * @return
   */
  public static byte[] sendDatas(int serialNumber, IEntity entity) {
    byte[] data = null;
    PackageHeader ph = null;
    try {
      data = entity.onEncode().getBytes("utf-8");
      ph = new PackageHeader(data.length, 0, serialNumber, entity.getFileType(), entity.getFunction(), (short) 0,
          (short) entity.getSubfunction(), (short) entity.getSubversion(), 0);
    } catch (UnsupportedEncodingException e) {
    }
    return PackageHeader.byteMerger(ph.getPackageHeader(), data);
  }

  /**
   * 心跳包
   * 
   * @return
   */
  public static byte[] sendHeartbeat(int mainFunction) {
    PackageHeader ph = new PackageHeader(0, 0, 1, 0, mainFunction, (short) 0, (short) 9999, 0, 0);
    return ph.getPackageHeader();
  }

  /**
   * 退出协议
   * 
   * @return
   */
  public static byte[] sendExit(int mainFunction) {

    PackageHeader ph = new PackageHeader(0, 0, 1, 0, mainFunction, (short) 0, (short) 9998, 0, 0);
    return ph.getPackageHeader();
  }
}
