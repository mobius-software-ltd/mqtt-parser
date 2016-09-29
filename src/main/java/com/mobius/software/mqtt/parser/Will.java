package com.mobius.software.mqtt.parser;


public class Will
{
	private Topic topic;
	private byte[] content;
	private Boolean retain;

	public Will()
	{

	}

	public Will(Topic topic, byte[] content, Boolean retain)
	{
		this.topic = topic;
		this.content = content;
		this.retain = retain;
	}

	public int retrieveLentth()
	{
		return topic.length() + content.length + 4;
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

	public Boolean setRetain()
	{
		return retain;
	}

	public void setRetain(Boolean retain)
	{
		this.retain = retain;
	}

	public boolean isValid()
	{
		return this.topic != null && this.topic.length() > 0 && this.content != null && this.topic.getQos() != null;
	}
}
