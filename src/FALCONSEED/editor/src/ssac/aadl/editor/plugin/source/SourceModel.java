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
 * @(#)SourceModel.java	1.17	2011/02/02
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)SourceModel.java	1.16	2010/09/27
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)SourceModel.java	1.14	2009/12/17
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)SourceModel.java	1.12	2009/04/20
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)SourceModel.java	1.10	2009/01/28
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.editor.plugin.source;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.EditorKit;
import javax.swing.text.PlainDocument;

import ssac.aadl.editor.AADLEditor;
import ssac.aadl.editor.EditorMessages;
import ssac.aadl.editor.build.ClassPaths;
import ssac.aadl.editor.build.CompileMessageElem;
import ssac.aadl.editor.build.CompileMessageMap;
import ssac.aadl.editor.build.ExecutorFactory;
import ssac.aadl.editor.document.IEditorTextDocument;
import ssac.aadl.editor.setting.AppSettings;
import ssac.aadl.editor.view.JEncodingComboBox;
import ssac.aadl.module.setting.AadlJarProperties;
import ssac.aadl.module.setting.CompileSettings;
import ssac.util.Objects;
import ssac.util.Strings;
import ssac.util.Validations;
import ssac.util.io.Files;
import ssac.util.io.JarFileInfo;
import ssac.util.logging.AppLogger;
import ssac.util.process.CommandExecutor;

/**
 * AADLソースのドキュメントモデル。
 * 
 * @version 1.17	2011/02/02
 * 
 * @since 1.10
 */
