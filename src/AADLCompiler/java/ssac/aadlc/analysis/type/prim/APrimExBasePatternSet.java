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
 * @(#)APrimExBasePatternSet.java	1.40	2010/02/19
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)APrimExBasePatternSet.java	1.30	2009/12/02
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadlc.analysis.type.prim;

import ssac.aadlc.analysis.type.AADLPrimitive;
import ssac.aadlc.analysis.type.AADLSetType;

/**
 * AADL : ExBasePatternSet
 * 
 * @version 1.40	2010/02/19
 * 
 * @since 1.30
 */
public class APrimExBasePatternSet extends AADLSetType implements AADLPrimitive
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	static public final APrimExBasePatternSet instance = new APrimExBasePatternSet();
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	protected APrimExBasePatternSet() {
		//super("ExBasePatternSet", exalge2.ExBasePatternSet.class, new APrimExBasePattern());
		super("ExBasePatternSet", exalge2.ExBasePatternSet.class, APrimExBasePattern.instance);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
}
