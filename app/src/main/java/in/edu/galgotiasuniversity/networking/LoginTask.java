package in.edu.galgotiasuniversity.networking;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

import dev.rg.ProgressButton.iml.ActionProcessButton;
import in.edu.galgotiasuniversity.Constants;
import in.edu.galgotiasuniversity.LoginActivity;
import in.edu.galgotiasuniversity.MainActivity;
import in.edu.galgotiasuniversity.R;
import in.edu.galgotiasuniversity.utils.Utils;

/**
 * Created on 25-01-2016.
 */

public class LoginTask extends AsyncTask<Void, Integer, Void> {

    private final String TAG = "LOGIN_TASK";
    public LoginActivity context;
    private boolean isShownAsDialog, isQuickPass;
    private Map<String, String> cookies;
    private Document document;
    private HashMap<String, String> POST_DATA;
    private Elements inputs;
    private String id, pwd;
    private int progress;
    private String captchaText;

    @SuppressWarnings("unchecked")
    LoginTask(LoginActivity context, boolean isShownAsDialog, String captchaText, Map<String, String> cookies, boolean isQuickPass) {
        this.context = context;
        this.isShownAsDialog = isShownAsDialog;
        this.cookies = cookies;
        this.captchaText = captchaText;
        this.isQuickPass = isQuickPass;
    }

