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

import com.dudu.android.launcher.LauncherApplication;
import com.dudu.android.launcher.ui.activity.LocationMapActivity;
import com.dudu.android.launcher.ui.activity.NaviBackActivity;
import com.dudu.android.launcher.ui.activity.NaviCustomActivity;
import com.dudu.android.launcher.ui.dialog.WaitingDialog;
import com.dudu.android.launcher.utils.ActivitiesManager;
import com.dudu.android.launcher.utils.CommonAddressUtil;
import com.dudu.android.launcher.utils.Constants;
import com.dudu.android.launcher.utils.FloatWindowUtil;
import com.dudu.android.launcher.utils.Utils;
import com.dudu.event.MapResultShow;
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

/**
 * Created by lxh on 2015/11/26.
 */
public class NavigationClerk {

    private static final int REMOVEWINDOW_TIME = 9 * 1000;

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

    private Activity topActivity;

    private Handler mhandler;

    private PoiResultInfo choosepoiResult;

    private WaitingDialog waitingDialog = null;// 搜索时进度条

    private Class intentClass;

    private Runnable removeWindowRunnable = new Runnable() {

        @Override
        public void run() {

            switch (navigationManager.getNavigationType()) {
                case NAVIGATION:
                    intentClass = NaviCustomActivity.class;
                    break;
                case BACKNAVI:
                    intentClass = NaviBackActivity.class;
                    break;
                case DEFAULT:
                    return;
            }
            FloatWindowUtil.removeFloatWindow();
            intentActivity();
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

        switch (NaviUtils.getOpenMode(mContext)) {
            case INSIDE:
                return (openType==OPEN_MAP)?openMapActivity():openActivity(openType);
            case OUTSIDE:
                EventBus.getDefault().post(NaviEvent.FloatButtonEvent.SHOW);
                Utils.startThirdPartyApp(ActivitiesManager.getInstance().getTopActivity(), "com.autonavi.minimap");
                break;
        }
        return true;
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

        switch (navigationManager.getNavigationType()) {
            case NAVIGATION:
                FloatWindowUtil.removeFloatWindow();
                intentClass = NaviCustomActivity.class;
                break;
            case BACKNAVI:
                FloatWindowUtil.removeFloatWindow();
                intentClass = NaviCustomActivity.class;
                break;
            case DEFAULT:
                if (isMapActivity()) {
                    isSearching();
                }
                if (openType == OPEN_VOICE)
                    navigationManager.setSearchType(SearchType.OPEN_NAVI);
                intentClass = LocationMapActivity.class;
                break;
        }
        intentActivity();
        return true;
    }

    private boolean isSearching(){
        SearchType type = navigationManager.getSearchType();
        if (type == SearchType.OPEN_NAVI || type == SearchType.SEARCH_DEFAULT) {
            navigationManager.setSearchType(SearchType.OPEN_NAVI);
            navigationManager.search();
            return true;
        } else {
            return false;
        }
    }
    private boolean isMapActivity(){
        topActivity = ActivitiesManager.getInstance().getTopActivity();
        return (topActivity !=null && topActivity instanceof LocationMapActivity);
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
        if (navigationManager.getSearchType()==SearchType.SEARCH_COMMONADDRESS)
            type = SearchType.SEARCH_COMMONPLACE;
        navigationManager.setSearchType(type);
        navigationManager.setKeyword(null);
        if (keyword == null)
            navigationManager.parseKeyword(semantic, service);
        else
            navigationManager.setKeyword(keyword);

        if(!isMapActivity()){
            intentClass = LocationMapActivity.class;
            intentActivity();
            return;
        }
        doSearch();
    }

    public void doSearch() {
        String msg;
        switch (navigationManager.getSearchType()) {
            case SEARCH_DEFAULT:
                return;
            case SEARCH_PLACE:
            case SEARCH_PLACE_LOCATION:
            case SEARCH_NEARBY:
            case SEARCH_NEAREST:
            case SEARCH_COMMONPLACE:
                if (TextUtils.isEmpty(navigationManager.getKeyword())) {
                    VoiceManager.getInstance().startSpeaking("关键字有误，请重新输入！",
                            SemanticConstants.TTS_START_UNDERSTANDING, true);
                    return;
                }
                msg = "正在搜索:" + navigationManager.getKeyword();
                if (Constants.CURRENT_POI.equals(navigationManager.getKeyword())) {
                    navigationManager.setSearchType(SearchType.SEARCH_CUR_LOCATION);
                    msg = "正在获取当前位置信息";
                }
                showProgressDialog(msg);
                break;

        }
        navigationManager.search();
    }

    public void onEventMainThread(NaviEvent.NaviVoiceBroadcast event) {
        VoiceManager.getInstance().clearMisUnderstandCount();
        if (navigationManager.getNavigationType() == NavigationType.DEFAULT) {
            removeCallback();
            VoiceManager.getInstance().stopUnderstanding();
            VoiceManager.getInstance().startSpeaking(event.getNaviVoice(), SemanticConstants.TTS_START_UNDERSTANDING, true);
        } else {
            if (FloatWindowUtil.IsWindowShow()) {
                VoiceManager.getInstance().startSpeaking(event.getNaviVoice(), SemanticConstants.TTS_START_UNDERSTANDING, false);
            } else {
                VoiceManager.getInstance().stopUnderstanding();
                VoiceManager.getInstance().startSpeaking(event.getNaviVoice(), SemanticConstants.TTS_DO_NOTHING, false);
            }
        }
    }

    public void onEventMainThread(NaviEvent.SearchResult event) {
        disMissProgressDialog();
        navigationManager.getLog().debug("----SearchResult: {}", navigationManager.getSearchType());
        if (event == NaviEvent.SearchResult.SUCCESS) {
            handlerPoiResult();
        }
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

        if (navigationManager.getNavigationType() == NavigationType.NAVIGATION)
            return;
        switch (event) {
            case NAVIGATION:
                intentClass = NaviCustomActivity.class;
                break;
            case BACKNAVI:
                intentClass = NaviBackActivity.class;
                break;
            case CALCULATEERROR:
                disMissProgressDialog();
                VoiceManager.getInstance().startSpeaking("路径规划出错，请检查网络", SemanticConstants.TTS_DO_NOTHING, true);
                toNaivActivity(REMOVEWINDOW_TIME);
                return;
        }
        intentActivity();

    }

    public void handlerPoiResult() {

        switch (navigationManager.getSearchType()) {
            case SEARCH_CUR_LOCATION:
                mhandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        VoiceManager.getInstance().startSpeaking(navigationManager.getCurlocationDesc(),
                                SemanticConstants.TTS_START_UNDERSTANDING, true);
                    }
                }, 200);
                break;
            case SEARCH_PLACE_LOCATION:
                String playText = "您好，" + navigationManager.getKeyword() + "的位置为："
                        + navigationManager.getPoiResultList().get(0).getAddressDetial();
                VoiceManager.getInstance().startSpeaking(playText, SemanticConstants.TTS_START_UNDERSTANDING, true);
                return;
            case SEARCH_NEAREST:
                showStrategyMethod(0);
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

