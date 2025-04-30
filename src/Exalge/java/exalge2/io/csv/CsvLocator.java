/*
 * @(#)CsvLocator.java	0.982	2009/09/13
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)CsvLocator.java	0.91	2007/08/09
 *     - created by Y.Ishizuka(PieCake.inc,)
 */

package exalge2.io.csv;

/**
 * @deprecated このインタフェースは使用されなくなりました。このインタフェースは将来削除されます。
 * 
 * CSVファイルとオブジェクトとの位置を関連付けるためのインターフェース。
 * <p>
 * 主に CSV ファイル読み込み時に、読み込みカラムとファイル上の位置との関連付けに使用される。
 * 
 * 
 * @version 0.91 2007/08/09
 * 
 * @author H.Deguchi(SOARS Project.)
 * @author Y.Ishizuka(PieCake.inc,)
 * 
 * @since 0.91
 *
 */
public interface CsvLocator {
	/**
	 * CSV ファイルの行番号を取得する。
	 * <br>
	 * CSV ファイルの行番号は 1 から始まるものとする。
	 * 
	 * @return 行番号
	 */
	public int getLineNumber();
	
	/**
	 * CSV ファイルのカラム番号を取得する。
	 * <br>
	 * CSV ファイルのカラム番号は 1 から始まるものとする。
	 * 
	 * @return カラム番号
	 */
	public int getColumnNumber();
	
	/**
	 * CSV ファイルの行番号を 1 つ進める。
	 *
	 */
	public void incrementLineNumber();
	
	/**
	 * CSV ファイルのカラム番号を 1 つ進める。
	 *
	 */
	public void incrementColumnNumber();
}
