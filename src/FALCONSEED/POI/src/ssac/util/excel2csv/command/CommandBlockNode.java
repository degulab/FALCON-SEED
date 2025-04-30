/*
 * @(#)CommandBlockNode.java	3.3.0	2016/05/10
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.excel2csv.command;

import ssac.util.excel2csv.poi.CellRect;

/**
 * <code>[Excel to CSV]</code> 変換定義におけるコマンドブロックノードのインタフェース。
 * 
 * @version 3.3.0
 * @since 3.3.0
 */
public interface CommandBlockNode extends CommandNode
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static public final String	BLOCK_BEGIN_SUFFIX	= "-begin";
	static public final String	BLOCK_END_SUFFIX	= "-end";

	//------------------------------------------------------------
	// Interfaces
	//------------------------------------------------------------

	/**
	 * コマンドブロック開始コマンド名を返す。
	 * @return	コマンドブロック開始コマンド名
	 */
	public String getBlockBeginName();
	
	/**
	 * コマンドブロック終了コマンド名を返す。
	 * @return	コマンドブロック終了コマンド名
	 */
	public String getBlockEndName();

	/**
	 * 子コマンド数を取得する。
	 * @return	子コマンド数
	 */
	public int getChildCount();

	/**
	 * 指定されたインデックスにある子コマンドを取得する。
	 * @param index	インデックス
	 * @return	子コマンドのオブジェクト
	 * @throws IndexOutOfBoundsException	インデックスが範囲外の場合
	 */
	public CommandNode getChildAt(int index);

	/**
	 * 指定されたコマンドの、このコマンドブロックでのインデックスを取得する。
	 * @param child	検索する子コマンド
	 * @return	このコマンドブロックの子コマンドであればそのインデックス、それ以外の場合は (-1)
	 */
	public int getChildIndex(CommandNode child);

	/**
	 * 指定されたコマンドを、このコマンドブロックの子コマンドリストの終端に追加する。
	 * 追加された子コマンドの親コマンドブロックには、このコマンドブロックが設定される。
	 * @param child	追加する子コマンド
	 * @return	追加された位置のインデックス
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	public int addChild(CommandNode child);

	/**
	 * 指定されたコマンドを、このコマンドブロックの子コマンドリストの指定されたインデックスに挿入する。
	 * インデックスが負の場合は子コマンドリストの先頭に挿入される。
	 * インデックスが子コマンドリストのサイズ以上の場合は子コマンドリストの終端に追加される。
	 * 挿入された子コマンドの親コマンドブロックには、このコマンドブロックが設定される。
	 * @param index	挿入位置のインデックス
	 * @param child	挿入する子コマンド
	 * @return	挿入された位置のインデックス
	 */
	public int insertChild(int index, CommandNode child);

	/**
	 * 指定されたインデックスにある子コマンドを削除する。
	 * 削除された子コマンドの親コマンドブロックには <tt>null</tt> が設定される。
	 * @param index	削除する位置のインデックス
	 * @return	インデックスが正当であれば <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean removeChild(int index);

	/**
	 * 指定されたコマンドを、このコマンドブロックから削除する。
	 * 削除された子コマンドの親コマンドブロックには <tt>null</tt> が設定される。
	 * @param child	削除するコマンド
	 * @return	削除できた場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean removeChild(CommandNode child);

	/**
	 * 子コマンドリストの容量を、現在の子コマンド数に合わせる。
	 */
	public void trimToChildList();

	/**
	 * 処理対象領域を相対座標で設定する。
	 * @param firstRowIndex	この処理対象領域の開始行インデックス(相対座標)
	 * @param firstColIndex	この処理対象領域の開始列インデックス(相対座標)
	 * @param rowCount		この処理対象領域の行数
	 * @param colCount		この処理対象領域の列数
	 */
	public void setRelativeTargetArea(int firstRowIndex, int firstColIndex, int rowCount, int colCount);

	/**
	 * 処理対象領域を相対座標で取得する。
	 * このメソッドが返すオブジェクトは、このオブジェクトが保持するインスタンスとなる。
	 * @return	相対処理対象領域を表すオブジェクト
	 */
	public CellRect getRelativeTargetArea();
}
