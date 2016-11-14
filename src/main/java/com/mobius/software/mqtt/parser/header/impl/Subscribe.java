package com.mobius.software.mqtt.parser.header.impl;

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

import com.mobius.software.mqtt.parser.Topic;
import com.mobius.software.mqtt.parser.header.api.CountableMessage;
import com.mobius.software.mqtt.parser.header.api.MQDevice;

public class Subscribe extends CountableMessage
{
	private Topic[] topics;

	public Subscribe(Topic[] topics)
	{
		this(null, topics);
	}

	public Subscribe(Integer packetID, Topic[] topics)
	{
		super(packetID);
		this.topics = topics;
	}

	@Override
	public int getLength()
	{
		int length = 0;
		length += getPacketID() != null ? 2 : 0;
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
		device.processSubscribe(getPacketID(), topics);
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
