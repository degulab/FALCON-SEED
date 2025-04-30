/*
 * @(#)DTCContentExalgeEditModel.java	1.1.0	2023/01/23
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge.container.editor.content.common.table;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;

import dtalge.container.editor.DtContainerEditorMessages;
import dtalge.container.editor.content.common.tree.IDtContainerContentTreeNode;
import exalge2.AbExBase;
import exalge2.ExBase;
import exalge2.Exalge;

/**
 * 交換代数元の編集用データモデル。
 * 
 * @version 1.1.0
 * @since 1.1.0
 */
public class DTCContentExalgeEditModel extends AbDTCContentAlgeEditModel
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	private static final long serialVersionUID = 1L;
	
	/** 交換代数基底キーの使用禁止文字の配列 **/
	static public final char[] INVALID_BASEKEY_CHARS	= AbExBase._ILLEGAL_BASEKEY_CHARS;
	
	/** 値の列インデックス **/
	static public final int COL_VALUE	= 0;
	/** ハットキーの列インデックス **/
	static public final int COL_HAT		= 1;
	/** 名前キーの列インデックス **/
	static public final int	COL_NAME	= 2;
	/** 単位キーの列インデックス **/
	static public final int	COL_UNIT	= 3;
	/** 時間キーの列インデックス **/
	static public final int	COL_TIME	= 4;
	/** 主体キーの列インデックス **/
	static public final int	COL_SUBJECT	= 5;
	/** 固定のカラム名 **/
	static public final String[] ColumnNames = {
			DtContainerEditorMessages.getInstance().ExalgeEditor_colname_value,
			DtContainerEditorMessages.getInstance().ExalgeEditor_colname_hat,
			DtContainerEditorMessages.getInstance().ExalgeEditor_colname_name,
			DtContainerEditorMessages.getInstance().ExalgeEditor_colname_unit,
			DtContainerEditorMessages.getInstance().ExalgeEditor_colname_time,
			DtContainerEditorMessages.getInstance().ExalgeEditor_colname_subject,
	};

	/** 内容が空で拡張不可能なデータモデルのインスタンス **/
	static public final DTCContentExalgeEditModel	EMPTY_MODEL	= new DTCContentExalgeEditModel(0, 0);

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	/**
	 * 標準のパラメーターで、新しいインスタンスを生成する。
	 * 最大許容行数は {@link AbDTCContentAlgeEditModel#DEFAULT_LIMIT_ROW_COUNT} となり、
	 * 最小行数は {@link AbDTCContentAlgeEditModel#DEFAULT_MIN_ROW_COUNT} となる。
	 */
	public DTCContentExalgeEditModel() {
		this(DEFAULT_LIMIT_ROW_COUNT, DEFAULT_MIN_ROW_COUNT);
	}
	
	/**
	 * 指定された交換代数元で、新しいインスタンスを生成する。
	 * 最大許容行数は {@link AbDTCContentAlgeEditModel#DEFAULT_LIMIT_ROW_COUNT} となり、
	 * 最小行数は {@link AbDTCContentAlgeEditModel#DEFAULT_MIN_ROW_COUNT} となる。
	 * @param srcAlge	初期値とする交換代数元
	 */
	public DTCContentExalgeEditModel(Exalge srcAlge) {
		this(srcAlge,  DEFAULT_LIMIT_ROW_COUNT, DEFAULT_MIN_ROW_COUNT);
	}
	
	/**
	 * 指定されたパラメータで、新しいインスタンスを生成する。
	 * @param limitRowCount		最大許容行数、負の値は 0 を指定されたものとみなす
	 * @param minRowCount		最小行数、最大許容行数よりも大きい場合は最大許容行数と同じ
	 */
	public DTCContentExalgeEditModel(int limitRowCount, int minRowCount)
	{
		super(limitRowCount, minRowCount, ColumnNames.length);
	}
	
	/**
	 * 指定されたパラメータで、新しいインスタンスを生成する。
	 * @param srcAlge			初期値とする交換代数元
	 * @param limitRowCount		最大許容行数、負の値は 0 を指定されたものとみなす
	 * @param minRowCount		最小行数、最大許容行数よりも大きい場合は最大許容行数と同じ
	 */
	public DTCContentExalgeEditModel(Exalge srcAlge, int limitRowCount, int minRowCount) {
		super(limitRowCount, minRowCount, ColumnNames.length);
		if (srcAlge != null && !srcAlge.isEmpty()) {
			_actualRows = new ArrayList<Object[]>(srcAlge.getNumElements());
			int rowcnt = 0;
			for (Map.Entry<ExBase, BigDecimal> entry : srcAlge.getUnmodifiableEntrySet()) {
				if (rowcnt >= getLimitRowCount()) {
					break;	// 最大許容数到達
				}
				Object[] rowData = createRowData(entry.getKey(), entry.getValue());
				_actualRows.add(rowData);
			}
			_virtualRowCount = Math.max(getMinimumRowCount(), _actualRows.size());
		}
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	/**
	 * 文字列の配列から、行データを生成する。
	 * 不正な値のチェックも行う。
	 * @param srcData	行データのソースとなる文字列の配列
	 * @return	行データ
	 */
	public Object[] createRowDataByStrings(String[] srcData) {
		Object[] rowData = createEmptyRowData();
		if (srcData == null) return rowData;

		String strValue;
		
		//--- value
		strValue = (COL_VALUE < srcData.length ? srcData[COL_VALUE] : null);
		if (strValue != null && !strValue.isEmpty()) {
			// 数値変換出来なければ、エラー
			try {
				new BigDecimal(strValue);
				rowData[COL_VALUE] = strValue;
			}
			catch (Throwable ex) {
				rowData[COL_VALUE] = new DTCContentInvalidCellValue(strValue, DtContainerEditorMessages.getInstance().msgInvalidDtalgeDecimalValue);
			}
		}
		// else: default
		
		//--- hat
		strValue = (COL_HAT < srcData.length ? srcData[COL_HAT] : null);
		if (strValue != null && !strValue.isEmpty()) {
			// ハットキーなら、強制的に Boolean 値に変換
			rowData[COL_HAT] = convertToHatKeyBooleanValue(strValue);
		}
		// else: default
		
		//--- name
		strValue = (COL_NAME < srcData.length ? srcData[COL_NAME] : null);
		if (strValue != null) {
			rowData[COL_NAME] = removeInvalidBaseKeyCharacters(strValue);
		}
		
		//--- unit
		strValue = (COL_UNIT < srcData.length ? srcData[COL_UNIT] : null);
		if (strValue != null) {
			rowData[COL_UNIT] = removeInvalidBaseKeyCharacters(strValue);
		}
		
		//--- time
		strValue = (COL_TIME < srcData.length ? srcData[COL_TIME] : null);
		if (strValue != null) {
			rowData[COL_TIME] = removeInvalidBaseKeyCharacters(strValue);
		}
		
		//--- subject
		strValue = (COL_SUBJECT < srcData.length ? srcData[COL_SUBJECT] : null);
		if (strValue != null) {
			rowData[COL_SUBJECT] = removeInvalidBaseKeyCharacters(strValue);
		}

		return rowData;
	}
	
	/**
	 * このデータモデルの内容を、{@code Exalge} オブジェクトとして取得する。
	 * @param ndDataHolder	このデータモデルを保持するツリーノード
	 * @return	交換代数元オブジェクト
	 * @throws DTCContentEditTableModelConversionError	交換代数元オブジェクトに変換できない場合
	 */
	public Exalge getValuesAsExalge(IDtContainerContentTreeNode ndDataHolder) throws DTCContentEditTableModelConversionError
	{
		Exalge retalge = new Exalge();
		for (int rowIndex = 0; rowIndex < getRowCount(); ++rowIndex) {
			//--- Value
			Object value = getValueAt(rowIndex, COL_VALUE);
			if (value instanceof DTCContentInvalidCellValue) {
				// 値のエラー
				DTCContentInvalidCellValue invalidCellValue = (DTCContentInvalidCellValue)value;
				String errmsg = DtContainerEditorMessages.getInstance().msgCouldNotConvertToDtalgeFromEdited + "\n" + String.valueOf(invalidCellValue.getMessage());
				throw new DTCContentEditTableModelConversionError(errmsg, ndDataHolder, invalidCellValue, rowIndex, COL_VALUE);
			}
			//--- hat key
			String strHat;
			Object objHat = getValueAt(rowIndex, COL_HAT);
			if (objHat == null) {
				strHat = ExBase.NO_HAT;
			}
			else if (objHat instanceof Boolean) {
				strHat = ((Boolean)objHat).booleanValue() ? ExBase.HAT : ExBase.NO_HAT;
			}
			else {
				String strval = objHat.toString();
				if (ExBase.HAT.equalsIgnoreCase(strval) || "^".equalsIgnoreCase(strval) || "TRUE".equalsIgnoreCase(strval)) {
					strHat = ExBase.HAT;
				} else {
					strHat = ExBase.NO_HAT;
				}
			}
			//--- name key
			Object objNameKey = getValueAt(rowIndex, COL_NAME);
			if (objNameKey instanceof DTCContentInvalidCellValue) {
				// 名前キーのエラー
				DTCContentInvalidCellValue invalidCellValue = (DTCContentInvalidCellValue)value;
				String errmsg = DtContainerEditorMessages.getInstance().msgCouldNotConvertToDtalgeFromEdited + "\n" + String.valueOf(invalidCellValue.getMessage());
				throw new DTCContentEditTableModelConversionError(errmsg, ndDataHolder, invalidCellValue, rowIndex, COL_NAME);
			}
			if (objNameKey == null || objNameKey.toString().isEmpty()) {
				// skip if name key is empty
				continue;
			}
			String name = objNameKey.toString();
			//--- unit key
			String unit = (String)getValueAt(rowIndex, COL_UNIT);
			//--- time key
			String time = (String)getValueAt(rowIndex, COL_TIME);
			//--- subject key
			String subject = (String)getValueAt(rowIndex, COL_SUBJECT);
			
			// convert value
			BigDecimal bdValue;
			if (value instanceof BigDecimal) {
				bdValue = (BigDecimal)value;
			}
			else if (value != null) {
				String strValue = value.toString();
				if (!strValue.isEmpty()) {
					try {
						bdValue = new BigDecimal(strValue);
					}
					catch (Throwable ex) {
						DTCContentInvalidCellValue invalidCellValue = new DTCContentInvalidCellValue(strValue, DtContainerEditorMessages.getInstance().msgInvalidDtalgeDecimalValue);
						updateActualCellValueAt(invalidCellValue, rowIndex, COL_VALUE);
						String errmsg = DtContainerEditorMessages.getInstance().msgCouldNotConvertToDtalgeFromEdited + "\n" + String.valueOf(invalidCellValue.getMessage());
						throw new DTCContentEditTableModelConversionError(errmsg, ndDataHolder, invalidCellValue, rowIndex, COL_VALUE);
					}
				}
				else {
					// null
					bdValue = null;
				}
			}
			else {
				bdValue = null;
			}
			
			// add to Exalge
			retalge.add(new ExBase(name, strHat, unit, time, subject), bdValue);
		}
		return retalge;
	}
	
	/**
	 * 値列のインデックスを取得する。
	 * @return	値列のインデックス
	 */
	public int getValueColumnIndex() {
		return COL_VALUE;
	}
	
	/**
	 * 指定された列インデックスが値列のインデックスかを判定する。
	 * @param columnIndex	判定する列インデックス
	 * @return	値列のインデックスなら {@code true}
	 */
	public boolean isValueColumn(int columnIndex) {
		return (columnIndex == COL_VALUE);
	}
	
	/**
	 * 基底の名前キーの列インデックスを取得する。
	 * @return	名前キーの列インデックス
	 */
	public int getNameKeyColumnIndex() {
		return COL_NAME;
	}
	
	/**
	 * 指定された列インデックスが、基底キー文字列を入力可能な列のインデックスかを判定する。
	 * @param columnIndex	判定する列インデックス
	 * @return	基底キー文字列を入力可能な列のインデックスなら {@code true}
	 */
	public boolean isBaseKeyStringColumnIndex(int columnIndex) {
		return (COL_NAME==columnIndex || COL_UNIT==columnIndex || COL_TIME==columnIndex || COL_SUBJECT==columnIndex);
	}
	
	/**
	 * 指定された列の表す、もっとも明確なスーパークラスを返す。
	 * このメソッドは {@code JTable} によって使用され、列のデフォルトのレンダラおよびエディタを設定する。
	 * @param columnIndex	列インデックス
	 * @return	列の値を表すクラス
	 */
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if (columnIndex == COL_HAT) {
			return Boolean.class;
		}
		else if (columnIndex == COL_VALUE) {
			return Object.class;
		}
		else if (0 <= columnIndex && columnIndex < getColumnCount()) {
			return String.class;
		}
		else {
			return Object.class;
		}
	}
	
	/**
	 * 指定された列の名前を取得する。
	 * @param columnIndex	列インデックス
	 * @return	列の名前
	 */
	@Override
	public String getColumnName(int columnIndex) {
		if (columnIndex >= 0 && columnIndex < ColumnNames.length) {
			return ColumnNames[columnIndex];
		} else {
			return super.getColumnName(columnIndex);
		}
	}
	
	/**
	 * 指定された位置のセルに、指定された値を設定する。
	 * このメソッドは、格納領域を自動的に拡張する。
	 * @param aValue		セルに設定する値
	 * @param rowIndex		行インデックス
	 * @param columnIndex	列インデックス
	 * @throws IndexOutOfBoundsException	行インデックスもしくは列インデックスが範囲外の場合
	 */
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		validRowIndexRange(rowIndex);
		validColumnIndexRange(columnIndex);
		
		// 格納領域の拡張
		filledActualRows(rowIndex+1);
		
		// 値の更新
		if (COL_VALUE == columnIndex) {
			// 基底に対応する数値
			if (aValue != null) {
				// 値を検証
				String strValue = aValue.toString();
				if (!strValue.isEmpty()) {
					// 数値変換出来なければ、エラー
					try {
						new BigDecimal(strValue);
						aValue = strValue;
					}
					catch (Throwable ex) {
						aValue = new DTCContentInvalidCellValue(strValue, DtContainerEditorMessages.getInstance().msgInvalidDtalgeDecimalValue);
					}
				}
				else {
					// 空文字列は null とみなす
					aValue = null;
				}
			}
			if (updateActualCellValueAt(aValue, rowIndex, columnIndex)) {
				// 更新された場合、名前キーに文字が入力されていない場合は、名前キーにエラーを設定
				if (aValue != null) {
					// TODO:
				}
			}
		}
		else if (COL_HAT == columnIndex) {
			// ハットキーなら、強制的に Boolean 値に変換
			Boolean hat = convertToHatKeyBooleanValue(aValue);
			updateActualCellValueAt(hat, rowIndex, columnIndex);
		}
		else if (COL_NAME == columnIndex) {
			// 名前キー
			if (aValue != null) {
				aValue = removeInvalidBaseKeyCharacters(aValue.toString());
			}
			updateActualCellValueAt(aValue, rowIndex, columnIndex);
		}
		else if (COL_UNIT==columnIndex || COL_TIME==columnIndex || COL_SUBJECT==columnIndex) {
			// 単位キー、時間キー、主体キー
			if (aValue != null) {
				aValue = removeInvalidBaseKeyCharacters(aValue.toString());
			}
			if (updateActualCellValueAt(aValue, rowIndex, columnIndex)) {
				// 更新された場合、名前キーに文字が入力されていない場合は、名前キーにエラーを設定
				// TODO:
			}
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	/**
	 * 内容が空の行データを生成する。
	 * @return	行データ
	 */
	protected Object[] createEmptyRowData() {
		return new Object[] {
			null,			// value
			Boolean.FALSE,	// hat key
			"",				// name key
			"",				// unit key
			"",				// time key
			"",				// subject key
		};
	}

	/**
	 * 指定された交換代数元の要素で、行データを生成する。
	 * @param base	交換代数基底
	 * @param value	交換代数基底に対応する値
	 * @return	生成された行データ
	 */
	protected Object[] createRowData(ExBase base, Object value) {
		if (base == null) {
			return createRowData(value, false, (String)null, (String)null, (String)null, (String)null);
		} else {
			return createRowData(value, base.isHat(), base.getNameKey(), base.getUnitKey(), base.getTimeKey(), base.getSubjectKey());
		}
	}
	
	/**
	 * 指定されたパラメータで、行データを生成する。
	 * @param value			交換代数基底に対応する値
	 * @param hatFlag		交換代数基底のハットの有無
	 * @param nameKey		交換代数基底の名前キー
	 * @param unitKey		交換代数基底の単位キー
	 * @param timeKey		交換代数基底の時間キー
	 * @param subjectKey	交換代数基底の主体キー
	 * @return	生成された行データ
	 */
	protected Object[] createRowData(Object value, Boolean hatFlag, String nameKey, String unitKey, String timeKey, String subjectKey) {
		// value は文字列にする
		String strValue;
		if (value instanceof BigDecimal) {
			strValue = ((BigDecimal)value).stripTrailingZeros().toPlainString();
		}
		else if (value != null) {
			strValue = value.toString();
		}
		else {
			strValue = null;
		}
		
		// カラム追加
		Object[] newrow = {
			strValue,
			hatFlag,
			ensureRawBaseKeyString(nameKey),
			ensureRawBaseKeyString(unitKey),
			ensureRawBaseKeyString(timeKey),
			ensureRawBaseKeyString(subjectKey),
		};
		return newrow;
	}
	
	/**
	 * 指定された列インデックスに対応する、デフォルト値を取得する。
	 * 仮想行のセルの表示に利用される。
	 * @param columnIndex	列インデックス
	 * @return	列インデックスに対応するデフォルト値
	 */
	protected Object getDefaultCellValueAt(int columnIndex) {
		if (COL_NAME <= columnIndex) {
			// 基底キー文字列のデフォルト値
			return "";
		}
		else if (COL_HAT == columnIndex) {
			// ハットキー
			return Boolean.FALSE;
		}
		else {
			// その他
			return null;
		}
	}
	
	/**
	 * 指定された基底キー文字列が省略を表す文字列や {@code null} の場合、空文字列に変換する。
	 * @param keystr	変換する文字列
	 * @return	変換後の文字列
	 */
	protected String ensureRawBaseKeyString(String keystr) {
		if (keystr == null) {
			return "";
		}
		else if (ExBase.OMITTED.equals(keystr)) {
			return "";
		}
		else {
			return keystr;
		}
	}
	
	/**
	 * 代数基底キーとして不正な文字の配列を返す。
	 * @return	代数基底キーとして不正な文字の配列
	 */
	protected char[] getIllegalBaseKeyChars() {
		return INVALID_BASEKEY_CHARS;
	}
	
	protected Boolean convertToHatKeyBooleanValue(Object value) {
		if (value == null)
			return null;
		
		if (value instanceof Boolean) {
			return (Boolean)value;
		}
		else if (value instanceof BigDecimal) {
			BigDecimal d = (BigDecimal)value;
			if (BigDecimal.ZERO.compareTo(d) == 0) {
				return Boolean.FALSE;
			} else {
				return Boolean.TRUE;
			}
		}
		else {
			// String
			String strValue = value.toString();
			if (strValue.isEmpty())
				return null;
			
			if ("true".equalsIgnoreCase(strValue)) {
				return Boolean.TRUE;
			}
			else if ("false".equalsIgnoreCase(strValue)) {
				return Boolean.FALSE;
			}
			else {
				return Boolean.FALSE;
			}
		}
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
