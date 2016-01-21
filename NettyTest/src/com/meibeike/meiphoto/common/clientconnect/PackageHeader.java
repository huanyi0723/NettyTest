package com.meibeike.meiphoto.common.clientconnect;


public class PackageHeader {

  /**
   * 包头字节长度,v0,26
   */
  private static int HEADER_LEN_V0 = 26;

  /**
   * 包头字节长度，v1，40
   */
  private static int HEADER_LEN_V1 = 40;

  public static int headerLenth = HEADER_LEN_V1;

  private long inclusionLenth; // 包体的总长度
  private byte encryption; // 加密: 包类型，0-json格式，1-上传文件格式，3-下载文件格式
  private short serialNumber; // 流水号
  private byte fileType; // 文件类型: 包类型，0-json格式，1-上传文件格式，3-下载文件格式
  private int mainFunction; // 主功能号即模块功能号
  private short headerFunction; // 包头版本号，值为0
  private int function; // 功能号: 子功能号即具体的业务功能号
  private short subversion; // 子版本号: 子版本号即具体的业务功能版本号
  /**
   * v1版本，时间戳
   */
  private long timestamp;
  private short retain; // 保留字段

  /**
   * v1版本，保留字段2
   */
  private byte[] retain2 = new byte[6]; // 保留字段

  /**
   * 通信格式转换
   * 
   * Java和一些windows编程语言如c、c++、delphi所写的网络程序进行通讯时， 需要进行相应的转换 高、低字节之间的转换
   * windows的字节序为低字节开头 linux,unix的字节序为高字节开头 java则无论平台变化，都是高字节开头
   * 
   * byte[]的数组基本类型需要大端和小端的转换 字符串不需要大端和小端的转换
   * 
   * 整数0x01020304的两种表示方法
   * 
   * 低地址----------------高地址
   * 
   * 04 03 02 01---------------->方法1：小端模式(高字节放到低地址上)
   * 
   * 01 02 03 04---------------->方法2：大端模式(高字节放到高地址上) 网络字节序
   * 
   * 
   */

  public PackageHeader() {
    this.timestamp = System.currentTimeMillis();
    headerLenth = HEADER_LEN_V1;
  }

  /**
   * 包头构造函数
   * 
   * @param inclusionLenth
   *          包体长度
   * @param encryption
   *          加密类型 0 为不加密
   * @param serialNumber
   *          流水号
   * @param fileType
   *          包类型
   * @param mainFunction
   *          主功能号，即模块功能号
   * @param headerFunction
   *        包头版本号
   * @param function
   *          业务功能号
   * @param subversion
   *          业务功能版本号
   * @param retain
   *          保留字
   */
  public PackageHeader(long inclusionLenth, int encryption, int serialNumber, int fileType, 
      int mainFunction, int headerFunction, int function, int subversion, int retain) {
    this.inclusionLenth = inclusionLenth;
    this.encryption = (byte) encryption;
    this.serialNumber = (short) serialNumber;
    this.fileType = (byte) fileType;
    this.mainFunction = mainFunction;
    this.headerFunction = (short) 1;// headerFunction;
    this.function = function;
    this.subversion = (short) subversion;
    this.timestamp = System.currentTimeMillis(); // 时间戳
    this.retain = (short) retain;
    this.retain2 = new byte[6];
    if (this.headerFunction >= 1) {
      headerLenth = HEADER_LEN_V1;
    } else {
      headerLenth = HEADER_LEN_V0;
    }
  }

  // 服务端获取值
  public PackageHeader(byte[] readBuffer) {

    // this.inclusionLenth = getinclusionLenth(readBuffer);
    // this.encryption = getEncryption(readBuffer);
    // this.serialNumber = getSerialNumber(readBuffer);
    // this.fileType = getFileType(readBuffer);
    // this.mainFunction = getMainFunction(readBuffer);
    // this.headerFunction = getHeaderFunction(readBuffer);
    // this.function = getFunction(readBuffer);
    // this.subversion = getSubversion(readBuffer);
    // this.retain = getRetain(readBuffer,pos);
    this.setPackageHeader(readBuffer);
  }

