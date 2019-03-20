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
 * @(#)AadlJarProfile.java	1.14	2009/12/09
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.module.setting;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

import ssac.aadl.module.ModuleArgDetail;
import ssac.aadl.module.ModuleFileManager;
import ssac.util.io.DefaultFile;
import ssac.util.io.Files;
import ssac.util.io.VirtualFile;

/**
 * AADL実行モジュール(JARファイル)に含めるプロファイル。
 * 
 * @version 1.14	2009/12/09
 * @since 1.14
 */
public class AadlJarProfile
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static public final String PREFS_FILENAME = "AADLProperties.xml";
	static public final String METADIR_NAME = "AADL_META_INF/";
	static public final String PROFILE_NAME = METADIR_NAME + PREFS_FILENAME;
	
	static private final Attributes.Name MA_AADL_VERSION = new Attributes.Name("AADL-Version");

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	private final VirtualFile			_jarFile;
	private final String				_mainClass;
	private final String				_compilerVer;
	private final AadlJarProperties	_props;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public AadlJarProfile(File jarFile) throws IOException
	{
		if (jarFile==null)
			throw new IllegalArgumentException("'jarFile' argument is null.");
		this._jarFile = ModuleFileManager.fromJavaFile(jarFile);
		LoadResult result = loadJarPropertyFromFile(jarFile);
		this._mainClass   = result.mainClassname;
		this._compilerVer = result.compilerVersion;
		this._props       = result.properties;
	}
	
	public AadlJarProfile(VirtualFile jarFile) throws IOException
	{
		if (jarFile==null)
			throw new IllegalArgumentException("'jarFile' argument is null.");
		this._jarFile = jarFile;
		LoadResult result = loadJarPropertyFromFile(jarFile);
		this._mainClass   = result.mainClassname;
		this._compilerVer = result.compilerVersion;
		this._props       = result.properties;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public VirtualFile getJarFile() {
		return _jarFile;
	}
	
	public boolean hasProperties() {
		return (_props != null);
	}
	
	//
	// Main-class
	//
	
	public String getMainClass() {
		return _mainClass;
	}
	
	//
	// AADL compiler version
	//
	
	public String getCompilerVersion() {
		return _compilerVer;
	}
	
	//
	// Revision
	//
	
	public int getRevision() {
		return (_props==null ? 0 : _props.getRevision());
	}

	//
	// Source Filename
	//
	
	public String getSourceFilename() {
		return (_props==null ? null : _props.getSourceFilename());
	}
	
	//
	// Title
	//
	
	public String getTitle() {
		return (_props==null ? null : _props.getTitle());
	}
	
	//
	// Description
	//
	
	public String getDescription() {
		return (_props==null ? null : _props.getDescription());
	}
	
	//
	// Note
	//
	
	public String getNote() {
		return (_props==null ? null : _props.getNote());
	}
	
	//
	// Program argument details
	//
	
	public ModuleArgDetail[] getArgumentDetails() {
		return (_props==null ? null : _props.getArgumentDetails());
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	static protected LoadResult loadJarPropertyFromStream(InputStream iStream)
	throws IOException
	{
		JarInputStream jis = new JarInputStream(iStream);
		try {
			// Manifest の取得
			Manifest mani = jis.getManifest();
			if (mani == null)
				return null;
			Attributes mainAttr = mani.getMainAttributes();
			if (mainAttr == null)
				return null;
			
			// Manifest からの情報取得
			//--- main class
			LoadResult result = new LoadResult();
			if (mainAttr.containsKey(Attributes.Name.MAIN_CLASS)) {
				result.mainClassname = mainAttr.getValue(Attributes.Name.MAIN_CLASS);
			}
			//--- compiler version
			if (mainAttr.containsKey(MA_AADL_VERSION)) {
				result.compilerVersion = mainAttr.getValue(MA_AADL_VERSION);
			}
			
			// Properties ファイルの読み込み
			AadlJarProperties jp = null;
			JarEntry je = null;
			while ((je = jis.getNextJarEntry()) != null) {
				try {
					if (PROFILE_NAME.equalsIgnoreCase(je.getName())) {
						jp = new AadlJarProperties();
						jp.loadFromStream(jis);
						break;
					}
				}
				finally {
					try {
						jis.closeEntry();
					} catch (Throwable ignoreEx) {}
				}
			}
			result.properties = jp;
			return result;
		}
		finally {
			Files.closeStream(jis);
		}
	}
	
	static protected LoadResult loadJarPropertyFromFile(VirtualFile jarFile)
	throws IOException
	{
		if (jarFile instanceof DefaultFile) {
			return loadJarPropertyFromFile(((DefaultFile)jarFile).getJavaFile());
		} else {
			return loadJarPropertyFromStream(jarFile.getInputStream());
		}
	}
	
	static protected LoadResult loadJarPropertyFromFile(File jarFile)
	throws IOException
	{
		JarFile jf = new JarFile(jarFile);
		try {
			// Manifest の取得
			Manifest mani = jf.getManifest();
			if (mani == null)
				return null;
			Attributes mainAttr = mani.getMainAttributes();
			if (mainAttr == null)
				return null;
			
			// Manifest からの情報取得
			//--- main class
			LoadResult result = new LoadResult();
			if (mainAttr.containsKey(Attributes.Name.MAIN_CLASS)) {
				result.mainClassname = mainAttr.getValue(Attributes.Name.MAIN_CLASS);
			}
			//--- compiler version
			if (mainAttr.containsKey(MA_AADL_VERSION)) {
				result.compilerVersion = mainAttr.getValue(MA_AADL_VERSION);
			}
			
			// Properties ファイルの読み込み
			AadlJarProperties jp = null;
			JarEntry je = jf.getJarEntry(PROFILE_NAME);
			if (je != null) {
				jp = new AadlJarProperties();
				InputStream jeis = jf.getInputStream(je);
				try {
					jp.loadFromStream(jeis);
				}
				finally {
					Files.closeStream(jeis);
				}
			}
			result.properties = jp;
			return result;
		}
		finally {
			try {
				jf.close();
			} catch (IOException ignoreEx) {}
		}
	}
	
	static protected class LoadResult
	{
		public String 				mainClassname;
		public String				compilerVersion;
		public AadlJarProperties	properties;
	}
}
