package dtalge.container.util.internal.csv.filter;

import dtalge.io.internal.CsvReader;

/**
 * 二項の論理積演算を表す CSV フィールド条件。
 * 
 * @version 0.2.0
 * @since 0.2.0
 * 
 * @author H.Deguchi (Chiba University of Commerce)
 * @author Y.Ozaki (Mitzba Denyosha CO.,LTD.)
 * @author Y.Ishizuka(PieCake,Inc.)
 */
public class CsvFIeldConditionAnd extends AbCsvFieldConditionPair
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
	 * 指定されたパラメーターで、このオブジェクトのインスタンスを生成する。
	 * @param firstElement	フィールド条件ペアの一方の要素
	 * @param secondElement	フィールド条件ペアのもう一方の要素
	 * @throws NullPointerException	引数のいずれかが <code>null</code> の場合
	 */
	public CsvFIeldConditionAnd(ICsvFieldCondition firstElement, ICsvFieldCondition secondElement)
	{
		super(firstElement, secondElement);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Implement interfaces
	//------------------------------------------------------------

	/**
	 * 指定された CSV レコードの内容が、このオブジェクトが保持するフィールド条件の両方を満たしているかどうかを判定する。
	 * @param csvrec	判定対象の CSV レコード
	 * @return	満たしている場合は <code>true</code>、それ以外の場合は <code>false</code>
	 */
	@Override
	public boolean isSatisfied(CsvReader.CsvRecord csvrec)
	{
		return (first.isSatisfied(csvrec) && second.isSatisfied(csvrec));
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
