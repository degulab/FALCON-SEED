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
 * @(#)PackageFileTreePane.java	1.14	2009/12/09
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.module.swing.tree;

import ssac.aadl.module.ModuleFileManager;
import ssac.util.io.VirtualFile;
import ssac.util.io.VirtualFileFilter;

/**
 * パッケージの選択を行うツリーペイン
 * 
 * @version 1.14	2009/12/09
 * @since 1.14
 */
public class PackageFileTreePane extends ModuleFileTreePane
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

	public PackageFileTreePane() {
		super();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	@Override
	protected ModuleFileTreeModel createDefaultTreeModel(VirtualFile rootFile, VirtualFileFilter vfFilter) {
		PackageFileTreeModel newModel = new PackageFileTreeModel(rootFile);
		if (rootFile != null) {
			newModel.setRootNodeIcon(ModuleFileManager.getSystemDisplayIcon(rootFile));
		}
		if (vfFilter != null) {
			newModel.setFileFilter(vfFilter);
		}
		return newModel;
	}
}
