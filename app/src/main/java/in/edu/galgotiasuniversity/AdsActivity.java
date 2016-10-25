package in.edu.galgotiasuniversity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by Rohan Garg on 06-03-2016.
 */
public class AdsActivity extends AppCompatActivity {

//       implements AdvancedWebView.Listener {

    boolean isAdLoaded;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // remove title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        setContentView(R.layout.activity_ads_);
//        mWebView = (AdvancedWebView) findViewById(R.id.exitAd);
//        assert mWebView!=null;
//        mWebView.setListener(this, this);
//        mWebView.loadUrl("http://www.galgotias.ga/exitAds.html");
    }
}
