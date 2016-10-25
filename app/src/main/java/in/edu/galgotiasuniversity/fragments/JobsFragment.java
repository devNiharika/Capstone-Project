package in.edu.galgotiasuniversity.fragments;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import in.edu.galgotiasuniversity.R;
import in.edu.galgotiasuniversity.utils.AppStatus;
import in.edu.galgotiasuniversity.utils.Utils;
import im.delight.android.webview.AdvancedWebView;

/**
 * Created by Rohan Garg on 17-04-2016.
 */
public class JobsFragment extends Fragment implements AdvancedWebView.Listener {

    View view;
    @Bind(R.id.jobsWebView)
    AdvancedWebView mWebView;
    ProgressDialog dialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (container == null) return null;
        view = inflater.inflate(R.layout.fragment_jobs, container, false);
        ButterKnife.bind(this, view);

        mWebView.setListener(getActivity(), this);
//        mWebView.setVerticalScrollBarEnabled(true);
//        mWebView.setHorizontalScrollBarEnabled(false);
        mWebView.loadUrl("http://m.monsterindia.com/");
        return view;
    }

    @Override
    public void onPageStarted(String url, Bitmap favicon) {
        dialog = ProgressDialog.show(getActivity(), "", "Loading...", true);
        dialog.setCancelable(true);
    }

    @Override
    public void onPageFinished(String url) {
        if (dialog != null) dialog.dismiss();
    }

    @Override
    public void onPageError(int errorCode, String description, String failingUrl) {
        mWebView.loadUrl("file:///android_asset/error.html");
        if (AppStatus.getInstance(getActivity()).isOnline())
            showToast("Aw, Snap! Please try again", Toast.LENGTH_SHORT);
        else
            showToast("Offline mode", Toast.LENGTH_SHORT);
        if (dialog != null) dialog.dismiss();
    }

    @Override
    public void onDownloadRequested(String url, String suggestedFilename, String mimeType, long contentLength, String contentDisposition, String userAgent) {

    }

    @Override
    public void onExternalPageRequest(String url) {

    }

    @OnClick(R.id.fab)
    void refresh() {
        if (mWebView != null) mWebView.loadUrl("http://m.monsterindia.com/");
    }

    void showToast(String msg, int duration) {
        Toast toast = Toast.makeText(this.getContext(), msg, duration);
        Utils.setFontAllView((ViewGroup) toast.getView());
        toast.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        mWebView.onResume();
    }

    @Override
    public void onPause() {
        mWebView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mWebView.onDestroy();
        super.onDestroy();
    }
}