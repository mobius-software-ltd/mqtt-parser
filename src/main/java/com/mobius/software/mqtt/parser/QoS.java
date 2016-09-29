package com.mobius.software.mqtt.parser;

import java.util.HashMap;
import java.util.Map;

public enum QoS
{
	AT_MOST_ONCE((byte) 0), AT_LEAST_ONCE((byte) 1), EXACTLY_ONCE((byte) 2);

	private byte value;

	private static final Map<Integer, QoS> intToTypeMap = new HashMap<Integer, QoS>();
	private static final Map<String, QoS> strToTypeMap = new HashMap<String, QoS>();

	static
	{
		for (QoS type : QoS.values())
		{
			intToTypeMap.put((int) type.value, type);
			strToTypeMap.put(type.name(), type);
		}
	}

//	@JsonValue
	public int getValue()
	{
		return value;
	}

	/*@JsonCreator
	public static QoS forValue(String value)
	{
		Integer intValue = null;
		try
		{
			intValue = Integer.parseInt(value);
		}
		catch (Exception ex)
		{

		}

		if (intValue != null)
			return intToTypeMap.get(intValue);
		else
			return strToTypeMap.get(value);
	}*/

	private QoS(final byte leg)
	{
		value = leg;
	}

	public static QoS valueOf(int type) throws MalformedMessageException
	{
		return intToTypeMap.get(type);
	}

	public int compare(QoS qos)
	{
		if (value == qos.getValue())
			return 0;
		if (value > qos.getValue())
			return 1;
		return -1;
	}
}
