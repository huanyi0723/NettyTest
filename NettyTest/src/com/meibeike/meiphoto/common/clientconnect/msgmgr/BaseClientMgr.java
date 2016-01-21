package com.meibeike.meiphoto.common.clientconnect.msgmgr;

import io.netty.channel.ChannelHandlerContext;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.nettytest.MeiApp;
import com.meibeike.communication.entity.SendData;
import com.meibeike.communication.manager.CommunicationThreadManager;
import com.meibeike.communication.mbk_interface.CommunicationCallBack;
import com.meibeike.meiphoto.common.clientconnect.ClientConstants;
import com.meibeike.meiphoto.common.clientconnect.ClientSocketUtils;
import com.meibeike.meiphoto.common.clientconnect.PackageHeader;
import com.meibeike.meiphoto.common.clientconnect.UdpEntity;
import com.meibeike.meiphoto.common.clientconnect.impl.IClientConnect;
import com.meibeike.meiphoto.common.protocol.bmodel.IEntity;

@SuppressLint("HandlerLeak")
public abstract class BaseClientMgr extends Subject implements IClientConnect {

  /** Context对象 */
  protected Context mContext;
  /** 当前是否正在连接 */
  protected boolean isRunning;
  /** 是否正在发送 线程是否被占用 */
  protected boolean isSending;
  private String mConnectKey = "BasicServicesMgr";
  /** 连接服务器的IP地址 */
  private String mHost;
  /** 连接服务器的端口号 */
  private int mPort;
  /** 通讯类型 */
  private int mCommunication;
  // /** 心跳超时 */
  // private int mOutTime;
  // /** 当前通讯句柄 */
  // private ChannelHandlerContext mChx;
  /** 该通讯层管理器 */
  protected CommunicationThreadManager mManager;
  /** 待发送消息集合 */
  protected ArrayList<IEntity> mEntityMsg = null;
  /** 数据解析线程 */
  protected ParseByteThread mParseByteThread = null;
  /** 线程连接池 */
  protected ExecutorService executor;
  //关闭连接功能号
  protected int function = 1200;
  //记录心跳超时次数
  private int heartTimeOutCount = 0;
  
  public static final int RESPONSE_SUCCESS = 0x401;
  public static final int RESPONSE_FAIL = 0x402;
  public static final int RESPONSE_TIMEOUT = 0x403;
  /** 心跳超时  */
  public static final int REQUEST_HEARTBEAT_TIMEOUT = 0x410;
  /** 用户未登录  */
  public static final int  NOT_LOGIN= 0x411;
  

  protected BaseClientMgr(String host, int port, String key) {
    init(host, port, key);
  }

  /**
   * @Description: 初始化
   * @param @param host ip地址
   * @param @param port 端口 参数说明
   * @return void 返回类型
   */
  private void init(String host, int port, String key) {
    this.mContext = MeiApp.mContext;
    isRunning = false;
    isSending = false;
    mHost = host;
    mPort = port;
    mConnectKey = key;
    mEntityMsg = new ArrayList<IEntity>();
    executor = Executors.newFixedThreadPool(10);
    mParseByteThread = new ParseByteThread(this);
    executor.execute(mParseByteThread);
  }

