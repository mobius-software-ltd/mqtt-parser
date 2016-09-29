package com.mobius.software.mqtt.parser.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import io.netty.buffer.ByteBufUtil;

import java.io.UnsupportedEncodingException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mobius.software.mqtt.parser.MQParser;
import com.mobius.software.mqtt.parser.MalformedMessageException;
import com.mobius.software.mqtt.parser.header.impl.Pubrec;

public class TestPubrec
{

	private Pubrec expected;

	@Before
	public void setUp()
	{
		expected = new Pubrec(100);
	}

	@After
	public void tearDown()
	{
		expected = null;
	}

	@Test
	public void testPositiveByteContent() throws UnsupportedEncodingException, MalformedMessageException
	{
		Pubrec actual = (Pubrec) MQParser.decode(MQParser.encode(expected));
		assertTrue("Invalid binary content", ByteBufUtil.equals(MQParser.encode(expected), MQParser.encode(actual)));
	}

	@Test
	public void testNegativeByteContent() throws UnsupportedEncodingException, MalformedMessageException
	{
		Pubrec actual = (Pubrec) MQParser.decode(MQParser.encode(expected));
		actual.setPacketID(1000); // mismatch
		assertFalse("Invalid binary content", ByteBufUtil.equals(MQParser.encode(expected), MQParser.encode(actual)));
	}

	@Test
	public void testNegativeStringContent() throws UnsupportedEncodingException, MalformedMessageException
	{
		Pubrec actual = (Pubrec) MQParser.decode(MQParser.encode(expected));
		actual.setPacketID(1000); // mismatch
		assertFalse("Invalid string content", expected.toString().equals(actual.toString()));
	}
}
