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
 *  Copyright 2007-2014  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)FilterValuesEditModelEvent.java	3.1.0	2014/05/19
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)FilterValuesEditModelEvent.java	2.0.0	2012/10/11
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module.swing;

import java.util.EventObject;

/**
 * フィルタ実行時引数値を保持するデータモデル用のイベントオブジェクト。
 * 
 * @version 3.1.0	2014/05/19
 * @since 2.0.0
 */
public class FilterValuesEditModelEvent extends EventObject
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	/**
	 * 無効な引数インデックス
	 * @since 3.1.0
	 */
	static public final int	INVALID_ARGUMENT_INDEX	= (-1);
	/**
	 * モジュールの実行順序リンクが更新されたことを示す
	 * @since 3.1.0
	 */
	static public final int	UPDATE_RUNORDER_LINK	= 0x10000;
	/** フィルタ定義の構成が変更されたことを示す **/
	static public final int	UPDATE_DEFINITION		= 0x1000;
	/** フィルタの引数属性が変更されたことを示す **/
	static public final int	UPDATE_ARGUMENT_TYPE	= 0x100;
	/** フィルタの引数説明が変更されたことを示す **/
	static public final int	UPDATE_ARGUMENT_DESC	= 0x10;
	/** フィルタの引数値が変更されたことを示す **/
	static public final int	UPDATE_ARGUMENT_VALUE	= 1;
	
	static public final int	UPDATE_ARGUMENT_DATA	= (UPDATE_ARGUMENT_TYPE | UPDATE_ARGUMENT_DESC | UPDATE_ARGUMENT_VALUE);

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** イベント種別 **/
	protected int	_type;
	/** 対象引数の開始インデックス **/
	protected int	_firstArgIndex;
	/** 対象引数の終端インデックス **/
	protected int	_lastArgIndex;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public FilterValuesEditModelEvent(int type, IFilterValuesEditModel source) {
		this(source, 0, Integer.MAX_VALUE, type);
	}
	
	public FilterValuesEditModelEvent(IFilterValuesEditModel source) {
		this(source, 0, Integer.MAX_VALUE, UPDATE_ARGUMENT_VALUE);
	}
	
	public FilterValuesEditModelEvent(IFilterValuesEditModel source, int argIndex) {
		this(source, argIndex, argIndex, UPDATE_ARGUMENT_VALUE);
	}
	
	public FilterValuesEditModelEvent(IFilterValuesEditModel source, int firstArgIndex, int lastArgIndex) {
		this(source, firstArgIndex, lastArgIndex, UPDATE_ARGUMENT_VALUE);
	}
	
	public FilterValuesEditModelEvent(IFilterValuesEditModel source, int firstArgIndex, int lastArgIndex, int type) {
		super(source);
		this._firstArgIndex = firstArgIndex;
		this._lastArgIndex  = lastArgIndex;
		this._type          = type;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public int getType() {
		return _type;
	}
	
	public int getFirstArgIndex() {
		return _firstArgIndex;
	}
	
	public int getLastArgIndex() {
		return _lastArgIndex;
	}
	
	public String paramString() {
		StringBuilder sb = new StringBuilder();
		
		if (_type == 0) {
			sb.append("No type,");
		}
		else {
			int type = _type;
			
			if ((type & UPDATE_RUNORDER_LINK) != 0) {
				sb.append("UPDATE_RUNORDER_LINK,");
				type &= ~UPDATE_RUNORDER_LINK;
			}
			
			if ((type & UPDATE_DEFINITION) != 0) {
				sb.append("UPDATE_DEFINITION,");
				type &= ~UPDATE_DEFINITION;
			}
			
			if ((type & UPDATE_ARGUMENT_TYPE) != 0) {
				sb.append("UPDATE_ARGUMENT_TYPE,");
				type &= ~UPDATE_ARGUMENT_TYPE;
			}
			
			if ((type & UPDATE_ARGUMENT_DESC) != 0) {
				sb.append("UPDATE_ARGUMENT_DESC,");
				type &= ~UPDATE_ARGUMENT_DESC;
			}
			
			if ((type & UPDATE_ARGUMENT_VALUE) != 0) {
				sb.append("UPDATE_ARGUMENT_VALUE,");
				type &= ~UPDATE_ARGUMENT_VALUE;
			}
			
			if (type != 0) {
				sb.append("Unknown type(");
				sb.append(type);
				sb.append("),");
			}
		}
		
		sb.append("first=");
		sb.append(_firstArgIndex);
		sb.append(",last=");
		sb.append(_lastArgIndex);
		
		return sb.toString();
	}

	@Override
	public String toString() {
		return getClass().getName() + "[" + paramString() + "] on " + String.valueOf(getSource());
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
