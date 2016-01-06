package com.dudu.map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
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
import com.dudu.android.launcher.ui.dialog.WaitingDialog;
import com.dudu.android.launcher.utils.ActivitiesManager;
import com.dudu.android.launcher.utils.CommonAddressUtil;
import com.dudu.android.launcher.utils.Constants;
import com.dudu.android.launcher.utils.FloatWindowUtil;
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
import com.dudu.voice.semantic.SemanticConstants;
import com.dudu.voice.semantic.SemanticType;
import com.dudu.voice.semantic.VoiceManager;
import com.dudu.voice.semantic.chain.ChoiseChain;
import com.dudu.voice.semantic.chain.ChoosePageChain;
import com.dudu.voice.semantic.engine.SemanticProcessor;

import de.greenrobot.event.EventBus;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by lxh on 2015/11/26.
 */
public class NavigationClerk {

    private static final int REMOVEWINDOW_TIME = 6 * 1000;

    private static NavigationClerk navigationClerk;

    private Context mContext;

    private NavigationManager navigationManager;

    public static final int OPEN_MANUAL = 1;

    public static final int OPEN_VOICE = 2;

    public static final int OPEN_MAP = 3;

    private boolean isAddressManual = false;

    private int chooseStep;

    private Point endPoint;

    private boolean isManual = false;

    private boolean isShowAddress = false;

    private Handler mhandler;

    private PoiResultInfo choosepoiResult;

    private WaitingDialog waitingDialog = null;// 搜索时进度条

    private Class intentClass;
    private String msg;
    private Subscription naviSubscription = null;
    private Subscription choosestrategyMethodSub = null;
    private Subscription chooseAddressSub = null;

    private long mLastClickTime = 0;

    private Runnable removeWindowRunnable = new Runnable() {

        @Override
        public void run() {
            FloatWindowUtil.removeFloatWindow();
        }
    };

    public void setIsShowAddress(boolean isShowAddress) {
        this.isShowAddress = isShowAddress;
    }

    public boolean isShowAddress() {
        return isShowAddress;
    }

    public void setIsManual(boolean isManual) {
        this.isManual = isManual;
    }

    public PoiResultInfo getChoosepoiResult() {
        return choosepoiResult;
    }

    public NavigationClerk() {
        this.mContext = LauncherApplication.getContext();
        navigationManager = NavigationManager.getInstance(mContext);
        EventBus.getDefault().unregister(this);
        EventBus.getDefault().register(this);
        mhandler = new Handler();
    }

    public static NavigationClerk getInstance() {
        if (navigationClerk == null)
            navigationClerk = new NavigationClerk();
        return navigationClerk;
    }


    public boolean openNavi(int openType) {
        if (checkFastClick()) {
            return false;
        }
        switch (NaviUtils.getOpenMode(mContext)) {
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
            FloatWindowUtil.removeFloatWindow();
        } else {
            if (!isMapActivity()) {
                intentClass = LocationMapActivity.class;
                if (openType == OPEN_VOICE)
                    navigationManager.setSearchType(SearchType.OPEN_NAVI);
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
        navigationManager.setKeyword(null);
        if (keyword == null)
            navigationManager.parseKeyword(semantic, service);
        else
            navigationManager.setKeyword(keyword);
        doSearch();
    }

    public void doSearch() {
        chooseAddressSub = null;
        choosestrategyMethodSub = null;
        if (NaviUtils.getOpenMode(mContext) == OpenMode.INSIDE) {
            if (!navigationManager.isNavigatining() && !isMapActivity()) {
                intentClass = LocationMapActivity.class;
                intentActivity();
                return;
            }
            isNaviActivity();
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
        mhandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                navigationManager.search();
            }
        }, 2000);

    }

