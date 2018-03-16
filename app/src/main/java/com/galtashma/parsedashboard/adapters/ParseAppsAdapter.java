package com.galtashma.parsedashboard.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.galtashma.parsedashboard.ParseServerConfig;
import com.galtashma.parsedashboard.R;
import com.lucasurbas.listitemview.ListItemView;

import java.util.List;

/**
 * Created by gal on 3/16/18.
 */

public class ParseAppsAdapter extends ArrayAdapter<ParseServerConfig> {
    public ParseAppsAdapter(@NonNull Context context, @NonNull List<ParseServerConfig> objects) {
        super(context, R.layout.card_list_item, objects);
    }

    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        ParseServerConfig server = getItem(position);

        if(convertView == null) {
            convertView = LayoutInflater.from(this.getContext()).inflate(R.layout.card_list_item, parent, false);
        }

        ListItemView item = (ListItemView) convertView.findViewById(R.id.parse_server_list_item);
        item.setTitle(server.appName);
        item.setSubtitle(server.serverUrl+"\n"+server.appId);
//        item.setIconResId(R.drawable.ic_plus_24dp);
//        item.setIconColor(Color.RED);
//        item.setCircularIconColor(Color.RED);

        return convertView;
    }
}
