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
 * @(#)ModuleFileTreeModel.java	1.17	2010/11/19
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ModuleFileTreeModel.java	1.14	2009/12/14
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.module.swing.tree;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;

import javax.swing.tree.TreeNode;

import ssac.aadl.module.ModuleFileManager;
import ssac.aadl.module.setting.ProjectSettings;
import ssac.util.Strings;
import ssac.util.io.DefaultFile;
import ssac.util.io.VirtualFile;
import ssac.util.io.VirtualFileFilter;
import ssac.util.io.VirtualFileManager;
import ssac.util.io.VirtualFileOperationException;
import ssac.util.swing.tree.VirtualFileTreeModel;
import ssac.util.swing.tree.VirtualFileTreeNode;

/**
 * モジュールのツリーモデル。
 * 本アプリケーションでは、プロジェクト名やフォルダ名・ファイル名は Windows 環境を
 * 基準とし、大文字小文字を区別しないでチェックする。
 * 
 * @version 1.17	2010/11/19
 * @since 1.14
 */
public class ModuleFileTreeModel extends VirtualFileTreeModel
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static protected final DefaultModuleFileFilter DefaultFilter = new DefaultModuleFileFilter();
	static protected final DefaultModuleComparator DefaultComparator = new DefaultModuleComparator();

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public ModuleFileTreeModel(ModuleFileTreeNode root) {
		super(root, DefaultFilter, DefaultComparator);
	}
	
	public ModuleFileTreeModel(VirtualFile rootPath) {
		super(rootPath, DefaultFilter, DefaultComparator);
	}
	
	public ModuleFileTreeModel(File rootFile) {
		this(VirtualFileManager.fromJavaFile(rootFile));
	}
	
	protected ModuleFileTreeModel(VirtualFile rootPath, VirtualFileFilter filter, Comparator<VirtualFileTreeNode> comparator) {
		super(rootPath, filter, comparator);
	}
	
	protected ModuleFileTreeModel(VirtualFileTreeNode rootNode, VirtualFileFilter filter, Comparator<VirtualFileTreeNode> comparator) {
		super(rootNode, filter, comparator);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	static public VirtualFileFilter getDefaultFileFilter() {
		return DefaultFilter;
	}
	
	static public Comparator<VirtualFileTreeNode> getDefaultComparator() {
		return DefaultComparator;
	}

	/**
	 * 指定された抽象パスがプロジェクトルートフォルダである場合に <tt>true</tt> を返す。
	 * プロジェクトルートフォルダの判定は、抽象パスが示すフォルダの直下にプロジェクト設定
	 * ファイルが存在するかで判定される。
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	static public boolean isProjectRootFile(VirtualFile pathname) {
		VirtualFile fProj = ModuleFileManager.getProjectPrefsFile(pathname);
		return (fProj.exists() && fProj.isFile());
	}
	
	static public boolean isProjectRootFile(File pathname) {
		return isProjectRootFile(new DefaultFile(pathname));
	}

	/**
	 * 指定された抽象パスがモジュールパッケージルートフォルダである場合に <tt>true</tt> を返す。
	 * モジュールパッケージルートフォルダの判定は、抽象パスが示すフォルダの直下にモジュール
	 * パッケージ設定ファイルが存在するかで判定される。
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	static public boolean isModulePackageRootFile(VirtualFile pathname) {
		VirtualFile fPack = ModuleFileManager.getModulePackagePrefsFile(pathname);
		return (fPack.exists() && fPack.isFile());
	}
	
	static public boolean isModulePackageRootFile(File pathname) {
		return isModulePackageRootFile(new DefaultFile(pathname));
	}
	
	//
	// for Project
	//

	/**
	 * 指定された名前のプロジェクトをルートノード直下に生成する。
	 * 
	 * @param projectName	プロジェクト名
	 * @return	生成されたプロジェクトのディレクトリを示すノード
	 * @throws IllegalArgumentException	引数が有効な文字列ではない場合
	 * @throws IllegalStateException		ルートノードが未定義の場合、もしくは同名のプロジェクトがすでに存在する場合
	 * @throws IOException					ディレクトリの作成、もしくはプロジェクト定義ファイルの作成に失敗した場合
	 */
	public ModuleFileTreeNode createProject(String projectName) throws VirtualFileOperationException
	{
		/*
		// プロジェクトディレクトリの作成
		String errmsg = null;
		try {
			if (!fProj.mkdir()) {
				errmsg = CommonMessages.getErrorMessage(CommonMessages.MessageID.ERR_FILE_CREATE, null, fProj.getAbsolutePath());
			}
		} catch (SecurityException ex) {
			errmsg = CommonMessages.getErrorMessage(CommonMessages.MessageID.ERR_FILE_CREATE, ex, fProj.getAbsolutePath());
		}
		if (errmsg != null) {
			throw new IOException(errmsg);
		}
		// プロジェクトファイルの作成
		try {
			ps.saveForTarget(fProjPrefs);
		} catch (IOException ex) {
			errmsg = CommonMessages.getErrorMessage(CommonMessages.MessageID.ERR_FILE_CREATE, ex, fProjPrefs.getAbsolutePath());
			fProj.delete();
			throw new IOException(errmsg);
		}
		 */
		if (Strings.isNullOrEmpty(projectName))
			throw new IllegalArgumentException("projectName argument is empty or null.");
		
		VirtualFile fRoot = getRootFile();
		if (fRoot == null)
			throw new IllegalStateException("rootNode does not exists.");
		
		// プロジェクトディレクトリの作成
		VirtualFile fProj = fRoot.getChildFile(projectName);
		if (fProj.exists()) {
			throw new IllegalStateException("Project directory already exists! : \"" + fProj.toString() + "\"");
		}
		try {
			if (!fProj.mkdir()) {
				throw new VirtualFileOperationException(VirtualFileOperationException.MKDIR, null, fProj);
			}
		} catch (SecurityException ex) {
			throw new VirtualFileOperationException(VirtualFileOperationException.MKDIR, null, fProj, ex);
		}
		
		// プロジェクトファイルの作成
		VirtualFile fProjPrefs = ModuleFileManager.getProjectPrefsFile(fProj);
		ProjectSettings ps = new ProjectSettings();
		try {
			ps.saveForTarget(fProjPrefs);
		} catch (IOException ex) {
			fProj.delete();
			throw new VirtualFileOperationException(VirtualFileOperationException.CREATE, null, fProjPrefs, ex);
		}
		
		// プロジェクトノードの作成
		ModuleFileTreeNode newNode = (ModuleFileTreeNode)createTreeNode(fProj);
		newNode.refreshProperties();
		int index = getInsertionIndex((VirtualFileTreeNode)getRoot(), newNode);
		if (index < 0)
			throw new AssertionError("Illegal insertion index : " + index);
		insertNodeInto(newNode, (VirtualFileTreeNode)getRoot(), index);
		
		return newNode;
	}

	/**
	 * <em>parent</em> の直下に、指定された名前のディレクトリを作成する。
	 * 
	 * @param parent	親ディレクトリとなる抽象パスを格納するツリーノード
	 * @param name		作成するディレクトリ名
	 * @return	生成されたディレクトリを示すノード
	 * @throws IllegalArgumentException	<em>parent</em> がディレクトリではない場合、
	 * 										もしくは <em>name</em> が有効な文字列ではない場合
	 * @throws IllegalStateException		<em>parent</em> の子に <em>name</em> と同名の
	 * 										ファイルもしくはディレクトリが存在する場合
	 * @throws VirtualFileOperationException	ディレクトリの作成に失敗した場合
	 */
	@Override
	public ModuleFileTreeNode createDirectory(VirtualFileTreeNode parent, String name)
	throws VirtualFileOperationException
	{
		/*
		String errmsg = null;
		try {
			if (!fDir.mkdir()) {
				errmsg = CommonMessages.getErrorMessage(CommonMessages.MessageID.ERR_FILE_CREATE, null, fDir.getAbsolutePath());
			}
		} catch (SecurityException ex) {
			errmsg = CommonMessages.getErrorMessage(CommonMessages.MessageID.ERR_FILE_CREATE, ex, fDir.getAbsolutePath());
		}
		if (errmsg != null) {
			throw new IOException(errmsg);
		}
		 */
		
		ModuleFileTreeNode newNode = (ModuleFileTreeNode)super.createDirectory(parent, name);
		// プロジェクトへの登録は、除外セットに記録しなければ登録されていることになるので、
		// 処理の必要はない。
		
		return newNode;
	}
	
	/**
	 * <em>parent</em> の直下に、指定された名前のファイルを作成する。
	 * 
	 * @param parent	親ディレクトリとなる抽象パスを格納するツリーノード
	 * @param name		作成するファイル名
	 * @return	生成されたファイルを示すノード
	 * @throws IllegalArgumentException	<em>parent</em> がディレクトリではない場合、
	 * 										もしくは <em>name</em> が有効な文字列ではない場合
	 * @throws IllegalStateException		<em>parent</em> の子に <em>name</em> と同名の
	 * 										ファイルもしくはディレクトリが存在する場合
	 * @throws VirtualFileOperationException	ファイルの作成に失敗した場合
	 */
	@Override
	public ModuleFileTreeNode createFile(VirtualFileTreeNode parent, String name)
	throws VirtualFileOperationException
	{
		/*
		String errmsg = null;
		try {
			if (!fFile.createNewFile()) {
				errmsg = CommonMessages.getErrorMessage(CommonMessages.MessageID.ERR_FILE_CREATE, null, fFile.getAbsolutePath());
			}
		} catch (SecurityException ex) {
			errmsg = CommonMessages.getErrorMessage(CommonMessages.MessageID.ERR_FILE_CREATE, ex, fFile.getAbsolutePath());
		}
		if (errmsg != null) {
			throw new IOException(errmsg);
		}
		 */
		
		ModuleFileTreeNode newNode = (ModuleFileTreeNode)super.createFile(parent, name);
		
		// プロジェクトへ登録
		// プロジェクトへの登録は、除外セットに記録しなければ登録されていることになるので、
		// 処理の必要はない。
		
		return newNode;
	}

	//------------------------------------------------------------
	// Implements FileTreeModel interfaces
	//------------------------------------------------------------

	@Override
	public void setRoot(TreeNode root) {
		// ModuleFileTreeNode 以外のインスタンスを許可しない
		super.setRoot((ModuleFileTreeNode)root);
	}

	@Override
	protected ModuleFileTreeNode createTreeNode(VirtualFile fileObject) {
		return new ModuleFileTreeNode(fileObject);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------

	/**
	 * エディタのツリービューに表示するファイルのみを選択するフィルタ
	 */
	static public class DefaultModuleFileFilter implements VirtualFileFilter
	{
		public boolean accept(VirtualFile pathname) {
			// 隠しファイルは表示しない
			if (pathname.isHidden()) {
				return false;
			}
			
			//@@@ added by Y.Ishizuka : 2010.10.01
			// '.' で始まるファイルやディレクトリは非表示とする
			if (Strings.startsWithIgnoreCase(pathname.getName(), ".")) {
				return false;
			}
			//@@@ end of added : 2010.10.01
			
			// .prefs ファイルは非表示とする
			if (pathname.isFile()) {
				// 設定ファイルは表示しない
				if (Strings.endsWithIgnoreCase(pathname.getName(), ModuleFileManager.EXT_FILE_PREFS)) {
					return false;
				}
			}
			
			// 表示許可する
			return true;
		}
	}

	/**
	 * エディタのツリービューに表示する順序を制御するコンパレーター
	 */
	static protected class DefaultModuleComparator implements Comparator<VirtualFileTreeNode>
	{
		public int compare(VirtualFileTreeNode o1, VirtualFileTreeNode o2) {
			ModuleFileTreeNode node1 = (ModuleFileTreeNode)o1;
			ModuleFileTreeNode node2 = (ModuleFileTreeNode)o2;

			// project < directory < package < file
			if (node1.isDirectory()) {
				if (node2.isDirectory()) {
					if (node1.isProjectRoot()) {
						// node1(project)
						if (!node2.isProjectRoot()) {
							// node1(project) < node2(!project)
							return (-1);
						}
					}
					else if (node1.isModulePackageRoot()) {
						// node1(package)
						if (node2.isProjectRoot()) {
							// node1(package) > node2(project)
							return (1);
						}
						else if (!node2.isModulePackageRoot()) {
							if (node2.isDirectory()) {
								// node1(package) > node2(directory)
								return (1);
							} else {
								// node1(package) < node2(file)
								return (-1);
							}
						}
					}
					else {
						// node1(directory)
						if (node2.isProjectRoot()) {
							// node1(directory) > node2(project)
							return (1);
						}
						else if (!node2.isDirectory() || node2.isModulePackageRoot()) {
							// node1(directory) < node2(package or file)
							return (-1);
						}
					}
				} else {
					// node1(directory) < node2(file)
					return (-1);
				}
			}
			else if (node2.isDirectory()) {
				// node1(file) > node2(directory)
				return 1;
			}
			
			// compare by File
			return node1.getFileObject().compareTo(node2.getFileObject());
		}
	}
}
