package com.dudu.android.launcher.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.AbsListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.dudu.android.launcher.R;
import com.dudu.android.launcher.bean.WindowMessageEntity;
import com.dudu.android.launcher.ui.adapter.RouteSearchAdapter;
import com.dudu.android.launcher.ui.adapter.StrategyAdapter;
import com.dudu.android.launcher.ui.view.RadioDialog;
import com.dudu.android.launcher.ui.view.SpeechDialogWindow;
import com.dudu.android.launcher.utils.Constants;
import com.dudu.android.launcher.utils.FloatWindow;
import com.dudu.android.launcher.utils.FloatWindow.AddressListItemClickCallback;
import com.dudu.android.launcher.utils.FloatWindow.AddressShowCallBack;
import com.dudu.android.launcher.utils.FloatWindow.CreateFloatWindowCallBack;
import com.dudu.android.launcher.utils.FloatWindow.FloatVoiceChangeCallBack;
import com.dudu.android.launcher.utils.FloatWindow.MessageShowCallBack;
import com.dudu.android.launcher.utils.FloatWindow.RemoveFloatWindowCallBack;
import com.dudu.android.launcher.utils.FloatWindow.StrategyChooseCallBack;
import com.dudu.android.launcher.utils.LogUtils;
import com.dudu.event.ListenerResetEvent;
import com.dudu.voice.semantic.SemanticConstants;
import com.dudu.voice.semantic.SemanticType;
import com.dudu.voice.semantic.VoiceManager;
import com.dudu.voice.semantic.chain.ChoosePageChain;
import com.dudu.voice.semantic.engine.SemanticProcessor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * 消息弹框Window管理服务
 */
