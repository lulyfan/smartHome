package com.ut.data.dataSource.remote.http;

/**
 * Created by ZYB on 2017-03-10.
 */

public class Result<T> {

    public int code;
    public String msg;
    public T data;

    @Override
    public String toString() {
        return "Result{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }

    public boolean isSuccess() {
        return this.code == 200;
    }
}
