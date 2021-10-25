package com.galtashma.parsedashboard.screens;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.afollestad.materialdialogs.MaterialDialog;
import com.galtashma.lazyparse.LazyList;
import com.galtashma.lazyparse.ScrollInfiniteAdapter;
import com.galtashma.lazyparse.ScrollInfiniteListener;
import com.galtashma.parsedashboard.Const;
import com.galtashma.parsedashboard.Hash;
import com.galtashma.parsedashboard.ListPreferenceStore;
import com.galtashma.parsedashboard.SortPreferenceStore;
import com.galtashma.parsedashboard.adapters.ParseObjectsAdapter;
import com.galtashma.parsedashboard.R;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.vlonjatg.progressactivity.ProgressRelativeLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SingleClassParseActivity extends AppCompatActivity implements ScrollInfiniteAdapter.OnClickListener<ParseObject> {

    private String className;
    ProgressRelativeLayout statefulLayout;
    private String[] fieldNames;
    private ListPreferenceStore visibleFieldsStore;
    private SortPreferenceStore sortPreferenceStore;

    private ParseObjectsAdapter adapter;

    private static final String PREF_KEY = "KEY_SingleClassParseActivity_";
    private static final String PREF_SORT = "SORT_SingleClassParseActivity";

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

        if (extra.containsKey(Const.BUNDLE_KEY_CLASS_FIELDS_NAME)){
            fieldNames = extra.getStringArray(Const.BUNDLE_KEY_CLASS_FIELDS_NAME);

            // Remove objectId field as it is special and is displayed anyway
            List<String> l = new ArrayList<String>(Arrays.asList(fieldNames));
            if (l.contains("objectId")){
                l.remove("objectId");
            }

            fieldNames = l.toArray(new String[l.size()]);

        } else {
            fieldNames = new String[]{"createdAt", "updatedAt"};
        }

        visibleFieldsStore = new ListPreferenceStore(PREF_KEY+className);
        if (visibleFieldsStore.isEmpty()){
            visibleFieldsStore.add("createdAt");
            visibleFieldsStore.add("updatedAt");
        }

        sortPreferenceStore = new SortPreferenceStore(PREF_SORT + className);
        statefulLayout = findViewById(R.id.stateful_layout);

        // TODO Replace old code with Firebase Analytics
//        Answers.getInstance().logContentView(new ContentViewEvent()
//                .putContentId(Hash.sha1(className))
//                .putContentName("Class Activity")
//                .putContentType("Screen"));

        initList();
    }

    private void initList(){
        ListView listView = findViewById(R.id.list_view);
        statefulLayout.showLoading();
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(className);

        if (!sortPreferenceStore.isEmpty()){
            if (sortPreferenceStore.isAsc()){
                query.orderByAscending(sortPreferenceStore.getKey());
            } else {
                query.orderByDescending(sortPreferenceStore.getKey());
            }
        }

        LazyList<ParseObject> list = new LazyList<ParseObject>(query);
        adapter  = new ParseObjectsAdapter(this, list, visibleFieldsStore.getList());
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

    private void refresh(){
        initList();
    }

    public void onRefresh(MenuItem item) {
        refresh();
        // TODO Replace old code with Firebase Analytics
//        Answers.getInstance().logCustom(new CustomEvent("Action")
//                .putCustomAttribute("type", "refresh class activity"));
    }

    public void onSelectFavFields(MenuItem item) {
        List<Integer> selectedIndices = new ArrayList<>();

        for (int i=0; i<fieldNames.length; i++){
            if (visibleFieldsStore.exists(fieldNames[i])){
                selectedIndices.add(i);
            }
        }

        Log.d("ParseDashboard", "selected indexes" + selectedIndices);

        new MaterialDialog.Builder(this)
                .title(R.string.action_select_fav_fields)
                .items(fieldNames)
                .itemsCallbackMultiChoice(selectedIndices.toArray(new Integer[selectedIndices.size()]), new MaterialDialog.ListCallbackMultiChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                        visibleFieldsStore.reset();
                        for(CharSequence key : text){
                            visibleFieldsStore.add(key.toString());
                        }
                        adapter.updatePreviewFields(visibleFieldsStore.getList());
                        return true;
                    }
                })
                .positiveText(R.string.save)
                .show();
    }

    private int findKeyIndex(String key){
        for (int i=0; i<fieldNames.length; i++){
            if (fieldNames[i].equals(key)){
                return i;
            }
        }
        return -1;
    }

    public void onSelectOrderFields(MenuItem menuItem) {
        final MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title("Sort Fields")
                .customView(R.layout.dialog_field_sort, false).build();

        final Spinner fieldSelector = (Spinner) dialog.getCustomView().findViewById(R.id.sort_dialog_selected_field);
        final RadioButton ascRadioButton = (RadioButton) dialog.getCustomView().findViewById(R.id.sort_dialog_order_asc);
        final RadioButton descRadioButton = (RadioButton) dialog.getCustomView().findViewById(R.id.sort_dialog_order_desc);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, fieldNames);
        fieldSelector.setAdapter(adapter);

        if (!sortPreferenceStore.isEmpty()) {
            int index = findKeyIndex(sortPreferenceStore.getKey());
            if (index != -1) {
                fieldSelector.setSelection(index);
                ascRadioButton.setChecked(sortPreferenceStore.isAsc());
                descRadioButton.setChecked(!sortPreferenceStore.isAsc());
            }
        }

        Button cancelButton = (Button) dialog.getCustomView().findViewById(R.id.sort_dialog_button_cancel);
        Button confirmButton = (Button) dialog.getCustomView().findViewById(R.id.sort_dialog_button_confirm);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String key = (String) fieldSelector.getSelectedItem();
                boolean asc = ascRadioButton.isChecked();
                sortPreferenceStore.update(key, asc);
                dialog.dismiss();
                refresh();
            }
        });

        dialog.show();
    }
}
