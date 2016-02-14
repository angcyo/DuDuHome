package com.dudu.aios.ui.map;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;

/**
 * Created by lxh on 2016/2/13.
 */
public class MapListItemObservable {

    public final ObservableField<String> addressName =new ObservableField<>();
    public final ObservableField<String> address =new ObservableField<>();
    public final ObservableField<String> distance =new ObservableField<>();
    public final ObservableField<String> number = new  ObservableField<>();
    public final ObservableBoolean showNumber = new ObservableBoolean();

    public MapListItemObservable() {
        addressName.set("海岸城");
        address.set("文心三路9号");
        distance.set("200米");
        number.set("1.");
    }
}
