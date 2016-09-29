package com.mobius.software.mqtt.parser.header.api;

public interface CountableMessage extends MQMessage
{
	Integer getPacketID();

	void setPacketID(Integer packetID);
}
