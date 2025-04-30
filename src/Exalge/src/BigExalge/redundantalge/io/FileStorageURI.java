/*
 * @(#)FileStorageURI.java	0.991	2019/02/23
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package redundantalge.io;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import redundantalge.net.StorageURI;

/**
 * ファイルをストレージとする代数オブジェクトの場所に関するインタフェース。
 * <p>このオブジェクトの実装は、不変オブジェクトとする。
 * 
 * @version 0.991
 * @since 0.991
 * 
 * @author H.Deguchi(SOARS Project.)
 * @author Y.Ishizuka(PieCake.inc,)
 */
public class FileStorageURI implements StorageURI
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	/** すべてのローカルファイルを表すスキーマ **/
	static public final String SCHEME_FILE		= "file";
	/** CSV 型式のローカルファイルを表すスキーマ **/
	static public final String SCHEME_CSVFILE	= "csvfile";
	/** XML 型式のローカルファイルを表すスキーマ **/
	static public final String SCHEME_XMLFILE	= "xmlfile";

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	/** スキーマ **/
	private final String	_scheme;
	/** 指定されたパスの拡張子 **/
	private final String	_ext;
	/** ファイルの場所 **/
	private final File		_file;
	
	/** このオブジェクトの文字列表現 **/
	private String			_string;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	/**
	 * 指定されたパラメータで、新しいインスタンスを生成する。
	 * @param struri	ファイルの場所を示す URI もしくはパス文字列
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	ファイルの場所を示す URI として不適切な場合
	 */
	public FileStorageURI(String struri) {
		if (struri == null)
			throw new NullPointerException("String argument is null.");
		URI uri;
		File f;
		try {
			uri = new URI(struri);
		} catch (URISyntaxException ex) {
			throw new IllegalArgumentException("Illegal format of URI : " + struri, ex);
		}
		//--- check
		String strScheme = uri.getScheme();
		String strPath   = uri.getPath();
		if (uri.isAbsolute()) {
			String strSchemePart = uri.getRawSchemeSpecificPart();
			if (strSchemePart.startsWith("//")) {
				//--- absolute
				String strAuth = uri.getAuthority();
				String strHost = uri.getHost();
				if (strAuth != null || strHost != null) {
					// invalid format
					throw new IllegalArgumentException("Illegal format of URI : " + struri);
				}
				f = new File(strPath);
			}
			else {
				//--- relative path
				f = new File(strPath);
			}
		}
		else {
			//--- relative
			f = new File(strPath);
		}
		_file = f;
		//--- get extension
		String name = f.getName();
		int idx = name.lastIndexOf('.');
		if (!".".equals(name) && idx > 0) {
			_ext = name.substring(idx+1);
		} else {
			_ext = null;
		}
		//--- check scheme
		if (strScheme != null) {
			_scheme = strScheme;
		}
		else if (_ext != null) {
			_scheme = getSchemeFromExtension(_ext);
		}
		else {
			_scheme = SCHEME_FILE;
		}
	}
	
	/**
	 * 指定されたパラメータで、新しいインスタンスを生成する。
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @param targetFile	ファイルの場所
	 */
	public FileStorageURI(File targetFile) {
		this(null, targetFile);
	}

	/**
	 * 指定されたパラメータで、新しいインスタンスを生成する。
	 * <p><em>scheme</em> が <tt>null</tt> の場合、ファイルの拡張子からファイルの種類を判定する。
	 * ファイルの種類が不明の場合は、{@link #SCHEME_FILE} が設定される。
	 * @param scheme	ファイルの種類を表すスキーマ
	 * @param targetFile	ファイルの場所
	 * @throws NullPointerException	<em>targetFile</em> が <tt>null</tt> の場合
	 */
	public FileStorageURI(String scheme, File targetFile) {
		_file = targetFile;
		String name = targetFile.getName();
		int idx = name.lastIndexOf('.');
		if (!".".equals(name) && idx > 0) {
			_ext = name.substring(idx+1);
		} else {
			_ext = null;
		}
		if (_ext != null) {
			_scheme = getSchemeFromExtension(_ext);
		}
		else {
			_scheme = SCHEME_FILE;
		}
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	/**
	 * ドットを含まない拡張子を表す文字列から、対応するスキーマを取得する。
	 * @param ext	判定する拡張子
	 * @return	対応するスキーマ、対応がない場合は {@link #SCHEME_FILE}
	 */
	static public String getSchemeFromExtension(String ext) {
		if ("csv".equalsIgnoreCase(ext)) {
			return SCHEME_CSVFILE;
		}
		else if ("xml".equalsIgnoreCase(ext)) {
			return SCHEME_XMLFILE;
		}
		else {
			return SCHEME_FILE;
		}
	}
	
	/**
	 * このオブジェクトの設定されているスキーマを返す。
	 * @return	スキーマを表す文字列
	 */
	@Override
	public String getScheme() {
		return _scheme;
	}
	
	/**
	 * この URI が絶対パスを表すかどうかを判定する。
	 * @return	絶対パスを表す場合は <tt>true</tt>
	 */
	public boolean isAbsolute() {
		return _file.isAbsolute();
	}
	
	/**
	 * ファイルのパスに含まれる拡張子を返す。
	 * 拡張子がない場合は <tt>null</tt> を返す。
	 * @return	ファイルの拡張子、拡張子がない場合は <tt>null</tt>
	 */
	public String getExtension() {
		return _ext;
	}
	
	/**
	 * ファイルの場所を返す。
	 * @return	ファイルの場所を表すオブジェクト
	 */
	public File getFile() {
		return _file;
	}

	/**
	 * このオブジェクトのハッシュ値を返す。
	 * @return	ハッシュ値
	 */
	@Override
	public int hashCode() {
		int h = (_scheme==null ? 0 : _scheme.hashCode());
		h = 31 * h + _file.hashCode();
		return h;
	}

	/**
	 * 指定されたオブジェクトと自身の内容が等しいかを判定する。
	 * @param obj	判定するオブジェクト
	 * @return	等しい場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		
		if (obj != null && obj.getClass().equals(this.getClass())) {
			FileStorageURI that = (FileStorageURI)obj;
			if (_scheme.equals(that._scheme) && _file.equals(that._file)) {
				return true;
			}
		}
		
		return false;
	}

	/**
	 * このオブジェクトの文字列表現を返す。
	 * @return	このオブジェクトの文字列表現
	 */
	@Override
	public String toString() {
		if (_string == null) {
			StringBuilder sb = new StringBuilder();
			if (_scheme != null) {
				sb.append(_scheme);
				sb.append(':');
			}
			if (_file.isAbsolute()) {
				//--- absolute path
				sb.append("//");
				if (File.separatorChar == '\\') {
					sb.append('/');
					String spath = _file.getPath();
					spath = spath.replaceAll("\\\\", "/");
					sb.append(spath);
				}
				else {
					sb.append(_file.getPath());
				}
			}
			else {
				//--- relative path
				if (File.separatorChar == '\\') {
					String spath = _file.getPath();
					spath = spath.replaceAll("\\\\", "/");
					sb.append(spath);
				} else {
					sb.append(_file.getPath());
				}
			}
		}
		return _string;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
