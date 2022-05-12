package com.galtashma.parsedashboard

import com.appizona.yehiahd.fastsave.FastSave

/**
 * Created by gal on 3/16/18, rewritten by Cyb3rKo on 05/12/22.
 */

class ListPreferenceStore(private val prefId: String) {
    var list = load()

    fun add(key: String) {
        if (!exists(key)) {
            list.add(key)
            save()
        }
    }

    fun remove(key: String) {
        if (list.contains(key)) {
            list.remove(key)
        }
        save()
    }

    fun reset() {
        list = mutableListOf()
        save()
    }

    fun exists(key: String) = list.contains(key)

    fun isEmpty() = list.isEmpty()

    fun size() = list.size

    private fun save() {
        FastSave.getInstance().saveObjectsList(prefId, list)
    }

    private fun load(): MutableList<String> {
        val l = FastSave.getInstance().getObjectsList(prefId, String::class.java)
        if (l != null) {
            return l
        }
        return mutableListOf()
    }
}
