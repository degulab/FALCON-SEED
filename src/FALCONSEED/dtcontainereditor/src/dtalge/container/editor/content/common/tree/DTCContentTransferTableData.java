/*
 * @(#)DTCContentTransferTableData.java	1.1.0	2023/01/27
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge.container.editor.content.common.tree;

import dtalge.container.editor.content.common.table.AbDTCContentAlgeEditModel;

/**
 * 交換代数またはデータ代数編集用テーブルモデルの、クリップボードコピー用データモデル。
 * 
 * @version 1.1.0
 * @since 1.1.0
 */
public class DTCContentTransferTableData
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	/**
	 * コピー対象データモデルを保持するツリーノード。
	 */
	protected IDtContainerContentTreeNode	_holder;
	/**
	 * コピー対象のデータモデル。
	 * このインスタンスは、コピー以降に未編集のものであり、
	 * コピー以降に編集が行われた場合は {@code null}
	 */
	protected AbDTCContentAlgeEditModel	_srcModel;
	/**
	 * コピー済みのデータモデル。
	 * このインスタンスは、コピー以降の編集が行われる直前に複製された、
	 * データモデルの全行データ。
	 * コピー以降に編集が行われていない場合は、{@code null}
	 */
	protected Object[][]	_copiedRows;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public DTCContentTransferTableData(IDtContainerContentTreeNode holder, AbDTCContentAlgeEditModel dataModel) {
		_holder = holder;
		_srcModel = dataModel;
		if (holder != null) {
			_holder.setTransferTableData(this);
		}
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public IDtContainerContentTreeNode getHolder() {
		return _holder;
	}
	
	public AbDTCContentAlgeEditModel getSourceDataModel() {
		return _srcModel;
	}
	
	public Object[][] getCopiedRows() {
		return _copiedRows;
	}
	
	/**
	 * コピー対象データの行数を取得する。
	 * @return	格納領域の行数、もしくはコピー済みの行数
	 */
	public int getRowCount() {
		if (_copiedRows != null) {
			return _copiedRows.length;
		}
		else if (_srcModel != null) {
			return _srcModel.getActualRowCount();
		}
		else {
			return 0;
		}
	}
	
	public Object[] getRowAt(int rowIndex) {
		if (_copiedRows != null) {
			return _copiedRows[rowIndex];
		}
		else if (_srcModel != null) {
			validRowIndexRange(_srcModel.getActualRowCount(), rowIndex);
			return _srcModel.getRowAt(rowIndex);
		}
		else {
			validRowIndexRange(0, rowIndex);
			return null;
		}
	}
	
	/**
	 * コピー対象のデータモデルの内容が複製済みかどうかを判定する。
	 * @return	複製済みなら {@code true}
	 */
	public boolean isAlreadyCopied() {
		return (_copiedRows != null);
	}
	
	/**
	 * 複製を除去する。
	 * このメソッド呼び出し以降では、コピーの内容は失われる。
	 */
	public void clean() {
		detachSourceAndHolder();
		_copiedRows = null;
	}
	
	/**
	 * コピー対象のデータモデルの内容を保存する。
	 * すでにコピー済みである場合、このメソッドは何もしない。
	 */
	public void keepCopiedRows() {
		if (_copiedRows == null && _srcModel != null) {
			// 行を複製
			int rowCount = _srcModel.getActualRowCount();
			Object[][] copied = new Object[rowCount][];
			for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
				Object[] srcRow = _srcModel.getRowAt(rowIndex);
				Object[] dstRow = new Object[srcRow.length];
				System.arraycopy(srcRow, 0, dstRow, 0, srcRow.length);
				copied[rowIndex] = dstRow;
			}
			_copiedRows = copied;
			// ソースを破棄
			detachSourceAndHolder();
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected void detachSourceAndHolder() {
		if (_srcModel != null) {
			if (_holder != null) {
				_holder.removeTransferTableData(this);
				_holder = null;
			}
			_srcModel = null;
		}
	}

	protected final void validRowIndexRange(final int rowCount, final int index) {
		if (index < 0)
			throw new IndexOutOfBoundsException("Actual row index out of range : index(" + index + ")<0");
		if (index >= rowCount)
			throw new IndexOutOfBoundsException("Actual row index out of range : index(" + index + ")>=" + rowCount);
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
