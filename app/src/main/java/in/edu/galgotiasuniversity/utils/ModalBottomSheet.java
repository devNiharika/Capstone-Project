package in.edu.galgotiasuniversity.utils;

import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import in.edu.galgotiasuniversity.R;

/**
 * Created by Rohan Garg on 04-04-2016.
 */
public class ModalBottomSheet
        extends BottomSheetDialogFragment {

    static BottomSheetDialogFragment newInstance() {
        return new BottomSheetDialogFragment();
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(
                R.layout.bottom_sheet_modal, container, false);
        Utils.setFontAllView((ViewGroup) v);
        return v;
    }
}