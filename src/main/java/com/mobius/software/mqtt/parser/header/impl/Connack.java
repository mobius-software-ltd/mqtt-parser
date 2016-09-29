package com.mobius.software.mqtt.parser.header.impl;

import com.mobius.software.mqtt.parser.header.api.MQDevice;
import com.mobius.software.mqtt.parser.header.api.MQMessage;

public class Connack implements MQMessage
{
	private boolean sessionPresent;
	private ConnackCode returnCode;

	public Connack(boolean sessionPresent, ConnackCode returnCode)
	{
		this.sessionPresent = sessionPresent;
		this.returnCode = returnCode;
	}

	@Override
	public int getLength()
	{
		return 2;
	}

	@Override
	public void processBy(MQDevice device)
	{
		device.processConnack(returnCode, sessionPresent);
	}

	@Override
	public MessageType getType()
	{
		return MessageType.CONNACK;
	}

	public boolean isSessionPresent()
	{
		return sessionPresent;
	}

	public void setSessionPresent(boolean sessionPresent)
	{
		this.sessionPresent = sessionPresent;
	}

	public ConnackCode getReturnCode()
	{
		return returnCode;
	}

	public void setReturnCode(ConnackCode returnCode)
	{
		this.returnCode = returnCode;
	}
}
