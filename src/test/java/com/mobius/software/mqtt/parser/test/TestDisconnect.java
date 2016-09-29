package com.mobius.software.mqtt.parser.test;

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
