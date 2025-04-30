package dtalge.container.util.internal.csv.filter;

import dtalge.container.util.CsvFieldConditionStringParseError;
import dtalge.container.util.internal.csv.CsvColumnInfo;
import dtalge.container.util.internal.csv.DtDoubleEntrySimpleSlipsCsvHeader;
import dtalge.container.util.internal.csv.filter.parser.CsvFieldConditionStringParser;
import dtalge.io.internal.CsvReader;
import dtalge.util.Strings;

/**
 * CSV フィールド条件文字列から構築されたフィールド条件を用いる、CSV レコードフィルター。
 * 
 * @version 0.2.0
 * @since 0.2.0
 * 
 * @author H.Deguchi (Chiba University of Commerce)
 * @author Y.Ozaki (Mitzba Denyosha CO.,LTD.)
 * @author Y.Ishizuka(PieCake,Inc.)
 */
public class DtDoubleEntrySimpleSlipsCsvNoteFilterImpl implements ICsvRecordFilter
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** フィールド条件に含まれるカラム情報の配列 **/
	protected CsvColumnInfo[]	_fieldPositions;
	
	/** 階層化されたフィールド条件の先頭 **/
	protected ICsvFieldCondition	_condition;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public DtDoubleEntrySimpleSlipsCsvNoteFilterImpl()
	{
		_fieldPositions = null;
		_condition = null;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public CsvColumnInfo[] getFieldPositions() {
		return _fieldPositions;
	}
	
	public ICsvFieldCondition getFieldCondition() {
		return _condition;
	}

	/**
	 * このオブジェクトに設定されたフィールド条件をリセットする。
	 */
	public void reset() {
		_fieldPositions = null;
		_condition = null;
	}

	/**
	 * このオブジェクトにフィールド条件が設定されているかどうかを判定する。
	 * @return	設定されている場合は <code>true</code>
	 */
	public boolean hasFieldCondition() {
		return (_condition != null);
	}

	/**
	 * フィールド条件文字列をパースし、このオブジェクトのフィールド条件に設定する。
	 * @param strCondition	パースする文字列、<code>null</code> もしくは空文字列の場合はフィールド条件がリセットされる
	 * @throws CsvFieldConditionStringParseError	文法が正しくない場合
	 */
	public void parseConditionString(String strCondition)
		throws CsvFieldConditionStringParseError
	{
		reset();
		
		if (!Strings.isNullOrEmpty(strCondition)) {
			// 条件文をパース
			CsvFieldConditionStringParser parser = new CsvFieldConditionStringParser(strCondition);
			ICsvFieldCondition condition = parser.parse();
			_condition = condition;
			_fieldPositions = (condition==null ? null : parser.getCachedFieldPositions());
		}
	}

	/**
	 * 指定された CSV ヘッダーの内容に基づき、フィールドの条件のフィールドインデックスを更新する。
	 * フィールド条件に設定されたカラム情報の名前に対応するフィールドが存在しない場合、フィールド条件に設定されたカラム情報のインデックスは無効(-1)に設定される。
	 * @param csvheader	CSV ヘッダー情報
	 * @return	フィールド条件に設定されたカラム情報が更新された場合は <code>true</code>、そうでない場合は <code>false</code>
	 */
	public boolean updateConditionByCsvHeader(DtDoubleEntrySimpleSlipsCsvHeader csvheader)
	{
		if (_fieldPositions == null || _fieldPositions.length <= 0)
			return false;
		
		// カラム情報のフィールドインデックスを更新
		boolean modified = false;
		for (CsvColumnInfo colInfo : _fieldPositions) {
			int fieldIndex = csvheader.noteItemIndexToCsvFieldIndex( csvheader.getNoteItemIndexByName(colInfo.getName()) );
			if (colInfo.setIndex(fieldIndex)) {
				// 変更あり
				modified = true;
			}
		}
		
		return modified;
	}

	/**
	 * 指定された CSV レコードの内容を受け入れるかどうかを判定する。
	 * @param csvrec	判定対象の CSV レコード
	 * @return	受け入れられる場合は <code>true</code>、そうでない場合は <code>false</code> を返す。
	 * 			条件が未定義の場合、このメソッドは <code>true</code> を返す。
	 */
	@Override
	public boolean accept(CsvReader.CsvRecord csvrec)
	{
		// 条件が未定義なら true
		if (_condition == null) {
			return true;
		}
		
		// 定義された条件で判定
		return _condition.isSatisfied(csvrec);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
