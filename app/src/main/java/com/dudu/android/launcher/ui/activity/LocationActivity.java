package com.dudu.android.launcher.ui.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.OnInfoWindowClickListener;
import com.amap.api.maps.AMap.OnMarkerClickListener;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.overlay.PoiOverlay;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.model.NaviLatLng;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.poisearch.Cinema;
import com.amap.api.services.poisearch.Dining;
import com.amap.api.services.poisearch.Hotel;
import com.amap.api.services.poisearch.PoiItemDetail;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.poisearch.PoiSearch.OnPoiSearchListener;
import com.amap.api.services.poisearch.PoiSearch.SearchBound;
import com.amap.api.services.poisearch.Scenic;
import com.dudu.android.launcher.R;
import com.dudu.android.launcher.bean.MapEntity;
import com.dudu.android.launcher.bean.MapLocation;
import com.dudu.android.launcher.bean.MapSlots;
import com.dudu.android.launcher.bean.MapSlotsLoc;
import com.dudu.android.launcher.bean.PoiResultInfo;
import com.dudu.android.launcher.bean.RestaurantEntity;
import com.dudu.android.launcher.bean.RestaurantSlots;
import com.dudu.android.launcher.db.DBManager;
import com.dudu.android.launcher.ui.activity.base.BaseNoTitlebarAcitivity;
import com.dudu.android.launcher.ui.dialog.RouteSearchPoiDialog;
import com.dudu.android.launcher.ui.dialog.RouteSearchPoiDialog.OnListItemClick;
import com.dudu.android.launcher.ui.dialog.StrategyChoiseDialog;
import com.dudu.android.launcher.ui.dialog.StrategyChoiseDialog.OnStrategyClickListener;
import com.dudu.android.launcher.ui.view.CleanableCompletaTextView;
import com.dudu.android.launcher.utils.ActivitiesManager;
import com.dudu.android.launcher.utils.Constants;
import com.dudu.android.launcher.utils.Coordinate;
import com.dudu.android.launcher.utils.FloatWindow;
import com.dudu.android.launcher.utils.FloatWindowUtil;
import com.dudu.android.launcher.utils.JourneyTool;
import com.dudu.android.launcher.utils.LcStringUtil;
import com.dudu.android.launcher.utils.LocationUtils;
import com.dudu.android.launcher.utils.ToastUtils;
import com.dudu.map.MapManager;
import com.dudu.map.Navigation;
import com.dudu.voice.semantic.SemanticConstants;
import com.dudu.voice.semantic.VoiceManager;
import com.dudu.voice.semantic.chain.ChoosePageChain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.greenrobot.event.EventBus;

