package com.meibeike.communication.mbk_interface;

import io.netty.channel.ChannelHandlerContext;

/**
 * 通讯句柄接口
 * com.meibeike.communication.mbk_interface.HandleCallBack
 * @author 张涢 <br/>
 * create at 2015-9-21 下午4:03:36
 */
public interface HandleCallBack {
  /**
   * 返回通讯层句柄
   * @param chx
   */
  public void callbackHandle(ChannelHandlerContext chx);
  
  /**
   * 接收到数据时 重置心跳
   */
  public void clearHeart();
  
  
}
