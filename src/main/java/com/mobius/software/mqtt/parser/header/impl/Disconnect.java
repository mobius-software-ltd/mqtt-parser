package com.mobius.software.mqtt.parser.header.impl;

import com.mobius.software.mqtt.parser.header.api.MQDevice;
import com.mobius.software.mqtt.parser.header.api.MQMessage;

public class Disconnect implements MQMessage
{
	@Override
	public int getLength()
	{
		return 0;
	}

	@Override
	public MessageType getType()
	{
		return MessageType.DISCONNECT;
	}

	@Override
	public void processBy(MQDevice device)
	{
		device.processDisconnect();
	}
}
