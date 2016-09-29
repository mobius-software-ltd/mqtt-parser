package com.mobius.software.mqtt.parser.test;

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
import com.mobius.software.mqtt.parser.header.impl.ConnackCode;
import com.mobius.software.mqtt.parser.header.impl.Connack;
import com.mobius.software.mqtt.parser.header.impl.MessageType;

public class TestConnack
{

	private Connack expected;
	EmbeddedChannel channel;
	ByteBufAllocator alloc;

	@Before
	public void setUp()
	{
		channel = new EmbeddedChannel(new FixedLengthFrameDecoder(10000));
		alloc = channel.alloc();
		expected = new Connack(true, ConnackCode.ACCEPTED);
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
		Connack actual = (Connack) MQParser.decode(MQParser.encode(expected));
		assertTrue("Invalid binary content", ByteBufUtil.equals(MQParser.encode(expected), MQParser.encode(actual)));
	}

	@Test
	public void testNegativeByteContent() throws UnsupportedEncodingException, MalformedMessageException
	{
		Connack actual = (Connack) MQParser.decode(MQParser.encode(expected));
		actual.setSessionPresent(false); // mismatch
		assertFalse("Invalid binary content", ByteBufUtil.equals(MQParser.encode(expected), MQParser.encode(actual)));
	}

	@Test
	public void testPositiveConstants()
	{
		assertEquals("Invalid length", expected.getLength(), 2);
	}

	@Test
	public void testNegativeConstants()
	{
		for (MessageType type : MessageType.values())
			if (!type.equals(MessageType.CONNACK))
				assertFalse(String.format("Invalid default type: %s", type.toString()), expected.getType().equals(type));
		assertFalse(String.format("Invalid length: %d", expected.getLength()), expected.getLength() != 2);
	}

}
