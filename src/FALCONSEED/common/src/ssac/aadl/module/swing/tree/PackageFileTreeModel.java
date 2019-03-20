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
 * @(#)PackageFileTreeModel.java	1.14	2009/12/14
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.module.swing.tree;

import java.util.Comparator;

import javax.swing.tree.TreeNode;

import ssac.util.io.VirtualFile;
import ssac.util.io.VirtualFileFilter;
import ssac.util.io.VirtualFileOperationException;
import ssac.util.swing.tree.VirtualFileTreeNode;

/**
 * モジュールパッケージを単一ファイルのように扱うツリーモデル。
 * モジュールマネージャのツリー表示や、パッケージアクセス用
 * ダイアログのツリーモデルとして使用する。
 * 
 * @version 1.14	2009/12/14
 * @since 1.14
 */
public class PackageFileTreeModel extends ModuleFileTreeModel
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static protected final DefaultPackageFileFilter DefaultPackageFilter = new DefaultPackageFileFilter();
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	public PackageFileTreeModel(VirtualFile rootPath) {
		this(rootPath, DefaultPackageFilter, DefaultComparator);
	}

	public PackageFileTreeModel(PackageFileTreeNode root) {
		this(root, DefaultPackageFilter, DefaultComparator);
	}
	
	protected PackageFileTreeModel(VirtualFile rootPath, VirtualFileFilter filter, Comparator<VirtualFileTreeNode> comparator) {
		super(rootPath, filter, comparator);
	}
	
	protected PackageFileTreeModel(VirtualFileTreeNode rootNode, VirtualFileFilter filter, Comparator<VirtualFileTreeNode> comparator) {
		super(rootNode, filter, comparator);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	static public VirtualFileFilter getDefaultFileFilter() {
		return DefaultPackageFilter;
	}

	/**
	 * このメソッドは、このクラスではサポートしない
	 */
	@Override
	public ModuleFileTreeNode createFile(VirtualFileTreeNode parent, String name) throws VirtualFileOperationException {
		throw new UnsupportedOperationException();
	}

	/**
	 * このメソッドは、このクラスではサポートしない
	 */
	@Override
	public ModuleFileTreeNode createProject(String projectName) throws VirtualFileOperationException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isLeaf(Object node) {
		if (node instanceof ModuleFileTreeNode) {
			ModuleFileTreeNode ndFile = (ModuleFileTreeNode)node;
			//--- パッケージルートは葉ノードとして扱う
			if (ndFile.isModulePackageRoot()) {
				return true;
			} else {
				return ndFile.isLeaf();
			}
		} else {
			return super.isLeaf(node);
		}
	}

	//------------------------------------------------------------
	// Implements FileTreeModel interfaces
	//------------------------------------------------------------

	@Override
	public void setRoot(TreeNode root) {
		// PackageFileTreeNode 以外のインスタンスを許可しない
		super.setRoot((PackageFileTreeNode)root);
	}

	@Override
	protected PackageFileTreeNode createTreeNode(VirtualFile fileObject) {
		return new PackageFileTreeNode(fileObject);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	/**
	 * エディタのツリービューに表示するファイルのみを選択するフィルタ
	 */
	static public class DefaultPackageFileFilter implements VirtualFileFilter
	{
		public boolean accept(VirtualFile pathname) {
			// 隠しファイルは表示しない
			if (pathname.isHidden()) {
				return false;
			}
			
			// ディレクトリ以外は非表示とする
			if (!pathname.isDirectory()) {
				return false;
			}
			
			// 表示許可する
			return true;
		}
	}
}
