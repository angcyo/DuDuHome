package com.dudu.navi.vauleObject;

/**
 * Created by lxh on 2015/11/25.
 */
public enum SearchType {

    /**
     * 搜索类型为当前位置
     */
    SEARCH_CUR_LOCATION,

    /**
     * 搜索某个地点（我要去XXX，导航到XXX）
     */

    SEARCH_PLACE,

    /**
     * 附近搜索
     */
    SEARCH_NEARBY,

    /**
     *
     * 某个地点的位置（xxx在哪，xxx的位置）
     */
    SEARCH_PLACE_LOCATION,

    /**
     * 搜索最近的xxx
     */
    SEARCH_NEAREST,

    /**
     * 添加常用地
     */
    SEARCH_COMMONADDRESS,

    SEARCH_COMMONPLACE,

    SEARCH_DEFAULT
}
