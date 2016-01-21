package com.meibeike.communication.thread;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;

import com.meibeike.communication.entity.PackageHeader;
import com.meibeike.communication.entity.SendData;
import com.meibeike.communication.manager.CommunicationThreadManager;
import com.meibeike.communication.mbk_interface.HandleCallBack;
import com.meibeike.communication.netty.send.NettySendObj;
import com.meibeike.communication.netty.thread.NettyClientThread;
import com.meibeike.communication.util.Util;

/**
 * 用户通讯线程 com.meibeike.communication.thread.UserThread
 * 
 * @author 张涢 <br/>
 *         create at 2015-9-21 下午1:44:23
 */
public class UserThread extends Thread {
  private static final String TAG = "UserThread";
  private Context mContext;
  private CommunicationThreadManager manager;// 当前通讯统一管理器
  private HandlerThread handlerThread; // 子线程Handler队列
  private Handler mHandler; // 子线程Handler队列
  private static final int HEART = 0x10001;// 心跳
  private static final int QUEST_OUTTIME = 0x10002;// 请求超时
  private NettyClientThread mNettyClientThread = null;// Netty通讯
  public P2PConnectThread mP2PConnectThread = null;// P2P通讯

  public UserThread(Context mContext, CommunicationThreadManager manager) {
    this.mContext = mContext;
    this.manager = manager;
    init();
  }

  /**
   * 创建子线程Handler队列
   */
  private void init() {
    handlerThread = new HandlerThread(manager.key + "UserThread");
    handlerThread.start();
    Looper looper = handlerThread.getLooper();
    mHandler = new Handler(looper) {
      public void handleMessage(android.os.Message msg) {
        switch (msg.what) {
          case HEART:

            if (manager.isRunning) {
              mHandler.removeMessages(HEART);
              if (manager.getHeartEntity().HEART_INDEX == manager.getHeartEntity().HEART_OUTTIME) {
                /**
                 * 超时 返回给业务提醒 已经超时 该通讯层已经关闭
                 */
                manager.mCommunicationCallBack.communicationOutTime();
                /**
                 * 关闭通讯层
                 */
                manager.closeTheadManager();
              } else {
                manager.getHeartEntity().HEART_INDEX++;
                mHandler.sendEmptyMessageDelayed(HEART, 1000);
              }
            }

            break;
          case QUEST_OUTTIME:
            // Log.i("zy", "请求超时打印 = " + manager.getHeartEntity().QUEST_INDEX);
            // if (manager.isRunning) {
            // mHandler.removeMessages(QUEST_OUTTIME);
            // if (manager.getHeartEntity().QUEST_INDEX ==
            // manager.getHeartEntity().QUEST_TIMEOUT) {
            // /**
            // * 请求超时 返回接口
            // */
            // manager.mCommunicationCallBack.questTimeOut();
            // } else {
            // manager.getHeartEntity().QUEST_INDEX++;
            // mHandler.sendEmptyMessageDelayed(QUEST_OUTTIME, 1000);
            // }
            // }
            break;

        }
      }
    };
    try {
      mHandler.sendEmptyMessage(HEART);
    } catch (Exception e) {
    }
  }

  /**
   * 线程运行
   */
  @Override
  public void run() {
    connect(manager.getCommunication());
  }

  /**
   * 根据通讯类型 请求连接 0未知 1Netty 2P2P 3UDP
   */
  private void connect(int index) {
    Util.LogUntils("zy", "连接通讯层" + index);
    switch (index) {
      case CommunicationThreadManager.MBK_COMMUNICATION_UNKNOW:// 未知
        break;

      case CommunicationThreadManager.MBK_COMMUNICATION_NETTY:// Netty
        mNettyClientThread = new NettyClientThread(manager.host, manager.port, manager.mCommunicationCallBack, mHandleCallBack);
        break;
      case CommunicationThreadManager.MBK_COMMUNICATION_P2P:// P2P
        mP2PConnectThread = new P2PConnectThread(mContext, manager.sn, manager.isClearUp, manager.getHeartEntity().HEART_OUTTIME, manager.mCommunicationCallBack);
        break;
      case CommunicationThreadManager.MBK_COMMUNICATION_UDP:// UDP

        break;
    }
  }

  /**
   * 清空P2P
   * 
   * @Description: TODO
   * @param 参数说明
   * @return void 返回类型
   */
  public void clearUp() {
    if (mP2PConnectThread != null) {
      mP2PConnectThread.cleanup();
    }
  }

  /**
   * 向服务端发送心跳
   */
  public void sendHeart(int serialNumber, int mainFunction) {
    switch (manager.getCommunication()) {
      case 0:

        break;

      case 1:// 通讯层为 Netty
        try {
          manager.sendDataToServer(sendHeartbeat(serialNumber, mainFunction));
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        break;

      case 2:// 通讯层为 P2P

        break;

      case 3:
        break;
    }

  }

  /**
   * 重置心跳
   */
  public void clearHeart() {
    manager.getHeartEntity().HEART_INDEX = 0;
    mHandler.removeMessages(QUEST_OUTTIME);
  }

  /**
   * 设置超时时间
   */
  public void setQueueTimeOut() {
    if (mHandler != null) {
      mHandler.sendEmptyMessage(QUEST_OUTTIME);
    }
  }

  /**
   * 关闭当前线程和缓存
   */
  public void closeCache() {
    try {
      if (mHandler != null) {
        mHandler.removeMessages(HEART);
      }
      if (handlerThread != null) {
        handlerThread.interrupt();
      }
    } catch (Exception e) {
      // TODO: handle exception
    }
    try {
      this.interrupt();
    } catch (Exception e) {
      // TODO: handle exception
    }

    if (manager.chx != null) {
      manager.chx.close();
    }
    
    mNettyClientThread = null;
    if (mP2PConnectThread != null) {
      mP2PConnectThread.closeThread();
    }

    handlerThread = null;
    mHandler = null;

  }

  /**
   * 客户端向服务端发送的心跳数据
   * 
   * @return
   */
  public static SendData sendHeartbeat(int serialNumber, int mainFunction) {
    PackageHeader ph = new PackageHeader(0, 0, serialNumber, 0, mainFunction, (short) 0, (short) 9999, 0, 0);

    return new SendData(0, ph.getPackageHeader(), null, null, 0, 0, null);
  }

  /**
   * 返回Netty 句柄
   */
  private HandleCallBack mHandleCallBack = new HandleCallBack() {

    /**
     * 连接成功返回句柄
     */
    @Override
    public void callbackHandle(ChannelHandlerContext chx) {
      manager.setNettyHandle(chx);
      manager.isRunning = true;
    }

    /**
     * 重置心跳
     */
    @Override
    public void clearHeart() {
      manager.getHeartEntity().HEART_INDEX = 0;
    }

  };
}
