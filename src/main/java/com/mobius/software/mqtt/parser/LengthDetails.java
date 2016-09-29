package com.mobius.software.mqtt.parser;

public class LengthDetails
{
	private int length;
	private int size;
	
	public LengthDetails(int length,int size)
	{
		this.length=length;
		this.size=size;
	}

	public int getLength()
	{
		return length;
	}

	public int getSize()
	{
		return size;
	}		
}
