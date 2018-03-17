package com.galtashma.parsedashboard.screens;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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

public class SingleObjectParseActivity extends AppCompatActivity implements GetCallback<ParseObject> {

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_object);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle extra = getIntent().getExtras();
        if (extra == null){
            extra = savedInstanceState;
        }

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

        if (object == null){
            Log.e(Const.TAG, "Error while fetching object. object is null");
            return;
        }

        ArrayList<ParseField> fields = new ArrayList<>();
        for (String key : object.keySet()){
            Object value = object.get(key);
            if (value == null){
                value = "<empty>";
            }

            fields.add(new ParseField(key, value.toString()));
        }

        ParseObjectFieldsAdapter adapter = new ParseObjectFieldsAdapter(this, fields);
        listView.setAdapter(adapter);
    }
}
