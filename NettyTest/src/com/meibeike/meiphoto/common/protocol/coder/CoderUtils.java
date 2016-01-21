/**
 * 
 */
package com.meibeike.meiphoto.common.protocol.coder;

/**
 * @author duminghui
 * 
 */
public class CoderUtils
{

    /**
     * 从字节数组中指定的位置读取一个Long类型的数据。 低位在前
     * 
     * @param b
     *            字节数组
     * @param pos
     *            指定的开始位置
     * @return 一个Long类型的数据
     */
    public static long bytes2Long(byte[] b, int pos)
    {
        long val = 0;
        val = b[pos + 7] & 0xff; // 多字节数据，低8位在前面的字节。
        val <<= 8;
        val |= b[pos + 6] & 0xff;
        val <<= 8;
        val |= b[pos + 5] & 0xff;
        val <<= 8;
        val |= b[pos + 4] & 0xff;
        val <<= 8;
        val |= b[pos + 3] & 0xff;
        val <<= 8;
        val |= b[pos + 2] & 0xff;
        val <<= 8;
        val |= b[pos + 1] & 0xff;
        val <<= 8;
        val |= b[pos] & 0xff;
        return val;
    }

    /**
     * 从字节数组中指定的位置读取一个Integer类型的数据。
     * 
     * @param b
     *            字节数组
     * @param pos
     *            指定的开始位置
     * @return 一个Integer类型的数据
     */
    public static int bytes2Integer(byte[] b, int pos)
    {
        int val = 0;
        val = b[pos + 3] & 0xff; // 多字节数据，低8位在前面的字节。
        val <<= 8;
        val |= b[pos + 2] & 0xff;
        val <<= 8;
        val |= b[pos + 1] & 0xff;
        val <<= 8;
        val |= b[pos] & 0xff;
        return val;
    }

    /**
     * 从字节数组中指定的位置读取一个Short类型的数据。
     * 
     * @param b
     *            字节数组
     * @param pos
     *            指定的开始位置
     * @return 一个Short类型的数据
     */
    public static short bytes2Short(byte[] b, int pos)
    {
        int val = 0;
        val = b[pos + 1] & 0xFF; // 多字节数据，低8位在前面的字节。
        val = val << 8;
        val |= b[pos] & 0xFF;
        return (short) val;
    }

    /**
     * 从字节数组中指定的位置读取一个String类型的数据。
     * 
     * @param b
     *            字节数组
     * @param pos
     *            指定的开始位置
     * @param len
     *            指定的字节长度
     * @return 一个String类型的数据
     */
    public static String bytes2StringZ(byte[] b, int pos, int len)
    {
        int strLen = b.length - pos;
        if (strLen <= 0)
        {
            return "";
        }
        int i = 0;
        int rlen = len < strLen ? len : strLen;
        for (; i < rlen; i++)
        {
            if (b[pos + i] == 0)
            {
                break;
            }
        }
        return bytesToString(b, pos, i);
    }

    /**
     * 从字节数组中指定的位置获得一个String类型数据的长度。[以\0结尾]
     * 
     * @param b
     *            字节数组
     * @param pos
     *            指定的开始位置
     * @return 一个String类型数据的长度
     */
    public static int bytes2StringZlen(byte[] b, int pos)
    {
        int i = pos;
        for (; i < b.length; i++)
        {
            if (b[i] == 0)
            {
                break;
            }
        }
        return (i - pos + 1);
    }

    public static int bytes2Stringlen(byte[] b, int pos)
    {
        int i = pos;
        for (; i < b.length; i += 2)
        {
            if (b[i] == 0 && b[i + 1] == 0)
            {
                break;
            }
        }
        return (i - pos + 1);
    }

    /**
     * 转换一个二进制数组为字符串
     * 
     * @param bytes
     *            需要转换的二进制数组
     * @return 转换后的字符串
     */
    public static String bytesToString(byte[] bytes)
    {
        return bytesToString(bytes, 0, bytes.length);
    }

    /**
     * 转换一个二进制数组中指定长度数据为字符串
     * 
     * @param bytes
     *            需要转换的二进制数组
     * @param off
     *            开始转换的起始位置
     * @param len
     *            转换的长度
     * @return 转换后的字符串
     */
    public static String bytesToString(byte[] bytes, int off, int len)
    {
        // System.out.println(System.getProperty("microedition.encoding"));
        String ret = null;
        if (bytes != null)
        {
            // if(Char_Enc_Type%2==0){
            // if (Char_Enc_Type==2||Char_Enc_Type==0) {
            // try { //首先我们强制转换成ISO8859-1方式
            // ret = new String(bytes, off, len, Char_Enc0);
            // Char_Enc_Type = 2;
            // } catch (Exception e) {}
            // }
            // if (ret == null) {
            // try { //再次尝试强制转换成ISO8859_1方式
            // ret = new String(bytes, off, len, Char_Enc1);
            // Char_Enc_Type=4;
            // } catch (Exception e) {}
            // }
            // }
            // if (ret == null) { //否则使用缺省编码方式
            ret = new String(bytes, off, len);
            Char_Enc_Type = 1;
            // }

        }
        return ret;
    }

    public static int Char_Enc_Type;

