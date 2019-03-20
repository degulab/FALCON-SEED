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
 *  Copyright 2007-2012  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)Project.java	1.81	2012/10/05 - Bug fix
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)Project.java	1.30	2009/12/02
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)Project.java	1.11	2008/06/19 - bug fix
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)Project.java	1.10	2008/05/20
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)Project.java	1.00	2007/11/29
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadlc.compile;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.TokenRewriteStream;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeNodeStream;
import org.antlr.runtime.tree.RewriteEmptyStreamException;

import ssac.aadlc.AADLMessage;
import ssac.aadlc.analysis.AADLAnalyzer;
import ssac.aadlc.core.AADLLexer;
import ssac.aadlc.core.AADLParser;
import ssac.aadlc.core.AADLWalker;
import ssac.aadlc.io.FileUtil;
import ssac.aadlc.io.Reporter;

/**
 * AADL プロジェクト
 *
 * 
 * @version 1.81	2012/10/05
 */
public class Project
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	static private final String WORK_DIR_PREFIX = "aadlc";
	static private final String WORK_DIR_SUFFIX = "wrk";
	
	static private final String JAR_SUFFIX = ".jar";

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	protected final Reporter	out;			// メッセージ出力先
	protected final Reporter	err;			// エラーメッセージ出力先
	
	protected final CommandLineArgs cmdArgs;	// コマンドライン引数情報
	
	private CommonTree		astTree;			// AADL解析木
	private AADLAnalyzer	analyzer;			// AADL Analyzer
	
	private String			mainClassName;		// Mainクラスの完全名(パッケージ含む)
	
	private File			dirWork;			// 作業用ディレクトリ
	private File			dirSource;			// ソースファイル用ディレクトリ
	private File			dirClasses;			// クラスファイル用ディレクトリ

	private File			fileSource;			// JAVAソースファイル
	private File			fileBaseClass;		// ベースクラスファイル
	private File			fileManifest;		// マニフェストファイル
	private File			fileJar;			// Jar ファイル
	private File			fileProp;			// Jar properties file
	
	private File			fileAADL;			// AADLソースファイル

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	public Project(Reporter outReporter, Reporter errReporter) {
		//--- cmdArgs
		this.cmdArgs = new CommandLineArgs();
		//--- out
		if (outReporter == null)
			throw new NullPointerException("outReporter");
		this.out = outReporter;
		//--- err
		if (errReporter == null)
			throw new NullPointerException("errReporter");
		this.err = errReporter;
	}

	//------------------------------------------------------------
	// Getter / Setter
	//------------------------------------------------------------
	
	public CommandLineArgs getCommandLineArgs() {
		return this.cmdArgs;
	}
	
	public CommonTree getTree() {
		return this.astTree;
	}
	
	public AADLAnalyzer getAnalyzer() {
		return this.analyzer;
	}
	
	public File getWorkDirectory() {
		return this.dirWork;
	}
	
	public File getSourceDirectory() {
		return this.dirSource;
	}
	
	public File getClassesDirectory() {
		return this.dirClasses;
	}
	
	public File getJavaSourceFile() {
		return this.fileSource;
	}
	
	public File getAADLSourceFile() {
		return this.fileAADL;
	}
	
	public File getBaseClassFile() {
		return this.fileBaseClass;
	}
	
	public File getManifestFile() {
		return this.fileManifest;
	}
	
	public File getDestinationFile() {
		return this.fileJar;
	}
	
	public File getJarPropertiesFile() {
		return this.fileProp;
	}
	
	public boolean hasMainClassName() {
		return (this.mainClassName != null);
	}
	
	public String getMainClassName() {
		return this.mainClassName;
	}
	
	protected void setMainClassName(String name) {
		this.mainClassName = name;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public void cleanup() {
		if (this.dirWork != null) {
			try {
				recursiveDelete(this.dirWork);
			}
			catch (Exception ex) {
				;	// 処理なし
			}
		}
	}

	/**
	 * コマンドライン引数を解析する
	 * 
	 * @return 正常=0、正常だが継続の必要がない場合は正の値、エラーの場合は負の値
	 * 
	 * @param args コマンドライン引数
	 */
	public int parseCommandLine(String[] args) {
		try {
			cmdArgs.parse(args);
		}
		catch (IllegalArgumentException ex) {
			//@@@ added by Y.Ishziuka : 2008/06/19
			if (cmdArgs.isVersion()) {
				CommandLineArgs.showVersion(out.info());
			}
			//@@@ end of added.
			err.errorPrintln(ex.getMessage());
			return (-1);
		}
		//--- show version
		if (cmdArgs.isVersion()) {
			CommandLineArgs.showVersion(out.info());
			/*@@@ deleted by Y.Ishizuka : 2008/05/20
			return 1;
			**@@@ end of deleted. */
		}
		//--- show help
		if (cmdArgs.isHelp()) {
			CommandLineArgs.showHelp(out.info());
			return 1;
		}

		// 正常終了
		return 0;
	}

	/**
	 * AADLソースファイルを解析する
	 * 
	 * @return 正常=true、エラー=false
	 */
	public boolean parseAADLSource() {
		String srcFile = cmdArgs.getAADLFilename();
		
		// create stream
		ANTLRInputStream antlrStream = null;
		try {
			this.fileAADL = new File(srcFile);
			FileInputStream fis = new FileInputStream(this.fileAADL);
			if (cmdArgs.hasEncoding())
				antlrStream = new ANTLRInputStream(fis, cmdArgs.getEncoding());
			else
				antlrStream = new ANTLRInputStream(fis);
		}
		catch (FileNotFoundException ex) {
			// File not found
			err.errorPrintln("'%s' file not found.", srcFile);
			err.debugPrintStackTrace(ex);
			antlrStream = null;
		}
		catch (UnsupportedEncodingException ex) {
			// Unsupported encoding
			err.errorPrintln("Unsupported '%s' encoding.", cmdArgs.getEncoding());
			err.debugPrintStackTrace(ex);
			antlrStream = null;
		}
		catch (IOException ex) {
			// I/O error
			err.errorPrintln("Cannot read '%s'.\n[%s]", srcFile, ex.getMessage());
			err.debugPrintStackTrace(ex);
			antlrStream = null;
		}
		if (antlrStream == null)
			return false;
		
		// parse by grammar
		if (!parseAADLGrammar(antlrStream))
			return false;
		
		// parse Tree
		if (!parseAADLTree(this.astTree))
			return false;
		
		// Completed!
		return true;
	}

	/**
	 * JAVA ソースコードを準備する
	 * 
	 * @return 正常=true、エラー=false
	 */
	public boolean setupJavaSource() {
		// setup working space
		if (!setupWorkingSpace())
			return false;
		
		// output java sources
		if (!outputJavaSource())
			return false;
		
		// setup file information(jar)
		setupFileInfo();
		
		// Completed!
		return true;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	private void setupFileInfo() {
		// manifest
		if (cmdArgs.hasManifest()) {
			this.fileManifest = new File(cmdArgs.getManifest());
		}
		
		// Jar properties file
		if (cmdArgs.hasJarPropertiesPath()) {
			this.fileProp = new File(cmdArgs.getJarPropertiesPath());
		}
		
		// jar
		String destname;
		if (cmdArgs.hasDestFilename()) {
			destname = cmdArgs.getDestFilename();
			String ext = destname.substring(destname.length() - 4);
			if (!ext.equalsIgnoreCase(JAR_SUFFIX)) {
				destname += JAR_SUFFIX;
			}
		}
		else {
			destname = analyzer.getAadlClassName() + JAR_SUFFIX;
		}
		this.fileJar = new File(destname);
	}
	
	private boolean parseAADLGrammar(ANTLRInputStream inStream) {
		out.tracePrintln("[%s] check the Grammar of AADL...", cmdArgs.getAADLFilename());
		boolean ret;
		try {
			// Lexer
			AADLLexer lexer = new AADLLexer(inStream);
			lexer.setOutputWriter(out.info());
			lexer.setErrorWriter(err.info());
			lexer.setSourceFileName(this.fileAADL.getAbsolutePath());
			TokenRewriteStream tokens = new TokenRewriteStream(lexer);
			
			// Parser(AST construction)
			AADLParser parser = new AADLParser(tokens);
			parser.setOutputWriter(out.info());
			parser.setErrorWriter(err.info());
			parser.setSourceFileName(this.fileAADL.getAbsolutePath());
			AADLParser.program_return result = parser.program();
			if (!lexer.hasError() && !parser.hasError()) {
				astTree = (CommonTree)result.getTree();
				ret = true;
			}
			else {
				ret = false;
			}
		}
		catch (RewriteEmptyStreamException ex) {
			ret = false;
			String msg = AADLMessage.printException(ex);
			err.errorPrintln(msg);
			err.debugPrintStackTrace(ex);
		}
		catch (RecognitionException ex) {
			ret = false;
			String msg = AADLMessage.printException(ex);
			err.errorPrintln(msg);
			err.debugPrintStackTrace(ex);
		}
		catch (Exception ex) {
			ret = false;
			String msg = AADLMessage.printException(ex);
			err.errorPrintln(msg);
			err.debugPrintStackTrace(ex);
		}
		//--- verbose
		if (ret)
			out.tracePrintln(".....OK!");
		else
			out.tracePrintln(".....NG");
		//--- for Debug
		if (out.isDebugEnabled()) {
			out.debugPrintln("@@@@@ debug : show tree by AADL grammer parse. @@@@@");
			if (astTree != null) {
				out.debugPrintln(astTree.toStringTree().replaceAll("%", "%%"));
			} else {
				out.debugPrintln("    Tree is nothing!");
			}
			out.debugPrintln("@@@@@ debug : show tree by AADL grammer parse. @@@@@");
		}
		//--- end of Debug
		return ret;
	}
	
	private boolean parseAADLTree(CommonTree astTree) {
		out.tracePrintln("[%s] check the detail of AADL analysis tree...", cmdArgs.getAADLFilename());
		boolean ret;
		try {
			// analyz AST
			CommonTreeNodeStream nodes = new CommonTreeNodeStream(astTree);
			AADLWalker walker = new AADLWalker(nodes);
			walker.setOutputWriter(out.info());
			walker.setErrorWriter(err.info());
			walker.setSourceFileName(this.fileAADL.getAbsolutePath());
			walker.program();
			if (!walker.hasError()) {
				analyzer = walker.getAnalyzer();
				ret = true;
			}
			else {
				ret = false;
			}
		}
		catch (Exception ex) {
			ret = false;
			String msg = AADLMessage.printException(ex);
			err.errorPrintln(msg);
			err.debugPrintStackTrace(ex);
		}
		//--- verbose
		if (ret)
			out.tracePrintln(".....OK!");
		else
			out.tracePrintln(".....NG");
		return ret;
	}
	
	private boolean setupWorkingSpace() {
		boolean ret;
		try {
			// 作業用ディレクトリを生成
			out.tracePrintln("setup working directories...");
			this.dirWork = File.createTempFile(WORK_DIR_PREFIX, WORK_DIR_SUFFIX);
			deleteFile(this.dirWork);
			makeDirectory(this.dirWork);
			
			// ソースディレクトリを生成
			if (cmdArgs.hasSourceDestPath()) {
				this.dirSource = new File(cmdArgs.getSourceDestPath());
			}
			else {
				this.dirSource = new File(this.dirWork, "src");
				makeDirectory(this.dirSource);
			}
			
			// クラスファイルディレクトリを生成
			this.dirClasses = new File(this.dirWork, "classes");
			makeDirectory(this.dirClasses);
			
			// Completed
			ret = true;
		}
		catch (Exception ex) {
			ret = false;
			String msg = AADLMessage.printException(ex);
			err.errorPrintln(msg);
			err.debugPrintStackTrace(ex);
		}
		//--- verbose
		if (ret)
			out.tracePrintln(".....OK!");
		else
			out.tracePrintln(".....NG");
		return ret;
	}
	
	private boolean outputJavaSource() {
		boolean ret;
		String srcFileName = analyzer.getAadlClassName() + ".java";
		this.fileSource = new File(this.dirSource, srcFileName);
		out.tracePrintln("Output Java source to '%s'...", this.fileSource.getAbsolutePath());
		try {
			//--- setup stream
			FileOutputStream fos = null;
			OutputStreamWriter osw = null;
			BufferedWriter bw = null;
			try {
				// setup stream
				fos = new FileOutputStream(this.fileSource);
				if (cmdArgs.hasEncoding())
					osw = new OutputStreamWriter(fos, cmdArgs.getEncoding());
				else
					osw = new OutputStreamWriter(fos);
				bw = new BufferedWriter(osw);
				
				// output source file
				analyzer.getJavaProgram().output(bw);
				
				// Completed!
				ret = true;
			}
			finally {
				if (bw != null)
					FileUtil.closeStream(bw);
				if (osw != null)
					FileUtil.closeStream(osw);
				if (fos != null)
					FileUtil.closeStream(fos);
			}
		}
		catch (FileNotFoundException ex) {
			ret = false;
			err.errorPrintln("'%s' file not found.", this.fileSource.getAbsolutePath());
			err.debugPrintStackTrace(ex);
		}
		catch (UnsupportedEncodingException ex) {
			ret = false;
			err.errorPrintln("'%s' is not supported.", cmdArgs.getEncoding());
			err.debugPrintStackTrace(ex);
		}
		catch (IOException ex) {
			ret = false;
			err.errorPrintln("Cannot to write Java source to '%s'.", this.fileSource.getAbsolutePath());
			err.debugPrintStackTrace(ex);
		}
		catch (Exception ex) {
			ret = false;
			String msg = AADLMessage.printException(ex);
			err.errorPrintln(msg);
			err.debugPrintStackTrace(ex);
		}
		//--- verbose
		if (ret)
			out.tracePrintln(".....OK!");
		else
			out.tracePrintln(".....NG");
		return ret;
	}

	//------------------------------------------------------------
	// Internal helper
	//------------------------------------------------------------
	
	static private void recursiveDelete(File rootDir)
		throws IOException
	{
		// delete all file in rootDir
		File[] files = rootDir.listFiles();
		for (File tgt : files) {
			if (tgt.isDirectory()) {
				recursiveDelete(tgt);
			}
			else {
				deleteFile(tgt);
			}
		}
		
		// delete rootDir
		deleteFile(rootDir);
	}
	
	static private void deleteFile(File targetFile)
		throws IOException
	{
		final String msgFormat = "Could not delete : \"%s\"";
		
		boolean retDelete;
		try {
			retDelete = targetFile.delete();
		}
		catch (Exception ex) {
			throw new IOException(String.format(msgFormat, targetFile.getAbsolutePath()));
		}
		
		if (!retDelete) {
			throw new IOException(String.format(msgFormat, targetFile.getAbsolutePath()));
		}
	}
	
	static private void makeDirectory(File targetDir)
		throws IOException
	{
		final String msgFormat = "Unable to create temporary directory : \"%s\"";
		
		boolean retMake;
		try {
			retMake = targetDir.mkdirs();
		}
		catch (Exception ex) {
			throw new IOException(String.format(msgFormat, targetDir.getAbsolutePath()));
		}
		
		if (!retMake) {
			throw new IOException(String.format(msgFormat, targetDir.getAbsolutePath()));
		}
	}

}
