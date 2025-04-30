/*
 * @(#)AbDtContainerContentTreeNode.java	1.1.0	2023/01/27
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AbDtContainerContentTreeNode.java	1.0.0	2022/12/05
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge.container.editor.content.common.tree;

import java.util.Map;
import java.util.Objects;

import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

import dtalge.container.editor.content.DtContainerContentTypes;
import ssac.util.Strings;
import ssac.util.logging.AppLogger;

/**
 * {@link IDtContainerContentTreeNode} インターフェースの共通実装。
 * 
 * @version	1.1.0
 */
public abstract class AbDtContainerContentTreeNode implements IDtContainerContentTreeNode
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	/** このノードの親ノード **/
	private IDtContainerContentTreeNode	_parent;
	/** このノードに設定されたコンテントタイプ、設定されていない場合は {@code null} */
	private DtContainerContentTypes	_contentType;
	/** このノードに設定されたユーザーオブジェクト、設定されていない場合は {@code null} */
	private	Object	_userobj;
	/** このノードに設定された、親ノード内での位置を示すインデックス、未設定なら負の値 **/
	private int		_nodePos;
	/** このノードに設定されたノード名、設定されていない場合は {@code null} **/
	private String	_nodeName;
	/** このノードの表示名、未設定なら空文字列 **/
	protected String	_displayName;
	
	/**
	 * ユーザーオブジェクトとして保持されている編集用データのコピーを保持するオブジェクト。
	 * コピーが要求されている場合はそのインスタンス、要求されていない場合は {@code null} となる。
	 * @since 1.1.0
	 */
	private DTCContentTransferTableData	_requestCopyUserData;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	/**
	 * すべてのパラメーターが未設定の、新しいインスタンスを生成する。
	 */
	public AbDtContainerContentTreeNode() {
		_contentType = null;
		_nodePos = -1;
		_nodeName = null;
		_userobj = null;
		resetDisplayNodeName();
	}
	
	/**
	 * ノード位置を示すインデックスを設定なしとし、指定されたパラメーターを保持する、新しいインスタンスを生成する。
	 * @param contentType	コンテントタイプ
	 * @param nodeName		ノード名、設定しない場合は {@code null}
	 * @param userObject	ユーザーオブジェクト
	 */
	public AbDtContainerContentTreeNode(DtContainerContentTypes contentType, String nodeName, Object userObject) {
		_contentType = contentType;
		_nodePos = -1;
		_nodeName = (nodeName!=null && nodeName.isEmpty() ? null : nodeName);
		_userobj = userObject;
		resetDisplayNodeName();
	}
	
	/**
	 * ノード名を設定なしとし、指定されたパラメータを保持する、新しいインスタンスを生成する。
	 * @param contentType	コンテントタイプ
	 * @param nodePosition		親ノード内での位置を示すインデックス、設定しない場合は負の値		
	 * @param userObject	ユーザーオブジェクト
	 */
	public AbDtContainerContentTreeNode(DtContainerContentTypes contentType, int nodePosition, Object userObject) {
		_contentType = contentType;
		_nodePos = (nodePosition < 0 ? -1 : nodePosition);
		_nodeName = null;
		_userobj = userObject;
		resetDisplayNodeName();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	/**
	 * このノードのルートからのパスを表す文字列を取得する。
	 * ルートノードを {@code '/'} とし、区切り文字も {@code '/'} とする。
	 * なお、オブジェクトの名前に  {@code '/'} が利用されている場合は、バックスラッシュ({@code '\'})でエスケープする。
	 * 
	 * @return ツリーのパスを表す文字列
	 */
	public String getContentPathString() {
		TreeNode[] nodes = getPath();
		StringBuilder strbuf = Strings.getThreadLocalStringBuilder();
		strbuf.setLength(0);
		for (int i = 1; i < nodes.length; ++i) {
			appendJsonPath(strbuf, (IDtContainerContentTreeNode)nodes[i]);
		}
		return strbuf.toString();
	}
	
	static protected void appendJsonPath(StringBuilder strbuf, IDtContainerContentTreeNode aNode) {
		strbuf.append('/');	// separator
		if (aNode.hasNodeName()) {
			// node name
			String strNodeName = aNode.getNodeName();
			if (strNodeName != null) {
				int pos = strNodeName.indexOf('/');
				if (pos >= 0) {
					strbuf.append(strNodeName, 0, pos);
					for (; pos < strNodeName.length(); ++pos) {
						char ch = strNodeName.charAt(pos);
						if (ch == '/') {
							strbuf.append('\\');
						}
						strbuf.append(ch);
					}
				}
				else {
					strbuf.append(strNodeName);
				}
			}
		}
		else {
			// index
			DtContainerContentParentTreeNode ndParent = (DtContainerContentParentTreeNode)aNode.getParent();
			int index = ndParent.getIndex(aNode);
			strbuf.append(index);
		}
	}

	//------------------------------------------------------------
	// Implements IDtContainerContentTreeNode interfaces
	//------------------------------------------------------------
	
	/**
	 * このノードに設定されたコンテントタイプを取得する。
	 * なお、コンテントタイプはユーザーオブジェクトとは連動しない。
	 * @return	設定されているコンテントタイプ、設定されていない場合は {@code null}
	 */
	public DtContainerContentTypes getContentType() {
		return _contentType;
	}
	
	/**
	 * このノードにコンテントタイプを設定する。
	 * @param newContentType	設定するコンテントタイプ、もしくは {@code null}
	 * @return	コンテントタイプが変更された場合は {@code true}
	 */
	public boolean setContentType(DtContainerContentTypes newContentType) {
		if (newContentType != _contentType) {
			DtContainerContentTypes oldContentType = _contentType;
			_contentType = newContentType;
			fireContentTypeChanged(oldContentType, newContentType);
			return true;
		}
		else {
			// not modified
			return false;
		}
	}
	
	/**
	 * このノードにノード名が設定されているかどうかを判定する。
	 * @return	ノード名が設定されていれば {@code true}
	 */
	public boolean hasNodeName() {
		return (_nodeName != null);
	}
	
	/**
	 * このノードに設定されているノード名を取得する。
	 * @return	設定されているノード名、設定されていない場合は {@code null}
	 */
	public String getNodeName() {
		return _nodeName;
	}
	
	/**
	 * このノードにノード名を設定する。
	 * <em>newName</em> が {@code null} もしくは空文字の場合、設定されるノード名はいずれも {@code null} となる。
	 * @param newName	設定するノード名、もしくは {@code null}
	 * @return	ノード名が変更された場合は {@code true}
	 */
	public boolean setNodeName(String newName) {
		if (newName != null && newName.isEmpty()) {
			newName = null;
		}
		
		if (!Objects.equals(_nodeName, newName)) {
			String oldName = _nodeName;
			_nodeName = newName;
			fireNodeNameChanged(oldName, newName);
			return true;
		}
		else {
			// not modified
			return false;
		}
	}
	
	/**
	 * このノードの親ノード内での位置を示すインデックスが設定されているかどうかを判定する。
	 * @return	インデックスが設定されていれば {@code true}、そうでない場合は {@code false}
	 */
	public boolean hasNodePosition() {
		return (_nodePos >= 0);
	}
	
	/**
	 * このノードに設定されている、このノードの親ノード内での位置を示すインデックスを取得する。
	 * @return	設定されているインデックス、設定されていない場合は負の値
	 */
	public int getNodePosition() {
		return _nodePos;
	}
	
	/**
	 * このノードの親ノード内での位置を示すインデックスを設定する。
	 * @param newPosition	設定するインデックス、未設定とする場合は負の値
	 * @return	位置を示すインデックスが更新された場合は {@code true}
	 */
	public boolean setNodePosition(int newPosition) {
		if (newPosition < -1) {
			newPosition = -1;
		}
		
		if (newPosition != _nodePos) {
			int oldPos = _nodePos;
			_nodePos = newPosition;
			fireNodePositionChanged(oldPos, newPosition);
			return true;
		}
		else {
			// not modified
			return false;
		}
	}
	
	/**
	 * ツリーのルートからこのノードまでのノード配列を取得する。
	 * @return	ルートからこのノードまでのノード配列
	 */
	public TreeNode[] getPath() {
		return getPathToRootRecursive(this, 0);
	}
	
	protected TreeNode[] getPathToRootRecursive(TreeNode aNode, int depth) {
		TreeNode[]	retNodes;
		
		if (aNode == null) {
			// root のとき
			if (depth == 0) {
				// ノード指定もなく、深さも 0 はありえない
				retNodes = null;
			}
			else {
				// aNode が null のとき、root ノードの親に到達した時点で、ノード配列を生成
				retNodes = new TreeNode[depth];
			}
		}
		else {
			++depth;	// 自身も含めた深さ
			retNodes = getPathToRootRecursive(aNode.getParent(), depth);	// 基点ノードから自身までの深さと親ノードから、再帰呼び出しによりバッファを生成
			retNodes[retNodes.length - depth] = aNode;
		}
		return retNodes;
	}
	
	/**
	 * このノードが保持するデータのクリップボード転送が要求されているかを判定する。
	 * @return	クリップボード転送が要求されている場合は {@code true}
	 * @since 1.1.0
	 */
	public boolean hasTransferableDataModel() {
		return (_requestCopyUserData != null);
	}
	
	/**
	 * このノードが保持するデータのクリップボード転送を要求する。
	 * クリップボード転送が要求されたことを記録するものであり、転送が即座に開始されるわけではない。
	 * 対象となるデータが編集される前には、{@link #keepTransferableDataBeforeEditing()} を必ず呼び出す必要がある。
	 * @param transferData	クリップボード転送データをラップするオブジェクト
	 * @since 1.1.0
	 */
	public void setTransferTableData(DTCContentTransferTableData transferData) {
		_requestCopyUserData = transferData;
	}
	
	/**
	 * <em>transferData</em> がこのノードに設定されている場合にのみ、クリップボード転送要求をクリアする。
	 * @param transferModel	クリアするクリップボード転送データをラップしたオブジェクト
	 * @return	クリアされた場合は {@code true}
	 * @since 1.1.0
	 */
	public boolean removeTransferTableData(DTCContentTransferTableData transferData) {
		if (_requestCopyUserData != null && _requestCopyUserData == transferData) {
			if (AppLogger.isTraceEnabled()) {
				String msg = String.format("@@@ removed DTCContentTransferTableData instance from tree node [%s]", String.valueOf(getContentPathString()));
				AppLogger.trace(msg);
			}
			_requestCopyUserData = null;
			return true;
		}
		else {
			return false;
		}
	}
	
	/**
	 * このノードが保持するデータに対してクリップボード転送が要求されている場合のみ、
	 * 対象となるデータの複製を生成し、クリップボード転送要求をクリアする。
	 * クリップボード転送が要求されていないか、すでにクリアされている場合、このメソッドは何もしない。
	 * @since 1.1.0
	 */
	public void keepTransferableDataBeforeEditing() {
		if (_requestCopyUserData != null) {
			_requestCopyUserData.keepCopiedRows();
			_requestCopyUserData = null;
		}
	}

	//------------------------------------------------------------
	// Implements javax.swing.tree.MutableTreeNode interfaces
	//------------------------------------------------------------

	/**
	 * このノードにユーザーオブジェクトを設定する。
	 * @param object	設定するユーザーオブジェクト、もしくは {@code null}
	 */
	@Override
	public void setUserObject(Object object) {
		if (!Objects.equals(object, _userobj)) {
			Object oldObject = _userobj;
			_userobj = object;
			fireUserObjectChanged(oldObject, _userobj);
		}
	}

	/**
	 * このノードをルートにするサブツリーをツリーから削除し、このノードの親を {@code null} に設定する。
	 */
	@Override
	public void removeFromParent() {
		IDtContainerContentTreeNode parent = getParent();
        if (parent != null) {
            parent.remove(this);
        }
	}
	
	/**
	 * 親ノードを設定する。
	 * このメソッドは、<em>newParent</em> に対して何も処理を行わない。
	 * @param newParent	親ノード、もしくは {@code null}
	 * @throws ClassCastException	<em>newParent</em> が {@code null} ではなく、{@link IDtContainerContentTreeNode} の実装ではない場合
	 */
	public void setParent(MutableTreeNode newParent) {
		if (newParent != _parent) {
			IDtContainerContentTreeNode oldParent = _parent;
			_parent = (IDtContainerContentTreeNode)newParent;
			fireParentTreeNodeChanged(oldParent, _parent);
		}
	}

	//------------------------------------------------------------
	// Implements javax.swing.tree.TreeNode interfaces
	//------------------------------------------------------------

	/**
	 * このノードの親ノードを取得する。
	 * @return	親ノード、設定されていない場合は {@code null}
	 */
	@Override
	public IDtContainerContentTreeNode getParent() {
		return _parent;
	}
	
	/**
	 * このノードのユーザーオブジェクトを取得する。
	 * @return	このノードに設定されたユーザーオブジェクト、設定されていない場合は {@code null}
	 */
	@Override
	public Object getUserObject() {
		return _userobj;
	}

	//------------------------------------------------------------
	// Implements java.lang.Object interfaces
	//------------------------------------------------------------
	
	@Override
	public String toString() {
		if (_displayName == null) {
			refreshDisplayNodeName();
		}
		return _displayName;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	/**
	 * このノードの親ノードのインスタンスが変更された後に呼び出されるイベントハンドラー。
	 * 
	 * @param oldParent	元の親ノード、または {@code null}
	 * @param newParent	新しい親ノード、または {@code null}
	 */
	protected void fireParentTreeNodeChanged(IDtContainerContentTreeNode oldParent, IDtContainerContentTreeNode newParent) {
		// no implement
	}
	
	protected void fireContentTypeChanged(DtContainerContentTypes oldType, DtContainerContentTypes newType) {
		resetDisplayNodeName();
	}
	
	protected void fireUserObjectChanged(Object oldObject, Object newObject) {
		resetDisplayNodeName();
	}
	
	protected void fireNodeNameChanged(String oldName, String newName) {
		resetDisplayNodeName();
	}
	
	protected void fireNodePositionChanged(int oldPos, int newPos) {
		resetDisplayNodeName();
	}
	
	/**
	 * このノードの表示名をリセットする。
	 */
	protected void resetDisplayNodeName() {
		_displayName = null;
	}
	
	/**
	 * 現在の設定で、ノードの表示名を更新する。
	 */
	protected void refreshDisplayNodeName() {
		StringBuilder sb = Strings.getThreadLocalStringBuilder();
		sb.setLength(0);
		
		// ノード名
		boolean existNodeName;
		if (_nodeName != null) {
			// 設定済みノード名を表示
			sb.append(_nodeName);
			existNodeName = true;
		}
		else if (_nodePos >= 0) {
			// 設定済みノード位置を表示
			sb.append('[');
			sb.append(_nodePos);
			sb.append(']');
			existNodeName = true;
		}
		else {
			// ノード名なし
			existNodeName = false;
		}
		
		// ユーザーオブジェクトのデータ型
		if (_contentType != null && !Map.class.equals(_contentType.contentClass())) {
			if (existNodeName)
				sb.append(' ');	// 空白でセパレート
			sb.append('(');
			sb.append(_contentType.contentClass().getSimpleName());
			sb.append(')');
		}
		
		_displayName = sb.toString();
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
