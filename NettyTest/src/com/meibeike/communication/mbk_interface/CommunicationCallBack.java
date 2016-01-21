package com.meibeike.communication.mbk_interface;



import io.netty.channel.ChannelHandlerContext;
/**
 * 通讯回调接口
 * com.meibeike.communication.mbk_interface.ServerCallBackListener
 * @author 张涢 <br/>
 * create at 2015-9-21 下午3:13:04
 */
public interface CommunicationCallBack {
  /**
   * 连接成功
   * @param ctx socket 句柄
   */
  public void connected(ChannelHandlerContext ctx);
  
  /**
   * 当前通讯连接失败 返回异常
   * @param ctx socket 句柄 
   */
  public void connectFailure(Exception e);
  /**
   *  通讯异常
   * @param ctx socket 句柄
   * @param cause 错误信息
   */
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause);
  
  /**
   * 返回数据
   * @param ctx socket 句柄
   * @param msg 接收到的信息   byte[] 
   * @param length 信息总长度
   */
  public void channelRead(ChannelHandlerContext ctx, byte[] msg);
  
  /**
   * 当前通讯超时
   */
  public void communicationOutTime();
  
  /**
   * 请求超时
   */
  public void questTimeOut();
  
}
