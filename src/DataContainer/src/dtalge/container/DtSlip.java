/*
 * @(#)DtSlip.java	0.2.0	2025/02/01
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)DtSlip.java	0.1.0	2022/07/14
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge.container;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import dtalge.DtAlgeSet;
import dtalge.Dtalge;
import exalge2.ExAlgeSet;
import exalge2.Exalge;

/**
 * データスリップ・クラス。
 * <p>
 * 伝票単位でのデータ保持を目的とした、データコンテナの基本実装クラス。
 * このクラスにより、交換代数やデータ代数を内包する、JSON フォーマットでのデータ交換が可能となる。
 * <p>
 * データスリップは、このオブジェクト固有の付加的説明情報を保持するノート(データ代数元)と、
 * 複数の名前付きオブジェクトから構成される。なお、名前付きオブジェクトに <code>null</code> は、許容しない。<br>
 * 名前付きオブジェクトとして格納できるのは、次のオブジェクトのみである。
 * <ul>
 * 	<li>交換代数元 {@code (exalge2.Exalge)}</li>
 * 	<li>交換代数集合 {@code (exalge2.ExAlgeSet)} </li>
 * 	<li>データ代数元 {@code (dtalge.Dtalge)}</li>
 * 	<li>データ代数集合 {@code (dtalge.DtAlgeSet)}</li>
 * </ul>
 * <p>
 * {@code JSON} 形式でのシリアライズ／デシリアライズについては、{@link dtalge.json.DtJSON} クラスの説明を参照。
 * データスリップの {@code JSON} 形式については、{@link dtalge.json.serialize.DtJsonDtSlipSerializer} の説明を参照。
 * 
 * @version 0.2.0
 * @since 0.1.0
 *
 * @author H.Deguchi (Chiba University of Commerce)
 * @author Y.Ozaki (Mitzba Denyosha CO.,LTD.)
 * @author Y.Ishizuka(PieCake,Inc.)
 * 
 * @see dtalge.json.DtJSON
 * @see dtalge.json.serialize.DtJsonDtSlipSerializer
 */
