package com.meibeike.meiphoto.common.clientconnect;

import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


@SuppressLint("HandlerLeak")
public class UdpSocket implements Runnable {
  private Thread udpThread = null;
  private boolean isRunning = false;
  private static UdpSocket instance = null;

  private DatagramSocket socket = null;
  private DatagramPacket sendPacket = null;
  private DatagramPacket resPacket = null;
  private UdpCallBack callback = null;

  private static final int buf_length = 1024;
  private byte[] buf = new byte[buf_length];
  public static final int PORT = 5959;

  private int count = 0;
  private ExecutorService executor = null;
  // private UdpEntity entity = null;
  
  long testMeiid = 1000217;

  private Handler runHandler = new Handler() {

    @Override
    public void handleMessage(Message msg) {
      super.handleMessage(msg);
      switch (msg.what) {
        case 1:
          Log.i("mbk","启动一个广播发送！");
          
          final long meiid = testMeiid;

          Message msgM = Message.obtain();
          msgM.obj = meiid;
          msgM.what = 1;
          runHandler.sendMessageDelayed(msgM, 1000);

          if (count < 3) {
            executor.execute(new Runnable() {

              @Override
              public void run() {
                InetAddress broadcast = null;
                Log.i("mbk","启动一个广播线程！");
                try {
                  // 外网扫描
                  // broadcast = InetAddress.getByName("255.255.255.255");
                  // 广播地址
                  // 获取本机IP 改变最后一段为255
                  //broadcast = InetAddress.getByName(MeiConfigs.broadIP);
                  sendData(meiid, broadcast, PORT);
                } catch (Exception e) {
                  e.printStackTrace();
                }
              }
            });
            count++;
          } else {
            count = 0;
            runHandler.removeMessages(1);
            isRunning = false;
          }

          break;

        default:
          break;
      }

    }

  };

  // 而要发送的数据
  public static UdpSocket getInstance() {
    if (instance == null) {
      synchronized (UdpSocket.class) {
        if (instance == null) {
          instance = new UdpSocket();
        }
      }
    }
    return instance;
  }

  private UdpSocket() {
    executor = Executors.newFixedThreadPool(3);
  }

  public void connectSocket(UdpCallBack callback) {
    this.callback = callback;
    isRunning = true;
    startThread();

    Message msg = Message.obtain();
    msg.obj = testMeiid;
    msg.what = 1;
    runHandler.sendMessage(msg);
  }

  private void startThread() {
    try {
      if (socket == null) {
        Log.i("mbk","建立socket");
        socket = new DatagramSocket(9666);
      }

      if (resPacket == null) {
        // 创建接受类型的数据报
        Log.i("mbk","建立resPacket");
        resPacket = new DatagramPacket(buf, buf_length);
      }
    } catch (Exception e) {
    }

    if (udpThread == null) {
      udpThread = new Thread(this);
      executor.execute(udpThread);
    }
  }

  public void sendData(long meiid, InetAddress sendTo, int port) {

    Log.i("mbk","方式广播数据！meiid=" + meiid);
    UdpEntity entity = new UdpEntity(meiid);
    byte[] data = entity.onEncode().getBytes();
    try {
      sendPacket = new DatagramPacket(data, data.length, sendTo, port);
      Log.i("mbk","UdpSocket-----" + sendPacket.getAddress().getHostAddress()+"---SocketAddress--"+sendPacket.getSocketAddress());
      socket.send(sendPacket);
    } catch (Exception e) {
      sendPacket = null;
    }
  }

  @Override
  public void run() {

    while (isRunning) {
      try {
        Log.i("mbk","启动接收监听！");
        // 接受数据报
        socket.receive(resPacket);

      } catch (Exception e) {
        if (resPacket != null) {
          resPacket = null;
        }

        if (socket != null) {
          socket.close();
          socket = null;
        }

        udpThread = null;
        break;
      }

      if (resPacket.getLength() <= 0) {
        continue;
      }

      String strJson = null;
      try {
        Log.i("mbk","生成字符串！");
        strJson = new String(buf, 0, buf.length, "UTF-8");
        //Log.i("mbk",strJson);
        UdpEntity entity = new UdpEntity(strJson);
        entity.setWifi(true);
        // 接收的ip地址
        String ip = resPacket.getAddress().toString();
        entity.setYunbangIp(ip.substring(1, ip.length()));
        entity.setYunbangPort(resPacket.getPort());
        if (callback != null) {

          callback.udpCallBack(entity);
        }
      } catch (UnsupportedEncodingException e) {
        e.printStackTrace();
      }
      // count = 0;
      // runHandler.removeMessages(1);
    }

    if (resPacket != null) {
      resPacket = null;
    }

    if (socket != null) {
      socket.close();
      socket = null;
    }

    udpThread = null;
  }

  public interface UdpCallBack {

    public abstract void udpCallBack(UdpEntity udpEntity);
  }

}
