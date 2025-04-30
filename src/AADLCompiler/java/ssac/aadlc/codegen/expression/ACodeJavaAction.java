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
