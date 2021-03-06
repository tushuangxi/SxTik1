package com.tushuangxi.smart.tv.library.nettyclient.netty;

/**

 * @describe
 */
public class Config {

    //是否允许重连
    public static boolean TCP_CONN_AGAIN = true;
    /**
     * Flag
     * 1   表示连接成功
     * 2   表示正常接受服务器端数据
     * 3   表示Tcp 连接异常的消息提示
     * 4   表示手动关闭Socket
     * 5   超时提醒自动重连
     */
    public static int TCP_CONN_SUCCESS = 1;
    public static int TCP_RECEIVE = 2;
    public static int TCP_CONN_Exception_MSG = 3;
    public static int TCP_MANUAL_CLOSE = 4;

    public static int TCP_RECONN = 5;

    //心跳连接次数
    public static int COUNT = 0;
    //是否是否通过验证
    public static boolean IS_VERIFY = false;
    //是否加密
    public static boolean IsEncrypt = false;


}
