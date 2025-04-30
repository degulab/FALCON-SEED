/*
 * @(#)PlotRecordNumberField.java	4.0.0	2021/08/23 : for Java11
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)PlotRecordNumberField.java	2.1.0	2013/07/18
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.swing.plot;

import java.math.BigDecimal;

/**
 * レコード番号を表示するデータ列に関する情報を保持するクラス。
 * 
 * @version 4.0.0
 * @since 2.1.0
 */
public class PlotRecordNumberField extends PlotDataField
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** レコード番号のオフセット **/
	private long	_offset;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * 指定されたパラメータで、新しいオブジェクトを生成する。
	 * フィールド名は、データテーブルから取得する。
	 * このメソッドでは、フィールドインデックスはチェックしない。
	 * @param targetTable	対象データテーブル
	 * @param numberOffset	レコード番号のオフセット
	 * @throws NullPointerException	<em>targetTable</em> が <tt>null</tt> の場合
	 */
	public PlotRecordNumberField(IDataTable targetTable, long numberOffset) {
		this(targetTable, numberOffset, null);
	}

	/**
	 * 指定されたパラメータで、新しいオブジェクトを生成する。
	 * <em>fieldName</em> に <tt>null</tt> を指定した場合、データテーブルからフィールド名を取得する。
	 * このメソッドでは、フィールドインデックスはチェックしない。
	 * @param targetTable	対象データテーブル
	 * @param numberOffset	レコード番号のオフセット
	 * @param fieldName		任意のフィールド名
	 * @throws NullPointerException	<em>targetTable</em> が <tt>null</tt> の場合
	 */
	public PlotRecordNumberField(IDataTable targetTable, long numberOffset, String fieldName) {
		this(targetTable, numberOffset, -1, fieldName);
	}

	/**
	 * 指定されたパラメータで、新しいオブジェクトを生成する。
	 * <em>fieldName</em> に <tt>null</tt> を指定した場合、データテーブルからフィールド名を取得する。
	 * このメソッドでは、フィールドインデックスはチェックしない。
	 * @param targetTable	対象データテーブル
	 * @param numberOffset	レコード番号のオフセット
	 * @param fieldIndex	データテーブル内の対象列インデックス
	 * @param fieldName		任意のフィールド名
	 * @throws NullPointerException	<em>targetTable</em> が <tt>null</tt> の場合
	 */
	public PlotRecordNumberField(IDataTable targetTable, long numberOffset, int fieldIndex, String fieldName) {
		super(targetTable, fieldIndex, BigDecimal.class, (fieldName==null ? "Record#" : fieldName));
		setOffset(numberOffset);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 設定されているレコード番号のオフセット値を返す。
	 */
	public long getOffset() {
		return _offset;
	}

	/**
	 * レコード番号のオフセット値を設定する。
	 * @param offset	新しいオフセット値
	 */
	public void setOffset(long offset) {
		_offset = offset;
		_minDecimal = new BigDecimal(1L + offset);
		_maxDecimal = new BigDecimal(_datatable.getDataRecordCount() + offset);
		_decimalRangeUpdated = true;
	}
	
	/**
	 * データ型が数値の場合はその値、それ以外の場合はプロット位置を示す数値を返す。
	 * 無効値の場合は <tt>null</tt> を返す。
	 * @param index	データレコード先頭からのインデックス
	 * @return	プロットする数値、無効値の場合は <tt>null</tt>
	 * @throws IndexOutOfBoundsException	インデックスが範囲外の場合
	 */
	@Override
	public BigDecimal getDecimalValue(long index) {
		return new BigDecimal(getRecordNumber(index));
	}

	@Override
	public int getFieldIndex() {
		return (-1);	// unknown field
	}

	@Override
	public Object getFieldValue(long index) {
		//return new Long(getRecordNumber(index));
		return Long.valueOf(getRecordNumber(index));
	}

	@Override
	public void resetDecimalRange() {
		// no operation
	}

	@Override
	public void refreshDecimalRange(long index) {
		// no operation
	}

	@Override
	public void updateDecimalRange() {
		// no operation
		_decimalRangeUpdated = true;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected long getRecordNumber(long index) {
		return (index + 1 + _offset);
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
