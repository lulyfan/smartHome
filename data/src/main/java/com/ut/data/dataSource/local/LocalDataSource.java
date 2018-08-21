package com.ut.data.dataSource.local;

import com.ut.data.dataSource.remote.http.data.DeviceInfo;
import com.ut.data.dataSource.remote.http.data.SceneInfo;
import com.ut.data.dataSource.remote.http.data.Shortcut;

import java.util.List;

/**
 * Created by huangkaifan on 2018/6/14.
 */

public class LocalDataSource {

    private DataDao dataDao;
    private static LocalDataSource INSTANCE;

    private LocalDataSource(DataDao dataDao) {
        this.dataDao = dataDao;
    }

    public static synchronized LocalDataSource getInstance (DataDao dataDao) {
        if (INSTANCE == null) {
            INSTANCE = new LocalDataSource(dataDao);
        }
        return INSTANCE;
    }

    public static LocalDataSource getInstance() {
        return INSTANCE;
    }

    public void saveDevices(List<DeviceInfo> deviceInfos) {
        dataDao.saveDevices(deviceInfos);
    }

    public List<DeviceInfo> getDevicesByHostId(int hostId) {
        return dataDao.getDevicesByHostId(hostId);
    }

    public List<DeviceInfo> getDevicesByRegion(int regionId, int hostId) {
        return dataDao.getDevicesByRegion(regionId, hostId);
    }

    public void deleteDevices(List<DeviceInfo> deviceInfos) {
        dataDao.deleteDevices(deviceInfos);
    }

    public List<SceneInfo> getScenesByHostId(int hostId) {
        return dataDao.getScenesByHostId(hostId);
    }

    public void saveScenes(List<SceneInfo> sceneInfos) {
        dataDao.saveScenes(sceneInfos);
    }

    public void deleteScenes(List<SceneInfo> sceneInfos) {
        dataDao.deleteScenes(sceneInfos);
    }

    public void saveShortcuts(List<Shortcut> shortcuts) {
        dataDao.saveShortcuts(shortcuts);
    }

    public void deleteShortcuts(List<Shortcut> shortcuts) {
        dataDao.deleteShortcuts(shortcuts);
    }

    public void deleteShortcut(Shortcut... shortcut) {
        dataDao.deleteShortcht(shortcut);
    }

    public List<Shortcut> getShortcutByHostId(int hostId) {
        return dataDao.getShortcutByHostId(hostId);
    }

    public Shortcut getShortcutById(int id) {
        return dataDao.getShortcutById(id);
    }

    //是否在设备常用列表里
    public boolean isInDeviceShortcut(int mainId) {
        Shortcut shortcut = dataDao.getDeviceShortcutByMainId(mainId);
        return shortcut != null;
    }

    public boolean isInSceneShortcut(int mainId) {
        Shortcut shortcut = dataDao.getSceneShortcutByMainId(mainId);
        return shortcut != null;
    }
}
