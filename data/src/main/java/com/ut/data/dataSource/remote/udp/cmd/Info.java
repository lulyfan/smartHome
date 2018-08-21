package com.ut.data.dataSource.remote.udp.cmd;

public class Info {
	
	
	public static class LocalDeviceType {
		public static final int GUIDE_CONTROL_HOST = 0x00;      //导轨式家居控制主机
		public static final int BOX_CONTROL_HOST = 0x01;        //信息箱控制主机
		public static final int WIRELESS_CONTROL_HOST = 0x02;   //无线控制主机(门锁网关)
		public static final int PC = 0x03;                      //PC端IDE集成客户端
		public static final int IOS_PHONE = 0x04;               //IOS平台手机APP
		public static final int ANDROID_PHONE = 0x05;           //android平台手机APP
		public static final int CLOUD_SERVER = 0x06;            //云服务器
	}
	
	public static class LinkCMD {
		public static final int SINGLE_DATA_FRAME = 0x01;        //单帧数据帧命令
		public static final int MULTI_DATA_FRAME = 0x02;         //多帧数据帧命令
		public static final int MULTI_INFO_FRAME = 0x03;         //多帧信息帧命令
		public static final int ACK = 0x7F;                      //确认ACK
		public static final int NAK = 0x80;                      //不确认NAK
	}
	
	public static class AppCMD {
		public static final int WRITE_LANGUAGE_CONFIG = 0x01;     //上位机写入语言配置数据
		public static final int READ_LANGUAGE_CONFIG = 0x02;      //上位机读取语言配置数据
		public static final int READ_DEVICE_STATE = 0x03;         //上位机读取总设备状态
		public static final int READ_BUS_NODE_INFO = 0x04;        //上位机读取总线节点信息
		public static final int READ_PRODUCT_VERSION_INFO = 0x05; //上位机读取产品版本信息
		public static final int SET_HOST_MODE = 0x06;             //上位机设置主机运行模式
		public static final int CONTROL_DEVICE = 0x07;            //上位机遥控设备指令
		public static final int MODIFY_LOGIN_PASSSWORD = 0x08;    //上位机修改登录密码
		public static final int WRITE_TIME = 0x09;                //上位机写入时间信息
		public static final int READ_TIME = 0x0A;                 //上位机读取主机时间信息
		public static final int WRITE_HOST_NAME = 0x0B;           //上位机写入主机名称
		public static final int READ_HOST_NAME = 0x0C;            //上位机读取主机名称
		public static final int WRITE_SERIAL_NUMBER = 0x0D;       //上位机写入生产序列号
		public static final int READ_SERIAL_NUMBER = 0x0E;        //上位机读取生产序列号
		public static final int LOGIN_HOST = 0x0F;                //上位机登录主机
		public static final int LOGOUT_HOST = 0x10;               //上位机注销登录
		public static final int LINK_HOST = 0x11;                 //上位机LINK主机
		public static final int BROCAST_SEARCH_HOST = 0x12;       //上位机广播搜索新主机
		public static final int CONTROL_JUMP = 0x13;              //上位机控制跳转命令
		public static final int UPDATE_HOST_FIREWARE = 0x14;      //上位机升级主机固件
		public static final int UPDATE_BUS_NODE_FIREWARE = 0x15;  //上位机升级总线节点固件
		public static final int UPLOAD_DEVICE_STATE = 0x40;       //主机上传设备状态
		public static final int LINK_SERVER = 0x41;               //主机LINK服务器
		public static final int SERVER_OPERATE_HOST_SCENE = 0x60; //服务器操作主机场景
		public static final int SERVER_WRITE_SCENE = 0x61;        //服务器写入场景表数据
		public static final int SERVER_READ_SCENE = 0x62;         //服务器读取场景表数据
		public static final int SERVER_UPDATE_HOST_HOLIDAY_TABLE = 0x63; //服务器更新主机节日表
		public static final int SERVER_READ_HOST_HOLIDAY_TABLE = 0x64;   //服务器读取主机节日表
		public static final int SERVER_OPERATE_DOORLOCK_AUTHORIZE_TABLE = 0x65; //服务器操作智能门锁授权表
		
		public static final int WRITE_HOST_CONFIG = 0xA0;         //上位机写入主机配置信息
		public static final int READ_HOST_CONFIG = 0xA1;          //上位机读取主机配置信息
		public static final int WRITE_NODE_PRODUCT_CONFIG = 0xA2; //上位机写入节点产品配置信息
		public static final int READ_NODE_PRODUCT_CONFIG = 0xA3;  //上位机读取节点产品信息配置
		public static final int WRITE_NODE_DEVICE_PARA = 0xA4;    //上位机写入节点设备参数信息
		public static final int READ_NODE_DEVICE_PARA = 0xA5;     //上位机读取节点设备参数信息
		public static final int BIND_RELATED_DEVICE = 0xA6;       //上位机绑定关联设备
		public static final int DETECT_RELATED_DEVICE = 0xA7;     //上位机检测关联设备
	}
}
