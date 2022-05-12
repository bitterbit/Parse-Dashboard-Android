package com.galtashma.parsedashboard.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.annotation.NonNull
import com.galtashma.parsedashboard.ParseField
import com.galtashma.parsedashboard.R
import com.lucasurbas.listitemview.ListItemView

/**
 * Created by gal on 3/16/18, rewritten by Cyb3rKo on 05/12/22.
 */

class ParseObjectFieldsAdapter(
    @NonNull context: Context,
    @NonNull objects: List<ParseField>
) : ArrayAdapter<ParseField>(context, R.layout.list_item, objects) {

    private var longClickListener: View.OnLongClickListener? = null

    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup
    ): View {
        val details = getItem(position)

        var finalView = convertView
        if (convertView == null) {
            finalView = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false)
        }

        val view = finalView as ListItemView

        if (details == null) {
            return finalView
        }

        view.title = details.value.ifBlank { "<empty>" }

        view.subtitle = details.key
        view.setOnLongClickListener(longClickListener)

        return finalView
    }

    fun setLongClickListener(longClickListener: View.OnLongClickListener) {
        this.longClickListener = longClickListener
    }
}