public class SourceModel extends PlainDocument implements IEditorTextDocument
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/**
	 * このドキュメントのマネージャインスタンス
	 */
	private final SourceComponentManager manager;
	/**
	 * コンパイルメッセージのマップ
	 */
	private final CompileMessageMap errMessages = new CompileMessageMap();
	/**
	 * ソースコンパイル用設定情報
	 */
	private final CompileSettings	settings = new CompileSettings();

	/**
	 * このドキュメントが新規ドキュメントであることを示すフラグ
	 */
	private boolean flgNew;
	/**
	 * テキストドキュメント変更状態を示すフラグ
	 */
	private boolean flgModified;
	/**
	 * ソースドキュメントの対象ファイル
	 */
	private File targetFile;
	/**
	 * ドキュメント対象ファイルのロード時の最終更新日時
	 * @since 1.17
	 */
	private long	_lastModifiedWhenLoadingTargetFile = 0L;
	/**
	 * 最後に適用したファイルエンコーディング
	 */
	private String _lastEncoding = null;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	protected SourceModel(final SourceComponentManager manager) {
		super();
		this.manager = Validations.validNotNull(manager);
		this._lastEncoding = AppSettings.getInstance().getAadlSourceEncodingName();
		this.flgNew = true;
		setModifiedFlag(true);
	}
	
	protected SourceModel(final SourceComponentManager manager, File srcFile) throws IOException
	{
		this(manager, srcFile, null);
	}
	
	protected SourceModel(final SourceComponentManager manager, File srcFile, String encoding) throws IOException
	{
		super();
		this.manager = Validations.validNotNull(manager, "'manager' is null.");
		this._lastEncoding = AppSettings.getInstance().getAadlSourceEncodingName();
		initDocument(Validations.validNotNull(srcFile, "'srcFile' is null."), encoding);
		this.flgNew = false;
		setModifiedFlag(false);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * コンパイルメッセージのクリア
	 */
	public void clearCompileMessages() {
		errMessages.clear();
	}

	/**
	 * コンパイルメッセージの設定
	 * @param exitCode		AADLコンパイラの終了コード
	 * @param strMessages	メッセージ
	 */
	public void setCompileMessages(int exitCode, String strMessages) {
		clearCompileMessages();
		if (exitCode != 2 && exitCode != 3) {
			// コンパイルエラーのメッセージではない
			return;
		}
		
		// パターン生成
		String strName = (targetFile==null ? null : targetFile.getName());
		if (strName == null) {
			strName = ".aadl";
		}
		final String indent = "    \n";
		Pattern patErr = Pattern.compile("\\A(\\[[^:]*\\])?\\S*[^:]*\\Q" + strName + "\\E:([^:]*):(?:([^:]*):)?\\s*(.*)\\Z", Pattern.DOTALL);
		Pattern patPos = Pattern.compile("\\A\\s*\\^\\s*\\Z", Pattern.DOTALL);
		Pattern patEx  = Pattern.compile("\\A\\Q[Error]\\E\\s+(.*)\\s+:\\s(.*)\\Z", Pattern.DOTALL);
		
		// メッセージ読み込み
		try {
			BufferedReader br = new BufferedReader(new StringReader(strMessages));
			String strLine;
			boolean bMatched = false;
			int lastLineNo = CompileMessageElem.IGNORE_NO;
			int lastColNo  = CompileMessageElem.IGNORE_NO;
			StringBuffer sb = new StringBuffer();
			while ((strLine = br.readLine()) != null) {
				strLine = strLine.trim();
				//--- 空白行は無視
				if (strLine.length() <= 0)
					continue;
				//--- マッチング
				Matcher mc = patErr.matcher(strLine);
				if (mc.matches()) {
					if (sb.length() > 0) {
						// 既存のメッセージをセット
						this.errMessages.addLineMessage(lastLineNo, lastColNo, sb.toString());
						lastLineNo = CompileMessageElem.IGNORE_NO;
						lastColNo  = CompileMessageElem.IGNORE_NO;
						sb.setLength(0);
					}
					//--- メッセージ新規登録
					//String strType = mc.group(1);
					String strLineNo = mc.group(2);
					String strColNo = mc.group(3);
					String strText = mc.group(4);
					//--- 行番号
					try {
						lastLineNo = Integer.valueOf(strLineNo).intValue();
						if (lastLineNo < 0) {
							lastLineNo = CompileMessageElem.IGNORE_NO;
						}
					} catch (Throwable ex) {
						lastLineNo = CompileMessageElem.IGNORE_NO;
					}
					//--- カラム番号
					try {
						lastColNo = Integer.valueOf(strColNo).intValue();
						if (lastColNo < 1) {
							lastColNo = CompileMessageElem.IGNORE_NO;
						}
					} catch (Throwable ex) {
						lastColNo = CompileMessageElem.IGNORE_NO;
					}
					//--- メッセージ
					sb.append(strText);
					bMatched = true;
				} else {
					Matcher mcpos = patPos.matcher(strLine);
					Matcher mcex = patEx.matcher(strLine);
					if (mcex.matches()) {
						// 非出力例外メッセージ
					}
					else if (mcpos.matches()) {
						// JAVAエラーのカラム位置インジケーター
						sb.append(indent);
						sb.append(strLine);
						// 既存のメッセージをセット
						this.errMessages.addLineMessage(lastLineNo, lastColNo, sb.toString());
						lastLineNo = CompileMessageElem.IGNORE_NO;
						lastColNo  = CompileMessageElem.IGNORE_NO;
						sb.setLength(0);
						bMatched = false;
					}
					else if (bMatched) {
						sb.append(indent);
						sb.append(strLine);
					}
				}
				//--- 後は何もしない
			}
			if (sb.length() > 0) {
				// 既存のメッセージをセット
				this.errMessages.addLineMessage(lastLineNo, lastColNo, sb.toString());
				lastLineNo = CompileMessageElem.IGNORE_NO;
				lastColNo  = CompileMessageElem.IGNORE_NO;
				sb.setLength(0);
			}
			br.close();
		}
		catch (IOException ignoreEx) {}
	}

	/**
	 * コンパイルメッセージの取得
	 * 
	 * @param lineNo	1 から始まる行番号
	 * @return	行番号に対応するメッセージを返す。
	 * 			行番号に対応するメッセージが存在しない場合は <tt>null</tt> を返す。
	 */
	public String getErrorMessagesByLine(int lineNo) {
		String ret = errMessages.getMessage(lineNo);
		if (Strings.isNullOrEmpty(ret)) {
			ret = null;
		}
		return ret;
	}
	
	//------------------------------------------------------------
	// Implement IEditorDocument interfaces
	//------------------------------------------------------------
	
	/**
	 * 表示に関するリソースを開放する。
	 * このドキュメントがコンパイルもしくは実行可能なドキュメントの場合、
	 * このメソッドの実行によってコンパイルもしくは実行に影響があってはならない。
	 * @since 1.16
	 */
	public void releaseViewResources() {
		// No implement.
	}

	public String getTitle() {
		if (targetFile != null)
			return targetFile.getName();
		else
			return EditorMessages.getInstance().newDocumentTitle;
	}

	public boolean hasTargetFile() {
		return (targetFile != null);
	}

	public File getTargetFile() {
		return targetFile;
	}
	
	/**
	 * このドキュメントの保存先を、指定されたパスに設定する。
	 * このメソッドでは、保存先の正当性検証は行わず、ターゲットを指定されたパスに
	 * 設定するのみとなる。編集状態なども変更されない。
	 * @param newTarget	新しい保存先となる抽象パス
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @since 1.14
	 */
	public void setTargetFile(File newTarget) {
		this.targetFile = Validations.validNotNull(newTarget);
		this.settings.setPropertyTarget(newTarget);
		updateLastModifiedTimeWhenLoadingTargetFile();
	}
	
	/**
	 * このドキュメントが読み込まれた時点での保存先ファイルの最終更新日時を取得する。
	 * 保存先ファイルが指定されていない場合は 0L を返す。
	 * @return	ドキュメントが読み込まれた時点での、最終更新日時
	 * @since 1.17
	 */
	public long lastModifiedTimeWhenLoadingTargetFile() {
		return _lastModifiedWhenLoadingTargetFile;
	}
	
	/**
	 * このドキュメントの保存先ファイルの、現在の最終更新日時を取得する。
	 * 保存先ファイルが指定されていない場合は 0L を返す。
	 * @return	最終更新日時
	 * @since 1.17
	 */
	public long lastModifiedTimeWhenCurrentTargetFile() {
		if (hasTargetFile()) {
			return getTargetFile().lastModified();
		} else {
			return 0L;
		}
	}
	
	/**
	 * このドキュメントが読み込まれた時点での保存先ファイルの最終更新日時を、
	 * 保存先ファイルの現在の更新日時で更新する。
	 * @since 1.17
	 */
	public void updateLastModifiedTimeWhenLoadingTargetFile() {
		if (hasTargetFile()) {
			_lastModifiedWhenLoadingTargetFile = getTargetFile().lastModified();
		}
	}

	public SourceComponentManager getManager() {
		return manager;
	}

	public boolean isNewDocument() {
		return flgNew;
	}

	public boolean isModified() {
		return flgModified;
	}
	
	public boolean canMoveTargetFile() {
		// ターゲットファイルが指定されていなければ、移動不可
		if (hasTargetFile()) {
			return false;
		}
		
		// 編集中のドキュメントなら、移動不可
		if (isModified()) {
			return false;
		}
		
		// 移動を許可
		return true;
	}

	/**
	 * 現在のドキュメントに関連付けられたファイルのエンコーディング名を返す。
	 * このメソッドが返すエンコーディング名は、読み込み時もしくは保存時に
	 * 適用されたものとなる。
	 * @return	エンコーディング名(<tt>null</tt> 以外)
	 */
	public String getLastEncodingName() {
		if (_lastEncoding == null) {
			return JEncodingComboBox.getDefaultEncoding();
		} else {
			return _lastEncoding;
		}
	}

	public void setModifiedFlag(boolean modified) {
		this.flgModified = modified;
	}
	
	/**
	 * このドキュメントに関連付けられている設定情報を、最新の情報に更新する。
	 * @return	最新の情報に更新された場合に <tt>true</tt> を返す。
	 * @since 1.14
	 */
	public boolean refreshSettings() {
		if (settings != null) {
			return settings.refresh();
		} else {
			return false;
		}
	}

	/**
	 * コンパイル設定情報のインスタンスを返す。
	 * @return <code>CompileSettings</code> のインスタンス(<tt>null</tt>以外)
	 */
	public CompileSettings getSettings() {
		return settings;
	}
	
	/**
	 * このドキュメントにおける実行可能ファイルの有無を判定する。
	 * @return	ドキュメントにおける実行可能ファイルが存在している場合は <tt>true</tt>
	 */
	public boolean hasExecutableFile() {
		return (getExecutableFile() != null);
	}
	
	/**
	 * このドキュメントにおける実行可能ファイルを取得する。
	 * このメソッドがファイルを返した場合、そのファイルは必ず存在している。
	 * @return	実行可能ファイルが存在していればそのファイルオブジェクトを返す。
	 * 			それ以外の場合は <tt>null</tt> を返す。
	 */
	public File getExecutableFile() {
		File jarFile = settings.getTargetFile();
		if (jarFile != null && jarFile.exists()) {
			return jarFile;
		} else {
			return null;
		}
	}
	
	/**
	 * このドキュメントがコンパイル可能か判定する。
	 * @return	このドキュメントがコンパイル可能ドキュメントなら <tt>true</tt> を返す。
	 */

	public boolean isCompilable() {
		return true;
	}

	public boolean isExecutable() {
		return true;
	}
	
	static public ProcessBuilder createCompileProcessBuilder(File sourcefile, CompileSettings compilesettings)
	{
		// create AADL jar module properties, and save to Temp file
		AadlJarProperties props = new AadlJarProperties(sourcefile, compilesettings);
		String strPropPath = null;
		try {
			File tempProp = props.saveToTempFile();
			strPropPath = tempProp.getAbsolutePath();
		}
		catch (IOException ex) {
			throw new IllegalStateException("Cannot create AADL Jar properties file.", ex);
		}
		
		// create command
		Vector<String> cmdList = new Vector<String>();
		//--- java command
		ExecutorFactory.addJavaCommandPath(cmdList,
				AppSettings.getInstance().getCurrentJavaCommandPath());
		//--- Max heap size
		//ExecutorFactory.addJavaMaxHeapSize(cmdList, AADLEditor.getMaxMemorySize());
		//--- java VM arguments
		//if (AADLEditor.isDebugEnabled()) {
		//	List<String> vmArgs = AppSettings.getInstance().getCompilerVMargsList();
		//	if (!vmArgs.isEmpty()) {
		//		cmdList.addAll(vmArgs);
		//	}
		//}
		//--- java VM arguments
		String maxMemorySize = AADLEditor.getMaxMemorySize();
		String strVmArgs = AppSettings.getInstance().appendCompilerVMargs(null);
		if (!Strings.isNullOrEmpty(maxMemorySize)) {
			ExecutorFactory.addJavaMaxHeapSize(cmdList, maxMemorySize);
			if (!Strings.isNullOrEmpty(strVmArgs)) {
				//--- ignore -Xmx???? and -Xms???? option
				String[] vmArgs = Strings.splitCommandLineWithDoubleQuoteEscape(strVmArgs);
				for (String vma : vmArgs) {
					if (!vma.startsWith("-Xmx") && !vma.startsWith("-Xms")) {
						cmdList.add(vma);
					}
				}
			}
		} else {
			ExecutorFactory.addJavaVMArgs(cmdList, strVmArgs);
		}
		
		// Check application settings
		//File javaCompiler = compilesettings.getTargetJavaCompilerFile();
		File javaCompiler = AppSettings.getInstance().getCurrentJavaCompilerFile();
		if (javaCompiler == null)
			throw new IllegalStateException("Java compiler file is null.");
		if (!javaCompiler.exists())
			throw new IllegalStateException("Java compiler file is not found : \"" + javaCompiler.getPath() + "\"");
		String[] cmpLibraries = AppSettings.getInstance().getCompileLibraries();
		for (String path : cmpLibraries) {
			if (Strings.isNullOrEmpty(path))
				throw new IllegalStateException("Class path for compile in Editor environment is illegal value.");
			if (!(new File(path)).exists())
				throw new IllegalStateException("Class path for compile in Editor environment is not found : \"" + path + "\"");
		}
		
		// ClassPath
		ClassPaths libPaths = new ClassPaths();
		libPaths.addPath(javaCompiler);
		libPaths.appendPaths(cmpLibraries);
		ExecutorFactory.addClassPath(cmdList, libPaths);
		
		// main class
		ExecutorFactory.addMainClassName(cmdList, "ssac.aadlc.Main");
		
		// Check user class paths
		File[] userClassPaths = compilesettings.getClassPathFiles();
		for (File file : userClassPaths) {
			if (file == null)
				throw new IllegalArgumentException("User class path file is null.");
			if (!file.exists())
				throw new IllegalArgumentException("User class path file is not found : \"" + file.getPath() + "\"");
		}
		
		// User class paths
		libPaths.clear();
		libPaths.appendPaths(userClassPaths);
		ExecutorFactory.addClassPath(cmdList, libPaths);
		
		// Options
		//--- debug
		if (AADLEditor.isDebugEnabled()) {
			cmdList.add("-debug");
		}
		//--- encoding
		//String strEncoding = compilesettings.getTargetEncodingName();
		String strEncoding = AppSettings.getInstance().getReadEncodingForAadlSource(compilesettings);
		if (!Strings.isNullOrEmpty(strEncoding)) {
			cmdList.add("-encoding");
			cmdList.add(strEncoding);
		}
		//--- destination
		String strDest = compilesettings.getDestinationPath();
		if (!Strings.isNullOrEmpty(strDest)) {
			cmdList.add("-d");
			cmdList.add(strDest);
		} else {
			// default destination
			cmdList.add("-d");
			//cmdList.add(srcModel.getDestinationAbsolutePath());
			cmdList.add(getDefaultDestinationFile(sourcefile).getName());
		}
		//--- source output dir
		File fSrcDir = compilesettings.getSourceOutputDirFile();
		if (fSrcDir != null && !Strings.isNullOrEmpty(fSrcDir.getPath())) {
			if (!fSrcDir.exists())
				throw new IllegalArgumentException("Source output directory(-sd) is not found : \"" + fSrcDir.getPath() + "\"");
			if (!fSrcDir.isDirectory())
				throw new IllegalArgumentException("Source output directory(-sd) is not directory : \"" + fSrcDir.getPath() + "\"");
			cmdList.add("-sd");
			cmdList.add(fSrcDir.getPath());
		}
		//--- no manifest
		if (compilesettings.isDisabledManifest()) {
			cmdList.add("-M");
		}
		//--- manifest
		File fManifest = compilesettings.getUserManifestFile();
		if (fManifest != null && !Strings.isNullOrEmpty(fManifest.getPath())) {
			if (!fManifest.exists())
				throw new IllegalArgumentException("Specified manifest file(-m) is not found : \"" + fManifest.getPath() + "\"");
			if (!fManifest.isFile())
				throw new IllegalArgumentException("Specified manifest file(-m) is not file : \"" + fManifest.getPath() + "\"");
			cmdList.add("-m");
			cmdList.add(fManifest.getPath());
		}
		//--- compile only
		if (compilesettings.isSpecifiedCompileOnly()) {
			cmdList.add("-c");
		}
		//--- no warn
		if (compilesettings.isDisabledWarning()) {
			cmdList.add("-nowarn");
		}
		//--- verbose
		if (compilesettings.isVerbose()) {
			cmdList.add("-verbose");
		}
		
		// set Jar properties option
		cmdList.add("-jarpropfile");
		cmdList.add(strPropPath);
		
		// source file
		cmdList.add(sourcefile.getAbsolutePath());
		
		// create ProcessBuilder
		ProcessBuilder builder = new ProcessBuilder(cmdList);
		builder.directory(sourcefile.getParentFile());
		
		// completed
		return builder;
	}

	public CommandExecutor createCompileExecutor()
	{
		// check exist target
		if (targetFile == null)
			throw new IllegalArgumentException("Target file is nothing.");
		if (!targetFile.exists())
			throw new IllegalArgumentException("Target file is not found : \"" + targetFile.getPath() + "\"");
		if (!targetFile.isFile())
			throw new IllegalArgumentException("Target is not file : \"" + targetFile.getPath() + "\"");
		
		// create AADL jar module properties, and save to Temp file
		AadlJarProperties props = new AadlJarProperties(targetFile, settings);
		String strPropPath = null;
		try {
			File tempProp = props.saveToTempFile();
			strPropPath = tempProp.getAbsolutePath();
		}
		catch (IOException ex) {
			throw new IllegalStateException("Cannot create AADL Jar properties file.", ex);
		}

		// create command
		Vector<String> cmdList = new Vector<String>();
		//--- java command
		ExecutorFactory.addJavaCommandPath(cmdList,
				AppSettings.getInstance().getCurrentJavaCommandPath());
		//--- Max heap size
		//ExecutorFactory.addJavaMaxHeapSize(cmdList, AADLEditor.getMaxMemorySize());
		//--- java VM arguments
		//if (AADLEditor.isDebugEnabled()) {
		//	List<String> vmArgs = AppSettings.getInstance().getCompilerVMargsList();
		//	if (!vmArgs.isEmpty()) {
		//		cmdList.addAll(vmArgs);
		//	}
		//}
		//--- java VM arguments
		String maxMemorySize = AADLEditor.getMaxMemorySize();
		String strVmArgs = AppSettings.getInstance().appendCompilerVMargs(null);
		if (!Strings.isNullOrEmpty(maxMemorySize)) {
			ExecutorFactory.addJavaMaxHeapSize(cmdList, maxMemorySize);
			if (!Strings.isNullOrEmpty(strVmArgs)) {
				//--- ignore -Xmx???? and -Xms???? option
				String[] vmArgs = Strings.splitCommandLineWithDoubleQuoteEscape(strVmArgs);
				for (String vma : vmArgs) {
					if (!vma.startsWith("-Xmx") && !vma.startsWith("-Xms")) {
						cmdList.add(vma);
					}
				}
			}
		} else {
			ExecutorFactory.addJavaVMArgs(cmdList, strVmArgs);
		}
		
		// Check application settings
		//File javaCompiler = settings.getTargetJavaCompilerFile();
		File javaCompiler = AppSettings.getInstance().getCurrentJavaCompilerFile();
		if (javaCompiler == null)
			throw new IllegalStateException("Java compiler file is null.");
		if (!javaCompiler.exists())
			throw new IllegalStateException("Java compiler file is not found : \"" + javaCompiler.getPath() + "\"");
		String[] cmpLibraries = AppSettings.getInstance().getCompileLibraries();
		for (String path : cmpLibraries) {
			if (Strings.isNullOrEmpty(path))
				throw new IllegalStateException("Class path for compile in Editor environment is illegal value.");
			if (!(new File(path)).exists())
				throw new IllegalStateException("Class path for compile in Editor environment is not found : \"" + path + "\"");
		}
		
		// ClassPath
		ClassPaths libPaths = new ClassPaths();
		libPaths.addPath(javaCompiler);
		libPaths.appendPaths(cmpLibraries);
		ExecutorFactory.addClassPath(cmdList, libPaths);
		
		// main class
		ExecutorFactory.addMainClassName(cmdList, "ssac.aadlc.Main");
		
		// Check user class paths
		File[] userClassPaths = settings.getClassPathFiles();
		for (File file : userClassPaths) {
			if (file == null)
				throw new IllegalArgumentException("User class path file is null.");
			if (!file.exists())
				throw new IllegalArgumentException("User class path file is not found : \"" + file.getPath() + "\"");
		}
		
		// User class paths
		libPaths.clear();
		libPaths.appendPaths(userClassPaths);
		ExecutorFactory.addClassPath(cmdList, libPaths);
		
		// Options
		//--- debug
		if (AADLEditor.isDebugEnabled()) {
			cmdList.add("-debug");
		}
		//--- encoding
		//String strEncoding = settings.getTargetEncodingName();
		String strEncoding = AppSettings.getInstance().getReadEncodingForAadlSource(settings);
		if (!Strings.isNullOrEmpty(strEncoding)) {
			cmdList.add("-encoding");
			cmdList.add(strEncoding);
		}
		//--- destination
		String strDest = settings.getDestinationPath();
		if (!Strings.isNullOrEmpty(strDest)) {
			cmdList.add("-d");
			cmdList.add(strDest);
		} else {
			// default destination
			cmdList.add("-d");
			//cmdList.add(srcModel.getDestinationAbsolutePath());
			cmdList.add(getDestinationFile().getName());
		}
		//--- source output dir
		File fSrcDir = settings.getSourceOutputDirFile();
		if (fSrcDir != null && !Strings.isNullOrEmpty(fSrcDir.getPath())) {
			if (!fSrcDir.exists())
				throw new IllegalArgumentException("Source output directory(-sd) is not found : \"" + fSrcDir.getPath() + "\"");
			if (!fSrcDir.isDirectory())
				throw new IllegalArgumentException("Source output directory(-sd) is not directory : \"" + fSrcDir.getPath() + "\"");
			cmdList.add("-sd");
			cmdList.add(fSrcDir.getPath());
		}
		//--- no manifest
		if (settings.isDisabledManifest()) {
			cmdList.add("-M");
		}
		//--- manifest
		File fManifest = settings.getUserManifestFile();
		if (fManifest != null && !Strings.isNullOrEmpty(fManifest.getPath())) {
			if (!fManifest.exists())
				throw new IllegalArgumentException("Specified manifest file(-m) is not found : \"" + fManifest.getPath() + "\"");
			if (!fManifest.isFile())
				throw new IllegalArgumentException("Specified manifest file(-m) is not file : \"" + fManifest.getPath() + "\"");
			cmdList.add("-m");
			cmdList.add(fManifest.getPath());
		}
		//--- compile only
		if (settings.isSpecifiedCompileOnly()) {
			cmdList.add("-c");
		}
		//--- no warn
		if (settings.isDisabledWarning()) {
			cmdList.add("-nowarn");
		}
		//--- verbose
		if (settings.isVerbose()) {
			cmdList.add("-verbose");
		}
		
		// set Jar properties option
		cmdList.add("-jarpropfile");
		cmdList.add(strPropPath);
		
		// source file
		cmdList.add(targetFile.getAbsolutePath());
		
		// create Executor
		CommandExecutor executor = new CommandExecutor(cmdList);
		
		// Working directory
		executor.setWorkDirectory(targetFile.getParentFile());
		
		// completed
		return executor;
	}

	public CommandExecutor createModuleExecutor() {
		return ExecutorFactory.createJarExecutor(settings);
	}

	public void onCompileFinished() {
		updateSettingsByCompileResult();
	}

	public void save(File targetFile) throws IOException {
		Validations.validNotNull(targetFile);
		saveDocument(targetFile);
		flgNew = false;
		setModifiedFlag(false);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	/**
	 * 指定されたファイルから、このドキュメントを初期化する。
	 * 
	 * @param srcFile	ドキュメントファイル
	 * @param encoding	ドキュメントファイル読み込み時に適用するファイルエンコーディング名。
	 * 					<tt>null</tt> の場合は、システム標準のエンコーディング名が適用される。
	 * @throws IOException	入出力エラーが発生した場合
	 */
	protected void initDocument(File srcFile, String encoding) throws IOException
	{
		// ファイルの有無を確認
		if (!srcFile.exists()) {
			throw new FileNotFoundException(srcFile.getAbsolutePath());
		}
		
		// 設定情報の更新
		this.targetFile = srcFile;
		this.settings.loadForTarget(srcFile);
		updateLastModifiedTimeWhenLoadingTargetFile();
		//String strEncoding = settings.getTargetEncodingName();
		String strEncoding = encoding;
		if (Strings.isNullOrEmpty(encoding)) {
			strEncoding = AppSettings.getInstance().getReadEncodingForAadlSource(settings);
		}
		
		// ソース読み込み
		InputStreamReader isr = null;
		BufferedReader br = null;
		try {
			//--- open
			if (Strings.isNullOrEmpty(strEncoding)) {
				// デフォルトエンコード
				_lastEncoding = JEncodingComboBox.getDefaultEncoding();
				isr = new InputStreamReader(new FileInputStream(srcFile));
			} else {
				// 指定のエンコード
				_lastEncoding = strEncoding;
				isr = new InputStreamReader(new FileInputStream(srcFile), strEncoding);
			}
			br = new BufferedReader(isr);
			//--- read
			EditorKit kit = new DefaultEditorKit();
			try {
				kit.read(br, this, 0);
			}
			catch (BadLocationException ex) {
				throw new IOException(ex.getMessage());
			}
		}
		finally {
			if (br != null) {
				Files.closeStream(br);
				br = null;
			}
			if (isr != null) {
				Files.closeStream(isr);
				isr = null;
			}
		}
	}

	/**
	 * 指定されたファイルにドキュメントを保存する。ファイルがすでに存在している場合は、
	 * このドキュメントの内容で上書きする。
	 * @param targetFile	保存先ファイル
	 * @throws IOException	入出力エラーが発生した場合
	 */
	protected void saveDocument(File targetFile) throws IOException
	{
		// 設定情報の取得
		//String strEncoding = settings.getTargetEncodingName();
		String strEncoding = AppSettings.getInstance().getWriteEncodingForAadlSource(settings);
		
		// ソース書き出し
		OutputStreamWriter osw = null;
		BufferedWriter bw = null;
		try {
			//--- open
			if (!Strings.isNullOrEmpty(strEncoding)) {
				// 指定のエンコード
				osw = new OutputStreamWriter(new FileOutputStream(targetFile), strEncoding);
				_lastEncoding = strEncoding;
			} else {
				// デフォルトエンコード
				osw = new OutputStreamWriter(new FileOutputStream(targetFile));
				_lastEncoding = JEncodingComboBox.getDefaultEncoding();
			}
			bw = new BufferedWriter(osw);
			//--- write
			EditorKit kit = new DefaultEditorKit();
			try {
				kit.write(bw, this, 0, this.getLength());
			}
			catch (BadLocationException ex) {
				throw new IOException(ex.getMessage());
			}
		}
		finally {
			if (bw != null) {
				Files.closeStream(bw);
				bw = null;
			}
			if (osw != null) {
				Files.closeStream(osw);
				osw = null;
			}
			if (Objects.isEqual(targetFile, this.targetFile)) {
				updateLastModifiedTimeWhenLoadingTargetFile();
			}
		}
		
		// 設定情報の永続化
		//if (!targetFile.equals(this.targetFile)) {
		//	// ターゲットと異なる場合のみ永続化
		//	this.targetFile = targetFile;
		//	this.settings.saveForTarget(targetFile);
		//}
		if (!Objects.isEqual(targetFile, this.targetFile)
			|| !Objects.isEqual(strEncoding, settings.getLastEncodingName()))
		{
			// 保存結果と設定内容が異なる場合のみ、設定情報を永続化
			this.targetFile = targetFile;
			updateLastModifiedTimeWhenLoadingTargetFile();
			this.settings.setLastEncodingName(strEncoding);
			this.settings.saveForTarget(targetFile);
		}
	}

	/**
	 * ソースファイルが存在するディレクトリの絶対パスを示す
	 * ファイルオブジェクトを取得する。
	 * 
	 * @return	ディレクトリを示す <code>File</code> オブジェクトを返す。
	 * 			ファイルに保存されていない新規ドキュメントの場合、
	 * 			現在のカレントディレクトリとなる。
	 */
	protected File getParentDirectory() {
		if (targetFile != null) {
			return targetFile.getParentFile();
		} else {
			return (new File("")).getAbsoluteFile();
		}
	}
	
	static protected File getDefaultDestinationFile(File sourcefile) {
		String strName = sourcefile.getAbsolutePath();
		if (Strings.endsWithIgnoreCase(strName, SourceMessages.getInstance().extSource)) {
			strName = Files.removeExtension(strName);
		}
		strName = Files.addExtension(strName, ".jar");
		return new File(strName);
	}

	/**
	 * コンパイル設定の出力先ファイルの絶対パスを示すファイルオブジェクトを取得する。
	 * このメソッドでは、出力先ファイル設定が相対パスの場合、ソースファイルの位置からの
	 * 相対パスとして絶対パスを生成する。
	 * 出力先ファイルが設定されていない場合、ソースファイルの拡張子 <code>&quot;.aadl&quot;</code> を
	 * 実行モジュール拡張子 <code>&quot;.jar&quot;</code> に変換したパスとなる。
	 * 
	 * @return	出力先実行ファイルの絶対パスを示す <code>File</code> オブジェクトを返す。
	 * 			ファイルに保存されていない新規ドキュメントの場合は <tt>null</tt> を返す。
	 */
	protected File getDestinationFile() {
		File dstFile = null;
		if (targetFile != null) {
			dstFile = settings.getDestinationFile();
			if (dstFile != null) {
				// 指定の出力ファイル
				if (!dstFile.isAbsolute()) {
					File file = new File(getParentDirectory(), dstFile.getPath());
					dstFile = Files.normalizeFile(file);
				}
			}
			else {
				String strName = targetFile.getAbsolutePath();
				if (Strings.endsWithIgnoreCase(strName, SourceMessages.getInstance().extSource)) {
					strName = Files.removeExtension(strName);
				}
				strName = Files.addExtension(strName, ".jar");
				dstFile = new File(strName);
			}
		}
		return dstFile;
	}
	
	static protected File getDestinationFile(File srcFile, CompileSettings compilesettings) {
		File dstFile = null;
		if (srcFile != null) {
			dstFile = compilesettings.getDestinationFile();
			if (dstFile != null) {
				// 指定の出力ファイル
				if (!dstFile.isAbsolute()) {
					File file = new File(srcFile.getParentFile(), dstFile.getPath());
					dstFile = Files.normalizeFile(file);
				}
			}
			else {
				String strName = srcFile.getAbsolutePath();
				if (Strings.endsWithIgnoreCase(strName, SourceMessages.getInstance().extSource)) {
					strName = Files.removeExtension(strName);
				}
				strName = Files.addExtension(strName, ".jar");
				dstFile = new File(strName);
			}
		}
		return dstFile;
	}

	/**
	 * 現在の設定情報から、実行対象 JAR ファイル情報を更新する。
	 * 設定が有効な場合は、更新しない。
	 * 
	 * @return	情報が更新された場合に <tt>true</tt> を返す。
	 */
	protected boolean updateSettingsByCompileResult() {
		/*---
		File fileDestJar = getDestinationFile();
		if (fileDestJar == null) {
			// 更新不可
			return false;
		}
		
		// 実行時ライブラリのクラスパス取得
		ClassPaths pathList = new ClassPaths();
		pathList.appendPaths(settings.getClassPathFiles());
		pathList.appendPaths(AppSettings.getInstance().getExecLibraries());
		String libClassPaths = pathList.getClassPathString();
		
		// Jar ファイル情報を取得
		String[] strMainClasses;
		String strMainClassName;
		try {
			JarFileInfo jfi = new JarFileInfo(fileDestJar, libClassPaths);
			strMainClasses = jfi.mainClasses();
			strMainClassName = jfi.getManifestMainClass();
		}
		catch (Throwable ex) {
			AppLogger.debug(ex);
			strMainClasses = null;
			strMainClassName = null;
		}
		if (strMainClasses == null) {
			// 更新不可
			return false;
		}
		
		// 実行対象メインクラスの検証
		String strTargetMainClass = settings.getTargetMainClass();
		if (!Strings.isNullOrEmpty(strTargetMainClass)) {
			boolean isExistMainClass = false;
			for (String cname : strMainClasses) {
				if (cname.equals(strTargetMainClass)) {
					// 選択済み実行クラスあり
					isExistMainClass = true;
					break;
				}
			}
			if (!isExistMainClass) {
				// 実行クラスが指定されていない場合は、マニフェストのメインクラスを適用
				strTargetMainClass = strMainClassName;
			}
		}
		else {
			// 実行クラスが指定されていない場合は、マニフェストのメインクラスを適用
			strTargetMainClass = strMainClassName;
		}
		
		// 設定情報を更新
		settings.setTargetFile(fileDestJar.getAbsoluteFile());
		settings.setTargetMainClass(strTargetMainClass);
		settings.incrementNextRevision();
		//--- 変更を保存
		try {
			settings.commit();
		}
		catch (Throwable ex) {
			AppLogger.debug(ex);
		}
		return true;
		---*/
		return updateSettingsByCompileSucceeded(targetFile, settings);
	}
	
	static public boolean updateSettingsByCompileSucceeded(File srcFile, CompileSettings compilesettings)
	{
		File fileDestJar = getDestinationFile(srcFile, compilesettings);
		if (fileDestJar == null) {
			// 更新不可
			return false;
		}
		
		// 実行時ライブラリのクラスパス取得
		ClassPaths pathList = new ClassPaths();
		pathList.appendPaths(compilesettings.getClassPathFiles());
		pathList.appendPaths(AppSettings.getInstance().getExecLibraries());
		String libClassPaths = pathList.getClassPathString();
		
		// Jar ファイル情報を取得
		String[] strMainClasses;
		String strMainClassName;
		try {
			JarFileInfo jfi = new JarFileInfo(fileDestJar, libClassPaths);
			strMainClasses = jfi.mainClasses();
			strMainClassName = jfi.getManifestMainClass();
		}
		catch (Throwable ex) {
			AppLogger.debug(ex);
			strMainClasses = null;
			strMainClassName = null;
		}
		if (strMainClasses == null) {
			// 更新不可
			return false;
		}
		
		// 実行対象メインクラスの検証
		String strTargetMainClass = compilesettings.getTargetMainClass();
		if (!Strings.isNullOrEmpty(strTargetMainClass)) {
			boolean isExistMainClass = false;
			for (String cname : strMainClasses) {
				if (cname.equals(strTargetMainClass)) {
					// 選択済み実行クラスあり
					isExistMainClass = true;
					break;
				}
			}
			if (!isExistMainClass) {
				// 実行クラスが指定されていない場合は、マニフェストのメインクラスを適用
				strTargetMainClass = strMainClassName;
			}
		}
		else {
			// 実行クラスが指定されていない場合は、マニフェストのメインクラスを適用
			strTargetMainClass = strMainClassName;
		}
		
		// 設定情報を更新
		compilesettings.setTargetFile(fileDestJar.getAbsoluteFile());
		compilesettings.setTargetMainClass(strTargetMainClass);
		compilesettings.incrementNextRevision();
		//--- 変更を保存
		try {
			compilesettings.commit();
		}
		catch (Throwable ex) {
			AppLogger.debug(ex);
		}
		return true;
	}
}
