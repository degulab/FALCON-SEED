package dtalge.container.util.internal.csv;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dtalge.DtBase;
import dtalge.DtBaseSet;
import dtalge.Dtalge;
import dtalge.container.DtSlip;
import dtalge.container.util.DebitCreditItem;
import dtalge.container.util.DebitCreditItemDefinitionTable;
import dtalge.container.util.DtDebitCreditValuePair;
import exalge2.ExBase;
import exalge2.ExBaseSet;
import exalge2.Exalge;

/**
 * 複式記述簡易データ伝票CSVのレコード構築に利用されるバッファ。
 * <p>
 * 複式記述簡易データ伝票CSVのヘッダ情報と貸借科目定義テーブルを保持し、ノート部と複式記述部の出力内容を整形する。
 * 
 * @version 0.2.0
 * @since 0.2.0
 * 
 * @author H.Deguchi (Chiba University of Commerce)
 * @author Y.Ozaki (Mitzba Denyosha CO.,LTD.)
 * @author Y.Ishizuka(PieCake,Inc.)
 */
public class DtDoubleEntrySimpleSlipsCsvRecordBuilder
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	/**
	 * CSV のヘッダー情報
	 */
	protected final DtDoubleEntrySimpleSlipsCsvHeader	_csvHeaderInfo;
	
	/**
	 * 借方・貸方の科目名を規定するテーブル
	 */
	protected final DebitCreditItemDefinitionTable		_sideDefTable;
	
	/**
	 * CSV 行のノート部に出力する内容
	 */
	protected ArrayList<String>	_noteItems;
	
	/**
	 * 交換代数基底と値の組を要素とする、借方と貸方のペアを保持するオブジェクトのリスト
	 */
	protected ArrayList<DtDebitCreditValuePair>	_doubleEntries;
	
	/**
	 * 借方・貸方の科目名を規定するテーブルに存在しない名前キーを持つ交換代数基底を保持するリスト
	 */
	protected ArrayList<ExBase>	_unknownSideBases;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * 指定されたパラメータで、このオブジェクトのインスタンスを生成する。
	 * @param csvheader	出力対象とするすべてのデータスリップから構成されたノート部の情報を保持している {@link DtDoubleEntrySimpleSlipsCsvHeader} オブジェクト
	 * @param table		借方・貸方の科目名を規定するテーブル
	 * @throws NullPointerException	引数のいずれかが <code>null</code> の場合
	 */
	public DtDoubleEntrySimpleSlipsCsvRecordBuilder(DtDoubleEntrySimpleSlipsCsvHeader csvheader, DebitCreditItemDefinitionTable table)
	{
		if (csvheader == null || table == null)
			throw new NullPointerException();
		
		_csvHeaderInfo = csvheader;
		_sideDefTable  = table;
		// ノート部の情報を初期化(すべて空文字)
		_noteItems = new ArrayList<>(csvheader.getNumNoteItems());
		for (int i = 0; i < csvheader.getNumNoteItems(); i++) {
			_noteItems.add("");
		}
		// その他の要素の初期化
		_doubleEntries = new ArrayList<>();
		_unknownSideBases = new ArrayList<>();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public DtDoubleEntrySimpleSlipsCsvHeader csvHeaderInfo() {
		return _csvHeaderInfo;
	}
	
	public DebitCreditItemDefinitionTable sideDefTable() {
		return _sideDefTable;
	}
	
	public void clearNoteItems() {
		Collections.fill(_noteItems, "");
	}
	
	public void clearDoubleEntries() {
		_doubleEntries.clear();
	}
	
	public void clearUnknownSideBases() {
		_unknownSideBases.clear();
	}
	
	public boolean isEmpty() {
		return (isNoteItemEmpty() && isDoubleEntryEmpty() && isUnknownSideBaseEmpty());
	}
	
	public boolean isNoteItemEmpty() {
		return _noteItems.isEmpty();
	}
	
	public boolean isDoubleEntryEmpty() {
		return _doubleEntries.isEmpty();
	}
	
	public boolean isUnknownSideBaseEmpty() {
		return _unknownSideBases.isEmpty();
	}
	
	public int getNumNoteItems() {
		return _noteItems.size();
	}
	
	public int getNumDoubleEntries() {
		return _doubleEntries.size();
	}
	
	public int getNumUnknownSideBases() {
		return _unknownSideBases.size();
	}
	
	public List<ExBase> getUnknownSideBases() {
		return _unknownSideBases;
	}
	
	public List<String> getNoteItemValues() {
		return _noteItems;
	}
	
	public List<DtDebitCreditValuePair>	getDoubleEntries() {
		return _doubleEntries;
	}
	
	public boolean refreshSlipNoteEntries(DtSlip slip) {
		// ノート出力内容のクリア
		clearNoteItems();
		if (slip.isNoteEmpty()) {
			return false;
		}

		// ノート出力内容の設定
		boolean exists = false;
		Dtalge note = slip.getNote();
		DtBaseSet bases = note.getBases();
		for (DtBase base : bases) {
			// 出力する値を取得
			Object value = note.get(base);
			String strValue = "";
			if (value != null) {
				if (value instanceof BigDecimal) {
					strValue = ((BigDecimal)value).stripTrailingZeros().toPlainString();
				} else {
					strValue = value.toString();
				}
			}

			// 出力位置を取得
			int noteItemIndex = _csvHeaderInfo.getNoteItemIndexByName(base.getNameKey());
			if (noteItemIndex < 0) {
				throw new IllegalStateException("Note item name does not exist in CSV header: (Note item name) " + base.getNameKey());
			}
			_noteItems.set(noteItemIndex, strValue);
			exists = true;
		}
		return exists;
	}

	/**
	 * 指定された交換代数元の要素を、<em>table</em> の内容に従い借方・貸方の要素に振り分け、借方・貸方の要素のペアとして追加する。
	 * 借方・貸方の振り分けは交換代数基底の名前キーのみで行い、交換代数元の基底の順序で追加される。
	 * このとき、借方と貸方のペアにならない場合は、いずれか一方のみを保持するデータとして追加する。
	 * また、借方・貸方科目定義テーブルで規定されていない交換代数元は追加されず、
	 * 借方・貸方の科目名を規定するテーブルに存在しない名前キーを持つ交換代数基底を保持するリストに追加される。
	 * @param alge	交換代数元
	 * @return	借方・貸方のペアのみが追加された場合は true、それ以外の場合は false を返す。
	 * 			要素が一つも追加されなかった場合も false を返す。
	 * @throws NullPointerException	<em>alge</em> が <code>null</code> の場合
	 */
	public boolean appendDoubleEntries(Exalge alge) {
		if (alge.isEmpty())
			return false;
		
		ExBaseSet bases = alge.getBases();
		DtDebitCreditValuePair pair = null;
		int numPairs = 0;
		int numOneSides = 0;
		for (ExBase base : bases) {
			DebitCreditItem defItem = _sideDefTable.get(base.getNameKey());
			if (defItem == null) {
				// 貸借科目定義テーブルに存在しない
				_unknownSideBases.add(base);
			}
			else if (defItem.isDebitSide() || defItem.isCreditSide()) {
				// 借方科目 or 貸方科目
				boolean debitSide;
				if (defItem.isDebitSide()) {
					// 借方科目
					debitSide = base.isNoHat();	// ハットなしなら借方(+)、ハット付きなら貸方(-)
				}
				else {
					// 貸方科目
					debitSide = base.isHat();	// ハット付なら借方(-)、ハットなしなら貸方(+)
				}
				if (pair != null) {
					if (!pair.hasItemBySide(debitSide)) {
						// ペア成立
						pair.setItemBySide(debitSide, alge.get(base), base);
						_doubleEntries.add(pair);
						++numPairs;
						pair = null;
					}
					else {
						// ペア不成立
						_doubleEntries.add(pair);
						++numOneSides;
						pair = new DtDebitCreditValuePair();
						pair.setItemBySide(debitSide, alge.get(base), base);
					}
				}
				else {
					// 新規作成
					pair = new DtDebitCreditValuePair();
					pair.setItemBySide(debitSide, alge.get(base), base);
				}
			}
			else {
				// unknown
				_unknownSideBases.add(base);
			}
		}
		if (pair != null) {
			// 最終の要素を出力
			if (pair.hasBothItems()) {
				// ペア成立
				_doubleEntries.add(pair);
				++numPairs;
			}
			else {
				// ペア不成立
				_doubleEntries.add(pair);
				++numOneSides;
			}
		}
		
		return (numPairs > 0 && numOneSides == 0);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
