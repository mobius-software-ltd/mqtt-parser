package com.mobius.software.mqtt.parser.test;

/**
 * Mobius Software LTD
 * Copyright 2015-2016, Mobius Software LTD
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.io.UnsupportedEncodingException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.mobius.software.mqtt.parser.MQParser;
import com.mobius.software.mqtt.parser.exceptions.MalformedMessageException;

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

	@Test(expected = MalformedMessageException.class)
	public void testNegativeNext() throws MalformedMessageException
	{
		ByteBuf buf = Unpooled.buffer(103);
		buf.writeByte(0x82);
		buf.writeByte(0x66); //encoded l=104, actual l=103
		buf.writeShort(10);
		buf.writeShort(97);
		buf.writeBytes(new byte[96]);
		buf.writeByte(1);
		MQParser.next(buf);
	}

	@Test
	public void testNegativeNextIndexNotReset() throws MalformedMessageException
	{
		ByteBuf buf = Unpooled.buffer(103);
		buf.writeByte(0x82);
		buf.writeByte(0x66); //encoded l=104, actual l=103
		buf.writeShort(10);
		buf.writeShort(97);
		buf.writeBytes(new byte[96]);
		buf.writeByte(1);
		try
		{
			MQParser.next(buf);
		}
		catch (MalformedMessageException e)
		{

		}
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

	@Test(expected = MalformedMessageException.class)
	public void testNextContentIncomplete() throws MalformedMessageException
	{
		ByteBuf buf = Unpooled.buffer(2120207);
		buf.writeByte(0x82);
		buf.writeByte(0x8a);
		buf.writeByte(0xB4);
		buf.writeByte(0x81);
		buf.writeByte(0x01);
		buf.writeBytes(new byte[2120201]); // one byte missing in content
		MQParser.next(buf);
		assertEquals("buffer index was not reset", 0, buf.readerIndex());
	}

	@Test
	public void testNextContentIncompleteBufferNotReset() throws MalformedMessageException
	{
		ByteBuf buf = Unpooled.buffer(2120207);
		buf.writeByte(0x82);
		buf.writeByte(0x8a);
		buf.writeByte(0xB4);
		buf.writeByte(0x81);
		buf.writeByte(0x01);
		buf.writeBytes(new byte[2120201]); // one byte missing in content
		try
		{
			MQParser.next(buf);
		}
		catch (MalformedMessageException e)
		{

		}
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

	@Test
	public void testEncodedLenthOneByte()
	{
		int length = 1;
		ByteBuf buf = MQParser.getBuffer(length);

		assertEquals(2, buf.readableBytes());
		assertEquals(0, buf.readByte());
		assertEquals(1, buf.readByte());
	}

	@Test
	public void testEncodedLenthTwoBytes()
	{
		int length = 128;
		byte[] expected = new byte[]
		{ (byte) 0x80, 0x01 };

		ByteBuf buf = MQParser.getBuffer(length);
		assertEquals(3, buf.readableBytes());
		assertEquals(0, buf.readByte());

		byte[] actual = new byte[buf.readableBytes()];
		buf.readBytes(actual);
		assertArrayEquals(expected, actual);
	}

	@Test
	public void testEncodedLenthThreeBytes()
	{
		int length = 16384;
		byte[] expected = new byte[]
		{ (byte) 0x80, (byte) 0x80, 0x01 };

		ByteBuf buf = MQParser.getBuffer(length);
		assertEquals(4, buf.readableBytes());
		assertEquals(0, buf.readByte());

		byte[] actual = new byte[buf.readableBytes()];
		buf.readBytes(actual);
		assertArrayEquals(expected, actual);
	}

	@Test
	public void testEncodedLenthFourBytes()
	{
		int length = 2097152;
		byte[] expected = new byte[]
		{ (byte) 0x80, (byte) 0x80, (byte) 0x80, 0x01 };

		ByteBuf buf = MQParser.getBuffer(length);
		assertEquals(5, buf.readableBytes());
		assertEquals(0, buf.readByte());

		byte[] actual = new byte[buf.readableBytes()];
		buf.readBytes(actual);
		assertArrayEquals(expected, actual);
	}
}
