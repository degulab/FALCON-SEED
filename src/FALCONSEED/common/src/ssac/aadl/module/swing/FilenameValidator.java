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
 * @(#)FilenameValidator.java	1.14	2009/12/09
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.module.swing;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.JTextField;

import ssac.aadl.common.CommonMessages;
import ssac.util.Strings;
import ssac.util.io.ExtensionFileFilter;
import ssac.util.io.VirtualFile;
import ssac.util.swing.TextFieldCharacterValidator;

/**
 * ファイル名(もしくはフォルダ名)をチェックするバリデータ。
 * このバリデータでは、プラットフォーム間のデータ互換性を維持するため、
 * 次のような制約を設ける。
 * <ul>
 * <li>次の文字は使用できない<br>&nbsp
 * ;&nbsp;<b>~</b>
 * ;&nbsp;<b>#</b>
 * ;&nbsp;<b>%</b>
 * ;&nbsp;<b>&amp;</b>
 * ;&nbsp;<b>*</b>
 * ;&nbsp;<b>{</b>
 * ;&nbsp;<b>}</b>
 * ;&nbsp;<b>\</b>
 * ;&nbsp;<b>:</b>
 * ;&nbsp;<b>;</b>
 * ;&nbsp;<b>&lt;</b>
 * ;&nbsp;<b>&gt;</b>
 * ;&nbsp;<b>?</b>
 * ;&nbsp;<b>/</b>
 * ;&nbsp;<b>|</b>
 * ;&nbsp;<b>&quot;</b>
 * </li>
 * <li>Windows プラットフォームに合わせる為、大文字小文字を区別しない<li>
 * <li>先頭もしくは終端が .(ドット) のファイル名は作成できない</li>
 * <li>.(ドットのみ) のファイル名は作成できない</li>
 * <li>..(2 つの連続ドットのみ) のファイル名は作成できない</li>
 * </ul>
 * 
 * @version 1.14	2009/12/09
 * @since 1.14
 */
public class FilenameValidator extends TextFieldCharacterValidator
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	/** このバリデータで使用を禁止する文字 **/
	static public char[] IllegalCharacters = {
		'~', '#', '&', '*', '{', '}', '/', '\\', ':', ';', '<', '>', '?', '|', '"',
	};

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** すでに存在する名前のコレクション **/
	private Collection<String> _alreadyExists;
	private List<ExtensionFileFilter>	_extFilters;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public FilenameValidator() {
		this(null);
	}
	
	public FilenameValidator(String errorMessageTitle) {
		super(errorMessageTitle);
		setAllowsInvalid(false);
		setCharacters(false, IllegalCharacters);
		setRequiredField(true);
		setRequiredErrorMessage(CommonMessages.getInstance().FilenameValidator_RequiredError);
		setCharacterSetErrorMessage(makeCharacterSetErrorMessage(CommonMessages.getInstance().FilenameValidator_CharsetError, getCharacterSet()));
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 重複をチェックするためのファイル名のコレクションを取得する。
	 * @return	コレクションを返す。コレクションが未定義の場合は <tt>null</tt> を返す。
	 */
	public Collection<String> getAlreadyExistsFilenames() {
		return _alreadyExists;
	}

	/**
	 * 重複をチェックするためのファイル名コレクションが定義されている場合に <tt>true</tt> を返す。
	 */
	public boolean isEmptyAlreadyExistsFilenames() {
		return (_alreadyExists==null ? false : _alreadyExists.isEmpty());
	}

	/**
	 * 重複をチェックするためのファイル名コレクションをクリアする。
	 */
	public void clearAlreadyExistsFilenames() {
		if (_alreadyExists != null) {
			_alreadyExists.clear();
			_alreadyExists = null;
		}
	}
	
	public void setAlreadyExistsFilenames(Collection<? extends String> names) {
		if (names != null && !names.isEmpty()) {
			if (_alreadyExists == null) {
				_alreadyExists = new ArrayList<String>(names);
			} else {
				_alreadyExists.clear();
				_alreadyExists.addAll(names);
			}
		} else {
			clearAlreadyExistsFilenames();
		}
	}
	
	public void setAlreadyExistsFilenames(File basePath) {
		if (basePath == null) {
			clearAlreadyExistsFilenames();
			return;
		}
		
		File dir = basePath;
		if (!basePath.isDirectory()) {
			dir = basePath.getParentFile();
		}
		String[] names = dir.list();
		if (names != null && names.length > 0) {
			if (_alreadyExists == null) {
				_alreadyExists = new ArrayList<String>(Arrays.asList(names));
			} else {
				_alreadyExists.clear();
				_alreadyExists.addAll(Arrays.asList(names));
			}
		} else {
			clearAlreadyExistsFilenames();
		}
	}
	
	public void setAlreadyExistsFilenames(VirtualFile basePath) {
		if (basePath == null) {
			clearAlreadyExistsFilenames();
			return;
		}
		
		VirtualFile dir = basePath;
		if (!basePath.isDirectory()) {
			dir = basePath.getParentFile();
		}
		String[] names = dir.list();
		if (names != null && names.length > 0) {
			if (_alreadyExists == null) {
				_alreadyExists = new ArrayList<String>(Arrays.asList(names));
			} else {
				_alreadyExists.clear();
				_alreadyExists.addAll(Arrays.asList(names));
			}
		} else {
			clearAlreadyExistsFilenames();
		}
	}
	
	public boolean isExtensionFilterEmpty() {
		return (_extFilters == null);
	}
	
	public void clearExtensionFilters() {
		if (_extFilters != null) {
			_extFilters.clear();
			_extFilters = null;
		}
	}
	
	public void setExtensionFilters(ExtensionFileFilter...filters) {
		if (filters == null || filters.length < 1) {
			clearExtensionFilters();
			return;
		}
		
		if (_extFilters == null) {
			_extFilters = new ArrayList<ExtensionFileFilter>(filters.length);
		} else {
			_extFilters.clear();
		}
		
		for (ExtensionFileFilter f : filters) {
			_extFilters.add(f);
		}
	}
	
	public void setExtensionFilters(Collection<? extends ExtensionFileFilter> filters) {
		if (filters == null || filters.isEmpty()) {
			clearExtensionFilters();
			return;
		}
		
		if (_extFilters == null) {
			_extFilters = new ArrayList<ExtensionFileFilter>(filters);
		} else {
			_extFilters.clear();
			_extFilters.addAll(filters);
		}
	}

	/**
	 * 拡張子フィルタが設定されている場合に、指定されたテキストをフィルタリングする。
	 * 拡張子フィルタが設定されていない場合は、指定されたテキストをそのまま返す。
	 * @param inputText	フィルタリングするテキスト
	 * @return	フィルタリング結果となる文字列を返す。拡張子フィルタが設定されていない
	 * 			場合は <em>inputText</em> の内容をそのまま返す。
	 * 			<em>inputText</em> が <tt>null</tt> もしくは空文字列の場合もそのまま返す。
	 */
	public String filterText(String inputText) {
		if (Strings.isNullOrEmpty(inputText) || isExtensionFilterEmpty())
			return inputText;
		
		for (ExtensionFileFilter f : _extFilters) {
			if (f.existsExtension(inputText)) {
				// 拡張子がある
				return inputText;
			}
		}
		
		// 標準の拡張子を付ける
		return _extFilters.get(0).appendExtension(inputText);
	}

	/**
	 * 指定されたメッセージと文字セットを組み合わせて、エラーメッセージを作成する。
	 * 基本的に次のような組み合わせでメッセージを作成する。
	 * <blockquote>
	 * <b><i>message</i></b>&nbsp;+&nbsp;&quot;\n&nbsp;&nbsp;&quot;&nbsp;+&nbsp;<b><i>(文字セットの各文字)</i></b>
	 * </blockquote>
	 * @param message	メッセージ
	 * @param cs		文字セット
	 * @return	生成したメッセージ文字列
	 */
	static public String makeCharacterSetErrorMessage(String message, Set<Character> cs) {
		StringBuilder sb = new StringBuilder();
		if (!Strings.isNullOrEmpty(message)) {
			sb.append(message);
			sb.append("\n  ");
		}
		if (cs != null && !cs.isEmpty()) {
			Iterator<Character> it = cs.iterator();
			while (it.hasNext()) {
				Character c = it.next();
				if (c != null) {
					sb.append(c);
				}
			}
			while (it.hasNext()) {
				Character c = it.next();
				if (c != null) {
					sb.append(" ");
					sb.append(c);
				}
			}
		}
		return sb.toString();
	}

	@Override
	public boolean verify(JTextField inputField, String inputText) {
		// 基本的なチェック
		if (!super.verify(inputField, inputText)) {
			return false;
		}
		
		// 拡張子の調整
		if (!isExtensionFilterEmpty()) {
			inputText = filterText(inputText);
		}
		
		// 禁止名
		if (inputText.equals(".") || inputText.equals("..") || inputText.startsWith(".") || inputText.endsWith(".")) {
			inputField.selectAll();
			showError(inputField, CommonMessages.getInstance().FilenameValidator_IllegalName);
			return false;
		}
		
		// 重複チェック
		if (_alreadyExists != null && !_alreadyExists.isEmpty()) {
			for (String name : _alreadyExists) {
				if (inputText.equalsIgnoreCase(name)) {
					inputField.selectAll();
					showError(inputField, CommonMessages.getInstance().FilenameValidator_MultipleName);
					return false;
				}
			}
		}
		
		// OK
		return true;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	@Override
	protected String getDefaultErrorMessage() {
		return CommonMessages.getInstance().FilenameValidator_ErrorMessage;
	}

	@Override
	protected String getDefaultErrorTitle() {
		return CommonMessages.getInstance().FilenameValidator_ErrorTitle;
	}
}
