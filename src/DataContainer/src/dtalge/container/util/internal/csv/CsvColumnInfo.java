package dtalge.container.util.internal.csv;

import java.util.Objects;

import dtalge.util.Strings;

/**
 * CSV のカラム(列)の位置と名前を保持するクラス。
 * 
 * @version 0.2.0
 * @since 0.2.0
 * 
 * @author H.Deguchi (Chiba University of Commerce)
 * @author Y.Ozaki (Mitzba Denyosha CO.,LTD.)
 * @author Y.Ishizuka(PieCake,Inc.)
 */
public class CsvColumnInfo
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	/** カラムのインデックス(0～)、設定されていない場合は (-1) **/
	protected int		_index;
	/** カラムの名前(空文字列以外)、設定されていない場合は <code>null</code> **/
	protected String	_name;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * インデックスが (-1)、名前が <code>null</code> の、新しいインスタンスを生成する。
	 */
	public CsvColumnInfo() {
		this(-1, null);
	}
	
	/**
	 * インデックスが (-1)、名前が <em>name</em> の、新しいインスタンスを生成する。
	 * @param name	名前
	 */
	public CsvColumnInfo(String name) {
		this(-1, name);
	}

	/**
	 * 指定されたパラメーターで、新しいインスタンスを生成する。
	 * なお、<em>index</em> が負の値の場合、インデックスは (-1) となる。
	 * @param index	設定するインデックス
	 * @param name	設定する名前
	 */
	public CsvColumnInfo(int index, String name) {
		_index = (index < 0 ? -1 : index);
		_name  = name;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	/**
	 * このオブジェクトの内容を、未設定の状態にリセットする。
	 */
	public void reset() {
		_index = (-1);
		_name  = null;
	}

	/**
	 * インデックスが設定されているかどうかを判定する。
	 * @return	インデックスが設定されている場合は <code>true</code>
	 */
	public boolean hasIndex() {
		return (_index >= 0);
	}

	/**
	 * 名前が設定されているかどうかを判定する。
	 * @return	名前が <code>null</code> ではない場合は <code>true</code>
	 */
	public boolean hasName() {
		return (_name != null);
	}

	/**
	 * このオブジェクトが保持する名前が <code>null</code> もしくは空文字列かどうかを判定する。
	 * @return	名前が <code>null</code> もしくは空文字列の場合は <code>true</code>
	 */
	public boolean isNameNullOrEmpty() {
		return (Strings.isNullOrEmpty(_name));
	}

	/**
	 * インデックスを取得する。
	 * @return	設定されているインデックス、設定されていない場合は負の値
	 */
	public int getIndex() {
		return _index;
	}

	/**
	 * 名前を取得する。
	 * @return	設定されている名前、設定されていない場合は <code>null</code>
	 */
	public String getName() {
		return _name;
	}

	/**
	 * インデックスを設定する。
	 * なお、<em>index</em> が負の値の場合、インデックスは (-1) となる。
	 * @param index	設定するインデックス
	 * @return	インデックスが更新された場合は <code>true</code>
	 */
	public boolean setIndex(int index) {
		if (index < 0) {
			if (this._index >= 0) {
				this._index = index;
				return true;
			}
		}
		else if (index != this._index) {
			this._index = index;
			return true;
		}
		
		// no changes
		return false;
	}

	/**
	 * 名前を設定する。
	 * @param name	設定する名前
	 * @return	名前が更新された場合は <code>true</code>
	 */
	public boolean setName(String name) {
		if (!Objects.equals(name, this._name)) {
			// updated
			this._name = name;
			return true;
		}
		else {
			// no changes
			return false;
		}
	}

	@Override
	public int hashCode() {
		int h = _index;
		h = 31 * h + (_name==null ? 0 : _name.hashCode());
		return h;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		
		if (obj != null && obj.getClass().equals(this.getClass())) {
			if (equalFieldValues(obj)) {
				return true;
			}
		}
		
		// not equals
		return false;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected boolean equalFieldValues(Object obj) {
		CsvColumnInfo another = (CsvColumnInfo)obj;

		if (another._index != this._index)
			return false;
		
		if (!Objects.equals(another._name, this._name))
			return false;
		
		// equal all values
		return true;
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
