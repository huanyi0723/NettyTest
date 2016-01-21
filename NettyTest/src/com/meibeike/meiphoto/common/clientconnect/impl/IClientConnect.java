package com.meibeike.meiphoto.common.clientconnect.impl;

import com.meibeike.meiphoto.common.clientconnect.PackageHeader;
import com.meibeike.meiphoto.common.protocol.bmodel.IEntity;

public interface IClientConnect {

  public void isConnect(String netType);

  public void sendAgain();

  public void sendMsgFail(String netType ,byte[] msg);

  public void connectFail(String netType);

  /**
   * 根据实体发送数据
   * 
   * @param entity
   */
  public void sendEntity(IEntity entity);

  public void sendByte(byte[] b);

  /**
   * 关闭
   */
  public void isClose();
  /**
   * 清楚当前数据
   * @Description: TODO
   * @param  参数说明 
   * @return void 返回类型
   */
  public void isClearMsg();

  /**
   * @param header
   *          包头
   * @param data
   *          返回数据
   * @param desc
   *          描述
   * @param type
   */
  public void callBack(PackageHeader header, byte[] data, String desc, int type);
  public void callBack(IEntity entity, String desc);
}
