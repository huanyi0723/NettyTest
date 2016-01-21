package com.meibeike.communication.netty.thread;

import java.util.concurrent.TimeUnit;
import com.meibeike.communication.mbk_interface.CommunicationCallBack;
import com.meibeike.communication.mbk_interface.HandleCallBack;
import com.meibeike.communication.netty.handler.ClientHandler;
import com.meibeike.communication.util.Util;

import android.util.Log;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.AdaptiveRecvByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

/**
 * Netty 客户端线程 com.meibeike.client.thread.NettyClientThread
 * 
 * @author 张涢 <br/>
 *         create at 2015-3-6 下午8:12:06
 */
public class NettyClientThread extends Thread {
  private static final String TAG = "NettyClientThread";
  private int port;
  private String host;
  private CommunicationCallBack mCommunicationCallBack;
  private HandleCallBack mHandleCallBack;

  private String path;
  public NettyClientThread(String host, int port, CommunicationCallBack mCommunicationCallBack , HandleCallBack mHandleCallBack) {
    this.port = port;
    this.host = host;
    this.mCommunicationCallBack = mCommunicationCallBack;
    this.mHandleCallBack = mHandleCallBack;
    this.start();
  }

  @Override
  public void run() {
    try {
      connect(port, host);
    } catch (Exception e) {
      // TODO: handle exception
      e.printStackTrace();
//      Log.i("Tag", "客户端连接服务端出错 e=== " + e);
      mCommunicationCallBack.connectFailure(e);
    }

  }

  public void connect(int port, String host) throws Exception {
    EventLoopGroup group = new NioEventLoopGroup();
    try {
      Bootstrap b = new Bootstrap();
//      b.option(ChannelOption.TCP_NODELAY, true);  
//      b.option(ChannelOption.SO_SNDBUF, 1024 * 48);  
//      b.option(ChannelOption.SO_KEEPALIVE, true);  
//      b.option(ChannelOption.SO_TIMEOUT, 20000);  
      
      b.option(ChannelOption.TCP_NODELAY, true);  
      b.option(ChannelOption.SO_KEEPALIVE, true);  
      
      b.option(ChannelOption.RCVBUF_ALLOCATOR, AdaptiveRecvByteBufAllocator.DEFAULT);
      
      b.group(group).channel(NioSocketChannel.class)
          .handler(new ChannelInitializer<Channel>() {

            @Override
            protected void initChannel(Channel ch) throws Exception {
              // ch.pipeline().addLast(new LineBasedFrameDecoder(1024));
              // ch.pipeline().addLast(new StringEncoder());
              // ch.pipeline().addLast(new FixedLengthFrameDecoder(100));
              // ch.pipeline().addLast(new ChunkedWriteHandler());
              // ch.pipeline().addLast(new StringDecoder());
              // ch.pipeline().addLast(new EchoClientHandler());
              // ch.pipeline().addLast(new
              // LengthFieldBasedFrameDecoder(Integer.MAX_VALUE,0,4,0,4));
              // ch.pipeline().addLast(new LengthFieldPrepender(4,false));
              // ch.pipeline().addLast(new ObjectEncoder());
              // ch.pipeline().addLast(new
              // ObjectDecoder(ClassResolvers.weakCachingConcurrentResolver(null)));
              // ch.pipeline().addLast(new ObjectEncoder());
              // ch.pipeline().addLast(new ObjectEncoder());
              // ch.pipeline().addLast(new LineBasedFrameDecoder(1024));

              // 第一个参数为信息最大长度，超过这个长度回报异常，
              // 第二参数为长度属性的起始（偏移）位，我们的协议中长度是0到第3个字节，所以这里写0，
              // 第三个参数为“长度属性”的长度，我们是4个字节，所以写4，
              // 第四个参数为长度调节值，在总长被定义为包含包头长度时，修正信息长度，
              // 第五个参数为跳过的字节数，根据需要我们跳过前4个字节，以便接收端直接接受到不含“长度属性”的内容。
              // 真实数据最大字节数为Integer.MAX_VALUE，解码时自动去掉前面四个字节
//              ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
//              // 前四个字节表示真实的发送的数据长度Length，编码时会自动加上；
//              ch.pipeline().addLast(new LengthFieldPrepender(4, false));
//              ch.pipeline().addLast(new ByteArrayDecoder());
//              ch.pipeline().addLast(new ByteArrayEncoder());
              ch.pipeline().addLast(new ClientHandler(mCommunicationCallBack,mHandleCallBack));
              
//              ch.config().setAllocator(PooledByteBufAllocator.DEFAULT);
            }
          });
      ChannelFuture f = b.connect(host, port).sync();
      f.channel().closeFuture().sync();
    } finally {
      group.shutdownGracefully();
    }
  }

}
