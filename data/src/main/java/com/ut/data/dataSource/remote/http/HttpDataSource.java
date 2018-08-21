package com.ut.data.dataSource.remote.http;

import android.util.Base64;
import android.util.Log;

import com.ut.data.dataSource.remote.http.data.AirConditionerControl;
import com.ut.data.dataSource.remote.http.data.AppInfo;
import com.ut.data.dataSource.remote.http.data.AuthKey;
import com.ut.data.dataSource.remote.http.data.AuthKeyOperate;
import com.ut.data.dataSource.remote.http.data.DeviceInfo;
import com.ut.data.dataSource.remote.http.data.DeviceOperateLog;
import com.ut.data.dataSource.remote.http.data.DeviceTypeStatus;
import com.ut.data.dataSource.remote.http.data.FAQ;
import com.ut.data.dataSource.remote.http.data.FAQType;
import com.ut.data.dataSource.remote.http.data.HostInfo;
import com.ut.data.dataSource.remote.http.data.LockConfig;
import com.ut.data.dataSource.remote.http.data.LockOperateLog;
import com.ut.data.dataSource.remote.http.data.RegionInfo;
import com.ut.data.dataSource.remote.http.data.SceneInfo;
import com.ut.data.dataSource.remote.http.data.SceneOperate;
import com.ut.data.dataSource.remote.http.data.Shortcut;
import com.ut.data.dataSource.remote.http.data.UserInfo;
import com.ut.data.util.DES;

import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by huangkaifan on 2018/6/15.
 */

public class HttpDataSource {
    private static final String BASE_URL = "http://39.108.208.108:8001";
    private HttpService httpService;
    private static HttpDataSource INSTANCE;
    private WebSocketHelper webSocketHelper;

    private HttpDataSource() {

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new SessionInterceptor())
                .build();

