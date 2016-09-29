package com.mobius.software.mqtt.parser.header.impl;

import com.mobius.software.mqtt.parser.Topic;
import com.mobius.software.mqtt.parser.header.api.CountableMessage;
import com.mobius.software.mqtt.parser.header.api.MQDevice;

public class Subscribe implements CountableMessage
{
	private Integer packetID;
	private Topic[] topics;

	public Subscribe(Topic[] topics)
	{
		this(null, topics);
	}

	public Subscribe(Integer packetID, Topic[] topics)
	{
		this.packetID = packetID;
		this.topics = topics;
	}

	@Override
	public int getLength()
	{
		int length = 0;
		length += this.packetID != null ? 2 : 0;
		for (Topic s : this.topics)
			length += s.getName().length() + 3;
		return length;
	}

	@Override
	public MessageType getType()
	{
		return MessageType.SUBSCRIBE;
	}

	@Override
	public void processBy(MQDevice device)
	{
		device.processSubscribe(packetID, topics);
	}

	public Integer getPacketID()
	{
		return packetID;
	}

	public void setPacketID(Integer packetID)
	{
		this.packetID = packetID;
	}

	public Topic[] getTopics()
	{
		return topics;
	}

	public void setTopics(Topic[] topics)
	{
		this.topics = topics;
	}
}
