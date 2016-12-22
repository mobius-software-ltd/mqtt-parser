package com.mobius.software.mqtt.parser;

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

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import com.mobius.software.mqtt.parser.avps.*;
import com.mobius.software.mqtt.parser.exceptions.MalformedMessageException;
import com.mobius.software.mqtt.parser.header.api.CountableMessage;
import com.mobius.software.mqtt.parser.header.api.MQMessage;
import com.mobius.software.mqtt.parser.header.impl.*;
import com.mobius.software.mqtt.parser.util.StringVerifier;

public class MQParser
{
	public static final Disconnect DISCONNECT = new Disconnect();
	public static final Pingreq PINGREQ = new Pingreq();
	public static final Pingresp PINGRESP = new Pingresp();

	private MQCache cache;

	public MQParser(MQCache cache)
	{
		this.cache = cache;
	}

	public static ByteBuf next(ByteBuf buf) throws MalformedMessageException
	{
		buf.markReaderIndex();
		MessageType type = MessageType.valueOf(((buf.readByte() >> 4) & 0xf));

		if (type == null)
		{
			buf.resetReaderIndex();
			throw new MalformedMessageException("invalid message type decoding");
		}

		switch (type)
		{
		case PINGREQ:
		case PINGRESP:
		case DISCONNECT:
			buf.resetReaderIndex();
			return Unpooled.buffer(2);
		default:
			LengthDetails length = LengthDetails.decode(buf);
			buf.resetReaderIndex();
			if (length.getLength() == 0)
				return null;
			int result = length.getLength() + length.getSize() + 1;
			if (result > buf.readableBytes())
				throw new MalformedMessageException("invalid length decoding for " + type + " result length:" + result + ", in buffer:" + buf.readableBytes());

			return Unpooled.buffer(result);
		}
	}

