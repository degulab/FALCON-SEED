/*
 * @(#)DtContainerNoteEditTableModel.java	1.0.0	2022/12/08
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge.container.editor.content;

import java.util.Map;

import dtalge.DtAlgeSet;
import dtalge.Dtalge;
import dtalge.container.DtBinder;
import dtalge.container.DtSlip;
import dtalge.container.DtSlipList;
import exalge2.ExAlgeSet;
import exalge2.Exalge;

/**
 * データコンテナの構成要素の種類。
 * {@code Enum} の値は、実際のクラスと重複しないよう、先頭に {@code "Content" } を付加している。
 * 
 * @version 1.0.0
 */
public enum DtContainerContentTypes
{
	//------------------------------------------------------------
	// Types
	//------------------------------------------------------------

	/** データバインダー **/
	ContentDtBinder("DtBinder", DtBinder.class, true),
	/** データバインダー内のノート **/
	ContentDtBinderNote("note (DtBinder)", Dtalge.class, false),
	/** データバインダー内の名前付きスリップのルート(マップ) **/
	ContentDtBinderSlips("slips (DtBinder)", Map.class, true),
	/** データスリップ集合(リスト) **/
	ContentDtSlipList("DtSlipList", DtSlipList.class, true),
	/** データスリップ(単体) **/
	ContentDtSlip("DtSlip", DtSlip.class, true),
	/** データスリップ内のノート **/
	ContentDtSlipNote("note (DtSlip)", Dtalge.class, false),
	/** データスリップ内の名前付きオブジェクトのルート(マップ) **/
	ContentDtSlipObjects("objects (DtSlip)", Map.class, true),
	/** データ代数集合 **/
	ContentDtAlgeSet("DtAlgeSet", DtAlgeSet.class, true),
	/** データ代数元 **/
	ContentDtalge("Dtalge", Dtalge.class, false),
	/** 交換代数集合 **/
	ContentExAlgeSet("ExAlgeSet", ExAlgeSet.class, true),
	/** 交換代数元 **/
	ContentExalge("Exalge", Exalge.class, false);

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	/** コンテンツの名前(表示名) **/
	private String		_contentName;
	/** コンテンツの型を示すクラス **/
	private Class<?>	_contentType;
	/** コンテンツが子の要素を持つかどうかを示すフラグ **/
	private boolean		_allowsChildren;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	private DtContainerContentTypes(String contentName, Class<?> contentType, boolean allowsChildren) {
		this._contentName = contentName;
		this._contentType = contentType;
		this._allowsChildren = allowsChildren;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	/**
	 * データコンテナのコンテンツの名称を取得する。
	 * @return	コンテンツの名称
	 */
	public String contentName() {
		return _contentName;
	}
	
	/**
	 * データコンテナのコンテンツの型(クラス)を取得する。
	 * @return	コンテンツの型を示すクラス
	 */
	public Class<?> contentClass() {
		return _contentType;
	}
	
	/**
	 * このデータコンテナ要素が、さらに子の要素を格納できるかどうかを取得する。
	 * @return	子の要素を持つ場合は {@code true}、持たない場合は {@code false}
	 */
	public boolean allowsChildren() {
		return _allowsChildren;
	}

	/**
	 * 名前に対応する {@code DtContainerContentTypes} のインスタンスを返す。
	 * ここで指定する名前は、{@link #contentName()} が返す値、もしくは、{@link #name()} が返す値と一致するかどうかで判定する。
	 * なお、この判定において、大文字小文字は区別されない。
	 * @param name	判定する名前(大文字小文字は区別しない)
	 * @return	名前に対応する {@code DtContainerContentTypes} のインスタンス、対応するものが見つからない場合は {@code null}
	 */
	static public DtContainerContentTypes fromName(String name) {
		if (name != null && !name.isEmpty()) {
			if (ContentDtBinder._contentName.equalsIgnoreCase(name) || ContentDtBinder.name().equalsIgnoreCase(name)) {
				return ContentDtBinder;
			}
			else if (ContentDtBinderNote._contentName.equalsIgnoreCase(name) || ContentDtBinderNote.name().equalsIgnoreCase(name)) {
				return ContentDtBinderNote;
			}
			else if (ContentDtBinderSlips._contentName.equalsIgnoreCase(name) || ContentDtBinderSlips.name().equalsIgnoreCase(name)) {
				return ContentDtBinderSlips;
			}
			else if (ContentDtSlipList._contentName.equalsIgnoreCase(name) || ContentDtSlipList.name().equalsIgnoreCase(name)) {
				return ContentDtSlipList;
			}
			else if (ContentDtSlip._contentName.equalsIgnoreCase(name) || ContentDtSlip.name().equalsIgnoreCase(name)) {
				return ContentDtSlip;
			}
			else if (ContentDtSlipNote._contentName.equalsIgnoreCase(name) || ContentDtSlipNote.name().equalsIgnoreCase(name)) {
				return ContentDtSlipNote;
			}
			else if (ContentDtSlipObjects._contentName.equalsIgnoreCase(name) || ContentDtSlipObjects.name().equalsIgnoreCase(name)) {
				return ContentDtSlipObjects;
			}
			else if (ContentDtAlgeSet._contentName.equalsIgnoreCase(name) || ContentDtAlgeSet.name().equalsIgnoreCase(name)) {
				return ContentDtAlgeSet;
			}
			else if (ContentDtalge._contentName.equalsIgnoreCase(name) || ContentDtalge.name().equalsIgnoreCase(name)) {
				return ContentDtalge;
			}
			else if (ContentExAlgeSet._contentName.equalsIgnoreCase(name) || ContentExAlgeSet.name().equalsIgnoreCase(name)) {
				return ContentExAlgeSet;
			}
			else if (ContentExalge._contentName.equalsIgnoreCase(name) || ContentExalge.name().equalsIgnoreCase(name)) {
				return ContentExalge;
			}
		}
		// not found
		return null;
	}
	
	static public DtContainerContentTypes namedObjectTypeFromClass(Class<?> contentClass) {
		if (contentClass != null) {
			if (Exalge.class.equals(contentClass)) {
				return ContentExalge;
			}
			else if (ExAlgeSet.class.equals(contentClass)) {
				return ContentExAlgeSet;
			}
			else if (Dtalge.class.equals(contentClass)) {
				return ContentDtalge;
			}
			else if (DtAlgeSet.class.equals(contentClass)) {
				return ContentDtAlgeSet;
			}
		}
		// not found
		return null;
	}
	
	static public DtContainerContentTypes namedObjectTypeFromFromObject(Object obj) {
		return (obj==null ? null : namedObjectTypeFromClass(obj.getClass()));
	}
	
	static public DtContainerContentTypes namedSlipTypeFromClass(Class<?> contentClass) {
		if (contentClass != null) {
			if (DtSlip.class.equals(contentClass)) {
				return ContentDtSlip;
			}
			else if (DtSlipList.class.equals(contentClass)) {
				return ContentDtSlipList;
			}
		}
		// not found
		return null;
	}
	
	static public DtContainerContentTypes namedSlipTypeFromObject(Object obj) {
		return (obj==null ? null : namedSlipTypeFromClass(obj.getClass()));
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
}
