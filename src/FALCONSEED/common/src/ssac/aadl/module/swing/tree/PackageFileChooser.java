/*
 * @(#)PackageFileChooser.java	1.14	2009/12/09
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.module.swing.tree;

import java.awt.Dialog;
import java.awt.Frame;

import ssac.aadl.module.swing.FilenameValidator;
import ssac.util.io.VirtualFile;
import ssac.util.io.VirtualFileFilter;

/**
 * ツリー形式のパッケージ選択ダイアログ
 * 
 * @version 1.14	2009/12/09
 * @since 1.14
 */
public class PackageFileChooser extends ModuleFileChooser
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static protected final VirtualFileFilter DefaultPackageFileFilter = new PackageFileFilter();
	static protected final VirtualFileFilter DefaultPackageFolderFilter = new PackageFolderFilter();

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private boolean _allowChooseRoot;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public PackageFileChooser(Frame owner,
								boolean rootVisible, boolean allowCreateFolder,
								VirtualFile root, VirtualFile initialSelection,
								String title, String description,
								String treeLabel, String inputLabel,
								VirtualFileFilter filter,
								FilenameValidator validator)
	{
		super(owner, rootVisible, allowCreateFolder, root, initialSelection,
				title, description, treeLabel, inputLabel, filter, validator);
	}

	public PackageFileChooser(Dialog owner,
								boolean rootVisible, boolean allowCreateFolder,
								VirtualFile root, VirtualFile initialSelection,
								String title, String description,
								String treeLabel, String inputLabel,
								VirtualFileFilter filter,
								FilenameValidator validator)
	{
		super(owner, rootVisible, allowCreateFolder, root, initialSelection,
				title, description, treeLabel, inputLabel, filter, validator);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	static public final VirtualFileFilter getDefaultPackageFileFilter() {
		return DefaultPackageFileFilter;
	}
	
	static public final VirtualFileFilter getDefaultPackageFolderFilter() {
		return DefaultPackageFolderFilter;
	}
	
	public boolean isRootSelectionEnabled() {
		return _allowChooseRoot;
	}
	
	public void setRootSelectionEnable(boolean enable) {
		_allowChooseRoot = enable;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	@Override
	protected ModuleFileTreePane createTreePane() {
		return new PackageFileTreePane();
	}

	@Override
	protected boolean canChooseFile(ModuleFileTreeNode node) {
		// ファイル選択可能の場合は、モジュールパッケージルートを選択する
		boolean allow = false;
		if (node.isDirectory()) {
			if (node.isModulePackageRoot()) {
				if (isFileSelectionEnabled()) {
					allow = true;
				}
			}
			else if (isFolderSelectionEnabled()) {
				if (node.isRoot()) {
					if (isRootSelectionEnabled()) {
						allow = true;
					}
				} else {
					allow = true;
				}
			}
		}
		else {
			// パッケージ選択では、実ファイルの選択は許可しない
			allow = false;
		}
		if (!node.isRoot()) {
		}
		return allow;
	}

	@Override
	protected boolean canCreateFolder(ModuleFileTreeNode node) {
		boolean allow = false;
		if (node.isDirectory()) {
			if (!node.isAssignedModulePackageFile()
				&& !node.isModulePackageRoot())
			{
				allow = true;
			}
		}
		return allow;
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------

	static protected class PackageFileFilter implements VirtualFileFilter
	{
		public boolean accept(VirtualFile pathname) {
			try {
				// 隠しファイルは許可しない
				if (pathname.isHidden()) {
					return false;
				}
				
				// フォルダではない場合は許可しない
				if (!pathname.isDirectory()) {
					return false;
				}
				
				// 最上位のモジュールパッケージルートのみ許可する
				return true;
			}
			catch (SecurityException ex) {
				// アクセス時にセキュリティ例外が発生した場合は許可しない
				return false;
			}
		}
	}
	
	static protected class PackageFolderFilter implements VirtualFileFilter
	{
		public boolean accept(VirtualFile pathname) {
			try {
				// 隠しファイルは許可しない
				if (pathname.isHidden()) {
					return false;
				}

				// フォルダではない場合は許可しない
				if (!pathname.isDirectory()) {
					return false;
				}

				// プロジェクトルートではないモジュールパッケージルートは、
				// 内部変更不可とするので許可しない
				boolean rootProj = ModuleFileTreeModel.isProjectRootFile(pathname);
				boolean rootPack = ModuleFileTreeModel.isModulePackageRootFile(pathname);
				if (rootPack && !rootProj) {
					return false;
				}

				// 上記以外のものは許可する
				return true;
			}
			catch (SecurityException ex) {
				// アクセス時にセキュリティ例外が発生した場合は許可しない
				return false;
			}
		}
	}
}
