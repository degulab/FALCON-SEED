/*
 * @(#)IRecordPointer.java	1.16	2010/09/27
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.nio;

import java.io.IOException;

/**
 * インデックスに対応するレコードの開始と終了位置を表すファイルポインタ。
 * 
 * @version 1.16	2010/09/27
 * @since 1.16
 */
public interface IFileRecordPointer extends ICloseable
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Interfaces
	//------------------------------------------------------------

	/**
	 * レコード数を返す。
	 */
	public long getRecordSize();

	/**
	 * 指定されたインデックスに対応するレコードの開始位置を返す。
	 * 
	 * @param index		レコードのインデックス
	 * @return	レコードの開始位置
	 * @throws IOException	入出力エラーが発生した場合
	 */
	public long getBegin(long index) throws IOException;

	/**
	 * 指定されたインデックスに対応するレコードの終端位置を返す。
	 * このメソッドが返す終了位置はレコード終端であり、次のレコード開始位置ではない。
	 * 
	 * @param index		レコードのインデックス
	 * @return	レコードの終端位置
	 * @throws IOException	入出力エラーが発生した場合
	 */
	public long getEnd(long index) throws IOException;
}
