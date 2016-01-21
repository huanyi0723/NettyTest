package com.meibeike.meiphoto.common.clientconnect;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.meibeike.meiphoto.common.protocol.bmodel.IEntity;

 
public class ClientConnectFactory {

  private static ClientConnectFactory instance = null;
  private ClientConnectService connectorService;

  public static ClientConnectFactory getInstance() {
    if (instance == null) {
      synchronized (ClientConnectFactory.class) {
        if (instance == null) {
          instance = new ClientConnectFactory();
        }
      }
    }
    return instance;
  }

  private ClientConnectFactory() {
    Log.i("mbk", "创建连接管理工程！");
  }

  /**
   * 初始化消息工厂， 建立绑定服务
   * 
   * @param context
   */
  public void init(Context context) {
    context.bindService(new Intent(context, ClientConnectService.class), serviceConnection, Context.BIND_AUTO_CREATE);
  }

  /**
   * 接收请求 ，并且将请求发送到消息发送服务
   * 
   * @param entity
   */
  public void sendEntity(IEntity entity) {
    try {
    
      connectorService.sendEntity(entity);
    } catch (Exception e) {
      Log.i("mbk", "---" + e.getMessage());
    }
  }

  public void isClose() {
    if (connectorService != null) {
      connectorService.isClose();
    }
  }
  public void isHomeClose(){
    if (connectorService != null) {
      connectorService.isHomeClose();
    }
  }
  
  ServiceConnection serviceConnection = new ServiceConnection() {
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
      connectorService = ((ClientConnectService.LocalBinder) service).getService();
      if (connectorService != null) {
        // 启动连接
        connectorService.connect();
        Log.i("mbk", "启动服务， 建立连接！");
      }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
    }
  };

}
