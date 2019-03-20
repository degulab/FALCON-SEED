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
 * @(#)FilterArgReferValueEditModel.java	3.1.0	2014/05/14
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module.swing;

import ssac.aadl.module.ModuleArgType;
import ssac.falconseed.module.args.IMExecArgParam;

/**
 * フィルタマクロ引数定義の参照を保持する引数値編集用のデータモデル。
 * このオブジェクトは編集用であり、データのインスタンスでハッシュ値を生成するため、
 * 内部の値によらず、<code>equals</code> メソッドはインスタンス値で判定する。
 * このオブジェクトの引数定義は、参照する引数定義となる。
 * このオブジェクトのクローン時には、参照する引数定義オブジェクトは、シャローコピーとなる。
 * 
 * @version 3.1.0	2014/05/14
 * @since 3.1.0
 */
public class FilterArgReferValueEditModel implements IFilterArgEditModel, Cloneable
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** 参照するフィルタマクロ引数定義 **/
	private FilterArgEditModel	_referedArgDef;
	/** 参照引数の前に付加する文字列 **/
	private String				_prefix = "";
	/** 参照引数の後に付加する文字列 **/
	private String				_suffix = "";

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * 指定された引数定義を参照する、このオブジェクトの新しいインスタンスを生成する。
	 * このコンストラクタで生成されたインスタンスでは、参照する引数の前後に付加する文字列は空となる。
	 * @param referedArg	参照するフィルタマクロ引数定義
	 * @throws NullPointerException	<em>referedArg</em> が <tt>null</tt> の場合
	 */
	public FilterArgReferValueEditModel(final FilterArgEditModel referedArg) {
		this(referedArg, null, null);
	}

	/**
	 * 指定された <em>prefix</em> と <em>suffix</em> を保持し、指定された引数定義を参照する、このオブジェクトの新しいインスタンスを生成する。
	 * @param referedArg	参照するフィルタマクロ引数定義
	 * @param prefix		参照引数の前に付加する文字列、もしくは <tt>null</tt>
	 * @param suffix		参照引数の後に付加する文字列、もしくは <tt>null</tt>
	 */
	public FilterArgReferValueEditModel(final FilterArgEditModel referedArg, String prefix, String suffix) {
		if (referedArg == null)
			throw new NullPointerException("FilterArgEditModel object is null.");
		_referedArgDef = referedArg;
		setPrefix(prefix);
		setSuffix(suffix);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * このオブジェクトが参照する引数定義を取得する。
	 * @return	参照するフィルタマクロ引数定義
	 */
	public FilterArgEditModel getReferencedArgument() {
		return _referedArgDef;
	}

	/**
	 * 参照する引数定義を設定する。
	 * @param referedArg	新しく参照するフィルタマクロ引数定義
	 * @throws NullPointerException	<em>referedArg</em> が <tt>null</tt> の場合
	 */
	public void setReferencedArgument(final FilterArgEditModel referedArg) {
		if (_referedArgDef == null)
			throw new NullPointerException("FilterArgEditModel object is null.");
		_referedArgDef = referedArg;
	}

	/**
	 * 参照する引数の前に付加する文字列が設定されていないかどうかを判定する。
	 * @return	設定されていなければ <tt>true</tt>
	 */
	public boolean isEmptyPrefix() {
		return (_prefix.length() <= 0);
	}

	/**
	 * 参照する引数の後に付加する文字列が設定されていないかどうかを判定する。
	 * @return	設定されていなければ <tt>true</tt>
	 */
	public boolean isEmptySuffix() {
		return (_suffix.length() <= 0);
	}

	/**
	 * 参照する引数の前に付加する文字列を取得する。
	 * @return	設定されている文字列、設定されていない場合は空文字
	 */
	public String getPrefix() {
		return _prefix;
	}

	/**
	 * 参照する引数の後に付加する文字列を取得する。
	 * @return	設定されている文字列、設定されていない場合は空文字
	 */
	public String getSuffix() {
		return _suffix;
	}

	/**
	 * 参照する引数の前に付加する文字列を設定する。
	 * @param prefix	付加する文字列、もしくは <tt>null</tt>
	 * @return	付加する文字列が変更された場合は <tt>true</tt>
	 */
	public boolean setPrefix(String prefix) {
		if (prefix == null)
			prefix = "";
		
		if (!prefix.equals(_prefix)) {
			_prefix = prefix;
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 参照する引数の後に付加する文字列を設定する。
	 * @param suffix	付加する文字列、もしくは <tt>null</tt>
	 * @return	付加する文字列が変更された場合は <tt>true</tt>
	 */
	public boolean setSuffix(String suffix) {
		if (suffix == null)
			suffix = "";
		
		if (!suffix.equals(_suffix)) {
			_suffix = suffix;
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * このオブジェクトの複製を生成する。
	 */
	public FilterArgReferValueEditModel clone() {
		try {
			FilterArgReferValueEditModel newData = (FilterArgReferValueEditModel)super.clone();
			return newData;
		}
		catch (CloneNotSupportedException ex) {
			throw new InternalError();
		}
	}

	@Override
	public int hashCode() {
		return System.identityHashCode(this);
	}

	@Override
	public boolean equals(Object obj) {
		return (obj == this);
	}
	
	public boolean equalValues(Object obj) {
		if (obj == this) {
			return true;
		}
		
		if (obj instanceof FilterArgReferValueEditModel) {
			FilterArgReferValueEditModel aModel = (FilterArgReferValueEditModel)obj;
			if (aModel._referedArgDef.equals(this._referedArgDef)
				&& aModel._prefix.equals(this._prefix)
				&& aModel._suffix.equals(this._suffix))
			{
				return true;
			}
		}
		
		return false;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getName());
		sb.append("@");
		sb.append(Integer.toHexString(hashCode()));
		sb.append("[");
		appendParameters(sb);
		sb.append("]");
		return sb.toString();
	}

	//------------------------------------------------------------
	// Implement IFilterArgEditModel interfaces
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Implement IModuleArgConfig interfaces
	//------------------------------------------------------------

	/**
	 * この引数の番号を取得する。
	 * @return	1から始まる引数番号
	 */
	public int getArgNo() {
		return _referedArgDef.getArgNo();
	}
	
	/**
	 * この引数の番号を設定する。
	 * @param argno	1から始まる引数番号
	 * @since 2.0.0
	 */
	public void setArgNo(int argno) {
		_referedArgDef.setArgNo(argno);
	}

	/**
	 * 引数説明を設定する。
	 * @param desc	新しい説明の文字列
	 */
	public void setDescription(String desc) {
		_referedArgDef.setDescription(desc);
	}

	/**
	 * 引数の値を設定する。
	 * @param newValue	新しい引数の値
	 */
	public void setValue(Object newValue) {
		_referedArgDef.setValue(newValue);
	}

	/**
	 * テンポラリファイルへの出力設定を取得する。
	 * @return	テンポラリファイルへ出力する場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean getOutToTempEnabled() {
		return _referedArgDef.getOutToTempEnabled();
	}

	/**
	 * テンポラリファイルへの出力を設定する。
	 * @param toEnable	テンポラリファイルへ出力する場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public void setOutToTempEnabled(boolean toEnable) {
		_referedArgDef.setOutToTempEnabled(toEnable);
	}

	/**
	 * テンポラリファイルのプレフィックスが指定されているかを判定する。
	 * @return	プレフィックスが指定されている場合は <tt>true</tt>、指定されていない場合は <tt>false</tt>
	 */
	public boolean hasTempFilePrefix() {
		return _referedArgDef.hasTempFilePrefix();
	}

	/**
	 * 指定されたテンポラリファイル用プレフィックスを取得する。
	 * @return	プレフィックスが指定されている場合はその文字列、指定されていない場合は <tt>null</tt>
	 */
	public String getTempFilePrefix() {
		return _referedArgDef.getTempFilePrefix();
	}

	/**
	 * テンポラリファイル用プレフィックスを設定する。
	 * @param prefix	テンポラリファイルのプレフィックス、指定しない場合は <tt>null</tt> もしくは長さが 0 の文字列
	 */
	public void setTempFilePrefix(String prefix) {
		_referedArgDef.setTempFilePrefix(prefix);
	}

	/**
	 * 実行完了後にファイルを表示するかどうかの設定を取得する。
	 * @return	表示する場合は <tt>true</tt>、表示しない場合は <tt>false</tt>
	 */
	public boolean getShowFileAfterRun() {
		return _referedArgDef.getShowFileAfterRun();
	}

	/**
	 * 実行完了後にファイルを表示するかどうかを設定する。
	 * @param toShow	表示する場合は <tt>true</tt>、表示しない場合は <tt>false</tt>
	 */
	public void setShowFileAfterRun(boolean toShow) {
		_referedArgDef.setShowFileAfterRun(toShow);
	}

	//------------------------------------------------------------
	// Implement IModuleArgValue interfaces
	//------------------------------------------------------------

	/**
	 * 引数種別を返す。
	 */
	public ModuleArgType getType() {
		return _referedArgDef.getType();
	}

	/**
	 * 引数説明を返す。
	 */
	public String getDescription() {
		return _referedArgDef.getDescription();
	}

	/**
	 * 実行時の引数値が固定されているかを判定する。
	 * @return	実行時引数が固定されている場合は <tt>true</tt>、変更可能な場合は <tt>false</tt>
	 */
	public boolean isFixedValue() {
		return _referedArgDef.isFixedValue();
	}

	/**
	 * 引数定義のパラメータ種別を返す。
	 * @return	実行時可変の場合は <code>IMExecArgParam</code> オブジェクト、実行時固定の場合は <tt>null</tt>
	 */
	public IMExecArgParam getParameterType() {
		return _referedArgDef.getParameterType();
	}

	/**
	 * 引数値が設定されているかを判定する。
	 * @return	引数値が <tt>null</tt> ではない場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean hasValue() {
		return _referedArgDef.hasValue();
	}

	/**
	 * 引数の値を取得する。
	 * @return	引数の値
	 */
	public Object getValue() {
		return _referedArgDef.getValue();
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	protected void appendParameters(StringBuilder buffer) {
		//--- argno
		buffer.append("prefix=\"");
		buffer.append(_prefix);
		buffer.append("\", suffix=\"");
		buffer.append(_suffix);
		buffer.append("\", refered=");
		buffer.append(_referedArgDef);
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
