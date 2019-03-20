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
 * @(#)ACodeJavaAction.java	1.00	2007/11/29
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadlc.codegen.expression;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.antlr.runtime.tree.CommonTree;

import ssac.aadlc.analysis.AADLAnalyzer;
import ssac.aadlc.analysis.type.AADLJavaAction;
import ssac.aadlc.codegen.ACodeObject;

/**
 * JAVAアクション
 * 
 * @version 1.00	2007/11/29
 */
public class ACodeJavaAction extends ACodeObject
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	private static final Pattern patJavaAction =
		Pattern.compile("\\A\\s*\\Q@{\\E(.*)\\Q}@\\E\\s*\\z", Pattern.DOTALL);
	
	private static final Pattern patJavaHeaderAction =
		Pattern.compile("\\A\\s*\\Q@header{\\E(.*)\\Q}@\\E\\s*\\z", Pattern.DOTALL);
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	protected ACodeJavaAction() {
		super();
	}
	
	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	// @header{...}
	static public ACodeJavaAction buildJavaHeaderAction(AADLAnalyzer analyzer, CommonTree astTree) {
		ACodeJavaAction retCode = new ACodeJavaAction();
		retCode.genJavaAction(analyzer, astTree, patJavaHeaderAction);
		return retCode;
	}
	
	// @{...}
	static public ACodeJavaAction buildJavaAction(AADLAnalyzer analyzer, CommonTree astTree) {
		ACodeJavaAction retCode = new ACodeJavaAction();
		retCode.genJavaAction(analyzer, astTree, patJavaAction);
		return retCode;
	}
	
	//------------------------------------------------------------
	// Code generators
	//------------------------------------------------------------
	
	private void genJavaAction(AADLAnalyzer analyzer, CommonTree astTree, Pattern patAction) {
		String strToken = astTree.getText();
		Matcher mc = patAction.matcher(strToken);
		if (mc.matches()) {
			strToken = mc.group(1);
		}
		
		String[] lines = strToken.split("\r?\n");
		if (lines.length > 0) {
			int lineNo = astTree.getLine();
			for (int i = 0; i < lines.length; i++) {
				jlb.appendLine(lines[i], lineNo+i);
			}
		}
		else {
			jlb.appendLine(strToken, astTree.getLine());
		}
		
		setType(AADLJavaAction.instance);
	}
	
	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

}
