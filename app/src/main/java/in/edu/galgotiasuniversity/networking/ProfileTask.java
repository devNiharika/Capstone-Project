package in.edu.galgotiasuniversity.networking;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Map;

import in.edu.galgotiasuniversity.Constants;
import in.edu.galgotiasuniversity.R;
import in.edu.galgotiasuniversity.interfaces.OnError;
import in.edu.galgotiasuniversity.interfaces.OnTaskCompleted;

/**
 * Created on 25-01-2016.
 */
public class ProfileTask extends AsyncTask<Void, Integer, Void> {

    private final String TAG = "PROFILE_TASK";
    private Activity context;
    private Map<String, String> cookies;
    private Connection.Response res;
    private ProgressDialog dialog;
    private Document document;
    private int progress;
    private OnTaskCompleted listener;
    private OnError error_listener;

    public ProfileTask(Activity context, OnTaskCompleted listener, OnError error_listener) {
        this.context = context;
        this.listener = listener;
        this.error_listener = error_listener;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        progress = values[0];
        dialog.setProgress(progress);
        if (progress < 0) {
            error_listener.onError();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        int currentOrientation = context.getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        } else {
            context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        }
        dialog = ProgressDialog.show(context, "", context.getString(R.string.loading), false);
        cookies = (Map<String, String>) readObjectFromMemory("cookies");
        progress = 0;
    }


    @Override
    protected Void doInBackground(Void... params) {
        try {
            if (cookies == null) return null;
            res = Jsoup
                    .connect(Constants.INFO_URL)
                    .userAgent(Constants.USER_AGENT)
                    .referrer(Constants.REFERRER)
                    .followRedirects(false)
                    .header(Constants.HEADER1[0], Constants.HEADER1[1])
                    .header(Constants.HEADER2[0], Constants.HEADER2[1])
                    .header(Constants.HEADER3[0], Constants.HEADER3[1])
                    .header(Constants.HEADER4[0], Constants.HEADER4[1])
                    .cookies(cookies)
                    .method(Connection.Method.GET)
                    .timeout(Constants.TIMEOUT)
                    .execute();
            document = res.parse();
            publishProgress(100);
        } catch (IOException e) {
            e.printStackTrace();
            publishProgress(-1);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (progress == 100) {
            JSONObject profile = new JSONObject();
            try {
                profile.put("0", new JSONArray().put(context.getString(R.string.name)).put(document.select("label[id*=lblName]").first().text()));
                profile.put("1", new JSONArray().put(context.getString(R.string.email)).put(document.select("label[id*=lblEmail1]").first().text()));
                if (!document.select("label[id*=lblPhone1]").first().text().equals(""))
                    profile.put("2", new JSONArray().put(context.getString(R.string.mobile)).put(document.select("label[id*=lblPhone1]").first().text()));
                else if (!document.select("label[id*=lblmob]").first().text().equals(""))
                    profile.put("2", new JSONArray().put(context.getString(R.string.mobile)).put(document.select("label[id*=lblmob]").first().text()));
                else
                    profile.put("2", new JSONArray().put(context.getString(R.string.mobile)).put(context.getString(R.string.no_data)));
                profile.put("3", new JSONArray().put(context.getString(R.string.dob)).put(document.select("label[id*=lblDOB]").first().text()));
                profile.put("4", new JSONArray().put(context.getString(R.string.gender)).put(document.select("label[id*=lblGender]").first().text()));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
            editor.putBoolean("isProfileLoaded", true);
            editor.putString("profile", profile.toString());
            editor.apply();
            listener.onTaskCompleted();
        }
        dialog.dismiss();
        context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
    }

    private Object readObjectFromMemory(String filename) {
        Object defaultObject = null;
        FileInputStream fis;
        try {
            fis = context.openFileInput(filename);
            ObjectInputStream is = new ObjectInputStream(fis);
            defaultObject = is.readObject();
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defaultObject;
    }
}