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
 * @(#)IRunnerTreeView.java	1.10	2011/02/14
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.runner.view;

import javax.swing.JTree;
import javax.swing.tree.TreeModel;

import ssac.util.io.VirtualFile;
import ssac.util.swing.menu.IMenuActionHandler;

/**
 * ツリービュー共通のインタフェース
 * 
 * @version 1.10	2011/02/14
 * @since 1.10
 */
public interface IRunnerTreeView extends IMenuActionHandler
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Interfaces
	//------------------------------------------------------------

	/**
	 * ツリーコンポーネントに設定されているツリーモデルを取得する。
	 */
	public TreeModel getTreeModel();
	
	public JTree getTreeComponent();
	
	public VirtualFile getSystemRootDirectory();
	
	public VirtualFile getUserRootDirectory();
	
	public void setSystemRootDir(VirtualFile newDir);
	
	public void setUserRootDir(VirtualFile newDir);
	
	public boolean isSelectionEmpty();
	
	public int getSelectionCount();
	
	public VirtualFile getSelectionFile();
	
	public VirtualFile[] getSelectionFiles();
	
//	public VirtualFile getSelectionMExecDefPrefsFile() {
//		return _cTree.getSelectionMExecDefPrefsFile();
//	}
//	
//	public VirtualFile[] getSelectionMExecDefPrefsFiles() {
//		return _cTree.getSelectionMExecDefPrefsFiles();
//	}
	
	/**
	 * このビューを格納するエディタフレームを取得する。
	 * @return	このビューを格納するエディタフレームのインスタンス。
	 * 			このビューがフレームに格納されていない場合は <tt>null</tt> を返す。
	 */
	public RunnerFrame getFrame();

	/**
	 * ルートノードを起点に、表示されているすべてのツリーノードを
	 * 最新の情報に更新する。
	 */
	public void refreshAllTree();

	/**
	 * 選択されているノードを起点に、そのノードの下位で表示されている
	 * すべてのツリーノードを最新の情報に更新する。
	 */
	public void refreshSelectedTree();

	/**
	 * 指定された抽象パスを持つノードの表示を更新する。
	 * 表示されていないノードの場合や、ツリー階層内の抽象パスではない
	 * 場合は何もしない。
	 * @param targetFile	対象の抽象パス
	 */
	public void refreshFileTree(VirtualFile targetFile);

	/**
	 * このビューのメインコンポーネントにフォーカスを設定する。
	 */
	public void requestFocusInComponent();
	
	//
	// File operations
	//

	/**
	 * ディレクトリの新規作成を許可する状態であれば <tt>true</tt> を返す。
	 */
	public boolean canCreateDirectory();
	
	/**
	 * ファイルの新規作成を許可する状態であれば <tt>true</tt> を返す。
	 */
	public boolean canCreateFile();

	/**
	 * ディレクトリ選択ダイアログを表示し、選択されたディレクトリを返す。
	 * @param title			ディレクトリ選択ダイアログのタイトルとする文字列
	 * @param initParent	初期選択位置とする抽象パス。このパスがディレクトリでは
	 * 						ない場合は、親ディレクトリを初期選択位置とする。
	 * 						ツリーに存在しない抽象パスであれば選択しない。
	 * @return	選択された抽象パスを返す。選択されなかった場合は <tt>null</tt> を返す。
	 */
	public VirtualFile chooseDirectory(String title, VirtualFile initParent);

	/**
	 * ディレクトリを新規に作成するためのディレクトリ名入力ダイアログを表示し、
	 * 入力されたディレクトリ名で新規フォルダを作成する。
	 * @return	正常に作成できた場合は <tt>true</tt>、そうでない場合は <tt>false</tt> を返す
	 */
	public boolean doCreateDirectory();

	/**
	 * ツリーノードで選択されているノードの名前変更が可能であれば <tt>true</tt> を返す。
	 */
	public boolean canRename();
	
	/**
	 * 選択されているノードが示すファイル名を変更する。
	 */
	public void doRename();

	/**
	 * ツリーノードで選択されているノードが移動可能であれば <tt>true</tt> を返す。
	 */
	public boolean canMoveTo();

	/**
	 * 選択されているノードを、ユーザー指定の場所に移動する。
	 */
	public void doMoveTo();

	/**
	 * 選択されているすべてのノードがコピー可能か検証する。
	 * @return	コピー可能であれば <tt>true</tt> を返す。
	 * 			未選択の場合は <tt>false</tt> を返す。
	 */
	public boolean canCopy();

	/**
	 * 選択されているノードが示すファイルをクリップボードへコピーする
	 */
	public void doCopy();

	/**
	 * 現在クリップボードに存在する内容が選択位置に貼り付け可能か検証する。
	 * @return	貼り付け可能であれば <tt>true</tt> を返す。
	 * 			未選択の場合は <tt>false</tt> を返す。
	 */
	public boolean canPaste();

	/**
	 * クリップボードに存在するデータを、ワークスペースにコピーする
	 */
	public void doPaste();

	/**
	 * 選択されているすべてのノードが削除可能か検証する。
	 * @return	削除可能であれば <tt>true</tt> を返す。
	 * 			未選択の場合は <tt>false</tt> を返す。
	 */
	public boolean canDelete();

	/**
	 * 選択されているノードが示すファイルを削除する
	 */
	public void doDelete();
}
