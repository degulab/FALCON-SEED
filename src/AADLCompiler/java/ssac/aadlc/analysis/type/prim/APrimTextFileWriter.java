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
 *  Copyright 2007-2010  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)APrimTextFileWriter.java	1.50	2010/09/27
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadlc.analysis.type.prim;

import ssac.aadlc.analysis.type.AADLJavaClass;
import ssac.aadlc.analysis.type.AADLPrimitive;

/**
 * AADL : TextFileWriter
 * 
 * @version 1.50	2010/09/27
 * @since 1.50
 */
public class APrimTextFileWriter extends AADLJavaClass implements AADLPrimitive
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	static public final APrimTextFileWriter instance = new APrimTextFileWriter();
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	protected APrimTextFileWriter() {
		super("TextFileWriter", ssac.aadl.runtime.io.TextFileWriter.class);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

}
