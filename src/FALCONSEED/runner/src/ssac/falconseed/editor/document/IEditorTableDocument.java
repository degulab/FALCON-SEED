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
 *  Copyright 2007-2010  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)IEditorTableDocument.java	1.00	2010/12/20
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.editor.document;

import javax.swing.event.UndoableEditListener;
import javax.swing.table.TableModel;

/**
 * エディタで利用可能なテーブルエディタの共通ドキュメントインタフェース。
 * 
 * @version 1.00	2010/12/20
 */
public interface IEditorTableDocument extends TableModel, IEditorDocument
{
	/**
	 * 任意の変更を通知するアンドゥリスナーを追加する。
	 * <code>UndoableEdit</code> で実行される「元に戻す/再実行」操作は、
	 * 適切な <code>DocumentEvent</code> を発生させて、ビューをモデルと
	 * 同期させる。
	 * 
	 * @param listener	追加する <code>UndoableEditListener</code>
	 */
	public void addUndoableEditListener(UndoableEditListener listener);

	/**
	 * アンドゥリスナーを削除する。
	 * 
	 * @param listener	削除する <code>UndoableEditListener</code>
	 */
	public void removeUndoableEditListener(UndoableEditListener listener);
}
