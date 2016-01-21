package com.meibeike.communication.entity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.meibeike.communication.manager.CommunicationThreadManager;

/**
 * 通讯集合
 * com.meibeike.communication.entity.CommunicationList
 * @author 张涢 <br/>
 * create at 2015-9-21 下午7:08:25
 */
public class CommunicationCollection {
  private static final String TAG = "CommunicationCollection";
  /**
   * 通讯集合HashMap
   */
  private static HashMap<String, CommunicationThreadManager> mCommunicationCollection = new HashMap<String, CommunicationThreadManager>();
  
  /**
   * 获取通讯集合 
   * @return
   */
  public static HashMap<String, CommunicationThreadManager> getCollection(){
    if (mCommunicationCollection != null) {
      return mCommunicationCollection;
    }
    return null;
  }
  
  /**
   * 添加key val 进通讯集合
   * @param key
   * @param mCommunicationThreadManager
   */
  public static void addManager(String key, CommunicationThreadManager mCommunicationThreadManager){
    if (key != null&& mCommunicationThreadManager != null) {
      if (mCommunicationCollection.containsKey(key)) {
        mCommunicationCollection.remove(key);
      }
      mCommunicationCollection.put(key, mCommunicationThreadManager);
    }
    
  }
  
  /**
   * 根据key获取通讯管理器
   * @param key
   * @return
   */
  public static CommunicationThreadManager getManager(String key){
    if (mCommunicationCollection.containsKey(key)) {
      return mCommunicationCollection.get(key);
    }
    return null;
  }
  
  /**
   * 从集合中移除管理器
   * @param key
   */
  public static void removeManager(String key){
    if (mCommunicationCollection == null || key == null|| key.equals("")) {
      return;
    }
    if (mCommunicationCollection.containsKey(key)) {
      CommunicationThreadManager manager = mCommunicationCollection.get(key);
      if (manager != null) {
        mCommunicationCollection.remove(key);
        manager.closeTheadManager();
      }
    }
  }
  
  /**
   * 清空通讯连接， 释放资源
   */
  public static void clearCollection() {
    if (mCommunicationCollection != null) {
      Iterator iter = mCommunicationCollection.entrySet().iterator();
      Map.Entry entry = null;
      CommunicationThreadManager value = null;
      while (iter.hasNext()) {
        entry = (Map.Entry) iter.next();
        value = (CommunicationThreadManager) entry.getValue();
        value.closeTheadManager();
        value = null;
        entry = null;
      }
    }
  }

  
}
