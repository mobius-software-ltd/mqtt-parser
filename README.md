# MQTT parser

MQTT parser is a library designed for encoding and decoding of MQTT 3.1.1 packets. The parser is written in Java.

## Getting Started

First you should clone MQTT parser. Then you should add the following lines within the <project> element of pom.xml file of your project:

```
<dependency>
	<groupId>com.mobius.software.mqtt</groupId>
	<artifactId>parser</artifactId>
	<version>0.0.1-SNAPSHOT</version>
</dependency>
```
Now you are able to start using MQTT parser.

# Examples

## Create, encode, decode message

```
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

```

