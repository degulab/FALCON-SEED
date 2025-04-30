package dtalge.container.util.internal.csv.filter;

import dtalge.io.internal.CsvReader;

/**
 * 
 * 
 * @version 0.2.0
 * @since 0.2.0
 * 
 * @author H.Deguchi (Chiba University of Commerce)
 * @author Y.Ozaki (Mitzba Denyosha CO.,LTD.)
 * @author Y.Ishizuka(PieCake,Inc.)
 */
public interface ICsvRecordFilter
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 指定された CSV レコードの内容を受け入れるかどうかを判定する。
	 * @param csvrec	判定対象の CSV レコード
	 * @return	受け入れられる場合は <code>true</code>、そうでない場合は <code>false</code>
	 */
	public boolean accept(CsvReader.CsvRecord csvrec);
}
