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
 * @(#)JavaCompiler.java	1.81	2012/10/05
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)JavaCompiler.java	1.30	2009/12/02
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)JavaCompiler.java	1.00	2007/11/29
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadlc.compile;

import java.io.BufferedReader;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ssac.aadlc.AADLMessage;
import ssac.aadlc.io.FileUtil;

/**
 * JAVA コンパイラー(1.5以上)
 *
 * 
 * @version 1.81	2012/10/05
 */
public class JavaCompiler
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	static private final String JAVACLASS_SUFFIX = ".class";

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private final Project	project;		// AADL プロジェクト

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	public JavaCompiler(Project target) {
		if (target == null)
			throw new NullPointerException();
		this.project = target;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public int compile() {
		project.out.tracePrintln("@@@@@ start java compile...");
		
		// setup message buffer
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		
		// make compile options
		String[] options = makeCompileOptions();
		
		// Execute javac
		int result;
        try {
            Class<?> c = Class.forName ("com.sun.tools.javac.Main");
            Object compiler = c.newInstance ();
            Method compile = c.getMethod ("compile", String[].class, PrintWriter.class);
            {
            	project.out.tracePrint("*> exec com.sun.tools.javac.Main.compile(");
            	if (options.length > 0) {
            		project.out.trace().print(options[0]);
            		for (int i = 1; i < options.length; i++) {
            			project.out.trace().print(",");
            			project.out.trace().print(options[i]);
            		}
            		project.out.trace().println(")");
            	}
            }
            result = ((Integer) compile.invoke(compiler, options, pw)).intValue();
        } catch (Exception ex) {
        	project.err.errorPrintln("Failed to execute com.sun.tools.javac.Main#compile method.");
        	String msg;
        	if (ex instanceof InvocationTargetException) {
        		Throwable innerEx = ((InvocationTargetException)ex).getCause();
        		msg = "(InvocationTargetException)" + AADLMessage.printException(innerEx);
        	}
        	else {
        		msg = AADLMessage.printException(ex);
        	}
			project.err.errorPrintln(msg);
			project.err.debugPrintStackTrace(ex);
			result = (-1);
        }
        //--- javac result message
        outputJavaCompilerMessages(sw.toString());
        
        // find AADL Main class name
        try {
        	searchMainClassPath();
        }
        catch (Exception ex) {
    		String msg = AADLMessage.printException(ex);
			project.err.debugPrintln(msg);
			project.err.debugPrintStackTrace(ex);
        }
        
		//--- verbose
		if (result == 0)
			project.out.tracePrintln(".....OK!");
		else
			project.out.tracePrintln(".....NG");
		return result;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	private String[] makeCompileOptions() {
		CommandLineArgs cmdArgs = project.getCommandLineArgs();
		ArrayList<String> options = new ArrayList<String>();
		//--- source path
		options.add("-sourcepath");
		options.add(project.getSourceDirectory().getAbsolutePath());
		//--- class path
		if (cmdArgs.hasClassPath()) {
			options.add("-classpath");
			options.add(cmdArgs.getClassPath());
		}
		//--- dest dir
		options.add("-d");
		options.add(project.getClassesDirectory().getAbsolutePath());
		//--- deprecation
		options.add("-deprecation");
		// TODO: ベース Java version の指定
		//--- source version
		options.add("-source");
		options.add("1.5");
		//--- target version
		options.add("-target");
		options.add("1.5");
		//--- debug option
		options.add("-g:source,lines");
		//--- encoding
		if (cmdArgs.hasEncoding()) {
			options.add("-encoding");
			options.add(cmdArgs.getEncoding());
		}
		//--- verbose
		if (cmdArgs.isVerbose()) {
			options.add("-verbose");
		}
		//--- nowarn
		if (cmdArgs.isNoWarn()) {
			options.add("-nowarn");
		}
		// TODO: JDK 1.7 コンパイラでの下位バージョンコンパイルに対する警告への対処
		//--- ignore java 1.7 warning
		String str = System.getProperty("java.version");
		if (str != null && !str.startsWith("1.5.") && !str.startsWith("1.6.")) {
			// コンパイラ(1.7)が出力するブートストラップクラスパスを指定せよという警告を無視
			options.add("-Xlint:-options");
		}
		
		//--- source files
		if (project.getBaseClassFile() != null) {
			options.add(project.getBaseClassFile().getAbsolutePath());
		}
		options.add(project.getJavaSourceFile().getAbsolutePath());
		
		// 生成
		return options.toArray(new String[options.size()]);
	}
	
	private void outputJavaCompilerMessages(String msg) {
		final String strPattern = "\\A\\S*\\Q" + this.project.getJavaSourceFile().getName() + "\\E:(\\d+):(.*)\\z";
		final Pattern patError = Pattern.compile(strPattern, Pattern.DOTALL);
		
		// JAVA エラーメッセージを加工
		StringReader sr = new StringReader(msg);
		BufferedReader br = new BufferedReader(sr);
		try {
			String strLine = br.readLine();
			while (strLine != null) {
				boolean isChanged = false;
				Matcher mc = patError.matcher(strLine);
				if (mc.matches()) {
					if (mc.groupCount() >= 2) {
						String lineNo = mc.group(1);
						String javacmsg = mc.group(2);
						// 行番号変換
						try {
							int javaLineNo = Integer.valueOf(lineNo);
							int aadlLineNo = project.getAnalyzer().getJavaProgram().getLineNoByJavaLine(javaLineNo);
							if (aadlLineNo >= 0) {
								isChanged = true;
								project.out.infoPrintln("%s:%d:(java:%d):%s",
										project.getAADLSourceFile().getAbsolutePath(),
										aadlLineNo, javaLineNo, javacmsg);
							}
						}
						catch (Exception ex) {
							// 何もしない
						}
					}
				}

				if (!isChanged) {
					// メッセージが変換されていなければ、そのまま出力
					project.out.infoPrintln(strLine);
				}
				//--- next
				strLine = br.readLine();
			}
		}
		catch (Exception ex) {
			// 加工無しで出力
	    	project.out.info().println(msg);
		}
		finally {
			FileUtil.closeStream(br);
		}
	}
	
	private void searchMainClassPath() {
		Stack<String> pathStack = new Stack<String>();
		String targetClassName = project.getAnalyzer().getAadlClassName();
		if (searchClassFile(project.getClassesDirectory(), targetClassName, pathStack)) {
			// Found Class position
			StringBuffer sb = new StringBuffer();
			if (!pathStack.empty()) {
				for (String pn : pathStack) {
					sb.append(pn);
					sb.append(".");
				}
			}
			sb.append(targetClassName);
			project.setMainClassName(sb.toString());
		}
	}
	
	private boolean searchClassFile(File targetDir, String searchClassName, Stack<String> pathStack) {
		File[] files = targetDir.listFiles();
		for (File fs : files) {
			if (fs.isDirectory()) {
				pathStack.push(fs.getName());
				if (searchClassFile(fs, searchClassName, pathStack)) {
					// Found!!
					return true;
				}
				pathStack.pop();
			}
			else if (fs.isFile()) {
				String filename = fs.getName();
				if (filename.toLowerCase().endsWith(JAVACLASS_SUFFIX) &&
					filename.startsWith(searchClassName) &&
					filename.length() == (searchClassName.length() + JAVACLASS_SUFFIX.length()))
				{
					// Found!!
					return true;
				}
			}
		}
		//--- Not found
		return false;
	}
}
