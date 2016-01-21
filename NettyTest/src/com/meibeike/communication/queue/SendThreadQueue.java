package com.meibeike.communication.queue;

import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.meibeike.communication.entity.SendData;
import com.meibeike.communication.mbk_interface.CommunicationInterface;

/**
 * 用户发送线程消息队列 com.meibeike.communication.queue.UserThreadQueue
 * 
 * @author 张涢 <br/>
 *         create at 2015-9-21 下午1:27:45
 */
public class SendThreadQueue {
  private static final String TAG = "SendThreadQueue";

  private BlockingQueue<SendData> mSendThreadQueue;

  public SendThreadQueue() {
    if (mSendThreadQueue == null) {
      mSendThreadQueue = new LinkedBlockingQueue<SendData>();
    } else {
      mSendThreadQueue.clear();
    }
  }

  /**
   * 获取当前消息队列的实例
   */
  public Queue<SendData> getQueue() throws InterruptedException {
    if (mSendThreadQueue == null) {
      mSendThreadQueue = new LinkedBlockingQueue<SendData>();
    }
    return mSendThreadQueue;
  }

  /**
   * 添加数据到消息队列的最后面
   */
  public void addQueue(SendData send) throws InterruptedException {
    if (mSendThreadQueue != null) {
      if (send != null) {
        mSendThreadQueue.offer(send);
      }
    }
  }

  /**
   * 获取并且移除队列的头。如果队列为空，则为nul
   */
  public SendData getFirstQueue() throws InterruptedException {
    if (mSendThreadQueue != null) {
      return (SendData) mSendThreadQueue.poll();
    }
    return null;
  }

  /**
   * 判断消息队列是否还有数据
   */
  public boolean isHave() throws InterruptedException {
    if (mSendThreadQueue != null) {
      if (!mSendThreadQueue.isEmpty()) {
        return true;
      }
    }
    return false;
  }

  /**
   * 清空当前消息队列
   */
  public void clearQueue() throws InterruptedException {
    if (mSendThreadQueue != null) {
      mSendThreadQueue.clear();
      // mSendThreadQueue.notify();
      mSendThreadQueue = null;
    }

  }

  /**
   * 获取当前消息队列还有多少个数据未处理
   */
  public int getQueueSize() throws InterruptedException {
    if (mSendThreadQueue != null) {
      return mSendThreadQueue.size();
    }
    return 0;
  }
}
