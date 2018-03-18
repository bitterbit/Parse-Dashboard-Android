package com.galtashma.parsedashboard.screens;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.galtashma.parsedashboard.ParseServerConfig;
import com.galtashma.parsedashboard.ParseServerConfigStorage;
import com.galtashma.parsedashboard.R;
import com.galtashma.parsedashboard.adapters.ParseAppsAdapter;
import com.galtashma.parsedashboard.Const;
import com.parse.Parse;

import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;



public class AppsMenuParseActivity extends AppCompatActivity implements MaterialDialog.SingleButtonCallback, ParseAppsAdapter.ParseAppAdapterListener {

    private ParseServerConfigStorage storage;

    private LinearLayout emptyStateLayout;
    private ListView configuredServersView;
    private ParseAppsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        emptyStateLayout = (LinearLayout) findViewById(R.id.empty_state_layout);
        configuredServersView = (ListView) findViewById(R.id.configured_servers_list);
        storage = new ParseServerConfigStorage(getApplicationContext());
        toggleMainScreen(isMainScreenEmpty());

        List<ParseServerConfig> list = storage.getServers();
        adapter = new ParseAppsAdapter(this, list);
        configuredServersView.setAdapter(adapter);
        adapter.setListener(this);
    }

    private boolean isMainScreenEmpty(){
        return storage.getServers().isEmpty();
    }

    private void toggleMainScreen(boolean isEmpty){
        if(isEmpty){
            configuredServersView.setVisibility(View.GONE);
            emptyStateLayout.setVisibility(View.VISIBLE);
        } else {
            configuredServersView.setVisibility(View.VISIBLE);
            emptyStateLayout.setVisibility(View.GONE);
        }
    }

    private void showDialog(){
        new MaterialDialog.Builder(this)
                .title("Add Parse Server")
                .customView(R.layout.add_app_dialog, true)
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
        // Re init parse sdk so we can open a new parse app
        Parse.destroy();
        initParse(config.appId, config.serverUrl, config.masterKey);

        Intent i = new Intent(this, SingleAppParseActivity.class);
        i.putExtra(Const.BUNDLE_KEY_PARSE_APP_NAME, config.appName);
        this.startActivityForResult(i, 1);
    }

    @Override
    public void onClickEdit(final ParseServerConfig config) {

        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title("Edit Parse Server")
                .customView(R.layout.add_app_dialog, true)
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
    }

    @Override
    public void onClickDelete(ParseServerConfig config) {
        storage.deleteServer(config.appId);
        adapter.remove(config);
        adapter.notifyDataSetChanged();
        toggleMainScreen(isMainScreenEmpty());
    }

    private void initParse(String appId, String serverUrl, String masterKey){
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
