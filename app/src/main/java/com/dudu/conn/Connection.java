package com.dudu.conn;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.dudu.android.launcher.utils.Encrypt;

import org.apache.mina.core.future.CloseFuture;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

public class Connection extends Thread {
    //    private String host = "192.168.124.177";
    private String host = "119.29.65.127";
    private int port = 8888;
    private IoConnector connector = null;
    private IoSession session = null;
    private boolean isSessionOpen = false;
    private PersistentStorage mPersistentStorage;
    private boolean isHasData = false;
    private boolean mIsNetworkOK = true;
    private List<String> important_msgList;
    private static Connection mConnection;
    private Logger log;
    private ConnectionResultHandler resultHandler;

    public Connection(Context context) {
        mPersistentStorage = PersistentStorage.getInstance(context);
        important_msgList = new ArrayList<>();
        log = LoggerFactory.getLogger("net.conn.mina");
        resultHandler = new ConnectionResultHandler();
        resultHandler.init();
    }

    public static Connection getInstance(Context context) {
        if (mConnection == null)
            mConnection = new Connection(context);
        return mConnection;
    }

    public void startConn() {
        log.debug("Connection 开始连接");
        connector = new NioSocketConnector();
        // 设置链接超时时间
        connector.setConnectTimeoutMillis(30 * 1000);
        // 添加过滤器
        TextLineCodecFactory tlcf = new TextLineCodecFactory(Charset.forName("UTF-8"));
        connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(tlcf));
        // 添加业务逻辑处理器类
        connector.setHandler(new MinaClientHandler());
        ConnectFuture future = connector.connect(new InetSocketAddress(host, port));// 创建连接
        future.awaitUninterruptibly(); // 等待连接创建完成
        try {
            session = future.getSession();
        } catch (Exception e) {
            log.warn("{}", e);
        }
    }

    // 网络状态正常
    public void onNetWorkOK() {
        mIsNetworkOK = true;
    }

    // 网络断开
    public void onNONetWork() {
        mIsNetworkOK = false;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }


    @Override
    public void run() {
        try {
            while (mIsNetworkOK) {

                if (!isSessionOpen) {
                    startConn();
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {

                    }
                }
                checkCache();
                Thread.sleep(5000);
            }
        } catch (Exception e) {
            isSessionOpen = false;
            log.error("Connection ", e);
        }

    }

    public void sendMessage(String msg, boolean needCache) {
        if (needCache) {
            synchronized (mPersistentStorage) {
                mPersistentStorage.addTail(msg);
                log.debug("添加发送消息队列:{}", mPersistentStorage.getCount());
            }
        }
    }

    /**
     * 直接发送，不缓存
     *
     * @param msg
     */
    public void sendMessage(String msg) {
        try {
            if (!TextUtils.isEmpty(msg) && session != null)
                session.write(msg);
        } catch (Exception e) {

        }
    }

    // 连接后检查是否有缓存数据，如果有，则发送
    private void checkCache() {
        if (isSessionOpen) {

            synchronized (important_msgList) {
                synchronized (mPersistentStorage) {
                    if (mPersistentStorage != null && mPersistentStorage.getCount() > 0) {
                        isHasData = true;
                        for (int i = 0; i < mPersistentStorage.getCount(); i++) {
                            if (!important_msgList.contains(mPersistentStorage.getAll().get(i))) {
                                important_msgList.add(mPersistentStorage.getAll().get(i));
                            }
                        }
                    }
                    if (important_msgList.size() > 0) {
                        if (!TextUtils.isEmpty(important_msgList.get(0)) && !important_msgList.get(0).equals("null")) {
                            session.write(important_msgList.get(0));
                        }
                        important_msgList.remove(0);
                    }
                }
            }
        }

    }

    /**
     * 关闭连接
     *
     * @return
     */
    public boolean closeConn() {
        if (session != null) {
            CloseFuture future = session.getCloseFuture();
            future.awaitUninterruptibly(1000);
            connector.dispose();
        }
        if (!important_msgList.isEmpty())
            important_msgList.clear();
        return true;
    }

    /**
     * 连接处理内部类
     */
    class MinaClientHandler extends IoHandlerAdapter {
        @Override
        public void exceptionCaught(IoSession session, Throwable cause) {
            isSessionOpen = false;
            log.debug("客户端连接异常" + cause);
            try {
                super.exceptionCaught(session, cause);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void inputClosed(IoSession session) {
        }

        @Override
        public void messageReceived(IoSession session, Object message) {
            String msg = message.toString();
            try {
                JSONObject jsonResult = new JSONObject(msg);
                if(jsonResult.has("resultCode")&&jsonResult.get("resultCode").equals("200")){
                    if (isHasData)
                        mPersistentStorage.deleteHeader();
                    log.debug("发送消息队列成功[{}]", mPersistentStorage.getCount());
                }else{
                    log.debug("received message{}", msg);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            EventBus.getDefault().post(new ConnectionEvent.ReceivedMessage(msg));

        }

        @Override
        public void messageSent(IoSession session, Object message) {

            try {
                log.debug("send message{}", Encrypt.AESDecrypt(message.toString(), Encrypt.vi));
                super.messageSent(session, message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void sessionClosed(IoSession session) {
            log.debug("客户端会话关闭");
            isSessionOpen = false;
            EventBus.getDefault().post(ConnectionEvent.SessionStateChange.SESSION_CLOSED);
        }

        @Override
        public void sessionCreated(IoSession session) {
            log.debug("客户端会话创建");

            EventBus.getDefault().post(ConnectionEvent.SessionStateChange.SESSION_CREATE);

        }

        @Override
        public void sessionIdle(IoSession session, IdleStatus idlestatus) {
            log.debug("客户端会话休眠");
            isSessionOpen = false;
            EventBus.getDefault().post(ConnectionEvent.SessionStateChange.SESSION_IDLE);

        }

        @Override
        public void sessionOpened(IoSession session) {
            log.debug("客户端会话打开");
            isSessionOpen = true;
            EventBus.getDefault().post(ConnectionEvent.SessionStateChange.SESSION_OPEND);

        }
    }

    public Logger getlog() {
        return log;
    }
}
