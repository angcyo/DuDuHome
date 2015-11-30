package com.dudu.navi;

import android.content.Context;

import com.amap.api.services.core.PoiItem;
import com.dudu.navi.entity.Navigation;
import com.dudu.navi.entity.PoiResultInfo;
import com.dudu.navi.repo.ResourceManager;
import com.dudu.navi.service.KeywordProcess;
import com.dudu.navi.service.NaviProcess;
import com.dudu.navi.service.SearchProcess;
import com.dudu.navi.vauleObject.CommonAddressType;
import com.dudu.navi.vauleObject.NaviDriveMode;
import com.dudu.navi.vauleObject.NavigationType;
import com.dudu.navi.vauleObject.SearchType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


/**
 * Created by lxh on 2015/11/14.
 */
public class NavigationManager {

    private static NavigationManager navigationManager;

    private Context mContext;

    private Logger log;

    private NavigationType navigationType = NavigationType.DEFAULT;

    private SearchType searchType = SearchType.SEARCH_DEFAULT;

    private String keyword;

    private CommonAddressType commonAddressType;

    private boolean isNavigatining = false;

    public boolean isNavigatining() {
        return isNavigatining;
    }

    public void setIsNavigatining(boolean isNavigatining) {

        this.isNavigatining = isNavigatining;
    }

    public CommonAddressType getCommonAddressType() {
        return commonAddressType;
    }

    public void setCommonAddressType(CommonAddressType commonAddressType) {

        this.commonAddressType = commonAddressType;
    }

    public void setNavigationType(NavigationType navigationType) {
        this.navigationType = navigationType;
    }

    public NavigationType getNavigationType() {
        return navigationType;
    }

    public void setSearchType(SearchType searchType) {
        this.searchType = searchType;
    }

    public SearchType getSearchType() {
        return searchType;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getKeyword() {
        return keyword;
    }

    public NavigationManager(Context context) {

        this.mContext = context;
        log = LoggerFactory.getLogger("lbs.navi");
    }

    public Logger getLog() {
        return log;
    }

    public static NavigationManager getInstance(Context context) {
        if (navigationManager == null)
            navigationManager = new NavigationManager(context);

        return navigationManager;
    }

    public void initNaviManager() {
        ResourceManager.getInstance(mContext).init();
        NaviProcess.getInstance(mContext).initNaviProcess();
    }

    public void startCalculate(Navigation navigation) {
        NaviProcess.getInstance(mContext).calculateDriverRoute(navigation);
    }

    public void existNavigation() {
        setSearchType(SearchType.SEARCH_DEFAULT);
        setNavigationType(NavigationType.DEFAULT);
        setIsNavigatining(false);
        NaviProcess.getInstance(mContext).destoryAmapNavi();
    }

    public void parseKeyword(String semantic,String service){
        setKeyword(KeywordProcess.parseKeyword(semantic, service));
    }

    public void search() {
        SearchProcess.getInstance(mContext).search(keyword);
    }

    public String getCurlocationDesc(){
        return ResourceManager.getInstance(mContext).getCur_locationDesc();
    }

    public List<PoiResultInfo> getPoiResultList(){
        return ResourceManager.getInstance(mContext).getPoiResultList();
    }

    public List<PoiItem>  getAmapPoiItem(){
        return ResourceManager.getInstance(mContext).getPoiItems();
    }

    public List<NaviDriveMode> getDriveModeList(){
        return  ResourceManager.getInstance(mContext).getDriveModeMap();
    }
}
