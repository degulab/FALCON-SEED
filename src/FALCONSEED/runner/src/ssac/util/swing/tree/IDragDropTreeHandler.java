/*
 * @(#)IDragDropTreeHandler.java	1.20	2012/03/22
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.swing.tree;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTargetDragEvent;

import javax.swing.Icon;
import javax.swing.tree.TreePath;

/**
 * ドラッグ＆ドロップに対応したツリーコンポーネント専用コントローラー。
 * このコントローラーの実装により、ツリーの概観や挙動を制御する。
 * 
 * @version 1.20	2012/03/22
 * @since 1.20
 */
public interface IDragDropTreeHandler
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	/**
	 * ツリーコンポーネント上でドラッグされているデータに対して、許可するドロップアクションを返す。
	 * このメソッドは、インポート可能なデータフレーバーがツリーコンポーネント上でドラッグされているとき、
	 * 呼び出される。
	 * @param tree			ドラッグを受信するツリーコンポーネント
	 * @param dtde			ドラッグイベント
	 * @param dragOverPath	ドラッグされている位置のツリーパス
	 */
	public int acceptTreeDragOverDropAction(DragDropTree tree, DropTargetDragEvent dtde, TreePath dragOverPath);
	/**
	 * ツリーコンポーネントが実際に一連のデータフレーバのインポートを試みる前に、
	 * データフレーバのインポートを受け入れるかどうかを示す。
	 * このメソッドは、ツリーコンポーネントの <code>TransferHandler</code> によって呼び出される。
	 * @param tree				転送を受信するツリーコンポーネント
	 * @param transferFlavors	有効なデータ形式
	 * @return	データをコンポーネントに挿入できる場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean canTransferImport(DragDropTree tree, DataFlavor[] transferFlavors);
	/**
	 * データ転送のソースとして使用する <code>Transferable</code> を作成し、
	 * 転送するデータの表現を返す。転送するデータが作成できない場合は <tt>null</tt> を返す。
	 * @param tree	転送データを保持するツリーコンポーネント
	 * @return	転送するデータの表現。転送データが作成できない場合は <tt>null</tt>
	 */
	public Transferable createTransferable(DragDropTree tree);
	/**
	 * ソースがサポートする転送アクションの種類を返す。
	 * 可変ではないモデルも存在し、そのようなモデルでは COPY の転送アクションだけを使用できる。
	 * @param tree	転送データを保持するコンポーネント
	 * @return	転送アクションの種類を返す。転送をすべて無効とする場合は NONE を返す。
	 */
	public int getTransferSourceAction(DragDropTree tree);
	/**
	 * 転送を外観を設定するオブジェクトを返す。
	 * @param tree	転送データを保持するツリーコンポーネント
	 * @param t		転送されるデータ
	 * @return	ビジュアル表現となるオブジェクト。デフォルトの動作とする場合は <tt>null</tt>
	 */
	public Icon getTransferVisualRepresentation(DragDropTree tree, Transferable t);
	/**
	 * クリップボードまたは DND ドロップ操作からコンポーネントへデータを転送する。
	 * <code>Transferable</code> は、コンポーネントにインポートされるデータを表す。
	 * @param tree	転送を受信するコンポーネント。
	 * @param t		インポートするデータ
	 * @return	データがコンポーネントに挿入された場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean importTransferData(DragDropTree tree, Transferable t);
	/**
	 * ツリーコンポーネントへのドロップ操作からコンポーネントへデータを転送する。
	 * <code>Transferable</code> は、コンポーネントにインポートされるデータを表す。
	 * @param tree	転送を受信するコンポーネント
	 * @param t		インポートするデータ
	 * @param action	ドロップ時のユーザーアクション
	 * @return	コンポーネントがデータを受け入れた場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean dropTransferData(DragDropTree tree, Transferable t, int action);
}
