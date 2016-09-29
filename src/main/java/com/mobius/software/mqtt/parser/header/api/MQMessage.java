package com.mobius.software.mqtt.parser.header.api;

import com.mobius.software.mqtt.parser.header.impl.MessageType;

public interface MQMessage
{
	int getLength();

	MessageType getType();

	void processBy(MQDevice device);
}
