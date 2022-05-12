package com.galtashma.parsedashboard.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.annotation.NonNull
import com.galtashma.parsedashboard.ParseServerConfig
import com.galtashma.parsedashboard.R
import com.lucasurbas.listitemview.ListItemView

/**
 * Created by gal on 3/16/18, rewritten by Cyb3rKo on 05/12/22.
 */

class ParseAppsAdapter(
    @NonNull context: Context,
    @NonNull objects: List<ParseServerConfig>
) : ArrayAdapter<ParseServerConfig>(context, R.layout.list_item_card, objects) {

    interface ParseAppAdapterListener {
        fun onClickOpen(config: ParseServerConfig)
        fun onClickEdit(config: ParseServerConfig)
        fun onClickDelete(config: ParseServerConfig)
    }

    private lateinit var listener: ParseAppAdapterListener

    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup
    ): View {
        val server = getItem(position)

        var finalView = convertView
        if (convertView == null) {
            finalView = LayoutInflater.from(context)
                .inflate(R.layout.list_item_card, parent, false)
        }

        val item = finalView?.findViewById<ListItemView>(R.id.parse_server_list_item)
        item?.title = server?.appName
        item?.subtitle = "${server?.serverUrl}\n${server?.appId}"

        if (server != null) {
            item?.setOnMenuItemClickListener { clickedItem ->
                if (clickedItem.itemId == R.id.action_edit) {
                    notifyEdit(server)
                } else if (clickedItem.itemId == R.id.action_remove) {
                    notifyDelete(server)
                }
            }
            item?.setOnClickListener { notifyClick(server) }
        }

        return finalView!!
    }

    private fun notifyClick(config: ParseServerConfig) {
        listener.onClickOpen(config)
    }

    private fun notifyEdit(config: ParseServerConfig) {
        listener.onClickEdit(config)
    }

    private fun notifyDelete(config: ParseServerConfig) {
        listener.onClickDelete(config)
    }

    fun setListener(listener: ParseAppAdapterListener) {
        this.listener = listener
    }
}
