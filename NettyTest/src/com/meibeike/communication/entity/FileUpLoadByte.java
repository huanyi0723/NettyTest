package com.meibeike.communication.entity;

import java.io.UnsupportedEncodingException;

/**
 * 文件上传 的byte流 com.meibeike.meiphoto.data.ImageUpLoadByte
 * 
 * @author 张涢 <br/>
 *         create at 2015-1-7 下午1:42:27
 */
public class FileUpLoadByte {
  private static final String TAG = "ImageUpLoadByte";
  private String imageid;// 文件id,占8个字节
  private long filesize;// 文件总长度，所占8个字节
  private long offset;// 本次上传的起始字节位置,占8个字节
//  private int filetype;// 文件类型,映射后缀名
  private byte[] imagedata;// 上传的文件数据

  private byte[] mByte;

  public FileUpLoadByte(String imageid, long filesize, long offset, byte[] imagedata) {
    setImageid(imageid);
    setFilesize(filesize);
    setOffset(offset);
//    setFiletype(filetype);
    if (imagedata == null || imagedata.length == 0) {
      if (offset != 0) {
        setImagedata(null);
      }
    } else {
      setImagedata(imagedata);
    }
  }

  public FileUpLoadByte(byte[] bt) {
    setFilesize(bt);
    setImageid(bt);
    setOffset(bt);
    setThisImagedata(bt);
  }

