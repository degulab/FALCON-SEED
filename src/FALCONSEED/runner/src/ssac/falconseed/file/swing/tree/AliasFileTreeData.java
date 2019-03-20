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
 * @(#)AliasFileTreeData.java	1.20	2012/03/27
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.file.swing.tree;

import ssac.util.io.VirtualFile;

/**
 * 別名を持つ，汎用ファイルツリー専用ノードに格納するデータオブジェクト。
 * 
 * @version 1.20	2012/03/27
 * @since 1.20
 */
public class AliasFileTreeData extends DefaultFileTreeData
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private String		_aliasName;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * 指定されたファイルオブジェクトを保持する、新しいインスタンスを生成する。
	 * @param file	<code>VirtualFile</code> オブジェクト
	 */
	public AliasFileTreeData(VirtualFile file) {
		super(file);
	}
	
	public AliasFileTreeData(VirtualFile file, String aliasName) {
		super(file);
		this._aliasName = aliasName;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public String getAliasName() {
		return _aliasName;
	}
	
	public void setAliasName(String aliasName) {
		this._aliasName = aliasName;
	}

	@Override
	public String getDisplayName() {
		return (_aliasName==null ? super.getDisplayName() : _aliasName);
	}

	/**
	 * このモデルのハッシュコードを返す。
	 * @return	ハッシュコード
	 */
	@Override
	public int hashCode() {
		int h = super.hashCode();
		h = 31 * h + (_aliasName==null ? 0 : _aliasName.hashCode());
		return h;
	}

	/**
	 * このモデルが指定されたオブジェクトと等しいかどうかを判定する。
	 * 引数が <tt>null</tt> ではなく、このモデルと同じファイルまたはディレクトリを示す抽象パス名である場合だけ <tt>true</tt> を返す。
	 * @param obj	このモデルと比較されるオブジェクト
	 * @return	2 つのオブジェクトが同じ場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	@Override
	public boolean equals(Object obj) {
		if (!super.equals(obj))
			return false;
		
		String thatAlias = ((AliasFileTreeData)obj)._aliasName;
		if (thatAlias == null) {
			return (this._aliasName == null);
		} else {
			return thatAlias.equals(this._aliasName);
		}
	}
	
	/**
	 * 指定された抽象パスから、このモデルの子を表すファイルモデルを生成する。
	 * このメソッドは <code>listFiles</code> メソッドからも呼び出される。
	 * <b>注意：</b>
	 * <blockquote>
	 * この実装では、子となるファイルモデルは <code>DefaultFileTreeData</code> オブジェクトとなる。
	 * エイリアスを持つ階層を実現する場合は、このクラスの派生クラスを実装すること。
	 * </blockquote>
	 * @param file	格納するファイルオブジェクト
	 * @return <code>IFileTreeData</code> を実装するオブジェクト
	 */
	public IFileTreeData createFileDataForChild(VirtualFile file) {
		return new DefaultFileTreeData(file);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
