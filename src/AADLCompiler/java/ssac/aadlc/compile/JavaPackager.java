/*
 * @(#)JavaPackager.java	4.0.0	2021/08/26
 *     - modified by Y.Ishizuka(PieCake.inc,)
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
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

import ssac.aadlc.AADLConstants;
import ssac.aadlc.AADLMessage;
import ssac.aadlc.io.FileUtil;
import ssac.aadlc.tools.libsinjar.LibFileInfo;
import ssac.aadlc.tools.libsinjar.LibFileType;
import ssac.aadlc.tools.libsinjar.LibsInJarInfo;

/**
 * JAR パッケージング
 *
 * 
 * @version 4.0.0
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
	private int	numExpandedLibFiles;

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
		
		// expand libraries (@since 4.0.0)
		numExpandedLibFiles = 0;
		if (project.hasLibFileInfo()) {
			project.out.tracePrintln("@@@@@ Expand libraries to include in jar.");
			if (!expandLibraries()) {
				return false;	// error
			}
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
	
	/**
	 * Jar に含める外部ライブラリを展開する。
	 * @return	成功なら <tt>true</tt>
	 */
	private boolean expandLibraries() {
		String newline = System.getProperty("line.separator");
		if (newline==null || newline.isEmpty()) newline = "\n";
		File fClassesDir = project.getClassesDirectory();
		StringBuilder sbIncludedLibs = new StringBuilder();
		sbIncludedLibs.append("//------------------------------------------------").append(newline);
		sbIncludedLibs.append("// Included libraries in this jar.").append(newline);
		sbIncludedLibs.append("//------------------------------------------------").append(newline);
		String strIncludedLibsDescHeader = sbIncludedLibs.toString();
		sbIncludedLibs.setLength(0);
		
		// Classes ディレクトリ内のエントリマップを生成(上書き禁止)
		Set<String> excludedPathSet = new HashSet<String>();
		collectFileEntries(excludedPathSet, fClassesDir, new DirectoryStack());
		Path classesPath = fClassesDir.toPath();
		
		// Expand libraries
		ArrayList<String> strIncludedLibsDescList = new ArrayList<String>();
		LibsInJarInfo libsinfo = project.getLibsInJarInfoObject();
		for (int i = libsinfo.size()-1; i >= 0; --i) {
			//--- リスト先頭が優先順位が高いので、リスト終端から展開
			LibFileInfo libinfo = libsinfo.get(i);
			File libPath = libinfo.getLibFile();
			if (libPath == null) continue;
			
			// expand to classes
			if (libinfo.getFileType() == LibFileType.JAR) {
				//--- JAR file
				if (!expandJarFile(classesPath, excludedPathSet, libPath)) {
					return false;	// error
				}
			}
			else if (libinfo.getFileType() == LibFileType.CLASSES) {
				//--- Classes directory
				DirectoryStack dirStack = new DirectoryStack();
				File[] subfiles = libPath.listFiles();
				if (subfiles != null && subfiles.length > 0) {
					for (File sfile : subfiles) {
						if (!expandClassesRecursive(classesPath, excludedPathSet, sfile, dirStack)) {
							return false;	// error
						}
					}
				}
			}
			else {
				continue;	// skip
			}
			
			// description
			sbIncludedLibs.append(newline);
			sbIncludedLibs.append(libinfo.getAvailableName()).append(newline);
			sbIncludedLibs.append("  module: ").append(libinfo.getLibFile().getName()).append(newline);
			if (libinfo.hasLicense()) {
				sbIncludedLibs.append("  license: ").append(libinfo.getLicenseName()).append(newline);
			}
			strIncludedLibsDescList.add(0, sbIncludedLibs.toString());	// 逆順
			sbIncludedLibs.setLength(0);
		}
		
		// output description text to META-INF
		if (!strIncludedLibsDescList.isEmpty()) {
			File destTextFile = new File(project.getMetaInfDirectory(), "included-libs.txt");
			FileOutputStream fos = null;
			OutputStreamWriter osw = null;
			try {
				fos = new FileOutputStream(destTextFile);
				osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
				osw.write(strIncludedLibsDescHeader);
				for (String str : strIncludedLibsDescList) {
					osw.write(str);
				}
			}
			catch (IOException ex) {
				project.err.errorPrintln("Failed to write information of included libraries to : %s\n  (cause) %s", destTextFile, String.valueOf(ex.getMessage()));
				return false;
			}
			finally {
				if (osw != null) {
					try {
						osw.close();
					} catch (Throwable ignoreEx) {}
				}
				if (fos != null) {
					try {
						fos.close();
					} catch (Throwable ignoreEx) {}
				}
			}
		}
		
		// succeeded
		numExpandedLibFiles = strIncludedLibsDescList.size();
		return true;
	}
	
	private boolean expandJarFile(Path pathDestDir, Set<String>excludedPathSet, File srcJar)
	{
		// Jar ファイルの情報取得
		JarFile jf = null;
		try {
			jf = new JarFile(srcJar);
			Enumeration<JarEntry> jentries = jf.entries();
			while (jentries.hasMoreElements()) {
				JarEntry je = jentries.nextElement();
				if (je.isDirectory())
					continue;	// skip Directory's entry
				final String name = je.getName();
				if (name.startsWith(Project.JAR_METAINF_ENTRY))
					continue;	// skip under META-INF/
				if (excludedPathSet.contains(name.toLowerCase()))
					continue;	// skip excluded-path
				//--- ensure directory
				Path pathDestFile = pathDestDir.resolve(name);
				java.nio.file.Files.createDirectories(pathDestFile.getParent());
				//--- copy stream to file
				InputStream jis = jf.getInputStream(je);
				try {
					java.nio.file.Files.copy(jis, pathDestFile, StandardCopyOption.REPLACE_EXISTING);
					long ltm = je.getTime();
					if (ltm > 0) {
						java.nio.file.Files.setLastModifiedTime(pathDestFile, FileTime.fromMillis(ltm));
					}
				}
				finally {
					if (jis != null) {
						try {
							jis.close();
						} catch (Throwable ignoreEx) {}
					}
				}
			}
		}
		catch (IOException ex) {
			project.err.errorPrintln("Failed to expand jar file : %s\n  (cause) %s", srcJar, String.valueOf(ex.getMessage()));
			return false;
		}
		finally {
			if (jf != null) {
				try {
					jf.close();
				} catch (Throwable ignoreEx) {}
			}
		}
		
		// succeeded
		return true;
	}
	
	private boolean expandClassesRecursive(Path pathDestDir, Set<String>excludedPathSet, File srcFile, DirectoryStack dirStack)
	{
		if (srcFile.isDirectory()) {
			//--- directory
			dirStack.push(srcFile.getName());
			try {
				File[] subfiles = srcFile.listFiles();
				if (subfiles != null && subfiles.length > 0) {
					for (File sfile : subfiles) {
						if (!expandClassesRecursive(pathDestDir, excludedPathSet, sfile, dirStack)) {
							return false;	// error
						}
					}
				}
			}
			finally {
				dirStack.pop();
			}
		}
		else {
			//--- file
			String pathString = dirStack.getEntryPathString() + srcFile.getName();
			if (pathString.startsWith(Project.JAR_METAINF_ENTRY))
				return true;	// skip under META-INF/
			if (excludedPathSet.contains(pathString.toLowerCase()))
				return true;	// skip exluded-path
			try {
				//--- ensure directory
				Path pathDestFile = pathDestDir.resolve(pathString);
				java.nio.file.Files.createDirectories(pathDestFile.getParent());
				//--- copy file to file
				java.nio.file.Files.copy(srcFile.toPath(), pathDestFile, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
			}
			catch (Throwable ex) {
				project.err.errorPrintln("Failed to copy file from classes directory : %s\n  (cause) %s", srcFile, String.valueOf(ex.getMessage()));
				return false;
			}
		}
		
		// succeeded
		return true;
	}
	
	private void collectFileEntries(Set<String> pathset, File targetDir, DirectoryStack dirStack)
	{
		String pathString = dirStack.getEntryPathString();

		// collect Files
		File[] files = targetDir.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				//--- in directory
				dirStack.push(file.getName());
				collectFileEntries(pathset, file, dirStack);
				dirStack.pop();
			}
			else {
				// file
				//--- 比較用にすべて小文字化
				String entryName = pathString + file.getName();
				entryName = entryName.toLowerCase();
				pathset.add(entryName);
			}
		}
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
			DirectoryStack ds = new DirectoryStack();
			
			// Write custome files in META-INF (@since 4.0.0)
			writeCustomMetaInfEntries(jos, project.getMetaInfDirectory(), ds);
			
			// Write jar properties entry
			writeJarPropertiesEntry(jos, project.getJarPropertiesFile());
			
			// Write jar entries
			ds.clear();
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
	
	/**
	 * @since 4.0.0
	 */
	private void writeCustomMetaInfEntries(JarOutputStream jos, File targetDir, DirectoryStack dirStack)
		throws IOException
	{
		// このメソッドは recursive 呼び出し禁止
		dirStack.push(Project.JAR_METAINF_NAME);
		String pathString = dirStack.getEntryPathString();

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
