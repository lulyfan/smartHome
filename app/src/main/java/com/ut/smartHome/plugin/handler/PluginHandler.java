package com.ut.smartHome.plugin.handler;

import org.apache.cordova.CallbackContext;
import org.json.JSONArray;
import org.json.JSONException;

public interface PluginHandler {
    String[] getActions();
    boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException;
}
