package com.ut.smartHome.plugin.handler;

import android.util.Log;

import com.ut.data.dataSource.remote.http.Result;
import com.ut.data.dataSource.remote.http.data.DeviceTypeStatus;
import com.ut.data.dataSource.remote.http.data.PushShortcut;
import com.ut.data.dataSource.remote.http.data.Shortcut;
import com.ut.data.dataSource.remote.http.data.pushData.PushResult;
import com.ut.smartHome.plugin.BasePlugin;
import com.ut.smartHome.plugin.SmartHomeContext;

import org.apache.cordova.CallbackContext;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IndexPageHandler extends BasePageHandler{

    private static final String ACTION_GET_FACORITE_LIST = "getFavoriteList";
    private static final String ACTION_ADD_FAVORITE_DATAS = "addFavoriteDatas";
    private static final String ACTION_DETELE_FAVORITE = "deleteFavorite";
    private static final String ACTION_GET_DEVICES_STATUS_COUNT = "getDeviceStatusCount";

    private static final String TAG_INDEX = "index";

    public IndexPageHandler(SmartHomeContext context, BasePlugin plugin) {
        super(context, plugin);
    }

    @Override
    public String[] getActions() {
        return new String[]{ACTION_GET_FACORITE_LIST, ACTION_ADD_FAVORITE_DATAS, ACTION_DETELE_FAVORITE,
                ACTION_GET_DEVICES_STATUS_COUNT};
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        switch (action) {
            case ACTION_GET_FACORITE_LIST:
                getFavoriteList(callbackContext);
                return true;

            case ACTION_ADD_FAVORITE_DATAS:
                addFavoriteDatas(args.getJSONArray(0), args.getJSONArray(1), callbackContext);
                return true;

            case ACTION_DETELE_FAVORITE:
                deleteFavorite(args.getInt(0), callbackContext);
                return true;

            case ACTION_GET_DEVICES_STATUS_COUNT:
                getDeviceStatusCount(callbackContext);
                return true;
        }
        return false;
    }

    private void getDeviceStatusCount(CallbackContext callbackContext) {
        httpDataSource.getDeviceTypeStatus(smartHomeContext.getCurrentHostMac(), new Callback<Result<List<DeviceTypeStatus>>>() {
            @Override
            public void onResponse(Call<Result<List<DeviceTypeStatus>>> call, Response<Result<List<DeviceTypeStatus>>> response) {
                Result<List<DeviceTypeStatus>> result = response.body();
                if (result == null) {
                    callbackContext.error("");
                    return;
                }

                if (result.isSuccess()) {
                    callbackContext.success(toJsonStr(result.data));
                } else {
                    callbackContext.error(result.msg);
                }
            }

            @Override
            public void onFailure(Call<Result<List<DeviceTypeStatus>>> call, Throwable t) {
                callbackContext.error(t.getMessage());
            }
        });
    }

    private void deleteFavorite(int id, CallbackContext callbackContext) {
        Log.i(TAG_INDEX, "delete");
        httpDataSource.deleteShortcut(id, new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                Result result = response.body();
                if (result == null) {
                    Log.i(TAG_INDEX, "null");
                    callbackContext.error("");
                    return;
                }

                if (result.isSuccess()) {
                    callbackContext.success();

                    runOnThreadPool(new Runnable() {
                        @Override
                        public void run() {
                            Shortcut shortcut = localDataSource.getShortcutById(id);
                            localDataSource.deleteShortcut(shortcut);
                        }
                    });
                    Log.i(TAG_INDEX, "success");
                } else {
                    callbackContext.error(result.msg);
                    Log.i(TAG_INDEX, result.msg);
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                callbackContext.error(t.getMessage());
                Log.i(TAG_INDEX, t.getMessage());
            }
        });
    }

    private void addFavoriteDatas(JSONArray sceneIds, JSONArray deviceIds, CallbackContext callbackContext) {

        String sSceneIds = "";
        String sDeviceIds = "";

        if (sceneIds.length() > 0) {
            for (int i = 0; i < sceneIds.length(); i++) {
                try {
                    sSceneIds += sceneIds.getInt(i) + ",";
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            sSceneIds = sSceneIds.substring(0, sSceneIds.length() - 1);
        }

        if (deviceIds.length() > 0) {
            for (int i = 0; i < deviceIds.length(); i++) {
                try {
                    sDeviceIds += deviceIds.getInt(i) + ",";
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            sDeviceIds = sDeviceIds.substring(0, sDeviceIds.length() - 1);
        }

        if ("".equals(sSceneIds) && "".equals(sDeviceIds)) {
            callbackContext.success();
            return;
        }

        httpDataSource.addBatchShortcutForAllType(sDeviceIds, sSceneIds, smartHomeContext.getCurrentHostMac(), new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                Result result = response.body();
                if (result == null) {
                    callbackContext.error("");
                    return;
                }

                if (result.isSuccess()) {
                    PushResult pushResult = new PushResult();
                    pushResult.code = 200;
                    callbackContext.success(toJsonStr(pushResult));
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

    private void getFavoriteList(CallbackContext callbackContext) {
        httpDataSource.listShortcut(smartHomeContext.getCurrentHostMac(), new Callback<Result<List<Shortcut>>>() {
            @Override
            public void onResponse(Call<Result<List<Shortcut>>> call, Response<Result<List<Shortcut>>> response) {
                Result<List<Shortcut>> result = response.body();
                if (result == null) {
                    callbackContext.error("");
                    return;
                }

                if (result.isSuccess()) {

                    List<Shortcut> sceneData = new ArrayList<>();
                    List<Shortcut> deviceData = new ArrayList<>();

                    for (Shortcut shortcut : result.data) {
                        if ("device".equals(shortcut.getType())) {
                            deviceData.add(shortcut);
                        } else if ("scene".equals(shortcut.getType())) {
                            sceneData.add(shortcut);
                        }
                    }
                    PushShortcut pushShortcut = new PushShortcut();
                    pushShortcut.deviceData = deviceData;
                    pushShortcut.sceneData = sceneData;

                    callbackContext.success(toJsonStr(pushShortcut));

                    runOnThreadPool(new Runnable() {
                        @Override
                        public void run() {
                            List<Shortcut> old = localDataSource.getShortcutByHostId(smartHomeContext.getCurrentHostId());
                            localDataSource.deleteShortcuts(old);
                            localDataSource.saveShortcuts(result.data);
                        }
                    });
                } else {
                    callbackContext.error(result.msg);
                }
            }

            @Override
            public void onFailure(Call<Result<List<Shortcut>>> call, Throwable t) {
                callbackContext.error(t.getMessage());
            }
        });
    }
}
