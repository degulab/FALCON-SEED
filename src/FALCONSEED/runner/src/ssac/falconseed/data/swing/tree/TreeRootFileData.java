/*
 * @(#)TreeRootFileData.java	1.10	2011/02/14
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.data.swing.tree;

import ssac.util.io.VirtualFile;

/**
 * ツリーのユーザー定義ルートとなる抽象パスを保持するファイルモデル。
 * このオブジェクトは、基本的に単一の抽象パスと表示名を保持する。
 * このオブジェクトは不変オブジェクトである。
 * 
 * @version 1.10	2011/02/14
 * @since 1.10
 */
public class TreeRootFileData extends TreeFileData
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** このパスを基準とするユーザー定義ルートの表示名 **/
	private final String	_displayName;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public TreeRootFileData(VirtualFile file) {
		this(null, file);
	}
	
	public TreeRootFileData(String displayName, VirtualFile file) {
		super(file);
		this._displayName = displayName;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public boolean equalsDisplayName(Object obj) {
		if (obj == null || obj instanceof String) {
			return isDisplayNameEquals(getDisplayString(), (String)obj);
		}
		
		// not equals
		return false;
	}

	@Override
	public String getDisplayName() {
		if (_displayName != null) {
			return _displayName;
		} else {
			return super.getDisplayName();
		}
	}
	
	@Override
	public ITreeFileData replaceFile(VirtualFile newFile) {
		return new TreeRootFileData(getDisplayString(), newFile);
	}

	@Override
	public int hashCode() {
		int h = (_displayName==null ? 0 : _displayName.hashCode());
		return (31 * h + super.hashCode());
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		
		if (obj instanceof TreeRootFileData) {
			TreeRootFileData aData = (TreeRootFileData)obj;
			if (isDisplayNameEquals(aData._displayName, this._displayName) && aData.getFile().equals(this.getFile())) {
				return true;
			}
		}
		
		return false;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	/**
	 * このオブジェクトに設定されている表示名を取得する。
	 * @return	設定されている表示名を返す。設定されていない場合は <tt>null</tt> を返す。
	 */
	protected String getDisplayString() {
		return _displayName;
	}

	/**
	 * 表示名が等しいかを判定する。このメソッドは大文字小文字を区別する。
	 * @param name1	判定する表示名
	 * @param name2	判定する表示名のもう一方
	 * @return	等しい場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	static protected final boolean isDisplayNameEquals(String name1, String name2) {
		if (name1 == name2) {
			return true;
		}
		else if (name1 != null && name1.equals(name2)) {
			return true;
		}
		else if (name2 != null && name2.equals(name1)) {
			return true;
		}
		else {
			return false;
		}
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
