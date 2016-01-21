package com.meibeike.meiphoto.common.clientconnect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.annotation.SuppressLint;
import android.util.Log;

import com.meibeike.meiphoto.common.clientconnect.impl.IClientConnect;
import com.meibeike.meiphoto.common.clientconnect.msgmgr.BasicServicesMgr;
import com.meibeike.meiphoto.common.clientconnect.msgmgr.Observer;
import com.meibeike.meiphoto.common.protocol.bmodel.IEntity;

/**
 * 连接管理类 com.meibeike.meiphoto.common.clientconnect.ClientConnectorManager
 * 
 * @author 缪运锦 <br/>
 *         create at 2015年4月29日 上午10:06:22
 */

@SuppressLint("UseSparseArrays")
public class ClientConnectorManager {

  public Map<String, IClientConnect> clientNettyMap = new HashMap<String, IClientConnect>();
  private static ClientConnectorManager instance = null;

  /** 通讯key 集合key */
  public static final String BASIC_SERVICES_MGR_KEY = "BasicServicesMgr";
  public static final String DOWNLOAD_IMAGE_MGR_KEY = "DownloadImageMgr";
  public static final String DOWNLOAD_MGR_KEY = "DownloadMgr";
  public static final String MEICAST_MGR_KEY = "MeicastMgr";
  public static final String UPLOAD_PHOTO_MGR_KEY = "UploadPhotoMgr";
  public static final String UPLOAD_VIDEO_MGR_KEY = "UploadVideoMgr";
  public static final String UPLOAD_VISTOR_MGR_KEY = "UploadVistorMgr";


  // 而要发送的数据
  public static ClientConnectorManager getInstance() {
    if (instance == null) {
      synchronized (ClientConnectorManager.class) {
        if (instance == null) {
          instance = new ClientConnectorManager();
        }
      }
    }
    return instance;
  }

  @SuppressWarnings("static-access")
  private ClientConnectorManager() {
    clientNettyMap.put(BASIC_SERVICES_MGR_KEY, BasicServicesMgr.getInstance());
 
  }

  /**
   * 选择数据连接 发送数据
   * 
   * @param entity
   */
  public void sendEntity(IEntity entity) {

    sendEntity(entity, BASIC_SERVICES_MGR_KEY);
  }

  public void sendEntity(IEntity entity, String key) {
    if (entity != null) {
      clientNettyMap.get(key).sendEntity(entity);
    }

  }

  /**
   * 统一退出 将所有数据清空
   */
  public void isClose() {
    for (String key : clientNettyMap.keySet()) {
      // 统一退出 将所有数据清空
      clientNettyMap.get(key).isClearMsg();
      clientNettyMap.get(key).isClose();
    }
  }
  /**
   * 按返回键退出允许后台上传
   */
  public void isHomeClose(){
    for (String key : clientNettyMap.keySet()) {
      if(key.equals(UPLOAD_PHOTO_MGR_KEY) || key.equals(UPLOAD_VIDEO_MGR_KEY))
        continue;
      clientNettyMap.get(key).isClearMsg();
      clientNettyMap.get(key).isClose();
    }
  }
  public void isClose(String key) {
    clientNettyMap.get(key).isClose();
  }

  /**
   * 获取一个连接
   * 
   * @param mainFunction
   */
  public IClientConnect getConnect(int mainFunction) {
    return clientNettyMap.get(mainFunction);
  }

  public IClientConnect getMeiCastMgr() {
    if (clientNettyMap != null) {

      return clientNettyMap.get(1000);
    }
    return null;
  }

 
}
