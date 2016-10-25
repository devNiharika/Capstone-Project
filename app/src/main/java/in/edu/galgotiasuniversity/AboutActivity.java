package in.edu.galgotiasuniversity;

import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup;

import in.edu.galgotiasuniversity.utils.Utils;

/**
 * Created by Rohan Garg on 01-02-2016.
 */
public class AboutActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Utils.setFontAllView((ViewGroup) findViewById(R.id.root));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.gc();
    }
}