/*
 * @(#)LibFileInfo.java	4.0.0	2021/08/27 : for Java11
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.module.setting;

import java.io.File;

/**
 * 1 つのライブラリファイルの情報を保持するクラス。
 * <blockquote>
 * このクラスの実装は、変更が影響しないよう完全なクローンに対応する。
 * </blockquote>
 * 
 * @version 4.0.0
 * @since 4.0.0
 */
public class LibFileInfo implements Cloneable
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	/** ライブラリファイルの種類(現在は jar のみ) **/
	private LibFileType	_filetype;
	/** ライブラリ名、もしくは <tt>null</tt> **/
	private String		_libname;
	/** ライブラリファイルの絶対パス **/
	private File		_libfile;
	/** ライセンスの名称 **/
	private String		_licenseName;
	/** ライセンス情報が記述されたローカルファイルの絶対パス **/
	private File		_licenseFile;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public LibFileInfo()
	{
		this(LibFileType.JAR);
	}
	
	public LibFileInfo(LibFileType filetype)
	{
		this._filetype = (filetype==null ? LibFileType.JAR : filetype);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	/**
	 * このオブジェクトインスタンスのクローンを生成する。
	 * このメソッドが返すインスタンスは、クローン元の内容に影響しないディープコピーとなる。
	 * @return このオブジェクトインスタンスのディープコピーとなるクローン
	 * @since 4.0.0
	 */
	@Override
	public LibFileInfo clone()
	{
		try {
			return (LibFileInfo)super.clone();
		}
		catch (CloneNotSupportedException ex) {
			throw new InternalError(ex);
		}
	}
	
	public LibFileType getFileType()
	{
		return _filetype;
	}
	
	public void setFileType(LibFileType filetype)
	{
		_filetype = (filetype==null ? LibFileType.JAR : filetype);
	}
	
	public boolean isEmptyLibName()
	{
		return (_libname == null);
	}
	
	public String getLibName()
	{
		return _libname;
	}
	
	public void setLibName(String name)
	{
		if (name != null && !name.isEmpty()) {
			_libname = name;
		} else {
			_libname = null;
		}
	}
	
	public File getLibFile()
	{
		return _libfile;
	}
	
	public void setLibFile(File file)
	{
		_libfile = file;
	}
	
	public String getAvailableName()
	{
		if (_libname != null)
			return _libname;
		else if (_libfile != null)
			return _libfile.getName();
		else
			return "";
	}
	
	public boolean hasLicense()
	{
		return (_licenseName != null);
	}
	
	public String getLicenseName()
	{
		return _licenseName;
	}
	
	public void setLicenseName(String name)
	{
		if (name != null && !name.isEmpty()) {
			_licenseName = name;
		} else {
			_licenseName = null;
		}
	}
	
	public File getLicenseFile()
	{
		return _licenseFile;
	}
	
	public void setLicenseFile(File file)
	{
		_licenseFile = file;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
