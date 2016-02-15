package com.dudu.map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.amap.api.maps.AMapException;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.NaviPara;
import com.dudu.aios.ui.map.GaodeMapActivity;
import com.dudu.aios.ui.map.MapDialog;
import com.dudu.aios.ui.map.NavigationActivity;
import com.dudu.aios.ui.map.event.ChooseEvent;
import com.dudu.android.launcher.LauncherApplication;
import com.dudu.android.launcher.R;
import com.dudu.android.launcher.ui.activity.NaviBackActivity;
import com.dudu.android.launcher.ui.activity.SimpleHudActivity;
import com.dudu.android.launcher.utils.ActivitiesManager;
import com.dudu.android.launcher.utils.CommonAddressUtil;
import com.dudu.android.launcher.utils.Constants;
import com.dudu.android.launcher.utils.FloatWindowUtils;
import com.dudu.android.launcher.utils.ToastUtils;
import com.dudu.android.launcher.utils.Utils;
import com.dudu.event.MapResultShow;
import com.dudu.monitor.Monitor;
import com.dudu.monitor.event.CarStatus;
import com.dudu.navi.NavigationManager;
import com.dudu.navi.Util.NaviUtils;
import com.dudu.navi.entity.Navigation;
import com.dudu.navi.entity.PoiResultInfo;
import com.dudu.navi.entity.Point;
import com.dudu.navi.event.NaviEvent;
import com.dudu.navi.vauleObject.NavigationType;
import com.dudu.navi.vauleObject.OpenMode;
import com.dudu.navi.vauleObject.SearchType;
import com.dudu.voice.VoiceManagerProxy;
import com.dudu.voice.semantic.constant.SceneType;
import com.dudu.voice.semantic.constant.TTSType;
import com.dudu.voice.semantic.engine.SemanticEngine;

import de.greenrobot.event.EventBus;
import rx.Observable;
import rx.Subscription;

/**
 * Created by lxh on 2015/11/26.
 */
public class NavigationProxy {

    private static final int REMOVE_WINDOW_TIME = 6 * 1000;

    private static NavigationProxy mInstance;

    private Context context;

    private NavigationManager navigationManager;

    private VoiceManagerProxy voiceManager;

    public static final int OPEN_MANUAL = 1;

    public static final int OPEN_VOICE = 2;

    public static final int OPEN_MAP = 3;

    private int chooseStep;

    public Point endPoint = null;

    private boolean isManual = false;

    private Handler handler;

    private MapDialog waitingDialog = null;

    private String msg;

    private Subscription naviSubscription = null;

    public void setChooseStep(int chooseStep) {
        this.chooseStep = chooseStep;
    }

    public boolean isManual() {
        return isManual;
    }

    private long mLastClickTime = 0;

    private Runnable removeWindowRunnable = new Runnable() {
        @Override
        public void run() {

            if (navigationManager.isNavigatining()) {
                intentActivity(NavigationActivity.class);
            }

            FloatWindowUtils.removeFloatWindow();
        }
    };

    private boolean needNotify = true;

    public NavigationProxy() {
        context = LauncherApplication.getContext();

        navigationManager = NavigationManager.getInstance(context);

        EventBus.getDefault().register(this);

        handler = new Handler();

        voiceManager = VoiceManagerProxy.getInstance();
    }

    public static NavigationProxy getInstance() {
        if (mInstance == null) {
            mInstance = new NavigationProxy();
        }
        return mInstance;
    }

    public int getChooseStep() {
        return chooseStep;
    }


    public void setIsManual(boolean isManual) {
        this.isManual = isManual;
    }


    public boolean openNavi(int openType) {
        if (checkFastClick()) {
            return false;
        }
        switch (NaviUtils.getOpenMode(context)) {
            case INSIDE:
                return (openType == OPEN_MAP) ? openMapActivity() : openActivity(openType);
            case OUTSIDE:
                openGaode();
                break;
        }
        return true;
    }

    private boolean checkFastClick() {
        long now = System.currentTimeMillis();
        if (now - mLastClickTime < 3000) {
            return true;
        }
        mLastClickTime = now;
        return false;
    }

    public void closeMap() {
        ActivitiesManager.getInstance().closeTargetActivity(
                GaodeMapActivity.class);
    }

    private boolean openMapActivity() {
        intentActivity(GaodeMapActivity.class);
        return true;
    }

