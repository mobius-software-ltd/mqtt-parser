package example;

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
