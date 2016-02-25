package com.dudu.aios.ui.map.observable;

import android.content.Context;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.dudu.aios.ui.map.MapDbHelper;
import com.dudu.aios.ui.map.MyLinearLayoutManager;
import com.dudu.aios.ui.map.adapter.MapListAdapter;
import com.dudu.aios.ui.map.adapter.RouteStrategyAdapter;
import com.dudu.aios.ui.map.event.ChooseEvent;
import com.dudu.aios.ui.voice.VoiceEvent;
import com.dudu.android.launcher.R;
import com.dudu.android.launcher.databinding.GaodeMapLayoutBinding;
import com.dudu.android.launcher.utils.Constants;
import com.dudu.event.MapResultShow;
import com.dudu.map.NavigationProxy;
import com.dudu.navi.NavigationManager;
import com.dudu.navi.entity.Navigation;
import com.dudu.navi.entity.PoiResultInfo;
import com.dudu.navi.entity.Point;
import com.dudu.navi.vauleObject.NaviDriveMode;
import com.dudu.navi.vauleObject.NavigationType;
import com.dudu.navi.vauleObject.SearchType;
import com.dudu.voice.VoiceManagerProxy;
import com.dudu.voice.semantic.constant.SceneType;
import com.dudu.voice.semantic.constant.TTSType;
import com.dudu.voice.semantic.engine.SemanticEngine;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;
import rx.Observable;
import rx.Subscription;

/**
 * Created by lxh on 2016/2/13.
 */
public class MapObservable {

    public static final int ADDRESS_VIEW_COUNT = 4;

    public final ObservableBoolean showList = new ObservableBoolean();
    public final ObservableBoolean showBottomButton = new ObservableBoolean();
    public final ObservableField<String> mapListTitle = new ObservableField<>();
    public final ObservableBoolean showEdt = new ObservableBoolean();
    public final ObservableInt historyCount = new ObservableInt();
    public final ObservableBoolean showDelete = new ObservableBoolean();

    private GaodeMapLayoutBinding binding;

    private Context mContext;

    public ArrayList<MapListItemObservable> mapList;

    private MapListAdapter mapListAdapter;

    private RouteStrategyAdapter routeStrategyAdapter;

    private NavigationManager navigationManager;

    private NavigationProxy navigationProxy;

    private MapDbHelper mapDbHelper;

    private Subscription chooseStrategyMethodSub = null;

    private Subscription chooseAddressSub = null;

    private MapListItemObservable itemObservable;

    private int pageIndex;

    public MapObservable(GaodeMapLayoutBinding binding) {

        this.binding = binding;
        this.mContext = binding.getRoot().getContext();
    }

    public void init() {

        showEdt.set(false);
        showList.set(false);
        showBottomButton.set(true);
        showDelete.set(false);

        EventBus.getDefault().register(this);

        mapList = new ArrayList<>();

        navigationManager = NavigationManager.getInstance(binding.getRoot().getContext());
        navigationProxy = NavigationProxy.getInstance();

        initData();
    }

    private void initData() {
        mapDbHelper = MapDbHelper.getDbHelper();

        mapList = mapDbHelper.getHistory();

        if (!mapList.isEmpty()) {
            mapListAdapter = new MapListAdapter(mapList, (view, postion) -> historyNavi(postion));
            binding.mapListView.setAdapter(mapListAdapter);
        }

        routeStrategyAdapter = new RouteStrategyAdapter((view, postion) -> {
            chooseDriveMode(postion);
        });

    }

    private void historyNavi(int position) {
        Point point = new Point(mapList.get(position).lat.get(), mapList.get(position).lon.get());
        Navigation navigation = new Navigation(point, NaviDriveMode.SPEEDFIRST, NavigationType.NAVIGATION);
        navigationProxy.startNavigation(navigation);

    }

    public void mapSearchBtn(View view) {
        this.showEdt.set(showEdt.get() ? false : true);
    }

    public void mapSearchEdt(View view) {

        if (historyCount.get() > 0)
            showList.set(true);
    }

    public void deleteEdt(View view) {

        binding.mapSearchEdt.setText("");
        showList.set(false);
        showBottomButton.set(true);
    }