public class DtSlip
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	/**
	 * データスリップの名前付きオブジェクトとして格納可能なクラスの配列。
	 */
	static public final Class<?> SupportedClasses[] = {
			Exalge.class,
			ExAlgeSet.class,
			Dtalge.class,
			DtAlgeSet.class,
	};

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	/** データスリップのノートとなる、データ代数元インスタンス **/
	protected Dtalge				_note;
	/** データスリップの名前付きオブジェクトを格納するマップインスタンス **/
	protected Map<String, Object>	_objects;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	/**
	 * 要素が空の、新しいインスタンスを生成する。
	 */
	public DtSlip() {
	}
	
	/**
	 * 指定されたデータスリップの要素を持つ、新しいインスタンスを生成する。
	 * <p><b>注意：</b>
	 * <blockquote>
	 * このコンストラクタでは、名前付きオブジェクトを保持するマップのみが新しいインスタンスで構成されるのみで、
	 * それ以外の要素は浅いコピーとなる。
	 * </blockquote>
	 * @param src	コピー元のデータスリップ、(途中)の場合は要素が空のソースと見なす
	 */
	public DtSlip(final DtSlip src) {
		if (src != null) {
			this._note = src._note;
			if (!src.isObjectEmpty()) {
				this._objects = new LinkedHashMap<>(src._objects);
			}
		}
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	/**
	 * 指定されたクラスが、データスリップの名前付きオブジェクトとしてサポートされているかを判定する。
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
	 * 指定されたオブジェクトが、、データスリップの名前付きオブジェクトとしてサポートされているかを判定する。
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
	 * ノートと名前付きオブジェクトの両要素が空であることを判定する。
	 * @return	ノートと名前付きオブジェクトのどらも空であれば <code>true</code> を返す。
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
	 * 名前付きオブジェクトが存在しないことを判定する。
	 * @return	名前付きオブジェクトが一つも存在しない場合は <code>true</code> を返す。
	 */
	public boolean isObjectEmpty() {
		return (_objects==null || _objects.isEmpty());
	}

	/**
	 * ノートと名前付きオブジェクトを、すべて破棄する。
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
	 * 名前付きオブジェクトをすべて破棄する。
	 */
	public void clearObjects() {
		if (_objects != null) {
			_objects.clear();
		}
	}
	
	/**
	 * このオブジェクトが保持するデータオブジェクト数を取得する。
	 * @return	データオブジェクト数
	 */
	public int getObjectCount() {
		return (_objects==null ? 0 : _objects.size());
	}
	
	/**
	 * 指定された名前がこのオブジェクトに含まれているかどうかを判定する。
	 * @param name	判定する名前
	 * @return	含まれている場合は <code>true</code>
	 */
	public boolean containsName(String name) {
		return (_objects==null ? false : _objects.containsKey(name));
	}
	
	/**
	 * 指定されたデータオブジェクトがこのオブジェクトに含まれているかどうかを判定する。
	 * @param value	判定するオブジェクト
	 * @return	含まれている場合は <code>true</code>
	 */
	public boolean containsObject(Object value) {
		return (_objects==null ? false : _objects.containsValue(value));
	}
	
	/**
	 * このオブジェクトに含まれる名前とデータオブジェクトの変更不可能なマップを取得する。
	 * @return	変更不可能なマップオブジェクト
	 */
	public Map<String,Object> getUnmodifiableObjects() {
		if (_objects == null) {
			return Collections.emptyMap();
		}
		else {
			return Collections.unmodifiableMap(_objects);
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
	 * データスリップに格納されているすべてのオブジェクトの名前の Set を返す。
	 * @return	名前の Set
	 */
	public Set<String> getAllObjectNames() {
		return _objects.keySet();
	}
	
	/**
	 * 指定されたクラスのいずれかに一致する、すべてのオブジェクトの名前の Set を返す。
	 * @param targetClasses	検索するクラスの配列
	 * @return	<em>targetClass</em> もしくはその派生のオブジェクトの名前の Set、
	 * 			該当するものが存在しない場合は空の Set
	 * @throws NullPointerException	引数もしくは引数の配列要素のいずれかが null の場合
	 * @since 0.2.0
	 */
	public Set<String> findObjectNamesByClasses(Class<?>...targetClasses) {
		if (targetClasses.length == 0 || this.isObjectEmpty()) {
			return Collections.emptySet();
		}
		
		LinkedHashSet<String> nameset = new LinkedHashSet<>();
		
		for (Map.Entry<String, Object> entry : _objects.entrySet()) {
			Class<?> objClass = entry.getValue().getClass();
			for (Class<?> reqcls : targetClasses) {
				if (reqcls.isAssignableFrom(objClass)) {
					nameset.add(entry.getKey());
				}
			}
		}
		
		return nameset;
	}
	
	/**
	 * 指定された名前に対応するデータオブジェクトを取得する。
	 * @param name	名前
	 * @return	名前に対応するデータオブジェクト、存在しない場合は <code>null</code>
	 */
	public Object getObject(String name) {
		return (_objects==null ? null : _objects.get(name));
	}
	
	/**
	 * 指定された名前に対応するデータオブジェクトのクラスを取得する。
	 * @param name	名前
	 * @return	名前に対応するデータオブジェクトのクラス、存在しない場合は <code>null</code>
	 */
	public Class<?> getObjectType(String name) {
		Object obj = getObject(name);
		return (obj==null ? null : obj.getClass());
	}
	
	/**
	 * 指定された名前でデータオブジェクトを登録する。
	 * すでに同名のデータオブジェクトが登録されている場合は、指定されたデータオブジェクトに置き替える。
	 * @param name	名前
	 * @param newObj	登録するデータオブジェクト
	 * @return	新規に登録された場合は <code>null</code>、すでに登録済みの場合は以前のデータオブジェクト
	 * @throws IllegalArgumentException	<em>newObj</em> がデータオブジェクトではない場合
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
	 * 指定された名前に対応するデータオブジェクトを、<code>Exalge</code> オブジェクトとして取得する。
	 * @param name	名前
	 * @return	名前に対応する <code>Exalge</code> オブジェクト、存在しない場合は <code>null</code>
	 * @throws ClassCastException	名前に対応するスリップオブジェクトが <code>Exalge</code> オブジェクトではない場合
	 */
	public exalge2.Exalge getExalgeObject(String name) {
		return (exalge2.Exalge)getObject(name);
	}
	
	/**
	 * 指定された名前で、<code>Exalge</code> オブジェクトを登録する。
	 * @param name	名前
	 * @param newAlge	登録する <code>Exalge</code> オブジェクト
	 * @return	新規に登録された場合は <code>null</code>、すでに登録済みの場合は以前のデータオブジェクト
	 */
	public Object putExalgeObject(String name, exalge2.Exalge newAlge) {
		return putObject(name, newAlge);
	}
	
	/**
	 * 指定された名前に対応するデータオブジェクトを、<code>ExAlgeSet</code> オブジェクトとして取得する。
	 * @param name	名前
	 * @return	名前に対応する <code>ExAlgeSet</code> オブジェクト、存在しない場合は <code>null</code>
	 * @throws ClassCastException	名前に対応するスリップオブジェクトが <code>ExAlgeSet</code> オブジェクトではない場合
	 */
	public exalge2.ExAlgeSet getExAlgeSetObject(String name) {
		return (exalge2.ExAlgeSet)getObject(name);
	}
	
	/**
	 * 指定された名前で、<code>ExAlgeSet</code> オブジェクトを登録する。
	 * @param name	名前
	 * @param newAlgeSet	登録する <code>ExAlgeSet</code> オブジェクト
	 * @return	新規に登録された場合は <code>null</code>、すでに登録済みの場合は以前のデータオブジェクト
	 */
	public Object putExAlgeSetObject(String name, exalge2.ExAlgeSet newAlgeSet) {
		return putObject(name, newAlgeSet);
	}
	
	/**
	 * 指定された名前に対応するデータオブジェクトを、<code>Dtalge</code> オブジェクトとして取得する。
	 * @param name	名前
	 * @return	名前に対応する <code>Dtalge</code> オブジェクト、存在しない場合は <code>null</code>
	 * @throws ClassCastException	名前に対応するスリップオブジェクトが <code>Dtalge</code> オブジェクトではない場合
	 */
	public dtalge.Dtalge getDtalgeObject(String name) {
		return (dtalge.Dtalge)getObject(name);
	}
	
	/**
	 * 指定された名前で、<code>Dtalge</code> オブジェクトを登録する。
	 * @param name	名前
	 * @param newAlge	登録する <code>Dtalge</code> オブジェクト
	 * @return	新規に登録された場合は <code>null</code>、すでに登録済みの場合は以前のデータオブジェクト
	 */
	public Object putDtalgeObject(String name, dtalge.Dtalge newAlge) {
		return putObject(name, newAlge);
	}
	
	/**
	 * 指定された名前に対応するデータオブジェクトを、<code>DtAlgeSet</code> オブジェクトとして取得する。
	 * @param name	名前
	 * @return	名前に対応する <code>DtAlgeSet</code> オブジェクト、存在しない場合は <code>null</code>
	 * @throws ClassCastException	名前に対応するスリップオブジェクトが <code>DtAlgeSet</code> オブジェクトではない場合
	 */
	public dtalge.DtAlgeSet getDtAlgeSetObject(String name) {
		return (dtalge.DtAlgeSet)getObject(name);
	}
	
	/**
	 * 指定された名前で、<code>DtAlgeSet</code> オブジェクトを登録する。
	 * @param name	名前
	 * @param newAlgeSet	登録する <code>DtAlgeSet</code> オブジェクト
	 * @return	新規に登録された場合は <code>null</code>、すでに登録済みの場合は以前のデータオブジェクト
	 */
	public Object putDtAlgeSetObject(String name, dtalge.DtAlgeSet newAlgeSet) {
		return putObject(name, newAlgeSet);
	}
	
	/**
	 * 指定された名前と対応するデータオブジェクトを、削除する。
	 * @param name	名前
	 * @return	削除された場合はそのデータオブジェクト、そうでない場合は <code>null</code>
	 */
	public Object removeObject(String name) {
		if (_objects != null) {
			return _objects.remove(name);
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
		h = 31 * h + (_objects==null ? 0 : _objects.hashCode());
		return h;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj == null) return false;
		if (this.getClass() != obj.getClass())
			return false;
		
		DtSlip aslip = (DtSlip)obj;
		return (equalsNote(aslip._note) && equalsObjects(aslip._objects));
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
		if (_objects == null) {
			_objects = new LinkedHashMap<>();
		}
		return _objects;
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
		if (this._objects == null) {
			return (aobjects == null || aobjects.isEmpty());
		}
		else if (aobjects == null) {
			return this._objects.isEmpty();
		}
		else {
			return this._objects.equals(aobjects);
		}
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
