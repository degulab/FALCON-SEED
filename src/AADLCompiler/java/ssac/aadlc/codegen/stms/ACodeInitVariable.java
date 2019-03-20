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
 *  Copyright 2007-2014  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)ACodeInitVariable.java	2.1.0	2014/05/29
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ACodeInitVariable.java	1.70	2011/06/29
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ACodeInitVariable.java	1.50	2010/09/27
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ACodeInitVariable.java	1.40	2010/02/19
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ACodeInitVariable.java	1.30	2009/12/02
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ACodeInitVariable.java	1.10	2008/05/16
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ACodeInitVariable.java	1.00	2007/11/29
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadlc.codegen.stms;

import java.util.List;

import org.antlr.runtime.Token;
import org.antlr.runtime.tree.CommonTree;

import ssac.aadlc.AADLMessage;
import ssac.aadlc.CompileException;
import ssac.aadlc.analysis.AADLAnalyzer;
import ssac.aadlc.analysis.AADLFileOptionEntry;
import ssac.aadlc.analysis.type.AADLObject;
import ssac.aadlc.analysis.type.AADLType;
import ssac.aadlc.analysis.type.prim.APrimBoolean;
import ssac.aadlc.analysis.type.prim.APrimCsvFileReader;
import ssac.aadlc.analysis.type.prim.APrimCsvFileWriter;
import ssac.aadlc.analysis.type.prim.APrimDecimal;
import ssac.aadlc.analysis.type.prim.APrimDecimalRange;
import ssac.aadlc.analysis.type.prim.APrimDtBase;
import ssac.aadlc.analysis.type.prim.APrimDtBasePattern;
import ssac.aadlc.analysis.type.prim.APrimExBase;
import ssac.aadlc.analysis.type.prim.APrimExBasePattern;
import ssac.aadlc.analysis.type.prim.APrimMqttCsvParameter;
import ssac.aadlc.analysis.type.prim.APrimNaturalNumberDecimalRange;
import ssac.aadlc.analysis.type.prim.APrimSimpleDecimalRange;
import ssac.aadlc.analysis.type.prim.APrimTextFileReader;
import ssac.aadlc.analysis.type.prim.APrimTextFileWriter;
import ssac.aadlc.codegen.ACodeObject;
import ssac.aadlc.codegen.define.ACodeVariable;
import ssac.aadlc.codegen.expression.ACodeInvolving;

/**
 * AADL 変数宣言文
 * 
 * @version 2.1.0	2014/05/29
 */
