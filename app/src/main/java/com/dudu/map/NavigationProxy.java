package com.dudu.map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;

import com.amap.api.maps.AMapException;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.NaviPara;
import com.dudu.android.launcher.LauncherApplication;
import com.dudu.android.launcher.ui.activity.LocationMapActivity;
import com.dudu.android.launcher.ui.activity.NaviBackActivity;
import com.dudu.android.launcher.ui.activity.NaviCustomActivity;
import com.dudu.android.launcher.ui.activity.SimpleHudActivity;
import com.dudu.android.launcher.ui.dialog.WaitingDialog;
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
import rx.functions.Action1;

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

    private boolean isAddressManual = false;

    private int chooseStep;

    private Point endPoint;

    private boolean isManual = false;

    private boolean isShowAddress = false;

    private Handler handler;

    private PoiResultInfo choosePoiResult;

    private WaitingDialog waitingDialog = null;// 搜索时进度条

    private Class intentClass;

    private String msg;

    private Subscription naviSubscription = null;

    private Subscription chooseStrategyMethodSub = null;

    private Subscription chooseAddressSub = null;

    private long mLastClickTime = 0;

    private Runnable removeWindowRunnable = new Runnable() {

        @Override
        public void run() {
            FloatWindowUtils.removeFloatWindow();
        }
    };

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

    public void setIsShowAddress(boolean isShowAddress) {
        this.isShowAddress = isShowAddress;
    }

    public void setIsManual(boolean isManual) {
        this.isManual = isManual;
    }

    public PoiResultInfo getChoosePoiResult() {
        return choosePoiResult;
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
                LocationMapActivity.class);
    }

    private boolean openMapActivity() {
        intentClass = LocationMapActivity.class;
        intentActivity();
        return true;
    }

    private boolean openActivity(int openType) {
        if (isShowAddress)
            return false;
        if (navigationManager.isNavigatining()) {
            switch (navigationManager.getNavigationType()) {
                case NAVIGATION:
                    intentClass = NaviCustomActivity.class;
                    break;
                case BACKNAVI:
                    intentClass = NaviBackActivity.class;
                    break;
            }
            FloatWindowUtils.removeFloatWindow();
        } else {
            if (!isMapActivity()) {
                intentClass = LocationMapActivity.class;

                if (openType == OPEN_VOICE) {
                    voiceManager.startSpeaking("您好，请说出您想去的地方或者关键字",
                            TTSType.TTS_START_UNDERSTANDING, true);

                    SemanticEngine.getProcessor().switchSemanticType(SceneType.NAVIGATION);
                }
            } else {
                return false;
            }
        }

        intentActivity();
        return true;
    }

    private Activity getTopActivity() {
        return ActivitiesManager.getInstance().getTopActivity();
    }

    private boolean isMapActivity() {
        return (getTopActivity() != null && getTopActivity() instanceof LocationMapActivity);
    }

    public void existNavi() {
        navigationManager.existNavigation();
        ActivitiesManager.getInstance().closeTargetActivity(
                NaviCustomActivity.class);
        ActivitiesManager.getInstance().closeTargetActivity(
                LocationMapActivity.class);
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
        chooseAddressSub = null;
        chooseStrategyMethodSub = null;
        if (NaviUtils.getOpenMode(context) == OpenMode.INSIDE) {
            if (!navigationManager.isNavigatining() && !isMapActivity()) {
                intentClass = LocationMapActivity.class;
                intentActivity();
            } else {
                isNaviActivity();
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
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                navigationManager.search();
            }
        }, 2000);
    }

    private boolean isNaviActivity() {

        if (navigationManager.isNavigatining()) {
            switch (navigationManager.getNavigationType()) {
                case NAVIGATION:
                    if (!(getTopActivity() instanceof NaviCustomActivity)) {
                        intentClass = NaviCustomActivity.class;
                    }
                    intentActivity();
                    return true;
                case BACKNAVI:
                    if (!(getTopActivity() instanceof NaviBackActivity)) {
                        intentClass = NaviBackActivity.class;
                    }
                    intentActivity();
                    return true;
                case DEFAULT:
                    return false;
            }
        }
        return false;
    }

    public void onEventMainThread(NaviEvent.NaviVoiceBroadcast event) {
        if (isManual)
            return;
        navigationManager.getLog().debug("-----NaviVoiceBroadcast stopUnderstanding");
        voiceManager.clearMisUnderstandCount();
        voiceManager.stopUnderstanding();
        removeCallback();
        voiceManager.startSpeaking(event.getNaviVoice(), TTSType.TTS_START_UNDERSTANDING, true);
    }

    public void onEventMainThread(NaviEvent.SearchResult event) {
        removeCallback();
        navigationManager.getLog().debug("----SearchResult: {}", navigationManager.getSearchType());
        if (event == NaviEvent.SearchResult.SUCCESS) {
            handlerPoiResult();
        } else {
            if (isManual) {
                ToastUtils.showToast("抱歉,搜索失败，请稍后重试");
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
                navigationManager.setNavigationType(NavigationType.NAVIGATION);
                intentClass = NaviCustomActivity.class;
                intentActivity();
                if (isMapActivity()) {
                    ActivitiesManager.getInstance().closeTargetActivity(LocationMapActivity.class);
                }
                break;
            case BACKNAVI:
                navigationManager.setNavigationType(NavigationType.BACKNAVI);
                intentClass = NaviBackActivity.class;
                intentActivity();
                break;
            case CALCULATEERROR:
                navigationManager.setSearchType(SearchType.SEARCH_DEFAULT);
                voiceManager.startSpeaking("路径规划出错，请稍后再试", TTSType.TTS_DO_NOTHING, true);
                removeWindow();
                return;
            case NAVIGATION_END:
                navigationManager.setNavigationType(NavigationType.DEFAULT);
                intentClass = LocationMapActivity.class;
                ActivitiesManager.getInstance().closeTargetActivity(NaviCustomActivity.class);
                ActivitiesManager.getInstance().closeTargetActivity(NaviBackActivity.class);
                ActivitiesManager.getInstance().closeTargetActivity(SimpleHudActivity.class);
                intentActivity();
                break;
        }
        navigationManager.setSearchType(SearchType.SEARCH_DEFAULT);


    }

    public void handlerPoiResult() {
        endPoint = null;
        choosePoiResult = null;
        switch (navigationManager.getSearchType()) {
            case SEARCH_CUR_LOCATION:
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        voiceManager.startSpeaking(navigationManager.getCurlocationDesc(),
                                TTSType.TTS_START_UNDERSTANDING, true);
                    }
                }, 200);
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
                endPoint = new Point(navigationManager.getPoiResultList().get(0).getLatitude(),
                        navigationManager.getPoiResultList().get(0).getLongitude());
                showStrategyMethod(0);
                navigationManager.setSearchType(SearchType.SEARCH_DEFAULT);
                return;
            default:
                SemanticEngine.getProcessor().switchSemanticType(
                        SceneType.MAP_CHOISE);
                isShowAddress = true;
                chooseStep = 1;
                if (isManual) {
                    EventBus.getDefault().post(MapResultShow.ADDRESS);
                } else {
                    showAddressByVoice();
                }
                break;

        }

    }

    private void initWaitingDialog(String message) {
        if (waitingDialog != null && waitingDialog.isShowing()) {
            waitingDialog.dismiss();
            waitingDialog = null;
        }
        waitingDialog = new WaitingDialog(ActivitiesManager.getInstance().getTopActivity(), message);
        Window dialogWindow = waitingDialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.x = 10; // 新位置X坐标
        lp.y = 0; // 新位置Y坐标
        lp.width = 280;
        lp.height = 120;
        lp.alpha = 0.8f; // 透明度
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

    public void showAddressByVoice() {
        if (navigationManager.getPoiResultList().isEmpty())
            return;
        voiceManager.startSpeaking(
                "为您找到以下地址，请选择第几个", TTSType.TTS_START_UNDERSTANDING, true);
        FloatWindowUtils.showAddress(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(
                            AdapterView<?> arg0,
                            View arg1,
                            int position, long arg3) {
                        if (position >= navigationManager.getPoiResultList().size())
                            return;
                        isAddressManual = true;
                        navigationManager.getLog().debug("-----manual click showAddressByVoice stopUnderstanding");
                        voiceManager.stopUnderstanding();
                        chooseAddress(position);
                    }
                });
    }

    public void chooseAddress(int position) {
        if (chooseAddressSub != null)
            return;
        chooseAddressSub = Observable.just(position).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer position) {

                try {
                    choosePoiResult = (isManual || isAddressManual) ? navigationManager.getPoiResultList().get(position)
                            : navigationManager.getPoiResultList().get(position - 1);
                    endPoint = new Point(choosePoiResult.getLatitude(), choosePoiResult.getLongitude());
                    if (choosePoiResult != null) {

                        if (navigationManager.getSearchType() == SearchType.SEARCH_COMMONPLACE) {
                            addCommonAddress(choosePoiResult);
                            return;
                        }
                        if (isManual) {
                            EventBus.getDefault().post(MapResultShow.STRATEGY);
                        } else {
                            showStrategyMethod(position);
                        }

                    }
                } catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                    if (!isManual) {
                        if (position > navigationManager.getPoiResultList().size()) {
                            navigationManager.getLog().debug("----- choose error stopUnderstanding");
                            voiceManager.stopUnderstanding();
                            String playText = "选择错误，请重新选择";
                            voiceManager.startSpeaking(playText,
                                    TTSType.TTS_START_UNDERSTANDING, false);
                        }
                    }
                }
            }
        });
    }

    /**
     * 添加常用地
     *
     * @param choosePoint
     */
    private void addCommonAddress(final PoiResultInfo choosePoint) {
        FloatWindowUtils.removeFloatWindow();
        final String addType = navigationManager.getCommonAddressType().getName();
        CommonAddressUtil.setCommonAddress(addType, context, choosePoint.getAddressTitle());
        CommonAddressUtil.setCommonLocation(addType,
                context, choosePoint.getLatitude(), choosePoint.getLongitude());
        navigationManager.getLog().debug("-----addCommonAddress stopUnderstanding");
        voiceManager.stopUnderstanding();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                voiceManager.startSpeaking("添加" + choosePoint.getAddressTitle() + "为 " + addType + " 地址成功！",
                        TTSType.TTS_DO_NOTHING, true);
            }
        }, 200);
        navigationManager.setSearchType(SearchType.SEARCH_DEFAULT);
        removeWindow();
    }

    /**
     * 语音选择路线优先策略
     */
    public void showStrategyMethod(int position) {
        if (chooseStrategyMethodSub != null)
            return;
        chooseStrategyMethodSub = Observable.just(position).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer position) {
                isShowAddress = true;
                navigationManager.getLog().debug("----showStrategyMethod stopUnderstanding");
                voiceManager.stopUnderstanding();
                voiceManager.clearMisUnderstandCount();
                if (!isAddressManual && position > navigationManager.getPoiResultList().size()) {
                    String playText = "选择错误，请重新选择";
                    voiceManager.startSpeaking(playText,
                            TTSType.TTS_START_UNDERSTANDING, false);
                    return;
                }
                chooseStep = 2;
                String playText = "请选择路线优先策略。";
                if (navigationManager.getSearchType() == SearchType.SEARCH_NEAREST)
                    playText = "已经为您找到最近的" + navigationManager.getKeyword() + ",请选择路线优先策略";
                    voiceManager.startSpeaking(playText,
                            TTSType.TTS_START_UNDERSTANDING, false);
                FloatWindowUtils.showStrategy(
                        new AdapterView.OnItemClickListener() {

                            @Override
                            public void onItemClick(
                                    AdapterView<?> arg0, View view,
                                    int position, long arg3) {
                                if (position >= 6)
                                    return;
                                startNavigation(new Navigation(endPoint, navigationManager.getDriveModeList().get(position),
                                        NavigationType.NAVIGATION));
                            }
                        });
            }
        });


    }

    // 选择路径规划策略
    public void chooseDriveMode(int position) {
        if (navigationManager.getPoiResultList().isEmpty())
            return;
        if (position > navigationManager.getDriveModeList().size()) {
            VoiceManagerProxy.getInstance().stopUnderstanding();
            String playText = "选择错误，请重新选择";
            VoiceManagerProxy.getInstance().startSpeaking(playText,
                    TTSType.TTS_START_UNDERSTANDING, false);
            return;
        }
        startNavigation(new Navigation(endPoint, navigationManager.getDriveModeList().get(position - 1),
                NavigationType.NAVIGATION));
    }

    public void onNextPage() {
        FloatWindowUtils.onNextPage();
    }

    public void onPreviousPage() {
        FloatWindowUtils.onPreviousPage();
    }

    public void onChoosePage(int page) {
        FloatWindowUtils.onChoosePage(page);
    }

    public void onChooseNumber(int option) {
        if (chooseStep == 1) {
            chooseAddress(option);
        } else {
            chooseDriveMode(option);
        }
    }

    public void startNavigation(Navigation navigation) {
        if (naviSubscription != null)
            return;
        naviSubscription = Observable.just(navigation)
                .subscribe(new Action1<Navigation>() {
                    @Override
                    public void call(Navigation navigation) {
                        navigationManager.getLog().debug("----startNavigation stopUnderstanding");
                        voiceManager.stopUnderstanding();
                        voiceManager.clearMisUnderstandCount();
                        voiceManager.startSpeaking("路径规划中，请稍后...", TTSType.TTS_DO_NOTHING, false);
                        FloatWindowUtils.removeFloatWindow();
                        if (NaviUtils.getOpenMode(context) == OpenMode.OUTSIDE) {
                            startNaviOutside(navigation);
                            return;
                        }
                        openActivity(OPEN_MANUAL);
                        showProgressDialog("路径规划中...");
                        isShowAddress = false;
                        isManual = false;
                        SemanticEngine.getProcessor().switchSemanticType(SceneType.HOME);
                        navigationManager.startCalculate(navigation);
                    }
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
        isShowAddress = false;
        if (navigationManager.getNavigationType() != NavigationType.DEFAULT) {
            navigationManager.setIsNavigatining(true);
        }
        if (navigationManager.getSearchType() == SearchType.SEARCH_DEFAULT) {
            handler.postDelayed(removeWindowRunnable, REMOVE_WINDOW_TIME);
        }
    }

    private void intentActivity() {
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
}
