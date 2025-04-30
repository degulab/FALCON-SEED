/*
 * @(#)DtContainerContentNameValidator.java	1.1.0	2023/01/28
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)DtContainerContentNameValidator.java	1.0.0	2022/12/15
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge.container.editor.content.common.swing;

import java.util.Set;

import javax.swing.JTextField;

import dtalge.container.editor.DtContainerEditorMessages;
import dtalge.container.editor.content.common.tree.DtContainerContentParentTreeNode;
import ssac.util.swing.TextFieldCharacterValidator;

/**
 * データコンテナ要素の名前付きオブジェクトの名前をチェックするバリデータ。
 * 特に禁止文字は設けないが、同レベル(兄弟ノード)での重複する名前の入力を禁止する。
 * 
 * @version 1.1.0
 */
public class DtContainerContentNameValidator extends TextFieldCharacterValidator
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	/** オブジェクト名を持つノードの親ノード、この親ノード内の子ノードと同じ名前は拒否、この条件で判定しない場合は {@code null} **/
	private DtContainerContentParentTreeNode	_namedParent;
	/** すでに存在しているオブジェクト名、この条件で判定しない場合は {@code null} **/
	private Set<String>	_existNames;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public DtContainerContentNameValidator() {
		this((DtContainerContentParentTreeNode)null);
	}
	
	public DtContainerContentNameValidator(Set<String> existNames) {
		_namedParent = null;
		_existNames = existNames;
		
		setAllowsInvalid(true);	// 禁止文字無効
		setRequiredField(true);	// 空文字列禁止
		setRequiredErrorMessage(DtContainerEditorMessages.getInstance().ObjectNameValidator_Empty);
	}
	
	public DtContainerContentNameValidator(DtContainerContentParentTreeNode namedNodesParent) {
		_namedParent = namedNodesParent;
		_existNames = null;
		
		setAllowsInvalid(true);	// 禁止文字無効
		setRequiredField(true);	// 空文字列禁止
		setRequiredErrorMessage(DtContainerEditorMessages.getInstance().ObjectNameValidator_Empty);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	@Override
	public boolean verify(JTextField inputField, String inputText) {
		// 基本処理
		if (!super.verify(inputField, inputText)) {
			return false;
		}
		
		// 重複チェック
		if (_namedParent != null) {
			int sortedIndex = _namedParent.findSortedPositionByName(inputText);
			if (sortedIndex >= 0) {
				// already exist
				showError(inputField, DtContainerEditorMessages.getInstance().ObjectNameValidator_AlreadyExists);
				return false;
			}
		}
		else if (_existNames != null && !_existNames.isEmpty()) {
			if (_existNames.contains(inputText)) {
				// already exist
				showError(inputField, DtContainerEditorMessages.getInstance().ObjectNameValidator_AlreadyExists);
				return false;
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
		return DtContainerEditorMessages.getInstance().ObjectNameValidator_DefaultError;
	}

	@Override
	protected String getDefaultErrorTitle() {
		return DtContainerEditorMessages.getInstance().ObjectNameValidator_ErrorTitle;
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
