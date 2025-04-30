/*
 * @(#)DtBinder.java	0.1.0	2022/07/27
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge.container;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import dtalge.Dtalge;

/**
 * データバインダー・クラス。
 * <p>
 * このオブジェクト固有の付加的説明情報を保持するノート(データ代数)と、
 * 複数の名前付きスリップオブジェクトから構成される。なお、名前付きスリップオブジェクトに <code>null</code> は許容しない。<br>
 * 名前付きスリップオブジェクトとして格納できるのは、次のスリップオブジェクトのみである。
 * <ul>
 * 	<li>データスリップ <code>{@link DtSlip}</code></li>
 * 	<li>データスリップ集合(リスト) <code>{@link DtSlip}</code></li>
 * </ul>
 * <p>
 * {@code JSON} 形式でのシリアライズ／デシリアライズについては、{@link dtalge.json.DtJSON} クラスの説明を参照。
 * データスリップの {@code JSON} 形式については、{@link dtalge.json.serialize.DtJsonDtBinderSerializer} の説明を参照。
 * 
 * @version 0.1.0
 * @since 0.1.0
 * 
 * @author H.Deguchi (Chiba University of Commerce)
 * @author Y.Ozaki (Mitzba Denyosha CO.,LTD.)
 * @author Y.Ishizuka(PieCake,Inc.)
 * 
 * @see dtalge.json.DtJSON
 * @see dtalge.json.serialize.DtJsonDtBinderSerializer
 */
