package dtalge.container.util.internal.csv.filter;

import java.util.Objects;

import dtalge.io.internal.CsvReader;

/**
 * 単項の論理否定演算を表す CSV フィールド条件。
 * 
 * @version 0.2.0
 * @since 0.2.0
 * 
 * @author H.Deguchi (Chiba University of Commerce)
 * @author Y.Ozaki (Mitzba Denyosha CO.,LTD.)
 * @author Y.Ishizuka(PieCake,Inc.)
 */
public class CsvFieldConditionNot implements ICsvFieldCondition
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** 結果を反転するフィールド条件 (must not null) **/
	protected final ICsvFieldCondition	_condition;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public CsvFieldConditionNot(ICsvFieldCondition condition)
	{
		if (condition == null)
			throw new NullPointerException("condition is null");
		
		_condition = condition;
	}

	//------------------------------------------------------------
	// Implement interfaces
	//------------------------------------------------------------

	/**
	 * 指定された CSV レコードの内容が、このオブジェクトが保持するフィールド条件を満たしていないかどうかを判定する。
	 * @param csvrec	判定対象の CSV レコード
	 * @return	満たしていない場合は <code>true</code>、それ以外の場合は <code>false</code>
	 */
	@Override
	public boolean isSatisfied(CsvReader.CsvRecord csvrec)
	{
		return !(_condition.isSatisfied(csvrec));
	}

	/**
	 * このインスタンスのハッシュ値を返す。
	 * 
	 * @return	ハッシュ値
	 */
	@Override
	public int hashCode() {
		return _condition.hashCode();
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
		CsvFieldConditionNot another = (CsvFieldConditionNot)obj;
		
		if (!Objects.equals(another._condition, this._condition))
			return false;
		
		// equals
		return true;
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