public class ACodeInitVariable extends ACodeAssign
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
	
	protected ACodeInitVariable() {
		super();
	}
	
	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 式による変数初期化
	 */
	static public ACodeInitVariable initByExpression(AADLAnalyzer analyzer, ACodeVariable aVar,
												 CommonTree astTree, ACodeObject exp)
	{
		ACodeInitVariable retCode = new ACodeInitVariable();
		if (aVar != null && astTree != null && exp != null) {
			retCode.genAssignExpression(analyzer, aVar, astTree, exp);
		}
		return retCode;
	}

	/**
	 * 内包式による変数初期化
	 */
	static public ACodeInitVariable initByInvolv(AADLAnalyzer analyzer, ACodeVariable aVar,
											 CommonTree astTree, ACodeInvolving involv)
	{
		ACodeInitVariable retCode = new ACodeInitVariable();
		if (aVar != null && astTree != null && involv != null) {
			retCode.genAssignInvolv(analyzer, aVar, astTree, involv);
		}
		return retCode;
	}

	/**
	 * ファイル入力による変数初期化
	 */
	static public ACodeInitVariable initByFile(AADLAnalyzer analyzer, ACodeVariable aVar, Token astDir,
												Token astFile, ACodeObject fileArg, ACodeObject encoding,
												List<AADLFileOptionEntry> foptions)
	{
		ACodeInitVariable retCode = new ACodeInitVariable();
		if (aVar != null && astDir != null && astFile != null && fileArg != null) {
			retCode.genAssignFile(analyzer, true, aVar, astDir, astFile, fileArg, encoding, foptions);
		}
		return retCode;
	}

	/**
	 * コンストラクタ引数の明示による変数初期化
	 */
	static public ACodeInitVariable construct(AADLAnalyzer analyzer, ACodeVariable aVar,
												List<ACodeObject> params)
	{
		ACodeInitVariable retCode = new ACodeInitVariable();
		if (aVar != null) {
			ACodeObject[] objs;
			if (params != null)
				objs = params.toArray(new ACodeObject[params.size()]);
			else
				objs = new ACodeObject[0];
			retCode.genConstruct(analyzer, aVar, objs);
		}
		return retCode;
	}
	
	//------------------------------------------------------------
	// Code generators
	//------------------------------------------------------------
	
	protected void genConstruct(AADLAnalyzer analyzer, ACodeVariable aVar, ACodeObject...params) {
		// コンストラクタの有無
		AADLType[] paramTypes = new AADLType[params.length];
		for (int i = 0; i < params.length; i++) {
			if (params[i] != null)
				paramTypes[i] = params[i].getType();
			else
				paramTypes[i] = AADLObject.instance;
		}
		
		// generate codes
		if (aVar.getType().hasConstructor(paramTypes)) {
			// コンストラクタ有り
			addVariableCode(analyzer, aVar);
			this.jlb.appendLine(aVar.getMaxLineNo(), " = new ", aVar.getType().getJavaConstructorName(), "(");
			if (params.length > 0) {
				if (params[0] != null) {
					this.jlb.add(params[0].getJavaLineBuffer());
				}
				for (int i = 1; i < params.length; i++) {
					if (params[i] != null) {
						this.jlb.add(", ", params[i].getJavaLineBuffer(), null);
					}
				}
			}
			this.jlb.appendLine(");", aVar.getMaxLineNo());
		}
		else if (paramTypes.length <= 0) {
			//
			// デフォルトコンストラクタが未定義のもの
			//
			if (aVar.getType().equals(APrimBoolean.instance)) {
				// Boolean型初期化
				addVariableCode(analyzer, aVar);
				this.jlb.appendLine(aVar.getMaxLineNo(), " = java.lang.Boolean.FALSE;");
			}
			else if (aVar.getType().equals(APrimDecimal.instance)) {
				// Decimal型初期化
				addVariableCode(analyzer, aVar);
				this.jlb.appendLine(aVar.getMaxLineNo(), " = java.math.BigDecimal.ZERO;");
			}
			else if (aVar.getType().equals(APrimExBase.instance)) {
				// ExBase型初期化
				addVariableCode(analyzer, aVar);
				this.jlb.appendLine(aVar.getMaxLineNo(), " = new exalge2.ExBase(exalge2.ExBase.OMITTED, exalge2.ExBase.NO_HAT);");
			}
			else if (aVar.getType().equals(APrimExBasePattern.instance)) {
				// ExBasePattern型初期化
				addVariableCode(analyzer, aVar);
				this.jlb.appendLine(aVar.getMaxLineNo(), " = new exalge2.ExBasePattern(exalge2.ExBasePattern.WILDCARD, exalge2.ExBasePattern.WILDCARD);");
			}
			else if (aVar.getType().equals(APrimDtBase.instance)) {
				// DtBase型初期化(データ型基底が不明となる為、インスタンスは null とする)
				addVariableCode(analyzer, aVar);
				this.jlb.appendLine(aVar.getMaxLineNo(), " = null;");
			}
			else if (aVar.getType().equals(APrimDtBasePattern.instance)) {
				// DtBasePattern 型初期化
				addVariableCode(analyzer, aVar);
				this.jlb.appendLine(aVar.getMaxLineNo(), " = new dtalge.DtBasePattern(dtalge.DtBasePattern.WILDCARD, dtalge.DtBasePattern.WILDCARD);");
			}
			else if (aVar.getType().equals(APrimTextFileReader.instance)
					|| aVar.getType().equals(APrimTextFileWriter.instance)
					|| aVar.getType().equals(APrimCsvFileReader.instance)
					|| aVar.getType().equals(APrimCsvFileWriter.instance))
			{
				// この型は、初期値を null とする
				addVariableCode(analyzer, aVar);
				this.jlb.appendLine(aVar.getMaxLineNo(), " = null;");
			}
			else if (aVar.getType().equals(APrimDecimalRange.instance)
					|| aVar.getType().equals(APrimSimpleDecimalRange.instance)
					|| aVar.getType().equals(APrimNaturalNumberDecimalRange.instance))
			{
				// この型は、初期値を null とする
				addVariableCode(analyzer, aVar);
				this.jlb.appendLine(aVar.getMaxLineNo(), " = null;");
			}
			else if (aVar.getType().equals(APrimMqttCsvParameter.instance))
			{
				// この型は、初期値を null とする
				addVariableCode(analyzer, aVar);
				this.jlb.appendLine(aVar.getMaxLineNo(), " = null;");
			}
			else if (aVar.getType().equals(AADLObject.instance)) {
				// Object型初期化(インスタンスが特定できないため、null とする)
				addVariableCode(analyzer, aVar);
				this.jlb.appendLine(aVar.getMaxLineNo(), " = null;");
			}
			else {
				// エラー：対応コンストラクタ無し
				String msg = AADLMessage.undefinedConstructor(aVar.getType().getName(),
															AADLMessage.toStringParams(paramTypes));
				throw new CompileException(aVar.getMinLineNo(), msg);
			}
		}
		else {
			// エラー：対応コンストラクタ無し
			String msg = AADLMessage.undefinedConstructor(aVar.getType().getName(),
														AADLMessage.toStringParams(paramTypes));
			throw new CompileException(aVar.getMinLineNo(), msg);
		}
	}
	
	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

}
