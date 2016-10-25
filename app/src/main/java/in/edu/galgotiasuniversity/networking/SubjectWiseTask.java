package in.edu.galgotiasuniversity.networking;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import in.edu.galgotiasuniversity.Constants;
import in.edu.galgotiasuniversity.MainActivity;
import in.edu.galgotiasuniversity.interfaces.OnError;
import in.edu.galgotiasuniversity.interfaces.OnTaskCompleted;

/**
 * Created by Rohan Garg on 29-01-2016.
 */
public class SubjectWiseTask extends AsyncTask<Void, Integer, Void> {

    protected final String TAG = "SUBJECT_WISE_TASK";
    MainActivity context;
    Map<String, String> cookies;
    Connection.Response res;
    ProgressDialog dialog;
    Document document;
    int progress;
    private OnTaskCompleted listener;
    private OnError error_listener;

    public SubjectWiseTask(MainActivity context, OnTaskCompleted listener, OnError error_listener) {
        this.context = context;
        this.listener = listener;
        this.error_listener = error_listener;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        progress = values[0];
        if (progress < 0) {
            error_listener.onError();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
//        context.setRequestedOrientation(context.getResources().getConfiguration().orientation);
        dialog = ProgressDialog.show(context, "", "Loading...", true);
        cookies = (Map<String, String>) readObjectFromMemory("cookies");
        progress = 0;
    }


    @Override
    protected Void doInBackground(Void... params) {
        try {
            if (cookies == null) return null;
            if (Constants.ATTENDANCE == null) {
                Constants.ATTENDANCE = Jsoup
                        .connect(Constants.ATTENDANCE_URL)
                        .userAgent(Constants.USER_AGENT)
                        .referrer(Constants.ATTENDANCE_URL)
                        .followRedirects(false)
                        .header(Constants.HEADER1[0], Constants.HEADER1[1])
                        .header(Constants.HEADER2[0], Constants.HEADER2[1])
                        .header(Constants.HEADER3[0], Constants.HEADER3[1])
                        .header(Constants.HEADER4[0], Constants.HEADER4[1])
                        .cookies(cookies)
                        .method(Connection.Method.GET)
                        .timeout(Constants.TIMEOUT)
                        .execute();
                Log.d(TAG, "FETCHED");
            }
            document = Constants.ATTENDANCE.parse();
            publishProgress(50);
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
            publishProgress(-1);
        }
        try {
            if (document == null) return null;
            HashMap<String, String> POST_DATA = new HashMap<>();
            Elements inputs = document.select("input:not([name*=btnShowAtt],[class=ctrlButtonCommon],[id=hdnAppPath],[disabled=disabled])");
            for (Element input : inputs) {
                POST_DATA.put(input.attr("name"), input.attr("value"));
            }
            POST_DATA.put(document.select("input[name*=btnSubjectWiseAtt]").attr("name"), document.select("input[name*=btnSubjectWiseAtt]").attr("value"));
            System.out.print(POST_DATA);
            res = Jsoup
                    .connect(Constants.ATTENDANCE_URL)
                    .userAgent(Constants.USER_AGENT)
                    .referrer(Constants.REFERRER)
                    .followRedirects(false)
                    .header(Constants.HEADER1[0], Constants.HEADER1[1])
                    .header(Constants.HEADER2[0], Constants.HEADER2[1])
                    .header(Constants.HEADER3[0], Constants.HEADER3[1])
                    .header(Constants.HEADER4[0], Constants.HEADER4[1])
                    .cookies(cookies)
                    .data(POST_DATA)
                    .method(Connection.Method.POST)
                    .timeout(Constants.TIMEOUT)
                    .execute();
            document = res.parse();
            System.out.print(document.toString());
            publishProgress(100);
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
            publishProgress(-1);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (progress == 100) {

            String value = document.select("input[name*=__VIEWSTATE]").attr("value");
            Log.d(TAG, value);
            byte[] data = Base64.decode(value, Base64.DEFAULT);
            try {
                String text = new String(data, "UTF-8");
                String sem = document.select("input[name*=txtSemester]").attr("value");
                text = text.replaceAll("[^a-zA-Z0-9&, ().:;-]", "");
                text = text.replaceAll("[:;]", "devRG");
                text = text.replaceAll("ddd", "devRG");
                String[] temp = text.split("devRG");

                String name;
                int present, absent, k = 0;
                JSONObject subjectWiseList = new JSONObject();
                for (int i = 0; i < temp.length; i++) {
                    if (temp[i].equals(sem)) {
                        name = temp[++i];
                        present = Integer.parseInt(temp[++i]);
                        absent = Integer.parseInt(temp[++i]);
                        if (present != 0 || absent != 0) {
                            float total = present * 100.0f / (present + absent);
                            subjectWiseList.put(String.valueOf(k), new JSONArray()
                                    .put(name)
                                    .put(Integer.toString(present))
                                    .put(Integer.toString(absent))
                                    .put(String.format(Locale.ENGLISH, "%.2f", total))
                            );
                            k++;
                        }
                    }
                }

                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
                editor.putBoolean("isSubjectWiseListLoaded", true);
                editor.putString("subjectWiseList", subjectWiseList.toString());
                editor.apply();

            } catch (Exception e) {
                Log.d(TAG, e.getMessage());
            }

            listener.onTaskCompleted();
        }
        dialog.dismiss();
//        context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
    }

    public Object readObjectFromMemory(String filename) {
        Object defaultObject = null;
        FileInputStream fis;
        try {
            fis = context.openFileInput(filename);
            ObjectInputStream is = new ObjectInputStream(fis);
            defaultObject = is.readObject();
            is.close();
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
        return defaultObject;
    }
}