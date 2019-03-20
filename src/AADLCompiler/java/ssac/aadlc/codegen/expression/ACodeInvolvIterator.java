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
 * @(#)ACodeInvolvIterator.java	1.50	2010/09/27
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ACodeInvolvIterator.java	1.30	2009/12/02
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ACodeInvolvIterator.java	1.00	2007/11/29
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadlc.codegen.expression;

import java.util.List;

import org.antlr.runtime.Token;

import ssac.aadlc.AADLMessage;
import ssac.aadlc.CompileException;
import ssac.aadlc.analysis.AADLAnalyzer;
import ssac.aadlc.analysis.AADLFileOptionEntry;
import ssac.aadlc.analysis.AADLFileType;
import ssac.aadlc.analysis.AADLIteratorIdentifier;
import ssac.aadlc.analysis.type.AADLIterableType;
import ssac.aadlc.analysis.type.AADLJavaAction;
import ssac.aadlc.analysis.type.AADLType;
import ssac.aadlc.analysis.type.prim.APrimCsvFileReader;
import ssac.aadlc.analysis.type.prim.APrimString;
import ssac.aadlc.analysis.type.prim.APrimTextFileReader;
import ssac.aadlc.codegen.ACodeObject;
import ssac.aadlc.codegen.define.ACodeVariable;

/**
 * 内包記法のイテレータ
 *
 * 
 * @version 1.50	2010/09/27
 */
public class ACodeInvolvIterator extends ACodeInvolving
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private ACodeInvolving innerInvolv = null;
	private ACodeObject	innerReaderDefinition = null;
	private ACodeObject	innerReaderTermination = null;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	protected ACodeInvolvIterator() {
		super();
	}
	
	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 内包記法イテレータのJAVAコード生成
	 */
	static public ACodeInvolvIterator buildIterator(AADLAnalyzer analyzer, Token astName, ACodeObject listCode) {
		ACodeInvolvIterator retCode = new ACodeInvolvIterator();
		retCode.genIterator(analyzer, astName, listCode);
		return retCode;
	}

	/**
	 * 内包記法リーダーイテレータのJAVAコード生成
	 * @since 1.40
	 */
	static public ACodeInvolvIterator buildReaderIterator(AADLAnalyzer analyzer,
															Token astName, Token astFile,
															ACodeObject fileArg, ACodeObject encoding,
															List<AADLFileOptionEntry> foptions)
	{
		ACodeInvolvIterator retCode = new ACodeInvolvIterator();
		if (astName != null && astFile != null && fileArg != null) {
			retCode.genReaderIterator(analyzer, astName, astFile, fileArg, encoding, foptions);
		}
		return retCode;
	}

	/**
	 * イテレータへの内包記法代入の有無
	 * 
	 * @return 内包記法代入ありの場合は true を返す。
	 */
	public boolean hasInnerInvolv() {
		return (this.innerInvolv != null);
	}

	/**
	 * イテレータへの代入内包記法コードの取得
	 * 
	 * @return
	 */
	public ACodeInvolving getInnerInvolv() {
		return this.innerInvolv;
	}

	/**
	 * リーダーの定義コードの有無
	 * @return	リーダーイテレータが定義されている場合は true を返す。
	 * @since 1.40
	 */
	public boolean hasInnerReaderDefinition() {
		return (this.innerReaderDefinition != null);
	}

	/**
	 * リーダーの定義コードの取得
	 * @return	リーダーの定義コードオブジェクトを返す。
	 * 			定義されていない場合は null を返す。
	 * @since 1.40
	 */
	public ACodeObject getInnerReaderDefinition() {
		return this.innerReaderDefinition;
	}

	/**
	 * リーダー終端コードの取得
	 * @return	リーダー終端のコードオブジェクトを返す。
	 * 			定義されていない場合は null を返す。
	 * @since 1.40
	 */
	public ACodeObject getInnerReaderTermination() {
		return this.innerReaderTermination;
	}
	
	//------------------------------------------------------------
	// Code generators
	//------------------------------------------------------------
	
	private void genIterator(AADLAnalyzer analyzer, Token astName, ACodeObject listCode) {
		// List check
		ACodeObject objList;
		if (listCode instanceof ACodeInvolving) {
			// 内包記法の多重化
			this.innerInvolv = (ACodeInvolving)listCode;
			objList = ACodeVariable.buildVariableRef(analyzer, astName.getLine(),
														this.innerInvolv.getSymbol());
		}
		else if (listCode == null || listCode.getType().isJavaAction()) {
			// イテレート元のオブジェクト型が特定できない
			String msg = AADLMessage.unknownDataType();
			throw new CompileException(astName.getLine(), astName.getCharPositionInLine(), msg);
		}
		else if (!(listCode.getType() instanceof AADLIterableType)) {
			// Not iterable
			String msg = AADLMessage.notIterable(astName.getText());
			throw new CompileException(astName.getLine(), astName.getCharPositionInLine(), msg);
		}
		else {
			objList = listCode;
		}
		AADLIterableType listType = (AADLIterableType)objList.getType();
		AADLType elemType = listType.getElementType();
		
		// Variable Definition
		ACodeVariable codeVar = ACodeVariable.buildVariableDefinition(analyzer, false, astName, elemType);
		
		// create Codes
		jlb.add(null, codeVar.getJavaLineBuffer(), " : ");
		jlb.add(objList.getJavaLineBuffer());
		
		// setup type
		setType(listType);
	}
	
	private void genReaderIterator(AADLAnalyzer analyzer, Token astName, Token astFile,
									ACodeObject fileArg, ACodeObject encoding, List<AADLFileOptionEntry> foptions)
	{
		// オプションは、現時点ではサポートしない
		if (foptions!=null && !foptions.isEmpty()) {
			String msg = AADLMessage.unsupportedFileOptions(astFile.getText());
			throw new CompileException(astFile.getLine(), astFile.getCharPositionInLine(), msg);
		}
		
		// ファイル種別
		int filetype = AADLFileType.getFileTypeByToken(astFile);
		if (filetype == AADLFileType.XML) {
			// XML file type not supported
			String msg = AADLMessage.notIterable(astFile.getText());
			throw new CompileException(astFile.getLine(), astFile.getCharPositionInLine(), msg);
		}
		
		// 引数
		if (fileArg == null) {
			String msg = AADLMessage.illegalParameter(astFile.getText() + "()", 1, "null", APrimString.instance.getName());
			throw new CompileException(astFile.getLine(), msg);
		}
		//--- check file argument
		if (!APrimString.instance.equals(fileArg.getType())) {
			// Not assign
			String msg = AADLMessage.illegalParameter(astFile.getText() + "()", 1,
													fileArg.getType().getName(), APrimString.instance.getName());
			throw new CompileException(fileArg.getMinLineNo(), msg);
		}
		//--- check encoding argument
		if (encoding != null && !encoding.getType().isJavaAction() && !encoding.getType().equals(APrimString.instance)) {
			String msg = AADLMessage.illegalParameter(astFile.getText() + "()", 2,
													encoding.getType().getName(), APrimString.instance.getName());
			throw new CompileException(encoding.getMinLineNo(), msg);
		}
		
		// リーダーオブジェクト生成コード
		AADLIterableType itType;
		//--- 変数名を生成
		AADLIteratorIdentifier vid = new AADLIteratorIdentifier();
		//--- 変数宣言を生成
		ACodeObject def = new ACodeObject(AADLJavaAction.instance);
		if (filetype == AADLFileType.CSV) {
			// csvFile
			itType = APrimCsvFileReader.instance;
			def.getJavaLineBuffer().appendLine(astFile.getLine(),
					APrimCsvFileReader.instance.getJavaClassName(),
					" ", vid.name(), " = null;");
			def.getJavaLineBuffer().appendLine(astFile.getLine(),
					"try{ ", vid.name(), " = newCsvFileReader");
		} else {
			// txtFile
			itType = APrimTextFileReader.instance;
			def.getJavaLineBuffer().appendLine(astFile.getLine(),
					APrimTextFileReader.instance.getJavaClassName(),
					" ", vid.name(), " = null;");
			def.getJavaLineBuffer().appendLine(astFile.getLine(),
					"try{ ", vid.name(), " = newTextFileReader");
		}
		if (encoding != null) {
			// with encoding
			def.getJavaLineBuffer().add("(", fileArg.getJavaLineBuffer(), ",");
			def.getJavaLineBuffer().add(null, encoding.getJavaLineBuffer(), ");");
		} else {
			// no encoding
			def.getJavaLineBuffer().add("(", fileArg.getJavaLineBuffer(), ");");
		}
		
		// finally ブロックコード
		ACodeObject term = new ACodeObject(AADLJavaAction.instance);
		term.getJavaLineBuffer().appendLine(astFile.getLine(),
				"} finally { if(null!=", vid.name(),
				") closeReader(", vid.name(), "); }");
		
		// イテレート・コード
		AADLType elemType = itType.getElementType();
		ACodeVariable codeVar = ACodeVariable.buildVariableDefinition(analyzer, false, astName, elemType);
		jlb.add(null, codeVar.getJavaLineBuffer(), " : " + vid.name());
		setType(itType);
		
		this.innerReaderDefinition  = def;
		this.innerReaderTermination = term;
	}
	
	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

}
