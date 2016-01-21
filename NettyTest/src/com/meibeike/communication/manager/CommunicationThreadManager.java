package com.meibeike.communication.manager;

import io.netty.channel.ChannelHandlerContext;
import android.content.Context;

import com.meibeike.communication.entity.CommunicationCollection;
import com.meibeike.communication.entity.RefreshHeartbeatEntity;
import com.meibeike.communication.entity.SendData;
import com.meibeike.communication.mbk_interface.CommunicationCallBack;
import com.meibeike.communication.queue.SendThreadQueue;
import com.meibeike.communication.thread.SendThread;
import com.meibeike.communication.thread.UserThread;

/**
 * 用户通讯管理器
 * com.meibeike.mbk_communicationframework.manager.CommunicationThreadManager
 * 
 * @author 张涢 <br/>
 *         create at 2015-9-21 上午11:46:11
 */
public class CommunicationThreadManager {
  private static final String TAG = "CommunicationThreadManager";
  private Context mContext;
  /**
   * 当前管理器在通讯集合中的key ,只用于打印，用于开发者分辨当前通讯
   */
  public String key = null;

  /**
   * 服务端SN号 P2P连接时需要
   */
  public String sn;
  /**
   * 服务端 P2P 是否需要clearUp
   */
  public boolean isClearUp = false;

  /**
   * 连接服务端的IP地址
   */
  public String host;

  /**
   * 连接服务端的端口号 默认9223
   */
  public int port = 9223;

  /**
   * 当前连接是否已经连接成功
   */
  public boolean isRunning;// 是否已经连接通讯

  /**
   * 通讯回调接口
   */
  public CommunicationCallBack mCommunicationCallBack;

  /**
   * ֪ 通讯未知
   */
  public static final int MBK_COMMUNICATION_UNKNOW = 0;

  /**
   * ͨ通讯层为Netty
   */
  public static final int MBK_COMMUNICATION_NETTY = 1;

  /**
   * 通讯层为P2P
   */
  public static final int MBK_COMMUNICATION_P2P = 2;

  /**
   * ͨ通讯层为UDP
   */
  public static final int MBK_COMMUNICATION_UDP = 3;

  /**
   * 通讯层Netty 句柄
   */
  public ChannelHandlerContext chx = null;

  /**
   * 当前通讯层 默认未知
   */
  private static int COMMUNICATION = 0;

  /**
   * 当前通讯层的心跳
   */
  private RefreshHeartbeatEntity beart = new RefreshHeartbeatEntity();

  /********************************** 消息队列 *************************************/
  /**
   * 发送线程消息队列
   */
  private SendThreadQueue mSendThreadQueue = new SendThreadQueue();

  /************************************ 线程 ***********************************/
  /**
   * 用户通讯主线程
   */
  public UserThread mUserThread = null;

  /**
   * 用户通讯层_发送数据线程
   */
  private SendThread mSendThread = null;

  /*********************************************** 初始化 ***************************************************/

  /**
   * 初始化通讯管理器
   */
  public CommunicationThreadManager(Context mContext, String sn, String key, String host, int port, int COMMUNICATION, CommunicationCallBack mCommunicationCallBack) {
    this(mContext, sn, false, key, host, port, COMMUNICATION, mCommunicationCallBack);
  }

  /**
   * 初始化通讯管理器
   */
  public CommunicationThreadManager(Context mContext, String sn, boolean isClearUp, String key, String host, int port, int COMMUNICATION,
      CommunicationCallBack mCommunicationCallBack) {
    this.mContext = mContext;
    this.sn = sn;
    this.isClearUp = isClearUp;
    this.key = key;
    this.host = host;
    this.port = port;
    this.COMMUNICATION = COMMUNICATION;
    this.mCommunicationCallBack = mCommunicationCallBack;
    startThread();
    CommunicationCollection.addManager(key, CommunicationThreadManager.this);
  }