  protected Handler basicHandler = new Handler() {

    @Override
    public void handleMessage(Message msg) {
      super.handleMessage(msg);
      switch (msg.what) {
        case ClientConstants.REQUEST:
          // 发送请求 连接占用
          if (mEntityMsg != null && mEntityMsg.size() > 0) {
            isSending = true;
            // 清除handler的消息
            basicHandler.removeMessages(ClientConstants.REQUEST);
            basicHandler.removeMessages(ClientConstants.REQUEST_CREATE_CONNECT);
            basicHandler.removeMessages(ClientConstants.REQUEST_SEND_MESSAGE);
            // 请求类型 当为网络请求时判断网络状态 建立连接
            // 检查连接是否可用
            if (isRunning) {
              // 直接发送消息
              basicHandler.removeMessages(ClientConstants.REQUEST_SEND_MESSAGE);
              basicHandler.sendEmptyMessage(ClientConstants.REQUEST_SEND_MESSAGE);
            } else {
              // 建立连接
              basicHandler.removeMessages(ClientConstants.REQUEST_CREATE_CONNECT);
              Message msgCreate = Message.obtain();
              msgCreate.what = ClientConstants.REQUEST_CREATE_CONNECT;
              msgCreate.arg1 = 0;
              basicHandler.sendMessage(msgCreate);
            }
            
 
          }
          break;
        case ClientConstants.REQUEST_CREATE_CONNECT:
          // 建立连接
          Log.i("mbk","建立连接！");
          isConnect( "netty" );
          
          break;
        case ClientConstants.REQUEST_SEND_MESSAGE:
          // 发送消息
          Log.i("mbk","发送消息！");
          if (isRunning) {
            if (mEntityMsg.size() > 0) {
              Log.i("mbk","发送数据!");
              sendData(mEntityMsg.get(0));
              basicHandler.removeMessages(ClientConstants.REQUEST_TIMEOUT);
              // 设置请求超时
              basicHandler.sendEmptyMessageDelayed(ClientConstants.REQUEST_TIMEOUT, 3000);
            } else {
              Log.i("mbk","数据发送完成！");
              isSending = false;
            }
          } else {
            // 重新建立连接
            basicHandler.removeMessages(ClientConstants.REQUEST_CREATE_CONNECT);
            basicHandler.sendEmptyMessage(ClientConstants.REQUEST_CREATE_CONNECT);
          }
          break;
        case ClientConstants.REQUEST_SEND_HEARTBEAT:
          Log.i("mbk","发送心跳！");
          mManager.sendHeart(function);
          heartTimeOutCount++;
          Log.i("lzy02", "heartTimeOutCount---------------"+heartTimeOutCount);
          if(heartTimeOutCount >= 3){//大于等于3则认为与云棒无连接
            callBack(null, null, "心跳超时！",  REQUEST_HEARTBEAT_TIMEOUT);
          }
          // // 发送心跳
          basicHandler.removeMessages(ClientConstants.REQUEST_SEND_HEARTBEAT);
          basicHandler.sendEmptyMessageDelayed(ClientConstants.REQUEST_SEND_HEARTBEAT, 3000);

          break;
        case ClientConstants.REQUEST_TIMEOUT://请求超时
          Log.i("mbk","请求超时！");
          isRunning = false;
          callBack(null, null, "请求超时！",  RESPONSE_TIMEOUT);
          break;

      }
    }
  };

  public void sendHeartbeat(int function) {
    this.function = function;
  }

  public void sendData(IEntity entity) {
    sendByte(ClientSocketUtils.sendDatas(mEntityMsg.get(0)));
  }

  /**
   * 建立连接
   * 
   * @param netType
   *          网络类型
   */
  @Override
  public void isConnect(String netType) {
    UdpEntity udpEntity = null;
    int type = CommunicationThreadManager.MBK_COMMUNICATION_NETTY;
    if (netType.equals("netty")) {
      // 建立一个netty连接
      type = CommunicationThreadManager.MBK_COMMUNICATION_NETTY;
      
      mManager = new CommunicationThreadManager(mContext, null, mConnectKey, "192.168.31.241", mPort, type, mCommunicationCallBack);
      
        Log.i("mbk","发送地址---" + "192.168.31.241");
        Log.i("mbk","发送端口号---" + mPort);
      
/*      if (udpEntity != null) {
        Log.i("lzy02", "udpEntity---209----------udpEntity=="+udpEntity.getYunbangIp());
        mManager = new CommunicationThreadManager(mContext, null, mConnectKey, "192.168.31.241", mPort, type, mCommunicationCallBack);
        //Toast.makeText(mContext, "已通过Netty发送 ", Toast.LENGTH_SHORT).show();
        Log.i("mbk","netty发送云棒IP号---" + udpEntity.getYunbangIp());
      } else {
        Log.i("lzy02", "udpEntity---211----------udpEntity == null");
        callBack(null, null, "无法连接netty！",  RESPONSE_FAIL);
      }*/
      // 使用netty是时候 清理p2p
      P2pClearUp();
    } else {
      
    }
    Log.i("mbk","初始化 连接服务器！" + netType);
  }

