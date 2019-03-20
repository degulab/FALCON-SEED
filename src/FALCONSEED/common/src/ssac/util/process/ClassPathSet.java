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
 * @(#)ClassPathSet.java	1.17	2010/11/19
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.process;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedHashSet;

import ssac.util.Strings;

/**
 * クラス・パスのセット。
 * このクラスの実装では、{@link java.util.LinkedHashSet} の実装を使用している。
 * そのため、スレッドセーフではない。
 * 
 * @version 1.17	2010/11/19
 * @since 1.17
 */
public class ClassPathSet implements Cloneable
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private LinkedHashSet<File> _setPaths;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public ClassPathSet() {
		this._setPaths = new LinkedHashSet<File>();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public boolean isEmpty() {
		return _setPaths.isEmpty();
	}
	
	public int getPathCount() {
		return _setPaths.size();
	}
	
	public boolean contains(String path) {
		if (!Strings.isNullOrEmpty(path)) {
			File file = new File(path);
			return _setPaths.contains(file);
		} else {
			return false;
		}
	}
	
	public boolean contains(File file) {
		return _setPaths.contains(file);
	}
	
	public String getClassPathString() {
		StringBuffer sb = new StringBuffer();
		//sb.append('"');
		if (_setPaths.size() > 0) {
			Iterator<File> it = _setPaths.iterator();
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
		_setPaths.clear();
	}
	
	public boolean addPath(String path) {
		if (!Strings.isNullOrEmpty(path)) {
			return _setPaths.add(new File(path));
		} else {
			return false;
		}
	}
	
	public boolean addPath(File file) {
		if (file != null) {
			return _setPaths.add(file);
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

	@SuppressWarnings("unchecked")
	@Override
	public Object clone()
	{
		try {
			ClassPathSet newSet = (ClassPathSet)super.clone();
			newSet._setPaths = (LinkedHashSet<File>)this._setPaths.clone();
			return newSet;
		} catch (CloneNotSupportedException ex) {
			throw new InternalError();
		}
	}

	@Override
	public int hashCode() {
		return _setPaths.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		
		if (obj instanceof ClassPathSet) {
			if (this._setPaths.equals(((ClassPathSet)obj)._setPaths)) {
				return true;
			}
		}
		
		// not equals
		return false;
	}

	@Override
	public String toString() {
		return _setPaths.toString();
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
