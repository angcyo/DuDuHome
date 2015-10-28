package com.dudu.android.launcher.utils;

import java.util.List;

import android.widget.AdapterView.OnItemClickListener;

import com.dudu.android.launcher.bean.PoiResultInfo;

public class FloatWindowUtil {

	//创建弹窗
	public static void createWindow(){
		if(FloatWindow.getInstance().getCreateFloatWindowCallBack()!=null){
			FloatWindow.getInstance().getCreateFloatWindowCallBack().createFloatWindow();
		}
	}
	// 消息显示
	public static void showMessage(String message,String type){
		if(FloatWindow.getInstance().getMessageShowCallBack()!=null){
			FloatWindow.getInstance().getMessageShowCallBack().showMessage(message, type);
		}
	}
	
	// 移除弹框
	public static void removeFloatWindow(){
		if(FloatWindow.getInstance().getRemoveFloatWindowCallBack()!=null){
			FloatWindow.getInstance().getRemoveFloatWindowCallBack().removeFloatWindow();
		}
	}
	
	// 显示地址选择
	public static void showAddress(List<PoiResultInfo> poiList,OnItemClickListener listener){
		if(FloatWindow.getInstance().getAddressShowCallBack()!=null){
			FloatWindow.getInstance().getAddressShowCallBack().showAddress(poiList);
		}
		if(FloatWindow.getInstance().getAddressListItemClickCallback()!=null){
			FloatWindow.getInstance().getAddressListItemClickCallback().onAddressListItemClick(listener);
		}
	}
	
	// 显示路线优先策略选择
	public static void showStrategy(String[] str,OnItemClickListener listener){
		if(FloatWindow.getInstance().getStrategyChooseCallBack()!=null){
			FloatWindow.getInstance().getStrategyChooseCallBack().showStrategy(str);
		}
		if(FloatWindow.getInstance().getAddressListItemClickCallback()!=null){
			FloatWindow.getInstance().getAddressListItemClickCallback().onAddressListItemClick(listener);
		}
	}
	// 话筒的状态改变
	public static void changeVoice(int voice){
		if(FloatWindow.getInstance().getFloatVoiceChangeCallBack()!=null){
			FloatWindow.getInstance().getFloatVoiceChangeCallBack().onVoiceChange(voice);
		}
	}
}