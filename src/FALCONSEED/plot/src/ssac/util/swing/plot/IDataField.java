/*
 * @(#)IDataField.java	2.1.0	2013/07/20
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.swing.plot;

/**
 * データフィールドの情報を取得するインタフェース。
 * 
 * @version 2.1.0	2013/07/20
 * @since 2.1.0
 */
public interface IDataField
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	/**
	 * この列が属するデータテーブルを返す。
	 */
	public IDataTable getDataTable();
	
	/**
	 * この列のテーブル上のインデックスを返す。
	 */
	public int getFieldIndex();
	
	/**
	 * 対象データのフィールド名(列名)を返す。
	 */
	public String getFieldName();
	
	/**
	 * このデータの総レコード数を返す。
	 * 総レコード数は、すべてのデータレコードとヘッダレコードの総数となる。
	 */
	public long getRecordCount();
	
	/**
	 * データ数(データレコード数)を返す。
	 */
	public long getDataCount();
	
	/**
	 * 対象データのデータ型を返す。
	 */
	public Class<?> getFieldType();
	
	/**
	 * 指定されたインデックスの実際の値を取得する。
	 * @param index	データレコード先頭からのインデックス
	 * @return	実際の値
	 * @throws IndexOutOfBoundsException	インデックスが範囲外の場合
	 */
	public Object getFieldValue(long index);
}
