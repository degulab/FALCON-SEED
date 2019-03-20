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
 * @(#)IComponentManager.java	1.00	2010/12/20
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.editor.plugin;

import java.awt.Component;
import java.awt.Font;
import java.io.File;
import java.io.IOException;

import javax.swing.Icon;

import ssac.falconseed.editor.document.IEditorDocument;
import ssac.falconseed.editor.view.IEditorView;
import ssac.util.io.ExtensionFileFilter;
import ssac.util.swing.menu.IMenuActionHandler;

/**
 * プラグインのコンポーネント・マネージャ・インタフェース。
 * 
 * @version 1.00	2010/12/20
 */
public interface IComponentManager extends IMenuActionHandler
{
	/**
	 * ファイル・オープン・ダイアログ用ファイルフィルタを返す。
	 * @return 定義されているファイルフィルタの配列を返す。未定義の場合は <tt>null</tt> を返す。
	 */
	public ExtensionFileFilter[] getOpenFileFilters();
	/**
	 * ファイル・セーブ・ダイアログ用ファイルフィルタを返す。
	 * @return	定義されているファイルフィルタの配列を返す。未定gの場合は <tt>null</tt> を返す。
	 */
	public ExtensionFileFilter[] getSaveFileFilters();
	/**
	 * このマネージャのインタフェースで、最後に選択されたファイルを返す。
	 * @return	ファイルオブジェクトを返す。未選択の場合は <tt>null</tt> を返す。
	 */
	public File getLastSelectedFile();
	/**
	 * このマネージャにおける、直前の選択ファイルを設定する。
	 * ここで指定したファイルは、ファイルダイアログを表示する際の初期位置となる。
	 * @param file	初期位置とするファイルオブジェクト
	 */
	public void setLastSelectedFile(File file);
	/**
	 *  このプラグインの識別子を返す。
	 *  この識別子は、メニューの新規作成や形式指定でファイルを開く場合に、
	 *  コンポーネントを判別するための識別子となる。
	 */
	public String getID();
	/**
	 * このプラグインの名称を返す。
	 * @return	プラグイン名
	 */
	public String getName();
	/**
	 * このプラグインの説明を返す。
	 * @return	プラグインの説明
	 */
	public String getDescription();
	/**
	 * このプラグインの表示用アイコンを返す。
	 * アイコンが設定されていない場合は <tt>null</tt> を返す。
	 * @return	表示用アイコン
	 */
	public Icon getDisplayIcon();
	/**
	 * このプラグインのデータ新規作成時の表示用アイコンを返す。
	 * アイコンが設定されていない場合は <tt>null</tt> を返す。
	 * @return	新規作成時の表示用アイコン
	 */
	public Icon getDisplayNewIcon();
	/**
	 * このプラグインの標準エディタフォントを返す。
	 * このメソッドは、アプリケーション設定などで呼び出される。
	 * @return 標準のエディタフォント
	 */
	public Font getDefaultEditorFont();
	/**
	 * このプラグインの、現在のエディタフォントを取得する。
	 * @return	現在のエディタフォント
	 */
	public Font getEditorFont();
	/**
	 * このプラグインのエディタフォントを設定する。
	 * 現在のフォントと異なるフォントが設定された場合、このマネージャが管理する
	 * 全てのビューに新しいフォントが適用される。
	 * @param font	新しいエディタフォント
	 * @return	新しいフォントが適用された場合に <tt>true</tt> を返す。
	 * 			変更されなかった場合は <tt>false</tt> を返す。
	 * @throws NullPointerException	<code>font</code> が <tt>null</tt> の場合
	 */
	public boolean setEditorFont(Font font);
	/**
	 * このプラグインのドキュメントが、新規作成可能なドキュメントかを取得する。
	 * @return	新規作成が許可されているドキュメントであれば <tt>true</tt>、それ以外の場合は <tt>false</tt> を返す。
	 */
	public boolean isAllowCreateNewDocument();
	/**
	 * 指定されたファイルが、このマネージャでサポートされているかを判定する。
	 * 基本的には、ファイル拡張子で判定する。このインタフェースを実装により、
	 * ファイルの内容によって判定する場合もある。
	 * 
	 * @param targetFile	判定するファイル
	 * @return	サポートしている場合は <tt>true</tt>
	 */
	public boolean isSupportedFileType(File targetFile);
	/**
	 * このマネージャがサポートするドキュメントのクラスを取得する。
	 * @return	マネージャがサポートするドキュメントの <code>Class</code> インスタンス
	 */
	public Class<?> getSupportedDocumentClass();
	/**
	 * このマネージャで読み込み可能なファイルの種別を表すフィルタを取得する。
	 * @return	読み込み用 <code>{@link ssac.aadl.editor.plugin.core.util.io.ExtensionFileFilter}</code> オブジェクト
	 */
	public ExtensionFileFilter getSupportedFileFilter();
	/**
	 * 指定のドキュメントに対応するビューを取得する。
	 * @param document	ドキュメント
	 * @return	ドキュメントに関連付けられたビュー
	 */
	public IEditorView getView(IEditorDocument document);
	/**
	 * 指定されたドキュメントをマネージャから削除する。
	 * このメソッドは、マネージャが管理するドキュメントとビューの関連付けを解除する。
	 * @param document	削除するドキュメント
	 * @return	削除したドキュメントに関連付けられていたビューを返す。指定したドキュメントが
	 * 			マネージャに存在しない場合は <tt>null</tt> を返す。
	 */
	public IEditorView removeDocument(IEditorDocument document);
	/**
	 * 指定されたビューに関連付けられているドキュメントをマネージャから削除する。
	 * このメソッドは、マネージャが管理するドキュメントとビューの関連付けを解除する。
	 * @param view	削除するドキュメントを保持するビュー
	 * @return	ドキュメントを削除した場合は <tt>true</tt> を返す。
	 */
	public boolean removeDocument(IEditorView view);
	/**
	 * 指定されたファイルを保存先とする新規ドキュメントを生成する。
	 * <em>templateText</em> が有効な文字列の場合、その文字列を内容とする
	 * 新しいファイルが作成される。ただし、内容についてはこのメソッド内で検証しない。
	 * <em>templateText</em> が <tt>null</tt> もしくは空文字列の場合は、この
	 * コンポーネント標準の内容を持つファイルが作成される。
	 * なお、指定されたファイルがすでに存在する場合は、ここで生成された内容で
	 * 強制的に上書きされる。
	 * 
	 * @param targetFile	新規ドキュメントの保存先とするファイル
	 * @return	新規ドキュメントに関連付けられたビューを返す。
	 * @throws IOException	ファイルの作成に失敗した場合
	 */
	public IEditorView newDocument(File targetFile, String templateText) throws IOException;
	/**
	 * 指定されたファイルの内容を格納するドキュメントを生成する。
	 * @param parentComponent	メソッド内部でウィンドウを表示する際の親となるコンポーネント
	 * @param targetFile	ドキュメントの内容となるファイル
	 * @return	ドキュメントに関連付けられたビューを返す。
	 * @throws NullPointerException	<code>targetFile</code> が <tt>null</tt> の場合
	 * @throws IOException	ファイルの読み込みに失敗した場合
	 */
	public IEditorView openDocument(Component parentComponent, File targetFile) throws IOException;
	/**
	 * 新規ドキュメントを生成し、ドキュメントが関連付けられたビューを返す。
	 * このメソッド内では必要に応じてファイル選択ダイアログを表示する。
	 * 
	 * @param parentComponent	メソッド内部でウィンドウを表示する際の親となるコンポーネント
	 * @return	ドキュメントが関連付けられたビュー
	 */
	public IEditorView onNewComponent(Component parentComponent);
	/**
	 * ファイルからドキュメントを生成し、ドキュメントが関連付けられたビューを返す。
	 * ファイル選択ダイアログは、このメソッド内で表示する。
	 * 
	 * @param parentComponent	メソッド内部でウィンドウを表示する際の親となるコンポーネント
	 * @return	ドキュメントが関連付けられたビュー
	 */
	//public IEditorView onOpenComponent(Component parentComponent) throws IOException;
	/**
	 * 指定されたビューに関連付けられたドキュメントを既存のファイルに上書き保存する。
	 * 保存先ファイルが設定されていない場合は例外をスローする。
	 * @param parentComponent	メソッド内部でウィンドウを表示する際の親となるコンポーネント
	 * @param targetView	保存対象のドキュメントが関連付けられたビュー
	 * @return	保存に成功した場合は <tt>true</tt>、それ以外の場合は <tt>false</tt> を返す。
	 * @throws IllegalArgumentException	ドキュメントが関連付けられていない場合、
	 * 										もしくは保存先ファイルが設定されていない場合
	 */
	public boolean onSaveComponent(Component parentComponent, IEditorView targetView);
	/**
	 * 指定されたビューに関連付けられたドキュメントを任意のファイルに保存する。
	 * ファイル選択ダイアログは、このメソッド内で表示する。
	 * ドキュメントが関連付けられていない場合は例外をスローする。
	 * @param parentComponent	メソッド内部でウィンドウを表示する際の親となるコンポーネント
	 * @param targetView	保存対象のドキュメントが関連付けられたビュー
	 * @return	保存に成功した場合は <tt>true</tt>、それ以外の場合は <tt>false</tt> を返す。
	 * @throws IllegalArgumentException	ドキュメントが関連付けられていない場合
	 */
	public boolean onSaveAsComponent(Component parentComponent, IEditorView targetView);
}
