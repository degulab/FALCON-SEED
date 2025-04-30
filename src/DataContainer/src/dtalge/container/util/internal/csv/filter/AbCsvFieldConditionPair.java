package dtalge.container.util.internal.csv.filter;

import java.util.Objects;

/**
 * 二項の CSV フィールド条件式を保持するクラス。
 * 
 * @version 0.2.0
 * @since 0.2.0
 * 
 * @author H.Deguchi (Chiba University of Commerce)
 * @author Y.Ozaki (Mitzba Denyosha CO.,LTD.)
 * @author Y.Ishizuka(PieCake,Inc.)
 */
public abstract class AbCsvFieldConditionPair implements ICsvFieldCondition
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** フィールド条件ペアの一方の要素 (must not null) **/
	public final ICsvFieldCondition first;
	/** フィールド条件ペアのもう一方の要素 (must not null) **/
	public final ICsvFieldCondition second;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * 指定されたパラメーターで、このオブジェクトのインスタンスを生成する。
	 * @param firstElement	フィールド条件ペアの一方の要素
	 * @param secondElement	フィールド条件ペアのもう一方の要素
	 * @throws NullPointerException	引数のいずれかが <code>null</code> の場合
	 */
	public AbCsvFieldConditionPair(ICsvFieldCondition firstElement, ICsvFieldCondition secondElement)
	{
		if (firstElement == null)
			throw new NullPointerException("first element is null");
		if (secondElement == null)
			throw new NullPointerException("second element is null");
		first = firstElement;
		second = secondElement;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * このインスタンスのハッシュ値を返す。
	 * 
	 * @return	ハッシュ値
	 */
	@Override
	public int hashCode() {
		int h = first.hashCode();
		h = 31 * h + second.hashCode();
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
		AbCsvFieldConditionPair another = (AbCsvFieldConditionPair)obj;
		
		if (!Objects.equals(another.first, this.first))
			return false;
		
		if (!Objects.equals(another.second, this.second))
			return false;
		
		// equals
		return true;
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