    @Override
    protected void onPreExecute() {
        int currentOrientation = context.getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        } else {
            context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        }
        if (context.loginID != null) {
            context.loginID.setEnabled(false);
            context.password.setEnabled(false);
            id = context.loginID.getText().toString();
            pwd = context.password.getText().toString();
            context.loginButton.setEnabled(false);
            context.loginButton.setMode(ActionProcessButton.Mode.PROGRESS);
            context.loginButton.setProgress(25);
        }
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        this.progress = progress[0];
        if (context.loginButton != null) {
            context.loginButton.setProgress(this.progress);
            if (this.progress < 0) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        context.loginButton.setEnabled(true);
                        if (!isShownAsDialog) {
                            context.loginID.setEnabled(true);
                            context.password.setEnabled(true);
                        } else context.finish();
                        context.loginButton.setProgress(0);
                    }
                }, 1500);
            }
        }
    }

    @Override
    protected Void doInBackground(Void... args) {

        if (isQuickPass) {
            Log.d(TAG, "Quick login success");
            publishProgress(100);
            return null;
        }

        try {
            Connection.Response res;
            if (cookies != null) {
                res = Jsoup
                        .connect(Constants.LOGIN_URL)
                        .userAgent(Constants.USER_AGENT)
                        .referrer(Constants.REFERRER)
                        .cookies(cookies)
                        .header(Constants.HEADER1[0], Constants.HEADER1[1])
                        .header(Constants.HEADER2[0], Constants.HEADER2[1])
                        .header(Constants.HEADER3[0], Constants.HEADER3[1])
                        .header(Constants.HEADER4[0], Constants.HEADER4[1])
                        .method(Connection.Method.GET)
                        .timeout(Constants.TIMEOUT)
                        .execute();
                cookies.putAll(res.cookies());
            } else {
                res = Jsoup
                        .connect(Constants.LOGIN_URL)
                        .userAgent(Constants.USER_AGENT)
                        .referrer(Constants.REFERRER)
                        .header(Constants.HEADER1[0], Constants.HEADER1[1])
                        .header(Constants.HEADER2[0], Constants.HEADER2[1])
                        .header(Constants.HEADER3[0], Constants.HEADER3[1])
                        .header(Constants.HEADER4[0], Constants.HEADER4[1])
                        .method(Connection.Method.GET)
                        .timeout(Constants.TIMEOUT)
                        .execute();
                cookies = res.cookies();
            }
            document = res.parse();
            POST_DATA = new HashMap<>();
            inputs = document.select("input:not([name=btnValue])");
            for (Element input : inputs) {
//                if (captchaText != null && input.attr("name").startsWith("txt"))
//                    POST_DATA.put(input.attr("name"), captchaText);
//                else
                POST_DATA.put(input.attr("name"), input.attr("value"));
            }

            inputs = document.select("input[class=text-box-background]");
            POST_DATA.put(inputs.get(0).attr("name"), id);
            POST_DATA.put(inputs.get(1).attr("name"), pwd);
            if (captchaText != null)
                POST_DATA.put(inputs.get(2).attr("name"), captchaText);
            POST_DATA.put(inputs.get(3).attr("name"), id);


//            POST_DATA.put("txtUserId", id);
//            POST_DATA.put("txtPass", pwd);

            for (String key : POST_DATA.keySet()) {
                System.out.println(key + " " + POST_DATA.get(key));
            }

//            ColorUtils colorUtils = new ColorUtils();
//            Log.d("Color Test", colorUtils.getColorNameFromHex(Color.parseColor("#00ff00")));
//            Log.d("Color Test", colorUtils.getColorNameFromHex(Color.parseColor("Blue")));
//            Log.d("Color Test", colorUtils.getColorNameFromHex(Color.parseColor("#006600")));
//            Log.d("Color Test", colorUtils.getColorNameFromHex(Color.parseColor("#00cc00")));
//            Log.d("Color Test", colorUtils.getColorNameFromHex(Color.parseColor("#0000ff")));
//            Log.d("Color Test", colorUtils.getColorNameFromHex(Color.parseColor("#ffc0cb")));
//            Log.d("Color Test", colorUtils.getColorNameFromHex(Color.parseColor("#ff0000")));

            publishProgress(50);
        } catch (IOException | IndexOutOfBoundsException e) {
            e.printStackTrace();
            publishProgress(-1);
            return null;
        }

        try {
            Connection.Response res = Jsoup
                    .connect(Constants.LOGIN_URL)
                    .userAgent(Constants.USER_AGENT)
                    .referrer(Constants.REFERRER)
                    .followRedirects(false)
                    .header(Constants.HEADER1[0], Constants.HEADER1[1])
                    .header(Constants.HEADER2[0], Constants.HEADER2[1])
                    .header(Constants.HEADER3[0], Constants.HEADER3[1])
                    .header(Constants.HEADER4[0], Constants.HEADER4[1])
                    .data(POST_DATA)
                    .cookies(cookies)
                    .method(Connection.Method.POST)
                    .timeout(Constants.TIMEOUT)
                    .execute();
            cookies.putAll(res.cookies());
            document = res.parse();
            publishProgress(75);
        } catch (IOException e) {
            e.printStackTrace();
            publishProgress(-1);
            return null;
        }

        try {
            Log.d(TAG, document.title());
            if (!(document.title().equals(context.getString(R.string.login_page_doc_title))))
                publishProgress(100);
            else publishProgress(-1);
        } catch (NullPointerException e) {
            e.printStackTrace();
            publishProgress(-1);
            return null;
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (progress == 100) {
            writeObjectToMemory("cookies", cookies);
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = sp.edit();
            if (context.rememberMe.isChecked()) editor.putBoolean("rememberMe", true);
            editor.putString("loginID", id);
            editor.putString("password", pwd);
            editor.apply();
            String name = "";
            if (sp.getBoolean("isProfileLoaded", false)) {
                try {
                    JSONObject profile = new JSONObject(sp.getString("profile", ""));
                    JSONArray data = profile.getJSONArray("0");
                    name = data.getString(1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (!isShownAsDialog) {
                showToast(context.getString(R.string.welcome_toast) + name, Toast.LENGTH_SHORT);
            } else {
                showToast(context.getString(R.string.welcome_back_toast) + name, Toast.LENGTH_SHORT);
            }
            context.startActivity(new Intent(context, MainActivity.class));
            context.overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
            MainActivity.isCookieRefreshed = true;
            context.finish();
        }
        context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
    }

    private void showToast(String msg, int duration) {
        Toast toast = Toast.makeText(context.getApplicationContext(), msg, duration);
        Utils.setFontAllView((ViewGroup) toast.getView());
        toast.show();
    }

    private void writeObjectToMemory(String filename, Object object) {
        FileOutputStream fos;
        try {
            fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(object);
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}