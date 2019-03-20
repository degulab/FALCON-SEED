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
 * @(#)SourceView.java	1.17	2011/02/02
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)SourceView.java	1.10	2008/12/02
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.editor.plugin.source;

import java.io.File;
import java.io.IOException;

import javax.swing.Action;

import ssac.aadl.editor.view.menu.EditorMenuResources;
import ssac.aadl.editor.view.text.AbTextEditorView;
import ssac.util.Strings;

/**
 * AADLソース専用ビュー。
 * 
 * @version 1.17	2011/02/02
 *
 * @since 1.10
 */
public class SourceView extends AbTextEditorView<SourceModel>
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	protected SourceView(SourceModel document) {
		super(document);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	/**
	 * ドキュメントのソースファイルの内容がすべてキャッシュされているかを判定する。
	 * キャッシュされている場合は、ソースファイルが変更されていても表示内容の
	 * 影響を受けない。
	 * @return	キャッシュされている場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @since 1.17
	 */
	public boolean cachedDocumentFromSourceFile() {
		return true;
	}
	
	/**
	 * ドキュメントの内容をソースファイルから再読込する。
	 * 再読込時の設定は、ドキュメント読込時点の設定と同じ内容とする。
	 * @throws IOException	入出力エラーが発生した場合
	 * @since 1.17
	 */
	public void refreshDocumentFromSourceFile() throws IOException
	{
		reopenDocument(getLastEncodingName());
	}
	
	/**
	 * エンコーディングを指定してドキュメントを開きなおす操作を許可するかを判定する。
	 * @return 許可する場合は <tt>true</tt>、そうでない場合は <tt>false</tt> を返す。
	 */
	public boolean canReopen() {
		SourceModel oldModel = getDocument();
		if (oldModel == null)
			return false;
		if (oldModel.isNewDocument())
			return false;
		if (oldModel.getTargetFile() == null)
			return false;
		
		return true;
	}
	
	/**
	 * 指定されたエンコーディングでドキュメントを開きなおす。
	 * この操作では、編集状態は破棄され、<em>newEncoding</em> を適用して
	 * ファイルから読み込む。
	 * 新規ドキュメントの場合は、何もしない。
	 * @param newEncoding	ファイル読み込み時に適用するエンコーディング名を指定する。
	 * 						標準のエンコーディングを適用する場合は <tt>null</tt> を指定する。
	 * @throws IOException	入出力エラーが発生した場合
	 */
	public void reopenDocument(String newEncoding) throws IOException
	{
		SourceModel oldModel = getDocument();
		if (oldModel==null || oldModel.isNewDocument() || oldModel.getTargetFile()==null) {
			return;		// cannot reopen
		}
		
		SourceComponentManager manager = (SourceComponentManager)getManager();
		File docFile = oldModel.getTargetFile();
		
		// 新しいドキュメントを生成
		if (Strings.isNullOrEmpty(newEncoding)) {
			newEncoding = null;
		}
		SourceModel newModel = new SourceModel(manager, docFile, newEncoding);
		
		// 新しいドキュメントを適用
		manager.removeDocument(oldModel);
		manager.putDocumentView(newModel, this);
		setDocument(newModel);
	}

	//------------------------------------------------------------
	// Implement IEditorMenuActionHandler interfaces
	//------------------------------------------------------------

	@Override
	public boolean onProcessMenuSelection(String command, Object source, Action action) {
		return super.onProcessMenuSelection(command, source, action);
	}

	@Override
	public boolean onProcessMenuUpdate(String command, Object source, Action action) {
		if (EditorMenuResources.ID_BUILD_COMPILE.equals(command)) {
			action.setEnabled(true);
			return true;
		}
		else if (EditorMenuResources.ID_BUILD_RUN.equals(command)) {
			action.setEnabled(true);
			return true;
		}
		else if (EditorMenuResources.ID_BUILD_COMPILE_RUN.equals(command)) {
			action.setEnabled(true);
			return true;
		}
		else if (EditorMenuResources.ID_BUILD_OPTION.equals(command)) {
			action.setEnabled(true);
			return true;
		}
		
		// 標準の更新処理
		return super.onProcessMenuUpdate(command, source, action);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
}
