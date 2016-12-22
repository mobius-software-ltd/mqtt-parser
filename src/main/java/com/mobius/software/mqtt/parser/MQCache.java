package com.mobius.software.mqtt.parser;

import com.mobius.software.mqtt.parser.avps.MessageType;
import com.mobius.software.mqtt.parser.header.api.MQMessage;

public interface MQCache
{
	MQMessage borrowMessage(MessageType type);

	void returnMessage(MQMessage message);
}
