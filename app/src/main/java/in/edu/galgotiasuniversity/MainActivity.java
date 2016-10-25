package in.edu.galgotiasuniversity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindString;
import butterknife.ButterKnife;
import dev.rg.VersionManager.WVersionManager;
import in.edu.galgotiasuniversity.fragments.DayByDayFragment;
import in.edu.galgotiasuniversity.fragments.LibraryFragment;
import in.edu.galgotiasuniversity.fragments.MainFragment;
import in.edu.galgotiasuniversity.fragments.MonthlyFragment;
import in.edu.galgotiasuniversity.fragments.ProfileFragment;
import in.edu.galgotiasuniversity.fragments.SubjectWiseFragment;
import in.edu.galgotiasuniversity.interfaces.OnError;
import in.edu.galgotiasuniversity.interfaces.OnTaskCompleted;
import in.edu.galgotiasuniversity.networking.LibraryTask;
import in.edu.galgotiasuniversity.networking.MonthlyTask;
import in.edu.galgotiasuniversity.utils.AppStatus;
import in.edu.galgotiasuniversity.utils.CustomTypefaceSpan;
import in.edu.galgotiasuniversity.utils.Utils;
import im.delight.android.webview.AdvancedWebView;

/**
 * Created by Rohan Garg
 */

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static boolean isCookieRefreshed;
    public static boolean isSubjectWiseRefreshed;
    public static boolean isMonthlyRefreshed;
    public static boolean isLibraryRefreshed;
    SharedPreferences sp;
    @BindString(R.string.shareHeader)
    String shareHeader;
    @BindString(R.string.shareSubject)
    String shareSubject;
    @BindString(R.string.shareText)
    String shareText;
    Fragment fragment;
    boolean doubleBackToExitPressedOnce;
    AdvancedWebView webView;
    private Activity currentActivity;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        isCookieRefreshed = false;
        isSubjectWiseRefreshed = false;
        isMonthlyRefreshed = false;
        isLibraryRefreshed = false;
        System.gc();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        currentActivity = this;
        ButterKnife.bind(currentActivity);
        sp = PreferenceManager.getDefaultSharedPreferences(currentActivity);
        checkUpdate();
        showChangelog(true);

        if (savedInstanceState != null) {
            switchContent(getSupportFragmentManager().getFragment(savedInstanceState, "fragment"));
        } else {
            switchContent(new MainFragment());
            new MonthlyTask(this, new OnTaskCompleted() {
                @Override
                public void onTaskCompleted() {
                    isMonthlyRefreshed = true;
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.detach(fragment);
                    ft.attach(fragment);
                    ft.commit();
                }
            }, new OnError() {
                @Override
                public void onError() {
                }
            }).execute();
            syncLibrary(null);
        }

        setupDrawerLayout();
        setupNavigationView();

        showBannerAds();
    }

    void setupDrawerLayout() {
        Toolbar toolbar = ButterKnife.findById(currentActivity, R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                currentActivity, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        if (drawer != null)
            drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    void setupNavigationView() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        assert navigationView != null;
        navigationView.setNavigationItemSelectedListener(this);
        ViewGroup navHeader = (ViewGroup) navigationView.getHeaderView(0);
        Utils.setFontAllView(navHeader);
        setFonts(navigationView.getMenu());

        TextView nav_user_id = ButterKnife.findById(navHeader, R.id.nav_header_user_id);
        nav_user_id.setText(sp.getString("loginID", ""));

        if (sp.getBoolean("isProfileLoaded", false)) {
            try {
                JSONObject profile = new JSONObject(sp.getString("profile", ""));
                JSONArray data = profile.getJSONArray("0");
                TextView nav_name = ButterKnife.findById(navHeader, R.id.nav_header_name);
                nav_name.setText(data.getString(1));
            } catch (JSONException e) {
                Log.d("PROFILE", e.getMessage());
            }
        }
    }

    void checkUpdate() {
        WVersionManager versionManager = new WVersionManager(currentActivity);
        versionManager.setUpdateNowLabel("Update");
        versionManager.setRemindMeLaterLabel("Later");
        versionManager.setIgnoreThisVersionLabel("");
        versionManager.setReminderTimer(60);
        versionManager.setVersionContentUrl("http://galgotias.ga/Updates"); // your update content url, see the response format below
        versionManager.checkVersion();
    }

    void showChangelog(boolean checkVersion) {
        final int currentVersionCode = new WVersionManager(currentActivity).getCurrentVersionCode();
        if (checkVersion && currentVersionCode <= getLastVersionCode()) return;
        AlertDialog.Builder builder = new AlertDialog.Builder(currentActivity);
        // Set the dialog title
        builder.setTitle("Change Log:");
        // Set the dialog view
        LayoutInflater inflater = currentActivity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.changelog, null);
        builder.setView(dialogView);
        builder.setPositiveButton("Cool", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                updateVersionInPreferences(currentVersionCode);
            }
        });
        builder.setCancelable(false);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    protected void updateVersionInPreferences(int mCurrentVersionCode) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("last_version_code", mCurrentVersionCode);

        // TODO: Update preferences from a background thread
        editor.apply();
    }

    protected int getLastVersionCode() {
        return sp.getInt("last_version_code", -1);
    }

    void showBannerAds() {
        webView = ButterKnife.findById(currentActivity, R.id.bannerAd);
        webView.setListener(currentActivity, new AdvancedWebView.Listener() {
            @Override
            public void onPageStarted(String s, Bitmap bitmap) {
                webView.setVisibility(View.GONE);
            }

            @Override
            public void onPageFinished(String s) {
                webView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageError(int i, String s, String s1) {
                webView.setVisibility(View.GONE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (AppStatus.getInstance(currentActivity).isOnline())
                            webView.loadUrl("http://galgotias.ga/BannerAds.html");
                    }
                }, 5000);
            }

            @Override
            public void onDownloadRequested(String url, String suggestedFilename, String mimeType, long contentLength, String contentDisposition, String userAgent) {

            }

            @Override
            public void onExternalPageRequest(String s) {
                if (s.equals("http://galgotias.ga/BannerAds.html"))
                    webView.reload();
                else {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(s));
                    startActivity(browserIntent);
                }
            }
        });
        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);
        webView.addPermittedHostname("www.galgotias.ga");
        webView.addPermittedHostname("fonts.googleapis.com");
        webView.loadUrl("http://galgotias.ga/BannerAds.html");
    }

    public void syncLibrary(View view) {
        new LibraryTask(this, new OnTaskCompleted() {
            @Override
            public void onTaskCompleted() {
                isLibraryRefreshed = true;
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.detach(fragment);
                ft.attach(fragment);
                ft.commit();
            }
        }, new OnError() {
            @Override
            public void onError() {
                showToast("Sync failed", Toast.LENGTH_SHORT);
            }
        }).execute();
    }

    private void like() {
        Uri uri = Uri.parse("market://details?id=" + currentActivity.getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market back-stack, After pressing back button,
        // to taken back to our application, we need to set following flags to intent.
        goToMarket.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + currentActivity.getPackageName())));
        }
    }

    private void share() {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_SUBJECT, shareSubject);
        i.putExtra(Intent.EXTRA_TEXT, shareText);
        startActivity(Intent.createChooser(i, shareHeader));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = ButterKnife.findById(currentActivity, R.id.drawer_layout);
        assert drawer != null;
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            finish();
        }
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Snackbar.make(ButterKnife.findById(currentActivity, R.id.root), "Press again to exit", Snackbar.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }


    private void setFonts(final Menu menu) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                Utils.setFontAllView((ViewGroup) findViewById(R.id.root));
                for (int i = 0; i < menu.size(); i++) {
                    MenuItem mi = menu.getItem(i);
                    //For applying a font to subMenu
                    SubMenu subMenu = mi.getSubMenu();
                    if (subMenu != null && subMenu.size() > 0) {
                        for (int j = 0; j < subMenu.size(); j++) {
                            MenuItem subMenuItem = subMenu.getItem(j);
                            applyFontToMenuItem(subMenuItem);
                        }
                    }
                    applyFontToMenuItem(mi);
                }
            }
        });
    }

    void showToast(String msg, int duration) {
        Toast toast = Toast.makeText(getApplicationContext(), msg, duration);
        Utils.setFontAllView((ViewGroup) toast.getView());
        toast.show();
    }

    private void applyFontToMenuItem(MenuItem mi) {
        Typeface font = Typeface.createFromAsset(getAssets(), "Montserrat-Regular.ttf");
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("", font), 0, mNewTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        setFonts(menu);
        return true;
    }

    public void logout() {
        sp.edit().clear().apply();
        finish();
        startActivity(new Intent(currentActivity, LoginActivity.class));
        overridePendingTransition(R.anim.push_left_in, R.anim.push_right_out);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.about) {
            startActivity(new Intent(currentActivity, AboutActivity.class));
        } else if (id == R.id.like) {
            like();
        } else if (id == R.id.share) {
            share();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        //MenuItem dbd=ButterKnife.findById()

        if (id == R.id.logout) {
            logout();
        } else if (id == R.id.home) {
            // if (!item.isChecked()) {
            switchContent(new MainFragment());
            item.setChecked(true);
            //}
        } else if (id == R.id.dayByDay) {
            //if (!item.isChecked()) {
            switchContent(new DayByDayFragment());
            item.setChecked(true);
            //}
        } else if (id == R.id.monthly) {
            //if (!item.isChecked()) {
            switchContent(new MonthlyFragment());
            item.setChecked(true);
            //}
        }
        else if (id == R.id.subjectWise) {
            //if (!item.isChecked()) {
            switchContent(new SubjectWiseFragment());
            item.setChecked(true);
            //}
        }
        else if (id == R.id.library) {
            //if (!item.isChecked()) {
            switchContent(new LibraryFragment());
            item.setChecked(true);
            //}
        } else if (id == R.id.profile) {
            //if (!item.isChecked()) {
            switchContent(new ProfileFragment());
            item.setChecked(true);
            //}
        }
//        else if (id == R.id.jobs) {
//            //if (!item.isChecked()) {
//            switchContent(new JobsFragment());
//            item.setChecked(true);
//            //}
//        }
        else if (id == R.id.share) {
            share();
        } else if (id == R.id.like) {
            like();
        } else if (id == R.id.whats_new) {
            showChangelog(false);
        } else if (id == R.id.about) {
            startActivity(new Intent(currentActivity, AboutActivity.class));
        }
        DrawerLayout drawer = ButterKnife.findById(currentActivity, R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void switchContent(Fragment fragment) {
        this.fragment = fragment;
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment).commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getSupportFragmentManager().putFragment(outState, "fragment", fragment);
    }
}