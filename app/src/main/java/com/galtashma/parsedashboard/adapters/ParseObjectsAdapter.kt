package com.galtashma.parsedashboard.adapters

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import com.galtashma.lazyparse.LazyList
import com.galtashma.lazyparse.LazyParseObjectHolder
import com.galtashma.lazyparse.ScrollInfiniteAdapter
import com.galtashma.parsedashboard.R
import com.lucasurbas.listitemview.ListItemView
import com.parse.ParseObject

/**
 * Created by gal on 3/16/18, rewritten by Cyb3rKo on 05/12/22.
 */

class ParseObjectsAdapter(
    context: Context,
    lazyValues: LazyList<ParseObject>,
    private var previewFieldNames: List<String>
) : ScrollInfiniteAdapter<ParseObject>(context, lazyValues, R.layout.list_item, 15) {

    fun updatePreviewFields(previewFields: List<String>){
        previewFieldNames = previewFields
        this.notifyDataSetChanged()
    }

    override fun renderReadyLazyObject(
        parseObject: ParseObject,
        view: View?,
        @NonNull viewGroup: ViewGroup
    ): View {
        val item = view as ListItemView
        item.title = parseObject.objectId
        item.setMultiline(true)

        val fields = HashMap<String, String?>()
        for (fieldName in previewFieldNames) {
            val value = formatParseField(parseObject, fieldName)
            if (value != null) {
                fields[fieldName] = formatParseField(parseObject, fieldName)
            }
        }
        item.subtitle = mapToString(fields)
        return view
    }

    private fun formatParseField(parseObject: ParseObject, key: String): String? {
        if (key == "createdAt") {
            return parseObject.createdAt.toString()
        }
        if (key == "updatedAt") {
            return parseObject.createdAt.toString()
        }

        if (parseObject.has(key)) {
            return parseObject.get(key).toString()
        }

        return null
    }

    override fun renderLoadingLazyObject(
        lazyParseObjectHolder: LazyParseObjectHolder<ParseObject>?,
        view: View?,
        viewGroup: ViewGroup
    ): View {
        val item = view as ListItemView
        item.title = "Loading..."
        return view
    }

    private fun mapToString(map: Map<String, String?>): String {
        if (map.isEmpty()) return ""

        val sb = StringBuilder()
        for (k in map.keys) {
            sb.append("$k: ${map[k]}\n")
        }

        sb.dropLast(1) // remove last \n

        return sb.toString()
    }
}
