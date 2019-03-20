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
 * @(#)PackageBaseLocation.java	1.14	2009/12/09
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.module;

import java.net.URI;

import javax.swing.Icon;

import ssac.util.io.VirtualFile;

/**
 * パッケージ格納位置の基準となる位置を示す抽象パス
 * 
 * @version 1.14	2009/12/09
 * @since 1.14
 */
public class PackageBaseLocation implements Comparable<PackageBaseLocation>
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private final URI			_baseURI;
	private final VirtualFile	_baseFile;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public PackageBaseLocation(URI uri) {
		if (uri == null)
			throw new IllegalArgumentException("uri argument is null!");
		VirtualFile file = ModuleFileManager.fromURI(uri);
		if (file == null)
			throw new IllegalArgumentException("uri not supported : " + uri.toString());
		this._baseURI = uri;
		this._baseFile = file;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public URI getURI() {
		return _baseURI;
	}
	
	public VirtualFile getFileObject() {
		return _baseFile;
	}
	
	public Icon getDisplayIcon() {
		if (_baseFile == null)
			return null;
		return ModuleFileManager.getSystemDisplayIcon(_baseFile);
	}

	public int compareTo(PackageBaseLocation obj) {
		return this._baseURI.compareTo(obj._baseURI);
	}

	@Override
	public int hashCode() {
		return _baseURI.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		
		if (obj instanceof PackageBaseLocation) {
			return ((PackageBaseLocation)obj)._baseURI.equals(this._baseURI);
		}
		
		return false;
	}

	@Override
	public String toString() {
		return _baseFile.toString();
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
}
