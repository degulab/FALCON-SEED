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
 * @(#)ACodeLiteral.java	1.50	2010/09/27
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ACodeLiteral.java	1.40	2010/02/23
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ACodeLiteral.java	1.30	2009/12/02
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ACodeLiteral.java	1.10	2008/05/12
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ACodeLiteral.java	1.00	2007/11/29
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadlc.codegen.expression;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;

import org.antlr.runtime.tree.CommonTree;

import ssac.aadlc.AADLMessage;
import ssac.aadlc.CompileException;
import ssac.aadlc.analysis.AADLAnalyzer;
import ssac.aadlc.analysis.type.AADLCollectionType;
import ssac.aadlc.analysis.type.AADLJavaAction;
import ssac.aadlc.analysis.type.AADLListType;
import ssac.aadlc.analysis.type.AADLMapType;
import ssac.aadlc.analysis.type.AADLType;
import ssac.aadlc.analysis.type.AADLTypeManager;
import ssac.aadlc.analysis.type.prim.APrimBoolean;
import ssac.aadlc.analysis.type.prim.APrimBooleanList;
import ssac.aadlc.analysis.type.prim.APrimCharacter;
import ssac.aadlc.analysis.type.prim.APrimDecimal;
import ssac.aadlc.analysis.type.prim.APrimDecimalList;
import ssac.aadlc.analysis.type.prim.APrimDouble;
import ssac.aadlc.analysis.type.prim.APrimDtAlgeSet;
import ssac.aadlc.analysis.type.prim.APrimDtBase;
import ssac.aadlc.analysis.type.prim.APrimDtBasePattern;
import ssac.aadlc.analysis.type.prim.APrimDtBasePatternSet;
import ssac.aadlc.analysis.type.prim.APrimDtBaseSet;
import ssac.aadlc.analysis.type.prim.APrimDtalge;
import ssac.aadlc.analysis.type.prim.APrimExAlgeSet;
import ssac.aadlc.analysis.type.prim.APrimExBase;
import ssac.aadlc.analysis.type.prim.APrimExBasePattern;
import ssac.aadlc.analysis.type.prim.APrimExBasePatternSet;
import ssac.aadlc.analysis.type.prim.APrimExBaseSet;
import ssac.aadlc.analysis.type.prim.APrimExalge;
import ssac.aadlc.analysis.type.prim.APrimFloat;
import ssac.aadlc.analysis.type.prim.APrimInteger;
import ssac.aadlc.analysis.type.prim.APrimLong;
import ssac.aadlc.analysis.type.prim.APrimNull;
import ssac.aadlc.analysis.type.prim.APrimString;
import ssac.aadlc.analysis.type.prim.APrimStringList;
import ssac.aadlc.codegen.ACodeObject;

/**
 * AADL リテラル
 *
 * 
 * @version 1.50	2010/09/27
 */
public class ACodeLiteral extends ACodeObject
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
	
	protected ACodeLiteral() {
		super();
	}
	
	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	// NullLiteral
	static public ACodeLiteral buildNull(AADLAnalyzer analyzer, CommonTree astTree) {
		ACodeLiteral retCode = new ACodeLiteral();
		retCode.genNullLiteral(analyzer, astTree);
		return retCode;
	}

	// CharacterLiteral
	static public ACodeLiteral buildCharacter(AADLAnalyzer analyzer, CommonTree astTree) {
		ACodeLiteral retCode = new ACodeLiteral();
		retCode.genCharacterLiteral(analyzer, astTree);
		return retCode;
	}

	// StringLiteral
	static public ACodeLiteral buildString(AADLAnalyzer analyzer, CommonTree astTree) {
		ACodeLiteral retCode = new ACodeLiteral();
		retCode.genStringLiteral(analyzer, astTree);
		return retCode;
	}
	
	// IntegerLiteral
	static public ACodeLiteral buildInteger(AADLAnalyzer analyzer, CommonTree astTree) {
		ACodeLiteral retCode = new ACodeLiteral();
		retCode.genIntegerLiteral(analyzer, astTree);
		return retCode;
	}

	// Hex IntegerLiteral
	static public ACodeLiteral buildHexInteger(AADLAnalyzer analyzer, CommonTree astTree) {
		ACodeLiteral retCode = new ACodeLiteral();
		retCode.genHexIntegerLiteral(analyzer, astTree);
		return retCode;
	}

	// Octal IntegerLiteral
	static public ACodeLiteral buildOctalInteger(AADLAnalyzer analyzer, CommonTree astTree) {
		ACodeLiteral retCode = new ACodeLiteral();
		retCode.genOctalIntegerLiteral(analyzer, astTree);
		return retCode;
	}

	// DecimalLiteral
	static public ACodeLiteral buildDecimal(AADLAnalyzer analyzer, CommonTree astTree) {
		ACodeLiteral retCode = new ACodeLiteral();
		retCode.genDecimalLiteral(analyzer, astTree);
		return retCode;
	}

	// BooleanLiteral
	static public ACodeLiteral buildBoolean(AADLAnalyzer analyzer, CommonTree astTree) {
		ACodeLiteral retCode = new ACodeLiteral();
		retCode.genBooleanLiteral(analyzer, astTree);
		return retCode;
	}

	// ExBaseLiteral
	static public ACodeLiteral buildExBase(AADLAnalyzer analyzer, CommonTree astTree,
											 List<ACodeObject> params)
	{
		ACodeLiteral retCode = new ACodeLiteral();
		retCode.genExBaseLiteral(analyzer, astTree, params);
		return retCode;
	}
	
	// DtBaseLietral
	static public ACodeLiteral buildDtBase(AADLAnalyzer analyzer, CommonTree astTree,
											 List<ACodeObject> params)
	{
		ACodeLiteral retCode = new ACodeLiteral();
		retCode.genDtBaseLiteral(analyzer, astTree, params);
		return retCode;
	}

	// ArrayLiteral
	static public ACodeLiteral buildArrayLiteral(AADLAnalyzer analyzer, CommonTree astTree,
												   List<ACodeObject> values)
	{
		ACodeLiteral retCode = new ACodeLiteral();
		retCode.genArrayLiteral(analyzer, astTree, values);
		return retCode;
	}

	// MapLiteral
	static public ACodeLiteral buildMapLiteral(AADLAnalyzer analyzer, CommonTree astTree,
												List<ACodeObject> keys, List<ACodeObject> values)
	{
		ACodeLiteral retCode = new ACodeLiteral();
		retCode.genMapLiteral(analyzer, astTree, keys, values);
		return retCode;
	}
	
	//------------------------------------------------------------
	// Code generators
	//------------------------------------------------------------
	
	// nullリテラルのJAVAコード生成
	private void genNullLiteral(AADLAnalyzer analyzer, CommonTree astTree) {
		jlb.appendLine(astTree.getText(), astTree.getLine());
		setType(APrimNull.instance);
	}
	
	// 文字リテラルのJAVAコード生成
	private void genCharacterLiteral(AADLAnalyzer analyzer, CommonTree astTree) {
		jlb.appendLine(astTree.getText(), astTree.getLine());
		setType(APrimCharacter.instance);
	}

	// 文字列リテラルのJAVAコード生成
	private void genStringLiteral(AADLAnalyzer analyzer, CommonTree astTree) {
		jlb.appendLine(astTree.getText(), astTree.getLine());
		setType(APrimString.instance);
	}

	// 8進リテラルのJAVAコード生成
	private void genOctalIntegerLiteral(AADLAnalyzer analyzer, CommonTree astTree) {
		String literal = astTree.getText();
		char lastChar = literal.charAt(literal.length()-1);
		if (lastChar=='l' || lastChar=='L') {
			// Long value
			String value = literal.substring(0, literal.length()-1);
			//--- check Long value range
			try {
				Long.parseLong(value, 8);
			}
			catch (NumberFormatException ex) {
				// error
				String msg = AADLMessage.literalOutOfRange(literal, APrimLong.instance.getName());
				throw new CompileException(astTree.getLine(), astTree.getCharPositionInLine(), msg);
			}
			//--- generate code
			jlb.appendLine(astTree.getLine(), "Long.parseLong(\"", value, "\",8)");
			setType(AADLTypeManager.getTypeByJavaClass(Long.TYPE));
		}
		else if (lastChar=='i' || lastChar=='I') {
			// Integer value
			String value = literal.substring(0, literal.length()-1);
			//--- check Integer value range
			try {
				Integer.parseInt(value, 8);
			}
			catch (NumberFormatException ex) {
				// error
				String msg = AADLMessage.literalOutOfRange(literal, APrimInteger.instance.getName());
				throw new CompileException(astTree.getLine(), astTree.getCharPositionInLine(), msg);
			}
			//--- generate code
			jlb.appendLine(astTree.getLine(), "Integer.parseInt(\"", value, "\",8)");
			setType(AADLTypeManager.getTypeByJavaClass(Integer.TYPE));
		}
		else {
			// BigDecimal value
			//--- check Decimal value range
			try {
				new BigInteger(literal, 8);
			}
			catch (NumberFormatException ex) {
				// error
				String msg = AADLMessage.illegalLiteralFormat(literal, APrimDecimal.instance.getName());
				throw new CompileException(astTree.getLine(), astTree.getCharPositionInLine(), msg);
			}
			//--- generate code
			jlb.appendLine(astTree.getLine(), "new ", APrimDecimal.instance.getJavaConstructorName(),
												"new java.math.BigInteger(\"", literal, "\",8))");
			setType(APrimDecimal.instance);
		}
	}

	// 16進リテラルのJAVAコード生成
	private void genHexIntegerLiteral(AADLAnalyzer analyzer, CommonTree astTree) {
		String literal = astTree.getText();
		char lastChar = literal.charAt(literal.length()-1);
		if (lastChar=='l' || lastChar=='L') {
			// Long value
			String value = literal.substring(2, literal.length()-1);
			//--- check Long value range
			try {
				Long.parseLong(value, 16);
			}
			catch (NumberFormatException ex) {
				// error
				String msg = AADLMessage.literalOutOfRange(literal, APrimLong.instance.getName());
				throw new CompileException(astTree.getLine(), astTree.getCharPositionInLine(), msg);
			}
			//--- generate code
			jlb.appendLine(astTree.getLine(), "Long.parseLong(\"", value, "\",16)");
			setType(AADLTypeManager.getTypeByJavaClass(Long.TYPE));
		}
		else if (lastChar=='i' || lastChar=='I') {
			// Integer value
			String value = literal.substring(2, literal.length()-1);
			//--- check Integer value range
			try {
				Integer.parseInt(value, 16);
			}
			catch (NumberFormatException ex) {
				// error
				String msg = AADLMessage.literalOutOfRange(literal, APrimInteger.instance.getName());
				throw new CompileException(astTree.getLine(), astTree.getCharPositionInLine(), msg);
			}
			//--- generate code
			jlb.appendLine(astTree.getLine(), "Integer.parseInt(\"", value, "\",16)");
			setType(AADLTypeManager.getTypeByJavaClass(Integer.TYPE));
		}
		else {
			// BigDecimal value
			String value = literal.substring(2);
			//--- check Decimal value range
			try {
				new BigInteger(value, 16);
			}
			catch (NumberFormatException ex) {
				// error
				String msg = AADLMessage.illegalLiteralFormat(literal, APrimDecimal.instance.getName());
				throw new CompileException(astTree.getLine(), astTree.getCharPositionInLine(), msg);
			}
			//--- generate code
			jlb.appendLine(astTree.getLine(), "new ", APrimDecimal.instance.getJavaConstructorName(),
												"new java.math.BigInteger(\"", value, "\",16))");
			setType(APrimDecimal.instance);
		}
	}
	
	// 整数値リテラルのJAVAコード生成
	private void genIntegerLiteral(AADLAnalyzer analyzer, CommonTree astTree) {
		String literal = astTree.getText();
		char lastChar = literal.charAt(literal.length()-1);
		if (lastChar=='l' || lastChar=='L') {
			// Long value
			String value = literal.substring(0, literal.length()-1);
			//--- check Long value range
			try {
				Long.parseLong(value, 10);
			}
			catch (NumberFormatException ex) {
				// error
				String msg = AADLMessage.literalOutOfRange(literal, APrimLong.instance.getName());
				throw new CompileException(astTree.getLine(), astTree.getCharPositionInLine(), msg);
			}
			//--- generate code
			jlb.appendLine(astTree.getLine(), "Long.parseLong(\"", value, "\",10)");
			setType(AADLTypeManager.getTypeByJavaClass(Long.TYPE));
		}
		else if (lastChar=='i' || lastChar=='I') {
			// Integer value
			String value = literal.substring(0, literal.length()-1);
			//--- check Integer value range
			try {
				Integer.parseInt(value, 10);
			}
			catch (NumberFormatException ex) {
				// error
				String msg = AADLMessage.literalOutOfRange(literal, APrimInteger.instance.getName());
				throw new CompileException(astTree.getLine(), astTree.getCharPositionInLine(), msg);
			}
			//--- generate code
			jlb.appendLine(astTree.getLine(), "Integer.parseInt(\"", value, "\",10)");
			setType(AADLTypeManager.getTypeByJavaClass(Integer.TYPE));
		}
		else {
			// BigDecimal value
			//--- check Decimal value range
			try {
				new BigDecimal(literal);
			}
			catch (NumberFormatException ex) {
				// error
				String msg = AADLMessage.illegalLiteralFormat(literal, APrimDecimal.instance.getName());
				throw new CompileException(astTree.getLine(), astTree.getCharPositionInLine(), msg);
			}
			//--- generate code
			jlb.appendLine(astTree.getLine(), "new ", APrimDecimal.instance.getJavaConstructorName(),
												"(\"", literal, "\")");
			setType(APrimDecimal.instance);
		}
	}

	// 実数値リテラルのJAVAコード生成
	private void genDecimalLiteral(AADLAnalyzer analyzer, CommonTree astTree) {
		String literal = astTree.getText();
		char lastChar = literal.charAt(literal.length()-1);
		if (lastChar=='f' || lastChar=='F') {
			// Long value
			String value = literal.substring(0, literal.length()-1);
			//--- check Float value range
			try {
				new BigDecimal(value);
			}
			catch (NumberFormatException ex) {
				// error
				String msg = AADLMessage.illegalLiteralFormat(literal, APrimFloat.instance.getName());
				throw new CompileException(astTree.getLine(), astTree.getCharPositionInLine(), msg);
			}
			try {
				Float.parseFloat(value);
			}
			catch (NumberFormatException ex) {
				// error
				String msg = AADLMessage.literalOutOfRange(literal, APrimFloat.instance.getName());
				throw new CompileException(astTree.getLine(), astTree.getCharPositionInLine(), msg);
			}
			//--- generate code
			jlb.appendLine(astTree.getLine(), "Float.parseFloat(\"", value, "\")");
			setType(AADLTypeManager.getTypeByJavaClass(Float.TYPE));
		}
		else if (lastChar=='d' || lastChar=='D') {
			// Integer value
			String value = literal.substring(0, literal.length()-1);
			//--- check Double value range
			try {
				new BigDecimal(value);
			}
			catch (NumberFormatException ex) {
				// error
				String msg = AADLMessage.illegalLiteralFormat(literal, APrimDouble.instance.getName());
				throw new CompileException(astTree.getLine(), astTree.getCharPositionInLine(), msg);
			}
			try {
				Double.parseDouble(value);
			}
			catch (NumberFormatException ex) {
				// error
				String msg = AADLMessage.literalOutOfRange(literal, APrimDouble.instance.getName());
				throw new CompileException(astTree.getLine(), astTree.getCharPositionInLine(), msg);
			}
			//--- generate code
			jlb.appendLine(astTree.getLine(), "Double.parseDouble(\"", value, "\")");
			setType(AADLTypeManager.getTypeByJavaClass(Double.TYPE));
		}
		else {
			// BigDecimal value
			//--- check Decimal value range
			try {
				new BigDecimal(literal);
			}
			catch (NumberFormatException ex) {
				// error
				String msg = AADLMessage.illegalLiteralFormat(literal, APrimDecimal.instance.getName());
				throw new CompileException(astTree.getLine(), astTree.getCharPositionInLine(), msg);
			}
			//--- generate code
			jlb.appendLine(astTree.getLine(), "new ", APrimDecimal.instance.getJavaConstructorName(),
												"(\"", literal, "\")");
			setType(APrimDecimal.instance);
		}
	}

	// 真偽値リテラルのJAVAコード生成
	private void genBooleanLiteral(AADLAnalyzer analyzer, CommonTree astTree) {
		jlb.appendLine(astTree.getLine(), "new ", APrimBoolean.instance.getJavaConstructorName(),
											"(\"", astTree.getText(), "\")");
		setType(APrimBoolean.instance);
	}

	// 交換代数基底リテラルのJAVAコード生成
	private void genExBaseLiteral(AADLAnalyzer analyzer, CommonTree astTree, List<ACodeObject> params)
	{
		// Check
		if (params == null || params.isEmpty()) {
			throw new CompileException(astTree.getLine(), AADLMessage.notExistExBaseLiteralKeys());
		}
		
		// ExBase new
		jlb.appendLine(astTree.getLine(), "new ",
						APrimExBase.instance.getJavaConstructorName(),
						"(new String[]{");
		
		// name key & hat key
		ACodeObject nameKey = params.get(0);
		if (nameKey == null || (!nameKey.getType().isJavaAction() && !nameKey.isInstanceOf(String.class))) {
			String msg = AADLMessage.illegalExBaseLiteralType(0);
			throw new CompileException((nameKey==null ? astTree.getLine() : nameKey.getMinLineNo()), msg);
		}
		jlb.add(null, nameKey.getJavaLineBuffer(), ","+APrimExBase.instance.getJavaCanonicalName()+".NO_HAT");
		
		// extended keys
		for (int i = 1; i < params.size(); i++) {
			ACodeObject extKey = params.get(i);
			if (extKey == null || (!extKey.getType().isJavaAction() && !extKey.isInstanceOf(String.class))) {
				String msg = AADLMessage.illegalExBaseLiteralType(i);
				throw new CompileException((extKey==null ? astTree.getLine() : extKey.getMinLineNo()), msg);
			}
			jlb.add(",", extKey.getJavaLineBuffer(), null);
		}
		
		// terminate
		jlb.catFooterAtLastLine("})");
		setType(APrimExBase.instance);
	}
	
	// データ代数基底リテラルのJAVAコード生成
	private void genDtBaseLiteral(AADLAnalyzer analyzer, CommonTree astTree, List<ACodeObject> params)
	{
		// Check
		if (params == null || params.isEmpty()) {
			throw new CompileException(astTree.getLine(), AADLMessage.notExistDtBaseLiteralKeys());
		}
		if (params.size() < 2) {
			throw new CompileException(astTree.getLine(), AADLMessage.notExistDtBaseTypeKey());
		}
		
		// DtBase new
		//jlb.appendLine(astTree.getLine(), "new ",
		//				APrimDtBase.instance.getJavaConstructorName(),
		//				"(new String[]{");
		jlb.appendLine(astTree.getLine(), APrimDtBase.instance.getJavaClassName(), ".newBase(new String[]{");
		
		// base keys
		ACodeObject baseKey = params.get(0);
		if (baseKey == null || (!baseKey.getType().isJavaAction() && !baseKey.isInstanceOf(String.class))) {
			String msg = AADLMessage.illegalDtBaseLiteralType(0);
			throw new CompileException((baseKey==null ? astTree.getLine() : baseKey.getMinLineNo()), msg);
		}
		jlb.add(null, baseKey.getJavaLineBuffer(), null);
		for (int i = 1; i < params.size(); i++) {
			baseKey = params.get(i);
			if (baseKey == null || (!baseKey.getType().isJavaAction() && !baseKey.isInstanceOf(String.class))) {
				String msg = AADLMessage.illegalDtBaseLiteralType(i);
				throw new CompileException((baseKey==null ? astTree.getLine() : baseKey.getMinLineNo()), msg);
			}
			jlb.add(",", baseKey.getJavaLineBuffer(), null);
		}
		
		// terminate
		jlb.catFooterAtLastLine("})");
		setType(APrimDtBase.instance);
	}

	// 配列リテラルのJAVAコード生成
	private void genArrayLiteral(AADLAnalyzer analyzer, CommonTree astTree, List<ACodeObject> values)
	{
		final String asListMethodName = "createArrayList";
		
		if (values == null || values.isEmpty()) {
			// 空の配列(型は JavaAction とする)
			this.jlb.appendLine(astTree.getLine(), asListMethodName, "()");
			setType(new AADLListType(AADLJavaAction.instance));
		}
		else {
			this.jlb.appendLine(astTree.getLine(), asListMethodName, "(");
			Iterator<ACodeObject> it = values.iterator();
			ACodeObject elemCode = null;
			// 先頭
			ACodeObject firstVal = it.next();
			if (firstVal != null) {
				if (!firstVal.getType().isJavaAction()) {
					elemCode = firstVal;
				}
				jlb.add(firstVal.getJavaLineBuffer());
			}
			// 以降
			while (it.hasNext()) {
				ACodeObject val = it.next();
				if (val != null) {
					AADLType valType = val.getType();
					if (!valType.isJavaAction()) {
						if (elemCode == null) {
							elemCode = val;
						}
						else if (!valType.equals(elemCode.getType())) {
							//--- 異なるデータタイプ
							String msg = AADLMessage.notEqualListElementType(valType.getNameKey());
							throw new CompileException(val.getMinLineNo(), msg);
						}
					}
					jlb.add(",", val.getJavaLineBuffer(), null);
				}
			}
			// 終端
			jlb.catFooterAtLastLine(")");
			// エレメントデータ型チェック
			//--- JavaAction, Boolean, Decimal, String, ExBase のみ
			AADLType dataType;
			if (elemCode == null) {
				dataType = AADLJavaAction.instance;
				setType(new AADLListType(dataType));
			}
			else {
				dataType = elemCode.getType();
				if (dataType.equals(APrimExBase.instance)) {
					// ExBase の配列は、ExBaseSetへ転換
					this.jlb.insertLine(0, "new ", APrimExBaseSet.instance.getJavaConstructorName(),
										"(", this.jlb.getMinLineNo());
					this.jlb.appendLine(this.jlb.getMaxLineNo(), ")");
					setType(APrimExBaseSet.instance);
				}
				else if (dataType.equals(APrimExalge.instance)) {
					// Exalge の配列は、ExAlgeSetへ転換
					this.jlb.insertLine(0, "new ", APrimExAlgeSet.instance.getJavaConstructorName(),
										"(", this.jlb.getMinLineNo());
					this.jlb.appendLine(this.jlb.getMaxLineNo(), ")");
					setType(APrimExAlgeSet.instance);
				}
				else if (dataType.equals(APrimExBasePattern.instance)) {
					// ExBasePattern の配列は、ExBasePatternSet へ転換
					this.jlb.insertLine(0, "new ", APrimExBasePatternSet.instance.getJavaConstructorName(),
										"(", this.jlb.getMinLineNo());
					this.jlb.appendLine(this.jlb.getMaxLineNo(), ")");
					setType(APrimExBasePatternSet.instance);
				}
				else if (dataType.equals(APrimDtBase.instance)) {
					// DtBase の配列は、DtBaseSetへ転換
					this.jlb.insertLine(0, "new ", APrimDtBaseSet.instance.getJavaConstructorName(),
										"(", this.jlb.getMinLineNo());
					this.jlb.appendLine(this.jlb.getMaxLineNo(), ")");
					setType(APrimDtBaseSet.instance);
				}
				else if (dataType.equals(APrimDtBasePattern.instance)) {
					// DtBasePattern の配列は、DtBasePatternSet へ転換
					this.jlb.insertLine(0, "new ", APrimDtBasePatternSet.instance.getJavaConstructorName(),
										"(", this.jlb.getMinLineNo());
					this.jlb.appendLine(this.jlb.getMaxLineNo(), ")");
					setType(APrimDtBasePatternSet.instance);
				}
				else if (dataType.equals(APrimDtalge.instance)) {
					// Dtalge の配列は、DtAlgeSetへ転換
					this.jlb.insertLine(0, "new ", APrimDtAlgeSet.instance.getJavaConstructorName(),
										"(", this.jlb.getMinLineNo());
					this.jlb.appendLine(this.jlb.getMaxLineNo(), ")");
					setType(APrimDtAlgeSet.instance);
				}
				else if (dataType.equals(APrimBoolean.instance)) {
					setType(APrimBooleanList.instance);
				}
				else if (dataType.equals(APrimDecimal.instance)) {
					setType(APrimDecimalList.instance);
				}
				else if (dataType.equals(APrimString.instance)) {
					setType(APrimStringList.instance);
				}
				else {
					// Not supported types
					String msg = AADLMessage.illegalListElementType(dataType.getName());
					throw new CompileException(elemCode.getMinLineNo(), msg);
				}
			}
		}
	}

	// HashリテラルのJAVAコード生成
	private void genMapLiteral(AADLAnalyzer analyzer, CommonTree astTree,
								List<ACodeObject> keys, List<ACodeObject> values)
	{
		String makerMethodName = "createLinkedHashMap";
		
		// Check
		if (keys == null || keys.isEmpty()) {
			throw new IllegalArgumentException("Keys not found!");
		}
		if (values == null || keys.size() != values.size()) {
			throw new IllegalArgumentException("keys.length not equal values.length");
		}
		
		// キー配列を生成
		ACodeLiteral keyCode = buildArrayLiteral(analyzer, astTree, keys);
		AADLType keyType = ((AADLCollectionType)keyCode.getType()).getElementType();
		
		// 値配列を生成
		ACodeLiteral valCode = buildArrayLiteral(analyzer, astTree, values);
		AADLType valType = ((AADLCollectionType)valCode.getType()).getElementType();
		
		// Map生成
		jlb.appendLine(makerMethodName + "(", astTree.getLine());
		jlb.add(keyCode.getJavaLineBuffer());
		jlb.add(",", valCode.getJavaLineBuffer(), ")");
		
		// プリミティブ設定
		AADLType mapType = new AADLMapType(keyType, valType);
		setType(mapType);
	}
	
	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
}
