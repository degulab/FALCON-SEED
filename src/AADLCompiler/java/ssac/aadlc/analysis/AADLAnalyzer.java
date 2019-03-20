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
 * @(#)AADLAnalyzer.java	1.30	2009/12/02
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AADLAnalyzer.java	1.20	2008/09/25
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AADLAnalyzer.java	1.00	2007/11/29
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadlc.analysis;

import java.util.ArrayList;
import java.util.List;

import org.antlr.runtime.Token;

import ssac.aadlc.AADLMessage;
import ssac.aadlc.CompileException;
import ssac.aadlc.analysis.type.AADLType;
import ssac.aadlc.analysis.type.AADLTypeManager;
import ssac.aadlc.analysis.type.func.AADLFuncManager;
import ssac.aadlc.codegen.AADLCode;
import ssac.aadlc.codegen.AADLJavaProgram;
import ssac.aadlc.codegen.ACodePackage;
import ssac.aadlc.codegen.define.ACodeFixedVariables;
import ssac.aadlc.core.AADLParser;

/**
 * AADL 解析情報
 * 
 * 
 * @version 1.30	2009/12/02
 */
public class AADLAnalyzer
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	static public final String ANONYMOUS_LABEL = "@ANONYMOUS@";
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private String aadlClassName = "";			// AADL クラス名
	private boolean parsingDeclarationBlock = false;	// 宣言エリア内であれば true
	private boolean parsingProgramBlock = false;	// Program Block 内であれば true
	

	// Symbols
	private final AADLCmdArgs		cmdArgs = new AADLCmdArgs();
	private final AADLSymbolMap	globalSymbols = new AADLSymbolMap();
	private final AADLSymbolStack	localSymbols = new AADLSymbolStack();
	
	// Involving stack
	private final AADLInvolvStack involvs = new AADLInvolvStack();
	
	// Type
	private final AADLTypeManager	aadlTypes = new AADLTypeManager();
	private final AADLFuncManager aadlFuncs = new AADLFuncManager();
	
	// Codes
	private final AADLJavaProgram javaCode = new AADLJavaProgram();
	private final ArrayList<AADLCode> memberDeclCodes = new ArrayList<AADLCode>();
	private final ArrayList<AADLCode> constructionCodes = new ArrayList<AADLCode>();
	private final ACodeFixedVariables fixedVarCodes;

	//------------------------------------------------------------
	// Static initialization
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public AADLAnalyzer() {
		this.fixedVarCodes = ACodeFixedVariables.buildFixedVariables(globalSymbols);
	}
	
	//------------------------------------------------------------
	// Cheker
	//------------------------------------------------------------
	
	static public String[] getTokenNames() {
		return AADLParser.tokenNames;
	}
	
	static public boolean isValidTokenType(Token astToken, int tokenID) {
		if (astToken != null && astToken.getType() == tokenID)
			return true;
		else
			return false;
	}
	
	static public String getTokenName(Token astToken) {
		if (astToken != null)
			return getTokenName(astToken.getTokenIndex());
		else
			return "";
	}
	
	static public String getTokenName(int tokenIndex) {
		if (0 <= tokenIndex && tokenIndex < AADLParser.tokenNames.length)
			return AADLParser.tokenNames[tokenIndex];
		else
			return "";
	}
	
	static public String getErrorReportFuncName() {
		return "aadlErrorReport";
	}
	
	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public void clear() {
		this.aadlClassName = "";
		clearSymbols();
		//this.aadlTypes.clear();
		this.aadlFuncs.clear();
		this.javaCode.clear();
	}
	
	public void clearSymbols() {
		this.cmdArgs.clear();
		this.globalSymbols.clear();
		this.localSymbols.clear();
	}

	public String getAadlClassName() {
		return this.aadlClassName;
	}
	
	public void setAadlClassName(String name) {
		this.aadlClassName = name;
	}
	
	public boolean hasAadlPackagePath() {
		return (this.javaCode.getPackageCode() != null);
	}
	
	public String getAadlPackagePath() {
		ACodePackage cPackage = javaCode.getPackageCode();
		return (cPackage==null ? null : cPackage.getPackagePath());
	}
	
	public AADLCmdArgs getCmdArgs() {
		return this.cmdArgs;
	}
	
	public AADLSymbolMap getGlobalSymbols() {
		return this.globalSymbols;
	}
	
	public AADLSymbolStack getLocalSymbols() {
		return this.localSymbols;
	}
	
	public AADLInvolvStack getInvolvStack() {
		return this.involvs;
	}
	
	public AADLTypeManager getTypeManager() {
		return this.aadlTypes;
	}
	
	public AADLFuncManager getFuncManager() {
		return this.aadlFuncs;
	}
	
	public AADLJavaProgram getJavaProgram() {
		return this.javaCode;
	}
	
	public List<AADLCode> getMemberDeclarationCodes() {
		return this.memberDeclCodes;
	}
	
	public List<AADLCode> getConstructionCodes() {
		return this.constructionCodes;
	}
	
	public ACodeFixedVariables getFixedVariableCodes() {
		return this.fixedVarCodes;
	}
	
	public void addMemberDeclarationCode(AADLCode code) {
		this.memberDeclCodes.add(code);
	}
	
	public void addConstructionCode(AADLCode code) {
		this.constructionCodes.add(code);
	}
	
	//------------------------------------------------------------
	// Header block informations
	//------------------------------------------------------------

	/**
	 * 宣言ブロック内で、かつ内包記述ブロック外の場合に <tt>true</tt> を返す。
	 * 宣言ブロックは特殊な宣言文を記述するため、コード生成ではこのステータスにより
	 * コード生成方法が異なる。ただ、内包記法内部では通常のコード生成になる為、
	 * このようなステータスを返す。
	 */
	public boolean isParsingDeclarationBlock() {
		return (parsingDeclarationBlock && !isParsingInvolvingBlock());
	}
	
	public void beginDeclarationBlockParsing() {
		parsingDeclarationBlock = true;
	}
	
	public void endDeclarationBlockParsing() {
		parsingDeclarationBlock = false;
	}
	
	//------------------------------------------------------------
	// Program block informations
	//------------------------------------------------------------
	
	public boolean isParsingProgramBlock() {
		return parsingProgramBlock;
	}
	
	public void beginProgramBlockParsing() {
		parsingProgramBlock = true;
	}
	
	public void endProgramBlockParsing() {
		parsingProgramBlock = false;
	}
	
	//------------------------------------------------------------
	// Involving block informations
	//------------------------------------------------------------
	
	public boolean isParsingInvolvingBlock() {
		return (!involvs.isEmpty());
	}
	
	public int getParsingInvolvingBlockCount() {
		return involvs.getLevelCount();
	}
	
	public AADLInvolvIdentifier beginInvolvingBlockParsing() {
		AADLInvolvIdentifier id = involvs.push();
		return id;
	}
	
	public AADLInvolvIdentifier endInvolvingBlockParsing() {
		AADLInvolvIdentifier id = involvs.pop();
		if (id == null)
			throw new AssertionError("involving stack is negative!");
		return id;
	}
	
	//------------------------------------------------------------
	// Symbols interfaces
	//------------------------------------------------------------

	public boolean isSymbolEmpty() {
		if (this.cmdArgs.isEmpty() && this.globalSymbols.isEmpty() && this.localSymbols.isEmpty())
			return true;
		else
			return false;
	}
	
	public int getLevelCount() {
		return this.localSymbols.getLevelCount();
	}
	
	public void pushScope() {
		this.localSymbols.pushScope();
	}
	
	public void popScope() {
		this.localSymbols.popScope();
	}
	
	public AADLSymbolMap peek() {
		if (this.localSymbols.getLevelCount() > 0)
			return this.localSymbols.peek();
		else
			return this.globalSymbols;
	}

	// AADL シンボル名の有無
	public boolean hasSymbol(String name) {
		return (getSymbolValue(name) != null);
	}

	// JAVA シンボル名の有無
	public boolean hasJavaSymbol(String name) {
		return (getJavaSymbolValue(name) != null);
	}

	// AADL シンボル名に対応するシンボル取得
	public AADLSymbol getSymbolValue(String name) {
		AADLSymbol curSymbol;
		// locals
		curSymbol = this.localSymbols.getSymbolValue(name);
		if (curSymbol != null) {
			return curSymbol;
		}
		// globals
		curSymbol = this.globalSymbols.getSymbolValue(name);
		if (curSymbol != null) {
			return curSymbol;
		}
		// cmdArgs
		if (isParsingProgramBlock()) {
			//--- program ブロック内に限る
			curSymbol = this.cmdArgs.getSymbolValue(name);
			if (curSymbol != null) {
				return curSymbol;
			}
		}
		// No match
		return null;
	}
	
	// JAVA シンボル名に対応するシンボル取得
	public AADLSymbol getJavaSymbolValue(String name) {
		AADLSymbol curSymbol;
		// locals
		curSymbol = this.localSymbols.getJavaSymbolValue(name);
		if (curSymbol != null) {
			return curSymbol;
		}
		// globals
		curSymbol = this.globalSymbols.getJavaSymbolValue(name);
		if (curSymbol != null) {
			return curSymbol;
		}
		// cmdArgs
		if (isParsingProgramBlock()) {
			//--- program ブロック内に限る
			curSymbol = this.cmdArgs.getJavaSymbolValue(name);
			if (curSymbol != null) {
				return curSymbol;
			}
		}
		// No match
		return null;
	}

	// シンボル情報設定
	public void setSymbol(AADLSymbol symbol) {
		// Check
		if (this.cmdArgs.hasSymbol(symbol.getAadlSymbolName())) {
			throw new IllegalArgumentException("Already exist AADL Symbol in CmdArgs!");
		}
		if (symbol.hasJavaSymbolName() && this.cmdArgs.hasJavaSymbol(symbol.getJavaSymbolName())) {
			throw new IllegalArgumentException("Already exist Java Symbol");
		}
		
		// set
		peek().setSymbol(symbol);
	}
	
	public void setSymbol(boolean isConst, String aadlName, AADLType dataType) {
		setSymbol(new AADLSymbol(isConst, aadlName, dataType));
	}
	
	public void setSymbol(boolean isConst, String aadlName, String javaName, AADLType dataType) {
		setSymbol(new AADLSymbol(isConst, aadlName, javaName, dataType));
	}
	
	// コマンドラインパラメータ設定
	public void setCommandArgSymbol(Token astCmdArg) {
		try {
			this.cmdArgs.setSymbol(astCmdArg.getText());
		}
		catch (Exception ex) {
			String msg = AADLMessage.undefinedToken(astCmdArg.getText());
			throw new CompileException(astCmdArg.getLine(), astCmdArg.getCharPositionInLine(), msg, ex);
		}
	}
	
	//------------------------------------------------------------
	// Involvings interfaces
	//------------------------------------------------------------
	
	public boolean isInvolvingEmpty() {
		return involvs.isEmpty();
	}
	
	public int getInvolvingLevelCount() {
		return involvs.getLevelCount();
	}
	
	public boolean containsInvolving(AADLInvolvIdentifier id) {
		return involvs.contains(id);
	}
	
	public boolean containsInvolvingName(String name) {
		return involvs.containsName(name);
	}
	
	public boolean containsInvolvingLabel(String label) {
		return involvs.containsLabel(label);
	}
	
	public AADLInvolvIdentifier peekInvolving() {
		return involvs.peek();
	}
	
	public String peekInvolvingName() {
		AADLInvolvIdentifier id = involvs.peek();
		if (id == null)
			throw new AssertionError("No such involving identifier!");
		return id.name();
	}
	
	public String peekInvolvingLabel() {
		AADLInvolvIdentifier id = involvs.peek();
		if (id == null)
			throw new AssertionError("No such involving identifier!");
		return id.label();
	}
	
	//------------------------------------------------------------
	// Function prototype interfaces
	//------------------------------------------------------------
}
