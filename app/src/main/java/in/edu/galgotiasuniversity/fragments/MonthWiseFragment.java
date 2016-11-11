package in.edu.galgotiasuniversity.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;
import in.edu.galgotiasuniversity.Constants;
import in.edu.galgotiasuniversity.MainActivity;
import in.edu.galgotiasuniversity.R;
import in.edu.galgotiasuniversity.adapters.MonthWiseAdapter;
import in.edu.galgotiasuniversity.data.Record;
import in.edu.galgotiasuniversity.interfaces.OnError;
import in.edu.galgotiasuniversity.interfaces.OnTaskCompleted;
import in.edu.galgotiasuniversity.models.Date;
import in.edu.galgotiasuniversity.models.Month;
import in.edu.galgotiasuniversity.networking.AttendanceTask;
import in.edu.galgotiasuniversity.utils.NetworkStatus;
import in.edu.galgotiasuniversity.utils.Utils;

//import in.edu.galgotiasuniversity.networking.MonthlyTask;

/**
 * Created on 25-01-2016.
 */
public class MonthWiseFragment extends Fragment {

    View view;
    RecyclerView recyclerView;
    MonthWiseAdapter monthWiseAdapter;
    List<Month> months;
    Date FROM_DATE, TO_DATE;
    DateFormat df;
    SharedPreferences sp;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (container == null) return null;
        view = inflater.inflate(R.layout.fragment_month_wise, container, false);
        ButterKnife.bind(this, view);

        recyclerView = ButterKnife.findById(view, R.id.monthWise_content_list);
        df = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);

        FROM_DATE = new Date();
        TO_DATE = new Date();
        months = new ArrayList<>();

        sp = PreferenceManager.getDefaultSharedPreferences(getActivity());

        try {
            if (sp.getString("FROM_DATE", "").equals(Constants.SEM_START_DATE)) {
                FROM_DATE.setDate(sp.getString("TO_DATE", ""), df);
            } else {
                // Init
                FROM_DATE.setDate(Constants.SEM_START_DATE, df);
                showToast("First sync may take a few minutes. Please be patient.", Toast.LENGTH_LONG);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (savedInstanceState != null) {
            FROM_DATE = savedInstanceState.getParcelable("FROM_DATE");
            TO_DATE = savedInstanceState.getParcelable("TO_DATE");
            months = Record.getMonths();
            setupRecyclerView(recyclerView);
        } else {
            setupRecyclerView(recyclerView);
            if (!MainActivity.isMonthWiseRefreshed) {
                new AttendanceTask(getActivity(), new OnTaskCompleted() {
                    @Override
                    public void onTaskCompleted() {
                        taskCompleted();
                    }
                }, new OnError() {
                    @Override
                    public void onError() {
                        onErrorReceived();
                    }
                }, FROM_DATE.getDate(), TO_DATE.getDate()).execute();
            } else display();
        }

        Utils.setFontAllView((ViewGroup) view);
        return view;
    }

    public void taskCompleted() {
        MainActivity.isMonthWiseRefreshed = true;
        SharedPreferences.Editor editor = sp.edit();
        if (!sp.getString("FROM_DATE", "").equals(Constants.SEM_START_DATE))
            editor.putString("FROM_DATE", FROM_DATE.getDate());
        editor.putString("TO_DATE", TO_DATE.getDate());
        editor.apply();
        display();
    }

    void display() {
        months = Record.getMonths();
        monthWiseAdapter = new MonthWiseAdapter(this.getContext(), months);
        recyclerView.setAdapter(monthWiseAdapter);
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == 0) {
//            getActivity().getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.fragment_container, new SubjectWiseFragment()).commit();
//        }
//    }

    public void onErrorReceived() {
//        if (NetworkStatus.getInstance(getActivity()).isOnline()) {
//            if (!MainActivity.isCookieRefreshed)
//                startActivity(new Intent(getActivity(), LoginActivity.class));
//                startActivityForResult(new Intent(getActivity(), LoginActivity.class), 0);
//            showToast("Reconnecting to the server", Toast.LENGTH_SHORT);
//        }
        if (new NetworkStatus(getActivity()).isOnline())
            showToast("Aw, Snap! Please try again", Toast.LENGTH_SHORT);
        else
            showToast("Offline!", Toast.LENGTH_SHORT);
        display();
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
        recyclerView.setHasFixedSize(true);
        monthWiseAdapter = new MonthWiseAdapter(this.getContext(), months);
        recyclerView.setAdapter(monthWiseAdapter);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("FROM_DATE", FROM_DATE);
        outState.putParcelable("TO_DATE", TO_DATE);
    }
}