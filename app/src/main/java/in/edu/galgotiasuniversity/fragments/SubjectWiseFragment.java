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
import in.edu.galgotiasuniversity.adapters.SubjectWiseAdapter;
import in.edu.galgotiasuniversity.data.Record;
import in.edu.galgotiasuniversity.interfaces.OnError;
import in.edu.galgotiasuniversity.interfaces.OnTaskCompleted;
import in.edu.galgotiasuniversity.models.Date;
import in.edu.galgotiasuniversity.models.Subject;
import in.edu.galgotiasuniversity.networking.AttendanceTask;
import in.edu.galgotiasuniversity.utils.NetworkStatus;
import in.edu.galgotiasuniversity.utils.Utils;

//import in.edu.galgotiasuniversity.networking.SubjectWiseTask;

/**
 * Created on 25-01-2016.
 */
public class SubjectWiseFragment extends Fragment {

    View view;
    RecyclerView recyclerView;
    SubjectWiseAdapter subjectWiseAdapter;
    List<Subject> subjects;
    Date FROM_DATE, TO_DATE;
    DateFormat df;
    SharedPreferences sp;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (container == null) return null;
        view = inflater.inflate(R.layout.fragment_subject_wise, container, false);
        ButterKnife.bind(this, view);
        recyclerView = ButterKnife.findById(view, R.id.subjectWise_content_list);
        df = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);

        FROM_DATE = new Date();
        TO_DATE = new Date();
        subjects = new ArrayList<>();

        sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        try {
            if (sp.getString("FROM_DATE", "").equals(Constants.SEM_START_DATE)) {
                FROM_DATE.setDate(sp.getString("TO_DATE", ""), df);
            } else {
                // Init
                FROM_DATE.setDate(Constants.SEM_START_DATE, df);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (savedInstanceState != null) {
            FROM_DATE = savedInstanceState.getParcelable("FROM_DATE");
            TO_DATE = savedInstanceState.getParcelable("TO_DATE");
            subjects = Record.getSubjects();
            setupRecyclerView(recyclerView);
        } else {
            setupRecyclerView(recyclerView);
            if (!MainActivity.isSubjectWiseRefreshed) {
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
        MainActivity.isSubjectWiseRefreshed = true;
        SharedPreferences.Editor editor = sp.edit();
        if (!sp.getString("FROM_DATE", "").equals(Constants.SEM_START_DATE))
            editor.putString("FROM_DATE", FROM_DATE.getDate());
        editor.putString("TO_DATE", TO_DATE.getDate());
        editor.apply();
        display();
    }

    void display() {
        subjects = Record.getSubjects();
        subjectWiseAdapter = new SubjectWiseAdapter(this.getContext(), subjects);
        recyclerView.setAdapter(subjectWiseAdapter);
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
        subjectWiseAdapter = new SubjectWiseAdapter(this.getContext(), subjects);
        recyclerView.setAdapter(subjectWiseAdapter);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("FROM_DATE", FROM_DATE);
        outState.putParcelable("TO_DATE", TO_DATE);
    }
}