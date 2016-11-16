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

import java.util.List;

import com.mobius.software.mqtt.parser.avps.MessageType;
import com.mobius.software.mqtt.parser.avps.SubackCode;
import com.mobius.software.mqtt.parser.header.api.CountableMessage;
import com.mobius.software.mqtt.parser.header.api.MQDevice;

public class Suback extends CountableMessage
{
	private List<SubackCode> returnCodes;

	public Suback()
	{
		super();
	}

	public Suback(Integer packetID, List<SubackCode> returnCodes)
	{
		super(packetID);
		this.returnCodes = returnCodes;
	}

	public Suback reInit(Integer packetID, List<SubackCode> returnCodes)
	{
		super.reInit(packetID);
		this.returnCodes = returnCodes;
		return this;
	}

	@Override
	public int getLength()
	{
		return 2 + returnCodes.size();
	}

	@Override
	public MessageType getType()
	{
		return MessageType.SUBACK;
	}

	@Override
	public void processBy(MQDevice device)
	{
		device.processSuback(getPacketID(), returnCodes);
	}

	public List<SubackCode> getReturnCodes()
	{
		return returnCodes;
	}

	public void setReturnCodes(List<SubackCode> returnCodes)
	{
		this.returnCodes = returnCodes;
	}
}
