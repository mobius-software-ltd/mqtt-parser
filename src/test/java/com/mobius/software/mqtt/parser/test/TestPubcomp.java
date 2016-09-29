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
import com.mobius.software.mqtt.parser.header.impl.Pubcomp;
import com.mobius.software.mqtt.parser.header.impl.MessageType;

public class TestPubcomp
{

	private Pubcomp expected;

	@Before
	public void setUp()
	{
		expected = new Pubcomp(100);
	}

	@After
	public void tearDown()
	{
		expected = null;
	}

	@Test
	public void testPositiveByteContent() throws UnsupportedEncodingException, MalformedMessageException
	{
		Pubcomp actual = (Pubcomp) MQParser.decode(MQParser.encode(expected));
		assertTrue("Invalid binary content", ByteBufUtil.equals(MQParser.encode(expected), MQParser.encode(actual)));
	}

	@Test
	public void testNegativeByteContent() throws UnsupportedEncodingException, MalformedMessageException
	{
		Pubcomp actual = (Pubcomp) MQParser.decode(MQParser.encode(expected));
		actual.setPacketID(1000); // mismatch
		assertFalse("Invalid binary content", ByteBufUtil.equals(MQParser.encode(expected), MQParser.encode(actual)));
	}

	@Test
	public void testPositiveConstants()
	{
		assertEquals("Invalid type", expected.getType(), MessageType.PUBCOMP);
	}

	@Test
	public void testNegativeConstants()
	{
		for (MessageType type : MessageType.values())
			if (!type.equals(MessageType.PUBCOMP))
				assertFalse(String.format("Invalid default type: %s", type.toString()), expected.getType().equals(type));
	}

}
