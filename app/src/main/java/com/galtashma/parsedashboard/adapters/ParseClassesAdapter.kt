package com.galtashma.parsedashboard.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.annotation.NonNull
import com.galtashma.parsedashboard.R
import com.lucasurbas.listitemview.ListItemView
import com.parse.ParseSchema

/**
 * Created by gal on 3/16/18, rewritten by Cyb3rKo on 05/12/22.
 */

class ParseClassesAdapter(
    @NonNull context: Context
) : ArrayAdapter<ParseSchema>(context, R.layout.list_item) {

    interface OnClickListener {
        fun onSchemaCLicked(schema: ParseSchema)
    }

    private lateinit var localListener: OnClickListener

    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup
    ): View {
        var finalView = convertView
        val schema = getItem(position)
        if (convertView == null) {
            finalView = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false)
        }

        val item = finalView as ListItemView

        item.title = schema?.name
        if (schema != null) {
            item.subtitle = getItemCountString(schema) + getSchemaString(schema)
        }

        item.setOnClickListener {
            if (schema != null) {
                localListener.onSchemaCLicked(schema)
            }
        }

        return finalView
    }

    private fun getItemCountString(schema: ParseSchema): String {
        val itemCount = schema.countIfFetched
        if (itemCount != -1) {
            return "($itemCount)\t"
        }

        schema.setOnCountListener { _, _ -> notifyDataSetChanged() }

        return ""
    }

    private fun getSchemaString(schema: ParseSchema): String {
        val list = schema.fields.keys.toList().sorted()
        return list.toString().replace("\\[|\\]", "").replace(",", " ")
    }

    fun setListener(listener: OnClickListener) {
        this.localListener = listener
    }
}
