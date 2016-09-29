package com.mobius.software.mqtt.parser.header.impl;

import java.util.HashMap;
import java.util.Map;

public enum ConnackCode
{
	ACCEPTED(0), UNACCEPTABLE_PROTOCOL_VERSION(1), IDENTIFIER_REJECTED(2), SERVER_UNUVALIABLE(3), BAD_USER_OR_PASS(4), NOT_AUTHORIZED(5);

	private int num;

	private static Map<Integer, ConnackCode> map = new HashMap<Integer, ConnackCode>();

	static
	{
		for (ConnackCode legEnum : ConnackCode.values())
		{
			map.put(legEnum.num, legEnum);
		}
	}

	public byte getNum()
	{
		return (byte) num;
	}

	private ConnackCode(final int leg)
	{
		num = leg;
	}

	public static ConnackCode valueOf(int type)
	{
		return map.get(type);
	}

}
