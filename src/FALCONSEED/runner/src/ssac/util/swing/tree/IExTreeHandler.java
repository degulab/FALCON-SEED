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
 *  Copyright 2007-2012  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)IExTreeHandler.java	1.20	2012/03/22
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.swing.tree;

import javax.swing.Icon;

/**
 * {@link ssac.util.swing.tree.ExTree} 用のイベントハンドラ。
 * 編集コマンドならびに描画イベントに介入するためのインタフェースを提供する。
 * 
 * @version 1.20	2012/03/22
 * @since 1.20
 */
public interface IExTreeHandler
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Interfaces
	//------------------------------------------------------------
	
	/**
	 * ツリーのアクションが実行されたときに呼び出される。
	 * @param tree		呼び出し元のツリーオブジェクト
	 * @param cmmandKey	呼び出されたときのコマンドキー
	 */
	public void onTreeEditActionPerformed(ExTree tree, String commandKey);

	/**
	 * {@link ssac.util.swing.tree.ExTree#convertValueToText(Object, boolean, boolean, boolean, int, boolean)} メソッドから
	 * 最初に呼び出されるインタフェース。このインタフェースが <tt>null</tt> 以外の値を返した場合、その値が使用される。
	 * <tt>null</tt> を返した場合は、{@link ssac.util.swing.tree.ExTree} 標準の値が使用される。
	 * @param tree		呼び出し元のツリーオブジェクト
	 * @param value		テキストに変換する <code>Object</code>
	 * @param selected	ノードが選択されている場合は <tt>true</tt>
	 * @param expanded	ノードが展開されている場合は <tt>true</tt>
	 * @param leaf		ノードが葉ノードの場合は <tt>true</tt>
	 * @param row		ノードの表示行を指定する <code>int</code> 値。0 は最初の行
	 * @param hasFocus	ノードがフォーカスを持つ場合は <tt>true</tt>
	 * @return	ノードの値の <code>String</code> 表現
	 */
	public String onConvertValueToText(ExTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus);

	/**
	 * {@link ssac.util.swing.tree.ExTree#convertValueToIcon(Object, boolean, boolean, boolean, int, boolean)} メソッドから
	 * 最初に呼び出されるインタフェース。このインタフェースが <tt>null</tt> 以外の値を返した場合、その値が使用される。
	 * <tt>null</tt> を返した場合は、{@link ssac.util.swing.tree.ExTree} 標準の値が使用される。
	 * @param tree		呼び出し元のツリーオブジェクト
	 * @param value		対象の値を示す <code>Object</code>
	 * @param selected	ノードが選択されている場合は <tt>true</tt>
	 * @param expanded	ノードが展開されている場合は <tt>true</tt>
	 * @param leaf		ノードが葉ノードの場合は <tt>true</tt>
	 * @param row		ノードの表示行を指定する <code>int</code> 値。0 は最初の行
	 * @param hasFocus	ノードがフォーカスを持つ場合は <tt>true</tt>
	 * @return	<code>Icon</code> オブジェクト。デフォルトのアイコン表示のままとする場合は <tt>null</tt> を返す。
	 */
	
	public Icon onConvertValueToIcon(ExTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus);

	/**
	 * {@link ssac.util.swing.tree.ExTree#getToolTipText(Object, boolean, boolean, boolean, int, boolean)} メソッドから
	 * 最初に呼び出されるインタフェース。このインタフェースが <tt>null</tt> 以外の値を返した場合、その値が使用される。
	 * <tt>null</tt> を返した場合は、{@link ssac.util.swing.tree.ExTree} 標準の値が使用される。
	 * @param tree		呼び出し元のツリーオブジェクト
	 * @param value		対象の値を示す <code>Object</code>
	 * @param selected	ノードが選択されている場合は <tt>true</tt>
	 * @param expanded	ノードが展開されている場合は <tt>true</tt>
	 * @param leaf		ノードが葉ノードの場合は <tt>true</tt>
	 * @param row		ノードの表示行を指定する <code>int</code> 値。0 は最初の行
	 * @param hasFocus	ノードがフォーカスを持つ場合は <tt>true</tt>
	 * @return	ツールチップ文字列。ツールチップを表示しない場合は <tt>null</tt>
	 */
	public String onGetToolTipText(ExTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus);
}
