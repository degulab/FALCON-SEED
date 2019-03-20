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
 *  Copyright 2007-2008  SOARS Project.
 *  <author> Hiroshi Deguchi(SOARS Project.)
 *  <author> Li Hou(SOARS Project.)
 *  <author> Yasunari Ishizuka(PieCake.inc,)
 */
/*
 * @(#)ExTimeKeyFactory.java	0.93	2007/09/03
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ExTimeKeyFactory.java	0.91	2007/08/09
 *     - created by Y.Ishizuka(PieCake.inc,)
 */

package exalge2.util;

import java.util.HashMap;
import java.util.Iterator;

/**
 * 時間キー文字列から時間キーユーティリティクラスを生成するためのユーティリティクラス。
 * <br>
 * 時間キー文字列を指定して {@link #createTimeKey(String)} メソッドを呼び出すと、
 * 時間キー文字列のフォーマットに適したクラスインスタンスを生成する。
 * 生成されたクラスインスタンスに対して、{@link ExTimeKey} インタフェースによる操作を
 * 行うことで、値として時間キーを操作できる。
 * <br>
 * 操作後の時間キー文字列は、{@link ExTimeKey#toString()} メソッドで取得できる。
 * 
 * @version 0.93 2007/09/03
 * 
 * @author H.Deguchi(SOARS Project.)
 * @author Y.Ishizuka(PieCake.inc,)
 *
 * @since 0.91
 */
public class ExTimeKeyFactory
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private static ExTimeKeyFactory _instance = null;
	
	private HashMap<Class, ExTimeKey> _entry = new HashMap<Class, ExTimeKey>();

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	protected ExTimeKeyFactory() {
		// デフォルトの登録プロセス
		this._entry.put(ExYearTimeKey.class, new ExYearTimeKey());
		this._entry.put(ExMonthTimeKey.class, new ExMonthTimeKey());
		this._entry.put(ExQuarterTimeKey.class, new ExQuarterTimeKey());
		this._entry.put(ExFiscalYearTimeKey.class, new ExFiscalYearTimeKey());
	}

	/**
	 * <code>ExTimeKeyFactory</code> クラスの唯一のインスタンスを取得する。
	 * 
	 * @return <code>ExTimeKeyFactory</code> インスタンス
	 */
	static public ExTimeKeyFactory getInstance() {
		if (_instance == null) {
			_instance = new ExTimeKeyFactory();
		}
		return _instance;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 時間キー文字列から、そのフォーマットに適した {@link ExTimeKey} インタフェース実装クラスの
	 * インスタンスを生成する。
	 * 
	 * @param timeKeyString 時間キー文字列
	 * 
	 * @return 時間キー文字列のフォーマットに適したクラスのインスタンス
	 * 
	 * @throws NullPointerException 引数が <tt>null</tt> の場合
	 * @throws IllegalArgumentException サポートされない形式か、値が有効範囲にない場合
	 */
	static public ExTimeKey createTimeKey(String timeKeyString)
	{
		ExTimeKeyFactory factory = getInstance();
		ExTimeKey newInstance = null;
		try {
			newInstance = factory.createClassByTimeKeyString(timeKeyString);
		}
		catch (IllegalAccessException ex) {
			throw new IllegalArgumentException("Failed to create instance", ex);
		}
		catch (InstantiationException ex) {
			throw new IllegalArgumentException("Failed to create instance", ex);
		}
		
		return newInstance;
	}

	/**
	 * 時間キーユーティリティクラスを登録する。
	 * <br>
	 * {@link ExYearTimeKey}、{@link ExMonthTimeKey}、{@link ExQuarterTimeKey}、{@link ExFiscalYearTimeKey} クラスは、
	 * すでに登録済みとなる。
	 * 
	 * @param timekeyFormatClass 登録するクラス。このクラスは、{@link ExTimeKey} インタフェースを
	 * 実装している必要がある。
	 * 
	 * @throws ClassCastException 指定されたクラスが {@link ExTimeKey} インタフェースを実装していない場合
	 * @throws IllegalAccessException 指定されたクラスのデフォルトコンストラクタにアクセスできない場合
	 * @throws InstantiationException 指定されたクラスがインタフェースまたは abstract クラスの場合
	 * @throws IllegalArgumentException 指定されたクラスが既に登録済みの場合
	 */
	public void registFormatClass(Class timekeyFormatClass)
		throws ClassCastException, IllegalAccessException, InstantiationException, IllegalArgumentException
	{
		if (!ExTimeKey.class.isAssignableFrom(timekeyFormatClass)) {
			throw new ClassCastException();
		}
		// チェック
		if (this._entry.containsKey(timekeyFormatClass)) {
			// すでに存在する
			throw new IllegalArgumentException("Already exist class");
		}
		// 生成
		ExTimeKey newInstance = (ExTimeKey)timekeyFormatClass.newInstance();
		
		// 登録
		this._entry.put(timekeyFormatClass, newInstance);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected ExTimeKey createClassByTimeKeyString(String timeKeyString)
		throws IllegalAccessException, InstantiationException, IllegalArgumentException
	{
		ExTimeKey target = null;
		
		// フォーマット判定
		Iterator<Class> it = this._entry.keySet().iterator();
		while (it.hasNext()) {
			Class type = it.next();
			ExTimeKey inst = this._entry.get(type);
			//--- チェック
			if (inst.isSupported(timeKeyString)) {
				// 適したフォーマット
				target = (ExTimeKey)type.newInstance();
				break;
			}
		}
		
		// フォーマットなし
		if (target == null) {
			throw new IllegalArgumentException("Not supportted fomat");
		}
		
		// 値設定
		target.set(timeKeyString);
		return target;
	}
}
