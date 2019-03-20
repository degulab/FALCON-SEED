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
 *  Copyright 2007-2009  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)ModuleSettings.java	1.14	2009/12/09
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.module.setting;

import java.io.IOException;
import java.util.ArrayList;

import ssac.aadl.module.ModuleArgDetail;
import ssac.aadl.module.ModuleArgType;
import ssac.util.Strings;

/**
 * モジュール設定情報を保持するクラス。
 * 
 * @version 1.14	2009/12/09
 * @since 1.14
 */
public class ModuleSettings extends AbstractSettings
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static public final String SECTION = "module";
	static public final String KEY_TITLE = SECTION + ".title";
	static public final String KEY_DESC  = SECTION + ".description";
	static public final String KEY_ARGS  = SECTION + ".args";
	static public final String KEY_ARGS_NUM  = KEY_ARGS + ".num";
	static public final String KEY_ARGS_TYPE = KEY_ARGS + ".type";
	static public final String KEY_ARGS_DESC = KEY_ARGS + ".desc";
	static public final String KEY_NOTE  = SECTION + ".note";

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private final ArrayList<ModuleArgDetail> _args = new ArrayList<ModuleArgDetail>();

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public ModuleSettings() {
		super();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	//
	// Title
	//

	/**
	 * モジュールのタイトルが設定されている場合に <tt>true</tt> を返す。
	 * 有効な文字列がタイトルとして設定されていない場合は <tt>false</tt> を返す。
	 */
	public boolean isSpecifiedTitle() {
		return (getTitle() != null);
	}

	/**
	 * モジュールのタイトルを返す。
	 * 有効な文字列がタイトルとして設定されていない場合は <tt>null</tt> を返す。
	 */
	public String getTitle() {
		String t = props.getString(KEY_TITLE, null);
		return (Strings.isNullOrEmpty(t) ? null : t);
	}

	/**
	 * モジュールのタイトルを設定する。
	 * @param title	タイトルを表す文字列を指定する。有効な文字列ではない場合は、
	 * 				エントリを除去する。
	 */
	public void setTitle(String title) {
		if (Strings.isNullOrEmpty(title)) {
			props.clearProperty(KEY_TITLE);
		} else {
			props.setString(KEY_TITLE, title);
		}
	}
	
	//
	// Description
	//

	/**
	 * モジュールの説明が設定されている場合に <tt>true</tt> を返す。
	 * 有効な文字列が説明として設定されていない場合は <tt>false</tt> を返す。
	 */
	public boolean isSpecifiedDescription() {
		return (getDescription() != null);
	}

	/**
	 * モジュールの説明を返す。
	 * 有効な文字列が説明として設定されていない場合は <tt>null</tt> を返す。
	 */
	public String getDescription() {
		String d = props.getString(KEY_DESC, null);
		return (Strings.isNullOrEmpty(d) ? null : d);
	}

	/**
	 * モジュールの説明を設定する。
	 * @param desc	説明を表す文字列を指定する。有効な文字列ではない場合は、
	 * 				エントリを除去する。
	 */
	public void setDescription(String desc) {
		if (Strings.isNullOrEmpty(desc)) {
			props.clearProperty(KEY_DESC);
		} else {
			props.setString(KEY_DESC, desc);
		}
	}
	
	//
	// Note
	//

	/**
	 * モジュールの備考が設定されている場合に <tt>true</tt> を返す。
	 * 有効な文字列が備考として設定されていない場合は <tt>false</tt> を返す。
	 */
	public boolean isSpecifiedNote() {
		return (getNote() != null);
	}

	/**
	 * モジュールの備考を返す。
	 * 有効な文字列が備考として設定されていない場合は <tt>null</tt> を返す。
	 */
	public String getNote() {
		String d = props.getString(KEY_NOTE, null);
		return (Strings.isNullOrEmpty(d) ? null : d);
	}

	/**
	 * モジュールの備考を設定する。
	 * @param desc	備考を表す文字列を指定する。有効な文字列ではない場合は、
	 * 				エントリを除去する。
	 */
	public void setNote(String note) {
		if (Strings.isNullOrEmpty(note)) {
			props.clearProperty(KEY_NOTE);
		} else {
			props.setString(KEY_NOTE, note);
		}
	}
	
	//
	// Arguments
	//

	/**
	 * 引数設定が存在しない場合に <tt>true</tt> を返す。
	 */
	public boolean isArgumentEmpty() {
		return _args.isEmpty();
	}

	/**
	 * 引数設定をすべて破棄する。
	 */
	public void clearArguments() {
		_args.clear();
	}

	/**
	 * 保持されている引数設定の総数を返す。
	 * @return	保持されている引数設定の総数
	 */
	public int getNumArguments() {
		return _args.size();
	}
	
	public int indexOfArgument(ModuleArgDetail arg) {
		return _args.indexOf(arg);
	}
	
	public ModuleArgDetail getArgument(int index) {
		return _args.get(index);
	}
	
	public ModuleArgDetail setArgument(int index, ModuleArgDetail newArg) {
		return _args.set(index, newArg);
	}
	
	public void addArgument(ModuleArgDetail newArg) {
		_args.add(newArg);
	}
	
	public void insertArgument(int index, ModuleArgDetail newArg) {
		_args.add(index, newArg);
	}
	
	public ModuleArgDetail removeArgument(int index) {
		return _args.remove(index);
	}
	
	public boolean removeArgument(ModuleArgDetail arg) {
		return _args.remove(arg);
	}

	/**
	 * 指定された位置の引数属性を取得する。
	 * @param index	取得する位置のインデックス
	 * @return	指定された位置の引数属性を返す。
	 * 			指定された位置に引数が存在しない場合は <tt>null</tt> を返す。
	 */
	public ModuleArgType getArgumentType(int index) {
		try {
			ModuleArgDetail arg = getArgument(index);
			return arg.type();
		} catch (Throwable ex) {
			return null;
		}
	}

	/**
	 * 指定された位置の引数説明を取得する。
	 * @param index	取得する位置のインデックス
	 * @return	指定された位置の引数説明を返す。
	 * 			指定された位置に引数が存在しない場合は <tt>null</tt> を返す。
	 */
	public String getArgumentDescription(int index) {
		try {
			ModuleArgDetail arg = getArgument(index);
			return arg.description();
		} catch (Throwable ex) {
			return null;
		}
	}
	
	public ModuleArgDetail[] getArgumentDetails() {
		return _args.toArray(new ModuleArgDetail[_args.size()]);
	}

	//------------------------------------------------------------
	// Implement AbstractSettings interfaces
	//------------------------------------------------------------

	@Override
	public void commit() throws IOException {
		// 引数設定をプロパティに保存
		Integer numArgs = props.getInteger(KEY_ARGS_NUM, Integer.valueOf(0));
		int index = 0;
		if (!_args.isEmpty()) {
			// 新しい設定を保存
			int len = _args.size();
			for (; index < len; index++) {
				ModuleArgDetail arg = _args.get(index);
				if (arg==null) {
					props.clearProperty(getArgsTypeKey(index));
					props.clearProperty(getArgsDescKey(index));
				} else {
					if (arg.type() == null)
						props.clearProperty(getArgsTypeKey(index));
					else
						props.setString(getArgsTypeKey(index), arg.type().typeName());
					if (Strings.isNullOrEmpty(arg.description()))
						props.clearProperty(getArgsDescKey(index));
					else
						props.setString(getArgsDescKey(index), arg.description());
				}
			}
		}
		// 古いプロパティを削除
		for (; index < numArgs.intValue(); index++) {
			props.clearProperty(getArgsTypeKey(index));
			props.clearProperty(getArgsDescKey(index));
		}
		// 引数設定の総数を保存
		props.setInteger(KEY_ARGS_NUM, _args.size());
		
		// プロパティの保存
		super.commit();
	}

	@Override
	public void rollback() {
		// プロパティの読み込み
		super.rollback();
		
		// プロパティから引数設定を取得する
		_args.clear();
		Integer numArgs = props.getInteger(KEY_ARGS_NUM, null);
		if (numArgs != null) {
			int len = numArgs.intValue();
			for (int i = 0; i < len; i++) {
				String strType = props.getString(getArgsTypeKey(i), null);
				String strDesc = props.getString(getArgsDescKey(i), null);
				ModuleArgType argType = ModuleArgType.fromName(strType);
				_args.add(new ModuleArgDetail(argType, strDesc));
			}
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	static protected final String getArgsTypeKey(int index) {
		return (KEY_ARGS_TYPE + Integer.toString(index+1));
	}
	
	static protected final String getArgsDescKey(int index) {
		return (KEY_ARGS_DESC + Integer.toString(index+1));
	}
}
