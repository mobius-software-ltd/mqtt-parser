package com.mobius.software.mqtt.parser.test;

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

import ua.mobius.media.server.utils.Text;

import com.mobius.software.mqtt.parser.MQParser;
import com.mobius.software.mqtt.parser.MalformedMessageException;
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
