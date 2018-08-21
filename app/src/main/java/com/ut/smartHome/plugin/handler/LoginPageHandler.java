package com.ut.smartHome.plugin.handler;

import android.util.Log;

import com.google.gson.Gson;
import com.ut.data.dataSource.remote.http.Result;
import com.ut.data.dataSource.remote.http.data.HostInfo;
import com.ut.smartHome.plugin.BasePlugin;
import com.ut.smartHome.plugin.SmartHomeContext;

import org.apache.cordova.CallbackContext;
import org.json.JSONArray;
import org.json.JSONException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginPageHandler extends BasePageHandler{

    private static final String ACTION_LOGIN = "loginApp";
    private static final String ACTION_GET_CURRENT_HOST = "getCurrentHost";
    private static final String ACTION_GET_VERIFY = "getVerify";
    private static final String ACTION_REGISTER = "register";
    private static final String ACTION_FIND_PASSWORD = "findPassword";

    public LoginPageHandler(SmartHomeContext context, BasePlugin plugin) {
        super(context, plugin);
    }

    @Override
    public String[] getActions() {
        return new String[]{ACTION_LOGIN, ACTION_GET_CURRENT_HOST, ACTION_GET_VERIFY, ACTION_REGISTER, ACTION_FIND_PASSWORD};
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

        switch (action) {
            case ACTION_LOGIN:
                login(args.getString(0), args.getString(1), callbackContext);
                return true;

            case ACTION_GET_CURRENT_HOST:

                getCurrentHost(callbackContext);
                return true;

            case ACTION_GET_VERIFY:

                getVerify(args.getString(0), callbackContext);
                return true;

            case ACTION_REGISTER:

                register(args.getString(0), args.getString(1), args.getString(2), callbackContext);
                return true;

            case ACTION_FIND_PASSWORD:

                findPassword(args.getString(0), args.getString(1), args.getString(2), callbackContext);
                return true;
        }
        return false;
    }

    private void findPassword(String phone, String password, String verify, CallbackContext callbackContext) {
        httpDataSource.findPassword(phone, password, verify, new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                Result result = response.body();

                if (result == null) {
                    callbackContext.error("null");
                    return;
                }

                if (result.isSuccess()) {
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

    private void register(String phone, String password, String verify, CallbackContext callbackContext) {
        httpDataSource.register(phone, password, verify, new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                Result result = response.body();

                if (result == null) {
                    callbackContext.error("null");
                    return;
                }

                if (result.isSuccess()) {
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

    private void getVerify(String phone, CallbackContext callbackContext) {
        httpDataSource.getVerif(phone, new Callback<Result<String>>() {
            @Override
            public void onResponse(Call<Result<String>> call, Response<Result<String>> response) {
                Result<String> result = response.body();

                if (result == null) {
                    callbackContext.error("null");
                    return;
                }

                if (result.isSuccess()) {
                    callbackContext.success();
                } else {
                    callbackContext.error(result.msg);
                }
            }

            @Override
            public void onFailure(Call<Result<String>> call, Throwable t) {
                callbackContext.error(t.getMessage());
            }
        });
    }

    private void login(String account, String password, CallbackContext callbackContext) {
        showLoadDialog("请稍候...");

        httpDataSource.login(account, password, new Callback<Result<Integer>>() {
            @Override
            public void onResponse(Call<Result<Integer>> call, Response<Result<Integer>> response) {

                Result<Integer> result = response.body();

                if (result == null) {
                    callbackContext.error("null");
                    return;
                }

                if (result.isSuccess()) {
                    int userId = result.data;
                    SmartHomeContext.saveLoginInfo(plugin.getContext(), account, password);
                    smartHomeContext.setUserId(userId);
                    httpDataSource.sendUserId(userId);
                    callbackContext.success(result.data + "");

                } else {
                    callbackContext.error(result.msg);
                }

                dismissLoadDialog();
            }

            @Override
            public void onFailure(Call<Result<Integer>> call, Throwable t) {
                callbackContext.error(t.getMessage());
                dismissLoadDialog();
            }
        });
    }

    private void getCurrentHost(CallbackContext callbackContext) {
        httpDataSource.getCurrentHost(new Callback<Result<HostInfo>>() {
            @Override
            public void onResponse(Call<Result<HostInfo>> call, Response<Result<HostInfo>> response) {
                Result<HostInfo> result = response.body();

                if (result == null) {
                    callbackContext.error("null");
                    return;
                }

                if (result.isSuccess()) {
                    HostInfo currentHost = result.data;
                    smartHomeContext.setCurrentHostMac(currentHost.getMac());
                    smartHomeContext.setCurrentHostId(currentHost.getId());
                    smartHomeContext.setHasCurrentHost(true);

                    Gson gson = new Gson();
                    callbackContext.success(gson.toJson(currentHost));
                } else {
                    smartHomeContext.setHasCurrentHost(false);
                    callbackContext.error(result.msg);
                }
            }

            @Override
            public void onFailure(Call<Result<HostInfo>> call, Throwable t) {
                smartHomeContext.setHasCurrentHost(false);
                callbackContext.error(t.getMessage());
            }
        });
    }
}
