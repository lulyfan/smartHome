package com.ut.smartHome.plugin;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.ut.data.dataSource.remote.http.WebSocketHelper;
import com.ut.smartHome.plugin.handler.BasePageHandler;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

public class BasePlugin extends CordovaPlugin{
    private Map<String, BasePageHandler> map = new HashMap<>();
    private static final String ACTION_TOAST = "showToast";

    protected void addPluginHandler(BasePageHandler handler) {

        String[] actions = handler.getActions();
        for (String action : actions) {
            map.put(action, handler);
        }
    }

    public Context getContext() {
        return cordova.getContext();
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

        if (ACTION_TOAST.equals(action)) {

            String text = args.getString(0);
            cordova.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(cordova.getContext(), text, Toast.LENGTH_SHORT).show();
                    callbackContext.success();
                }
            });
            return true;
        }


        if (!map.containsKey(action)) {
            return false;
        }

        BasePageHandler handler = map.get(action);
        return handler.execute(action, args, callbackContext);
    }

    public void callJs(String method, String para) {
        cordova.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                webView.loadUrl("javascript:" + method + "(" + para + ")");
            }
        });
    }
}
