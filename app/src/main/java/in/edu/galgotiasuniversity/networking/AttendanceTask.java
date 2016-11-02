package in.edu.galgotiasuniversity.networking;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.activeandroid.ActiveAndroid;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import in.edu.galgotiasuniversity.Constants;
import in.edu.galgotiasuniversity.MainActivity;
import in.edu.galgotiasuniversity.data.Record;
import in.edu.galgotiasuniversity.interfaces.OnError;
import in.edu.galgotiasuniversity.interfaces.OnTaskCompleted;

/**
 * Created on 25-01-2016.
 */
public class AttendanceTask extends AsyncTask<Void, Integer, Void> {

    private final String TAG = "ATTENDANCE_TASK";
    private MainActivity context;
    private Map<String, String> cookies;
    private Connection.Response res;
    private ProgressDialog dialog;
    private Document document;
    private int progress;
    private String FROM_DATE, TO_DATE;
    private OnTaskCompleted listener;
    private OnError error_listener;

    public AttendanceTask(MainActivity context, OnTaskCompleted listener, OnError error_listener, String FROM_DATE, String TO_DATE) {
        this.context = context;
        this.listener = listener;
        this.error_listener = error_listener;
        this.FROM_DATE = FROM_DATE;
        this.TO_DATE = TO_DATE;
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
//            POST_DATA.put(document.select("input[name*=btnDateWiseAtt]").attr("name"), document.select("input[name*=btnDateWiseAtt]").attr("value"));
//            Calendar c = Calendar.getInstance();
//            String date = String.valueOf(c.get(Calendar.DATE));
//            //String date = "4";
//            String month = String.valueOf(c.get(Calendar.MONTH) + 1);
//            String year = String.valueOf(c.get(Calendar.YEAR));
//            Log.d("Date", date + "/" + month + "/" + year);
//            POST_DATA.put("ctl00$ctl00$MCPH1$SCPH$txtFrom", date + "/" + month + "/" + year);
//            POST_DATA.put("ctl00$ctl00$MCPH1$SCPH$txtTo", date + "/" + month + "/" + year);
            POST_DATA.put("ctl00$ctl00$MCPH1$SCPH$txtFrom", FROM_DATE);
            POST_DATA.put("ctl00$ctl00$MCPH1$SCPH$txtTo", TO_DATE);
            POST_DATA.put("ctl00$ctl00$MCPH1$SCPH$btnShowAtt", "Show");

//            for (String key : POST_DATA.keySet()) {
//                System.out.println(key + " " + POST_DATA.get(key));
//            }

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
//            Log.d("Document",document.toString());
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
            Iterator<Element> i = document.select("span[id*=lblSubjectName],span[id*=lblDate],span[id*=lblNAME],span[id*=lblTimeSlot],span[id*=lblAttType],span[id*=lblProgram]").iterator();
            ActiveAndroid.beginTransaction();
            Calendar c = Calendar.getInstance();
            DateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
            try {
                while (i.hasNext()) {
                    Record record = new Record();
                    record.SEMESTER = i.next().text();
                    Date date = df.parse(i.next().text());
                    c.setTime(date);
                    String year = String.format(Locale.ENGLISH, "%04d", c.get(Calendar.YEAR));
                    String month = String.format(Locale.ENGLISH, "%02d", c.get(Calendar.MONTH) + 1);
                    String day = String.format(Locale.ENGLISH, "%02d", c.get(Calendar.DATE));
                    record.DATE = Long.parseLong(year + month + day);
                    record.MM = c.get(Calendar.MONTH) + 1;
                    record.SUBJECT_NAME = i.next().text();
                    record.TIME_SLOT = i.next().text();
                    record.ATTENDANCE_TYPE = i.next().text();
                    record.STATUS = i.next().text();
                    record.KEY = String.valueOf(record.DATE) + record.TIME_SLOT;
                    record.save();
                }
                ActiveAndroid.setTransactionSuccessful();
            } catch (ParseException e) {
                e.printStackTrace();
            } finally {
                ActiveAndroid.endTransaction();
            }
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
            editor.putBoolean("isDayByDayListLoaded", true);
            editor.putString("from_date", FROM_DATE);
            editor.putString("to_date", TO_DATE);
            editor.apply();
            listener.onTaskCompleted();
        }
        dialog.dismiss();
//        context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
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
            Log.d(TAG, e.getMessage());
        }
        return defaultObject;
    }
}