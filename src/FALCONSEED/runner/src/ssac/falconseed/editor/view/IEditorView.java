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
 *  Copyright 2007-2011  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)IEditorView.java	1.10	2011/02/14
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)IEditorView.java	1.00	2010/12/20
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.editor.view;

import java.awt.Font;
import java.io.File;
import java.io.IOException;

import javax.swing.JComponent;

import ssac.falconseed.editor.document.IEditorDocument;
import ssac.falconseed.editor.plugin.IComponentManager;
import ssac.falconseed.editor.view.dialog.AbFindReplaceHandler;
import ssac.falconseed.runner.menu.RunnerMenuBar;
import ssac.falconseed.runner.view.RunnerFrame;
import ssac.util.swing.menu.IMenuActionHandler;

/**
 * エディタ・ビュー共通インタフェース。
 * 
 * @version 1.10	2011/02/14
 */
public interface IEditorView extends IMenuActionHandler
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	/**
	 * エディタの変更状態を示すフラグを格納するプロパティ名
	 */
	static public final String PROP_MODIFIED	= "Editor.modified";
	/**
	 * エディタドキュメントの選択状態を示すフラグを格納するプロパティ名
	 */
	static public final String PROP_SELECTED	= "Editor.selection";

	//------------------------------------------------------------
	// Basic interfaces
	//------------------------------------------------------------

	/**
	 * このビューオブジェクトに関連付けられている全てのリソースを開放する。
	 */
	public void destroy();

	/**
	 * このドキュメントの保存先ファイルが読み取り専用の場合に <tt>true</tt> を返す。
	 * ファイルそのものが読み取り専用ではない場合でも、モジュールパッケージに
	 * 含まれるファイルの場合にも <tt>true</tt> を返す。
	 */
	public boolean isReadOnly();
	/**
	 * このドキュメントが編集されているかを判定する。
	 * @return	編集されていれば <tt>true</tt> を返す。
	 */
	public boolean isModified();
	/**
	 * このビューに関連付けられているドキュメントのタイトルを取得する。
	 * ここで取得されたタイトル文字列は、エディタフレームのタイトル、
	 * エディタタブのテキストとして使用される。
	 * @return	タイトル文字列
	 */
	public String getDocumentTitle();
	/**
	 * このビューに関連付けられているドキュメントの保存先ファイルを取得する。
	 * 保存先ファイルが定義されていない場合は <tt>null</tt> を返す。
	 * @return	ドキュメントの保存先ファイルを返す。保存先ファイルが設定されて
	 * 			いない場合は <tt>null</tt> を返す。
	 */
	public File getDocumentFile();
	/**
	 * このビューに関連付けられているドキュメントの保存先ファイルのフルパスを取得する。
	 * ここで取得された文字列は、ドキュメントの正式名称として、
	 * エディタフレームやエディタタブのツールチップとして使用される。
	 * @return	ドキュメント保存先ファイルのフルパスを返す。保存先ファイルが
	 * 			設定されていない場合は <tt>null</tt> を返す。
	 */
	public String getDocumentPath();
	/**
	 * このビューに関連付けられているドキュメントを取得する。
	 * @return	このビューに関連付けられているドキュメント	
	 */
	public IEditorDocument getDocument();
	/**
	 * このビューに関連付けられたドキュメントの設定情報を、
	 * 最新の情報に更新する。
	 * @return	更新された場合に <tt>true</tt> を返す。
	 */
	public boolean refreshDocumentSettings();
	/**
	 * このビューのコンポーネントを取得する。
	 * このメソッドは基本的に <code>this</code> インスタンスを返す。
	 * @return	このビューのコンポーネントオブジェクト
	 */
	public JComponent getComponent();
	/**
	 * このビューのコンポーネントがフォーカスを保持しているかを判定する。
	 * ビューが複数のコンポーネントを持つ場合、フォーカスを保持するべき
	 * コンポーネントにフォーカスがあれば <tt>true</tt> を返す。
	 * @return	コンポーネントがフォーカスを所持していれば <tt>true</tt>
	 */
	public boolean hasFocusInComponent();
	/**
	 * このビューのコンポーネントにフォーカスを要求する。
	 * ビューが複数のコンポーネントを持つ場合、標準となる
	 * コンポーネントにフォーカスを設定する。
	 */
	public void requestFocusInComponent();
	/**
	 * このビューを格納するエディタフレームを取得する。
	 * @return	このビューを格納するエディタフレームのインスタンス。
	 * 			このビューがフレームに格納されていない場合は <tt>null</tt> を返す。
	 */
	public RunnerFrame getFrame();
	/**
	 * このドキュメントを管理するコンポーネント・マネージャを返す。
	 * @return	コンポーネント・マネージャ
	 */
	public IComponentManager getManager();
	/**
	 * このビューに関連付けられたドキュメント専用のメニューバーを返す。
	 * 専用メニューバーが未定義の場合は <tt>null</tt> を返す。
	 * @return	ドキュメント専用メニューバー
	 */
	public RunnerMenuBar getDocumentMenuBar();
	/**
	 * このエディタビューのフォント変更要求を処理する。
	 * このメソッドは、{@link IComponentManager} から呼び出される。
	 * @param manager	このメソッドの呼び出し元となるマネージャ
	 * @param font	新しいエディタフォント
	 */
	public void onChangedEditorFont(IComponentManager manager, Font font);

	//------------------------------------------------------------
	// Editing interfaces
	//------------------------------------------------------------

	/**
	 * ドキュメントの保存先ファイルが移動可能かを判定する。
	 * @return	移動可能な場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @since 1.10
	 */
	public boolean canMoveDocumentFile();
	/**
	 * ドキュメントに適用されたエンコーディングとなる文字セット名を返す。
	 * このメソッドの実装では <tt>null</tt> を返してはならない。
	 */
	public String getLastEncodingName();
	/**
	 * ドキュメントのソースファイルの内容がすべてキャッシュされているかを判定する。
	 * キャッシュされている場合は、ソースファイルが変更されていても表示内容の
	 * 影響を受けない。
	 * @return	キャッシュされている場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @since 1.10
	 */
	public boolean cachedDocumentFromSourceFile();
	/**
	 * ドキュメントの内容をソースファイルから再読込する。
	 * 再読込時の設定は、ドキュメント読込時点の設定と同じ内容とする。
	 * @throws IOException	入出力エラーが発生した場合
	 * @since 1.10
	 */
	public void refreshDocumentFromSourceFile() throws IOException;
	/**
	 * エンコーディングを指定してドキュメントを開きなおす操作を許可するかを判定する。
	 * @return 許可する場合は <tt>true</tt>、そうでない場合は <tt>false</tt> を返す。
	 */
	public boolean canReopen();
	/**
	 * 指定されたエンコーディングでドキュメントを開きなおす。
	 * この操作では、編集状態は破棄され、<em>newEncoding</em> を適用して
	 * ファイルから読み込む。
	 * 新規ドキュメントの場合は、何もしない。
	 * @param newEncoding	ファイル読み込み時に適用するエンコーディング名を指定する。
	 * 						標準のエンコーディングを適用する場合は <tt>null</tt> を指定する。
	 * @throws IOException	入出力エラーが発生した場合
	 */
	public void reopenDocument(String newEncoding) throws IOException;
	
	/**
	 * このビューに関連付けられたドキュメントの状態から、
	 * このエディタの編集状態を更新する。
	 */
	public void refreshEditingStatus();

	//------------------------------------------------------------
	// Find/Replace interfaces
	//------------------------------------------------------------

	/**
	 * このビューで検索／置換が可能かを判定する。このインタフェースは、
	 * フレームワークの検索／置換処理から呼び出される。
	 * @return このビューで検索／置換が可能な場合は <tt>true</tt> を返す。
	 */
	public boolean canFindReplace();
	/**
	 * 検索／置換処理のイベントハンドラを返す。
	 * このインタフェースが返すハンドラは、フレームワークの検索／置換ダイアログに
	 * 適用されるインタフェースであり、検索／置換ダイアログ、もしくは前方／後方
	 * 検索メニューにより実行される。
	 * @return	このビューの検索／置換処理イベントハンドラを返す。
	 * 			このビューが検索／置換に対応していない場合は <tt>null</tt> を返す。
	 */
	public AbFindReplaceHandler getFindReplaceHandler();
}
