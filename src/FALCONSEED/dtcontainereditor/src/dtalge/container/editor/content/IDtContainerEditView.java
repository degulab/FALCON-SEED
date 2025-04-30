/*
 * @(#)IDtContainerEditView.java	1.0.0	2022/12/21
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge.container.editor.content;

import java.awt.Font;
import java.io.File;
import java.io.IOException;

import javax.swing.JComponent;

import dtalge.container.editor.content.common.table.DTCContentEditTableModelConversionError;
import dtalge.container.editor.menu.DtContainerEditorMenuBar;
import dtalge.container.editor.view.DtContainerEditorFrame;
import ssac.util.swing.menu.IMenuActionHandler;

/**
 * データコンテナの編集用GUIを提供するビューのインターフェース。
 * 
 * @version 1.0.0
 */
public interface IDtContainerEditView extends IMenuActionHandler
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	/**
	 * エディタの変更状態を示すフラグを格納するプロパティ名
	 */
	static public final String PROP_MODIFIED	= "DCEditor.modified";
	/**
	 * エディタドキュメントの選択状態を示すフラグを格納するプロパティ名
	 */
	static public final String PROP_SELECTED	= "DCEditor.selection";

	//------------------------------------------------------------
	// Basic interfaces
	//------------------------------------------------------------

	/**
	 * このビューオブジェクトに関連付けられている全てのリソースを開放する。
	 */
	public void destroy();

	/**
	 * このドキュメントの保存先ファイルが読み取り専用の場合に <code>true</code> を返す。
	 * ファイルそのものが読み取り専用ではない場合でも、モジュールパッケージに
	 * 含まれるファイルの場合にも <code>true</code> を返す。
	 */
	public boolean isReadOnly();
	/**
	 * このドキュメントが編集されているかを判定する。
	 * @return	編集されていれば <tt>true</tt> を返す。
	 */
	public boolean isModified();
	/**
	 * このビューのドキュメントの変更状態に合わせて、変更を管理するプロパティを更新する。
	 * プロパティの値が変更されると、{@link IDtContainerEditView#PROP_MODIFIED} プロパティ変更イベントが発生する。
	 */
	public void updateEditorModifiedProperty();
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
	public IDtContainerDocument getDocument();
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
	public DtContainerEditorFrame getFrame();
	/**
	 * このドキュメントを管理するエディット・コントローラーを返す。
	 * @return	エディット・コントローラー
	 */
	public IDtContainerEditController getEditController();
	/**
	 * このビューに関連付けられたドキュメント専用のメニューバーを返す。
	 * 専用メニューバーが未定義の場合は <tt>null</tt> を返す。
	 * @return	ドキュメント専用メニューバー
	 */
	public DtContainerEditorMenuBar getDocumentMenuBar();
	/**
	 * このエディタビューのフォント変更要求を処理する。
	 * このメソッドは、{@link IDtContainerEditController} から呼び出される。
	 * @param controller	このメソッドの呼び出し元となるマネージャ
	 * @param font	新しいエディタフォント
	 */
	public void onChangedEditorFont(IDtContainerEditController controller, Font font);

	//------------------------------------------------------------
	// Editing interfaces
	//------------------------------------------------------------

	/**
	 * ドキュメントの保存先ファイルが移動可能かを判定する。
	 * @return	移動可能な場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
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
	 */
	public boolean cachedDocumentFromSourceFile();
	/**
	 * ドキュメントの内容をソースファイルから再読込する。
	 * 再読込時の設定は、ドキュメント読込時点の設定と同じ内容とする。
	 * @throws IOException	入出力エラーが発生した場合
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

	/**
	 * <em>cause</em> が示す値をビューで表示し、選択する。
	 * @param cause	エラー情報を保持する {@link DTCContentEditTableModelConversionError} 例外オブジェクト
	 */
	public void visibleAndSelectInvalidValueCell(DTCContentEditTableModelConversionError cause);
}
