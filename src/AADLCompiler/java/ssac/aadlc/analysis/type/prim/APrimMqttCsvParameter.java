/*
 * @(#)APrimMqttCsvParameter.java	2.1.0	2014/05/29
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadlc.analysis.type.prim;

import ssac.aadlc.analysis.type.AADLJavaClass;
import ssac.aadlc.analysis.type.AADLPrimitive;

/**
 * AADL : MqttCsvParameter
 * 
 * @version 2.1.0	2014/05/29
 * 
 * @since 2.1.0
 */
public class APrimMqttCsvParameter extends AADLJavaClass implements AADLPrimitive
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	static public final APrimMqttCsvParameter instance = new APrimMqttCsvParameter();
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	protected APrimMqttCsvParameter() {
		super("MqttCsvParameter", ssac.aadl.runtime.mqtt.MqttCsvParameter.class);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
}
