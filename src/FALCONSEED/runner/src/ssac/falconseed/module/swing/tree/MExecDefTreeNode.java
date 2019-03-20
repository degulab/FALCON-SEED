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
 *  Copyright 2007-2015  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)MExecDefTreeNode.java	3.2.1	2015/07/08
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MExecDefTreeNode.java	2.0.0	2012/11/02
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MExecDefTreeNode.java	1.00	2010/12/20
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module.swing.tree;

import java.util.Collections;
import java.util.Comparator;

import javax.swing.tree.DefaultMutableTreeNode;

import ssac.util.io.VirtualFile;

/**
 * モジュール実行定義のファイルツリーのノード。
 * 本アプリケーションでは、プロジェクト名やフォルダ名・ファイル名は Windows 環境を
 * 基準とし、大文字小文字を区別しないでチェックする。
 * 
 * @version 3.2.1
 */
public class MExecDefTreeNode extends DefaultMutableTreeNode
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** このノードの表示名 **/
	private String	_displayName;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public MExecDefTreeNode(IMExecDefFile file) {
		this(file, !file.isFile());
	}
	
	protected MExecDefTreeNode(IMExecDefFile file, boolean allowsChildren) {
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
	public IMExecDefFile getUserObject() {
		return (IMExecDefFile)super.getUserObject();
	}

	@Override
	public void setUserObject(Object userObject) {
		if (!(userObject instanceof IMExecDefFile))
			throw new IllegalArgumentException("The specified object is not instance of IMExecDefFile.");
		super.setUserObject(userObject);
	}
	
	@Override
	public String toString() {
		return getDisplayName();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	public boolean exists() {
		return getUserObject().exists();
	}
	
	public boolean isFile() {
		return getUserObject().isFile();
	}
	
	public boolean isDirectory() {
		return getUserObject().isDirectory();
	}

	/**
	 * このノードが管理するディレクトリが、モジュール実行定義であるかを判定する。
	 * @return	モジュール実行定義のディレクトリなら <tt>true</tt>、それ以外なら <tt>false</tt>
	 */
	public boolean isExecDefData() {
		return getUserObject().isExecDefData();
	}

	/**
	 * このノードが管理するディレクトリが、フィルタマクロであるかを判定する。
	 * @return	フィルタマクロのディレクトリなら <tt>true</tt>、それ以外なら <tt>false</tt>
	 * @since 2.0.0
	 */
	public boolean isExecDefFilterMacro() {
		return getUserObject().isExecDefFilterMacro();
	}

	/**
	 * このノードが管理するディレクトリが、汎用フィルタであるかを判定する。
	 * @return	汎用フィルタのディレクトリなら <tt>true</tt>、それ以外なら <tt>false</tt>
	 * @since 3.2.1
	 */
	public boolean isExecDefGenericFilter() {
		return getUserObject().isExecDefGenericFilter();
	}
	
	public long lastModified() {
		return getUserObject().lastModified();
	}
	
	public String getFilename() {
		return getUserObject().getFilename();
	}
	
	public String getDisplayName() {
		if (_displayName != null)
			return _displayName;
		else
			return getUserObject().getDisplayName();
	}
	
	public void setDisplayName(String newName) {
		this._displayName = newName;
	}
	
	public VirtualFile getFileObject() {
		return getUserObject().getFile();
	}
	
	public String getFilePath() {
		return getUserObject().getFile().getPath();
	}
	
	@SuppressWarnings("unchecked")
	public void sortChildren(Comparator<? super MExecDefTreeNode> nodeComparator) {
		Collections.sort(children, nodeComparator);
	}
	
	@SuppressWarnings("unchecked")
	public int binarySearch(MExecDefTreeNode child, Comparator<? super MExecDefTreeNode> nodeComparator) {
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
	public MExecDefTreeNode getChildNodeByFile(Object fileObject) {
		int numChildren = getChildCount();
		for (int i = 0; i < numChildren; i++) {
			MExecDefTreeNode child = (MExecDefTreeNode)getChildAt(i);
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
	
	protected String getDisplayString() {
		return _displayName;
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
