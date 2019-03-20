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
package ssac.util.swing.list;

import java.io.File;

/**
 * パス情報を保持するクラス。
 * 
 * @version 1.00 2008/03/24
 */
public class FileListElement
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private final File targetFile;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public FileListElement(String pathname) {
		this(new File(pathname));
	}
	
	public FileListElement(File file) {
		this.targetFile = file;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public File getFile() {
		return this.targetFile;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof FileListElement) {
			return this.targetFile.equals(((FileListElement)obj).targetFile);
		}
		
		// not equal
		return false;
	}

	@Override
	public int hashCode() {
		return this.targetFile.hashCode();
	}

	@Override
	public String toString() {
		String strout = this.targetFile.getAbsolutePath();
		if (this.targetFile.isDirectory()) {
			if (!strout.endsWith(File.separator)) {
				strout = strout + File.separator;
			}
		}
		return strout;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
}
