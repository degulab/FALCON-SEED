/*
 * @(#)IDataTable.java	2.1.0	2013/07/11
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.swing.plot;

/**
 * レコード(行)とフィールド(列)を持つテーブル形式のデータを取得する
 * インタフェース。
 * 
 * @version 2.1.0	2013/07/11
 * @since 2.1.0
 */
public interface IDataTable
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 総レコード数を返す。
	 * レコード数は、ヘッダレコード数とデータレコード数の合計となる。
	 */
	public long getRecordCount();
	/**
	 * ヘッダとなるレコード数を返す。
	 */
	public long getHeaderRecordCount();
	/**
	 * 実データの総レコード数を返す。
	 */
	public long getDataRecordCount();
	/**
	 * 最大フィールド数を返す。
	 */
	public int getFieldCount();
	/**
	 * 指定されたフィールドの名称を返す。
	 * @param fieldIndex	フィールドの位置を示すインデックス
	 * @return	フィールド名
	 * @throws IndexOutOfBoundsException	フィールドインデックスが範囲外の場合
	 */
	public String getFieldName(int fieldIndex);
	/**
	 * 指定された位置のヘッダ値を返す。
	 * @param headerRecordIndex	レコードの位置を示すインデックス(<code>getHeaderRecordCount()</code> を上限とする)
	 * @param fieldIndex	フィールドの位置を示すインデックス
	 * @return	ヘッダ値
	 * @throws IndexOutOfBoundsException	レコードインデックスもしくはフィールドインデックスが範囲外の場合
	 */
	public Object getHeaderValue(long headerRecordIndex, int fieldIndex);
	/**
	 * 指定された位置の実値を返す。
	 * @param dataRecordIndex	レコードの位置を示すインデックス(<code>getDataRecordCount()</code> を上限とする)
	 * @param fieldIndex	フィールドの位置を示すインデックス
	 * @return	実値
	 * @throws IndexOutOfBoundsException	レコードインデックスもしくはフィールドインデックスが範囲外の場合
	 */
	public Object getValue(long dataRecordIndex, int fieldIndex);
}
