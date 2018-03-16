package com.galtashma.parsedashboard.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import com.galtashma.lazyparse.LazyList;
import com.galtashma.lazyparse.LazyParseObjectHolder;
import com.galtashma.lazyparse.ScrollInfiniteAdapter;
import com.galtashma.parsedashboard.LazyParseSchema;
import com.galtashma.parsedashboard.R;
import com.lucasurbas.listitemview.ListItemView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by gal on 3/9/18.
 */

public class ParseClassesAdapter extends ScrollInfiniteAdapter<LazyParseSchema> {

    public ParseClassesAdapter(Context context, LazyList<LazyParseSchema> lazyValues) {
        super(context, lazyValues, R.layout.list_item, 15);
    }

    @Override
    public View renderReadyLazyObject(final LazyParseSchema lazyParseSchema, View view, @NonNull ViewGroup viewGroup) {
        ListItemView item = (ListItemView) view;
        item.setTitle(lazyParseSchema.getClassName());
        item.setSubtitle(getSchemaString(lazyParseSchema));

        return view;
    }

    @Override
    public View renderLoadingLazyObject(LazyParseObjectHolder<LazyParseSchema> lazyParseObjectHolder, View view, @NonNull ViewGroup viewGroup) {
        ListItemView item = (ListItemView) view;
        item.setTitle("Loading...");
        return view;
    }


    private String getSchemaString(LazyParseSchema schema){
        List<String> list = new ArrayList<String>(schema.getFields().keySet());
        Collections.sort(list);
        return list.toString().replaceAll("\\[|\\]","").replaceAll(","," ");
    }
}
