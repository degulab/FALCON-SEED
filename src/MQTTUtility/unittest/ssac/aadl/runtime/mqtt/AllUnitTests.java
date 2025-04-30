package ssac.aadl.runtime.mqtt;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import ssac.aadl.runtime.mqtt.internal.MqttBufferedSessionImplBasicTest;
import ssac.aadl.runtime.mqtt.internal.MqttBufferedSessionImplCoreTest;
import ssac.aadl.runtime.mqtt.internal.MqttBufferedSessionImplExtraTest;
import ssac.aadl.runtime.mqtt.internal.MqttSessionImplTest;
import ssac.falconseed.mqtt.broker.MoquetteBrokerLauncherTest;

@RunWith(Suite.class)
@SuiteClasses({
	MqPayloadTest.class,
	MqttArrivedMessageTest.class,
	MqttConnectionParamsTest.class,
	MqttCsvParameterTest.class,
	MqttSessionImplTest.class,
	MqttBufferedSessionImplBasicTest.class,
	MqttBufferedSessionImplCoreTest.class,
	MqttBufferedSessionImplExtraTest.class,
	MqttUtilTest.class,
	MqttPublisherTest.class,
	MqttSubscriberTest.class,
	MoquetteBrokerLauncherTest.class
})

public class AllUnitTests {
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------

}
