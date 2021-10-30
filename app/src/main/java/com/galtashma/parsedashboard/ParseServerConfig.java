package com.galtashma.parsedashboard;

/**
 * Created by gal on 3/16/18.
 */

public class ParseServerConfig {

    public String appName;
    public String appId;
    public String masterKey;
    public String serverUrl;

    public ParseServerConfig(){}

    public ParseServerConfig(String appName, String appId, String masterKey, String appUrl) {
        this.appName = appName;
        this.appId = appId;
        this.masterKey = masterKey;
        this.serverUrl = appUrl;
    }
}
