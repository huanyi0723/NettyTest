package com.meibeike.communication.mbk_interface;

import java.util.Queue;

import android.annotation.SuppressLint;

/**
 * 消息队列  
 * 信息处理机制  信息存放在消息队列里，先进先出 处理
 * com.meibeike.communication.mbk_interface.CommunicationInterface
 * @author 张涢 <br/>
 * create at 2015-9-21 下午1:28:19
 */
public interface CommunicationInterface {
  /**
   * 获取消息队列
   */
  public Queue<byte[]> getQueue()throws InterruptedException ;
  /**
   * 添加数据到消息队列的最后面
   * @throws InterruptedException 
   */
  public void addQueue(byte[] bt) throws InterruptedException;
  /**
   * 获取当前消息队列第一个数据，并remove掉第一个数据
   * @return
   */
  public byte[]  getFirstQueue()throws InterruptedException ;
  
  /**
   *  判断消息队列是否还有数据
   * @return
   */
  public boolean isHave()throws InterruptedException ;
  
  /**
   *  清空当前消息队列
   */
  public void clearQueue()throws InterruptedException ;
  
  /**
   * 获取当前消息队列还有多少个数据未处理
   * @return
   * @throws InterruptedException
   */
  public int getQueueSize()throws InterruptedException ;
  
}
