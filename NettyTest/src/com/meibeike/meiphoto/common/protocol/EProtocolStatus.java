/**
 * 
 */
package com.meibeike.meiphoto.common.protocol;

/**
 * @author duminghui
 * 
 */
public enum EProtocolStatus
{
	/**
	 * 等待处理
	 */
	wait,
	/**
	 * 成功
	 */
	success,
	/**
	 * 服务器出错
	 */
	serverError,
	/**
	 * 解析服务器返回的数据出错
	 */
	parseError;
}
