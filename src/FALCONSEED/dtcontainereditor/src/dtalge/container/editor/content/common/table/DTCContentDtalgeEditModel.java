/*
 * @(#)DTCContentDtalgeEditModel.java	1.1.0	2023/01/27
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge.container.editor.content.common.table;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;

import dtalge.AbDtBase;
import dtalge.DtBase;
import dtalge.Dtalge;
import dtalge.container.editor.DtContainerEditorMessages;
import dtalge.container.editor.content.common.tree.IDtContainerContentTreeNode;
import ssac.aadl.data.dtalge.DtalgeDataTypes;

/**
 * データ代数元の編集用データモデル。
 * 
 * @version 1.1.0
 * @since 1.1.0
 */
public class DTCContentDtalgeEditModel extends AbDTCContentAlgeEditModel
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	private static final long serialVersionUID = 1L;
	
	/** データ代数基底キーの使用禁止文字の配列 **/
	static public final char[] INVALID_BASEKEY_CHARS	= AbDtBase._ILLEGAL_BASEKEY_ARRAY;
	
	/** 値の列インデックス **/
	static public final int COL_VALUE	= 0;
	/** 名前キーの列インデックス **/
	static public final int	COL_NAME	= 1;
	/** データ型キーの列インデックス **/
	static public final int	COL_TYPE	= 2;
	/** 属性キーの列インデックス **/
	static public final int	COL_ATTR	= 3;
	/** 主体キーの列インデックス **/
	static public final int	COL_SUBJECT	= 4;
	/** 固定のカラム名 **/
	static public final String[] ColumnNames = {
			DtContainerEditorMessages.getInstance().DtalgeEditor_colname_value,
			DtContainerEditorMessages.getInstance().DtalgeEditor_colname_name,
			DtContainerEditorMessages.getInstance().DtalgeEditor_colname_type,
			DtContainerEditorMessages.getInstance().DtalgeEditor_colname_attr,
			DtContainerEditorMessages.getInstance().DtalgeEditor_colname_subject,
	};

	/** 内容が空で拡張不可能なデータモデルのインスタンス **/
	static public final DTCContentDtalgeEditModel	EMPTY_MODEL	= new DTCContentDtalgeEditModel(0, 0);

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
	public DTCContentDtalgeEditModel() {
		this(DEFAULT_LIMIT_ROW_COUNT, DEFAULT_MIN_ROW_COUNT);
	}
	
	/**
	 * 指定されたデータ代数元で、新しいインスタンスを生成する。
	 * 最大許容行数は {@link AbDTCContentAlgeEditModel#DEFAULT_LIMIT_ROW_COUNT} となり、
	 * 最小行数は {@link AbDTCContentAlgeEditModel#DEFAULT_MIN_ROW_COUNT} となる。
	 * @param srcAlge	初期値とするデータ代数元
	 */
	public DTCContentDtalgeEditModel(Dtalge srcAlge) {
		this(srcAlge,  DEFAULT_LIMIT_ROW_COUNT, DEFAULT_MIN_ROW_COUNT);
	}
	
	/**
	 * 指定されたパラメータで、新しいインスタンスを生成する。
	 * @param limitRowCount		最大許容行数、負の値は 0 を指定されたものとみなす
	 * @param minRowCount		最小行数、最大許容行数よりも大きい場合は最大許容行数と同じ
	 */
	public DTCContentDtalgeEditModel(int limitRowCount, int minRowCount)
	{
		super(limitRowCount, minRowCount, ColumnNames.length);
	}
	
	/**
	 * 指定されたパラメータで、新しいインスタンスを生成する。
	 * @param srcAlge			初期値とするデータ代数元
	 * @param limitRowCount		最大許容行数、負の値は 0 を指定されたものとみなす
	 * @param minRowCount		最小行数、最大許容行数よりも大きい場合は最大許容行数と同じ
	 */
	public DTCContentDtalgeEditModel(Dtalge srcAlge, int limitRowCount, int minRowCount) {
		super(limitRowCount, minRowCount, ColumnNames.length);
		if (srcAlge != null && !srcAlge.isEmpty()) {
			_actualRows = new ArrayList<Object[]>(srcAlge.getNumElements());
			int rowcnt = 0;
			for (Map.Entry<DtBase, Object> entry : srcAlge.getUnmodifiableEntrySet()) {
				if (rowcnt >= getLimitRowCount()) {
					break;	// 最大許容数到達
				}
				Object[] rowData = createRowData(entry.getKey(), entry.getValue());
				_actualRows.add(rowData);
				++rowcnt;
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
		
		//--- data type
		strValue = (COL_TYPE < srcData.length ? srcData[COL_TYPE] : null);
		// データ型名以外の文字列なら、string
		DtalgeDataTypes valType = convertDtalgeDataType(strValue);
		rowData[COL_TYPE] = valType;
		
		//--- value
		strValue = (COL_VALUE < srcData.length ? srcData[COL_VALUE] : null);
		if (strValue != null && !strValue.isEmpty()) {
			// 基底に対応する値なら、文字列を検証
			rowData[COL_VALUE] = checkDtalgeElemValue(valType, strValue);
		}
		// else: default
		
		//--- name
		strValue = (COL_NAME < srcData.length ? srcData[COL_NAME] : null);
		if (strValue != null) {
			rowData[COL_NAME] = removeInvalidBaseKeyCharacters(strValue);
		}
		
		//--- attr
		strValue = (COL_ATTR < srcData.length ? srcData[COL_ATTR] : null);
		if (strValue != null) {
			rowData[COL_ATTR] = removeInvalidBaseKeyCharacters(strValue);
		}
		
		//--- subject
		strValue = (COL_SUBJECT < srcData.length ? srcData[COL_SUBJECT] : null);
		if (strValue != null) {
			rowData[COL_SUBJECT] = removeInvalidBaseKeyCharacters(strValue);
		}

		return rowData;
	}
	
	/**
	 * このデータモデルの内容を、{@code Dtalge} オブジェクトとして取得する。
	 * @param ndDataHolder	このデータモデルを保持するツリーノード
	 * @return	データ代数元オブジェクト
	 * @throws DTCContentEditTableModelConversionError	データ代数元オブジェクトに変換できない場合
	 */
	public Dtalge getValuesAsDtalge(IDtContainerContentTreeNode ndDataHolder) throws DTCContentEditTableModelConversionError
	{
		Dtalge retalge = new Dtalge();
		for (int rowIndex = 0; rowIndex < getActualRowCount(); ++rowIndex) {
			//--- Value
			Object value = getValueAt(rowIndex, COL_VALUE);
			if (value instanceof DTCContentInvalidCellValue) {
				// 値のエラー
				DTCContentInvalidCellValue invalidCellValue = (DTCContentInvalidCellValue)value;
				String errmsg = DtContainerEditorMessages.getInstance().msgCouldNotConvertToDtalgeFromEdited + "\n" + String.valueOf(invalidCellValue.getMessage());
				throw new DTCContentEditTableModelConversionError(errmsg, ndDataHolder, invalidCellValue, rowIndex, COL_VALUE);
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
			//--- type key
			DtalgeDataTypes typeKey = (DtalgeDataTypes)getValueAt(rowIndex, COL_TYPE);
			String strType = (typeKey == null ? DtalgeDataTypes.STRING.toString() : typeKey.toString());
			//--- attr key
			String attr = (String)getValueAt(rowIndex, COL_ATTR);
			//--- subject key
			String subject = (String)getValueAt(rowIndex, COL_SUBJECT);
			
			// convert value
			if (typeKey == DtalgeDataTypes.BOOLEAN) {
				// Boolean
				if (value instanceof Boolean) {
					;	// そのまま
				}
				else if (value != null) {
					String strValue = value.toString();
					if (strValue != null && !strValue.isEmpty()) {
						if ("true".equalsIgnoreCase(strValue)) {
							value = Boolean.TRUE;
						}
						else if ("false".equalsIgnoreCase(strValue)) {
							value = Boolean.FALSE;
						}
						else {
							// invalid value
							setValueAt(value, rowIndex, COL_VALUE);
							DTCContentInvalidCellValue invalidCellValue = (DTCContentInvalidCellValue)getValueAt(rowIndex, COL_VALUE);
							String errmsg = DtContainerEditorMessages.getInstance().msgCouldNotConvertToDtalgeFromEdited + "\n" + String.valueOf(invalidCellValue.getMessage());
							throw new DTCContentEditTableModelConversionError(errmsg, ndDataHolder, invalidCellValue, rowIndex, COL_VALUE);
						}
					}
					else {
						// null
						value = null;
					}
				}
			}
			else if (typeKey == DtalgeDataTypes.DECIMAL) {
				// Decimal
				if (value instanceof BigDecimal) {
					;	// そのまま
				}
				else if (value != null) {
					String strValue = value.toString();
					if (!strValue.isEmpty()) {
						try {
							value = new BigDecimal(strValue);
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
						value = null;
					}
					value = new BigDecimal(value.toString());
				}
			}
			else {
				// String
				if (value != null) {
					String strValue = value.toString();
					value = strValue.isEmpty() ? null : strValue;
				}
			}
			
			// add to Dtalge
			retalge.add(DtBase.newBase(name, strType, attr, subject), value);
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
		return (COL_NAME==columnIndex || COL_ATTR==columnIndex || COL_SUBJECT==columnIndex);
	}
	
	/**
	 * 指定された列の表す、もっとも明確なスーパークラスを返す。
	 * このメソッドは {@code JTable} によって使用され、列のデフォルトのレンダラおよびエディタを設定する。
	 * @param columnIndex	列インデックス
	 * @return	列の値を表すクラス
	 */
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if (columnIndex == COL_TYPE) {
			return DtalgeDataTypes.class;
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
		if (COL_NAME == columnIndex) {
			// 名前キー
			if (aValue != null) {
				aValue = removeInvalidBaseKeyCharacters(aValue.toString());
			}
			updateActualCellValueAt(aValue, rowIndex, columnIndex);
		}
		else if (COL_ATTR == columnIndex || COL_SUBJECT == columnIndex) {
			// 属性キー、主体キー
			if (aValue != null) {
				aValue = removeInvalidBaseKeyCharacters(aValue.toString());
			}
			updateActualCellValueAt(aValue, rowIndex, columnIndex);
		}
		else if (COL_TYPE == columnIndex) {
			// データ型キー
			// データ型名以外の文字列なら、string
			DtalgeDataTypes valType = convertDtalgeDataType(aValue);
			if (updateActualCellValueAt(valType, rowIndex, columnIndex)) {
				// 更新された場合は、基底に対応する値のデータ型をチェックし、更新する
				Object algeElemValue = checkDtalgeElemValue(valType, getValueAt(rowIndex, COL_VALUE));
				updateActualCellValueAt(algeElemValue, rowIndex, COL_VALUE);
			}
		}
		else if (COL_VALUE == columnIndex) {
			// 基底に対応する値なら、文字列を検証
			Object valType = getValueAt(rowIndex, COL_TYPE);
			aValue = checkDtalgeElemValue(valType, aValue);
			updateActualCellValueAt(aValue, rowIndex, columnIndex);
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	/**
	 * <em>aValue</em> が、<em>dataType</em> のデータ型として適切かを判定する。
	 * @param dataType	データ型を表す {@code DtalgeDataTypes} オブジェクト
	 * @param aValue	判定する値
	 * @return	データ型として適切なら <em>aValue</em> をそのまま返す。
	 * 			適切でない場合は、{@code DtContainerContentInvalidCellValue} オブジェクトを返す。
	 */
	protected Object checkDtalgeElemValue(Object dataType, Object aValue) {
		if (aValue != null) {
			String strValue = aValue.toString();
			if (!strValue.isEmpty()) {
				if (dataType == DtalgeDataTypes.DECIMAL) {
					// 数値変換出来なければ、エラー
					try {
						new BigDecimal(strValue);
						aValue = strValue;
					}
					catch (Throwable ex) {
						aValue = new DTCContentInvalidCellValue(aValue, DtContainerEditorMessages.getInstance().msgInvalidDtalgeDecimalValue);
					}
				}
				else if (dataType == DtalgeDataTypes.BOOLEAN) {
					// Boolean として適切な文字列でなければ、エラー
					if (isValidBooleanString(strValue)) {
						aValue = strValue;
					} else {
						aValue = new DTCContentInvalidCellValue(aValue, DtContainerEditorMessages.getInstance().msgInvalidDtalgeBooleanValue);
					}
				}
				else {
					// 上記以外は文字列とみなす
					aValue = strValue;
				}
			}
			else {
				// 空文字列は null とみなす
				aValue = null;
			}
		}
		return aValue;
	}
	
	/**
	 * 内容が空の行データを生成する。
	 * @return	行データ
	 */
	protected Object[] createEmptyRowData() {
		return new Object[] {
			null,					// value
			"",						// name key
			DtalgeDataTypes.STRING,	// type key
			"",						// attr key
			"",						// subject key
		};
	}
	
	/**
	 * 指定されたデータ代数元の要素で、行データを生成する。
	 * @param base	データ代数基底
	 * @param value	データ代数基底に対応する値
	 * @return	生成された行データ
	 */
	protected Object[] createRowData(DtBase base, Object value) {
		if (base == null) {
			return createRowData(value, (String)null, (DtalgeDataTypes)null, (String)null, (String)null);
		} else {
			return createRowData(value, base.getNameKey(), base.getTypeKey(), base.getAttributeKey(), base.getSubjectKey());
		}
	}
	
	/**
	 * 指定されたパラメーターで、行データを生成する。
	 * @param value			データ代数基底に対応する値
	 * @param nameKey		データ代数基底の名前キー
	 * @param typeKey		データ代数基底のデータ型キーを表す文字列
	 * @param attrKey		データ代数基底の属性キー
	 * @param subjectKey	データ代数基底の主体キー
	 * @return	生成された行データ
	 */
	protected Object[] createRowData(Object value, String nameKey, String typeKey, String attrKey, String subjectKey) {
		return createRowData(value, nameKey, DtalgeDataTypes.fromName(typeKey), attrKey, subjectKey);
	}
	
	/**
	 * 指定されたパラメーターで、行データを生成する。
	 * @param value			データ代数基底に対応する値
	 * @param nameKey		データ代数基底の名前キー
	 * @param typeKey		データ代数基底のデータ型キーとなる、{@code DtalgeDataTypes} オブジェクト
	 * @param attrKey		データ代数基底の属性キー
	 * @param subjectKey	データ代数基底の主体キー
	 * @return	生成された行データ
	 */
	protected Object[] createRowData(Object value, String nameKey, DtalgeDataTypes typeKey, String attrKey, String subjectKey) {
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
		
		// typeKey
		if (typeKey == null) {
			typeKey = DtalgeDataTypes.STRING;
		}
		
		// カラム追加
		Object[] newrow = {
			strValue,
			ensureRawBaseKeyString(nameKey),
			typeKey,
			ensureRawBaseKeyString(attrKey),
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
		if (COL_NAME == columnIndex || COL_ATTR == columnIndex || COL_SUBJECT == columnIndex) {
			// 基底キー文字列のデフォルト値
			return "";
		}
		else if (COL_TYPE == columnIndex) {
			// データ型キー
			return DtalgeDataTypes.STRING;
		}
		else {
			// その他
			return null;
		}
	}
	
	/**
	 * 指定された値を、データ代数基底のデータ型キーの値に正規化する。
	 * @param value	値
	 * @return	正規化後の値
	 */
	protected DtalgeDataTypes convertDtalgeDataType(Object value) {
		if (value == null) {
			return DtalgeDataTypes.STRING;
		}

		if (value instanceof DtalgeDataTypes) {
			return (DtalgeDataTypes)value;
		}
		else {
			String strValue = value.toString();
			DtalgeDataTypes argType = DtalgeDataTypes.fromName(strValue);
			if (argType == null)
				argType = DtalgeDataTypes.STRING;
			return argType;
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
		else if (DtBase.OMITTED.equals(keystr)) {
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

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
