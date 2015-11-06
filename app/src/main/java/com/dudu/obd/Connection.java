package com.dudu.obd;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

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

import android.content.Context;
import android.util.Log;

import com.dudu.android.launcher.LauncherApplication;

public class Connection extends Thread {
	private String TAG = "DUDU_CONNETION";
//	private String host = "192.168.1.177";
	private String host = "119.29.65.127";
	private int port = 8888;
	private IoConnector connector = null;
	private IoSession session = null;
	private boolean isSessionOpen = false;
	/**
	 * session 的状态
	 */
	public static int SESSION_CREATE = 0;
	public static int SESSION_OPEND = 1;
	public static int SESSION_IDLE = 2;
	public static int SESSION_CLOSED = 3;

	public static String METHOD_GPSDATA = "coordinates";
	public static String METHOD_OBDDATA = "obdDatas";
	public static String METHOD_FLAMEOUTDATA = "driveDatas";
	/**
	 * 接收到服务端返回信息回调接口
	 */
	private List<OnRecieveCallBack> mOnRecieveCallBackList;
	private List<onSessionStateChangeCallBack> mOnStateChangeCallBackList;
	private PersistentStorage mPersistentStorage;
	private boolean isHasData = false;
	private boolean mIsNetworkOK = true;
    private List<String> important_msgList;
    private TakePhotoCallBack takePhotoCallBack;
	private static Connection mConnection;

	public Connection(Context context) {
		mOnRecieveCallBackList = new ArrayList<>();
		mOnStateChangeCallBackList = new ArrayList<>();
		mPersistentStorage = PersistentStorage.getInstance(context);
		important_msgList = new ArrayList<>();
	}
	public static Connection getInstance(Context context) {
		if (mConnection == null)
			mConnection = new Connection(context);
		return mConnection;
	}
	public void startConn() {
		Log.d(TAG, "Connection 开始连接");
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
		session = future.getSession(); // 获得session
	}

	// 网络状态正常
	public void onNetWorkOK() {
		mIsNetworkOK = true;
	}

	// 网络断开
	public void onNONetWork() {
		mIsNetworkOK = false;
	}

	/**
	 * 接收到服务端返回信息回调接口
	 * 
	 */
	public interface OnRecieveCallBack {
		/**
		 * 
		 * @param method
		 *            对应的方法名
		 * @param resultCode
		 *            结果码
		 * @param resultDes
		 *            结果描述
		 */
		public void OnRecieveFromServerMsg(String method, String resultCode, String resultDes);
		
	}

	/**
	 * 
	 * @author 会话状态改变回调接口
	 *
	 */
	public interface onSessionStateChangeCallBack {
		public void onSessionStateChange(int state);
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

	/**
	 * 添加接收到消息的监听
	 * 
	 * @param callBack
	 * @return
	 */
	public boolean addReceivedCallBack(OnRecieveCallBack callBack) {
		if (mOnRecieveCallBackList != null)
			return mOnRecieveCallBackList.add(callBack);
		else
			return false;
	}

	/**
	 * 移除接收到消息的监听
	 * 
	 * @param callBack
	 * @return
	 */
	public boolean removeReceivedCallBack(OnRecieveCallBack callBack) {
		if (mOnRecieveCallBackList != null && callBack != null)
			return mOnRecieveCallBackList.remove(callBack);
		else
			return false;

	}

	/**
	 * 添加状态改变监听
	 * 
	 * @param callBack
	 * @return
	 */
	public boolean addStateChangeCallBack(onSessionStateChangeCallBack callBack) {
		if (mOnStateChangeCallBackList != null)
			return mOnStateChangeCallBackList.add(callBack);
		else
			return false;
	}

	public boolean removeStateChangeCallBack(onSessionStateChangeCallBack callBack) {
		if (mOnStateChangeCallBackList != null && callBack != null)
			return mOnStateChangeCallBackList.remove(callBack);
		else
			return false;
	}

	@Override
	public void run() {
		try {
			while (mIsNetworkOK) {
				
				if(!isSessionOpen){
					startConn();
					Thread.sleep(3000);
				}
			   checkCache();
			   Thread.sleep(2000);
			}
		} catch (Exception e) {
			isSessionOpen = false;
			Log.d(TAG, "Connection 连接异常");
			e.printStackTrace();

		}

	}

	/**
	 * 发送消息
	 * 
	 * @param message
	 */
	public void sendMessage(String message) {
		if (session != null && isSessionOpen) {
			session.write(message);
		}
	}

