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
 *  Copyright 2007-2012  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)VirtualFileManager.java	2.0.0	2012/10/26
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)VirtualFileManager.java	1.14	2009/12/09
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.io;

import java.io.File;
import java.net.URI;
import java.util.HashMap;

/**
 * <code>VirtualFileFactory</code> のインスタンスを保持し、URI から
 * 対応する <code>VirtualFile</code> インスタンスを生成するマネージャクラス。
 * 
 * @version 2.0.0	2012/10/26
 * @since 1.14
 */
public class VirtualFileManager
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** <code>VirtualFileFactory</code> のインスタンス **/
	protected final HashMap<VirtualFileFactory, Object> _factoryMap = new HashMap<VirtualFileFactory, Object>();

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public VirtualFileManager() {}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	static public DefaultFile fromJavaFile(File file) {
		return (file==null ? null : new DefaultFile(file));
	}

	/**
	 * 指定されたオブジェクトが <code>VirtualFile</code> オブジェクトの場合はそのまま返す。
	 * <code>java.io.File</code> オブジェクトの場合は <code>VirtualFile</code> オブジェクトに変換する。
	 * @param file	変換するオブジェクト
	 * @return	変換できた場合は <code>VirtualFile</code> オブジェクト、変換できない場合は <tt>null</tt>
	 */
	static public VirtualFile toVirtualFile(Object file) {
		if (file instanceof VirtualFile) {
			return (VirtualFile)file;
		}
		else if (file instanceof File) {
			return new DefaultFile((File)file);
		}
		else {
			return null;
		}
	}

	/**
	 * ファクトリが一つも登録されていない場合は <tt>true</tt> を返す。
	 */
	public boolean isFactoryEmpty() {
		return _factoryMap.isEmpty();
	}

	/**
	 * 登録されているすべてのファクトリを破棄する。
	 */
	public void clearFactories() {
		_factoryMap.clear();
	}

	/**
	 * 指定されたファクトリが登録されているかを判定する。
	 * @param factory	判定する <code>VirtualFileFactory</code> オブジェクト
	 * @return	登録されている場合は <tt>true</tt>
	 */
	public boolean containsFactory(VirtualFileFactory factory) {
		return _factoryMap.containsKey(factory);
	}

	/**
	 * 指定されたファクトリに関連するユーザーオブジェクトを取得する。
	 * @param factory	ユーザーオブジェクトを取得するキーとなる <code>VirtualFileFactory</code> オブジェクト
	 * @return	登録されているファクトリのユーザーオブジェクトを返す。
	 * 			登録されていない場合は <tt>null</tt> を返す。ただし、ユーザーオブジェクトが <tt>null</tt> の場合もある。
	 */
	public Object getFactoryUserObject(VirtualFileFactory factory) {
		return _factoryMap.get(factory);
	}

	/**
	 * ファクトリを登録する。
	 * @param factory	登録する <code>VirtualFileFactory</code> オブジェクト
	 * @return 以前登録されていたユーザーオブジェクトを返す。
	 * @throws IllegalArgumentException	引数が <tt>null</tt> の場合
	 */
	public Object registerFactory(VirtualFileFactory factory, Object userObject) {
		if (factory == null)
			throw new IllegalArgumentException("'factory' argument is null.");
		return _factoryMap.put(factory, userObject);
	}

	/**
	 * ファクトリを除外する。
	 * @param factory	除外する <code>VirtualFileFactory</code> オブジェクト
	 * @return 以前登録されていたユーザーオブジェクトを返す。
	 */
	public Object unregisterFactory(VirtualFileFactory factory) {
		return _factoryMap.remove(factory);
	}
	
	/**
	 * 指定された URI が、登録されているファクトリでサポートされていれば <tt>true</tt> を返す。
	 * @param uri	判定する URI
	 * @return	指定された URI をサポートするファクトリが存在する場合は <tt>true</tt> を返す。
	 * @throws NullPointerException	<code>uri</code> が <tt>null</tt> の場合
	 */
	public boolean isSupportedURI(URI uri) {
		return (fromURI(uri)!=null);
	}

	/**
	 * 指定された URI をサポートするファクトリを取得する。
	 * @param uri	判定する URI
	 * @return	指定された URI をサポートする <code>VirtualFileFactory</code> オブジェクトを返す。
	 * 			サポートするファクトリが存在しない場合は <tt>null</tt> を返す。
	 * @throws NullPointerException	<code>uri</code> が <tt>null</tt> の場合
	 */
	public VirtualFileFactory getSupportedFactory(URI uri) {
		VirtualFile file = fromURI(uri);
		return (file==null ? null : file.getFactory());
	}
	
	/**
	 * 指定された URI をサポートするファクトリを使用し、URI を <code>VirtualFile</code> オブジェクトに変換する。
	 * @param uri	このシステムのモジュールアイテムを表す URI
	 * @return	生成された <code>VirtualFile</code> オブジェクトを返す。
	 * 			変換できない場合は <tt>null</tt> を返す。
	 * @throws NullPointerException	<code>uri</code> が <tt>null</tt> の場合
	 */
	public VirtualFile fromURI(URI uri) {
		if (uri == null)
			throw new NullPointerException("'uri' argument is null.");
		if (_factoryMap.isEmpty())
			return null;
		
		for (VirtualFileFactory factory : _factoryMap.keySet()) {
			try {
				VirtualFile file = factory.newFile(uri);
				return file;
			}
			catch (Throwable ignoreEx) {}
		}
		
		// no support
		return null;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
}
