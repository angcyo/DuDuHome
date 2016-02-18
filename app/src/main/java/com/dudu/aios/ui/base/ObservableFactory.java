package com.dudu.aios.ui.base;

/**
 * Created by lxh on 2016/1/20.
 */
public class ObservableFactory {

    private static ObservableFactory observableFactory;

    private TitleBarObservable titleBarObservable;

    private CommonObservable commonObservable;


    public ObservableFactory() {

    }

    public static ObservableFactory getInstance() {

        if (observableFactory == null) {
            observableFactory = new ObservableFactory();
        }
        return observableFactory;
    }

    public TitleBarObservable getTitleObservable() {

        if (titleBarObservable == null) {
            titleBarObservable = new TitleBarObservable();
            titleBarObservable.init();
        }
        return titleBarObservable;
    }

    public CommonObservable getCommonObservable() {

        if (commonObservable == null) {
            commonObservable = new CommonObservable();
        }
        return commonObservable;
    }

}
