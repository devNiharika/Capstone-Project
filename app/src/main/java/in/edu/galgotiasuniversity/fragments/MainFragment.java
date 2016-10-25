package in.edu.galgotiasuniversity.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import dev.rg.WaveProgress.WaveLoadingView;
import in.edu.galgotiasuniversity.R;
import in.edu.galgotiasuniversity.utils.ColorUtils;
import in.edu.galgotiasuniversity.utils.Utils;

/**
 * Created by Rohan Garg on 02-02-2016.
 */
public class MainFragment extends Fragment {

    @Bind(R.id.dataContainer)
    View dataContainer;
    @Bind(R.id.waveLoadingView)
    WaveLoadingView waveLoadingView;
    @Bind(R.id.waveProgressCenter)
    TextView waveProgressCenter;
    @Bind(R.id.topMessageText1)
    TextView topMessageText1;

    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (container == null) return null;
        view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        if (sp.getBoolean("isMonthlyListLoaded", false)) {
            try {
                JSONObject list = new JSONObject(sp.getString("monthlyList", ""));
                JSONArray data = list.getJSONArray(Integer.toString(list.length() - 1));
//                int percentage = Float.valueOf(data.getString(3)).intValue();
                String color = data.getString(3);

//                if (percentage >= 75) {
                if (color.equals("DarkGreen") || color.equals("Green") || color.equals("Lime")) {
                    topMessageText1.setText("TIP: Good going!");
                } else {
                    topMessageText1.setText("TIP: Attendance below 75%!");
                    topMessageText1.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.tipRed));
                }

                waveProgressCenter.setText("Attendance: " + ColorUtils.getAttendanceRange(color));
                waveLoadingView.setProgressValue(ColorUtils.getAttendanceValue(color));
                dataContainer.setVisibility(View.VISIBLE);
                TextView data1 = ButterKnife.findById(dataContainer, R.id.data1);
                data1.setText(data.getString(2));
                TextView data2 = ButterKnife.findById(dataContainer, R.id.data2);
                data2.setText(data.getString(1));

                if (sp.getBoolean("isLibraryLoaded", false)) {
                    JSONObject library = new JSONObject(sp.getString("library", ""));
                    JSONArray libData = library.getJSONArray("0");
                    TextView data3 = ButterKnife.findById(dataContainer, R.id.data3);
                    data3.setText(libData.getString(3));
                }
            } catch (JSONException | NumberFormatException e) {
                e.printStackTrace();
            }
        }

        Utils.setFontAllView((ViewGroup) view);
        return view;
    }
}

