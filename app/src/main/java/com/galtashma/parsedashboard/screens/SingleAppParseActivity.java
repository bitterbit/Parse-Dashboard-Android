package com.galtashma.parsedashboard.screens;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.afollestad.ason.Ason;
import com.galtashma.parsedashboard.Const;
import com.galtashma.parsedashboard.Hash;
import com.galtashma.parsedashboard.ParseServerConfig;
import com.galtashma.parsedashboard.R;
import com.galtashma.parsedashboard.adapters.ParseClassesAdapter;
import com.parse.Parse;
import com.parse.ParseQuery;
import com.parse.ParseSchema;
import com.vlonjatg.progressactivity.ProgressRelativeLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import bolts.Continuation;
import bolts.Task;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;


public class SingleAppParseActivity extends AppCompatActivity {

    private ParseClassesAdapter adapter;
    private ProgressRelativeLayout statefulLayout;

    private Map<String, ParseSchema> schemas = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_app);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        String appName = "";

        Bundle extra = getIntent().getExtras();
        if (extra == null){
            extra = savedInstanceState;
        }

        if (extra != null && extra.containsKey(Const.BUNDLE_KEY_PARSE_APP_NAME)){
            appName = extra.getString(Const.BUNDLE_KEY_PARSE_APP_NAME);
        }

        setTitle(appName);

        statefulLayout = findViewById(R.id.stateful_layout);
        adapter = new ParseClassesAdapter(this);
        adapter.setListener(new ParseClassesAdapter.OnClickListener() {
            @Override
            public void onSchemaClicked(ParseSchema schema) {
                showTable(schema.getName());
            }
        });
        ListView listView = findViewById(R.id.list_view);
        listView.setAdapter(adapter);
        fetchSchemasAsync();

//        Answers.getInstance().logContentView(new ContentViewEvent()
//                .putContentId(Hash.sha1(appName))
//                .putContentName("App Activity")
//                .putContentType("Screen"));
    }

    private void fetchSchemasAsync(){
        statefulLayout.showLoading();
        ParseSchema.getParseSchemasAsync().continueWith(new Continuation<List<ParseSchema>, Void>() {
            @Override
            public Void then(Task<List<ParseSchema>> task) throws Exception {
                if (task.isFaulted() || task.isCancelled()){
                    showErrorOnUIThread(getString(R.string.schemas_screen_error_title), task.getError());
                    Log.e("ParseDashboard","Error fetching from "  + " parse server.", task.getError());
                    return null;
                }

                Log.i(Const.TAG, "found schemas " + task.getResult());
                List <ParseSchema> s = task.getResult();
                updateListOnUIThread(s);
                for(ParseSchema ps : s){
                    schemas.put(ps.getName(), ps);
                }

                return null;
            }
        });
    }

    private void updateListOnUIThread(final List<ParseSchema> schemasList){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (schemasList.size() == 0){
                    showEmptyState();
                    return;
                }
                statefulLayout.showContent();
                adapter.clear();
                adapter.addAll(schemasList);
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void showErrorOnUIThread(final String title, final Exception e){
        runOnUiThread(new Runnable() {
              @Override
              public void run() {
                  statefulLayout.showError(R.drawable.ic_parse_24dp, title, e.getLocalizedMessage(), "Retry", new View.OnClickListener() {
                      @Override
                      public void onClick(View view) {
                          fetchSchemasAsync();
                      }
                  });
              }
        });

    }

    private void showEmptyState(){
        statefulLayout.showEmpty(R.drawable.ic_parse_24dp, getString(R.string.empty_state_schemas_screen_short), getString(R.string.empty_state_schemas_screen_long));
    }

    private void showTable(String tableName){
        Intent intent = new Intent(this, SingleClassParseActivity.class);
        intent.putExtra(Const.BUNDLE_KEY_CLASS_NAME, tableName);
        if (schemas.containsKey(tableName)){
            int size = schemas.get(tableName).getFields().size();
            String[] fieldsArr = new String[size];

            Iterator<String> it = schemas.get(tableName).getFields().keySet().iterator();
            for (int i=0; i<size; i++){
                if (it.hasNext()) {
                    fieldsArr[i] = it.next();
                }
            }
            intent.putExtra(Const.BUNDLE_KEY_CLASS_FIELDS_NAME, fieldsArr);
        }
        this.startActivityForResult(intent, 1);
    }
}