	public void sendMessage(String msg, boolean needCache) {
		if (needCache) {
			synchronized (mPersistentStorage) {
				mPersistentStorage.addTail(msg);
			}
		}
	}
	// 连接后检查是否有缓存数据，如果有，则发送
    private void checkCache(){
    	if(isSessionOpen){
    		synchronized (important_msgList) {
        		if(mPersistentStorage!=null&&mPersistentStorage.getCount()>0){
            		isHasData = true;
            		for(int i = 0; i<mPersistentStorage.getCount();i++){
            			if(!important_msgList.contains(mPersistentStorage.getAll().get(i))){
            				important_msgList.add(mPersistentStorage.getAll().get(i));
            			}
            		}
            	}
        		if(important_msgList.size()>0){
        			session.write(important_msgList.get(0));
    				important_msgList.remove(0);
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
		if(!important_msgList.isEmpty())
			important_msgList.clear();
		return true;
	}

	/**
	 * 连接处理内部类
	 *
	 */
	class MinaClientHandler extends IoHandlerAdapter {
		@Override
		public void exceptionCaught(IoSession session, Throwable cause) {
			Log.e(TAG, "客户端连接异常" + cause);
			try {
				super.exceptionCaught(session, cause);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		public void inputClosed(IoSession session) {
			// TODO Auto-generated method stub
		}

		@Override
		public void messageReceived(IoSession session, Object message) {
			// TODO Auto-generated method stub
			String msg = message.toString();
			if (isHasData)
				mPersistentStorage.deleteHeader();
			Log.d(TAG, "received message：" + msg);
			try {
				JSONObject jsonResult = new JSONObject(msg);
				if(jsonResult.has("result")&&jsonResult.has("resultCode")&&jsonResult.has("resultDesc")){
					if (!mOnRecieveCallBackList.isEmpty()) {
						for (int i = 0; i < mOnRecieveCallBackList.size(); i++) {
							try {
								jsonResult = new JSONObject(msg);
								mOnRecieveCallBackList.get(i).OnRecieveFromServerMsg(jsonResult.getString("result"),
										jsonResult.getString("resultCode"), jsonResult.getString("resultDesc"));
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}
				if(jsonResult.has("method")&&jsonResult.has("openid")){
					if(takePhotoCallBack!=null){
						takePhotoCallBack.takePhoto(jsonResult.getString("openid"));
					}
				}
				if(jsonResult.has("method")&&jsonResult.has("openid")&&jsonResult.has("lat")&&jsonResult.has("lon")){

				}
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			
		}

		@Override
		public void messageSent(IoSession session, Object message) {
			Log.d(TAG, "sent message :" + message.toString());
			try {
				super.messageSent(session, message);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		public void sessionClosed(IoSession session) {
			Log.d(TAG, "客户端会话关闭");
			isSessionOpen = false;
			if (!mOnStateChangeCallBackList.isEmpty()) {
				for (int i = 0; i < mOnStateChangeCallBackList.size(); i++) {
					mOnStateChangeCallBackList.get(i).onSessionStateChange(SESSION_CLOSED);
				}
			}
			try {
				super.sessionClosed(session);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		public void sessionCreated(IoSession session) {
			Log.d(TAG, "客户端会话创建");
			if (!mOnStateChangeCallBackList.isEmpty()) {
				for (int i = 0; i < mOnStateChangeCallBackList.size(); i++) {
					mOnStateChangeCallBackList.get(i).onSessionStateChange(SESSION_CREATE);
				}
			}
			try {
				super.sessionCreated(session);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		public void sessionIdle(IoSession session, IdleStatus idlestatus) {
			Log.d(TAG, "客户端会话休眠");
			if (!mOnStateChangeCallBackList.isEmpty()) {
				for (int i = 0; i < mOnStateChangeCallBackList.size(); i++) {
					mOnStateChangeCallBackList.get(i).onSessionStateChange(SESSION_IDLE);
				}
			}
			try {
				super.sessionIdle(session, idlestatus);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		public void sessionOpened(IoSession session) {
			Log.d(TAG, "客户端会话打开");
			isSessionOpen = true;
			if (!mOnStateChangeCallBackList.isEmpty()) {
				for (int i = 0; i < mOnStateChangeCallBackList.size(); i++) {
					mOnStateChangeCallBackList.get(i).onSessionStateChange(SESSION_OPEND);
				}
			}
			try {
				super.sessionOpened(session);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	// 重连
	private void reConn() {
		try {
			closeConn();
			startConn();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 *用于接收后台发送的拍照指令
	 */
	public interface TakePhotoCallBack{
		void takePhoto(String openId);
	}

	/**
	 * 
	 *用户接收后台发送的目的地导航指令
	 */
	public interface StartNaviCallBack{
		/**
		 * 启动导航
		 * @param lat
		 * @param lon
		 */
		void startNavi(double lat, double lon);
	}
	public TakePhotoCallBack getTakePhotoCallBack() {
		return takePhotoCallBack;
	}

	public void setTakePhotoCallBack(TakePhotoCallBack takePhotoCallBack) {
		this.takePhotoCallBack = takePhotoCallBack;
	}


}
