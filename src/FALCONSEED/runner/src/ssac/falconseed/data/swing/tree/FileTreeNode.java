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
 * @(#)FileTreeNode.java	1.10	2011/02/14
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.data.swing.tree;

import java.util.Collections;
import java.util.Comparator;

import javax.swing.tree.DefaultMutableTreeNode;

import ssac.util.io.VirtualFile;

/**
 * ファイルツリー専用ノードの標準インタフェース。
 * 
 * @version 1.10	2011/02/14
 * @since 1.10
 */
public class FileTreeNode extends DefaultMutableTreeNode
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
	
	public FileTreeNode(ITreeFileData file) {
		this(file, !file.isFile());
	}
	
	protected FileTreeNode(ITreeFileData file, boolean allowsChildren) {
		super(file, allowsChildren);
	}

	//------------------------------------------------------------
	// Implement DefaultMutableTreeNode interfaces
	//------------------------------------------------------------

	@Override
	public boolean isLeaf() {
		return getUserObject().isFile();
	}

	@Override
	public ITreeFileData getUserObject() {
		return (ITreeFileData)super.getUserObject();
	}

	@Override
	public void setUserObject(Object userObject) {
		if (!(userObject instanceof ITreeFileData))
			throw new IllegalArgumentException("The specified object is not instance of ITreeFileData.");
		super.setUserObject(userObject);
	}
	
	@Override
	public String toString() {
		return getDisplayName();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public boolean canRead() {
		return getUserObject().canRead();
	}
	
	public boolean canWrite() {
		return getUserObject().canWrite();
	}

	public boolean exists() {
		return getUserObject().exists();
	}
	
	public boolean isHidden() {
		return getUserObject().isHidden();
	}
	
	public boolean isFile() {
		return getUserObject().isFile();
	}
	
	public boolean isDirectory() {
		return getUserObject().isDirectory();
	}
	
	public long lastModified() {
		return getUserObject().lastModified();
	}
	
	public String getFilename() {
		return getUserObject().getFilename();
	}
	
	public String getDisplayName() {
		return getUserObject().getDisplayName();
	}
	
	public VirtualFile getFileObject() {
		return getUserObject().getFile();
	}
	
	public String getFilePath() {
		return getUserObject().getFile().getPath();
	}
	
	@SuppressWarnings("unchecked")
	public void sortChildren(Comparator<? super FileTreeNode> nodeComparator) {
		Collections.sort(children, nodeComparator);
	}
	
	@SuppressWarnings("unchecked")
	public int binarySearch(FileTreeNode child, Comparator<? super FileTreeNode> nodeComparator) {
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
	 * @return	<code>fileObject</code> を格納する子ノードを返す。
	 */
	public FileTreeNode getChildNodeByFile(Object fileObject) {
		int numChildren = getChildCount();
		for (int i = 0; i < numChildren; i++) {
			FileTreeNode child = (FileTreeNode)getChildAt(i);
			if (child.getFileObject().equals(fileObject)) {
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
