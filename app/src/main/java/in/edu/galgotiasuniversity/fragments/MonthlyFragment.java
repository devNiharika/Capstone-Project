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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.ButterKnife;
import in.edu.galgotiasuniversity.MainActivity;
import in.edu.galgotiasuniversity.R;
import in.edu.galgotiasuniversity.adapters.SubjectWiseAdapter;
import in.edu.galgotiasuniversity.utils.NetworkStatus;
import in.edu.galgotiasuniversity.utils.Utils;

//import in.edu.galgotiasuniversity.networking.MonthlyTask;

/**
 * Created on 25-01-2016.
 */
public class MonthlyFragment extends Fragment {

    View view;
    ArrayList<String> titles, contents1, contents2, contents3;
    LinearLayoutManager layoutManager;
    SharedPreferences sp;
    View recyclerView;
    SubjectWiseAdapter subjectWiseAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (container == null) return null;
        view = inflater.inflate(R.layout.fragment_subject_wise, container, false);
        ButterKnife.bind(this, view);
        titles = new ArrayList<>();
        contents1 = new ArrayList<>();
        contents2 = new ArrayList<>();
        contents3 = new ArrayList<>();
        recyclerView = ButterKnife.findById(view, R.id.subjectWise_content_list);
        assert recyclerView != null;

        sp = PreferenceManager.getDefaultSharedPreferences(getActivity());

        if (savedInstanceState != null) {
            titles = savedInstanceState.getStringArrayList("titles");
            contents1 = savedInstanceState.getStringArrayList("contents1");
            contents2 = savedInstanceState.getStringArrayList("contents2");
            contents3 = savedInstanceState.getStringArrayList("contents3");
            setupRecyclerView((RecyclerView) recyclerView);
        } else {
//            setupRecyclerView((RecyclerView) recyclerView);
//            if (!MainActivity.isMonthlyRefreshed) {
//                new MonthlyTask((MainActivity) this.getContext(), new OnTaskCompleted() {
//                    @Override
//                    public void onTaskCompleted() {
//                        taskCompleted();
//                    }
//                }, new OnError() {
//                    @Override
//                    public void onError() {
//                        onErrorReceived();
//                    }
//                }).execute();
//            } else display();
        }

        Utils.setFontAllView((ViewGroup) view);
        return view;
    }

    public void taskCompleted() {
        MainActivity.isMonthlyRefreshed = true;
        display();
    }

    void display() {
        try {
            JSONObject list = new JSONObject(sp.getString("monthlyList", ""));
            for (int i = 0; i < list.length(); i++) {
                JSONArray data = list.getJSONArray(Integer.toString(i));
                titles.add(data.getString(2));
//                contents1.add("Present: " + data.getString(1));
//                contents2.add("Absent: " + data.getString(2));
                contents1.add(data.getString(1));
                contents2.add(data.getString(0));
                contents3.add(data.getString(3));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        subjectWiseAdapter.notifyDataSetChanged();
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
            showToast("Offline mode", Toast.LENGTH_SHORT);
        if (!(sp.getBoolean("isMonthlyListLoaded", false))) {
            titles.add("Aw, Snap!");
            contents1.add("");
            contents2.add("Error connecting to the server");
            contents3.add("");
            subjectWiseAdapter.notifyDataSetChanged();
            return;
        }
        display();
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
//        subjectWiseAdapter = new SubjectWiseAdapter(this.getContext(), titles, contents1, contents2, contents3);
        recyclerView.setAdapter(subjectWiseAdapter);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList("titles", titles);
        outState.putStringArrayList("contents1", contents1);
        outState.putStringArrayList("contents2", contents2);
        outState.putStringArrayList("contents3", contents3);
    }
}