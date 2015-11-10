package com.dudu.android.launcher.utils;

import java.util.UUID;

public class Constants {

	public static final String FLOW_UPDATE_BROADCAST = "broadcast_flow_update";
	
	public static final String VIDEO_PREVIEW_BROADCAST = "broadcast_video_preview";

	public static final boolean DEBUG = true;
	public static boolean IS_SHOW_MESSAGE = true;
	public static final UUID UUIDS = UUID.fromString(UUID.randomUUID().toString());
	public static final String XUFEIID = "55bda6e9";


	public static final String NAVIGATION = "导航";
	public static final String MUSIC = "音乐";
	public static final String REDIO = "收音机";
	public static final String BLUETOOTH = "蓝牙";
	public static final String VIDEO = "视频";
	public static final String WIFIB = "WIFI";
	public static final String WIFI = "wifi";
	public static final String HOTSPOT = "热点";
	public static final String MESSAGE_SHOW = "消息显示";
	public static final String SPEECH = "语音";
	public static final String TRAFFIC = "流量";
	public static final String LUXIANG = "录像";
	public static final String SHEXIANG = "摄像";
	public static final String JIEDAN = "接单";
	public static final String CLZJ = "车辆";

	public static final String EXECUTE = "执行";
	public static final String REALTIME = "实时";
	public static final String EXIT = "退出";
	public static final String OPEN = "打开";
	public static final String START = "开始";
	public static final String CLOSE = "关闭";
	public static final String END = "结束";
	public static final String BACK = "返回";
	public static final String YES = "是";
	public static final String NO = "否";
	public static final String JIE = "单";
	public static final String ZIJIAN = "自检";

    public static final String REFUEL = "加油";
	public static final String GAS_STATION = "加油站";
	public static final String DRAW_MONEY = "取钱";
	public static final String BANK = "银行";
	public static final String HOTEL = "旅馆";
	public static final String SLEEP = "睡觉";
	public static final String TIRED = "累了";
	public static final String SLEEPY = "困了";


	public static final String WAKEUP_WORDS = "您好";
	public static final String WAKEUP_NETWORK_UNAVAILABLE = "网络状态关闭，请检查网络";
	
	public static final String UNDERSTAND_EXIT = "嘟嘟累了，稍后与你再见";
	public static final String UNDERSTAND_MISUNDERSTAND = "嘟嘟无法识别，请重说";
	public static final String UNDERSTAND_NO_INPUT = "没有检测到语音输入";
	public static final String UNDERSTAND_NETWORK_PROBLEM = "当前网络较差，请稍后再试";

	public static final String ONE1 = "1";
	public static final String ONE = "一";
	public static final String TWO1 = "2";
	public static final String TWO = "二";
	public static final String THREE1 = "3";
	public static final String THREE = "三";
	public static final String FOUR1 = "4";
	public static final String FOUR = "四";
	public static final String FIVE1 = "5";
	public static final String FIVE = "五";
	public static final String SIX1 = "6";
	public static final String SIX = "六";
	public static final String SEVEN1 = "7";
	public static final String SEVEN = "七";
	public static final String EIGHT1 = "8";
	public static final String EIGHT = "八";
	public static final String NINE1 = "9";
	public static final String NINE = "九";
	public static final String TEN1 = "10";
	public static final String TEN = "十";
	public static final String ELEVEN1 = "11";
	public static final String ELEVEN = "十一";
	public static final String TWELVE1 = "12";
	public static final String TWELVE = "十二";
	public static final String THIRTEEN1 = "13";
	public static final String THIRTEEN = "十三";
	public static final String FOURTEEN1 = "14";
	public static final String FOURTEEN = "十四";
	public static final String FIFTEEN1 = "15";
	public static final String FIFTEEN = "十五";
	public static final String SIXTEEN1 = "16";
	public static final String SIXTEEN = "十六";
	public static final String SEVENTEEN1 = "17";
	public static final String SEVENTEEN = "十七";
	public static final String EIGHTEEN1 = "18";
	public static final String EIGHTEEN = "十八";
	public static final String NINETEEN1 = "19";
	public static final String NINETEEN = "十九";
	public static final String TWENTW1 = "20";
	public static final String TWENTW = "二十";
	
	public static final int VOICE_WAKEUP_CURTHRESH = 10;

	// 代表我的位置
	public static final String CURRENT_POI = "CURRENT_POI";
	// 地址不够详细
	public static final String LOC_BASIC = "LOC_BASIC";

	public static final int NO_VALUE_FLAG = -999;// 无
	public static final int SUNNY = 0;// 晴
	public static final int CLOUDY = 1;// 多云
	public static final int OVERCAST = 2;// 阴
	public static final int SHOWER = 3;// 阵雨
	public static final int THUNDERSHOWER = 4;// 雷阵雨
	public static final int LIGHT_RAIN = 5;// 小雨
	public static final int MODERATE_RAIN = 6;// 中雨
	public static final int HEAVY_RAIN = 7;// 大雨
	public static final int STORM = 8;// 暴风雨
	public static final int HEAVY_STORM = 9;// 大暴风雨
	public static final int SEVERE_STORM = 10;// 飓风

	public static final int LIGHT_SNOW = 11;// 小雪
	public static final int MODERATE_SNOW = 12;// 中雪
	public static final int HEAVY_SNOW = 13;// 大雪
	public static final int SNOWSTORM = 14;// 暴雪
	public static final int SNOW_SHOWER = 15;// 阵雪
	public static final int FOGGY = 16;// 雾
	public static final int LIGHT_TO_MODERATE_RAIN = 17;
	public static final int MODERATE_TO_HEAVY_RAIN = 18;
	public static final int RAIN_TO_STORM = 19;
	public static final int STORM_TO_HEAVY_STORM = 20;
	public static final int HEAVY_TO_SEVERE_STORM = 21;
	public static final int LIGHT_TO_MODERATE_SNOW = 22;
	public static final int MODERATE_TO_HEAVY_SNOW = 23;
	public static final int HEAVY_TO_SNOWSTORM = 24;
	
	public static final String PARAM_MAP_DATA = "map_data";

	public static final String NAVI_TRAFFIC_BROADCAST = "路况播报";
	public static final String NAVI_TRAFFIC = "路况";
	public static final String RERURN_JOURNEY = "返程";
	public static final String NAVI_PREVIEW = "全程预览";
	public static final String REALTIME_TRAFFIC = "实时路况";
	public static final String NAVI_LISTEN = "听";
	public static final String NAVI_LOOK = "查看";

    public static final int VERSION_TYPE_TAXI = 1;
	public static final int VERSION_TYPE_CAR = 2;

}
