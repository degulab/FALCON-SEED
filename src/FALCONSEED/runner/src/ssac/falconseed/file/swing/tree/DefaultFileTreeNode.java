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
 * @(#)DefaultFileTreeNode.java	1.20	2012/03/05
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.file.swing.tree;

import java.util.Collections;
import java.util.Comparator;

import javax.swing.tree.DefaultMutableTreeNode;

import ssac.util.io.VirtualFile;

/**
 * 汎用ファイルツリー専用ノードの標準実装。
 * 
 * @version 1.20	2012/03/05
 * @since 1.20
 */
public class DefaultFileTreeNode extends DefaultMutableTreeNode
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
	
	public DefaultFileTreeNode(IFileTreeData file) {
		super(file, !file.isFile());
	}

	//------------------------------------------------------------
	// Implement DefaultMutableTreeNode interfaces
	//------------------------------------------------------------

	@Override
	public boolean isLeaf() {
		return getFileData().isFile();
	}

	@Override
	public IFileTreeData getUserObject() {
		return (IFileTreeData)super.getUserObject();
	}

	@Override
	public void setUserObject(Object newUserObject) {
		if (!(newUserObject instanceof IFileTreeData))
			throw new IllegalArgumentException("The specified object is not instance of IFileTreeElement.");
		super.setUserObject(newUserObject);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public IFileTreeData getFileData() {
		return (IFileTreeData)super.getUserObject();
	}
	
	public void setFileData(IFileTreeData filedata) {
		if (filedata == null)
			throw new NullPointerException("The specified filedata is null.");
		super.setUserObject(filedata);
	}
	
	public boolean canRead() {
		return getFileData().canRead();
	}
	
	public boolean canWrite() {
		return getFileData().canWrite();
	}

	public boolean exists() {
		return getFileData().exists();
	}
	
	public boolean isHidden() {
		return getFileData().isHidden();
	}
	
	public boolean isFile() {
		return getFileData().isFile();
	}
	
	public boolean isDirectory() {
		return getFileData().isDirectory();
	}
	
	public long lastModified() {
		return getFileData().lastModified();
	}
	
	public String getFilename() {
		return getFileData().getFilename();
	}
	
	public String getDisplayName() {
		return getFileData().getDisplayName();
	}
	
	public VirtualFile getFileObject() {
		return getFileData().getFile();
	}
	
	public String getFilePath() {
		if (getUserObject()==null) {
			return null;
		} else {
			return getFileData().getFile().getPath();
		}
	}
	
	@Override
	public String toString() {
		return getDisplayName();
	}
	
	@SuppressWarnings("unchecked")
	public void sortChildren(Comparator<? super DefaultFileTreeNode> nodeComparator) {
		Collections.sort(children, nodeComparator);
	}
	
	@SuppressWarnings("unchecked")
	public int binarySearch(DefaultFileTreeNode child, Comparator<? super DefaultFileTreeNode> nodeComparator) {
		if (child == null)
			throw new IllegalArgumentException("The specified child object is null.");
		if (children == null || children.isEmpty())
			return (-1);
		if (nodeComparator == null) {
			// 終端のインデックス
			return (-getChildCount());
		}
		
		return Collections.binarySearch(children, child, nodeComparator);
	}

	/**
	 * <code>fileObject</code> を格納するノードを、このノードの子ノードから取得する。
	 * このメソッドでは、自身のノードは検証しない。
	 * 子ノードに <code>fileObject</code> を格納するノードが存在しない場合は <tt>null</tt> を返す。
	 * <p>
	 * このメソッドでは、ノードに格納されているオブジェクトと等しいかを判定するため、
	 * <code>equals</code> メソッドを使用する。
	 * @param fileObject	検索するキーとなるオブジェクト
	 * @return	<code>fileObject</code> を格納する，最初に発見された子ノードを返す。
	 */
	public DefaultFileTreeNode getChildNodeByFile(Object fileObject) {
		int numChildren = getChildCount();
		for (int i = 0; i < numChildren; i++) {
			DefaultFileTreeNode child = (DefaultFileTreeNode)getChildAt(i);
			VirtualFile childFile = child.getFileObject();
			if (childFile == null) {
				if (fileObject == null) {
					return child;
				}
			}
			else if (childFile.equals(fileObject)) {
				return child;
			}
		}
		
		// not found
		return null;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
