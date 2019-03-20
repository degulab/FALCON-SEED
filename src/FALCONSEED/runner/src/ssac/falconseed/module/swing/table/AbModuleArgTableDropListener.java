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
 * @(#)AbArgValueDropTargetListener.java	2.0.0	2012/11/08
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module.swing.table;

import java.awt.Point;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;

import ssac.aadl.module.swing.table.ModuleArgTable;
import ssac.util.logging.AppLogger;

/**
 * モジュール実行定義用引数を表示もしくは設定するテーブルペインの共通実装。
 * <p>
 * このクラスの利用においては、コンストラクタによるインスタンス生成後、
 * 必ず {@link #initialComponent()} を呼び出すこと。
 * 
 * @version 2.0.0	2012/11/08
 */
public abstract class AbModuleArgTableDropListener implements DropTargetListener
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	protected boolean			_dropEnabled = true;
	protected ModuleArgTable	_table;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public AbModuleArgTableDropListener() {
		this(null);
	}
	
	public AbModuleArgTableDropListener(final ModuleArgTable table) {
		this._table = table;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public boolean isDataDropEnabled() {
		return _dropEnabled;
	}
	
	public void setDataDropEnabled(boolean toEnable) {
		this._dropEnabled = toEnable;
	}
	
	public ModuleArgTable getTableComponent() {
		return _table;
	}
	
	public void setTableComponent(final ModuleArgTable table) {
		this._table = table;
	}

	//------------------------------------------------------------
	// Event handler
	//------------------------------------------------------------

	@Override
	public void dropActionChanged(DropTargetDragEvent dtde) {}
	
	public void dragEnter(DropTargetDragEvent dtde) {
		if (!isDataDropEnabled()) {
			dtde.rejectDrag();
			return;
		}
		
		// ソースアクションにコピーが含まれていない場合は、許可しない
		if ((dtde.getSourceActions() & DnDConstants.ACTION_COPY) == 0) {
			// サポートされないドロップアクション
			dtde.rejectDrag();
			return;
		}
		
		// サポートする DataFlavor のチェック
		if (!acceptDataFlavor(dtde)) {
			// サポートされないデータ形式
			dtde.rejectDrag();
			return;
		}
		
		// ドロップアクションをコピー操作に限定
		dtde.acceptDrag(DnDConstants.ACTION_COPY);
	}

	@Override
	public void dragExit(DropTargetEvent dte) {
	}

	@Override
	public void dragOver(DropTargetDragEvent dtde) {
		if (!isDataDropEnabled()) {
			dtde.rejectDrag();
			return;
		}
		
		if (acceptDataFlavor(dtde)) {
			// Drop 位置のカラム判定
			ModuleArgTable table = getTableComponent();
			Point pos = dtde.getLocation();
			int row = table.rowAtPoint(pos);
			int col = table.columnAtPoint(pos);
			if (acceptDropPosition(row, col, table)) {
				dtde.acceptDrag(DnDConstants.ACTION_COPY);
				return;
			}
		}
		
		dtde.rejectDrag();
	}

	@Override
	public void drop(DropTargetDropEvent dtde) {
		if (!isDataDropEnabled()) {
			dtde.rejectDrop();
			return;
		}
		
		// ソースアクションにコピーが含まれていない場合は、許可しない
		if ((dtde.getSourceActions() & DnDConstants.ACTION_COPY) == 0) {
			// サポートされないドロップアクション
			dtde.rejectDrop();
			return;
		}
		
		// サポートする DataFlavor のチェック
		if (!acceptDataFlavor(dtde)) {
			// サポートされないデータ形式
			dtde.rejectDrop();
			return;
		}

		// カラム位置の取得
		ModuleArgTable table = getTableComponent();
		int row = table.rowAtPoint(dtde.getLocation());
		int col = table.columnAtPoint(dtde.getLocation());
		if (!acceptDropPosition(row, col, table)) {
			// 許容されない位置
			dtde.rejectDrop();
			return;
		}

		// データソースの取得
		dtde.acceptDrop(DnDConstants.ACTION_COPY);
		Transferable t = dtde.getTransferable();
		if (t == null) {
			dtde.rejectDrop();
			return;
		}
		
		// ドロップデータの受け入れ
		try {
			onDropData(dtde, table, row, col, t);
		}
		catch (Throwable ex) {
			AppLogger.error("Failed to drop to Table.", ex);
		}
		
		dtde.rejectDrop();
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	/**
	 * データがドロップされ、ドロップが受け付けられているときに呼び出される。
	 * @param dtde			ドロップイベントオブジェクト
	 * @param table			ドロップターゲットのテーブル
	 * @param rowIndex		ドロップ位置となる表示上の行インデックス
	 * @param columnIndex	ドロップ位置となる表示上の列インデックス
	 * @param t				転送データ
	 */
	abstract protected void onDropData(DropTargetDropEvent dtde, ModuleArgTable table, int rowIndex, int columnIndex, Transferable t);

	/**
	 * ドラッグ操作時のデータフレーバを受け付けるかを判定する。
	 * この判定は、<code>dtde.isDataFlavorSupported()</code> メソッドを使用して、
	 * 任意のデータフレーバーがサポートされているかを判定する。
	 * @param dtde	ドラッグ中のイベントオブジェクト
	 * @return	ドロップを受け付ける場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	abstract protected boolean acceptDataFlavor(final DropTargetDragEvent dtde);
	
	/**
	 * ドロップ時のデータフレーバを受け付けるかを判定する。
	 * この判定は、<code>dtde.isDataFlavorSupported()</code> メソッドを使用して、
	 * 任意のデータフレーバーがサポートされているかを判定する。
	 * @param dtde	ドラッグ中のイベントオブジェクト
	 * @return	ドロップを受け付ける場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	abstract protected boolean acceptDataFlavor(final DropTargetDropEvent dtde);

	/**
	 * ドロップ位置に対応する行列インデックスが、受け入れ可能なインデックスかを判定する。
	 * このメソッドに渡される行列インデックスは、マウスカーソル位置から特定されたものであり、
	 * カーソル位置に該当する行もしくは列が存在しない場合は (-1) が渡される。
	 * @param rowIndex	表示上の行インデックス
	 * @param columnIndex	表示上の列インデックス
	 * @param table		ドロップターゲットのテーブル
	 * @return	ドロップを受け付ける場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	protected boolean acceptDropPosition(int rowIndex, int columnIndex, ModuleArgTable table) {
		return (rowIndex >= 0 && columnIndex >= 0);
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
