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
import com.mobius.software.mqtt.parser.header.impl.Pingreq;
import com.mobius.software.mqtt.parser.header.impl.MessageType;

public class TestPingreq {

	private Pingreq expected;

	@Before
	public void setUp() {
		expected = new Pingreq();
	}

	@After
	public void tearDown() {
		expected = null;
	}

	@Test
	public void testPositiveByteContent() throws UnsupportedEncodingException, MalformedMessageException {
		Pingreq actual = (Pingreq) MQParser.decode(MQParser.encode(expected));
		assertTrue("Invalid binary content",
				ByteBufUtil.equals(MQParser.encode(expected), MQParser.encode(actual)));
	}

	@Test
	public void testPositiveConstants() {
		Pingreq pingreq = new Pingreq();
		assertEquals("Invalid type", pingreq.getType(), MessageType.PINGREQ);
		assertEquals("Invalid length", pingreq.getLength(), 0);
	}

	@Test
	public void testNegativeConstants() {
		Pingreq pingreq = new Pingreq();
		for (MessageType type : MessageType.values())
			if (!type.equals(MessageType.PINGREQ))
				assertFalse(String.format("Invalid default type: %s", type.toString()), pingreq.getType()
						.equals(type));
		assertFalse(String.format("Invalid length: %d", pingreq.getLength()), pingreq.getLength() != 0);
	}
}
