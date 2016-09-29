package com.mobius.software.mqtt.parser;

public class StringVerifier
{
	private static final String NULL_CHARACTER = "\u0000";

	public static boolean verify(String topic)
	{
		if (topic.length() > 0)
		{
			if (topic.contains(NULL_CHARACTER))
				return false;

			for (int i = 0; i < topic.length(); i++)
			{
				char c = topic.charAt(i);
				if (Character.isSurrogate(c))
					return false;
			}
		}

		return true;
	}

}
