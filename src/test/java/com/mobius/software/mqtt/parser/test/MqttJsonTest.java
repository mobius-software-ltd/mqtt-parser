package com.mobius.software.mqtt.parser.test;

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

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.BeforeClass;
import org.junit.Test;
import com.mobius.software.mqtt.parser.MQJsonParser;
import com.mobius.software.mqtt.parser.avps.QoS;
import com.mobius.software.mqtt.parser.avps.SubackCode;
import com.mobius.software.mqtt.parser.avps.Text;
import com.mobius.software.mqtt.parser.avps.Topic;
import com.mobius.software.mqtt.parser.avps.Will;
import com.mobius.software.mqtt.parser.header.api.MQMessage;
import com.mobius.software.mqtt.parser.header.impl.Connack;
import com.mobius.software.mqtt.parser.header.impl.Connect;
import com.mobius.software.mqtt.parser.header.impl.Disconnect;
import com.mobius.software.mqtt.parser.header.impl.Pingreq;
import com.mobius.software.mqtt.parser.header.impl.Pingresp;
import com.mobius.software.mqtt.parser.header.impl.Puback;
import com.mobius.software.mqtt.parser.header.impl.Pubcomp;
import com.mobius.software.mqtt.parser.header.impl.Publish;
import com.mobius.software.mqtt.parser.header.impl.Pubrec;
import com.mobius.software.mqtt.parser.header.impl.Pubrel;
import com.mobius.software.mqtt.parser.header.impl.Suback;
import com.mobius.software.mqtt.parser.header.impl.Subscribe;
import com.mobius.software.mqtt.parser.header.impl.Unsuback;
import com.mobius.software.mqtt.parser.header.impl.Unsubscribe;
import com.mobius.software.mqtt.parser.avps.ConnackCode;

public class MqttJsonTest {

	private static MQJsonParser parser;
	
	private static Topic topic;
	private static byte[] content;
	private static ByteBuf buffContent;
	
	private static Topic[] topics;
	private static Text[] topicsName;
	
	private static List<SubackCode> returnCodes;
			
	private static String connectExample = "{\"username\":\"John\",\"password\":\"1234567890\",\"clientID\":\"_123456789\",\"protocolLevel\":4,\"cleanSession\":true,\"keepalive\":60,\"will\":{\"topic\":{\"name\":\"lookup\",\"qos\":1},\"content\":\"Sm9objogaSdsbCBiZSBiYWNr\",\"retain\":true},\"willFlag\":true,\"usernameFlag\":true,\"passwordFlag\":true,\"protocolName\":\"MQTT\",\"packet\":1}";
	private static String connackExample = "{\"sessionPresent\":true,\"returnCode\":0,\"packet\":2}";
	private static String publishExample = "{\"packetID\":2,\"topic\":{\"name\":\"lookup\",\"qos\":1},\"content\":\"Sm9objogaSdsbCBiZSBiYWNr\",\"retain\":true,\"dup\":false,\"packet\":3}";
	private static String pubackExample = "{\"packetID\":32,\"packet\":4}";
	private static String pubrecExample = "{\"packetID\":56,\"packet\":5}";
	private static String pubrelExample = "{\"packetID\":45,\"packet\":6}";
	private static String pubcompExample = "{\"packetID\":67,\"packet\":7}";
	private static String subscribeExample = "{\"packetID\":435,\"topics\":[{\"name\":\"lookup\",\"qos\":1},{\"name\":\"lookup\",\"qos\":1},{\"name\":\"lookup\",\"qos\":1}],\"packet\":8}";
	private static String unsubscribeExample = "{\"packetID\":46,\"topics\":[\"some topic\",\"new topic\",\"my topic\"],\"packet\":10}";
	private static String subackExample = "{\"packetID\":4,\"returnCodes\":[0,2,1],\"packet\":9}";
	private static String unsubackExample = "{\"packetID\":97,\"packet\":11}";
	private static String pingreqExample = "{\"packet\":12}";
	private static String pingrespExample = "{\"packet\":13}";
	private static String disconnectExample = "{\"packet\":14}";
	
