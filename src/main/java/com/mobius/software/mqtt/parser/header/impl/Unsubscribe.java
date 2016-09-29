package com.mobius.software.mqtt.parser.header.impl;

import com.mobius.software.mqtt.parser.header.api.CountableMessage;
import com.mobius.software.mqtt.parser.header.api.MQDevice;

import ua.mobius.media.server.utils.Text;

public class Unsubscribe implements CountableMessage
{
	private Integer packetID;
	private Text[] topics;

	public Unsubscribe(Text[] topics)
	{
		this(null, topics);
	}

	public Unsubscribe(Integer packetID, Text[] topics)
	{
		this.packetID = packetID;
		this.topics = topics;
	}

	@Override
	public int getLength()
	{
		int length = 2;
		for (Text topic : topics)
			length += topic.length() + 2;
		return length;
	}

	@Override
	public MessageType getType()
	{
		return MessageType.UNSUBSCRIBE;
	}

	@Override
	public void processBy(MQDevice device)
	{
		device.processUnsubscribe(packetID, topics);
	}

	public Integer getPacketID()
	{
		return packetID;
	}

	public void setPacketID(Integer packetID)
	{
		this.packetID = packetID;
	}

	public Text[] getTopics()
	{
		return topics;
	}

	public void setTopics(Text[] topics)
	{
		this.topics = topics;
	}
}
