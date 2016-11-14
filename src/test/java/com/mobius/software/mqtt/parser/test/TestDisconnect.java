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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mobius.software.mqtt.parser.MQParser;
import com.mobius.software.mqtt.parser.MalformedMessageException;
import com.mobius.software.mqtt.parser.header.impl.Disconnect;
import com.mobius.software.mqtt.parser.header.impl.MessageType;

public class TestDisconnect
{
	private Disconnect expected;

	@Before
	public void setUp()
	{
		expected = new Disconnect();
	}

	@After
	public void tearDown()
	{
		expected = null;
	}

	@Test
	public void testPositiveByteContent() throws UnsupportedEncodingException, MalformedMessageException
	{
		Disconnect actual = (Disconnect) MQParser.decode(MQParser.encode(expected));
		assertTrue("Invalid binary content", ByteBufUtil.equals(MQParser.encode(expected), MQParser.encode(actual)));
	}

	@Test
	public void testPositiveConstants()
	{
		Disconnect disconnect = new Disconnect();
		assertEquals("Invalid type", disconnect.getType(), MessageType.DISCONNECT);
		assertEquals("Invalid length", disconnect.getLength(), 0);
	}

	@Test
	public void testNegativeConstants()
	{
		Disconnect disconnect = new Disconnect();
		for (MessageType type : MessageType.values())
			if (!type.equals(MessageType.DISCONNECT))
				assertFalse(String.format("Invalid default type: %s", type.toString()), disconnect.getType().equals(type));
		assertFalse(String.format("Invalid length: %d", disconnect.getLength()), disconnect.getLength() != 0);
	}

}
