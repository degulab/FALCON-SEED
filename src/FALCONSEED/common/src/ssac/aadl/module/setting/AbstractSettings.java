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
 * @(#)AbstractSettings.java	2.0.0	2012/10/31
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AbstractSettings.java	1.17	2010/11/19
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AbstractSettings.java	1.14	2009/12/09
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AbstractSettings.java	1.10	2009/01/28
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AbstractSettings.java	1.00	2008/03/24
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.module.setting;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;

import ssac.util.Strings;
import ssac.util.io.DefaultFile;
import ssac.util.io.DefaultFileFactory;
import ssac.util.io.Files;
import ssac.util.io.VirtualFile;
import ssac.util.io.VirtualFileFactory;
import ssac.util.logging.AppLogger;
import ssac.util.properties.ExProperties;
import ssac.util.properties.JavaXmlPropertiesModel;

/**
 * アプリケーションで使用する設定情報を操作する共通機能を提供する抽象クラス。
 * 
 * @version 2.0.0	2012/10/31
 */
public abstract class AbstractSettings
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	static public final String FILE_EXT = ".prefs";

	static protected final String[] EMPTY_STRING_ARRAY = new String[0];
	static protected final URI[] EMPTY_URI_ARRAY = new URI[0];
	static protected final File[] EMPTY_JAVAFILE_ARRAY = new File[0];
	static protected final VirtualFile[] EMPTY_VIRTUALFILE_ARRAY = new VirtualFile[0];
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** プロパティ **/
	protected final ExProperties	props;
	/** プロパティの保存先ファイル **/
	private VirtualFile	_propFile;
	/** プロパティファイル読み込み時もしくは書き込み時の更新日時 **/
	private long			_lastModified;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public AbstractSettings() {
		this((ExProperties)null);
	}
	
	public AbstractSettings(ExProperties defaults) {
		this.props = new ExProperties(new JavaXmlPropertiesModel(), defaults);
	}
	
	//***********************************************************************
	// コンストラクタで rollback() を行うと、派生クラスのフィールドが
	// 未初期化の状態なので、コンストラクタではフィールドの初期化のみとし、
	// プロパティファイルのロードは行わないようにする。
	//***********************************************************************

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 指定されたプロパティと内容が等しい場合に <tt>true</tt> を返す。
	 * <p>このメソッドではデフォルトの内容を比較される。
	 * @param otherProperties	比較するプロパティ
	 * @return 内容が等しい場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @since 2.0.0
	 */
	public boolean equalsProperties(AbstractSettings otherProperties) {
		if (otherProperties == null)
			return false;
		if (this == otherProperties)
			return true;
		
		return props.equalsProperties(otherProperties.props);
	}

	/**
	 * 指定されたプロパティと内容が等しい場合に <tt>true</tt> を返す。
	 * <p>このメソッドでは、デフォルトのプロパティは比較対象には含まない。
	 * @param otherProperties	比較するプロパティ
	 * @return 内容が等しい場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @since 2.0.0
	 */
	public boolean equalsPropertiesWithoutDefaults(AbstractSettings otherProperties) {
		if (otherProperties == null)
			return false;
		if (this == otherProperties)
			return true;
		
		return props.equalsPropertiesWithoutDefaults(otherProperties.props);
	}

	/**
	 * このプロパティに設定されている保存先を返す。
	 * 保存先が設定されていない場合は <tt>null</tt> を返す。
	 * @since 1.14
	 */
	public VirtualFile getVirtualPropertyFile() {
		return _propFile;
	}
	
	/**
	 * このプロパティに設定されている保存先を表す抽象パスを返す。
	 * 保存先が設定されていない場合は <tt>null</tt> を返す。
	 * このメソッドは、<code>java.io.File</code> オブジェクトを返すものであり、
	 * 設定されている抽象パスが <code>java.io.File</code> オブジェクトに変換できない
	 * 場合は例外をスローする
	 * @return <code>java.io.File</code> オブジェクト
	 * @throws IllegalStateException	<code>java.io.File</code> オブジェクトに変換できない場合
	 * @since 1.14
	 */
	public File getJavaPropertyFile() {
		if (_propFile == null) {
			return null;
		} else if (_propFile instanceof DefaultFile) {
			return ((DefaultFile)_propFile).getJavaFile();
		} else {
			throw new IllegalStateException("Property file instance is not java.io.File : ("
											+ _propFile.getClass().getName() + ") : "
											+ _propFile.toString());
		}
	}

	/**
	 * <em>targetFile</em> を保存先に設定し、<em>targetFile</em> から
	 * プロパティの内容を読み込む。
	 * @param targetFile	保存先に設定する抽象パス
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	public void loadForTarget(File targetFile) {
		updateSettingFile(targetFile);
		rollback();
	}
	
	/**
	 * <em>targetFile</em> を保存先に設定し、<em>targetFile</em> から
	 * プロパティの内容を読み込む。
	 * @param targetFile	保存先に設定する抽象パス
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @since 1.14
	 */
	public void loadForTarget(VirtualFile targetFile) {
		updateSettingFile(targetFile);
		rollback();
	}

	/**
	 * <em>targetFile</em> を保存先に設定し、現在のプロパティを指定の
	 * 保存先に保存する。
	 * @param targetFile	保存先に設定する抽象パス
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws IOException	入出力エラーが発生した場合
	 */
	public void saveForTarget(File targetFile) throws IOException {
		updateSettingFile(targetFile);
		commit();
	}
	
	/**
	 * <em>targetFile</em> を保存先に設定し、現在のプロパティを指定の
	 * 保存先に保存する。
	 * @param targetFile	保存先に設定する抽象パス
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws IOException	入出力エラーが発生した場合
	 * @since 1.14
	 */
	public void saveForTarget(VirtualFile targetFile) throws IOException {
		updateSettingFile(targetFile);
		commit();
	}
	
	/**
	 * このプロパティの新しいターゲットファイルを設定する。
	 * このメソッドはターゲットファイルを設定するのみで、
	 * パスの検証やファイル操作は行わない。
	 * @param targetFile	新しいターゲットとする抽象パス
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @since 1.14
	 */
	public void setPropertyTarget(VirtualFile targetFile) {
		updateSettingFile(targetFile);
	}
	
	/**
	 * このプロパティの新しいターゲットファイルを設定する。
	 * このメソッドはターゲットファイルを設定するのみで、
	 * パスの検証やファイル操作は行わない。
	 * @param targetFile	新しいターゲットとする抽象パス
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @since 1.14
	 */
	public void setPropertyTarget(File targetFile) {
		updateSettingFile(targetFile);
	}

	/**
	 * 変更されたプロパティの内容を元に戻す。
	 * <p>
	 * このメソッドは、プロパティをクリアし初期化した後、
	 * 保存先が設定されている場合はその保存先から
	 * 保存されているプロパティを読み込む。
	 */
	public void rollback() {
		// 初期化
		props.clear();
		initializeProperties();
		if (_propFile != null) {
			loadProperties();
		}
	}

	/**
	 * 現在のプロパティの内容を、保存先に指定された場所に保存する。
	 * 保存先が設定されていない場合、このメソッドは処理を行わない。
	 * @throws IOException	入出力エラーが発生した場合
	 */
	public void commit() throws IOException {
		if (_propFile != null) {
			saveProperties();
		}
	}

	/**
	 * このプロパティの保存先ファイルに最後にアクセスしたときの
	 * 最終更新日時と、現在の保存先ファイルの最終更新日時が異なる
	 * 場合にのみ、プロパティの内容をファイルから読み出しなおす。
	 * @return	読み込みが行われた場合は <tt>true</tt> を返す。
	 * 			読み込みが行われない場合や、保存先ファイルが指定されて
	 * 			いない場合は <tt>false</tt> を返す。
	 */
	public boolean refresh() {
		if (_propFile == null) {
			// 保存先ファイルが指定されていない
			return false;
		}
		
		long curLastModified = 0L;
		boolean canAccess;
		try {
			curLastModified = _propFile.lastModified();
			canAccess = true;
		} catch (Throwable ex) {
			canAccess = false;
		}
		
		if (!canAccess) {
			// アクセスできないため、読み込み不可
			return false;
		}
		if (curLastModified == _lastModified) {
			// 同一時刻の場合は、読み込まない
			return false;
		}
		
		// rollback
		rollback();
		return true;
	}

	//------------------------------------------------------------
	// Helper methods
	//------------------------------------------------------------

	static public String getLoadErrorMessage(VirtualFile targetFile) {
		return getLoadErrorMessage(targetFile==null ? null : targetFile.toString());
	}

	static public String getLoadErrorMessage(File targetFile) {
		return getLoadErrorMessage(targetFile==null ? null : targetFile.toString());
	}
	
	static public String getLoadErrorMessage(String pathname) {
		return "Failed to load properties from \"" + (pathname != null ? pathname : "null") + "\"";
	}
	
	static public String getSaveErrorMessage(VirtualFile targetFile) {
		return getSaveErrorMessage(targetFile==null ? null : targetFile.toString());
	}
	
	static public String getSaveErrorMessage(File targetFile) {
		return getSaveErrorMessage(targetFile==null ? null : targetFile.toString());
	}
	
	static public String getSaveErrorMessage(String pathname) {
		return "Failed to save properties to \"" + (pathname != null ? pathname : "null") + "\"";
	}
	
	static public String getSettingFilePath(String path) {
		return Files.addExtension(path, FILE_EXT);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	/**
	 * このオブジェクトの保存先ファイルを、指定の抽象パスに更新する。
	 * 指定された抽象パスの拡張子が &quot;.prefs&quot; ではない場合、設定する抽象パスには &quot;.prefs&quot; が付加される。
	 * 指定された抽象パスと設定済みの抽象パスが等しい場合、このメソッドは何もしない。
	 * @param targetFile	設定する抽象パス
	 * @return	抽象パスが更新された場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	protected boolean updateSettingFile(File targetFile) {
		VirtualFile prefsFile = new DefaultFile(getSettingFilePath(targetFile.getAbsolutePath()));
		if (!prefsFile.equals(_propFile)) {
			setSettingFile(prefsFile);
			return true;
		} else {
			return false;
		}
		//setSettingFile(new DefaultFile(getSettingFilePath(targetFile.getAbsolutePath())));
	}

	/**
	 * このオブジェクトの保存先ファイルを、指定の抽象パスに更新する。
	 * 指定された抽象パスの拡張子が &quot;.prefs&quot; ではない場合、設定する抽象パスには &quot;.prefs&quot; が付加される。
	 * 指定された抽象パスと設定済みの抽象パスが等しい場合、このメソッドは何もしない。
	 * @param targetFile	設定する抽象パス
	 * @return	抽象パスが更新された場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	protected boolean updateSettingFile(VirtualFile targetFile) {
		VirtualFile prefsFile = targetFile.getFactory().newFile(getSettingFilePath(targetFile.getAbsolutePath()));
		if (!prefsFile.equals(_propFile)) {
			setSettingFile(prefsFile);
			return true;
		} else {
			return false;
		}
		//setSettingFile(targetFile.getFactory().newFile(getSettingFilePath(targetFile.getAbsolutePath())));		
	}

	/**
	 * 指定された抽象パスを、このオブジェクトの保存先ファイルに設定し、
	 * プロパティファイルの更新日時をリセット(0 に設定)する。
	 * @param targetFile	設定する抽象パス
	 */
	protected void setSettingFile(VirtualFile targetFile) {
		this._propFile = targetFile;
		_lastModified = 0L;
	}

	/**
	 * このメソッドは、{@link #rollback()} メソッドの内部で呼び出される。
	 * <code>rollback</code> メソッドでは、プロパティオブジェクトがクリアされた
	 * 後、初期プロパティを設定するため、このメソッドを呼び出す。
	 */
	protected void initializeProperties() {
		
	}

	/**
	 * 設定されている保存先からプロパティを読み込む。
	 * <p>
	 * このメソッドは、保存先が設定されている場合のみ、
	 * {@link #rollback()} から呼び出される。
	 * @return 正常に読み込めた場合は <tt>true</tt> を返す。
	 * @since 1.14
	 */
	protected boolean loadProperties() {
		if (_propFile == null)
			return false;

		boolean result;
		InputStream iStream = null;
		try {
			iStream = _propFile.getInputStream();
			props.loadFromStream(iStream);
			result = true;
		}
		catch (Throwable ex) {
			AppLogger.debug(getLoadErrorMessage(_propFile), ex);
			result = false;
		}
		finally {
			if (iStream != null) {
				Files.closeStream(iStream);
			}
		}
		
		if (result) {
			try {
				_lastModified = _propFile.lastModified();
			} catch (Throwable ex) {
				_lastModified = 0L;
			}
		}
		
		return result;
	}

	/**
	 * 設定されている保存先に現在のプロパティを保存する。
	 * 保存先が設定されていない場合は、何もしない。
	 * <p>
	 * このメソッドは、保存先が設定されている場合のみ、
	 * {@link #commit()} から呼び出される。
	 * @throws NullPointerException	保存先が設定されていない場合
	 * @throws IOException	入出力エラーが発生した場合
	 */
	protected void saveProperties() throws IOException
	{
		VirtualFile dir = _propFile.getParentFile();
		if (dir.exists()) {
			if (dir.isFile()) {
				String msg = "the specified directory=" + dir.getAbsolutePath()
						+ " is a file";
				throw new IOException(msg);
			} else if (!dir.isDirectory()) {
				String msg = "the specified directory=" + dir.getAbsolutePath()
						+ " is not known type";
				throw new IOException(msg);
			}
		} else {
			if (!dir.mkdirs()) {
				String msg = "failed to create the specified directory="
						+ dir.getAbsolutePath();
				throw new IOException(msg);
			}
		}
		OutputStream oStream = _propFile.getOutputStream();
		try {
			props.saveToStream(oStream, null);
		}
		finally {
			Files.closeStream(oStream);
		}
		
		// update last modified time
		try {
			_lastModified = _propFile.lastModified();
		} catch (Throwable ex) {
			_lastModified = 0L;
		}
	}
	
	protected URI getAbsoluteURIProperty(String key, URI defaultValue) {
		// 文字列を取得
		String path = this.props.getString(key, null);
		if (Strings.isNullOrEmpty(path)) {
			return defaultValue;
		}
		
		// URIは絶対パス
		URI uri;
		try {
			uri = new URI(path);
		} catch (Throwable ex) {
			AppLogger.debug("Failed to create URI from \"" + path + "\"", ex);
			uri = defaultValue;
		}
		return uri;
	}
	
	protected URI[] getAbsoluteURIsProperty(String key, URI[] defaultValues) {
		// 文字列を取得
		String[] paths = props.getStringArray(key, null);
		if (paths == null) {
			return defaultValues;
		} else if (paths.length < 1) {
			return EMPTY_URI_ARRAY;
		}
		
		if (AppLogger.isDebugEnabled()) {
			AppLogger.debug("AbstractSettings#getAbsoluteURIsProperty(" + String.valueOf(key) + ")");
		}
		
		// URI は絶対パス
		ArrayList<URI> uris = new ArrayList<URI>(paths.length);
		for (int i = 0; i < paths.length; i++) {
			String path = paths[i];
			if (!Strings.isNullOrEmpty(path)) {
				try {
					URI uri = new URI(path);
					if (AppLogger.isDebugEnabled()) {
						AppLogger.debug("    - created URI from path[" + i + "]=\"" + path + "\"");
					}
					uris.add(uri);
				} catch (Throwable ex) {
					AppLogger.debug("    - Failed to create URI from path[" + i + "]=\"" + path + "\"");
				}
			} else {
				AppLogger.debug("    - skip conversion cause path[" + i + "] is empty.");
			}
		}
		return uris.toArray(new URI[uris.size()]);
	}
	
	protected void setAbsoluteURIProperty(String key, URI uri) {
		if (uri == null) {
			props.clearProperty(key);
			return;
		}
		
		props.setString(key, uri.toString());
	}
	
	protected void setAbsoluteURIsProperty(String key, URI[] uris) {
		if (uris == null || uris.length < 1) {
			props.clearProperty(key);
			return;
		}
		
		ArrayList<String> uriStrings = new ArrayList<String>(uris.length);
		for (URI uri : uris) {
			if (uri != null) {
				uriStrings.add(uri.toString());
			}
		}
		if (uriStrings.isEmpty()) {
			props.clearProperty(key);
		} else {
			props.setStringArray(key, uriStrings.toArray(new String[uriStrings.size()]));
		}
	}

	/**
	 * 指定されたプロパティから、<code>java.io.File</code> オブジェクトを取得する。
	 * プロパティの値が相対パスであれば、基準パスからの相対パスに変換する。
	 * プロパティの値が絶対パスであれば、そのまま返す。
	 * 基準パスが<tt>null</tt> の場合は、標準の実装によって絶対パスに変換される。
	 * @param key			プロパティのキー
	 * @param basePath		基準とする抽象パス
	 * @param defaultValue	プロパティが存在しなかった場合のデフォルト値
	 * @return	<code>java.io.File</code> オブジェクト
	 * @since 1.14
	 */
	protected File getAbsoluteJavaFileProperty(String key, File basePath,
												File defaultValue)
	{
		// 文字列を取得
		String path = this.props.getString(key, null);
		if (path == null)
			return defaultValue;
		
		// 絶対パスに変換
		File file = new File(path);
		if (!file.isAbsolute() && basePath != null) {
			file = new File(basePath, path);
		}
		//--- 正規化
		File abFile = Files.normalizeFile(file);
		//if (AppLogger.isDebugEnabled()) {
		//	String msg = String.format("AbstractSettings#getAbsoluteJavaFileProperty(%s) - convert \"%s\" to \"%s\"",
		//			String.valueOf(key), path, abFile.getPath());
		//	AppLogger.debug(msg);
		//}
		
		return abFile;
	}
	
	/**
	 * 指定されたプロパティから、<code>VirtualFile</code> オブジェクトを取得する。
	 * プロパティの値が相対パスであれば、基準パスからの相対パスに変換する。
	 * プロパティの値が絶対パスであれば、そのまま返す。
	 * 基準パスが<tt>null</tt> の場合は、標準の実装によって絶対パスに変換される。
	 * <p>
	 * このメソッドは、パス文字列からファイルオブジェクトを生成するため <em>factory</em> を使用する。
	 * @param factory		ファイルオブジェクト生成用ファクトリ
	 * @param key			プロパティのキー
	 * @param basePath		基準とする抽象パス
	 * @param defaultValue	プロパティが存在しなかった場合のデフォルト値
	 * @return	<code>VirtualFile</code> オブジェクト
	 * @throws NullPointerException	<em>factory</em> が <tt>null</tt> の場合
	 * @since 1.14
	 */
	protected VirtualFile getAbsoluteVirtualFileProperty(VirtualFileFactory factory,
														  String key,
														  VirtualFile basePath,
														  VirtualFile defaultValue)
	{
		if (factory == null)
			throw new IllegalArgumentException("'factory' argument is null.");
		
		// 文字列を取得
		String path = this.props.getString(key, null);
		if (path == null)
			return defaultValue;
		
		// 絶対パスに変換
		VirtualFile file = factory.newFile(path);
		if (!file.isAbsolute() && basePath != null) {
			file = factory.newFile(basePath, path);
		}
		//--- 正規化
		VirtualFile abFile = file.getNormalizedFile();
		//if (AppLogger.isDebugEnabled()) {
		//	String msg = String.format("AbstractSettings#getAbsoluteVirtualFileProperty(%s) - convert \"%s\" to \"%s\"",
		//			String.valueOf(key), path, abFile.getPath());
		//	AppLogger.debug(msg);
		//}
		
		return abFile;
	}

	/**
	 * 指定されたプロパティから、<code>java.io.File</code> オブジェクトの配列を取得する。
	 * プロパティの値が相対パスであれば、基準パスからの相対パスに変換する。
	 * プロパティの値が絶対パスであれば、そのまま返す。
	 * 基準パスが<tt>null</tt> の場合は、標準の実装によって絶対パスに変換される。
	 * @param key				プロパティのキー
	 * @param basePath			基準とする抽象パス
	 * @param defaultValues		プロパティが存在しなかった場合のデフォルト値
	 * @return	プロパティが存在する場合は <code>java.io.File</code> オブジェクトの配列を返す。
	 * 			プロパティが存在しない場合は <em>defaultValues</em> を返す。
	 * @since 1.14
	 */
	protected File[] getAbsoluteJavaFilesProperty(String key, File basePath,
													File[] defaultValues)
	{
		// 文字列を取得
		String[] paths = props.getStringArray(key, null);
		if (paths == null) {
			return defaultValues;
		} else if (paths.length < 1) {
			return EMPTY_JAVAFILE_ARRAY;
		}
		
		if (AppLogger.isDebugEnabled()) {
			AppLogger.debug("AbstractSettings#getAbsoluteJavaFilesProperty(" + String.valueOf(key) + ")");
		}
		
		// 絶対パスに変換
		File[] retFiles = new File[paths.length];
		if (basePath != null) {
			for (int i = 0; i < paths.length; i++) {
				String path = (paths[i]==null ? "" : paths[i]);
				File file = new File(path);
				if (!file.isAbsolute()) {
					file = new File(basePath, path);
				}
				//--- 正規化
				file = Files.normalizeFile(file);
				//if (AppLogger.isDebugEnabled()) {
				//	String msg = String.format("    - convert \"%s\" to \"%s\"", path, file.getPath());
				//	AppLogger.debug(msg);
				//}
				retFiles[i] = file;
			}
		}
		else {
			for (int i = 0; i < paths.length; i++) {
				String path = (paths[i]==null ? "" : paths[i]);
				File file = new File(path);
				if (!file.isAbsolute()) {
					file = (new File(path)).getAbsoluteFile();
				}
				//--- 正規化
				file = Files.normalizeFile(file);
				//if (AppLogger.isDebugEnabled()) {
				//	String msg = String.format("    - convert \"%s\" to \"%s\"", path, file.getPath());
				//	AppLogger.debug(msg);
				//}
				retFiles[i] = file;
			}
			
		}
		
		return retFiles;
	}
	
	/**
	 * 指定されたプロパティから、<code>VirtualFile</code> オブジェクトの配列を取得する。
	 * プロパティの値が相対パスであれば、基準パスからの相対パスに変換する。
	 * プロパティの値が絶対パスであれば、そのまま返す。
	 * 基準パスが<tt>null</tt> の場合は、標準の実装によって絶対パスに変換される。
	 * <p>
	 * このメソッドは、パス文字列からファイルオブジェクトを生成するため <em>factory</em> を使用する。
	 * @param factory			ファイルオブジェクト生成用ファクトリ
	 * @param key				プロパティのキー
	 * @param basePath			基準とする抽象パス
	 * @param defaultValues		プロパティが存在しなかった場合のデフォルト値
	 * @return	プロパティが存在する場合は <code>VirtualFile</code> オブジェクトの配列を返す。
	 * 			プロパティが存在しない場合は <em>defaultValues</em> を返す。
	 * @throws NullPointerException	<em>factory</em> が <tt>null</tt> の場合
	 * @since 1.14
	 */
	protected VirtualFile[] getAbsoluteVirtualFilesProperty(VirtualFileFactory factory,
															 String key,
															 VirtualFile basePath,
															 VirtualFile[] defaultValues)
	{
		if (factory == null)
			throw new IllegalArgumentException("'factory' argument is null.");
		
		// 文字列を取得
		String[] paths = props.getStringArray(key, null);
		if (paths == null) {
			return defaultValues;
		} else if (paths.length < 1) {
			return EMPTY_VIRTUALFILE_ARRAY;
		}
		
		if (AppLogger.isDebugEnabled()) {
			AppLogger.debug("AbstractSettings#getAbsoluteVirtualFilesProperty(" + String.valueOf(key) + ")");
		}
		
		// 絶対パスに変換
		VirtualFile[] retFiles = new VirtualFile[paths.length];
		if (basePath != null) {
			for (int i = 0; i < paths.length; i++) {
				String path = (paths[i]==null ? "" : paths[i]);
				//--- パス生成
				VirtualFile file = factory.newFile(path);
				if (!file.isAbsolute()) {
					file = factory.newFile(basePath, path);
				}
				//--- 正規化
				file = file.getNormalizedFile();
				//if (AppLogger.isDebugEnabled()) {
				//	String msg = String.format("    - convert \"%s\" to \"%s\"", path, file.getPath());
				//	AppLogger.debug(msg);
				//}
				retFiles[i] = file;
			}
		}
		else {
			for (int i = 0; i < paths.length; i++) {
				String path = (paths[i]==null ? "" : paths[i]);
				//--- パス生成
				VirtualFile file = factory.newFile(path);
				if (!file.isAbsolute()) {
					file = file.getAbsoluteFile();
				}
				//--- 正規化
				file = file.getNormalizedFile();
				//if (AppLogger.isDebugEnabled()) {
				//	String msg = String.format("    - convert \"%s\" to \"%s\"", path, file.getPath());
				//	AppLogger.debug(msg);
				//}
				retFiles[i] = file;
			}
		}
		
		return retFiles;
	}
	
	/**
	 * <em>file</em> を指定されたプロパティに保存する。
	 * <em>basePath</em> が <tt>null</tt> 以外の場合は、<em>basePath</em> の
	 * 相対パスに変換された後に保存される。相対パスが構成できない場合は絶対パスで保存される。
	 * <p>
	 * <em>file</em> が <tt>null</tt> の場合、指定されたプロパティが破棄される。
	 * @param key			プロパティのキー
	 * @param basePath		基準とする抽象パス
	 * @param file			保存する抽象パス
	 * @since 1.14
	 */
	protected void setAbsoluteJavaFileProperty(String key, File basePath, File file)
	{
		if (file == null) {
			props.clearProperty(key);
			return;
		}
		
		// 相対パスに変換
		String relPath;
		if (basePath != null) {
			relPath = Files.convertAbsoluteToRelativePath(basePath, file, Files.CommonSeparatorChar);
		}
		else {
			relPath = Files.normalizePath(file.getAbsolutePath(), Files.CommonSeparatorChar);
		}
		if (Strings.isNullOrEmpty(relPath)) {
			//--- 相対パスが空文字の場合、カレントを表す "." とする。
			relPath = ".";
		}
		
		//if (AppLogger.isDebugEnabled()) {
		//	String msg = String.format("AbstractSettings#setAbsoluteJavaFileProperty(%s) - convert \"%s\" to \"%s\"",
		//			String.valueOf(key), file.getPath(), relPath);
		//	AppLogger.debug(msg);
		//}
		
		// プロパティ更新
		props.setString(key, relPath);
	}
	
	/**
	 * <em>file</em> を指定されたプロパティに保存する。
	 * <em>basePath</em> が <tt>null</tt> 以外の場合は、<em>basePath</em> の
	 * 相対パスに変換された後に保存される。相対パスが構成できない場合は絶対パスで保存される。
	 * <p>
	 * <em>file</em> が <tt>null</tt> の場合、指定されたプロパティが破棄される。
	 * @param key			プロパティのキー
	 * @param basePath		基準とする抽象パス
	 * @param file			保存する抽象パス
	 * @since 1.14
	 */
	protected void setAbsoluteVirtualFileProperty(String key, VirtualFile basePath, VirtualFile file)
	{
		if (file == null) {
			props.clearProperty(key);
			return;
		}
		
		// 相対パスに変換
		String relPath;
		if (basePath != null) {
			relPath = file.relativePathFrom(basePath, Files.CommonSeparatorChar);
		}
		else {
			relPath = file.getAbsoluteFile().getNormalizedPath(Files.CommonSeparatorChar);
		}
		if (Strings.isNullOrEmpty(relPath)) {
			//--- 相対パスが空文字の場合、カレントを表す "." とする。
			relPath = ".";
		}
		
		//if (AppLogger.isDebugEnabled()) {
		//	String msg = String.format("AbstractSettings#setAbsoluteVirtualFileProperty(%s) - convert \"%s\" to \"%s\"",
		//			String.valueOf(key), file.getPath(), relPath);
		//	AppLogger.debug(msg);
		//}
		
		// プロパティ更新
		props.setString(key, relPath);
	}
	
	
	/**
	 * <em>files</em> を指定されたプロパティに保存する。
	 * <em>basePath</em> が <tt>null</tt> 以外の場合は、<em>basePath</em> の
	 * 相対パスに変換された後に保存される。相対パスが構成できない場合は絶対パスで保存される。
	 * <p>
	 * <em>files</em> が <tt>null</tt> もしくは空の配列の場合、指定されたプロパティが破棄される。
	 * @param key			プロパティのキー
	 * @param basePath		基準とする抽象パス
	 * @param files			保存する抽象パスの配列
	 * @throws NullPointerException	<em>files</em> の要素が <tt>null</tt> の場合
	 * @since 1.14
	 */
	protected void setAbsoluteJavaFilesProperty(String key, File basePath, File[] files)
	{
		if (files == null || files.length < 1) {
			props.clearProperty(key);
			return;
		}
		
		if (AppLogger.isDebugEnabled()) {
			AppLogger.debug("AbstractSettings#setAbsoluteJavaFilesProperty(" + String.valueOf(key) + ")");
		}
		
		// 相対パスに変換
		String[] relPaths = new String[files.length];
		if (basePath != null) {
			for (int i = 0; i < files.length; i++) {
				String rpath = Files.convertAbsoluteToRelativePath(basePath, files[i], Files.CommonSeparatorChar);
				if (Strings.isNullOrEmpty(rpath)) {
					//--- 相対パスが空文字の場合、カレントを表す "." とする。
					rpath = ".";
				}
				//if (AppLogger.isDebugEnabled()) {
				//	String msg = String.format("    - convert \"%s\" to \"%s\"", files[i].getPath(), rpath);
				//	AppLogger.debug(msg);
				//}
				relPaths[i] = rpath;
			}
		}
		else {
			for (int i = 0; i < files.length; i++) {
				String rpath = Files.normalizePath(files[i].getAbsolutePath(), Files.CommonSeparatorChar);
				//if (Strings.isNullOrEmpty(rpath)) {
				//	//--- 相対パスが空文字の場合、カレントを表す "." とする。
				//	rpath = ".";
				//}
				//if (AppLogger.isDebugEnabled()) {
				//	String msg = String.format("    - convert \"%s\" to \"%s\"", files[i].getPath(), rpath);
				//	AppLogger.debug(msg);
				//}
				relPaths[i] = rpath;
			}
		}
		
		// プロパティ更新
		props.setStringArray(key, relPaths);
	}
	
	/**
	 * <em>files</em> を指定されたプロパティに保存する。
	 * <em>basePath</em> が <tt>null</tt> 以外の場合は、<em>basePath</em> の
	 * 相対パスに変換された後に保存される。相対パスが構成できない場合は絶対パスで保存される。
	 * <p>
	 * <em>files</em> が <tt>null</tt> もしくは空の配列の場合、指定されたプロパティが破棄される。
	 * @param key			プロパティのキー
	 * @param basePath		基準とする抽象パス
	 * @param files			保存する抽象パスの配列
	 * @throws NullPointerException	<em>files</em> の要素が <tt>null</tt> の場合
	 * @since 1.14
	 */
	protected void setAbsoluteVirtualFilesProperty(String key, VirtualFile basePath, VirtualFile[] files)
	{
		if (files == null || files.length < 1) {
			props.clearProperty(key);
			return;
		}
		
		if (AppLogger.isDebugEnabled()) {
			AppLogger.debug("AbstractSettings#setAbsoluteVirtualFilesProperty(" + String.valueOf(key) + ")");
		}
		
		// 相対パスに変換
		String[] relPaths = new String[files.length];
		if (basePath != null) {
			for (int i = 0; i < files.length; i++) {
				String rpath = files[i].relativePathFrom(basePath, Files.CommonSeparatorChar);
				if (Strings.isNullOrEmpty(rpath)) {
					//--- 相対パスが空文字の場合、カレントを表す "." とする。
					rpath = ".";
				}
				//if (AppLogger.isDebugEnabled()) {
				//	String msg = String.format("    - convert \"%s\" to \"%s\"", files[i].getPath(), rpath);
				//	AppLogger.debug(msg);
				//}
				relPaths[i] = rpath;
			}
		}
		else {
			for (int i = 0; i < files.length; i++) {
				String rpath = files[i].getAbsoluteFile().getNormalizedPath(Files.CommonSeparatorChar);
				//if (Strings.isNullOrEmpty(rpath)) {
				//	//--- 相対パスが空文字の場合、カレントを表す "." とする。
				//	rpath = ".";
				//}
				//if (AppLogger.isDebugEnabled()) {
				//	String msg = String.format("    - convert \"%s\" to \"%s\"", files[i].getPath(), rpath);
				//	AppLogger.debug(msg);
				//}
				relPaths[i] = rpath;
			}
		}
		
		// プロパティ更新
		props.setStringArray(key, relPaths);
	}
	
	/**
	 * 指定されたプロパティから、<code>java.io.File</code> オブジェクトを取得する。
	 * プロパティの値が相対パスであれば、保存先抽象パスからの相対パスに変換する。
	 * プロパティの値が絶対パスであれば、そのまま返す。
	 * 保存先抽象パスが設定されていない場合は、Java標準の実装により絶対パスに変換される。
	 * @param key			プロパティのキー
	 * @param defaultValue	プロパティが存在しなかった場合のデフォルト値
	 * @return	<code>java.io.File</code> オブジェクト
	 * @throws IllegalStateException	保存先抽象パスが <code>java.io.File</code> オブジェクトに変換できない場合
	 * @since 1.14
	 */
	protected File getAbsoluteJavaFileProperty(String key, File defaultValue)
	{
		File propFile = getJavaPropertyFile();
		if (propFile == null) {
			return getAbsoluteJavaFileProperty(key, null, defaultValue);
		} else {
			return getAbsoluteJavaFileProperty(key, propFile.getParentFile(), defaultValue);
		}
	}
	
	/**
	 * 指定されたプロパティから、<code>java.io.File</code> オブジェクトの配列を取得する。
	 * プロパティの値が相対パスであれば、保存先抽象パスからの相対パスに変換する。
	 * プロパティの値が絶対パスであれば、そのまま返す。
	 * 保存先抽象パスが設定されていない場合は、Java標準の実装により絶対パスに変換される。
	 * @param key				プロパティのキー
	 * @param defaultValues		プロパティが存在しなかった場合のデフォルト値
	 * @return	プロパティが存在する場合は <code>java.io.File</code> オブジェクトの配列を返す。
	 * 			プロパティが存在しない場合は <em>defaultValues</em> を返す。
	 * @throws IllegalStateException	保存先抽象パスが <code>java.io.File</code> オブジェクトに変換できない場合
	 * @since 1.14
	 */
	protected File[] getAbsoluteJavaFilesProperty(String key, File[] defaultValues)
	{
		File propFile = getJavaPropertyFile();
		if (propFile == null) {
			return getAbsoluteJavaFilesProperty(key, null, defaultValues);
		} else {
			return getAbsoluteJavaFilesProperty(key, propFile.getParentFile(), defaultValues);
		}
	}
	
	/**
	 * 指定されたプロパティから、<code>VirtualFile</code> オブジェクトを取得する。
	 * プロパティの値が相対パスであれば、保存先抽象パスからの相対パスに変換する。
	 * プロパティの値が絶対パスであれば、そのまま返す。
	 * 保存先抽象パスが設定されていない場合、<code>java.io.File</code> オブジェクトの
	 * 実装を利用して抽象パスを生成する。
	 * @param key			プロパティのキー
	 * @param defaultValue	プロパティが存在しなかった場合のデフォルト値
	 * @return	<code>VirtualFile</code> オブジェクト
	 * @since 1.14
	 */
	protected VirtualFile getAbsoluteVirtualFileProperty(String key, VirtualFile defaultValue)
	{
		VirtualFile propFile = getVirtualPropertyFile();
		if (propFile == null) {
			return getAbsoluteVirtualFileProperty(DefaultFileFactory.getInstance(),
													key, null, defaultValue);
		} else {
			return getAbsoluteVirtualFileProperty(propFile.getFactory(), key,
													propFile.getParentFile(), defaultValue);
		}
	}
	
	/**
	 * 指定されたプロパティから、<code>VirtualFile</code> オブジェクトの配列を取得する。
	 * プロパティの値が相対パスであれば、保存先抽象パスからの相対パスに変換する。
	 * プロパティの値が絶対パスであれば、そのまま返す。
	 * 保存先抽象パスが設定されていない場合、<code>java.io.File</code> オブジェクトの
	 * 実装を利用して抽象パスを生成する。
	 * @param key				プロパティのキー
	 * @param defaultValues		プロパティが存在しなかった場合のデフォルト値
	 * @return	プロパティが存在する場合は <code>VirtualFile</code> オブジェクトの配列を返す。
	 * 			プロパティが存在しない場合は <em>defaultValues</em> を返す。
	 * @since 1.14
	 */
	protected VirtualFile[] getAbsoluteVirtualFilesProperty(String key, VirtualFile[] defaultValues)
	{
		VirtualFile propFile = getVirtualPropertyFile();
		if (propFile == null) {
			return getAbsoluteVirtualFilesProperty(DefaultFileFactory.getInstance(),
													key, null, defaultValues);
		} else {
			return getAbsoluteVirtualFilesProperty(propFile.getFactory(), key,
													propFile.getParentFile(), defaultValues);
		}
	}
	
	/**
	 * <em>file</em> を指定されたプロパティに保存する。
	 * 保存先が設定されている場合は、保存先抽象パスからの
	 * 相対パスに変換された後に保存される。
	 * 相対パスが構成できない場合は絶対パスで保存される。
	 * <p>
	 * <em>file</em> が <tt>null</tt> の場合、指定されたプロパティが破棄される。
	 * @param key			プロパティのキー
	 * @param basePath		基準とする抽象パス
	 * @param file			保存する抽象パス
	 * @throws IllegalStateException	保存先抽象パスが <code>java.io.File</code> オブジェクトに変換できない場合
	 * @since 1.14
	 */
	protected void setAbsoluteJavaFileProperty(String key, File file)
	{
		File propFile = getJavaPropertyFile();
		if (propFile == null) {
			setAbsoluteJavaFileProperty(key, null, file);
		} else {
			setAbsoluteJavaFileProperty(key, propFile.getParentFile(), file);
		}
	}
	
	/**
	 * <em>files</em> を指定されたプロパティに保存する。
	 * 保存先が設定されている場合は、保存先抽象パスからの
	 * 相対パスに変換された後に保存される。
	 * 相対パスが構成できない場合は絶対パスで保存される。
	 * <p>
	 * <em>files</em> が <tt>null</tt> もしくは空の配列の場合、指定されたプロパティが破棄される。
	 * @param key			プロパティのキー
	 * @param basePath		基準とする抽象パス
	 * @param files			保存する抽象パスの配列
	 * @since 1.14
	 * @throws NullPointerException	<em>files</em> の要素が <tt>null</tt> の場合
	 * @throws IllegalStateException	保存先抽象パスが <code>java.io.File</code> オブジェクトに変換できない場合
	 */
	protected void setAbsoluteJavaFilesProperty(String key, File[] files)
	{
		File propFile = getJavaPropertyFile();
		if (propFile == null) {
			setAbsoluteJavaFilesProperty(key, null, files);
		} else {
			setAbsoluteJavaFilesProperty(key, propFile.getParentFile(), files);
		}
	}
	
	/**
	 * <em>file</em> を指定されたプロパティに保存する。
	 * 保存先が設定されている場合は、保存先抽象パスからの
	 * 相対パスに変換された後に保存される。
	 * 相対パスが構成できない場合は絶対パスで保存される。
	 * <p>
	 * <em>file</em> が <tt>null</tt> の場合、指定されたプロパティが破棄される。
	 * @param key			プロパティのキー
	 * @param basePath		基準とする抽象パス
	 * @param file			保存する抽象パス
	 * @since 1.14
	 */
	protected void setAbsoluteVirtualFileProperty(String key, VirtualFile file)
	{
		VirtualFile propFile = getVirtualPropertyFile();
		if (propFile == null) {
			setAbsoluteVirtualFileProperty(key, null, file);
		} else {
			setAbsoluteVirtualFileProperty(key, propFile.getParentFile(), file);
		}
	}
	
	/**
	 * <em>files</em> を指定されたプロパティに保存する。
	 * 保存先が設定されている場合は、保存先抽象パスからの
	 * 相対パスに変換された後に保存される。
	 * 相対パスが構成できない場合は絶対パスで保存される。
	 * <p>
	 * <em>files</em> が <tt>null</tt> もしくは空の配列の場合、指定されたプロパティが破棄される。
	 * @param key			プロパティのキー
	 * @param basePath		基準とする抽象パス
	 * @param files			保存する抽象パスの配列
	 * @throws NullPointerException	<em>files</em> の要素が <tt>null</tt> の場合
	 * @since 1.14
	 */
	protected void setAbsoluteVirtualFilesProperty(String key, VirtualFile[] files)
	{
		VirtualFile propFile = getVirtualPropertyFile();
		if (propFile == null) {
			setAbsoluteVirtualFilesProperty(key, null, files);
		} else {
			setAbsoluteVirtualFilesProperty(key, propFile.getParentFile(), files);
		}
	}
}
