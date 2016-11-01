package in.edu.galgotiasuniversity.fragments;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.ButterKnife;
import butterknife.OnClick;
import in.edu.galgotiasuniversity.MainActivity;
import in.edu.galgotiasuniversity.R;
import in.edu.galgotiasuniversity.adapters.DateWiseAdapter;
import in.edu.galgotiasuniversity.data.AttendanceColumns;
import in.edu.galgotiasuniversity.data.AttendanceProvider;
import in.edu.galgotiasuniversity.interfaces.OnError;
import in.edu.galgotiasuniversity.interfaces.OnTaskCompleted;
import in.edu.galgotiasuniversity.models.Date;
import in.edu.galgotiasuniversity.networking.AttendanceTask;
import in.edu.galgotiasuniversity.utils.NetworkStatus;
import in.edu.galgotiasuniversity.utils.Utils;

//import in.edu.galgotiasuniversity.networking.AttendanceTask;

/**
 * Created on 25-01-2016.
 */
public class DateWiseFragment extends Fragment {

    private static final String FRAG_TAG_DATE_PICKER = "fragment_date_picker_name";
    //    private static final int CURSOR_LOADER_ID = 0;
    View view;
    ArrayList<String> titles, contents1, contents2, contents3, contents4;
    LinearLayoutManager layoutManager;
    SharedPreferences sp;
    View recyclerView;
    DateWiseAdapter dateWiseAdapter;
    Date from_date, to_date;
    Calendar c;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (container == null) return null;
        view = inflater.inflate(R.layout.fragment_day_by_day, container, false);
        ButterKnife.bind(this, view);
        titles = new ArrayList<>();
        contents1 = new ArrayList<>();
        contents2 = new ArrayList<>();
        contents3 = new ArrayList<>();
        contents4 = new ArrayList<>();
        recyclerView = ButterKnife.findById(view, R.id.dayByDay_content_list);
        assert recyclerView != null;

        sp = PreferenceManager.getDefaultSharedPreferences(getActivity());

        c = Calendar.getInstance();
        from_date = new Date(c.get(Calendar.DATE), c.get(Calendar.MONTH), c.get(Calendar.YEAR));
        to_date = new Date(c.get(Calendar.DATE), c.get(Calendar.MONTH), c.get(Calendar.YEAR));

        if (savedInstanceState != null) {
            titles = savedInstanceState.getStringArrayList("titles");
            contents1 = savedInstanceState.getStringArrayList("contents1");
            contents2 = savedInstanceState.getStringArrayList("contents2");
            contents3 = savedInstanceState.getStringArrayList("contents3");
            contents4 = savedInstanceState.getStringArrayList("contents4");
            from_date = savedInstanceState.getParcelable("from_date");
            to_date = savedInstanceState.getParcelable("to_date");
            setupRecyclerView((RecyclerView) recyclerView);
        } else {
            setupRecyclerView((RecyclerView) recyclerView);
            Log.d("FROM_DATE", from_date.getDate());
            fetch();
        }

