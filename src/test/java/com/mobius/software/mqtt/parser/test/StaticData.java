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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.io.IOUtils;

public class StaticData
{
	final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

	public static String bytesToHex(byte[] bytes)
	{
		char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++)
		{
			int v = bytes[j] & 0xFF;
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
			hexChars[j * 2] = hexArray[v >>> 4];
		}

		if (hexChars[hexChars.length - 1] != 'F')
			return new String(hexChars);

		return new String(hexChars, 0, hexChars.length - 1);
	}

	public static byte[] loadBytesFromResource(String resource) throws IOException
	{
		InputStream is = StaticData.class.getClassLoader().getResourceAsStream(resource);
		byte[] result;
		result = IOUtils.toByteArray(is);
		// result = hexStringToByteArray(IOUtils.toString(is));
		is.close();
		return result;
	}

	public static String[] getResourcesList(String folder, boolean absolutePath) throws IOException
	{
		File currFile = new File(StaticData.class.getClassLoader().getResource(folder).getPath());
		File[] allFiles = currFile.listFiles();
		String[] result = new String[allFiles.length];
		for (int i = 0; i < allFiles.length; i++)
		{
			if (absolutePath)
				result[i] = allFiles[i].getAbsolutePath();
			else
				result[i] = allFiles[i].getName();
		}

		return result;
	}

	public static File[] getFilesList(String folder) throws IOException
	{
		File currFile = new File(StaticData.class.getClassLoader().getResource(folder).getPath());
		File[] allFiles = currFile.listFiles();

		return allFiles;
	}

	static String readFile(String path, String encoding) throws IOException
	{
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}

	public static byte[] hexStringToByteArray(String file)
	{
		String s;
		byte[] data = null;
		try
		{
			s = readFile(file, "UTF-8");
			data = new byte[s.length() / 2];
			for (int i = 0; i < data.length; i++)
			{
				data[i] = (byte) Integer.parseInt(s.substring(2 * i, 2 * i + 2), 16);
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return data;
	}

	public static File getFile(String resource)
	{
		return new File(StaticData.class.getClassLoader().getResource(resource).getPath());
	}

	public static boolean isGetter(Method method)
	{
		if (!method.getName().startsWith("get"))
			return false;
		if (method.getParameterTypes().length != 0)
			return false;
		if (void.class.equals(method.getReturnType()))
			return false;
		return true;
	}

}
