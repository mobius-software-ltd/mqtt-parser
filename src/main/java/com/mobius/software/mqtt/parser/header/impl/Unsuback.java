package com.mobius.software.mqtt.parser.header.impl;

import com.mobius.software.mqtt.parser.header.api.MQDevice;
import com.mobius.software.mqtt.parser.header.api.MQMessage;

public class Unsuback implements MQMessage
{
	private Integer packetID;

	public Unsuback(Integer packetID)
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
		return MessageType.UNSUBACK;
	}

	@Override
	public void processBy(MQDevice device)
	{
		device.processUnsuback(packetID);
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