  public void setPackageHeader(byte[] readBuffer) {
    // int pos = 0;
    // this.inclusionLenth = getinclusionLenth(readBuffer,pos);
    // this.encryption = getEncryption(readBuffer,pos);
    // this.serialNumber = getSerialNumber(readBuffer,pos);
    // this.fileType = getFileType(readBuffer,pos);
    // this.mainFunction = getMainFunction(readBuffer,pos);
    // this.headerFunction = getHeaderFunction(readBuffer,pos);
    // this.function = getFunction(readBuffer,pos);
    // this.subversion = getSubversion(readBuffer,pos);
    //
    // this.retain = getRetain(readBuffer,pos);

    int pos = 0;
    this.inclusionLenth = getinclusionLenth(readBuffer, pos);
    pos += 8;

    this.encryption = getEncryption(readBuffer, pos);
    pos += 1;

    this.serialNumber = getSerialNumber(readBuffer, pos);
    pos += 2;

    this.fileType = getFileType(readBuffer, pos);
    pos += 1;

    this.mainFunction = getMainFunction(readBuffer, pos);
    pos += 4;

    this.headerFunction = getHeaderFunction(readBuffer, pos);
    pos += 2;

    this.function = getFunction(readBuffer, pos);
    pos += 4;

    this.subversion = getSubversion(readBuffer, pos);
    pos += 2;

    if (headerFunction >= 1) {
      this.timestamp = getTimestamp(readBuffer, pos);
      pos += 8;
    }

    this.retain = getRetain(readBuffer, pos);
    pos += 2;

    if (headerFunction >= 1) {
      this.retain2 = getRetain2(readBuffer, pos);
      pos += 6;
    }
  }

  private short getHeaderFunction(byte[] readBuffer, int pos) {
    // return ByteUtil.byteToShort(readBuffer, 12+4);
    return ByteUtil.byteToShort(readBuffer, pos);
  }

  private int getMainFunction(byte[] readBuffer, int pos) {
    // return ByteUtil.byteArrayToInt(readBuffer, 8+4);
    return ByteUtil.byteArrayToInt(readBuffer, pos);
  }

  private short getSubversion(byte[] readBuffer, int pos) {
    // return ByteUtil.byteToShort(readBuffer, 18+4);
    return ByteUtil.byteToShort(readBuffer, pos);
  }

  private long getTimestamp(byte[] readBuffer, int pos) {
    // return ByteUtil.bytestoLong(readBuffer, 18+4);
    return ByteUtil.bytestoLong(readBuffer, pos);
  }

  private int getFunction(byte[] readBuffer, int pos) {
    // return ByteUtil.byteArrayToInt(readBuffer, 14+4);
    return ByteUtil.byteArrayToInt(readBuffer, pos);
  }

  private byte getFileType(byte[] readBuffer, int pos) {
    // return readBuffer[7+4];
    return readBuffer[pos];
  }

  public byte getFileType() {
    return this.fileType;
  }

  public void setFileType(byte fileType) {
    this.fileType = fileType;
  }

  public int getFunction() {
    return this.function;
  }

  public void setFunction(int function) {
    this.function = function;
  }

  public short getSubversion() {
    return this.subversion;
  }

  public void setSubversion(short subversion) {
    this.subversion = subversion;
  }

  public long getInclusionLenth() {
    return this.inclusionLenth;
  }

  public byte getEncryption() {
    return this.encryption;
  }

  public short getSerialNumber() {
    return this.serialNumber;
  }

  public short getRetain() {
    return this.retain;
  }

  private short getRetain(byte[] readBuffer, int pos) {
    // return ByteUtil.byteToShort(readBuffer, 20+4);
    return ByteUtil.byteToShort(readBuffer, pos);
  }

  private byte[] getRetain2(byte[] readBuffer, int pos) {
    // return ByteUtil.byteToShort(readBuffer, 20+4);
    return new byte[6];
  }

