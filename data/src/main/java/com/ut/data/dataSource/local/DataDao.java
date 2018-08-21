package com.ut.data.dataSource.local;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.ut.data.dataSource.remote.http.data.DeviceInfo;
import com.ut.data.dataSource.remote.http.data.SceneInfo;
import com.ut.data.dataSource.remote.http.data.Shortcut;

import java.util.List;

/**
 * Created by huangkaifan on 2018/6/14.
 */
@Dao
public interface DataDao {

    @Query("Select * From deviceInfo Where hostId == :hostId")
    List<DeviceInfo> getDevicesByHostId(int hostId);

    @Query("Select * From deviceInfo Where region == :regionId AND hostId == :hostId")
    List<DeviceInfo> getDevicesByRegion(int regionId, int hostId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveDevices(List<DeviceInfo> deviceInfos);

    @Query("Select * From sceneinfo Where hostIds Like '%' + (:hostId) + '%'")
    List<SceneInfo> getScenesByHostId(int hostId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveScenes(List<SceneInfo> sceneInfos);

    @Delete
    void deleteScenes(List<SceneInfo> sceneInfos);

    @Delete
    void deleteDevices(List<DeviceInfo> deviceInfos);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveShortcuts(List<Shortcut> shortcuts);

    @Query("Select * From Shortcut Where hostId == :hostId")
    List<Shortcut> getShortcutByHostId(int hostId);

    @Query("Select * From Shortcut Where mainId == :mainId AND type LIKE 'device' LIMIT 1")
    Shortcut getDeviceShortcutByMainId(int mainId);

    @Query("Select * From Shortcut Where mainId == :mainId AND type LIKE 'scene' LIMIT 1")
    Shortcut getSceneShortcutByMainId(int mainId);

    @Delete
    void deleteShortcuts(List<Shortcut> shortcut);

    @Delete
    void deleteShortcht(Shortcut... shortcut);

    @Query("Select * From Shortcut Where id == :id LIMIT 1")
    Shortcut getShortcutById(int id);
}
