/*
 * @(#)AbDataSeries.java	2.1.0	2013/07/20
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.swing.plot;

/**
 * データ系列の 2 フィールド情報を取得するインタフェースの共通実装
 * 
 * @version 2.1.0	2013/07/20
 * @since 2.1.0
 */
public abstract class AbDataSeries implements IDataSeries
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

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 対象データの総レコード数を返す。
	 * この総レコード数は、ヘッダレコード数とデータレコード数の合計となる。
	 */
	public long getRecordCount() {
		return Math.max((hasXField() ? getXField().getRecordCount() : 0L), (hasYField() ? getYField().getRecordCount() : 0L));
	}

	/**
	 * この系列のデータ数を返す。
	 * このデータ数は、X 値列のデータ数と Y 値列のデータ数のうち、大きい方を返す。
	 * @return	この系列のデータ数
	 */
	public long getValueCount() {
		return Math.max((hasXField() ? getXField().getDataCount() : 0L), (hasYField() ? getYField().getDataCount() : 0L));
	}

	/**
	 * この系列の有効な系列名を返す。
	 * 系列名が定義されていない場合、自動生成された系列名を返す。
	 * @return	有効な系列名
	 */
	public String getAvailableLegend() {
		return getDefaultLegend();
	}

	/**
	 * 自動生成された系列名を返す。
	 * @return	自動生成された系列名
	 */
	public String getDefaultLegend() {
		return createDefaultLegend();
	}

	/**
	 * 系列の X 値とするフィールドのデータテーブルを返す。
	 * X 値のフィールドが設定されていない場合は <tt>null</tt> を返す。
	 */
	public IDataTable getXDataTable() {
		return (hasXField() ? getXField().getDataTable() : null);
	}

	/**
	 * 系列の Y 値とするフィールドのデータテーブルを返す。
	 * Y 値のフィールドが設定されていない場合は <tt>null</tt> を返す。
	 */
	public IDataTable getYDataTable() {
		return (hasYField() ? getYField().getDataTable() : null);
	}
	
	/**
	 * X 値のデータ列から、指定されたインデックスの実際の値を取得する。
	 * @param index	データレコード先頭からのインデックス
	 * @return	実際の値
	 * @throws NullPointerException		X 値とするデータ列が設定されていない場合
	 * @throws IndexOutOfBoundsException	インデックスが範囲外の場合
	 */
	public Object getXFieldValue(long index) {
		return getXField().getFieldValue(index);
	}
	
	/**
	 * Y 値のデータ列から、指定されたインデックスの実際の値を取得する。
	 * @param index	データレコード先頭からのインデックス
	 * @return	実際の値
	 * @throws NullPointerException		Y 値とするデータ列が設定されていない場合
	 * @throws IndexOutOfBoundsException	インデックスが範囲外の場合
	 */
	public Object getYFieldValue(long index) {
		return getYField().getFieldValue(index);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected String createDefaultLegend() {
		String strLegend;
		if (hasXField() && hasYField()) {
			String xLegend = getXField().getFieldName();
			String yLegend = getYField().getFieldName();
			if (xLegend.length() > 0 && yLegend.length() > 0) {
				strLegend = xLegend + "-" + yLegend;
			}
			else if (xLegend.length() > 0) {
				strLegend = xLegend;
			}
			else if (yLegend.length() > 0) {
				strLegend = yLegend;
			}
			else {
				strLegend = "Unknown";
			}
		}
		else if (hasXField()) {
			strLegend = getXField().getFieldName();
		}
		else if (hasYField()) {
			strLegend = getYField().getFieldName();
		}
		else {
			strLegend = "";
		}
		return strLegend;
	}
}
