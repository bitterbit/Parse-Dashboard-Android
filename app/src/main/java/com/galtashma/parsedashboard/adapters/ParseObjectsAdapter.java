package com.galtashma.parsedashboard.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;

import com.galtashma.lazyparse.LazyList;
import com.galtashma.lazyparse.LazyParseObjectHolder;
import com.galtashma.lazyparse.ScrollInfiniteAdapter;
import com.galtashma.parsedashboard.R;
import com.lucasurbas.listitemview.ListItemView;
import com.parse.ParseObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by gal on 3/14/18.
 */

public class ParseObjectsAdapter extends ScrollInfiniteAdapter<ParseObject> {

    private List<String> previewFieldNames;

    public ParseObjectsAdapter(Context context, LazyList<ParseObject> lazyValues) {
        super(context, lazyValues, R.layout.list_item, 15);
        previewFieldNames = Arrays.asList("createdAt", "updatedAt");
    }

    public ParseObjectsAdapter(Context context, LazyList<ParseObject> lazyValues, List<String> previewFields) {
        super(context, lazyValues, R.layout.list_item, 15);
        this.previewFieldNames = previewFields;
    }

    public void updatePreviewFields(List<String> previewFields){
        this.previewFieldNames = previewFields;
        this.notifyDataSetChanged();
    }

    @Override
    public View renderReadyLazyObject(ParseObject t, View view, @NonNull ViewGroup viewGroup) {
        ListItemView item = (ListItemView) view;
        item.setTitle(t.getObjectId());
        item.setMultiline(true);

        Map<String, String> fields = new HashMap<>();
        for (String fieldName : previewFieldNames) {
            String value = formatParseField(t, fieldName);
            if (value != null) {
                fields.put(fieldName, formatParseField(t, fieldName));
            }
        }
        item.setSubtitle(mapToString(fields));
        return view;
    }

    private String formatParseField(ParseObject t, String key) {
        if (key.equals("createdAt")) {
            return t.getCreatedAt().toString();
        }
        if (key.equals("updatedAt")) {
            return t.getCreatedAt().toString();
        }

        if (t.has(key)) {
            return t.get(key).toString();
        }

        return null;
    }

    @Override
    public View renderLoadingLazyObject(LazyParseObjectHolder<ParseObject> lazyParseObjectHolder, View view, @NonNull ViewGroup viewGroup) {
        ListItemView item = (ListItemView) view;
        item.setTitle("Loading...");
        return view;
    }

    private String mapToString(Map<String, String> map) {
        if (map.size() <= 0) {
            return "";
        }
        
        StringBuilder sb = new StringBuilder();
        for (String k : map.keySet()) {
            sb.append(k);
            sb.append(": ");
            sb.append(map.get(k));
            sb.append("\n");
        }

        sb.delete(sb.length()-1, sb.length()); // remove last \n

        return sb.toString();
    }
}
