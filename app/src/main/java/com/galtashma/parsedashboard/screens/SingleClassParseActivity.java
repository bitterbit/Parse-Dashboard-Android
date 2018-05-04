package com.galtashma.parsedashboard.screens;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.crashlytics.android.answers.CustomEvent;
import com.galtashma.lazyparse.LazyList;
import com.galtashma.lazyparse.ScrollInfiniteAdapter;
import com.galtashma.lazyparse.ScrollInfiniteListener;
import com.galtashma.parsedashboard.Const;
import com.galtashma.parsedashboard.Hash;
import com.galtashma.parsedashboard.adapters.ParseObjectsAdapter;
import com.galtashma.parsedashboard.R;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.vlonjatg.progressactivity.ProgressRelativeLayout;

public class SingleClassParseActivity extends AppCompatActivity implements ScrollInfiniteAdapter.OnClickListener<ParseObject> {

    private String className;
    ProgressRelativeLayout statefulLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_class);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle extra = getIntent().getExtras();
        if (extra == null){
            extra = savedInstanceState;
        }

        if (extra == null || !extra.containsKey(Const.BUNDLE_KEY_CLASS_NAME)){
            finish();
            return;
        }

        className = extra.getString(Const.BUNDLE_KEY_CLASS_NAME);
        setTitle(className);

        statefulLayout = findViewById(R.id.stateful_layout);
        ListView listView = findViewById(R.id.list_view);
        initList(listView);

        Answers.getInstance().logContentView(new ContentViewEvent()
                .putContentId(Hash.sha1(className))
                .putContentName("Class Activity")
                .putContentType("Screen"));
    }

    private void initList(ListView listView){
        statefulLayout.showLoading();
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(className);
        LazyList<ParseObject> list = new LazyList<ParseObject>(query);
        ParseObjectsAdapter adapter  = new ParseObjectsAdapter(this, list);
        listView.setAdapter(adapter);
        listView.setOnScrollListener(new ScrollInfiniteListener(adapter));
        adapter.setOnClickListener(this);

        if (list.getLimit() == 0){
            statefulLayout.showEmpty(R.drawable.ic_parse_24dp, getString(R.string.empty_state_objects_screen_short), getString(R.string.empty_state_objects_screen_long));
            return;
        }

        statefulLayout.showContent();
    }

    @Override
    public void onClick(ParseObject parseObject) {
        Intent i = new Intent(this, SingleObjectParseActivity.class);
        i.putExtra(Const.BUNDLE_KEY_CLASS_NAME, parseObject.getClassName());
        i.putExtra(Const.BUNDLE_KEY_OBJECT_ID, parseObject.getObjectId());
        this.startActivityForResult(i, 1);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_class_view, menu);
        return true;
    }

    public void onRefresh(MenuItem item) {
        initList((ListView) findViewById(R.id.list_view));
        Answers.getInstance().logCustom(new CustomEvent("Action")
                .putCustomAttribute("type", "refresh class activity"));
    }
}
