package in.edu.galgotiasuniversity.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.activeandroid.Cache;

import java.util.List;

import in.edu.galgotiasuniversity.R;
import in.edu.galgotiasuniversity.models.Subject;


/**
 * Created by Rohan Garg on 15-06-2016.
 */

public class WidgetViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private Context mContext;
    private List<Subject> subjects;
    private int mWidgetId;
    private Cursor mCursor;

    WidgetViewsFactory(Context context, Intent intent) {
        mContext = context;
        mWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onDestroy() {
//        if (mCursor != null)
        mCursor.close();
//        if (subjects != null)
//        subjects = null;
    }

    @Override
    public int getCount() {
//        if (mCursor != null)

        return mCursor.getCount();
//        if (subjects != null)
//        return subjects.size();
//        return 0;
    }

    @Override
    public RemoteViews getViewAt(int position) {
//        if (mCursor != null) {
//            mCursor.moveToPosition(position);
//            RemoteViews view = new RemoteViews(mContext.getPackageName(), R.layout.);
//            view.setTextViewText(R.id.stock_symbol, mCursor.getString(mCursor.getColumnIndex(QuoteColumns.SYMBOL)));
//            view.setTextViewText(R.id.bid_price, mCursor.getString(mCursor.getColumnIndex(QuoteColumns.BIDPRICE)));
//            view.setTextViewText(R.id.change, mCursor.getString(mCursor.getColumnIndex(QuoteColumns.CHANGE)));
//
//            if (mCursor.getInt(mCursor.getColumnIndex(QuoteColumns.ISUP)) == 1) {
//                view.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_green);
//            } else {
//                view.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_red);
//            }
//            return view;
//        }
        RemoteViews view = new RemoteViews(mContext.getPackageName(), R.layout.widget_row_subject_wise);
        mCursor.moveToPosition(position);
        view.setTextViewText(R.id.title, mCursor.getString(mCursor.getColumnIndex("SUBJECT_NAME")));
        view.setTextViewText(R.id.content1, mCursor.getString(mCursor.getColumnIndex("ATTENDANCE_TYPE")));
//        if (subjects != null) {
//        Log.d(getClass().getSimpleName(), subjects.get(position).NAME);
//        view.setTextViewText(R.id.title, subjects.get(position).NAME);
//        Log.d(getClass().getSimpleName(), subjects.get(position).ATTENDANCE_TYPE);
//        view.setTextViewText(R.id.content1, subjects.get(position).ATTENDANCE_TYPE);
//        Log.d(getClass().getSimpleName(), String.valueOf(subjects.get(position).PRESENT));
//        view.setTextViewText(R.id.content2, "Present :" + subjects.get(position).PRESENT);
//        Log.d(getClass().getSimpleName(), String.valueOf(subjects.get(position).ABSENT));
//        view.setTextViewText(R.id.content3, "Absent :" + subjects.get(position).ABSENT);
//        Log.d(getClass().getSimpleName(), String.valueOf(subjects.get(position).PERCENTAGE));
//        Float percentage = subjects.get(position).PERCENTAGE;
//        view.setTextViewText(R.id.content4, String.format(Locale.ENGLISH, "%.2f", percentage) + "%");

//        if (percentage >= 75f) {
//            view.setInt(R.id.cardColor, "setBackgroundColor", ContextCompat.getColor(mContext, R.color.green));
//        } else {
//            view.setInt(R.id.cardColor, "setBackgroundColor", ContextCompat.getColor(mContext, R.color.dark_red));
//        }
        return view;
//        }
//        return null;
    }

    @Override
    public void onDataSetChanged() {
//        subjects = Record.getSubjects();
//        String[] projection = new String[]{"SUBJECT_NAME", "STATUS", "ATTENDANCE_TYPE"};
//        String selection = "STATUS" + " = ?";
//        String[] selectionArgs = new String[]{"P"};
//        mCursor = mContext.getContentResolver().query(ContentProvider.createUri(Record.class, null), null, null, null, null);
        // Query all items without any conditions
        String resultRecords = "SELECT DISTINCT * FROM Records GROUP BY SUBJECT_NAME ORDER BY SUBJECT_NAME ASC";
        System.out.println(resultRecords);
        // Execute query on the underlying ActiveAndroid SQLite database
        mCursor = Cache.openDatabase().rawQuery(resultRecords, null);
        mCursor.moveToFirst();
//        if (mCursor != null) {
//            mCursor.moveToFirst();
//            System.out.println(DatabaseUtils.dumpCursorToString(mCursor));
//            mCursor.close();
//        } else
//            Log.d(getClass().getSimpleName(), "CURSOR IS NULL");
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
