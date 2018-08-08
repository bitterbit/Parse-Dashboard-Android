package com.galtashma.parsedashboard.screens;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.crashlytics.android.answers.CustomEvent;
import com.galtashma.parsedashboard.Const;
import com.galtashma.parsedashboard.Hash;
import com.galtashma.parsedashboard.adapters.ParseObjectFieldsAdapter;
import com.galtashma.parsedashboard.ParseField;
import com.galtashma.parsedashboard.R;
import com.lucasurbas.listitemview.ListItemView;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.vlonjatg.progressactivity.ProgressRelativeLayout;

import java.util.ArrayList;

public class SingleObjectParseActivity extends AppCompatActivity implements GetCallback<ParseObject>, View.OnLongClickListener {

    private String className, objectId;
    private ListView listView;
    private ProgressRelativeLayout statefulLayout;

    private ParseObject parseObject = null;


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

        className = extra.getString(Const.BUNDLE_KEY_CLASS_NAME);
        objectId = extra.getString(Const.BUNDLE_KEY_OBJECT_ID);
        setTitle(String.format("%s - %s", className, objectId));

        statefulLayout = findViewById(R.id.stateful_layout);
        listView = (ListView) findViewById(R.id.list_view);
        fetch();

        Answers.getInstance().logContentView(new ContentViewEvent()
                .putContentId(Hash.sha1(className+objectId))
                .putContentName("Object Activity")
                .putContentType("Screen"));
    }

    private void fetch(){
        statefulLayout.showLoading();
        getQuery().getFirstInBackground(this);
    }

    @Override
    public void done(ParseObject object, ParseException e) {
        if (e != null){
            Log.e(Const.TAG, "Error while fetching object", e);
            showError("Error fetching Parse Object.", e);
            return;
        }

        if (object == null){
            Log.e(Const.TAG, "Error while fetching object. object is null");
            showError("Empty Parse Object.");
            return;
        }
        parseObject = object;
        statefulLayout.showContent();

        ArrayList<ParseField> fields = new ArrayList<>();
        fields.add(new ParseField("objectId", object.getObjectId()));
        fields.add(new ParseField("createdAt", object.getCreatedAt().toString()));
        fields.add(new ParseField("updatedAt", object.getUpdatedAt().toString()));
        for (String key : object.keySet()){
            Object value = object.get(key);
            if (value == null){
                value = "<empty>";
            }

            fields.add(new ParseField(key, value.toString()));
        }

        ParseObjectFieldsAdapter adapter = new ParseObjectFieldsAdapter(this, fields);
        adapter.setLongClickListener(this);
        listView.setAdapter(adapter);
    }

    private ParseQuery<ParseObject> getQuery(){
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(className);
        query.whereEqualTo("objectId", objectId);
        return query;
    }

    private void showError(String message, Exception e){
        showError(message + "\n" + e.getMessage());
    }

    private void showError(String message){
        statefulLayout.showError(R.drawable.ic_parse_24dp, "Error", message, "Retry", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetch();
            }
        });
    }

    private void setTitle(String text){
        this.getSupportActionBar().setTitle("");
        ((TextView)findViewById(R.id.big_title_text)).setText(text);
    }

    @Override
    public boolean onLongClick(View view) {
        ListItemView listItemView = (ListItemView) view;
        listItemView.getTitle();
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(listItemView.getSubtitle(), listItemView.getTitle());
        clipboard.setPrimaryClip(clip);
        showMessage(getString(R.string.copied_to_clipboard));
        Answers.getInstance().logCustom(new CustomEvent("Action")
                .putCustomAttribute("type", "copied field to clipboard"));
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_object_view, menu);
        return true;
    }

    public void onRemoveClick(MenuItem item) {
        statefulLayout.showLoading();
        try {
            parseObject.delete();
        } catch (ParseException e) {
            e.printStackTrace();
            statefulLayout.showContent();
            showMessage("Error deleting object ("+e.getMessage()+")");
            return;
        }

        statefulLayout.showEmpty(R.drawable.ic_parse_24dp, "Item Deleted", "The item was successfully deleted.");
        Answers.getInstance().logCustom(new CustomEvent("Action")
                .putCustomAttribute("type", "remove object"));
    }


    private void showMessage(String message){
        Snackbar.make(statefulLayout, message, Snackbar.LENGTH_LONG).show();
    }
}
