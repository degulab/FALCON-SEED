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
 * @(#)ACodeInvolving.java	1.50	2010/09/27
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ACodeInvolving.java	1.30	2009/12/02
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ACodeInvolving.java	1.00	2007/11/29
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadlc.codegen.expression;

import java.util.List;
import java.util.Stack;

import org.antlr.runtime.tree.CommonTree;

import ssac.aadlc.AADLMessage;
import ssac.aadlc.CompileException;
import ssac.aadlc.analysis.AADLAnalyzer;
import ssac.aadlc.analysis.AADLInvolvIdentifier;
import ssac.aadlc.analysis.AADLSymbol;
import ssac.aadlc.analysis.type.AADLListType;
import ssac.aadlc.analysis.type.AADLType;
import ssac.aadlc.analysis.type.AADLVoid;
import ssac.aadlc.codegen.ACodeObject;
import ssac.aadlc.codegen.stms.ACodeBlock;
import ssac.aadlc.core.AADLLexer;

/**
 * 内包記法
 * 
 * @version 1.50	2010/09/27
 */
public class ACodeInvolving extends ACodeObject
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private AADLSymbol varSymbol = null;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	protected ACodeInvolving() {
		super();
	}
	
	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 内包記述ブロックのコード生成
	 * 
	 * @param analyzer		アナライザー
	 * @param astInvolv		内包記述ブロックのツリー
	 * @param exp			内包式
	 * @param astCond		内包条件式
	 * @param filters		内包条件コード
	 * @return	ACodeInvolving インスタンス
	 */
	static public ACodeInvolving buildInvolving(AADLAnalyzer analyzer, CommonTree astInvolv,
													ACodeObject exp, CommonTree astCond,
													List<ACodeObject> filters)
	{
		ACodeInvolving retCode = new ACodeInvolving();
		if (astInvolv != null && astCond != null && exp != null) {
			ACodeObject[] objs;
			if (filters != null)
				objs = filters.toArray(new ACodeObject[filters.size()]);
			else
				objs = new ACodeObject[0];
			retCode.genInvolv(analyzer, astInvolv, exp, astCond, objs);
		}
		return retCode;
	}
	
	public boolean hasSymbol() {
		return (this.varSymbol != null);
	}
	
	public AADLSymbol getSymbol() {
		return this.varSymbol;
	}
	
	//------------------------------------------------------------
	// Code generators
	//------------------------------------------------------------
	
	private void genInvolv(AADLAnalyzer analyzer, CommonTree astInvolv, ACodeObject exp,
							CommonTree astCond, ACodeObject...filters)
	{
		// get current involv ID
		AADLInvolvIdentifier invid = analyzer.peekInvolving();
		
		boolean voidInvolving = false;
		if (astInvolv.getToken().getType() == AADLLexer.VOIDINVOLV) {
			voidInvolving = true;
		}
		
		// setup variable
		if (!voidInvolving) {
			// 値を返す内包記法
			String varName = invid.name();
			AADLListType varType = new AADLListType(java.util.ArrayList.class, exp.getType());
			//--- 要素格納リストの変数設定
			try {
				analyzer.setSymbol(false, varName, varType);
			}
			catch (Exception ex) {
				String msg = AADLMessage.alreadyExistVariable(varName);
				throw new CompileException(astInvolv.getLine(), astInvolv.getCharPositionInLine(), msg, ex);
			}
			this.varSymbol = analyzer.getSymbolValue(varName);
			//--- 要素格納リストの生成
			String strCode = String.format("%s %s = new %s();",
					varType.getJavaClassName(),
					this.varSymbol.getJavaSymbolName(),
					varType.getJavaConstructorName());
			this.jlb.appendLine(strCode, astInvolv.getLine());
		} else {
			// 値を返さない内包記法
			this.varSymbol = null;
		}
		
		// 内包記法ラベルの生成
		boolean setLabel = false;
		String invLabel = invid.label();
		if (invLabel == null)
			throw new AssertionError("Embedded notation's label is not exist!");
		if (!AADLAnalyzer.ANONYMOUS_LABEL.equals(invLabel)) {
			setLabel = true;
		}

		// 内包記法のループ
		Stack<ACodeObject> termStack = new Stack<ACodeObject>();
		for (ACodeObject flt : filters) {
			if (flt instanceof ACodeInvolvIterator) {
				// 内包記法イテレータ
				ACodeInvolvIterator iit = (ACodeInvolvIterator)flt;
				if (iit.hasInnerInvolv()) {
					this.jlb.add(iit.getInnerInvolv().getJavaLineBuffer());
				}
				if (iit.hasInnerReaderDefinition()) {
					this.jlb.add(iit.getInnerReaderDefinition().getJavaLineBuffer());
				}
				if (setLabel) {
					setLabel = false;
					this.jlb.appendLine(astInvolv.getLine(), invLabel, ":");
				}
				this.jlb.add("for(", iit.getJavaLineBuffer(), ") {");
				termStack.push(iit.getInnerReaderTermination());
			}
			else if (flt instanceof ACodeInvolvAlias) {
				// 内包記法エイリアス
				this.jlb.add(flt.getJavaLineBuffer());
			}
			else if (flt instanceof ACodeBlock) {
				// 内包記述内部ブロック
				this.jlb.add(flt.getJavaLineBuffer());
			}
			else if (flt != null) {
				// 内包記法評価式
				AADLType expType = flt.getType();
				if (expType.isJavaAction() || expType.canBooleanOperation()) {
					this.jlb.add("if (!(", flt.getJavaLineBuffer(), ")) continue;");
				}
				else {
					String msg = AADLMessage.notConditionalExpression();
					throw new CompileException(flt.getMinLineNo(), msg);
				}
			}
		}
		
		// 内包記法の実行コード
		if (voidInvolving) {
			// 値を返さない内包記法
			this.jlb.add(null, exp.getJavaLineBuffer(), ";");
		} else {
			// 値を返す内包記法
			this.jlb.add(this.varSymbol.getJavaSymbolName() + ".add(", exp.getJavaLineBuffer(), ");");
		}
		
		// 内包記法を閉じる
		while (!termStack.isEmpty()) {
			this.jlb.appendLine("}", this.jlb.getMaxLineNo());
			ACodeObject term = termStack.pop();
			if (term != null) {
				this.jlb.add(term.getJavaLineBuffer());
			}
		}
		
		// データ型設定
		if (this.varSymbol != null) {
			setType(this.varSymbol.getType());
		} else {
			setType(AADLVoid.instance);
		}
	}
	
	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	/*
	static public String getInvolvLabel() {
		String name = String.format("l_involv$%d", involvNo);
		return name;
	}

	static private String getInvolvName() {
		String name = String.format("involv$%d", involvNo);
		involvNo++;
		return name;
	}
	*/
}
