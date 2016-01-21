package com.example.nettytest;

import com.meibeike.meiphoto.common.clientconnect.ClientConnectFactory;
import com.meibeike.meiphoto.common.clientconnect.ClientConnectService;
import com.meibeike.meiphoto.common.protocol.bmodel.Base1106Entity;
import com.meibeike.meiphoto.common.protocol.bmodel.IEntity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {
  
  private Base1106Entity entity1106;// 登录云棒协议
  
  public static final int RESPONSE_SUCCESS = 0x401;
  public static final int RESPONSE_FAIL = 0x402;
  public static final int RESPONSE_TIMEOUT = 0x403;
  public static final int REQUEST_HEARTBEAT_TIMEOUT = 0x410; //心跳超时
  public static final int  NOT_LOGIN= 0x411; //用户未登录
  
  public Handler mHandler = new Handler() {

    @Override
    public void handleMessage(Message msg) {
      super.handleMessage(msg);
      switch (msg.what) {
        case  RESPONSE_SUCCESS:
          IEntity entity = (IEntity) msg.obj;
          if (entity != null) {
            responseSuccess((IEntity) msg.obj);
          } else {
            responseFail(-1, "返回数据为空！");
          }
          break;
        case   RESPONSE_FAIL:// 请求失败
          if (msg != null && msg.obj != null)
            responseFail(-10001, (String) msg.obj);
          break;
        case   RESPONSE_TIMEOUT:// 请求超时
          if (msg != null && msg.obj != null)
            responseFail(-10000, (String) msg.obj);
          break;
        case   NOT_LOGIN:// 用户未登录
          if (msg != null && msg.obj != null)
            responseFail(-10002, (String) msg.obj);
          break;
      }
    }
  };
  

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    
    Button login = (Button)findViewById(R.id.login);
    login.setOnClickListener(new View.OnClickListener() {
      
      @Override
      public void onClick(View v) {
        reqEntity1106();
      }
    });
    
    
  }

  
  public void reqEntity1106() {
    entity1106 = new Base1106Entity();
    entity1106.setMeiid(1000217);
    entity1106.setUserid("mm910@mbk.com");
    entity1106.setUsername("");
    entity1106.setPassword("e10adc3949ba59abbe56e057f20f883e");
    entity1106.setAccounttype( 0 );
    entity1106.setDevicetype(3);
    entity1106.setDeviceid("864376025909275");
    entity1106.setHandler(mHandler);
    ClientConnectFactory.getInstance().sendEntity(entity1106);
  }
  
  public void responseSuccess(IEntity entity) {
    Toast.makeText(MainActivity.this,  ((Base1106Entity)entity).toString(), Toast.LENGTH_LONG).show();
  }
  
  public void responseFail(int code, String msg) {
    Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
  }
  
  
}
