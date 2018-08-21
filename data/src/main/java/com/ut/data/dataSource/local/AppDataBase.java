package com.ut.data.dataSource.local;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.ut.data.dataSource.remote.http.data.DeviceInfo;
import com.ut.data.dataSource.remote.http.data.SceneInfo;
import com.ut.data.dataSource.remote.http.data.Shortcut;

/**
 * Created by huangkaifan on 2018/6/14.
 */
@Database(entities = {DeviceInfo.class, SceneInfo.class, Shortcut.class}, version = 5)
public abstract class AppDataBase extends RoomDatabase {

    private static final String DATABASE_NAME = "SmartHome.db";

    public abstract DataDao dataDao();

    private static AppDataBase INSTANCE;

    public synchronized static AppDataBase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                    AppDataBase.class, DATABASE_NAME)
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return INSTANCE;
    }
}
