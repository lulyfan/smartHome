package com.ut.data.util;


public abstract class CrcCheckBase {
	public abstract boolean Check(byte[] checkData);

	public abstract boolean GetCheckCode(byte[] checkData);
}