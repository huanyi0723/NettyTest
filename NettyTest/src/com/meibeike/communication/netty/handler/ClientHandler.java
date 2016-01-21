package com.meibeike.communication.netty.handler;

import java.net.InetSocketAddress;
import com.meibeike.communication.mbk_interface.CommunicationCallBack;
import com.meibeike.communication.mbk_interface.HandleCallBack;

import android.util.Log;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelMetadata;
/**
 * 客户端连接
 * com.meibeike.client.handler.ClientHandler
 * @author 张涢 <br/>
 * create at 2015-3-6 下午8:15:44
 */
public class ClientHandler extends ChannelInboundHandlerAdapter{
  private static final String TAG = "ClientHandler";
 
  private CommunicationCallBack mCommunicationCallBack; 
  
  private  HandleCallBack mHandleCallBack;
  
  private int start = 0; // 当前读取的字节

  private int top = 4;// 文件开头 标示 文件长度的 字段

  private boolean isTop = true; // 是否是文件开头

  private int length = 0; // 文件的长度

  
  
  private byte[] bytes = null;
  
  
  public ClientHandler(CommunicationCallBack call, HandleCallBack mHandleCallBack){
    mCommunicationCallBack = call;
    this.mHandleCallBack = mHandleCallBack;
  }
  /**
   * 客户端连接
   */
  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    Log.i("Tag", "客户端连接服务端成功");
//    
//    InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
//    String ip = insocket.getAddress().getHostAddress();
//    Log.i("Tag", "当前连接的服务端IP地址  === " + ip);
//    ChannelMetadata metadata = ctx.channel().metadata();
//    Log.i("Tag", "当前Channel 元数据 == " + metadata);
//    
//    ChannelConfig config = ctx.channel().config();
//    
//    Log.i("Tag", "当前Channel 配置信息 === " + config);
    mHandleCallBack.callbackHandle(ctx);
    mCommunicationCallBack.connected(ctx);
  }
  
  
  /**
   * 读取到的数据
   */
  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    ByteBuf buff = null;
    try {
      
//      if (isTop) {
////      removeHandler();
//      buff = (ByteBuf) msg;
//      byte[] top = new byte[4];
//      buff.readerIndex();
//      buff.readBytes(top);
//      length = DataConversion.byteArrayToInt(top);
//      Log.i("Tag", "从客户端传到服务端的文件总长度  === " + length);
//      top = null;
//    }
//
//      if (buff == null) {
//        buff = (ByteBuf) msg;
//      }
//     if (length > 0) {
//       isTop = false;
//       if (buff.isReadable()) {
//         bytes = new byte[buff.readableBytes()];
//         buff.readBytes(bytes);
//         mNettyClientCallBackListener.channelRead(ctx, bytes,length);
//         start += bytes.length;
//
//         if (start == length) {
//           Log.i("Tag", "文件传输完毕  == " + length);
//           isTop = true;
//           length = 0;
//         }
//       }
//    }
      buff = (ByteBuf) msg;
      bytes = new byte[buff.readableBytes()];
      buff.readBytes(bytes);
      mCommunicationCallBack.channelRead(ctx, bytes);
      mHandleCallBack.clearHeart();
    } catch (Exception e) {
      // TODO: handle exception
      e.printStackTrace();
      mCommunicationCallBack.exceptionCaught(ctx, e);
    }finally{
      bytes = null;
      buff.release();
    }

  }
  /**
   * 有异常时调用
   */
  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    //当前有异常，关闭
    cause.printStackTrace();
    mCommunicationCallBack.exceptionCaught(ctx, cause);
//    ctx.close();
  }
  
  
  
  
}
