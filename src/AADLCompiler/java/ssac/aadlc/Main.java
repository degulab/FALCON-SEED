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
 * @(#)Main.java	1.00	2007/11/29
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadlc;

import java.io.PrintStream;
import java.io.PrintWriter;

import org.antlr.runtime.tree.CommonTree;

import ssac.aadlc.analysis.AADLAnalyzer;
import ssac.aadlc.compile.CommandLineArgs;
import ssac.aadlc.compile.JavaCompiler;
import ssac.aadlc.compile.JavaPackager;
import ssac.aadlc.compile.Project;
import ssac.aadlc.io.ReportPrinter;
import ssac.aadlc.io.ReportStream;
import ssac.aadlc.io.ReportWriter;
import ssac.aadlc.io.Reporter;


/**
 * AADL コンパイラー・メインモジュール
 * 
 * @version 1.00	2007/11/29
 *
 */
public class Main
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	static public int EXITCODE_SUCCEEDED = 0;
	static public int EXITCODE_FAILED_ARGS = 1;
	static public int EXITCODE_SYNTAX_ERROR = 2;
	static public int EXITCODE_FAILED_JAVA_COMPILE = 3;
	static public int EXITCODE_FAILED_JAR_PACKAGE = 4;
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private Project project = null;
	
	private final Reporter out;			// 標準出力オブジェクト
	private final Reporter err;			// エラー出力オブジェクト

	//------------------------------------------------------------
	// Static interfaces
	//------------------------------------------------------------

	// Entry point
	public static void main(String[] args) {
		Main compiler = new Main();
		int retCode = compiler.compile(args);
		compiler.out.info().println();
		System.exit(retCode);
	}

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public Main() {
		this((ReportPrinter)null, (ReportPrinter)null);
	}
	
	public Main(PrintWriter ow) {
		this(ow != null ? new ReportWriter(ow) : null);
	}
	
	public Main(PrintWriter ow, PrintWriter ew) {
		this(ow != null ? new ReportWriter(ow) : null, ew != null ? new ReportWriter(ew) : null);
	}
	
	public Main(PrintStream os) {
		this(os != null ? new ReportStream(os) : null);
	}
	
	public Main(PrintStream os, PrintStream es) {
		this(os != null ? new ReportStream(os) : null, es != null ? new ReportStream(es) : null);
	}
	
	protected Main(ReportPrinter outPrinter) {
		this(outPrinter, outPrinter);
	}
	
	protected Main(ReportPrinter outPrinter, ReportPrinter errPrinter) {
		//--- 標準出力
		if (outPrinter == null)
			this.out = new Reporter(new ReportStream(System.out));
		else
			this.out = new Reporter(outPrinter);
		//--- エラー出力
		if (errPrinter == null)
			this.err = new Reporter(new ReportStream(System.err));
		else
			this.err = new Reporter(errPrinter);
		//--- 初期化
		this.out.setDebugHeader(AADLMessage.DEBUG_HEADER);
		this.err.setDebugHeader(AADLMessage.DEBUG_HEADER);
		this.out.setWarningHeader(AADLMessage.WARN_HEADER);
		this.err.setWarningHeader(AADLMessage.WARN_HEADER);
		this.out.setErrorHeader(AADLMessage.ERROR_HEADER);
		this.err.setErrorHeader(AADLMessage.ERROR_HEADER);
	}

	//------------------------------------------------------------
	// Getter
	//------------------------------------------------------------
	
	public CommandLineArgs getCommandLineArgs() {
		if (this.project != null)
			return this.project.getCommandLineArgs();
		else
			return null;
	}
	
	public CommonTree getTree() {
		if (this.project != null)
			return this.project.getTree();
		else
			return null;
	}
	
	public AADLAnalyzer getAnalyzer() {
		if (this.project != null)
			return this.project.getAnalyzer();
		else
			return null;
	}
	
	public Project getProject() {
		return this.project;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public int compile(String[] args) {
		// 初期化
		this.project = new Project(this.out, this.err);
		
		// コマンドライン解析
		int retCode = this.project.parseCommandLine(args);
		if (retCode > 0) {
			return EXITCODE_SUCCEEDED;
		}
		else if (retCode < 0) {
			return EXITCODE_FAILED_ARGS;
		}
		//--- update reporter
		updateReporter(this.project.getCommandLineArgs());

		try {
			// Syntax check
			out.tracePrintln("\n===== Phase : Syntax check =====");
			if (!this.project.parseAADLSource())
				return EXITCODE_SYNTAX_ERROR;
		
			// Java compile
			out.tracePrintln("\n===== Phase : JAVA code compile =====");
			if (!this.project.setupJavaSource())
				return EXITCODE_FAILED_JAVA_COMPILE;
			JavaCompiler javac = new JavaCompiler(this.project);
			int retJavac = javac.compile();
			if (retJavac != 0)
				return EXITCODE_FAILED_JAVA_COMPILE;
		
			// check syntax check only
			if (this.project.getCommandLineArgs().isCheckOnly())
			{
				out.tracePrintln("\n===== Syntax OK! =====");
				return EXITCODE_SUCCEEDED;
			}
		
			// Jar packaging
			out.tracePrintln("\n===== Phase : Jar packaging =====");
			JavaPackager jar = new JavaPackager(this.project);
			if (!jar.pack())
				return EXITCODE_FAILED_JAR_PACKAGE;
		}
		finally {
			// テンポラリの破棄
			this.project.cleanup();
		}
		
		// 成功
		out.tracePrintln("\n===== All succeeded! =====");
		return EXITCODE_SUCCEEDED;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	// report print update by Command line arguments
	private void updateReporter(CommandLineArgs cmdArgs) {
		//--- debug
		out.setDebugEnable(cmdArgs.isDebug());
		err.setDebugEnable(cmdArgs.isDebug());
		//--- verbose
		out.setTraceEnable(cmdArgs.isVerbose());
		err.setTraceEnable(cmdArgs.isVerbose());
		//--- warning
		out.setWarningEnable(!cmdArgs.isNoWarn());
		err.setWarningEnable(!cmdArgs.isNoWarn());
	}

	//------------------------------------------------------------
	// Helper methods
	//------------------------------------------------------------
	
}