@SuppressLint("RtlHardcoded")
public class LocationActivity extends BaseNoTitlebarAcitivity implements
		LocationSource, AMapLocationListener{

	private final static int CALCULATEERROR = 1;// 启动路径计算失败状态
	private final static int CALCULATESUCCESS = 2;// 启动路径计算成功状态
	// 驾车路径规划起点，终点的list
	private List<NaviLatLng> mStartPoints = new ArrayList<NaviLatLng>();
	private List<NaviLatLng> mEndPoints = new ArrayList<NaviLatLng>();
	// 记录起点、终点位置
	private NaviLatLng mStartPoint = new NaviLatLng();
	private NaviLatLng mEndPoint = new NaviLatLng();
	private MapView mapView;
	private AMap aMap;
	private OnLocationChangedListener mListener;
	private LocationManagerProxy mAMapLocationManager;
	private String cityCode;

	private ProgressDialog progDialog = null;// 搜索时进度条
	private PoiResult poiResult; // poi返回的结果
	private RouteSearchPoiDialog addressDialog; // 地址选择弹出框
	private StrategyChoiseDialog strategyDialog;// 优先策略选择弹出框
	private int currentPage = 0;// 当前页面，从0开始计数
	private PoiSearch.Query query;// Poi查询条件类
	private PoiSearch poiSearch;// POI搜索
	private String keyWord;
	private List<PoiItem> poiItems = null;
	private PoiOverlay poiOverlay = null;

	// ---------------------变量---------------------
	private String[] mStrategyMethods;// 记录行车策略的数组

	private Marker detailMarker;// 显示Marker的详情
	private Marker locationMarker; // 选择的点
	private LatLonPoint mLatLonPoint = null;
	private LatLng latlong = null;
	private String startKeyWord;
	private String endKeyWord;
	private boolean startBool = true;
	private String desc = "";
	private int type = 1;

	private boolean isManual = false;

	private LinearLayout endLocationLL;
	private CleanableCompletaTextView search_edit;
	private Button search_btn;
	private Button search_enter;

	private Button mBackButton;

	private boolean isFirst = true;

	private AMapLocation cur_location;

	private String naviAddress;

	private String[] end_Address;

	private DBManager mDBManager;

	private List<PoiResultInfo> poiResultList = new ArrayList<PoiResultInfo>();

    private MapManager mapManager = MapManager.getInstance();
	private Handler mhandler;
	private Runnable removeWindowRunnable = new Runnable() {

		@Override
		public void run() {
			FloatWindowUtil.removeFloatWindow();
		}
	};



	@Override
	public int initContentView() {
		return R.layout.location;
	}

	@Override
	public void initView(Bundle savedInstanceState) {
		endLocationLL = (LinearLayout) findViewById(R.id.endLocationLL);
		search_edit = (CleanableCompletaTextView) findViewById(R.id.search_edit);
		search_btn = (Button) findViewById(R.id.search_btn);
		mBackButton = (Button) findViewById(R.id.back_button);
		search_enter = (Button) findViewById(R.id.search_enter);
		mhandler = new Handler();

		mapView = (MapView) findViewById(R.id.map);
		mapView.onCreate(savedInstanceState);// 此方法必须重写
		if (progDialog == null)
			progDialog = new ProgressDialog(LocationActivity.this);
		if (progDialog.isShowing())
			progDialog.dismiss();
		progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progDialog.setIndeterminate(false);
		progDialog.setCancelable(false);
		mDBManager = DBManager.getInstance(this);

	}

	@Override
	public void initListener() {
		aMap.setOnMarkerClickListener(mOnMarkerClickListener);
		aMap.setOnInfoWindowClickListener(mOnInfoWindowClickListener);
		search_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				isManual = true;
				mapManager.setSearchType(MapManager.SEARCH_POI);
				search(getEndLocation());
			}
		});

		mBackButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				LocationActivity.this.finish();
			}
		});

		search_enter.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				endLocationLL.setVisibility(View.VISIBLE);
			}
		});

		search_edit.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					InputMethodManager imm = (InputMethodManager) v
							.getContext().getSystemService(
									Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
					isManual = true;
					mapManager.setSearchType(MapManager.SEARCH_POI);
					search(getEndLocation());
					return true;
				}

				return false;
			}
		});

	}

	@Override
	public void initDatas() {
		Bundle bundle = getIntent().getExtras();
		isManual = bundle.getBoolean("isManual", false);
		if (!isManual) {
			endLocationLL.setVisibility(View.GONE);
		}

		if (aMap == null) {
			aMap = mapView.getMap();
			setUpMap();
		}

		initResources();

		Serializable data = bundle.getSerializable(Constants.PARAM_MAP_DATA);
		if (data != null) {
			if (data instanceof MapEntity) {
				startLocationInit((MapEntity) data, false, false);
			} else if (data instanceof RestaurantEntity) {
				startRestaurantLocationInit((RestaurantEntity) data, false,
						false);
			}
		} else {
			boolean isPoi = bundle.getBoolean("isPoi", false);
			if (isPoi) {
				this.keyWord = bundle.getString("poiKeyWord");
			} else {
				mapManager.setSearchType(MapManager.SEARCH_POI);
			}
		}
	}

	private String getEndLocation() {
		return search_edit.getText().toString();
	}

	private void setUpMap() {
		aMap.moveCamera(CameraUpdateFactory.zoomTo(16));
		aMap.setLocationSource(this);// 设置定位监听
		aMap.getUiSettings().setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示，true显示，false不显示
		aMap.getUiSettings().setZoomControlsEnabled(false);// 隐藏地图放大缩小按钮
		aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
		// 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
		aMap.setMyLocationType(AMap.LOCATION_TYPE_MAP_FOLLOW);
		// 初始语音播报资源
		setVolumeControlStream(AudioManager.STREAM_MUSIC);// 设置声音控制

	}

	/**
	 * 初始化资源文件，主要是字符串
	 */
	private void initResources() {
		mStrategyMethods = new String[] {
				getString(R.string.navi_strategy_speed),
				getString(R.string.navi_strategy_cost),
				getString(R.string.navi_strategy_distance),
				getString(R.string.navi_strategy_nohighway),
				getString(R.string.navi_strategy_timenojam),
				getString(R.string.navi_strategy_costnojam) };
	}

	public void startRestaurantLocationInit(RestaurantEntity restaurantEntity,
			boolean bool, boolean ismanual) {
		isManual = ismanual;
		if (restaurantEntity == null) {
			Toast.makeText(LocationActivity.this, "无法识别，请从新输入",
					Toast.LENGTH_SHORT).show();
			return;
		}

		RestaurantSlots slots = restaurantEntity.getRestaurantSlots();
		if (slots != null) {
			mapManager.setSearchType(MapManager.SEARCH_NEARBY);
			keyWord = slots.getCategory();
		}
	}

	public void startLocationInit(MapEntity mapEntity, boolean bool,
			boolean ismanual) {
		isManual = ismanual;
		if (mapEntity == null) {
			Toast.makeText(LocationActivity.this, "无法识别，请从新输入",
					Toast.LENGTH_SHORT).show();
			return;
		}

		MapSlots slots = mapEntity.getSlots();
		MapLocation location = slots.getLocation();
		if (location != null) {
			mapManager.setSearchType(MapManager.SEARCH_POI);
			if (Constants.LOC_BASIC.equals(location.getType())) {
				String playText = "请提供详细地址";
				if (!isManual) {
					VoiceManager.getInstance().startSpeaking(playText, SemanticConstants.TTS_START_UNDERSTANDING,false);
				}

			} else if (LcStringUtil.checkStringNotNull(location.getArea())
					&& LcStringUtil.checkStringNotNull(location.getPoi())) {
				keyWord = LcStringUtil.checkString(location.getCity());
			} else {
				keyWord = LcStringUtil.checkString(location.getArea())
						+ location.getPoi();
			}
		} else {
			MapSlotsLoc startLoc = slots.getStartLoc();
			startKeyWord = LcStringUtil.checkString(startLoc.getArea())
					+ startLoc.getPoi();

			MapSlotsLoc endLoc = slots.getEndLoc();
			endKeyWord = LcStringUtil.checkString(endLoc.getArea())
					+ endLoc.getPoi();
			if (endKeyWord.equals("null") || TextUtils.isEmpty(endKeyWord)) {
				endKeyWord = endLoc.getCity();
			}
			if (Constants.CURRENT_POI.equals(startLoc.getPoi())) {
				mapManager.setSearchType(MapManager.SEARCH_NAVI);
				keyWord = endKeyWord;
			}
		}


		if (bool) {
			startPoi();
		}
	}

	public void startLocationPoi(String keyWord, boolean ismanual) {
		isManual = ismanual;
		startSearchPoi(keyWord, 3);
	}

	public void startSearchPoi(String keyWord, boolean ismanual) {
		isManual = ismanual;
		startSearchPoi(keyWord, -1);
	}

	public void startSearchPoi(String keyWord, int cmdType) {
		this.keyWord = keyWord;
		if (cmdType == -1) {
			mapManager.setSearchType(MapManager.SEARCH_POI);
		} else {
			mapManager.setSearchType(cmdType);
		}
		startPoi();
	}

	public void startPoi() {

		if (mapManager.getSearchType()==MapManager.SEARCH_POI) {
			if (!Constants.CURRENT_POI.equals(keyWord)) {
				if (LcStringUtil.checkStringNotNull(keyWord)) {
					search(keyWord);
				} else {
					if (!isManual) {
						new Handler().postDelayed(new Runnable() {

							@Override
							public void run() {
								mapManager.setSearch(true);
								String playText = "您好，请说出您想去的地方或者关键字";
								VoiceManager.getInstance().startSpeaking(
										playText, SemanticConstants.TTS_START_UNDERSTANDING);
							}
						}, 500);

					}
				}
			} else {
				String playText = "您好，您现在在" + desc;
				if (TextUtils.isEmpty(desc)) {
					playText = "暂时无法获取到您的详细位置，请稍后再试";
				}
				VoiceManager.getInstance().startSpeaking(playText,
						SemanticConstants.TTS_START_WAKEUP);
				removeFloatWindow();
			}
		} else if (mapManager.getSearchType() == MapManager.SEARCH_NAVI) {
			startKeyWord = LcStringUtil.checkString(endKeyWord);
			search(startKeyWord);
		} else if (mapManager.getSearchType() == MapManager.SEARCH_NEARBY) {
			search(this.keyWord);
		}
	}

	public void search(String name) {
		keyWord = LcStringUtil.checkString(name);
		if ("".equals(keyWord)) {
			String playText = "您好，关键字有误，请重新输入";
			if (isManual) {
				Toast.makeText(this, playText, Toast.LENGTH_SHORT).show();
			} else {
				VoiceManager.getInstance().startSpeaking(playText,
						SemanticConstants.TTS_START_UNDERSTANDING,false);
			}
			return;
		}

		showProgressDialog();// 显示进度框
		currentPage = 0;
		if (mapManager.getSearchType()==MapManager.SEARCH_NEARBY) {
			query = new PoiSearch.Query(keyWord, "", cityCode);// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
			// 有团购或者优惠
			query.setLimitGroupbuy(false);
			query.setLimitDiscount(false);
		} else {
			query = new PoiSearch.Query(keyWord, "", cityCode);// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
		}

		query.setPageSize(20);// 设置每页最多返回多少条poi item
		query.setPageNum(currentPage);// 设置查第一页
		poiSearch = new PoiSearch(this, query);
		if (mapManager.getSearchType()==MapManager.SEARCH_NEARBY) {
			poiSearch.setBound(new SearchBound(mLatLonPoint, 2000));
		}
		poiSearch.setOnPoiSearchListener(onPoiSearchListener);
		poiSearch.searchPOIAsyn();
		Log.d("lxh", "搜索");
	}

	/**
	 * 识别 地址选择 还是 优先策略选择
	 */
	public void startChoiseResult(int size, String text) {
		if (type == 1) {
			if (!checkChoiseType(text)) {
				String playText = "请选择第几个";
				VoiceManager.getInstance().startSpeaking(playText,
						SemanticConstants.TTS_START_UNDERSTANDING,false);
				return;
			}
			calculateNavigationStart(size);
		} else if (type == 2) {
			if (size > mStrategyMethods.length) {
				String playText = "选择错误，请重新选择";
				VoiceManager.getInstance().startSpeaking(playText,
						SemanticConstants.TTS_START_UNDERSTANDING,false);
				return;
			}

			startDriveMode(Integer.parseInt(Constants.ONE1));
		}
	}

	private boolean checkChoiseType(String text) {
		for (String type : mStrategyMethods) {
			if (text.contains(type)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * 查单个poi详情
	 * 
	 * @param poiId
	 */
	public void doSearchPoiDetail(String poiId) {
		if (poiSearch != null && poiId != null) {
			poiSearch.searchPOIDetailAsyn(poiId);
		}
	}

	private OnInfoWindowClickListener mOnInfoWindowClickListener = new OnInfoWindowClickListener() {
		@Override
		public void onInfoWindowClick(Marker arg0) {
			if (locationMarker != null) {
				locationMarker.hideInfoWindow();
				mLatLonPoint = new LatLonPoint(
						locationMarker.getPosition().latitude,
						locationMarker.getPosition().longitude);
				locationMarker.destroy();
			}
		}
	};

	private OnMarkerClickListener mOnMarkerClickListener = new OnMarkerClickListener() {

		@Override
		public boolean onMarkerClick(Marker marker) {
			if (poiOverlay != null && poiItems != null && poiItems.size() > 0) {
				detailMarker = marker;
				doSearchPoiDetail(poiItems.get(poiOverlay.getPoiIndex(marker))
						.getPoiId());
			}
			return false;
		}

	};

	private OnPoiSearchListener onPoiSearchListener = new OnPoiSearchListener() {
		/**
		 * POI详情回调
		 */
		@Override
		public void onPoiItemDetailSearched(PoiItemDetail result, int rCode) {
			dissmissProgressDialog();// 隐藏对话框
			if (rCode == 0) {
				if (result != null) {// 搜索poi的结果
					if (detailMarker != null) {
						StringBuffer sb = new StringBuffer(result.getSnippet());
						if ((result.getGroupbuys() != null && result
								.getGroupbuys().size() > 0)
								|| (result.getDiscounts() != null && result
										.getDiscounts().size() > 0)) {

							if (result.getGroupbuys() != null
									&& result.getGroupbuys().size() > 0) {// 取第一条团购信息
								sb.append("\n团购："
										+ result.getGroupbuys().get(0)
												.getDetail());
							}
							if (result.getDiscounts() != null
									&& result.getDiscounts().size() > 0) {// 取第一条优惠信息
								sb.append("\n优惠："
										+ result.getDiscounts().get(0)
												.getDetail());
							}
						} else {
							sb = new StringBuffer("地址：" + result.getSnippet()
									+ "\n电话：" + result.getTel() + "\n类型："
									+ result.getTypeDes());
						}
						// 判断poi搜索是否有深度信息
						if (result.getDeepType() != null) {
							sb = getDeepInfo(result, sb);
							detailMarker.setSnippet(sb.toString());
						} else {
							// ToastUtils.showTip(LocationActivity.this,
							// "此Poi点没有深度信息");
						}
					}

				} else {
					VoiceManager.getInstance().startSpeaking(
							getString(R.string.no_result), SemanticConstants.TTS_START_UNDERSTANDING);
				}
			} else if (rCode == 27) {
				FloatWindowUtil.showMessage(getString(R.string.error_network),
						FloatWindow.MESSAGE_IN);
				removeFloatWindow();
			} else if (rCode == 32) {
				FloatWindowUtil.showMessage(getString(R.string.error_key),
						FloatWindow.MESSAGE_IN);
				removeFloatWindow();
			} else {
				FloatWindowUtil.showMessage(getString(R.string.error_other),
						FloatWindow.MESSAGE_IN);
				removeFloatWindow();
			}
		}

		/**
		 * POI深度信息获取
		 */
		private StringBuffer getDeepInfo(PoiItemDetail result,
				StringBuffer sbuBuffer) {
			switch (result.getDeepType()) {
			// 餐饮深度信息
			case DINING:
				if (result.getDining() != null) {
					Dining dining = result.getDining();
					sbuBuffer.append("\n菜系：" + dining.getTag() + "\n特色："
							+ dining.getRecommend() + "\n来源："
							+ dining.getDeepsrc());
				}
				break;
			// 酒店深度信息
			case HOTEL:
				if (result.getHotel() != null) {
					Hotel hotel = result.getHotel();
					sbuBuffer.append("\n价位：" + hotel.getLowestPrice() + "\n卫生："
							+ hotel.getHealthRating() + "\n来源："
							+ hotel.getDeepsrc());
				}
				break;
			// 景区深度信息
			case SCENIC:
				if (result.getScenic() != null) {
					Scenic scenic = result.getScenic();
					sbuBuffer.append("\n价钱：" + scenic.getPrice() + "\n推荐："
							+ scenic.getRecommend() + "\n来源："
							+ scenic.getDeepsrc());
				}
				break;
			// 影院深度信息
			case CINEMA:
				if (result.getCinema() != null) {
					Cinema cinema = result.getCinema();
					sbuBuffer
							.append("\n停车：" + cinema.getParking() + "\n简介："
									+ cinema.getIntro() + "\n来源："
									+ cinema.getDeepsrc());
				}
				break;
			default:
				break;
			}
			return sbuBuffer;
		}

		/**
		 * POI信息查询回调方法
		 */
		@Override
		public void onPoiSearched(PoiResult result, int rCode) {
			Log.d("lxh", "----------POI信息查询回调方法-");
			dissmissProgressDialog();// 隐藏对话框
			if (rCode == 0) {
				if (result != null && result.getQuery() != null) {// 搜索poi的结果
					if (result.getQuery().equals(query)) {// 是否是同一条
						mapManager.setSearch(false);
						poiResult = result;
						// 取得搜索到的poiitems有多少页
						poiItems = poiResult.getPois();// 取得第一页的poiitem数据，页数从数字0开始
						List<SuggestionCity> suggestionCities = poiResult
								.getSearchSuggestionCitys();// 当搜索不到poiitem数据时，会返回含有搜索关键字的城市信息
						aMap.clear();// 清理之前的图标
						if (poiItems != null && poiItems.size() > 0) {
							setPoiList();
							switch (mapManager.getSearchType()) {
							case 1:
							case 3:
								aMap.moveCamera(CameraUpdateFactory.zoomTo(20));
								aMap.addMarker(new MarkerOptions()
										.position(latlong)
										.icon(BitmapDescriptorFactory
												.fromResource(R.drawable.location_marker)));

								poiOverlay = new PoiOverlay(aMap, poiItems);
								poiOverlay.removeFromMap();
								poiOverlay.addToMap();
								poiOverlay.zoomToSpan();
								// break;
							case 2:
							case 4:
								type = 1;
								mapManager.setShowAddress(true);
								if (isManual) {
									showAddressDialog();
								} else {
									String playText = "请选择列表中的地址";
									VoiceManager.getInstance().startSpeaking(
											playText, SemanticConstants.TTS_START_UNDERSTANDING,false);
									FloatWindowUtil.showAddress(poiResultList,
											new OnItemClickListener() {

												@Override
												public void onItemClick(
														AdapterView<?> arg0,
														View arg1,
														int position, long arg3) {
													// TODO Auto-generated
													// method stub
													calculateNavigationStart(position);
												}
											});

								}
								break;
							}
						} else if (suggestionCities != null
								&& suggestionCities.size() > 0) {
							showSuggestCity(suggestionCities);
						} else {
							VoiceManager.getInstance().stopUnderstanding();
							VoiceManager.getInstance().startSpeaking(
									getString(R.string.no_result),
									SemanticConstants.TTS_DO_NOTHING);

						}
					}
				} else {
					VoiceManager.getInstance().stopUnderstanding();
					VoiceManager.getInstance().startSpeaking(
							getString(R.string.no_result), SemanticConstants.TTS_DO_NOTHING);
				}
			} else if (rCode == 27) {
				VoiceManager.getInstance().stopUnderstanding();
				FloatWindowUtil.showMessage(getString(R.string.error_network),
						FloatWindow.MESSAGE_IN);
				removeFloatWindow();
			} else if (rCode == 32) {
				VoiceManager.getInstance().stopUnderstanding();
				VoiceManager.getInstance().stopUnderstanding();
				FloatWindowUtil.showMessage(getString(R.string.error_key),
						FloatWindow.MESSAGE_IN);
				removeFloatWindow();
			} else {
				VoiceManager.getInstance().stopUnderstanding();
				FloatWindowUtil.showMessage(getString(R.string.error_other),
						FloatWindow.MESSAGE_IN);
				removeFloatWindow();
			}
		}

	};

	/**
	 * poi没有搜索到数据，返回一些推荐城市的信息
	 */
	private void showSuggestCity(List<SuggestionCity> cities) {
		String infomation = "我没有听清楚，请再说一次";
		VoiceManager.getInstance().startSpeaking(infomation,SemanticConstants.TTS_START_UNDERSTANDING);
		FloatWindowUtil.showMessage(infomation, FloatWindow.MESSAGE_IN);
		removeFloatWindow();
	}

	/**
	 * 弹出地址选择框
	 */
	private void showAddressDialog() {
		// ReceiverSendUtils.stopMessageShowService(this);
		if (isManual) {
			if (addressDialog != null && addressDialog.isShowing()) {
				addressDialog.dismiss();
			}
			addressDialog = new RouteSearchPoiDialog(LocationActivity.this,
					poiResultList);

			Window dialogWindow = addressDialog.getWindow();
			WindowManager.LayoutParams lp = dialogWindow.getAttributes();
			lp.x = 10; // 新位置X坐标
			lp.y = 0; // 新位置Y坐标
			lp.width = WindowManager.LayoutParams.MATCH_PARENT;
			lp.height = WindowManager.LayoutParams.MATCH_PARENT;
			lp.alpha = 0.8f; // 透明度
			dialogWindow.setAttributes(lp);
			addressDialog.show();
			addressDialog.setOnListClickListener(new OnListItemClick() {
				@Override
				public void onListItemClick(int position) {
					addressDialog.dismiss();
					calculateNavigationStart(position);
				}
			});
		}
	}

	/**
	 * 弹出策略选择框
	 */
	private void showStrategyDialog(LatLonPoint[] points) {
		if (isManual) {
			if (strategyDialog != null && strategyDialog.isShowing()) {
				strategyDialog.dismiss();
			}
			strategyDialog = new StrategyChoiseDialog(LocationActivity.this,end_Address, points);

			Window dialogWindow = strategyDialog.getWindow();
			WindowManager.LayoutParams lp = dialogWindow.getAttributes();
			lp.x = 10; // 新位置X坐标
			lp.y = 0; // 新位置Y坐标
			lp.width = WindowManager.LayoutParams.MATCH_PARENT;
			lp.height = WindowManager.LayoutParams.MATCH_PARENT;
			lp.alpha = 0.8f; // 透明度
			dialogWindow.setAttributes(lp);
			strategyDialog.show();
			strategyDialog
					.setOnStrategyClickListener(new OnStrategyClickListener() {

						@Override
						public void onStrategyClick(int position) {
							startDriveMode(position);
						}
					});
		}
	}

	private void startDriveMode(int driveMode){

		double[] destination = {mEndPoints.get(0).getLatitude(),mEndPoints.get(0).getLongitude()};

		EventBus.getDefault().post(new Navigation(destination,Navigation.NAVI_NORMAL,driveMode));


	}

	private void calculateNavigationStart(int position) {
		try {
			PoiResultInfo startpoiItem = isManual ? poiResultList.get(position)
					: poiResultList.get(position - 1);
			if (startpoiItem != null) {

				if (cur_location != null) {
						mStartPoint.setLatitude(cur_location.getLatitude());
						mStartPoint.setLongitude(cur_location.getLongitude());
						mStartPoints.clear();
						mStartPoints.add(mStartPoint);
					}
					LatLonPoint point = new LatLonPoint(
							startpoiItem.getLatitude(),
							startpoiItem.getLongitude());
					mEndPoint.setLatitude(startpoiItem.getLatitude());
					mEndPoint.setLongitude(startpoiItem.getLongitude());
					mEndPoints.clear();
					mEndPoints.add(mEndPoint);
					naviAddress = startpoiItem.getAddressTitle();
					if (addressDialog != null && addressDialog.isShowing()) {
						addressDialog.dismiss();
					}
					type = 2;
					if (isManual) {
						String address = startpoiItem.getAddressDetial();
						end_Address = new String[] {
								startpoiItem.getAddressTitle(),
								TextUtils.isEmpty(address) ? "中国" : address };
						LatLonPoint startPoint = new LatLonPoint(
								mStartPoint.getLatitude(),
								mStartPoint.getLongitude());
						LatLonPoint[] points = { startPoint, point };
						showStrategyDialog(points);
					} else {
						if (position > poiResultList.size()) {
							String playText = "选择错误，请重新选择";
							VoiceManager.getInstance().startSpeaking(playText,
									SemanticConstants.TTS_START_UNDERSTANDING,false);
							return;
						}
						String playText = "请选择路线优先策略。";
						VoiceManager.getInstance().clearMisUnderstandCount();
						VoiceManager.getInstance().startSpeaking(playText,
								SemanticConstants.TTS_START_UNDERSTANDING,false);
						FloatWindowUtil.showStrategy(null,
								new OnItemClickListener() {

									@Override
									public void onItemClick(
											AdapterView<?> arg0, View view,
											int position, long arg3) {


									}
								});

					}

			}
		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
			if (!isManual) {
				if (position > poiItems.size()) {
					String playText = "选择错误，请重新选择";
					VoiceManager.getInstance().startSpeaking(playText,
							SemanticConstants.TTS_START_UNDERSTANDING,false);
				}
			}
		}

	}



	/**
	 * 方法必须重写
	 */
	@Override
	protected void onResume() {
		super.onResume();
		mapView.onResume();
		mDBManager.open();
		if(LocationUtils.getInstance(this).getCurrentLocation()!=null){
			mStartPoint.setLatitude(LocationUtils.getInstance(this).getCurrentLocation()[0]);
			mStartPoint.setLatitude(LocationUtils.getInstance(this).getCurrentLocation()[1]);
		}
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			String type = bundle.getString("Searchtype");
			if (type != null && type.equals("search")) {
				keyWord = bundle.getString("keyWord");
				startSearchPoi(keyWord, false);
			}
		}
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onPause() {
		super.onPause();
		mapView.onPause();
		progDialog = null;

	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		DBManager.getInstance(this).close();
		mapManager.setSearch(false);
		mapManager.setShowAddress(false);
		if (mapView != null) {
			mapView.onDestroy();
			mapView = null;
		}
		if (progDialog != null && progDialog.isShowing()) {
			progDialog.dismiss();
			progDialog = null;
		}
		if (addressDialog != null && addressDialog.isShowing())
			addressDialog.dismiss();
		if (strategyDialog != null && strategyDialog.isShowing())
			strategyDialog.dismiss();

		if(mDBManager!=null)
			mDBManager.close();
	}

	/**
	 * 此方法已经废弃
	 */
	@Override
	public void onLocationChanged(Location location) {
	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	/**
	 * 定位成功后回调函数
	 */
	@Override
	public void onLocationChanged(AMapLocation aLocation) {
		if (mListener != null && aLocation != null) {
			mListener.onLocationChanged(aLocation);// 显示系统小蓝点
			Bundle locBundle = aLocation.getExtras();
			if (locBundle != null) {
				cityCode = locBundle.getString("citycode");
				desc = locBundle.getString("desc");
			}
			mLatLonPoint = new LatLonPoint(aLocation.getLatitude(),
					aLocation.getLongitude());

			latlong = new LatLng(mLatLonPoint.getLatitude(),
					mLatLonPoint.getLongitude());
			mStartPoint.setLatitude(aLocation.getLatitude());
			mStartPoint.setLongitude(aLocation.getLongitude());

			if (isFirst) {
				isFirst = false;
				startPoi();
				mStartPoint.setLatitude(aLocation.getLatitude());
				mStartPoint.setLongitude(aLocation.getLongitude());
			}
			cur_location = aLocation;
		}
	}

	/**
	 * 激活定位
	 */
	@Override
	public void activate(OnLocationChangedListener listener) {
		mListener = listener;
		if (mAMapLocationManager == null) {
			mAMapLocationManager = LocationManagerProxy.getInstance(this);
			mAMapLocationManager.requestLocationData(
					LocationProviderProxy.AMapNetwork, 2000, 10, this);
		}
	}

	/**
	 * 停止定位
	 */
	@SuppressWarnings("deprecation")
	@Override
	public void deactivate() {
		mListener = null;
		if (mAMapLocationManager != null) {
			mAMapLocationManager.removeUpdates(this);
			mAMapLocationManager.destory();
		}
		mAMapLocationManager = null;
	}

	/**
	 * 显示进度框
	 */
	private void showProgressDialog() {
		if (progDialog != null) {
			progDialog.setMessage("正在搜索:\n" + keyWord);
			progDialog.show();
		}
	}

	/**
	 * 隐藏进度框
	 */
	private void dissmissProgressDialog() {
		if (progDialog != null && progDialog.isShowing()) {
			progDialog.dismiss();
		}
	}

	public void trafficInfo() {
		aMap.setTrafficEnabled(true);
	}

	public void closeTraffic() {
		aMap.setTrafficEnabled(false);
	}

	private void setPoiList() {

		if (mStartPoint != null && !poiItems.isEmpty()) {
			// 先将高德坐标转换为真实坐标，再将真实坐标转换为百度坐标，调用百度的获取距离的工具类来计算距离
			double[] startPoints_gaode = Coordinate.chinatowg(
					mStartPoint.getLongitude(), mStartPoint.getLatitude());
			poiResultList.clear();
			for (int i = 0; i < poiItems.size(); i++) {
				PoiResultInfo poiResultInfo = new PoiResultInfo();
				poiResultInfo.setAddressDetial(poiItems.get(i).getSnippet());
				poiResultInfo.setAddressTitle(poiItems.get(i).getTitle());
				poiResultInfo.setLatitude(poiItems.get(i).getLatLonPoint()
						.getLatitude());
				poiResultInfo.setLongitude(poiItems.get(i).getLatLonPoint()
						.getLongitude());
					
				double[] endPoints_gaode = Coordinate.chinatowg(poiItems.get(i)
						.getLatLonPoint().getLongitude(), poiItems.get(i)
						.getLatLonPoint().getLatitude());
				poiResultInfo.setDistance(JourneyTool.getDistance(startPoints_gaode[1], startPoints_gaode[0],
						endPoints_gaode[1], endPoints_gaode[0]));
				poiResultList.add(poiResultInfo);
			}

			Collections.sort(poiResultList, new MyComparator());
		}
	}

	private class MyComparator implements Comparator<PoiResultInfo> {

		@Override
		public int compare(PoiResultInfo lhs, PoiResultInfo rhs) {
			if (lhs.getDistance() > rhs.getDistance())
				return 1;
			else if (lhs.getDistance() == rhs.getDistance()) {
				return 0;
			} else {
				return -1;
			}

		}

	}

	// 当提示搜索结果有错后1500ms移除弹框
	private void removeFloatWindow() {
		if (mhandler != null && removeWindowRunnable != null) {
			mhandler.postDelayed(removeWindowRunnable, 1500);
		}
	}

	public void choosePage(int type){

		if(type== ChoosePageChain.NEXT_PAGE){
			if(isManual){
				if(addressDialog!=null)
					addressDialog.nextPage();
			}else{

				FloatWindowUtil.chooseAddressPage(ChoosePageChain.NEXT_PAGE);
			}

		}else{
			if(isManual){
				if(addressDialog!=null)
					addressDialog.lastPage();
			}else{

				FloatWindowUtil.chooseAddressPage(ChoosePageChain.LAST_PAGE);
			}

		}
	}
}
