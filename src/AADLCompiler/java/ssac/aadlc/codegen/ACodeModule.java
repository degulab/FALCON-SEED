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
 * @(#)ACodeModule.java	1.70	2011/06/29
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ACodeModule.java	1.50	2010/09/27
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ACodeModule.java	1.30	2009/12/02
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ACodeModule.java	1.21	2008/12/25
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ACodeModule.java	1.20	2008/09/25
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ACodeModule.java	1.11	2008/06/19
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ACodeModule.java	1.00	2007/11/29
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadlc.codegen;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

import org.antlr.runtime.Token;
import org.antlr.runtime.tree.CommonTree;

import ssac.aadlc.AADLMessage;
import ssac.aadlc.CompileException;
import ssac.aadlc.analysis.AADLAnalyzer;
import ssac.aadlc.analysis.AADLKeyword;
import ssac.aadlc.analysis.AADLSymbol;
import ssac.aadlc.analysis.type.AADLJavaAction;
import ssac.aadlc.codegen.define.ACodeFunctionType;
import ssac.aadlc.codegen.stms.ACodeBlock;

/**
 * AADLプログラムモジュール
 * 
 * @version 1.70	2011/06/29
 */
public class ACodeModule extends AADLCode
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
	
	protected ACodeModule() {
		super(AADLJavaAction.instance);
	}
	
	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	static public ACodeModule buildFunctionModule(AADLAnalyzer analyzer, List<Token> modifiers, CommonTree astTree,
													ACodeFunctionType funcType, ACodeBlock funcBody)
	{
		// check modifiers
		boolean withPublic = false;
		if (modifiers != null && !modifiers.isEmpty()) {
			for (Token tk : modifiers) {
				String strModifier = tk.getText();
				if ("public".equals(strModifier)) {
					if (withPublic) {
						// 'public' modifier is multiple
						String msg = AADLMessage.duplicateModifierForFunction(strModifier,
								funcType==null ? null : funcType.getFuncType().getName());
						throw new CompileException(tk.getLine(), tk.getCharPositionInLine(), msg);
					}
					withPublic = true;
				}
				else {
					// unsupported modifier token
					String msg = AADLMessage.undefinedToken(strModifier);
					throw new CompileException(tk.getLine(), tk.getCharPositionInLine(), msg);
				}
			}
		}

		// generate Code module object
		ACodeModule retCode = new ACodeModule();
		// 連結のみ
		if (funcType != null && funcBody != null) {
			if (withPublic) {
				//--- public member method
				retCode.jlb.add("public ", funcType.getJavaLineBuffer(), null);
			} else {
				//--- protect member method
				retCode.jlb.add("protected ", funcType.getJavaLineBuffer(), null);
			}
			retCode.jlb.add(funcBody.getJavaLineBuffer());
		}
		return retCode;
	}
	
	static public ACodeModule buildMainModule(AADLAnalyzer analyzer, CommonTree astTree,
												ACodeBlock mainBody)
	{
		ACodeModule retCode = new ACodeModule();
		if (mainBody != null) {
			retCode.genMainModule(analyzer, astTree, mainBody);
		}
		return retCode;
	}
	
	static public ACodeModule buildMainClassBegin(AADLAnalyzer analyzer) {
		ACodeModule retCode = new ACodeModule();
		retCode.jlb.appendLine("public class ", analyzer.getAadlClassName(), " extends ssac.aadl.runtime.AADLModule {", -1);
		return retCode;
	}
	
	static public ACodeModule buildMainClassEnd(AADLAnalyzer analyzer) {
		ACodeModule retCode = new ACodeModule();
		retCode.addErrorReporter(analyzer);
		retCode.jlb.appendLine("}", -1);
		return retCode;
	}

	//------------------------------------------------------------
	// generate codes
	//------------------------------------------------------------
	
	private void genMainModule(AADLAnalyzer analyzer, CommonTree astTree, ACodeBlock mainBody) {
		// 名前チェック
		if (AADLKeyword.instance.contains(analyzer.getAadlClassName())) {
			String msg = AADLMessage.undefinedToken(analyzer.getAadlClassName());
			throw new CompileException(astTree.getLine(), msg);
		}
		
		// ローカルメンバ
		//------- ローカルメンバは、AADLModule クラスで実装したため、コード出力は行わない
		//this.jlb.appendLine(-1, "protected final ssac.aadl.runtime.io.internal.StreamManager _aadl$stream$manager = new ssac.aadl.runtime.io.internal.StreamManager();");
		
		// グローバル定数宣言生成
		//------- グローバル変数は、AADLModule クラスで実装したため、コード出力は行わない
		//if (!analyzer.getFixedVariableCodes().isEmpty()) {
		//	this.jlb.add(analyzer.getFixedVariableCodes().getJavaLineBuffer());
		//}
		
		// 宣言ブロック生成
		List<AADLCode> declCodes = analyzer.getMemberDeclarationCodes();
		if (!declCodes.isEmpty()) {
			for (AADLCode code : declCodes) {
				if (code != null) {
					this.jlb.add(null, code.getJavaLineBuffer(), ";");
				}
			}
		}
		
		// AADLプログラム引数シンボルの補完
		analyzer.getCmdArgs().complementSymbolsToMaxArgNo();
		
		// メインモジュール生成
		int lineNo = astTree.getLine();
		// method definition
		StringBuffer sb = new StringBuffer();
		sb.append("public int aadlRun(");
		if (!analyzer.getCmdArgs().isEmpty()) {
			Iterator<AADLSymbol> it = analyzer.getCmdArgs().getSymbols().iterator();
			if (it.hasNext()) {
				int cnt = 0;
				AADLSymbol cmdSymbol = it.next();
				sb.append(cmdSymbol.getType().getJavaClassName());
				sb.append(" ");
				sb.append(cmdSymbol.getJavaSymbolName());
				++cnt;
				while (it.hasNext()) {
					if (cnt >= 5) {
						this.jlb.appendLine(sb.toString(), lineNo);
						cnt = 0;
						sb.setLength(0);
						sb.append("    ");
					}
					cmdSymbol = it.next();
					sb.append(", ");
					sb.append(cmdSymbol.getType().getJavaClassName());
					sb.append(" ");
					sb.append(cmdSymbol.getJavaSymbolName());
					++cnt;
				}
			}
		}
		sb.append(")");
		// main module codes
		this.jlb.appendLine(sb.toString(), lineNo);
		this.jlb.appendLine("{", -1);
		this.jlb.appendLine("    int _aadl$result = 0;", -1);
		this.jlb.appendLine("    try {", -1);
		this.jlb.add(mainBody.getJavaLineBuffer());
		this.jlb.appendLine("    }", -1);
		this.jlb.appendLine("    catch (Throwable ex) {", -1);
		this.jlb.appendLine("        _aadl$result = (1);", -1);
		this.jlb.appendLine("        " + AADLAnalyzer.getErrorReportFuncName() + "(ex);", -1);
		//this.jlb.appendLine("        //ex.printStackTrace();", -1);
		//this.jlb.appendLine("        //System.err.println();", -1);
		this.jlb.appendLine("    }", -1);
		this.jlb.appendLine("    finally {", -1);
//		this.jlb.appendLine("        _aadl$stream$manager.cleanup();", -1);
		this.jlb.appendLine("        dispose();", -1);
		this.jlb.appendLine("    }", -1);
		this.jlb.appendLine("    return _aadl$result;", -1);
		this.jlb.appendLine("}", -1);
		
		// java program codes
		this.addConstructor(analyzer);
		this.addEntryMethods(analyzer);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	// AADL entry method のコード生成
	private void addEntryMethods(AADLAnalyzer analyzer) {
		// static main
		this.jlb.appendLine(-1, "");
		this.jlb.appendLine(-1, "// java main method");
		this.jlb.appendLine(-1, "static public void main(String[] args) {");
		if (!analyzer.getCmdArgs().isEmpty()) {
			int needArgs = analyzer.getCmdArgs().getMaxArgNo();
			this.jlb.appendLine(-1, "    if (args.length < " + needArgs + ") {");
			this.jlb.appendLine(-1, "        System.err.println(\"Program argument is insufficient.\");");
			this.jlb.appendLine(-1, "        System.err.println(\"Need " + needArgs + " arguments.\");");
			this.jlb.appendLine(-1, "        System.exit(1);");
			this.jlb.appendLine(-1, "    }");
		}
		this.jlb.appendLine(-1, String.format("    %1$s aadlpg = new %1$s();", analyzer.getAadlClassName()));
		this.jlb.appendLine(-1, "    int retCode = aadlpg.aadlRun(args);");
		this.jlb.appendLine(-1, "    System.exit(retCode);");
		this.jlb.appendLine(-1, "}");
		
		// entry member method
		this.jlb.appendLine(-1, "");
		this.jlb.appendLine(-1, "// AADL entry method");
		this.jlb.appendLine(-1, "public int aadlRun(String[] args) {");
		if (!analyzer.getCmdArgs().isEmpty()) {
			// Call aadlRun(...)
			this.jlb.appendLine(-1, "    return aadlRun(");
			Iterator<Integer> it = analyzer.getCmdArgs().numbers().iterator();
			if (it.hasNext()) {
				Integer argNo = it.next();
				// getAADLProgramArgument() は、AADLプログラム番号(1～)を引数に渡す
				this.jlb.appendLine(-1, String.format("         getAADLProgramArgument(args, %d)", argNo.intValue()));
				while (it.hasNext()) {
					argNo = it.next();
					this.jlb.appendLine(-1, String.format("        ,getAADLProgramArgument(args, %d)", argNo.intValue()));
				}
			}
			this.jlb.appendLine(-1, "    );");
		} else {
			this.jlb.appendLine(-1, "    return aadlRun();");
		}
		this.jlb.appendLine(-1, "}");
	}

	// デフォルトコンストラクターの生成
	private void addConstructor(AADLAnalyzer analyzer) {
		this.jlb.appendLine(-1, "");
		this.jlb.appendLine(-1, "// ", analyzer.getAadlClassName(), " class constructor");
		this.jlb.appendLine(-1, "public ", analyzer.getAadlClassName(), "() {");
		this.jlb.appendLine(-1, "    super();");

		List<AADLCode> initCodes = analyzer.getConstructionCodes();
		if (!initCodes.isEmpty()) {
			for (AADLCode code : initCodes) {
				if (code != null) {
					this.jlb.add(code.getJavaLineBuffer());
				}
			}
		}
		
		this.jlb.appendLine(-1, "}");
	}
	
	// 行番号配列初期化関数名の生成
	private String getLineNumberInitializerName(int id) {
		return String.format("_aadl$lnoInitializer%d", id);
	}

	// エラーリポーター
	//--- JAVAコードとAADLコードの行番号対応、ならびに例外情報のレポート機能の追加
	// このコードは、JVMの1メソッドのバイトコードサイズは64KB以内という制約によって、
	// 大きなAADLコードを記述するとコンパイルエラーとなるため、修正。
	private void addErrorReporter(AADLAnalyzer analyzer) {
		// 行番号情報の収集
		TreeMap<Integer,Integer> lineMap = new TreeMap<Integer,Integer>();
		analyzer.getJavaProgram().buildLineMap(lineMap);
		
		// 行番号バッファの生成
		this.jlb.appendLine(-1, "static final int[][] _aadl$lno = new int[2][" + lineMap.size() + "];");
		
		// 行番号バッファ初期化コードの生成
		ArrayList<String> aryInitNames = new ArrayList<String>();
		final int maxBlockLines = 1000;
		int blockLine = 0;
		int idx = 0;
		int id = 1;
		StringBuffer sb = new StringBuffer();
		aryInitNames.add(getLineNumberInitializerName(id));
		this.jlb.appendLine(-1, "static final void ", aryInitNames.get(0), "() {");
		for (Integer jlno : lineMap.keySet()) {
			//System.err.println("@@@ Debug : [" + jlno + "] -> [" + lineMap.get(jlno) + "]");
			if (blockLine >= maxBlockLines) {
				this.jlb.appendLine(-1, "}");
				aryInitNames.add(getLineNumberInitializerName(++id));
				this.jlb.appendLine(-1, "static final void ", aryInitNames.get(aryInitNames.size()-1), "() {");
				blockLine = 0;
			}
			int javaLineNo = (jlno != null ? jlno.intValue() : -1);
			int aadlLineNo = (lineMap.get(jlno) != null ? lineMap.get(jlno).intValue() : -1);
			String strLine = String.format(" _aadl$lno[0][%1$d]=%2$d; _aadl$lno[1][%1$d]=%3$d;", idx, javaLineNo, aadlLineNo);
			idx++;
			if (sb.length() > 0) {
				this.jlb.appendLine(-1, "   ", sb.toString(), strLine);
				blockLine++;
				sb.setLength(0);
			} else {
				sb.append(strLine);
			}
		}
		if (sb.length() > 0) {
			this.jlb.appendLine(-1, "   ", sb.toString());
		}
		this.jlb.appendLine(-1, "}");
		//System.err.println("@@@ Debug : initializer list : " + aryInitNames);
		
		// 行番号バッファの初期化実行コードの生成
		this.jlb.appendLine(-1, "static {");
		for (String strName : aryInitNames) {
			this.jlb.appendLine(-1, "    ", strName, "();");
		}
		this.jlb.appendLine(-1, "}");

		//--- error report function
		this.jlb.appendLine(-1, "");
		this.jlb.appendLine(-1, "protected final int _aadl$getAADLLineNumber(int javaLineNo) {");
		this.jlb.appendLine(-1, "    int lno = java.util.Arrays.binarySearch(_aadl$lno[0], javaLineNo);");
		this.jlb.appendLine(-1, "    if (lno >= 0) {");
		this.jlb.appendLine(-1, "        lno = _aadl$lno[1][lno];");
		this.jlb.appendLine(-1, "    }");
		this.jlb.appendLine(-1, "    return lno;");
		this.jlb.appendLine(-1, "}");
		this.jlb.appendLine(-1, "");
		//------- aadlErrorReport() は、AADLModule クラスにメンバーメソッドとして実装したため、コード出力は行わない
	}
}
