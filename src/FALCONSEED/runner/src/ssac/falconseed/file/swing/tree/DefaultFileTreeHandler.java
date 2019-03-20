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
 *  Copyright 2007-2012  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)DefaultFileTreeHandler.java	1.20	2012/03/24
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.file.swing.tree;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTargetDragEvent;
import java.util.LinkedHashSet;

import javax.swing.Icon;
import javax.swing.TransferHandler;
import javax.swing.tree.TreePath;

import ssac.falconseed.file.VirtualFileTransferable;
import ssac.util.io.VirtualFile;
import ssac.util.swing.tree.DnDTree;
import ssac.util.swing.tree.IDnDTreeHandler;

/**
 * 汎用ファイルツリー専用の標準ツリーハンドラ。
 * 
 * @version 1.20	2012/03/24
 * @since 1.20
 */
public class DefaultFileTreeHandler implements IDnDTreeHandler
{
	public int acceptTreeDragOverDropAction(DnDTree tree, DropTargetDragEvent dtde, TreePath dragOverPath)
	{
		// ドロップ先ノードを取得
		if (dragOverPath == null) {
			// ドロップ先ノードが存在しない場合は、無効
			return TransferHandler.NONE;
		}
		
		// データソースがこの同じコンポーネント上のものでなければ、コピーのみ許可する。
		int retDropAction;
		if (tree.isDnDLocalDragging()) {
			retDropAction = dtde.getDropAction();
		} else {
			retDropAction = TransferHandler.COPY;
		}
		
		return retDropAction;
	}

	public boolean canTransferImport(DnDTree tree, DataFlavor[] transferFlavors) {
		// VirtualFileに変換可能な形式なら、インポート許可
		if (VirtualFileTransferable.containsSupportableDataFlavor(transferFlavors)) {
			return true;
		}
		
		// 禁止
		return false;
	}

	public Transferable createTransferable(DnDTree tree) {
		if (tree instanceof FileTree) {
			VirtualFile[] files = ((FileTree)tree).getSelectionFiles();
			if (files != null && files.length > 0) {
				// 操作対象のファイルリストを作成
				LinkedHashSet<VirtualFile> srcFileSet = new LinkedHashSet<VirtualFile>();
				for (VirtualFile srcFile : files) {
					srcFileSet.add(srcFile);
				}
				return new VirtualFileTransferable(srcFileSet);
			} else {
				// 操作対象が存在しない
				return null;
			}
		} else {
			return null;
		}
	}

	public int getTransferSourceAction(DnDTree tree) {
		int action;
		TreePath[] paths = tree.getSelectionPaths();
		if (paths != null && paths.length > 0) {
			action = TransferHandler.NONE;
			for (TreePath tp : paths) {
				DefaultFileTreeNode node = (DefaultFileTreeNode)tp.getLastPathComponent();
				if (node.isRoot()) {
					// ルートノードが含まれている場合は，コピーのみ許可
					action = TransferHandler.COPY;
					break;
				} else {
					// ルートノード以外なら，コピーまたは移動を許可
					action = TransferHandler.COPY_OR_MOVE;
				}
			}
		} else {
			action = TransferHandler.NONE;
		}
		return action;
	}

	public Icon getTransferVisualRepresentation(DnDTree tree, Transferable t) {
		return null;
	}

	public boolean importTransferData(DnDTree tree, Transferable t) {
		// このメソッドは使用しない
		return false;
	}

	public boolean dropTransferData(DnDTree tree, Transferable t, int action) {
		// FileTree インスタンス以外は処理しない
		if (!(tree instanceof FileTree)) {
			return false;
		}
		
		// 転送データを取得
		VirtualFile[] files = VirtualFileTransferable.getFilesFromTransferData(t);
		if (files == null || files.length <= 0) {
			return false;	// No data
		}
		
		// 転送先の位置を取得
		TreePath tp = tree.getDragOverPath();
		if (tp == null) {
			return false;	// No target
		}
		
		// 転送
		boolean result;
		if (action == TransferHandler.MOVE) {
			if (tree.isDnDLocalDragging()) {
				result = moveTransferable((FileTree)tree, files, tp);
			} else {
				// 移動は、ローカルのDrag&Dropのみ許可する
				result = false;
			}
		}
		else if (action == TransferHandler.COPY) {
			result = copyTransferable((FileTree)tree, files, tp);
		}
		else {
			// no action
			result = false;
		}
		
		// 完了
		return result;
	}
	
	protected boolean copyTransferable(FileTree tree, VirtualFile[] sourceFiles, TreePath targetPath) {
		return false;
	}
	
	protected boolean moveTransferable(FileTree tree, VirtualFile[] sourceFiles, TreePath targetPath) {
		return false;
	}
}
