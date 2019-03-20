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
 * @(#)ACodeDataType.java	1.30	2009/12/02
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ACodeDataType.java	1.00	2007/11/29
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadlc.codegen.define;

import java.util.List;

import org.antlr.runtime.Token;

import ssac.aadlc.AADLMessage;
import ssac.aadlc.CompileException;
import ssac.aadlc.analysis.AADLAnalyzer;
import ssac.aadlc.analysis.AADLLocation;
import ssac.aadlc.analysis.AADLLocationRange;
import ssac.aadlc.analysis.type.AADLType;
import ssac.aadlc.analysis.type.AADLTypeManager;
import ssac.aadlc.codegen.ACodeObject;

/**
 * AADLデータ型定義
 * 
 * @version 1.30	2009/12/02
 */
public class ACodeDataType extends ACodeObject
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private final AADLLocationRange codeRange = new AADLLocationRange();

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	protected ACodeDataType() {
		super();
	}
	
	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public AADLLocation getMinLocation() {
		return codeRange.getMin();
	}
	
	public AADLLocation getMaxLocation() {
		return codeRange.getMax();
	}
	
	public int getMinLine() {
		return codeRange.getMinLine();
	}
	
	public int getMaxLine() {
		return codeRange.getMaxLine();
	}
	
	public int getMinCharPositionInLine() {
		return codeRange.getMinCharPositionInLine();
	}
	
	public int getMaxCharPositionInLine() {
		return codeRange.getMaxCharPositionInLine();
	}
	
	// Common
	static public ACodeDataType buildType(AADLAnalyzer analyzer, Token astToken) {
		ACodeDataType retCode = new ACodeDataType();
		retCode.genDataType(analyzer, astToken);
		return retCode;
	}
	
	static public ACodeDataType buildType(AADLAnalyzer analyzer, List<Token> astTokens) {
		ACodeDataType retCode = new ACodeDataType();
		retCode.genDataType(analyzer, astTokens.toArray(new Token[astTokens.size()]));
		return retCode;
	}
	
	//------------------------------------------------------------
	// Code generators
	//------------------------------------------------------------
	
	private void genDataType(AADLAnalyzer analyzer, Token...astTokens) {
		if (astTokens.length <= 0)
			throw new AssertionError("Data type token is nothing!");
		
		String strType;
		if (astTokens.length == 1) {
			Token t = astTokens[0];
			strType = t.getText();
			codeRange.set(t.getLine(), t.getCharPositionInLine(),
					t.getLine(), (t.getCharPositionInLine()+strType.length()));
		} else {
			Token ft = astTokens[0];
			Token et = ft;
			StringBuilder sb = new StringBuilder();
			sb.append(ft.getText());
			for (int i = 0; i < astTokens.length; i++) {
				et = astTokens[i];
				sb.append('.');
				sb.append(et.getText());
			}
			strType = sb.toString();
			codeRange.set(ft.getLine(), ft.getCharPositionInLine(),
					et.getLine(), (et.getCharPositionInLine()+et.getText().length()));
		}
		
		// get AADLType
		AADLType dataType = AADLTypeManager.getTypeByName(strType);
		if (dataType == null) {
			//--- Javaクラス名から、AADLTypeを取得
			try {
				Class javaClass = Class.forName(strType);
				dataType = AADLTypeManager.getTypeByJavaClass(javaClass);
			}
			catch (Exception ex) {
				//--- Not found
			}
			//--- 現在、Javaクラスの利用はできない
			dataType = null;
		}
		
		// Check
		if (dataType == null) {
			String strmsg = AADLMessage.undefinedToken(strType);
			throw new CompileException(codeRange.getMinLine(), codeRange.getMinCharPositionInLine(), strmsg);
		}
		
		// gen Codes
		jlb.appendLine(codeRange.getMinLine(), dataType.getJavaClassName());
		setType(dataType);
	}
	
	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

}
