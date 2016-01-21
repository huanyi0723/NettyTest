package com.meibeike.communication.netty.send;

import android.util.Log;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;

/**
 * 发送二进制文件给服务端
 * com.meibeike.client.send.SendObj
 * @author 张涢 <br/>
 * create at 2015-3-13 上午11:49:39
 */
public class NettySendObj {
  private static final String TAG = "SendObj";
  private Channel mChannel;

  public NettySendObj(Channel mChannel) {
    this.mChannel = mChannel;
  }

  /**
   * 发送二进制文件 给服务端     
   * 可以包头 包体合并成一个文件发送    也可以分开按照顺序发      具体文件也可以分段发   没有设置限制   只需要顺序对
   * 
   * 
   * @param bytes 二进制文件
   */
  public void sendData(byte[] bytes) {
    ByteBuf buf = Unpooled.buffer(bytes.length);
    buf.writerIndex();
    buf.writeBytes(bytes);
    mChannel.writeAndFlush(buf);
  }

}
