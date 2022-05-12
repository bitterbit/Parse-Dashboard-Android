package com.galtashma.parsedashboard

import android.content.Context
import com.afollestad.ason.AsonArray

/**
 * Created by gal on 3/16/18, rewritten by Cyb3rKo on 05/12/22.
 */

private const val PREF_KEY = "parse_server_key"
private const val PREF_SERVERS_KEY = "parse_server_config_key"

class ParseServerConfigStorage(val context: Context) {
    fun saveServer(config: ParseServerConfig) {
        val servers = getServersAson()
        servers.add(config)
        overrideServersAson(servers)
    }

    fun deleteServer(appId: String) {
        val servers = getServers()
        var toRemove: ParseServerConfig? = null

        servers.forEach {
            if (it.appId == appId) {
                toRemove = it
            }
        }

        if (toRemove != null) {
            servers.remove(toRemove)
            overrideServers(servers)
        }
    }

    fun getServers(): MutableList<ParseServerConfig> {
        val servers = getServersAson()
        return servers.deserializeList(ParseServerConfig::class.java)
    }

    private fun getServersAson(): AsonArray<ParseServerConfig> {
        val pref = context.getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE)
        val input = pref.getString(PREF_SERVERS_KEY, "[]")
        return AsonArray(input)
    }

    private fun overrideServers(servers: List<ParseServerConfig>) {
        val asonArray = AsonArray<ParseServerConfig>()
        servers.forEach {
            asonArray.add(it)
        }

        overrideServersAson(asonArray)
    }

    private fun overrideServersAson(servers: AsonArray<ParseServerConfig>) {
        val pref = context.getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE)
        val editor = pref.edit()
        editor.putString(PREF_SERVERS_KEY, servers.toString())
        editor.apply()
    }
}