  @Override
  public void sendByte(byte[] b) {
    try {
      if (mManager != null) {
        mManager.sendDataToServer(new SendData(b));
      } else {
        isClose();
      }
    } catch (InterruptedException e) {
      isClose();
    }
  }

  /**
   * 服务端回调
   */
  private CommunicationCallBack mCommunicationCallBack = new CommunicationCallBack() {

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
      Log.i("mbk","--------------------------请求异常--------------------------" + mCommunication);
      isRunning = false;
      callBack(null, null, "请求异常！",  RESPONSE_FAIL);

    }

    @Override
    public void connected(ChannelHandlerContext ctx) {
      Log.i("mbk","--------------------------连接成功--------------------------" + mCommunication);
      // mChx = ctx;
      isRunning = true;
      sendAgain();
    }

    @Override
    public void connectFailure(Exception e) {
      Log.i("mbk","--------------------------连接服务器失败--------------------------" + mCommunication);
      isRunning = false;
      callBack(null, null, "连接服务器失败！",  RESPONSE_FAIL);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, byte[] msg) {
      Log.i("mbk","--------------------------服务端返回--------------------------" + mCommunication);
      if (mParseByteThread != null) {
        mParseByteThread.sendParseByte(msg);
      }
    }

    @Override
    public void communicationOutTime() {
      Log.i("mbk","--------------------------连接超时--------------------------" + mCommunication);
      isRunning = false;
      callBack(null, null, "连接超时！",  RESPONSE_TIMEOUT);
    }

