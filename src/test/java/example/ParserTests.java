package example;

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

import static org.junit.Assert.fail;
import io.netty.buffer.ByteBuf;

import org.junit.Test;

import com.mobius.software.mqtt.parser.MQParser;
import com.mobius.software.mqtt.parser.avps.QoS;
import com.mobius.software.mqtt.parser.avps.Text;
import com.mobius.software.mqtt.parser.avps.Topic;
import com.mobius.software.mqtt.parser.avps.Will;
import com.mobius.software.mqtt.parser.header.api.MQMessage;
import com.mobius.software.mqtt.parser.header.impl.Connect;

public class ParserTests
{
	@Test
	public void testParserEncodeDecodeFunctions()
	{
		try
		{
			// Message creation
			String username = "foo@bar.net";
			String password = "password";
			String clientID = "dummy-client-1";
			boolean cleanSession = true;
			int keepalive = 10;

			Text topicName = new Text("root/example");
			Topic topic = Topic.valueOf(topicName, QoS.AT_LEAST_ONCE);
			String content = "message";
			boolean retain = false;
			Will will = new Will(topic, content.getBytes(), retain);
			Connect connect = new Connect(username, password, clientID, cleanSession, keepalive, will);

			// Encode message
			ByteBuf encoded = MQParser.encode(connect);
			// process encoded value...

			// Decode message
			MQMessage decoded = MQParser.decode(encoded);
			// process decoded value...
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail();
		}
	}
}
