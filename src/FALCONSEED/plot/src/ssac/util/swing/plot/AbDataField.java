/*
 * @(#)AbDataField.java	2.1.0	2013/07/20
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.swing.plot;


/**
 * データフィールドの情報を取得するインタフェースの共通実装。
 * この実装では、フィールド名は指定されたデータテーブルから取得する。
 * 
 * @version 2.1.0	2013/07/20
 * @since 2.1.0
 */
public abstract class AbDataField implements IDataField
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	/** フィールドのインデックス **/
	protected final int			_fieldIndex;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public AbDataField(int fieldIndex) {
		_fieldIndex = fieldIndex;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	/**
	 * この列のテーブル上のインデックスを返す。
	 */
	public int getFieldIndex() {
		return _fieldIndex;
	}
	
	/**
	 * 対象データのフィールド名(列名)を返す。
	 */
	public String getFieldName() {
		return getDataTable().getFieldName(_fieldIndex);
	}
	
	/**
	 * このデータの総レコード数を返す。
	 * 総レコード数は、すべてのデータレコードとヘッダレコードの総数となる。
	 */
	public long getRecordCount() {
		return getDataTable().getRecordCount();
	}
	
	/**
	 * データ数(データレコード数)を返す。
	 */
	public long getDataCount() {
		return getDataTable().getDataRecordCount();
	}
	
	/**
	 * 指定されたインデックスの実際の値を取得する。
	 * @param index	データレコード先頭からのインデックス
	 * @return	実際の値
	 * @throws IndexOutOfBoundsException	インデックスが範囲外の場合
	 */
	public Object getFieldValue(long index) {
		return getDataTable().getValue(index, _fieldIndex);
	}

	@Override
	public int hashCode() {
		int h = getDataTable().hashCode();
		h = 31 * h + _fieldIndex;
		return h;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		
		if (obj instanceof AbDataField) {
			AbDataField aField = (AbDataField)obj;
			if (aField._fieldIndex == this._fieldIndex && aField.getDataTable().equals(this.getDataTable())) {
				return true;
			}
		}
		
		return false;
	}

	@Override
	public String toString() {
		return getFieldName();
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
}
