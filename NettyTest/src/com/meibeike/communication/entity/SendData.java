package com.meibeike.communication.entity;

/**
 * 发送给服务端的数据 com.meibeike.meiphoto.data.SendEntity
 * 
 * @author 张涢 <br/>
 *         create at 2015-5-29 下午3:34:00
 */
public class SendData {
  /**
   * 发送文件类型 0基础协议 1发送文件
   */
  public int sendType = 0;

  /****************** 发送的信息 ***************************/
  public byte[] bytes;// 发送的数据

  /**************************** P2P使用下列参数 ********************************************/
  public PackageHeader header; // 包头
  public String fileID;// 文件临时ID
  public long fileSize;// 文件长度
  public long offset;// 偏移量
//  public int filetype; // 文件类型 映射后缀名
  public String path;// 文件路径

  public void clearData() {
    bytes = null;
    header = null;
    fileID = null;
  }

  /**
   * 文件发送
   * 
   * @param sendType
   *          发送的文件类型 0基础协议 1文件
   * @param bytes
   *          传输的数据 允许为空 例如心跳 P2P传输文件
   * @param header
   *          包头
   * @param fileID
   *          文件临时ID
   * @param fileSize
   *          文件总长度
   * @param offset
   *          文件偏移量
   * @param filetype
   *          文件类型 映射后缀名
   * @param path
   *          文件路径 P2P传输文件时 不允许为空
   */
  public SendData(byte[] bytes) {
    this.bytes = bytes;

  };

  public SendData(int sendType, byte[] bytes, PackageHeader header, String fileID, long fileSize, long offset, String path) {
    this.sendType = sendType;
    this.bytes = bytes;
    this.header = header;
    this.fileID = fileID;
    this.fileSize = fileSize;
    this.offset = offset;
//    this.filetype = filetype;
    this.path = path;
  };
}
