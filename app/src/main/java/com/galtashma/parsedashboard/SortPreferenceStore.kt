package com.galtashma.parsedashboard

import com.appizona.yehiahd.fastsave.FastSave

/**
 * Created by gal on 3/16/18, rewritten by Cyb3rKo on 05/12/22.
 */

class SortPreferenceStore(private val prefId: String) {
    fun update(key: String, asc: Boolean) {
        FastSave.getInstance().saveObject(prefId, SortPreferenceItem(key, asc))
    }

    fun getKey() = if (!isEmpty()) getSavedItem().key else ""

    fun isAsc(): Boolean {
        return if (isEmpty()) {
            false
        } else {
            getSavedItem().asc
        }
    }

    fun isEmpty() = !FastSave.getInstance().isKeyExists(this.prefId)

    private fun getSavedItem(): SortPreferenceItem {
        return FastSave.getInstance().getObject(this.prefId, SortPreferenceItem::class.java)
    }

    class SortPreferenceItem(val key: String, val asc: Boolean)
}