	public MQMessage decodeUsingCache(ByteBuf buf) throws MalformedMessageException
	{
		byte fixedHeader = buf.readByte();

		LengthDetails length = LengthDetails.decode(buf);

		MessageType type = MessageType.valueOf((fixedHeader >> 4) & 0xf);
		MQMessage header = cache.borrowMessage(type);
		try
		{
			switch (type)
			{
			case CONNECT:

				byte[] nameValue = new byte[buf.readUnsignedShort()];
				buf.readBytes(nameValue, 0, nameValue.length);
				String name = new String(nameValue, "UTF-8");
				if (!name.equals("MQTT"))
					throw new MalformedMessageException("CONNECT, protocol-name set to " + name);

				int protocolLevel = buf.readUnsignedByte();

				byte contentFlags = buf.readByte();

				boolean userNameFlag = ((contentFlags >> 7) & 1) == 1 ? true : false;
				boolean userPassFlag = ((contentFlags >> 6) & 1) == 1 ? true : false;
				boolean willRetain = ((contentFlags >> 5) & 1) == 1 ? true : false;
				QoS willQos = QoS.valueOf(((contentFlags & 0x1f) >> 3) & 3);
				if (willQos == null)
					throw new MalformedMessageException("CONNECT, will QoS set to " + willQos);
				boolean willFlag = (((contentFlags >> 2) & 1) == 1) ? true : false;

				if (willQos.getValue() > 0 && !willFlag)
					throw new MalformedMessageException("CONNECT, will QoS set to " + willQos + ", willFlag not set");

				if (willRetain && !willFlag)
					throw new MalformedMessageException("CONNECT, will retain set, willFlag not set");

				boolean cleanSession = ((contentFlags >> 1) & 1) == 1 ? true : false;

				boolean reservedFlag = (contentFlags & 1) == 1 ? true : false;
				if (reservedFlag)
					throw new MalformedMessageException("CONNECT, reserved flag set to true");

				int keepalive = buf.readUnsignedShort();

				byte[] clientIdValue = new byte[buf.readUnsignedShort()];
				buf.readBytes(clientIdValue, 0, clientIdValue.length);
				String clientID = new String(clientIdValue, "UTF-8");
				if (!StringVerifier.verify(clientID))
					throw new MalformedMessageException("ClientID contains restricted characters: U+0000, U+D000-U+DFFF");

				Text willTopic = null;
				byte[] willMessage = null;
				String username = null;
				String password = null;

				Will will = null;
				if (willFlag)
				{
					if (buf.readableBytes() < 2)
						throw new MalformedMessageException("Invalid encoding will/username/password");

					byte[] willTopicValue = new byte[buf.readUnsignedShort()];
					if (buf.readableBytes() < willTopicValue.length)
						throw new MalformedMessageException("Invalid encoding will/username/password");

					buf.readBytes(willTopicValue, 0, willTopicValue.length);

					String willTopicName = new String(willTopicValue, "UTF-8");
					if (!StringVerifier.verify(willTopicName))
						throw new MalformedMessageException("WillTopic contains one or more restricted characters: U+0000, U+D000-U+DFFF");
					willTopic = new Text(willTopicName);

					if (buf.readableBytes() < 2)
						throw new MalformedMessageException("Invalid encoding will/username/password");

					willMessage = new byte[buf.readUnsignedShort()];
					if (buf.readableBytes() < willMessage.length)
						throw new MalformedMessageException("Invalid encoding will/username/password");

					buf.readBytes(willMessage, 0, willMessage.length);
					if (willTopic.length() == 0)
						throw new MalformedMessageException("invalid will encoding");
					will = new Will(new Topic(willTopic, willQos), willMessage, willRetain);
					if (!will.isValid())
						throw new MalformedMessageException("invalid will encoding");
				}

				if (userNameFlag)
				{
					if (buf.readableBytes() < 2)
						throw new MalformedMessageException("Invalid encoding will/username/password");

					byte[] userNameValue = new byte[buf.readUnsignedShort()];
					if (buf.readableBytes() < userNameValue.length)
						throw new MalformedMessageException("Invalid encoding will/username/password");

					buf.readBytes(userNameValue, 0, userNameValue.length);
					username = new String(userNameValue, "UTF-8");
					if (!StringVerifier.verify(username))
						throw new MalformedMessageException("Username contains one or more restricted characters: U+0000, U+D000-U+DFFF");
				}

				if (userPassFlag)
				{
					if (buf.readableBytes() < 2)
						throw new MalformedMessageException("Invalid encoding will/username/password");

					byte[] userPassValue = new byte[buf.readUnsignedShort()];
					if (buf.readableBytes() < userPassValue.length)
						throw new MalformedMessageException("Invalid encoding will/username/password");

					buf.readBytes(userPassValue, 0, userPassValue.length);
					password = new String(userPassValue, "UTF-8");
					if (!StringVerifier.verify(password))
						throw new MalformedMessageException("Password contains one or more restricted characters: U+0000, U+D000-U+DFFF");
				}

				if (buf.readableBytes() > 0)
					throw new MalformedMessageException("Invalid encoding will/username/password");

				Connect connect = (Connect) header;
				connect.reInit(username, password, clientID, cleanSession, keepalive, will);
				if (protocolLevel != 4)
					connect.setProtocolLevel(protocolLevel);
				break;

			case CONNACK:
				byte sessionPresentValue = buf.readByte();
				if (sessionPresentValue != 0 && sessionPresentValue != 1)
					throw new MalformedMessageException(String.format("CONNACK, session-present set to %d", sessionPresentValue & 0xff));
				boolean isPresent = sessionPresentValue == 1 ? true : false;

				short connackByte = buf.readUnsignedByte();
				ConnackCode connackCode = ConnackCode.valueOf(connackByte);
				if (connackCode == null)
					throw new MalformedMessageException("Invalid connack code: " + connackByte);
				Connack connack = (Connack) header;
				connack.reInit(isPresent, connackCode);
				break;

			case PUBLISH:

				fixedHeader &= 0xf;

				boolean dup = ((fixedHeader >> 3) & 1) == 1 ? true : false;

				QoS qos = QoS.valueOf((fixedHeader & 0x07) >> 1);
				if (qos == null)
					throw new MalformedMessageException("invalid QoS value");
				if (dup && qos == QoS.AT_MOST_ONCE)
					throw new MalformedMessageException("PUBLISH, QoS-0 dup flag present");

				boolean retain = ((fixedHeader & 1) == 1) ? true : false;

				byte[] topicNameValue = new byte[buf.readUnsignedShort()];
				buf.readBytes(topicNameValue, 0, topicNameValue.length);
				String topicName = new String(topicNameValue, "UTF-8");
				if (!StringVerifier.verify(topicName))
					throw new MalformedMessageException("Publish-topic contains one or more restricted characters: U+0000, U+D000-U+DFFF");

				Integer packetID = null;
				if (qos != QoS.AT_MOST_ONCE)
				{
					packetID = buf.readUnsignedShort();
					if (packetID < 0 || packetID > 65535)
						throw new MalformedMessageException("Invalid PUBLISH packetID encoding");
				}

				ByteBuf data = Unpooled.buffer(buf.readableBytes());
				data.writeBytes(buf);

				Publish publish = (Publish) header;
				publish.reInit(packetID, new Topic(new Text(topicName), qos), data, retain, dup);
				break;

			case PUBACK:
			case PUBREC:
			case PUBREL:
			case PUBCOMP:
			case UNSUBACK:
				CountableMessage countable = (CountableMessage) header;
				countable.reInit(buf.readUnsignedShort());
				break;

			case SUBSCRIBE:
				Integer subID = buf.readUnsignedShort();
				List<Topic> subscriptions = new ArrayList<>();
				while (buf.isReadable())
				{
					byte[] value = new byte[buf.readUnsignedShort()];
					buf.readBytes(value, 0, value.length);
					QoS requestedQos = QoS.valueOf(buf.readByte());
					if (requestedQos == null)
						throw new MalformedMessageException("Subscribe qos must be in range from 0 to 2: " + requestedQos);
					String topic = new String(value, "UTF-8");
					if (!StringVerifier.verify(topic))
						throw new MalformedMessageException("Subscribe topic contains one or more restricted characters: U+0000, U+D000-U+DFFF");
					Topic subscription = new Topic(new Text(topic), requestedQos);
					subscriptions.add(subscription);
				}
				if (subscriptions.isEmpty())
					throw new MalformedMessageException("Subscribe with 0 topics");

				Subscribe subscribe = (Subscribe) header;
				subscribe.reInit(subID, subscriptions.toArray(new Topic[subscriptions.size()]));
				break;

			case SUBACK:
				Integer subackID = buf.readUnsignedShort();
				List<SubackCode> subackCodes = new ArrayList<>();
				while (buf.isReadable())
				{
					short subackByte = buf.readUnsignedByte();
					SubackCode subackCode = SubackCode.valueOf(subackByte);
					if (subackCode == null)
						throw new MalformedMessageException("Invalid suback code: " + subackByte);
					subackCodes.add(subackCode);
				}
				if (subackCodes.isEmpty())
					throw new MalformedMessageException("Suback with 0 return-codes");

				Suback suback = (Suback) header;
				suback.reInit(subackID, subackCodes);
				break;

			case UNSUBSCRIBE:
				Integer unsubID = buf.readUnsignedShort();
				List<Text> unsubscribeTopics = new ArrayList<>();
				while (buf.isReadable())
				{
					byte[] value = new byte[buf.readUnsignedShort()];
					buf.readBytes(value, 0, value.length);
					String topic = new String(value, "UTF-8");
					if (!StringVerifier.verify(topic))
						throw new MalformedMessageException("Unsubscribe topic contains one or more restricted characters: U+0000, U+D000-U+DFFF");
					unsubscribeTopics.add(new Text(topic));
				}
				if (unsubscribeTopics.isEmpty())
					throw new MalformedMessageException("Unsubscribe with 0 topics");
				Unsubscribe unsubscribe = (Unsubscribe) header;
				unsubscribe.reInit(unsubID, unsubscribeTopics.toArray(new Text[unsubscribeTopics.size()]));
				break;

			case PINGREQ:
			case PINGRESP:
			case DISCONNECT:
				break;

			default:
				throw new MalformedMessageException("Invalid header type: " + type);
			}

			if (buf.isReadable())
				throw new MalformedMessageException("unexpected bytes in content");

			if (length.getLength() != header.getLength())
				throw new MalformedMessageException(String.format("Invalid length. Encoded: %d, actual: %d", length.getLength(), header.getLength()));

			return header;
		}
		catch (UnsupportedEncodingException e)
		{
			throw new MalformedMessageException("unsupported string encoding:" + e.getMessage());
		}
	}

