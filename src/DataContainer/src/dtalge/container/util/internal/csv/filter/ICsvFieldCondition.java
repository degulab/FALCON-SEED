package dtalge.container.util.internal.csv.filter;

import dtalge.io.internal.CsvReader;

/**
 * CSV フィールド条件を表すインターフェース。
 * <p>
 * 条件の優先順に従い、最小の条件式の改造構造を持つ。
 * 
 * @version 0.2.0
 * @since 0.2.0
 * 
 * @author H.Deguchi (Chiba University of Commerce)
 * @author Y.Ozaki (Mitzba Denyosha CO.,LTD.)
 * @author Y.Ishizuka(PieCake,Inc.)
 */
public interface ICsvFieldCondition
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 指定された CSV レコードの内容が、このオブジェクトが規定する条件を満たしているかどうかを判定する。
	 * @param csvrec	判定対象の CSV レコード
	 * @return	条件を満たしている場合は <code>true</code>、それ以外の場合は <code>false</code>
	 */
	public boolean isSatisfied(CsvReader.CsvRecord csvrec);

	/**
	 * このインスタンスのハッシュ値を返す。
	 * 
	 * @return	ハッシュ値
	 */
	public int hashCode();

	/**
	 * 指定されたオブジェクトとこのインスタンスの内容が等しいかどうかを判定する。
	 * <p>
	 * このメソッドは、階層かされた条件式すべてが等しいかどうかを判定する。
	 * 
	 * @param obj	同値性を判定するオブジェクトの一方
	 * 
	 * @return 同値である場合に <code>true</code> を返す。
	 */
	public boolean equals(Object obj);

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
}
