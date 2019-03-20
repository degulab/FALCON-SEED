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
 *  Copyright 2007-2016  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)EtcWorkbookManager.java	3.3.0	2016/05/10
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.excel2csv;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.regex.Pattern;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.model.InternalWorkbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import ssac.util.excel2csv.poi.EtcPoiUtil;
import ssac.util.excel2csv.poi.SheetInfo;

import com.github.mygreen.cellformatter.POICellFormatter;

/**
 * <code>[Excel to CSV]</code> における、ワークブックマネージャー。
 * 
 * @version 3.3.0
 * @since 3.3.0
 */
public class EtcWorkbookManager
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	/** 変換定義シート名 **/
	static private final String	CONFIG_SHEET_NAME	= "@config";
	/** 変換定義シート名のプレフィックス **/
	static private final String	CONFIG_SHEET_PREFIX	= "@config-";

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** 対象の Excel ファイル **/
	private final File			_excelfile;
	/** 対象の Excel ブック **/
	private Workbook			_excelbook;
	/** 変換定義シートの情報オブジェクトの配列 **/
	private SheetInfo[]			_configSheetInfos;
	/** データシートの情報オブジェクトの配列 **/
	private SheetInfo[]			_dataSheetInfos;
	/** Excel-CellFormatter オブジェクト(結合セルは考慮しない) **/
	private POICellFormatter	_cellFormatter;
	/** Apache POI 標準のフォーマッター(結合セルは考慮しない) **/
	private DataFormatter		_poiFormatter;
	/** この Excel ブックの日付基準日が 1904 年開始なら <tt>true</tt>、未判定なら <tt>null</tt> **/
	private boolean				_dateStart1904;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	/**
	 * 指定されたワークブックファイルを、パスワードなしで読み取り専用で開く。
	 * @param file	ワークブックファイル
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws EncryptedDocumentException 	パスワード保護によりワークブックを開くことができない場合
	 * @throws InvalidFormatException 		ワークブックが読み込み可能な形式ではない場合
	 * @throws IOException 					入出力エラーが発生した場合
	 */
	public EtcWorkbookManager(File file)
	throws EncryptedDocumentException, InvalidFormatException, IOException
	{
		this(file, null);
	}

	/**
	 * 指定されたワークブックファイルを、指定されたパスワードで読み取り専用で開く。
	 * <em>password</em> が <tt>null</tt> の場合は、パスワードなしで開く。
	 * @param file		ワークブックファイル
	 * @param password	パスワード、もしくは <tt>null</tt>
	 * @throws NullPointerException			<em>file</em> が <tt>null</tt> の場合
	 * @throws EncryptedDocumentException 	パスワード保護によりワークブックを開くことができない場合
	 * @throws InvalidFormatException 		ワークブックが読み込み可能な形式ではない場合
	 * @throws IOException 					入出力エラーが発生した場合
	 */
	public EtcWorkbookManager(File file, String password)
	throws EncryptedDocumentException, InvalidFormatException, IOException
	{
		if (file == null)
			throw new NullPointerException();
		
		Workbook wb = null;
		// check sheets
		try {
			// open excel workbook
			wb = WorkbookFactory.create(file, password, true);
			
			ArrayList<Integer> dataSheetList   = new ArrayList<Integer>();
			ArrayList<Integer> configSheetList = new ArrayList<Integer>();
			for (int i = 0; i < wb.getNumberOfSheets(); ++i) {
				String name = wb.getSheetName(i);
				if (CONFIG_SHEET_NAME.equals(name)) {
					// @config
					configSheetList.add(i);
				}
				else if (name.startsWith(CONFIG_SHEET_PREFIX)) {
					// @config-????
					configSheetList.add(i);
				}
				else {
					// data-sheet
					dataSheetList.add(i);
				}
			}
			
			// make data-sheets array
			_dataSheetInfos = new SheetInfo[dataSheetList.size()];
			for (int i = 0; i < _dataSheetInfos.length; ++i) {
				_dataSheetInfos[i] = new SheetInfo(wb, dataSheetList.get(i));
			}
			
			// make config-sheets array
			_configSheetInfos = new SheetInfo[configSheetList.size()];
			for (int i = 0; i < _configSheetInfos.length; ++i) {
				_configSheetInfos[i] = new SheetInfo(wb, configSheetList.get(i));
			}
			
			// succeeded
			_excelfile = file;
			_excelbook = wb;
			_cellFormatter = new POICellFormatter();
			_cellFormatter.setConsiderMergedCell(false);	// 結合セルは考慮しない
			_poiFormatter  = new DataFormatter();
			_dateStart1904 = checkDateStarted1904(wb);
			wb = null;
		}
		finally {
			if (wb != null) {
				EtcPoiUtil.closeSilent(wb);
				wb = null;
			}
		}
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * <code>Excel-CellFormatter</code> ライブラリの <code>POI</code> 用フォーマッターオブジェクトを取得する。
	 * <p>このメソッドが返すフォーマッターは結合セルを考慮しないので、結合セルの基準セルから値を取得する場合は、
	 * {@link ssac.util.excel2csv.poi.EtcPoiUtil#getMergedBaseCell(org.apache.poi.ss.usermodel.Cell)} によって
	 * 結合セルの基準セルを取得すること。
	 * @return	<code>POICellFormatter</code> オブジェクト
	 */
	public POICellFormatter getCellFormatter() {
		return _cellFormatter;
	}

	/**
	 * <code>Apache POI</code> ライブラリのデータフォーマッターオブジェクトを取得する。
	 * <p>このメソッドが返すフォーマッターは結合セルを考慮しないので、結合セルの基準セルから値を取得する場合は、
	 * {@link ssac.util.excel2csv.poi.EtcPoiUtil#getMergedBaseCell(org.apache.poi.ss.usermodel.Cell)} によって
	 * 結合セルの基準セルを取得すること。
	 * @return	<code>DataFormatter</code> オブジェクト
	 */
	public DataFormatter getPoiFormatter() {
		return _poiFormatter;
	}

	/**
	 * このワークブックの抽象パスを返す。
	 * @return	ワークブックの抽象パス
	 */
	public File getFile() {
		return _excelfile;
	}

	/**
	 * このオブジェクトが保持するワークブックを返す。
	 * @return	ワークブックが開かれている場合はそのオブジェクト、そうでない場合は <tt>null</tt>
	 */
	public Workbook getWorkbook() {
		return _excelbook;
	}

	/**
	 * ワークブックが開かれているかどうかを判定する。
	 * @return	ワークブックが開かれている場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean isOpened() {
		return (_excelbook != null);
	}

	/**
	 * ワークブックを閉じる。
	 * <p>このメソッドは例外をスローしない。
	 */
	public void closeSilent() {
		if (_excelbook != null) {
			try {
				_excelbook.close();
			} catch (IOException ex) {}
			_excelbook = null;
		}
	}

	/**
	 * データが記述されているシートの有無を判定する。
	 * @return	データ用シートが一つも存在しない場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean isDataSheetsEmpty() {
		return (_dataSheetInfos.length == 0);
	}

	/**
	 * 変換定義が記述されているシートの有無を判定する。
	 * @return	変換定義用シートが一つも存在しない場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean isConfigSheetsEmpty() {
		return (_configSheetInfos.length == 0);
	}

	/**
	 * データ用シートのシート数を返す。
	 * @return	データ用シートのシート数
	 */
	public int getDataSheetCount() {
		return _dataSheetInfos.length;
	}

	/**
	 * 変換定義用シートのシート数を返す。
	 * @return	変換定義用シートのシート数
	 */
	public int getConfigSheetCount() {
		return _configSheetInfos.length;
	}

	/**
	 * データ用シートの情報オブジェクトの配列を取得する。
	 * @return	データ用シート情報の配列
	 */
	public SheetInfo[] getDataSheetInfos() {
		return _dataSheetInfos;
	}

	/**
	 * 変換定義用シートの情報オブジェクトの配列を取得する。
	 * @return	変換定義用シート情報の配列
	 */
	public SheetInfo[] getConfigSheetInfos() {
		return _configSheetInfos;
	}

	/**
	 * 指定されたシート名もしくはシートパターンに一致するデータ用シートをすべて取得する。
	 * <em>name</em> が {@link Pattern} の場合、指定された正規表現に一致するシート名を検索する。
	 * @param name	シート名もしくはシートパターン
	 * @return	シート名もしくはシートパターンに一致するシートが存在する場合はそのシート情報の配列、そうでない場合は空の配列
	 * @throws IllegalStateException	ワークブックが開かれていない場合
	 */
	public SheetInfo[] findDataSheets(Object name) {
		ensureWorkbookOpened();
		if (name == null || _dataSheetInfos.length == 0)
			return SheetInfo.EMPTY_SHEETINFO_ARRAY;
		ArrayList<SheetInfo> list = new ArrayList<SheetInfo>(_dataSheetInfos.length);
		if (name instanceof Pattern) {
			// Pattern
			Pattern inputpattern = (Pattern)name;
			for (SheetInfo sinfo : _dataSheetInfos) {
				if (inputpattern.matcher(sinfo.getSheetName()).matches()) {
					list.add(sinfo);
				}
			}
		}
		else {
			// String
			String inputname = name.toString();
			for (SheetInfo sinfo : _dataSheetInfos) {
				if (inputname.equals(sinfo.getSheetName())) {
					list.add(sinfo);
					break;	// 同一名は存在しない(はず)
				}
			}
		}
		
		if (list.isEmpty()) {
			return SheetInfo.EMPTY_SHEETINFO_ARRAY;
		} else {
			return list.toArray(new SheetInfo[list.size()]);
		}
	}

	/**
	 * 指定されたシート名もしくはシートパターンに一致するデータ用シートが存在するかどうかを判定する。
	 * <em>name</em> が {@link Pattern} の場合、指定された正規表現に一致するシート名を検索する。
	 * @param name	シート名もしくはシートパターン
	 * @return	シート名もしくはシートパターンに一致するシートが存在する場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws IllegalStateException	ワークブックが開かれていない場合
	 */
	public boolean containsDataSheet(Object name)
	{
		ensureWorkbookOpened();
		if (name == null)
			return false;
		if (name instanceof Pattern) {
			// Pattern
			Pattern inputpattern = (Pattern)name;
			for (SheetInfo sinfo : _dataSheetInfos) {
				if (inputpattern.matcher(sinfo.getSheetName()).matches()) {
					return true;
				}
			}
		}
		else {
			// String
			String inputname = name.toString();
			for (SheetInfo sinfo : _dataSheetInfos) {
				if (inputname.equals(sinfo.getSheetName())) {
					return true;
				}
			}
		}
		// not found
		return false;
	}

	/**
	 * 指定されたシート情報に対応するシートを取得する。
	 * @param sinfo	シート情報
	 * @return	{@link EtcConversionSheet} オブジェクト
	 * @throws NullPointerException			引数が <tt>null</tt> の場合
	 * @throws IllegalStateException		ワークブックが開かれていない場合
	 * @throws IndexOutOfBoundsException	インデックスが範囲外の場合
	 */
	public EtcConversionSheet getSheet(SheetInfo sinfo) {
		ensureWorkbookOpened();
		return new EtcConversionSheet(this, sinfo);
	}

	/**
	 * この Excel ワークブックにおける日付値が 1904 年開始かどうかを判定する。
	 * @return	1904 年開始なら <tt>true</tt>、1900 年開始なら <tt>false</tt>
	 */
	public boolean isDateStarted1904() {
		return _dateStart1904;
	}

	/**
	 * 指定されたセルの値を、日付時刻値として取得する。
	 * @param cell	対象のセル
	 * @return	日付時刻として適切な値であれば <code>Calendar</code> オブジェクト、そうでない場合は <tt>null</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	public Calendar getJavaCalendarValue(Cell cell) {
		if (EtcPoiUtil.isNumericCellValue(cell)) {
			return DateUtil.getJavaCalendar(cell.getNumericCellValue(), _dateStart1904);
		} else {
			return null;
		}
	}

	/**
	 * 指定された数値を、日付時刻値に変換する。
	 * @param cellValue	対象の値
	 * @return	日付時刻として適切な値であれば <code>Calendar</code> オブジェクト、そうでない場合は <tt>null</tt>
	 */
	public Calendar getJavaCalendarValue(double cellValue) {
		return DateUtil.getJavaCalendar(cellValue, _dateStart1904);
	}

	/**
	 * 指定されたセルの値を、Excel の書式に準じて整形された文字列として取得する。
	 * @param cell	対象のセル
	 * @return	整形された文字列、<em>cell</em> が <tt>null</tt> もしくはブランクセルの場合は空文字
	 */
	public String getFormattedExcelValue(Cell cell) {
		// Excel-CellFormatter で整形
		try {
			return _cellFormatter.formatAsString(cell);
		} catch (Throwable ex) {}
		
		// Apache POI のフォーマッターを使用
		try {
			return _poiFormatter.formatCellValue(cell);
		} catch (Throwable ex) {}
		
		// 整形できない場合は、Cell#toString() の結果とする
		return (cell==null ? "" : cell.toString());
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected void ensureWorkbookOpened()
	{
		if (_excelbook == null) {
			throw new IllegalStateException("Excel Workbook was already closed.");
		}
	}
	
	/**
	 * 指定された Excel ワークブックにおける日付値が 1904 年開始かどうかを判定する。
	 * @param wb	Excel ワークブック
	 * @return	1904 年開始なら <tt>true</tt>、1900 年開始なら <tt>false</tt>
	 */
	protected boolean checkDateStarted1904(Workbook wb) {
		Boolean ret = null;
		if (wb instanceof HSSFWorkbook) {
			// 拡張子が xls の場合
			try {
				Method method = HSSFWorkbook.class.getDeclaredMethod("getWorkbook");
				method.setAccessible(true);

				InternalWorkbook iw = (InternalWorkbook) method.invoke(wb);
				ret = iw.isUsing1904DateWindowing();
			}
			catch (NoSuchMethodException ex) {
//				String errmsg = "fail access method HSSFWorkbook.getWorkbook.";
				ret = null;
			}
			catch (SecurityException ex) {
//				String errmsg = "fail access method HSSFWorkbook.getWorkbook.";
				ret = null;
			}
			catch (IllegalAccessException ex) {
//				String errmsg = "fail invoke method HSSFWorkbook.getWorkbook.";
				ret = null;
			}
			catch (IllegalArgumentException ex) {
//				String errmsg = "fail invoke method HSSFWorkbook.getWorkbook.";
				ret = null;
			}
			catch (InvocationTargetException ex) {
//				String errmsg = "fail invoke method HSSFWorkbook.getWorkbook.";
				ret = null;
			}
		}
		else if (wb instanceof XSSFWorkbook) {
			// 拡張子が xlsx の場合
			try {
				Method method = XSSFWorkbook.class.getDeclaredMethod("isDate1904");
				method.setAccessible(true);
            
				ret = (Boolean)method.invoke(wb);
			}
			catch (NoSuchMethodException ex) {
//				String errmsg = "fail access method XSSFWorkbook.isDate1904.";
				ret = null;
			}
			catch (SecurityException ex) {
//				String errmsg = "fail access method XSSFWorkbook.isDate1904.";
				ret = null;
			}
			catch (IllegalAccessException ex) {
//				String errmsg = "fail invoke method XSSFWorkbook.isDate1904.";
				ret = null;
			}
			catch (IllegalArgumentException ex) {
//				String errmsg = "fail invoke method XSSFWorkbook.isDate1904.";
				ret = null;
			}
			catch (InvocationTargetException ex) {
//				String errmsg = "fail invoke method XSSFWorkbook.isDate1904.";
				ret = null;
			}
		}
		//--- inconverted
		if (ret == null) {
			// unknown workbook type
			// check OS(Mac) is 1904 started?
			ret = false;
		}
		
		return ret.booleanValue();
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
