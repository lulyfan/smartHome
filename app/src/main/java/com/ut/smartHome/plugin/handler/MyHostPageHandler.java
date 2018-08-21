package com.ut.smartHome.plugin.handler;

import com.google.gson.Gson;
import com.ut.data.dataSource.remote.http.Result;
import com.ut.data.dataSource.remote.http.data.HostInfo;
import com.ut.data.dataSource.remote.http.data.pushData.SearchHostData;
import com.ut.data.dataSource.remote.udp.Data.HostDeviceInfo;
import com.ut.data.dataSource.remote.udp.cmd.BroadcastCallBack;
import com.ut.smartHome.plugin.BasePlugin;
import com.ut.smartHome.plugin.SmartHomeContext;

import org.apache.cordova.CallbackContext;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyHostPageHandler extends BasePageHandler{

    private static final String ACTION_GET_HOST_LIST    = "getHostList";
    private static final String ACTION_SET_CURRENT_HOST = "setCurrentHost";
    private static final String ACTION_UNBIND_HOST      = "unbindHost";
    private static final String ACTION_SEARCH_HOST      = "searchHost";
    private static final String ACTION_ADD_HOST         = "addHost";

    public MyHostPageHandler(SmartHomeContext context, BasePlugin plugin) {
        super(context, plugin);
    }

    @Override
    public String[] getActions() {
        return new String[]{ACTION_GET_HOST_LIST, ACTION_SET_CURRENT_HOST, ACTION_UNBIND_HOST, ACTION_SEARCH_HOST, ACTION_ADD_HOST};
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        switch (action) {
            case ACTION_GET_HOST_LIST:
                getHostList(callbackContext);
                return true;

            case ACTION_SET_CURRENT_HOST:
                setCurrentHost(args.getString(0), callbackContext);
                return true;

            case ACTION_UNBIND_HOST:
                unbindHost(args.getString(0), callbackContext);
                return true;

            case ACTION_SEARCH_HOST:
                search(callbackContext);
                return true;

            case ACTION_ADD_HOST:
                addHost(args.getString(0), args.getString(1), callbackContext);
                return true;
        }
        return false;
    }

    private void addHost(String mac, String password, CallbackContext callbackContext) {
        httpDataSource.loginHost(mac, password, new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                Result result = response.body();

                if (result == null) {
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

    private void search(CallbackContext callbackContext) {
        udpDataSource.searchHost(new BroadcastCallBack<HostDeviceInfo>() {
            @Override
            public void success(List<HostDeviceInfo> result) {
                callbackContext.success();

                for (HostDeviceInfo hostDeviceInfo : result) {
                    SearchHostData searchHostData = getHostPushData(hostDeviceInfo);
                    pushSearhHost(toJsonStr(searchHostData));
                }

            }

            @Override
            public void fail(List<Integer> errorCodes) {

            }

            @Override
            public void timeout() {

            }
        });

    }

    private void unbindHost(String mac, CallbackContext callbackContext) {
        httpDataSource.unbindHost(mac, new Callback<Result>() {
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

    private void setCurrentHost(String mac, CallbackContext callbackContext) {
        httpDataSource.setCurrentHost(mac, new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                Result result = response.body();

                if (result == null) {
                    callbackContext.error("null");
                    return;
                }

                if (result.isSuccess()) {
                    smartHomeContext.setCurrentHostMac(mac);
                    smartHomeContext.setHasCurrentHost(true);
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

    private void getHostList(CallbackContext callbackContext) {
        httpDataSource.getHostList(new Callback<Result<List<HostInfo>>>() {
            @Override
            public void onResponse(Call<Result<List<HostInfo>>> call, Response<Result<List<HostInfo>>> response) {
                Result<List<HostInfo>> result = response.body();

                if (result == null) {
                    callbackContext.error("null");
                    return;
                }

                if (result.isSuccess()) {
                    Gson gson = new Gson();
                    String str = gson.toJson(result.data);
                    callbackContext.success(str);

                    for (HostInfo hostInfo : result.data) {
                        if (hostInfo.isCurrentHost()) {
                            smartHomeContext.setCurrentHostId(hostInfo.getId());
                            smartHomeContext.setCurrentHostMac(hostInfo.getMac());
                        }
                    }
                } else {
                    callbackContext.error(result.msg);
                }
            }

            @Override
            public void onFailure(Call<Result<List<HostInfo>>> call, Throwable t) {
                callbackContext.error(t.getMessage());
            }
        });
    }

    //对从UDP广播搜索主机得到的主机数据进行解析以传给前端显示
    //102:表示是广播搜索主机数据
    private static SearchHostData getHostPushData(HostDeviceInfo hostDeviceInfo) {
        SearchHostData searchHostData = new SearchHostData();
        searchHostData.setCode(102);
        SearchHostData.DataBean data = new SearchHostData.DataBean();

        byte[] ip = hostDeviceInfo.iPAddress;
        String sIp = String.valueOf(ip[0]) + "." + String.valueOf(ip[1]) + "." +  String.valueOf(ip[2]) + "." + String.valueOf(ip[3]);
        data.setIp(sIp);

        byte[] mac = hostDeviceInfo.mac;
        String sMac = "";

        for (int i=0; i<6; i++) {
            String temp = Integer.toHexString(mac[i] & 0xFF);
            if (temp.length() == 1) {
                temp = "0" + temp;
            }

            if (sMac.length() > 0) {
                sMac += "-";
            }
            sMac += temp;
        }

        System.out.println("sMac:" + sMac);
        data.setMac(sMac);

        byte[] name = hostDeviceInfo.name;
        String sName = "";
        try {
            sName = new String(name, 0, hostDeviceInfo.nameLength, "GB2312");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        data.setName(sName);
        searchHostData.setData(data);

        return searchHostData;
    }

    private void pushSearhHost(String data) {
        plugin.callJs("onReceivePushData", data);
    }
}
