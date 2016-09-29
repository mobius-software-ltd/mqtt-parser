package com.mobius.software.mqtt.parser.header.impl;

import com.mobius.software.mqtt.parser.header.api.MQDevice;
import com.mobius.software.mqtt.parser.header.api.MQMessage;

public class Pubrec implements MQMessage
{
	private Integer packetID;

	public Pubrec(Integer packetID)
	{
		this.packetID = packetID;
	}

	@Override
	public void processBy(MQDevice device)
	{
		device.processPubrec(packetID);
	}

	@Override
	public int getLength()
	{
		return 2;
	}

	@Override
	public MessageType getType()
	{
		return MessageType.PUBREC;
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
