package com.meibeike.meiphoto.common.protocol.bmodel;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

/**
 * 用户登录云棒 1106
 * 
 * com.meibeike.meiphoto.common.protocol.model.Base1106Entity
 * 
 * @author 谢凯 <br/>
 *         create at 2015年1月4日 下午8:56:18
 */
@SuppressWarnings("unused")
public class Base1106Entity extends EntityImpl implements Serializable {

  private static final long serialVersionUID = 4032934690381637841L;

  private static final String TAG = "Base1106Entity";

  public Base1106Entity() {
    super(1100, 1106, 0);
  }

  private long meiid;// 美贝壳内部id
  private String userid;// 用户id
  private String username;// 用户姓名
  private String password;// 用户密码
  private int accounttype;// 账号类型 ____________ 参见数据字典之账号类型
  private int devicetype;// 设备类型 ____________ 参见数据字典之终端设备类型
  private String deviceid;// 设备id ____________ 终端设备硬件唯一识别码

  private int code;// 返回码
  private String message;// 返回消息
  private String sessionkey;// sessionkey ____________ 若登录成功，会返回此sessionkey
  private String mainsn;

  // 编码发送
  public String onEncode() {

    JSONObject json = new JSONObject();
    try {
      json.put("meiid", getMeiid());
      json.put("userid", getUserid());
      json.put("username", getUsername());
      json.put("password", getPassword());
      json.put("accounttype", getAccounttype());
      json.put("devicetype", getDevicetype());
      json.put("deviceid", getDeviceid());
    } catch (Exception e) {
      Log.i("mbk",e.getMessage());
    }
   

   Log.i("mbk",getFunction() + "---" + json.toString());

    return json.toString();
  }

  // 解码接收
  public void onDecode(String temp) {

    Log.i("mbk", getFunction() + "---" + temp);

    try {
      JSONObject json = null;
      if (temp!=null && temp.equals("")) {
        json = new JSONObject(temp);

        setCode(json.getInt("code"));
        setMessage(json.getString("message"));
        setSessionkey(json.getString("sessionkey"));
        setMainsn(json.getString("mainsn"));

      }
    } catch (JSONException e) {
      Log.e(TAG, "", e);
    }

  }

  public String getMainsn() {
    return this.mainsn;
  }

  public void setMainsn(String mainsn) {
    this.mainsn = mainsn;
  }

  public long getMeiid() {
    return this.meiid;
  }

  public void setMeiid(long meiid) {
    this.meiid = meiid;
  }

  public String getUserid() {
    return this.userid;
  }

  public void setUserid(String userid) {
    this.userid = userid;
  }

  public String getUsername() {
    return this.username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return this.password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public int getAccounttype() {
    return this.accounttype;
  }

  public void setAccounttype(int accounttype) {
    this.accounttype = accounttype;
  }

  public int getDevicetype() {
    return this.devicetype;
  }

  public void setDevicetype(int devicetype) {
    this.devicetype = devicetype;
  }

  public String getDeviceid() {
    return this.deviceid;
  }

  public void setDeviceid(String deviceid) {
    this.deviceid = deviceid;
  }

  public int getCode() {
    return this.code;
  }

  public void setCode(int code) {
    this.code = code;
  }

  public String getMessage() {
    return this.message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getSessionkey() {
    return this.sessionkey;
  }

  public void setSessionkey(String sessionkey) {
    this.sessionkey = sessionkey;
  }

  @Override
  public String toString() {
    return "Base1106Entity [meiid=" + meiid + ", userid=" + userid + ", username=" + username + ", password=" + password + ", accounttype=" + accounttype + ", devicetype="
        + devicetype + ", deviceid=" + deviceid + ", code=" + code + ", message=" + message + ", sessionkey=" + sessionkey + ", mainsn=" + mainsn + "]";
  }

}
