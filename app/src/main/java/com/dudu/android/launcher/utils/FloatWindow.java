package com.dudu.android.launcher.utils;

import java.util.List;
import android.widget.AdapterView.OnItemClickListener;
import com.dudu.navi.entity.PoiResultInfo;

public class FloatWindow {

	private static FloatWindow floatWindow;

	private MessageShowCallBack messageShowCallBack;
	private AddressShowCallBack addressShowCallBack;
	private StrategyChooseCallBack strategyChooseCallBack;
	private FloatVoiceChangeCallBack floatVoiceChangeCallBack;
	private AddressListItemClickCallback addressListItemClickCallback;
	private RemoveFloatWindowCallBack removeFloatWindowCallBack;
	private CreateFloatWindowCallBack createFloatWindowCallBack;
	private ChooseAddressPageCallBack chooseAddressPageCallBack;

	public static final String MESSAGE_IN = "input";
	public static final String MESSAGE_OUT = "output";

	private boolean isWindowShow = false;

	public static FloatWindow getInstance() {
		if(floatWindow == null)
			floatWindow = new FloatWindow();
		return floatWindow;
	}
	
	public interface MessageShowCallBack{
		/**
		 * 显示对话消息
		 * @param message
		 * @param type
		 */
		void showMessage(String message, String type);
	}

	public interface FloatVoiceChangeCallBack{
		/**
		 * 说话声音改变时的回调
		 * @param voice
		 */
		void onVoiceChange(int voice);
	}

	public interface AddressShowCallBack{

		void showAddress();
		
	}
	
	public interface StrategyChooseCallBack{
		/**
		 * 显示路线优先策略选择列表
		 * @param
		 */
		void showStrategy();
	}
	
    public interface AddressListItemClickCallback{
    	/**
    	 * 地址选择列表的点击事件
    	 * @param listener
    	 */
    	void onAddressListItemClick(OnItemClickListener listener);
    }
    public interface RemoveFloatWindowCallBack{
    	/**
    	 * 移除悬浮窗口
    	 */
    	void removeFloatWindow();
    }
    
    public interface CreateFloatWindowCallBack{
    	void createFloatWindow();
    }

	public interface ChooseAddressPageCallBack{
		/**
		 * 上一页，下一页
		 * @param type
		 * @param page 第几页
		 */
		void choosePage(int type,int page);
	}

	public MessageShowCallBack getMessageShowCallBack() {
		return messageShowCallBack;
	}

	public void setMessageShowCallBack(MessageShowCallBack messageShowCallBack) {
		this.messageShowCallBack = messageShowCallBack;
	}

	public AddressShowCallBack getAddressShowCallBack() {
		return addressShowCallBack;
	}

	public void setAddressShowCallBack(AddressShowCallBack addressShowCallBack) {
		this.addressShowCallBack = addressShowCallBack;
	}

	public StrategyChooseCallBack getStrategyChooseCallBack() {
		return strategyChooseCallBack;
	}

	public void setStrategyChooseCallBack(
			StrategyChooseCallBack strategyChooseCallBack) {
		this.strategyChooseCallBack = strategyChooseCallBack;
	}
	public FloatVoiceChangeCallBack getFloatVoiceChangeCallBack() {
		return floatVoiceChangeCallBack;
	}

	public void setFloatVoiceChangeCallBack(
			FloatVoiceChangeCallBack floatVoiceChangeCallBack) {
		this.floatVoiceChangeCallBack = floatVoiceChangeCallBack;
	}

	public AddressListItemClickCallback getAddressListItemClickCallback() {
		return addressListItemClickCallback;
	}

	public void setAddressListItemClickCallback(
			AddressListItemClickCallback addressListItemClickCallback) {
		this.addressListItemClickCallback = addressListItemClickCallback;
	}

	public RemoveFloatWindowCallBack getRemoveFloatWindowCallBack() {
		return removeFloatWindowCallBack;
	}

	public void setRemoveFloatWindowCallBack(
			RemoveFloatWindowCallBack removeFloatWindowCallBack) {
		this.removeFloatWindowCallBack = removeFloatWindowCallBack;
	}

	public CreateFloatWindowCallBack getCreateFloatWindowCallBack() {
		return createFloatWindowCallBack;
	}

	public void setCreateFloatWindowCallBack(CreateFloatWindowCallBack createFloatWindowCallBack) {
		this.createFloatWindowCallBack = createFloatWindowCallBack;
	}

	public ChooseAddressPageCallBack getChooseAddressPageCallBack() {
		return chooseAddressPageCallBack;
	}

	public void setChooseAddressPageCallBack(ChooseAddressPageCallBack chooseAddressPageCallBack) {
		this.chooseAddressPageCallBack = chooseAddressPageCallBack;
	}

	public boolean isWindowShow(){
		return isWindowShow;
	}

	public void setIsWindowShow(boolean isWindowShow) {
		this.isWindowShow = isWindowShow;
	}
}
