package com.mobius.software.mqtt.parser.header.impl;

import java.util.ArrayList;
import java.util.List;

import com.mobius.software.mqtt.parser.header.api.MQDevice;
import com.mobius.software.mqtt.parser.header.api.MQMessage;

public class Suback implements MQMessage
{
	private Integer packetID;
	private List<SubackCode> returnCodes = new ArrayList<>();

	public Suback(Integer packetID, List<SubackCode> returnCodes)
	{
		this.packetID = packetID;
		this.returnCodes = returnCodes;
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
		device.processSuback(packetID, returnCodes);
	}

	public Integer getPacketID()
	{
		return packetID;
	}

	public void setPacketID(Integer packetID)
	{
		this.packetID = packetID;
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
