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
 * @(#)GenericSchemaElementTree	3.2.1	2015/07/01
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)GenericSchemaElementTree	3.2.0	2015/06/24
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.filter.generic.gui.util;

import java.awt.Color;
import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;

import ssac.falconseed.filter.generic.gui.table.GenericTableSchemaRootNode;

/**
 * 汎用フィルタ編集における、入出力スキーマ定義用ツリーコンポーネント。
 * 入力と出力のどちらでも利用する機能の実装となる。
 * 
 * @version 3.2.1
 * @since 3.2.0
 */
public class GenericSchemaElementTree extends JTree
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** このコンポーネント標準の <code>TreeCellRenderer</code> **/
	transient private TreeCellRenderer				_defTreeCellRenderer;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	public GenericSchemaElementTree() {
		super(new GenericSchemaElementTreeModel(new GenericTableSchemaRootNode()));
		initialSetup();
	}
	
	public GenericSchemaElementTree(TreeModel model) {
		super(model);
		if (model != null && !(model instanceof GenericSchemaElementTreeModel)) {
			throw new IllegalArgumentException("Tree model object is not " + GenericSchemaElementTreeModel.class.getSimpleName() + " class : " + model.getClass().getName());
		}
		initialSetup();
	}
	
	private void initialSetup() {
		// setup status
		setRootVisible(true);
		setEditable(false);
		
		// ToolTip
		ToolTipManager.sharedInstance().registerComponent(this);
		
		// setup TreeCellRenderer
		setCellRenderer(getDefaultTreeCellRenderer());
		
//		// setup TransferHandler
//		setTransferHandler(getDefaultTransferHandler());
//		
//		// setup DropSource
//		setDragEnabled(true);
//		//DnDTreeDragSource dndSource = new DnDTreeDragSource(this, DnDConstants.ACTION_COPY_OR_MOVE);
//		
//		// setup DropTargetListener
//		DefaultTreeDropTargetListener listener = getDefaultTreeDropTargetListener();
//		if (listener != null) {
//			new DropTarget(this, listener);
//		}
//		
//		// 編集アクションの設定
//		setupEditActions();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * レンダリングによって呼び出され、指定された値をテキストに変換する。
	 * この実装は、<code>value.toString</code> を返し、ほかのすべての引数を無視する。
	 * 変換を制御するには、このメソッドをサブクラス化し、必要な任意の引数を使用する。
	 * ツリーハンドラが設定されている場合は、そのハンドラのメソッドを呼び出す。
	 * @param value		テキストに変換する <code>Object</code>
	 * @param selected	ノードが選択されている場合は <tt>true</tt>
	 * @param expanded	ノードが展開されている場合は <tt>true</tt>
	 * @param leaf		ノードが葉ノードの場合は <tt>true</tt>
	 * @param row		ノードの表示行を指定する <code>int</code> 値。0 は最初の行
	 * @param hasFocus	ノードがフォーカスを持つ場合は <tt>true</tt>
	 * @return	ノードの値の <code>String</code> 表現
	 */
	@Override
	public String convertValueToText(Object value, boolean selected, boolean expanded,
									boolean leaf, int row, boolean hasFocus)
	{
		// for Default
		return super.convertValueToText(value, selected, expanded, leaf, row, hasFocus);
	}

	/**
	 * レンダリングによって呼び出され、指定された値をアイコンに変換する。
	 * 特殊な変換を行うには、このメソッドをサブクラス化し、必要な任意の引数を使用する。
	 * ツリーハンドラが設定されている場合は、そのハンドラのメソッドを呼び出す。
	 * @param value		アイコンに変換する <code>Object</code>
	 * @param selected	ノードが選択されている場合は <tt>true</tt>
	 * @param expanded	ノードが展開されている場合は <tt>true</tt>
	 * @param leaf		ノードが葉ノードの場合は <tt>true</tt>
	 * @param row		ノードの表示行を指定する <code>int</code> 値。0 は最初の行
	 * @param hasFocus	ノードがフォーカスを持つ場合は <tt>true</tt>
	 * @return	<code>Icon</code> オブジェクト。デフォルトのアイコン表示のままとする場合は <tt>null</tt> を返す。
	 */
	public Icon convertValueToIcon(Object value, boolean selected, boolean expanded,
									boolean leaf, int row, boolean hasFocus)
	{
		// for Default
		return null;
	}

	/**
	 * レンダリングによって呼び出され、指定された値のツールチップ文字列を取得する。
	 * この実装は、<code>getToolTipText()</code> を返し、すべての引数を無視する。
	 * 特殊なツールチップを取得するには、このメソッドをサブクラス化し、必要な任意の引数を使用する。
	 * ツリーハンドラが設定されている場合は、そのハンドラのメソッドを呼び出す。
	 * @param value		対象の値を示す <code>Object</code>
	 * @param selected	ノードが選択されている場合は <tt>true</tt>
	 * @param expanded	ノードが展開されている場合は <tt>true</tt>
	 * @param leaf		ノードが葉ノードの場合は <tt>true</tt>
	 * @param row		ノードの表示行を指定する <code>int</code> 値。0 は最初の行
	 * @param hasFocus	ノードがフォーカスを持つ場合は <tt>true</tt>
	 * @return	ツールチップ文字列。ツールチップを表示しない場合は <tt>null</tt>
	 */
	public String getToolTipText(Object value, boolean selected, boolean expanded,
									boolean leaf, int row, boolean hasFocus)
	{
		if (value instanceof GenericSchemaElementTreeNode) {
			GenericSchemaElementTreeNode treeNode = (GenericSchemaElementTreeNode)value;
			String strToolTip = treeNode.getAvailableErrorMessage();
			if (strToolTip != null) {
				return strToolTip;
			}
		}
		
		// for Default
		return super.getToolTipText();
	}

	//------------------------------------------------------------
	// Implement JTree interfaces
	//------------------------------------------------------------

	@Override
	public TreeModel getModel() {
		return (GenericSchemaElementTreeModel)super.getModel();
	}

	@Override
	public void setModel(TreeModel newModel) {
		if (newModel != null && !(newModel instanceof GenericSchemaElementTreeModel)) {
			throw new IllegalArgumentException("Tree model object is not " + GenericSchemaElementTreeModel.class.getSimpleName() + " class : " + newModel.getClass().getName());
		}
		super.setModel(newModel);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected TreeCellRenderer getDefaultTreeCellRenderer() {
		if (_defTreeCellRenderer == null) {
			_defTreeCellRenderer = createDefaultTreeCellRenderer();
		}
		return _defTreeCellRenderer;
	}
	
	protected GenericSchemaElementTreeCellRenderer createDefaultTreeCellRenderer() {
		GenericSchemaElementTreeCellRenderer renderer = new GenericSchemaElementTreeCellRenderer();
		return renderer;
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
	
	static protected class GenericSchemaElementTreeCellRenderer extends DefaultTreeCellRenderer
	{
		private static final long serialVersionUID = 1L;

		public Component getTreeCellRendererComponent(
				JTree tree,
				Object value,
				boolean sel,
				boolean expanded,
				boolean leaf, int row,
				boolean hasFocus)
		{
			// DnDTree 専用レンダリング
			Icon targetIcon = null;
			String tooltip = null;
			if (tree instanceof GenericSchemaElementTree) {
				GenericSchemaElementTree customTree = (GenericSchemaElementTree)tree;
				
//				// ドラッグオーバーノードの強調表示
//				if (value!=null && dndTree.getDragOverTreeObject()==value) {
//					sel = true;
//				}
				
				// 個別アイコン設定
				targetIcon = customTree.convertValueToIcon(value, sel, expanded, leaf, row, hasFocus);
				
				// ツールチップ
				tooltip = customTree.getToolTipText(value, sel, expanded, leaf, row, hasFocus);
			}
			
			// default
			super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
			
			// カラー設定
			if (value instanceof GenericSchemaElementTreeNode) {
				GenericSchemaElementTreeNode treeNode = (GenericSchemaElementTreeNode)value;
				if (treeNode.hasError()) {
					// exist error
					setForeground(Color.RED);
				}
			}
			
			// アイコン設定
			if (targetIcon != null) {
				if (!tree.isEnabled()) {
					setDisabledIcon(targetIcon);
				} else {
					setIcon(targetIcon);
				}
			}
			
			// ツールチップ設定
			setToolTipText(tooltip);
			
			// 完了
			return this;
		}
	}
}
