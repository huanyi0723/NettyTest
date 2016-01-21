/**
 * 
 */
package com.meibeike.meiphoto.common.protocol;

/**
 * 服务端异常，业务协议中服务器所下发的错误信息及错误码
 * @author duminghui
 * 修改记录：
 * 2013.12.24 祝丰华，增加错误码 errorNo
 */
public class ServerException extends Exception
{
    /**
     * 错误码
     */
    private int errorCode;

	/**
     * 
     */
	private static final long serialVersionUID = 8576511820957729948L;

	/**
	 * @param message
	 */
	public ServerException(String message)
	{
		super(message);
		this.errorCode = 0;
	}
	
	public ServerException(int errorNo,String message){
	    super(message);
	    this.errorCode = errorNo;
	    
	}
	
	public int getErrorCode(){
	    return errorCode;
	}

}