        webSocketHelper = new WebSocketHelper(client);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        httpService = retrofit.create(HttpService.class);
    }

    public synchronized static HttpDataSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new HttpDataSource();
        }
        return INSTANCE;
    }

    public void sendUserId(int userId) {
        webSocketHelper.sendUserId(userId);
    }

    public void sendUserId() {
        webSocketHelper.sendUserId();
    }

    public void setPushDataListener(WebSocketHelper.PushDataListener pushDataListener) {
        webSocketHelper.setPushDataListener(pushDataListener);
    }

    /*-------------------------------------------------我的页-----------------------------------------------------*/

    public void getUserInfo(Callback<Result<UserInfo>> callback) {
        httpService.getUserInfo().enqueue(callback);
    }

    public void resetPassword(String oldPassword, String newPassword, Callback<Result> callback) {
        httpService.resetPassword(oldPassword, newPassword).enqueue(callback);
    }

    public void logout(Callback<Result> callback) {
        httpService.logout().enqueue(callback);
    }

    public void isLogin(Callback<Result<Boolean>> callback) {
        httpService.isLogin().enqueue(callback);
    }

    public void getUserId(Callback<Result<Double>> callback) {
        httpService.getUserId().enqueue(callback);
    }

    public void getHostList(Callback<Result<List<HostInfo>>> callback) {
        httpService.getHostList().enqueue(callback);
    }

    public void loginHost(String mac, String password, Callback<Result> callback) {
        httpService.loginHost(mac, password).enqueue(callback);
    }

    public void logoutHost(Callback<Result> callback) {
        httpService.logoutHost().enqueue(callback);
    }

    public void setCurrentHost(double hostId, Callback<Result> callback) {
        httpService.setCurrentHost(hostId).enqueue(callback);
    }

    public void setCurrentHost(String mac, Callback<Result> callback) {
        httpService.setCurrentHost(mac).enqueue(callback);
    }

    public void getCurrentHost(Callback<Result<HostInfo>> callback) {
        httpService.getCurrentHost().enqueue(callback);
    }

    public void bindHost(String mac, Callback<Result> callback) {
        httpService.bindHost(mac).enqueue(callback);
    }

    public void unbindHost(String mac, Callback<Result> callback) {
        httpService.unbindHost(mac).enqueue(callback);
    }

    public void updateHostName(String mac, String name, Callback<Result> callback) {
        httpService.updateHostName(mac, name).enqueue(callback);
    }

    public void updateHostPassword(String oldPassword, String newPassword, String mac, Callback<Result> callback) {
        httpService.updateHostPassword(oldPassword, newPassword, mac).enqueue(callback);
    }

    public void upgradeCfg(String mac, Callback<Result> callback) {
        httpService.upgradeCfg(mac).enqueue(callback);
    }

    public void loginHostToken(String token, Callback<Result> callback) {
        httpService.loginHostToken(token).enqueue(callback);
    }

    public void getVersionInfo(int typeId, Callback<Result<AppInfo>> callback) {
        httpService.getVersionInfo(typeId).enqueue(callback);
    }

    public void insertOpinionFeedback(String title, String text, Callback<Result> callback) {
        httpService.insertOpinionFeedback(title, text).enqueue(callback);
    }

    public void insertBugFeedback(String title, String text, String img, Callback<Result> callback) {
        httpService.insertBugFeedback(title, text, img).enqueue(callback);
    }

    public void uploadimg(RequestBody img, Callback<Result<String>> callback) {
        httpService.uploadimg(img).enqueue(callback);
    }

    public void fAQList(Callback<Result<List<FAQ>>> callback) {
        httpService.fAQList().enqueue(callback);
    }

    public void fAQTypeList(Callback<Result<List<FAQType>>> callback) {
        httpService.fAQTypeList().enqueue(callback);
    }

    /* --------------------------------------------------登录页--------------------------------------------------------*/

    public void login(String account, String password, Callback<Result<Integer>> callback) {
        SessionInterceptor.createUUID();
        String entryPW = Base64.encodeToString(DES.encrypt(password.getBytes()), Base64.DEFAULT);
        httpService.login(account, entryPW).enqueue(callback);
    }

    public void register(String account, String password, String verif, Callback<Result> callback) {
        String entryPW = Base64.encodeToString(DES.encrypt(password.getBytes()), Base64.DEFAULT);
        httpService.register(account, entryPW, verif).enqueue(callback);
    }

    public void findPassword(String account, String password, String verif, Callback<Result> callback) {
        String entryPW = Base64.encodeToString(DES.encrypt(password.getBytes()), Base64.DEFAULT);
        httpService.findPassword(account, entryPW, verif).enqueue(callback);
    }

    public void getVerif(String mobile, Callback<Result<String>> callback) {
        httpService.getVerif(mobile).enqueue(callback);
    }


    /*-----------------------------------------------------场景页-----------------------------------------------------*/

    public void getUserScenes(int hostId, Callback<Result<List<SceneInfo>>> callback) {
        httpService.getUserScenes(hostId).enqueue(callback);
    }

    public void getUserScenes(String mac, Callback<Result<List<SceneInfo>>> callback) {
        httpService.getUserScenes(mac).enqueue(callback);
    }

    public void getUserScenes(Callback<Result<List<SceneInfo>>> callback) {
        httpService.getUserScenes().enqueue(callback);
    }

    public void getHostScenes(int hostId, Callback<Result<List<SceneInfo>>> callback) {
        httpService.getHostScenes(hostId).enqueue(callback);
    }

    public void getHostScenes(String mac, Callback<Result<List<SceneInfo>>> callback) {
        httpService.getHostScenes(mac).enqueue(callback);
    }

    public void getDevices(int hostId, Callback<Result<List<DeviceInfo>>> callback) {
        httpService.getDevices(hostId).enqueue(callback);
    }

    public void getDevices(String mac, Callback<Result<List<DeviceInfo>>> callback) {
        httpService.getDevices(mac).enqueue(callback);
    }

    public void addDevice(String name, byte[] img, String imageUrl, String time, String weeks, String deviceData, Callback<Result> callback) {
        httpService.addDevice(name, img, imageUrl, time, weeks, deviceData).enqueue(callback);
    }

    public void executeScene(int sceneId, Callback<Result> callback) {
        httpService.executeScene(sceneId).enqueue(callback);
    }

    public void getSceneOperate(int sceneId, Callback<Result<SceneOperate>> callback) {
        httpService.getSceneOperate(sceneId).enqueue(callback);
    }

    public void addScene(String name, byte[] img, String imageUrl, String time, String weeks, String deviceData, Callback<Result> callback) {
        httpService.addScene(name, img, imageUrl, time, weeks, deviceData).enqueue(callback);
    }

    public void updateScene(int sceneId,String name, byte[] img, String imageUrl, String time, String weeks, String deviceData,
                            Callback<Result> callback) {
        httpService.updateScene(sceneId, name, img, imageUrl, time, weeks, deviceData).enqueue(callback);
    }

    public void deleteScene(int sceneId, Callback<Result> callback) {
        httpService.deleteScene(sceneId).enqueue(callback);
    }

    /*--------------------------------------------------设备页----------------------------------------------------*/

    public void updateDeviceName(String name, String address, String mac, Callback<Result> callback) {
        httpService.updateDeviceName(name, address, mac).enqueue(callback);
    }

    public void getDeviceCode(int deviceTypeCode, Callback<ResponseBody> callback) {
        httpService.getDeviceCode(deviceTypeCode).enqueue(callback);
    }

    public void getRegions(int hostId, Callback<Result<List<RegionInfo>>> callback) {
        httpService.getRegions(hostId).enqueue(callback);
    }

    public void getRegions(String mac, Callback<Result<List<RegionInfo>>> callback) {
        httpService.getRegions(mac).enqueue(callback);
    }

    public void getDeviceByRegion(int ordinal, Callback<Result<List<DeviceInfo>>> callback) {
        httpService.getDeviceByRegion(ordinal).enqueue(callback);
    }

    public void addLog(int hostId, String address, String statusCode, int value, Callback<Result> callback) {
        httpService.addLog(hostId, address, statusCode, value).enqueue(callback);
    }

    public void addLog(String mac, String address, String statusCode, int value, Callback<Result> callback) {
        httpService.addLog(mac, address, statusCode, value).enqueue(callback);
    }

    public void getLogByUser(Callback<Result<DeviceOperateLog>> callback) {
        httpService.getLogByUser().enqueue(callback);
    }

    public void getLogByDevice(int hostId, String address, Callback<Result<DeviceOperateLog>> callback) {
        httpService.getLogByDevice(hostId, address).enqueue(callback);
    }

    public void getLogByDevice(String mac, String address, Callback<Result<DeviceOperateLog>> callback) {
        httpService.getLogByDevice(mac, address).enqueue(callback);
    }

    public void saveLockConfig(int hostId, String blueMac, String address, String type,
                               String name, String value, int auth, Callback<Result> callback) {
        httpService.saveLockConfig(hostId, blueMac, address, type, name, value, auth).enqueue(callback);
    }

    public void saveLockConfig(String mac, String blueMac, String address, String type,
                               String name, String value, int auth, Callback<Result> callback) {
        httpService.saveLockConfig(mac, blueMac, address, type, name, value, auth).enqueue(callback);
    }

    public void batchSaveLockConfig(int hostId, String blueMac, String address, String type,
                                    String names, String values, String auths, String incode, Callback<Result> callback) {
        httpService.batchSaveLockConfig(hostId, blueMac, address, type, names, values, auths, incode).enqueue(callback);
    }

    public void batchSaveLockConfig(String mac, String blueMac, String address, String type,
                                    String names, String values, String auths, String incode, Callback<Result> callback) {
        httpService.batchSaveLockConfig(mac, blueMac, address, type, names, values, auths, incode).enqueue(callback);
    }

    public void getLockConfig(int hostId, String blueMac, String address, String type, Callback<Result<List<LockConfig>>> callback) {
        httpService.getLockConfig(hostId, blueMac, address, type).enqueue(callback);
    }

    public void getLockConfig(String mac, String blueMac, String address, String type, Callback<Result<List<LockConfig>>> callback) {
        httpService.getLockConfig(mac, blueMac, address, type).enqueue(callback);
    }

    public void addBlueLockLog(int hostId, String blueMac, String address, String date, Callback<Result> callback) {
        httpService.addBlueLockLog(hostId, blueMac, address, date).enqueue(callback);
    }

    public void addBlueLockLog(String mac, String blueMac, String address, String date, Callback<Result> callback) {
        httpService.addBlueLockLog(mac, blueMac, address, date).enqueue(callback);
    }

    public void getLockLog(int hostId, String blueMac, String address, Callback<Result<List<LockOperateLog>>> callback) {
        httpService.getLockLog(hostId, blueMac, address).enqueue(callback);
    }

    public void getLockLog(String mac, String blueMac, String address, Callback<Result<List<LockOperateLog>>> callback) {
        httpService.getLockLog(mac, blueMac, address).enqueue(callback);
    }

    public void authKey(int oper, int id, int type, int keyId, String password, long start, long end, int times,
                        String address, int hostId, int authCode, Callback<Result> callback) {
        httpService.authKey(oper, id, type, keyId, password, start, end, times, address, hostId, authCode).enqueue(callback);
    }

    public void authKey(int oper, int id, int type, int keyId, String password, long start, long end, int times,
                        String address, String mac, int authCode, Callback<Result> callback) {
        httpService.authKey(oper, id, type, keyId, password, start, end, times, address, mac, authCode).enqueue(callback);
    }

    public void addAuthKey(int type, int keyId, String password, long start, long end, int times, String address,
                           int hostId, Callback<Result> callback) {
        authKey(AuthKeyOperate.ADD, 0, type, keyId, password, start, end, times, address, hostId, 0, callback);
    }

    public void addAuthKey(int type, int keyId, String password, long start, long end, int times, String address,
                           String mac, Callback<Result> callback) {
        authKey(AuthKeyOperate.ADD, 0, type, keyId, password, start, end, times, address, mac, 0, callback);
    }

    public void deleteAuthKey(int id, int hostId, String address, int authCode, Callback<Result> callback) {
        authKey(AuthKeyOperate.DELETE, id, 0, 0, "", 0, 0, 0, address, hostId, authCode, callback);
    }

    public void updateAuthKey(int id, int type, int keyId, String password,
                              long start, long end, int times, int authCode, String address, String mac, Callback<Result> callback){
        authKey(AuthKeyOperate.UPDATE, id, type, keyId, password, start, end, times, address, mac, authCode, callback);
    }

    public void listAuthKey(String address, String mac, Callback<Result<List<AuthKey>>> callback) {
        httpService.listAuthKey(AuthKeyOperate.QUERY, 0, 0, 0, "", 0, 0, 0, address, mac, 0).enqueue(callback);
    }

    public void sendAuthPassword(String phone, String password, Callback<Result> callback) {
        httpService.sendAuthPassword(phone, password).enqueue(callback);
    }

    public void sCurtainControl(int oper, String address, int status, int hostId, Callback<Result> callback) {
        httpService.sCurtainControl(oper, address, status, hostId).enqueue(callback);
    }

    public void sCurtainControl(int oper, String address, int status, String mac, Callback<Result> callback) {
        httpService.sCurtainControl(oper, address, status, mac).enqueue(callback);
    }

    public void dCurtainControl(int oper, int type, String address, int in, int out, int hostId, Callback<Result> callback) {
        httpService.dCurtainControl(oper, type, address, in, out, hostId).enqueue(callback);
    }

    public void dCurtainControl(int oper, int type, String address, int in, int out, String mac, Callback<Result> callback) {
        httpService.dCurtainControl(oper, type, address, in, out, mac).enqueue(callback);
    }

    public void lightControl(int oper, String address, int hostId, Callback<Result> callback) {
        httpService.lightControl(oper, address, hostId).enqueue(callback);
    }

    public void lightControl(int oper, String address, String mac, Callback<Result> callback) {
        httpService.lightControl(oper, address, mac).enqueue(callback);
    }

    public void airConditionerControl(int tem, int status, int mode, int wind, int swingLR, int swingUD, int sleep,
                                      String mac, String address, Callback<Result> callback) {
        httpService.airConditionerControl(tem, status, mode, wind, swingLR, swingUD, sleep, mac, address).enqueue(callback);
    }

    public void airConditionerControl(int tem, int status, int mode, int wind, int swingLR, int swingUD, int sleep,
                                      int hostId, String address, Callback<Result> callback) {
        httpService.airConditionerControl(tem, status, mode, wind, swingLR, swingUD, sleep, hostId, address).enqueue(callback);
    }

    public void airConditionerControl(AirConditionerControl data, int hostId, String address, Callback<Result> callback) {
        airConditionerControl(data.getTem(), data.getStatus(), data.getMode(), data.getWind(), data.getSwingLR(),
                data.getSwingUD(), data.getSleep(), hostId, address, callback);
    }

    public void airConditionerControl(AirConditionerControl data, String mac, String address, Callback<Result> callback) {
        airConditionerControl(data.getTem(), data.getStatus(), data.getMode(), data.getWind(), data.getSwingLR(),
                data.getSwingUD(), data.getSleep(), mac, address, callback);
    }

    public void exhaustFanControl(int oper, String address, int hostId, Callback<Result> callback) {
        httpService.exhaustFanControl(oper, address, hostId).enqueue(callback);
    }

    public void exhaustFanControl(int oper, String address, String mac, Callback<Result> callback) {
        httpService.exhaustFanControl(oper, address, mac).enqueue(callback);
    }

    public void refreshDevStatus(String mac, Callback<Result> callback) {
        httpService.refreshDevStatus(mac).enqueue(callback);
    }

    /* --------------------------------------------------常用页--------------------------------------------------------*/

    public void addShortcut(String type, int mainId, String mac, Callback<Result> callback) {
        httpService.addShortcut(type, mainId, mac).enqueue(callback);
    }

    public void addBatchShortcut(String type, int[] mainId, String mac, Callback<Result> callback) {
        httpService.addBatchShortcut(type, mainId, mac).enqueue(callback);
    }

    public void deleteShortcut(int id, Callback<Result> callback) {
        httpService.deleteShortcut(id).enqueue(callback);
    }

    public void deleteBatchShortcut(int[] id, Callback<Result> callback) {
        httpService.deleteBatchShortcut(id);
    }

    public void listShortcut(String mac, Callback<Result<List<Shortcut>>> callback) {
        httpService.listShortcut(mac).enqueue(callback);
    }

    public void addBatchShortcutForAllType(String deviceIds, String sceneIds, String mac, Callback<Result> callback) {
        httpService.addBatchShortcutForAllType(deviceIds, sceneIds, mac).enqueue(callback);
    }

    public void getDeviceTypeStatus(String mac, Callback<Result<List<DeviceTypeStatus>>> callback) {
        httpService.getDeviceTypeStatus(mac).enqueue(callback);
    }
}
