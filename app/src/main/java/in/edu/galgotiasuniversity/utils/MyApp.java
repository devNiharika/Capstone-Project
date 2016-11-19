package in.edu.galgotiasuniversity.utils;

import android.content.Context;

import com.devs.acr.AutoErrorReporter;

import in.edu.galgotiasuniversity.BuildConfig;
import in.edu.galgotiasuniversity.R;

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
        if (!BuildConfig.DEBUG)
            AutoErrorReporter.get(this)
                    .setEmailAddresses(getString(R.string.developer_email))
                    .setEmailSubject(getString(R.string.crash_email_subject))
                    .start();
        Utils.loadFonts();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

}