    private void searchHint() {
        msg = "正在搜索" + navigationManager.getKeyword();
        boolean isShow = false;
        if (Monitor.getInstance(mContext).getCurrentLocation() == null) {
            navigationManager.setSearchType(SearchType.SEARCH_DEFAULT);
            msg = "暂未获取到您的当前位置，不能搜索，请稍后再试";
            if (isManual) {
                ToastUtils.showToast(msg);
            } else {
                VoiceManager.getInstance().startSpeaking(msg, SemanticConstants.TTS_DO_NOTHING, true);
                removeWindow();
            }
            return;
        }
        if (TextUtils.isEmpty(navigationManager.getKeyword())) {
            VoiceManager.getInstance().startSpeaking("关键字有误，请重新输入！",
                    SemanticConstants.TTS_START_UNDERSTANDING, true);
            return;
        }
        msg = "正在搜索 " + navigationManager.getKeyword();
        if (Constants.CURRENT_POI.equals(navigationManager.getKeyword())) {
            navigationManager.setSearchType(SearchType.SEARCH_CUR_LOCATION);
            msg = "正在获取您的当前位置";
            isShow = true;
        }
        if (!isManual)
            VoiceManager.getInstance().startSpeaking(msg, SemanticConstants.TTS_DO_NOTHING, isShow);
        showProgressDialog(msg);
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
        VoiceManager.getInstance().clearMisUnderstandCount();
        VoiceManager.getInstance().stopUnderstanding();
        removeCallback();
        VoiceManager.getInstance().startSpeaking(event.getNaviVoice(), SemanticConstants.TTS_START_UNDERSTANDING, true);
        removeWindow();

    }

    public void onEventMainThread(NaviEvent.SearchResult event) {
        removeCallback();
        navigationManager.getLog().debug("----SearchResult: {}", navigationManager.getSearchType());
        if (event == NaviEvent.SearchResult.SUCCESS) {
            handlerPoiResult();
        } else {
            if (isManual)
                ToastUtils.showToast("抱歉,搜索失败，请稍后重试");
            navigationManager.setSearchType(SearchType.SEARCH_DEFAULT);
        }
        disMissProgressDialog();
    }

    public void onEventBackgroundThread(NaviEvent.ChangeSemanticType event) {
        SemanticType type = null;
        switch (event) {
            case MAP_CHOISE:
                type = SemanticType.MAP_CHOISE;
                break;
            case NORMAL:
                type = SemanticType.NORMAL;
                break;
            case NAVIGATION:
                type = SemanticType.NAVIGATION;
                break;
        }
        SemanticProcessor.getProcessor().switchSemanticType(type);
    }

    public void onEventMainThread(NavigationType event) {
        disMissProgressDialog();
        removeCallback();
        naviSubscription = null;
        switch (event) {
            case NAVIGATION:
                navigationManager.setNavigationType(NavigationType.NAVIGATION);
                intentClass = NaviCustomActivity.class;
                if (isMapActivity())
                    ActivitiesManager.getInstance().closeTargetActivity(LocationMapActivity.class);
                break;
            case BACKNAVI:
                navigationManager.setNavigationType(NavigationType.BACKNAVI);
                intentClass = NaviBackActivity.class;
                break;
            case CALCULATEERROR:
                navigationManager.setSearchType(SearchType.SEARCH_DEFAULT);
                VoiceManager.getInstance().startSpeaking("路径规划出错，请稍后再试", SemanticConstants.TTS_DO_NOTHING, true);
                removeWindow();
                return;
            case NAVIGATION_END:
                navigationManager.setNavigationType(NavigationType.DEFAULT);
                intentClass = LocationMapActivity.class;
                ActivitiesManager.getInstance().closeTargetActivity(NaviCustomActivity.class);
                ActivitiesManager.getInstance().closeTargetActivity(NaviBackActivity.class);
                break;
        }
        navigationManager.setSearchType(SearchType.SEARCH_DEFAULT);
        intentActivity();

    }

