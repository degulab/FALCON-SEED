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
 * @(#)CsvMacroFiles.java	2.1.0	2014/05/29
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)CsvMacroFiles.java	2.0.0	2014/03/18 : move 'ssac.util.*' to 'ssac.aadl.macro.util.*' (recursive) 
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)CsvMacroFiles.java	1.10	2009/12/13
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)CsvMacroFiles.java	1.00	2008/12/07
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.macro.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import ssac.aadl.macro.command.AbstractTokenizer;
import ssac.aadl.macro.command.CommandActionNode;
import ssac.aadl.macro.command.CommandNode;
import ssac.aadl.macro.command.CommandToken;
import ssac.aadl.macro.command.MacroAction;
import ssac.aadl.macro.command.MacroModifier;
import ssac.aadl.macro.command.RecognitionException;
import ssac.aadl.macro.data.MacroData;
import ssac.aadl.macro.data.MacroNode;
import ssac.aadl.macro.data.ModuleArgument;
import ssac.aadl.macro.util.Strings;
import ssac.aadl.macro.util.io.CsvReader;
import ssac.aadl.macro.util.io.CsvWriter;
import ssac.aadl.macro.util.io.JarFileInfo;

/**
 * CSV形式のマクロファイルの入出力機能を提供するユーティリティ。
 * 
 * @version 2.1.0	2014/05/29
 * @since 1.00
 */
