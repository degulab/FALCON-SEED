/*
 * @(#)DtContainerContentDisplayableTypesManager.java	1.1.0	2023/01/25
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)DtContainerContentDisplayableTypesManager.java	1.0.0	2022/12/15
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge.container.editor.content;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import dtalge.container.editor.DtContainerEditorMessages;

/**
 * データコンテナのコンテントタイプごとの表示名を管理するクラス。
 * 基本的に、このオブジェクトは不変である。
 * 
 * @version 1.1.0
 */
public class DtContainerContentTypesManager
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	static private DtContainerContentTypesManager	_instance;
	
	private Map<DtContainerContentTypes, DtContainerContentDisplayableType>	_dispTypeMap;
	/** コンテンツタイプが許容する子のコンテントタイプの配列のマップ **/
	private Map<DtContainerContentTypes, DtContainerContentDisplayableType[]>	_childTypesMap;
	/** コンテンツのテキスト形式が JSON のみのもの **/
	private Set<DtContainerContentTypes>	_jsonOnlyTypes;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	protected DtContainerContentTypesManager() {
		_dispTypeMap = new HashMap<>();
		putDisplayableTextIntoMap(_dispTypeMap, DtContainerContentTypes.ContentDtBinder, DtContainerEditorMessages.getInstance().contentName_DtBinderRoot);
		putDisplayableTextIntoMap(_dispTypeMap, DtContainerContentTypes.ContentDtBinderNote, DtContainerEditorMessages.getInstance().contentName_DtBinderNote);
		putDisplayableTextIntoMap(_dispTypeMap, DtContainerContentTypes.ContentDtBinderSlips, DtContainerEditorMessages.getInstance().contentName_DtBinderSlips);
		putDisplayableTextIntoMap(_dispTypeMap, DtContainerContentTypes.ContentDtSlipList, DtContainerEditorMessages.getInstance().contentName_DtSlipList);
		putDisplayableTextIntoMap(_dispTypeMap, DtContainerContentTypes.ContentDtSlip, DtContainerEditorMessages.getInstance().contentName_DtSlipRoot);
		putDisplayableTextIntoMap(_dispTypeMap, DtContainerContentTypes.ContentDtSlipNote, DtContainerEditorMessages.getInstance().contentName_DtSlipNote);
		putDisplayableTextIntoMap(_dispTypeMap, DtContainerContentTypes.ContentDtSlipObjects, DtContainerEditorMessages.getInstance().contentName_DtSlipObjects);
		putDisplayableTextIntoMap(_dispTypeMap, DtContainerContentTypes.ContentDtAlgeSet, DtContainerEditorMessages.getInstance().contentName_DtAlgeSet);
		putDisplayableTextIntoMap(_dispTypeMap, DtContainerContentTypes.ContentDtalge, DtContainerEditorMessages.getInstance().contentName_Dtalge);
		putDisplayableTextIntoMap(_dispTypeMap, DtContainerContentTypes.ContentExAlgeSet, DtContainerEditorMessages.getInstance().contentName_ExAlgeSet);
		putDisplayableTextIntoMap(_dispTypeMap, DtContainerContentTypes.ContentExalge, DtContainerEditorMessages.getInstance().contentName_Exalge);
		
		_childTypesMap = new HashMap<>();
		putAllowsChildDisplayableTypesIntoMap(_dispTypeMap, _childTypesMap, DtContainerContentTypes.ContentDtBinder,
												DtContainerContentTypes.ContentDtSlip, DtContainerContentTypes.ContentDtSlipList);
		putAllowsChildDisplayableTypesIntoMap(_dispTypeMap, _childTypesMap, DtContainerContentTypes.ContentDtBinderSlips,
												DtContainerContentTypes.ContentDtSlip, DtContainerContentTypes.ContentDtSlipList);
		putAllowsChildDisplayableTypesIntoMap(_dispTypeMap, _childTypesMap, DtContainerContentTypes.ContentDtSlipList,
												DtContainerContentTypes.ContentDtSlip);
		putAllowsChildDisplayableTypesIntoMap(_dispTypeMap, _childTypesMap, DtContainerContentTypes.ContentDtSlip,
												DtContainerContentTypes.ContentExalge, DtContainerContentTypes.ContentExAlgeSet,
												DtContainerContentTypes.ContentDtalge, DtContainerContentTypes.ContentDtAlgeSet);
		putAllowsChildDisplayableTypesIntoMap(_dispTypeMap, _childTypesMap, DtContainerContentTypes.ContentDtSlipObjects,
												DtContainerContentTypes.ContentExalge, DtContainerContentTypes.ContentExAlgeSet,
												DtContainerContentTypes.ContentDtalge, DtContainerContentTypes.ContentDtAlgeSet);
		putAllowsChildDisplayableTypesIntoMap(_dispTypeMap, _childTypesMap, DtContainerContentTypes.ContentExAlgeSet,
												DtContainerContentTypes.ContentExalge);
		putAllowsChildDisplayableTypesIntoMap(_dispTypeMap, _childTypesMap, DtContainerContentTypes.ContentDtAlgeSet,
												DtContainerContentTypes.ContentDtalge);
		
		// JSON only
		_jsonOnlyTypes = new HashSet<>();
		_jsonOnlyTypes.add(DtContainerContentTypes.ContentDtBinder);
		_jsonOnlyTypes.add(DtContainerContentTypes.ContentDtBinderSlips);
		_jsonOnlyTypes.add(DtContainerContentTypes.ContentDtSlipList);
		_jsonOnlyTypes.add(DtContainerContentTypes.ContentDtSlip);
		_jsonOnlyTypes.add(DtContainerContentTypes.ContentDtSlipObjects);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	static public DtContainerContentTypesManager getInstance() {
		if (_instance == null) {
			_instance = new DtContainerContentTypesManager();
		}
		return _instance;
	}
	
	public DtContainerContentDisplayableType getDisplayableType(DtContainerContentTypes type) {
		return _dispTypeMap.get(type);
	}
	
	public DtContainerContentDisplayableType[] getChildDisplayableTypes(DtContainerContentTypes type) {
		return _childTypesMap.get(type);
	}
	
	public boolean isTextFormatJsonOnly(DtContainerContentTypes type) {
		return _jsonOnlyTypes.contains(type);
	}
	
	/**
	 * 指定されたコンテントタイプが、データバインダー{@code(DtBinder)}の要素かどうかを判定する。
	 * この判定では、ノート{@code (Dtalge)}、名前付きスリップオブジェクトのルート、名前付きスリップオブジェクトを、
	 * データバインダー{@code(DtBinder)}の要素とみなす。
	 * @param type	判定するコンテントタイプ
	 * @return	データバインダー{@code(DtBinder)}の要素であれば {@code true}
	 */
	public boolean isDtBinderElement(DtContainerContentTypes type) {
		switch (type) {
			case ContentDtSlip:
			case ContentDtSlipList:
			case ContentDtBinderSlips:
			case ContentDtBinderNote:
				return true;
			default:
				return false;
		}
	}
	
	/**
	 * <em>parentType</em> に対して、<em>childType</em> を追加可能かどうかを判定する。
	 * 引数のいずれかが {@code null} の場合、このメソッドは {@code false} を返す。
	 * @param parentType	親ノードのコンテントタイプ
	 * @param childType		子ノードのコンテントタイプ
	 * @return	追加可能なら {@code true}、そうでない場合は {@code false}
	 * @since 1.1.0
	 */
	public boolean isAppendableIntoParent(DtContainerContentTypes parentType, DtContainerContentTypes childType) {
		if (parentType == null || childType == null)	return false;
		
		switch (parentType) {
			case ContentExAlgeSet:
				return (DtContainerContentTypes.ContentExalge==childType);
			case ContentDtAlgeSet:
				switch (childType) {
					case ContentDtalge:
					case ContentDtSlipNote:		// note も Dtalge として追加可能
					case ContentDtBinderNote:	// note も Dtalge として追加可能
						return true;
					default:
						return false;
				}
			case ContentDtSlipObjects:
				switch (childType) {
					case ContentExalge:
					case ContentExAlgeSet:
					case ContentDtalge:
					case ContentDtAlgeSet:
					case ContentDtSlipNote:		// note も Dtalge として追加可能
					case ContentDtBinderNote:	// note も Dtalge として追加可能
						return true;
					default:
						return false;
				}
			case ContentDtSlip:
				return (DtContainerContentTypes.ContentDtSlipNote==childType || DtContainerContentTypes.ContentDtSlipObjects==childType);
			case ContentDtSlipList:
				return (DtContainerContentTypes.ContentDtSlip==childType);
			case ContentDtBinderSlips:
				return (DtContainerContentTypes.ContentDtSlip==childType || DtContainerContentTypes.ContentDtSlipList==childType);
			case ContentDtBinder:
				return (DtContainerContentTypes.ContentDtBinderNote==childType || DtContainerContentTypes.ContentDtBinderSlips==childType);
			default:
				return false;
		}
	}
	
	/**
	 * 指定されたコンテントタイプが、データスリップ{@code(DtSlip)}の要素かどうかを判定する。
	 * この判定では、ノート{@code (Dtalge)}、名前付きスリップオブジェクトのルート、名前付きスリップオブジェクトを、
	 * データスリップ{@code(DtSlip)}の要素とみなす。
	 * @param type	判定するコンテントタイプ
	 * @return	データスリップ{@code(DtSlip)}の要素であれば {@code true}
	 */
	public boolean isDtSlipElement(DtContainerContentTypes type) {
		switch (type) {
			case ContentExalge:
			case ContentDtalge:
			case ContentExAlgeSet:
			case ContentDtAlgeSet:
			case ContentDtSlipObjects:
			case ContentDtSlipNote:
				return true;
			default:
				return false;
		}
	}
	
	/**
	 * 指定されたコンテントタイプが、インデックスで子要素を管理するリストタイプのオブジェクトかどうかを判定する。
	 * @param type	判定するコンテントタイプ
	 * @return	インデックスで子要素を管理するリストタイプなら {@code true}
	 */
	public boolean isIndexedChildren(DtContainerContentTypes type) {
		switch (type) {
			case ContentExAlgeSet:
			case ContentDtAlgeSet:
			case ContentDtSlipList:
				return true;
			default:
				return false;
		}
	}
	
	/**
	 * 指定されたコンテントタイプが、名前で子要素を管理するマップタイプのオブジェクトかどうかを判定する。
	 * @param type	判定するコンテントタイプ
	 * @return	名前で子要素を管理するマップタイプなら {@code true}
	 */
	public boolean isNamedChildren(DtContainerContentTypes type) {
		switch (type) {
			case ContentDtSlipObjects:
			case ContentDtBinderSlips:
				return true;
			default:
				return false;
		}
	}
	
	/**
	 * 指定されたコンテントタイプが、データスリップ{@code(DtSlip)}、もしくはデータスリップ集合{@code (DtSlipList)} の
	 * いずれかであるかを判定する。
	 * @param type	判定するコンテントタイプ
	 * @return	データスリップ{@code(DtSlip)}、もしくはデータスリップ集合{@code (DtSlipList)} のいずれかなら {@code true}
	 */
	public boolean isDtSlipObject(DtContainerContentTypes type) {
		return (type == DtContainerContentTypes.ContentDtSlip || type == DtContainerContentTypes.ContentDtSlipList);
	}
	
	/**
	 * 指定されたコンテントタイプが、データスリップ{@code(DtSlip)}、もしくはデータバインダー{@code (DtBinder)} のノートオブジェクトかどうかを判定する。
	 * @param type	判定するコンテントタイプ
	 * @return	データスリップ{@code(DtSlip)}、もしくはデータバインダー{@code (DtBinder)} のノートオブジェクトなら {@code true}
	 */
	public boolean isNoteObject(DtContainerContentTypes type) {
		return (type == DtContainerContentTypes.ContentDtSlipNote || type == DtContainerContentTypes.ContentDtBinderNote);
	}
	
	/**
	 * 指定されたコンテントタイプが、データスリップ{@code(DtSlip)}、もしくはデータスリップ集合{@code (DtSlipList)} を子要素として格納できるかどうかを判定する。
	 * @param type	判定するコンテントタイプ
	 * @return	データスリップ{@code(DtSlip)}、もしくはデータスリップ集合{@code (DtSlipList)} を子要素として格納できるなら {@code true}
	 */
	public boolean isAllowStoreSlipObjectChild(DtContainerContentTypes type) {
		switch (type) {
			case ContentDtBinderSlips:
			case ContentDtSlipList:
				return true;
			default:
				return false;
		}
	}
	
	/**
	 * 指定されたコンテントタイプが、以下のいずれかのデータオブジェクトを子要素として格納できるかどうかを判定する。
	 * <ul>
	 * <li>{@link DtContainerContentTypes#ContentExalge}</li>
	 * <li>{@link DtContainerContentTypes#ContentExAlgeSet}</li>
	 * <li>{@link DtContainerContentTypes#ContentDtalge}</li>
	 * <li>{@link DtContainerContentTypes#ContentDtAlgeSet}</li>
	 * </ul>
	 * 
	 * データスリップ{@code(DtSlip)}、もしくはデータスリップ集合{@code (DtSlipList)} を子要素として格納できるかどうかを判定する。
	 * @param type	判定するコンテントタイプ
	 * @return	許容されるデータオブジェクトを子要素として格納できるなら {@code true}
	 */
	public boolean isAllowStoreDataObjectChild(DtContainerContentTypes type) {
		switch (type) {
			case ContentExAlgeSet:
			case ContentDtAlgeSet:
			case ContentDtSlipObjects:
				return true;
			default:
				return false;
		}
	}
	
	public boolean isAllowAddChild(DtContainerContentTypes type) {
		switch (type) {
			case ContentExalge:
			case ContentDtalge:
			case ContentExAlgeSet:
			case ContentDtAlgeSet:
			case ContentDtSlipObjects:
			case ContentDtSlipList:
			case ContentDtBinderSlips:
			case ContentDtSlip:
			case ContentDtBinder:
				return true;
			default:
				return false;
		}
	}
	
	public boolean isAllowReplace(DtContainerContentTypes type) {
		switch (type) {
			case ContentExalge:
			case ContentDtalge:
			case ContentExAlgeSet:
			case ContentDtAlgeSet:
			case ContentDtSlip:
			case ContentDtSlipList:
			case ContentDtSlipNote:
			case ContentDtBinderNote:
				return true;
			default:
				return false;
		}
	}
	
	public boolean isAllowExport(DtContainerContentTypes type) {
		switch (type) {
			case ContentExalge:
			case ContentDtalge:
			case ContentExAlgeSet:
			case ContentDtAlgeSet:
			case ContentDtSlip:
			case ContentDtSlipList:
			case ContentDtSlipNote:
			case ContentDtBinderNote:
				return true;
			default:
				return false;
		}
	}
	
	public boolean isAllowCut(DtContainerContentTypes type) {
		switch (type) {
			case ContentExalge:
			case ContentDtalge:
			case ContentExAlgeSet:
			case ContentDtAlgeSet:
			case ContentDtSlip:
			case ContentDtSlipList:
				return true;
			default:
				return false;
		}
	}
	
	public boolean isAllowCopy(DtContainerContentTypes type) {
		switch (type) {
			case ContentExalge:
			case ContentDtalge:
			case ContentExAlgeSet:
			case ContentDtAlgeSet:
			case ContentDtSlip:
			case ContentDtSlipList:
			case ContentDtSlipNote:
			case ContentDtBinderNote:
				return true;
			default:
				return false;
		}
	}
	
	public boolean isAllowPaste(DtContainerContentTypes type) {
		// TODO: クリップボードのフレーバーも考慮する
		return false;
	}
	
	public boolean isAllowDelete(DtContainerContentTypes type) {
		switch (type) {
			case ContentExalge:
			case ContentDtalge:
			case ContentExAlgeSet:
			case ContentDtAlgeSet:
			case ContentDtSlip:
			case ContentDtSlipList:
				return true;
			default:
				return false;
		}
	}
	
	public boolean isAllowRemoveAllElements(DtContainerContentTypes type) {
		switch (type) {
			case ContentExalge:
			case ContentDtalge:
			case ContentExAlgeSet:
			case ContentDtAlgeSet:
			case ContentDtSlipList:
			case ContentDtSlipObjects:
			case ContentDtBinderSlips:
			case ContentDtSlipNote:
			case ContentDtBinderNote:
				return true;
			default:
				return false;
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	static protected void putDisplayableTextIntoMap(Map<DtContainerContentTypes, DtContainerContentDisplayableType> map, DtContainerContentTypes type, String dispText)
	{
		DtContainerContentDisplayableType disptype = new DtContainerContentDisplayableType(type, dispText);
		map.put(type, disptype);
	}
	
	static protected void putAllowsChildDisplayableTypesIntoMap(Map<DtContainerContentTypes, DtContainerContentDisplayableType> typesMap,
																Map<DtContainerContentTypes, DtContainerContentDisplayableType[]> dstMap,
																DtContainerContentTypes parentType, DtContainerContentTypes...childTypes)
	{
		DtContainerContentDisplayableType[] dispChildTypes = new DtContainerContentDisplayableType[childTypes.length];
		for (int i = 0; i < childTypes.length; ++i) {
			dispChildTypes[i] = typesMap.get(childTypes[i]);
		}
		dstMap.put(parentType, dispChildTypes);
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
