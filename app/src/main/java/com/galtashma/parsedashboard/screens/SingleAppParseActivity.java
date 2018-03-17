package com.galtashma.parsedashboard.screens;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ListView;

import com.afollestad.ason.Ason;
import com.galtashma.parsedashboard.Const;
import com.galtashma.parsedashboard.ParseServerConfig;
import com.galtashma.parsedashboard.R;
import com.galtashma.parsedashboard.adapters.ParseClassesAdapter;
import com.parse.Parse;
import com.parse.ParseSchema;

import java.util.List;

import bolts.Continuation;
import bolts.Task;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;


public class SingleAppParseActivity extends AppCompatActivity {

    private ParseClassesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_app);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        Bundle extra = getIntent().getExtras();
        if (extra == null){
            extra = savedInstanceState;
        }

        String parseConfigJson = extra.getString(Const.BUNDLE_KEY_PARSE_CONFIG);
        ParseServerConfig config = Ason.deserialize(parseConfigJson, ParseServerConfig.class);

        initParse(config.appId, config.serverUrl, config.masterKey);
        setTitle(config.appName);

        adapter = new ParseClassesAdapter(this);
        ListView listView = findViewById(R.id.list_view_view);
        listView.setAdapter(adapter);

        ParseSchema.getParseSchemasAsync().onSuccess(new Continuation<List<ParseSchema>, Void>() {
            @Override
            public Void then(Task<List<ParseSchema>> task) throws Exception {
                Log.i(Const.TAG, "found schemas " + task.getResult());
                updateListOnUIThread(task.getResult());
                return null;
            }
        });

        adapter.setListener(new ParseClassesAdapter.OnClickListener() {
            @Override
            public void onSchemaClicked(ParseSchema schema) {
                showTable(schema.getName());
            }
        });
    }

    private void updateListOnUIThread(final List<ParseSchema> schemas){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.addAll(schemas);
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void showTable(String tableName){
        Intent i = new Intent(this, SingleClassParseActivity.class);
        i.putExtra(Const.BUNDLE_KEY_CLASS_NAME, tableName);
        this.startActivityForResult(i, 1);
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
