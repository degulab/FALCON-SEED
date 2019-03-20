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
 * @(#)ACodeFixedVariables.java	1.30	2009/12/02
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadlc.codegen.define;

import ssac.aadlc.analysis.AADLSymbol;
import ssac.aadlc.analysis.AADLSymbolMap;
import ssac.aadlc.analysis.type.AADLJavaAction;
import ssac.aadlc.analysis.type.AADLType;
import ssac.aadlc.analysis.type.prim.APrimString;
import ssac.aadlc.codegen.AADLCode;

/**
 * AADL グローバル定数宣言
 * 
 * @version 1.30	2009/12/02
 * 
 * @since 1.30
 */
public class ACodeFixedVariables extends AADLCode
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	protected ACodeFixedVariables() {
		super(AADLJavaAction.instance);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	static public ACodeFixedVariables buildFixedVariables(AADLSymbolMap symbolmap) {
		ACodeFixedVariables retCode = new ACodeFixedVariables();
		retCode.initVariables(symbolmap);
		return retCode;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	private void initVariables(AADLSymbolMap symbolmap) {
		// ExTransfer 定数(exalge2との依存関係を持たないようにする－後方互換性)
		genCode(symbolmap, APrimString.instance, "ATTR_AGGRE",    "\"aggre\"");
		genCode(symbolmap, APrimString.instance, "ATTR_HAT",      "\"hat\"");
		genCode(symbolmap, APrimString.instance, "ATTR_RATIO",    "\"ratio\"");
		genCode(symbolmap, APrimString.instance, "ATTR_MULTIPLY", "\"multiply\"");
	}
	
	private void genCode(AADLSymbolMap symbolmap, AADLType dataType, String varName, String valueCode) {
		AADLSymbol symbol = new AADLSymbol(true, varName, dataType);
		symbol.setInitialized();
		String strCode = String.format("static private final %s %s = %s;", dataType.getJavaClassName(), varName, valueCode);
		this.jlb.appendLine(-1, strCode);
		symbolmap.setSymbol(symbol);
	}
}
