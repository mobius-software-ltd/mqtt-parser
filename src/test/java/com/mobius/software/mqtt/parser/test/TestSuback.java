package com.mobius.software.mqtt.parser.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import io.netty.buffer.ByteBufUtil;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mobius.software.mqtt.parser.MQParser;
import com.mobius.software.mqtt.parser.MalformedMessageException;
import com.mobius.software.mqtt.parser.header.impl.Suback;
import com.mobius.software.mqtt.parser.header.impl.SubackCode;

public class TestSuback
{

	private Suback expected;

	@Before
	public void setUp()
	{
		SubackCode[] codes =
		{ SubackCode.ACCEPTED_QOS0, SubackCode.ACCEPTED_QOS1, SubackCode.ACCEPTED_QOS2, SubackCode.FAILURE };
		expected = new Suback(100, Arrays.asList(codes));
	}

	@After
	public void tearDown()
	{
		expected = null;
	}

	@Test
	public void testPositiveByteContent() throws UnsupportedEncodingException, MalformedMessageException
	{
		Suback actual = (Suback) MQParser.decode(MQParser.encode(expected));
		assertTrue("Invalid binary content", ByteBufUtil.equals(MQParser.encode(expected), MQParser.encode(actual)));
	}

	@Test
	public void testNegativeByteContent() throws UnsupportedEncodingException, MalformedMessageException
	{
		Suback actual = (Suback) MQParser.decode(MQParser.encode(expected));
		actual.setPacketID(1000);
		assertFalse("Invalid binary content", ByteBufUtil.equals(MQParser.encode(expected), MQParser.encode(actual)));
	}
}
