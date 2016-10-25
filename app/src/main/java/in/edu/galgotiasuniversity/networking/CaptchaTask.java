package in.edu.galgotiasuniversity.networking;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Map;

import in.edu.galgotiasuniversity.Constants;
import in.edu.galgotiasuniversity.LoginActivity;
import in.edu.galgotiasuniversity.R;
import in.edu.galgotiasuniversity.utils.Utils;

/**
 * Created on 25-01-2016.
 */
public class CaptchaTask extends AsyncTask<Void, Integer, Void> {

    private final String TAG = "CAPTCHA_TASK";
    private LoginActivity context;
    private Map<String, String> cookies, quickLoginCookies;
    private boolean isCaptchaRequired;
    private int progress;
    private boolean isShownAsDialog;
    private Connection.Response res;
    private Dialog dialog;
    private String captchaText;
    private AppCompatButton button;

    @SuppressWarnings("unchecked")
    public CaptchaTask(LoginActivity context, boolean isShownAsDialog, boolean isCaptchaRequired) {
        this.context = context;
        this.isShownAsDialog = isShownAsDialog;
        this.isCaptchaRequired = isCaptchaRequired;
        quickLoginCookies = (Map<String, String>) readObjectFromMemory("cookies");
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
//        context.setRequestedOrientation(context.getResources().getConfiguration().orientation);
        progress = 1;
        context.loginButton.setProgress(progress);
        context.loginButton.setEnabled(false);
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_captcha);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                context.loginButton.setProgress(0);
                context.loginButton.setEnabled(true);
            }
        });
        button = (AppCompatButton) dialog.findViewById(R.id.captchaButton);
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
//        super.onProgressUpdate(values);
        this.progress = progress[0];
        if (context != null && !context.isFinishing() && this.progress < 0) {
            context.loginButton.setProgress(this.progress);
            context.loginButton.setEnabled(true);
            showToast("Captcha could not be loaded!\nPlease try again", Toast.LENGTH_LONG);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    context.loginButton.setProgress(0);
                }
            }, 1500);
        }
    }

    private void showToast(String msg, int duration) {
        Toast toast = Toast.makeText(context, msg, duration);
        Utils.setFontAllView((ViewGroup) toast.getView());
        toast.show();
    }

    @Override
    protected Void doInBackground(Void... params) {

        if (isShownAsDialog) {
            try {
                Log.d(TAG, quickLoginCookies.toString());
                if (quickLoginCookies != null) {
                    Connection.Response res = Jsoup
                            .connect(Constants.HOME_URL)
                            .userAgent(Constants.USER_AGENT)
                            .referrer(Constants.REFERRER)
                            .followRedirects(false)
                            .header(Constants.HEADER1[0], Constants.HEADER1[1])
                            .header(Constants.HEADER2[0], Constants.HEADER2[1])
                            .header(Constants.HEADER3[0], Constants.HEADER3[1])
                            .header(Constants.HEADER4[0], Constants.HEADER4[1])
                            .cookies(quickLoginCookies)
                            .method(Connection.Method.GET)
                            .timeout(Constants.TIMEOUT)
                            .execute();
                    Document document = res.parse();
                    Log.d(TAG, document.title());
                    if (document.title().equals("SIM")) {
                        cookies = quickLoginCookies;
                        publishProgress(101);
                        return null;
                    }
                }
            } catch (IOException | NullPointerException e) {
                Log.d(TAG, e.getMessage());
            }
        }

        if (isCaptchaRequired) {
            try {
                res = Jsoup
                        .connect(Constants.CAPTCHA_URL)
                        .userAgent(Constants.USER_AGENT)
                        .referrer(Constants.REFERRER)
                        .header(Constants.HEADER1[0], Constants.HEADER1[1])
                        .header(Constants.HEADER2[0], Constants.HEADER2[1])
                        .header(Constants.HEADER3[0], Constants.HEADER3[1])
                        .header(Constants.HEADER4[0], Constants.HEADER4[1])
                        .method(Connection.Method.GET)
                        .timeout(Constants.TIMEOUT)
                        .ignoreContentType(true)
                        .execute();
                cookies = res.cookies();
                publishProgress(100);
            } catch (IOException e) {
                Log.d(TAG, e.getMessage());
                publishProgress(-1);
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (progress == 101) {
            new LoginTask(context, isShownAsDialog, captchaText, cookies, true).execute();
        } else if (progress == 100) {
            byte[] bitmap_data = res.bodyAsBytes();
            Bitmap bitmap = BitmapFactory.decodeByteArray(bitmap_data, 0, bitmap_data.length);
            ImageView cap = (ImageView) dialog.findViewById(R.id.captchaImage);
            cap.setImageBitmap(bitmap);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onCaptchaButtonClick();
                }
            });
            if (dialog != null) dialog.show();
        } else if (!isCaptchaRequired) onCaptchaButtonClick();
//        context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
    }

    private void onCaptchaButtonClick() {
        if (dialog != null && dialog.isShowing()) {
            final EditText captchaTextView = (EditText) dialog.findViewById(R.id.captchaText);
            TextInputLayout captchaTextLayout = (TextInputLayout) dialog.findViewById(R.id.captchaTextLayout);
            captchaText = captchaTextView.getText().toString().trim();
            captchaTextView.setText(captchaText);
            captchaTextLayout.setErrorEnabled(false);
            if (captchaText.length() == 0) {
                captchaTextLayout.setError("Required!");
                return;
            } else if (captchaText.length() < 3) {
                captchaTextLayout.setError("At least 3 characters required!");
                return;
            }
            dialog.dismiss();
        }
        new LoginTask(context, isShownAsDialog, captchaText, cookies, false).execute();
    }

    private Object readObjectFromMemory(String filename) {
        Object defaultObject;
        FileInputStream fis;
        try {
            fis = context.openFileInput(filename);
            ObjectInputStream is = new ObjectInputStream(fis);
            defaultObject = is.readObject();
            is.close();
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
            return null;
        }
        return defaultObject;
    }
}