        setFromButtonText();
        setToButtonText();
        Utils.setFontAllView((ViewGroup) view);
        return view;
    }

    void setFromButtonText() {
        Button button = ButterKnife.findById(view, R.id.from_date);
        String string = "FROM: " + from_date.getDate();
        button.setText(string);
    }

    void setToButtonText() {
        Button button = ButterKnife.findById(view, R.id.to_date);
        String string = "TO: " + to_date.getDate();
        button.setText(string);
    }

    void fetch() {
        new AttendanceTask((MainActivity) this.getContext(), new OnTaskCompleted() {
            @Override
            public void onTaskCompleted() {
                taskCompleted();
            }
        }, new OnError() {
            @Override
            public void onError() {
                onErrorReceived();
            }
        }, from_date.getDate(), to_date.getDate()).execute();
    }

    public void taskCompleted() {
//        try {
//            JSONObject subjectWiseList = new JSONObject(sp.getString("dayByDayList", ""));
//            titles.clear();
//            contents1.clear();
//            contents2.clear();
//            contents3.clear();
//            contents4.clear();
//            for (int i = 0; i < subjectWiseList.length(); i++) {
//                JSONArray data = subjectWiseList.getJSONArray(Integer.toString(i));
//                titles.add(data.getString(1));
//                contents1.add(data.getString(0));
//                contents2.add(data.getString(2));
//                contents3.add(data.getString(3));
//                contents4.add(data.getString(4));
//            }
//            from_date.setDate(sp.getString("from_date", from_date.getDate()));
//            to_date.setDate(sp.getString("to_date", to_date.getDate()));
//            setFromButtonText();
//            setToButtonText();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        dateWiseAdapter.notifyDataSetChanged();
//        getActivity().getLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);

        Cursor mCursor = getContext().getContentResolver().query(AttendanceProvider.Attendance.CONTENT_URI,
                new String[]{AttendanceColumns.KEY, AttendanceColumns.SEMESTER, AttendanceColumns.DATE, AttendanceColumns.SUBJECT_NAME, AttendanceColumns.TIME_SLOT, AttendanceColumns.ATTENDANCE_TYPE, AttendanceColumns.STATUS},
                null,
                null,
                null);

        mCursor.moveToFirst();
        Log.d("DWF", String.valueOf(mCursor.getCount()));
        for (int i = 0; i < mCursor.getCount(); i++) {
            Log.d("DWF", mCursor.getString(mCursor.getColumnIndex(AttendanceColumns.SEMESTER)) + mCursor.getString(mCursor.getColumnIndex(AttendanceColumns.DATE)) + mCursor.getString(mCursor.getColumnIndex(AttendanceColumns.SUBJECT_NAME)) + mCursor.getString(mCursor.getColumnIndex(AttendanceColumns.TIME_SLOT)) + mCursor.getString(mCursor.getColumnIndex(AttendanceColumns.ATTENDANCE_TYPE)) + mCursor.getString(mCursor.getColumnIndex(AttendanceColumns.STATUS)));
            mCursor.moveToNext();
        }
        mCursor.close();

    }

    public void onErrorReceived() {
//        if (NetworkStatus.getInstance(getActivity()).isOnline()) {
//            if (!MainActivity.isCookieRefreshed)
//                startActivity(new Intent(getActivity(), LoginActivity.class));
//            showToast("Reconnecting to the server", Toast.LENGTH_SHORT);
//        }
        if (new NetworkStatus(getActivity()).isOnline())
            showToast("Aw, Snap! Please try again", Toast.LENGTH_SHORT);
        else
            showToast("Offline!", Toast.LENGTH_SHORT);
//        if (!(sp.getBoolean("isDayByDayListLoaded", false))) {
//            titles.clear();
//            contents1.clear();
//            contents2.clear();
//            contents3.clear();
//            contents4.clear();
//            titles.add("Aw, Snap!");
//            contents1.add("");
//            contents2.add("Error connecting to the server");
//            contents3.add("");
//            contents4.add("");
//            dateWiseAdapter.notifyDataSetChanged();
//            return;
//        }
        taskCompleted();
    }

    void showToast(String msg, int duration) {
        Toast toast = Toast.makeText(this.getContext(), msg, duration);
        Utils.setFontAllView((ViewGroup) toast.getView());
        toast.show();
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        layoutManager = new LinearLayoutManager(this.getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        dateWiseAdapter = new DateWiseAdapter(this.getContext(), titles, contents1, contents2, contents3, contents4);
        recyclerView.setAdapter(dateWiseAdapter);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList("titles", titles);
        outState.putStringArrayList("contents1", contents1);
        outState.putStringArrayList("contents2", contents2);
        outState.putStringArrayList("contents3", contents3);
        outState.putStringArrayList("contents4", contents4);
        outState.putParcelable("from_date", from_date);
        outState.putParcelable("to_date", to_date);
    }

    @OnClick(R.id.from_date)
    void setFROM_DATE() {
        CalendarDatePickerDialogFragment cdp = new CalendarDatePickerDialogFragment()
                .setOnDateSetListener(new CalendarDatePickerDialogFragment.OnDateSetListener() {
                    @Override
                    public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear, int dayOfMonth) {
                        from_date.setDay(dayOfMonth);
                        from_date.setMonth(monthOfYear);
                        from_date.setYear(year);
                        Log.d("FROM_DATE", from_date.getDate());
                        setFromButtonText();
                        fetch();
                    }
                })
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setPreselectedDate(from_date.getYear(), from_date.getMonth(), from_date.getDay())
                .setDateRange(null, null)
                .setThemeDark();
        cdp.show(getActivity().getSupportFragmentManager(), FRAG_TAG_DATE_PICKER);
    }

    @OnClick(R.id.to_date)
    void setTO_DATE() {
        CalendarDatePickerDialogFragment cdp = new CalendarDatePickerDialogFragment()
                .setOnDateSetListener(new CalendarDatePickerDialogFragment.OnDateSetListener() {
                    @Override
                    public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear, int dayOfMonth) {
                        to_date.setDay(dayOfMonth);
                        to_date.setMonth(monthOfYear);
                        to_date.setYear(year);
                        Log.d("TO_DATE", to_date.getDate());
                        setToButtonText();
                        fetch();
                    }
                })
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setPreselectedDate(to_date.getYear(), to_date.getMonth(), to_date.getDay())
                .setDateRange(null, null)
                .setThemeDark();
        cdp.show(getActivity().getSupportFragmentManager(), FRAG_TAG_DATE_PICKER);
    }
}