  private void setImageid(byte[] bt) {
    // imageid = bytetoLong(bt, 0);
    // imageid = bytestoLong(bt, 0);
    try {
      imageid = new String(bt, 8, 32, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
  }

  public void setFilesize(byte[] bt) {
    // filesize = bytetoLong(bt, 8);
    filesize = bytestoLong(bt, 0);
  }

  public void setOffset(byte[] bt) {
    // offset = bytetoLong(bt, 16);
    offset = bytestoLong(bt, 40);
  }

  public void setThisImagedata(byte[] bt) {
    imagedata = getByte(bt, 48);
  }

  public byte[] getByte() {
    byte[] bt1 = longtoByte(getFilesize());
    byte[] bt2 = getImageIDByte(getImageid().getBytes());
    byte[] bt3 = longtoByte(getOffset());
    if (imagedata == null || imagedata.length == 0) {
      mByte = byteMerger(bt1, bt2, bt3);
    } else {
      mByte = byteMerger(bt1, bt2, bt3,imagedata);
    }
    return mByte;
  }

  public int getLength() {
    return mByte.length;
  }
  private byte[] getImageIDByte(byte[] bt){
    byte[] bit = new byte[32];
    int size = bt.length;
    for (int i = 0; i < size; i++) {
      bit[i] = bt[i];
    }
    bt = null;
    return bit;
    
  }
  // java 合并4个byte数组
  public static byte[] byteMerger(byte[] byte_1, byte[] byte_2, byte[] byte_3) {
    byte[] byteTotal = new byte[byte_1.length + byte_2.length + byte_3.length];
    System.arraycopy(byte_1, 0, byteTotal, 0, byte_1.length);
    System.arraycopy(byte_2, 0, byteTotal, byte_1.length, byte_2.length);
    System.arraycopy(byte_3, 0, byteTotal, byte_1.length + byte_2.length, byte_3.length);
    byte_1 = null;
    byte_2 = null;
    byte_3 = null;
    return byteTotal;
  }

  

  // java 合并4个byte数组
  public static byte[] byteMerger(byte[] byte_1, byte[] byte_2, byte[] byte_3, byte[] byte_4) {
    byte[] byteTotal = new byte[byte_1.length + byte_2.length + byte_3.length + byte_4.length];
    System.arraycopy(byte_1, 0, byteTotal, 0, byte_1.length);
    System.arraycopy(byte_2, 0, byteTotal, byte_1.length, byte_2.length);
    System.arraycopy(byte_3, 0, byteTotal, byte_1.length + byte_2.length, byte_3.length);
    System.arraycopy(byte_4, 0, byteTotal, byte_1.length + byte_2.length + byte_3.length, byte_4.length);
    byte_1 = null;
    byte_2 = null;
    byte_3 = null;
    byte_4 = null;
    return byteTotal;
  }

  // java 合并5个byte数组
  public static byte[] byteMerger(byte[] byte_1, byte[] byte_2, byte[] byte_3, byte[] byte_4, byte[] byte_5) {
    byte[] byteTotal = new byte[byte_1.length + byte_2.length + byte_3.length + byte_4.length + byte_5.length];
    System.arraycopy(byte_1, 0, byteTotal, 0, byte_1.length);
    System.arraycopy(byte_2, 0, byteTotal, byte_1.length, byte_2.length);
    System.arraycopy(byte_3, 0, byteTotal, byte_1.length + byte_2.length, byte_3.length);
    System.arraycopy(byte_4, 0, byteTotal, byte_1.length + byte_2.length + byte_3.length, byte_4.length);
    System
        .arraycopy(byte_5, 0, byteTotal, byte_1.length + byte_2.length + byte_3.length + byte_4.length, byte_5.length);
    byte_1 = null;
    byte_2 = null;
    byte_3 = null;
    byte_4 = null;
    byte_5 = null;
    return byteTotal;
  }

  public String getImageid() {
    return this.imageid;
  }

  public void setImageid(String imageid) {
    this.imageid = imageid;
  }

  public long getFilesize() {
    return this.filesize;
  }

  public void setFilesize(long filesize) {
    this.filesize = filesize;
  }

  public long getOffset() {
    return this.offset;
  }

  public void setOffset(long offset) {
    this.offset = offset;
  }

  public byte[] getImagedata() {
    return this.imagedata;
  }

  public void setImagedata(byte[] imagedata) {
    this.imagedata = imagedata;
  }

  /**
   * byte[8]转long
   * 
   * @param b
   * @param offset
   *          b的偏移量
   * @return
   */
  public static long bytetoLong(byte[] b, int offset) {
    byte[] bt = new byte[8];
    for (int i = 0; i < 8; i++) {
      bt[i] = b[i + offset];
    }
    long bytes2long = bytes2long(bt);
    return bytes2long;

  }

  public static long bytes2long(byte[] b) {
    return ((((long) b[0] & 0xff) << 56) | (((long) b[1] & 0xff) << 48) | (((long) b[2] & 0xff) << 40)
        | (((long) b[3] & 0xff) << 32)

        | (((long) b[4] & 0xff) << 24) | (((long) b[5] & 0xff) << 16) | (((long) b[6] & 0xff) << 8) | (((long) b[7] & 0xff) << 0));
  }

  public static long bytestoLong(byte[] b, int off) {
    return ((((long) b[0 + off] & 0xff) << 56) | (((long) b[1 + off] & 0xff) << 48)
        | (((long) b[2 + off] & 0xff) << 40) | (((long) b[3 + off] & 0xff) << 32)

        | (((long) b[4 + off] & 0xff) << 24) | (((long) b[5 + off] & 0xff) << 16) | (((long) b[6 + off] & 0xff) << 8) | (((long) b[7 + off] & 0xff) << 0));
  }

  /**
   * long转byte[8]
   * 
   * @param a
   * @param b
   * @param offset
   *          b的偏移量
   * @return
   */
  public static byte[] longtoByte(long a) {
    byte[] byteTotal = new byte[8];
    byteTotal[0] = (byte) (a >> 56);
    byteTotal[1] = (byte) (a >> 48);
    byteTotal[2] = (byte) (a >> 40);
    byteTotal[3] = (byte) (a >> 32);

    byteTotal[4] = (byte) (a >> 24);
    byteTotal[5] = (byte) (a >> 16);
    byteTotal[6] = (byte) (a >> 8);
    byteTotal[7] = (byte) (a);

    return byteTotal;
  }

  // 获取文件的byte[]流
  public byte[] getByte(byte[] bt, int start) {

    if (bt.length == 48) {
      return null;
    }
    byte[] bjq = new byte[bt.length - start];
    for (int i = 0; i < bt.length - start; i++) {
      bjq[i] = bt[i + start];
    }
    return bjq;
  }


  public byte[] getmByte() {
    return this.mByte;
  }

  public void setmByte(byte[] mByte) {
    this.mByte = mByte;
  }

}
