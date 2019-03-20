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
 *  Copyright 2007-2009  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)AADLVoid.java	1.00	2007/11/29
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadlc.analysis.type;



/**
 * void 型 を表す、特殊なデータ型
 * 
 * 
 * @version 1.00	2007/11/29
 */
public final class AADLVoid extends AADLType
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	static public final AADLVoid instance = new AADLVoid();
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	protected AADLVoid() {
		super("void", void.class);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	// AADL operator
	// void型は、常にサポートなし
	public String getOperatorMethodName(int opType) {
		return NO_METHOD;
	}
}
