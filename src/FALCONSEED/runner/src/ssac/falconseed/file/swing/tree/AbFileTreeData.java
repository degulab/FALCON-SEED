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
 * @(#)AbFileTreeData.java	2.0.0	2012/10/29
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AbFileTreeData.java	1.20	2012/03/27
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.file.swing.tree;

import java.util.ArrayList;

import ssac.util.io.VirtualFile;
import ssac.util.io.VirtualFileFilter;

/**
 * 汎用ファイルツリー専用ノードに格納する標準データオブジェクトの基本実装。
 * 
 * @version 2.0.0	2012/10/29
 * @since 1.20
 */
public abstract class AbFileTreeData implements IFileTreeData
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static protected IFileTreeData[] EMPTY_ARRAY = new IFileTreeData[0];

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * このモデルにファイル(抽象パス)が関連付けられていれば <tt>true</tt> を返す。
	 * @return	抽象パスが関連付けられている場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @since 2.0.0
	 */
	public boolean hasFile() {
		return (getFile() != null);
	}

	/**
	 * このモデルに関連付けられているファイルの実体の長さ(バイト数)を返す。
	 * @return	ファイルの長さ(バイト数)。ファイルが存在しない場合は 0L を返す。
	 */
	public long length() {
		VirtualFile file = getFile();
		return (file==null ? 0L : file.length());
	}

	/**
	 * このモデルに関連付けられているファイルをアプリケーションが読み込めるかどうかを判定します。
	 * @return	ファイルが存在し、さらにアプリケーションがそれを読み込める場合だけ <tt>true</tt>、
	 * 			そうでない場合は <tt>false</tt>
	 */
	public boolean canRead() {
		VirtualFile file = getFile();
		return (file==null ? false : file.canRead());
	}
	
	/**
	 * このモデルに関連付けられているファイルをアプリケーションが修正できるかどうかを判定する。
	 * @return	ファイルが実際にあり、さらにアプリケーションがそのファイルに書き込める場合は <tt>true</tt>、
	 * 			そうでない場合は <tt>false</tt>
	 */
	public boolean canWrite() {
		VirtualFile file = getFile();
		return (file==null ? false : file.canWrite());
	}

	/**
	 * このモデルに関連付けられているファイルの実体が存在するかを検証する。
	 * @return	存在する場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean exists() {
		VirtualFile file = getFile();
		return (file==null ? false : file.exists());
	}

	/**
	 * このモデルに関連付けられているファイルが隠しファイルかどうかを判定する。
	 * 「隠し」の正確な定義は、関連付けられているファイルに依存する。
	 * @return	隠しファイルと見なされる場合のみ <tt>true</tt>
	 */
	public boolean isHidden() {
		VirtualFile file = getFile();
		return (file==null ? false : file.isHidden());
	}

	/**
	 * このモデルに関連付けられているファイルが普通のファイルかどうかを判定する。
	 * ファイルは、それがディレクトリではなく、システムに依存するほかの基準を満たす場合に
	 * 「普通」のファイルと見なされる。
	 * @return	ファイルが存在し、さらにそれが普通のファイルである場合は <tt>true</tt>、
	 * 			そうでない場合は <tt>false</tt>
	 */
	public boolean isFile() {
		VirtualFile file = getFile();
		return (file==null ? false : file.isFile());
	}

	/**
	 * このモデルに関連付けられているファイルがディレクトリかどうかを判定する。
	 * @return	ファイルが存在し、さらにそれがディレクトリである場合は <tt>true</tt>、
	 * 			そうでない場合は <tt>false</tt>
	 */
	public boolean isDirectory() {
		VirtualFile file = getFile();
		return (file==null ? false : file.isDirectory());
	}

	/**
	 * このモデルに関連付けられているファイルの最終更新時刻を取得する。
	 * @return	ファイルが最後に変更された時刻を表す <code>long</code> 値。
	 * 			エポック (1970 年 1 月 1 日 0 時 0 分 0 秒、グリニッジ標準時) からミリ秒単位で測定。
	 * 			ファイルが存在しないか、入出力エラーが発生した場合は 0L
	 */
	public long lastModified() {
		VirtualFile file = getFile();
		return (file==null ? 0L : file.lastModified());
	}

	/**
	 * このモデルに関連付けられているファイルのパスを含まないファイル名を取得する。
	 * ファイルが関連付けられていない場合は，空文字列を返す。
	 * @return	<tt>null</tt> ではない，パスを含まないファイル名
	 */
	public String getFilename() {
		VirtualFile file = getFile();
		return (file==null ? "" : file.getName());
	}

	/**
	 * このモデルの表示名を取得する。このメソッドは必ず <tt>null</tt> 以外の値を返す。
	 * @return	モデルの表示名
	 */
	public String getDisplayName() {
		return getFilename();
	}

	/**
	 * このモデルが示すディレクトリ内のファイルを示すファイルモデルの配列を取得する。
	 * このモデルがディレクトリを示さない場合、このメソッドは <tt>null</tt> を返す。
	 * 結果の配列は特定の順序にはならない。
	 * @return	このモデルが示すディレクトリ内のファイルおよびディレクトリを示すファイルモデルの配列。
	 * 			配列は、ディレクトリが空の場合は空になる。このモデルがディレクトリを示さない場合、
	 * 			または入出力エラーが発生した場合は <tt>null</tt>
	 */
	public IFileTreeData[] listFiles() {
		return listFiles(null);
	}
	
	/**
	 * このモデルが示すディレクトリにあるファイルおよびディレクトリの中で、
	 * 指定されたフィルタの基準を満たすもののファイルモデルの配列を取得する。
	 * このメソッドの動作は、{@link #listFiles()} メソッドと同じだが、
	 * 返された配列内のファイルモデルはフィルタの基準を満たす必要がある。
	 * 指定された <em>filter</em> が <tt>null</tt> の場合、すべてのパス名が受け入れられる。
	 * そうでない場合、ファイルモデルがフィルタの基準を満たすのは、このフィルタの {@link ssac.util.io.VirtualFileFilter#accept(VirtualFile)} メソッドが
	 * 呼び出されたときに <tt>true</tt> の値が返される場合だけとなる。
	 * @param filter	ファイルフィルタ
	 * @return	このモデルが示すディレクトリ内のファイルおよびディレクトリを示すファイルモデルの配列。
	 * 			配列は、ディレクトリが空の場合は空になる。このモデルがディレクトリを示さない場合、
	 * 			または入出力エラーが発生した場合は <tt>null</tt>
	 */
	public IFileTreeData[] listFiles(VirtualFileFilter filter) {
		VirtualFile thisFile = getFile();
		if (thisFile == null)
			return null;
		
		VirtualFile[] files;
		if (filter != null) {
			files = thisFile.listFiles(filter);
		} else {
			files = thisFile.listFiles();
		}
		if (files == null) {
			return null;
		}
		if (files.length <= 0) {
			return EMPTY_ARRAY;
		}
		
		ArrayList<IFileTreeData> list = new ArrayList<IFileTreeData>(files.length);
		for (VirtualFile f : files) {
			list.add(createFileDataForChild(f));
		}
		
		return list.toArray(new IFileTreeData[list.size()]);
	}

	/**
	 * このモデルのハッシュコードを返す。
	 * @return	ハッシュコード
	 */
	abstract public int hashCode();

	/**
	 * このモデルが指定されたオブジェクトと等しいかどうかを判定する。
	 * 引数が <tt>null</tt> ではなく、このモデルと同じファイルまたはディレクトリを示す抽象パス名である場合だけ <tt>true</tt> を返す。
	 * @param obj	このモデルと比較されるオブジェクト
	 * @return	2 つのオブジェクトが同じ場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	abstract public boolean equals(Object obj);

	/**
	 * このオブジェクトの文字列表現を返す。
	 * このメソッドが返す文字列は、{@link #getDisplayName()} が返す文字列と同じものとなる。
	 * @return	このオブジェクトの文字列表現
	 */
	@Override
	public String toString() {
		return getDisplayName();
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
