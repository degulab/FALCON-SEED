/*
 * @(#)IDtContainerContentTreeNode.java	1.0.0	2023/01/25
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)IDtContainerContentTreeNode.java	1.0.0	2022/12/05
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge.container.editor.content.common.tree;

import java.util.Collections;
import java.util.Enumeration;

import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

import dtalge.container.editor.content.DtContainerContentTypes;

/**
 * データコンテナ要素に対応するツリーノードのインターフェース。
 * 
 * @version 1.1.0
 */
public interface IDtContainerContentTreeNode extends MutableTreeNode
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	/** 要素が空の {@link Enumeration} **/
	static public final Enumeration<IDtContainerContentTreeNode>	EMPTY_ENUMERATION	= Collections.emptyEnumeration();

	//------------------------------------------------------------
	// Interfaces
	//------------------------------------------------------------
	
	/**
	 * このノードに設定されたコンテントタイプを取得する。
	 * なお、コンテントタイプはユーザーオブジェクトとは連動しない。
	 * @return	設定されているコンテントタイプ、設定されていない場合は {@code null}
	 */
	DtContainerContentTypes getContentType();
	
	/**
	 * このノードにコンテントタイプを設定する。
	 * @param newContentType	設定するコンテントタイプ、もしくは {@code null}
	 * @return	コンテントタイプが変更された場合は {@code true}
	 */
	boolean setContentType(DtContainerContentTypes newContentType);
	
	/**
	 * このノードにノード名が設定されているかどうかを判定する。
	 * @return	ノード名が設定されていれば {@code true}
	 */
	boolean hasNodeName();
	
	/**
	 * このノードに設定されているノード名を取得する。
	 * @return	設定されているノード名、設定されていない場合は {@code null}
	 */
	String getNodeName();
	
	/**
	 * このノードにノード名を設定する。
	 * <em>newName</em> が {@code null} もしくは空文字の場合、設定されるノード名はいずれも {@code null} となる。
	 * @param newName	設定するノード名、もしくは {@code null}
	 * @return	ノード名が変更された場合は {@code true}
	 */
	boolean setNodeName(String newName);
	
	/**
	 * このノードの親ノード内での位置を示すインデックスが設定されているかどうかを判定する。
	 * @return	インデックスが設定されていれば {@code true}、そうでない場合は {@code false}
	 */
	boolean hasNodePosition();
	
	/**
	 * このノードに設定されている、このノードの親ノード内での位置を示すインデックスを取得する。
	 * @return	設定されているインデックス、設定されていない場合は負の値
	 */
	int getNodePosition();
	
	/**
	 * このノードの親ノード内での位置を示すインデックスを設定する。
	 * @param newPosition	設定するインデックス、未設定とする場合は負の値
	 * @return	位置を示すインデックスが更新された場合は {@code true}
	 */
	boolean setNodePosition(int newPosition);
	
	/**
	 * このノードのルートからのパスを表す文字列を取得する。
	 * ルートノードを {@code '/'} とし、区切り文字も {@code '/'} とする。
	 * なお、オブジェクトの名前に  {@code '/'} が利用されている場合は、バックスラッシュ({@code '\'})でエスケープする。
	 * 
	 * @return ツリーのパスを表す文字列
	 */
	String getContentPathString();
	
	/**
	 * <em>targetNode</em> が <em>fromNode</em> の上位ノード、もしくは <em>fromNode</em> 自身であるかどうかを判定する。
	 * <em>fromtNode</em> もしくは <em>targetNode</em> が {@code null} の場合、このメソッドは {@code false} を返す。
	 * @param fromNode		基準ノード
	 * @param targetNode	判定するノード
	 * @return	<em>targetNode</em> が <em>fromNode</em> の上位ノードもしくは <em>fromNode</em> 自身である場合は {@code true}、それ以外の場合は {@code false}
	 */
	public static boolean isAncestorFrom(TreeNode fromNode, TreeNode targetNode) {
		if (fromNode != null && targetNode != null) {
			TreeNode ndAncestor = fromNode;
			do {
				if (ndAncestor == targetNode) {
					return true;
				}
			} while ((ndAncestor = ndAncestor.getParent()) != null);
		}
		// not ancestor
		return false;
	}
	
	/**
	 * <em>targetNode</em> が <em>fromNode</em> の下位ノード、もしくは <em>fromNode</em> 自身であるかどうかを判定する。
	 * <em>fromtNode</em> もしくは <em>targetNode</em> が {@code null} の場合、このメソッドは {@code false} を返す。
	 * @param fromNode		基準ノード
	 * @param targetNode	判定するノード
	 * @return	<em>targetNode</em> が <em>fromNode</em> の下位ノードもしくは <em>fromNode</em> 自身である場合は {@code true}、それ以外の場合は {@code false}
	 */
	public static boolean isDescendantFrom(TreeNode fromNode, TreeNode targetNode) {
		return isAncestorFrom(targetNode, fromNode);
	}
	
	/**
	 * <em>anotherNode</em> がこのノードの上位ノード、もしくはこのノード自身であるかどうかを判定する。
	 * <em>anotherNode</em> が {@code null} の場合、このメソッドは {@code false} を返す。
	 * @param anotherNode	判定するノード
	 * @return	このノードが <em>anotherNode</em> の下位ノードもしくはこのノード自身である場合は {@code true}、それ以外の場合は {@code false}
	 */
	default boolean isNodeAncestor(TreeNode anotherNode) {
		return isAncestorFrom(this, anotherNode);
	}
	
	/**
	 * <em>anotherNode</em> がこのノードの下位ノード、もしくはこのノード自身であるかどうかを判定する。
	 * <em>anotherNode</em> が {@code null} の場合、このメソッドは {@code false} を返す。
	 * @param anotherNode	判定するノード
	 * @return	このノードが <em>anotherNode</em> の上位ノードもしくはこのノード自身である場合は {@code true}、それ以外の場合は {@code false}
	 */
	default boolean isNodeDescendant(TreeNode anotherNode) {
		return isDescendantFrom(this, anotherNode);
	}
	
	/**
	 * このノードの最上位に位置するルートノードを取得する。
	 * @return	このノードを格納するツリーのルートノード
	 */
	default TreeNode getRoot() {
		TreeNode ndParent = this;
		TreeNode ndPrev;
		
		do {
			ndPrev = ndParent;
			ndParent = ndParent.getParent();
		} while (ndParent != null);
		
		return ndPrev;
	}
	
	/**
	 * ツリーのルートからこのノードまでのノード配列を取得する。
	 * @return	ルートからこのノードまでのノード配列
	 */
	TreeNode[] getPath();
	
	/**
	 * このノードのユーザーオブジェクトを取得する。
	 * @return	このノードに設定されたユーザーオブジェクト、設定されていない場合は {@code null}
	 */
	Object getUserObject();

	/**
	 * このノードの親ノードを取得する。
	 * @return	親ノード、設定されていない場合は {@code null}
	 */
	@Override
	IDtContainerContentTreeNode getParent();
	
	/**
	 * 親ノードを設定する。
	 * このメソッドは、<em>newParent</em> に対して何も処理を行わない。
	 * @param newParent	親ノード、もしくは {@code null}
	 * @throws ClassCastException	<em>newParent</em> が {@code null} ではなく、{@link IDtContainerContentTreeNode} の実装ではない場合
	 */
	public void setParent(MutableTreeNode newParent);

	/**
	 * このノードの子ノード配列にある、指定された位置の子ノードを取得する。
	 * @param childIndex	子ノードの位置を示すインデックス
	 * @return	指定された位置の子ノード
	 * @throws IndexOutOfBoundsException	<em>childIndex</em> が範囲外の場合
	 */
	@Override
	public IDtContainerContentTreeNode getChildAt(int childIndex);

	/**
	 * このノードの子ノードの順方向列挙を取得する。
	 * このノードの子ノード配列を変更すると、変更前に作成された子ノード列挙はどれも無効となる。
	 * @return	新たに作成された子ノードの順方向列挙を表す {@link Enumeration}
	 */
	@Override
	Enumeration<IDtContainerContentTreeNode> children();
	
	/**
	 * このノードが保持するデータのクリップボード転送が要求されているかを判定する。
	 * @return	クリップボード転送が要求されている場合は {@code true}
	 * @since 1.1.0
	 */
	public boolean hasTransferableDataModel();
	
	/**
	 * このノードが保持するデータのクリップボード転送を要求する。
	 * クリップボード転送が要求されたことを記録するものであり、転送が即座に開始されるわけではない。
	 * 対象となるデータが編集される前には、{@link #keepTransferableDataBeforeEditing()} を必ず呼び出す必要がある。
	 * @param transferData	クリップボード転送データをラップするオブジェクト
	 * @since 1.1.0
	 */
	public void setTransferTableData(DTCContentTransferTableData transferData);
	
	/**
	 * <em>transferData</em> がこのノードに設定されている場合にのみ、クリップボード転送要求をクリアする。
	 * @param transferModel	クリアするクリップボード転送データをラップしたオブジェクト
	 * @return	クリアされた場合は {@code true}
	 * @since 1.1.0
	 */
	public boolean removeTransferTableData(DTCContentTransferTableData transferData);
	
	/**
	 * このノードが保持するデータに対してクリップボード転送が要求されている場合のみ、
	 * 対象となるデータの複製を生成し、クリップボード転送要求をクリアする。
	 * クリップボード転送が要求されていないか、すでにクリアされている場合、このメソッドは何もしない。
	 * @since 1.1.0
	 */
	public void keepTransferableDataBeforeEditing();
}
