package com.mobius.software.mqtt.parser.header.api;

import java.util.List;

import com.mobius.software.mqtt.parser.Text;
import com.mobius.software.mqtt.parser.Topic;
import com.mobius.software.mqtt.parser.Will;
import com.mobius.software.mqtt.parser.header.impl.ConnackCode;
import com.mobius.software.mqtt.parser.header.impl.SubackCode;

public interface MQDevice
{
	void processConnect(boolean cleanSession, int keepalive, Will will);

	void processConnack(ConnackCode code, boolean sessionPresent);

	void processSubscribe(Integer packetID, Topic[] topics);

	void processSuback(Integer packetID, List<SubackCode> codes);

	void processUnsubscribe(Integer packetID, Text[] topics);

	void processUnsuback(Integer packetID);

	void processPublish(Integer packetID, Topic topic, byte[] content, boolean retain, boolean isDup);

	void processPuback(Integer packetID);

	void processPubrec(Integer packetID);

	void processPubrel(Integer packetID);

	void processPubcomp(Integer packetID);

	void processPingreq();

	void processPingresp();

	void processDisconnect();
}