	@BeforeClass
	public static void init() {
		
		parser = new MQJsonParser();
		
		topic = new Topic(new Text("lookup"), QoS.AT_LEAST_ONCE);
		content = "John: i'll be back".getBytes();
		buffContent = Unpooled.copiedBuffer(content);
				
		topics = new Topic[] { topic, topic, topic };
		topicsName = new Text[] { new Text("some topic"), new Text("new topic"), new Text("my topic") };
		
		returnCodes = new ArrayList<SubackCode>();
		returnCodes.add(SubackCode.ACCEPTED_QOS0);
		returnCodes.add(SubackCode.ACCEPTED_QOS2);
		returnCodes.add(SubackCode.ACCEPTED_QOS1);
	}
	
	@Test
	public void testMqttJsonEncoding()
	{
		try {
						
			String connect = this.packet(this.getConnectPacket());
			assertEquals("connect packet", connectExample, connect);
			
			String connack = this.packet(this.getConnackPacket());
			assertEquals("connack packet", connackExample, connack);

			String publish = this.packet(this.getPublishPacket());
			assertEquals("publish packet", publishExample, publish);

			String puback = this.packet(this.getPubackPacket());
			assertEquals("puback packet", pubackExample, puback);

			String pubrec = this.packet(this.getPubrecPacket());
			assertEquals("pubrec packet", pubrecExample, pubrec);

			String pubrel = this.packet(this.getPubrelPacket());
			assertEquals("pubrel packet",pubrelExample, pubrel);

			String pubcomp = this.packet(this.getPubcompPacket());
			assertEquals("pubcomp packet", pubcompExample, pubcomp);

			String subscribe = this.packet(this.getSubscribePacket());
			assertEquals("subscribe packet", subscribeExample, subscribe);

			String suback = this.packet(this.getSubackPacket());
			assertEquals("suback packet", subackExample, suback);

			String unsubscribe = this.packet(this.getUnsubscribePacket());
			assertEquals("unsubscribe packet", unsubscribeExample, unsubscribe);

			String unsuback = this.packet(this.getUnsubackPacket());
			assertEquals("unsuback packet", unsubackExample, unsuback);

			String pingreq = this.packet(this.getPingreqPacket());
			assertEquals("pingreq packet", pingreqExample, pingreq);

			String pingresp = this.packet(this.getPingrespPacket());
			assertEquals("pingresp packet", pingrespExample, pingresp);

			String disconnect = this.packet(this.getDisconnectPacket());
			assertEquals("disconnect packet", disconnectExample, disconnect);
	
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public <T> String packet(MQMessage message) throws Exception {
		String json = parser.jsonString(message);
		MQMessage newMessage = parser.messageObject(json);
		String newJson = new String(parser.encode(newMessage));
		return (json != null && json.equals(newJson)) ? newJson : null;
	}
	
	public MQMessage getConnectPacket() {
		return new Connect("John", "1234567890", "_123456789", true, 60, new Will(topic, content, true));
	}
	
	public MQMessage getConnackPacket() {
		return new Connack(true, ConnackCode.ACCEPTED);
	}
	
	public MQMessage getPublishPacket() {
		return new Publish(2, topic, buffContent, true, false);
	}
	
	public MQMessage getPubackPacket() {
		return new Puback(32);
	}
	
	public MQMessage getPubrecPacket() {
		return new Pubrec(56);
	}
	
	public MQMessage getPubrelPacket() {
		return new Pubrel(45);
	}
	
	public MQMessage getPubcompPacket() {
		return new Pubcomp(67);
	}
	
	public MQMessage getSubscribePacket() {
		return new Subscribe(435, topics);
	}
	
	public MQMessage getUnsubscribePacket() {
		return new Unsubscribe(46, topicsName);
	}
	
	public MQMessage getSubackPacket() {
		return new Suback(4, returnCodes);
	}
	
	public MQMessage getUnsubackPacket() {
		return new Unsuback(97);
	}
	
	public MQMessage getPingreqPacket() {
		return new Pingreq();
	}
	
	public MQMessage getPingrespPacket() {
		return new Pingresp();
	}
	
	public MQMessage getDisconnectPacket() {
		return new Disconnect();
	}
		
}