    public void handlerPoiResult() {
        endPoint = null;
        choosepoiResult = null;
        switch (navigationManager.getSearchType()) {
            case SEARCH_CUR_LOCATION:
                mhandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        VoiceManager.getInstance().startSpeaking(navigationManager.getCurlocationDesc(),
                                SemanticConstants.TTS_DO_NOTHING, true);
                    }
                }, 200);
                navigationManager.setSearchType(SearchType.SEARCH_DEFAULT);
                removeWindow();
                break;
            case SEARCH_PLACE_LOCATION:
                String playText = "您好，" + navigationManager.getKeyword() + "的位置为："
                        + navigationManager.getPoiResultList().get(0).getAddressDetial();
                VoiceManager.getInstance().startSpeaking(playText, SemanticConstants.TTS_DO_NOTHING, true);
                navigationManager.setSearchType(SearchType.SEARCH_DEFAULT);
                removeWindow();
                return;
            case SEARCH_NEAREST:
                SemanticProcessor.getProcessor().switchSemanticType(
                        SemanticType.MAP_CHOISE);
                endPoint = new Point(navigationManager.getPoiResultList().get(0).getLatitude(),
                        navigationManager.getPoiResultList().get(0).getLongitude());
                showStrategyMethod(0);
                navigationManager.setSearchType(SearchType.SEARCH_DEFAULT);
                return;
            default:
                SemanticProcessor.getProcessor().switchSemanticType(
                        SemanticType.MAP_CHOISE);
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

        FloatWindowUtil.removeFloatWindow();
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
        VoiceManager.getInstance().startSpeaking(
                "为您找到以下地址，请选择第几个", SemanticConstants.TTS_START_UNDERSTANDING, true);
        FloatWindowUtil.showAddress(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(
                            AdapterView<?> arg0,
                            View arg1,
                            int position, long arg3) {
                        isAddressManual = true;
                        navigationManager.getLog().debug("-----manual click showAddressByVoice stopUnderstanding");
                        VoiceManager.getInstance().stopUnderstanding();
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
                    choosepoiResult = (isManual || isAddressManual) ? navigationManager.getPoiResultList().get(position)
                            : navigationManager.getPoiResultList().get(position - 1);
                    endPoint = new Point(choosepoiResult.getLatitude(), choosepoiResult.getLongitude());
                    if (choosepoiResult != null) {

                        if (navigationManager.getSearchType() == SearchType.SEARCH_COMMONPLACE) {
                            addCommonAddress(choosepoiResult);
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
                            VoiceManager.getInstance().stopUnderstanding();
                            String playText = "选择错误，请重新选择";
                            VoiceManager.getInstance().startSpeaking(playText,
                                    SemanticConstants.TTS_START_UNDERSTANDING, false);
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
        FloatWindowUtil.removeFloatWindow();
        final String addType = navigationManager.getCommonAddressType().getName();
        CommonAddressUtil.setCommonAddress(addType, mContext, choosePoint.getAddressTitle());
        CommonAddressUtil.setCommonLocation(addType,
                mContext, choosePoint.getLatitude(), choosePoint.getLongitude());
        navigationManager.getLog().debug("-----addCommonAddress stopUnderstanding");
        VoiceManager.getInstance().stopUnderstanding();
        mhandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                VoiceManager.getInstance().startSpeaking("添加" + choosePoint.getAddressTitle() + "为 " + addType + " 地址成功！",
                        SemanticConstants.TTS_DO_NOTHING, true);
            }
        }, 200);
        navigationManager.setSearchType(SearchType.SEARCH_DEFAULT);
        removeWindow();
    }

    /**
     * 语音选择路线优先策略
     */
    public void showStrategyMethod(int position) {
        if (choosestrategyMethodSub != null)
            return;
        choosestrategyMethodSub = Observable.just(position).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer position) {
                isShowAddress = true;
                navigationManager.getLog().debug("----showStrategyMethod stopUnderstanding");
                VoiceManager.getInstance().stopUnderstanding();
                VoiceManager.getInstance().clearMisUnderstandCount();
                if (!isAddressManual && position > navigationManager.getPoiResultList().size()) {
                    String playText = "选择错误，请重新选择";
                    VoiceManager.getInstance().startSpeaking(playText,
                            SemanticConstants.TTS_START_UNDERSTANDING, false);
                    return;
                }
                chooseStep = 2;
                String playText = "请选择路线优先策略。";
                if (navigationManager.getSearchType() == SearchType.SEARCH_NEAREST)
                    playText = "已经为您找到最近的" + navigationManager.getKeyword() + ",请选择路线优先策略";
                VoiceManager.getInstance().startSpeaking(playText,
                        SemanticConstants.TTS_START_UNDERSTANDING, false);
                FloatWindowUtil.showStrategy(
                        new AdapterView.OnItemClickListener() {

                            @Override
                            public void onItemClick(
                                    AdapterView<?> arg0, View view,
                                    int position, long arg3) {
                                startNavigation(new Navigation(endPoint, navigationManager.getDriveModeList().get(position),
                                        NavigationType.NAVIGATION));
                            }
                        });
            }
        });


    }

    // 选择路径规划策略
    public void chooseDriveMode(int position) {
        if (position > navigationManager.getDriveModeList().size()) {
            navigationManager.getLog().debug("-----choose error DriveMode stopUnderstanding");
            VoiceManager.getInstance().stopUnderstanding();
            String playText = "选择错误，请重新选择";
            VoiceManager.getInstance().startSpeaking(playText,
                    SemanticConstants.TTS_START_UNDERSTANDING, false);
            return;
        }
        startNavigation(new Navigation(endPoint, navigationManager.getDriveModeList().get(position - 1),
                NavigationType.NAVIGATION));
    }


    public void startChooseResult(int size, int type) {
        if (type == ChoiseChain.TYPE_NORMAL) {

            if (chooseStep == 1) {
                chooseAddress(size);
            } else {
                chooseDriveMode(size);
            }
        } else {
            FloatWindowUtil.chooseAddressPage(ChoosePageChain.CHOOSE_PAGE, size);

        }

    }

    public void choosePage(int type) {

        if (type == ChoosePageChain.NEXT_PAGE) {
            FloatWindowUtil.chooseAddressPage(ChoosePageChain.NEXT_PAGE, 0);
        } else {
            FloatWindowUtil.chooseAddressPage(ChoosePageChain.LAST_PAGE, 0);
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
                        VoiceManager.getInstance().stopUnderstanding();
                        VoiceManager.getInstance().clearMisUnderstandCount();
                        VoiceManager.getInstance().startSpeaking("路径规划中，请稍后...", SemanticConstants.TTS_DO_NOTHING, false);
                        FloatWindowUtil.removeFloatWindow();
                        if (NaviUtils.getOpenMode(mContext) == OpenMode.OUTSIDE) {
                            startNaviOutside(navigation);
                            return;
                        }
                        openActivity(OPEN_MANUAL);
                        showProgressDialog("路径规划中...");
                        isShowAddress = false;
                        isManual = false;
                        SemanticProcessor.getProcessor().switchSemanticType(SemanticType.NORMAL);
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
            AMapUtils.openAMapNavi(naviPara, mContext);
            LauncherApplication.getContext().setReceivingOrder(true);
            naviSubscription = null;
        } catch (AMapException e) {
            e.printStackTrace();
        }
    }

    public void removeCallback() {
        if (mhandler != null && removeWindowRunnable != null) {
            mhandler.removeCallbacks(removeWindowRunnable);
        }
    }

    public void removeWindow() {
        disMissProgressDialog();
        isShowAddress = false;
        if (navigationManager.getNavigationType() != NavigationType.DEFAULT) {
            navigationManager.setIsNavigatining(true);
        }
        if (navigationManager.getSearchType() == SearchType.SEARCH_DEFAULT) {
            mhandler.postDelayed(removeWindowRunnable, REMOVEWINDOW_TIME);
        }
    }

    private void intentActivity() {
        Intent standIntent = new Intent(mContext, intentClass);
        standIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                | Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(standIntent);
        ActivitiesManager.getInstance().closeTargetActivity(LocationMapActivity.class);
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
        if (!FloatWindowUtil.IsWindowShow()) {
            VoiceManager.getInstance().clearMisUnderstandCount();
            VoiceManager.getInstance().startSpeaking(event.getInfo(), SemanticConstants.TTS_DO_NOTHING, false);
        }
    }

    public void openGaode() {
        EventBus.getDefault().post(NaviEvent.FloatButtonEvent.SHOW);
        Utils.startThirdPartyApp(ActivitiesManager.getInstance().getTopActivity(), "com.autonavi.minimap");
    }
}
