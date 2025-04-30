/*
 * @(#)OutputLogFile.java	3.0.0	2014/03/11
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.process;

import java.io.File;

/**
 * 標準出力、標準エラー出力の内容を記録するためのログファイル用インタフェース。
 * <p>ログファイルの構造はCSVファイルであり、第１列は種別、第２列はデータとなる。
 * 種別には、標準出力(=1)、標準エラー出力(=2)のどちらかを記述する。
 * データは改行なども含まれるため、基本的にダブルクオートでエスケープする。
 * 
 * @version 3.0.0	2014/03/11
 * @since 3.0.0
 */
public class OutputLogFile
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** ログファイル(CSV)の抽象パス **/
	private final File	_logFile;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * このオブジェクトの新たしいインスタンスを生成する。
	 * @param targetCsvFile	ログファイルの抽象パス
	 * @throws NullPointerException	<em>targetCsvFile</em> が <tt>null</tt> の場合
	 */
	protected OutputLogFile(File targetCsvFile) {
		if (targetCsvFile == null)
			throw new NullPointerException("Target CSV file object is null.");
		_logFile = targetCsvFile;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * ログファイルの抽象パスを返す。
	 * @return	ログファイルの抽象パス
	 */
	public File getFile() {
		return _logFile;
	}

	//------------------------------------------------------------
	// Implement java.lang.Object interfaces
	//------------------------------------------------------------

	@Override
	public int hashCode() {
		return _logFile.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		
		if (obj instanceof OutputLogFile) {
			return equalFields((OutputLogFile)obj);
		}
		
		// not equals
		return false;
	}

	@Override
	public String toString() {
		return _logFile.toString();
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected boolean equalFields(OutputLogFile aObject) {
		if (!this._logFile.equals(aObject._logFile)) {
			return false;
		}
		
		// equals all fields
		return true;
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------

}
