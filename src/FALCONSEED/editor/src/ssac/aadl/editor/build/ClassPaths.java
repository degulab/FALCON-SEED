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
 * @(#)ClassPaths.java	1.00	2008/03/24
 *     - created by Y.Ishizuka(PieCake.inc,)
 * @(#)ClassPaths.java	1.10	2008/12/01
 *     - modified by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.editor.build;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedHashSet;

import ssac.util.Strings;

/**
 * クラス・パスのリストを保持するクラス。
 * 
 * @version 1.10	2008/12/01
 */
public final class ClassPaths
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private final LinkedHashSet<File> setPaths;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public ClassPaths() {
		this.setPaths = new LinkedHashSet<File>();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public boolean isEmpty() {
		return setPaths.isEmpty();
	}
	
	public int getPathCount() {
		return setPaths.size();
	}
	
	public boolean contains(String path) {
		if (!Strings.isNullOrEmpty(path)) {
			File file = new File(path);
			return setPaths.contains(file);
		} else {
			return false;
		}
	}
	
	public boolean contains(File file) {
		return setPaths.contains(file);
	}
	
	public String getClassPathString() {
		StringBuffer sb = new StringBuffer();
		//sb.append('"');
		if (setPaths.size() > 0) {
			Iterator<File> it = setPaths.iterator();
			File path = it.next();
			sb.append(path.getAbsolutePath());
			
			while (it.hasNext()) {
				path = it.next();
				sb.append(File.pathSeparatorChar);
				sb.append(path.getAbsolutePath());
			}
		}
		//sb.append('"');
		return sb.toString();
	}
	
	public void clear() {
		setPaths.clear();
	}
	
	public boolean addPath(String path) {
		if (!Strings.isNullOrEmpty(path)) {
			return setPaths.add(new File(path));
		} else {
			return false;
		}
	}
	
	public boolean addPath(File file) {
		if (file != null) {
			return setPaths.add(file);
		} else {
			return false;
		}
	}
	
	public void appendPaths(String...paths) {
		for (String path : paths) {
			addPath(path);
		}
	}
	
	public void appendPaths(File...files) {
		for (File path : files) {
			addPath(path);
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
}
