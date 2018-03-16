package com.galtashma.parsedashboard.screens;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.galtashma.lazyparse.LazyList;
import com.galtashma.lazyparse.ScrollInfiniteAdapter;
import com.galtashma.lazyparse.ScrollInfiniteListener;
import com.galtashma.parsedashboard.Const;
import com.galtashma.parsedashboard.LazyParseSchema;
import com.galtashma.parsedashboard.R;
import com.galtashma.parsedashboard.SchemaListAdapter;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseSchemaQuery;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;


public class AppParseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parse_app);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initParse(getString(R.string.parse_app_id), getString(R.string.parse_server_url), getString(R.string.parse_master_key));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        ParseSchemaQuery<LazyParseSchema> query = LazyParseSchema.getQuery();

        LazyList<LazyParseSchema> list = new LazyList<LazyParseSchema>(query);
        SchemaListAdapter adapter  = new SchemaListAdapter(this, list);

        ListView listView = findViewById(R.id.list_view_view);
        listView.setAdapter(adapter);
        listView.setOnScrollListener(new ScrollInfiniteListener(adapter));

        adapter.setOnClickListener(new ScrollInfiniteAdapter.OnClickListener<LazyParseSchema>(){
            @Override
            public void onClick(LazyParseSchema parseObject) {
                showTable(parseObject.getClassName());
            }
        });
    }

    private void showTable(String tableName){
        Toast.makeText(this, "Clicked " + tableName, Toast.LENGTH_SHORT).show();

        Intent i = new Intent(this, ClassParseActivity.class);
        i.putExtra(Const.BUNDLE_KEY_CLASS_NAME, tableName);
        this.startActivityForResult(i, 1);
    }

    private void initParse(String appId, String serverUrl, String masterKey){
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.networkInterceptors().add(httpLoggingInterceptor);

        ParseObject.registerSubclass(LazyParseSchema.class);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(appId)
                .server(serverUrl)
                .masterKey(masterKey)
                .clientBuilder(builder)
                .build());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_parse_app, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
