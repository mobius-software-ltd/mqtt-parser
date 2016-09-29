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

import ua.mobius.media.server.utils.Text;

import com.mobius.software.mqtt.parser.MQParser;
import com.mobius.software.mqtt.parser.MalformedMessageException;
import com.mobius.software.mqtt.parser.QoS;
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
