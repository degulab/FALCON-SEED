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
 * @(#)AbstractVirtualFile.java	1.14	2009/12/09
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.io;

import java.util.ArrayList;

/**
 * {@link VirtualFile} インタフェースの共通実装。
 * 
 * @version 1.14	2009/12/09
 * @since 1.14
 */
public abstract class AbstractVirtualFile implements VirtualFile
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

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public VirtualFile getParentFile() {
		String strParentPath = getParentPath();
		if (strParentPath == null)
			return null;
		else
			return getFactory().newFile(strParentPath);
	}
	
	public VirtualFile getAbsoluteFile() {
		if (isAbsolute())
			return this;
		else
			return getFactory().newFile(getAbsolutePath());
	}
	
	public VirtualFile getChildFile(String child) {
		return getFactory().newFile(this, child);
	}

	public String[] list(VirtualFilenameFilter filter) {
		String names[] = list();
		if ((names == null) || (filter == null) || (names.length==0)) {
			return names;
		}
		
		ArrayList<String> filteredNames = new ArrayList<String>(names.length);
		for (String name : names) {
			if (filter.accept(this, name)) {
				filteredNames.add(name);
			}
		}
		return filteredNames.toArray(new String[filteredNames.size()]);
	}
	
	public int countFiles() {
		if (isFile()) {
			return 1;
		}
		else if (isDirectory()) {
			int numFiles = 1;	// 自身の分
			VirtualFile[] flist = listFiles();
			if (flist != null && flist.length > 0) {
				for (VirtualFile f : flist) {
					numFiles += f.countFiles();
				}
			}
			return numFiles;
		}
		else {
			// unknown object
			return 0;
		}
	}
	
	public int countFiles(VirtualFilenameFilter filter) {
		if (isFile()) {
			return 1;
		}
		else if (isDirectory()) {
			int numFiles = 1;	// 自身の分
			VirtualFile[] flist = listFiles(filter);
			if (flist != null && flist.length > 0) {
				for (VirtualFile f : flist) {
					numFiles += f.countFiles(filter);
				}
			}
			return numFiles;
		}
		else {
			// unknown object
			return 0;
		}
	}
	
	public int countFiles(VirtualFileFilter filter) {
		if (isFile()) {
			return 1;
		}
		else if (isDirectory()) {
			int numFiles = 1;	// 自身の分
			VirtualFile[] flist = listFiles(filter);
			if (flist != null && flist.length > 0) {
				for (VirtualFile f : flist) {
					numFiles += f.countFiles(filter);
				}
			}
			return numFiles;
		}
		else {
			// unknown object
			return 0;
		}
	}
	
	public String toString() {
		return getPath();
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
}
