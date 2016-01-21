/**********************************************************
  copyright   : Copyright (C) 2013-2014, chenbichao,
                All rights reserved.
  filename    : pgJniConnect.java
  discription : 
  modify      : create, chenbichao, 2014/4/4
 **********************************************************/

package com.peergine.connect.android;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;

import com.meibeike.communication.entity.FileUpLoadByte;
import com.meibeike.communication.entity.SendData;
import com.meibeike.communication.manager.CommunicationThreadManager;
import com.meibeike.communication.util.Util;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class pgJniConnect {

  // /----------------------------------------------------
  // 常量定义

  // /
  // *.so lib name.
  // 工程生成的动态库的名称，必须修改成与最终的动态库文件名一致。
  // 例如: 动态库的名称为“libDEMO.so”，则PG_MODE_LIB = "DEMO"。
  public static final String PG_MODE_LIB = "ConnectClient";

  // /
  // P2P运行模式：客户端或侦听端
  public static final int PG_MODE_CLIENT = 0; // 客户端
  public static final int PG_MODE_LISTEN = 1; // 侦听端（通常是设备端）

  // /
  // 错误码定义
  public static final int PG_ERROR_OK = 0; // 成功
  public static final int PG_ERROR_INIT = -1; // 没有调用Initialize()或者已经调用Cleanup()清理模块
  public static final int PG_ERROR_CLOSE = -2; // 会话已经关闭（会话已经不可恢复）
  public static final int PG_ERROR_BADPARAM = -3; // 传递的参数错误
  public static final int PG_ERROR_NOBUF = -4; // 会话发送缓冲区已满
  public static final int PG_ERROR_NODATA = -5; // 会话没有数据到达
  public static final int PG_ERROR_NOSPACE = -6; // 传递的接收缓冲区太小
  public static final int PG_ERROR_TIMEOUT = -7; // 操作超时
  public static final int PG_ERROR_BUSY = -8; // 系统正忙
  public static final int PG_ERROR_NOLOGIN = -9; // 还没有登录到P2P服务器
  public static final int PG_ERROR_MAXSESS = -10; // 会话数限制
  public static final int PG_ERROR_NOCONNECT = -11; // 会话还没有连接完成
  public static final int PG_ERROR_MAXINST = -12; // 实例数限制
  public static final int PG_ERROR_SYSTEM = -127; // 系统错误

  // /
  // 事件ID定义
  // 对应 OnEventListener 接口的 iEventNew 参数的值
  public static final int PG_EVENT_NULL = 0; // NULL
  public static final int PG_EVENT_CONNECT = 1; // 会话连接成功了，可以调用Write()发送数据
  public static final int PG_EVENT_CLOSE = 2; // 会话被对端关闭，需要调用Close()才能彻底释放会话资源
  public static final int PG_EVENT_WRITE = 3; // 会话的底层发送缓冲区的空闲空间增加了，可以调用Write()发送新数据
  public static final int PG_EVENT_READ = 4; // 会话的底层接收缓冲区有数据到达，可以调用Read()接收新数据
  public static final int PG_EVENT_OFFLINE = 5; // 会话的对端不在线了，调用Open()后，如果对端不在线，则上报此事件
  public static final int PG_EVENT_INFO = 6; // 会话的连接方式或NAT类型检测有变化了，可以调用Info()获取最新的连接信息
  public static final int PG_EVENT_SVR_LOGIN = 16; // 登录到P2P服务器成功（上线）
  public static final int PG_EVENT_SVR_LOGOUT = 17; // 从P2P服务器注销或掉线（下线）
  public static final int PG_EVENT_SVR_REPLY = 18; // P2P服务器应答事件，可以调用ServerReply()接收应答
  public static final int PG_EVENT_SVR_NOTIFY = 19; // P2P服务器推送事件，可以调用ServerNotify()接收推送
  // public static final int PG_EVENT_LAN_SCAN = 32; //
  // 扫描局域网的P2P节点返回事件。可以调用pgLanScanResult()去接收结果。

  // /
  // 数据发送/接收优先级
  public static final int PG_PRIORITY_0 = 0; // 优先级0,
  // 最高优先级。（这个优先级上不能发送太大流量的数据，因为可能会影响P2P模块本身的握手通信。）
  public static final int PG_PRIORITY_1 = 1; // 优先级1
  public static final int PG_PRIORITY_2 = 2; // 优先级2
  public static final int PG_PRIORITY_3 = 3; // 优先级3, 最低优先级

  // /
  // P2P连接类型定义
  public static final int PG_CNNT_Unknown = 0; // 未知，还没有检测到连接类型
  public static final int PG_CNNT_IPV4_Pub = 4; // 公网IPv4地址
  public static final int PG_CNNT_IPV4_NATConeFull = 5; // 完全锥形NAT
  public static final int PG_CNNT_IPV4_NATConeHost = 6; // 主机限制锥形NAT
  public static final int PG_CNNT_IPV4_NATConePort = 7; // 端口限制锥形NAT
  public static final int PG_CNNT_IPV4_NATSymmet = 8; // 对称NAT
  public static final int PG_CNNT_IPV4_Private = 12; // 私网直连
  public static final int PG_CNNT_IPV4_NATLoop = 13; // 私网NAT环回
  public static final int PG_CNNT_IPV4_TunnelTCP = 16; // TCPv4转发
  public static final int PG_CNNT_IPV4_TunnelHTTP = 17; // HTTPv4转发
  public static final int PG_CNNT_IPV6_Pub = 32; // 公网IPv6地址
  public static final int PG_CNNT_IPV6_TunnelTCP = 40; // TCPv6转发
  public static final int PG_CNNT_IPV6_TunnelHTTP = 41; // HTTPv6转发
  public static final int PG_CNNT_Offline = 0xffff; // 对端不在线

  // /---------------------------------------------------

  public static final int FILE_RECV_INFO = 1000;// 文件接收文件中的信息
  public static final int FILE_RECV_STATE = 1001;// 文件接收的状态
  public static final int FILE_SEND_FINISH = 1002;// 文件发送完成
  public static final int FILE_SEND_INFO = 1003;// 文件发送中的信息
  public static final int FILE_SEND_FAILED = 1004;// 文件发送失败

  // API接口函数的输出参数类型定义

  // Info()函数的输出参数
  public class OutInfo {
    public String sPeerID = "";
    public String sAddrPub = "";
    public String sAddrPriv = "";
    public int iCnntType = 0;

    public OutInfo() {
    }
  }

  // LanScanResult()函数的输出参数
  public class OutLanScanResult {
    public class Item {
      public String sID;
      public String sAddr;

      public Item() {
      }
    }

    public Item[] Result = null;

    public OutLanScanResult() {
    }
  }

  public int Write(int iSessID, byte[] byData, int iPrio) {
    if (m_iInstID != 0) {
      return jniWrite(m_iInstID, iSessID, byData, iPrio);
    }
    return PG_ERROR_INIT;
  }

  public int getReply(OutSvrReply outReply) {
    if (m_iInstID != 0) {
      return jniServerReply(m_iInstID, outReply);
    }

    return PG_ERROR_INIT;
  }

  public int getNotify(OutSvrNotify outNotify) {
    if (m_iInstID != 0) {
      return jniServerNotify(m_iInstID, outNotify);
    }

    return PG_ERROR_INIT;
  }

  public int read(OutRead read, int iSessID) {
    if (m_iInstID != 0) {
      return jniRead(m_iInstID, iSessID, m_iBufSize, read);
    }

    return PG_ERROR_INIT;
  }

  // Read()函数的输出参数
  public class OutRead {
    public byte[] byBuf = null;
    public int iPrio = 0;

    public OutRead() {
    }
  }

  // ServerReply()函数的输出参数
  public class OutSvrReply {
    public String sData = "";
    public int iParam = 0;

    public OutSvrReply() {
    }
  }

  // ServerNotify()函数的输出参数
  public class OutSvrNotify {
    public String sData = "";

    public OutSvrNotify() {
    }
  }

  private class OutMsgLanScanResult {
    public int iErr = 0;
    public OutLanScanResult out;

    public OutMsgLanScanResult() {
      out = new OutLanScanResult();
    }
  }

  // /-----------------------------------------------------
  // API接口函数（必须在主线程中调用）
  // 函数与pgLibConnect.h中的C接口函数一一对应，请参考C接口的使用说明。
  public static int m_iBufSize = 32 * 1024;

  // public static int m_iBufSize = 256 * 1024;

  public int Initialize(int iMode, String sUser, String sPass, String sSvrAddr, String sSvrAddrBack, String sRelayList, int iBufSize0, int iBufSize1, int iBufSize2, int iBufSize3,
      int iSessTimeout, int iTryP2PTimeout, int iForwardSpeed) {
    try {
      // Init P2P instance.
      int iInstID = jniInitialize(iMode, sUser, sPass, sSvrAddr, sSvrAddrBack, sRelayList, m_iBufSize, m_iBufSize, m_iBufSize, m_iBufSize, iSessTimeout, iTryP2PTimeout,
          iForwardSpeed);
      if (iInstID < 0) {
        // Error code.
        return iInstID;
      }
      // Start p2p event receive thread.
      m_bEventRun = true;
      m_Thread = new ThreadEvent();
      m_Thread.start();

      m_iInstID = iInstID;
      return iInstID;
    } catch (Exception ex) {
      Log.d("pgJniConnect", "Initialize, ex=" + ex.toString());
      return PG_ERROR_SYSTEM;
    }
  }

  public void Cleanup() {
    try {
      m_bEventRun = false;
      if (m_Thread != null) {
        m_Thread.join();
        m_Thread = null;
      }

      if (m_iInstID != 0) {
        jniCleanup(m_iInstID);
        m_iInstID = 0;
      }
    } catch (Exception ex) {
      Log.d("pgJniConnect", "Cleanup, ex=" + ex.toString());
    }
  }

  public int Open(String sListenID) {
    if (m_iInstID != 0) {
      m_iSessID = jniOpen(m_iInstID, sListenID);
      return PG_ERROR_OK;
    }
    return PG_ERROR_INIT;
  }

  public void Close() {
    if (m_iInstID != 0) {
      jniClose(m_iInstID, m_iSessID);
    }
  }

  // public void SendFile(String sFilePath) {
  // m_fileInfo = new FileInfo(sFilePath);
  // // m_bSendFile = true;
  // }

  public int LanScanStart() {
    if (m_iInstID != 0) {
      return jniLanScanStart(m_iInstID);
    }
    return PG_ERROR_INIT;
  }

  public int LanScanResult(int iTimeout, OutLanScanResult out) {
    if (m_iInstID != 0) {
      int j = 0;
      int c = 0;
      while (j <= 0 && c < 300) {
        j = jniLanScanResult(m_iInstID, out);
        c++;
        try {
          Thread.sleep(10);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
      return j;
    }
    return PG_ERROR_INIT;
  }

  // /-----------------------------------------------------
  // 事件侦听接口定义
  public interface OnEventListener {
    void event(int iEventNew, int iSessIDNow, int iPrio, Message msg);
  }

  // 调用此 SetEventListener() 函数设置事件侦听接口的实例对象。
  private OnEventListener m_eventListener = null;

  public void SetEventListener(OnEventListener eventListener) {
    m_eventListener = eventListener;
  }

  // /-----------------------------------------------------
  // Constructor.
  public pgJniConnect() {
    m_iInstID = 0;
    m_Thread = null;
  }

  // /-----------------------------------------------------
  // The event poll thread.
  private boolean m_bEventRun = false;

  class ThreadEvent extends Thread {
    public void run() {
      OutEvent out = new OutEvent();
      while (m_bEventRun) {
        int iErr = jniEvent(m_iInstID, 1000, out);
        if (iErr != PG_ERROR_OK) {
          if (iErr != PG_ERROR_TIMEOUT) {
            Log.d("pgJniConnect", "jniEvent: iErr=" + iErr);
          }
          continue;
        }

        if (m_eventListener != null) {
          Log.i("ThreadEvent", "EventProc: iEvent=" + out.iEventNow + ", iSessID=" + out.iSessNow + ", iPrio=" + out.iPrio);
          Message msg = Message.obtain(m_eventHandler, out.iEventNow, out.iSessNow, out.iPrio, this);
          m_eventListener.event(out.iEventNow, out.iSessNow, out.iPrio, msg);// 回调接口
        } else {
          Log.e("ThreadEvent", "EventProc is m_eventListener == null");
        }
        // EventProc(out);
      }
    }
  }

  // 读取文件用的类
  public class FileInfo {
    public long iTotalSize; // byte
    public long iSendSize;
    public long iMilliSec;
    public String sFilePath;
    public RandomAccessFile fileStream;
    public ByteArrayOutputStream baos;
    public int iErrorNumber;
    public byte[] buffer;

    public FileInfo(String sPath) {
      sFilePath = sPath;
      iTotalSize = 0;
      iSendSize = 0;
      iMilliSec = 0;
      fileStream = null;
      baos = null;
      iErrorNumber = 0;
      buffer = null;
    }
  }

  private ThreadEvent eventThread = null;
  public static boolean isSendFile = false;// 发送文件是否发送完成，true正在发送，false闲置当中，可以发送文件数据。

  /**
   * 文件发送
   * 
   * @param manager
   * 
   * @param iSessID
   *          客户端ID
   * @param iPrio
   *          优先级
   * @param m_fileInfo
   *          文件发送的信息存储类
   */
  public void sendFile(CommunicationThreadManager manager, int iSessID, int iPrio, SendData mSendData) {
    isSendFile = true;
    long file_length = 0;
    // Util.LogUntils("tag", "sendFile is start path " + mSendData.path);
    FileInfo m_fileInfo = new FileInfo(mSendData.path);
    int iErr_code = 0;
    try {
      int iErr = PG_ERROR_OK;
      if (m_fileInfo.fileStream == null) {// 第一次发送数据,
        Util.LogUntils("tag", "sendFile is first data" + ",类型：" + manager.key);
        m_fileInfo.fileStream = new RandomAccessFile(new File(m_fileInfo.sFilePath), "rw");
        m_fileInfo.fileStream.seek(mSendData.offset); // 定位指针到fileSize位置

        m_fileInfo.iTotalSize = m_fileInfo.fileStream.getChannel().size();
        m_fileInfo.iMilliSec = new Date().getTime();
        m_fileInfo.baos = new ByteArrayOutputStream();

        // 设置包头数据
        int inclusionLenth = (int) (m_fileInfo.iTotalSize + 48);// 包体的长度
        // Util.LogUntils("tag", "sendFile first is m_fileInfo.iTotalSize = " +
        // m_fileInfo.iTotalSize);
        mSendData.header.setInclusionLenth(inclusionLenth);
        mSendData.fileSize = m_fileInfo.iTotalSize;

        byte[] data = mSendData.header.getPackageHeader();
        // Util.LogUntils("tag", "sendFile first is data.length = " +
        // data.length);
        // 发送包头
        iErr = jniWrite(m_iInstID, iSessID, data, iPrio);
        Log.i("p2p", "iErr1  " + iErr);
        file_length += data.length;
        Util.LogUntils("tag", "send client packageHeader is iErr = " + iErr + ",类型：" + manager.key);
        if (iErr < PG_ERROR_OK) {
          isSendFile = false;
          Util.LogUntils("tag", "write file packageHeader failed!");
          manager.mCommunicationCallBack.connectFailure(null);
        }

        // 发送四个long数据
        FileUpLoadByte fileByte = new FileUpLoadByte(mSendData.fileID, mSendData.fileSize, mSendData.offset, null);
        iErr = jniWrite(m_iInstID, iSessID, fileByte.getByte(), iPrio);
        Log.i("p2p", "iErr2  " + iErr);
        file_length += fileByte.getByte().length;
        Util.LogUntils("tag", "send client three long is iErr = " + iErr + ",类型：" + manager.key);
        if (iErr < PG_ERROR_OK) {
          isSendFile = false;
          Util.LogUntils("tag", "write file three long data failed!");
          manager.mCommunicationCallBack.connectFailure(null);
        }
      }

      if (isSendFile && m_fileInfo.fileStream != null && m_fileInfo.baos != null) {// 读取路径文件数据
        byte[] tempBuf = new byte[m_iBufSize];
        int iSize = -1;
        while (isSendFile && (-1 != (iSize = m_fileInfo.fileStream.read(tempBuf)))) {// 发送文件
          m_fileInfo.baos.write(tempBuf, 0, iSize);
          iErr = jniWrite(m_iInstID, iSessID, m_fileInfo.baos.toByteArray(), iPrio);

          file_length += m_fileInfo.baos.toByteArray().length;
          Log.i("tag", "send---- client file length iSessID = " + iSessID + "; iErr = " + iErr + ";  iSize = " + iSize + ",类型：" + manager.key);
          // Util.LogUntils("tag", "send client file length iErr = " + iErr +
          // ";  iSize = " + iSize);
          if (iErr >= PG_ERROR_OK) {
            m_fileInfo.iSendSize += iSize;
            m_fileInfo.baos.reset();
            recordFileSend(m_fileInfo, iSize, iSessID, iPrio);
            // Util.LogUntils("tag", "send client file m_fileInfo.iTotalSize = "
            // + m_fileInfo.iTotalSize + "; m_fileInfo.iSendSize = " +
            // m_fileInfo.iSendSize);
            if (m_fileInfo.iTotalSize == m_fileInfo.iSendSize) {// 发送完成
              isSendFile = false;
              Util.LogUntils("tag", "send client file is finish");

              m_eventListener.event(FILE_SEND_FINISH, iSessID, iPrio, null);
              break;
            }
          } else if (iErr == PG_ERROR_NOBUF) {
            while (iErr == PG_ERROR_NOBUF) {
              try {
                Thread.sleep(1000);
                Log.e("tag_path", "send client file   " + mSendData.path);
              } catch (Exception e) {
                e.printStackTrace();
              }
              iErr = jniWrite(m_iInstID, iSessID, m_fileInfo.baos.toByteArray(), iPrio);

              file_length += m_fileInfo.baos.toByteArray().length;
//              Log.i("tag", "send client file length iErr = " + iErr + ";  iSize = " + iSize);
              Util.LogUntils("tag", "send client file length iErr = " + iErr + ";  iSize = " + iSize + ",类型：" + manager.key);
              if (iErr >= PG_ERROR_OK) {
                m_fileInfo.iSendSize += iSize;
                m_fileInfo.baos.reset();
                recordFileSend(m_fileInfo, iSize, iSessID, iPrio);
                Util.LogUntils("tag", "send client file m_fileInfo.iTotalSize = " + m_fileInfo.iTotalSize + "; m_fileInfo.iSendSize = " + m_fileInfo.iSendSize);
                if (m_fileInfo.iTotalSize == m_fileInfo.iSendSize) {// 发送完成
                  isSendFile = false;
                  Util.LogUntils("tag", "send client file is finish" + ",类型：" + manager.key);

                  m_eventListener.event(FILE_SEND_FINISH, iSessID, iPrio, null);
                  break;
                }
              }
            }
            if (iErr < PG_ERROR_OK) {// 发送失败
              if (iErr == PG_ERROR_CLOSE) {
                Cleanup();
              }
              Log.i("p2p", "iErr3  " + iErr);
              isSendFile = false;
              manager.mCommunicationCallBack.connectFailure(null);
              Log.e("tag", "send client file is error iErr = " + iErr + ",类型：" + manager.key);
            }
          } else {// 发送失败
            Log.i("p2p", "iErr4  " + iErr);
            isSendFile = false;
            manager.mCommunicationCallBack.connectFailure(null);
            Log.e("tag", "send client file is error iErr = " + iErr + ",类型：" + manager.key);
          }
          try {
            Thread.sleep(50);
            Log.e("tag_path", "send client file   " + mSendData.path);
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      }
      iErr_code = iErr;
    } catch (Exception e) {// 文件发送失败
      m_eventListener.event(FILE_SEND_FAILED, iSessID, iPrio, null);
      Log.e("tag", "sendFile is Exception error : " + e);// 文件不存在或无法访问！
      e.printStackTrace();
    } finally {
      Log.i("p2p", "iErr_code  " + iErr_code);
      Log.i("tag", "file_length = " + file_length);
      Util.LogUntils("tag", "sendFile is finally isSendFile = " + isSendFile);
      isSendFile = false;
      if (m_fileInfo != null) {
        try {
          if (m_fileInfo.fileStream != null) {
            m_fileInfo.fileStream.close();
            m_fileInfo.fileStream = null;
          }

          m_fileInfo.iTotalSize = 0;
          m_fileInfo.iMilliSec = 0;

          if (m_fileInfo.baos != null) {
            m_fileInfo.baos.reset();
            m_fileInfo.baos.close();
            m_fileInfo.baos = null;
          }
          m_fileInfo = null;
        } catch (IOException e) {
          Log.e("tag", "sendFile is finally IOException error : ", e);
        }
      }
    }
  }

  /**
   * 记录文件正在发送中，清除数据
   * 
   * @param m_fileInfo
   * @param iSize
   * @param iSessID
   * @param iPrio
   */
  private void recordFileSend(FileInfo m_fileInfo, int iSize, int iSessID, int iPrio) {
    // 发送进度信息
    long iNow = new Date().getTime();
    OutEvent out = new OutEvent();
    Util.LogUntils("tag", "send:" + m_fileInfo.iSendSize + " time:" + (iNow - m_fileInfo.iMilliSec));
    long iRate = Math.round((m_fileInfo.iSendSize / (iNow - m_fileInfo.iMilliSec)) * 1000 / 1024);
    String sInfo = "进度:" + m_fileInfo.iSendSize + "/" + m_fileInfo.iTotalSize;
    sInfo += "     速度:" + iRate + "KBytes/s";

    Message msg = Message.obtain(m_eventHandler, out.iEventNow, out.iSessNow, out.iPrio, sInfo);
    m_eventListener.event(FILE_SEND_INFO, iSessID, iPrio, msg);
  }

  private void EventProc(OutEvent out) {
    Log.d("FileTranClient", "EventProc: iEvent=" + out.iEventNow + ", iSessID=" + out.iSessNow + ", iPrio=" + out.iPrio);

    switch (out.iEventNow) {
      case PG_EVENT_CONNECT:
      case PG_EVENT_CLOSE:
      case PG_EVENT_OFFLINE:
      case PG_EVENT_INFO:
      case PG_EVENT_SVR_LOGIN:
      case PG_EVENT_SVR_LOGOUT:
      case PG_EVENT_WRITE:
        break;

      case PG_EVENT_READ: {
        OutMsgRead outMsg = new OutMsgRead();
        outMsg.iErr = jniRead(m_iInstID, out.iSessNow, 32 * 1024, outMsg.out);
      }
        break;

      case PG_EVENT_SVR_REPLY: {
        OutMsgSvrReply outMsg = new OutMsgSvrReply();
        outMsg.iErr = jniServerReply(m_iInstID, outMsg.out);
      }
        break;

      // case PG_EVENT_LAN_SCAN:
      // {
      // OutMsgLanScanResult outMsg = new OutMsgLanScanResult();
      // outMsg.iErr = jniLanScanResult(m_iInstID, outMsg.out);
      // m_listOutMsgLanScanResult.add(outMsg);
      // WaitEnd();
      // }
      // break;

      case PG_EVENT_SVR_NOTIFY: {
        OutMsgSvrNotify outMsg = new OutMsgSvrNotify();
        outMsg.iErr = jniServerNotify(m_iInstID, outMsg.out);
      }
        break;
    }

    // Post the event to UI thread.
    Message msg = Message.obtain(m_eventHandler, out.iEventNow, out.iSessNow, out.iPrio, this);
    m_eventHandler.sendMessage(msg);

    // // sendfile
    // if (m_bSendFile && out.iEventNow == PG_EVENT_WRITE) {
    // TransFile(out.iSessNow);
    // }
  }

  private void TransFile(int iSessID) {
    try {
      int iErr = PG_ERROR_OK;
      if (m_fileInfo.fileStream == null) {
        m_fileInfo.fileStream = new RandomAccessFile(new File(m_fileInfo.sFilePath), "rw");
        m_fileInfo.fileStream.seek(0);
        m_fileInfo.iTotalSize = m_fileInfo.fileStream.getChannel().size();
        m_fileInfo.iMilliSec = new Date().getTime();

        // 发送文件信息
        int iPos = m_fileInfo.sFilePath.lastIndexOf("/");
        String sFileName = m_fileInfo.sFilePath;
        if (iPos < (m_fileInfo.sFilePath.length() - 1)) {
          sFileName = m_fileInfo.sFilePath.substring(iPos + 1);
        }

        String sFileInfo = sFileName + ";" + m_fileInfo.iTotalSize;
        iErr = jniWrite(m_iInstID, iSessID, sFileInfo.getBytes("UTF-8"), 0);
        if (iErr < 0) {
          m_fileInfo.fileStream.close();
          m_fileInfo.fileStream = null;
          m_fileInfo.iTotalSize = 0;
          m_fileInfo.iMilliSec = 0;
          Log.d("FileTranClient", "write file information failed!");
          return;
        }
      }

      if (m_fileInfo.buffer == null && m_fileInfo.fileStream != null) {
        byte[] tempBuf = new byte[m_iBufSize * 1024];
        int iSize = m_fileInfo.fileStream.read(tempBuf);
        if (iSize > 0) {
          m_fileInfo.buffer = new byte[iSize];
          System.arraycopy(tempBuf, 0, m_fileInfo.buffer, 0, iSize);
        } else if (iSize == -1) {
          m_fileInfo.fileStream.close();
          m_fileInfo.fileStream = null;
          m_fileInfo.buffer = null;
        }
      }

      if (m_fileInfo.buffer != null) {
        iErr = jniWrite(m_iInstID, iSessID, m_fileInfo.buffer, 3);
        if (iErr >= 0) {
          m_fileInfo.iSendSize += m_fileInfo.buffer.length;
          m_fileInfo.buffer = null;

        } else {
          Log.d("FileTranClient", "write failed, iErr=" + iErr);
          return;
        }
      }

      // send message
      if (m_fileInfo.fileStream == null) {
        // m_bSendFile = false;

        // 发送完成
        Message msg = Message.obtain(m_eventHandler, FILE_SEND_FINISH, 0, 0, "");
        m_eventHandler.sendMessage(msg);
      } else {
        // 发送进度信息
        long iNow = new Date().getTime();
        Log.d("FileTranClient", "send:" + m_fileInfo.iSendSize + " time:" + (iNow - m_fileInfo.iMilliSec));
        long iRate = Math.round((m_fileInfo.iSendSize / (iNow - m_fileInfo.iMilliSec)) * 1000 / 1024);
        String sInfo = "进度:" + m_fileInfo.iSendSize + "/" + m_fileInfo.iTotalSize;
        sInfo += "     速度:" + iRate + "KBytes/s";
        Message msg = Message.obtain(m_eventHandler, FILE_SEND_INFO, 0, 0, sInfo);
        m_eventHandler.sendMessage(msg);
      }
    } catch (Exception e) {
      if (m_fileInfo.fileStream != null) {
        try {
          m_fileInfo.fileStream.close();
          m_fileInfo.fileStream = null;
        } catch (Exception e1) {
          e1.printStackTrace();
        }
      }

      Message msg = Message.obtain(m_eventHandler, FILE_SEND_INFO, 0, 0, "文件不存在或无法访问！");
      m_eventHandler.sendMessage(msg);

      e.printStackTrace();
    }
  }

  // /
  // Output message classes.
  private class OutMsgRead {
    public int iErr = 0;
    public OutRead out;

    public OutMsgRead() {
      out = new OutRead();
    }
  }

  private class OutMsgSvrReply {
    public int iErr = 0;
    public OutSvrReply out;

    public OutMsgSvrReply() {
      out = new OutSvrReply();
    }
  }

  private class OutMsgSvrNotify {
    public int iErr = 0;
    public OutSvrNotify out;

    public OutMsgSvrNotify() {
      out = new OutSvrNotify();
    }
  }

  // /
  // Output event class.
  private class OutEvent {
    public int iEventNow = 0;
    public int iSessNow = 0;
    public int iPrio = 0;

    public OutEvent() {
    }
  }

  // /-----------------------------------------------------

  // /-----------------------------------------------------
  // this P2P instance.
  private int m_iInstID = 0;
  private int m_iSessID = 0;
  private ThreadEvent m_Thread = null;
  private Handler m_eventHandler = null;
  // private boolean m_bSendFile = false;
  private FileInfo m_fileInfo;

  // /-----------------------------------------------------
  // JNI API. Call to C/C++ API in JAVA.

  public native static int jniInitialize(int iMode, String sUser, String sPass, String sSvrAddr, String sSvrAddrBack, String sRelayList, int iBufSize0, int iBufSize1,
      int iBufSize2, int iBufSize3, int iSessTimeout, int iTryP2PTimeout, int iForwardSpeed);

  public native static void jniCleanup(int iInstID);

  public native static String jniSelf(int iInstID);

  public native static int jniEvent(int iInstID, int iTimeout, OutEvent out);

  public native static int jniOpen(int iInstID, String sListenID);

  public native static void jniClose(int iInstID, int iSessID);

  public native static int jniInfo(int iInstID, int iSessID, OutInfo out);

  public native static int jniWrite(int iInstID, int iSessID, byte[] byData, int iPrio);

  public native static int jniRead(int iInstID, int iSessID, int iSize, OutRead out);

  public native static int jniServerRequest(int iInstID, String sData, int iParam);

  public native static int jniServerReply(int iInstID, OutSvrReply out);

  public native static int jniServerNotify(int iInstID, OutSvrNotify out);

  public native static int jniLanScanStart(int iInstID);

  public native static int jniLanScanResult(int iInstID, OutLanScanResult out);

  public native static String jniVersion();

  static {
    try {
      System.loadLibrary(PG_MODE_LIB);
    } catch (Exception ex) {
      System.out.println("Load " + PG_MODE_LIB + ": " + ex.toString());
    }
  }
}