public class DtBinder
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	/**
	 * データバインダーの名前付きスリップオブジェクトとして格納可能なクラスの配列。
	 */
	static public final Class<?> SupportedClasses[] = {
			DtSlip.class,
			DtSlipList.class,
	};

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	/** データバインダーのノートとなる、データ代数元インスタンス **/
	protected Dtalge				_note;
	/** データバインダーの名前付きスリップオブジェクトを格納するマップインスタンス **/
	protected Map<String, Object>	_slips;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	/**
	 * 要素が空の、新しいインスタンスを生成する。
	 */
	public DtBinder() {
	}
	
	/**
	 * 指定されたデータバインダーの要素を持つ、新しいインスタンスを生成する。
	 * <p><b>注意：</b>
	 * <blockquote>
	 * このコンストラクタでは、名前付きスリップオブジェクトを保持するマップのみが新しいインスタンスで構成されるのみで、
	 * それ以外の要素は浅いコピーとなる。
	 * </blockquote>
	 * @param src	コピー元のデータバインダー、(途中)の場合は要素が空のソースと見なす
	 */
	public DtBinder(final DtBinder src) {
		if (src != null) {
			this._note = src._note;
			if (!src.isObjectEmpty()) {
				this._slips = new LinkedHashMap<>(src._slips);
			}
		}
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	/**
	 * 指定されたクラスが、データバインダーの名前付きスリップオブジェクトとしてサポートされているかを判定する。
	 * @param clazz	判定するクラス
	 * @return	サポートされていれば <code>true</code>、それ以外の場合は <code>false</code>
	 */
	static public boolean isObjectTypeSupported(Class<?> clazz) {
		for (Class<?> supported : SupportedClasses) {
			if (supported.equals(clazz)) {
				return true;	// supported
			}
		}
		return false;	// not supported
	}
	
	/**
	 * 指定されたオブジェクトが、、データバインダーの名前付きスリップオブジェクトとしてサポートされているかを判定する。
	 * @param obj	判定するオブジェクト
	 * @return	サポートされていれば <code>true</code>、それ以外の場合は <code>false</code>
	 */
	static public boolean isInstanceSupported(Object obj) {
		if (obj != null) {
			return isObjectTypeSupported(obj.getClass());
		}
		else {
			return false;	// null is not supported
		}
	}
	
	/**
	 * ノートと名前付きスリップオブジェクトの両要素が空であることを判定する。
	 * @return	ノートと名前付きスリップオブジェクトのどらも空であれば <code>true</code> を返す。
	 */
	public boolean isEmpty() {
		return (isNoteEmpty() && isObjectEmpty());
	}
	
	/**
	 * ノートの要素が空であることを判定する。
	 * @return	ノートが空であれば <code>true</code> を返す。
	 */
	public boolean isNoteEmpty() {
		return (_note==null || _note.isEmpty());
	}
	
	/**
	 * 名前付きスリップオブジェクトが存在しないことを判定する。
	 * @return	名前付きスリップオブジェクトが一つも存在しない場合は <code>true</code> を返す。
	 */
	public boolean isObjectEmpty() {
		return (_slips==null || _slips.isEmpty());
	}

	/**
	 * ノートと名前付きスリップオブジェクトを、すべて破棄する。
	 */
	public void clear() {
		clearNote();
		clearObjects();
	}
	
	/**
	 * ノートの要素をすべて破棄する。
	 */
	public void clearNote() {
		_note = null;
	}
	
	/**
	 * 名前付きスリップオブジェクトをすべて破棄する。
	 */
	public void clearObjects() {
		if (_slips != null) {
			_slips.clear();
		}
	}

	/**
	 * このオブジェクトが保持するスリップオブジェクト数を取得する。
	 * @return	スリップオブジェクト数
	 */
	public int getObjectCount() {
		return (_slips==null ? 0 : _slips.size());
	}
	
	/**
	 * 指定された名前がこのオブジェクトに含まれているかどうかを判定する。
	 * @param name	判定する名前
	 * @return	含まれている場合は <code>true</code>
	 */
	public boolean containsName(String name) {
		return (_slips==null ? false : _slips.containsKey(name));
	}

	/**
	 * 指定されたスリップオブジェクトがこのオブジェクトに含まれているかどうかを判定する。
	 * @param value	判定するオブジェクト
	 * @return	含まれている場合は <code>true</code>
	 */
	public boolean containsObject(Object value) {
		return (_slips==null ? false : _slips.containsValue(value));
	}
	
	/**
	 * このオブジェクトに含まれる名前とスリップオブジェクトの変更不可能なマップを取得する。
	 * @return	変更不可能なマップオブジェクト
	 */
	public Map<String,Object> getUnmodifiableObjects() {
		if (_slips == null) {
			return Collections.emptyMap();
		}
		else {
			return Collections.unmodifiableMap(_slips);
		}
	}

	/**
	 * ノートを取得する。
	 * @return	このオブジェクトが保持するノートオブジェクト
	 */
	public Dtalge getNote() {
		return ensureNote();
	}

	/**
	 * ノートを設定する。
	 * @param newNote	設定するノートオブジェクト
	 */
	public void setNote(Dtalge newNote) {
		_note = newNote;
	}

	/**
	 * 指定された名前に対応するスリップオブジェクトを取得する。
	 * @param name	名前
	 * @return	名前に対応するスリップオブジェクト、存在しない場合は <code>null</code>
	 */
	public Object getObject(String name) {
		return (_slips==null ? null : _slips.get(name));
	}

	/**
	 * 指定された名前に対応するスリップオブジェクトのクラスを取得する。
	 * @param name	名前
	 * @return	名前に対応するスリップオブジェクトのクラス、存在しない場合は <code>null</code>
	 */
	public Class<?> getObjectType(String name) {
		Object obj = getObject(name);
		return (obj==null ? null : obj.getClass());
	}

	/**
	 * 指定された名前でスリップオブジェクトを登録する。
	 * すでに同名のスリップオブジェクトが登録されている場合は、指定されたスリップオブジェクトに置き替える。
	 * @param name	名前
	 * @param newObj	登録するスリップオブジェクト
	 * @return	新規に登録された場合は <code>null</code>、すでに登録済みの場合は以前のスリップオブジェクト
	 * @throws IllegalArgumentException	<em>newObj</em> がスリップオブジェクトではない場合
	 */
	public Object putObject(String name, Object newObj) {
		if (newObj == null) {
			return ensureObjectMap().put(name, null);
		}
		else if (isObjectTypeSupported(newObj.getClass())) {
			return ensureObjectMap().put(name, newObj);
		}
		else {
			throw new IllegalArgumentException("Unsupported object type for DtSlip : " + newObj.getClass().getName());
		}
	}

	/**
	 * 指定された名前に対応するスリップオブジェクトを、データスリップ({@link DtSlip})として取得する。
	 * @param name	名前
	 * @return	名前に対応するデータスリップ、存在しない場合は <code>null</code>
	 * @throws ClassCastException	名前に対応するスリップオブジェクトがデータスリップ({@link DtSlip})ではない場合
	 */
	public DtSlip getDtSlipObject(String name) {
		return (DtSlip)getObject(name);
	}

	/**
	 * 指定された名前で、データスリップ({@link DtSlip})を登録する。
	 * @param name	名前
	 * @param newSlip	登録するデータスリップ({@link DtSlip})
	 * @return	新規に登録された場合は <code>null</code>、すでに登録済みの場合は以前のスリップオブジェクト
	 */
	public Object putDtSlipObject(String name, DtSlip newSlip) {
		return putObject(name, newSlip);
	}
	
	/**
	 * 指定された名前に対応するスリップオブジェクトを、データスリップ集合({@link DtSlipList})として取得する。
	 * @param name	名前
	 * @return	名前に対応するデータスリップ、存在しない場合は <code>null</code>
	 * @throws ClassCastException	名前に対応するスリップオブジェクトがデータスリップ集合({@link DtSlipList})ではない場合
	 */
	public DtSlipList getDtSlipListObject(String name) {
		return (DtSlipList)getObject(name);
	}
	
	/**
	 * 指定された名前で、データスリップ集合({@link DtSlipList})を登録する。
	 * @param name	名前
	 * @param newSlipList	登録するデータスリップ集合({@link DtSlipList})
	 * @return	新規に登録された場合は <code>null</code>、すでに登録済みの場合は以前のスリップオブジェクト
	 */
	public Object putDtSlipListObject(String name, DtSlipList newSlipList) {
		return putObject(name, newSlipList);
	}

	/**
	 * 指定された名前と対応するスリップオブジェクトを、削除する。
	 * @param name	名前
	 * @return	削除された場合はそのスリップオブジェクト、そうでない場合は <code>null</code>
	 */
	public Object removeObject(String name) {
		if (_slips != null) {
			return _slips.remove(name);
		} else {
			return null;
		}
	}
	
	//------------------------------------------------------------
	// Implement Map interfaces
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Overrides
	//------------------------------------------------------------

	@Override
	public int hashCode() {
		int h = 1;
		h = 31 * h + (_note==null ? 0 : _note.hashCode());
		h = 31 * h + (_slips==null ? 0 : _slips.hashCode());
		return h;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj == null) return false;
		if (this.getClass() != obj.getClass())
			return false;

		DtBinder abinder = (DtBinder)obj;
		return (equalsNote(abinder._note) && equalsObjects(abinder._slips));
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	/**
	 * ノート部のデータ代数元が <code>null</code> の場合に、要素が空のデータ代数元を返す。
	 * @return	データ代数元のインスタンス
	 */
	protected Dtalge ensureNote() {
		if (_note == null) {
			_note = new Dtalge();
		}
		return _note;
	}
	
	protected Map<String,Object> ensureObjectMap() {
		if (_slips == null) {
			_slips = new LinkedHashMap<>();
		}
		return _slips;
	}
	
	protected boolean equalsNote(Dtalge anote) {
		if (this._note == null) {
			return (anote == null || anote.isEmpty());
		}
		else if (anote == null) {
			return this._note.isEmpty();
		}
		else {
			return this._note.equals(anote);
		}
	}
	
	protected boolean equalsObjects(Map<String, Object> aobjects) {
		if (this._slips == null) {
			return (aobjects == null || aobjects.isEmpty());
		}
		else if (aobjects == null) {
			return this._slips.isEmpty();
		}
		else {
			return this._slips.equals(aobjects);
		}
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
