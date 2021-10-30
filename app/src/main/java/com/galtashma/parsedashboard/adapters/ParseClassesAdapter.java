package com.galtashma.parsedashboard.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import androidx.annotation.NonNull;

import com.galtashma.parsedashboard.R;
import com.lucasurbas.listitemview.ListItemView;
import com.parse.ParseSchema;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class ParseClassesAdapter extends ArrayAdapter<ParseSchema> {

    public interface OnClickListener {
        void onSchemaClicked(ParseSchema schema);
    }

    private OnClickListener listener;

    public ParseClassesAdapter(@NonNull Context context) {
        super(context, R.layout.list_item);
    }

    public ParseClassesAdapter(@NonNull Context context, @NonNull List<ParseSchema> objects) {
        super(context, R.layout.list_item, objects);
    }

    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        final ParseSchema schema = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(this.getContext()).inflate(R.layout.list_item, parent, false);
        }

        ListItemView item = (ListItemView) convertView;

        item.setTitle(schema.getName());
        item.setSubtitle(getItemCountString(schema) + getSchemaString(schema));

        item.setOnClickListener(view -> {
            if (listener != null){
                listener.onSchemaClicked(schema);
            }
        });

        return convertView;
    }

    private String getItemCountString(ParseSchema schema) {
        int itemCount = schema.getCountIfFetched();
        if (itemCount != -1) {
            return "("+itemCount+")\t";
        }

        schema.setOnCountListener((count, exception) -> notifyDataSetChanged());

        return "";
    }

    private String getSchemaString(ParseSchema schema){
        List<String> list = new ArrayList<>(schema.getFields().keySet());
        Collections.sort(list);
        return list.toString().replaceAll("\\[|\\]","").replaceAll(","," ");
    }

    public void setListener(OnClickListener listener) {
        this.listener = listener;
    }
}
