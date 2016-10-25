package in.edu.galgotiasuniversity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.ViewGroup;
import android.widget.Toast;

import in.edu.galgotiasuniversity.utils.AppStatus;
import in.edu.galgotiasuniversity.utils.Utils;

/**
 * Created by Rohan Garg
 */

public class LogoActivity extends Activity {

    Toast toast;

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        ButterKnife.unbind(this);
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
                    if (AppStatus.getInstance(getApplicationContext()).isOnline()) {
                        showToast("Reconnecting to the server...", Toast.LENGTH_SHORT);
                        startActivityForResult(new Intent(getApplicationContext(), LoginActivity.class), 0);
                        overridePendingTransition(R.anim.push_up_in, 0);
                    } else {
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
                        finish();
                    }
                } else {
                    PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().clear().apply();
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
                    finish();
                }
            }
        }, 1000);
    }

    void showToast(String msg, int duration) {
        toast = Toast.makeText(getApplicationContext(), msg, duration);
        Utils.setFontAllView((ViewGroup) toast.getView());
        toast.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        showToast("Please wait", Toast.LENGTH_SHORT);
    }
}