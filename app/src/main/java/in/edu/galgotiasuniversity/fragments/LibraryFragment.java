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
import in.edu.galgotiasuniversity.adapters.LibraryAdapter;
import in.edu.galgotiasuniversity.interfaces.OnError;
import in.edu.galgotiasuniversity.interfaces.OnTaskCompleted;
import in.edu.galgotiasuniversity.networking.LibraryTask;
import in.edu.galgotiasuniversity.utils.NetworkStatus;
import in.edu.galgotiasuniversity.utils.Utils;

public class LibraryFragment extends Fragment {

    View view;
    ArrayList<String> titles, contents1, contents2, contents3;
    LinearLayoutManager layoutManager;
    SharedPreferences sp;
    View recyclerView;
    LibraryAdapter libraryAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (container == null) return null;
        view = inflater.inflate(R.layout.fragment_library, container, false);
        ButterKnife.bind(this, view);
        titles = new ArrayList<>();
        contents1 = new ArrayList<>();
        contents2 = new ArrayList<>();
        contents3 = new ArrayList<>();
        recyclerView = ButterKnife.findById(view, R.id.library_content_list);
        assert recyclerView != null;

        sp = PreferenceManager.getDefaultSharedPreferences(getActivity());

        if (savedInstanceState != null) {
            titles = savedInstanceState.getStringArrayList("titles");
            contents1 = savedInstanceState.getStringArrayList("contents1");
            contents2 = savedInstanceState.getStringArrayList("contents2");
            contents3 = savedInstanceState.getStringArrayList("contents3");
            setupRecyclerView((RecyclerView) recyclerView);
        } else {
            setupRecyclerView((RecyclerView) recyclerView);
            if (!MainActivity.isLibraryRefreshed) {
                new LibraryTask(getActivity(), new OnTaskCompleted() {
                    @Override
                    public void onTaskCompleted() {
                        taskCompleted();
                    }
                }, new OnError() {
                    @Override
                    public void onError() {
                        onErrorReceived();
                    }
                }).execute();
            } else display();
        }

        Utils.setFontAllView((ViewGroup) view);
        return view;
    }

    public void taskCompleted() {
        MainActivity.isLibraryRefreshed = true;
        display();
    }

    void display() {
        try {
            JSONObject library = new JSONObject(sp.getString("library", ""));
            JSONArray data = library.getJSONArray("0");
            if (data.getString(0).equals("")) {
                titles.add(getString(R.string.no_data));
                contents1.add(getString(R.string.no_value));
                contents2.add(getString(R.string.no_value));
                contents3.add(getString(R.string.no_value));
            } else {
                titles.add(data.getString(0));
                contents1.add(data.getString(1));
                contents2.add(getString(R.string.fine) + ": " + data.getString(2));
                contents3.add(getString(R.string.balance) + ": " + data.getString(3));
            }
            for (int i = 1; i < library.length(); i++) {
                data = library.getJSONArray(Integer.toString(i));
                titles.add(data.getString(0));
                contents1.add(data.getString(1));
                contents2.add(data.getString(2));
                contents3.add(data.getString(3));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        libraryAdapter.notifyDataSetChanged();
    }

    public void onErrorReceived() {
//        if (NetworkStatus.getInstance(getActivity()).isOnline()) {
//            if (!MainActivity.isCookieRefreshed)
//                startActivity(new Intent(getActivity(), LoginActivity.class));
//            showToast("Reconnecting to the server", Toast.LENGTH_SHORT);
//        }
        if (new NetworkStatus(getActivity()).isOnline())
            showToast(getString(R.string.error_toast), Toast.LENGTH_SHORT);
        else
            showToast(getString(R.string.offline), Toast.LENGTH_SHORT);
        if (!(sp.getBoolean("isLibraryLoaded", false))) {
            titles.add(getString(R.string.error_title));
            contents1.add("");
            contents2.add(getString(R.string.error_connecting));
            contents3.add("");
            libraryAdapter.notifyDataSetChanged();
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
        libraryAdapter = new LibraryAdapter(this.getContext(), titles, contents1, contents2, contents3);
        recyclerView.setAdapter(libraryAdapter);
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
