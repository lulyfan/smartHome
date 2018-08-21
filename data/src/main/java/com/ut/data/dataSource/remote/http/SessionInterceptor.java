package com.ut.data.dataSource.remote.http;

import java.io.IOException;
import java.util.UUID;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class SessionInterceptor implements Interceptor{
    private static String uuid = UUID.randomUUID().toString();
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request oldRequest = chain.request();

        Request newRequest = oldRequest.newBuilder()
                .header("mobile_session_flag", "true")
                .header("session_token", uuid)
                .build();

        Response response =  chain.proceed(newRequest);
        return response;
    }

    public static void createUUID() {
        uuid = UUID.randomUUID().toString();
        System.out.println("createUUID:" + uuid);
    }
}
