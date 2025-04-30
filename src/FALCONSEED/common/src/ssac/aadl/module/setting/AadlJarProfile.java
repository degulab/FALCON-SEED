/*
 * @(#)AadlJarProfile.java	4.0.0	2021/08/27 : for Java11
 *     - modified by Y.Ishizuka(PieCake.inc,)
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
import ssac.util.io.JarFileInfo;
import ssac.util.io.VirtualFile;

/**
 * AADL実行モジュール(JARファイル)に含めるプロファイル。
 * 
 * @version 4.0.0
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

	private final VirtualFile		_jarFile;
	private final String			_mainClass;
	private final String			_compilerVer;
	private final boolean			_fatjar;
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
		this._fatjar      = result.fatjar;
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
		this._fatjar      = result.fatjar;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	/**
	 * 指定されたファイルが、included-libs.txt を含む Fat-jar かどうかを判定する。
	 * このメソッドでは、読み込みエラーが発生した場合も例外をスローせずに <tt>false</tt> を返す。
	 * @param jarFile	判定対象のファイル
	 * @return	Fat-jar なら <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @since 4.0.0
	 */
	static public boolean isFatJarFile(VirtualFile jarFile)
	{
		if (jarFile instanceof DefaultFile) {
			return JarFileInfo.isFatJarFile(((DefaultFile)jarFile).getJavaFile());
		}
		
		// use stream
		JarInputStream jis = null;
		try {
			jis = new JarInputStream(jarFile.getInputStream());
			JarEntry je = null;
			while ((je = jis.getNextJarEntry()) != null) {
				try {
					if (JarFileInfo.FATJAR_INCLUDINGLIBS_ENTRYNAME.equals(je.getName())) {
						return true;
					}
				}
				finally {
					try {
						jis.closeEntry();
					} catch (Throwable ignoreEx) {}
				}
			}
			return false;
		}
		catch (Throwable ignoreEx) {
			return false;
		}
		finally {
			if (jis != null) {
				try {
					jis.close();
				} catch (Throwable ignoreEx) {}
			}
		}
	}
	
	public VirtualFile getJarFile() {
		return _jarFile;
	}
	
	public boolean hasProperties() {
		return (_props != null);
	}
	
	public boolean isFatJar() {
		return _fatjar;
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
						if (result.fatjar) {
							break;
						}
					}
					else if (JarFileInfo.FATJAR_INCLUDINGLIBS_ENTRYNAME.equals(je.getName())) {
						result.fatjar = true;
						if (jp != null) {
							break;
						}
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
			
			// Including-libs.txt の読み込み
			JarEntry je = jf.getJarEntry(JarFileInfo.FATJAR_INCLUDINGLIBS_ENTRYNAME);
			if (je != null) {
				result.fatjar = true;
				//result.includingLibsText = null;
				//InputStreamReader isr = null;
				//InputStream jeis = jf.getInputStream(je);
				//try {
				//	isr = new InputStreamReader(jeis, StandardCharsets.UTF_8);
				//	StringBuilder sb = new StringBuilder((int)je.getSize());
				//	char[] buffer = new char[8192];
				//	int read;
				//	while ((read = isr.read(buffer)) >= 0) {
				//		sb.append(buffer, 0, read);
				//	}
				//	result.includingLibsText = sb.toString();
				//}
				//finally {
				//	Files.closeStream(isr);
				//	Files.closeStream(jeis);
				//}
			}
			else {
				result.fatjar = false;
				//result.includingLibsText = null;
			}
			
			// Properties ファイルの読み込み
			AadlJarProperties jp = null;
			je = jf.getJarEntry(PROFILE_NAME);
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
		/**
		 * AADL コンパイラが Fat-jar 生成時に出力する、ライブラリ情報ファイルの有無。
		 * このファイルが存在する場合は、Fat-jar。
		 * @since 4.0.0
		 */
		public boolean				fatjar;
		///**
		// * AADL コンパイラが Fat-jar 生成時に出力する、ライブラリ情報ファイルの内容(テキスト)。
		// * 存在しない場合は <tt>null</tt>。
		// * @since 4.0.0
		// */
		//public String				includingLibsText;
		public AadlJarProperties	properties;
	}
}
