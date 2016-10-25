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
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.ButterKnife;
import in.edu.galgotiasuniversity.MainActivity;
import in.edu.galgotiasuniversity.R;
import in.edu.galgotiasuniversity.adapters.ProfileAdapter;
import in.edu.galgotiasuniversity.interfaces.OnError;
import in.edu.galgotiasuniversity.interfaces.OnTaskCompleted;
import in.edu.galgotiasuniversity.networking.ProfileTask;
import in.edu.galgotiasuniversity.utils.NetworkStatus;
import in.edu.galgotiasuniversity.utils.Utils;

/**
 * Created on 25-01-2016.
 */
public class ProfileFragment extends Fragment {

    View view;
    ArrayList<String> titles, contents;
    LinearLayoutManager layoutManager;
    SharedPreferences sp;
    View recyclerView;
    ProfileAdapter profileAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (container == null) return null;
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, view);
        titles = new ArrayList<>();
        contents = new ArrayList<>();
        recyclerView = ButterKnife.findById(view, R.id.profile_content_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

        sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        if (!(sp.getBoolean("isProfileLoaded", false))) {
            new ProfileTask((MainActivity) this.getContext(), new OnTaskCompleted() {
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
        } else {
            taskCompleted();
        }
        Utils.setFontAllView((ViewGroup) view);
        return view;
    }

    public void taskCompleted() {
        try {
            JSONObject profile = new JSONObject(sp.getString("profile", ""));
            //Log.d("profile", profile.toString());
            titles.add("Admission Number");
            contents.add(sp.getString("loginID", ""));
            for (int i = 0; i < profile.length(); i++) {
                JSONArray data = profile.getJSONArray(Integer.toString(i));
                titles.add(data.getString(0));
                contents.add(data.getString(1));
            }
            Log.d("Titles", titles.toString());
            Log.d("contents", contents.toString());
            profileAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
            showToast("Offline mode", Toast.LENGTH_SHORT);
        titles.add("Aw, Snap!");
        contents.add("Error connecting to the server");
        profileAdapter.notifyDataSetChanged();
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
        profileAdapter = new ProfileAdapter(this.getContext(), titles, contents);
        recyclerView.setAdapter(profileAdapter);
    }

}
