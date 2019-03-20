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
 * @(#)CsvFileModel.java	2.0.0	2012/11/07
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)CsvFileModel.java	1.17	2011/02/02
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.editor.plugin.csv;

import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import ssac.aadl.editor.document.IEditorTableDocument;
import ssac.aadl.editor.view.JEncodingComboBox;
import ssac.aadl.module.setting.AbstractSettings;
import ssac.util.Objects;
import ssac.util.Strings;
import ssac.util.Validations;
import ssac.util.io.Files;
import ssac.util.logging.AppLogger;
import ssac.util.nio.csv.CsvFileData;
import ssac.util.nio.csv.CsvParameters;
import ssac.util.nio.csv.CsvRecordCursor;
import ssac.util.process.CommandExecutor;
import ssac.util.swing.table.SpreadSheetModel;

/**
 * CSVファイルの閲覧用テーブルモデル。
 * 
 * @version 2.0.0	2012/11/07
 *
 * @since 1.16
 */
public class CsvFileModel extends SpreadSheetModel implements IEditorTableDocument
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	/**
	 * テーブルで表示可能な行数（実際は、134,217,725 行)
	 * @since 2.0.0
	 */
	static public final int	MAX_VISIBLE_ROWS	= 134210000;
	
	/** テーブルの初期容量(行) **/
	static private final int INITIAL_ROW_CAPACITY = 0;
	///** 標準カラム数 **/
	//static private final int DEFAULT_COLUMN_COUNT = 256;
	///** このモデルの最小行数 **/
	//static private final int MINIMUM_ROW_COUNT = 256;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** このドキュメントのマネージャインスタンス **/
	private final CsvFileComponentManager manager;
	
	/** CSVファイルの読み込みカーソル **/
	private final CsvRecordCursor	_csvCursor;
	
	/** CSVファイル読み込み時のエンコーディング文字セット名 **/
	private final String			_lastEncoding;
	
	/** 検索用CSVファイル読み込みカーソル **/
	private CsvRecordCursor	_csvSearchCursor;
	/**
	 * ドキュメント対象ファイルのロード時の最終更新日時
	 * @since 1.17
	 */
	private long	_lastModifiedWhenLoadingTargetFile = 0L;

	/** テーブルコンポーネントの表示領域となる行インデックスと列インデックスの領域情報 **/
	private Rectangle		_viewBox;
	/**
	 * ドキュメント変更状態を示すフラグ
	 */
	private boolean flgModified;
	/**
	 * 表示する行数
	 * @since 2.0.0
	 */
	private int	_numVisibleRows;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public CsvFileModel(final CsvFileComponentManager manager, CsvRecordCursor cursor)
	{
		super(INITIAL_ROW_CAPACITY);
		this.manager = Validations.validNotNull(manager, "'manager' argument is null.");
		this._csvCursor = Validations.validNotNull(cursor, "'cursor' argument is null.");
		this._lastEncoding = JEncodingComboBox.getAvailableCharsetName(cursor.getFileData().getEncoding());
		this._numVisibleRows = (int)Math.min(cursor.getRecordSize(), MAX_VISIBLE_ROWS);
		setModifiedFlag(false);
		updateLastModifiedTimeWhenLoadingTargetFile();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public Rectangle getViewBox() {
		return _viewBox;
	}
	
	public void setViewBox(Rectangle newValue) {
		Rectangle oldValue = getViewBox();
		if (Objects.isEqual(oldValue, newValue)) {
			return;
		}
		
		this._viewBox = newValue;
		if (newValue != null) {
			int begin = newValue.y;
			int end = newValue.y + newValue.height - 1;
			fireTableRowsUpdated(begin, end);
		}
	}

	/**
	 * このモデルに割り当てられているデータを検索するための
	 * 専用カーソルを返す。このカーソルは、このモデルデータを管理する
	 * カーソルとは異なるキャッシュを持つ。
	 * <p>このメソッドが返すカーソルは、このモデルのビューであり、
	 * カーソルリソースの開放はこのモデルで行われるため、ユーザーは
	 * リソースを開放する必要はない。
	 * @return	カーソルが取得できた場合は <code>CsvRecordCursor</code> オブジェクトを返す。
	 * 			取得できなかった場合は <tt>null</tt> を返す。
	 */
	public CsvRecordCursor getSearchCursor() {
		if (_csvSearchCursor == null) {
			_csvSearchCursor = new CsvRecordCursor(_csvCursor.getFileData());
		}
		return _csvSearchCursor;
	}

	/**
	 * このモデルに割り当てられているデータの読み込み時に指定された CSV パラメータを
	 * 取得する。このメソッドが返すパラメータのオブジェクトは複製となる。そのため、
	 * このメソッドが返すオブジェクトのインタフェースでパラメータを変更しても
	 * このドキュメントは影響を受けない。
	 * @return	<code>CsvParameters</code> オブジェクト
	 */
	public CsvParameters getCsvParameters() {
		return new CsvParameters(_csvCursor.getFileData());
	}

	//------------------------------------------------------------
	// Implement IEditorDocument interfaces
	//------------------------------------------------------------
	
	/**
	 * 表示に関するリソースを開放する。
	 * このドキュメントがコンパイルもしくは実行可能なドキュメントの場合、
	 * このメソッドの実行によってコンパイルもしくは実行に影響があってはならない。
	 * @since 1.16
	 */
	public void releaseViewResources() {
		// ビューボックスを空にする
		_viewBox = new Rectangle(0,0,0,0);
		
		// カーソルを閉じる
		if (_csvSearchCursor != null) {
			_csvSearchCursor.close();
		}
		if (_csvCursor != null) {
			_csvCursor.close();
		}
	}
	
	/**
	 * このドキュメントのタイトルを取得する。
	 * @return	このドキュメントのタイトル
	 */
	public String getTitle() {
		return getFileData().getFile().getName();
	}
	
	/**
	 * このドキュメントに保存先ファイルが指定されているかを判定する。
	 * @return	保存先ファイルが指定されていれば <tt>true</tt>
	 */
	public boolean hasTargetFile() {
		return (getFileData().getFile() != null);
	}
	
	/**
	 * このドキュメントの保存先ファイルを取得する。
	 * 保存先ファイルが定義されていない場合は <tt>null</tt> を返す。
	 * @return	保存先ファイル
	 */
	public File getTargetFile() {
		return getFileData().getFile();
	}
	
	/**
	 * このメソッドはサポートされない。
	 * @throws UnsupportedOperationException	常にこの例外をスローする
	 */
	public void setTargetFile(File newTarget) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * このドキュメントが読み込まれた時点での保存先ファイルの最終更新日時を取得する。
	 * 保存先ファイルが指定されていない場合は 0L を返す。
	 * @return	ドキュメントが読み込まれた時点での、最終更新日時
	 * @since 1.17
	 */
	public long lastModifiedTimeWhenLoadingTargetFile() {
		return _lastModifiedWhenLoadingTargetFile;
	}

	/**
	 * このドキュメントの保存先ファイルの、現在の最終更新日時を取得する。
	 * 保存先ファイルが指定されていない場合は 0L を返す。
	 * @return	最終更新日時
	 * @since 1.17
	 */
	public long lastModifiedTimeWhenCurrentTargetFile() {
		if (hasTargetFile()) {
			return getTargetFile().lastModified();
		} else {
			return 0L;
		}
	}
	
	/**
	 * このドキュメントが読み込まれた時点での保存先ファイルの最終更新日時を、
	 * 保存先ファイルの現在の更新日時で更新する。
	 * @since 1.17
	 */
	public void updateLastModifiedTimeWhenLoadingTargetFile() {
		if (hasTargetFile()) {
			_lastModifiedWhenLoadingTargetFile = getTargetFile().lastModified();
		}
	}
	
	/**
	 * このドキュメントを管理するコンポーネント・マネージャを返す。
	 * @return	コンポーネント・マネージャ
	 */
	public CsvFileComponentManager getManager() {
		return manager;
	}
	
	/**
	 * 編集不可能なため、常に <tt>false</tt> を返す。
	 */
	public boolean isNewDocument() {
		return false;
	}
	
	public boolean isModified() {
		return flgModified;
	}
	
	public void setModifiedFlag(boolean modified) {
		this.flgModified = modified;
	}
	
	public boolean canMoveTargetFile() {
		// このドキュメントは、対象ファイルを常に開いている状態なので、
		// 基本的に移動不可
		return false;
	}
	
	/**
	 * 現在のドキュメントに関連付けられたファイルのエンコーディング名を返す。
	 * このメソッドが返すエンコーディング名は、読み込み時もしくは保存時に
	 * 適用されたものとなる。
	 * @return	エンコーディング名(<tt>null</tt> 以外)
	 */
	public String getLastEncodingName() {
		return _lastEncoding;
	}

	/**
	 * このメソッドはサポートされない。
	 * @throws UnsupportedOperationException	常にこの例外をスローする
	 */
	public void save(File targetFile) throws IOException {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * 常に <tt>false</tt> を返す。
	 */
	public boolean refreshSettings() {
		return false;
	}
	
	/**
	 * 常に <tt>null</tt> を返す。
	 */
	public AbstractSettings getSettings() {
		return null;
	}
	
	/**
	 * 常に <tt>false</tt> を返す。
	 */
	public boolean hasExecutableFile() {
		return false;
	}
	
	/**
	 * 常に <tt>null</tt> を返す。
	 */
	public File getExecutableFile() {
		return null;
	}
	
	/**
	 * 常に <tt>false</tt> を返す。
	 */
	public boolean isCompilable() {
		return false;
	}
	
	/**
	 * 常に <tt>false</tt> を返す。
	 */
	public boolean isExecutable() {
		return false;
	}
	
	/**
	 * 常に <tt>null</tt> を返す。
	 */
	public CommandExecutor createCompileExecutor() {
		return null;
	}
	
	/**
	 * 常に <tt>null</tt> を返す。
	 */
	public CommandExecutor createModuleExecutor() {
		return null;
	}
	
	/**
	 * このメソッドは何も実行しない。
	 */
	public void onCompileFinished() {}

	/**
	 * 指定されたファイルのパスを、このドキュメントが格納される位置からの
	 * 相対パスに変換する。相対パスとはならない場合、絶対パスを返す。
	 * 
	 * @param file	変換するファイル
	 * @return	相対パスを示す文字列
	 */
	public String convertRelativePath(File file) {
		File parentFile = getTargetFile().getAbsoluteFile().getParentFile();
		String relPath = Files.convertAbsoluteToRelativePath(parentFile, file, '/');	// プラットフォーム依存をなくす
		if (relPath.length() < 1) {
			//--- 相対パスが空文字の場合、カレントを表す "." とする。
			relPath = ".";
		}
		return relPath;
	}

	/**
	 * 指定されたパス文字列を、このドキュメントが格納される位置からの
	 * 相対パスとみなし、絶対パスに変換する。
	 * 
	 * @param path	変換するパス文字列
	 * @return	変換後の絶対パスを示すファイル
	 */
	public File convertAbsolutePath(String path) {
		// 空文字列の場合は、このドキュメントそのものの位置を返す。
		if (Strings.isNullOrEmpty(path)) {
			return getTargetFile();
		}
		
		// 絶対パスの場合は、そのまま返す
		File file = new File(path);
		if (file.isAbsolute()) {
			return file;
		}
		
		// 絶対パスに変換する
		File parentFile = getTargetFile().getAbsoluteFile().getParentFile();
		file = new File(parentFile, path);
		return Files.normalizeFile(file);
	}

	//------------------------------------------------------------
	// Implement SpreadSheetModel interfaces
	//------------------------------------------------------------

	/**
	 * このモデルは編集不可。
	 */
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}

	/**
	 * このクラスでは、常に文字列クラスを返す。
	 * 
	 * @param columnIndex	列インデックス
	 * 
	 * @return	マクロセルクラス
	 */
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return String.class;
	}

	@Override
	public String getColumnName(int columnIndex) {
		String strName;
		try {
			strName = getFileData().getFieldName(columnIndex);
			if (strName==null) {
				// このモデルのカラム名は、1から始まる番号とする
				strName = String.valueOf(columnIndex+1);
			}
		} catch (Throwable ex) {
			// このモデルのカラム名は、1から始まる番号とする
			strName = String.valueOf(columnIndex+1);
		}
		return strName;
	}

	@Override
	public int getColumnCount() {
		return getFileData().getMaxFieldSize();
	}

	@Override
	public int getRowCount() {
		return _numVisibleRows;
	}
	
	public long getRealRowCount() {
		return getCursor().getRecordSize();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		final Rectangle viewbox = getViewBox();
		if (viewbox != null && !viewbox.contains(columnIndex, rowIndex)) {
			if (isSkipCell(rowIndex, columnIndex)) {
				return getSkipValue(rowIndex, columnIndex);
			}
		}
		try {
			return getCursor().getRecord(rowIndex)[columnIndex];
		}
		catch (Exception ex) {
			final String exMessage = ex.getLocalizedMessage();
			if (exMessage != null) {
				AppLogger.error("(" + rowIndex + ", " + columnIndex + ") Failed to read CSV file : " + exMessage, ex);
				return ex;
			} else {
				AppLogger.error("(" + rowIndex + ", " + columnIndex + ") Failed to read CSV file.", ex);
				return CsvFileMessages.getInstance().msgValueReadError;
			}
		}
	}

	@Override
	public int adjustMinimumRows(int minimumRowCount) {
		return getRowCount();
	}

	@Override
	public int getValidColumnCount(int rowIndex) {
		return getColumnCount();
	}

	@Override
	public int getValidRowCount() {
		return getRowCount();
	}
	
	//
	// following methods not supported
	//

	/**
	 * このメソッドは、サポートされない。
	 * 
	 * @throws UnsupportedOperationException	常にこの例外がスローされる
	 */
	@Override
	public void addColumn(Object columnName, ColumnDataModel columnData) {
		throw new UnsupportedOperationException();
	}

	/**
	 * このメソッドは、サポートされない。
	 * 
	 * @throws UnsupportedOperationException	常にこの例外がスローされる
	 */
	@Override
	public void addColumn(Object columnName) {
		throw new UnsupportedOperationException();
	}

	/**
	 * このメソッドは、サポートされない。
	 * 
	 * @throws UnsupportedOperationException	常にこの例外がスローされる
	 */
	@Override
	public void setData(List<List<?>> dataList, Collection<?> columnNames) {
		throw new UnsupportedOperationException();
	}

	/**
	 * このメソッドは、サポートされない。
	 * 
	 * @throws UnsupportedOperationException	常にこの例外がスローされる
	 */
	@Override
	public void setData(Object[][] dataList, Collection<?> columnNames) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * このメソッドは、サポートされない。
	 * 
	 * @throws UnsupportedOperationException	常にこの例外がスローされる
	 */
	@Override
	public void addRow(RowDataModel rowData) {
		throw new UnsupportedOperationException();
	}

	/**
	 * このメソッドは、サポートされない。
	 * 
	 * @throws UnsupportedOperationException	常にこの例外がスローされる
	 */
	@Override
	public RowDataModel getRow(int rowIndex) {
		throw new UnsupportedOperationException();
	}

	/**
	 * このメソッドは、サポートされない。
	 * 
	 * @throws UnsupportedOperationException	常にこの例外がスローされる
	 */
	@Override
	public void insertRow(int rowIndex, RowDataModel rowData) {
		throw new UnsupportedOperationException();
	}

	/**
	 * このメソッドは、サポートされない。
	 * 
	 * @throws UnsupportedOperationException	常にこの例外がスローされる
	 */
	@Override
	public void removeRow(int rowIndex) {
		throw new UnsupportedOperationException();
	}

	/**
	 * このメソッドは、サポートされない。
	 * 
	 * @throws UnsupportedOperationException	常にこの例外がスローされる
	 */
	@Override
	public void setRows(int rowCount) {
		throw new UnsupportedOperationException();
	}

	/**
	 * このメソッドは、サポートされない。
	 * 
	 * @throws UnsupportedOperationException	常にこの例外がスローされる
	 */
	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		throw new UnsupportedOperationException();
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected CsvRecordCursor getCursor() {
		return _csvCursor;
	}
	
	protected CsvFileData getFileData() {
		return getCursor().getFileData();
	}
	
	protected boolean isSkipCell(int rowIndex, int columnIndex) {
		return true;
	}
	
	protected Object getSkipValue(int rowIndex, int columnIndex) {
		Class<?> clazz = getColumnClass(columnIndex);
		if (clazz == String.class) {
			return CsvFileMessages.getInstance().msgNowValueReading;
		} else {
			return null;
		}
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
