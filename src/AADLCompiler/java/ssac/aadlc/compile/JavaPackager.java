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
 * @(#)JavaPackager.java	1.30	2009/12/02
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)JavaPackager.java	1.00	2007/11/29
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadlc.compile;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Stack;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

import ssac.aadlc.AADLConstants;
import ssac.aadlc.AADLMessage;
import ssac.aadlc.io.FileUtil;

/**
 * JAR パッケージング
 *
 * 
 * @version 1.30	2009/12/02
 */
public class JavaPackager
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	static private final Attributes.Name MA_AADL_VERSION = new Attributes.Name("AADL-Version");
	static private final Attributes.Name MA_CREATED_BY = new Attributes.Name("Created-By");

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private final Project	project;		// AADL プロジェクト

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	public JavaPackager(Project target) {
		if (target == null)
			throw new NullPointerException();
		this.project = target;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public boolean pack() {
		// make Manifest
		Manifest mani = null;
		if (project.getCommandLineArgs().isNoManifest()) {
			project.out.tracePrintln("@@@@@ Without Manifest from %s.", project.getAnalyzer().getAadlClassName());
		}
		else {
			project.out.tracePrintln("@@@@@ Setup Manifest for %s.", project.getAnalyzer().getAadlClassName());
			mani = createManifestInstance();
			if (mani == null)
				return false;
			setupManifest(mani);
		}
		
		// make Jar file
		project.out.tracePrintln("@@@@@ Create Jar [%s].", project.getDestinationFile().getAbsolutePath());
		if (!makeJar(mani))
			return false;
		
		// Completed!
		return true;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	// Manifest の新規インスタンス生成
	private Manifest createManifestInstance() {
		if (project.getManifestFile() == null) {
			// 新規インスタンス生成
			return new Manifest();
		}
		
		// ユーザー指定のファイルから Manifest 生成
		Manifest retMani = null;
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		try {
			fis = new FileInputStream(project.getManifestFile());
			bis = new BufferedInputStream(fis);
			
			retMani = new Manifest(fis);
		}
		catch (Exception ex) {
			project.err.errorPrintln("Failed to read manifest from '%s'.", project.cmdArgs.getManifest());
       		String msg = AADLMessage.printException(ex);
			project.err.errorPrintln(msg);
			project.err.debugPrintStackTrace(ex);
			retMani = null;
		}
		finally {
			if (bis != null)
				FileUtil.closeStream(bis);
			if (fis != null)
				FileUtil.closeStream(fis);
		}
		
		return retMani;
	}
	
	// Manifest 情報の設定
	private void setupManifest(Manifest target) {
		Attributes maniAttr = target.getMainAttributes();
		
		//--- Manifest version
		if (!maniAttr.containsKey(Attributes.Name.MANIFEST_VERSION)) {
			maniAttr.put(Attributes.Name.MANIFEST_VERSION, "1.0");
		}
		//--- AADL version
		if (!maniAttr.containsKey(MA_AADL_VERSION)) {
			maniAttr.put(MA_AADL_VERSION, AADLConstants.VERSION);
		}
		//--- Created-By
		if (!maniAttr.containsKey(MA_CREATED_BY)) {
			String javaVendor = System.getProperty("java.vendor");
			String javaVersion = System.getProperty("java.version");
			maniAttr.put(MA_CREATED_BY, String.format("%s(%s)", javaVersion, javaVendor));
		}
		//--- Main class
		if (!maniAttr.containsKey(Attributes.Name.MAIN_CLASS)) {
			if (project.hasMainClassName()) {
				maniAttr.put(Attributes.Name.MAIN_CLASS, project.getMainClassName());
			}
		}
		//--- Class path
		// クラスパスは含めない
		/*---
		if (!maniAttr.containsKey(Attributes.Name.CLASS_PATH)) {
			if (project.getCommandLineArgs().hasClassPath()) {
				//--- カレントパスもクラスパスに含めないと、正しく動作しない
				String clspaths = ". " + project.getCommandLineArgs().getClassPath().replaceAll(";", " ");
				maniAttr.put(Attributes.Name.CLASS_PATH, clspaths);
			}
		}
		---*/
		
		// for Debug
		project.out.debugPrintln("----- Manifest entry -----");
		for (Object key : maniAttr.keySet()) {
			Object val = maniAttr.get(key);
			project.out.debug().printf("  %s : %s", key.toString(), val.toString());
			project.out.debug().println();
		}
		project.out.debugPrintln("----- End of Manifest entry -----");
	}
	
	private boolean makeJar(Manifest mani) {
		boolean ret;
		JarOutputStream jos = null;
		BufferedOutputStream bos = null;
		FileOutputStream fos = null;
		try {
			// make output stream
			fos = new FileOutputStream(project.getDestinationFile());
			bos = new BufferedOutputStream(fos);
			if (mani != null)
				jos = new JarOutputStream(bos, mani);
			else
				jos = new JarOutputStream(bos);
			
			// Write jar properties entry
			writeJarPropertiesEntry(jos, project.getJarPropertiesFile());
			
			// Write jar entries
			DirectoryStack ds = new DirectoryStack();
			writeJarEntries(jos, project.getClassesDirectory(), ds);
			
			// Finish
			jos.finish();
			ret = true;
		}
		catch (Exception ex) {
			project.err.errorPrintln("Failed to write entry to Jar file.");
       		String msg = AADLMessage.printException(ex);
			project.err.errorPrintln(msg);
			project.err.debugPrintStackTrace(ex);
			ret = false;
		}
		finally {
			if (jos != null)
				FileUtil.closeStream(jos);
			if (bos != null)
				FileUtil.closeStream(bos);
			if (fos != null)
				FileUtil.closeStream(fos);
		}
		
		return ret;
	}
	
	private void writeJarPropertiesEntry(JarOutputStream jos, File propFile)
		throws IOException
	{
		if (propFile == null)
			return;	// no target properties file
		final String destName = "AADL_META_INF/AADLProperties.xml";
		project.out.tracePrintln("  %s <<- %s", project.getDestinationFile().getName(), destName);
		
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(propFile);
			//--- create Jar entry
			ZipEntry entry = new ZipEntry(destName);
			jos.putNextEntry(entry);
			//--- write target file to jar
			byte[] byteBuffer = new byte[8 * 1024];
			int count = 0;
			do {
				jos.write(byteBuffer, 0, count);
				count = fis.read(byteBuffer, 0, byteBuffer.length);
			} while (count != -1);
			jos.closeEntry();
		}
		finally {
			if (fis != null)
				FileUtil.closeStream(fis);
		}
	}
	
	private void writeJarEntries(JarOutputStream jos, File targetDir, DirectoryStack dirStack)
		throws IOException
	{
		String pathString = dirStack.getEntryPathString();
		
		// write Directory
		if (!dirStack.empty()) {
			writeDirectoryToJar(jos, pathString);
		}

		// write Files
		File[] files = targetDir.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				//--- in directory
				dirStack.push(file.getName());
				writeJarEntries(jos, file, dirStack);
				dirStack.pop();
			}
			else {
				//--- file
				writeFileToJar(jos, pathString, file);
			}
		}
	}
	
	private void writeDirectoryToJar(JarOutputStream jos, String entryPath)
		throws IOException
	{
		project.out.tracePrintln("  %s <<- %s", project.getDestinationFile().getName(), entryPath);
		final JarEntry entry = new JarEntry(entryPath);
		entry.setMethod(JarEntry.STORED);
		entry.setSize(0);
		entry.setCrc(0);
		jos.putNextEntry(entry);
		jos.closeEntry();
	}
	
	private void writeFileToJar(JarOutputStream jos, String entryPath, File targetFile)
		throws FileNotFoundException, IOException
	{
		String entryName = entryPath + targetFile.getName();
		project.out.tracePrintln("  %s <<- %s", project.getDestinationFile().getName(), entryName);
		
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(targetFile);
			//--- create Jar entry
			JarEntry entry = new JarEntry(entryName);
			jos.putNextEntry(entry);
			//--- write target file to jar
			byte[] byteBuffer = new byte[8 * 1024];
			int count = 0;
			do {
				jos.write(byteBuffer, 0, count);
				count = fis.read(byteBuffer, 0, byteBuffer.length);
			} while (count != -1);
			jos.closeEntry();
		}
		finally {
			if (fis != null)
				FileUtil.closeStream(fis);
		}
	}

	//------------------------------------------------------------
	// Internal classes
	//------------------------------------------------------------
	
	protected static class DirectoryStack extends Stack<String> {
		public String getEntryPathString() {
			if (empty())
				return "";
			StringBuffer sb = new StringBuffer();
			for (String dir : this) {
				sb.append(dir);
				sb.append("/");
				//sb.append(File.separatorChar);
			}
			return sb.toString();
		}
	}
}
