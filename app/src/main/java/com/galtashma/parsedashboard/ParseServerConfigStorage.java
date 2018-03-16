package com.galtashma.parsedashboard;

import android.content.Context;
import android.content.SharedPreferences;
import com.afollestad.ason.AsonArray;
import com.parse.Parse;

import java.util.List;

/**
 * Created by gal on 3/16/18.
 */

public class ParseServerConfigStorage {

    private static final String PREF_KEY = "parse_server_key";
    private static final String PREF_SERVERS_KEY = "parse_server_config_key";

    private final Context context;

    public ParseServerConfigStorage(Context context){
        this.context = context;
    }

    public  void saveServer(ParseServerConfig config){
        AsonArray<ParseServerConfig> servers = getServersAson();
        servers.add(config);
        overrideServersAson(servers);
    }

    public void deleteServer(String appId){
        List<ParseServerConfig> servers = getServers();
        ParseServerConfig toRemove = null;

        for (ParseServerConfig server : servers){
            if (server.appId.equals(appId)){
                toRemove = server;
            }
        }

        if (toRemove != null ){
            servers.remove(toRemove);
            overrideServers(servers);
        }
    }

    public List<ParseServerConfig> getServers(){
        AsonArray<ParseServerConfig> servers = getServersAson();
        return servers.deserializeList(ParseServerConfig.class);
    }

    private AsonArray<ParseServerConfig> getServersAson(){
        SharedPreferences pref = context.getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE);
        String input = pref.getString(PREF_SERVERS_KEY, "[]");
        return new AsonArray<>(input);
    }

    private void overrideServers(List<ParseServerConfig> servers){
        AsonArray<ParseServerConfig> asonArray = new AsonArray<>();
        for (ParseServerConfig s : servers){
            asonArray.add(s);
        }

        overrideServersAson(asonArray);
    }


    private void overrideServersAson(AsonArray<ParseServerConfig> servers){
        SharedPreferences pref = context.getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(PREF_SERVERS_KEY, servers.toString());
        editor.commit();
    }


}
