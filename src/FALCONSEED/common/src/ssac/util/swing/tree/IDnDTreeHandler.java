/*
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *  Copyright 2007-2009  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)IDnDTreeHandler.java	1.14	2009/12/09
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
 * @version 1.14	2009/12/09
 * @since 1.14
 */
public interface IDnDTreeHandler
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

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
	 * ツリーコンポーネント上でドラッグされているデータに対して、許可するドロップアクションを返す。
	 * このメソッドは、インポート可能なデータフレーバーがツリーコンポーネント上でドラッグされているとき、
	 * 呼び出される。
	 * @param tree			ドラッグを受信するツリーコンポーネント
	 * @param dtde			ドラッグイベント
	 * @param dragOverPath	ドラッグされている位置のツリーパス
	 */
	public int acceptTreeDragOverDropAction(DnDTree tree, DropTargetDragEvent dtde, TreePath dragOverPath);
	/**
	 * ツリーコンポーネントが実際に一連のデータフレーバのインポートを試みる前に、
	 * データフレーバのインポートを受け入れるかどうかを示す。
	 * このメソッドは、ツリーコンポーネントの <code>TransferHandler</code> によって呼び出される。
	 * @param tree				転送を受信するツリーコンポーネント
	 * @param transferFlavors	有効なデータ形式
	 * @return	データをコンポーネントに挿入できる場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean canTransferImport(DnDTree tree, DataFlavor[] transferFlavors);
	/**
	 * データ転送のソースとして使用する <code>Transferable</code> を作成し、
	 * 転送するデータの表現を返す。転送するデータが作成できない場合は <tt>null</tt> を返す。
	 * @param tree	転送データを保持するツリーコンポーネント
	 * @return	転送するデータの表現。転送データが作成できない場合は <tt>null</tt>
	 */
	public Transferable createTransferable(DnDTree tree);
	/**
	 * ソースがサポートする転送アクションの種類を返す。
	 * 可変ではないモデルも存在し、そのようなモデルでは COPY の転送アクションだけを使用できる。
	 * @param tree	転送データを保持するコンポーネント
	 * @return	転送アクションの種類を返す。転送をすべて無効とする場合は NONE を返す。
	 */
	public int getTransferSourceAction(DnDTree tree);
	/**
	 * 転送を外観を設定するオブジェクトを返す。
	 * @param tree	転送データを保持するツリーコンポーネント
	 * @param t		転送されるデータ
	 * @return	ビジュアル表現となるオブジェクト。デフォルトの動作とする場合は <tt>null</tt>
	 */
	public Icon getTransferVisualRepresentation(DnDTree tree, Transferable t);
	/**
	 * クリップボードまたは DND ドロップ操作からコンポーネントへデータを転送する。
	 * <code>Transferable</code> は、コンポーネントにインポートされるデータを表す。
	 * @param tree	転送を受信するコンポーネント。
	 * @param t		インポートするデータ
	 * @return	データがコンポーネントに挿入された場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean importTransferData(DnDTree tree, Transferable t);
	/**
	 * ツリーコンポーネントへのドロップ操作からコンポーネントへデータを転送する。
	 * <code>Transferable</code> は、コンポーネントにインポートされるデータを表す。
	 * @param tree	転送を受信するコンポーネント
	 * @param t		インポートするデータ
	 * @param action	ドロップ時のユーザーアクション
	 * @return	コンポーネントがデータを受け入れた場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean dropTransferData(DnDTree tree, Transferable t, int action);

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
}