public class CsvMacroFiles
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static public final String CsvMacroFileHeader = "#AADLMacro";
	
	static public final int FIELD_COMMAND     = 0;
	static public final int FIELD_PROCNAME    = 1;
	static public final int FIELD_COMMENT     = 2;
	static public final int FIELD_JARNAME     = 3;
	static public final int FIELD_CLASSPATH   = 4;
	static public final int FIELD_MAINCLASS   = 5;
	static public final int FIELD_PARAMETERS  = 6;
	static public final int FIELD_ARG_TYPE1   = 7;
	static public final int FIELD_ARG_VLAUE1  = 8;
	static public final int FIELD_ARG_TYPE99  = 203;
	static public final int FIELD_ARG_VALUE99 = 204;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 指定されたファイルが、CSV形式のAADLマクロファイルかを評価する。
	 * <p>
	 * このメソッドは、指定されたファイルの先頭行がマジックワードと一致するかを
	 * 検証する。マジックワードが一致した場合のみ、AADLマクロファイルと認定する。
	 * それ以外の場合(例外が発生した場合も含む)は、異なるファイル形式とする。
	 * 
	 * @param file	検証対象のファイル
	 * @param encoding	ファイルを読み込む際の文字セット名。
	 * 					<tt>null</tt>の場合はプラットフォームに依存。
	 * 
	 * @return CSV形式のAADLマクロファイルなら <tt>true</tt> を返す。
	 */
	static public boolean isCsvMacroFile(File file, String encoding) {
		boolean result;
		
		// 先頭行読み込みによるフォーマットチェック
		CsvReader csvReader = null;
		try {
			// open csv file
			if (encoding != null && encoding.length() > 0)
				csvReader = new CsvReader(file, encoding);
			else
				csvReader = new CsvReader(file);
			
			// MagicWord 確認(第1行目、第1列が MagicWord かチェック)
			//****************************************************************
			// Excel が生成するCSVは、最大列数分カンマをパディングするので、
			// 先頭行第１列のみのチェックとする
			//****************************************************************
			CsvReader.CsvRecord rec = csvReader.readRecord();
			if (rec == null || !rec.hasFields() || !isCsvMacroFileHeader(rec.getField(0).getValue())) {
				// mismatched csv macro file header
				throw new CsvFormatException("Mismatched CSV header expecting \"" + CsvMacroFileHeader + "\".", 1, 1);
			}
			
			// accept format
			result = true;
		}
		catch (Throwable ignoreEx) {
			// not accept
			ignoreEx = null;
			result = false;
		}
		finally {
			if (csvReader != null) {
				csvReader.close();
				csvReader = null;
			}
		}
		
		// finished
		return result;
	}

	/**
	 * 指定された文字列がCSV形式マクロファイルのヘッダー文字列であるかを検証する。
	 * 
	 * @param text	評価対象の文字列
	 * @return	マジックワードに一致した場合のみ <tt>true</tt> を返す。
	 */
	static public boolean isCsvMacroFileHeader(String text) {
		return CsvMacroFileHeader.equals(text);
	}

	/**
	 * 指定されたファイルから、CSV形式のマクロデータを読み込み、そのデータから
	 * 生成された <code>{@link MacroData}</code> のインスタンスを返す。
	 * 
	 * @param file	対象ファイル
	 * @param encoding	ファイルを読み込む際の文字セット名。
	 * 					<tt>null</tt>の場合はプラットフォームに依存。
	 * @return	生成された <code>{@link MacroData}</code> インスタンス
	 * 
	 * @throws FileNotFoundException ファイルが存在しないか、何らかの理由で開くことができない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws CsvFormatException カラムのデータが正しくない場合にスローされる
	 * @throws UnsupportedEncodingException 指定された文字セットがサポートされていない場合
	 */
	static public MacroData fromFile(File file, String encoding) throws IOException
	{
		// reader オブジェクト生成
		CsvReader csvReader = null;
		if (encoding != null && encoding.length() > 0)
			csvReader = new CsvReader(file, encoding);
		else
			csvReader = new CsvReader(file);
		
		// MacroData
		MacroData data = null;
		
		// 読み込み
		CsvFieldMap mapForTempID = new CsvFieldMap();
		CsvFieldMap mapForArgID  = new CsvFieldMap();
		try {
			// MagicWord 確認(第1行目、第1列が MagicWord かチェック)
			//****************************************************************
			// Excel が生成するCSVは、最大列数分カンマをパディングするので、
			// 先頭行第１列のみのチェックとする
			//****************************************************************
			CsvReader.CsvRecord rec = csvReader.readRecord();
			if (rec == null || !rec.hasFields() || !isCsvMacroFileHeader(rec.getField(0).getValue())) {
				// mismatched csv macro file header
				throw new CsvFormatException("Mismatched CSV header expecting \"" + CsvMacroFileHeader + "\".", 1, 1);
			}
			
			// MacroData 生成
			data = new MacroData();
			data.setMacroName(file.getAbsolutePath());
			data.setWorkDir(file.getAbsoluteFile().getParentFile());
			Set<String> groupProcNames = new HashSet<String>();

			// エントリ読み込み
			CsvReader.CsvRecord csvRecord = null;
			while ((csvRecord = csvReader.readRecord()) != null) {
				// 空行はスキップ
				if (!csvRecord.hasFields()) {
					continue;
				}
				
				// マクロのエントリを取得
				loadMacroEntry(csvRecord, data, groupProcNames, mapForTempID, mapForArgID);
			}
		}
		finally {
			csvReader.close();
		}
		
		// 実行時引数のマップ生成
		if (!mapForArgID.isEmpty()) {
			for (String argID : mapForArgID.keys()) {
				data.macroArgs().put(argID, null);
			}
		}
		
		// テンポラリファイルのマップ生成
		if (!mapForTempID.isEmpty()) {
			for (String tempID : mapForTempID.keys()) {
				data.tempFiles().put(tempID, null);
			}
		}
		
		// 非同期待機の正当性チェック(@since 2.1.0)
		if (!data.isEmptyMacroNodes()) {
			Map<String, MacroNode> mapProcName = data.getProcessNameMap();
			Set<MacroNode> setValidAfterModifier = new HashSet<MacroNode>();	// 非同期待機が正当なノード
			for (MacroNode node : data.macroNodes()) {
				String ownProcName = node.getProcessName();
				CommandActionNode nodeAction = node.getCommandNode();
				// 非同期待機修飾子(after)チェック
				if (node.hasCommandModifier() && node.getCommandModifier() == MacroModifier.AFTER) {
					// 修飾子が after のものをチェック
					Map<String, CommandToken> map = nodeAction.getModifier().getProcessNameListNode().getProcessNameMap();
					//--- 待機するプロセス名の正当性をチェック
					for (Map.Entry<String, CommandToken> entry : map.entrySet()) {
						String refProcName = entry.getKey();
						CommandToken tokenProcName = entry.getValue();
						MacroNode waitNode = mapProcName.get(entry.getKey());	// after() が待機するプロセス名のノード
						if (refProcName.equals(ownProcName)) {
							//--- error : 自身のプロセス名を非同期待機
							int lineNo = node.getLocation().getStartPosition() + tokenProcName.getLine() - 1;
							throw new CsvFormatException(errorReferOwnProcessNameInAfterModifier(refProcName), lineNo, tokenProcName.getPositionInLine());
						}
						else if (waitNode == null) {
							//--- error : 待機するプロセス名に対応するノードが存在しない
							int lineNo = node.getLocation().getStartPosition() + tokenProcName.getLine() - 1;
							throw new CsvFormatException(errorUndefinedIdentifier(refProcName), lineNo, tokenProcName.getPositionInLine());
						}
						else if (waitNode.hasCommandModifier() && waitNode.getCommandModifier() == MacroModifier.AFTER) {
							//--- 待機するプロセス名に対応するノードが、非同期待機
							if (!setValidAfterModifier.contains(waitNode)) {
								//--- error : 待機するプロセス名に対応するノードが、待機不可能な非同期待機
								int lineNo = node.getLocation().getStartPosition() + tokenProcName.getLine() - 1;
								throw new CsvFormatException(errorReferNotStartedProcessNameInAfterModifier(refProcName), lineNo, tokenProcName.getPositionInLine());
							}
						}
						//--- else : 待機するプロセス名に対応するノードが、メインストリームノード
					}
					//--- 非同期待機は正当
					setValidAfterModifier.add(node);
				}
				// 待機アクション(wait)チェック
				if (node.getCommandAction() == MacroAction.WAIT) {
					// 待機コマンドをチェック
					Map<String, CommandToken> map = nodeAction.getProcessNameListNode().getProcessNameMap();
					//--- 待機するプロセス名の正当性をチェック
					for (Map.Entry<String, CommandToken> entry : map.entrySet()) {
						String refProcName = entry.getKey();
						CommandToken tokenProcName = entry.getValue();
						MacroNode waitNode = mapProcName.get(entry.getKey());	// wait() が待機するプロセス名のノード
						if (refProcName.equals(ownProcName)) {
							//--- error : 自身のプロセス名を待機
							int lineNo = node.getLocation().getStartPosition() + tokenProcName.getLine() - 1;
							throw new CsvFormatException(errorReferOwnProcessNameInWaitAction(refProcName), lineNo, tokenProcName.getPositionInLine());
						}
						else if (waitNode == null) {
							//--- error : 待機するプロセス名に対応するノードが存在しない
							int lineNo = node.getLocation().getStartPosition() + tokenProcName.getLine() - 1;
							throw new CsvFormatException(errorUndefinedIdentifier(refProcName), lineNo, tokenProcName.getPositionInLine());
						}
						else if (waitNode.hasCommandModifier() && waitNode.getCommandModifier() == MacroModifier.AFTER) {
							//--- 待機するプロセス名に対応するノードが、非同期待機
							if (!setValidAfterModifier.contains(waitNode)) {
								//--- error : 待機するプロセス名に対応するノードが、待機不可能な非同期待機
								int lineNo = node.getLocation().getStartPosition() + tokenProcName.getLine() - 1;
								throw new CsvFormatException(errorReferNotStartedProcessNameInWaitAction(refProcName), lineNo, tokenProcName.getPositionInLine());
							}
						}
						//--- else : 待機するプロセス名に対応するノードが、メインストリームノード
					}
					//--- 待機は正当
				}
			}
		}
		
		// 完了
		return data;
	}
	
	static public void toFile(MacroData data, File file, String encoding) throws IOException
	{
		// writer オブジェクト生成
		CsvWriter csvWriter = null;
		if (encoding != null && encoding.length() > 0)
			csvWriter = new CsvWriter(file, encoding);
		else
			csvWriter = new CsvWriter(file);
		
		// 書き込み
		try {
			// MagicWord 書き込み
			csvWriter.writeLine(CsvMacroFileHeader);
			
			// エントリ書き込み
			for (int i = 0; i < data.getNumMacroNodes(); i++) {
				MacroNode node = data.getMacroNode(i);
				saveMacroEntry(csvWriter, node);
			}
		}
		finally {
			csvWriter.close();
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	static public String errorInvalidProcessNameForExit(String fieldValue) {
		return "Invalid parameter for 'exit' command in the process name : \"" + fieldValue + "\"";
	}
	
	static public String errorInvalidProcessNameCharacter(String fieldValue) {
		return "Invalid character is included in the process name : \"" + fieldValue + "\"";
	}
	
	static public String errorProcessNameSameCommand(String fieldValue) {
		return "Invalid process name because same Macro command : \"" + fieldValue + "\"";
	}
	
	static public String errorProcessNameSameModifier(String fieldValue) {
		return "Invalid process name because same Macro command modifier : \"" + fieldValue + "\"";
	}
	
	static public String errorMultipledProcessName(String fieldValue) {
		return "Already exist process name : \"" + fieldValue + "\"";
	}
	
	static public String errorUndefinedIdentifier(String fieldValue) {
		return "Undefined process name : \"" + fieldValue + "\"";
	}
	
	static public String errorReferSameGroupProcessName(String fieldValue) {
		return "Cannot to refer to process name of same group : \"" + fieldValue + "\"";
	}
	
	static public String errorReferOwnProcessNameInAfterModifier(String fieldValue) {
		return "Cannot to refer to own process name in 'after' modifier : \"" + fieldValue + "\"";
	}
	
	static public String errorReferOwnProcessNameInWaitAction(String fieldValue) {
		return "Cannot to refer to own process name in 'wait' command : \"" + fieldValue + "\"";
	}
	
	static public String errorReferNotStartedProcessNameInAfterModifier(String fieldValue) {
		return "Cannot to refer to process name does not start in 'after' modifier : \"" + fieldValue + "\"";
	}
	
	static public String errorReferNotStartedProcessNameInWaitAction(String fieldValue) {
		return "Cannot to refer to process name does not start in 'wait' command : \"" + fieldValue + "\"";
	}
	
	static public String errorJarModuleFileNotFound(String fieldValue) {
		return "Jar module file not found : \"" + fieldValue + "\"";
	}
	
	static public String errorMacroModuleFileNotFound(String fieldValue) {
		return "Sub-Macro file not found : \"" + fieldValue + "\"";
	}
	
	static public String errorUndefinedJavaMainClass() {
		return "Undefined Java main-class name.";
	}
	
	static public String errorIllegalTemporaryID(String fieldValue) {
		return "Illegal Temporary file ID : \"" + fieldValue + "\"";
	}
	
	static public String errorIllegalMacroArgumentID(String fieldValue) {
		return "Illegal Macro Argument ID : \"" + fieldValue + "\"";
	}
	
	static public CommandToken findInvalidIdentifierByCommand(final Set<String> procNames, final Set<String> groupProcNames, final CommandActionNode node) {
		// check process name list for modifier (@since 2.1.0)
		if (node.hasModifier()) {
			CommandNode nodeProcNameList = node.getModifier().getProcessNameListNode();
			if (nodeProcNameList != null) {
				CommandToken token = findInvalidIdentifier(procNames, groupProcNames, nodeProcNameList);
				if (token != null)
					return token;
			}
		}
		
		// check process name list for action (@since 2.1.0)
		if (node.hasProcessNameList()) {
			CommandNode nodeProcNameList = node.getProcessNameListNode();
			if (nodeProcNameList != null) {
				CommandToken token = findInvalidIdentifier(procNames, groupProcNames, nodeProcNameList);
				if (token != null)
					return token;
			}
		}
		
		// check conditions
		if (!node.hasChildren())
			return null;	// undefined expression
		for (int i = 0; i < node.getNumChildren(); i++) {
			CommandNode child = node.getChild(i);
			CommandToken token = findInvalidIdentifier(procNames, groupProcNames, child);
			if (token != null)
				return token;
		}
		
		return null;
	}
	
	static private CommandToken findInvalidIdentifier(final Set<String> procNames, final Set<String> groupProcNames, final CommandNode node) {
		CommandToken token = node.getToken();
		if (token != null && token.getType() == CommandToken.IDENTIFIER) {
			String id = token.getText();
			if (!procNames.contains(id)) {
				return token;
			}
			if (groupProcNames.contains(id)) {
				return token;
			}
		}
		
		for (int i = 0; i < node.getNumChildren(); i++) {
			CommandNode child = node.getChild(i);
			CommandToken childToken = findInvalidIdentifier(procNames, groupProcNames, child);
			if (childToken != null)
				return childToken;
		}
		
		return null;
	}
	
	static private void loadMacroEntry(CsvReader.CsvRecord record, MacroData data, Set<String> groupProcNames,
										 CsvFieldMap mapForTempID, CsvFieldMap mapForArgID)
	throws IOException
	{
		// 有効な終端フィールドを検索
		int validFieldBounds = record.getNumFields() - 1;
		for (; validFieldBounds >= 0; validFieldBounds--) {
			CsvReader.CsvField field = record.getField(validFieldBounds);
			if (field.hasValue()) {
				break;	// 有効な終端
			}
		}
		
		// 有効なフィールドが一つも存在しない場合、空行とみなす。
		if (validFieldBounds < 0) {
			return;
		}

		CsvReader.CsvField field = null;
		// エントリの生成
		MacroNode newNode = new MacroNode();
		//--- ロケーション
		newNode.setLocation(new CsvNodeLocation(record.getLineNo()));
		//--- コマンド
		field = record.getField(FIELD_COMMAND);
		newNode.setCommand(field.getValue());
		{
			//--- parse command
			try {
				newNode.parseAction();
			} catch (RecognitionException ex) {
				int lineNo = ex.getLine();
				int posInLine = ex.getPositionInLine();
				if (lineNo > 0) {
					lineNo = field.getLineNo() + lineNo - 1;
				} else {
					lineNo = field.getLineNo();
				}
				throw new CsvFormatException(ex.getLocalizedMessage(), lineNo, posInLine);
			}
			//--- check identifier define
			if (newNode.getCommandAction() != MacroAction.GROUP) {
				//--- reset group process names
				groupProcNames.clear();
				//--- check identifier
				CommandToken unToken = findInvalidIdentifierByCommand(data.definedProcessNames(), groupProcNames, newNode.getCommandNode());
				if (unToken != null) {
					String fieldValue = unToken.getText();
					int lineNo = field.getLineNo() + unToken.getLine() - 1;
					int posInLine = field.getPositionInLine() + unToken.getPositionInLine() - 1;
					throw new CsvFormatException(errorUndefinedIdentifier(fieldValue), lineNo, posInLine);
				}
			} else {
				//--- check identifier
				CommandToken unToken = findInvalidIdentifierByCommand(data.definedProcessNames(), groupProcNames, newNode.getCommandNode());
				if (unToken != null) {
					String fieldValue = unToken.getText();
					int lineNo = field.getLineNo() + unToken.getLine() - 1;
					int posInLine = field.getPositionInLine() + unToken.getPositionInLine() - 1;
					if (groupProcNames.contains(unToken.getText())) {
						//--- reffer process name is in same group
						throw new CsvFormatException(errorReferSameGroupProcessName(fieldValue), lineNo, posInLine);
					} else {
						//--- undefined identifier
						throw new CsvFormatException(errorUndefinedIdentifier(fieldValue), lineNo, posInLine);
					}
				}
			}
		}
		MacroAction action = newNode.getCommandAction();
		//--- プロセス名
		if (FIELD_PROCNAME <= validFieldBounds) {
			field = record.getField(FIELD_PROCNAME);
			if (field.hasValue()) {
				String fieldValue = field.getValue();
				if (action == MacroAction.COMMENT) {
					;	// no implement
				}
				else if (action == MacroAction.EXIT) {
					if (!AbstractTokenizer.isIntegerString(fieldValue)) {
						if (!AbstractTokenizer.isReferenceIdentifierString(fieldValue)) {
							throw new CsvFormatException(errorInvalidProcessNameForExit(fieldValue),
									field.getLineNo(), field.getPositionInLine());
						} else {
							String pn = AbstractTokenizer.getReferencedIdentifier(fieldValue);
							if (!data.containsProcessName(pn)) {
								throw new CsvFormatException(errorUndefinedIdentifier(fieldValue),
										field.getLineNo(), field.getPositionInLine());
							}
						}
					}
				}
				else {
					//--- check for Character validation
					if (!AbstractTokenizer.isIdentifierString(fieldValue)) {
						throw new CsvFormatException(errorInvalidProcessNameCharacter(fieldValue),
								field.getLineNo(), field.getPositionInLine());
					}
					//--- check for Unique
					if (data.containsProcessName(fieldValue)) {
						throw new CsvFormatException(errorMultipledProcessName(fieldValue),
								field.getLineNo(), field.getPositionInLine());
					}
					//--- check for same action
					if (MacroAction.fromCommand(fieldValue) != null) {
						throw new CsvFormatException(errorProcessNameSameCommand(fieldValue),
								field.getLineNo(), field.getPositionInLine());
					}
					//--- check for same modifier
					if (MacroModifier.fromCommand(fieldValue) != null) {
						// @since 2.1.0
						throw new CsvFormatException(errorProcessNameSameModifier(fieldValue),
								field.getLineNo(), field.getPositionInLine());
					}
				}
				newNode.setProcessName(fieldValue);
				if (action == MacroAction.GROUP) {
					groupProcNames.add(fieldValue);
				}
			}
		}
		//--- コメント
		if (FIELD_COMMENT <= validFieldBounds) {
			field = record.getField(FIELD_COMMENT);
			if (field.hasValue()) {
				newNode.setComment(field.getValue());
			}
		}
		//--- 実行モジュール名
		if (FIELD_JARNAME <= validFieldBounds) {
			field = record.getField(FIELD_JARNAME);
			if (field.hasValue()) {
				newNode.setJarModulePath(field.getValue());
				if (action==MacroAction.JAVA || action==MacroAction.GROUP) {
					String jarModuleName = newNode.getAvailableJarModulePath();
					// jar file check
					File jarFile = new File(jarModuleName);
					if (!jarFile.isAbsolute()) {
						jarFile = new File(data.getWorkDir(), jarModuleName);
					}
					if (!jarFile.exists()) {
						throw new CsvFormatException(errorJarModuleFileNotFound(jarFile.getPath()),
								field.getLineNo(), field.getPositionInLine());
					}
					// Manifest のメインクラス取得
					try {
						JarFileInfo jarInfo = new JarFileInfo(jarFile);
						String mainClass = jarInfo.getManifestMainClass();
						if (mainClass != null && mainClass.length() > 0) {
							newNode.setJarModulePath(field.getValue(), mainClass);
						}
					} catch (IOException ignoreEx) {
						ignoreEx = null;
					}
				}
				else if (action==MacroAction.MACRO) {
					String macroName = newNode.getJarModulePath();
					// macro file chek
					File macroFile = new File(macroName);
					if (!macroFile.isAbsolute()) {
						macroFile = new File(data.getWorkDir(), macroName);
					}
					if (!macroFile.exists()) {
						throw new CsvFormatException(errorMacroModuleFileNotFound(macroFile.getPath()),
								field.getLineNo(), field.getPositionInLine());
					}
				}
			}
		}
		//--- クラスパス
		if (FIELD_CLASSPATH <= validFieldBounds) {
			field = record.getField(FIELD_CLASSPATH);
			if (field.hasValue()) {
				newNode.setClassPath(field.getValue());
			}
		}
		//--- クラス名
		if (FIELD_MAINCLASS <= validFieldBounds) {
			field = record.getField(FIELD_MAINCLASS);
			if (field.hasValue()) {
				newNode.setMainClass(field.getValue());
			}
		}
		if (action==MacroAction.JAVA || action==MacroAction.GROUP) {
			String mainClassName = newNode.getAvailableMainClass();
			if (mainClassName == null || mainClassName.length() <= 0) {
				throw new CsvFormatException(errorUndefinedJavaMainClass(),
						record.getLineNo(), 0);
			}
		}
		//--- JAVAパラメータ
		if (FIELD_PARAMETERS <= validFieldBounds) {
			field = record.getField(FIELD_PARAMETERS);
			if (field.hasValue()) {
				newNode.setJavaParameters(field.getValue());
			}
		}
		//--- 引数
		for (int i = FIELD_ARG_TYPE1; i <= validFieldBounds && i <= FIELD_ARG_VALUE99;) {
			String strType = record.getField(i++).getValue();
			String strValue = (i > validFieldBounds ? "" : record.getField(i).getValue());
			i++;
			if (!Strings.isNullOrEmpty(strValue)
				&& (action==MacroAction.JAVA || action==MacroAction.GROUP
				|| action==MacroAction.MACRO || action==MacroAction.SHELL))
			{
				//--- check temp file or macro argument
				String validTempID = AbstractTokenizer.getValidTemporaryID(strValue);
				if (validTempID != null) {
					if (validTempID.length() == 0) {
						// invalid temporary ID
						throw new CsvFormatException(errorIllegalTemporaryID(strValue),
														field.getLineNo(), field.getPositionInLine());
					} else {
						// Valid Temporary file ID
						mapForTempID.add(validTempID, field);
					}
				}
				else {
					//--- check macro argument
					String[] validArgID = AbstractTokenizer.getValidMacroArgumentID(strValue);
					if (validArgID != null) {
						if (validArgID.length == 0) {
							// invlid macro argument ID
							throw new CsvFormatException(errorIllegalMacroArgumentID(strValue),
													field.getLineNo(), field.getPositionInLine());
						} else {
							for (String aid : validArgID) {
								mapForArgID.add(aid, field);
							}
						}
					}
				}
			}
			newNode.addModuleArgument(strType, strValue);
		}
		
		// 保存
		data.addMacroNode(newNode);
	}
	
	static private void saveMacroEntry(CsvWriter writer, MacroNode node) throws IOException
	{
		//--- コマンド
		writer.writeField(false, node.getCommandString());
		//--- プロセス名
		writer.writeField(false, node.getProcessName());
		//--- コメント
		writer.writeField(false, node.getComment());
		//--- 実行モジュール名
		writer.writeField(false, node.getJarModulePath());
		//--- クラスパス
		writer.writeField(false, node.getClassPath());
		//--- クラス名
		writer.writeField(false, node.getMainClass());
		//--- JAVAパラメータ,引数
		if (node.isEmptyModuleArguments()) {
			//--- JAVAパラメータ
			writer.writeField(true, node.getJavaParameters());
		}
		else {
			//--- JAVAパラメータ
			writer.writeField(false, node.getJavaParameters());
			//--- 引数
			int limArgs = node.getNumModuleArguments() - 1;
			int index = 0;
			ModuleArgument marg = null;
			for (; index < limArgs; index++) {
				marg = node.getModuleArgument(index);
				writer.writeField(false, marg.getType());
				writer.writeField(false, marg.getValue());
			}
			marg = node.getModuleArgument(index);
			writer.writeField(false, marg.getType());
			writer.writeField(true, marg.getValue());
		}
	}
}
