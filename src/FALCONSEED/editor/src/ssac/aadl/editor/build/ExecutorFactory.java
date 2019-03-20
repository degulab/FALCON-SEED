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
 * @(#)ExecutorFactory.java	1.14	2009/12/09
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ExecutorFactory.java	1.10	2009/01/28
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ExecutorFactory.java	1.00	2008/04/14
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.editor.build;

import java.io.File;
import java.util.Arrays;
import java.util.Vector;

import ssac.aadl.editor.AADLEditor;
import ssac.aadl.editor.setting.AppSettings;
import ssac.aadl.module.setting.ExecSettings;
import ssac.util.Strings;
import ssac.util.process.CommandExecutor;

/**
 * アプリケーションによって設定されたコンパイルもしくは実行設定に従い、
 * <code>{@link CommandExecutor}</code> インスタンスを生成するユーティリティ。
 * 
 * @version 1.14	2009/12/09
 */
public final class ExecutorFactory
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/*--- IEditorDocument の実装内へ移行
	static public CommandExecutor createCompileExecutor(SourceDocumentModel srcModel) {
		// check exist document
		if (srcModel == null)
			throw new NullPointerException();
		final CompileSettings settings = srcModel.getSettings();
		
		// check exist target
		File targetFile = srcModel.getFile();
		if (targetFile == null)
			throw new IllegalArgumentException("Target file is nothing.");
		if (!targetFile.exists())
			throw new IllegalArgumentException("Target file is not found.\n[File] " + targetFile.getAbsolutePath());
		if (!targetFile.isFile())
			throw new IllegalArgumentException("Target is not file.\n[File] " + targetFile.getAbsolutePath());

		// create command
		Vector<String> cmdList = new Vector<String>();
		//--- java command
		addJavaCommandPath(cmdList, AppSettings.getInstance().getCurrentJavaCommandPath());
		//--- Max heap size
		addJavaMaxHeapSize(cmdList, AADLEditor.getMaxMemorySize());
		//--- java VM arguments
		
		// ClassPath
		ClassPaths libPaths = new ClassPaths();
		libPaths.addPath(settings.getTargetJavaCompilerJar());
		libPaths.appendPaths(AppSettings.getInstance().getCompileLibraries());
		addClassPath(cmdList, libPaths);
		
		// main class
		addMainClassName(cmdList, "ssac.aadlc.Main");
		
		// User class paths
		libPaths.clear();
		libPaths.appendPaths(settings.getClassPaths());
		addClassPath(cmdList, libPaths);
		
		// Options
		//--- encoding
		String strEncoding = settings.getTargetEncodingName();
		if (StringHelper.hasString(strEncoding)) {
			cmdList.add("-encoding");
			cmdList.add(strEncoding);
		}
		//--- destination
		String strDest = settings.getDestinationFile();
		if (StringHelper.hasString(strDest)) {
			cmdList.add("-d");
			cmdList.add(strDest);
		} else {
			// default destination
			cmdList.add("-d");
			//cmdList.add(srcModel.getDestinationAbsolutePath());
			cmdList.add(srcModel.getDestinationFilename());
		}
		//--- source output dir
		String strSrcDir = settings.getSourceOutputDir();
		if (StringHelper.hasString(strSrcDir)) {
			cmdList.add("-sd");
			cmdList.add(strSrcDir);
		}
		//--- no manifest
		if (settings.isDisabledManifest()) {
			cmdList.add("-M");
		}
		//--- manifest
		String strManifest = settings.getUserManifestFile();
		if (StringHelper.hasString(strManifest)) {
			cmdList.add("-m");
			cmdList.add(strManifest);
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
		
		// source file
		cmdList.add(srcModel.getAbsolutePath());
		
		// create Executor
		CommandExecutor executor = new CommandExecutor(cmdList);
		
		// Working directory
		executor.setWorkDirectory(targetFile.getParentFile());
		
		// completed
		return executor;
	}
	---*/
	
	static public CommandExecutor createJarExecutor(ExecSettings settings) {
		// check exist settings
		if (settings == null)
			throw new NullPointerException();
		
		// check exist target
		File targetFile = settings.getTargetFile();
		if (targetFile == null)
			throw new IllegalArgumentException("Target file is nothing.");
		if (!targetFile.exists())
			throw new IllegalArgumentException("Target file is not found : \"" + targetFile.getPath() + "\"");
		if (!targetFile.isFile())
			throw new IllegalArgumentException("Target is not file : \"" + targetFile.getPath() + "\"");
		
		// create command
		Vector<String> cmdList = new Vector<String>();
		//--- java command
		//addJavaCommandPath(cmdList, settings.getTargetJavaCommandPath());
		addJavaCommandPath(cmdList, AppSettings.getInstance().getCurrentJavaCommandPath());
		
		// Java VM arguments
		String params;
		//--- AADL csv encoding property
		params = AppSettings.getInstance().getAadlCsvEncodingName();
		if (!Strings.isNullOrEmpty(params)) {
			cmdList.add("-Daadl.csv.encoding=" + params);
		}
		//--- AADL txt encoding property
		params = AppSettings.getInstance().getAadlTxtEncodingName();
		if (!Strings.isNullOrEmpty(params)) {
			cmdList.add("-Daadl.txt.encoding=" + params);
		}
		//--- java VM arguments
		String maxMemorySize = AADLEditor.getMaxMemorySize();
		String strVmArgs = AppSettings.getInstance().appendJavaVMoptions(settings.getJavaVMArgs());
		if (!Strings.isNullOrEmpty(maxMemorySize)) {
			addJavaMaxHeapSize(cmdList, maxMemorySize);
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
			addJavaVMArgs(cmdList, strVmArgs);
		}
		
		// Check user class-paths
		File[] userClassPaths = settings.getClassPathFiles();
		for (File file : userClassPaths) {
			if (file == null)
				throw new IllegalArgumentException("User class path file is null.");
			if (!file.exists())
				throw new IllegalArgumentException("User class path file is not found : \"" + file.getPath() + "\"");
		}
		
		// Check Exec libraries
		String[] execLibraries = AppSettings.getInstance().getExecLibraries();
		for (String path : execLibraries) {
			if (Strings.isNullOrEmpty(path))
				throw new IllegalStateException("Class path for execution in Editor environment is illegal value.");
			if (!(new File(path)).exists())
				throw new IllegalStateException("Class path for execution in Editor environment is not found : \"" + path + "\"");
		}
		
		// ClassPath
		ClassPaths pathList = new ClassPaths();
		pathList.appendPaths(userClassPaths);
		pathList.addPath(targetFile);
		pathList.appendPaths(execLibraries);
		addClassPath(cmdList, pathList);
		
		// Main class
		addMainClassName(cmdList, settings.getTargetMainClass());
		
		// Program arguments
		String[] args = settings.getProgramArgs();
		if (args != null && args.length > 0) {
			for (String arg : args) {
				if (Strings.isNullOrEmpty(arg)) {
					cmdList.add("");
				} else {
					cmdList.add(arg);
				}
			}
		}
		
		// Check Working directory
		File dir = null;
		if (settings.isSpecifiedWorkDir()) {
			dir = settings.getWorkDirFile();
			if (dir != null) {
				if (!dir.exists()) {
					throw new IllegalArgumentException("Working directory is not found : \"" + dir.getPath() + "\"");
				}
				else if (!dir.isDirectory()) {
					throw new IllegalArgumentException("Working directory is not directory : \"" + dir.getPath() + "\"");
				}
			}
		}
		
		// create Executor
		CommandExecutor executor = new CommandExecutor(cmdList);
		
		// Working directory
		if (dir != null && !Strings.isNullOrEmpty(dir.getPath())) {
			executor.setWorkDirectory(dir);
		} else {
			executor.setWorkDirectory(targetFile.getParentFile());
		}
		
		// completed
		return executor;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	static public void addJavaCommandPath(Vector<String> cmdList, String javaCmdPath) {
		if (!Strings.isNullOrEmpty(javaCmdPath)) {
			cmdList.add(javaCmdPath);
		} else {
			cmdList.add("java");
		}
	}
	
	static public void addJavaMaxHeapSize(Vector<String> cmdList, String maxSize) {
		if (!Strings.isNullOrEmpty(maxSize)) {
			cmdList.add("-Xmx" + maxSize);
		}
	}
	
	static public void addJavaVMArgs(Vector<String> cmdList, String argsList) {
		if (!Strings.isNullOrEmpty(argsList)) {
			String[] args = Strings.splitCommandLineWithDoubleQuoteEscape(argsList);
			cmdList.addAll(Arrays.asList(args));
		}
	}
	
	static public void addClassPath(Vector<String> cmdList, ClassPaths pathList) {
		if (pathList != null && !pathList.isEmpty()) {
			cmdList.add("-classpath");
			cmdList.add(pathList.getClassPathString());
		}
	}
	
	static public void addMainClassName(Vector<String> cmdList, String mainClassName) {
		if (!Strings.isNullOrEmpty(mainClassName)) {
			cmdList.add(mainClassName);
		} else {
			cmdList.add("");
		}
	}
}
