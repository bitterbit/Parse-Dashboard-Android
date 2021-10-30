package com.galtashma.parsedashboard.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import androidx.annotation.NonNull;

import com.galtashma.parsedashboard.ParseServerConfig;
import com.galtashma.parsedashboard.R;
import com.lucasurbas.listitemview.ListItemView;

import java.util.List;

/**
 * Created by gal on 3/16/18.
 */

public class ParseAppsAdapter extends ArrayAdapter<ParseServerConfig> {

    public interface ParseAppAdapterListener{
        void onClickOpen(ParseServerConfig config);
        void onClickEdit(ParseServerConfig config);
        void onClickDelete(ParseServerConfig config);
    }

    private ParseAppAdapterListener listener;

    public ParseAppsAdapter(@NonNull Context context, @NonNull List<ParseServerConfig> objects) {
        super(context, R.layout.list_item_card, objects);
    }

    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        final ParseServerConfig server = getItem(position);

        if(convertView == null) {
            convertView = LayoutInflater.from(this.getContext()).inflate(R.layout.list_item_card, parent, false);
        }

        ListItemView item = convertView.findViewById(R.id.parse_server_list_item);
        item.setTitle(server.appName);
        item.setSubtitle(server.serverUrl+"\n"+server.appId);

        item.setOnMenuItemClickListener(clickedItem -> {
            if (clickedItem.getItemId() == R.id.action_edit) {
                notifyEdit(server);
            } else if (clickedItem.getItemId() == R.id.action_remove) {
                notifyDelete(server);
            }
        });
        item.setOnClickListener(view -> notifyClick(server));

        return convertView;
    }

    private void notifyClick(ParseServerConfig config){
        if (listener!=null){
            listener.onClickOpen(config);
        }
    }

    private void notifyEdit(ParseServerConfig config){
        if (listener != null){
            listener.onClickEdit(config);
        }
    }

    private void notifyDelete(ParseServerConfig config){
        if (listener != null){
            this.listener.onClickDelete(config);
        }
    }

    public void setListener(ParseAppAdapterListener listener) {
        this.listener = listener;
    }
}
