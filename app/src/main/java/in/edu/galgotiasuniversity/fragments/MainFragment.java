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

import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;
import dev.rg.WaveProgress.WaveLoadingView;
import in.edu.galgotiasuniversity.Constants;
import in.edu.galgotiasuniversity.R;
import in.edu.galgotiasuniversity.data.Record;
import in.edu.galgotiasuniversity.models.Month;
import in.edu.galgotiasuniversity.utils.Utils;

/**
 * Created on 25-01-2016.
 */
public class MainFragment extends Fragment {

    SharedPreferences sp;

    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (container == null) return null;
        view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);

        sp = PreferenceManager.getDefaultSharedPreferences(getActivity());

        try {
            showData();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Utils.setFontAllView((ViewGroup) view);
        return view;
    }

    void showData() throws JSONException {
        if (sp.getString("FROM_DATE", "").equals(Constants.SEM_START_DATE)) {
            int present = 0, absent = 0;
            List<Month> months = Record.getMonths();
            for (Month month : months) {
                present += month.PRESENT;
                absent += month.ABSENT;
            }
            View subContainer1 = ButterKnife.findById(view, R.id.subContainer1);
            View subContainer2 = ButterKnife.findById(view, R.id.subContainer2);
            TextView a = ButterKnife.findById(subContainer1, R.id.data1);
            TextView p = ButterKnife.findById(subContainer2, R.id.data2);
            a.setText(String.valueOf(absent));
            p.setText(String.valueOf(present));
            subContainer1.setVisibility(View.VISIBLE);
            subContainer2.setVisibility(View.VISIBLE);
            float percentage = (100f * present) / (present + absent);
            WaveLoadingView waveLoadingView = ButterKnife.findById(view, R.id.waveLoadingView);
            TextView waveProgressCenter = ButterKnife.findById(view, R.id.waveProgressCenter);
            TextView topMessageText1 = ButterKnife.findById(view, R.id.topMessageText1);
            topMessageText1.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
            waveProgressCenter.setText(getString(R.string.attendance) + ": " + String.format(Locale.ENGLISH, "%.2f", percentage) + "%");
            waveLoadingView.setProgressValue((int) percentage);
            if ((int) percentage > 75) {
                topMessageText1.setText(getString(R.string.tip_good));
                topMessageText1.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.green));
            } else {
                topMessageText1.setText(getString(R.string.tip_alert));
                topMessageText1.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.tipRed));
            }
        }
        if (sp.getBoolean("isLibraryLoaded", false)) {
            JSONObject library = new JSONObject(sp.getString("library", ""));
            JSONArray libData = library.getJSONArray("0");
            View subContainer3 = ButterKnife.findById(view, R.id.subContainer3);
            TextView data3 = ButterKnife.findById(subContainer3, R.id.data3);
            if (libData.getString(0).equals(""))
                data3.setText("0");
            else
                data3.setText(libData.getString(3));
            subContainer3.setVisibility(View.VISIBLE);
        }
    }
}
