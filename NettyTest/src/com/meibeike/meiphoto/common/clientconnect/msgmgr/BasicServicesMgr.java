package com.meibeike.meiphoto.common.clientconnect.msgmgr;

import com.meibeike.meiphoto.common.clientconnect.ClientConnectorManager;
import com.meibeike.meiphoto.common.clientconnect.ClientConstants;
import com.meibeike.meiphoto.common.protocol.bmodel.IEntity;

public class BasicServicesMgr extends BaseClientMgr {

  public static BasicServicesMgr instance = null;

  public static BasicServicesMgr getInstance() {
    if (instance == null) {
      instance = new BasicServicesMgr();
    }
    return instance;
  }

  private BasicServicesMgr() {
    super( "192.168.43.1", 9223, ClientConnectorManager.BASIC_SERVICES_MGR_KEY);
  }

  /**
   * 接收需要发送的实体
   * 
   * @param entity
   */
  @Override
  public void sendEntity(IEntity entity) {
    if (entity != null) {
      
      // 请求列表每次最多保存两个请求
      if (mEntityMsg != null && mEntityMsg.size() == 2) {
        mEntityMsg.remove(1);
      }
      mEntityMsg.add(entity);
      if (!isSending) {
        // 启动一个发送
        isSending = true;
        basicHandler.sendEmptyMessage(ClientConstants.REQUEST);
      }
      
    }
  }
}
