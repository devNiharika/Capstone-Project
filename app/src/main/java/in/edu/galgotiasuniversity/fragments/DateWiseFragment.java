package in.edu.galgotiasuniversity.fragments;

import android.content.SharedPreferences;
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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import in.edu.galgotiasuniversity.Constants;
import in.edu.galgotiasuniversity.R;
import in.edu.galgotiasuniversity.adapters.DateWiseAdapter;
import in.edu.galgotiasuniversity.data.Record;
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
    View view;
    RecyclerView recyclerView;
    DateWiseAdapter dateWiseAdapter;
    Date FROM_DATE, TO_DATE, Q_FROM_DATE;
    List<Record> records;

    @BindView(R.id.fetch)
    Button fetch;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (container == null) return null;
        view = inflater.inflate(R.layout.fragment_date_wise, container, false);
        ButterKnife.bind(this, view);
        recyclerView = ButterKnife.findById(view, R.id.dayByDay_content_list);

        FROM_DATE = new Date();
        TO_DATE = new Date();
        Q_FROM_DATE = new Date();

        if (savedInstanceState != null) {
            FROM_DATE = savedInstanceState.getParcelable("FROM_DATE");
            TO_DATE = savedInstanceState.getParcelable("TO_DATE");
            records = Record.getAttendance(FROM_DATE, TO_DATE);
            showFetchButton(false);
            setupRecyclerView(recyclerView);
        } else {
            records = Record.getAttendance(FROM_DATE, TO_DATE);
            setupRecyclerView(recyclerView);
            fetch();
        }

        setFromButtonText();
        setToButtonText();
        Utils.setFontAllView((ViewGroup) view);
        return view;
    }

    void setFromButtonText() {
        Button button = ButterKnife.findById(view, R.id.from_date);
        String string = "FROM: " + FROM_DATE.getDate();
        button.setText(string);
    }

    void setToButtonText() {
        Button button = ButterKnife.findById(view, R.id.to_date);
        String string = "TO: " + TO_DATE.getDate();
        button.setText(string);
    }

    @OnClick(R.id.fetch)
    void fetch() {
        showFetchButton(false);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        try {
            if (sp.getString("FROM_DATE", "").equals(Constants.SEM_START_DATE))
                Q_FROM_DATE.setDate(sp.getString("TO_DATE", ""), df);
            else
                Q_FROM_DATE.setDate(FROM_DATE.getDate(), df);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        new AttendanceTask(this.getContext(), new OnTaskCompleted() {
            @Override
            public void onTaskCompleted() {
                taskCompleted();
            }
        }, new OnError() {
            @Override
            public void onError() {
                onErrorReceived();
            }
        }, Q_FROM_DATE.getDate(), TO_DATE.getDate()).execute();
    }

    void showFetchButton(boolean show) {
        if (show)
            fetch.setVisibility(View.VISIBLE);
        else
            fetch.setVisibility(View.GONE);
    }

    public void taskCompleted() {
        records = Record.getAttendance(FROM_DATE, TO_DATE);
        dateWiseAdapter = new DateWiseAdapter(this.getContext(), records);
        recyclerView.setAdapter(dateWiseAdapter);
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
        showFetchButton(true);
        taskCompleted();
    }

    void showToast(String msg, int duration) {
        Toast toast = Toast.makeText(this.getContext(), msg, duration);
        Utils.setFontAllView((ViewGroup) toast.getView());
        toast.show();
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        dateWiseAdapter = new DateWiseAdapter(this.getContext(), records);
        recyclerView.setAdapter(dateWiseAdapter);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("FROM_DATE", FROM_DATE);
        outState.putParcelable("TO_DATE", TO_DATE);
    }

    @OnClick(R.id.from_date)
    void setFROM_DATE() {
        CalendarDatePickerDialogFragment cdp = new CalendarDatePickerDialogFragment()
                .setOnDateSetListener(new CalendarDatePickerDialogFragment.OnDateSetListener() {
                    @Override
                    public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear, int dayOfMonth) {
                        FROM_DATE.setDay(dayOfMonth);
                        FROM_DATE.setMonth(monthOfYear);
                        FROM_DATE.setYear(year);
                        Log.d("FROM_DATE", FROM_DATE.getDate());
                        setFromButtonText();
                        showFetchButton(true);
                    }
                })
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setPreselectedDate(FROM_DATE.getYear(), FROM_DATE.getMonth(), FROM_DATE.getDay())
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
                        TO_DATE.setDay(dayOfMonth);
                        TO_DATE.setMonth(monthOfYear);
                        TO_DATE.setYear(year);
                        Log.d("TO_DATE", TO_DATE.getDate());
                        setToButtonText();
                        showFetchButton(true);
                    }
                })
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setPreselectedDate(TO_DATE.getYear(), TO_DATE.getMonth(), TO_DATE.getDay())
                .setDateRange(null, null)
                .setThemeDark();
        cdp.show(getActivity().getSupportFragmentManager(), FRAG_TAG_DATE_PICKER);
    }
}