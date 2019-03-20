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
 * @(#)ITextComponent.java	1.00	2008/03/24
 *     - created by Y.Ishizuka(PieCake.inc,)
 * @(#)ITextComponent.java	1.10	2008/12/02
 *     - modified by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.editor.view.text;

import javax.swing.text.JTextComponent;

/**
 * テキスト・コンポーネント共通の機能を定義するインタフェース。
 * 
 * @version 1.10	2008/12/02
 */
public interface ITextComponent
{
	/**
	 * このエディタが保持するドキュメントの編集中／未編集の状態を保持するプロパティ。
	 */
	static public final String PROP_MODIFIED	= "Editor.modified";
	/**
	 * このエディタが保持するドキュメントの選択状態を保持するプロパティ。
	 */
	static public final String PROP_SELECTED	= "Editor.selection";

	/**
	 * テキストコンポーネントのドキュメントが変更されているかを取得する。
	 * 
	 * @return 変更されていれば true
	 */
	public boolean isModified();

	/**
	 * テキストコンポーネントが編集可能であるかを検証する。
	 * 
	 * @return 編集可能であれば true
	 */
	public boolean canEdit();

	/**
	 * テキストコンポーネントで選択されているテキストが
	 * 存在するかを検証する。
	 * 
	 * @return 選択テキストが存在していれば true
	 */
	public boolean hasSelectedText();

	/**
	 * テキストコンポーネントが Undo 可能かを検証する。
	 * 
	 * @return Undo 可能なら true
	 */
	public boolean canUndo();

	/**
	 * テキストコンポーネントが Redo 可能かを検証する。
	 * 
	 * @return Redo 可能なら true
	 */
	public boolean canRedo();

	/**
	 * テキストコンポーネントが Cut 可能かを検証する。
	 * 
	 * @return Cut 可能なら true
	 */
	public boolean canCut();

	/**
	 * クリップボードにペースト可能なデータが存在し、
	 * テキストコンポーネントにペースト可能な状態かを検証する。
	 * 
	 * @return ペースト可能なら true
	 */
	public boolean canPaste();

	/**
	 * テキストコンポーネントを取得する。
	 * 
	 * @return JTextComponent インスタンス
	 */
	public JTextComponent getTextComponent();

	/**
	 * テキストコンポーネントに対して Undo する。
	 */
	public void undo();

	/**
	 * テキストコンポーネントに対して Redo する。
	 */
	public void redo();

	/**
	 * 選択テキストのカット
	 */
	public void cut();

	/**
	 * 選択テキストのコピー
	 */
	public void copy();

	/**
	 * 現在のキャレット位置にペースト
	 */
	public void paste();

	/**
	 * 選択テキストのデリート
	 */
	public void delete();

	/**
	 * テキストコンポーネントの全てのテキストを選択する。
	 */
	public void selectAll();

	/**
	 * テキストコンポーネントの行数を取得する。
	 * 
	 * @return テキストコンポーネントの行数
	 */
	public int getLineCount();

	/**
	 * キャレットをドキュメントの先頭に移動する。
	 */
	public void jumpToBegin();

	/**
	 * キャレットをドキュメントの終端に移動する。
	 *
	 */
	public void jumpToEnd();

	/**
	 * キャレットを指定行の先頭に移動する。
	 * 行番号は、先頭行を 1 とする。
	 * 
	 * @param lineNo 1 から始まる行番号
	 */
	public void jumpToLine(int lineNo);

	/**
	 * 所属するテキストコンポーネントにフォーカスを設定する。
	 *
	 */
	public void requestFocusInTextComponent();
}
