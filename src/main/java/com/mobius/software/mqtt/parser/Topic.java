package com.mobius.software.mqtt.parser;

import ua.mobius.media.server.utils.Text;

public class Topic
{
	private static final String SEPARATOR = ":";
	private Text name;
	private QoS qos;

	public Topic()
	{

	}

	public Topic(Text name, QoS qos)
	{
		this.name = name;
		this.qos = qos;
	}

	public String toString()
	{
		return name.toString() + SEPARATOR + qos;
	}

	public Text getName()
	{
		return name;
	}

	public void setName(Text name)
	{
		this.name = name;
	}

	public QoS getQos()
	{
		return qos;
	}

	public void setQos(QoS qos)
	{
		this.qos = qos;
	}

	public int length()
	{
		return name.length();
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Topic other = (Topic) obj;
		if (name == null)
		{
			if (other.name != null)
				return false;
		}
		else if (!name.equals(other.name))
			return false;
		return true;
	}

	public static Topic valueOf(Text topic, QoS qos)
	{
		return new Topic(topic, qos);
	}
}
