/*
 * @(#)CsvRecordComparator.java	1.90	2013/08/02
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.runtime.csv.internal;

import java.util.List;

/**
 * CSV レコードの内容で比較するコンパレータ。
 * 
 * @version 1.90	2013/08/02
 * @since 1.90
 */
public interface CsvRecordComparator extends java.util.Comparator<List<String>>
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	public void sort(List<List<String>> records);
	
	public void sort(int skipHeaderRows, List<List<String>> records);
}
