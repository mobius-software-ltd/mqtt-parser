package com.mobius.software.mqtt.parser.header.impl;

import com.mobius.software.mqtt.parser.header.api.CountableMessage;
import com.mobius.software.mqtt.parser.header.api.MQDevice;

public class Pubrel implements CountableMessage
{
	private Integer packetID;

	public Pubrel(Integer packetID)
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
		return MessageType.PUBREL;
	}

	@Override
	public void processBy(MQDevice device)
	{
		device.processPubrel(packetID);
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
