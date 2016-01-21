package com.meibeike.meiphoto.common.clientconnect;

import java.util.ArrayList;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.example.nettytest.MeiApp;
import com.meibeike.meiphoto.common.clientconnect.ClientConnectFactory;
import com.meibeike.meiphoto.common.clientconnect.ClientConnectorManager;
import com.meibeike.meiphoto.common.clientconnect.ClientConstants;
import com.meibeike.meiphoto.common.clientconnect.UdpEntity;
import com.meibeike.meiphoto.common.clientconnect.UdpSocket;
import com.meibeike.meiphoto.common.clientconnect.UdpSocket.UdpCallBack;
import com.meibeike.meiphoto.common.clientconnect.impl.IClientConnect;
import com.meibeike.meiphoto.common.protocol.bmodel.IEntity;

 
public class ClientConnectService extends Service {
  private static final String TAG = "ClientConnectService";
  private IBinder binder = new ClientConnectService.LocalBinder();
  private IntentFilter mIntentFilter;
  private ClientConnectorManager connectMgr;
  public static int netType = ClientConstants.NET_NO;
  public static ArrayList<UdpEntity> UdpList = new ArrayList<UdpEntity>();
  
  public static final String ACTION_CONNECTIVITY_CHANGE = "android.net.conn.CONNECTIVITY_CHANGE";
  public static final String NETWORK_PROMPT = "com.network.prompt";
  String broadIP;

  @Override
  public void onCreate() {
    connectMgr = ClientConnectorManager.getInstance();
    netType = isConnected(this);
    mIntentFilter = new IntentFilter();
    mIntentFilter.addAction( ACTION_CONNECTIVITY_CHANGE);
    registerReceiver(mBroadCastReceiver, mIntentFilter);
  }

  /**
   * 获取一个连接
   * 
   * @param mainFunction
   */
  public IClientConnect getConnect(int mainFunction) {
    return connectMgr.getConnect(mainFunction);
  }

  /**
   * 将数据发送到连接管理类，并且在管理类中选择连接
   * 
   * @param serialNumber
   * @param entity
   */
  public void sendEntity(IEntity entity) {
    connectMgr.sendEntity(entity);
  }

  public void isClose() {
    connectMgr.isClose();
  }
  public void isHomeClose() {
    connectMgr.isHomeClose();
  }
  @Override
  public int onStartCommand(Intent intent, int flag, int startId) {
    return START_STICKY;
  }

  public void connect() {
    // 初始化连接 但是连接还没有开始建立
    Log.i(TAG, "初始化连接 ！");
  }

  @Override
  public IBinder onBind(Intent arg0) {
    return binder;
  }

  public class LocalBinder extends Binder {
    public ClientConnectService getService() {
      return ClientConnectService.this;
    }
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    unregisterReceiver(mBroadCastReceiver);
  }

  private BroadcastReceiver mBroadCastReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      String action = intent.getAction();
      // 用于网络状态监听
      if (action.equals( ACTION_CONNECTIVITY_CHANGE)) {
        int netCode = isConnected(context);
        // 网络状态切换 更新网络连接
        Log.i("lzy02", "切换网络状态，当前网络状态是 ：　" + netCode);
        netType = netCode;
        Intent mIntent = new Intent( NETWORK_PROMPT);
        MeiApp.mContext.sendBroadcast(mIntent);
        // 网络切换 清空udp集合
        // 当前网络状态是wifi 其他p2p扫描
        // 关闭所有连接
        ClientConnectFactory.getInstance().isClose();
        getBroadIP();
        //MbkConfiguration.getInstance().setNetType(netCode);
        UdpList.clear();
        //MbkConfiguration.getInstance().clearUdpList();
        // 只有在wifi下才执行udp扫描
        if (netCode == ClientConstants.NET_WIFI) {
          Log.i("lzy02", "------mBroadCastReceiver---------resSetUDP();-");
          resSetUDP();
        }
      }
    }
  };

  private void getBroadIP() {
    WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE); // 获取wifi服务
    // 判断wifi是否开启
    if (!wifiManager.isWifiEnabled()) {
      // 默认打开wifi
      // wifiManager.setWifiEnabled(true);
      broadIP = null;
      return;
    }
    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
    int ipAddress = wifiInfo.getIpAddress();
    broadIP = (ipAddress & 0xFF) + "." + ((ipAddress >> 8) & 0xFF) + "." + ((ipAddress >> 16) & 0xFF) + "." + "255";
  }

  /**
   * 当前网络状态是wifi 其他p2p扫描
   */
  public static void resSetUDP() {
    // UdpList.clear();
     //MbkConfiguration.getInstance().clearUdpList();
    if (ClientConstants.NET_WIFI == netType) {
      UdpSocket udp = UdpSocket.getInstance();
      udp.connectSocket(new UdpCallBack() {
        @Override
        public void udpCallBack(UdpEntity udpEntity) {
          if (udpEntity != null && udpEntity.isWifi()) {
            // 必须去除重复数据
            UdpList.add(udpEntity);
            //MbkConfiguration.getInstance().setUdpList(udpEntity);
          }
        }
      });
    }
  }
  
 

  /**
   * 判断当前是否网络连接
   * 
   * @param context
   * @return 状态码
   */
  public int isConnected(Context context) {
    int stateCode = ClientConstants.NET_NO;
    ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo ni = cm.getActiveNetworkInfo();
    if (ni != null && ni.isConnectedOrConnecting()) {
      switch (ni.getType()) {
        case ConnectivityManager.TYPE_WIFI:
          stateCode = ClientConstants.NET_WIFI;
          break;
        case ConnectivityManager.TYPE_MOBILE:
          switch (ni.getSubtype()) {
            case TelephonyManager.NETWORK_TYPE_GPRS: // 联通2g
            case TelephonyManager.NETWORK_TYPE_CDMA: // 电信2g
            case TelephonyManager.NETWORK_TYPE_EDGE: // 移动2g
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
              stateCode = ClientConstants.NET_2G;
              break;
            case TelephonyManager.NETWORK_TYPE_EVDO_A: // 电信3g
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
              stateCode = ClientConstants.NET_3G;
              break;
            case TelephonyManager.NETWORK_TYPE_LTE:
              stateCode = ClientConstants.NET_4G;
              break;
            default:
              stateCode = ClientConstants.NET_UNKNOWN;
          }
          break;
        default:
          stateCode = ClientConstants.NET_UNKNOWN;
      }

    }
    return stateCode;
  }
}
