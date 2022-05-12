package com.galtashma.parsedashboard.screens

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.afollestad.materialdialogs.MaterialDialog
import com.galtashma.lazyparse.LazyList
import com.galtashma.lazyparse.ScrollInfiniteAdapter
import com.galtashma.lazyparse.ScrollInfiniteListener
import com.galtashma.parsedashboard.Const
import com.galtashma.parsedashboard.ListPreferenceStore
import com.galtashma.parsedashboard.R
import com.galtashma.parsedashboard.SortPreferenceStore
import com.galtashma.parsedashboard.adapters.ParseObjectsAdapter
import com.parse.ParseObject
import com.parse.ParseQuery
import com.vlonjatg.progressactivity.ProgressRelativeLayout

/**
 * Created by gal on 3/16/18, rewritten by Cyb3rKo on 05/12/22.
 */

private const val PREF_KEY = "KEY_SingleClassParseActivity_"
private const val PREF_SORT = "SORT_SingleClassParseActivity"

class SingleClassParseActivity :
    AppCompatActivity(),
    ScrollInfiniteAdapter.OnClickListener<ParseObject> {

    private var className = ""
    private lateinit var statefulLayout: ProgressRelativeLayout
    private lateinit var fieldNames: MutableList<String>
    private lateinit var visibleFieldsStore: ListPreferenceStore
    private lateinit var sortPreferenceStore: SortPreferenceStore

    private lateinit var adapter: ParseObjectsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_class)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        var extra = intent.extras
        if (extra == null) {
            extra = savedInstanceState
        }

        if (extra == null || !extra.containsKey(Const.BUNDLE_KEY_CLASS_NAME)) {
            finish()
            return
        }

        className = extra.getString(Const.BUNDLE_KEY_CLASS_NAME).toString()
        title = className

        if (extra.containsKey(Const.BUNDLE_KEY_CLASS_FIELDS_NAME)) {
            fieldNames = extra.getStringArray(Const.BUNDLE_KEY_CLASS_FIELDS_NAME)!!.toMutableList()

            // Remove objectId field as it is special and is displayed anyway
            if (fieldNames.contains("objectId")) {
                fieldNames.remove("objectId")
            }
        } else {
            fieldNames = mutableListOf("createdAt", "updatedAt")
        }

        visibleFieldsStore = ListPreferenceStore(PREF_KEY + className)
        if (visibleFieldsStore.isEmpty()) {
            visibleFieldsStore.add("createdAt")
            visibleFieldsStore.add("updatedAt")
        }

        sortPreferenceStore = SortPreferenceStore(PREF_SORT + className)
        statefulLayout = findViewById(R.id.stateful_layout)

        initList()
    }

    private fun initList() {
        val listView = findViewById<ListView>(R.id.list_view)
        statefulLayout.showLoading()
        val query = ParseQuery<ParseObject>(className)

        if (!sortPreferenceStore.isEmpty()) {
            if (sortPreferenceStore.isAsc()) {
                query.orderByAscending(sortPreferenceStore.getKey())
            } else {
                query.orderByDescending(sortPreferenceStore.getKey())
            }
        }

        val list = LazyList(query)
        adapter = ParseObjectsAdapter(this, list, visibleFieldsStore.list)
        listView.adapter = adapter
        listView.setOnScrollListener(ScrollInfiniteListener(adapter))
        adapter.setOnClickListener(this)

        if (list.limit == 0) {
            statefulLayout.showEmpty(
                R.drawable.ic_parse_24dp,
                getString(R.string.empty_state_objects_screen_short),
                getString(R.string.empty_state_objects_screen_long)
            )
            return
        }

        statefulLayout.showContent()
    }

    override fun onClick(parseObject: ParseObject?) {
        val i = Intent(this, SingleObjectParseActivity::class.java)
        i.putExtra(Const.BUNDLE_KEY_CLASS_NAME, parseObject?.className)
        i.putExtra(Const.BUNDLE_KEY_OBJECT_ID, parseObject?.objectId)
        startActivityForResult(i, 1)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_class_view, menu)
        return true
    }

    private fun refresh() = initList()

    @Suppress("UNUSED_PARAMETER")
    fun onRefresh(item: MenuItem) = refresh()

    @Suppress("UNUSED_PARAMETER")
    fun onSelectFavFields(item: MenuItem) {
        val selectedIndices = mutableListOf<Int>()

        fieldNames.forEachIndexed { index, fieldName ->
            if (visibleFieldsStore.exists(fieldName)) {
                selectedIndices.add(index)
            }
        }

        Log.d("ParseDashboard", "Selected indices: $selectedIndices")

        MaterialDialog.Builder(this)
            .title(R.string.action_select_fav_fields)
            .items(fieldNames)
            .itemsCallbackMultiChoice(selectedIndices.toTypedArray()) { _, _, text ->
                visibleFieldsStore.reset()
                text.forEach { key ->
                    visibleFieldsStore.add(key.toString())
                }
                adapter.updatePreviewFields(visibleFieldsStore.list)
                true
            }
            .positiveText(R.string.save)
            .show()
    }

    private fun findKeyIndex(key: String): Int {
        fieldNames.forEachIndexed { index, k ->
            if (k == key) return index
        }
        return -1
    }

    @Suppress("UNUSED_PARAMETER")
    fun onSelectOrderFields(item: MenuItem) {
        val dialog = MaterialDialog.Builder(this)
            .title("Sort Fields")
            .customView(R.layout.dialog_field_sort, false).build()

        if (dialog.customView == null) return

        val dialogView = dialog.customView!!
        val fieldSelector = dialogView.findViewById<Spinner>(R.id.sort_dialog_selected_field)
        val ascRadioButton = dialogView.findViewById<RadioButton>(R.id.sort_dialog_order_asc)
        val descRadioButton = dialogView.findViewById<RadioButton>(R.id.sort_dialog_order_desc)
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            fieldNames
        )
        fieldSelector.adapter = adapter

        if (!sortPreferenceStore.isEmpty()) {
            val index = findKeyIndex(sortPreferenceStore.getKey())
            if (index != -1) {
                fieldSelector.setSelection(index)
                ascRadioButton.isChecked = sortPreferenceStore.isAsc()
                descRadioButton.isChecked = !sortPreferenceStore.isAsc()
            }
        }

        val cancelButton = dialogView.findViewById<Button>(R.id.sort_dialog_button_cancel)
        val confirmButton = dialogView.findViewById<Button>(R.id.sort_dialog_button_confirm)
        cancelButton.setOnClickListener { dialog.dismiss() }

        confirmButton?.setOnClickListener {
            val key = fieldSelector.selectedItem.toString()
            val asc = ascRadioButton.isChecked
            sortPreferenceStore.update(key, asc)
            dialog.dismiss()
            refresh()
        }

        dialog.show()
    }
}
