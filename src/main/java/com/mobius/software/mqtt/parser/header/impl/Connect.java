package com.mobius.software.mqtt.parser.header.impl;

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
