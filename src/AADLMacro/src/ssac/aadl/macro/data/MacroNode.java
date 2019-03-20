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
 * @(#)MacroNode.java	2.1.0	2014/05/29
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MacroNode.java	2.0.0	2014/03/14 : modified and move 'ssac.util.*' to 'ssac.aadl.macro.util.*' (recursive) 
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MacroNode.java	1.10	2009/12/02
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MacroNode.java	1.00	2008/12/07
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.macro.data;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import ssac.aadl.macro.AADLMacroEngine;
import ssac.aadl.macro.CommandLineParser;
import ssac.aadl.macro.command.AbstractTokenizer;
import ssac.aadl.macro.command.CommandActionNode;
import ssac.aadl.macro.command.CommandParser;
import ssac.aadl.macro.command.MacroAction;
import ssac.aadl.macro.command.MacroModifier;
import ssac.aadl.macro.command.RecognitionException;
import ssac.aadl.macro.process.VerboseStream;
import ssac.aadl.macro.util.Strings;

/**
 * AADLマクロを構成する要素。
 * <p>
 * このクラスは、マクロ定義における最小要素となる情報を格納する。
 * 
 * @version 2.1.0	2014/05/29
 * @since 1.00
 */
public class MacroNode implements Cloneable
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	/**
	 * 未定義のフィールドの値
	 */
	static public final String EMPTY_VALUE = "";

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/**
	 * モジュール引数のリスト
	 */
	protected ArrayList<ModuleArgument> moduleArgs = new ArrayList<ModuleArgument>();

	/**
	 * マクロ定義位置を示す情報
	 */
	protected INodeLocation location;
	/**
	 * マクロコマンド
	 */
	protected String cmdString;
	protected CommandActionNode cmdNode;
	/**
	 * プロセス名
	 */
	protected String procName;
	/**
	 * コメント
	 */
	protected String comment;
	/**
	 * 実行モジュール名
	 */
	protected String jarName;
	/**
	 * クラスパス
	 */
	protected String classPath;
	/**
	 * メインクラス名
	 */
	protected String mainClass;
	/**
	 * マニフェストに定義されたメインクラス名
	 */
	protected String jarMainClass;
	/**
	 * Javaパラメータ
	 */
	protected String parameters;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public MacroNode() {
		this.location     = null;
		this.cmdString    = EMPTY_VALUE;
		this.cmdNode      = null;
		this.procName     = EMPTY_VALUE;
		this.comment      = EMPTY_VALUE;
		this.jarName      = EMPTY_VALUE;
		this.classPath    = EMPTY_VALUE;
		this.mainClass    = EMPTY_VALUE;
		this.jarMainClass = EMPTY_VALUE;
		this.parameters   = EMPTY_VALUE;
	}

	//------------------------------------------------------------
	// Public interfaces (Setter / Getter)
	//------------------------------------------------------------
	
	public INodeLocation getLocation() {
		return location;
	}
	
	public void setLocation(INodeLocation location) {
		this.location = location;
	}

	/**
	 * マクロコマンドのアクションを取得する。
	 * @return	このノードのマクロアクション、指定されていない場合は <tt>null</tt>
	 */
	public MacroAction getCommandAction() {
		return (cmdNode == null ? null : cmdNode.getAction());
	}

	/**
	 * マクロコマンドに修飾子が指定されているかを判定する。
	 * @return	修飾子が指定されていれば <tt>true</tt>
	 * @since 2.1.0
	 */
	public boolean hasCommandModifier() {
		return (cmdNode != null && cmdNode.hasModifier());
	}

	/**
	 * マクロコマンドに指定されている修飾子を取得する。
	 * @return	修飾子が指定されていればそのオブジェクト、なければ <tt>null</tt>
	 * @since 2.1.0
	 */
	public MacroModifier getCommandModifier() {
		return (cmdNode != null && cmdNode.hasModifier() ? cmdNode.getModifier().getModifier() : null);
	}

	/**
	 * マクロコマンドに指定されている修飾子に、プロセス名リストが指定されているかを判定する。
	 * @return	修飾子が指定されており、かつプロセス名リストが指定されていれば <tt>true</tt>
	 * @since 2.1.0
	 */
	public boolean hasCommandProcessNameListAsModifier() {
		return (cmdNode != null && cmdNode.hasModifier() && cmdNode.getModifier().hasProcessNameList());
	}

	/**
	 * マクロコマンドに指定されている修飾子に指定されているプロセス名の集合を取得する。
	 * @return	修飾子が指定されており、かつプロセス名リストが指定されていれば、そのプロセス名を要素に持つ <code>Set</code> を返す。
	 * 			プロセス名がない場合は、要素が空の <code>Set</code> を返す。
	 * @since 2.1.0
	 */
	public Set<String> getCommandProcessNameListAsModifier() {
		if (hasCommandProcessNameListAsModifier())
			return cmdNode.getModifier().getProcessNameListNode().getProcessNameSet();
		else
			return Collections.<String>emptySet();
	}

	/**
	 * マクロコマンドのアクションに、プロセス名リストが指定されているかを判定する。
	 * @return	アクションにプロセス名リストが指定されていれば <tt>true</tt>
	 * @since 2.1.0
	 */
	public boolean hasCommandProcessNameListAsAction() {
		return (cmdNode != null && cmdNode.hasProcessNameList());
	}

	/**
	 * マクロコマンドのアクションに指定されているプロセス名の集合を取得する。
	 * @return	アクションにプロセス名リストが指定されていれば、そのプロセス名を要素に持つ <code>Set</code> を返す。
	 * 			プロセス名がない場合は、要素が空の <code>Set</code> を返す。
	 * @since 2.1.0
	 */
	public Set<String> getCommandProcessNameListAsAction() {
		if (hasCommandProcessNameListAsAction())
			return cmdNode.getProcessNameListNode().getProcessNameSet();
		else
			return Collections.<String>emptySet();
	}
	
	public String getCommandString() {
		return cmdString;
	}
	
	public CommandActionNode getCommandNode() {
		return cmdNode;
	}
	
	public void setCommand(String cmdString) {
		this.cmdString = (cmdString == null ? EMPTY_VALUE : cmdString);
		this.cmdNode = null;
	}
	
	public String getProcessName() {
		return procName;
	}
	
	public void setProcessName(String procName) {
		this.procName = (procName == null ? EMPTY_VALUE : procName);
	}
	
	public String getComment() {
		return comment;
	}
	
	public void setComment(String comment) {
		this.comment = (comment == null ? EMPTY_VALUE : comment.intern());
	}
	
	public String getJarModulePath() {
		return jarName;
	}
	
	public void setJarModulePath(String path) {
		this.jarName = (path == null ? EMPTY_VALUE : path.intern());
		this.jarMainClass = EMPTY_VALUE;
	}
	
	public void setJarModulePath(String path, String manifestMainClass) {
		this.jarName = (path == null ? EMPTY_VALUE : path.intern());
		this.jarMainClass = (manifestMainClass == null ? EMPTY_VALUE : manifestMainClass.intern());
	}
	
	public String getClassPath() {
		return classPath;
	}
	
	public void setClassPath(String classPath) {
		this.classPath = (classPath == null ? EMPTY_VALUE : classPath.intern());
	}
	
	public String getMainClass() {
		return mainClass;
	}
	
	public String getJarManifestMainClass() {
		return jarMainClass;
	}
	
	public void setMainClass(String mainClass) {
		this.mainClass = (mainClass == null ? EMPTY_VALUE : mainClass.intern());
	}
	
	public String getJavaParameters() {
		return parameters;
	}
	
	public void setJavaParameters(String parameters) {
		this.parameters = (parameters == null ? EMPTY_VALUE : parameters.intern());
	}
	
	public List<ModuleArgument> args() {
		return moduleArgs;
	}
	
	public boolean isEmptyModuleArguments() {
		return moduleArgs.isEmpty();
	}
	
	public int getNumModuleArguments() {
		return moduleArgs.size();
	}
	
	public ModuleArgument getModuleArgument(int index) {
		return moduleArgs.get(index);
	}
	
	public void addModuleArgument(String type, String value) {
		moduleArgs.add(new ModuleArgument(type, value));
	}
	
	public void insertModuleArgument(int index, String type, String value) {
		moduleArgs.add(index, new ModuleArgument(type, value));
	}

	//------------------------------------------------------------
	// Public interfaces (Helper)
	//------------------------------------------------------------
	
	static public String getAvailableJarModulePath(String jarName) {
		if (jarName.length() > 0 && !Strings.endsWithIgnoreCase(jarName, ".jar"))
			return (jarName + ".jar");
		else
			return jarName;
	}
	
	public void parseAction() throws RecognitionException
	{
		CommandParser parser = new CommandParser();
		this.cmdNode = (CommandActionNode)parser.parseCommand(this.cmdString);
	}
	
	public String getAvailableJarModulePath() {
		return getAvailableJarModulePath(jarName);
	}

	/**
	 * プロセス名から終了コードを取得する。このメソッドは、プロセス名に数値、
	 * プロセス名参照、プロセス名が格納されていることを前提とする。
	 * 
	 * @param data	マクロデータ
	 * @param defaultExitCode	終了コードが取得できなかった場合のデフォルト値
	 * @return	取得した終了コードを返す。
	 */
	public int getExitCodeFromProcessName(final MacroData data, final int defaultExitCode) {
		if (procName != null && procName.length() > 0) {
			Integer integer;
			try {
				// 数値に変換
				integer = Integer.valueOf(procName);
			}
			catch (NumberFormatException ex) {
				// プロセス名に対応する終了コードを取得する
				if (AbstractTokenizer.isReferenceIdentifierString(procName))
					integer = data.getProcessExitCode(AbstractTokenizer.getReferencedIdentifier(procName));
				else
					integer = data.getProcessExitCode(procName);
			}
			if (integer != null) {
				return integer.intValue();
			}
		}
		
		return defaultExitCode;
	}
	
	public String getEchoString(final MacroData data) {
		final StringBuilder sb = new StringBuilder(1024);
		
		//--- comment
		appendEchoString(sb, data, comment);
		//--- module
		appendEchoString(sb, data, jarName);
		//--- classPath
		appendEchoString(sb, data, classPath);
		//--- mainClass
		appendEchoString(sb, data, mainClass);
		//--- cmdArgs
		appendEchoString(sb, data, parameters);
		//--- moduelArgs
		for (ModuleArgument marg : moduleArgs) {
			appendEchoString(sb, data, marg.getType());
			appendEchoString(sb, data, marg.getValue());
		}
		
		return sb.toString();
	}
	
	public String[] getShellCommandArray(final MacroData data) {
		// comment, module, classPath, mainClass は無視
		
		// VM引数を分解
		List<String> vmArgs = getAvailableJavaParameterList();
		
		// 格納用バッファ生成
		int length = vmArgs.size() + getNumModuleArguments();
		final ArrayList<String> cmdArgs = new ArrayList<String>(length);
		
		// VM引数を格納
		cmdArgs.addAll(vmArgs);
		
		// モジュール引数を格納
		//for (ModuleArgument marg : moduleArgs) {
		//	cmdArgs.add(marg.getValue());
		//}
		appendModuleArguments(data, cmdArgs, false);
		
		// 完了
		return cmdArgs.toArray(new String[cmdArgs.size()]);
	}
	
	public String[] getJavaModuleCommandArray(final AADLMacroEngine engine, final MacroData data) {
		// VMオプション取得
		List<String> vmArgs = getAvailableJavaParameterList();
		
		// コマンド文字列バッファ作成
		int buflen = vmArgs.size() + moduleArgs.size() + 4;
		ArrayList<String> cmdArgs = new ArrayList<String>(buflen);
		
		// Java コマンド
		String javacmd = AADLMacroEngine.getJavaCommandPath();
		cmdArgs.add(javacmd);
		
		// クラスパス
		String libPath = engine.getAvailableLibPath();
		String classPath = getAvailableClassPathString();
		if (classPath != null) {
			cmdArgs.add("-classpath");
			if (libPath != null && libPath.length() > 0)
				cmdArgs.add(classPath + File.pathSeparatorChar + libPath);
			else
				cmdArgs.add(classPath);
		} else if (libPath != null && libPath.length() > 0) {
			cmdArgs.add("-classpath");
			cmdArgs.add(libPath);
		}
		
		// VMオプション
		setJavaVMArgsForJavaCommand(engine, vmArgs, cmdArgs);
		
		// メインクラス名
		String mainClassName = getAvailableMainClass();
		cmdArgs.add(mainClassName);
		
		// モジュール引数
		//for (ModuleArgument marg : moduleArgs) {
		//	cmdArgs.add(marg.getValue());
		//}
		appendModuleArguments(data, cmdArgs, false);
		
		// コマンドリストを返す
		return cmdArgs.toArray(new String[cmdArgs.size()]);
	}
	
	public String[] getSubMacroCommandArray(final AADLMacroEngine engine, final MacroData data) {
		// VMオプション取得
		List<String> vmArgs = getAvailableJavaParameterList();
		List<String> meOpts = engine.getCommandLine().getInheritOptions();
		
		// コマンド文字列バッファ作成
		int buflen = vmArgs.size() + meOpts.size() + moduleArgs.size() + 4;
		ArrayList<String> cmdArgs = new ArrayList<String>(buflen);
		
		// Java コマンド
		String javacmd = AADLMacroEngine.getJavaCommandPath();
		cmdArgs.add(javacmd);
		
		// クラスパス
		cmdArgs.add("-classpath");
		cmdArgs.add(AADLMacroEngine.getMacroEnginePath());
		
		// VM オプション
		setJavaVMArgsForSubMacroCommand(engine, vmArgs, cmdArgs);
		
		// メインクラス名
		cmdArgs.add(AADLMacroEngine.class.getName());
		
		// AADLマクロ実行エンジン引数の継承
		if (!meOpts.isEmpty()) {
			cmdArgs.addAll(meOpts);
		}
		
		// マクロファイル名
		if (!Strings.isNullOrEmpty(jarName)) {
			cmdArgs.add(jarName);
		} else {
			cmdArgs.add("");
		}
		
		// モジュール引数
		//for (ModuleArgument marg : moduleArgs) {
		//	cmdArgs.add(marg.getValue());
		//}
		appendModuleArguments(data, cmdArgs, true);
		
		// コマンドリストを返す
		return cmdArgs.toArray(new String[cmdArgs.size()]);
	}
	
	void setJavaVMArgsForJavaCommand(final AADLMacroEngine engine, List<String> vmArgs, List<String> cmdArgs) {
		CommandLineParser cmdLine = engine.getCommandLine();
		
		// set AADL csv encoding property
		if (cmdLine.hasAadlCsvEncoding()) {
			cmdArgs.add("-Daadl.csv.encoding=" + cmdLine.getAadlCsvEncoding());
		}
		
		// set AADL txt encoding property
		if (cmdLine.hasAadlTxtEncoding()) {
			cmdArgs.add("-Daadl.txt.encoding=" + cmdLine.getAadlTxtEncoding());
		}
		
		// set Verbose options
		String verboseOption = VerboseStream.getVerboseOption();
		if (verboseOption != null) {
			cmdArgs.add(verboseOption);
		}
		
		// set specified VM args
		if (cmdLine.hasHeapMax()) {
			cmdArgs.add("-Xmx" + cmdLine.getHeapMax());
			//--- ignore -Xmx???? and -Xms???? option
			for (String vma : vmArgs) {
				if (!vma.startsWith("-Xmx") && !vma.startsWith("-Xms")) {
					cmdArgs.add(vma);
				}
			}
		} else if (!vmArgs.isEmpty()) {
			cmdArgs.addAll(vmArgs);
		}
		
		if (cmdLine.hasJavaVMoptions()) {
			cmdArgs.addAll(cmdLine.getJavaVMoptionList());
		}
	}
	
	void setJavaVMArgsForSubMacroCommand(final AADLMacroEngine engine, List<String> vmArgs, List<String> cmdArgs) {
		CommandLineParser cmdLine = engine.getCommandLine();
		
		// set specified VM args
		if (cmdLine.hasHeapMax()) {
			cmdArgs.add("-Xmx" + cmdLine.getHeapMax());
			//--- ignore -Xmx???? and -Xms???? option
			for (String vma : vmArgs) {
				if (!vma.startsWith("-Xmx") && !vma.startsWith("-Xms")) {
					cmdArgs.add(vma);
				}
			}
		} else if (!vmArgs.isEmpty()) {
			cmdArgs.addAll(vmArgs);
		}
		
		if (cmdLine.hasJavaVMoptions()) {
			cmdArgs.addAll(cmdLine.getJavaVMoptionList());
		}
	}

	//------------------------------------------------------------
	// Public interfaces (Override)
	//------------------------------------------------------------

	@Override
	@SuppressWarnings("unchecked")
	public MacroNode clone() throws CloneNotSupportedException
	{
		try {
			MacroNode n = (MacroNode)super.clone();
			if (this.cmdNode != null) {
				n.cmdNode = this.cmdNode.clone();
			}
			n.moduleArgs = (ArrayList<ModuleArgument>)this.moduleArgs.clone();
			return n;
		}
		catch (CloneNotSupportedException ex) {
			throw new InternalError();
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		//--- location
		sb.append("location[");
		sb.append(String.valueOf(location));
		sb.append("]");
		//--- action
		sb.append(" command[");
		sb.append(cmdString.toString());
		sb.append("]");
		//--- comment
		sb.append(" comment[");
		sb.append(comment);
		sb.append("]");
		//--- module
		sb.append(" jarModule[");
		sb.append(jarName);
		sb.append("] jarMainClass[");
		sb.append(jarMainClass);
		sb.append("]");
		//--- classPath
		sb.append(" classPath[");
		sb.append(classPath);
		sb.append("]");
		//--- mainClass
		sb.append(" mainClass[");
		sb.append(mainClass);
		sb.append("]");
		//--- cmdArgs
		sb.append(" javaParameters[");
		sb.append(parameters);
		sb.append("]");
		//--- moduleArgs
		sb.append(" moduleArgs");
		sb.append(moduleArgs.toString());
		
		return sb.toString();
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	private String getAvailableClassPathString() {
		String mainModule = getAvailableJarModulePath();
		if (classPath != null && classPath.length() > 0) {
			String cp = classPath.replaceAll(AADLMacroEngine.MACRO_CLASSPATH_SEPARATOR, File.pathSeparator);
			if (mainModule.length() > 0)
				return cp + File.pathSeparatorChar + mainModule;
			else
				return cp;
		} else {
			if (mainModule.length() > 0)
				return mainModule;
			else
				return null;
		}
	}
	
	public String getAvailableMainClass() {
		if (mainClass != null && mainClass.length() > 0)
			return mainClass;
		else
			return jarMainClass; 
	}
	
	private List<String> getAvailableJavaParameterList() {
		// VM引数を分解
		String[] vmArgs = Strings.splitCommandLineWithDoubleQuoteEscape(parameters);
		return Arrays.asList(vmArgs);
	}

	/**
	 * 指定されたリストにコマンドのモジュール引数を追加する。
	 * このモジュール引数は、テンポラリファイル識別子、実行時引数識別子を適切なパラメータに
	 * 変換した後、指定されたリストに追加する。
	 * @param data	マクロデータ
	 * @param cmdArgs	追加するリスト
	 * @param convertRelativeToAbsoluteInOutArgments	'[IN]' もしくは '[OUT]' 属性の引数のみ
	 * 													相対パスを絶対パスに変換する場合は <tt>true</tt> を指定する
	 * @since 1.10
	 */
	private void appendModuleArguments(final MacroData data, List<String> cmdArgs, boolean convertRelativeToAbsoluteInOutArgments) {
		File workDir = data.getWorkDir();
		for (ModuleArgument marg : moduleArgs) {
			String strValue = convertReferenceID(data, marg.getValue());
			if (strValue == null) {
				cmdArgs.add("");
			}
			else {
				if (convertRelativeToAbsoluteInOutArgments) {
					if (marg.isTypeIN() || marg.isTypeOUT()) {
						File f = new File(strValue);
						if (!f.isAbsolute()) {
							if (workDir != null) {
								f = new File(workDir, strValue);
							}
							strValue = f.getAbsolutePath();
						}
					}
				}
				cmdArgs.add(strValue);
			}
		}
	}

	/**
	 * 指定された文字列からモジュール引数とする文字列に変換する。
	 * この変換では、参照ID(プロセスID、モジュール引数ID、テンポラリファイルID)を
	 * 適切なパラメータに変換する。
	 * @param data		変換に必要な情報を保持するマクロデータ
	 * @param value		変換する値
	 * @return	変換後の値
	 * 
	 * @since 1.10
	 */
	private String convertReferenceID(final MacroData data, final String value) {
		if (value == null)
			return null;
		int len = value.length();
		if (len <= 0)
			return value;
		
		// テンポラリ識別子を取得
		String strTempID = AbstractTokenizer.getValidTemporaryID(value);
		//--- この時点でテンポラリファイル識別子は正当
		if (strTempID != null) {
			File tempFile = data.tempFiles().get(strTempID);
			if (tempFile != null) {
				// テンポラリファイル識別子は
				// テンポラリファイルへのパスのみを実パラメータとする。
				return tempFile.getPath();
			}
		}
		
		// 識別子の有無を判定
		int topIndex = AbstractTokenizer.findRefIdentifierStart(value, 0);
		if (topIndex < 0) {
			// no reference
			return value;
		}
		
		// 識別子の変換
		int nextIndex = 0;
		int sbIndex, endIndex;
		String strRefID;
		StringBuilder sb = new StringBuilder();
		do {
			if (nextIndex < topIndex) {
				sb.append(value.substring(nextIndex, topIndex));
			}
			
			sbIndex = value.indexOf('{', topIndex+1);
			endIndex = AbstractTokenizer.findRefIdentifierEnd(value, sbIndex+1);
			nextIndex = endIndex + 1;
			strRefID = value.substring(topIndex, nextIndex);
			if ((sbIndex - topIndex) == 1) {
				//--- process name reference or macro argument reference
				String macroArg = data.macroArgs().get(strRefID);
				if (macroArg != null) {
					// exist macro argument
					strRefID = macroArg;
				} else {
					Integer exitCode = data.getProcessExitCode(value.substring(sbIndex+1, endIndex));
					if (exitCode != null) {
						// exist exit code
						strRefID = exitCode.toString();
					}
				}
			}
			//--- この時点でテンポラリファイル識別子は存在していないので、処理しない
			sb.append(strRefID);
		} while ((nextIndex < len) && ((topIndex = AbstractTokenizer.findRefIdentifierStart(value, nextIndex)) >= 0));
		
		if (nextIndex < len && topIndex < 0) {
			// no more ref-id
			sb.append(value.substring(nextIndex));
		}
		
		return sb.toString();
	}
	
	private void appendEchoString(final StringBuilder sb, final MacroData data, final String str) {
		if (str == null)
			return;		// no text
		int len = str.length();
		if (len <= 0)
			return;		// no text
		
		if (sb.length() > 0)
			sb.append(" ");	// add delimiter
		
		int nextIndex = 0;
		int topIndex, sbIndex, endIndex;
		String strRefID;
		do {
			topIndex = AbstractTokenizer.findRefIdentifierStart(str, nextIndex);
			if (topIndex < 0) {
				// no more ref-id
				sb.append(str.substring(nextIndex));
				break;
			} else if (nextIndex < topIndex) {
				sb.append(str.substring(nextIndex, topIndex));
			}
			
			sbIndex = str.indexOf('{', topIndex+1);
			endIndex = AbstractTokenizer.findRefIdentifierEnd(str, sbIndex+1);
			nextIndex = endIndex + 1;
			strRefID = str.substring(topIndex, nextIndex);
			if ((sbIndex - topIndex) == 1) {
				//--- process name reference or macro argument reference
				String macroArg = data.macroArgs().get(strRefID);
				if (macroArg != null) {
					// exist macro argument
					strRefID = macroArg;
				} else {
					Integer exitCode = data.getProcessExitCode(str.substring(sbIndex+1, endIndex));
					if (exitCode != null) {
						// exist exit code
						strRefID = exitCode.toString();
					}
				}
			} else {
				//--- temporary file reference
				String strTempID = "$tmp" + str.substring(sbIndex, nextIndex);
				File tempFile = data.tempFiles().get(strTempID);
				if (tempFile != null) {
					strRefID = tempFile.getPath();
				}
			}
			sb.append(strRefID);

			/*--- old codes
			String procName = str.substring(topIndex+2, endIndex);
			Integer exitCode = data.getProcessExitCode(procName);
			if (exitCode != null) {
				// exist exit code
				sb.append(exitCode);
			} else {
				// undefined exit code
				sb.append(str.substring(topIndex, nextIndex));
			}
			**--- end of old codes ---*/
		} while (nextIndex < len);
	}
}
