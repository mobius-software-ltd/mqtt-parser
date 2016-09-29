package com.mobius.software.mqtt.parser.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.io.UnsupportedEncodingException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.mobius.software.mqtt.parser.MQParser;
import com.mobius.software.mqtt.parser.MalformedMessageException;

public class TestParser
{
	@Rule
	public ExpectedException expectedEx = ExpectedException.none();

	@Test
	public void testPositivenext() throws MalformedMessageException
	{
		ByteBuf buf = Unpooled.buffer(114);
		buf.writeByte(0x82);
		buf.writeByte(0x66);
		buf.writeShort(10);
		buf.writeShort(97);
		buf.writeBytes(new byte[96]);
		buf.writeByte(1);
		buf.writeByte(0);
		assertEquals("Invalid next header length", 104, MQParser.next(buf).capacity());
		assertEquals("buffer index was not reset", 0, buf.readerIndex());
	}

	@Test
	public void testNegativenext() throws MalformedMessageException
	{
		ByteBuf buf = Unpooled.buffer(103);
		buf.writeByte(0x82);
		buf.writeByte(0x66); //encoded l=104, actual l=103
		buf.writeShort(10);
		buf.writeShort(97);
		buf.writeBytes(new byte[96]);
		buf.writeByte(1);
		assertNull("Invalid next header length", MQParser.next(buf));
		assertEquals("buffer index was not reset", 0, buf.readerIndex());
	}

	@Test
	public void testNextLengthIncomplete() throws MalformedMessageException
	{
		ByteBuf buf = Unpooled.buffer(2120207);
		buf.writeByte(0x82);
		buf.writeByte(0x8a);
		buf.writeByte(0xB4);
		buf.writeByte(0x81); // one byte missing in length
		assertNull("Invalid next header length", MQParser.next(buf));
		assertEquals("buffer index was not reset", 0, buf.readerIndex());
	}

	@Test
	public void testNextContentIncomplete() throws MalformedMessageException
	{
		ByteBuf buf = Unpooled.buffer(2120207);
		buf.writeByte(0x82);
		buf.writeByte(0x8a);
		buf.writeByte(0xB4);
		buf.writeByte(0x81);
		buf.writeByte(0x01);
		buf.writeBytes(new byte[2120201]); // one byte missing in content
		assertNull("Invalid next header length", MQParser.next(buf));
		assertEquals("buffer index was not reset", 0, buf.readerIndex());
	}

	@Test
	public void testHeaderTypeNull() throws UnsupportedEncodingException, MalformedMessageException
	{
		expectedEx.expect(MalformedMessageException.class);
		ByteBuf buf = Unpooled.buffer(2);
		buf.writeByte(0);
		buf.writeByte(0);
		MQParser.decode(buf);
	}

	@Test
	public void testConackCodeNull() throws UnsupportedEncodingException, MalformedMessageException
	{
		expectedEx.expect(MalformedMessageException.class);
		ByteBuf buf = Unpooled.buffer(3);
		buf.writeByte(0x20);
		buf.writeByte(2);
		buf.writeByte(0);
		buf.writeByte(6);
		MQParser.decode(buf);
	}

	@Test
	public void testSubackCodeNull() throws UnsupportedEncodingException, MalformedMessageException
	{
		expectedEx.expect(MalformedMessageException.class);
		ByteBuf buf = Unpooled.buffer(3);
		buf.writeByte(0x90);
		buf.writeByte(3);
		buf.writeShort(10);
		buf.writeByte(3);
		MQParser.decode(buf);
	}

	@Test
	public void testDecodeLengthOne()
	{
		ByteBuf buf = Unpooled.buffer(114);
		buf.writeByte(0x82);
		buf.writeByte(0x66);
		buf.writeShort(10);
		buf.writeShort(97);
		buf.writeBytes(new byte[96]);
		buf.writeByte(1);
		buf.writeByte(0);
	}

	@Test
	public void testDecodeLengthTwo()
	{
		ByteBuf buf = Unpooled.buffer(205);
		buf.writeByte(0x82);
		buf.writeByte(0xCA);
		buf.writeByte(0x01);
		buf.writeShort(10);
		buf.writeShort(197);
		buf.writeBytes(new byte[196]);
		buf.writeByte(1);
		buf.writeByte(0);
	}

	@Test
	public void testDecodeLengthThree()
	{
		ByteBuf buf = Unpooled.buffer(20206);
		buf.writeByte(0x82);
		buf.writeByte(0xEA);
		buf.writeByte(0x9D);
		buf.writeByte(0x01);
		buf.writeShort(10);
		buf.writeShort(20197);
		buf.writeBytes(new byte[20196]);
		buf.writeByte(1);
		buf.writeByte(0);
	}

	@Test
	public void testDecodeLengthFour()
	{
		ByteBuf buf = Unpooled.buffer(2097159);
		buf.writeByte(0x82);
		buf.writeByte(0x82);
		buf.writeByte(0x80);
		buf.writeByte(0x80);
		buf.writeByte(0x01);
		buf.writeShort(10);
		for (int i = 0; i < 32; i++)
		{
			buf.writeShort(65533);
			buf.writeBytes(new byte[65532]);
			buf.writeByte(1);
			buf.writeByte(0);
		}
	}

}
