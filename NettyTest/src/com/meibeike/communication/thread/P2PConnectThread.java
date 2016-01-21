package com.meibeike.communication.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.os.Message;
import android.util.Log;

import com.meibeike.communication.mbk_interface.CommunicationCallBack;
import com.peergine.connect.android.pgJniConnect;

/**
 * P2P用户通讯线程 com.meibeike.communication.p2p.P2PConnect
 * 
 * @author 张涢 <br/>
 *         create at 2015-9-23 上午11:24:52
 */
public class P2PConnectThread extends Thread {
  private static final String TAG = "P2PConnect";
  private Context mContext;
  private String sn;// 需要连接的服务端SN号
  public pgJniConnect m_Cnnt = null;
  public int m_iSessID = 0;// 服务端ID号
  private int m_iConnState = 0;// 连接状态
  private CommunicationCallBack mCommunicationCallBack;// 回调接口
  private int outTime = 180;// 超时时间
  private ExecutorService executor;

  /**
   * 初始化
   * 
   * @param mContext
   * @param sn
   * @param isClearUp
   *          是否clearUp
   * @param outTime
   * @param mCommunicationCallBack
   */
  public P2PConnectThread(Context mContext, String sn, boolean isClearUp, int outTime, CommunicationCallBack mCommunicationCallBack) {
    this.mContext = mContext;
    this.sn = sn;
    this.outTime = outTime;
    this.mCommunicationCallBack = mCommunicationCallBack;
    m_Cnnt = new pgJniConnect();
    executor = Executors.newFixedThreadPool(5);
    // 第一次使用 需要先clearup
    if (isClearUp) {
      cleanup();
    }
    init();
  }

  /**
   * P2P初始化
   */
  private void init() {
    m_Cnnt.SetEventListener(m_eventListener);
    int m_iSess_ID = m_Cnnt.Initialize(pgJniConnect.PG_MODE_CLIENT, "FileTranClient", "XXXX", "120.24.245.123:7781", "", "", 0, 0, 0, 0, 0, 0, 0);
    // int m_iSess_ID = m_Cnnt.Initialize(pgJniConnect.PG_MODE_CLIENT,
    // "FileTranClient", "XXXX", "120.24.245.123:7781", "", "", 0, 0, 0, 0,
    // outTime, 40, 0);
    Log.i("p2p", "init() - " + m_iSess_ID);
    if (m_iSess_ID > 0) {
      isConnect();
    } else {
      if(m_iSess_ID == pgJniConnect.PG_ERROR_SYSTEM){
        cleanup();
      }
      mCommunicationCallBack.connectFailure(null);
      
    }

  }

  @Override
  public void run() {

  }

  /**
   * p2p与云棒建立连接
   */
  public void isConnect() {
    if (m_iConnState != 0) {
      // 已连接或正在连接中！
      return;
    }

    executor.execute(new Thread(new Runnable() {
      @Override
      public void run() {
        int iErr = m_Cnnt.Open(sn);
        Log.i("p2p", "Open()" + iErr);
        if (iErr > pgJniConnect.PG_ERROR_OK) {
          // "连接错误，iErr=" + iErr;
          // LogUtils.getLogger().i("isConnect()=====连接错误，iErr=" + iErr);
        } else {
          // "连接中...";
          // LogUtils.getLogger().i("isConnect()===========连接中...");
          m_iConnState = 1;
        }
      }
    }));

    // int iErr = m_Cnnt.Open(sn);
    // if (iErr >= pgJniConnect.PG_ERROR_OK) {
    // m_iConnState = 2;
    // mCommunicationCallBack.connected(null);
    // // "连接错误，iErr=" + iErr;
    // } else {
    // // "连接中...";
    // mCommunicationCallBack.connectFailure(null);
    // m_iConnState = 1;
    // }
  }

  private pgJniConnect.OnEventListener m_eventListener = new pgJniConnect.OnEventListener() {
    @Override
    public void event(int iEventNow, int iSessIDNow, int iPrio, Message msg) {
      Log.i("p2p", "iEventNow - " + iEventNow);
      switch (iEventNow) {
        case pgJniConnect.PG_EVENT_CONNECT:// 会话连接成功了，可以调用Write()发送数据
          m_iSessID = iSessIDNow;
          m_iConnState = 2;
          mCommunicationCallBack.connected(null);
          break;
        case pgJniConnect.PG_EVENT_CLOSE:// 会话被对端关闭，需要调用Close()才能彻底释放会话资源
          mCommunicationCallBack.connectFailure(null);
          break;
        case pgJniConnect.PG_EVENT_WRITE:// 会话的底层发送缓冲区的空闲空间增加了，可以调用Write()发送新数据
          break;
        case pgJniConnect.PG_EVENT_READ:// 会话的底层接收缓冲区有数据到达，可以调用Read()接收新数据
          mCommunicationCallBack.channelRead(null, readData());
          break;
        case pgJniConnect.PG_EVENT_OFFLINE:// 连接失败

          mCommunicationCallBack.connectFailure(null);
          break;
        // case pgJniConnect.PG_EVENT_LAN_SCAN://
        // 会话的对端不在线了，调用Open()后，如果对端不在线，则上报此事件
        // LogUtils.getLogger().i("===========会话的对端不在线了，调用Open()后，如果对端不在线，则上报此事件");
        // break;
        case pgJniConnect.FILE_SEND_FINISH:// 发送完成！

          break;
        case pgJniConnect.FILE_SEND_INFO:
          break;
      }
    }
  };

  /**
   * 获取返回的数据
   * 
   * @return
   */
  public byte[] readData() {
    pgJniConnect.OutRead out = m_Cnnt.new OutRead();
    int iErr = m_Cnnt.read(out, m_iSessID);
    Log.i("p2p", "readData - " + iErr);
    if (iErr > pgJniConnect.PG_ERROR_OK) {// 0
      // 设备端返回的数据存放在pgJniConnect.OutRead里面
      return out.byBuf;
    } else {
    }
    return out.byBuf;
  }

  public void closeThread() {
    m_Cnnt.Close();
  }

  public void cleanup() {
    if (m_Cnnt != null) {
      m_Cnnt.Close();
      m_Cnnt.Cleanup();
    }
  }
}
