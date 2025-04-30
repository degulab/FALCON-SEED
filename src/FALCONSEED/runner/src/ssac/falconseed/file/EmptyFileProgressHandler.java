/*
 * @(#)EmptyFileProgressHandler.java	1.20	2012/03/08
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.file;


/**
 * 何もしない進捗状況監視ハンドラ。
 * 
 * @version 1.20	2012/03/08
 * @since 1.20
 */
public class EmptyFileProgressHandler implements FileProgressHandler
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static public final EmptyFileProgressHandler instance = new EmptyFileProgressHandler();

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 処理の中断が要求された場合に <tt>true</tt> を返す。
	 * このメソッドが <tt>true</tt> を返したとき、このメソッドを呼び出す側は、
	 * 可能な限り迅速に処理を中断する必要がある。
	 * @return	常に <tt>false</tt>
	 */
	public boolean isTerminateRequested() {
		return false;
	}

	/**
	 * 進捗状況を表す数値範囲の最大値を返す。
	 * @return	常に 0
	 */
	public long getProgressMaximum() {
		return 0L;
	}

	/**
	 * 現在の進捗状況を表す値を返す。
	 * @return	常に 0
	 */
	public long getProgressValue() {
		return 0L;
	}

	/**
	 * 現在の進捗状況をパーセント(0～100)で返す。
	 * @return	常に 0
	 */
	public double getProgressPercent() {
		return 0L;
	}

	/**
	 * 進捗状況を表す数値範囲の最大値に、指定された値を加算する。
	 * このインタフェースは、進捗状況を通知する側が、独自の分解能で設定した上限値を
	 * 更新するために呼び出される。
	 * @param value		加算する値
	 * @return	常に 0
	 */
	public long addProgressMaximum(long value) {
		return 0L;
	}

	/**
	 * 現在の進捗状況を表す値に、指定された値を加算する。
	 * このインタフェースは、進捗状況を通知する側が、独自の分解能で分割した現在の
	 * 進捗状況を更新するために呼び出される。
	 * @param value		加算する値
	 * @return	常に 0
	 */
	public long addProgressValue(long value) {
		return 0L;
	}

	/**
	 * 現在の進捗状況を表す値に、指定された値を設定する。
	 * このインタフェースは、進捗状況を通知する側が、独自の分解能で分割した現在の
	 * 進捗状況を更新するために呼び出される。
	 * @param value	設定する値
	 * @return	常に 0
	 */
	public long setProgressValue(long value) {
		return 0L;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
}
