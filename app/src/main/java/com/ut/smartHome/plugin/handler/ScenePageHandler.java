package com.ut.smartHome.plugin.handler;

import com.ut.data.dataSource.remote.http.Result;
import com.ut.data.dataSource.remote.http.data.SceneInfo;
import com.ut.data.dataSource.remote.http.data.SceneOperate;
import com.ut.smartHome.plugin.BasePlugin;
import com.ut.smartHome.plugin.SmartHomeContext;

import org.apache.cordova.CallbackContext;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScenePageHandler extends BasePageHandler {

    private static final String ACTION_GET_SCENELIST    = "getSceneList";
    private static final String ACTION_EXECUTE_SCENE    = "executeScene";
    private static final String ACTION_DELETE_SCENE     = "deleteScene";
    private static final String ACTION_GET_SCENE_BY_ID  = "getSceneById";
    private static final String ACTION_ADD_SCENE        = "addScene";
    private static final String ACTION_UPDATE_SCENE     = "updateScene";

    public ScenePageHandler(SmartHomeContext context, BasePlugin plugin) {
        super(context, plugin);
    }

    @Override
    public String[] getActions() {

        return new String[]{
                ACTION_GET_SCENELIST,
                ACTION_EXECUTE_SCENE,
                ACTION_DELETE_SCENE,
                ACTION_GET_SCENE_BY_ID,
                ACTION_ADD_SCENE,
                ACTION_UPDATE_SCENE};
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

        switch (action) {
            case ACTION_GET_SCENELIST:
                if (args.length() != 0) {
                    getSceneListFromDB(callbackContext);
                } else {
                    getSceneListFromNet(callbackContext);
                }
                return true;

            case ACTION_EXECUTE_SCENE:
                executeScene(args.getInt(0), callbackContext);
                return true;

            case ACTION_DELETE_SCENE:
                deleteScene(args.getInt(0), callbackContext);
                return true;

            case ACTION_GET_SCENE_BY_ID:
                getSceneById(args.getInt(0), callbackContext);
                return true;

            case ACTION_ADD_SCENE:
                addScene(args.getString(0), args.getString(1), args.getString(2),
                        args.getString(3), callbackContext);
                return true;

            case ACTION_UPDATE_SCENE:
                updateScene(args.getInt(0), args.getString(1), args.getString(2),
                        args.getString(3), args.getString(4), callbackContext);
                return true;

        }
        return false;
    }

    private void getSceneListFromDB(CallbackContext callbackContext) {

        if (!smartHomeContext.isHasCurrentHost()) {
            callbackContext.error("当前主机未设置");
        }

        runOnThreadPool(new Runnable() {
            @Override
            public void run() {
                List<SceneInfo> sceneInfos = localDataSource.getScenesByHostId(smartHomeContext.getCurrentHostId());
                for (SceneInfo sceneInfo : sceneInfos) {
                    sceneInfo.setWeek(sceneInfo.getWeeks());
                }
                callbackContext.success(toJsonStr(sceneInfos));

                getSceneListFromNet();
            }
        });
    }

    private void updateScene(int sceneId, String name, String time, String weeks,
                             String deviceData, CallbackContext callbackContext) {
        httpDataSource.updateScene(sceneId, name, null, null, time, weeks, deviceData, new Callback<Result>() {
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

    private void addScene(String name, String time, String weeks, String deviceData, CallbackContext callbackContext) {

        if ("".equals(time.trim()) || "".equals(weeks.trim())) {
            time = null;
            weeks = null;
        }

        httpDataSource.addScene(name, null, "www", time, weeks, deviceData, new Callback<Result>() {
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

    private void getSceneById(int sceneId, CallbackContext callbackContext) {
        httpDataSource.getSceneOperate(sceneId, new Callback<Result<SceneOperate>>() {
            @Override
            public void onResponse(Call<Result<SceneOperate>> call, Response<Result<SceneOperate>> response) {
                Result<SceneOperate> result = response.body();

                if (result == null) {
                    callbackContext.error("null");
                    return;
                }

                if (result.isSuccess()) {

                    SceneOperate sceneOperate = result.data;
                    sceneOperate.setId(sceneId);
                    sceneOperate.setWeek(sceneOperate.getWeeks());

                    String str = toJsonStr(sceneOperate);
                    callbackContext.success(str);
                } else {
                    callbackContext.error(result.msg);
                }
            }

            @Override
            public void onFailure(Call<Result<SceneOperate>> call, Throwable t) {
                callbackContext.error(t.getMessage());
            }
        });
    }

    private void deleteScene(int sceneId, CallbackContext callbackContext) {
        httpDataSource.deleteScene(sceneId, new Callback<Result>() {
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

    private void executeScene(int sceneId, CallbackContext callbackContext) {
        showLoadDialog("请稍候...");
        httpDataSource.executeScene(sceneId, new Callback<Result>() {
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

                dismissLoadDialog();
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                callbackContext.error(t.getMessage());
                dismissLoadDialog();
            }
        });
    }

    private void getSceneListFromNet(CallbackContext callbackContext) {

        if (!smartHomeContext.isHasCurrentHost()) {
            callbackContext.error("当前主机未设置");
        }

        httpDataSource.getUserScenes(smartHomeContext.getCurrentHostMac(), new Callback<Result<List<SceneInfo>>>() {
            @Override
            public void onResponse(Call<Result<List<SceneInfo>>> call, Response<Result<List<SceneInfo>>> response) {
                Result<List<SceneInfo>> result = response.body();

                if (result == null) {
                    callbackContext.error("null");
                    return;
                }

                if (result.isSuccess()) {

                    runOnThreadPool(new Runnable() {
                        @Override
                        public void run() {
                            List<SceneInfo> sceneInfos = result.data;
                            for (SceneInfo sceneInfo : sceneInfos) {
                                sceneInfo.setWeek(sceneInfo.getWeeks());

                                if (localDataSource.isInSceneShortcut(sceneInfo.getId())) {
                                    sceneInfo.setSelect(true);
                                }
                            }

                            String str = toJsonStr(sceneInfos);
                            callbackContext.success(str);
                        }
                    });

                    updateSceneTable(result.data);

                } else {
                    callbackContext.error(result.msg);
                }
            }

            @Override
            public void onFailure(Call<Result<List<SceneInfo>>> call, Throwable t) {
                callbackContext.error(t.getMessage());
            }
        });
    }

    private void getSceneListFromNet() {

        if (!smartHomeContext.isHasCurrentHost()) {
            return;
        }

        httpDataSource.getUserScenes(smartHomeContext.getCurrentHostMac(), new Callback<Result<List<SceneInfo>>>() {
            @Override
            public void onResponse(Call<Result<List<SceneInfo>>> call, Response<Result<List<SceneInfo>>> response) {
                Result<List<SceneInfo>> result = response.body();

                if (result == null) {
                    return;
                }

                if (result.isSuccess()) {

                    for (SceneInfo sceneInfo : result.data) {
                        sceneInfo.setWeek(sceneInfo.getWeeks());
                    }

                    String str = toJsonStr(result.data);
                    pushSceneData(str);
                    updateSceneTable(result.data);
                }
            }

            @Override
            public void onFailure(Call<Result<List<SceneInfo>>> call, Throwable t) {
            }
        });
    }

    private void updateSceneTable(List<SceneInfo> newSceneInfos) {
        runOnThreadPool(new Runnable() {
            @Override
            public void run() {
                List<SceneInfo> oldScenes = localDataSource.getScenesByHostId(smartHomeContext.getCurrentHostId());
                localDataSource.deleteScenes(oldScenes);
                localDataSource.saveScenes(newSceneInfos);
            }
        });

    }

    private void pushSceneData(String data) {
        plugin.callJs("getSceneSuccessFromDb", data);
    }
}
