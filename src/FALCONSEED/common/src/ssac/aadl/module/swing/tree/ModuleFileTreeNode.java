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
 * @(#)ModuleFileTreeNode.java	1.14	2009/12/14
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.module.swing.tree;

import java.io.File;

import javax.swing.tree.TreeNode;

import ssac.aadl.module.ModuleFileManager;
import ssac.aadl.module.setting.PackageSettings;
import ssac.aadl.module.setting.ProjectSettings;
import ssac.util.io.VirtualFile;
import ssac.util.io.VirtualFileManager;
import ssac.util.swing.tree.VirtualFileTreeNode;

/**
 * モジュールのツリーコンポーネントのノードとなるツリーノード
 * 
 * @version 1.14	2009/12/14
 * @since 1.14
 */
public class ModuleFileTreeNode extends VirtualFileTreeNode
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** プロジェクトのプロパティ **/
	protected ProjectSettings	_propProject;
	/** モジュールパッケージのプロパティ **/
	protected PackageSettings	_propPackage;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public ModuleFileTreeNode(VirtualFile file) {
		super(file);
		refreshProperties();
	}
	
	public ModuleFileTreeNode(File file) {
		this(VirtualFileManager.fromJavaFile(file));
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	@Override
	public void refreshProperties() {
		if (isDirectory()) {
			loadProjectProperties();
			loadPackageProperties();
		} else {
			this._propProject = null;
			this._propPackage = null;
		}
	}

	/**
	 * このノードが示す抽象パスがプロジェクトの管理対象である場合に <tt>true</tt> を返す。
	 * このノードが最上位プロジェクトの管理対象外の場合や、最上位プロジェクトルートノードが
	 * 見つからない場合、このノード自身が最上位プロジェクトルートノードの場合は <tt>false</tt> を返す。
	 */
	public boolean isRegisteredProject() {
		return (getRegisteredProject() != null);
	}

	/**
	 * このノードが登録されているプロジェクトルートノードを返す。
	 * このノードが最上位プロジェクトの管理対象外の場合や、最上位プロジェクトルートノードが
	 * 見つからない場合、このノード自身が最上位プロジェクトルートノードの場合は <tt>null</tt> を返す。
	 */
	public ModuleFileTreeNode getRegisteredProject() {
		ModuleFileTreeNode ndProj = getTopProjectRootNode(this);
		if (ndProj == null || ndProj == this) {
			return null;
		}

		ProjectSettings proj = ndProj.getProjectProperties();
		ModuleFileTreeNode node = this;
		do {
			if (proj.containsExclusiveProjectFile(node.getFileObject())) {
				// 自身もしくは親ディレクトリが除外セットに存在する場合は、
				// プロジェクトから除外されているものとみなす。
				return null;
			}
		} while ((node = (ModuleFileTreeNode)node.getParent()) != ndProj);
		
		// 最上位プロジェクトルートノードが存在し、除外セットに階層が登録されていない
		// 場合はプロジェクトファイルとして有効とする
		return ndProj;
	}

	/*
	 * ※アルゴリズム見直し
	 * このノードが示すファイルもしくはフォルダを、このノードの上位プロジェクトに
	 * 登録する。
	 * @return	新たに登録した場合は <tt>true</tt>、すでに登録されている場合は <tt>false</tt> を返す。
	 * @throws IllegalStateException	このノードの上位にプロジェクトが存在しない場合、
	 * 									もしくはこのノード自身がプロジェクトルートの場合
	 *
	public boolean registerToProject() {
		ModuleFileTreeNode nProj = getTopProjectRootNode(this);
		if (nProj == null)
			throw new IllegalStateException("Ancestor Project node does not exists.");
		if (nProj == this)
			throw new IllegalStateException("This node is top level Project root.");
		
		ProjectSettings setting = nProj.getProjectProperties();
		boolean ret = setting.removeExclusiveProjectFile(getFileObject());
		if (ret) {
			// commit
			try {
				setting.commit();
			} catch (IOException ex) {
				AppLogger.warn("Failed to commit that register to project : \"" + getFileObject().getAbsolutePath() + "\"", ex);
				setting.rollback();
				ret = false;
			}
		}
		
		return ret;
	}
	*/

	/*
	 * ※アルゴリズム見直し
	 * このノードが示すファイルもしくはフォルダを、このノードの上位プロジェクトから除外する。
	 * @return	プロジェクトから除外した場合は <tt>true</tt>、すでに除外されている場合は <tt>false</tt> を返す。
	 * @throws IllegalStateException	このノードの上位にプロジェクトが存在しない場合、
	 * 									もしくはこのノード自身がプロジェクトルートの場合
	 *
	public boolean unregisterFromProject() {
		ModuleFileTreeNode nProj = getTopProjectRootNode(this);
		if (nProj == null)
			throw new IllegalStateException("Ancestor Project node does not exists.");
		if (nProj == this)
			throw new IllegalStateException("This node is top level Project root.");
		
		ProjectSettings setting = nProj.getProjectProperties();
		boolean ret = setting.addExclusiveProjectFile(getFileObject());
		if (ret) {
			// commit
			try {
				setting.commit();
			} catch (IOException ex) {
				AppLogger.warn("Failed to commit that unregister from project : \"" + getFileObject().getAbsolutePath() + "\"", ex);
				setting.rollback();
				ret = false;
			}
		}
		
		return ret;
	}
	*/

	/**
	 * このノードが示すファイルもしくはフォルダが、最上位モジュールパッケージに
	 * 含まれる場合に <tt>true</tt> を返す。
	 * このノードの上位にモジュールパッケージルートが存在しない場合や、このノード自身が
	 * 最上位モジュールパッケージルートノードの場合は <tt>false</tt> を返す。
	 * このメソッドが <tt>false</tt> を返す場合でも、このノード自身がモジュールパッケージ
	 * ルートノードの場合もある。
	 */
	public boolean isAssignedModulePackageFile() {
		return (getAssignedModulePackageRoot() != null);
	}

	/**
	 * このノードが示すファイルもしくはフォルダの所属する、最上位モジュールパッケージの
	 * ルートフォルダを示すノードを返す。
	 * このノードの上位にモジュールパッケージルートが存在しない場合や、このノード自身が
	 * 最上位モジュールパッケージルートノードの場合は <tt>null</tt> を返す。
	 */
	public ModuleFileTreeNode getAssignedModulePackageRoot() {
		ModuleFileTreeNode topPackRoot = getTopModulePackageRootNode(this);
		if (topPackRoot != null && topPackRoot != this) {
			return topPackRoot;
		} else {
			return null;
		}
	}

	/**
	 * このノードの抽象パスがプロジェクトのルートディレクトリである場合に <tt>true</tt> を返す。
	 */
	public boolean isProjectRoot() {
		return (_propProject != null);
	}

	/**
	 * このノードの抽象パスがモジュールパッケージのルートディレクトリである場合に <tt>true</tt> を返す。
	 */
	public boolean isModulePackageRoot() {
		return (_propPackage != null);
	}

	/**
	 * このノードの抽象パスがプロジェクトのルートディレクトリである場合に、
	 * プロジェクトのプロパティを返す。
	 * プロジェクトのルートディレクトリではない場合は <tt>null</tt> を返す。
	 */
	public ProjectSettings getProjectProperties() {
		return _propProject;
	}

	/**
	 * このノードの抽象パスがモジュールパッケージのルートディレクトリである場合に、
	 * モジュールパッケージのプロパティを返す。
	 * モジュールパッケージのルートディレクトリではない場合は <tt>null</tt> を返す。
	 */
	public PackageSettings getModulePackageProperties() {
		return _propPackage;
	}
	
	/**
	 * 指定されたノードの親ノードを辿り、最後に見つかったプロジェクトルートノードを返す。
	 * プロジェクトルートノードが見つからない場合は <tt>null</tt> を返す。
	 * 指定されたノードが最上位のプロジェクトルートの場合は、指定されたノードを返す。
	 * プロジェクトルートノードとは、プロジェクトのプロパティファイルが格納されている
	 * ディレクトリを示すノードを指す。
	 */
	static public ModuleFileTreeNode getTopProjectRootNode(TreeNode node) {
		ModuleFileTreeNode topProjRoot = null;
		TreeNode ancestor = node;
		
		do {
			if (ancestor instanceof ModuleFileTreeNode) {
				ModuleFileTreeNode anItem = (ModuleFileTreeNode)ancestor;
				if (anItem.isProjectRoot()) {
					topProjRoot = anItem;
				}
			}
		} while ((ancestor = ancestor.getParent()) != null);
		
		return topProjRoot;
	}

	/**
	 * 指定されたノードの親ノードを辿り、最後に見つかったモジュールパッケージルートノードを返す。
	 * モジュールパッケージルートノードが見つからない場合は <tt>null</tt> を返す。
	 * 指定されたノードが最上位のモジュールパッケージルートの場合は、指定されたノードを返す。
	 * モジュールパッケージルートノードとは、モジュールパッケージのプロパティファイルが
	 * 格納されているディレクトリを示すノードを指す。
	 */
	static public ModuleFileTreeNode getTopModulePackageRootNode(TreeNode node) {
		ModuleFileTreeNode topPackRoot = null;
		TreeNode ancestor = node;
		
		do {
			if (ancestor instanceof ModuleFileTreeNode) {
				ModuleFileTreeNode anItem = (ModuleFileTreeNode)ancestor;
				if (!anItem.isProjectRoot() && anItem.isModulePackageRoot()) {
					topPackRoot = anItem;
				}
			}
		} while ((ancestor = ancestor.getParent()) != null);
		
		return topPackRoot;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	/*
	 * 指定されたノードの親ノードを辿り、最初に見つかったプロジェクトルートノードを返す。
	 * プロジェクトルートノードが見つからない場合は <tt>null</tt> を返す。
	 * 指定されたノードがプロジェクトルートノードの場合は、指定されたノードを返す。
	 * プロジェクトルートノードとは、プロジェクトのプロパティファイルが格納されている
	 * ディレクトリを示すノードを指す。
	 *
	static private ModuleFileTreeNode getProjectRootNode(TreeNode node) {
		TreeNode ancestor = node;
		
		do {
			if (ancestor instanceof ModuleFileTreeNode) {
				ModuleFileTreeNode anItem = (ModuleFileTreeNode)ancestor;
				if (anItem.isProjectRoot()) {
					return anItem;
				}
			}
		} while ((ancestor = ancestor.getParent()) != null);
		
		// not found
		return null;
	}
	*/

	/*
	 * 指定されたノードの親ノードを辿り、最初に見つかったモジュールパッケージルートノードを返す。
	 * モジュールパッケージルートノードが見つからない場合は <tt>null</tt> を返す。
	 * 指定されたノードがモジュールパッケージルートの場合は、指定されたノードを返す。
	 * モジュールパッケージルートノードとは、モジュールパッケージのプロパティファイルが
	 * 格納されているディレクトリを示すノードを指す。
	 *
	static private ModuleFileTreeNode getModulePackageRootNode(TreeNode node) {
		TreeNode ancestor = node;
		
		do {
			if (ancestor instanceof ModuleFileTreeNode) {
				ModuleFileTreeNode anItem = (ModuleFileTreeNode)ancestor;
				if (!anItem.isProjectRoot() && anItem.isModulePackageRoot()) {
					return anItem;
				}
			}
		} while ((ancestor = ancestor.getParent()) != null);
		
		// not found
		return null;
	}
	*/
	
	protected void loadProjectProperties() {
		VirtualFile fPrefs = ModuleFileManager.getProjectPrefsFile(getFileObject());
		if (fPrefs.exists() && fPrefs.isFile()) {
			if (this._propProject == null) {
				this._propProject = new ProjectSettings();
			}
			if (fPrefs.equals(this._propProject.getVirtualPropertyFile())) {
				this._propProject.rollback();
			} else {
				this._propProject.loadForTarget(fPrefs);
			}
		} else {
			this._propProject = null;
		}
	}
	
	protected void loadPackageProperties() {
		VirtualFile fPrefs = ModuleFileManager.getModulePackagePrefsFile(getFileObject());
		if (fPrefs.exists() && fPrefs.isFile()) {
			if (this._propPackage == null) {
				this._propPackage = new PackageSettings();
			}
			if (fPrefs.equals(this._propPackage.getVirtualPropertyFile())) {
				this._propPackage.rollback();
			} else {
				this._propPackage.loadForTarget(fPrefs);
			}
		} else {
			this._propPackage = null;
		}
	}
}
