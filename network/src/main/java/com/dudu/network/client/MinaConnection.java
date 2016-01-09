package com.dudu.network.client;

import android.text.TextUtils;

import com.dudu.network.interfaces.IConnectCallBack;
import com.dudu.network.interfaces.IConnection;
import com.dudu.network.utils.DuduLog;
import com.dudu.network.valueobject.ConnectionParam;
import com.dudu.network.valueobject.ConnectionState;

import org.apache.mina.core.future.CloseFuture;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.firewall.ConnectionThrottleFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;


/**
 * Created by dengjun on 2015/11/27.
 * Description :
 */
public class MinaConnection extends IoHandlerAdapter implements IConnection{

    private boolean isConnected = false;
    private IoConnector connector = null;
    private IoSession session = null;
    private IConnectCallBack iConnectCallBack = null;

    private Logger log;



    public MinaConnection() {
        log = LoggerFactory.getLogger("network");
    }

    @Override
    public boolean isConnected() {
        return isConnected;
    }

    @Override
    public void connect(ConnectionParam connectionParam) {
        try{
            connector = new NioSocketConnector();
            connector.setConnectTimeoutMillis(connectionParam.getConnectTimeout());// 设置链接超时时间

            // 添加过滤器
            TextLineCodecFactory tlcf = new TextLineCodecFactory(Charset.forName("UTF-8"));
            tlcf.setDecoderMaxLineLength(20480);
            tlcf.setEncoderMaxLineLength(20480);
            connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(tlcf));

            // 添加业务逻辑处理器类
            connector.setHandler(this);
            ConnectFuture future = connector.connect(new InetSocketAddress(connectionParam.getHost(), connectionParam.getPort()));// 创建连接
            log.info("开始连接网络："+ "IP："+connectionParam.getHost()+"  Port："+connectionParam.getPort());
            //如果不等待 网络连接过程异步
            future.awaitUninterruptibly(); // 等待连接创建完成
            session = future.getSession();

//            future.addListener(new ConnectListener());

        }catch(Exception e){
            log.warn("连接异常", e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void disConnect() {
        isConnected = false;
        if (session != null) {
            CloseFuture future = session.getCloseFuture();
            future.awaitUninterruptibly(1000);
            connector.dispose();
        }
    }

    @Override
    public void setConnectCallBack(IConnectCallBack iConnectCallBack) {
        this.iConnectCallBack = iConnectCallBack;
    }

    @Override
    public void sendMessage(String sendMessage) {
        try {
            if(!TextUtils.isEmpty(sendMessage)&&session!=null)
                session.write(sendMessage);
        }catch (Exception e){
            log.error("异常："+ e);
        }
    }

    @Override
    public void sessionCreated(IoSession session) throws Exception {
        log.debug("客户端会话创建");//当有新的连接建立的时候，该方法被调用。
        if (iConnectCallBack != null){
            iConnectCallBack.onConnectionState(new ConnectionState(ConnectionState.CONNECTION_CREATE));
        }
    }

    @Override
    public void sessionOpened(IoSession session) throws Exception {
        log.debug("客户端会话打开");//当有新的连接打开的时候，该方法被调用。该方法在 sessionCreated之后被调用。
        isConnected = true;
        this.session = session;
        if (iConnectCallBack != null){
            iConnectCallBack.onConnectionState(new ConnectionState(ConnectionState.CONNECTION_SUCCESS));
        }
    }

    @Override  //当连接被关闭的时候，此方法被调用。
    public void sessionClosed(IoSession session) throws Exception {
        log.debug("客户端会话关闭");
        isConnected = false;
        if (iConnectCallBack != null){
            iConnectCallBack.onConnectionState(new ConnectionState(ConnectionState.CONNECTION_FAIL));
        }
    }

    @Override  //默认情况下，闲置时间设置是禁用的，可以通过 IoSessionConfig.setIdleTime(IdleStatus, int) 来进行设置
    public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
        log.debug("客户端会话休眠");
        isConnected = false;
        if (iConnectCallBack != null){
            iConnectCallBack.onConnectionState(new ConnectionState(ConnectionState.CONNECTION_IDLE));
        }
    }

    @Override  //当 I/O 处理器的实现或是 Apache MINA 中有异常抛出的时候，此方法被调用。
    public void exceptionCaught(IoSession session, Throwable cause) {
        log.error("客户端连接异常",cause);
        isConnected = false;
        if (iConnectCallBack != null){
            iConnectCallBack.onConnectionState(new ConnectionState(ConnectionState.CONNECTION_FAIL));
        }
    }

    @Override  // 	当接收到新的消息的时候，此方法被调用
    public void messageReceived(IoSession session, Object message) throws Exception {
        String msg = message.toString();
        log.debug("收到消息：" + msg);
        if (iConnectCallBack != null){
            iConnectCallBack.onReceive(msg);
        }
    }

    @Override //当消息被成功发送出去的时候，此方法被调用。
    public void messageSent(IoSession session, Object message) throws Exception {
        String messageSent = message.toString();
        log.debug("成功发送消息："+ messageSent);
        if (iConnectCallBack != null){
            iConnectCallBack.onMessageSent(messageSent);
        }
    }
}
