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
import com.parse.ParseObject;

/**
 * Created by gal on 3/14/18.
 */

public class ObjectListAdapter extends ScrollInfiniteAdapter<ParseObject> {

    public ObjectListAdapter(Context context, LazyList<ParseObject> lazyValues) {
        super(context, lazyValues, android.R.layout.simple_list_item_1, 15);
    }

    @Override
    public View renderReadyLazyObject(ParseObject t, View view, @NonNull ViewGroup viewGroup) {
        final TextView tv = view.findViewById(android.R.id.text1);
        tv.setTextColor(Color.BLACK);
        tv.setText(t.getClassName());
        return view;
    }

    @Override
    public View renderLoadingLazyObject(LazyParseObjectHolder<ParseObject> lazyParseObjectHolder, View view, @NonNull ViewGroup viewGroup) {
        final TextView tv = view.findViewById(android.R.id.text1);
        tv.setTextColor(Color.BLACK);
        tv.setText("Loading...");
        return view;
    }
}
