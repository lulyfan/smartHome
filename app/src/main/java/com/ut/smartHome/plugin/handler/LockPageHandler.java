package com.ut.smartHome.plugin.handler;

import android.app.Activity;
import android.app.FragmentManager;
import android.util.Log;

import com.ut.data.dataSource.remote.bluetooth.BleMsg;
import com.ut.data.dataSource.remote.bluetooth.Data.BleLockState;
import com.ut.data.dataSource.remote.bluetooth.Data.KeyInfo;
import com.ut.data.dataSource.remote.bluetooth.Data.PushData;
import com.ut.data.dataSource.remote.bluetooth.Data.Status;
import com.ut.data.dataSource.remote.bluetooth.cmd.BleCallBack;
import com.ut.data.dataSource.remote.bluetooth.cmd.ChangeBlePwd;
import com.ut.data.dataSource.remote.bluetooth.cmd.GetBleInfo;
import com.ut.data.dataSource.remote.bluetooth.cmd.GetKey;
import com.ut.data.dataSource.remote.bluetooth.cmd.GetKeyConfig;
import com.ut.data.dataSource.remote.bluetooth.cmd.OpenBleLock;
import com.ut.data.dataSource.remote.bluetooth.cmd.WriteKeyConfig;
import com.ut.data.dataSource.remote.http.Result;
import com.ut.data.dataSource.remote.http.data.AuthKey;
import com.ut.data.dataSource.remote.http.data.LockConfig;
import com.ut.data.dataSource.remote.http.data.LockOperateLog;
import com.ut.smartHome.plugin.BasePlugin;
import com.ut.smartHome.plugin.SmartHomeContext;
import com.ut.smartHome.ui.LoadDialogFragment;

import org.apache.cordova.CallbackContext;
import org.json.JSONArray;
import org.json.JSONException;

