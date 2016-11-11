package in.edu.galgotiasuniversity.networking;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.util.Log;

import com.activeandroid.ActiveAndroid;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import in.edu.galgotiasuniversity.Constants;
import in.edu.galgotiasuniversity.data.Record;
import in.edu.galgotiasuniversity.data.Utils;
import in.edu.galgotiasuniversity.interfaces.OnError;
import in.edu.galgotiasuniversity.interfaces.OnTaskCompleted;
import in.edu.galgotiasuniversity.models.Date;

/**
 * Created on 25-01-2016.
 */
public class AttendanceTask extends AsyncTask<Void, Integer, Void> {

    private final String TAG = "ATTENDANCE_TASK";
    private Activity context;
    private Map<String, String> cookies;
    private ProgressDialog dialog;
    private Document document;
    private int progress;
    private String FROM_DATE, TO_DATE;
    private OnTaskCompleted listener;
    private OnError error_listener;
    private ArrayList<Record> records = new ArrayList<>();

    public AttendanceTask(Activity context, OnTaskCompleted listener, OnError error_listener, String FROM_DATE, String TO_DATE) {
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
        dialog = ProgressDialog.show(context, "", "Loading...", false);
        cookies = Utils.readObjectFromMemory(context, "cookies");
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
            e.printStackTrace();
            publishProgress(-1);
        }
        try {
            if (document == null) return null;
            HashMap<String, String> POST_DATA = new HashMap<>();
            Elements inputs = document.select("input:not([name*=btnShowAtt],[class=ctrlButtonCommon],[id=hdnAppPath],[disabled=disabled])");
            for (Element input : inputs) {
                POST_DATA.put(input.attr("name"), input.attr("value"));
            }
            POST_DATA.put("ctl00$ctl00$MCPH1$SCPH$btnShowAtt", "Show");
            DateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
            LocalDate START_DATE = new LocalDate(new DateTime(df.parse(FROM_DATE)));
            LocalDate END_DATE = new LocalDate(new DateTime(df.parse(TO_DATE)));

            for (LocalDate date = START_DATE; !date.isAfter(END_DATE); date = date.plusDays(Constants.MAX_DIFFERENCE)) {
                POST_DATA.put("ctl00$ctl00$MCPH1$SCPH$txtFrom", df.format(date.toDate()));
                POST_DATA.put("ctl00$ctl00$MCPH1$SCPH$txtTo", df.format(date.plusDays(Constants.MAX_DIFFERENCE).toDate()));
                System.out.println(POST_DATA);
                Connection.Response res = Jsoup
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
                addToRecords();
            }
            publishProgress(100);
        } catch (IOException e) {
            e.printStackTrace();
            publishProgress(-1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void addToRecords() {
        Iterator<Element> i = document.select("span[id*=lblSubjectName],span[id*=lblDate],span[id*=lblNAME],span[id*=lblTimeSlot],span[id*=lblAttType],span[id*=lblProgram]").iterator();
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        Date date = new Date();
        try {
            while (i.hasNext()) {
                Record record = new Record();
                record.SEMESTER = i.next().text();
                record.STRING_DATE = i.next().text();
                date.setDate(record.STRING_DATE, df);
                record.NUMERIC_DATE = date.getNumericDate();
                record.MM = date.getMonth() + 1;
                record.SUBJECT_NAME = i.next().text();
                record.TIME_SLOT = i.next().text();
                record.ATTENDANCE_TYPE = i.next().text();
                record.STATUS = i.next().text();
                record.KEY = date.getNumericDate() + record.TIME_SLOT;
                records.add(record);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (progress == 100) {

            ActiveAndroid.beginTransaction();
            try {
                System.out.println(String.valueOf(records.size()));
                for (Record record : records) {
                    record.save();
                }
                ActiveAndroid.setTransactionSuccessful();
            } finally {
                ActiveAndroid.endTransaction();
            }
            listener.onTaskCompleted();
        }
        dialog.dismiss();
        context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
    }
}