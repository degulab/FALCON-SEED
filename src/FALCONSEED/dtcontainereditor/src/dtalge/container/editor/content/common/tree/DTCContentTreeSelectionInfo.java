/*
 * @(#)DTCContentTreeSelectionInfo.java	1.1.0	2023/01/25
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge.container.editor.content.common.tree;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.tree.TreePath;

import dtalge.container.editor.content.DtContainerContentTypes;
import dtalge.container.editor.content.DtContainerContentTypesManager;

/**
 * データコンテナ要素のツリーにおいて、選択されたツリーノードの情報を保持するオブジェクト。
 * 
 * @version 1.1.0
 * @since 1.1.0
 */
public class DTCContentTreeSelectionInfo
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	/** 選択されたすべてのノードが切り取り可能であれば {@code true}、未設定なら {@code null} */
	private Boolean	_allowCut;
	/** 選択されたすべてのノードがコピー可能であれば {@code true}、未設定なら {@code null} */
	private Boolean	_allowCopy;
	/** 選択されたすべてのノードが削除可能であれば {@code true}、未設定なら {@code null} */
	private Boolean	_allowDelete;
	
	/** 選択されたツリーノードの、共通の親ノード。ツリーノードの選択は、同一レベルに限定されるため、親は単一となる。 **/
	private IDtContainerContentTreeNode	_ndParent;
	/** 選択されたツリーノードのリスト **/
	private ArrayList<IDtContainerContentTreeNode>	_ndSelections;
	/** 選択されたツリーノードのコンテントタイプのセット **/
	private Set<DtContainerContentTypes>	_selectedContentTypes;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public DTCContentTreeSelectionInfo() {
		_ndSelections = new ArrayList<IDtContainerContentTreeNode>();
		_selectedContentTypes = new HashSet<DtContainerContentTypes>();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public boolean canCut() {
		return (_allowCut==null ? false : _allowCut);
	}
	
	public boolean canCopy() {
		return (_allowCopy==null ? false : _allowCopy);
	}
	
	public boolean canDelete() {
		return (_allowDelete==null ? false : _allowDelete);
	}
	
	public void clear() {
		_allowCut    = null;
		_allowCopy   = null;
		_allowDelete = null;
		
		_ndParent = null;
		_ndSelections.clear();
		_selectedContentTypes.clear();
	}
	
	public boolean isEmpty() {
		return _ndSelections.isEmpty();
	}
	
	public int size() {
		return _ndSelections.size();
	}
	
	public int getContentTypeCount() {
		return _selectedContentTypes.size();
	}
	
	public Set<DtContainerContentTypes> getContentTypes() {
		return _selectedContentTypes;
	}
	
	public List<IDtContainerContentTreeNode> getNodes() {
		return _ndSelections;
	}
	
	public IDtContainerContentTreeNode getParentNode() {
		return _ndParent;
	}
	
	public void setParentNode(IDtContainerContentTreeNode parent) {
		_ndParent = parent;
	}
	
	/**
	 * ツリーで選択されたすべてのパスで、このオブジェクトの内容を更新する。
	 * 既存の内容は破棄される。
	 * 指定されたツリーノードの親が共通ではない場合、もしくはルートノードの場合、このオブジェクトの内容はすべて破棄される。
	 * @param paths	ツリーで選択されたノードのパスの配列、または {@code null}
	 * @throws ClassCastException	<em>paths</em> の各パスの終端ノードが {@link IDtContainerContentTreeNode} のインスタンスではない場合
	 */
	public void setSelectionPaths(TreePath[] paths) {
		clear();
		if (paths != null && paths.length > 0) {
			//--- 先頭パスの終端ノードを追加
			IDtContainerContentTreeNode ndTarget = (IDtContainerContentTreeNode)paths[0].getLastPathComponent();
			IDtContainerContentTreeNode ndCommonParent = ndTarget.getParent();
			if (ndCommonParent == null)
				return;	// ルートノードは拒否
			add(ndTarget);
			//--- ２番目以降のパスの終端ノードを追加
			for (int i = 1; i < paths.length; i++) {
				ndTarget = (IDtContainerContentTreeNode)paths[i].getLastPathComponent();
				IDtContainerContentTreeNode ndParent = ndTarget.getParent();
				if (ndParent != ndCommonParent) {
					clear();
					return;	// 親が共通でない場合は、拒否
				}
				add(ndTarget);
			}
			//--- 共通の親を保存
			_ndParent = ndCommonParent;
		}
	}

	//------------------------------------------------------------
	// 判定
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected void add(IDtContainerContentTreeNode node) {
		DtContainerContentTypes contentType = node.getContentType();
		_selectedContentTypes.add(contentType);
		_ndSelections.add(node);
		//--- check allow cut
		if (_allowCut==null || _allowCut.booleanValue()) {
			_allowCut = DtContainerContentTypesManager.getInstance().isAllowCut(contentType);
		}
		//--- check allow copy
		if (_allowCopy==null || _allowCopy.booleanValue()) {
			if (DtContainerContentTypesManager.getInstance().isNoteObject(contentType)) {
				// ノートの場合、単一選択ならコピー可能
				_allowCopy = (_ndSelections.size() == 1);
			}
			else {
				// ノート以外
				_allowCopy = DtContainerContentTypesManager.getInstance().isAllowCopy(contentType);
			}
		}
		//--- check allow delete
		if (_allowDelete==null || _allowDelete.booleanValue()) {
			_allowDelete = DtContainerContentTypesManager.getInstance().isAllowDelete(contentType);
		}
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
