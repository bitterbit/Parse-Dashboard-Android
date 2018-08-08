package com.galtashma.parsedashboard.screens;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
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
import com.parse.ParseSchema;
import com.vlonjatg.progressactivity.ProgressRelativeLayout;

import java.util.ArrayList;
import java.util.List;

public class SingleClassParseActivity extends AppCompatActivity implements ScrollInfiniteAdapter.OnClickListener<ParseObject> {

    private String className;
    ProgressRelativeLayout statefulLayout;
    private String[] fieldNames;

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

        if (extra.containsKey(Const.BUNDLE_KEY_CLASS_FIELDS_NAME)){
            fieldNames = extra.getStringArray(Const.BUNDLE_KEY_CLASS_FIELDS_NAME);
        } else {
            fieldNames = new String[]{"objectId", "createdAt", "updatedAt"};
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

    public void onSelectFavFields(MenuItem item) {

        new MaterialDialog.Builder(this)
                .title(R.string.action_select_fav_fields)
                .items(fieldNames)
                .itemsCallbackMultiChoice(null, new MaterialDialog.ListCallbackMultiChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                        /**
                         * If you use alwaysCallMultiChoiceCallback(), which is discussed below,
                         * returning false here won't allow the newly selected check box to actually be selected
                         * (or the newly unselected check box to be unchecked).
                         * See the limited multi choice dialog example in the sample project for details.
                         **/

                        Log.d("ParseDashboard", "onSelection " + which + " " + text);
                        return true;
                    }
                })
                .positiveText(R.string.save)
                .show();
    }
}
