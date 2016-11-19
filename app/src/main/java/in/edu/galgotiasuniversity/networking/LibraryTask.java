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
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import in.edu.galgotiasuniversity.Constants;
import in.edu.galgotiasuniversity.R;
import in.edu.galgotiasuniversity.interfaces.OnError;
import in.edu.galgotiasuniversity.interfaces.OnTaskCompleted;

public class LibraryTask extends AsyncTask<Void, Integer, Void> {

    private final String TAG = "LIBRARY_TASK";
    private Activity context;
    private Map<String, String> cookies;
    private Connection.Response res;
    private ProgressDialog dialog;
    private Document document;
    private int progress;
    private OnTaskCompleted listener;
    private OnError error_listener;

    public LibraryTask(Activity context, OnTaskCompleted listener, OnError error_listener) {
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
                    .connect(Constants.LIBRARY_URL)
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
            publishProgress(50);
        } catch (IOException e) {
            e.printStackTrace();
            publishProgress(-1);
        }
        try {
            if (document == null) return null;
            HashMap<String, String> POST_DATA = new HashMap<>();
            Elements inputs = document.select("input:not([name*=btnReturned],[name*=btnDue],[name*=btnIssue],[id=hdnAppPath])");
            for (Element input : inputs) {
                POST_DATA.put(input.attr("name"), input.attr("value"));
            }
            POST_DATA.put(document.select("input[name*=btnIssue]").attr("name"), document.select("input[name*=btnIssue]").attr("value"));
            //POST_DATA.put(document.select("input[name*=btnReturned]").attr("name"), document.select("input[name*=btnIssue]").attr("value"));
            res = Jsoup
                    .connect(Constants.LIBRARY_URL)
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
            JSONObject library = new JSONObject();
            Element table = document.select("table[class=top-heading]").first();
            Iterator<Element> h = document.select("table[class=table]").first().select("label[id*=lblprogram],label[id*=lblsem],label[id*=lblttlfine],label[id*=lblbal]").iterator();

            //Iterator<Element> i = table.select("span[id*=txtTitle],span[id*=txtIssueDate],span[id*=txtDueDate],span[id*=ReturnDate],span[id*=txtTotAmount],span[id*=txtFineStatus]").iterator();
            Iterator<Element> i = table.select("span[id*=txtTitle],span[id*=txtDueDate],span[id*=ReturnDate],span[id*=txtTotAmount],span[id*=txtFineStatus]").iterator();

            try {
                library.put("0", new JSONArray()
                        .put(h.next().text())
                        .put(h.next().text())
                        .put(h.next().text())
                        .put(h.next().text()));
                int k = 1;
                while (i.hasNext()) {
                    library.put(Integer.toString(k), new JSONArray()
                            .put(i.next().text())
                            //.put("Issued: " + i.next().text() + "\nDue Date: " + i.next().text())
                            .put(context.getString(R.string.due_date) + ": " + i.next().text())
                            .put(context.getString(R.string.return_date) + ": " + i.next().text())
                            .put(context.getString(R.string.fine) + ": " + i.next().text() + " ( " + context.getString(R.string.status) + ": " + i.next().text() + " )"));
                    k++;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
            editor.putBoolean("isLibraryLoaded", true);
            editor.putString("library", library.toString());
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