  /**
   * 启动线程
   */
  private void startThread() {
    mUserThread = new UserThread(mContext, this);
    mUserThread.start();
    mSendThread = new SendThread(mContext, this);
    mSendThread.start();
  }

  /******************************************************************/
  /**
   * 获取当前主用户线程的消息队列
   * 
   * @return
   */
  public SendThreadQueue getUserThreadQueue() {
    if (mSendThreadQueue != null) {
      return mSendThreadQueue;
    }
    return null;
  }

  /**
   * 发送数据给服务端
   * 
   * @param mByte
   * @throws InterruptedException
   */
  public void sendDataToServer(SendData data) throws InterruptedException {
    if (mSendThreadQueue != null && mSendThread != null && data != null) {
      mSendThreadQueue.addQueue(data);
      mSendThread.notifyLock();
    }
  }

  /**
   * 获取当前通讯层类型
   * 
   * @return
   */
  public int getCommunication() {
    return COMMUNICATION;
  }

  /**
   * 设置当前通讯连接类型
   * 
   * @param type
   */
  public void setConnectType(int type) {
    if (type != MBK_COMMUNICATION_UNKNOW && type != MBK_COMMUNICATION_NETTY && type != MBK_COMMUNICATION_P2P && type != MBK_COMMUNICATION_UDP) {
      type = MBK_COMMUNICATION_UNKNOW;
    } else {
      COMMUNICATION = type;
    }
  }

  /**
   * 获取当前心跳实例
   * 
   * @return
   */
  public RefreshHeartbeatEntity getHeartEntity() {
    return beart;
  }

  /**
   * 设置当前心跳超时时间 不设置的话 默认心跳超时3分钟
   * 
   * @param time
   */
  public void setHeartOutTime(int time) {
    beart.setHeartTimeout(time);
  }

  /**
   * 设置请求超时时间 单位秒
   * 
   * @param timeOut
   */
  public void setQuestTimeOut(int timeOut) {
    beart.setQuestTimeOut(timeOut);
  }

  /**
   * 向客户端发送心跳 心跳跟业务逻辑相关性太紧密，让业务来处理
   */
  public void sendHeart(int mainFunction) {
    if (mUserThread != null) {
      mUserThread.sendHeart(1, mainFunction);
    }
  }

  /**
   * 向客户端发送心跳 心跳跟业务逻辑相关性太紧密，让业务来处理
   */
  public void sendHeart(int serialNumber, int mainFunction) {
    if (mUserThread != null) {
      mUserThread.sendHeart(serialNumber, mainFunction);
    }
  }

  /**
   * 重置心跳
   */
  public void clearHeart() {
    if (mUserThread != null) {
      mUserThread.clearHeart();
    }
  }

  /**
   * 清空P2P
   * 
   * @Description: TODO
   * @param 参数说明
   * @return void 返回类型
   */
  public void p2pCleanup() {
    mUserThread.clearUp();
  }

  /**
   * 设置Netty句柄
   */
  public void setNettyHandle(ChannelHandlerContext chx) {
    this.chx = chx;
  }

  /***************************** 关闭当前通讯 ****************************/

  /**
   * 关闭当前线程管理器中的所有线程和 清楚当前所有缓存
   */
  public void closeTheadManager() {

    isRunning = false;
    try {
      if (mSendThreadQueue != null) {
        mSendThreadQueue.clearQueue();
      }
    } catch (InterruptedException e) {
    }
    /**
     * 关闭主用户线程
     */
    if (mUserThread != null) {
      mUserThread.closeCache();
    }

    /**
     * 关闭发送线程
     */
    if (mSendThread != null) {
      mSendThread.closeThread();

    }

    // try {
    // if (chx != null) {
    // chx.close();
    // }
    // } catch (Exception e) {
    // // TODO: handle exception
    // }
    if (key != null && !"".equals(key)) {
      CommunicationCollection.removeManager(key);
    }

    chx = null;
    mUserThread = null;
    mSendThread = null;
    mSendThreadQueue = null;
  }

}
