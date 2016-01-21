package com.meibeike.communication.thread;

import io.netty.channel.Channel;
import android.content.Context;
import android.util.Log;

import com.meibeike.communication.entity.SendData;
import com.meibeike.communication.manager.CommunicationThreadManager;
import com.meibeike.communication.netty.send.NettySendObj;
import com.meibeike.communication.util.Util;
import com.peergine.connect.android.pgJniConnect;

/**
 * 数据发送线程 com.meibeike.communication.thread.SendThread
 * 
 * @author 张涢 <br/>
 *         create at 2015-9-21 下午4:58:03
 */
public class SendThread extends Thread {    
  private static final String TAG = "SendThread";
  private Context mContext;
  private CommunicationThreadManager manager;// 通讯管理器
  private boolean running = true;// 是否循环
  private int number;// 记录没有获取数据的次数

  public SendThread(Context mContext, CommunicationThreadManager manager) {
    this.mContext = mContext;
    this.manager = manager;
  }

  @Override
  public void run() {

    while (running) {
      int size = 0;
      try {
        size = manager.getUserThreadQueue().getQueueSize();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

      if (size > 0) {
      SendData mSendData = null;
        try {
          mSendData =  manager.getUserThreadQueue().getFirstQueue();
        } catch (InterruptedException e) {
        }
        
        if (mSendData != null) {
          sendToServer(mSendData);
        }else {
          Log.i(TAG, "当前发送到服务端的数据为空");
        }
        
        try {
          Thread.sleep(5);
        } catch (InterruptedException e) {
        }
        
      } else {
        dataWaiting();
      }

    }

  }

  /**
   * 发送数据到服务端
   */
  private void sendToServer(SendData mSendData) {
    switch (manager.getCommunication()) {
      case CommunicationThreadManager.MBK_COMMUNICATION_UNKNOW:
        /**
         * 当前通讯层为未知
         */

        break;

      case CommunicationThreadManager.MBK_COMMUNICATION_NETTY:
        /**
         * 当前通讯层为Netty
         */
        nettySendData(mSendData.bytes);
        break;

      case CommunicationThreadManager.MBK_COMMUNICATION_P2P:
    	  Util.LogUntils("tag", "p2p线程开始发送：" + manager.key);
        /**
         * 当前通讯层为P2P
         */
        sendP2P(mSendData);
        Util.LogUntils("tag", "p2p线程发送结束：" + manager.key);
        if (manager != null &&  manager.mUserThread != null) {
          manager.mUserThread.setQueueTimeOut();
        }
        Log.d("p2p", "线程切换");
        break;
      case CommunicationThreadManager.MBK_COMMUNICATION_UDP:
        /**
         * 当前通讯层为UDP
         */
        break;
    }
  }

  /**
   * 发送消息 P2P
   * 
   * @param bytes
   */
  public void sendP2P(SendData mSendData) {
    if (mSendData.sendType == 0) {
      if(manager == null || manager.mUserThread == null || manager.mUserThread.mP2PConnectThread == null ||manager.mUserThread.mP2PConnectThread.m_Cnnt == null)
        return;
      int iErrWirte = manager.mUserThread.mP2PConnectThread.m_Cnnt.Write(manager.mUserThread.mP2PConnectThread.m_iSessID,mSendData.bytes, pgJniConnect.PG_PRIORITY_0);
      Util.LogUntils(TAG,"P2P返回信息成功 返回用户ID==" +  manager.mUserThread.mP2PConnectThread.m_iSessID + ",iErrWirte === " + iErrWirte);
      if (iErrWirte > pgJniConnect.PG_ERROR_OK) {
        Util.LogUntils(TAG, "P2P返回信息成功 返回用户ID === " + manager.mUserThread.mP2PConnectThread.m_iSessID + " 返回字节 == " + mSendData.bytes.length);
      }else {
        manager.mCommunicationCallBack.connectFailure(null);
      }
    }else if (mSendData.sendType == 1) {
      if (mSendData != null  && mSendData.path != null && mSendData.header != null
          && mSendData.path.length() > 1) {
        manager.mUserThread.mP2PConnectThread.m_Cnnt.sendFile(manager ,manager.mUserThread.mP2PConnectThread.m_iSessID,  pgJniConnect.PG_PRIORITY_1,mSendData);
      }
    }
  }
  
  
  /**
   * 当前通讯层为Netty时发送数据
   * 
   * @param mByte
   */
  private void nettySendData(byte[] bytes) {
    if (manager.chx != null) {
      Channel mChannel = manager.chx.channel();
      if (mChannel.isActive() && mChannel.isOpen() && mChannel.isRegistered()) {
        NettySendObj send = new NettySendObj(mChannel);
        send.sendData(bytes);
        manager.mUserThread.setQueueTimeOut();
      }
    }
  }

  /**
   * 数据锁
   */
  private void dataWaiting() {
    try {
      number++;
      synchronized (SendThread.this) {
    	  Util.LogUntils(TAG,String.format("call dataWaiting,number=%s,线程为：%s",number,this.manager.key));
        if (number > 2) {
          this.number = 0;
          this.wait();
        } else {
          try {
            Thread.sleep(5);
          } catch (Exception e) {
            // TODO: handle exception
          }
        }
      }
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  /**
   * 打开锁，让线程可以获取数据，激活线程
   */
  public void notifyLock() {
	  Util.LogUntils(TAG,String.format("call notifyLock,number=%s,线程为：%s",number,this.manager.key));
    try {
      synchronized (SendThread.this) {
        this.number = 0;
        this.notify();
      }
    } catch (Exception e) {
      // TODO: handle exception
    }
  }

  /**
   * 关闭线程
   */
  public void closeThread() {
	  Util.LogUntils(TAG,String.format("call notifyLock,number=%s,线程为：%s",number,this.manager.key));
    running = false;
    notifyLock();
    try {
      this.interrupt();
    } catch (Exception e) {
    }
  }
}
