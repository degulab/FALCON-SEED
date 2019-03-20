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
 * @(#)FileDialogManager.java	1.14	2009/12/09
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.module.swing;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;
import java.io.File;
import java.util.Collection;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import ssac.aadl.common.CommonMessages;
import ssac.util.Strings;
import ssac.util.io.VirtualFile;
import ssac.util.swing.IDialogResult;
import ssac.util.swing.InputDialog;
import ssac.util.swing.SwingTools;
import ssac.util.swing.TextFieldCharacterValidator;

/**
 * ファイル操作に関するダイアログのマネージャ
 * 
 * @version 1.14	2009/12/09
 * @since 1.14
 */
public class FileDialogManager
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
	
	private FileDialogManager() {}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 標準のパラメータで、新しい {@link FilenameValidator} のインスタンスを生成する。
	 */
	static public FilenameValidator createDefaultFilenameValidator() {
		return createDefaultFilenameValidator(null);
	}

	/**
	 * 指定されたエラーメッセージボックスのタイトルを持つ、
	 * 新しい {@link FilenameValidator} のインスタンスを生成する。
	 * @param title	エラーメッセージボックスのタイトルとする文字列を指定する。
	 * 				<tt>null</tt> もしくは空文字列の場合 {@link CommonMessages#msgboxTitleError} が使用される。
	 * @return	<code>FilenameValidator</code> オブジェクト
	 */
	static public FilenameValidator createDefaultFilenameValidator(String title) {
		String msgboxTitle = (Strings.isNullOrEmpty(title) ? CommonMessages.getInstance().msgboxTitleError : title);
		return new FilenameValidator(msgboxTitle);
	}
	
	static public PackageImportValidator createPackageImportValidator(VirtualFile alreadyExistsBasePath) {
		return createPackageImportValidator(alreadyExistsBasePath, null);
	}
	
	static public PackageImportValidator createPackageImportValidator(VirtualFile alreadyExistsBasePath, String title) {
		String msgboxTitle = (Strings.isNullOrEmpty(title) ? CommonMessages.getInstance().msgboxTitleError : title);
		return new PackageImportValidator(alreadyExistsBasePath, msgboxTitle);
	}

	/**
	 * 指定されたパラメータで、入力ダイアログを表示する。
	 * 表示されるダイアログはモーダルダイアログであり、ダイアログが閉じられるまで
	 * 制御を戻さない。
	 * このメソッドは説明テキストを表示しないダイアログを生成する。
	 * 
	 * @param parentComponent	入力ダイアログの親となるコンポーネント
	 * @param title				ダイアログのタイトル
	 * @param label				入力フィールドのラベル
	 * @param initialValue		入力フィールドに表示する初期値
	 * @param validator			入力内容を検証するバリデータ
	 * @return	[OK] ボタンが押されたときの入力フィールドの内容を返す。
	 * 			[OK] 以外のボタンでダイアログが終了した場合は <tt>null</tt> を返す。
	 */
	static public String showInputDialog(Component parentComponent,
											String title, String label,
											String initialValue,
											TextFieldCharacterValidator validator)
	{
		return showInputDialog(parentComponent, title, null, label, initialValue, validator);
	}
	
	/**
	 * 指定されたパラメータで、入力ダイアログを表示する。
	 * 表示されるダイアログはモーダルダイアログであり、ダイアログが閉じられるまで
	 * 制御を戻さない。
	 * 
	 * @param parentComponent	入力ダイアログの親となるコンポーネント
	 * @param title				ダイアログのタイトル
	 * @param description		ダイアログ上部に表示する説明テキスト
	 * @param label				入力フィールドのラベル
	 * @param initialValue		入力フィールドに表示する初期値
	 * @param validator			入力内容を検証するバリデータ
	 * @return	[OK] ボタンが押されたときの入力フィールドの内容を返す。
	 * 			[OK] 以外のボタンでダイアログが終了した場合は <tt>null</tt> を返す。
	 */
	static public String showInputDialog(Component parentComponent,
											String title, String description,
											String label, String initialValue,
											TextFieldCharacterValidator validator)
	{
		// create Dialog
		InputDialog dlg;
		Window owner = SwingTools.getWindowForComponent(parentComponent);
		if (owner instanceof Frame)
			dlg = new InputDialog((Frame)owner, label, null, title, description, initialValue, validator);
		else
			dlg = new InputDialog((Dialog)owner, label, null, title, description, initialValue, validator);
		
		// show Dialog
		dlg.pack();
		dlg.setVisible(true);
		dlg.dispose();
		if (dlg.getDialogResult() == IDialogResult.DialogResult_OK) {
			// valid input
			return dlg.getFieldText();
		} else {
			// user canceled
			return null;
		}
	}
	
	static public String showCreateProjectDialog(Component parentComponent, VirtualFile workspaceRoot) {
		if (workspaceRoot == null)
			throw new IllegalArgumentException("workspaceRoot argument is null.");
		if (!workspaceRoot.exists())
			throw new IllegalArgumentException("workspaceRoot file does not exists : \"" + workspaceRoot.toString() + "\"");
		if (!workspaceRoot.isDirectory())
			throw new IllegalArgumentException("workspaceRoot file is not directory : \"" + workspaceRoot.toString() + "\"");
		
		// message resources
		String dlgTitle = CommonMessages.getInstance().InputTitle_NewProject;
		String dlgLabel = CommonMessages.getInstance().InputLabel_ProjectName;
		
		// shwo Dialog
		FilenameValidator validator = createDefaultFilenameValidator();
		validator.setAlreadyExistsFilenames(workspaceRoot);
		String result = showInputDialog(parentComponent, dlgTitle, dlgLabel, null, validator);
		return result;
	}
	
	static public String showSimpleCreateFolderDialog(Component parentComponent, VirtualFile parentFolder) {
		if (parentFolder == null)
			throw new IllegalArgumentException("parentFolder argument is null.");
		if (!parentFolder.exists())
			throw new IllegalArgumentException("parentFolder file does not exists : \"" + parentFolder.toString() + "\"");
		if (!parentFolder.isDirectory())
			throw new IllegalArgumentException("parentFolder file is not directory : \"" + parentFolder.toString() + "\"");
		
		// message resources
		String dlgTitle = CommonMessages.getInstance().InputTitle_NewFolder;
		String dlgLabel = CommonMessages.getInstance().InputLabel_FolderName;
		
		// show Dialog
		FilenameValidator validator = createDefaultFilenameValidator();
		validator.setAlreadyExistsFilenames(parentFolder);	// 既存のファイル名を設定
		String result = showInputDialog(parentComponent, dlgTitle, dlgLabel, null, validator);
		return result;
	}
	
	static public String showRenameDialog(Component parentComponent, VirtualFile target) {
		if (target == null)
			throw new IllegalArgumentException("target argument is null.");
		if (!target.exists())
			throw new IllegalArgumentException("target file does not exists : \"" + target.toString() + "\"");
		
		// message resources
		String dlgTitle = CommonMessages.getInstance().InputTitle_Rename;
		String dlgLabel = CommonMessages.getInstance().InputLabel_NewName;
		
		// create Dialog
		FilenameValidator validator = createDefaultFilenameValidator();
		validator.setAlreadyExistsFilenames(target.getParentFile());
		String result = showInputDialog(parentComponent, dlgTitle, dlgLabel, target.getName(), validator);
		return result;
	}
	
	static public final File chooseDirectory(Component parentComponent, File initialFile,
												String title, String approveText)
	{
		JFileChooser fc = createFileChooser(JFileChooser.DIRECTORIES_ONLY, false, initialFile);
		fc.setDialogTitle(title);
		fc.setApproveButtonText(approveText);

		int ret = fc.showOpenDialog(parentComponent);
		if (ret != JFileChooser.APPROVE_OPTION) {
			// user canceled
			return null;
		}
		
		// selected
		return fc.getSelectedFile();
	}
	
	static public final File chooseSaveDirectory(Component parentComponent, File initialFile,
													String title, String approveText)
	{
		JFileChooser fc = createFileChooser(JFileChooser.DIRECTORIES_ONLY, false, initialFile);
		fc.setDialogTitle(title);
		fc.setApproveButtonText(approveText);
		
		int ret = fc.showSaveDialog(parentComponent);
		if (ret != JFileChooser.APPROVE_OPTION) {
			// user canceled
			return null;
		}
		
		// selected
		return fc.getSelectedFile();
	}
	
	static public synchronized JFileChooser createFileChooser(int selectionMode, boolean multiSelection,
																  File initialFile, FileFilter...filters)
	{
		// setup chooser
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(selectionMode);
		chooser.setMultiSelectionEnabled(multiSelection);
		
		// setup filters
		for (int i = filters.length - 1; i >= 0; i--) {
			chooser.setFileFilter(filters[i]);
		}
		
		// setup initial file
		File initDir = null;
		if (initialFile != null) {
			try {
				if (initialFile.isDirectory()) {
					initDir = initialFile;
				}
				else {
					File parentDir = initialFile.getParentFile();
					if (parentDir != null && parentDir.isDirectory()) {
						initDir = parentDir;
					}
				}
			}
			catch (Throwable ex) {
				// no implement
			}
		}
		if (initDir == null) {
			initDir = new File("");
		}
		chooser.setCurrentDirectory(initDir);
		
		return chooser;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	/**
	 * パッケージのインポート専用のバリデータ。
	 * このバリデータはプロジェクト名の重複をチェックする目的で使用されるため、
	 * 一度設定された既存ファイル名リストは変更できないようにする。
	 * この処理は、{@link ssac.aadl.module.swing.tree.ModuleFileTreePane} クラス内で
	 * ツリーの選択が変更されたときに書き換えられるのを防止する。
	 */
	static protected class PackageImportValidator extends FilenameValidator
	{
		public PackageImportValidator(VirtualFile alreadyExistsBasePath, String errorMessageTitle) {
			super(errorMessageTitle);
			super.setAlreadyExistsFilenames(alreadyExistsBasePath);
		}

		@Override
		public void clearAlreadyExistsFilenames() {
			// 変更しない
		}

		@Override
		public void setAlreadyExistsFilenames(Collection<? extends String> names) {
			// 変更しない
		}

		@Override
		public void setAlreadyExistsFilenames(File basePath) {
			// 変更しない
		}

		@Override
		public void setAlreadyExistsFilenames(VirtualFile basePath) {
			// 変更しない
		}
	}
}