    private boolean openActivity(int openType) {
        navigationManager.getLog().debug(">>>>>> {}", navigationManager.isNavigatining());

        if (navigationManager.isNavigatining()) {
            switch (navigationManager.getNavigationType()) {
                case NAVIGATION:
                    intentActivity(NavigationActivity.class);
                    break;
                case BACKNAVI:
                    break;
            }
            FloatWindowUtils.removeFloatWindow();
        } else {
            navigationManager.getLog().debug(">>>>>> {}", isMapActivity());

            if (!isMapActivity()) {
                navigationManager.getLog().debug("openActivity");
                if (openType == OPEN_VOICE) {
                    voiceManager.startSpeaking(context.getString(R.string.openNavi_notice),
                            TTSType.TTS_START_UNDERSTANDING, true);
                    SemanticEngine.getProcessor().switchSemanticType(SceneType.NAVIGATION);
                }
                intentActivity(GaodeMapActivity.class);
            } else {
                return false;
            }
        }

        return true;
    }

    private Activity getTopActivity() {
        return ActivitiesManager.getInstance().getTopActivity();
    }

    private boolean isMapActivity() {
        return (getTopActivity() != null && getTopActivity() instanceof GaodeMapActivity);
    }

    public void existNavi() {
        navigationManager.existNavigation();
        ActivitiesManager.getInstance().closeTargetActivity(
                NavigationActivity.class);
        ActivitiesManager.getInstance().closeTargetActivity(
                GaodeMapActivity.class);
        ActivitiesManager.getInstance().closeTargetActivity(
                NaviBackActivity.class);
    }

    public void searchControl(String semantic, String service, String keyword, SearchType type) {
        if (navigationManager.getSearchType() == SearchType.SEARCH_COMMONADDRESS)
            type = SearchType.SEARCH_COMMONPLACE;
        navigationManager.setSearchType(type);
        if (keyword == null)
            navigationManager.parseKeyword(semantic, service);
        else
            navigationManager.setKeyword(keyword);
        doSearch();
    }

    public void doSearch() {

        if (NaviUtils.getOpenMode(context) == OpenMode.INSIDE) {
            if (!isMapActivity()) {
                intentActivity(GaodeMapActivity.class);
            }
        } else {
            openGaode();
        }
        switch (navigationManager.getSearchType()) {
            case SEARCH_DEFAULT:
                return;
            case SEARCH_NEARBY:
            case SEARCH_NEAREST:
            case SEARCH_PLACE:
            case SEARCH_PLACE_LOCATION:
            case SEARCH_COMMONPLACE:
                searchHint();
                break;
        }

    }

    private void searchHint() {
        needNotify = true;
        msg = "正在搜索" + navigationManager.getKeyword();
        boolean isShow = false;
        if (TextUtils.isEmpty(navigationManager.getKeyword())) {
            voiceManager.stopUnderstanding();
            voiceManager.startSpeaking("关键字有误，请重新输入！",
                    TTSType.TTS_START_UNDERSTANDING, true);
            return;
        }

        if (Monitor.getInstance(context).getCurrentLocation() == null) {
            navigationManager.setSearchType(SearchType.SEARCH_DEFAULT);
            msg = "暂未获取到您的当前位置，不能搜索，请稍后再试";
            if (isManual) {
                ToastUtils.showToast(msg);
            } else {
                voiceManager.startSpeaking(msg, TTSType.TTS_DO_NOTHING, true);
                removeWindow();
            }
            return;
        }
        if (Constants.CURRENT_POI.equals(navigationManager.getKeyword())) {
            navigationManager.setSearchType(SearchType.SEARCH_CUR_LOCATION);
            msg = "正在获取您的当前位置";
            isShow = true;
        }
        if (!isManual) {
            voiceManager.startSpeaking(msg, TTSType.TTS_DO_NOTHING, isShow);
        }
        showProgressDialog(msg);
        handler.postDelayed(() -> navigationManager.search(), 2000);
    }


    public void onEventMainThread(NaviEvent.NaviVoiceBroadcast event) {
        if (isManual)
            return;
        navigationManager.getLog().debug("NaviVoiceBroadcast stopUnderstanding");
        voiceManager.clearMisUnderstandCount();
        voiceManager.stopUnderstanding();
        removeCallback();
        voiceManager.startSpeaking(event.getNaviVoice(), TTSType.TTS_START_UNDERSTANDING, true);
    }

    public void onEventMainThread(NaviEvent.SearchResult event) {
        removeCallback();
        navigationManager.getLog().debug("SearchResult: {},{}", navigationManager.getSearchType(), event);
        if (event == NaviEvent.SearchResult.SUCCESS) {
            handlerPoiResult();
        } else {
            if (isManual) {
                ToastUtils.showToast(context.getString(R.string.search_fail));
            }
            navigationManager.setSearchType(SearchType.SEARCH_DEFAULT);
        }
        disMissProgressDialog();
    }

