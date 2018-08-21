package com.ut.data.dataSource.remote.http;

import com.ut.data.dataSource.remote.http.data.AppInfo;
import com.ut.data.dataSource.remote.http.data.AuthKey;
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

import org.json.JSONObject;

import java.util.List;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by huangkaifan on 2018/6/15.
 */

public interface HttpService {

    /* ----------------------------------------------我的页-------------------------------------------------*/
    @POST("loginApp/getUserinfo?json")
    Call<Result<UserInfo>> getUserInfo();

    @FormUrlEncoded
    @POST("loginApp/resetPassword?json")
    Call<Result> resetPassword(@Field("oldPassword") String oldPassword, @Field("newPassword") String newPassword);

    @POST("/loginApp/logout?json")
    Call<Result> logout();

    //要登录后才能调用
    @POST("/loginApp/isLogin?json")
    Call<Result<Boolean>> isLogin();

    @POST("/loginApp/getUserId?json")
    Call<Result<Double>> getUserId();

    @POST("userHost/list?json")
    Call<Result<List<HostInfo>>> getHostList();

    @FormUrlEncoded
    @POST("userHost/loginHost?json")
    Call<Result> loginHost(@Field("mac") String mac, @Field("password") String password);

    //失败，理由为空
    @POST("userHost/logoutHost?json")
    Call<Result> logoutHost();

    //返回类型为布尔类型
    @FormUrlEncoded
    @POST("userHost/setCurrentHost?json")
    Call<Result> setCurrentHost(@Field("hostId") double hostId);

    @FormUrlEncoded
    @POST("userHost/setCurrentHost?json")
    Call<Result> setCurrentHost(@Field("mac") String mac);

    //返回类型不是double
    @POST("userHost/getCurrentHost?json")
    Call<Result<HostInfo>> getCurrentHost();

    @FormUrlEncoded
    @POST("userHost/bindHost?json")
    Call<Result> bindHost(@Field("mac") String mac);

    // failed:null
    @FormUrlEncoded
    @POST("userHost/unbindHost?json")
    Call<Result> unbindHost(@Field("mac") String mac);

    @FormUrlEncoded
    @POST("userHost/updateName?json")
    Call<Result> updateHostName(@Field("mac") String mac, @Field("name") String name);

    @FormUrlEncoded
    @POST("userHost/updatePassword?json")
    Call<Result> updateHostPassword(@Field("oldPassword") String oldPassword, @Field("newPassword") String newPassword, @Field("mac") String mac);

    @FormUrlEncoded
    @POST("host/upgradeCfg?json")
    Call<Result> upgradeCfg(@Field("mac") String mac);

    // failed:null
    @FormUrlEncoded
    @POST("userHost/loginHostToken?json")
    Call<Result> loginHostToken(@Field("token") String token);

    //???
    @FormUrlEncoded
    @POST("versionuser/app/getVersionInfo?json")
    Call<Result<AppInfo>> getVersionInfo(@Field("typeId") int typeId);

    //failed:null
    @FormUrlEncoded
    @POST("/feedback/insertOpinionFeedback?json")
    Call<Result> insertOpinionFeedback(@Field("title") String title, @Field("text") String text);

    //failed:null
    @FormUrlEncoded
    @POST("/feedback/insertBugFeedback?json")
    Call<Result> insertBugFeedback(@Field("title") String title, @Field("text") String text, @Field("img") String img);

    @POST("/feedback/uploadimg?json")
    Call<Result<String>> uploadimg(RequestBody img);

    @POST("/feedback/fAQList?json")
    Call<Result<List<FAQ>>> fAQList();

    @POST("/feedback/fAQTypeList?json")
    Call<Result<List<FAQType>>> fAQTypeList();

    /* ------------------------------------------登录页---------------------------------------------------------*/
    @FormUrlEncoded
    @POST("/loginApp/login?json")
    Call<Result<Integer>> login(@Field("account") String account, @Field("password") String password);

    @FormUrlEncoded
    @POST("/loginApp/register?json")
    Call<Result> register(@Field("account") String account, @Field("password") String password, @Field("verif") String verif);

    @FormUrlEncoded
    @POST("/loginApp/findPassword?json")
    Call<Result> findPassword(@Field("account") String account, @Field("password") String password, @Field("verif") String verif);

