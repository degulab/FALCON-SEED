package dtalge.container.util.internal.csv.filter;

import java.util.Objects;
import java.util.regex.Pattern;

import dtalge.container.util.internal.csv.CsvColumnInfo;
import dtalge.io.internal.CsvReader;

/**
 * 単一のフィールド比較条件として、対象とするフィールド位置とフィールドパターンを保持するクラス。
 * 
 * @version 0.2.0
 * @since 0.2.0
 * 
 * @author H.Deguchi (Chiba University of Commerce)
 * @author Y.Ozaki (Mitzba Denyosha CO.,LTD.)
 * @author Y.Ishizuka(PieCake,Inc.)
 */
public class CsvFieldConditionPattern implements ICsvFieldCondition
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	/** 判定対象のフィールド位置 (must not null) **/
	protected final CsvColumnInfo	_fieldPosition;
	/** フィールド値の判定に用いる正規表現 (must not null) **/
	protected final Pattern	_fieldPattern;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	/**
	 * 指定されたパラメーターで、このオブジェクトのインスタンスを生成する。
	 * @param fieldIndex	判定対象のフィールドインデックス(0～)
	 * @param fieldName		判定対象フィールドの名前(設定しない場合は <code>null</code>)
	 * @param fieldPattern	フィールド値の判定に用いる正規表現
	 * @throws NullPointerException	<em>fieldPattern</em> が <code>null</code> の場合
	 */
	public CsvFieldConditionPattern(int fieldIndex, String fieldName, Pattern fieldPattern)
	{
		this(new CsvColumnInfo(fieldIndex, fieldName), fieldPattern);
	}

	/**
	 * 指定されたパラメーターで、このオブジェクトのインスタンスを生成する。
	 * @param fieldName		判定対象フィールドの名前(設定しない場合は <code>null</code>)
	 * @param fieldPattern	フィールド値の判定に用いる正規表現
	 * @throws NullPointerException	<em>fieldPattern</em> が <code>null</code> の場合
	 */
	public CsvFieldConditionPattern(String fieldName, Pattern fieldPattern)
	{
		this(new CsvColumnInfo(fieldName), fieldPattern);
	}

	/**
	 * 指定されたパラメーターで、このオブジェクトのインスタンスを生成する。
	 * @param fieldPos		判定対象のフィールドの位置を示すカラム情報
	 * @param fieldPattern	フィールド値の判定に用いる正規表現
	 * @throws NullPointerException	<em>fieldPos</em> または <em>fieldPattern</em> が <code>null</code> の場合
	 */
	public CsvFieldConditionPattern(CsvColumnInfo fieldPos, Pattern fieldPattern)
	{
		if (fieldPos == null)
			throw new NullPointerException("field position is null");
		if (fieldPattern == null)
			throw new NullPointerException("fieldPattern is null");

		_fieldPosition = fieldPos;
		_fieldPattern  = fieldPattern;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public int fieldIndex() {
		return _fieldPosition.getIndex();
	}
	
	public String fieldName() {
		return _fieldPosition.getName();
	}
	
	public CsvColumnInfo fieldPosition() {
		return _fieldPosition;
	}
	
	public Pattern fieldPattern() {
		return _fieldPattern;
	}

	//------------------------------------------------------------
	// Implement interfaces
	//------------------------------------------------------------

	/**
	 * 指定された CSV レコードのうち、このオブジェクトに設定されたフィールドインデックスに対応する値の一部が
	 * このオブジェクトのフィールド正規表現に一致するかどうかを判定する。
	 * @param csvrec	判定対象の CSV レコード
	 * @return	条件を満たしている場合は <code>true</code>、それ以外の場合は <code>false</code>
	 */
	@Override
	public boolean isSatisfied(CsvReader.CsvRecord csvrec)
	{
		CsvReader.CsvField field = csvrec.getField(_fieldPosition.getIndex());
		if (field == null) {
			// フィールドが存在しない場合は false
			return false;
		}

		// 部分一致判定
		return _fieldPattern.matcher(field.getValue()).find();
	}

	/**
	 * このインスタンスのハッシュ値を返す。
	 * 
	 * @return	ハッシュ値
	 */
	@Override
	public int hashCode() {
		int h = _fieldPosition.hashCode();
		h = 31 * h + _fieldPattern.hashCode();
		return h;
	}

	/**
	 * 指定されたオブジェクトとこのインスタンスの内容が等しいかどうかを判定する。
	 * <p>
	 * このメソッドは、階層かされた条件式すべてが等しいかどうかを判定する。
	 * 
	 * @param obj	同値性を判定するオブジェクトの一方
	 * 
	 * @return 同値である場合に <code>true</code> を返す。
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		
		if (obj != null && obj.getClass() == this.getClass()) {
			if (this.equalFieldValues(obj)) {
				return true;
			}
		}
		
		// not equals
		return false;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected boolean equalFieldValues(Object obj)
	{
		CsvFieldConditionPattern another = (CsvFieldConditionPattern)obj;
		
		if (!Objects.equals(another._fieldPosition, this._fieldPosition))
			return false;
		
		if (!equalPattern(another._fieldPattern))
			return false;
		
		// equals
		return true;
	}
	
	protected boolean equalPattern(Pattern another) {
		// Pattern オブジェクトの equals() は (this == obj) しか実行しないので、パータン文字列を比較する
		return Objects.equals(another.pattern(), this._fieldPattern.pattern());
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
