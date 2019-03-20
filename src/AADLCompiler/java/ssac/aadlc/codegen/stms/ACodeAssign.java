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
 *  Copyright 2007-2011  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)ACodeAssign.java	1.70	2011/06/29
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ACodeAssign.java	1.50	2010/09/27
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ACodeAssign.java	1.30	2009/12/02
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ACodeAssign.java	1.20	2008/09/29
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ACodeAssign.java	1.10	2008/05/19
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ACodeAssign.java	1.00	2007/11/29
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
import ssac.aadlc.analysis.AADLFileType;
import ssac.aadlc.analysis.AADLSymbol;
import ssac.aadlc.analysis.type.AADLCollectionType;
import ssac.aadlc.analysis.type.AADLJavaClass;
import ssac.aadlc.analysis.type.AADLMapType;
import ssac.aadlc.analysis.type.AADLVoid;
import ssac.aadlc.analysis.type.prim.APrimDecimal;
import ssac.aadlc.analysis.type.prim.APrimString;
import ssac.aadlc.analysis.type.prim.APrimStringList;
import ssac.aadlc.codegen.ACodeObject;
import ssac.aadlc.codegen.JavaLineBuffer;
import ssac.aadlc.codegen.define.ACodeVariable;
import ssac.aadlc.codegen.expression.ACodeInvolving;

/**
 * AADL 代入文
 *
 * 
 * @version 1.70	2011/06/29
 */
