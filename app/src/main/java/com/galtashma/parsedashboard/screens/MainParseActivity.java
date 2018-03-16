package com.galtashma.parsedashboard.screens;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListAdapter;
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListItem;
import com.galtashma.parsedashboard.ParseServerConfig;
import com.galtashma.parsedashboard.ParseServerConfigStorage;
import com.galtashma.parsedashboard.R;

public class MainParseActivity extends AppCompatActivity implements MaterialDialog.SingleButtonCallback {

    private MaterialDialog dialog = null;
    private ParseServerConfigStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
         Log.i("TAG", storage.getServers().toString());
    }

    private void showDialog(){
        this.dialog = new MaterialDialog.Builder(this)
                .title("Add Parse Server")
                .customView(R.layout.add_app_dialog, true)
                .positiveText("OK")
                .onPositive(this)
                .show();
    }

    @Override
    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
        if (dialog != null){
            View v = dialog.getCustomView();
            EditText appName = v.findViewById(R.id.inputAppName);
            EditText appId = v.findViewById(R.id.inputAppId);
            EditText masterKey = v.findViewById(R.id.inputAppMasterKey);
            EditText serverUrl = v.findViewById(R.id.inputServerUrl);

            ParseServerConfig serverConfig = new ParseServerConfig(
                    appName.getText().toString(),
                    appId.getText().toString(),
                    masterKey.getText().toString(),
                    serverUrl.getText().toString());

            storage.saveServer(serverConfig);
        }
    }
}
