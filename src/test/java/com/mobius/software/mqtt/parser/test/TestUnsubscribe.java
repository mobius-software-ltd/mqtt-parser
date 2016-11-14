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
import io.netty.buffer.ByteBufUtil;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mobius.software.mqtt.parser.MQParser;
import com.mobius.software.mqtt.parser.MalformedMessageException;
import com.mobius.software.mqtt.parser.Text;
import com.mobius.software.mqtt.parser.header.impl.Unsubscribe;

public class TestUnsubscribe
{

	private Unsubscribe expected;

	@Before
	public void setUp()
	{
		Text[] topics =
		{ new Text("topic1"), new Text("topic2"), new Text("topic3") };
		expected = new Unsubscribe(100, topics);
	}

	@After
	public void tearDown()
	{
		expected = null;
	}

	@Test
	public void testPositiveByteContent() throws UnsupportedEncodingException, MalformedMessageException
	{
		Unsubscribe actual = (Unsubscribe) MQParser.decode(MQParser.encode(expected));
		assertTrue("Invalid binary content", ByteBufUtil.equals(MQParser.encode(expected), MQParser.encode(actual)));
	}

	@Test
	public void testNegativeByteContent() throws UnsupportedEncodingException, MalformedMessageException
	{
		Unsubscribe actual = (Unsubscribe) MQParser.decode(MQParser.encode(expected));
		actual.setPacketID(1000);
		assertFalse("Invalid binary content", ByteBufUtil.equals(MQParser.encode(expected), MQParser.encode(actual)));
	}

	@Test
	public void testEncodeLength()
	{
		List<Text> topics = new ArrayList<>();
		topics.add(new Text(new String(new byte[98])));
		Unsubscribe unsubscribe = new Unsubscribe(10, topics.toArray(new Text[topics.size()]));
		assertEquals("invalid header length", 102, unsubscribe.getLength());
		topics.add(new Text(new String(new byte[97])));
		unsubscribe.setTopics(topics.toArray(new Text[topics.size()]));
		assertEquals("invalid header length", 201, unsubscribe.getLength());
		for (int i = 0; i < 200; i++)
			topics.add(new Text(new String(new byte[98]) + new String((i + ""))));
		unsubscribe.setTopics(topics.toArray(new Text[topics.size()]));
		assertEquals("invalid header length", 20691, unsubscribe.getLength());
	}

}
