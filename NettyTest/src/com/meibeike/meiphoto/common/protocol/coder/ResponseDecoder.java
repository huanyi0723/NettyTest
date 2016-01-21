package com.meibeike.meiphoto.common.protocol.coder;

import android.util.Log;

public class ResponseDecoder {
  private byte[] data;
  private int index;
  private int len;
  private int dataLen;

  public ResponseDecoder(byte[] rev) {
    data = rev;
    index = 0;
    len = 0;
    dataLen = rev.length;
    Log.i("NetJson", String.format("响应:%s", rev.toString()));
  }

  /**
   * 对应协议中类型string
   * 
   * @return
   */
  public String getString() {
    if (index >= dataLen) {
      return "";
    }
    // len = CoderUtils.bytes2StringZlen(data, index);
    // String value = CoderUtils.bytes2StringZ(data, index, len);
    String value = null;
    // index += len;
      value = new String(data);
    return value;
  }

  public String getString(int length) {
    if (index >= dataLen) {
      return "";
    }
    String value = CoderUtils.bytes2StringZ(data, index, length);
    index += length;
    return value;
  }

  /**
   * 对应协议中类型wstring
   * 
   * @return
   */
  public String getUnicodeString() {
    if (index >= (dataLen - 2)) {
      return "";
    }
    len = CoderUtils.bytes2Stringlen(data, index);
    String value = CoderUtils.bytes2String(data, index, len);
    index += len + 1;
    return value;

  }

  public String getUnicodeString(int length) {
    if (index >= dataLen) {
      return "";
    }
    String value = CoderUtils.bytes2String(data, index, length);
    index += length;
    return value;
  }

  public long getLong() {
    if (index >= dataLen) {
      return 0L;
    }
    long value = CoderUtils.bytes2Long(data, index);
    index += 8;
    return value;
  }

  public int getInt() {
    if (index > (dataLen - 4)) {
      return 0;
    }
    int value = CoderUtils.bytes2Integer(data, index);
    index += 4;
    return value;
  }

  public short getShort() {
    if (index >= dataLen) {
      return 0;
    }
    short value = CoderUtils.bytes2Short(data, index);
    index += 2;
    return value;
  }

  public byte getByte() {
    if (index >= dataLen) {
      return 0;
    }
    byte value = data[index];
    index += 1;
    return value;
  }

  public void skip(int length) {
    index += length;
  }

  public void skipString() {
    len = CoderUtils.bytes2StringZlen(data, index);
    index += len;
  }

  public void skipUnicodeString() {
    len = CoderUtils.bytes2Stringlen(data, index);
    index += len + 1;
  }

  // public int getSize() {
  // if (data != null) {
  // return data.length;
  // }
  // return 0;
  // }
  //
  // public int getIndex() {
  // return index;
  // }
  //
  // public byte[] getData() {
  // return data;
  // }
  //
  // public String getString(int length, int index_) {
  // String value = CoderUtils.bytes2StringZ(data, index_, length);
  // index += length;
  // return value;
  // }

}
