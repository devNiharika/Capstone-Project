package in.edu.galgotiasuniversity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dev.rg.ProgressButton.iml.ActionProcessButton;
import dev.rg.VersionManager.WVersionManager;
import in.edu.galgotiasuniversity.networking.CaptchaTask;
import in.edu.galgotiasuniversity.networking.LoginTask;
import in.edu.galgotiasuniversity.utils.MyApp;
import in.edu.galgotiasuniversity.utils.NetworkStatus;
import in.edu.galgotiasuniversity.utils.Utils;

/**
 * Created on 25-01-2016.
 */
public class LoginActivity extends Activity {

    @BindView(R.id.loginID)
    public TextInputEditText loginID;
    @BindView(R.id.password)
    public TextInputEditText password;
    @BindView(R.id.rememberMe)
    public AppCompatCheckBox rememberMe;
    @BindView(R.id.loginButton)
    public ActionProcessButton loginButton;
    @BindView(R.id.loginIDLayout)
    TextInputLayout loginIDLayout;
    @BindView(R.id.passwordLayout)
    TextInputLayout passwordLayout;
    @BindView(R.id.loginHeadline)
    TextView loginHeadline;
    boolean isShownAsDialog;
    CaptchaTask captchaTask;
    LoginTask loginTask;

    @Override
    public void onBackPressed() {
        if (loginTask != null && loginTask.getStatus() == AsyncTask.Status.RUNNING)
            showToast(getString(R.string.please_wait), Toast.LENGTH_SHORT);
        else super.onBackPressed();
//        if (captchaTask != null)
//            captchaTask.cancel(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        ButterKnife.unbind(this);
        System.gc();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        setupFonts();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (sharedPreferences.getBoolean("rememberMe", false)) {
            loginID.setText(sharedPreferences.getString("loginID", ""));
            password.setText(sharedPreferences.getString("password", ""));
            showAsDialog();
            validate();
        } else {
            checkUpdate();
        }
    }

    private void checkUpdate() {
        WVersionManager versionManager = new WVersionManager(this);
        versionManager.setUpdateNowLabel(getString(R.string.update_now));
        versionManager.setTitle(getString(R.string.univ_name));
        versionManager.setRemindMeLaterLabel(getString(R.string.remind_me_later));
        versionManager.setIgnoreThisVersionLabel("");
        versionManager.setReminderTimer(1440);
        // Update content url
        versionManager.setVersionContentUrl(Constants.UPDATES_URL);
        versionManager.checkVersion();
    }

    void showAsDialog() {
        loginHeadline.setText(getString(R.string.please_wait));
        loginIDLayout.setVisibility(View.GONE);
        passwordLayout.setVisibility(View.GONE);
        rememberMe.setVisibility(View.GONE);
        loginButton.setNormalText(getString(R.string.login_button_normal));
        loginButton.setLoadingText(getString(R.string.login_button_loading));
        loginButton.setCompleteText(getString(R.string.login_button_complete));
        isShownAsDialog = true;
    }

    void setupFonts() {
        Utils.setFontAllView((ViewGroup) findViewById(R.id.root));
        Typeface regularFont = Typeface.createFromAsset(MyApp.getContext().getAssets(),
                Utils.REGULAR_FONT_PATH);
        loginIDLayout.setTypeface(regularFont);
        passwordLayout.setTypeface(regularFont);
    }

    void showToast(String msg, int duration) {
        Toast toast = Toast.makeText(getApplicationContext(), msg, duration);
        Utils.setFontAllView((ViewGroup) toast.getView());
        toast.show();
    }

    @OnClick(R.id.loginButton)
    void validate() {
        if (!new NetworkStatus(LoginActivity.this).isOnline()) {
            showToast(getString(R.string.no_internet), Toast.LENGTH_SHORT);
            loginHeadline.setText(getString(R.string.offline));
            return;
        } else if (!isShownAsDialog) {
            String id = loginID.getText().toString().trim();
            loginID.setText(id);
            String pwd = password.getText().toString();
            loginIDLayout.setErrorEnabled(false);
            passwordLayout.setErrorEnabled(false);
            if (id.length() == 0) {
                loginID.requestFocus();
                loginID.setError(getString(R.string.required));
                return;
            } else if (id.length() < 6) {
                loginID.requestFocus();
                loginIDLayout.setError(getString(R.string.login_id_min_required));
                return;
            } else if (pwd.length() == 0) {
                password.requestFocus();
                password.setError(getString(R.string.required));
                return;
            } else if (pwd.length() < 3) {
                password.requestFocus();
                passwordLayout.setError(getString(R.string.password_min_required));
                return;
            }
        } else
            loginHeadline.setText(getString(R.string.please_wait));
//        loginTask = new LoginTask(this, isShownAsDialog, null, null);
//        loginTask.execute();
        captchaTask = new CaptchaTask(this, isShownAsDialog, Constants.isCaptchaRequired);
        captchaTask.execute();
    }
}