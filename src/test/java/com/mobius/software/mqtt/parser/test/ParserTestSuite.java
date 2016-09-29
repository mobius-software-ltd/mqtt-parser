package com.mobius.software.mqtt.parser.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(
{ TestConnack.class, TestConnect.class, TestDisconnect.class, TestPingreq.class, TestPingresp.class, TestPuback.class, TestPubcomp.class, TestPublish.class, TestPubrec.class, TestPubrel.class, TestSuback.class, TestUnsuback.class, TestUnsubscribe.class, TestParser.class })
public class ParserTestSuite
{

}
