package in.edu.galgotiasuniversity.utils;

import android.content.Context;

public class MyApp extends com.activeandroid.app.Application {

    private static MyApp instance;

    public MyApp() {
        super();
        instance = this;
    }

    public static MyApp getApp() {
        return instance;
    }

    public static Context getContext() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Utils.loadFonts();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

}