public class NewMessageShowService extends Service implements MessageShowCallBack, AddressShowCallBack,
        StrategyChooseCallBack, FloatVoiceChangeCallBack,
        AddressListItemClickCallback, RemoveFloatWindowCallBack, CreateFloatWindowCallBack, FloatWindow.ChooseAddressPageCallBack {

    private FloatWindow mFloatWindow;

    // 悬浮窗View的实例
    private SpeechDialogWindow floatWindowLayout;

    // 悬浮窗View的参数
    private LayoutParams windowParams;

    // 用于控制在屏幕上添加或移除悬浮窗
    private WindowManager windowManager;

    private RadioDialog radioDialog;

    private ListView addressList;

    private RouteSearchAdapter mRouteSearchAdapter;

    private StrategyAdapter mStrategyAdapter;

    private ListView messageList;

    private List<WindowMessageEntity> list = new ArrayList<WindowMessageEntity>();

    private MessageAdapter mMessageAdapter;

    private boolean isShowWindow = false;

    private boolean isShowAddress = false;

    private int pageIndex = 0;

    private Logger log_init;

    private Context mContext;

    private boolean removeHasCalled = false;

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        log_init = LoggerFactory.getLogger("init.ui.float");

        log_init.debug("onStartCommand");
        init();
        return super.onStartCommand(intent, flags, startId);
    }

    private void init() {
        initWindow();
        mFloatWindow = FloatWindow.getInstance();

        mFloatWindow.setAddressShowCallBack(this);
        mFloatWindow.setMessageShowCallBack(this);
        mFloatWindow.setStrategyChooseCallBack(this);
        mFloatWindow.setFloatVoiceChangeCallBack(this);
        mFloatWindow.setAddressListItemClickCallback(this);
        mFloatWindow.setRemoveFloatWindowCallBack(this);
        mFloatWindow.setChooseAddressPageCallBack(this);
    }

    // 初始化window
    private void initWindow() {
        if (floatWindowLayout == null) {
            floatWindowLayout = new SpeechDialogWindow(this);
            if (windowParams == null) {
                windowParams = new LayoutParams();
                windowParams.type = LayoutParams.TYPE_SYSTEM_ALERT;
                windowParams.format = PixelFormat.RGBA_8888;
                windowParams.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | LayoutParams.FLAG_NOT_FOCUSABLE;
                windowParams.width = getWmWidth();
                windowParams.height = getWmHeigth();

                windowParams.x = 0;
                windowParams.y = 0;
                windowParams.alpha = 1.0f;

            }
            addressList = (ListView) floatWindowLayout.findViewById(R.id.show_addressListView);
            View v = new View(this);
            v.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, 200));
            addressList.addFooterView(v);
            messageList = (ListView) floatWindowLayout.findViewById(R.id.message_listview);
            messageList.addFooterView(v);
            mMessageAdapter = new MessageAdapter(this);
            messageList.setAdapter(mMessageAdapter);
        }
        radioDialog = (RadioDialog) floatWindowLayout
                .findViewById(R.id.radioDialog);

        radioDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeFloatWindow();

                VoiceManager.getInstance().stopUnderstanding();

                VoiceManager.getInstance().stopSpeaking();

                VoiceManager.getInstance().removeFloatCallback();

                SemanticProcessor.getProcessor().switchSemanticType(SemanticType.NORMAL);
            }
        });
    }


    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if (removeHasCalled == true) {
                log_init.debug("五秒内没有重新打开窗口");
                EventBus.getDefault().post(new ListenerResetEvent(ListenerResetEvent.LISTENER_OFF));
                removeHasCalled = false;
            } else {
                log_init.debug("五秒内没有已经被打开窗口");
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        log_init.debug("onDestroy");
        if (floatWindowLayout != null && windowManager != null) {
            windowManager.removeView(floatWindowLayout);
            floatWindowLayout = null;
            windowManager = null;
        }
        addressList = null;
        messageList = null;
        mStrategyAdapter = null;
        mRouteSearchAdapter = null;
        mMessageAdapter = null;
        if (!list.isEmpty()) {
            list.clear();
        }
        isShowWindow = false;
        isShowAddress = false;
        mFloatWindow.setIsWindowShow(false);
        super.onDestroy();
    }

    @Override
    public void showStrategy() {
        pageIndex = 0;
        isShowAddress = true;
        if (!isShowWindow) {
            if (windowManager != null && floatWindowLayout != null && windowParams != null) {
                windowManager.addView(floatWindowLayout, windowParams);
                showWindowCallback();
            }
        }
        if (messageList != null)
            messageList.setVisibility(View.GONE);
        if (addressList != null)
            addressList.setVisibility(View.VISIBLE);
        mStrategyAdapter = new StrategyAdapter(this);
        addressList.setAdapter(mStrategyAdapter);
        mStrategyAdapter.notifyDataSetChanged();
        isShowWindow = true;
        mFloatWindow.setIsWindowShow(true);
    }

    @Override
    public void showAddress() {
        isShowAddress = true;
        pageIndex = 0;
        if (!isShowWindow) {
            if (windowManager != null && floatWindowLayout != null && windowParams != null) {
                windowManager.addView(floatWindowLayout, windowParams);
                showWindowCallback();
            }
        }
        if (messageList != null)
            messageList.setVisibility(View.GONE);
        if (addressList != null)
            addressList.setVisibility(View.VISIBLE);
        mRouteSearchAdapter = new RouteSearchAdapter(this, 1);
        addressList.setAdapter(mRouteSearchAdapter);
        mRouteSearchAdapter.notifyDataSetChanged();
        isShowWindow = true;
        mFloatWindow.setIsWindowShow(true);
    }

    //消息显示
    @Override
    public void showMessage(String message, String type) {
        if (isShowAddress)
            return;
        try {
            if (TextUtils.isEmpty(message))
                return;
            if (!isShowWindow) {
                if (windowManager != null && floatWindowLayout != null && windowParams != null) {
                    windowManager.addView(floatWindowLayout, windowParams);
                    showWindowCallback();
                }
            }
            if (addressList != null)
                addressList.setVisibility(View.GONE);
            if (messageList != null)
                messageList.setVisibility(View.VISIBLE);
            if (list == null) {
                list = new ArrayList<>();
            }
            WindowMessageEntity wme = new WindowMessageEntity();
            wme.setContent(message);
            wme.setType(type);
            list.add(wme);
            if (floatWindowLayout != null && messageList != null) {
                if (android.os.Build.VERSION.SDK_INT >= 8) {
                    messageList.smoothScrollToPosition(list.size() - 1);
                } else {
                    messageList.setSelection(list.size() - 1);
                }
                if (mMessageAdapter != null)
                    mMessageAdapter.notifyDataSetChanged();
                isShowWindow = true;
                mFloatWindow.setIsWindowShow(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onVoiceChange(int voice) {
        if (isShowWindow && radioDialog != null) {
            radioDialog.setPressCounts(voice);
        }
    }

    @Override
    public void onAddressListItemClick(OnItemClickListener listener) {
        if (addressList != null)
            addressList.setOnItemClickListener(listener);

    }

    @Override
    public void choosePage(int type, int page) {

        switch (type) {
            case ChoosePageChain.NEXT_PAGE:
                if (pageIndex > 5) {
                    VoiceManager.getInstance().startSpeaking("已经是最后一页", SemanticConstants.TTS_DO_NOTHING, false);
                    return;
                }
                pageIndex++;
                break;
            case ChoosePageChain.LAST_PAGE:
                if (pageIndex <= 0) {
                    VoiceManager.getInstance().startSpeaking("已经是第一页", SemanticConstants.TTS_DO_NOTHING, false);
                    return;
                }
                pageIndex--;
                break;
            case ChoosePageChain.CHOOSE_PAGE:
                if (pageIndex > 5)
                    return;
                if (page > 5 || page < 1) {
                    VoiceManager.getInstance().stopUnderstanding();
                    VoiceManager.getInstance().startSpeaking("选择错误，请重新选择", SemanticConstants.TTS_START_UNDERSTANDING, false);
                    return;
                }
                pageIndex = page - 1;
                break;
        }
        Log.d("lxh", "choosePage ：" + pageIndex);
        addressList.setSelection(pageIndex * Constants.ADDRESS_VIEW_COUNT);
    }


    class MessageAdapter extends BaseAdapter {


        public MessageAdapter(Context context) {
            mContext = context;
        }

        @Override
        public int getCount() {
            return list == null ? 0 : list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            WindowMessageEntity message = list.get(position);
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = View.inflate(mContext, R.layout.list_message_item_layout, null);
                holder.leftView = convertView.findViewById(R.id.list_message_item_left);
                holder.rightView = convertView.findViewById(R.id.list_message_item_right);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            if (FloatWindow.MESSAGE_IN.equalsIgnoreCase(message.getType())) {
                holder.leftView.setVisibility(View.VISIBLE);
                holder.rightView.setVisibility(View.GONE);
                TextView tv_chatcontent = (TextView) holder.leftView.findViewById(R.id.tv_chatcontent);
                tv_chatcontent.setText(message.getContent());
            } else if (FloatWindow.MESSAGE_OUT.equalsIgnoreCase(message.getType())) {
                holder.leftView.setVisibility(View.GONE);
                holder.rightView.setVisibility(View.VISIBLE);
                TextView tv_chatcontent = (TextView) holder.rightView.findViewById(R.id.tv_chatcontent);
                tv_chatcontent.setText(message.getContent());
            }
            return convertView;
        }

        class ViewHolder {
            View leftView;
            View rightView;
        }

    }

    private synchronized WindowManager getWindowManager() {
        if (windowManager == null) {
            windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        }
        return windowManager;
    }

    private int getWmWidth() {
        return getWindowManager().getDefaultDisplay().getWidth();// 屏幕宽度
    }

    private int getWmHeigth() {
        return getWindowManager().getDefaultDisplay().getHeight();// 屏幕高度
    }

    @Override
    public void removeFloatWindow() {
        SemanticProcessor.getProcessor().switchSemanticType(SemanticType.NORMAL);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (floatWindowLayout != null && windowManager != null && isShowWindow) {
                    removeHasCalled = true;
                    VoiceManager.getInstance().setUnderstandingOrSpeaking(false);
                    VoiceManager.getInstance().stopUnderstanding();
                    mHandler.sendEmptyMessageDelayed(1, 5000);
                    windowManager.removeView(floatWindowLayout);
                }
                isShowWindow = false;
                isShowAddress = false;
                mFloatWindow.setIsWindowShow(false);
                if (!list.isEmpty())
                    list.clear();
            }
        }, 100);
    }

    @Override
    public void createFloatWindow() {
        log_init.debug("打开窗口");
        if (!isShowWindow) {
            if (windowManager != null && floatWindowLayout != null && windowParams != null) {
                windowManager.addView(floatWindowLayout, windowParams);
                showWindowCallback();
            }
        }

        isShowWindow = true;
    }

    private void showWindowCallback() {
        log_init.error("调用显示window的回调");
        EventBus.getDefault().post(new ListenerResetEvent(ListenerResetEvent.LISTENER_ON));

        if (removeHasCalled == false) {
            EventBus.getDefault().post(new ListenerResetEvent(ListenerResetEvent.LISTENER_ON));
        }else{
            log_init.debug("五秒内被打开窗口");
            mHandler.removeMessages(1);
        }
        removeHasCalled = false;
    }

}