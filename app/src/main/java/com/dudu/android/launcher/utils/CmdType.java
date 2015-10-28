package com.dudu.android.launcher.utils;

public interface CmdType {

	/**
	 * 单词
	 */
	public static final String SERVICE_CHAT = "chat";
	/**
	 * 问题
	 */
	public static final String SERVICE_OPENQA = "openQA";
	
	/**
	 * 
	 */
	public static final String SERVICE_FAQ = "faq";

	/**
	 * 百科
	 */
	public static final String SERVICE_BAIKE = "baike";
	/**
	 * 地图
	 */
	public static final String SERVICE_MAP = "map";

	/**
	 * 酒店
	 */
	public static final String SERVICE_HOTEL = "hotel";

	/**
	 * "餐馆(restaurant)"支持餐厅、美食、特色菜搜索查找的语义解析，搜索词中还可以包含价位、打折信息
	 */
	public static final String SERVICE_RESTAURANT = "restaurant";

	/**
	 * 自定义语义 添加打电话
	 */
	public static final String SERVICE_TELEPHONE = "telephone";

	/**
	 * 自定义语义 命令
	 */
	public static final String SERVICE_CMD = "cmd";
	/**
	 * 自定义语义 附近 poi关键字搜索
	 */
	public static final String SERVICE_NEARBY = "nearby";

	/**
	 * 自定义语义 选择
	 */
	public static final String SERVICE_CHOISE = "choise";

	/**
	 * 自定义语义 添加常用
	 */
	public static final String SERVICE_ADDFINAL = "addfinal";

	/**
	 * 自定义语义 添加电话
	 */
	public static final String SERVICE_PHONE = "phone";

	/**
	 * 自定义语义 删除
	 */
	public static final String SERVICE_DELETE = "delete";

	public static final String SERVICE_APP = "app";

	public static final String SERVICE_WEBSITE = "website";

	/**
	 * "天气(weather)"主要用于天气情况查询的语义解析
	 */
	public static final String SERVICE_WEATHER = "weather";

	/**
	 * "音乐(music)"支持基于歌曲名、歌手名、专辑名称、歌曲类型的音乐查找或播放的语义解析
	 */
	public static final String SERVICE_MUSIC = "music";

	/**
	 * "股票(stock)"支持基于股票名称或股票代码的分时图、K线图、日K线、周K线、月K线的查询搜索的语义解析
	 */
	public static final String SERVICE_STOCK = "stock";

	/**
	 * "网页搜索(websearch)"主要用于各引擎的网页搜索结果的语义解析
	 */
	public static final String SERVICE_WEBSEARCH = "websearch";

	/**
	 * "翻译(translation)"主要用于多语种即时翻译的语义解析
	 */
	public static final String SERVICE_TRANSLATION = "translation";

	/**
	 * "微博(weibo)"主要用于微博私信的发布、搜索、查看、转发、评论的语义解析
	 */
	public static final String SERVICE_WEIBO = "weibo";

	/**
	 * "电视控制(tvControl)"支持电视控制命令的语义解析
	 */
	public static final String SERVICE_TVCONTROL = "tvControl";

	public static final String SERVICE_AIRCONTROL = "airControl";

	public static final String SERVICE_WIFI = "mwifi";

	public static final String SERVICE_VOICE = "voice";

	public static final String SERVICE_MCALL = "mcall";

	public static final String SERVICE_MESSAGE = "message";
	
	public static final String SERVICE_CHECKING = "checking";

}
