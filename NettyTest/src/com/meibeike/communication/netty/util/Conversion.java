package com.meibeike.communication.netty.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
/**
 * 工具类
 * com.meibeike.mbk_communicationframework.communication.netty.util.Conversion
 * @author 张涢 <br/>
 * create at 2015-9-21 上午11:52:24
 */
public class Conversion {
  private static final String TAG = "Conversion";
  /**
   * 文件转化为字节数组  
   * @param path 文件地址
   * @return
   */
    public static byte[] getBytesFromFile(String path) {   
        if (path == null) {   
            return null;   
        }   
        try {   
          File file = new File(path);
          if (file == null) {
            return null; 
          }
          
            FileInputStream stream = new FileInputStream(file);   
            
            ByteArrayOutputStream out = new ByteArrayOutputStream(1024);   
            byte[] b = new byte[1024];   
            int n;   
            while ((n = stream.read(b)) != -1) {  
                out.write(b, 0, n);   
               }  
            stream.close();   
            out.close();   
            return out.toByteArray();   
        } catch (IOException e) {  
          e.printStackTrace();
        }   
        return null;   
    }   
}
