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
 * @(#)ACodeControl.java	1.50	2010/09/27
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ACodeControl.java	1.30	2009/12/02
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ACodeControl.java	1.20	2008/09/25
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ACodeControl.java	1.00	2007/11/29
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadlc.codegen.stms;

import org.antlr.runtime.Token;
import org.antlr.runtime.tree.CommonTree;

import ssac.aadlc.AADLMessage;
import ssac.aadlc.CompileException;
import ssac.aadlc.analysis.AADLAnalyzer;
import ssac.aadlc.analysis.type.prim.APrimBoolean;
import ssac.aadlc.analysis.type.prim.APrimDecimal;
import ssac.aadlc.codegen.ACodeObject;

/**
 * AADL 制御文
 * 
 * @version 1.50	2010/09/27
 */
public class ACodeControl extends ACodeStatement
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	protected ACodeControl() {
		super();
	}
	
	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 式のみの文展開
	 */
	static public ACodeControl binExpression(AADLAnalyzer analyzer, ACodeObject exp) {
		ACodeControl retCode = new ACodeControl();
		if (exp != null) {
			retCode.jlb.add(null, exp.getJavaLineBuffer(), ";");
		}
		return retCode;
	}
	
	/**
	 * 式のみの文展開(セミコロンは付加しない)
	 * @since 1.40
	 */
	static public ACodeControl binExpressionWithoutTerm(AADLAnalyzer analyzer, ACodeObject exp) {
		ACodeControl retCode = new ACodeControl();
		if (exp != null) {
			retCode.jlb.add(null, exp.getJavaLineBuffer(), null);
		}
		return retCode;
	}

	/**
	 * IF 制御構文の展開
	 */
	static public ACodeControl ctrlIf(AADLAnalyzer analyzer, CommonTree astTree,
										ACodeObject aCond, ACodeStatement stmThen, ACodeStatement stmElse)
	{
		ACodeControl retCode = new ACodeControl();
		if (astTree != null && aCond != null && stmThen != null) {
			retCode.genIfStatement(analyzer, astTree, aCond, stmThen, stmElse);
		}
		return retCode;
	}

	/**
	 * ELSE 制御構文の展開
	 */
	static public ACodeControl ctrlElse(AADLAnalyzer analyzer, CommonTree astTree,
										  ACodeStatement stm)
	{
		ACodeControl retCode = new ACodeControl();
		if (astTree != null && stm != null) {
			retCode.genElseStatement(analyzer, astTree, stm);
		}
		return retCode;
	}

	/**
	 * RETURN 文の展開
	 */
	static public ACodeControl ctrlReturn(AADLAnalyzer analyzer, CommonTree astTree,
											ACodeObject retValue)
	{
		ACodeControl retCode = new ACodeControl();
		if (astTree != null) {
			retCode.genReturnStatement(analyzer, astTree, retValue);
		}
		return retCode;
	}
	
	/**
	 * BREAK 文の展開
	 */
	static public ACodeControl ctrlBreak(AADLAnalyzer analyzer, CommonTree astTree, CommonTree idTree) {
		ACodeControl retCode = new ACodeControl();
		if (astTree != null) {
			if (idTree != null && idTree.getToken() != null)
				retCode.genBreakStatement(analyzer, astTree, idTree.getToken());
			else
				retCode.genBreakStatement(analyzer, astTree, null);
		}
		return retCode;
	}
	
	//------------------------------------------------------------
	// Code generators
	//------------------------------------------------------------
	
	private void genIfStatement(AADLAnalyzer analyzer, CommonTree astTree, ACodeObject aCond,
									ACodeStatement stmThen, ACodeStatement stmElse)
	{
		// Check
		if (!aCond.getType().canBooleanOperation()) {
			String msg = AADLMessage.cannotConvert(aCond.getType().getName(), APrimBoolean.instance.getName());
			if (astTree.getChildCount() > 0) {
				int charPos = astTree.getChild(0).getCharPositionInLine();
				throw new CompileException(aCond.getMinLineNo(), charPos, msg);
			}
			else {
				throw new CompileException(aCond.getMinLineNo(), msg);
			}
		}
		
		// create codes
		this.jlb.appendLine("if", astTree.getLine());
		this.jlb.add(" (", aCond.getJavaLineBuffer(), ") ");
		this.jlb.appendLine("{", astTree.getLine());
		this.jlb.add(stmThen.getJavaLineBuffer());
		this.jlb.appendLine("}", astTree.getLine());
		if (stmElse != null) {
			this.jlb.add(stmElse.getJavaLineBuffer());
		}
	}
	
	private void genElseStatement(AADLAnalyzer analyzer, CommonTree astTree, ACodeStatement stm) {
		this.jlb.appendLine("else {", astTree.getLine());
		this.jlb.add(stm.getJavaLineBuffer());
		this.jlb.appendLine("}", astTree.getLine());
	}
	
	private void genReturnStatement(AADLAnalyzer analyzer, CommonTree astTree, ACodeObject retValue) {
		if (analyzer.isParsingProgramBlock()) {
			// "program" ブロック内のステートメント
			if (retValue != null) {
				if (retValue.getType().equals(APrimDecimal.instance)) {
					// Decimal型なら、整数値を取得するメソッドを使用
					this.jlb.appendLine("return ", astTree.getLine());
					this.jlb.add("(", retValue.getJavaLineBuffer(), ").intValueExact();");
				} else {
					// Decimal以外の型なら、そのまま(Javaコンパイラに委譲)
					this.jlb.appendLine("return ", astTree.getLine());
					this.jlb.add("(", retValue.getJavaLineBuffer(), ");");
				}
			}
			else {
				// error
				this.jlb.appendLine("return;", astTree.getLine());
			}
		}
		else {
			// "program" ブロック以外のステートメント
			if (retValue != null) {
				this.jlb.appendLine("return ", astTree.getLine());
				this.jlb.add("(", retValue.getJavaLineBuffer(), ");");
			}
			else {
				this.jlb.appendLine("return;", astTree.getLine());
			}
		}
	}
	
	private void genBreakStatement(AADLAnalyzer analyzer, CommonTree astTree, Token identifier) {
		// 記述位置チェック
		if (!analyzer.isParsingInvolvingBlock()) {
			// 内包記述外での break 文は、使用禁止
			String msg = AADLMessage.cannotUseBreak();
			throw new CompileException(astTree.getLine(), astTree.getCharPositionInLine(), msg);
		}
		
		// ラベルチェック
		//--- 現在は、ラベル名の記述は不可
		if (identifier != null) {
			String msg = AADLMessage.undefinedToken(identifier.getText());
			throw new CompileException(identifier.getLine(), identifier.getCharPositionInLine(), msg);
		}
		
		// 直上の内包記述の外へ脱出する break 文を記述
		String breaklabel = analyzer.peekInvolvingLabel();
		if (breaklabel == null)
			throw new AssertionError("Break label is not exist!");
		if (AADLAnalyzer.ANONYMOUS_LABEL.equals(breaklabel)) {
			this.jlb.appendLine(astTree.getLine(), "break;");
		} else {
			this.jlb.appendLine(astTree.getLine(), "break ", breaklabel, ";");
		}
	}
	
	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

}