    /**
     * 从字节数组中指定的位置读取一个String类型的数据。 字节数据必须是已经编码为Unicode码的内容。
     * 
     * @param b
     *            字节数组
     * @param pos
     *            指定的开始位置
     * @param len
     *            指定的字节长度
     * @return 一个String类型的数据
     */
    public static String bytes2String(byte[] b, int pos, int len)
    {
        int i = 0;
        if (len > b.length)
        {
            i = b.length / 2;
        } else
        {
            i = len / 2;
        }
        char[] cs = new char[i];
        int j;
        for (j = 0; j < i; j++)
        {
            cs[j] = (char) (b[pos + 2 * j + 1] & 0xFF);
            cs[j] <<= 8;
            char c = (char) (b[pos + 2 * j] & 0xFF);
            cs[j] += c;
            if (cs[j] == (char) 0)
            {
                break;
            }
        }
        return (new String(cs, 0, j)).trim();
    }

    /**
     * 将一个short 类型的数据转换为字节并存入到指定的字节数组指定的位置。
     * 
     * @param b
     *            字节数组
     * @param pos
     *            指定的开始位置
     * @param val
     *            short类型的数据。
     */
    public static void short2Bytes(byte[] b, int pos, short val)
    {
        b[pos + 1] = (byte) (val >>> 8 & 0xff);
        b[pos] = (byte) (val & 0xff);
    }

    public static byte[] short2Bytes(short val)
    {
        byte[] b = new byte[2];
        b[1] = (byte) (val >>> 8 & 0xff);
        b[0] = (byte) (val & 0xff);
        return b;
    }

    /**
     * 将一个short 类型的数据转换为字节并存入到指定的字节数组指定的位置。高位在前
     * 
     * @param b
     *            字节数组
     * @param pos
     *            指定的开始位置
     * @param val
     *            short类型的数据。
     */
    public static void shortToBytes(byte[] b, int pos, short val)
    {

        for (int i = 0; i < 2; i++)
        {
            b[pos + i] = (byte) (val >>> (8 - i * 8));
        }
    }

    /**
     * 将一个long类型的数据转换为字节并存入到指定的字节数组指定的位置，低位在前。
     * 
     * @param b
     * @param pos
     * @param val
     */
    public static void long2Bytes(byte[] b, int pos, long val)
    {
        int len = 8;
        for (int i = 0; i < len; i++)
        {
            b[pos + i] = (byte) (val >>> i * 8 & 0xff);
        }
    }

    /**
     * 将一个int类型的数据转换为字节并存入到指定的字节数组指定的位置,低位在前
     * 
     * @param b
     *            字节数组
     * @param pos
     *            指定的开始位置
     * @param val
     *            int类型的数据。
     */
    public static void integer2Bytes(byte[] b, int pos, int val)
    {
        b[pos + 3] = (byte) (val >>> 24 & 0xff); // 低位在前
        b[pos + 2] = (byte) (val >>> 16 & 0xff);
        b[pos + 1] = (byte) (val >>> 8 & 0xff);
        b[pos] = (byte) (val & 0xff);
    }

    /**
     * 将一个int类型的数据转换为字节并存入到指定的字节数组指定的位置,高位在前
     * 
     * @param b
     * @param pos
     * @param val
     */
    public static void integerToBytes(byte[] b, int pos, int val)
    {
        for (int i = 0; i < 4; i++)
        {
            b[pos + i] = (byte) (val >>> (24 - i * 8));
        }

    }

    /**
     * 将一个int类型的数据转换为字节并存入到指定的字节数组指定的位置,高位在前
     * 
     * @param val
     *            int
     * @return byte[]
     */
    public static byte[] integerToBytes(int val)
    {
        byte[] b = new byte[4];
        for (int i = 0; i < 4; i++)
        {
            // b[i] = (byte) (val >>> (24 - i * 8));
            b[i] = (byte) ((val >> (i * 8)) & 0xff);
        }

        return b;
    }

    /**
     * 将一个String类型的数据转换为字节并存入到指定的字节数组指定的位置。
     * 
     * @param b
     *            字节数组
     * @param pos
     *            指定的开始位置
     * @param val
     *            String类型的数据。
     */
    // public static void string2Bytes(byte[] b, int pos, String s) {
    // int strLen = s.length() * 2;
    // if (strLen < 1) {
    // return;
    // }
    // char c;
    // for (int j = 0; j < s.length(); j++) {
    // c = s.charAt(j);
    // b[pos + j * 2 + 1] = (byte) (c >>> 8 & 0xff);
    // b[pos + j * 2] = (byte) (c & 0xff);
    // }
    // }
    public static byte[] string2UnicodeBytes(String s)
    {
        if (s == null)
            return null;
        int strLen = s.length() * 2;
        byte[] buffer = new byte[strLen];
        char c;
        for (int j = 0; j < s.length(); j++)
        {
            c = s.charAt(j);
            buffer[j * 2 + 1] = (byte) (c >>> 8 & 0xff);
            buffer[j * 2] = (byte) (c & 0xff);
        }
        return buffer;
    }

    /**
     * 转换一个字符串为一个二进制字节数组
     * 
     * @param s
     *            需要转换的字符串
     * @return 转换后的二进制字节数组
     */
    public static byte[] stringToBytes(String s)
    {
        return s == null ? new byte[] {} : s.getBytes();
    }
}
