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
 * @(#)DefaultFileFactory.java	1.14	2009/12/09
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.io;

import java.net.URI;

/**
 * {@link java.io.File} を実体とする {@link VirtualFile} インスタンスを
 * 生成するための {@link VirtualFileFactory} の実装。
 * 
 * @version 1.14	2009/12/09
 * @since 1.14
 */
public class DefaultFileFactory implements VirtualFileFactory
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static private DefaultFileFactory _instance;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	private DefaultFileFactory() {}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	static public DefaultFileFactory getInstance() {
		if (_instance == null) {
			_instance = new DefaultFileFactory();
		}
		return _instance;
	}

	//------------------------------------------------------------
	// Implements VirtualFileFactory interfaces
	//------------------------------------------------------------
	
	public DefaultFile newFile(String pathname) {
		return new DefaultFile(pathname);
	}
	
	public DefaultFile newFile(String parent, String child) {
		return new DefaultFile(parent, child);
	}
	
	public DefaultFile newFile(VirtualFile parent, String child) {
		return new DefaultFile((DefaultFile)parent, child);
	}
	
	public DefaultFile newFile(URI uri) {
		return new DefaultFile(uri);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
}