    @FormUrlEncoded
    @POST("/loginApp/getVerif?json")
    Call<Result<String>> getVerif(@Field("mobile") String mobile);

    /*---------------------------------------------场景页--------------------------------------------------------*/
    @FormUrlEncoded
    @POST("scene/list?json")
    Call<Result<List<SceneInfo>>> getUserScenes(@Field("hostId") int hostId);

    @FormUrlEncoded
    @POST("scene/list?json")
    Call<Result<List<SceneInfo>>> getUserScenes(@Field("mac") String mac);

    @POST("scene/list?json")
    Call<Result<List<SceneInfo>>> getUserScenes();

    @FormUrlEncoded
    @POST("scene/listInHost?json")
    Call<Result<List<SceneInfo>>> getHostScenes(@Field("hostId") int hostId);

    @FormUrlEncoded
    @POST("scene/listInHost?json")
    Call<Result<List<SceneInfo>>> getHostScenes(@Field("hostId") String mac);

    @FormUrlEncoded
    @POST("userDevice/list?json")
    Call<Result<List<DeviceInfo>>> getDevices(@Field("mac") String mac);

    @FormUrlEncoded
    @POST("userDevice/list?json")
    Call<Result<List<DeviceInfo>>> getDevices(@Field("hostId") int hostId);

    @FormUrlEncoded
    @POST("scene/add?json")
    Call<Result> addDevice(@Field("name") String name, @Field("img") byte[] img, @Field("imageUrl") String imageUrl,
                           @Field("time") String time, @Field("weeks") String weeks, @Field("deviceData") String deviceData);

    @FormUrlEncoded
    @POST("scene/execute?json")
    Call<Result> executeScene(@Field("sceneId") int sceneId);

    @FormUrlEncoded
    @POST("scene/get?json")
    Call<Result<SceneOperate>> getSceneOperate(@Field("sceneId") int sceneId);

    @FormUrlEncoded
    @POST("scene/add?json")
    Call<Result> addScene(@Field("name") String name, @Field("img") byte[] img, @Field("imageUrl") String imageUrl,
                          @Field("time") String time, @Field("weeks") String weeks, @Field("deviceData") String deviceData);

    @FormUrlEncoded
    @POST("scene/update?json")
    Call<Result> updateScene(@Field("sceneId") int sceneId, @Field("name") String name, @Field("img") byte[] img,
                             @Field("imageUrl") String imageUrl, @Field("time") String time, @Field("weeks") String weeks,
                             @Field("deviceData") String deviceData);

    @FormUrlEncoded
    @POST("scene/delete?json")
    Call<Result> deleteScene(@Field("sceneId") int sceneId);

    @FormUrlEncoded
    @POST("userDevice/getCode")
    Call<ResponseBody> getDeviceCode(@Field("deviceTypeCode") int deviceTypeCode);

    /*------------------------------------------------设备页-----------------------------------------------------*/

    @FormUrlEncoded
    @POST("userDevice/updateDeviceName?json")
    Call<Result> updateDeviceName(@Field("name") String name, @Field("address") String address, @Field("mac") String mac);

    @FormUrlEncoded
    @POST("region/list?json")
    Call<Result<List<RegionInfo>>> getRegions(@Field("mac") String mac);

    @FormUrlEncoded
    @POST("region/list?json")
    Call<Result<List<RegionInfo>>> getRegions(int hostId);

    @FormUrlEncoded
    @POST("device/getDeviceByRegion?json")
    Call<Result<List<DeviceInfo>>> getDeviceByRegion(@Field("ordinal") int ordinal);

    @FormUrlEncoded
    @POST("log/add?json")
    Call<Result> addLog(@Field("hostId") int hostId, @Field("address") String address,
                        @Field("statusCode") String statusCode, @Field("value") int value);

    @FormUrlEncoded
    @POST("log/add?json")
    Call<Result> addLog(@Field("mac") String mac, @Field("address") String address,
                        @Field("statusCode") String statusCode, @Field("value") int value);

    @FormUrlEncoded
    @POST("log/listByUser?json")
    Call<Result<DeviceOperateLog>> getLogByUser();

    @FormUrlEncoded
    @POST("log/listByDevice?json")
    Call<Result<DeviceOperateLog>> getLogByDevice(@Field("hostId") int hostId, @Field("address") String address);

