package com.mobius.software.mqtt.parser.header.impl;

import com.mobius.software.mqtt.parser.header.api.MQDevice;
import com.mobius.software.mqtt.parser.header.api.MQMessage;

public class Puback implements MQMessage
{
	private Integer packetID;

	public Puback(Integer packetID)
	{
		this.packetID = packetID;
	}

	@Override
	public int getLength()
	{
		return 2;
	}

	@Override
	public MessageType getType()
	{
		return MessageType.PUBACK;
	}

	@Override
	public void processBy(MQDevice device)
	{
		device.processPuback(packetID);
	}

	public Integer getPacketID()
	{
		return packetID;
	}

	public void setPacketID(Integer packetID)
	{
		this.packetID = packetID;
	}
}
