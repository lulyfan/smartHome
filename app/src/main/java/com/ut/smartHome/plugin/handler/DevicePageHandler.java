package com.ut.smartHome.plugin.handler;

import com.ut.data.dataSource.remote.http.Result;
import com.ut.data.dataSource.remote.http.data.AirConditionerControl;
import com.ut.data.dataSource.remote.http.data.DeviceInfo;
import com.ut.data.dataSource.remote.http.data.RegionInfo;
import com.ut.data.dataSource.remote.http.data.Shortcut;
import com.ut.smartHome.plugin.BasePlugin;
import com.ut.smartHome.plugin.SmartHomeContext;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DevicePageHandler extends BasePageHandler{

    private static final String ACTION_GET_USER_DEVICE_LIST             = "getUserDeviceList";
    private static final String ACTION_GET_UESR_DEVICE_LIST_FROM_CACHE  = "getUserDeviceListFromCache";
    private static final String ACTION_GET_ALL_REGION                   = "getAllRegion";
    private static final String ACTION_GET_REGION_DEV                   = "getRegionDev";
    private static final String ACTION_REFRESH_DEV_STATUS               = "refreshDevStatus";
    private static final String ACTION_OPERATE_DCURTAIN                 = "operateDCurtain";
    private static final String ACTION_OPERATE_SCURTAIN                 = "operateSCurtain";
    private static final String ACTION_OPERATE_LIGHT                    = "operateLight";
    private static final String ACTION_OPERATE_AIRCONDITIONER           = "operateAirConditioner";
    private static final String ACTION_MODIFY_DEVICE_NAME               = "modifyDeviceName";


    private static final int ALL_REGION = -1;

    private Integer[] filter_scecePage =  {0x000B, 0x7F03, 0x7F04, 0x7F05, 0x7F06, 0x7F07};     //场景页要过滤的设备类型
    private Integer[] filter_devicePage = {0x7F03, 0x7F04, 0x7F05, 0x7F06, 0x7F07};             //设备页要过滤的设备类型


    public DevicePageHandler(SmartHomeContext context, BasePlugin plugin) {
        super(context, plugin);
    }

    @Override
    public String[] getActions() {
        return new String[]{
                       ACTION_GET_USER_DEVICE_LIST,
                       ACTION_GET_UESR_DEVICE_LIST_FROM_CACHE,
                       ACTION_GET_ALL_REGION,
                       ACTION_GET_REGION_DEV,
                       ACTION_REFRESH_DEV_STATUS,
                       ACTION_OPERATE_DCURTAIN,
                       ACTION_OPERATE_SCURTAIN,
                       ACTION_OPERATE_LIGHT,
                       ACTION_OPERATE_AIRCONDITIONER,
                       ACTION_MODIFY_DEVICE_NAME};
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        switch (action) {
            case ACTION_GET_USER_DEVICE_LIST:

                if (args.length() != 0) {
                    getUserDeviceListFromDB(true, null, filter_devicePage, callbackContext);
                } else {
                    getUserDeviceListFromNet(true, null, filter_devicePage, callbackContext);
                }
                return true;

            case ACTION_GET_UESR_DEVICE_LIST_FROM_CACHE:
                getUserDeviceListFromCache(args.getString(0), callbackContext);
                return true;

            case ACTION_GET_REGION_DEV:
                getRegionDev(args.getInt(0), callbackContext);
                return true;

            case ACTION_REFRESH_DEV_STATUS:
                refreshDevStatus(callbackContext);
                return true;

            case ACTION_OPERATE_DCURTAIN:
                operateDCurtain(args.getString(0), args.getInt(1), args.getInt(2),
                        args.getInt(3), args.getInt(4), callbackContext);
                return true;

            case ACTION_OPERATE_SCURTAIN:
                operateSCurtain(args.getString(0), args.getInt(1), args.getInt(2), callbackContext);
                return true;

            case ACTION_OPERATE_LIGHT:
                operateLight(args.getString(0), args.getInt(1), callbackContext);
                return true;

            case ACTION_GET_ALL_REGION:
                getAllRegion(callbackContext);
                return true;

            case ACTION_OPERATE_AIRCONDITIONER:
                operateAirconditioner(args.getString(0), args.getInt(1), args.getInt(2), args.getInt(3),
                        args.getInt(4), args.getInt(5), args.getInt(6), args.getInt(7), callbackContext);
                return true;

            case ACTION_MODIFY_DEVICE_NAME:
                modifyDeviceName(args.getString(0), args.getString(1), callbackContext);
                return true;
        }
        return false;
    }

    private void modifyDeviceName(String name, String address, CallbackContext callbackContext) {
        httpDataSource.updateDeviceName(name, address, smartHomeContext.getCurrentHostMac(), new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                Result result = response.body();
                if (result == null) {
                    callbackContext.error("修改失败");
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

    //    this.devAddress, tem, status, mode, wind, swingLR, swingUDB, sleep
    private void operateAirconditioner(String devAddress, int tem, int status, int mode,
                                       int wind, int swingLR, int swingUDB, int sleep, CallbackContext callbackContext) {
        AirConditionerControl control = new AirConditionerControl();
        control.setTem(tem);
        control.setStatus(status);
        control.setMode(mode);
        control.setWind(wind);
        control.setSwingLR(swingLR);
        control.setSwingUD(swingUDB);
        control.setSleep(sleep);
        httpDataSource.airConditionerControl(control, smartHomeContext.getCurrentHostMac(), devAddress, new Callback<Result>() {
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

    private void getUserDeviceListFromDB(boolean isFilter, List<String> addedAddress, Integer[] fiterDeviceType, CallbackContext callbackContext) {

        if (!smartHomeContext.isHasCurrentHost()) {
            callbackContext.error("当前主机未设置");
        }

        runOnThreadPool(new Runnable() {
            @Override
            public void run() {
                List<DeviceInfo> devices = localDataSource.getDevicesByHostId(smartHomeContext.getCurrentHostId());
                devices = handleDevices(isFilter, addedAddress, devices, fiterDeviceType);
                callbackContext.success(toJsonStr(devices));

                getUserDeviceListFromNet(isFilter, addedAddress, fiterDeviceType);
            }
        });
    }

    private void getUserDeviceListFromCache(String addresses, CallbackContext callbackContext) {

        String[] addressArray = addresses.split(",");
        List<String> addressList = Arrays.asList(addressArray);

        getUserDeviceListFromDB(true, addressList, filter_scecePage, callbackContext);

    }

    private void getAllRegion(CallbackContext callbackContext) {

        if (!smartHomeContext.isHasCurrentHost()) {
            callbackContext.error("当前主机未设置");
        }

        httpDataSource.getRegions(smartHomeContext.getCurrentHostMac(), new Callback<Result<List<RegionInfo>>>() {
            @Override
            public void onResponse(Call<Result<List<RegionInfo>>> call, Response<Result<List<RegionInfo>>> response) {
                Result<List<RegionInfo>> result = response.body();

                if (result == null) {
                    callbackContext.error("null");
                    return;
                }

                if (result.isSuccess()) {
                    String str = toJsonStr(result.data);
                    callbackContext.success(str);
                } else {
                    callbackContext.error(result.msg);
                }
            }

            @Override
            public void onFailure(Call<Result<List<RegionInfo>>> call, Throwable t) {
                callbackContext.error(t.getMessage());
            }
        });
    }

    private void getRegionDev(int ordinal, CallbackContext callbackContext) {

        if (ordinal == ALL_REGION) {
            getUserDeviceListFromDB(true, null, filter_devicePage, callbackContext);

        } else {
            runOnThreadPool(new Runnable() {
                @Override
                public void run() {
                    if (!smartHomeContext.isHasCurrentHost()) {
                        callbackContext.error("当前主机未设置");
                    }

                    List<DeviceInfo> devices = localDataSource.getDevicesByRegion(ordinal, smartHomeContext.getCurrentHostId());
                    devices = handleDevices(true, null, devices, filter_devicePage);
                    callbackContext.success(toJsonStr(devices));
                }
            });
        }

    }

    private void operateSCurtain(String deviceAddress, int oper, int status, CallbackContext callbackContext) {
        httpDataSource.sCurtainControl(oper, deviceAddress, status, smartHomeContext.getCurrentHostMac(), new Callback<Result>() {
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

    private void operateLight(String deviceAddress, int oper, CallbackContext callbackContext) {
        showLoadDialog("请稍候...");
        httpDataSource.lightControl(oper, deviceAddress, smartHomeContext.getCurrentHostMac(), new Callback<Result>() {
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

    private void operateDCurtain(String deviceAddress, int type, int oper, int in, int out, CallbackContext callbackContext) {
        httpDataSource.dCurtainControl(oper, type, deviceAddress, in, out, smartHomeContext.getCurrentHostMac(), new Callback<Result>() {
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

    private void refreshDevStatus(CallbackContext callbackContext) {

        if (!smartHomeContext.isHasCurrentHost()) {
            callbackContext.error("当前主机未设置");
        }

        httpDataSource.refreshDevStatus(smartHomeContext.getCurrentHostMac(), new Callback<Result>() {
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

    private void getUserDeviceListFromNet(boolean isFilter, List<String> addedAddress, Integer[] fiterDeviceType, CallbackContext callbackContext) {

        if (!smartHomeContext.isHasCurrentHost()) {
            callbackContext.error("当前主机未设置");
        }

        httpDataSource.getDevices(smartHomeContext.getCurrentHostMac(), new Callback<Result<List<DeviceInfo>>>() {
            @Override
            public void onResponse(Call<Result<List<DeviceInfo>>> call, Response<Result<List<DeviceInfo>>> response) {
                Result<List<DeviceInfo>> result = response.body();

                if (result == null) {
                    callbackContext.error("null");
                    return;
                }

                if (result.isSuccess()) {
                    runOnThreadPool(new Runnable() {
                        @Override
                        public void run() {
                            List<DeviceInfo> deviceInfoList = result.data;
                            deviceInfoList = handleDevices(isFilter, addedAddress, deviceInfoList, fiterDeviceType);

                            for (DeviceInfo deviceInfo : deviceInfoList) {
                                if (localDataSource.isInDeviceShortcut(deviceInfo.getId())) {
                                    deviceInfo.setSelect(true);
                                }
                            }

                            String str = toJsonStr(deviceInfoList);
                            callbackContext.success(str);

                        }
                    });

                    updateDeviceTable(result.data);

                } else {
                    callbackContext.error(result.msg);
                }
            }

            @Override
            public void onFailure(Call<Result<List<DeviceInfo>>> call, Throwable t) {
                callbackContext.error(t.getMessage());
            }
        });
    }

    //从网络中获取设备数据，并推送到前端
    private void getUserDeviceListFromNet(boolean isFilter, List<String> addedAddress, Integer[] fiterDeviceType) {

        if (!smartHomeContext.isHasCurrentHost()) {
            return;
        }

        httpDataSource.getDevices(smartHomeContext.getCurrentHostMac(), new Callback<Result<List<DeviceInfo>>>() {
            @Override
            public void onResponse(Call<Result<List<DeviceInfo>>> call, Response<Result<List<DeviceInfo>>> response) {
                Result<List<DeviceInfo>> result = response.body();

                if (result == null) {
                    return;
                }

                if (result.isSuccess()) {
                    List<DeviceInfo> deviceInfoList = result.data;
                    deviceInfoList = handleDevices(isFilter, addedAddress, deviceInfoList, fiterDeviceType);

                    String str = toJsonStr(deviceInfoList);
                    pushDeviceData(str);
                    updateDeviceTable(result.data);
                }
            }

            @Override
            public void onFailure(Call<Result<List<DeviceInfo>>> call, Throwable t) {
            }
        });
    }

    private List<DeviceInfo> handleDevices(boolean isFilter, List<String> addedAddress, List<DeviceInfo> devices, Integer[] fiterDeviceType) {

        if (devices == null) {
            return null;
        }

        if (isFilter) {
            devices = filterDevice(devices, fiterDeviceType);
        }

        if (addedAddress != null) {
            for (DeviceInfo deviceInfo : devices) {
                if (addedAddress.contains(deviceInfo.getAddress())) {
                    deviceInfo.setAdded(true);
                }
            }
        }

        return devices;
    }



    //过滤掉某些设备
    private List<DeviceInfo> filterDevice(List<DeviceInfo> deviceInfos, Integer[] filterDeviceType) {
        List<DeviceInfo> newDevices = new ArrayList<>();

        List<Integer> filterList = new ArrayList<>();
        filterList.addAll(Arrays.asList(filterDeviceType));

        for (DeviceInfo deviceInfo : deviceInfos) {

            if (!filterList.contains(deviceInfo.getDeviceTypeCode())) {
                newDevices.add(deviceInfo);
            }

        }

        return newDevices;
    }

    //更新数据库中的设备数据
    private void updateDeviceTable(List<DeviceInfo> newDeviceInfos) {
        runOnThreadPool(new Runnable() {
            @Override
            public void run() {
                List<DeviceInfo> oldDevices = localDataSource.getDevicesByHostId(smartHomeContext.getCurrentHostId());
                localDataSource.deleteDevices(oldDevices);
                localDataSource.saveDevices(newDeviceInfos);
            }
        });
    }

    private void pushDeviceData(String data) {
        plugin.callJs("getUserDeviceListFromDb", data);
    }
}
