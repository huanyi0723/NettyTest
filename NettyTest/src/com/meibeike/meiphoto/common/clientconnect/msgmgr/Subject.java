package com.meibeike.meiphoto.common.clientconnect.msgmgr;

import java.util.ArrayList;
import java.util.List;

import android.os.Handler;
import android.os.Message;

import com.example.nettytest.MeiApp;
import com.meibeike.meiphoto.common.protocol.bmodel.IEntity;

public abstract class Subject {
  /**
   * 用来保存注册的观察者对象
   */
  private List<Observer> list = new ArrayList<Observer>();

  private Handler subHandler = new Handler(MeiApp.mContext.getMainLooper()) {
    public void handleMessage(Message msg) {
      if (list != null && list.size() > 0) {
        for (int i = 0; i < list.size(); i++) {
          list.get(i).update((IEntity) msg.obj);
        }
      }
    }
  };

  /**
   * 注册观察者对象
   * 
   * @param observer
   *          观察者对象
   */
  public void attach(Observer observer) {
    if (list != null) {
      list.add(observer);
    }
  }

  /**
   * 删除观察者对象
   * 
   * @param observer
   *          观察者对象
   */
  public void detach(Observer observer) {
    if (list != null && list.size() > 0 && observer != null) {
      list.remove(observer);

    }
  }

  /**
   * 删除观察者对象
   * 
   * @param observer
   *          观察者对象
   */
  public void clear() {

    if (list != null && list.size() > 0) {
      list.clear();
    }

  }

  /**
   * 通知所有注册的观察者对象
   */
  public void nodifyObservers(final IEntity newState) {

    new Thread(new Runnable() {

      @Override
      public void run() {
        Message msg = Message.obtain();
        msg.obj = newState;
        subHandler.sendMessage(msg);

      }
    }).start();

  }
}
