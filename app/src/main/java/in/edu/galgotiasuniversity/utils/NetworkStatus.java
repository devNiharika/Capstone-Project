package in.edu.galgotiasuniversity.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class NetworkStatus {

    private Context context;
    private boolean connected = false;

    public NetworkStatus(Context ctx) {
        context = ctx.getApplicationContext();
    }

    public boolean isOnline() {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            connected = networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected();
            return connected;
        } catch (Exception e) {
            System.out.println("CheckConnectivity Exception: " + e.getMessage());
            Log.v("Connectivity", e.toString());
        }
        return connected;
    }
}