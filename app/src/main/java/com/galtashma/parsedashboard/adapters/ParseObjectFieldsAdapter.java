package com.galtashma.parsedashboard.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.galtashma.parsedashboard.ParseField;
import com.galtashma.parsedashboard.R;
import com.lucasurbas.listitemview.ListItemView;

import java.util.List;
import java.util.Map;

/**
 * Created by gal on 3/16/18.
 */

public class ParseObjectFieldsAdapter extends ArrayAdapter<ParseField> {

    public ParseObjectFieldsAdapter(@NonNull Context context, @NonNull List<ParseField> objects) {
        super(context, R.layout.list_item, objects);
    }

    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        ParseField details = getItem(position);
        if(convertView == null) {
            convertView = LayoutInflater.from(this.getContext()).inflate(R.layout.list_item, parent, false);
        }

        ListItemView view = (ListItemView) convertView;
        view.setTitle(details.value);
        view.setSubtitle(details.key);

        return convertView;
    }
}