  private short getSerialNumber(byte[] readBuffer, int pos) {
    return ByteUtil.byteToShort(readBuffer, 5 + 4);
  }

  private byte getEncryption(byte[] readBuffer, int pos) {
    // return readBuffer[4+4];
    return readBuffer[pos];
  }

  private long getinclusionLenth(byte[] readBuffer, int pos) {
    // return ByteUtil.bytestoLong(readBuffer, 0);
    return ByteUtil.bytestoLong(readBuffer, pos);
  }

  public int getMainFunction() {
    return this.mainFunction;
  }

  public void setMainFunction(int mainFunction) {
    this.mainFunction = mainFunction;
  }

  public short getHeaderFunction() {
    return this.headerFunction;
  }

  public void setHeaderFunction(short headerFunction) {
    this.headerFunction = headerFunction;
  }

  public void setInclusionLenth(int inclusionLenth) {
    this.inclusionLenth = inclusionLenth;
  }

  public void setEncryption(byte encryption) {
    this.encryption = encryption;
  }

  public void setSerialNumber(short serialNumber) {
    this.serialNumber = serialNumber;
  }

  public void setRetain(short retain) {
    this.retain = retain;
  }

  // 获取整个包头
  public byte[] getPackageHeader() {
    byte[] total_1 = byteMerger(ByteUtil.longtoByte(inclusionLenth), ByteUtil.byteToByteArray(encryption));
    byte[] total_2 = byteMerger(total_1, ByteUtil.shorToByte(serialNumber));
    byte[] total_3 = byteMerger(total_2, ByteUtil.byteToByteArray(fileType));
    byte[] total_4 = byteMerger(total_3, ByteUtil.intToByte(mainFunction));
    byte[] total_5 = byteMerger(total_4, ByteUtil.shorToByte(headerFunction));
    byte[] total_6 = byteMerger(total_5, ByteUtil.intToByte(function));
    byte[] total_7 = byteMerger(total_6, ByteUtil.shorToByte(subversion));
    byte[] total_8;
    if (headerFunction >= 1) {
      byte[] total_timestamp = byteMerger(total_7, ByteUtil.longtoByte(timestamp));
      total_8 = byteMerger(total_timestamp, ByteUtil.shorToByte(retain));
    } else {
      total_8 = byteMerger(total_7, ByteUtil.shorToByte(retain));

    }

    if (headerFunction >= 1) {
      byte[] total_retain2 = byteMerger(total_8, retain2);
      return total_retain2;
    } else {
      return total_8;
    }

    // return byteMerger(intToByte(inclusionLenth), byteToByteArray(encryption),
    // shorToByte(serialNumber),
    // shorToByte(retain));
  }

  // java 合并2个byte数组
  public static byte[] byteMerger(byte[] byte_1, byte[] byte_2) {
    byte[] byteTotal = new byte[byte_1.length + byte_2.length];
    System.arraycopy(byte_1, 0, byteTotal, 0, byte_1.length);
    System.arraycopy(byte_2, 0, byteTotal, byte_1.length, byte_2.length);
    return byteTotal;
  }

  // java 合并4个byte数组
  public static byte[] byteMerger(byte[] byte_1, byte[] byte_2, byte[] byte_3, byte[] byte_4) {
    byte[] byteTotal = new byte[byte_1.length + 1 + byte_3.length + byte_4.length];

    System.arraycopy(byte_1, 0, byteTotal, 0, byte_1.length);
    System.arraycopy(byte_2, 0, byteTotal, byte_1.length, 1);
    System.arraycopy(byte_3, 0, byteTotal, byte_1.length + byte_2.length, byte_3.length);
    System.arraycopy(byte_4, 0, byteTotal, byte_1.length + byte_2.length + byte_3.length, byte_4.length);

    return byteTotal;
  }

  public String toString() {
    return "inclusionLenth = " + inclusionLenth + ";  encryption = " + encryption + ";  serialNumber = " + serialNumber + ";  fileType = " + fileType + ";  function = " + function
        + "; subversion = " + subversion + ";timestamp = " + timestamp + "; retain = " + retain;
  }

}
