package com.meibeike.meiphoto.common.clientconnect;

public class ClientConstants {
  // 网络状态
  public static final int NET_NO = 0;
  public static final int NET_2G = 1;
  public static final int NET_3G = 2;
  public static final int NET_4G = 3;
  public static final int NET_WIFI = 4;
  public static final int NET_UNKNOWN = 5;
  // 当前的连接类型
  public static final String LINK_NO = "no"; //
  public static final String LINK_NETTY = "netty"; //
  public static final String LINK_P2P = "p2p"; //

  // 连接使用常量
  /** 发起请求 */
  public static final int REQUEST = 0x001;
  /** 发送消息 */
  public static final int REQUEST_SEND_MESSAGE = 0x002;
  /** 建立连接 */
  public static final int REQUEST_CREATE_CONNECT = 0x003;
  /** 请求超时 */
  public static final int REQUEST_TIMEOUT = 0x004;
  /** 连接超时 */
  public static final int REQUEST_CONNECT_TIMEOUT = 0x005;
  /** 发送心跳 */
  public static final int REQUEST_SEND_HEARTBEAT = 0x006;

  /** 请求超时时间 */
  public static final int TIMEOUT = 0;
  /** 连接超时时间 */
  public static final int LINK_TIMEOUT = 0;
  /** 心跳间隔时间 */
  public static final int HEARTBEAT_TIME = 0;

}