        if (waitingDialog != null) {
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

        initWaitingDialog(message);
        waitingDialog.show();
        switch (navigationManager.getSearchType()) {
            case SEARCH_PLACE_LOCATION:
            case SEARCH_CUR_LOCATION:
                return;
        }
        FloatWindowUtil.removeFloatWindow();
    }

    public void disMissProgressDialog() {
        if (waitingDialog != null && waitingDialog.isShowing()) {
            waitingDialog.dismiss();
        }
    }

    public void showAddressByVoice() {
        VoiceManager.getInstance().startSpeaking(
                "请选择列表中的地址", SemanticConstants.TTS_START_UNDERSTANDING, true);
        FloatWindowUtil.showAddress(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(
                            AdapterView<?> arg0,
                            View arg1,
                            int position, long arg3) {
                        isAddressManual = true;
                        VoiceManager.getInstance().stopUnderstanding();
                        chooseAddress(position);
                    }
                });
    }

    public void chooseAddress(int position) {
        try {
            choosepoiResult = (isManual || isAddressManual) ? navigationManager.getPoiResultList().get(position)
                    : navigationManager.getPoiResultList().get(position - 1);
            endPoint = new Point(choosepoiResult.getLatitude(), choosepoiResult.getLongitude());
            if (choosepoiResult != null) {

                if (navigationManager.getSearchType()==SearchType.SEARCH_COMMONPLACE) {
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
                    VoiceManager.getInstance().stopUnderstanding();
                    String playText = "选择错误，请重新选择";
                    VoiceManager.getInstance().startSpeaking(playText,
                            SemanticConstants.TTS_START_UNDERSTANDING, false);
                }
            }
        }

    }

    /**
     * 添加常用地
     *
     * @param choosePoint
     */
    private void addCommonAddress(PoiResultInfo choosePoint) {
        String addType = navigationManager.getCommonAddressType().getName();
        CommonAddressUtil.setCommonAddress(addType, mContext, choosePoint.getAddressTitle());
        CommonAddressUtil.setCommonLocation(addType,
                mContext, choosePoint.getLatitude(), choosePoint.getLongitude());
        VoiceManager.getInstance().stopUnderstanding();
        VoiceManager.getInstance().startSpeaking("添加" + choosePoint.getAddressTitle() + "为" + addType + "地址成功！",
                SemanticConstants.TTS_DO_NOTHING, true);
        navigationManager.setSearchType(SearchType.SEARCH_DEFAULT);

    }

    /**
     * 语音选择路线优先策略
     */
    public void showStrategyMethod(int position) {
        if (!isAddressManual && position > navigationManager.getPoiResultList().size()) {
            VoiceManager.getInstance().stopUnderstanding();
            String playText = "选择错误，请重新选择";
            VoiceManager.getInstance().startSpeaking(playText,
                    SemanticConstants.TTS_START_UNDERSTANDING, false);
            return;
        }
        VoiceManager.getInstance().stopUnderstanding();
        VoiceManager.getInstance().clearMisUnderstandCount();
        chooseStep = 2;
        String playText = "请选择路线优先策略。";
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

    // 选择路径规划策略
    public void chooseDriveMode(int position) {
        if (navigationManager.getPoiResultList().isEmpty())
            return;
        if (position > navigationManager.getDriveModeList().size()) {
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
        if (waitingDialog != null) {
            waitingDialog.setMessage("路径规划中...");
            waitingDialog.show();
        }
        if (NaviUtils.getOpenMode(mContext) == OpenMode.OUTSIDE) {
            LauncherApplication.getContext().setReceivingOrder(true);
        }
        isShowAddress = false;
        isManual = false;
        SemanticProcessor.getProcessor().switchSemanticType(SemanticType.NORMAL);
        navigationManager.setSearchType(SearchType.SEARCH_DEFAULT);
        FloatWindowUtil.removeFloatWindow();
        VoiceManager.getInstance().stopUnderstanding();
        navigationManager.startCalculate(navigation);
    }


    public void removeCallback() {
        if (mhandler != null && removeWindowRunnable != null) {
            mhandler.removeCallbacks(removeWindowRunnable);
        }
    }

    public void toNaivActivity(int t) {
        mhandler.postDelayed(removeWindowRunnable, REMOVEWINDOW_TIME);
    }

    private void intentActivity(){
        topActivity = ActivitiesManager.getInstance().getTopActivity();
        Intent standIntent = new Intent(topActivity, intentClass);
        standIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                | Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(standIntent);
        if(isMapActivity()){
            topActivity.finish();
        }
    }
}