    @FormUrlEncoded
    @POST("log/listByDevice?json")
    Call<Result<DeviceOperateLog>> getLogByDevice(@Field("mac") String mac, @Field("address") String address);

    @FormUrlEncoded
    @POST("lock/saveLockConfig?json")
    Call<Result> saveLockConfig(@Field("hostId") int hostId, @Field("blueMac") String blueMac, @Field("address") String address,
                                @Field("type") String type, @Field("name") String name, @Field("value") String value,
                                @Field("auth") int auth);

    @FormUrlEncoded
    @POST("lock/saveLockConfig?json")
    Call<Result> saveLockConfig(@Field("mac") String mac, @Field("blueMac") String blueMac, @Field("address") String address,
                                @Field("type") String type, @Field("name") String name, @Field("value") String value,
                                @Field("auth") int auth);

    @FormUrlEncoded
    @POST("lock/batchSaveLockConfig?json")
    Call<Result> batchSaveLockConfig(@Field("hostId") int hostId, @Field("blueMac") String blueMac, @Field("address") String address,
                                     @Field("type") String type, @Field("names") String names, @Field("values") String values,
                                     @Field("auths") String auths, @Field("incode") String incode);

    @FormUrlEncoded
    @POST("lock/batchSaveLockConfig?json")
    Call<Result> batchSaveLockConfig(@Field("mac") String mac, @Field("blueMac") String blueMac, @Field("address") String address,
                                     @Field("type") String type, @Field("names") String names, @Field("values") String values,
                                     @Field("auths") String auths, @Field("incode") String incode);

    @FormUrlEncoded
    @POST("lock/listLockConfig?json")
    Call<Result<List<LockConfig>>> getLockConfig(@Field("hostId") int hostId, @Field("blueMac") String blueMac,
                                           @Field("address") String address, @Field("type") String type);

    @FormUrlEncoded
    @POST("lock/listLockConfig?json")
    Call<Result<List<LockConfig>>> getLockConfig(@Field("mac") String mac, @Field("blueMac") String blueMac,
                                           @Field("address") String address, @Field("type") String type);

    @FormUrlEncoded
    @POST("lock/addBlueLockLog?json")
    Call<Result> addBlueLockLog(@Field("hostId") int hostId, @Field("blueMac") String blueMac, @Field("address") String address,
                                @Field("date") String date);

    @FormUrlEncoded
    @POST("lock/addBlueLockLog?json")
    Call<Result> addBlueLockLog(@Field("mac") String mac, @Field("blueMac") String blueMac, @Field("address") String address,
                                @Field("date") String date);


    @FormUrlEncoded
    @POST("lock/listLockLog?json")
    Call<Result<List<LockOperateLog>>> getLockLog(@Field("hostId") int hostId, @Field("blueMac") String blueMac, @Field("address") String address);

    @FormUrlEncoded
    @POST("lock/listLockLog?json")
    Call<Result<List<LockOperateLog>>> getLockLog(@Field("mac") String mac, @Field("blueMac") String blueMac, @Field("address") String address);

    @FormUrlEncoded
    @POST("lock/authKey?json")
    Call<Result> authKey(@Field("oper") int oper, @Field("id") int id, @Field("type") int type, @Field("keyId") int keyId,
                         @Field("password") String password, @Field("start") long start, @Field("end") long end,
                         @Field("times") int times, @Field("address") String address, @Field("hostId") int hostId,
                         @Field("authCode") int authCode);

    @FormUrlEncoded
    @POST("lock/authKey?json")
    Call<Result> authKey(@Field("oper") int oper, @Field("id") int id, @Field("type") int type, @Field("keyId") int keyId,
                         @Field("password") String password, @Field("start") long start, @Field("end") long end,
                         @Field("times") int times, @Field("address") String address, @Field("mac") String mac,
                         @Field("authCode") int authCode);


    @FormUrlEncoded
    @POST("lock/authKey?json")
    Call<Result<List<AuthKey>>> listAuthKey(@Field("oper") int oper, @Field("id") int id, @Field("type") int type, @Field("keyId") int keyId,
                                            @Field("password") String password, @Field("start") long start, @Field("end") long end,
                                            @Field("times") int times, @Field("address") String address, @Field("mac") String mac,
                                            @Field("authCode") int authCode);

