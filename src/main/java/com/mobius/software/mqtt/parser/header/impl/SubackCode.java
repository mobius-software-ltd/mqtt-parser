package com.mobius.software.mqtt.parser.header.impl;

import java.util.HashMap;
import java.util.Map;

public enum SubackCode
{
	ACCEPTED_QOS0(0), ACCEPTED_QOS1(1), ACCEPTED_QOS2(2), FAILURE(128);

	private int num;

	private static Map<Integer, SubackCode> map = new HashMap<Integer, SubackCode>();

	static
	{
		for (SubackCode legEnum : SubackCode.values())
		{
			map.put(legEnum.num, legEnum);
		}
	}

	public byte getNum()
	{
		return (byte) num;
	}

	private SubackCode(final int leg)
	{
		num = leg;
	}

	public static SubackCode valueOf(int type)
	{
		return map.get(type);
	}

}
