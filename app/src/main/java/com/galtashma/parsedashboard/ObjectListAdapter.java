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
import com.lucasurbas.listitemview.ListItemView;
import com.parse.ParseObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by gal on 3/14/18.
 */

public class ObjectListAdapter extends ScrollInfiniteAdapter<ParseObject> {

    public ObjectListAdapter(Context context, LazyList<ParseObject> lazyValues) {
        super(context, lazyValues, R.layout.list_item, 15);
    }

    @Override
    public View renderReadyLazyObject(ParseObject t, View view, @NonNull ViewGroup viewGroup) {
        ListItemView item = (ListItemView) view;
        item.setTitle(t.getObjectId());
        item.setMultiline(true);

        Map<String, String> fields = new HashMap<>();
        fields.put("createdAt", t.getCreatedAt().toString());
        fields.put("updatedAt", t.getUpdatedAt().toString());

        item.setSubtitle(mapToString(fields));

        return view;
    }

    @Override
    public View renderLoadingLazyObject(LazyParseObjectHolder<ParseObject> lazyParseObjectHolder, View view, @NonNull ViewGroup viewGroup) {
        ListItemView item = (ListItemView) view;
        item.setTitle("Loading...");
        return view;
    }

    private String mapToString(Map<String, String> map){
        StringBuilder sb = new StringBuilder();
        for(String k : map.keySet()){
            sb.append(k);
            sb.append(": ");
            sb.append(map.get(k));
            sb.append("\n");
        }

        sb.delete(sb.length()-1, sb.length()); // remove last \n

        return sb.toString();
    }
}
