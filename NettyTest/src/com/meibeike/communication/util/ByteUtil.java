package com.meibeike.communication.util;

import android.annotation.SuppressLint;

public class ByteUtil {
  // 转换成byte[]

  // byte[]数组转换成Int
  public static int byteArrayToInt(byte[] b, int offset) {
    int value = 0;
    for (int i = 0; i < 4; i++) {
      int shift = (4 - 1 - i) * 8;
      value += (b[i + offset] & 0x000000FF) << shift;
    }
    return value;
  }

  // byte转换成short
  public static short byteToShort(byte[] b, int start) {
    return (short) ((((int) b[start]) << 8) + b[start + 1]);
  }

  // int转换成byte
  public static byte[] intToByte(int value) {
    byte[] result = new byte[4];

    result[0] = (byte) (value >> 24);// 取最高8位放到0下标
    result[1] = (byte) (value >> 16);// 取次高8为放到1下标
    result[2] = (byte) (value >> 8); // 取次低8位放到2下标
    result[3] = (byte) (value); // 取最低8位放到3下标
    return result;
  }

  // short转换成byte
  public static byte[] shorToByte(short value) {
    byte[] result = new byte[2];

    result[0] = (byte) (value >> 8); // 取次低8位放到0下标
    result[1] = (byte) (value); // 取最低8位放到1下标
    return result;
  }

  // byte转换成byteArray
  @SuppressLint("UseValueOf")
  public static byte[] byteToByteArray(byte value) {
    byte[] result = new byte[1];
    result[0] = new Byte((byte) value);

    return result;
  }

  public static long getResult(byte[] data, int count) {
    byte[] b = new byte[8];
    System.arraycopy(data, count, b, 0, b.length);
    return getLong(b);
  }

  public static long getLong(byte[] bb) {
    return ((((long) bb[0] & 0xff) << 56) | (((long) bb[1] & 0xff) << 48) | (((long) bb[2] & 0xff) << 40)
        | (((long) bb[3] & 0xff) << 32) | (((long) bb[4] & 0xff) << 24) | (((long) bb[5] & 0xff) << 16)
        | (((long) bb[6] & 0xff) << 8) | (((long) bb[7] & 0xff) << 0));
  }

  // java 合并3个byte数组
  public static byte[] byteCase(byte[] byte_1, byte[] byte_2, byte[] byte_3) {
    byte[] byteTotal = new byte[byte_1.length + byte_2.length + byte_3.length];

    System.arraycopy(byte_1, 0, byteTotal, 0, byte_1.length);
    System.arraycopy(byte_2, 0, byteTotal, byte_1.length, byte_2.length);
    System.arraycopy(byte_3, 0, byteTotal, byte_1.length + byte_2.length, byte_3.length);

    return byteTotal;
  }

  // java 合并4个byte数组
  public static byte[] byteMerger(byte[] byte_1, byte[] byte_2, byte[] byte_3, byte[] byte_4) {
    byte[] byteTotal = new byte[byte_1.length + byte_2.length + byte_3.length + byte_4.length];

    System.arraycopy(byte_1, 0, byteTotal, 0, byte_1.length);
    System.arraycopy(byte_2, 0, byteTotal, byte_1.length, byte_2.length);
    System.arraycopy(byte_3, 0, byteTotal, byte_1.length + byte_2.length, byte_3.length);
    System.arraycopy(byte_4, 0, byteTotal, byte_1.length + byte_2.length + byte_3.length, byte_4.length);

    return byteTotal;
  }
  
  // java 合并两个byte数组
  public static byte[] byteMerger(byte[] byte_1, byte[] byte_2) {
    byte[] byteTotal = new byte[byte_1.length + byte_2.length];
    System.arraycopy(byte_1, 0, byteTotal, 0, byte_1.length);
    System.arraycopy(byte_2, 0, byteTotal, byte_1.length, byte_2.length);
    return byteTotal;
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
    byte[] b = new byte[8];
    b[0] = (byte) (a >> 56);
    b[1] = (byte) (a >> 48);
    b[2] = (byte) (a >> 40);
    b[3] = (byte) (a >> 32);

    b[4] = (byte) (a >> 24);
    b[5] = (byte) (a >> 16);
    b[6] = (byte) (a >> 8);
    b[7] = (byte) (a);

    return b;
  }

  public static long bytestoLong(byte[] b, int off) {

    return ((((long) b[0 + off] & 0xff) << 56) | (((long) b[1 + off] & 0xff) << 48)
        | (((long) b[2 + off] & 0xff) << 40) | (((long) b[3 + off] & 0xff) << 32)

        | (((long) b[4 + off] & 0xff) << 24) | (((long) b[5 + off] & 0xff) << 16) | (((long) b[6 + off] & 0xff) << 8) | (((long) b[7 + off] & 0xff) << 0));
  }

}
