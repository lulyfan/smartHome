package com.ut.smartHome.plugin;

import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.ut.data.dataSource.remote.http.HttpDataSource;
import com.ut.data.dataSource.remote.http.Result;
import com.ut.data.dataSource.remote.http.WebSocketHelper;
import com.ut.smartHome.AppData;
import com.ut.smartHome.plugin.handler.IndexPageHandler;
import com.ut.smartHome.plugin.handler.DevicePageHandler;
import com.ut.smartHome.plugin.handler.LoginPageHandler;
import com.ut.smartHome.plugin.handler.MinePageHandler;
import com.ut.smartHome.plugin.handler.MyHostPageHandler;
import com.ut.smartHome.plugin.handler.ScenePageHandler;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SmartHomePlugins extends BasePlugin implements WebSocketHelper.PushDataListener{

    private static final int LOGIN_SUCCESS = 1;

    private MutableLiveData<Boolean> loginState = AppData.getInstance().loginState;
    private SmartHomeContext smartHomeContext;

    @Override
    protected void pluginInitialize() {

        cordova.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                smartHomeContext = SmartHomeContext.getInstance(cordova.getContext());
                smartHomeContext.getHttpDataSource().setPushDataListener(SmartHomePlugins.this);

                addPluginHandler(new DevicePageHandler(smartHomeContext, SmartHomePlugins.this));
                addPluginHandler(new LoginPageHandler(smartHomeContext, SmartHomePlugins.this));
                addPluginHandler(new MinePageHandler(smartHomeContext, SmartHomePlugins.this));
                addPluginHandler(new MyHostPageHandler(smartHomeContext, SmartHomePlugins.this));
                addPluginHandler(new ScenePageHandler(smartHomeContext, SmartHomePlugins.this));
                addPluginHandler(new IndexPageHandler(smartHomeContext, SmartHomePlugins.this));
            }
        });


    }

    @Override
    public Object onMessage(String id, Object data) {
        if ("onPageFinished".equals(id)) {
            Log.i("login", "onPageFinished");
            Context context = cordova.getContext();
            if (SmartHomeContext.isHasLoginInfo(context)) {
                String account = SmartHomeContext.getAccount(context);
                String password = SmartHomeContext.getPassword(context);
                login(account, password);
            } else {
                loginState.postValue(false);
            }
        }
        return null;
    }

    private void login(String account, String password) {

        HttpDataSource httpDataSource = HttpDataSource.getInstance();
        httpDataSource.login(account, password, new Callback<Result<Integer>>() {
            @Override
            public void onResponse(Call<Result<Integer>> call, Response<Result<Integer>> response) {
                Result<Integer> result = response.body();

                if (result !=null && result.isSuccess()) {

                    int userId = result.data;
                    smartHomeContext.setUserId(userId);
                    httpDataSource.sendUserId(userId);

                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Log.i("login", "onLoginReturn");
                            onLoginReturn(LOGIN_SUCCESS);
                            loginState.postValue(true);
                        }
                    }, 1000);


                } else {
                    loginState.postValue(false);
                }
            }

            @Override
            public void onFailure(Call<Result<Integer>> call, Throwable t) {
                loginState.postValue(false);
            }
        });
    }

    @Override
    public void onReceive(String data) {
        pushWebSocketData(data);
    }

    protected void onLoginReturn(int message) {
        callJs("onLoginReturn", message + "");
    }

    private void pushWebSocketData(String data) {
        callJs("onReceivePushData", data);
    }

}
