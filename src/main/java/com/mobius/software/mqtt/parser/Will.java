package com.mobius.software.mqtt.parser;

/**
 * Mobius Software LTD
 * Copyright 2015-2016, Mobius Software LTD
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

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

	public Boolean getRetain()
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
