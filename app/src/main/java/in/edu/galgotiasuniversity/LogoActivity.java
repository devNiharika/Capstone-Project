package in.edu.galgotiasuniversity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.ViewGroup;
import android.widget.Toast;

import in.edu.galgotiasuniversity.utils.Utils;

public class LogoActivity extends Activity {

    Toast toast;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.gc();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);
        Utils.setFontAllView((ViewGroup) findViewById(R.id.root));
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (toast != null) {
                    toast.cancel();
                }
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                if (sharedPreferences.getBoolean("rememberMe", false)) {
                    showToast(getString(R.string.reconnecting), Toast.LENGTH_SHORT);
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    overridePendingTransition(R.anim.push_up_in, 0);
                } else {
                    PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().clear().apply();
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
                }
                finish();
            }
        }, 1000);
    }

    void showToast(String msg, int duration) {
        toast = Toast.makeText(getApplicationContext(), msg, duration);
        Utils.setFontAllView((ViewGroup) toast.getView());
        toast.show();
    }

    @Override
    public void onBackPressed() {
        showToast(getString(R.string.please_wait), Toast.LENGTH_SHORT);
    }
}