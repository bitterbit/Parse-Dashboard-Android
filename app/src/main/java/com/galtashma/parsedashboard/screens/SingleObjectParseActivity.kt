package com.galtashma.parsedashboard.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.galtashma.parsedashboard.Const
import com.galtashma.parsedashboard.ParseField
import com.galtashma.parsedashboard.R
import com.galtashma.parsedashboard.adapters.ParseObjectFieldsAdapter
import com.google.android.material.snackbar.Snackbar
import com.lucasurbas.listitemview.ListItemView
import com.parse.GetCallback
import com.parse.ParseException
import com.parse.ParseObject
import com.parse.ParseQuery
import com.vlonjatg.progressactivity.ProgressRelativeLayout

/**
 * Created by gal on 3/16/18, rewritten by Cyb3rKo on 05/12/22.
 */

class SingleObjectParseActivity :
    AppCompatActivity(),
    GetCallback<ParseObject>,
    View.OnLongClickListener {

    private var className = ""
    private var objectId = ""
    private lateinit var listView: ListView
    private lateinit var statefulLayout: ProgressRelativeLayout

    private var parseObject: ParseObject? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_object)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        var extra = intent.extras
        if (extra == null) {
            extra = savedInstanceState
        }

        className = extra!!.getString(Const.BUNDLE_KEY_CLASS_NAME).toString()
        objectId = extra.getString(Const.BUNDLE_KEY_OBJECT_ID).toString()
        setTitle("$className - $objectId")

        statefulLayout = findViewById(R.id.stateful_layout)
        listView = findViewById(R.id.list_view)
        fetch()
    }

    private fun fetch() {
        statefulLayout.showLoading()
        getQuery().getFirstInBackground(this)
    }

    override fun done(fetchedObject: ParseObject, e: ParseException?) {
        if (e != null) {
            Log.e(Const.TAG, "Error while fetching object", e)
            showError("Error fetching Parse Object.", e)
            return
        }

        parseObject = fetchedObject
        statefulLayout.showContent()

        val fields = mutableListOf<ParseField>()
        fields.add(ParseField("objectId", fetchedObject.objectId))
        fields.add(ParseField("createdAt", fetchedObject.createdAt.toString()))
        fields.add(ParseField("updatedAt", fetchedObject.updatedAt.toString()))
        fetchedObject.keySet().forEach { key ->
            var value = fetchedObject.get(key)
            if (value == null) {
                value = "<empty>"
            }

            fields.add(ParseField(key, value.toString()))
        }

        val adapter = ParseObjectFieldsAdapter(this, fields)
        adapter.setLongClickListener(this)
        listView.adapter = adapter
    }

    private fun getQuery(): ParseQuery<ParseObject> {
        val query = ParseQuery<ParseObject>(className)
        query.whereEqualTo("objectId", objectId)
        return query
    }

    private fun showError(message: String, e: Exception? = null) {
        var logMessage = message
        if (e != null) {
            logMessage += "\n${e.message}"
        }

        statefulLayout.showError(
            R.drawable.ic_parse_24dp,
            "Error",
            logMessage,
            "Retry"
        ) {
            fetch()
        }
    }

    private fun setTitle(text: String) {
        supportActionBar?.title = ""
        findViewById<TextView>(R.id.big_title_text).text = text
    }

    override fun onLongClick(view: View?): Boolean {
        val listItemView = view as ListItemView
        listItemView.title
        val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(listItemView.subtitle, listItemView.title)
        clipboard.setPrimaryClip(clip)
        showMessage(getString(R.string.copied_to_clipboard))
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_object_view, menu)
        return true
    }

    @Suppress("UNUSED_PARAMETER")
    fun onRemoveClick(item: MenuItem) {
        statefulLayout.showLoading()
        try {
            parseObject?.delete()
        } catch (e: ParseException) {
            e.printStackTrace()
            statefulLayout.showContent()
            showMessage("Error deleting object (${e.message})")
            return
        }

        statefulLayout.showEmpty(
            R.drawable.ic_parse_24dp,
            "Item Deleted",
            "The item was successfully deleted."
        )
    }

    private fun showMessage(message: String) {
        Snackbar.make(statefulLayout, message, Snackbar.LENGTH_LONG).show()
    }
}
