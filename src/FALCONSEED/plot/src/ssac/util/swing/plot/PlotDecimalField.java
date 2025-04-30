/*
 * @(#)PlotDecimalField.java	2.1.0	2013/07/22
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.swing.plot;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * 数値型のデータ列に関する情報を保持するクラス。
 * 
 * @version 2.1.0	2013/07/22
 * @since 2.1.0
 */
public class PlotDecimalField extends PlotDataField
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * 指定されたパラメータで、新しいオブジェクトを生成する。
	 * フィールド名は、データテーブルから取得する。
	 * このメソッドでは、フィールドインデックスはチェックしない。
	 * @param targetTable	対象データテーブル
	 * @param fieldIndex	データテーブル内の対象列インデックス
	 * @throws NullPointerException	<em>targetTable</em> が <tt>null</tt> の場合
	 * @throws IndexOutOfBoundsException	<em>fieldIndex</em> が範囲外の場合
	 */
	public PlotDecimalField(IDataTable targetTable, int fieldIndex) {
		this(targetTable, fieldIndex, null);
	}

	/**
	 * 指定されたパラメータで、新しいオブジェクトを生成する。
	 * <em>fieldName</em> に <tt>null</tt> を指定した場合、データテーブルからフィールド名を取得する。
	 * このメソッドでは、フィールドインデックスはチェックしない。
	 * @param targetTable	対象データテーブル
	 * @param fieldIndex	データテーブル内の対象列インデックス
	 * @param fieldName		任意のフィールド名
	 * @throws NullPointerException	<em>targetTable</em> が <tt>null</tt> の場合
	 * @throws IndexOutOfBoundsException	<em>fieldIndex</em> が範囲外の場合
	 */
	public PlotDecimalField(IDataTable targetTable, int fieldIndex, String fieldName) {
		super(targetTable, fieldIndex, BigDecimal.class, fieldName);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	/**
	 * データ型が数値の場合はその値、それ以外の場合はプロット位置を示す数値を返す。
	 * 無効値の場合は <tt>null</tt> を返す。
	 * @param index	データレコード先頭からのインデックス
	 * @return	プロットする数値、無効値の場合は <tt>null</tt>
	 * @throws IndexOutOfBoundsException	インデックスが範囲外の場合
	 */
	@Override
	public BigDecimal getDecimalValue(long index) {
		Object fvalue = _datatable.getValue(index, _fieldIndex);
		BigDecimal retValue = null;
		if (fvalue instanceof BigDecimal) {
			retValue = (BigDecimal)fvalue;
		}
		else if (fvalue instanceof BigInteger) {
			retValue = new BigDecimal((BigInteger)fvalue);
		}
		else if ((fvalue instanceof Double) || (fvalue instanceof Float)) {
			try {
				retValue = new BigDecimal(((Number)fvalue).doubleValue());
			} catch (NumberFormatException ex) {
				retValue = null;
			}
		}
		else if (fvalue instanceof Number) {
			retValue = new BigDecimal(((Number)fvalue).longValue());
		}
		else if (fvalue != null) {
			String strValue = fvalue.toString();
			if (strValue.length() > 0) {
				try {
					retValue = new BigDecimal(fvalue.toString());
				} catch (NumberFormatException ex) {
					retValue = null;
				}
			}
		}
		
		if (isInvalidValueAsZero() && retValue == null) {
			retValue = BigDecimal.ZERO;
		}
		
		return retValue;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
