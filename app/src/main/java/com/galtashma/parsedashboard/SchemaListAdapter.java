package com.galtashma.parsedashboard;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.galtashma.lazyparse.LazyList;
import com.galtashma.lazyparse.LazyParseObjectHolder;
import com.galtashma.lazyparse.ScrollInfiniteAdapter;

/**
 * Created by gal on 3/9/18.
 */

public class SchemaListAdapter extends ScrollInfiniteAdapter<LazyParseSchema> {

    public SchemaListAdapter(Context context, LazyList<LazyParseSchema> lazyValues) {
        super(context, lazyValues, android.R.layout.simple_list_item_1, 15);
    }

    @Override
    public View renderReadyLazyObject(final LazyParseSchema lazyParseSchema, View view, @NonNull ViewGroup viewGroup) {
        final TextView tv = view.findViewById(android.R.id.text1);
        tv.setTextColor(Color.BLACK);
        tv.setText(lazyParseSchema.getClassName());
        return view;
    }

    @Override
    public View renderLoadingLazyObject(LazyParseObjectHolder<LazyParseSchema> lazyParseObjectHolder, View view, @NonNull ViewGroup viewGroup) {
        final TextView tv = view.findViewById(android.R.id.text1);
        tv.setTextColor(Color.BLACK);
        tv.setText("Loading...");
        return view;
    }
}
