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
 *  Copyright 2007-2011  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)SubDataFileTree.java	1.10	2011/02/14
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.runner.view;

import ssac.falconseed.data.swing.tree.DataFileTree;

/**
 * @deprecated	モジュール引数設定ダイアログのモードレス化に伴い、使用しない
 * データファイル専用のツリービューと同じモデルを持つ、
 * データファイル用ツリーコンポーネント。
 * 
 * @version 1.10	2011/02/14
 * @since 1.10
 */
public class SubDataFileTree extends DataFileTree
{
//	//------------------------------------------------------------
//	// Constants
//	//------------------------------------------------------------
//
//	//------------------------------------------------------------
//	// Fields
//	//------------------------------------------------------------
//
//	/** このツリーコンポーネントのモデル構造を持つツリービュー **/
//	private final DataFileTreeView	_view;
//
//	//------------------------------------------------------------
//	// Constructions
//	//------------------------------------------------------------
//	
//	public SubDataFileTree(final DataFileTreeView view) {
//		super(view.getTreeModel(), new SubTreeHandler());
//		this._view = view;
//		this.setDragEnabled(true);
//	}
//
//	//------------------------------------------------------------
//	// Public interfaces
//	//------------------------------------------------------------
//	
//	public void detachTreeViewModel() {
//		this.setModel(new DataFileTreeModel());
//	}
//
//	//------------------------------------------------------------
//	// Internal methods
//	//------------------------------------------------------------
//	
//	//------------------------------------------------------------
//	// Inner classes
//	//------------------------------------------------------------
//
//	/**
//	 * ツリーコンポーネントの制御を行う標準のハンドラ
//	 */
//	static protected class SubTreeHandler extends DataFileTree.DefaultTreeHandler
//	{
//		public int acceptTreeDragOverDropAction(DnDTree tree, DropTargetDragEvent dtde, TreePath dragOverPath) {
//			// ドロップ禁止
//			return TransferHandler.NONE;
//		}
//
//		public boolean canTransferImport(DnDTree tree, DataFlavor[] transferFlavors) {
//			// インポート禁止
//			return false;
//		}
//
//		public int getTransferSourceAction(DnDTree tree) {
//			int action;
//			TreePath[] paths = tree.getSelectionPaths();
//			if (paths != null && paths.length > 0) {
//				action = TransferHandler.NONE;
//				for (TreePath tp : paths) {
//					FileTreeNode node = (FileTreeNode)tp.getLastPathComponent();
//					if (!node.isRoot()) {
//						// コピーのみ可能とする
//						action = TransferHandler.COPY;
//					}
//				}
//			} else {
//				action = TransferHandler.NONE;
//			}
//			return action;
//		}
//
//		public boolean importTransferData(DnDTree tree, Transferable t) {
//			// このメソッドは使用しない
//			return false;
//		}
//
//		public boolean dropTransferData(DnDTree tree, Transferable t, int action) {
//			// 処理しない
//			return false;
//		}
//	}
}