    public void searchManual(View view) {
        navigationProxy.setIsManual(true);
        if (TextUtils.isEmpty(binding.mapSearchEdt.getText().toString()))
            return;
        if (containsEmoji(binding.mapSearchEdt.getText().toString())) {
            VoiceManagerProxy.getInstance().startSpeaking(mContext.getString(R.string.notice_searchKeyword));
            return;
        }

        navigationManager.setSearchType(SearchType.SEARCH_PLACE);
        navigationManager.setKeyword(binding.mapSearchEdt.getText().toString());
        navigationProxy.doSearch();
    }

    private static boolean containsEmoji(String str) {
        int len = str.length();
        for (int i = 0; i < len; i++) {
            if (isEmojiCharacter(str.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    private static boolean isEmojiCharacter(char codePoint) {
        return !((codePoint == 0x0) ||
                (codePoint == 0x9) ||
                (codePoint == 0xA) ||
                (codePoint == 0xD) ||
                ((codePoint >= 0x20) && (codePoint <= 0xD7FF)) ||
                ((codePoint >= 0xE000) && (codePoint <= 0xFFFD)) ||
                ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF)));
    }


    public void onEventMainThread(MapResultShow event) {

        showEdt.set(navigationProxy.isManual() ? true : false);
        showList.set(true);
        showBottomButton.set(false);
        chooseAddressSub = null;
        chooseStrategyMethodSub = null;

        navigationProxy.setShowList(true);
        SemanticEngine.getProcessor().switchSemanticType(SceneType.MAP_CHOISE);

        switch (event) {
            case ADDRESS:
                showAddress();
                break;
            case STRATEGY:
                showStrategy();
                break;
        }

    }


    private void showAddress() {

        if (navigationProxy.isManual()) {
            mapListTitle.set("共找到" + navigationManager.getPoiResultList().size() + "个结果");
        } else {

            VoiceManagerProxy.getInstance().stopUnderstanding();
            VoiceManagerProxy.getInstance().clearMisUnderstandCount();
            VoiceManagerProxy.getInstance().startSpeaking(binding.getRoot().getContext().getString(R.string.plaseChoose_place),
                    TTSType.TTS_START_UNDERSTANDING, false);

            mapListTitle.set(binding.getRoot().getContext().getString(R.string.search_result));
        }

        mapList.clear();
        mapList = getmapList();
        mapListAdapter = new MapListAdapter(mapList, (view, position) -> {
            if (position > mapList.size()) {
                return;
            }
            chooseAddress(mapList.get(position).poiResult.get(), position);
        });
        binding.mapListView.setAdapter(mapListAdapter);
        showList.set(true);
        navigationProxy.setChooseStep(1);
    }


    private void showStrategy() {

        if (!navigationProxy.isManual()) {

            VoiceManagerProxy.getInstance().stopUnderstanding();
            VoiceManagerProxy.getInstance().clearMisUnderstandCount();
            VoiceManagerProxy.getInstance().startSpeaking(binding.getRoot().getContext().getString(R.string.plaseChoose_strategy),
                    TTSType.TTS_START_UNDERSTANDING, false);
        }

        mapListTitle.set(binding.getRoot().getContext().getString(R.string.choose_strategy));
        mapList.clear();
        mapListAdapter = null;
        binding.mapListView.setAdapter(routeStrategyAdapter);

        navigationProxy.setChooseStep(2);

    }


    public void chooseAddress(PoiResultInfo result, int position) {
        if (chooseAddressSub != null)
            return;
        Log.d("lxh", "------------chooseAddress ");
        chooseAddressSub = Observable.just(result).subscribe(poiResultInfo -> {

            if (position > mapList.size() && !navigationProxy.isManual()) {
                VoiceManagerProxy.getInstance().startSpeaking(binding.getRoot().getContext().getString(R.string.choose_error),
                        TTSType.TTS_START_UNDERSTANDING, false);
                return;
            }
            navigationProxy.endPoint = new Point(poiResultInfo.getLatitude(), poiResultInfo.getLongitude());
            if (navigationManager.getSearchType() == SearchType.SEARCH_COMMONPLACE) {
                navigationProxy.addCommonAddress(poiResultInfo);
                return;
            }
            showStrategy();
        });
    }


    public void chooseDriveMode(int position) {
        if (navigationManager.getPoiResultList().isEmpty())
            return;

        if (chooseStrategyMethodSub != null) {
            return;
        }

        showList.set(false);
        showBottomButton.set(true);
        showEdt.set(false);

        chooseStrategyMethodSub = Observable.just(position).subscribe(integer -> {
            if (position > routeStrategyAdapter.getDriveModeList().size()) {
                VoiceManagerProxy.getInstance().stopUnderstanding();
                VoiceManagerProxy.getInstance().startSpeaking(binding.getRoot().getContext().getString(R.string.choose_error),
                        TTSType.TTS_START_UNDERSTANDING, false);
                return;
            }
            navigationProxy.startNavigation(new Navigation(navigationProxy.endPoint,
                    routeStrategyAdapter.getDriveModeList().get(position).driveMode.get(),
                    NavigationType.NAVIGATION));
        });


    }


    private ArrayList<MapListItemObservable> getmapList() {

        ArrayList<MapListItemObservable> list = new ArrayList<>();

        for (int i = 0; i < navigationManager.getPoiResultList().size(); i++) {

            PoiResultInfo poiResultInfo = navigationManager.getPoiResultList().get(i);
            MapListItemObservable mapListItemObservable = new MapListItemObservable(poiResultInfo, i + 1 + ".", !navigationProxy.isManual());
            list.add(mapListItemObservable);
        }
        return list;
    }


    public void onEventMainThread(VoiceEvent event) {

        switch (event) {
            case THRICE_UNSTUDIED:

                displayList();
                break;
        }

    }

    public void onEventMainThread(ChooseEvent event) {

        MyLinearLayoutManager lm = (MyLinearLayoutManager) binding.mapListView.getLayoutManager();
        pageIndex = (int) Math.floor(lm.findFirstVisibleItemPosition() / Constants.ADDRESS_VIEW_COUNT);
        switch (event.getChooseType()) {

            case ChooseEvent.ADDRESS_NUMBER:
                chooseAddress(mapList.get(event.getPosition() - 1).poiResult.get(), event.getPosition() - 1);
                break;
            case ChooseEvent.STRATEGY_NUMBER:
                chooseDriveMode(event.getPosition() - 1);
                break;
            case ChooseEvent.NEXTPAGE:
                nextPage(lm);
                break;
            case ChooseEvent.PREVIOUSPAGE:
                previousPage(lm);
                break;
            case ChooseEvent.CHOOSEPAGE:
                choosePage(event.getPosition());
        }

    }


    private void nextPage(LinearLayoutManager lm) {
        if (lm.findLastVisibleItemPosition() == mapList.size() - 1) {
            VoiceManagerProxy.getInstance().stopUnderstanding();
            VoiceManagerProxy.getInstance().startSpeaking("已经是最后一页", TTSType.TTS_START_UNDERSTANDING, false);
            return;
        }

        pageIndex++;
        binding.mapListView.scrollToPosition(pageIndex * MapObservable.ADDRESS_VIEW_COUNT + 3);
    }


    private void previousPage(LinearLayoutManager lm) {
        if (pageIndex <= 0) {
            VoiceManagerProxy.getInstance().stopUnderstanding();
            VoiceManagerProxy.getInstance().startSpeaking("已经是第一页", TTSType.TTS_START_UNDERSTANDING, false);
            return;
        }
        pageIndex--;
        binding.mapListView.scrollToPosition(pageIndex * MapObservable.ADDRESS_VIEW_COUNT - 3);
    }

    private void choosePage(int page) {
        if (page > 5 || page < 1) {
            VoiceManagerProxy.getInstance().stopUnderstanding();
            VoiceManagerProxy.getInstance().startSpeaking("选择错误，请重新选择", TTSType.TTS_START_UNDERSTANDING, false);
            return;
        }
        pageIndex = page - 1;
        int page_i = pageIndex * MapObservable.ADDRESS_VIEW_COUNT;
        binding.mapListView.scrollToPosition(
                page == 1 ? page_i : page_i + 3);
    }


    public void release() {
        EventBus.getDefault().unregister(this);
        displayList();
    }


    public void displayList() {
        showList.set(false);
        showBottomButton.set(true);
        navigationProxy.setShowList(false);
        navigationProxy.setIsManual(false);
        navigationProxy.setIsManual(false);
        navigationProxy.disMissProgressDialog();
        navigationProxy.removeCallback();
        navigationProxy.setShowList(false);
        navigationProxy.naviSubscription = null;

        mapList.clear();

        navigationManager.setSearchType(SearchType.SEARCH_DEFAULT);

        SemanticEngine.getProcessor().switchSemanticType(SceneType.HOME);

        VoiceManagerProxy.getInstance().onStop();
    }


}
