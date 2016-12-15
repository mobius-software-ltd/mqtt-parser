package com.mobius.software.mqtt.parser.header.api;

import java.util.HashMap;
import java.util.Map;

public enum Protocol
{
	MQTT(1), MQTT_SN(2), COAP(3), AMQP(4);

	private static final Map<Integer, Protocol> intToTypeMap = new HashMap<Integer, Protocol>();
	private static final Map<String, Protocol> strToTypeMap = new HashMap<String, Protocol>();
	static
	{
		for (Protocol type : Protocol.values())
		{
			intToTypeMap.put(type.value, type);
			strToTypeMap.put(type.toString(), type);
		}
	}

	private int value;

	private Protocol(int value)
	{
		this.value = value;
	}

	public static Protocol fromInt(int i)
	{
		Protocol type = intToTypeMap.get(Integer.valueOf(i));
		return type;
	}

	public static Protocol fromString(String value)
	{
		return strToTypeMap.get(value);
	}

	public int getValue()
	{
		return value;
	}
}