import java.nio.ByteBuffer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LockPageHandler extends BasePageHandler{

    private static final String BLE_TAG = "bluetoothLock";

    private static final String ACTION_CHECK_BLE_VERIFY_STATE = "checkBleVerifyState";
    private static final String ACTION_REALEASE_BLE_LOCK = "releaseBleLock";
    private static final String ACTION_VERIFY_BLE = "verifyBle";
    private static final String ACTION_CHECK_IF_BLE_HAS_PWD = "checkIfBleHasPwd";
    private static final String ACTION_OPEN_BLE_LOCK = "openBleLock";
    private static final String ACTION_CHECK_IS_OPEN_LOCK_IN_BG = "checkIsOpenLockInbg";
    private static final String ACTION_CHANGE_OPEN_IN_BG_STATE = "changeOpenInbgState";
    private static final String ACTION_CHANGE_BLE_PWD = "modifyBluePwdProcess";
    private static final String ACTION_LIST_LOCK_LOG = "listLockLog";
    private static final String ACTION_GET_LOCK_CONFIG = "getLockConfig";
    private static final String ACTION_CHECK_BLE_CONNECT_STATE = "checkBleConnectState";
    private static final String ACTION_BATCH_SAVE_CONFIG = "batchSaveConfig";
    private static final String ACTION_LIST_AUTH_KEY = "listAuthKey";
    private static final String ACTION_DELETE_AUTH_KEY = "deleteAuthKey";
    private static final String ACTION_SEND_AUTH_PASSWORD = "sendAuthPassword";
    private static final String ACTION_INSERT_KEY = "insertAuthKey";
    private static final String ACTION_MODIFY_KEY = "modifyAuthKey";
    private static final String ACTION_CHECK_ADMIN_PWD = "checkAdminPwd";

    public static final String OPEN_BLUETOOTH = "openBluetooth";
    public static final String CLOSE_BLUETOOTH = "closeBluetooth";
    public static final String BLE_DISCONNECTED = "disconnected";
    public static final String BLE_CONNECTED = "connected";
    public static final String BLE_STATUS = "bleStatus";

    //校验蓝牙锁密码失败的错误码
    private static final int ERROR_PWD = -1;          //蓝牙密码校验时，没有密码或密码错误
    private static final int ERROR_CONNECT = -2;      //连接超时或者没有连接
    private static final int ERROR_RESPONSE = -3;

    private static final int DEV_NO_LOCK_STATE = 6;    //门锁状态的设备编号
    private static final int DEV_NO_ELEC = 0;          //电量设备编号
    private static final int DEV_NO_ALARM = 7;         //报警信息设备编号

    private boolean isBleConnect;

    public LockPageHandler(SmartHomeContext context, BasePlugin plugin) {
        super(context, plugin);
    }

    @Override
    public String[] getActions() {
        return new String[]{ ACTION_CHECK_BLE_VERIFY_STATE,
                                         ACTION_REALEASE_BLE_LOCK,
                                         ACTION_VERIFY_BLE,
                                         ACTION_CHECK_IF_BLE_HAS_PWD,
                                         ACTION_OPEN_BLE_LOCK,
                                         ACTION_CHECK_IS_OPEN_LOCK_IN_BG,
                                         ACTION_CHANGE_OPEN_IN_BG_STATE,
                                         ACTION_CHANGE_BLE_PWD,
                                         ACTION_LIST_LOCK_LOG,
                                         ACTION_GET_LOCK_CONFIG,
                                         ACTION_CHECK_BLE_CONNECT_STATE,
                                         ACTION_BATCH_SAVE_CONFIG,
                                         ACTION_LIST_AUTH_KEY,
                                         ACTION_DELETE_AUTH_KEY,
                                         ACTION_SEND_AUTH_PASSWORD,
                                         ACTION_INSERT_KEY,
                                         ACTION_MODIFY_KEY,
                                         ACTION_CHECK_ADMIN_PWD
        };
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        switch (action) {
            case ACTION_CHECK_BLE_VERIFY_STATE:
                checkBleVerifyState(args.getString(0), args.getString(1), callbackContext);
                return true;

            case ACTION_REALEASE_BLE_LOCK:
                releaseBleLock(callbackContext);
                return true;

            case ACTION_VERIFY_BLE:
                verifyBle(args.getString(0), args.getString(1), callbackContext);
                return true;

            case ACTION_CHECK_IF_BLE_HAS_PWD:
                checkIfBleHasPwd(callbackContext);
                return true;

            case ACTION_OPEN_BLE_LOCK:
                openBleLock(callbackContext);
                return true;

            case ACTION_CHECK_IS_OPEN_LOCK_IN_BG:
                checkIsOpenLockInbg(callbackContext);
                return true;

            case ACTION_CHANGE_OPEN_IN_BG_STATE:
                changeOpenInbgState(args.getString(0), callbackContext);
                return true;

            case ACTION_CHANGE_BLE_PWD:
                changeBlePwd(args.getString(0), args.getString(1), args.getString(2), callbackContext);
                return true;

            case ACTION_LIST_LOCK_LOG:
                listLockLog(args.getString(0), callbackContext);
                return true;

            case ACTION_GET_LOCK_CONFIG:
                getLockConfig(args.getString(0), callbackContext);
                return true;

            case ACTION_CHECK_BLE_CONNECT_STATE:
                checkBleConnectState(callbackContext);
                return true;

            case ACTION_BATCH_SAVE_CONFIG:
                batchSaveConfig(args.getString(0), args.getString(1), args.getString(2), args.getString(3),
                        args.getString(4), args.getString(5), args.getString(6), callbackContext);
                return true;

            case ACTION_LIST_AUTH_KEY:
                listAuthKey(args.getString(0), callbackContext);
                return true;

            case ACTION_DELETE_AUTH_KEY:
                deleteAuthKey(args.getString(0), args.getInt(1), args.getInt(2), callbackContext);
                return true;

            case ACTION_SEND_AUTH_PASSWORD:
                sendAuthPassword(args.getString(0), args.getString(1), callbackContext);
                return true;

            case ACTION_INSERT_KEY:
                insertAuthKey(args.getString(0), args.getString(1), args.getString(2), args.getString(3),
                        args.getString(4), args.getString(5), args.getString(6), callbackContext);
                return true;

            case ACTION_MODIFY_KEY:
                modifyAuthKey(args.getString(0), args.getString(1), args.getString(2), args.getString(3), args.getString(4),
                        args.getString(5), args.getString(6), args.getString(7), args.getString(8), callbackContext);
                return true;

            case ACTION_CHECK_ADMIN_PWD:
                checkAdminPwd(args.getString(0), args.getString(1), callbackContext);
                return true;
        }

        return false;
    }

    private void checkAdminPwd(String devAddress, String pwd, CallbackContext callbackContext) {
        String adminPwd = SmartHomeContext.getBlePwd(plugin.getContext());
        if (adminPwd.equals(pwd)) {
            callbackContext.success();
        } else {
            callbackContext.error("");
        }
    }

    private void checkIfBleHasPwd(CallbackContext callbackContext) {
        String pwd = SmartHomeContext.getBlePwd(plugin.getContext());
        if ("".equals(pwd)) {
            callbackContext.error("");
        } else {
            callbackContext.success();
        }
    }

    private void modifyAuthKey(String devAddress, String id, String type, String value, String tempPwdValue, String startTimeStamp,
                               String endTimeStamp, String frequencyValues, String authCode, CallbackContext callbackContext) {

        long start = getTimeStamp(startTimeStamp);
        long end = getTimeStamp(endTimeStamp);

        httpDataSource.updateAuthKey(Integer.parseInt(id), Integer.parseInt(type), Integer.parseInt(value), tempPwdValue, start, end, Integer.parseInt(frequencyValues),
                Integer.parseInt(authCode), devAddress, smartHomeContext.getCurrentHostMac(), new Callback<Result>() {
                    @Override
                    public void onResponse(Call<Result> call, Response<Result> response) {
                        Result result = response.body();
                        if (result == null) {
                            callbackContext.error("result = null");
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

    private void insertAuthKey(String devAddress, String type, String value, String tempPwdValue, String startTimeStamp,
                               String endTimeStamp, String frequencyValues, CallbackContext callbackContext) {
//        yyyy-MM-dd'T'HH:mm:ss
        long start = getTimeStamp(startTimeStamp);
        long end = getTimeStamp(endTimeStamp);

        httpDataSource.addAuthKey(Integer.parseInt(type), Integer.parseInt(value), tempPwdValue, start,
                end, Integer.parseInt(frequencyValues), devAddress, smartHomeContext.getCurrentHostMac(),
                new Callback<Result>() {
                    @Override
                    public void onResponse(Call<Result> call, Response<Result> response) {
                        Result result = response.body();
                        if (result == null) {
                            callbackContext.error("result = null");
                            return;
                        }

                        if (result.isSuccess()) {
                            callbackContext.success("1");
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

    private long getTimeStamp(String time) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(time).getTime() / 1000;
        } catch (ParseException e) {
            e.printStackTrace();
            return -1;
        }
    }

    private void sendAuthPassword(String phone, String message, CallbackContext callbackContext) {
        httpDataSource.sendAuthPassword(phone, message, new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                Result result = response.body();

                if (result != null && result.isSuccess()) {
                    callbackContext.success();
                } else {
                    callbackContext.error("");
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                callbackContext.error(t.getMessage());
            }
        });
    }

    private void deleteAuthKey(String devAddress, int id, int authCode, CallbackContext callbackContext) {
        httpDataSource.deleteAuthKey(id, smartHomeContext.getCurrentHostId(), devAddress, authCode, new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                Result result = response.body();
                if (result == null) {
                    callbackContext.error("response = null");
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

    private void listAuthKey(String devAddress, CallbackContext callbackContext) {

        showLoadDialog("请稍候...");

        httpDataSource.listAuthKey(devAddress, smartHomeContext.getCurrentHostMac(), new Callback<Result<List<AuthKey>>>() {
            @Override
            public void onResponse(Call<Result<List<AuthKey>>> call, Response<Result<List<AuthKey>>> response) {
                Result<List<AuthKey>> result = response.body();
                if (result == null) {
                    callbackContext.error("result = null");
                    return;
                }

                if (result.isSuccess()) {
                    callbackContext.success(toJsonStr(result.data));
                } else {
                    callbackContext.error(result.msg);
                }

                dismissLoadDialog();
            }

            @Override
            public void onFailure(Call<Result<List<AuthKey>>> call, Throwable t) {
                callbackContext.error(t.getMessage());
                dismissLoadDialog();
            }
        });
    }

    private void batchSaveConfig(String devAddress, String blueMac, String types, String names, String keyIds, String auths,
                                 String keyInNums, CallbackContext callbackContext) {

        if (isBleConnect) {
            String[] keyIdTemp = keyIds.split(",");
            String[] authTemp = auths.split(",");
            String[] keyInNumTemp = keyInNums.split(",");
            String[] typeTemp = types.split(",");
            List<KeyInfo> keyInfos = new ArrayList<>();

            for (int i = 0; i < keyIdTemp.length; i++) {
                KeyInfo keyInfo = new KeyInfo();
                keyInfo.keyNo = (byte) Integer.parseInt(keyIdTemp[i]);
                keyInfo.auth = (byte) Integer.parseInt(authTemp[i]);
                keyInfo.type = (byte) Integer.parseInt(typeTemp[i]);
                keyInfo.keyInNum = (byte) Integer.parseInt(keyInNumTemp[i]);
                keyInfos.add(keyInfo);
            }
            batchSaveConfigByBle(callbackContext, keyInfos);

            httpDataSource.batchSaveLockConfig(smartHomeContext.getCurrentHostMac(), null, devAddress, types, names, keyIds,
                    auths, keyInNums, new Callback<Result>() {
                        @Override
                        public void onResponse(Call<Result> call, Response<Result> response) {
                        }

                        @Override
                        public void onFailure(Call<Result> call, Throwable t) {
                        }
                    });
        } else {

            httpDataSource.batchSaveLockConfig(smartHomeContext.getCurrentHostMac(), null, devAddress, types, names, keyIds,
                    auths, keyInNums, new Callback<Result>() {
                        @Override
                        public void onResponse(Call<Result> call, Response<Result> response) {
                            Result result = response.body();
                            if (result == null) {
                                callbackContext.error("服务器响应异常");
                                return;
                            }

                            if (result.isSuccess()) {
                                callbackContext.success("保存成功");
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
    }

    private void batchSaveConfigByBle(CallbackContext callbackContext, List<KeyInfo> keyInfos) {
        getKey(callbackContext, new GetKeyListener() {
            @Override
            public void success() {

                WriteKeyConfig writeKeyConfig = new WriteKeyConfig();
                writeKeyConfig.setKeyInfos(keyInfos);
                writeKeyConfig.sendMsg(new BleCallBack<WriteKeyConfig.Data>() {
                    @Override
                    public void success(WriteKeyConfig.Data result) {
                        if (result.isSuccess()) {
                            callbackContext.success("保存成功");
                        }
                    }

                    @Override
                    public void fail() {
                        callbackContext.error("保存失败,蓝牙锁响应异常");
                    }

                    @Override
                    public void timeout() {
                        callbackContext.error("蓝牙连接超时");
                    }
                });
            }

            @Override
            public void fail(int errorCode) {
                callbackContext.error("保存失败");
            }
        });
    }

    private void checkBleConnectState(CallbackContext callbackContext) {
        if (isBleConnect) {
            callbackContext.success();
        } else {
            callbackContext.error("no connect");
        }
    }

    private void getLockConfig(String devAddress, CallbackContext callbackContext) {

        showLoadDialog("请稍候...");

        httpDataSource.getLockConfig(smartHomeContext.getCurrentHostMac(), null, devAddress, "", new Callback<Result<List<LockConfig>>>() {
            @Override
            public void onResponse(Call<Result<List<LockConfig>>> call, Response<Result<List<LockConfig>>> response) {

                Result<List<LockConfig>> result = response.body();
                if (result == null) {
                    callbackContext.error("response = null");
                    return;
                }

                if (result.isSuccess()) {
                    callbackContext.success();

                    if (isBleConnect) {
                        getLockConfigByBle(callbackContext, result.data);
                    } else {
                        pushWebKeyConfigList(toJsonStr(result.data));
                    }

                } else {
                    getLockConfigByBle(callbackContext, result.data);
                }
                dismissLoadDialog();
            }

            @Override
            public void onFailure(Call<Result<List<LockConfig>>> call, Throwable t) {
                dismissLoadDialog();
                getLockConfigByBle(callbackContext, null);
            }
        });
    }

    private void getLockConfigByBle(CallbackContext callbackContext, List<LockConfig> webLockConfigs) {

        getKey(callbackContext, new GetKeyListener() {
            @Override
            public void success() {

                GetKeyConfig getKeyConfig = new GetKeyConfig();
                getKeyConfig.sendMsg(new BleCallBack<GetKeyConfig.Data>() {
                    @Override
                    public void success(GetKeyConfig.Data result) {

                        List<KeyInfo> keyInfos = result.keyInfos;
                        List<LockConfig> lockConfigList = new ArrayList<>();
                        for (KeyInfo keyInfo : keyInfos) {

                            LockConfig lockConfig = new LockConfig();
                            lockConfig.setValue(keyInfo.keyNo);
                            lockConfig.setType(keyInfo.type + "");
                            lockConfig.setAuth(keyInfo.auth);
                            lockConfig.setKeyInNum(keyInfo.keyInNum + "");

                            if (webLockConfigs != null) {
                                for (LockConfig weblockConfig : webLockConfigs) {
                                    if (lockConfig.getValue() == weblockConfig.getValue()) {
                                        lockConfig.setName(weblockConfig.getName());
                                    }
                                }
                            }

                            if ("1".equals(lockConfig.getType()) && "0".equals(lockConfig.getKeyInNum())) {
                                lockConfig.setCanChangeAuth(false);
                                lockConfig.setName("管理员密码");
                            } else {
                                lockConfig.setCanChangeAuth(true);
                            }

                            lockConfigList.add(lockConfig);
                        }


                        for (LockConfig lockConfig : lockConfigList) {

                            String name = lockConfig.getName();
                            if (name != null && !"".equals(name) && !"undefined".equals(name)) {
                                continue;
                            }

                            String type = lockConfig.getType();
                            String incode = lockConfig.getKeyInNum();
                            switch (type) {
                                case "1":

                                    if (!"0".equals(incode)) {
                                        lockConfig.setName("密码_" + incode);
                                    }

                                    break;
                                case "0":
                                    lockConfig.setName("指纹_" + incode);
                                    break;
                                case "4":
                                    lockConfig.setName("用户卡_" + incode);
                                    break;
                                case "2":
                                    lockConfig.setName("电子钥匙_" + incode);
                                    break;
                            }
                        }

                        pushBleKeyConfigList(toJsonStr(lockConfigList));
                    }

                    @Override
                    public void fail() {

                    }

                    @Override
                    public void timeout() {

                    }
                });
            }

            @Override
            public void fail(int errorCode) {

            }
        });


    }

    private void listLockLog(String devAddress, CallbackContext callbackContext) {

        showLoadDialog("请稍候...");
        httpDataSource.getLockLog(smartHomeContext.getCurrentHostMac(), null, devAddress, new Callback<Result<List<LockOperateLog>>>() {
            @Override
            public void onResponse(Call<Result<List<LockOperateLog>>> call, Response<Result<List<LockOperateLog>>> response) {
                Result<List<LockOperateLog>> result = response.body();
                if (result == null) {
                    callbackContext.error("response = null");
                    return;
                }

                if(result.isSuccess()) {
                    callbackContext.success(toJsonStr(result.data));
                } else {
                    callbackContext.error(result.msg);
                }
                dismissLoadDialog();
            }

            @Override
            public void onFailure(Call<Result<List<LockOperateLog>>> call, Throwable t) {
                dismissLoadDialog();
                callbackContext.error(t.getMessage());
            }
        });
    }

    private void changeBlePwd(String oldPwd, String newPwd, String devAddress, CallbackContext callbackContext) {

        if (oldPwd.length() != 6 || newPwd.length() != 6) {
            callbackContext.error("密码长度必须为6位");
            return;
        }

        getKey(callbackContext, new GetKeyListener() {
            @Override
            public void success() {
                ChangeBlePwd changeBlePwd = new ChangeBlePwd();
                changeBlePwd.setOldPwd(oldPwd.getBytes());
                changeBlePwd.setNewPwd(newPwd.getBytes());
                changeBlePwd.sendMsg(new BleCallBack<ChangeBlePwd.Data>() {
                    @Override
                    public void success(ChangeBlePwd.Data data) {
                        if (data.isSuccess()) {
                            SmartHomeContext.saveBlePwd(plugin.getContext(), newPwd);
                            callbackContext.success("修改成功");
                        } else {
                            callbackContext.error("修改失败");
                        }
                    }

                    @Override
                    public void fail() {
                        callbackContext.error("回复异常");
                    }

                    @Override
                    public void timeout() {
                        callbackContext.error("连接超时");
                    }
                });
            }

            @Override
            public void fail(int errorCode) {
                callbackContext.error("修改失败");
            }
        });

    }

    private void changeOpenInbgState(String autoOpenLockState, CallbackContext callbackContext) {
        boolean isAutoOpenLock = "1".equals(autoOpenLockState);
        SmartHomeContext.setIsAutoOpenLock(plugin.getContext(), isAutoOpenLock);
        callbackContext.success();
    }

    private void checkIsOpenLockInbg(CallbackContext callbackContext) {
        boolean isAutoOpenLock = SmartHomeContext.isAutoOpenLock(plugin.getContext());
        if (isAutoOpenLock) {
            callbackContext.success("1");
        } else {
            callbackContext.success("0");
        }

    }

    private void openBleLock(CallbackContext callbackContext) {
        Log.i("bloothLock", "openBleLock");
        showLoadDialog("正在开锁");

        getKey(callbackContext, new GetKeyListener() {
            @Override
            public void success() {

                OpenBleLock openBleLock = new OpenBleLock();
                openBleLock.sendMsg(new BleCallBack<OpenBleLock.Data>() {
                    @Override
                    public void success(OpenBleLock.Data data) {
                        if (data.result == 1) {
                            callbackContext.success();
                            addOpenLockLog();
                        } else {
                            callbackContext.error("开锁失败");
                        }

                        dismissLoadDialog();
                    }

                    @Override
                    public void fail() {
                        callbackContext.error(ERROR_RESPONSE);
                        dismissLoadDialog();
                    }

                    @Override
                    public void timeout() {
                        callbackContext.error(ERROR_CONNECT);
                        dismissLoadDialog();
                    }
                });
            }

            @Override
            public void fail(int errorCode) {
                callbackContext.error("开锁失败");
                dismissLoadDialog();
            }
        });
    }

    private void openBleLock() {
        Log.i("bloothLock", "openBleLock");
        String pwd = SmartHomeContext.getBlePwd(plugin.getContext());

        getKey(pwd, new GetKeyListener() {
            @Override
            public void success() {

                OpenBleLock openBleLock = new OpenBleLock();
                openBleLock.sendMsg(new BleCallBack<OpenBleLock.Data>() {
                    @Override
                    public void success(OpenBleLock.Data data) {
                    }

                    @Override
                    public void fail() {
                    }

                    @Override
                    public void timeout() {
                    }
                });
            }

            @Override
            public void fail(int errorCode) {
            }
        });
    }

    private void addOpenLockLog() {
        httpDataSource.addBlueLockLog(null, smartHomeContext.getBlueLockMac(), null, null, new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {

            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {

            }
        });
    }

    private void verifyBle(String devAddress, String pwd, CallbackContext callbackContext) {
        getKey(pwd, new GetKeyListener() {
            @Override
            public void success() {
                handleVerifySuccess();
            }

            @Override
            public void fail(int errorCode) {
                if (errorCode == ERROR_PWD) {
                    handleVerifyPwdError("密码错误，请重新输入");
                }
            }
        });
        callbackContext.success();
    }

    private void releaseBleLock(CallbackContext callbackContext) {
        plugin.webView.postMessage(CLOSE_BLUETOOTH, null);
        callbackContext.success();
    }

    private void checkBleVerifyState(String devAddress, String needCheck, CallbackContext callbackContext) {

        if ("0".equals(needCheck)) {

            if (isBleConnect) {
                callbackContext.success();
            } else {
                callbackContext.error(ERROR_CONNECT);
            }

        } else {
            plugin.webView.postMessage(OPEN_BLUETOOTH, null);
            callbackContext.success();
        }
    }

    private void getKey(CallbackContext callbackContext, GetKeyListener listener) {
        String pwd = SmartHomeContext.getBlePwd(plugin.getContext());
        if ("".equals(pwd)) {
            callbackContext.error(ERROR_PWD);
            return;
        }

        getKey(pwd, listener);
    }

    private void getKey(String pwd, GetKeyListener listener) {

        if ("".equals(pwd) || pwd == null) {
            if (listener != null) {
                listener.fail(ERROR_PWD);
            }
            return;
        }

        if (!isBleConnect) {
            if (listener != null) {
                listener.fail(ERROR_CONNECT);
            }
            return;
        }

        GetKey getKey = new GetKey();
        getKey.setIsAutoOpenLock((byte) 0x0);
        getKey.setKey(pwd);

        getKey.sendMsg(new BleCallBack<GetKey.Data>() {
            @Override
            public void success(GetKey.Data result) {
                byte verifyResult =  result.verifyResult;

                if (verifyResult == 1) {
                    if (listener != null) {
                        listener.success();
                        SmartHomeContext.saveBlePwd(plugin.getContext(), pwd);
                    }

                } else {
                    if (listener != null) {
                        listener.fail(ERROR_PWD);
                    }
                }
            }

            @Override
            public void fail() {
                if (listener != null) {
                    listener.fail(ERROR_RESPONSE);
                }
            }

            @Override
            public void timeout() {
                if (listener != null) {
                    listener.fail(ERROR_CONNECT);
                }
            }
        });
    }

    private void getBleInfo() {

        String pwd = SmartHomeContext.getBlePwd(plugin.getContext());

        getKey(pwd, new GetKeyListener() {
            @Override
            public void success() {

                GetBleInfo getBleInfo = new GetBleInfo();
                getBleInfo.setMac(smartHomeContext.getMac());
                getBleInfo.setPhone(SmartHomeContext.getAccount(plugin.getContext()));
                getBleInfo.sendMsg(new BleCallBack<GetBleInfo.Data>() {
                    @Override
                    public void success(GetBleInfo.Data result) {

                        BleLockState bleLockState = result.getBleLockState();
                        PushData<BleLockState> pushData = new PushData<>();
                        pushData.code = 0;
                        pushData.data = bleLockState;
                        pushVerifyResult(toJsonStr(pushData));

                        boolean isAutoOpenLock = SmartHomeContext.isAutoOpenLock(plugin.getContext());
                        if (isAutoOpenLock) {
                            openBleLock();
                        }
                    }

                    @Override
                    public void fail() {

                    }

                    @Override
                    public void timeout() {

                    }
                });
            }

            @Override
            public void fail(int errorCode) {

            }
        });

    }

    public void handleDisconnect() {
        isBleConnect = false;
        pushBleDisconnect("");
    }

    public void handleConnect() {

        isBleConnect = true;

        String pwd = SmartHomeContext.getBlePwd(plugin.getContext());

        if (!"".equals(pwd)) {
            getKey(pwd, new GetKeyListener() {
                @Override
                public void success() {
                    handleVerifySuccess();
                }

                @Override
                public void fail(int errorCode) {
                    Log.i(BLE_TAG, "校验密码错误");
                    handleVerifyPwdError("请输入密码");
                }
            });
        } else {
            Log.i(BLE_TAG, "校验密码为空");
            handleVerifyPwdError("请输入密码");
        }
    }

    private void handleVerifySuccess() {
        getBleInfo();
    }

    private void handleVerifyPwdError(String tip) {
        PushData<String> pushData = new PushData<>();
        pushData.code = ERROR_PWD;
        pushData.data = tip;
        pushVerifyResult(toJsonStr(pushData));
    }

    public void handleBleStatus(BleMsg msg) {
        ByteBuffer buffer = ByteBuffer.wrap(msg.getContent());
        byte statusNum = buffer.get();
        List<Status> statusList = new ArrayList<>();

        for (int i=0; i<statusNum; i++) {
            byte devNo = buffer.get();
            byte type = buffer.get();

            Status status = new Status(devNo, type);
            buffer.get(status.value);
            statusList.add(status);
        }

        BleLockState bleLockState = new BleLockState();
        for (Status status : statusList) {

            switch (status.devNo) {
                case DEV_NO_ELEC:
                    bleLockState.setElect(status.value[0] + "");
                    break;
                case DEV_NO_LOCK_STATE:
                    bleLockState.setStatus(status.value[0] + "");
                    break;
                case DEV_NO_ALARM:
                    bleLockState.setAlarm(status.value[0]);
                    break;
                    default:
            }
        }

        PushData<BleLockState> pushData = new PushData<>();
        pushData.code = 1;
        pushData.data = bleLockState;

        Log.i(BLE_TAG, bleLockState.toString());
        pushVerifyResult(toJsonStr(pushData));
    }

    interface GetKeyListener {
        void success();
        void fail(int errorCode);
    }

    public void pushVerifyResult(String data) {
        plugin.callJs("onCheckVerifyCallback", "\'" + data + "\'");
    }

    public void pushBleDisconnect(String data) {
        plugin.callJs("onBleDisconnect", data);
    }

    public void pushBleState(String data) {
        plugin.callJs("onReceivePushData", data);
    }

    public void pushWebKeyConfigList(String data) {
        plugin.callJs("setWebKeyConfigList", "\'" + data + "\'");
    }

    public void pushBleKeyConfigList(String data) {
        plugin.callJs("setBleKeyConfigList", "\'" + data + "\'");
    }

    public void pushUpdateKeyconfigList(String data) {
        plugin.callJs("updateKeyconfigListFunc", "\'" + data + "\'");
    }

}
