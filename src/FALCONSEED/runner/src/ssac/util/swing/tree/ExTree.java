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
 * @(#)ExTree.java	1.20	2012/03/22
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.swing.tree;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.Icon;
import javax.swing.InputMap;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.TransferHandler;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;

import ssac.util.swing.SwingTools;
import ssac.util.swing.menu.MenuItemResource;

/**
 * {@link javax.swing.JTree} の機能拡張。
 * ツリーノードのアイコンやツールチップの表示機能を、内部メソッドのオーバーライドでカスタマイズ可能としたもの。
 * 
 * @version 1.20	2012/03/22
 * @since 1.20
 */
public class ExTree extends JTree
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static protected final String TreeEditCutActionName    = "dndtree.edit.cut";
	static protected final String TreeEditCopyActionName   = "dndtree.edit.copy";
	static protected final String TreeEditPasteActionName  = "dndtree.edit.paste";
	static protected final String TreeEditDeleteActionName = "dndtree.edit.delete";
	
	static protected final String MenuAccelerationActionName = "Dummy.menu.accelerator";

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	static protected final Action editCutAction    = new TreeEditActionHandler(TreeEditCutActionName, "cut");
	static protected final Action editCopyAction   = new TreeEditActionHandler(TreeEditCopyActionName, "copy");
	static protected final Action editPasteAction  = new TreeEditActionHandler(TreeEditPasteActionName, "paste");
	static protected final Action editDeleteAction = new TreeEditActionHandler(TreeEditDeleteActionName, "delete");
	static protected final Action dummyMenuAction  = new MenuAcceleratorActionHandler();

	/** このコンポーネント標準の <code>TreeCellRenderer</code> **/
	transient private TreeCellRenderer	_defTreeCellRenderer;

	/** このツリーの機能を制御するハンドラ **/
	protected IExTreeHandler		_treeHandler = null;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	public ExTree() {
		super();
		constructExTree();
	}

	public ExTree(Hashtable<?, ?> value) {
		super(value);
		constructExTree();
	}

	public ExTree(Object[] value) {
		super(value);
		constructExTree();
	}

	public ExTree(TreeModel newModel) {
		super(newModel);
		constructExTree();
	}

	public ExTree(TreeNode root, boolean asksAllowsChildren) {
		super(root, asksAllowsChildren);
		constructExTree();
	}

	public ExTree(TreeNode root) {
		super(root);
		constructExTree();
	}

	public ExTree(Vector<?> value) {
		super(value);
		constructExTree();
	}
	
	protected void constructExTree() {
		// setup TreeCellRenderer
		setCellRenderer(getDefaultTreeCellRenderer());
		
		// 編集アクションの設定
		// get Maps
		InputMap imap = getInputMap(WHEN_FOCUSED);
		ActionMap amap = getActionMap();
		
		// 編集アクション設定
		if (imap != null) {
			//--- cut
			SwingTools.registerActionToMaps(imap, amap,
					MenuItemResource.getEditCutShortcutKeyStroke(),
					getExTreeCutAction().getValue(Action.ACTION_COMMAND_KEY),
					getExTreeCutAction());
			//--- copy
			SwingTools.registerActionToMaps(imap, amap,
					MenuItemResource.getEditCopyShortcutKeyStroke(),
					getExTreeCopyAction().getValue(Action.ACTION_COMMAND_KEY),
					getExTreeCopyAction());
			//--- paste
			SwingTools.registerActionToMaps(imap, amap,
					MenuItemResource.getEditPasteShortcutKeyStroke(),
					getExTreePasteAction().getValue(Action.ACTION_COMMAND_KEY),
					getExTreePasteAction());
			//--- delete
			SwingTools.registerActionToMaps(imap, amap,
					MenuItemResource.getEditDeleteShortcutKeyStroke(),
					getExTreeDeleteAction().getValue(Action.ACTION_COMMAND_KEY),
					getExTreeDeleteAction());
		}
		
		// 既存のカット、コピー、ペーストアクションを変更
		Object key;
		//--- cut
		key = TransferHandler.getCutAction().getValue(Action.NAME);
		if (amap.get(key) != null) {
			amap.put(key, getExTreeCutAction());
		}
		//--- copy
		key = TransferHandler.getCopyAction().getValue(Action.NAME);
		if (amap.get(key) != null) {
			amap.put(key, getExTreeCopyAction());
		}
		//--- paste
		key = TransferHandler.getPasteAction().getValue(Action.NAME);
		if (amap.get(key) != null) {
			amap.put(key, getExTreePasteAction());
		}
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * このツリーコンポーネント固有のカット・アクションを返す。
	 * @return	カット・アクション
	 */
	static public final Action getExTreeCutAction() {
		return editCutAction;
	}
	
	/**
	 * このツリーコンポーネント固有のコピー・アクションを返す。
	 * @return	コピー・アクション
	 */
	static public final Action getExTreeCopyAction() {
		return editCopyAction;
	}
	
	/**
	 * このツリーコンポーネント固有のペースト・アクションを返す。
	 * @return	ペースト・アクション
	 */
	static public final Action getExTreePasteAction() {
		return editPasteAction;
	}
	
	/**
	 * このツリーコンポーネント固有の削除アクションを返す。
	 * @return	削除アクション
	 */
	static public final Action getExTreeDeleteAction() {
		return editDeleteAction;
	}

	/**
	 * このコンポーネントに設定されているハンドラを取得する。
	 * @return	設定されている <code>IExTreeHandler</code> オブジェクト、設定されていない場合は <tt>null</tt>
	 */
	public IExTreeHandler getTreeHanler() {
		return _treeHandler;
	}

	/**
	 * ツリーの機能を制御するハンドラを設定する。
	 * @param newHandler	<code>IExTreeHandler</code> オブジェクト
	 */
	public void setTreeHandler(IExTreeHandler newHandler) {
		_treeHandler = newHandler;
	}

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
		if (_treeHandler != null) {
			String str = _treeHandler.onConvertValueToText(this, value, selected, expanded, leaf, row, hasFocus);
			if (str != null)
				return str;
		}
		// for Default
		return super.convertValueToText(value, selected, expanded, leaf, row, hasFocus);
	}

	/**
	 * レンダリングによって呼び出され、指定された値をアイコンに変換する。
	 * 特殊な変換を行うには、このメソッドをサブクラス化し、必要な任意の引数を使用する。
	 * ツリーの描画ハンドラが設定されている場合は、そのハンドラのメソッドを呼び出す。
	 * @param value		対象の値を示す <code>Object</code>
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
		if (_treeHandler != null) {
			Icon icon = _treeHandler.onConvertValueToIcon(this, value, selected, expanded, leaf, row, hasFocus);
			if (icon != null)
				return icon;
		}
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
		if (_treeHandler != null) {
			String str = _treeHandler.onGetToolTipText(this, value, selected, expanded, leaf, row, hasFocus);
			if (str != null)
				return str;
		}
		// for Default
		return super.getToolTipText();
	}

	/**
	 * このコンポーネントに、メニューアクセラレータキーとなるキーストロークを
	 * 登録する。ここで登録されたキーストロークは、テーブル内で処理をせず、
	 * メニューコンポーネントのキーストロークとして処理される。
	 * <b>注：</b>
	 * <blockquote>
	 * 次のキーストロークは、<code>DnDTree</code> 独自の処理を実行する。
	 * <ul>
	 * <li>[Ctrl]+[X] - カット
	 * <li>[Ctrl]+[C] - コピー
	 * <li>[Ctrl]+[V] - ペースト
	 * <li>[Delete]   - 削除
	 * </ul>
	 * 上記以外のキーストロークも <code>DnDTree</code> の標準キーストローク
	 * マッピングとして登録されているものもある。
	 * </blockquote>
	 * 
	 * @param strokes	登録するキーストローク
	 * @throws NullPointerException	<code>strokes</code> が <tt>null</tt> の場合、
	 * 									もしくは、<code>strokes</code> 内の要素に <tt>null</tt> が含まれている場合
	 */
	public void registMenuAcceleratorKeyStroke(KeyStroke...strokes) {
		// check argument
		if (strokes == null)
			throw new NullPointerException("'strokes' is null.");
		
		// get Maps
		InputMap imap = getInputMap(WHEN_FOCUSED);
		ActionMap amap = getActionMap();

		// regist key strokes
		for (KeyStroke ks : strokes) {
			if (ks == null)
				throw new NullPointerException("'strokes' in null element.");
			SwingTools.registerActionToMaps(imap, amap, ks, MenuAccelerationActionName, dummyMenuAction);
		}
	}

	/**
	 * このコンポーネントのツールチップ表示を制御する。
	 * なお、現在のステータスは取得できない。
	 * @param enable	ツールチップを有効にする場合は <tt>true</tt>、無効にする場合は <tt>false</tt>
	 */
	public void setTooltipEnabled(boolean enable) {
        ToolTipManager toolTipManager = ToolTipManager.sharedInstance();
        if (enable) {
        	toolTipManager.registerComponent(this);
        } else {
        	toolTipManager.unregisterComponent(this);
        }
	}

	//------------------------------------------------------------
	// Internal Event handler
	//------------------------------------------------------------

	/**
	 * 編集機能のキーストロークによるアクションハンドラ。
	 * カット、コピー、ペースト、削除の処理を行う。
	 * @param commandKey	操作の種別を表すコマンド名
	 */
	protected void onTreeEditActionPerformed(String commandKey) {
		// 編集中の場合は、編集をキャンセル
		if (isEditing()) {
			cancelEditing();
		}
		
		// ハンドラがあれば、呼び出す
		if (_treeHandler != null) {
			_treeHandler.onTreeEditActionPerformed(this, commandKey);
		}
	}

	/**
	 * 指定された値が選択された状態かどうかを判定する。
	 * このメソッドは標準のレンダラから呼び出され、テキストやアイコンの描画前に選択状態を変更することができる。
	 * @param value		対象の値
	 * @param selected	ノードが選択されている場合は <tt>true</tt>
	 * @param expanded	ノードが展開されている場合は <tt>true</tt>
	 * @param leaf		ノードが葉ノードの場合は <tt>true</tt>
	 * @param row		ノードの表示行を指定する <code>int</code> 値。0 は最初の行
	 * @param hasFocus	ノードがフォーカスを持つ場合は <tt>true</tt>
	 * @return	選択された状態とする場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	protected boolean isValueSelectedOnRendering(Object value, boolean selected, boolean expanded,
													boolean leaf, int row, boolean hasFocus)
	{
		return selected;
	}
	
	@Override
	protected boolean processKeyBinding(KeyStroke ks, KeyEvent e, int condition, boolean pressed) {
		/*
		if (MenuItemResource.getEditCopyShortcutKeyStroke().equals(ks)) {
			StringBuilder sb = new StringBuilder();
			sb.append("[Debug] DnDTree#processKeyBinding");
			sb.append("\n  - <KeyStroke> " + ks.toString());
			sb.append("\n  - <KeyEvent>  " + e.toString());
			sb.append("\n  - <condition> " + condition);
			sb.append("\n  - <pressed>   " + pressed);
			System.err.println(sb.toString());
		}
		/**/
		if (condition == WHEN_FOCUSED) {
			// (注) JTree の場合、UI の InputMap が存在するのは WHEN_FOCUSED になっている
			//--- 基本的なJComponent.processKeyBinding を処理する。
			//--- このコンポーネントがキーバインディングを処理してしまい、
			//--- メニューアクセラレーターが有効とならない問題を回避するため、
			//--- 独自の処理によりコンポーネントのキーバインディングを処理してしまう。
			//--- getInputMap(int,boolean) や getActionMap(boolean) は、同一パッケージのみのアクセスとなっており、
			//--- 同様の操作を行うためのインタフェースは用意されていないので、create されても仕方がない
			InputMap imap = getInputMap(condition);
			ActionMap amap = getActionMap();
			if (imap != null && amap != null && isEnabled()) {
				Object binding = imap.get(ks);
				Action action = (binding == null) ? null : amap.get(binding);
				//--- Test for default Input map
				/*
				System.err.println("@@@ DnDTree#processKeyBinding - show all default key stroke mapping in InputMap");
				{
					System.err.println("  - <binding> " + String.valueOf(binding) + " / <action> " + String.valueOf(action));
					System.err.println("  - <KeyStroke> " + ks + " / <KeyEvent> " + e.toString());
					java.util.TreeMap<String, String> km = new java.util.TreeMap<String,String>();
					KeyStroke[] keys = imap.allKeys();
					for (KeyStroke mks : keys) {
						Object name = imap.get(mks);
						Action maction = amap.get(name);
						String str = "  - KS[" + mks + "] / Key[" + String.valueOf(name) + "] / Action[" + String.valueOf(maction) + "]";
						km.put(mks.toString(), str);
					}
					for (java.util.Map.Entry<String, String> entry : km.entrySet()) {
						System.err.println(entry.getValue());
					}
				}
				System.err.println("@@@ DnDTree#processKeyBinding - End of show all default key stroke mapping.");
				/**/
				//--- End of test for default input map
				if (action instanceof MenuAcceleratorActionHandler) {
					//--- この場合、キーストロークはメニューアクセラレータとして
					//--- 登録されているため、このクラスインスタンスでは処理しない
					return false;
				}
				else if (action != null) {
					boolean isProcessed = SwingUtilities.notifyAction(action, ks, e, this, e.getModifiers());
					if (isProcessed) {
						//--- ここでキーストロークが処理された場合は、以降の処理は必要ない
						//--- JTree#processKeyBinding の実装の通り
						return isProcessed;
					}
				}
			}
		}

		// このコンポーネントの標準のキーストローク処理
		return super.processKeyBinding(ks, e, condition, pressed);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected DefaultExTreeCellRenderer createDefaultTreeCellRenderer() {
		DefaultExTreeCellRenderer renderer = new DefaultExTreeCellRenderer();
		return renderer;
	}
	
	protected TreeCellRenderer getDefaultTreeCellRenderer() {
		if (_defTreeCellRenderer == null) {
			_defTreeCellRenderer = createDefaultTreeCellRenderer();
		}
		return _defTreeCellRenderer;
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------

	/**
	 * <code>ExTree</code> 専用のレンダラー。
	 * 
	 * @version 1.20	2012/03/22
	 * @since 1.20
	 */
	static class DefaultExTreeCellRenderer extends DefaultTreeCellRenderer
	{
		public Component getTreeCellRendererComponent(
				JTree tree,
				Object value,
				boolean sel,
				boolean expanded,
				boolean leaf, int row,
				boolean hasFocus)
		{
			// 専用レンダリング
			Icon targetIcon = null;
			String tooltip = null;
			if (tree instanceof ExTree) {
				ExTree extree = (ExTree)tree;
				sel = extree.isValueSelectedOnRendering(value, sel, expanded, leaf, row, hasFocus);
				//--- 個別アイコン設定
				targetIcon = extree.convertValueToIcon(value, sel, expanded, leaf, row, hasFocus);
				//--- ツールチップ
				tooltip = extree.getToolTipText(value, sel, expanded, leaf, row, hasFocus);
			}
			
			// default
			super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
			
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
	
	/**
	 * このツリーの編集アクションハンドラ。
	 * カット、コピー、ペースト、削除のキーストロークのトリガによって
	 * 実行される。
	 * 
	 * @version 1.20	2012/03/22
	 * @since 1.20
	 */
	static class TreeEditActionHandler extends AbstractAction {
		public TreeEditActionHandler(String commandKey, String name) {
			super(name);
			putValue(ACTION_COMMAND_KEY, commandKey);
		}
		
		public void actionPerformed(ActionEvent ae) {
			Object srcComp = ae.getSource();
			if (srcComp instanceof ExTree) {
				((ExTree)srcComp).onTreeEditActionPerformed(ae.getActionCommand());
			}
		}
	}

	/**
	 * メニューアクセラレータキー用のアクションハンドラ。
	 * このクラスは、JTreeのキーストロークマッピング(InputMap)を変更するために
	 * インスタンス化され、アクションマップでこのインスタンスを取得した場合は、
	 * <code>JTree#processKeyBinding()</code> を呼び出さないように処理する。
	 * これは、JTree標準のキーストロークマッピングに処理されないためのもの。
	 * <p>このクラスはダミーインスタンスとなり、このクラスインスタンスが
	 * 何らかのアクションを実行することはない。
	 * 
	 * @version 1.14	2009/12/09
	 * @since 1.14
	 */
	static class MenuAcceleratorActionHandler extends AbstractAction {
		public MenuAcceleratorActionHandler() {
			super(MenuAccelerationActionName);
		}
		
		public void actionPerformed(ActionEvent ae) {}	// 処理しない
	}
	
	static public class DefaultExTreeHandler implements IExTreeHandler
	{

		public Icon onConvertValueToIcon(ExTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus)
		{
			return null;
		}

		public String onConvertValueToText(ExTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus)
		{
			return null;
		}

		public String onGetToolTipText(ExTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus)
		{
			return null;
		}

		public void onTreeEditActionPerformed(ExTree tree, String commandKey) {}
	}
}
