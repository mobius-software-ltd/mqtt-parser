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

import com.mobius.software.mqtt.parser.Will;
import com.mobius.software.mqtt.parser.header.api.MQDevice;
import com.mobius.software.mqtt.parser.header.api.MQMessage;

public class Connect implements MQMessage
{
	private static final byte defaultProtocolLevel = 4;
	private static final String PROTOCOL_NAME = "MQTT";

	private String username;
	private String password;
	private String clientID;

	private byte protocolLevel = defaultProtocolLevel;
	private boolean cleanSession;
	private int keepalive;

	private Will will;

	public Connect(String username, String password, String clientID, boolean isClean, int keepalive, Will will)
	{
		this.username = username;
		this.password = password;
		this.clientID = clientID;
		this.cleanSession = isClean;
		this.keepalive = keepalive;
		this.will = will;
	}

	@Override
	public MessageType getType()
	{
		return MessageType.CONNECT;
	}

	@Override
	public void processBy(MQDevice device)
	{
		device.processConnect(cleanSession, keepalive, will);
	}

	@Override
	public int getLength()
	{
		int length = 10;
		length += clientID.length() + 2;
		length += isWillFlag() ? will.retrieveLentth() : 0;
		length += username != null ? username.length() + 2 : 0;
		length += password != null ? password.length() + 2 : 0;
		return length;
	}

	public int getProtocolLevel()
	{
		return protocolLevel;
	}

	public void setProtocolLevel(int protocolLevel)
	{
		this.protocolLevel = (byte) protocolLevel;
	}

	public boolean isClean()
	{
		return cleanSession;
	}

	public void setCleanSession(boolean cleanSession)
	{
		this.cleanSession = cleanSession;
	}

	public boolean isWillFlag()
	{
		return will != null;
	}

	public Will getWill()
	{
		return will;
	}

	public void setWill(Will will)
	{
		this.will = will;
	}

	public int getKeepAlive()
	{
		return keepalive;
	}

	public void setKeepAlive(int keepAlive)
	{
		this.keepalive = keepAlive;
	}

	public String getClientID()
	{
		return clientID;
	}

	public void setClientID(String clientID)
	{
		this.clientID = clientID;
	}

	public String getUserName()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	public boolean isUsernameFlag()
	{
		return username != null;
	}

	public boolean isPasswordFlag()
	{
		return password != null;
	}

	public String getName()
	{
		return PROTOCOL_NAME;
	}
}
