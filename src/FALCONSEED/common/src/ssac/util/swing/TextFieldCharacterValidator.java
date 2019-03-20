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
 * @(#)TextFieldCharacterValidator.java	1.14	2009/12/09
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.swing;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;

import ssac.util.Strings;

/**
 * テキストフィールドの入力文字を監視するドキュメントフィルター。
 * ここで設定した条件で、入力内容をチェックすることも可能。
 * 
 * @version 1.14	2009/12/09
 * @since 1.14
 */
public class TextFieldCharacterValidator extends DocumentFilter
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** 処理対象のコンポーネント **/
	private JTextField		_target;
	private DocumentFilter	_oldFilter;

	/** 値が何も入力されていない場合にエラーとするフラグ **/
	private boolean	_required;
	/** 値が何も入力されていない場合に表示するエラーメッセージ **/
	private String		_requiredErrorMessage;
	/** 設定された文字セットが入力可能文字であることを示すフラグ **/
	private boolean	_charsetForValid;
	/** 入力を検証する際に使用する文字セット **/
	private Set<Character>	_charset;
	/** 入力文字に不正があった場合に表示するエラーメッセージ **/
	private String		_charsetErrorMessage;
	/** 不正な文字も入力を許可することを示すフラグ **/
	private boolean	_allowsInvalid;

	/** エラーメッセーのタイトル **/
	private String		_errTitle;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * 標準のコンストラクタ。
	 */
	public TextFieldCharacterValidator() {
		this(null);
	}
	
	public TextFieldCharacterValidator(String errorMessageTitle) {
		setErrorMessageTitle(errorMessageTitle);
	}

	//------------------------------------------------------------
	// Implements DocumentFilter interfaces
	//------------------------------------------------------------
	
	@Override
	public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
	throws BadLocationException
	{
		if (string == null) {
			return;
		} else {
			replace(fb, offset, 0, string, attr);
		}
	}

	@Override
	public void remove(FilterBypass fb, int offset, int length)
	throws BadLocationException
	{
		replace(fb, offset, length, "", null);
	}

	@Override
	public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
	throws BadLocationException
	{
		if (!getAllowsInvalid() && !Strings.isNullOrEmpty(text)) {
			int len = text.length();
			for (int i = 0; i < len; i++) {
				if (!isValidCharacter(text.charAt(i))) {
					// error
					String after = getAfterString(fb, offset, length, text);
					throw new BadLocationException(after, offset);
				}
			}
		}
		
		// ok
		fb.replace(offset, length, text, attrs);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public String getErrorMessageTitle() {
		return _errTitle;
	}
	
	public void setErrorMessageTitle(String title) {
		if (Strings.isNullOrEmpty(title)) {
			_errTitle = null;
		} else {
			_errTitle = title;
		}
	}
	
	public JTextField getTargetTextField() {
		return _target;
	}
	
	public void setTargetTextField(JTextField newTargetField) {
		if (newTargetField == _target) {
			// 同じインスタンスが設定済みなら、処理しない
			return;
		}
		
		JTextField oldTargetField = _target;
		_target = newTargetField;
		
		if (oldTargetField != null) {
			((AbstractDocument)oldTargetField.getDocument()).setDocumentFilter(_oldFilter);
		}
		
		if (newTargetField != null) {
			AbstractDocument doc = (AbstractDocument)newTargetField.getDocument();
			_oldFilter = doc.getDocumentFilter();
			doc.setDocumentFilter(this);
		} else {
			_oldFilter = null;
		}
	}
	
	public boolean getAllowsInvalid() {
		return _allowsInvalid;
	}
	
	public void setAllowsInvalid(boolean allow) {
		_allowsInvalid = allow;
	}
	
	public boolean getRequiredField() {
		return _required;
	}
	
	public void setRequiredField(boolean required) {
		_required = required;
	}
	
	public boolean isCharacterSetForValid() {
		return _charsetForValid;
	}
	
	public Set<Character> getCharacterSet() {
		return _charset;
	}
	
	public void setCharacters(boolean useForValid, char[] chars) {
		_charset = createCharacterSetFromArray(chars);
	}
	
	public void setCharacters(boolean useForValid, CharSequence chars) {
		_charset = createCharacterSetFromSequence(chars);
	}
	
	public void setCharacters(boolean useForValid, Collection<? extends Character> chars) {
		_charset = createCharacterSetFromCollection(chars);
	}
	
	public String getRequiredErrorMessage() {
		return _requiredErrorMessage;
	}
	
	public void setRequiredErrorMessage(String message) {
		_requiredErrorMessage = (Strings.isNullOrEmpty(message) ? null : message);
	}
	
	public String getCharacterSetErrorMessage() {
		return _charsetErrorMessage;
	}
	
	public void setCharacterSetErrorMessage(String message) {
		_charsetErrorMessage = (Strings.isNullOrEmpty(message) ? null : message);
	}

	public boolean verify(JTextField inputField, String inputText) {
		boolean verify = true;
		if (Strings.isNullOrEmpty(inputText)) {
			if (_required) {
				verify = false;
				showError(inputField, _requiredErrorMessage);
			}
		} else if (!_charset.isEmpty()) {
			int len = inputText.length();
			if (_charsetForValid) {
				for (int i = 0; i < len; i++) {
					if (!_charset.contains(inputText.charAt(i))) {
						// error
						verify = false;
						inputField.selectAll();
						showError(inputField, _charsetErrorMessage);
						break;
					}
				}
			} else {
				for (int i = 0; i < len; i++) {
					if (_charset.contains(inputText.charAt(i))) {
						// error
						verify = false;
						inputField.selectAll();
						showError(inputField, _charsetErrorMessage);
						break;
					}
				}
			}
		}
		return verify;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected String getDefaultErrorTitle() {
		return "Error";
	}
	
	protected String getDefaultErrorMessage() {
		return "Input error!";
	}
	
	protected void showError(String message) {
		showError(getTargetTextField(), message, getErrorMessageTitle());
	}
	
	protected void showError(String message, String title) {
		showError(getTargetTextField(), message, title);
	}
	
	protected void showError(JComponent input, String message) {
		showError(input, message, getErrorMessageTitle());
	}
	
	protected void showError(JComponent input, String message, String title) {
		String _t = (Strings.isNullOrEmpty(title) ? getDefaultErrorTitle() : title);
		String _m = (Strings.isNullOrEmpty(message) ? getDefaultErrorMessage() : message);
		JOptionPane.showMessageDialog(SwingTools.getWindowForComponent(input), _m, _t, JOptionPane.ERROR_MESSAGE);
	}
	
	protected Set<Character> createCharacterSetFromArray(char[] chars) {
		Set<Character> set;
		if (chars != null && chars.length > 0) {
			set = new HashSet<Character>();
			for (char c : chars) {
				set.add(c);
			}
		} else {
			set = Collections.emptySet();
		}
		return set;
	}
	
	protected Set<Character> createCharacterSetFromSequence(CharSequence chars) {
		Set<Character> set;
		if (chars != null && chars.length() > 0) {
			set = new HashSet<Character>();
			int len = chars.length();
			for (int i = 0; i < len; i++) {
				set.add(chars.charAt(i));
			}
		} else {
			set = Collections.emptySet();
		}
		return set;
	}
	
	protected Set<Character> createCharacterSetFromCollection(Collection<? extends Character> chars) {
		Set<Character> set;
		if (chars != null && !chars.isEmpty()) {
			set = new HashSet<Character>(chars);
		} else {
			set = Collections.emptySet();
		}
		return set;
	}
	
	protected String getAfterString(DocumentFilter.FilterBypass fb, int offset, int length, String text)
	throws BadLocationException
	{
		Document doc = fb.getDocument();
		int curlen = doc.getLength();
		String curtext = doc.getText(0, curlen);
		StringBuilder sb = new StringBuilder();
		sb.append(curtext.substring(0, offset));
		sb.append(text==null ? "" : text);
		sb.append(curtext.substring(length+offset, curlen));
		return sb.toString();
	}
	
	protected boolean isValidCharacter(char c) {
		if (_charset.isEmpty()) {
			return true;
		}
		else if (_charsetForValid) {
			return _charset.contains(c);
		}
		else {
			return !_charset.contains(c);
		}
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
