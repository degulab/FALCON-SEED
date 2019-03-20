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
 * @(#)MacroComponentManager.java	1.16	2010/09/27
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MacroComponentManager.java	1.14	2009/12/09
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MacroComponentManager.java	1.10	2008/12/12
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.editor.plugin.macro;

import java.awt.Component;
import java.awt.Font;
import java.io.File;
import java.io.IOException;

import javax.swing.Icon;

import ssac.aadl.common.CommonResources;
import ssac.aadl.editor.plugin.AbComponentManager;
import ssac.aadl.editor.setting.AppSettings;
import ssac.aadl.macro.file.CsvMacroFiles;
import ssac.util.io.ExtensionFileFilter;
import ssac.util.swing.table.SpreadSheetTable;

/**
 * AADLマクロのドキュメントとビューを管理するマネージャ。
 * 
 * @version 1.16	2010/09/27
 *
 * @since 1.10
 */
public class MacroComponentManager extends AbComponentManager
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static private final String PluginID = "AADLMacro";

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	// * AADLマクロファイルのテキスト・エンコーディング名
	//private String csvEncoding = null;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public MacroComponentManager() {
		super();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public String getDefaultFileEncoding() {
		//return AADLMacroEngine.getDefaultMacroEncoding();
		return AppSettings.getInstance().getDefaultAadlMacroEncodingName();
	}
	
	public String getFileEncoding() {
		/*
		if (Strings.isNullOrEmpty(csvEncoding))
			return getDefaultFileEncoding();
		else
			return csvEncoding;
		*/
		return AppSettings.getInstance().getAadlMacroEncodingName();
	}

	/*
	public void setFileEncoding(String charsetName) {
		this.csvEncoding = charsetName;
	}
	*/

	//------------------------------------------------------------
	// Implement IComponentManager interfaces
	//------------------------------------------------------------
	
	public String getID() {
		return PluginID;
	}
	
	public String getName() {
		return MacroMessages.getInstance().pluginName;
	}
	
	public String getDescription() {
		return MacroMessages.getInstance().pluginDesc;
	}
	
	public Icon getDisplayIcon() {
		return CommonResources.ICON_MACROFILE;
	}
	
	public Icon getDisplayNewIcon() {
		return CommonResources.ICON_NEW_MACROFILE;
	}
	
	public Class<MacroModel> getSupportedDocumentClass() {
		return MacroModel.class;
	}
	
	public ExtensionFileFilter getSupportedFileFilter() {
		return this.forOpenFilters[0];
	}
	
	public boolean isSupportedFileType(File targetFile) {
		// ファイルの拡張子を判定する
		if (!super.isSupportedFileType(targetFile)) {
			// mismatched open file extensions
			return false;
		}
		
		// ファイルの内容がマクロファイルフォーマットか判定する。
		if (!CsvMacroFiles.isCsvMacroFile(targetFile, getFileEncoding())) {
			// this file is not supported
			return false;
		}
		
		// supported file
		return true;
	}
	
	/**
	 * このプラグインのドキュメントが、新規作成可能なドキュメントかを取得する。
	 * @return	常に <tt>true</tt> を返す。
	 * @since 1.16
	 */
	public boolean isAllowCreateNewDocument() {
		return true;
	}

	/**
	 * AADLエディタの標準フォントを返す。
	 * @return 標準のエディタフォント
	 */
	public Font getDefaultEditorFont() {
		return SpreadSheetTable.getDefaultTableFont();
	}
	
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
	 * @since 1.14
	 */
	public MacroView newDocument(File targetFile, String templateText) throws IOException
	{
		MacroModel doc = new MacroModel(this, templateText, targetFile);
		doc.save(targetFile);
		//MacroView view = new MacroView(doc);
		MacroView view = new MacroView();
		view.initialComponent(doc);
		putDocumentView(doc, view);
		// view.setEditorFont(getEditorFont());
		return view;
	}
	
	public MacroView openDocument(Component parentComponent, File targetFile) throws IOException
	{
		MacroModel doc = new MacroModel(this, targetFile, getFileEncoding());
		//MacroView view = new MacroView(doc);
		MacroView view = new MacroView();
		view.initialComponent(doc);
		putDocumentView(doc, view);
		//view.setEditorFont(getEditorFont());
		return view;
	}
	
	public MacroView onNewComponent(Component parentComponent) {
		// choose target file
		File targetFile = chooseSaveFile(parentComponent, getFileChooserTitleForSave());
		if (targetFile == null) {
			// not selected
			return null;
		}
		
		// create new document
		//MacroModel doc = new MacroModel(this, targetFile);
		MacroModel doc;
		try {
			doc = new MacroModel(this, null, targetFile);
		} catch (IOException ignoreEx) {
			// 例外は発生しない
			throw new InternalError();
		}
		//MacroView view = new MacroView(doc);
		MacroView view = new MacroView();
		view.initialComponent(doc);
		putDocumentView(doc, view);
		// view.setEditorFont(getEditorFont());
		return view;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	@Override
	protected String getFileChooserTitleForOpen() {
		return MacroMessages.getInstance().chooserTitleOpen;
	}

	@Override
	protected String getFileChooserTitleForSave() {
		return MacroMessages.getInstance().chooserTitleSave;
	}

	/**
	 * ファイル・オープン・ダイアログ用のファイルフィルタを返す。
	 * このメソッドは、このインスタンスの初期化に呼び出される。
	 * @return	ファイルフィルタの配列
	 */
	protected ExtensionFileFilter[] createForOpenFileFilters() {
		return new ExtensionFileFilter[]{
				new ExtensionFileFilter(MacroMessages.getInstance().descExtOpen, MacroMessages.getInstance().extOpen),
				//new ExtensionFileFilter(MacroMessages.getInstance().descExtMacro, MacroMessages.getInstance().extMacro),
				//new ExtensionFileFilter(MacroMessages.getInstance().descExtCsv, MacroMessages.getInstance().extCsv),
		};
	}

	/**
	 * ファイル・セーブ・ダイアログ用のファイルフィルタを返す。
	 * このメソッドは、このインスタンス初期化時に呼び出される。
	 * @return	ファイルフィルタの配列
	 */
	protected ExtensionFileFilter[] createForSaveFileFilters() {
		return new ExtensionFileFilter[]{
				new ExtensionFileFilter(MacroMessages.getInstance().descExtMacro, MacroMessages.getInstance().extMacro),
				//new ExtensionFileFilter(MacroMessages.getInstance().descExtCsv, MacroMessages.getInstance().extCsv),
		};
	}
}
