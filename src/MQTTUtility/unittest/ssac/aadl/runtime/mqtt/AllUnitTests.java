/*
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *  Copyright 2007-2015  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
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
