package in.edu.galgotiasuniversity.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.content.ContextCompat;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.activeandroid.Cache;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import in.edu.galgotiasuniversity.R;
import in.edu.galgotiasuniversity.models.Subject;

public class WidgetViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private Context mContext;
    private int mWidgetId;
    private List<Subject> subjects;

    WidgetViewsFactory(Context context, Intent intent) {
        mContext = context;
        mWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        subjects = new ArrayList<>();
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onDestroy() {
        if (subjects != null)
            subjects = null;
    }

    @Override
    public int getCount() {
        if (subjects != null)
            return subjects.size();
        return 0;
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if (subjects != null) {
            RemoteViews view = new RemoteViews(mContext.getPackageName(), R.layout.widget_row_subject_wise);
            view.setTextViewText(R.id.title, subjects.get(position).NAME);
            view.setTextViewText(R.id.content1, subjects.get(position).ATTENDANCE_TYPE);
            view.setTextViewText(R.id.content2, mContext.getString(R.string.present) + " :" + subjects.get(position).PRESENT);
            view.setTextViewText(R.id.content3, mContext.getString(R.string.absent) + " :" + subjects.get(position).ABSENT);
            Float percentage = subjects.get(position).PERCENTAGE;
            view.setTextViewText(R.id.content4, String.format(Locale.ENGLISH, "%.2f", percentage) + "%");

            if (percentage >= 75f) {
                view.setInt(R.id.cardColor, "setBackgroundColor", ContextCompat.getColor(mContext, R.color.green));
            } else {
                view.setInt(R.id.cardColor, "setBackgroundColor", ContextCompat.getColor(mContext, R.color.dark_red));
            }
            return view;
        }
        return null;
    }

    @Override
    public void onDataSetChanged() {
        subjects.clear();
        String subjects_query = "SELECT DISTINCT * FROM Records GROUP BY SUBJECT_NAME ORDER BY SUBJECT_NAME ASC";
        // Execute query on the underlying ActiveAndroid SQLite database
        Cursor subjects_cursor = getCursor(subjects_query);
        while (subjects_cursor.moveToNext()) {
            Subject subject = new Subject();
            subject.NAME = subjects_cursor.getString(subjects_cursor.getColumnIndex("SUBJECT_NAME"));
            String status_query = "SELECT * FROM Records WHERE SUBJECT_NAME = '" + subject.NAME + "' AND STATUS = ";
            subject.PRESENT = getCursor(status_query + "'P'").getCount();
            subject.ABSENT = getCursor(status_query + "'A'").getCount();
            subject.PERCENTAGE = (100f * subject.PRESENT) / (subject.PRESENT + subject.ABSENT);
            subject.ATTENDANCE_TYPE = subjects_cursor.getString(subjects_cursor.getColumnIndex("ATTENDANCE_TYPE"));
            subjects.add(subject);
        }
        subjects_cursor.close();
    }

    private Cursor getCursor(String query) {
        return Cache.openDatabase().rawQuery(query, null);
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