    public void onEventBackgroundThread(NaviEvent.ChangeSemanticType event) {
        SceneType type = null;
        switch (event) {
            case MAP_CHOISE:
                type = SceneType.MAP_CHOISE;
                break;
            case NORMAL:
                type = SceneType.HOME;
                break;
            case NAVIGATION:
                type = SceneType.NAVIGATION;
                break;
        }
        SemanticEngine.getProcessor().switchSemanticType(type);
    }

    public void onEventMainThread(NavigationType event) {
        disMissProgressDialog();
        removeCallback();
        naviSubscription = null;

        switch (event) {
            case NAVIGATION:
                if (!needNotify)
                    return;
                navigationManager.setNavigationType(NavigationType.NAVIGATION);
                intentActivity(NavigationActivity.class);
                if (isMapActivity()) {
                    ActivitiesManager.getInstance().closeTargetActivity(GaodeMapActivity.class);
                }
                break;
            case BACKNAVI:
                navigationManager.setNavigationType(NavigationType.BACKNAVI);
                intentActivity(NaviBackActivity.class);
                break;
            case CALCULATEERROR:
                if (!needNotify)
                    return;
                navigationManager.setSearchType(SearchType.SEARCH_DEFAULT);
                voiceManager.startSpeaking("路径规划出错，请稍后再试", TTSType.TTS_DO_NOTHING, true);
                removeWindow();
                return;
            case NAVIGATION_END:
                navigationManager.setNavigationType(NavigationType.DEFAULT);
                ActivitiesManager.getInstance().closeTargetActivity(NavigationActivity.class);
                ActivitiesManager.getInstance().closeTargetActivity(NaviBackActivity.class);
                ActivitiesManager.getInstance().closeTargetActivity(SimpleHudActivity.class);
                intentActivity(GaodeMapActivity.class);
                break;
        }
        navigationManager.setSearchType(SearchType.SEARCH_DEFAULT);


    }

    public void handlerPoiResult() {
        endPoint = null;
        switch (navigationManager.getSearchType()) {
            case SEARCH_CUR_LOCATION:
                handler.postDelayed(() -> voiceManager.startSpeaking(navigationManager.getCurlocationDesc(),
                        TTSType.TTS_START_UNDERSTANDING, true), 200);
                navigationManager.setSearchType(SearchType.SEARCH_DEFAULT);
                break;
            case SEARCH_PLACE_LOCATION:
                String playText = "您好，" + navigationManager.getKeyword() + "的位置为："
                        + navigationManager.getPoiResultList().get(0).getAddressDetial();
                voiceManager.startSpeaking(playText, TTSType.TTS_START_UNDERSTANDING, true);
                navigationManager.setSearchType(SearchType.SEARCH_DEFAULT);
                return;
            case SEARCH_NEAREST:
                SemanticEngine.getProcessor().switchSemanticType(
                        SceneType.MAP_CHOISE);
                this.endPoint = new Point(navigationManager.getPoiResultList().get(0).getLatitude(),
                        navigationManager.getPoiResultList().get(0).getLongitude());
                EventBus.getDefault().post(MapResultShow.STRATEGY);
                navigationManager.setSearchType(SearchType.SEARCH_DEFAULT);
                return;
            default:
                SemanticEngine.getProcessor().switchSemanticType(
                        SceneType.MAP_CHOISE);
                EventBus.getDefault().post(MapResultShow.ADDRESS);
                break;

        }

    }

    private void initWaitingDialog(String message) {
        if (waitingDialog != null && waitingDialog.isShowing()) {
            waitingDialog.dismiss();
            waitingDialog = null;
        }
        waitingDialog = new MapDialog(ActivitiesManager.getInstance().getTopActivity(), message, cancel);
        Window dialogWindow = waitingDialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.x = 10; // 新位置X坐标
        lp.y = 0; // 新位置Y坐标
        lp.width = 296;
        lp.height = 208;
        dialogWindow.setAttributes(lp);
    }

    /**
     * 显示进度框
     */
    public void showProgressDialog(String message) {
        try {
            initWaitingDialog(message);
            waitingDialog.show();
            switch (navigationManager.getSearchType()) {
                case SEARCH_PLACE_LOCATION:
                case SEARCH_CUR_LOCATION:
                    return;
            }
        } catch (Exception e) {

        }

        FloatWindowUtils.removeFloatWindow();
    }

    public void disMissProgressDialog() {
        if (waitingDialog != null && waitingDialog.isShowing()) {
            waitingDialog.dismiss();
            waitingDialog = null;
        }
    }


