/*
 * @(#)EmptyDataTable.java	2.1.0	2013/07/12
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.swing.plot;

/**
 * レコードとフィールドを持たない、空のデータテーブルの実装。
 * 
 * @version 2.1.0	2013/07/12
 * @since 2.1.0
 */
public class EmptyDataTable implements IDataTable
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static public final EmptyDataTable instance = new EmptyDataTable();

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 総レコード数を返す。
	 * レコード数は、ヘッダレコード数とデータレコード数の合計となる。
	 */
	public long getRecordCount() {
		return 0L;
	}
	
	/**
	 * ヘッダとなるレコード数を返す。
	 */
	public long getHeaderRecordCount() {
		return 0L;
	}
	
	/**
	 * 実データの総レコード数を返す。
	 */
	public long getDataRecordCount() {
		return 0L;
	}
	
	/**
	 * 最大フィールド数を返す。
	 */
	public int getFieldCount() {
		return 0;
	}
	
	/**
	 * 指定されたフィールドの名称を返す。
	 * @param fieldIndex	フィールドの位置を示すインデックス
	 * @return	フィールド名
	 * @throws IndexOutOfBoundsException	フィールドインデックスが範囲外の場合
	 */
	public String getFieldName(int fieldIndex) {
		throw new IndexOutOfBoundsException("Field index out of range (0) : " + fieldIndex);
	}
	
	/**
	 * 指定された位置のヘッダ値を返す。
	 * @param headerRecordIndex	レコードの位置を示すインデックス(<code>getHeaderRecordCount()</code> を上限とする)
	 * @param fieldIndex	フィールドの位置を示すインデックス
	 * @return	ヘッダ値
	 * @throws IndexOutOfBoundsException	レコードインデックスもしくはフィールドインデックスが範囲外の場合
	 */
	public Object getHeaderValue(long headerRecordIndex, int fieldIndex) {
		throw new IndexOutOfBoundsException("Header record index out of range (0) : " + headerRecordIndex);
	}
	
	/**
	 * 指定された位置の実値を返す。
	 * @param dataRecordIndex	レコードの位置を示すインデックス(<code>getDataRecordCount()</code> を上限とする)
	 * @param fieldIndex	フィールドの位置を示すインデックス
	 * @return	実値
	 * @throws IndexOutOfBoundsException	レコードインデックスもしくはフィールドインデックスが範囲外の場合
	 */
	public Object getValue(long dataRecordIndex, int fieldIndex) {
		throw new IndexOutOfBoundsException("Data record index out of range (0) : " + dataRecordIndex);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
}