public class ACodeAssign extends ACodeStatement
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------

	static protected final int	FILETYPE_TXT	= AADLFileType.TEXT;
	static protected final int	FILETYPE_CSV	= AADLFileType.CSV;
	static protected final int	FILETYPE_XML	= AADLFileType.XML;
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	protected ACodeAssign() {
		super();
	}
	
	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 変数への式の代入
	 */
	static public ACodeAssign assignExpression(AADLAnalyzer analyzer, ACodeVariable aVar,
												 CommonTree astTree, ACodeObject exp)
	{
		ACodeAssign retCode = new ACodeAssign();
		if (aVar != null && astTree != null && exp != null) {
			retCode.genAssignExpression(analyzer, aVar, astTree, exp);
		}
		return retCode;
	}

	/**
	 * 変数への内包式の代入
	 */
	static public ACodeAssign assignInvolv(AADLAnalyzer analyzer, ACodeVariable aVar,
											 CommonTree astTree, ACodeInvolving involv)
	{
		ACodeAssign retCode = new ACodeAssign();
		if (aVar != null && astTree != null && involv != null) {
			retCode.genAssignInvolv(analyzer, aVar, astTree, involv);
		}
		return retCode;
	}

	/**
	 * ファイル入出力
	 */
	static public ACodeAssign assignFile(AADLAnalyzer analyzer, ACodeVariable aVar, Token astDir,
											Token astFile, ACodeObject fileArg, ACodeObject encoding,
											List<AADLFileOptionEntry> foptions)
	{
		ACodeAssign retCode = new ACodeAssign();
		if (aVar != null && astFile != null && fileArg != null) {
			retCode.genAssignFile(analyzer, false, aVar, astDir, astFile, fileArg, encoding, foptions);
		}
		return retCode;
	}
	
	//------------------------------------------------------------
	// Code generators
	//------------------------------------------------------------
	
	protected void validReassignVariable(AADLAnalyzer analyzer, ACodeVariable aVar) {
		// 再代入チェック
		AADLSymbol vs = aVar.getSymbol();
		if (vs.isConst() && vs.isInitialized()) {
			// const 変数への再代入は禁止
			String msg = AADLMessage.cannotReassignConstVariable(vs.getAadlSymbolName());
			Token vt = aVar.getVariableNameToken();
			if (vt != null) {
				throw new CompileException(vt.getLine(), vt.getCharPositionInLine(), msg);
			} else {
				throw new CompileException(aVar.getMinLineNo(), msg);
			}
		}
		vs.setInitialized();
	}
	
	protected void addVariableCode(AADLAnalyzer analyzer, ACodeVariable aVar) {
		// 再代入チェック
		validReassignVariable(analyzer, aVar);
		
		// コード追加
		if (analyzer.isParsingDeclarationBlock() && aVar.hasReferenceCode()) {
			// ヘッダーブロック内での変数初期化は、変数宣言をクラスメンバーとし、
			// 変数初期化をコンストラクタ内で行う
			analyzer.addMemberDeclarationCode(aVar);
			this.jlb.add(aVar.getReferenceCode().getJavaLineBuffer());
		}
		else {
			this.jlb.add(aVar.getJavaLineBuffer());
		}
	}
	
	protected void genAssignExpression(AADLAnalyzer analyzer, ACodeVariable aVar,
										 CommonTree astTree, ACodeObject exp)
	{
		// 型チェック
		if (aVar.getType().equals(exp.getType()) || exp.getType().isJavaAction()) {
			// 同じ型、もしくは代入する値がJAVA_ACTIONなら、そのまま代入
			addVariableCode(analyzer, aVar);
			this.jlb.appendLine(" = ", astTree.getLine());
			this.jlb.add(null, exp.getJavaLineBuffer(), ";");
			return;
		}
		//--- コレクション系データ型の要素型チェック
		if (exp.getType() instanceof AADLMapType) {
			//--- Map
			if (aVar.getType() instanceof AADLMapType) {
				AADLMapType mapVar = (AADLMapType)aVar.getType();
				AADLMapType mapExp = (AADLMapType)exp.getType();
				if (!mapExp.getKeyType().isInstanceOf(mapVar.getKeyType()) ||
					!mapExp.getValueType().isInstanceOf(mapVar.getValueType()))
				{
					// Not assign
					String msg = AADLMessage.cannotConvert(exp.getType().getName(), aVar.getType().getName());
					throw new CompileException(astTree.getLine(), msg);
				}
			}
			else {
				// Not assign
				String msg = AADLMessage.cannotConvert(exp.getType().getName(), aVar.getType().getName());
				throw new CompileException(astTree.getLine(), msg);
			}
		}
		else if (exp.getType() instanceof AADLCollectionType) {
			//--- Collection
			if (aVar.getType() instanceof AADLCollectionType) {
				AADLCollectionType clVar = (AADLCollectionType)aVar.getType();
				AADLCollectionType clExp = (AADLCollectionType)exp.getType();
				if (!clExp.getElementType().isInstanceOf(clVar.getElementType())) {
					// Not assign
					String msg = AADLMessage.cannotConvert(exp.getType().getName(), aVar.getType().getName());
					throw new CompileException(astTree.getLine(), msg);
				}
			}
			else {
				// Not assign
				String msg = AADLMessage.cannotConvert(exp.getType().getName(), aVar.getType().getName());
				throw new CompileException(astTree.getLine(), msg);
			}
		}
		
		// データ型の代入
		if (exp.getType().isInstanceOf(aVar.getType())) {
			// 代入可能
			addVariableCode(analyzer, aVar);
			this.jlb.appendLine(" = ", astTree.getLine());
			this.jlb.add(null, exp.getJavaLineBuffer(), ";");
		}
		else if (exp.getType() instanceof AADLCollectionType && aVar.getType().hasConstructor(exp.getType())) {
			// 変数型が式の戻り値型からの派生なら、コンストラクタ経由で代入可能
			addVariableCode(analyzer, aVar);
			this.jlb.appendLine(" = new " ,aVar.getType().getJavaConstructorName(), "(", astTree.getLine());
			this.jlb.add(exp.getJavaLineBuffer());
			this.jlb.appendLine(");", astTree.getLine());
		}
		else if (aVar.getType().equals(APrimDecimal.instance) && exp.getType().isJavaPrimitive() && aVar.getType().hasConstructor(exp.getType())) {
			// Decimal型変数の代入において、式の戻り値型がJavaプリミティブの場合、式戻り値型のコンストラクタが存在すれば、代入可能とする
			addVariableCode(analyzer, aVar);
			this.jlb.appendLine(" = new ", aVar.getType().getJavaConstructorName(), "(", astTree.getLine());
			this.jlb.add(exp.getJavaLineBuffer());
			this.jlb.appendLine(");", astTree.getLine());
		}
		/*@@@ deleted by Y.Ishizuka : 2008.05.18
		else if (aVar.getType().hasConstructor(exp.getType())) {
			// コンストラクタ経由で代入可能
			addVariableCode(analyzer, aVar);
			this.jlb.appendLine(" = ", astTree.getLine());
			this.jlb.appendLine("new " ,aVar.getType().getJavaConstructorName(), "(", astTree.getLine());
			this.jlb.add(exp.getJavaLineBuffer());
			this.jlb.appendLine(");", astTree.getLine());
		}
		**@@@ end of deleted. */
		else {
			// Not assign
			String msg = AADLMessage.cannotConvert(exp.getType().getName(), aVar.getType().getName());
			throw new CompileException(astTree.getLine(), msg);
		}
	}
	
	protected void genAssignInvolv(AADLAnalyzer analyzer, ACodeVariable aVar,
									 CommonTree astTree, ACodeInvolving involv)
	{
		// 内包式の変数オブジェクト生成
		AADLSymbol involvSymbol = involv.getSymbol();
		if (involvSymbol == null) {
			// 値を返さない内包記法(Not assign)
			String msg = AADLMessage.cannotConvert(AADLVoid.instance.getName(), aVar.getType().getName());
			throw new CompileException(astTree.getLine(), msg);
		}
		ACodeVariable varInvolv = ACodeVariable.buildVariableRef(analyzer, astTree.getLine(), involvSymbol);
		
		// 内包式の展開
		this.jlb.add(involv.getJavaLineBuffer());
		
		// 代入式
		this.genAssignExpression(analyzer, aVar, astTree, varInvolv);

		/*
		// 型チェック
		AADLType typeVar = aVar.getType();
		if (!(typeVar instanceof AADLCollectionType)) {
			// Not assign
			String msg = AADLMessage.cannotConvert(involv.getType().getName(), typeVar.getName());
			throw new CompileException(astTree.getLine(), msg);
		}
		AADLCollectionType clVar = (AADLCollectionType)aVar.getType();
		AADLCollectionType clExp = (AADLCollectionType)involv.getType();
		if (!clExp.getElementType().isInstanceOf(clVar.getElementType())) {
			// Not assign
			String msg = AADLMessage.cannotConvert(involv.getType().getName(), typeVar.getName());
			throw new CompileException(astTree.getLine(), msg);
		}
		
		// java 型チェック
		if (clExp.isInstanceOf(typeVar)) {
			// 代入可能
			AADLSymbol involvSymbol = involv.getSymbol();
			this.jlb.add(involv.getJavaLineBuffer());
			this.jlb.add(aVar.getJavaLineBuffer());
			this.jlb.appendLine(" = ", astTree.getLine());
			this.jlb.appendLine(null, involvSymbol.getJavaSymbolName(), ";", astTree.getLine());
		}
		else if (typeVar.hasConstructor(clExp)) {
			// コンストラクタで代入
			AADLSymbol involvSymbol = involv.getSymbol();
			this.jlb.add(involv.getJavaLineBuffer());
			this.jlb.add(aVar.getJavaLineBuffer());
			this.jlb.appendLine(" = ", astTree.getLine());
			this.jlb.appendLine("new " + typeVar.getJavaClassName() + "(",
					involvSymbol.getJavaSymbolName(), ");", astTree.getLine());
		}
		else {
			// Not assign
			String msg = AADLMessage.cannotConvert(involv.getType().getName(), typeVar.getName());
			throw new CompileException(astTree.getLine(), msg);
		}
		*/
	}

	protected void genAssignFile(AADLAnalyzer analyzer, boolean isDefinition, ACodeVariable aVar,
								   Token astDir, Token astFile, ACodeObject fileArg, ACodeObject encoding,
								   List<AADLFileOptionEntry> foptions)
	{
		// オプションは、現時点ではサポートしない
		if (foptions!=null && !foptions.isEmpty()) {
			String msg = AADLMessage.unsupportedFileOptions(astFile.getText());
			throw new CompileException(astFile.getLine(), astFile.getCharPositionInLine(), msg);
		}
		
		// ファイル種別
		int filetype = AADLFileType.getFileTypeByToken(astFile);
		boolean isInput = isInput(astDir);
		AADLJavaClass typeFile = new AADLJavaClass(java.io.File.class);
		
		// 引数
		if (fileArg == null) {
			String msg = AADLMessage.illegalParameter(astFile.getText() + "()", 1, "null", APrimString.instance.getName());
			throw new CompileException(astFile.getLine(), msg);
		}
		if (!APrimString.instance.equals(fileArg.getType())) {
			// Not assign
			String msg = AADLMessage.illegalParameter(astFile.getText() + "()", 1,
													fileArg.getType().getName(), APrimString.instance.getName());
			throw new CompileException(fileArg.getMinLineNo(), msg);
		}

		// 入出力コード生成
		if (isInput) {
			//===============================
			// for Input
			//===============================
			JavaLineBuffer encjlb = null;
			String encodingGetterName;
			String className;
			String methodName;
			if (filetype == FILETYPE_TXT) {
				// txtFile
				className = "exalge2.io.FileUtil";
				methodName = "fromTextFile";
				if (encoding != null) {
					if (!encoding.getType().isJavaAction() && !encoding.getType().equals(APrimString.instance)) {
						String msg = AADLMessage.undefinedFunction(methodName,
								AADLMessage.toStringParams(fileArg.getType(), encoding.getType()));
						throw new CompileException(astDir.getLine(), msg);
					}
					encodingGetterName = null;
					encjlb = encoding.getJavaLineBuffer();
				} else {
					//encodingGetterName = "_getTextEncoding";
					encodingGetterName = "getDefaultTextEncoding";
				}
				if (aVar.getType().equals(APrimString.instance)) {
					methodName = "stringFromTextFile";
				}
				else if (aVar.getType().equals(APrimStringList.instance)) {
					methodName = "stringListFromTextFile";
				}
				else {
					String msg;
					if (encoding != null) {
						msg = AADLMessage.undefinedFunction(methodName,
								AADLMessage.toStringParams(fileArg.getType(), encoding.getType()));
					} else {
						msg = AADLMessage.undefinedFunction(methodName,
								AADLMessage.toStringParams(fileArg.getType()));
					}
					throw new CompileException(astDir.getLine(), msg);
				}
			} else {
				// csvFile or xmlFile
				className = aVar.getType().getJavaCanonicalName();
				methodName = (filetype == FILETYPE_CSV) ? "fromCSV" : "fromXML";
				//--- check exist method
				if (encoding != null) {
					if (!aVar.getType().hasMethod(methodName, typeFile, encoding.getType())) {
						String msg = AADLMessage.undefinedFunction(methodName,
								AADLMessage.toStringParams(fileArg.getType(), encoding.getType()));
						throw new CompileException(astDir.getLine(), msg);
					}
					encodingGetterName = null;
					encjlb = encoding.getJavaLineBuffer();
				}
				else {
					if (!aVar.getType().hasMethod(methodName, typeFile)) {
						String msg = AADLMessage.undefinedFunction(methodName,
								AADLMessage.toStringParams(fileArg.getType()));
						throw new CompileException(astDir.getLine(), msg);
					}
					if (filetype == FILETYPE_CSV) {
						if (aVar.getType().hasMethod(methodName, typeFile, APrimString.instance)) {
							// デフォルトエンコーディング取得メソッドを使用する
							//encodingGetterName = "_getCsvEncoding";
							encodingGetterName = "getDefaultCsvEncoding";
						} else {
							encodingGetterName = null;
						}
					} else {
						encodingGetterName = null;
					}
				}
			}
			// generate codes
			if (isDefinition) {
				validReassignVariable(analyzer, aVar);
				if (analyzer.isParsingDeclarationBlock() && aVar.hasReferenceCode()) {
					// ヘッダーブロック内での変数初期化は、変数宣言をクラスメンバーとし、
					// 変数初期化をコンストラクタ内で行う
					analyzer.addMemberDeclarationCode(aVar);
				} else {
					this.jlb.add(null, aVar.getJavaLineBuffer(), ";");
				}
			}
			this.jlb.appendLine(-1, "try {");
			genFileInputStatement(encodingGetterName, isDefinition, astDir, astFile, className, methodName, aVar, fileArg, encjlb);
			this.jlb.appendLine(-1, "} catch (Exception ex) {");
			//this.jlb.appendLine(-1, "throw new RuntimeException(ex);");
			this.jlb.appendLine(-1, "throw new ssac.aadl.runtime.AADLRuntimeException(ex);");
			this.jlb.appendLine(-1, "}");
		}
		else {
			//===============================
			// for Output
			//===============================
			JavaLineBuffer encjlb = null;
			String encodingGetterName;
			String methodName;
			if (filetype == FILETYPE_TXT) {
				// txtFile
				methodName = "toTextFile";
				if (encoding != null) {
					if (!encoding.getType().isJavaAction() && !encoding.getType().equals(APrimString.instance)) {
						String msg = AADLMessage.undefinedFunction(methodName,
								AADLMessage.toStringParams(fileArg.getType(), encoding.getType()));
						throw new CompileException(astDir.getLine(), msg);
					}
					encodingGetterName = null;
					encjlb = encoding.getJavaLineBuffer();
				} else {
					//encodingGetterName = "_getTextEncoding";
					encodingGetterName = "getDefaultTextEncoding";
				}
				if (aVar.getType().equals(APrimString.instance)) {
					methodName = "stringToTextFile";
				}
				else if (aVar.getType().equals(APrimStringList.instance)) {
					methodName = "stringListToTextFile";
				}
				else {
					String msg;
					if (encoding != null) {
						msg = AADLMessage.undefinedFunction(methodName,
								AADLMessage.toStringParams(fileArg.getType(), encoding.getType()));
					} else {
						msg = AADLMessage.undefinedFunction(methodName,
								AADLMessage.toStringParams(fileArg.getType()));
					}
					throw new CompileException(astDir.getLine(), msg);
				}
			} else {
				// csvFile or xmlFile
				methodName = (filetype == FILETYPE_CSV) ? "toCSV" : "toXML";
				//--- check exist method
				if (encoding != null) {
					if (!aVar.getType().hasMethod(methodName, typeFile, encoding.getType())) {
						String msg = AADLMessage.undefinedFunction(methodName,
								AADLMessage.toStringParams(fileArg.getType(), encoding.getType()));
						throw new CompileException(astDir.getLine(), msg);
					}
					encodingGetterName = null;
					encjlb = encoding.getJavaLineBuffer();
				}
				else {
					if (!aVar.getType().hasMethod(methodName, typeFile)) {
						String msg = AADLMessage.undefinedFunction(methodName,
								AADLMessage.toStringParams(fileArg.getType()));
						throw new CompileException(astDir.getLine(), msg);
					}
					if (filetype == FILETYPE_CSV) {
						if (aVar.getType().hasMethod(methodName, typeFile, APrimString.instance)) {
							// デフォルトエンコーディング取得メソッドを使用する
							//encodingGetterName = "_getCsvEncoding";
							encodingGetterName = "getDefaultCsvEncoding";
						} else {
							encodingGetterName = null;
						}
					} else {
						encodingGetterName = null;
					}
				}
			}
			// generate codes
			this.jlb.appendLine(-1, "try {");
			genFileOutputStatement(encodingGetterName, filetype, astDir, astFile, methodName, aVar, fileArg, encjlb);
			this.jlb.appendLine(-1, "} catch (Exception ex) {");
			//this.jlb.appendLine(-1, "throw new RuntimeException(ex);");
			this.jlb.appendLine(-1, "throw new ssac.aadl.runtime.AADLRuntimeException(ex);");
			this.jlb.appendLine(-1, "}");
		}
	}
	
	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected boolean isInput(Token astDir) {
		boolean isInput;
		String strDir = astDir.getText();
		if (strDir.equals("<<-")) {
			isInput = true;
		}
		else if (strDir.equals("->>")) {
			isInput = false;
		}
		else {
			// Unknown
			String msg = AADLMessage.undefinedToken(strDir);
			throw new CompileException(astDir.getLine(), astDir.getCharPositionInLine(), msg);
		}
		return isInput;
	}

	// ファイル入力用JAVAコード生成
	protected void genFileInputStatementImpl(boolean isDefinition, Token astDir, Token astFile,
												String className, String methodName, ACodeVariable aVar,
												ACodeObject fileArg, JavaLineBuffer encoding)
	{
		if (isDefinition) {
			this.jlb.appendLine(astDir.getLine(), aVar.getSymbol().getJavaSymbolName(), " = ",
					className, ".", methodName, "(");
		}
		else {
			this.jlb.add(aVar.getJavaLineBuffer());
			this.jlb.appendLine(astDir.getLine(), " = ", className, ".", methodName, "(");
		}
		this.jlb.add("new java.io.File(", fileArg.getJavaLineBuffer(), ")");
		if (encoding != null) {
			this.jlb.add(",", encoding, null);
		}
		this.jlb.appendLine(");", astFile.getLine());
	}

	// デフォルトエンコーディングを含めた、ファイル入力用JAVAコード生成
	protected void genFileInputStatement(String encodingGetterMethodName, boolean isDefinition,
											Token astDir, Token astFile, String className, String methodName,
											ACodeVariable aVar, ACodeObject fileArg, JavaLineBuffer encoding)
	{
		if (encodingGetterMethodName != null) {
			// 指定されたメソッドからエンコード名を取得する。
			this.jlb.appendLine(-1, "String _defEncoding = ", encodingGetterMethodName, "();");
			this.jlb.appendLine(-1, "if (_defEncoding==null) {");
			genFileInputStatementImpl(isDefinition, astDir, astFile, className, methodName, aVar, fileArg, null);
			this.jlb.appendLine(-1, "} else {");
			JavaLineBuffer encjlb = new JavaLineBuffer();
			encjlb.appendLine(astFile.getLine(), "_defEncoding");
			genFileInputStatementImpl(isDefinition, astDir, astFile, className, methodName, aVar, fileArg, encjlb);
			this.jlb.appendLine(-1, "}");
		} else {
			// 指定されたエンコード名を使用する。
			genFileInputStatementImpl(isDefinition, astDir, astFile, className, methodName, aVar, fileArg, encoding);
		}
	}

	// ファイル出力用JAVAコード生成
	protected void genFileOutputStatementImpl(int fileType, Token astDir, Token astFile,
												String methodName, ACodeVariable aVar,
												ACodeObject fileArg, JavaLineBuffer encoding)
	{
		if (fileType==FILETYPE_TXT) {
			this.jlb.appendLine(astDir.getLine(), "exalge2.io.FileUtil.", methodName, "(");
			this.jlb.add(aVar.getJavaLineBuffer());
			this.jlb.add(", new java.io.File(", fileArg.getJavaLineBuffer(), ")");
		} else {
			this.jlb.add(aVar.getJavaLineBuffer());
			this.jlb.appendLine(astFile.getLine(), ".", methodName, "(");
			this.jlb.add("new java.io.File(", fileArg.getJavaLineBuffer(), ")");
		}
		if (encoding != null) {
			this.jlb.add(",", encoding, null);
		}
		this.jlb.appendLine(");", astFile.getLine());
	}

	// デフォルトエンコーディングを含めた、ファイル出力用JAVAコード生成
	protected void genFileOutputStatement(String encodingGetterMethodName, int fileType,
											Token astDir, Token astFile,
											String methodName, ACodeVariable aVar,
											ACodeObject fileArg, JavaLineBuffer encoding)
	{
		if (encodingGetterMethodName != null) {
			// 指定されたメソッドからエンコード名を取得する。
			this.jlb.appendLine(-1, "String _defEncoding = ", encodingGetterMethodName, "();");
			this.jlb.appendLine(-1, "if (_defEncoding==null) {");
			genFileOutputStatementImpl(fileType, astDir, astFile, methodName, aVar, fileArg, null);
			this.jlb.appendLine(-1, "} else {");
			JavaLineBuffer encjlb = new JavaLineBuffer();
			encjlb.appendLine(astFile.getLine(), "_defEncoding");
			genFileOutputStatementImpl(fileType, astDir, astFile, methodName, aVar, fileArg, encjlb);
			this.jlb.appendLine(-1, "}");
		} else {
			// 指定されたエンコード名を使用する。
			genFileOutputStatementImpl(fileType, astDir, astFile, methodName, aVar, fileArg, encoding);
		}
	}
}
