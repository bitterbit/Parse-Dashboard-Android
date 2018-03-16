package com.galtashma.parsedashboard.screens;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;
import android.widget.Toast;

import com.galtashma.lazyparse.LazyList;
import com.galtashma.lazyparse.ScrollInfiniteAdapter;
import com.galtashma.lazyparse.ScrollInfiniteListener;
import com.galtashma.parsedashboard.Const;
import com.galtashma.parsedashboard.adapters.ParseObjectsAdapter;
import com.galtashma.parsedashboard.R;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class SingleClassParseActivity extends AppCompatActivity implements ScrollInfiniteAdapter.OnClickListener<ParseObject> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parse_table);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Bundle extra = getIntent().getExtras();
        if (extra == null){
            extra = savedInstanceState;
        }

        String tableName = extra.getString(Const.BUNDLE_KEY_CLASS_NAME);
        setTitle(tableName);

        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(tableName);
        LazyList<ParseObject> list = new LazyList<ParseObject>(query);
        ParseObjectsAdapter adapter  = new ParseObjectsAdapter(this, list);

        ListView listView = findViewById(R.id.list_view_view);
        listView.setAdapter(adapter);
        listView.setOnScrollListener(new ScrollInfiniteListener(adapter));

        adapter.setOnClickListener(this);
    }

    @Override
    public void onClick(ParseObject parseObject) {
        Toast.makeText(this, "Clicked " + parseObject.getObjectId(), Toast.LENGTH_SHORT).show();

        Intent i = new Intent(this, SingleObjectParseActivity.class);
        i.putExtra(Const.BUNDLE_KEY_CLASS_NAME, parseObject.getClassName());
        i.putExtra(Const.BUNDLE_KEY_OBJECT_ID, parseObject.getObjectId());
        this.startActivityForResult(i, 1);
    }
}
