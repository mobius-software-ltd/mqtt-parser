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
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;

import java.io.UnsupportedEncodingException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mobius.software.mqtt.parser.MQParser;
import com.mobius.software.mqtt.parser.MalformedMessageException;
import com.mobius.software.mqtt.parser.QoS;
import com.mobius.software.mqtt.parser.Text;
import com.mobius.software.mqtt.parser.Topic;
import com.mobius.software.mqtt.parser.Will;
import com.mobius.software.mqtt.parser.header.impl.Connect;

public class TestConnect
{

	private Connect expected;
	EmbeddedChannel channel;
	ByteBufAllocator alloc;

	@Before
	public void setUp()
	{

		channel = new EmbeddedChannel(new FixedLengthFrameDecoder(10000));
		alloc = channel.alloc();
		expected = new Connect("John", "1234567890", "_123456789", true, 60, new Will(new Topic(new Text("lookup"), QoS.AT_LEAST_ONCE), "John: i'll be back".getBytes(), true));
	}

	@After
	public void tearDown()
	{
		alloc = null;
		channel = null;
		expected = null;
	}

	@Test
	public void testPositiveByteContent() throws UnsupportedEncodingException, MalformedMessageException
	{
		Connect actual = (Connect) MQParser.decode(MQParser.encode(expected));
		assertTrue("Encoding error: ", ByteBufUtil.equals(MQParser.encode(expected), MQParser.encode(actual)));
	}

	@Test
	public void testNegativeByteContent() throws UnsupportedEncodingException, MalformedMessageException
	{
		Connect actual = (Connect) MQParser.decode(MQParser.encode(expected));
		actual.setClientID("_987654321");
		assertFalse("Expected unequal: ", ByteBufUtil.equals(MQParser.encode(expected), MQParser.encode(actual)));
	}

	@Test
	public void testChangeClientId()
	{
		expected.setClientID("newId");
		assertEquals("changeClientId results with invalid length", expected.getLength(), 63);
	}

	@Test
	public void testChangeWillTopic()
	{
		expected.getWill().getTopic().setName(new Text("newTopic"));
		assertEquals("changeWillTopic results with invalid length", expected.getLength(), 70);
	}

	@Test
	public void testChangeWillMessage()
	{
		expected.getWill().setContent("newMessage".getBytes());
		assertEquals("changeWillMessage results with invalid length", expected.getLength(), 60);
	}

	@Test
	public void testChangeUserName()
	{
		expected.setUsername("Jebediah");
		assertEquals("changeUserName results with invalid length", expected.getLength(), 72);
	}

	@Test
	public void testChangeUserPass()
	{
		expected.setPassword("1111");
		assertEquals("changeUserPass results with invalid length", expected.getLength(), 62);
	}

	@Test
	public void testWillFlag()
	{
		assertTrue("changeWillTopic doesn't change willFlag", expected.isWillFlag());
	}

	@Test
	public void testUserNameFlag()
	{
		assertTrue("userNameFlag doesn't toggle", expected.isUsernameFlag());
	}

	@Test
	public void testUserPassFlag()
	{
		assertTrue("userPassFlag doesn't toggle", expected.isPasswordFlag());
	}
}
