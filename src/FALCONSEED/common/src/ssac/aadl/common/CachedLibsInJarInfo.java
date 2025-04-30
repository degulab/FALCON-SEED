/*
 * @(#)AppSettings.java	4.0.0	2021/08/27 : for Java11
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.common;

import java.io.File;

import ssac.aadl.module.setting.LibsInJarInfo;

/**
 * AADLランタイムライブラリ等の情報を保持するライブラリ情報ファイルのキャッシュ。
 * <p>アプリケーションごとに、対象とするライブラリ情報ファイルを指定し、インスタンスを保持する。
 * また、動的にファイルが更新された場合にも再読み込みに対応する。
 * <p>なお、対象とするファイルが存在しない場合や読み込みエラーが発生した場合は、キャッシュ無効となる。
 * <blockquote>
 * このオブジェクトは、スレッドセーフである。
 * </blockquote>
 * 
 * @version 4.0.0
 * @since 4.0.0
 */
public class CachedLibsInJarInfo
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static protected final String[]	EMPTY_LIBPATHS = new String[0];

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	/** 読み込み対象のライブラリ情報ファイルの絶対パス **/
	private final File	_targetFile;
	
	/** 有効なキャッシュ読み込み時の、ライブラリ情報ファイルの最終更新日時を示すエポックミリ秒、キャッシュが無効なら 0 **/
	private long		_cachedLastModified;
	/** キャッシュされたライブラリ情報オブジェクト、キャッシュが無効なら <tt>null</tt> **/
	private LibsInJarInfo	_cache;
	/** キャッシュられたライブラリ情報に含まれるライブラリファイルの絶対パスの配列、キャッシュが無効なら要素が空の配列 **/
	private String[]		_libpaths = EMPTY_LIBPATHS;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	/**
	 * 指定されたライブラリ情報ファイルを読み込み対象に指定して、新しいインスタンスを生成する。
	 * @param targetFile	読み込み対象のライブラリ情報ファイルの抽象パス、相対パスの場合はカレントディレクトリが読み込み対象となる
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	public CachedLibsInJarInfo(File targetFile)
	{
		if (targetFile == null) throw new NullPointerException("Target file is null.");
		_targetFile = (targetFile.isAbsolute() ? targetFile : targetFile.getAbsoluteFile());
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	/**
	 * このオブジェクトに指定されている情報ファイルの絶対パスを返す。
	 * @return	ライブラリ情報ファイルの絶対パス
	 */
	public File getTargetFile()
	{
		return _targetFile;
	}
	
	/**
	 * キャッシュが有効かどうかを判定する。
	 * @return	キャッシュが有効なら <tt>true</tt>、キャッシュが無効もしくは読み込まれていない場合は <tt>false</tt>
	 */
	public synchronized boolean isAvailable()
	{
		if (_cache != null) {
			//--- ファイルが存在しないかディレクトリなら、キャッシュ無効
			if (_targetFile.exists() && _targetFile.isFile()) {
				//--- キャッシュされた情報の最終更新日時以降にライブラリ情報ファイルが更新されていれば、キャッシュ無効
				if (_cachedLastModified >= _targetFile.lastModified()) {
					// キャッシュ有効
					return true;
				}
			}
			//--- キャッシュ無効化
			clearCache();
		}
		// キャッシュ無効
		return false;
	}
	
	/**
	 * キャッシュを強制的に更新する。
	 * @return	更新後のキャッシュが有効なら <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public synchronized boolean refresh()
	{
		refresh();
		return (_cache != null);
	}
	
	/**
	 * 必要に応じてキャッシュを更新し、キャッシュされているライブラリ情報オブジェクトを取得する。
	 * @return	キャッシュされているライブラリ情報オブジェクト、キャッシュが無効の場合は <tt>null</tt>
	 * <blockquote>
	 * <b>注意：</b>このメソッドが返すオブジェクト変更した場合はキャッシュの内容も変更されるので、参照のみに利用すること。
	 * </blockquote>
	 */
	public synchronized LibsInJarInfo getCachedObject()
	{
		updateCache();
		return _cache;
	}
	
	/**
	 * 必要に応じてキャッシュを更新し、キャッシュされているライブラリ情報オブジェクトのクローンを取得する。
	 * クローンされたオブジェクトを返すため、キャッシュされているオブジェクトに影響しない。
	 * @return	キャッシュされているライブラリ情報オブジェクトのクローン、キャッシュが無効の場合は要素が空の新しいライブラリ情報オブジェクト
	 */
	public synchronized LibsInJarInfo getClonedObject()
	{
		updateCache();
		return (_cache==null ? new LibsInJarInfo() : _cache.clone());
	}
	
	/**
	 * 必要に応じてキャッシュを更新し、キャッシュされているライブラリ情報のすべてのパスを保持する配列を取得する。
	 * @return	キャッシュされているライブラリ情報が保持するライブラリパスの配列、キャッシュが無効の場合は要素が空の配列
	 */
	public synchronized String[] getLibraryPaths()
	{
		updateCache();
		return _libpaths;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	/**
	 * キャッシュ無効に設定する
	 */
	protected void clearCache()
	{
		_cachedLastModified = 0L;
		_cache = null;
		_libpaths = EMPTY_LIBPATHS;
	}
	
	/**
	 * キャッシュを強制的に更新する
	 */
	protected void refreshCache()
	{
		try {
			LibsInJarInfo prop = new LibsInJarInfo();
			prop.loadFromFile(_targetFile);
			_cachedLastModified = _targetFile.lastModified();
			_cache = prop;
			_libpaths = new String[prop.size()];
			for (int i = 0; i < prop.size(); ++i) {
				_libpaths[i] = prop.get(i).getLibFile().getAbsolutePath();
			}
		}
		catch (Throwable ex) {
			clearCache();
		}
	}
	
	/**
	 * キャッシュを最新の状態に更新する。
	 */
	protected void updateCache()
	{
		if (_cache != null) {
			//--- ファイルが存在しないかディレクトリなら、キャッシュ無効
			if (_targetFile.exists() && _targetFile.isFile()) {
				//--- キャッシュされた情報の最終更新日時以降にライブラリ情報ファイルが更新されていれば、キャッシュ無効
				if (_cachedLastModified >= _targetFile.lastModified()) {
					// キャッシュ有効
					return;
				}
			}
		}
		
		refreshCache();
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
