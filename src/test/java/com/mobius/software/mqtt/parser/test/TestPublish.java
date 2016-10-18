package com.mobius.software.mqtt.parser.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;

import java.io.UnsupportedEncodingException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mobius.software.mqtt.parser.MQParser;
import com.mobius.software.mqtt.parser.MalformedMessageException;
import com.mobius.software.mqtt.parser.QoS;
import com.mobius.software.mqtt.parser.Text;
import com.mobius.software.mqtt.parser.Topic;
import com.mobius.software.mqtt.parser.header.api.MQMessage;
import com.mobius.software.mqtt.parser.header.impl.Publish;

public class TestPublish
{
	private Publish expected;

	@Before
	public void setUp()
	{
		byte[] content = new byte[]
		{ 0x48, 0x65, 0x6c, 0x6c, 0x6f, 0x20, 0x77, 0x6f, 0x72, 0x6c, 0x64 };
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
		MQMessage actual = MQParser.decode(buf);
		assertTrue("Invalid binary content", ByteBufUtil.equals(MQParser.encode(expected), MQParser.encode(actual)));
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
		byte[] data =
		{ 0x48, 0x65, 0x6c, 0x6c, 0x6f, 0x20, 0x77, 0x6f, 0x72, 0x6c, 0x64 };
		Publish actual = new Publish(expected.getPacketID(), expected.getTopic(), data, expected.isRetain(), expected.isDup());
		assertEquals("setData results with invalid length", actual.getLength(), 24);
	}

	@Test
	public void testEncodeLength()
	{
		Publish actual = new Publish(expected.getPacketID(), new Topic(new Text("name"), expected.getTopic().getQos()), new byte[121], expected.isRetain(), expected.isDup());
		assertEquals("invalid header length", 129, actual.getLength());
		actual = new Publish(expected.getPacketID(), new Topic(new Text("name"), expected.getTopic().getQos()), new byte[122], expected.isRetain(), expected.isDup());
		assertEquals("invalid header length", actual.getLength(), 130);
		actual = new Publish(expected.getPacketID(), new Topic(new Text("name"), expected.getTopic().getQos()), new byte[16378], expected.isRetain(), expected.isDup());
		assertEquals("invalid header length", actual.getLength(), 16386);
		actual = new Publish(expected.getPacketID(), new Topic(new Text("name"), expected.getTopic().getQos()), new byte[2097146], expected.isRetain(), expected.isDup());
		assertEquals("invalid header length", actual.getLength(), 2097154);
	}

	@Test
	public void testTime() throws UnsupportedEncodingException, MalformedMessageException
	{
		Publish actual = new Publish(20, new Topic(new Text("root/first/second/third/fourth/fifth/andsomeadditionalsymbols"), QoS.EXACTLY_ONCE), "1234567890-=qwertyuiop[]asdfghjkl".getBytes(), false, false);
		ByteBuf buf = MQParser.encode(actual);
		buf = MQParser.encode(actual);
		ByteBuf slice = MQParser.next(buf);
		buf.readBytes(slice);
		MQParser.decode(slice);
	}
}
