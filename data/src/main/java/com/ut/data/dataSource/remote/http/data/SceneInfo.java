package com.ut.data.dataSource.remote.http.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class SceneInfo {

    @PrimaryKey
    private int id;
    private String hostSceneId;
    private int timer;
    private String weeks;
    private String imageUrl;
    private String name;
    private String time;
    private String hostIds;

    @Ignore
    private boolean isSelect;  //是否已被加入常用列表，在首页中用到
    @Ignore
    private WeekItem[] week;
    @Ignore
    private boolean open;

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    @Override
    public String toString() {
        return "scene name:" + name;
    }

    public String getHostSceneId() {
        return hostSceneId;
    }

    public void setHostSceneId(String hostSceneId) {
        this.hostSceneId = hostSceneId;
    }

    public int getTimer() {
        return timer;
    }

    public void setTimer(int timer) {
        this.timer = timer;
    }

    public String getWeeks() {
        return weeks;
    }

    public void setWeeks(String weeks) {
        this.weeks = weeks;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getHostIds() {
        return hostIds;
    }

    public void setHostIds(String hostIds) {
        this.hostIds = hostIds;
    }

    public WeekItem[] getWeek() {
        return week;
    }

    public void setWeek(String weeks) {
        this.week = parseWeeksToWeek(weeks);
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }


    public static WeekItem[] parseWeeksToWeek(String weeks) {
        if (weeks == null) {
            return null;
        }

        String[] days = weeks.split(",");
        WeekItem[] weekItems = {new WeekItem("一"),
                                new WeekItem("二"),
                                new WeekItem("三"),
                                new WeekItem("四"),
                                new WeekItem("五"),
                                new WeekItem("六"),
                                new WeekItem("日")};

        for (String day : days) {
            try {
                int i = Integer.parseInt(day) - 1;
                weekItems[i].value = 1;
            } catch (NumberFormatException e) {

            }
        }

        return weekItems;
    }

    static class WeekItem {

        String name;
        int value;

        public WeekItem(String name, int value) {
            this.name = name;
            this.value = value;
        }

        public WeekItem(String name) {
            this(name, 0);
        }
    }

}
