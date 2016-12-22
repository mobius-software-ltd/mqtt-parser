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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;

import java.io.UnsupportedEncodingException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mobius.software.mqtt.parser.MQParser;
import com.mobius.software.mqtt.parser.avps.QoS;
import com.mobius.software.mqtt.parser.avps.Text;
import com.mobius.software.mqtt.parser.avps.Topic;
import com.mobius.software.mqtt.parser.exceptions.MalformedMessageException;
import com.mobius.software.mqtt.parser.header.api.MQMessage;
import com.mobius.software.mqtt.parser.header.impl.Publish;

public class TestPublish
{
	private Publish expected;

	@Before
	public void setUp()
	{
		byte[] data = new byte[]
		{ 0x48, 0x65, 0x6c, 0x6c, 0x6f, 0x20, 0x77, 0x6f, 0x72, 0x6c, 0x64 };
		ByteBuf content = Unpooled.buffer(data.length).writeBytes(data);
		expected = new Publish(100, new Topic(new Text("new_topic"), QoS.EXACTLY_ONCE), content, true, true);
	}

	@After
	public void tearDown()
	{
		expected = null;
	}

	@Test
	public void testPositiveByteContent() throws UnsupportedEncodingException, MalformedMessageException
	{
		ByteBuf buf = MQParser.encode(expected);
		MQMessage actual = MQParser.decode(Unpooled.copiedBuffer(buf));
		assertTrue("Invalid binary content", ByteBufUtil.equals(buf, MQParser.encode(actual)));
	}

	@Test
	public void testNegativeByteContent() throws UnsupportedEncodingException, MalformedMessageException
	{
		Publish actual = new Publish(expected.getPacketID(), new Topic(new Text("other_topic"), expected.getTopic().getQos()), expected.getContent(), expected.isRetain(), expected.isDup());
		assertFalse("Invalid binary content", ByteBufUtil.equals(MQParser.encode(expected), MQParser.encode(actual)));
	}

	@Test
	public void testSetTopic()
	{
		Publish actual = new Publish(expected.getPacketID(), new Topic(new Text("other_topic"), expected.getTopic().getQos()), expected.getContent(), expected.isRetain(), expected.isDup());
		assertEquals("changeTopicName results with invalid length", actual.getLength(), 26);
	}

	@Test
	public void testSetData()
	{
		ByteBuf data = Unpooled.buffer().writeBytes(new byte[]
		{ 0x48, 0x65, 0x6c, 0x6c, 0x6f, 0x20, 0x77, 0x6f, 0x72, 0x6c, 0x64 });
		Publish actual = new Publish(expected.getPacketID(), expected.getTopic(), data, expected.isRetain(), expected.isDup());
		assertEquals("setData results with invalid length", actual.getLength(), 24);
	}

	@Test
	public void testEncodeLength()
	{
		Publish actual = new Publish(expected.getPacketID(), new Topic(new Text("name"), expected.getTopic().getQos()), Unpooled.buffer(121).writeBytes(new byte[121]), expected.isRetain(), expected.isDup());
		assertEquals("invalid header length", 129, actual.getLength());
		actual = new Publish(expected.getPacketID(), new Topic(new Text("name"), expected.getTopic().getQos()), Unpooled.buffer(122).writeBytes(new byte[122]), expected.isRetain(), expected.isDup());
		assertEquals("invalid header length", actual.getLength(), 130);
		actual = new Publish(expected.getPacketID(), new Topic(new Text("name"), expected.getTopic().getQos()), Unpooled.buffer(16378).writeBytes(new byte[16378]), expected.isRetain(), expected.isDup());
		assertEquals("invalid header length", actual.getLength(), 16386);
		actual = new Publish(expected.getPacketID(), new Topic(new Text("name"), expected.getTopic().getQos()), Unpooled.buffer(2097146).writeBytes(new byte[2097146]), expected.isRetain(), expected.isDup());
		assertEquals("invalid header length", actual.getLength(), 2097154);
	}

	@Test
	public void testTime() throws UnsupportedEncodingException, MalformedMessageException
	{
		Publish actual = new Publish(20, new Topic(new Text("root/first/second/third/fourth/fifth/andsomeadditionalsymbols"), QoS.EXACTLY_ONCE), Unpooled.buffer().writeBytes("1234567890-=qwertyuiop[]asdfghjkl".getBytes()), false, false);
		ByteBuf buf = MQParser.encode(actual);
		buf = MQParser.encode(actual);
		ByteBuf slice = MQParser.next(buf);
		buf.readBytes(slice);
		MQParser.decode(slice);
	}
}
