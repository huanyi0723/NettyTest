/**
 * 
 */
package com.meibeike.meiphoto.common.protocol.coder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import android.util.Log;


 
public class RequestCoder
{
	private ByteArrayOutputStream bos = new ByteArrayOutputStream();

	public byte[] getData()
	{
		return bos.toByteArray();
	}

	/**
	 * 按字(String)格式编码数据
	 * 
	 * @param value
	 * @param beUnicode
	 */
	public void addString(String value, boolean beUnicode)
	{
	    Log.i("NetJson",String.format("请求：%s", value));
		try
		{
			if (value != null)
			{
				if (beUnicode)
				{
					bos.write(CoderUtils.string2UnicodeBytes(value));
				} else
				{
					bos.write(CoderUtils.stringToBytes(value));
				}
			}
			bos.write(0);
			if (beUnicode)
			{
				bos.write(0);
			}
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 添加有长度限制的String
	 * 
	 * @param value
	 * @param maxLength
	 */
	public void addString(String value, int maxLength)
	{
		byte[] codebuffer = new byte[maxLength];
		byte[] cbytes = CoderUtils.stringToBytes(value);
		System.arraycopy(cbytes, 0, codebuffer, 0, cbytes.length);
		addBuffer(codebuffer);
	}

	/**
	 * 按字(64位long)格式编码数据
	 * 
	 * @param value
	 */
	public void addLong64(long value)
	{
		byte[] buffer = new byte[8];
		CoderUtils.long2Bytes(buffer, 0, value);
		addBuffer(buffer);
	}

	/**
	 * 按字(32位int)格式编码数据
	 * 
	 * @param value
	 */
	public void addInt32(int value)
	{
		byte[] buffer = new byte[4];
		CoderUtils.integer2Bytes(buffer, 0, value);
		addBuffer(buffer);
	}

	/**
	 * 按字(short-16位)格式编码数据
	 * 
	 * @param value
	 */
	public void addShort(short value)
	{
		byte[] buffer = CoderUtils.short2Bytes(value);
		addBuffer(buffer);
	}

	/**
	 * 按字(byte-8位)格式编码数据
	 * 
	 * @param value
	 */
	public void addByte(int oneByte)
	{
		bos.write(oneByte);
	}

	/**
	 * 按字(byte[])格式编码数据
	 * 
	 * @param buffer
	 */
	public void addBuffer(byte[] buffer)
	{
		try
		{
			bos.write(buffer);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

}
