package com.meibeike.communication.entity;
/**
 *  心跳机制参数
 * com.meibeike.mbk_communicationframework.manager.RefreshHeartbeat
 * @author �ś� <br/>
 * create at 2015-9-21 ����10:43:21
 */
public class RefreshHeartbeatEntity {
  private static final String TAG = "RefreshHeartbeat";
  
  /**
   * 当前的心跳时间
   */
  public int  HEART_INDEX = 0;
  
  /**
   * 当前的请求超时时间
   */
  public int QUEST_INDEX = 0;
  
  /**
   * 心跳时间默认180秒超时
   */
  public static  int  HEART_OUTTIME = 180;
  
  /**
   * 请求超时时间  默认20秒
   */
  public static int QUEST_TIMEOUT = 20;
  
  
  /**
   * 设置超时时间
   * @param heart
   */
  public void setHeartTimeout(int heart){
    this.HEART_OUTTIME = heart;
  }
  
  /**
   * 设置请求超时时间
   * @param timeOUt
   */
  public void setQuestTimeOut(int timeOut){
    this.QUEST_TIMEOUT = timeOut;
  }
}
