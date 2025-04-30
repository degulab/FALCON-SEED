/*
 * @(#)GenericSchemaTreeData.java	3.2.0	2015/06/28
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.filter.generic.gui.util;

import ssac.aadl.fs.module.schema.SchemaElementObject;


/**
 * 汎用フィルタ定義データをツリーデータとするためのインタフェース。
 * 
 * @version 3.2.0
 * @since 3.2.0
 */
public interface GenericSchemaTreeData extends SchemaElementObject
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * このオブジェクトのデータ実体を返す。
	 */
	public Object getData();

	/**
	 * ツリーに表示する際の文字列表現
	 */
	public String toTreeString();
}
