package com.meibeike.meiphoto.common.clientconnect;

import org.json.JSONException;
import org.json.JSONObject;


public class UdpEntity {

  /** 美贝壳会员id，若为0，则获取所有已绑定会员列表 */

  private long meiid;

  /** 云棒内网ssid */
  private String ssid;
  /** 云棒名称 */
  private String yunbangname;
  /** 云棒母棒sn */
  private String yunbangsn;
  /** 云棒分辨率高度 */
  private int yunbangwidth;
  /** 云棒分辨率高度 */
  private int yunbangheight;

  private String yunbangIp;

  private int yunbangPort;

  private int connectType = 0;// 当前网络连接类型

  private boolean isWifi = false;
  public boolean isSelected = false; //绑定列表中是否选中 默认不选中
  public int getConnectType() {
    return this.connectType;
  }

  public void setConnectType(int connectType) {
    this.connectType = connectType;
  }

  public String getYunbangIp() {
    return this.yunbangIp;
  }

  public void setYunbangIp(String yunbangIp) {
    this.yunbangIp = yunbangIp;
  }

  public int getYunbangPort() {
    return this.yunbangPort;
  }

  public void setYunbangPort(int yunbangPort) {
    this.yunbangPort = yunbangPort;
  }

  public UdpEntity() {
  }

  public UdpEntity(long meiid) {
    this.meiid = meiid;
  }

  public UdpEntity(String temp) {
    onDecode(temp);
  }

  /**
   * 拼装请求json并且放到map当中
   * 
   * @return
   */
  public String onEncode() {

    JSONObject json = new JSONObject();
    try {
      json.put("meiid", getMeiid());
    } catch (JSONException e) {
      
    }

    return json.toString();

  }

  /**
   * 获取请求返回json字符串 并且解析 将数据添加到实体类当中
   * 
   * @param temp
   */
  public void onDecode(String temp) {

    try {
      JSONObject json = null;
      if (!temp.equals("")) {
        json = new JSONObject(temp);

        /** 云棒内网ssid */
        setSsid(json.getString("ssid"));
        /** 云棒名称 */
        setYunbangname(json.getString("yunbangname"));
        /** 云棒母棒sn */
        setYunbangsn(json.getString("yunbangsn"));
        /** 云棒分辨率高度 */
        setYunbangwidth(json.getInt("yunbangwidth"));
        /** 云棒分辨率高度 */
        setYunbangheight(json.getInt("yunbangheight"));

      }
    } catch (JSONException e) {

    }

  }

  public long getMeiid() {
    return this.meiid;
  }

  public void setMeiid(long meiid) {
    this.meiid = meiid;
  }

  public String getSsid() {
    return this.ssid;
  }

  public void setSsid(String ssid) {
    this.ssid = ssid;
  }

  public String getYunbangname() {
    return this.yunbangname;
  }

  public void setYunbangname(String yunbangname) {
    this.yunbangname = yunbangname;
  }

  public String getYunbangsn() {
    return this.yunbangsn;
  }

  public void setYunbangsn(String yunbangsn) {
    this.yunbangsn = yunbangsn;
  }

  public int getYunbangwidth() {
    return this.yunbangwidth;
  }

  public void setYunbangwidth(int yunbangwidth) {
    this.yunbangwidth = yunbangwidth;
  }

  public int getYunbangheight() {
    return this.yunbangheight;
  }

  public void setYunbangheight(int yunbangheight) {
    this.yunbangheight = yunbangheight;
  }

  public boolean isWifi() {
    return this.isWifi;
  }

  public void setWifi(boolean isWifi) {
    this.isWifi = isWifi;
  }

  public boolean equals(Object obj) {
    if (this == obj) {// 如果是引用同一个实例
      return true;
    }
    if (obj != null && obj instanceof UdpEntity) {
      UdpEntity u = (UdpEntity) obj;
      return this.yunbangsn.equals(u.yunbangsn) && this.yunbangIp.equals(u.yunbangIp);
    } else {
      return false;
    }
  }
}
