package com.galtashma.parsedashboard.screens;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.galtashma.parsedashboard.Const;
import com.galtashma.parsedashboard.adapters.ParseObjectFieldsAdapter;
import com.galtashma.parsedashboard.ParseField;
import com.galtashma.parsedashboard.R;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;

public class ObjectParseActivity extends AppCompatActivity implements GetCallback<ParseObject> {

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parse_object);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle extra = getIntent().getExtras();
        String className = extra.getString(Const.BUNDLE_KEY_CLASS_NAME);
        String objectId = extra.getString(Const.BUNDLE_KEY_OBJECT_ID);

        listView = (ListView) findViewById(R.id.list_view_view);


        setTitle(String.format("%s - %s", className, objectId));

        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(className);
        query.whereEqualTo("objectId", objectId);
        query.getFirstInBackground(this);
    }

    @Override
    public void done(ParseObject object, ParseException e) {
        if (e != null){
            Log.e(Const.TAG, "Error while fetching object", e);
            return;
        }

        ArrayList<ParseField> fields = new ArrayList<>();
        for (String key : object.keySet()){
            fields.add(new ParseField(key, object.get(key).toString()));
        }

        ParseObjectFieldsAdapter adapter = new ParseObjectFieldsAdapter(this, fields);
        listView.setAdapter(adapter);
    }
}