    @Override
    public void questTimeOut() {
      Log.i("mbk","--------------------------请求超时--------------------------" + mCommunication);
      isRunning = false;
      callBack(null, null, "请求超时！",  RESPONSE_TIMEOUT);
    }
  };

  @Override
  public void sendAgain() {
    // 连接成功 发起请求
    Log.i("mbk","连接成功,数据重新发送!");
    
    //basicHandler.sendEmptyMessage(ClientConstants.REQUEST_SEND_MESSAGE);
    basicHandler.sendEmptyMessageDelayed(ClientConstants.REQUEST_SEND_MESSAGE, 500);
  }

  /**
   * 接收需要发送的实体
   * 
   * @param entity
   */
  @Override
  public void sendEntity(IEntity entity) {
    if (mEntityMsg != null && entity != null) {
      mEntityMsg.add(entity);
      if (!isSending) {
        // 启动一个发送
        Log.i("mbk","发起请求！REQUEST_NET");
        basicHandler.sendEmptyMessage(ClientConstants.REQUEST);
      }
    }
    // if (mEntityMsg != null && mEntityMsg.size() == 2) {
    // mEntityMsg.remove(1);
    // }

  }

  @Override
  public void callBack(PackageHeader header, byte[] data, String desc, int type) {
    basicHandler.removeMessages(ClientConstants.REQUEST_SEND_HEARTBEAT);

    switch (type) {
      case  RESPONSE_SUCCESS:
        heartTimeOutCount = 0;
        basicHandler.sendEmptyMessageDelayed(ClientConstants.REQUEST_SEND_HEARTBEAT, 20000);
        switch (header.getFunction()) {
          case 9998:
            Log.i("mbk","服务端关闭！");
            isClose();
            break;
          case 9999:
            Log.i("mbk","成功返回一个心跳！");
            break;
          case 999:
            Log.i("mbk","未知错误！");
            callBack(null, null, "未知错误",  RESPONSE_FAIL);
            break;
          default:
            responseSuccess(header, data, desc, type);
            break;
        }
        break;
      case   REQUEST_HEARTBEAT_TIMEOUT://心跳超时3次认为与云棒无连接
/*        Intent m2Intent = new Intent(MeiConfigs.NETWORK_PROMPT);
        m2Intent.putExtra("islogin", "3003");
        MeiApp.mContext.sendBroadcast(m2Intent);*/
        break;
      case  RESPONSE_FAIL:
        responseFail(header, data, desc, type);
        break;
      case  RESPONSE_TIMEOUT:
        responseFail(header, data, desc, type);
        break;
    }
  }

  /**
   * @Description: 请求成功
   * @param @param header
   * @param @param data
   * @param @param desc
   * @param @param type 参数说明
   * @return void 返回类型
   */
  public void responseSuccess(PackageHeader header, byte[] data, String desc, int type) {

    try {
      if (mEntityMsg.size() > 0 && mEntityMsg.get(0).getHandler() != null) {
        IEntity entity = mEntityMsg.get(0);
        if (data != null && data.length > 0) {
          entity.onDecode(new String(data, "utf-8"));
          //Log.i("mbk","云棒返回---" + "---" + new String(data, "utf-8"));
          // 请求成功
 
            Log.i("lzy02", "1--------------"+entity.getCode());
            Log.i("mbk","返回一条数据！");
            Message msg = Message.obtain();
            msg.obj = entity;
            msg.arg1 = header.getFunction();
            msg.what = type;
            entity.getHandler().sendMessage(msg);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      isClose();
    }
    if (mEntityMsg != null && mEntityMsg.size() > 0) {
      mEntityMsg.remove(0);
    }
    basicHandler.removeMessages(ClientConstants.REQUEST_TIMEOUT);
    isSending = false;
    if (mEntityMsg.size() > 0) {
      basicHandler.sendEmptyMessage(ClientConstants.REQUEST);
    }
  }

  /**
   * @Description: 请求失败
   * @param @param header
   * @param @param data
   * @param @param desc
   * @param @param type 参数说明
   * @return void 返回类型
   */
  public void responseFail(PackageHeader header, byte[] data, String desc, int type) {
    Log.i("mbk","请求失败!   " + desc);
    Message msg = Message.obtain();
    msg.obj = desc;
    msg.arg1 = 0;
    msg.what = type;
    if (mEntityMsg.size() > 0 && mEntityMsg.get(0).getHandler() != null) {
      mEntityMsg.get(0).getHandler().sendMessage(msg);
    }
    isClose();
  }

  /* (non-Javadoc)
   * 请求本地缓存返回
   */
  @Override
  public void callBack(IEntity entity, String desc) {
    Log.i("mbk","回一返个缓存数据!  ");
    if ("cache".equals(desc)) {
      if (entity != null && entity.getHandler() != null) {
        Message msg = Message.obtain();
        msg.obj = entity;
        msg.what =  RESPONSE_SUCCESS;
        entity.getHandler().sendMessage(msg);
      }
    }
  }

  public void P2pClearUp() {
    if (mManager != null) {
      mManager.p2pCleanup();
    }
  }

  @Override
  public void isClose() {
    Log.i("mbk","关闭连接！" + isRunning);
    if (mManager != null) {
      if (isRunning) {
        try {
          mManager.sendDataToServer(new SendData(ClientSocketUtils.sendExit(function)));
        } catch (InterruptedException e) {
        }
      } else {
        mManager.closeTheadManager();
        mManager = null;
      }
    }
    if(mParseByteThread != null)
      mParseByteThread.closeThread();
    if (mEntityMsg != null) {
      mEntityMsg.clear();
    }
    P2pClearUp();
    basicHandler.removeMessages(ClientConstants.REQUEST_SEND_HEARTBEAT);
    basicHandler.removeMessages(ClientConstants.REQUEST_TIMEOUT);
    isRunning = false;
    isSending = false;
  }

  /********************************************************/
  @Override
  public void sendMsgFail(String netType, byte[] msg) {
  }

  @Override
  public void connectFail(String netType) {
  }

  @Override
  public void isClearMsg() {
    if (mEntityMsg != null) {
      mEntityMsg.clear();
    }
  }

}
