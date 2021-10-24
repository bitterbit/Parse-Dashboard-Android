package com.galtashma.parsedashboard.screens;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.galtashma.parsedashboard.ParseServerConfig;
import com.galtashma.parsedashboard.ParseServerConfigStorage;
import com.galtashma.parsedashboard.R;
import com.galtashma.parsedashboard.adapters.ParseAppsAdapter;
import com.galtashma.parsedashboard.Const;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.parse.Parse;
import com.vlonjatg.progressactivity.ProgressRelativeLayout;

import java.util.List;

import javax.xml.transform.TransformerFactoryConfigurationError;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;



public class AppsMenuParseActivity extends AppCompatActivity implements MaterialDialog.SingleButtonCallback, ParseAppsAdapter.ParseAppAdapterListener {

    private ParseServerConfigStorage storage;

    private ParseAppsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_apps_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });

        storage = new ParseServerConfigStorage(getApplicationContext());
        toggleMainScreen(isMainScreenEmpty());

        List<ParseServerConfig> list = storage.getServers();
        adapter = new ParseAppsAdapter(this, list);
        ListView listView = findViewById(R.id.list_view);
        listView.setAdapter(adapter);
        adapter.setListener(this);

//        Answers.getInstance().logContentView(new ContentViewEvent()
//                .putContentName("All Apps Activity")
//                .putContentType("Screen"));
//
//        Stargazer.with(this).init("138d14dbfbef4570bf340407aa5acc3d")
//                .setInstallDays(2)
//                .setLaunchTimes(4);

        // Show dialog after 40 seconds
//        final Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                Stargazer.with(AppsMenuParseActivity.this).showIfMeetsConditions();
//            }
//        }, 1000*40);
    }

    private boolean isMainScreenEmpty(){
        return storage.getServers().isEmpty();
    }

    private void toggleMainScreen(boolean isEmpty){
        ProgressRelativeLayout layout = (ProgressRelativeLayout) findViewById(R.id.stateful_layout);
        if(isEmpty){
            layout.showEmpty(R.drawable.ic_parse_24dp, getString(R.string.empty_state_apps_screen_short), getString(R.string.empty_state_apps_screen_long));
        } else {
            layout.showContent();
        }
    }

    private void showDialog(){
        new MaterialDialog.Builder(this)
                .title("Add Parse Server")
                .customView(R.layout.dialog_add_app, true)
                .positiveText("OK")
                .onPositive(this)
                .show();
    }

    @Override
    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
        ParseServerConfig serverConfig = getConfigFromDialog(dialog);
        storage.saveServer(serverConfig);
        adapter.add(serverConfig);
        adapter.notifyDataSetChanged();
        toggleMainScreen(isMainScreenEmpty());
//        Answers.getInstance().logCustom(new CustomEvent("Action")
//                .putCustomAttribute("type", "add new server config"));

    }

    private ParseServerConfig getConfigFromDialog(MaterialDialog dialog){
        View v = dialog.getCustomView();
        EditText appName = v.findViewById(R.id.inputAppName);
        EditText appId = v.findViewById(R.id.inputAppId);
        EditText masterKey = v.findViewById(R.id.inputAppMasterKey);
        EditText serverUrl = v.findViewById(R.id.inputServerUrl);
        return new ParseServerConfig(
                appName.getText().toString(),
                appId.getText().toString(),
                masterKey.getText().toString(),
                serverUrl.getText().toString());
    }

    @Override
    public void onClickOpen(ParseServerConfig config) {
        String error = checkForParseConfigError(config);
        if (error != null){
            Snackbar.make(findViewById(R.id.stateful_layout), error, Snackbar.LENGTH_LONG).show();
            return;
        }

        // Re init parse sdk so we can open a new parse app
        Parse.destroy();
        initParse(config.appId, config.serverUrl, config.masterKey);

        Intent i = new Intent(this, SingleAppParseActivity.class);
        i.putExtra(Const.BUNDLE_KEY_PARSE_APP_NAME, config.appName);
        this.startActivityForResult(i, 1);
    }

    // Validate parse server config. If No error returns null, otherwise returns error message.
    private String checkForParseConfigError(ParseServerConfig config){
        if (config.appId.equals("")) {
            return getString(R.string.error_app_id_missing);
        }
        if (config.serverUrl.equals("")) {
            return getString(R.string.error_server_url_missing);
        }
        if (!config.serverUrl.startsWith("http://") && !config.serverUrl.startsWith("https://")){
            return getString(R.string.error_server_url_malformed);
        }
        if (config.masterKey.equals("")) {
            return getString(R.string.error_master_key_missing);
        }
        return null;
    }

    @Override
    public void onClickEdit(final ParseServerConfig config) {
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title("Edit Parse Server")
                .customView(R.layout.dialog_add_app, true)
                .positiveText("OK")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        adapter.remove(config);
                        storage.deleteServer(config.appId);
                        ParseServerConfig newConfig = getConfigFromDialog(dialog);
                        adapter.add(newConfig);
                        storage.saveServer(newConfig);
                        adapter.notifyDataSetChanged();
                    }
                })
                .show();

        View v = dialog.getCustomView();
        ((EditText)v.findViewById(R.id.inputAppName)).setText(config.appName);
        ((EditText)v.findViewById(R.id.inputAppId)).setText(config.appId);
        ((EditText)v.findViewById(R.id.inputAppMasterKey)).setText(config.masterKey);
        ((EditText)v.findViewById(R.id.inputServerUrl)).setText(config.serverUrl);

//        Answers.getInstance().logCustom(new CustomEvent("Action")
//                .putCustomAttribute("type", "edit parse server config"));
    }

    @Override
    public void onClickDelete(ParseServerConfig config) {
        storage.deleteServer(config.appId);
        adapter.remove(config);
        adapter.notifyDataSetChanged();
        toggleMainScreen(isMainScreenEmpty());
//        Answers.getInstance().logCustom(new CustomEvent("Action")
//                .putCustomAttribute("type", "delete parse server config"));
    }

    private void initParse(String appId, String serverUrl, String masterKey){
        Log.i("ParseDashboard", "Starting client for " + serverUrl + " appId: " + appId);
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.networkInterceptors().add(httpLoggingInterceptor);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(appId)
                .server(serverUrl)
                .masterKey(masterKey)
                .clientBuilder(builder)
                .build());
    }
}