    @FormUrlEncoded
    @POST("lock/sendAuthPassword?json")
    Call<Result> sendAuthPassword(@Field("mobile") String phone, @Field("password") String password);


    @FormUrlEncoded
    @POST("curtain/sOper?json")
    Call<Result> sCurtainControl(@Field("oper") int oper, @Field("address") String address, @Field("status") int status, @Field("hostId") int hostId);

    @FormUrlEncoded
    @POST("curtain/sOper?json")
    Call<Result> sCurtainControl(@Field("oper") int oper, @Field("address") String address, @Field("status") int status, @Field("mac") String mac);

    @FormUrlEncoded
    @POST("curtain/dOper?json")
    Call<Result> dCurtainControl(@Field("oper") int oper, @Field("type") int type, @Field("address") String address,
                                 @Field("in") int in, @Field("out") int out, @Field("hostId") int hostId);

    @FormUrlEncoded
    @POST("curtain/dOper?json")
    Call<Result> dCurtainControl(@Field("oper") int oper, @Field("type") int type, @Field("address") String address,
                                 @Field("in") int in, @Field("out") int out, @Field("mac") String mac);

    @FormUrlEncoded
    @POST("light/oper?json")
    Call<Result> lightControl(@Field("oper") int oper, @Field("address") String address, @Field("hostId") int hostId);

    @FormUrlEncoded
    @POST("light/oper?json")
    Call<Result> lightControl(@Field("oper") int oper, @Field("address") String address, @Field("mac") String mac);

    @FormUrlEncoded
    @POST("airConditioner/set")
    Call<Result> airConditionerControl(@Field("tem") int tem, @Field("status") int status, @Field("mode") int mode,
                                       @Field("wind") int wind, @Field("swingLR") int swingLR, @Field("swingUD") int swingUD,
                                       @Field("sleep") int sleep, @Field("hostId") int hostId, @Field("address") String address);

    @FormUrlEncoded
    @POST("airConditioner/set")
    Call<Result> airConditionerControl(@Field("tem") int tem, @Field("status") int status, @Field("mode") int mode,
                                       @Field("wind") int wind, @Field("swingLR") int swingLR, @Field("swingUD") int swingUD,
                                       @Field("sleep") int sleep, @Field("mac") String mac, @Field("address") String address);

    @FormUrlEncoded
    @POST("exhaustFan/oper?json")
    Call<Result> exhaustFanControl(@Field("oper") int oper, @Field("address") String address, @Field("hostId") int hostId);

    @FormUrlEncoded
    @POST("exhaustFan/oper?json")
    Call<Result> exhaustFanControl(@Field("oper") int oper, @Field("address") String address, @Field("mac") String mac);

    @FormUrlEncoded
    @POST("host/refreshDevStatus?json")
    Call<Result> refreshDevStatus(@Field("mac") String mac);

    /*------------------------------------------------常用页-----------------------------------------------------*/

    @FormUrlEncoded
    @POST("/quick/set?json")
    Call<Result> addShortcut(@Field("type") String type, @Field("mainId") int mainId, @Field("mac") String mac);

    @FormUrlEncoded
    @POST("/quick/batchSet?json")
    Call<Result> addBatchShortcut(@Field("type") String type, @Field("mainId") int[] mainId, @Field("mac") String mac);

    @FormUrlEncoded
    @POST("/quick/delete?json")
    Call<Result> deleteShortcut(@Field("id") int id);

    @FormUrlEncoded
    @POST("/quick/batchDelete?json")
    Call<Result> deleteBatchShortcut(@Field("id") int[] id);

    @FormUrlEncoded
    @POST("/quick/list?json")
    Call<Result<List<Shortcut>>> listShortcut(@Field("mac") String mac);

    @FormUrlEncoded
    @POST("/quick/batchAllSet?json")
    Call<Result> addBatchShortcutForAllType(@Field("deviceIds") String deviceIds, @Field("sceneIds") String sceneIds,
                                            @Field("mac") String mac);

    @FormUrlEncoded
    @POST("userDevice/getDeviceTypeStatus?json")
    Call<Result<List<DeviceTypeStatus>>> getDeviceTypeStatus(@Field("mac") String mac);
}
