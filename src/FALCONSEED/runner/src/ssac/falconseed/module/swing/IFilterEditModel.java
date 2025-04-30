/*
 * @(#)IFilterEditModel.java	3.1.0	2014/05/16
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module.swing;

import ssac.falconseed.file.VirtualFilePathFormatterList;
import ssac.util.io.VirtualFile;

/**
 * フィルタ編集データモデルの共通インタフェース。
 * 
 * @version 3.1.0	2014/05/16
 * @since 3.1.0
 */
public interface IFilterEditModel
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Interfaces
	//------------------------------------------------------------

	/**
	 * フィルタが編集中であれば <tt>true</tt> を返す。
	 */
	public boolean isEditing();

	/**
	 * パス整形用フォーマッターを取得する。
	 * @return	フォーマッターが存在していればそのオブジェクト、存在しない場合は <tt>null</tt>
	 */
	public VirtualFilePathFormatterList getFormatter();

	/**
	 * パスフォーマット用の基準パスを返す。
	 * @return	基準パスを示すオブジェクト、基準パスが存在しない場合は <tt>null</tt>
	 */
	public VirtualFile getBasePath();
}
