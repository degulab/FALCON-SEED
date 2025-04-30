package dtalge.container.util.internal.csv;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import dtalge.DtBase;
import dtalge.DtBaseSet;
import dtalge.Dtalge;
import dtalge.container.DtBinder;
import dtalge.container.DtSlip;
import dtalge.container.DtSlipList;
import dtalge.container.util.DtDoubleEntrySimpleSlipsCsvFormatError;
import dtalge.container.util.internal.BaseKeyTransformer;
import dtalge.io.internal.CsvReader.CsvField;
import dtalge.io.internal.CsvReader.CsvRecord;
import dtalge.io.internal.CsvWriter;
import dtalge.util.Strings;

/**
 * 複式記述簡易データ伝票CSVの項目定義行の内容を保持するクラス。
 * 
 * @version 0.2.0
 * @since 0.2.0
 * 
 * @author H.Deguchi (Chiba University of Commerce)
 * @author Y.Ozaki (Mitzba Denyosha CO.,LTD.)
 * @author Y.Ishizuka(PieCake,Inc.)
 */
public class DtDoubleEntrySimpleSlipsCsvHeader 
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	/** 複式記述部の借方の科目名の列インデックス **/
	static public final int CSV_COLIDX_DEBIT_NAME = 0;
	/** 複式記述部の借方の値の列インデックス **/
	static public final int CSV_COLIDX_DEBIT_VALUE = 1;
	/** 複式記述部の借方の単位名の列インデックス **/
	static public final int CSV_COLIDX_DEBIT_UNIT = 2;
	/** 複式記述部の貸方の科目名の列インデックス **/
	static public final int CSV_COLIDX_CREDIT_NAME = 3;
	/** 複式記述部の貸方の値の列インデックス **/
	static public final int CSV_COLIDX_CREDIT_VALUE = 4;
	/** 複式記述部の貸方の単位名の列インデックス **/
	static public final int CSV_COLIDX_CREDIT_UNIT = 5;
	/** ノート部の開始位置となる列インデックス **/
	static public final int CSV_COLIDX_NOTEITEM_BEGIN = 6;

	/** 複式記述部の列ヘッダーのデフォルト値 **/
	static public final String[] DefaultDebitCreditPartHeader = {
			"Dr.name", "Dr.value", "Dr.unit",
			"Cr.name", "Cr.value", "Cr.unit",
	};

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	/**
	 * 複式記述部の項目名を保持する配列。
	 * CSV ヘッダー出力時に、この内容が優先される。
	 * 未定義のフィールドは <code>null</code>
	 */
	protected String[]	_doublentryItemNames = null;
	
	/**
	 * ノート部の列ヘッダーに記述される項目名の配列。
	 * CSV 列ヘッダーの順序で格納される。
	 */
	protected List<String>	_noteItemNames;

	/**
	 *  複式記述簡易データ伝票CSVにおけるノート部のヘッダー情報を保持する、名前とノート部内インデックスのマップ。
	 *  インデックス番号順の順序を保持する。
	 **/
	protected Map<String, Integer> _noteItemIndexMap;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public DtDoubleEntrySimpleSlipsCsvHeader() {
		_noteItemNames = new ArrayList<>();
		_noteItemIndexMap = new LinkedHashMap<>();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * <em>noteItemIndex</em> をフィールドインデックスに変換する。
	 * @param noteItemIndex	変換するノート項目インデックス(0～)
	 * @return	変換結果のインデックス、<em>noteItemIndex</em> が負の値の場合は (-1)
	 */
	public int noteItemIndexToCsvFieldIndex(int noteItemIndex) {
		return (noteItemIndex < 0 ? -1 : (noteItemIndex + CSV_COLIDX_NOTEITEM_BEGIN));
	}

	/**
	 * <em>fieldIndex</em> をノート項目インデックスに変換する。
	 * @param fieldIndex	変換するフィールドインデックス
	 * @return	変換結果のインデックス、<em>fieldIndex</em> がノート項目インデックスの先頭より小さい場合は (-1)
	 */
	public int csvFieldIndexToNoteItemIndex(int fieldIndex) {
		if (fieldIndex < CSV_COLIDX_NOTEITEM_BEGIN) {
			return (-1);
		}
		else {
			return (fieldIndex - CSV_COLIDX_NOTEITEM_BEGIN);
		}
	}
	
	/**
	 * 複式記述部の項目名をすべて消去する。
	 */
	public void clearDoubleEntryItems() {
		_doublentryItemNames = null;
	}

	/**
	 * ノート部の項目名をすべて消去する。
	 */
	public void clearNoteItems() {
		_noteItemNames.clear();
		_noteItemIndexMap.clear();
	}

	/**
	 * ノート部の項目が空かどうかを判定する。
	 * @return	ノート部の項目が存在しない場合は true、それ以外の場合は false
	 */
	public boolean isNoteItemEmpty() {
		return _noteItemNames.isEmpty();
	}
	
	/**
	 * 重複する項目名も含めた、ノート部の項目名の数を取得する。
	 * @return	重複を含むノート部項目名数
	 */
	public int getNumNoteItems() {
		return _noteItemNames.size();
	}
	
	/**
	 * 指定された項目名がノート部に存在するかどうかを判定する。
	 * @param noteItemName	判定する項目名
	 * @return	存在する場合は true、それ以外の場合は false
	 */
	public boolean containsNoteItem(Object noteItemName) {
		return _noteItemIndexMap.containsKey(noteItemName);
	}
	
	/**
	 * 指定されたインデックスに対応する、複式記述部の項目名を返す。
	 * インデックスに対応する項目名が指定されていない場合は、デフォルトの項目名を返す。
	 * @param index	複式記述部のインデックス (0 &lt;= index &lt;= 5)
	 * @return	複式記述部の項目名
	 * @throws IndexOutOfBoundsException	インデックスが範囲外の場合
	 */
	public String getAvailableDoubleEntryItemName(int index) {
		Objects.checkIndex(index, CSV_COLIDX_NOTEITEM_BEGIN);
		
		// specified value
		if (_doublentryItemNames != null) {
			String strValue = _doublentryItemNames[index];
			if (!Strings.isNullOrEmpty(strValue)) {
				// 有効な文字列が設定されている場合のみ、返却
				return strValue;
			}
		}
		
		// default value
		return DefaultDebitCreditPartHeader[index];
	}

	/**
	 * 指定された項目名に対応する、ノート部の項目インデックスを取得する。
	 * @param name	検索する項目名
	 * @return	項目名に対応するインデックスが設定されている場合はその値、それ以外の場合は (-1)
	 */
	public int getNoteItemIndexByName(String name) {
		Integer index = _noteItemIndexMap.get(name);
		return (index != null ? index : -1);
	}
	
	/**
	 * 指定されたCSVフィールドインデックスに対応する項目名を取得する。
	 * このメソッドでは、<code>(index - {@link #CSV_COLIDX_NOTEITEM_BEGIN})</code> によりノート部の項目インデックスに変換する。
	 * @param index	CSVフィールドインデックス
	 * @return	インデックスに対応する項目名、インデックスが範囲外の場合は <code>null</code>
	 */
	public String getNoteItemNameByCsvFieldIndex(int index) {
		return getNoteItemNameByNoteItemIndex(index - CSV_COLIDX_NOTEITEM_BEGIN);
	}
	
	/**
	 * 指定されたノート部の項目インデックスに対応する項目名を取得する。
	 * @param index	ノート部の項目インデックス(0～)
	 * @return	インデックスに対応する項目名、インデックスが範囲外の場合は <code>null</code>
	 */
	public String getNoteItemNameByNoteItemIndex(int index) {
		if (index >= 0 && index < _noteItemNames.size()) {
			return _noteItemNames.get(index);
		}
		else {
			return null;
		}
	}

	/**
	 * 重複を許可せずに、ノート部の項目名を追加する。
	 * <em>noteItemName</em> に指定された項目名がすでに存在する場合は、追加されない。
	 * @param noteItemName	追加する項目名
	 * @return	新たに追加された場合は true、それ以外の場合は false
	 * @throws NullPointerException	<em>noteItemNames</em> が <code>null</code> の場合
	 */
	public boolean addNoteItemWithoutMultiple(String noteItemName) {
		if (noteItemName == null)
			throw new NullPointerException();
		
		if (!_noteItemIndexMap.containsKey(noteItemName)) {
			// 項目名配列の終端に追加したときのインデックスをマップに格納
			_noteItemIndexMap.put(noteItemName, _noteItemNames.size());
			// 項目名配列の終端に追加
			_noteItemNames.add(noteItemName);
			return true;
		}
		else {
			// すでに存在する項目名は追加しない
			return false;
		}
	}

	/**
	 * 重複を許可して、ノート部の項目名を追加する。
	 * <em>noteItemName</em> に指定された項目名がすでに存在する場合、
	 * その項目名のインデックスはすでに存在する項目名のインデックスと同じとなる。
	 * @param noteItemName	追加する項目名
	 * @return	新たに追加された場合は true、それ以外の場合は false
	 * @throws NullPointerException	<em>noteItemNames</em> が <code>null</code> の場合
	 */
	public boolean addNoteItemAllowMultiple(String noteItemName) {
		if (noteItemName == null)
			throw new NullPointerException();
		
		boolean addedIndex = false;
		if (!_noteItemIndexMap.containsKey(noteItemName)) {
			// 項目名配列の終端に追加したときのインデックスをマップに格納
			_noteItemIndexMap.put(noteItemName, _noteItemNames.size());
			addedIndex = true;
		}
		// 項目名配列の終端に追加(重複を許可)
		_noteItemNames.add(noteItemName);
		return addedIndex;
	}
	
	/**
	 * データスリップのノートのすべての項目名を、このオブジェクトのノート部に追加する。
	 * データスリップのノートの項目名は、データ代数の名前キーを対象とする。
	 * なお、このメソッドは、すでに保持されている項目名を破棄しない。
	 * @param slip	データスリップ
	 * @throws NullPointerException	<em>slip</em> が <code>null</code> の場合
	 */
	public void appendAllNoteItemNames(DtSlip slip) {
		Dtalge note = slip.getNote();
		if (note != null && !note.isEmpty()) {
			DtBaseSet bases = note.getBases();
			for (DtBase base : bases) {
				String nameKey = base.getNameKey();
				addNoteItemWithoutMultiple(nameKey);
			}
		}
	}
	
	/**
	 * データスリップリストに含まれるすべてのデータスリップと対象として、データスリップのノートのすべての項目名を、このオブジェクトのノート部に追加する。
	 * データスリップのノートの項目名は、データ代数の名前キーを対象とする。
	 * なお、このメソッドは、すでに保持されている項目名を破棄しない。
	 * @param sliplist	データスリップリスト
	 * @throws NullPointerException	<em>sliplist</em> が <code>null</code> の場合
	 */
	public void appendAllNoteItemNames(DtSlipList sliplist) {
		for (DtSlip slip : sliplist) {
			appendAllNoteItemNames(slip);
		}
	}

	/**
	 * データバインダーに含まれるすべてのデータスリップとデータスリップリストを対象として、データスリップのノートのすべての項目名を、このオブジェクトのノート部に追加する。
	 * データスリップのノートの項目名は、データ代数の名前キーを対象とする。
	 * なお、このメソッドは、すでに保持されている項目名を破棄しない。
	 * @param binder	データバインダー
	 * @throws NullPointerException	<em>binder</em> が <code>null</code> の場合
	 */
	public void appendAllNoteItemNamesFromBinder(DtBinder binder) {
		Map<String,Object> slipmap = binder.getUnmodifiableObjects();
		for (Object objSlip : slipmap.values()) {
			if (objSlip instanceof DtSlipList) {
				appendAllNoteItemNames((DtSlipList)objSlip);
			}
			else if (objSlip instanceof DtSlip) {
				appendAllNoteItemNames((DtSlip)objSlip);
			}
		}
	}

	//------------------------------------------------------------
	// CSV I/O
	//------------------------------------------------------------
	
	/**
	 * 指定された CSV レコードの内容を列ヘッダーとして読み込み、このオブジェクトの内容をリセットする。
	 * 項目名が重複している場合は、{@link IOException} をスローする。
	 * @param csvrec	CSV レコードオブジェクト
	 * @param transformInvalidBaseKeyChars	ノート部の項目名利用時に基底キー使用禁止文字を変換する場合は true、エラーとする場合は false
	 * @throws NullPointerException	<em>csvrec</em> が <code>null</code> の場合
	 * @throws DtDoubleEntrySimpleSlipsCsvFormatError	項目名が重複している場合、
	 * 						<em>transformInvalidBaseKeyChars</em> に true が指定され基底キー使用禁止文字が項目名に含まれていた場合
	 */
	public void readHeaderFromCsvRecord(CsvRecord csvrec, boolean transformInvalidBaseKeyChars)
		throws DtDoubleEntrySimpleSlipsCsvFormatError
	{
		// クリア
		clearDoubleEntryItems();
		clearNoteItems();
		
		// フィールド数の取得
		int numFields = csvrec.getNumFields();
		if (numFields <= 0) {
			// 複式記述部が存在しないので終了
			return;
		}
		
		// 複式記述部
		boolean hasDoubleEntryItems = false;
		String strDoubleEntryItems[] = new String[CSV_COLIDX_NOTEITEM_BEGIN];
		for (int i = 0; i < CSV_COLIDX_NOTEITEM_BEGIN; i++) {
			String strValue = csvrec.getValue(i);
			if (!Strings.isNullOrEmpty(strValue)) {
				// 有効な文字列が指定されていれば、保存
				hasDoubleEntryItems = true;
				strDoubleEntryItems[i] = strValue;
			}
			else {
				// 有効な文字列ではない場合は null
				strDoubleEntryItems[i] = null;
			}
		}
		//--- 保存
		if (hasDoubleEntryItems) {
			_doublentryItemNames = strDoubleEntryItems;
		}
		
		// ノート部
		for (int i = CSV_COLIDX_NOTEITEM_BEGIN; i < numFields; i++) {
			CsvField csvfield = csvrec.getField(i);
			if (csvfield == null) {
				// 項目名が存在しないなら、スキップ
				// ここではエラーにしない
				continue;
			}
			
			String strValue = csvfield.getValue();
			if (Strings.isNullOrEmpty(strValue)) {
				// 有効な文字列が指定されていなければ、空文字列としておく
				strValue = "";
				// 空白は重複を許可して追加
				addNoteItemAllowMultiple(strValue);
			}
			else {
				// 有効な文字列
				//--- 使用禁止文字の変換を行わない場合に、使用禁止文字が含まれていればエラー
				if (!transformInvalidBaseKeyChars && BaseKeyTransformer.containsInvalidCharacters(strValue)) {
					String errmsg = "Item name includes invalid character as Base key: " + strValue;
					throw new DtDoubleEntrySimpleSlipsCsvFormatError(errmsg, csvrec.getLineNo(), csvfield.getIndex()+1);
				}
				//--- 重複を許可せずに追加
				if (!addNoteItemWithoutMultiple(strValue)) {
					// 重複は読み込みエラー
					throw new DtDoubleEntrySimpleSlipsCsvFormatError("Item name in note part already exists: " + strValue, csvrec.getLineNo(), csvfield.getIndex()+1);
				}
			}
		}
	}
	
	/**
	 * このオブジェクトの内容をCSVレコードとして、指定された <em>csvwriter</em> に出力する。
	 * @param csvwriter	CSVライターオブジェクト
	 * @param transformInvalidBaseKeyChars	ノート部の項目名出力時に基底キー使用禁止文字から変換された文字列を元に戻して出力する場合は true、そうでない場合は false
	 * @throws IOException	入出力エラーが発生した場合
	 */
	public void writeCsvHeader(CsvWriter csvwriter, boolean transformInvalidBaseKeyChars)
		throws IOException
	{
		// 複式記述部
		for (int i = 0; i < CSV_COLIDX_NOTEITEM_BEGIN; i++) {
			csvwriter.writeField(getAvailableDoubleEntryItemName(i));
		}
		
		// ノート部
		for (String strName : _noteItemNames) {
			if (transformInvalidBaseKeyChars) {
				// 使用禁止文字から変換されたパターンを元に戻す
				strName = BaseKeyTransformer.resumeInvalidCharacters(strName);
			}
			csvwriter.writeField(strName);
		}
		
		// レコード終端
		csvwriter.newLine();
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
