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
 * @(#)JarFileInfo.java	2.0.0	2014/03/18 : append method, and move 'ssac.util.io' to 'ssac.aadl.macro.util.io'
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)JarFileInfo.java	1.00	2008/10/16
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.macro.util.io;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Vector;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.regex.PatternSyntaxException;

/**
 * Jarファイルの情報を収集／保持するクラス。
 * 
 * @version 2.0.0	2014/03/18
 *
 * @since 1.00
 */
public final class JarFileInfo
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	static private final String[] EmptyClassNames = new String[0];
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private File		jarFile;
	private Manifest	jarManifest;
	private String[]	mainClassNames;
	private String[]	classNames;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public JarFileInfo() {
		clear();
	}
	
	public JarFileInfo(String filename)
		throws IOException
	{
		this(new File(filename), null);
	}
	
	public JarFileInfo(File file)
		throws IOException
	{
		this(file, null);
	}
	
	public JarFileInfo(String filename, String classpath)
		throws IOException
	{
		this(new File(filename), classpath);
	}
	
	public JarFileInfo(File file, String classpath)
		throws IOException
	{
		this();
		analyzJarFile(file, classpath);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public void clear() {
		this.jarFile = null;
		this.jarManifest = null;
		this.mainClassNames = EmptyClassNames;
		this.classNames = EmptyClassNames;
	}
	
	public File getFile() {
		return this.jarFile;
	}
	
	public Manifest getManifest() {
		return this.jarManifest;
	}
	
	public String getManifestMainClass() {
		String retName = null;
		if (this.jarManifest != null) {
			Attributes mainAttr = this.jarManifest.getMainAttributes();
			if (mainAttr != null) {
				retName = mainAttr.getValue(Attributes.Name.MAIN_CLASS);
			}
		}
		return retName;
	}
	
	public String getManifestClassPaths() {
		String retPaths = null;
		if (this.jarManifest != null) {
			Attributes mainAttr = this.jarManifest.getMainAttributes();
			if (mainAttr != null) {
				retPaths = mainAttr.getValue(Attributes.Name.CLASS_PATH);
			}
		}
		return retPaths;
	}

	/**
	 * マニフェストファイルのクラスパス定義から、絶対パス名の配列を取得する。
	 * @return	クラスパスの絶対パス名の配列
	 */
	public String[] getManifestClassPathArray() {
		String[] retPaths;
		String paths = getManifestClassPaths();
		if (paths != null && paths.length() > 0) {
			File jarDir = jarFile.getParentFile();
			if (jarDir == null) {
				jarDir = new File("");
			}
			jarDir = jarDir.getAbsoluteFile();
			ArrayList<File> filelist = new ArrayList<File>();
			String[] aryPaths = paths.split("\\s");
			for (String p : aryPaths) {
				if (p != null && p.length() > 0) {
					File cp = new File(p);
					if (!cp.isAbsolute()) {
						cp = new File(jarDir, p);
					}
					filelist.add(Files.normalizeFile(cp));
				}
			}
			retPaths = new String[filelist.size()];
			for (int i = 0; i < retPaths.length; ++i) {
				retPaths[i] = filelist.get(i).getAbsolutePath();
			}
		} else {
			// no paths
			retPaths = new String[0];
		}
		return retPaths;
	}
	
	public String[] mainClasses() {
		return this.mainClassNames;
	}
	
	public String[] classes() {
		return this.classNames;
	}
	
	public void parse(String filename, String classpath)
		throws IOException
	{
		parse(new File(filename), classpath);
	}
	
	public void parse(File file, String classpath)
		throws IOException
	{
		analyzJarFile(file, classpath);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	private void analyzJarFile(File file, String classpath)
		throws IOException
	{
		// Jar ファイルの情報取得
		JarFile jf = new JarFile(file);
		Vector<String> tmpArray = new Vector<String>();
		try {
			// Manifest の取得
			Manifest mani = jf.getManifest();
			if (mani != null) {
				this.jarManifest = (Manifest)mani.clone();
			}

			// ファイルに含まれるクラス名の収集
			Enumeration<JarEntry> jentries = jf.entries();
			while (jentries.hasMoreElements()) {
				JarEntry je = jentries.nextElement();
				if (je.isDirectory())
					continue;
				final String name = je.getName();
				final String postfix = name.substring(name.length()-6, name.length());
				if (postfix.equalsIgnoreCase(".class")) {
					// Java class
					tmpArray.add(convertEntryNameToClassName(name));
				}
			}
			this.classNames = tmpArray.toArray(new String[tmpArray.size()]);
		}
		finally {
			try {
				jf.close();
			} catch (IOException ignoreEx) {
				ignoreEx = null;
			}
		}
		
		// メインクラス名は、Jarに含まれる全てのクラスとする。
		this.mainClassNames = this.classNames;
		
		/*------------------------------------------------------------------------
		 * 以下のコードは、ClassLoader を利用して、mainメソッドを持つ
		 * クラスを収集する機能をある程度実現しようとしたものであるが、
		 * 期待通りの動作はしない。
		 * 
		 * JarファイルのURLからClassLoaderを生成し、loadClassを実行した
		 * 時点で、URLのJarファイルからバイナリコードを取得し、クラス
		 * オブジェクトを生成しているが、Jarを読み込んだ時点で、Jarファイルが
		 * キャッシュされる。そのため、ソースの一部を変更してJarファイルを
		 * 生成した直後に実行すると、URLが同一のJarであれば、キャッシュから
		 * バイナリコードが取得されるような動作となるが、この場合には
		 * loadClassに必ず失敗する。同一のJarファイルは、本アプリケーションを
		 * 再起動した後にはクラス情報を取得できることから、このキャッシュが
		 * 問題の原因であると推測できる。
		 * なお、URLClassLoaderのJarファイルリソースを取得するための機能は
		 *   sum.misc.URLClassPath
		 * クラスに含まれており、このクラスはパブリックではないため、
		 * これ以上の解析は不可能である。
		 * 従って、現時点では、Jarファイルの解析では、Jarに含まれるクラス名
		 * のみを取得するようにしている。
		 * 
		// Jar ファイルの情報取得
		JarFile jf = new JarFile(file);
		Vector<String> tmpArray = new Vector<String>();
		try {
			// Manifest の取得
			Manifest mani = jf.getManifest();
			if (mani != null) {
				this.jarManifest = (Manifest)mani.clone();
			}

			// ファイルに含まれるクラス名の収集
			Enumeration<JarEntry> jentries = jf.entries();
			while (jentries.hasMoreElements()) {
				JarEntry je = jentries.nextElement();
				if (je.isDirectory())
					continue;
				final String name = je.getName();
				final String postfix = name.substring(name.length()-6, name.length());
				if (postfix.equalsIgnoreCase(".class")) {
					// Java class
					tmpArray.add(convertEntryNameToClassName(name));
				}
			}
			this.classNames = tmpArray.toArray(new String[tmpArray.size()]);
		}
		finally {
			try {
				jf.close();
			} catch (IOException ignoreEx) {
				// ignore exception
				if (AppLogger.isDebugEnabled()) {
					AppLogger.debug("Failed to close \"" + file.getAbsolutePath() + "\"", ignoreEx);
				}
			}
		}
		
		// JARにあるクラスから、メインを持つクラスを収集
		tmpArray.clear();
		String strClassPaths;
		if (StringHelper.hasString(classpath)) {
			strClassPaths = file.getAbsolutePath() + File.pathSeparator + classpath;
			//strClassPaths = classpath + File.pathSeparator + file.getAbsolutePath();
		} else {
			strClassPaths = file.getAbsolutePath();
		}
		//URL[] paths = convertClasspathToURLs(jf.getName());
		URL[] paths = convertClasspathToURLs(strClassPaths);
		//URLClassLoader cl = new URLClassLoader(paths, AADLEditor.class.getClassLoader());
		URLClassLoader cl = URLClassLoader.newInstance(paths, AADLEditor.class.getClassLoader());
		Class clazz = null;
		Method m = null;
		try {
			for (int i = 0; i < this.classNames.length; i++) {
				try {
					clazz = cl.loadClass(this.classNames[i]);
					m = clazz.getMethod("main", String[].class);
					int mod = m.getModifiers();
					if (Modifier.isStatic(mod) && Modifier.isPublic(mod)) {
						// static public main() あり
						tmpArray.add(this.classNames[i]);
					}
				}
				catch (Throwable ex) {
					// through
					if (AppLogger.isDebugEnabled()) {
						AppLogger.debug("Failed to get \"Main\" method from ["
								+ this.classNames[i] + "] in [" + file.getAbsolutePath() + "]", ex);
					}
				}
			}
		}
		finally {
			// クラスローダー開放
			m = null;
			clazz = null;
			cl = null;
			System.gc();
		}
		this.mainClassNames = tmpArray.toArray(new String[tmpArray.size()]);
		--------------------------------------------------------------*/
	}
	
	// JarEntry名からクラス名に変換する
	static private String convertEntryNameToClassName(String name) {
		String clsname = name.substring(0, name.length()-6);
		return clsname.replaceAll("/", ".");
	}
	
	// クラスパス文字列をURL配列に変換する
	static public URL[] convertClasspathToURLs(String classpath) {
		Vector<URL> tmpArray = new Vector<URL>();
		URL[] urls = null;
		
		try {
			String[] parts = classpath.split(File.pathSeparator);
			
			for (int i=0 ; i<parts.length ; i++) {
				final String path = parts[i];
				
				try {
					URL url = null;
					final String postfix = path.substring(path.length() - 4, path.length());
					if (postfix.equalsIgnoreCase(".jar") || postfix.equalsIgnoreCase(".zip")) {
						final String base = (new File(path).getCanonicalFile().toURL()).toString();
						url = new URL("jar:" + base + "!/");
					} else {
						url = new File(path).getCanonicalFile().toURL();
					}
					
					tmpArray.add(url);
				} catch(IOException e) {
					// through
				}
			}
		} catch(PatternSyntaxException e) {
			throw new IllegalArgumentException(e);
		} catch(NullPointerException e) {
			throw new IllegalArgumentException(e);
		}

		urls = new URL[tmpArray.size()]; 

		for(int j=0 ; j<tmpArray.size() ; j++) {
			urls[j] = (URL) tmpArray.get(j);
		}

		return urls;
	}
}
