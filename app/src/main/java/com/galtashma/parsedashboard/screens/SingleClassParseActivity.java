package com.galtashma.parsedashboard.screens;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;

import com.galtashma.lazyparse.LazyList;
import com.galtashma.lazyparse.ScrollInfiniteAdapter;
import com.galtashma.lazyparse.ScrollInfiniteListener;
import com.galtashma.parsedashboard.Const;
import com.galtashma.parsedashboard.adapters.ParseObjectsAdapter;
import com.galtashma.parsedashboard.R;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.vlonjatg.progressactivity.ProgressRelativeLayout;

public class SingleClassParseActivity extends AppCompatActivity implements ScrollInfiniteAdapter.OnClickListener<ParseObject> {

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

        String tableName = extra.getString(Const.BUNDLE_KEY_CLASS_NAME);
        setTitle(tableName);

        ProgressRelativeLayout statefulLayout = findViewById(R.id.stateful_layout);
        statefulLayout.showContent();

        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(tableName);
        LazyList<ParseObject> list = new LazyList<ParseObject>(query);
        ParseObjectsAdapter adapter  = new ParseObjectsAdapter(this, list);

        ListView listView = findViewById(R.id.list_view);
        listView.setAdapter(adapter);
        listView.setOnScrollListener(new ScrollInfiniteListener(adapter));

        adapter.setOnClickListener(this);

        if (list.getLimit() == 0){
            statefulLayout.showEmpty(R.drawable.ic_parse_24dp, getString(R.string.empty_state_objects_screen_short), getString(R.string.empty_state_objects_screen_long));
        }
    }

    @Override
    public void onClick(ParseObject parseObject) {
        Intent i = new Intent(this, SingleObjectParseActivity.class);
        i.putExtra(Const.BUNDLE_KEY_CLASS_NAME, parseObject.getClassName());
        i.putExtra(Const.BUNDLE_KEY_OBJECT_ID, parseObject.getObjectId());
        this.startActivityForResult(i, 1);
    }
}