    /**
     * 添加常用地
     *
     * @param choosePoint
     */
    public void addCommonAddress(final PoiResultInfo choosePoint) {
        FloatWindowUtils.removeFloatWindow();
        final String addType = navigationManager.getCommonAddressType().getName();
        CommonAddressUtil.setCommonAddress(addType, context, choosePoint.getAddressTitle());
        CommonAddressUtil.setCommonLocation(addType,
                context, choosePoint.getLatitude(), choosePoint.getLongitude());

        voiceManager.stopUnderstanding();

        handler.postDelayed(() -> voiceManager.startSpeaking("添加" + choosePoint.getAddressTitle() + "为 " + addType + " 地址成功！",
                TTSType.TTS_DO_NOTHING, true), 200);
        navigationManager.setSearchType(SearchType.SEARCH_DEFAULT);
        removeWindow();
    }


    public void onNextPage() {
        EventBus.getDefault().post(new ChooseEvent(ChooseEvent.NEXTPAGE, 0));
    }

    public void onPreviousPage() {
        EventBus.getDefault().post(new ChooseEvent(ChooseEvent.PREVIOUSPAGE, 0));
    }

    public void onChoosePage(int page) {
        EventBus.getDefault().post(new ChooseEvent(ChooseEvent.CHOOSEPAGE, page));
    }

    public void onChooseNumber(int position) {
        if (chooseStep == 1) {
            EventBus.getDefault().post(new ChooseEvent(ChooseEvent.ADDRESS_NUMBER, position));
        } else {
            EventBus.getDefault().post(new ChooseEvent(ChooseEvent.STRATEGY_NUMBER, position));
        }
    }

    public void startNavigation(Navigation navigation) {
        if (naviSubscription != null)
            return;
        needNotify = true;
        naviSubscription = Observable.just(navigation)
                .subscribe(navigation1 -> {
                    navigationManager.getLog().debug("----startNavigation stopUnderstanding");
                    voiceManager.stopUnderstanding();
                    voiceManager.clearMisUnderstandCount();
                    voiceManager.startSpeaking("路径规划中，请稍后...", TTSType.TTS_DO_NOTHING, false);
                    FloatWindowUtils.removeFloatWindow();
                    if (NaviUtils.getOpenMode(context) == OpenMode.OUTSIDE) {
                        startNaviOutside(navigation1);
                        return;
                    }
                    openActivity(OPEN_MANUAL);
                    showProgressDialog(context.getString(R.string.routePlanning));
                    isManual = false;
                    SemanticEngine.getProcessor().switchSemanticType(SceneType.HOME);
                    navigationManager.startCalculate(navigation1);
                });

    }

    private void startNaviOutside(Navigation navigation) {
        NaviPara naviPara = new NaviPara();
        naviPara.setTargetPoint(new LatLng(navigation.getDestination().latitude,
                navigation.getDestination().longitude));
        naviPara.setNaviStyle(navigation.getDriveMode().ordinal());
        try {
            AMapUtils.openAMapNavi(naviPara, context);
            naviSubscription = null;
        } catch (AMapException e) {
            e.printStackTrace();
        }
    }

    public void removeCallback() {
        if (handler != null && removeWindowRunnable != null) {
            handler.removeCallbacks(removeWindowRunnable);
        }
    }

    public void removeWindow() {
        disMissProgressDialog();
        if (navigationManager.getNavigationType() != NavigationType.DEFAULT) {
            navigationManager.setIsNavigatining(true);
        }
        if (navigationManager.getSearchType() == SearchType.SEARCH_DEFAULT) {
            handler.postDelayed(removeWindowRunnable, REMOVE_WINDOW_TIME);
        }
    }

    private void intentActivity(Class intentClass) {
        Intent standIntent = new Intent(context, intentClass);
        standIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(standIntent);
        mLastClickTime = 0;
    }

    public void onEvent(CarStatus event) {
        switch (event) {
            case OFFLINE:
                existNavi();
                break;
        }
    }

    public void onEvent(NaviEvent.NavigationInfoBroadcast event) {
        voiceManager.clearMisUnderstandCount();
        voiceManager.startSpeaking(event.getInfo(), TTSType.TTS_DO_NOTHING, false);
    }

    public void openGaode() {
        EventBus.getDefault().post(NaviEvent.FloatButtonEvent.SHOW);
        Utils.startThirdPartyApp(ActivitiesManager.getInstance().getTopActivity(), "com.autonavi.minimap");
    }

    private View.OnClickListener cancel = v -> needNotify = false;
}
