package com.mobius.software.mqtt.parser.header.impl;

import java.util.HashMap;
import java.util.Map;

import com.mobius.software.mqtt.parser.MalformedMessageException;

public enum MessageType
{
	CONNECT(1), CONNACK(2), PUBLISH(3), PUBACK(4), PUBREC(5), PUBREL(6), PUBCOMP(7), SUBSCRIBE(8), SUBACK(9), UNSUBSCRIBE(10), UNSUBACK(11), PINGREQ(12), PINGRESP(13), DISCONNECT(14);

	private int num;

	private static Map<Integer, MessageType> map = new HashMap<Integer, MessageType>();

	static
	{
		for (MessageType legEnum : MessageType.values())
		{
			map.put(legEnum.num, legEnum);
		}
	}

	public int getNum()
	{
		return num;
	}

	private MessageType(final int leg)
	{
		num = leg;
	}

	public static MessageType valueOf(int type) throws MalformedMessageException
	{
		MessageType result = map.get(type);
		if (result == null)
			throw new MalformedMessageException(String.format("Header code undefined: %d", type));
		return result;
	}

}
