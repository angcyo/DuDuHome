package com.dudu.aios.ui.map.observable;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableDouble;
import android.databinding.ObservableField;
import android.view.View;

import com.dudu.map.NavigationProxy;
import com.dudu.navi.entity.PoiResultInfo;

import java.text.DecimalFormat;

/**
 * Created by lxh on 2016/2/13.
 */
public class MapListItemObservable {

    public final ObservableField<String> addressName =new ObservableField<>();
    public final ObservableField<String> address =new ObservableField<>();
    public final ObservableField<String> distance =new ObservableField<>();
    public final ObservableField<String> number = new  ObservableField<>();
    public final ObservableBoolean showNumber = new ObservableBoolean();
    public final ObservableDouble lat = new ObservableDouble();
    public final ObservableDouble lon = new ObservableDouble();

    public final ObservableField<PoiResultInfo> poiResult = new ObservableField<>();

    public MapListItemObservable() {
    }

    public MapListItemObservable(String addressName,String address,String distance,String number,double lat,double lon){
        this.addressName.set(addressName);
        this.address.set(address);
        this.distance.set(distance);
        this.number.set(number);
        this.showNumber.set(number!=null);
    }

    public MapListItemObservable(PoiResultInfo poiResultInfo,String number,boolean showNumber){
        this.poiResult.set(poiResultInfo);
        this.addressName.set(poiResultInfo.getAddressTitle());
        this.address.set(poiResultInfo.getAddressDetial());
        double distance = poiResultInfo.getDistance();
        String s = distance > 1000 ? formatMapDouble(distance / 1000) + "千米" : Math
                .round(distance) + "米";
        this.distance.set(s);
        this.number.set(number);
        this.showNumber.set(showNumber);
        this.lat.set(poiResultInfo.getLatitude());
        this.lon.set(poiResultInfo.getLongitude());
    }

    private double formatMapDouble(double value) {
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        return Double.parseDouble(decimalFormat.format(value));
    }

}
