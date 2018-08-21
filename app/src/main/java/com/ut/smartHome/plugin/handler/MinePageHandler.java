package com.ut.smartHome.plugin.handler;

import com.google.gson.Gson;
import com.ut.data.dataSource.remote.http.Result;
import com.ut.data.dataSource.remote.http.data.UserInfo;
import com.ut.smartHome.plugin.BasePlugin;
import com.ut.smartHome.plugin.SmartHomeContext;

import org.apache.cordova.CallbackContext;
import org.json.JSONArray;
import org.json.JSONException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MinePageHandler extends BasePageHandler{

    private static final String ACTION_GET_USER_INFO = "getUserInfo";
    private static final String ACTION_LOGOUT = "logout";

    public MinePageHandler(SmartHomeContext context, BasePlugin plugin) {
        super(context, plugin);
    }

    @Override
    public String[] getActions() {
        return new String[]{ACTION_GET_USER_INFO, ACTION_LOGOUT};
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) {

        if (ACTION_GET_USER_INFO.equals(action)) {
            getUserInfo(callbackContext);
            return true;
        } else if (ACTION_LOGOUT.equals(action)) {
            logout(callbackContext);
            return true;
        }
        return false;
    }

    private void logout(CallbackContext callbackContext) {

        httpDataSource.logout(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                Result result = response.body();

                if (result == null) {
                    callbackContext.error("null");
                    return;
                }

                if (result.isSuccess()) {
                    SmartHomeContext.clearLoginInfo(plugin.getContext());
                    callbackContext.success();
                } else {
                    callbackContext.error(result.msg);
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                callbackContext.error(t.getMessage());
            }
        });
    }

    private void getUserInfo(CallbackContext callbackContext) {

        httpDataSource.getUserInfo(new Callback<Result<UserInfo>>() {
            @Override
            public void onResponse(Call<Result<UserInfo>> call, Response<Result<UserInfo>> response) {

                Result<UserInfo> result = response.body();

                if (result == null) {
                    callbackContext.error("null");
                    return;
                }

                if (result.isSuccess()) {
                    Gson gson = new Gson();
                    String str = gson.toJson(result.data);
                    callbackContext.success(str);
                } else {
                    callbackContext.error(result.msg);
                }
            }

            @Override
            public void onFailure(Call<Result<UserInfo>> call, Throwable t) {
                callbackContext.error(t.getMessage());
            }
        });
    }
}
