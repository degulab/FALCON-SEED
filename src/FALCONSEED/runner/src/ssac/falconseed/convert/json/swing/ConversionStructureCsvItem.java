/*
 * @(#)ConversionStructureCsvItem.java	3.4.0	2020/03/14
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.convert.json.swing;

import java.util.ArrayList;

/**
 * JSON-CSV 変換のための、CSV 変換構造アイテム・モデル。
 * 
 * @version 3.4.0
 * @since 3.4.0
 */
public class ConversionStructureCsvItem extends AbConversionStructureItem
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	/** フィールド(列)のヘッダー行を除くデータ行数 **/
	protected long			_numDataRecords;
	/** ヘッダー行順に格納されたフィールド文字列、ヘッダー行が存在しない場合は空のリスト **/
	protected ArrayList<String>	_aryHeaderNames;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public ConversionStructureCsvItem() {
		_numDataRecords = 0L;
		_aryHeaderNames = new ArrayList<String>();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	/**
	 * 現在のアイテムの内容で、表示用の値を更新する。
	 */
	public void refreshDisplayValues() {
		super.refreshDisplayValues();
		setDefaultName(concatNames(_aryHeaderNames));
	}
	
	public long getDataRecordCount() {
		return _numDataRecords;
	}
	
	public boolean setDataRecordCount(long value) {
		if (value != _numDataRecords) {
			_numDataRecords = value;
			return true;
		}
		else {
			return false;
		}
	}
	
	public long incrementDataRecordCount() {
		++_numDataRecords;
		return _numDataRecords;
	}
	
	public boolean isEmptyHeaderNames() {
		return _aryHeaderNames.isEmpty();
	}
	
	public int getHeaderNameCount() {
		return _aryHeaderNames.size();
	}
	
	public String[] getHeaderNames() {
		return _aryHeaderNames.toArray(new String[_aryHeaderNames.size()]);
	}
	
	public String getHeaderName(int index) {
		return _aryHeaderNames.get(index);
	}
	
	public boolean clearHeaderNames() {
		if (!_aryHeaderNames.isEmpty()) {
			_aryHeaderNames.clear();
			return true;
		}
		else {
			return false;
		}
	}
	
	public boolean removeHeaderName(int index) {
		if (index >= 0 && index < _aryHeaderNames.size()) {
			_aryHeaderNames.remove(index);
			return true;
		}
		else {
			return false;
		}
	}
	
	public void addHeaderName(String name) {
		_aryHeaderNames.add(name);
	}
	
	public void setHeaderName(int index, String name) {
		_aryHeaderNames.set(index, name);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
