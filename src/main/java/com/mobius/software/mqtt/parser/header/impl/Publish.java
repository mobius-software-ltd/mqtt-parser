package com.mobius.software.mqtt.parser.header.impl;

import com.mobius.software.mqtt.parser.Topic;
import com.mobius.software.mqtt.parser.header.api.CountableMessage;
import com.mobius.software.mqtt.parser.header.api.MQDevice;

public class Publish implements CountableMessage
{
	private Integer packetID;
	private Topic topic;
	private byte[] content;
	private boolean retain;
	private boolean dup;

	public Publish(Topic topic, byte[] content, boolean retain, boolean dup)
	{
		this(null, topic, content, retain, dup);
	}

	public Publish(Integer packetID, Topic topic, byte[] content, boolean retain, boolean dup)
	{
		this.packetID = packetID;
		this.topic = topic;
		this.content = content;
		this.retain = retain;
		this.dup = dup;
	}

	@Override
	public MessageType getType()
	{
		return MessageType.PUBLISH;
	}

	@Override
	public void processBy(MQDevice device)
	{
		device.processPublish(packetID, topic, content, retain, dup);
	}

	@Override
	public int getLength()
	{
		int length = 0;
		length += packetID != null ? 2 : 0;
		length += topic.length() + 2;
		length += content.length;
		return length;
	}

	public Integer getPacketID()
	{
		return packetID;
	}

	public void setPacketID(Integer packetID)
	{
		this.packetID = packetID;
	}

	public Topic getTopic()
	{
		return topic;
	}

	public void setTopic(Topic topic)
	{
		this.topic = topic;
	}

	public byte[] getContent()
	{
		return content;
	}

	public void setContent(byte[] content)
	{
		this.content = content;
	}

	public boolean isRetain()
	{
		return retain;
	}

	public void setRetain(boolean retain)
	{
		this.retain = retain;
	}

	public boolean isDup()
	{
		return dup;
	}

	public void setDup(boolean dup)
	{
		this.dup = dup;
	}
}
