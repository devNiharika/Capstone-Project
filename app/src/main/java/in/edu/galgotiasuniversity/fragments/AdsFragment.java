package in.edu.galgotiasuniversity.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import in.edu.galgotiasuniversity.R;

/**
 * Created by Rohan Garg on 02-02-2016.
 */
public class AdsFragment extends Fragment {

    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (container == null) return null;
        view = inflater.inflate(R.layout.fragment_ads, container, false);
        ButterKnife.bind(this, view);

        return view;
    }


}
