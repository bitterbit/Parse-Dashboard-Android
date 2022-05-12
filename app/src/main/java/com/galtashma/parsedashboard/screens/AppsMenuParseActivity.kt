package com.galtashma.parsedashboard.screens

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.PersistableBundle
import android.util.Log
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.afollestad.materialdialogs.DialogAction
import com.afollestad.materialdialogs.MaterialDialog
import com.galtashma.parsedashboard.Const
import com.galtashma.parsedashboard.ParseServerConfig
import com.galtashma.parsedashboard.ParseServerConfigStorage
import com.galtashma.parsedashboard.R
import com.galtashma.parsedashboard.adapters.ParseAppsAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.parse.Parse
import com.vlonjatg.progressactivity.ProgressRelativeLayout
import com.vorlonsoft.android.rate.AppRate
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

/**
 * Created by gal on 3/16/18, rewritten by Cyb3rKo on 05/12/22.
 */

class AppsMenuParseActivity :
    AppCompatActivity(),
    MaterialDialog.SingleButtonCallback,
    ParseAppsAdapter.ParseAppAdapterListener {

    private lateinit var storage: ParseServerConfigStorage
    private lateinit var adapter: ParseAppsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_apps_menu)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener { showDialog() }

        storage = ParseServerConfigStorage(applicationContext)
        toggleMainScreen(isMainScreenEmpty())

        val list = storage.getServers()
        adapter = ParseAppsAdapter(this, list)
        val listView = findViewById<ListView>(R.id.list_view)
        listView.adapter = adapter
        adapter.setListener(this)

        AppRate.with(this)
            .setInstallDays(2)
            .setLaunchTimes(4)
            .setShowLaterButton(true)
            .setShowNeverButton(true)
            .monitor()

        // Show dialog after 40 seconds
        Handler(Looper.getMainLooper()).postDelayed({
            AppRate.showRateDialogIfMeetsConditions(this)
        }, 1000L * 40)
    }

    private fun isMainScreenEmpty() = storage.getServers().isEmpty()

    private fun toggleMainScreen(isEmpty: Boolean) {
        val layout = findViewById<ProgressRelativeLayout>(R.id.stateful_layout)
        if (isEmpty) {
            layout.showEmpty(
                R.drawable.ic_parse_24dp,
                getString(R.string.empty_state_apps_screen_short),
                getString(R.string.empty_state_apps_screen_long)
            )
        } else {
            layout.showContent()
        }
    }

    private fun showDialog() {
        MaterialDialog.Builder(this)
            .title("Add Parse Server")
            .customView(R.layout.dialog_add_app, true)
            .positiveText("OK")
            .onPositive(this)
            .show()
    }

    override fun onClick(dialog: MaterialDialog, which: DialogAction) {
        val serverConfig = getConfigFromDialog(dialog)
        storage.saveServer(serverConfig)
        adapter.add(serverConfig)
        adapter.notifyDataSetChanged()
        toggleMainScreen(isMainScreenEmpty())
    }

    private fun getConfigFromDialog(dialog: MaterialDialog): ParseServerConfig {
        val view = dialog.customView
        view?.apply {
            val appName = findViewById<EditText>(R.id.inputAppName)
            val appId = findViewById<EditText>(R.id.inputAppId)
            val masterKey = findViewById<EditText>(R.id.inputAppMasterKey)
            val serverUrl = findViewById<EditText>(R.id.inputServerUrl)

            return ParseServerConfig(
                appName?.text.toString(),
                appId?.text.toString(),
                masterKey?.text.toString(),
                serverUrl?.text.toString()
            )
        }

        // If view is somehow null
        return ParseServerConfig(
            "",
            "",
            "",
            ""
        )
    }

    override fun onClickOpen(config: ParseServerConfig) {
        val error = checkForParseConfigError(config)
        println(config.appId)
        println(config.appName)
        println(config.serverUrl)
        println(config.masterKey)
        if (error != null) {
            Snackbar.make(findViewById(R.id.stateful_layout), error, Snackbar.LENGTH_LONG).show()
            return
        }

        // Re init parse sdk so we can open a new parse app
        Parse.destroy()
        initParse(config.appId, config.serverUrl, config.masterKey)

        val i = Intent(this, SingleAppParseActivity::class.java)
        i.putExtra(Const.BUNDLE_KEY_PARSE_APP_NAME, config.appName)
        startActivityForResult(i, 1)
    }

    // Validate parse server config. If no error returns null, otherwise returns error message.
    private fun checkForParseConfigError(config: ParseServerConfig): String? {
        if (config.appId == "") {
            return getString(R.string.error_app_id_missing)
        }
        if (config.serverUrl == "") {
            return getString(R.string.error_server_url_missing)
        }
        if (!config.serverUrl.startsWith("http://") && !config.serverUrl.startsWith("https://")) {
            return getString(R.string.error_server_url_malformed)
        }
        if (config.masterKey == "") {
            return getString(R.string.error_master_key_missing)
        }
        return null
    }

    override fun onClickEdit(config: ParseServerConfig) {
        val dialog = MaterialDialog.Builder(this)
            .title("Edit Parse Server")
            .customView(R.layout.dialog_add_app, true)
            .positiveText("OK")
            .onPositive { thisDialog, _ ->
                adapter.remove(config)
                storage.deleteServer(config.appId)
                val newConfig = getConfigFromDialog(thisDialog)
                adapter.add(newConfig)
                storage.saveServer(newConfig)
                adapter.notifyDataSetChanged()
            }
            .show()

        val view = dialog.customView
        view?.apply {
            findViewById<EditText>(R.id.inputAppName).setText(config.appName)
            findViewById<EditText>(R.id.inputAppId).setText(config.appId)
            findViewById<EditText>(R.id.inputAppMasterKey).setText(config.masterKey)
            findViewById<EditText>(R.id.inputServerUrl).setText(config.serverUrl)
        }
    }

    override fun onClickDelete(config: ParseServerConfig) {
        storage.deleteServer(config.appId)
        adapter.remove(config)
        adapter.notifyDataSetChanged()
        toggleMainScreen(isMainScreenEmpty())
    }

    private fun initParse(
        appId: String,
        serverUrl: String,
        clientKey: String
    ) {
        Log.i("ParseDashboard", "Starting client for $serverUrl appId: $appId")
        val builder = OkHttpClient.Builder()
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        builder.networkInterceptors().add(httpLoggingInterceptor)

        Parse.initialize(Parse.Configuration.Builder(this)
            .applicationId(appId)
            .server(serverUrl)
            .masterKey(clientKey)
            .clientBuilder(builder)
            .build())
    }
}