	public static MQMessage decode(ByteBuf buf) throws MalformedMessageException
	{
		MQMessage header = null;

		byte fixedHeader = buf.readByte();

		LengthDetails length = LengthDetails.decode(buf);

		MessageType type = MessageType.valueOf((fixedHeader >> 4) & 0xf);
		try
		{
			switch (type)
			{
			case CONNECT:

				byte[] nameValue = new byte[buf.readUnsignedShort()];
				buf.readBytes(nameValue, 0, nameValue.length);
				String name = new String(nameValue, "UTF-8");
				if (!name.equals("MQTT"))
					throw new MalformedMessageException("CONNECT, protocol-name set to " + name);

				int protocolLevel = buf.readUnsignedByte();

				byte contentFlags = buf.readByte();

				boolean userNameFlag = ((contentFlags >> 7) & 1) == 1 ? true : false;
				boolean userPassFlag = ((contentFlags >> 6) & 1) == 1 ? true : false;
				boolean willRetain = ((contentFlags >> 5) & 1) == 1 ? true : false;
				QoS willQos = QoS.valueOf(((contentFlags & 0x1f) >> 3) & 3);
				if (willQos == null)
					throw new MalformedMessageException("CONNECT, will QoS set to " + willQos);
				boolean willFlag = (((contentFlags >> 2) & 1) == 1) ? true : false;

				if (willQos.getValue() > 0 && !willFlag)
					throw new MalformedMessageException("CONNECT, will QoS set to " + willQos + ", willFlag not set");

				if (willRetain && !willFlag)
					throw new MalformedMessageException("CONNECT, will retain set, willFlag not set");

				boolean cleanSession = ((contentFlags >> 1) & 1) == 1 ? true : false;

				boolean reservedFlag = (contentFlags & 1) == 1 ? true : false;
				if (reservedFlag)
					throw new MalformedMessageException("CONNECT, reserved flag set to true");

				int keepalive = buf.readUnsignedShort();

				byte[] clientIdValue = new byte[buf.readUnsignedShort()];
				buf.readBytes(clientIdValue, 0, clientIdValue.length);
				String clientID = new String(clientIdValue, "UTF-8");
				if (!StringVerifier.verify(clientID))
					throw new MalformedMessageException("ClientID contains restricted characters: U+0000, U+D000-U+DFFF");

				Text willTopic = null;
				byte[] willMessage = null;
				String username = null;
				String password = null;

				Will will = null;
				if (willFlag)
				{
					if (buf.readableBytes() < 2)
						throw new MalformedMessageException("Invalid encoding will/username/password");

					byte[] willTopicValue = new byte[buf.readUnsignedShort()];
					if (buf.readableBytes() < willTopicValue.length)
						throw new MalformedMessageException("Invalid encoding will/username/password");

					buf.readBytes(willTopicValue, 0, willTopicValue.length);

					String willTopicName = new String(willTopicValue, "UTF-8");
					if (!StringVerifier.verify(willTopicName))
						throw new MalformedMessageException("WillTopic contains one or more restricted characters: U+0000, U+D000-U+DFFF");
					willTopic = new Text(willTopicName);

					if (buf.readableBytes() < 2)
						throw new MalformedMessageException("Invalid encoding will/username/password");

					willMessage = new byte[buf.readUnsignedShort()];
					if (buf.readableBytes() < willMessage.length)
						throw new MalformedMessageException("Invalid encoding will/username/password");

					buf.readBytes(willMessage, 0, willMessage.length);
					if (willTopic.length() == 0)
						throw new MalformedMessageException("invalid will encoding");
					will = new Will(new Topic(willTopic, willQos), willMessage, willRetain);
					if (!will.isValid())
						throw new MalformedMessageException("invalid will encoding");
				}

				if (userNameFlag)
				{
					if (buf.readableBytes() < 2)
						throw new MalformedMessageException("Invalid encoding will/username/password");

					byte[] userNameValue = new byte[buf.readUnsignedShort()];
					if (buf.readableBytes() < userNameValue.length)
						throw new MalformedMessageException("Invalid encoding will/username/password");

					buf.readBytes(userNameValue, 0, userNameValue.length);
					username = new String(userNameValue, "UTF-8");
					if (!StringVerifier.verify(username))
						throw new MalformedMessageException("Username contains one or more restricted characters: U+0000, U+D000-U+DFFF");
				}

				if (userPassFlag)
				{
					if (buf.readableBytes() < 2)
						throw new MalformedMessageException("Invalid encoding will/username/password");

					byte[] userPassValue = new byte[buf.readUnsignedShort()];
					if (buf.readableBytes() < userPassValue.length)
						throw new MalformedMessageException("Invalid encoding will/username/password");

					buf.readBytes(userPassValue, 0, userPassValue.length);
					password = new String(userPassValue, "UTF-8");
					if (!StringVerifier.verify(password))
						throw new MalformedMessageException("Password contains one or more restricted characters: U+0000, U+D000-U+DFFF");
				}

				if (buf.readableBytes() > 0)
					throw new MalformedMessageException("Invalid encoding will/username/password");

				Connect connect = new Connect(username, password, clientID, cleanSession, keepalive, will);
				if (protocolLevel != 4)
					connect.setProtocolLevel(protocolLevel);
				header = connect;
				break;

			case CONNACK:
				byte sessionPresentValue = buf.readByte();
				if (sessionPresentValue != 0 && sessionPresentValue != 1)
					throw new MalformedMessageException(String.format("CONNACK, session-present set to %d", sessionPresentValue & 0xff));
				boolean isPresent = sessionPresentValue == 1 ? true : false;

				short connackByte = buf.readUnsignedByte();
				ConnackCode connackCode = ConnackCode.valueOf(connackByte);
				if (connackCode == null)
					throw new MalformedMessageException("Invalid connack code: " + connackByte);
				header = new Connack(isPresent, connackCode);
				break;

			case PUBLISH:

				fixedHeader &= 0xf;

				boolean dup = ((fixedHeader >> 3) & 1) == 1 ? true : false;

				QoS qos = QoS.valueOf((fixedHeader & 0x07) >> 1);
				if (qos == null)
					throw new MalformedMessageException("invalid QoS value");
				if (dup && qos == QoS.AT_MOST_ONCE)
					throw new MalformedMessageException("PUBLISH, QoS-0 dup flag present");

				boolean retain = ((fixedHeader & 1) == 1) ? true : false;

				byte[] topicNameValue = new byte[buf.readUnsignedShort()];
				buf.readBytes(topicNameValue, 0, topicNameValue.length);
				String topicName = new String(topicNameValue, "UTF-8");
				if (!StringVerifier.verify(topicName))
					throw new MalformedMessageException("Publish-topic contains one or more restricted characters: U+0000, U+D000-U+DFFF");

				Integer packetID = null;
				if (qos != QoS.AT_MOST_ONCE)
				{
					packetID = buf.readUnsignedShort();
					if (packetID < 0 || packetID > 65535)
						throw new MalformedMessageException("Invalid PUBLISH packetID encoding");
				}

				ByteBuf data = Unpooled.buffer(buf.readableBytes());
				data.writeBytes(buf);
				header = new Publish(packetID, new Topic(new Text(topicName), qos), data, retain, dup);
				break;

			case PUBACK:
				header = new Puback(buf.readUnsignedShort());
				break;

			case PUBREC:
				header = new Pubrec(buf.readUnsignedShort());
				break;

			case PUBREL:
				header = new Pubrel(buf.readUnsignedShort());
				break;

			case PUBCOMP:
				header = new Pubcomp(buf.readUnsignedShort());
				break;

			case SUBSCRIBE:
				Integer subID = buf.readUnsignedShort();
				List<Topic> subscriptions = new ArrayList<>();
				while (buf.isReadable())
				{
					byte[] value = new byte[buf.readUnsignedShort()];
					buf.readBytes(value, 0, value.length);
					QoS requestedQos = QoS.valueOf(buf.readByte());
					if (requestedQos == null)
						throw new MalformedMessageException("Subscribe qos must be in range from 0 to 2: " + requestedQos);
					String topic = new String(value, "UTF-8");
					if (!StringVerifier.verify(topic))
						throw new MalformedMessageException("Subscribe topic contains one or more restricted characters: U+0000, U+D000-U+DFFF");
					Topic subscription = new Topic(new Text(topic), requestedQos);
					subscriptions.add(subscription);
				}
				if (subscriptions.isEmpty())
					throw new MalformedMessageException("Subscribe with 0 topics");

				header = new Subscribe(subID, subscriptions.toArray(new Topic[subscriptions.size()]));
				break;

			case SUBACK:
				Integer subackID = buf.readUnsignedShort();
				List<SubackCode> subackCodes = new ArrayList<>();
				while (buf.isReadable())
				{
					short subackByte = buf.readUnsignedByte();
					SubackCode subackCode = SubackCode.valueOf(subackByte);
					if (subackCode == null)
						throw new MalformedMessageException("Invalid suback code: " + subackByte);
					subackCodes.add(subackCode);
				}
				if (subackCodes.isEmpty())
					throw new MalformedMessageException("Suback with 0 return-codes");

				header = new Suback(subackID, subackCodes);
				break;

			case UNSUBSCRIBE:
				Integer unsubID = buf.readUnsignedShort();
				List<Text> unsubscribeTopics = new ArrayList<>();
				while (buf.isReadable())
				{
					byte[] value = new byte[buf.readUnsignedShort()];
					buf.readBytes(value, 0, value.length);
					String topic = new String(value, "UTF-8");
					if (!StringVerifier.verify(topic))
						throw new MalformedMessageException("Unsubscribe topic contains one or more restricted characters: U+0000, U+D000-U+DFFF");
					unsubscribeTopics.add(new Text(topic));
				}
				if (unsubscribeTopics.isEmpty())
					throw new MalformedMessageException("Unsubscribe with 0 topics");
				header = new Unsubscribe(unsubID, unsubscribeTopics.toArray(new Text[unsubscribeTopics.size()]));
				break;

			case UNSUBACK:
				header = new Unsuback(buf.readUnsignedShort());
				break;

			case PINGREQ:
				header = PINGREQ;
				break;
			case PINGRESP:
				header = PINGRESP;
				break;
			case DISCONNECT:
				header = DISCONNECT;
				break;

			default:
				throw new MalformedMessageException("Invalid header type: " + type);
			}

			if (buf.isReadable())
				throw new MalformedMessageException("unexpected bytes in content");

			if (length.getLength() != header.getLength())
				throw new MalformedMessageException(String.format("Invalid length. Encoded: %d, actual: %d", length.getLength(), header.getLength()));

			return header;
		}
		catch (UnsupportedEncodingException e)
		{
			throw new MalformedMessageException("unsupported string encoding:" + e.getMessage());
		}
	}

