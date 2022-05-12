package com.galtashma.parsedashboard.screens

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.galtashma.parsedashboard.Const
import com.galtashma.parsedashboard.R
import com.galtashma.parsedashboard.adapters.ParseClassesAdapter
import com.parse.ParseSchema
import com.vlonjatg.progressactivity.ProgressRelativeLayout

/**
 * Created by gal on 3/16/18, rewritten by Cyb3rKo on 05/12/22.
 */

class SingleAppParseActivity : AppCompatActivity() {
    private lateinit var adapter: ParseClassesAdapter
    private lateinit var statefulLayout: ProgressRelativeLayout

    private val schemas = hashMapOf<String, ParseSchema>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_app)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        var extra = intent.extras
        if (extra == null) {
            extra = savedInstanceState
        }

        val appName = if (extra != null && extra.containsKey(Const.BUNDLE_KEY_PARSE_APP_NAME)) {
            extra.getString(Const.BUNDLE_KEY_PARSE_APP_NAME)
        } else ""

        title = appName

        statefulLayout = findViewById(R.id.stateful_layout)
        adapter = ParseClassesAdapter(this)
        adapter.setListener(object: ParseClassesAdapter.OnClickListener {
            override fun onSchemaCLicked(schema: ParseSchema) {
                showTable(schema.name)
            }
        })
        val listView = findViewById<ListView>(R.id.list_view)
        listView.adapter = adapter
        fetchSchemasAsync()
    }

    private fun fetchSchemasAsync() {
        statefulLayout.showLoading()
        ParseSchema.getParseSchemasAsync().continueWith { task ->
            if (task.isFaulted || task.isCancelled) {
                showErrorOnUIThread(getString(R.string.schemas_screen_error_title), task.error)
                Log.e("ParseDashboard","Error fetching from parse server.", task.error)
                return@continueWith null
            }

            Log.i(Const.TAG, "Found schemas: ${task.result}")
            val s = task.result
            updateListOnUIThread(s)
            s.forEach { ps ->
                schemas[ps.name] = ps
            }

            return@continueWith null
        }
    }

    private fun updateListOnUIThread(schemasList: List<ParseSchema>) {
        runOnUiThread {
            if (schemasList.isEmpty()) {
                showEmptyState()
                return@runOnUiThread
            }
            statefulLayout.showContent()
            adapter.clear()
            adapter.addAll(schemasList)
            adapter.notifyDataSetChanged()
        }
    }

    private fun showErrorOnUIThread(title: String, e: Exception) {
        runOnUiThread {
            statefulLayout.showError(
                R.drawable.ic_parse_24dp,
                title,
                e.localizedMessage,
                "Retry"
            ) {
                fetchSchemasAsync()
            }
        }
    }

    private fun showEmptyState() {
        statefulLayout.showEmpty(
            R.drawable.ic_parse_24dp,
            getString(R.string.empty_state_schemas_screen_short),
            getString(R.string.empty_state_schemas_screen_long)
        )
    }

    private fun showTable(tableName: String) {
        val intent = Intent(this, SingleClassParseActivity::class.java)
        intent.putExtra(Const.BUNDLE_KEY_CLASS_NAME, tableName)
        if (schemas.containsKey(tableName)) {
            val fields = schemas[tableName]?.fields?.keys?.toTypedArray()
            if (fields != null) {
                intent.putExtra(Const.BUNDLE_KEY_CLASS_FIELDS_NAME, fields)
                this.startActivityForResult(intent, 1)
            }
        }
    }
}
