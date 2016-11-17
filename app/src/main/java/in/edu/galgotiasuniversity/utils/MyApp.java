package in.edu.galgotiasuniversity.utils;

import android.content.Context;

import com.devs.acr.AutoErrorReporter;

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
        //Crash Report
        if (!(new DebugStatus().isDebuggable(getContext())))
            AutoErrorReporter.get(this)
                    .setEmailAddresses("dev.NiharikaRastogi@gmail.com")
                    .setEmailSubject("GU mSIM Crash Report")
                    .start();
        Utils.loadFonts();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

}