	public static ByteBuf encode(MQMessage header) throws MalformedMessageException
	{
		int length = header.getLength();
		ByteBuf buf = getBuffer(length);
		MessageType type = header.getType();
		try
		{
			switch (type)
			{
			case CONNECT:
				Connect connect = (Connect) header;
				if (connect.isWillFlag() && !connect.getWill().isValid())
					throw new MalformedMessageException("invalid will encoding");

				buf.setByte(0, (byte) (type.getNum() << 4));
				buf.writeShort(4);
				buf.writeBytes(connect.getName().getBytes());
				buf.writeByte(connect.getProtocolLevel());

				byte contentFlags = 0;
				if (connect.isClean())
					contentFlags += 2;
				if (connect.isWillFlag())
				{
					contentFlags += 4;
					contentFlags += connect.getWill().getTopic().getQos().getValue() << 3;
					if (connect.getWill().isRetain())
						contentFlags += 0x20;
				}
				if (connect.isPasswordFlag())
					contentFlags += 0x40;
				if (connect.isUsernameFlag())
					contentFlags += 0x80;
				buf.writeByte(contentFlags);

				buf.writeShort(connect.getKeepAlive());
				buf.writeShort(connect.getClientID().length());
				buf.writeBytes(connect.getClientID().getBytes("UTF-8"));

				if (connect.isWillFlag())
				{
					Text willTopic = connect.getWill().getTopic().getName();
					if (willTopic != null)
					{
						buf.writeShort(willTopic.length());
						buf.writeBytes(willTopic.toString().getBytes("UTF-8"));
					}

					byte[] willMessage = connect.getWill().getContent();
					if (willMessage != null)
					{
						buf.writeShort(willMessage.length);
						buf.writeBytes(willMessage);
					}
				}

				String username = connect.getUserName();
				if (username != null)
				{
					buf.writeShort(username.length());
					buf.writeBytes(username.getBytes("UTF-8"));
				}

				String password = connect.getPassword();
				if (password != null)
				{
					buf.writeShort(password.length());
					buf.writeBytes(password.getBytes("UTF-8"));
				}
				break;

			case CONNACK:
				Connack connack = (Connack) header;
				buf.setByte(0, (byte) (type.getNum() << 4));
				buf.writeBoolean(connack.isSessionPresent());
				buf.writeByte(connack.getReturnCode().getNum());
				break;

			case PUBLISH:
				Publish publish = (Publish) header;
				byte firstByte = (byte) (type.getNum() << 4);
				firstByte |= publish.isDup() ? 8 : 0;
				firstByte |= (publish.getTopic().getQos().getValue() << 1);
				firstByte |= publish.isRetain() ? 1 : 0;
				buf.setByte(0, firstByte);
				buf.writeShort(publish.getTopic().length());
				buf.writeBytes(publish.getTopic().getName().toString().getBytes("UTF-8"));
				switch (publish.getTopic().getQos())
				{
				case AT_MOST_ONCE:
					if (publish.getPacketID() != null)
						throw new MalformedMessageException("publish qos-0 must not contain packetID");
					break;
				case AT_LEAST_ONCE:
				case EXACTLY_ONCE:
					if (publish.getPacketID() == null)
						throw new MalformedMessageException("publish qos-1,2 must contain packetID");
					buf.writeShort(publish.getPacketID());
					break;
				}
				buf.writeBytes(publish.getContent());
				break;

			case PUBACK:
				Puback puback = (Puback) header;
				buf.setByte(0, (byte) (type.getNum() << 4));
				if (puback.getPacketID() == null)
					throw new MalformedMessageException("puback must contain packetID");
				buf.writeShort(puback.getPacketID());
				break;

			case PUBREC:
				Pubrec pubrec = (Pubrec) header;
				buf.setByte(0, (byte) (type.getNum() << 4));
				if (pubrec.getPacketID() == null)
					throw new MalformedMessageException("pubrec must contain packetID");
				buf.writeShort(pubrec.getPacketID());
				break;

			case PUBREL:
				Pubrel pubrel = (Pubrel) header;
				buf.setByte(0, (byte) ((type.getNum() << 4) | 0x2));
				if (pubrel.getPacketID() == null)
					throw new MalformedMessageException("pubrel must contain packetID");
				buf.writeShort(pubrel.getPacketID());
				break;

			case PUBCOMP:
				Pubcomp pubcomp = (Pubcomp) header;
				buf.setByte(0, (byte) (type.getNum() << 4));
				if (pubcomp.getPacketID() == null)
					throw new MalformedMessageException("pubcomp must contain packetID");
				buf.writeShort(pubcomp.getPacketID());
				break;

			case SUBSCRIBE:
				Subscribe sub = (Subscribe) header;
				buf.setByte(0, (byte) ((type.getNum() << 4) | 0x2));
				if (sub.getPacketID() == null)
					throw new MalformedMessageException("subscribe must contain packetID");
				buf.writeShort(sub.getPacketID());
				for (Topic subscription : sub.getTopics())
				{
					buf.writeShort(subscription.getName().length());
					buf.writeBytes(subscription.getName().toString().getBytes("UTF-8"));
					buf.writeByte(subscription.getQos().getValue());
				}
				break;

			case SUBACK:
				Suback suback = (Suback) header;
				buf.setByte(0, (byte) (type.getNum() << 4));
				if (suback.getPacketID() == null)
					throw new MalformedMessageException("suback must contain packetID");
				buf.writeShort(suback.getPacketID());
				for (SubackCode code : suback.getReturnCodes())
					buf.writeByte(code.getNum());
				break;

			case UNSUBSCRIBE:
				Unsubscribe unsub = (Unsubscribe) header;
				buf.setByte(0, (byte) ((type.getNum() << 4) | 0x2));
				if (unsub.getPacketID() == null)
					throw new MalformedMessageException("subscribe must contain packetID");
				buf.writeShort(unsub.getPacketID());
				for (Text topic : unsub.getTopics())
				{
					buf.writeShort(topic.length());
					buf.writeBytes(topic.toString().getBytes("UTF-8"));
				}
				break;

			case UNSUBACK:
				Unsuback unsuback = (Unsuback) header;
				buf.setByte(0, (byte) (type.getNum() << 4));
				if (unsuback.getPacketID() == null)
					throw new MalformedMessageException("unsuback must contain packetID");
				buf.writeShort(unsuback.getPacketID());
				break;

			case DISCONNECT:
			case PINGREQ:
			case PINGRESP:
				buf.setByte(0, (byte) (type.getNum() << 4));
				break;

			default:
				throw new MalformedMessageException("Invalid header type: " + type);
			}

			return buf;
		}
		catch (UnsupportedEncodingException e)
		{
			throw new MalformedMessageException("unsupported string encoding:" + e.getMessage());
		}
	}

	public static ByteBuf getBuffer(final int length) throws MalformedMessageException
	{
		byte[] lengthBytes;

		if (length <= 127)
			lengthBytes = new byte[1];
		else if (length <= 16383)
			lengthBytes = new byte[2];
		else if (length <= 2097151)
			lengthBytes = new byte[3];
		else if (length <= 26843545)
			lengthBytes = new byte[4];
		else
			throw new MalformedMessageException("header length exceeds maximum of 26843545 bytes");

		byte encByte;
		int pos = 0, l = length;
		do
		{
			encByte = (byte) (l % 128);
			l /= 128;
			if (l > 0)
				lengthBytes[pos++] = (byte) (encByte | 128);
			else
				lengthBytes[pos++] = encByte;
		}
		while (l > 0);

		int bufferSize = 1 + lengthBytes.length + length;
		ByteBuf buf = Unpooled.buffer(bufferSize);

		buf.writeByte(0);
		buf.writeBytes(lengthBytes);

		return buf;
	}
}
