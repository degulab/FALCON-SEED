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
 * @(#)IMutableFileTreeNode.java	1.20	2012/03/27
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.file.swing.tree;

import javax.swing.tree.MutableTreeNode;

import ssac.util.io.VirtualFile;
import ssac.util.io.VirtualFileFilter;
import ssac.util.io.VirtualFilenameFilter;

/**
 * 汎用ファイルツリー専用ノードのインタフェース
 * 
 * @version 1.20	2012/03/27
 * @since 1.20
 */
public interface IMutableFileTreeNode extends MutableTreeNode
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Interfaces
	//------------------------------------------------------------

	/**
	 * このノードに関連付けられているファイルオブジェクトを取得する。
	 * @return	<code>VirtualFile</code> オブジェクト
	 */
	public VirtualFile getFileObject();

	/**
	 * このノードに新しいファイルオブジェクトを設定する。
	 * 引数に <tt>null</tt> を指定した場合の挙動は、このインタフェースを実装するクラスに依存する。
	 * @param newFile	新しい <code>VirtualFile</code> オブジェクト
	 */
	public void setFileObject(VirtualFile newFile);

	/**
	 * このノードに関連付けられているファイルの実体の長さ(バイト数)を返す。
	 * @return	ファイルの長さ(バイト数)。ファイルが存在しない場合は 0L を返す。
	 */
	public long length();

	/**
	 * このノードに関連付けられているファイルをアプリケーションが読み込めるかどうかを判定します。
	 * @return	ファイルが存在し、さらにアプリケーションがそれを読み込める場合だけ <tt>true</tt>、
	 * 			そうでない場合は <tt>false</tt>
	 */
	public boolean canRead();
	
	/**
	 * このノードに関連付けられているファイルをアプリケーションが修正できるかどうかを判定する。
	 * @return	ファイルが実際にあり、さらにアプリケーションがそのファイルに書き込める場合は <tt>true</tt>、
	 * 			そうでない場合は <tt>false</tt>
	 */
	public boolean canWrite();

	/**
	 * このノードに関連付けられているファイルの実体が存在するかを検証する。
	 * @return	存在する場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean exists();

	/**
	 * このノードに関連付けられているファイルが隠しファイルかどうかを判定する。
	 * 「隠し」の正確な定義は、関連付けられているファイルに依存する。
	 * @return	隠しファイルと見なされる場合のみ <tt>true</tt>
	 */
	public boolean isHidden();

	/**
	 * このノードに関連付けられているファイルが普通のファイルかどうかを判定する。
	 * ファイルは、それがディレクトリではなく、システムに依存するほかの基準を満たす場合に
	 * 「普通」のファイルと見なされる。
	 * @return	ファイルが存在し、さらにそれが普通のファイルである場合は <tt>true</tt>、
	 * 			そうでない場合は <tt>false</tt>
	 */
	public boolean isFile();

	/**
	 * このノードに関連付けられているファイルがディレクトリかどうかを判定する。
	 * @return	ファイルが存在し、さらにそれがディレクトリである場合は <tt>true</tt>、
	 * 			そうでない場合は <tt>false</tt>
	 */
	public boolean isDirectory();

	/**
	 * このノードに関連付けられているファイルの最終更新時刻を取得する。
	 * @return	ファイルが最後に変更された時刻を表す <code>long</code> 値。
	 * 			エポック (1970 年 1 月 1 日 0 時 0 分 0 秒、グリニッジ標準時) からミリ秒単位で測定。
	 * 			ファイルが存在しないか、入出力エラーが発生した場合は 0L
	 */
	public long lastModified();

	/**
	 * このノードに関連付けられているファイルのパスを含まないファイル名を取得する。
	 * ファイルが関連付けられていない場合は，空文字列を返す。
	 * @return	<tt>null</tt> ではない，パスを含まないファイル名
	 */
	public String getFilename();

	/**
	 * このノードの表示名を取得する。このメソッドは必ず <tt>null</tt> 以外の値を返す。
	 * 通常は、{@link #getFilename()} の戻り値を返す。
	 * @return	ノードの表示名
	 */
	public String getDisplayName();

	/**
	 * このノードが示すディレクトリ内のファイルを示すファイルオブジェクトの配列を取得する。
	 * このモデルがディレクトリを示さない場合、このメソッドは <tt>null</tt> を返す。
	 * 結果の配列は特定の順序にはならない。
	 * @return	このノードが示すディレクトリ内のファイルおよびディレクトリを示すファイルオブジェクトの配列。
	 * 			配列は、ディレクトリが空の場合は空になる。このノードがディレクトリを示さない場合、
	 * 			または入出力エラーが発生した場合は <tt>null</tt>
	 */
	public VirtualFile[] listFiles();
	/**
	 * このノードが示すディレクトリにあるファイルおよびディレクトリの中で、
	 * 指定されたフィルタの基準を満たすもののファイルオブジェクトの配列を取得する。
	 * このメソッドの動作は、{@link #listFiles()} メソッドと同じだが、
	 * 返された配列内のファイルオブジェクトはフィルタの基準を満たす必要がある。
	 * 指定された <em>filter</em> が <tt>null</tt> の場合、すべてのパス名が受け入れられる。
	 * そうでない場合、ファイルオブジェクトがフィルタの基準を満たすのは、このフィルタの {@link ssac.util.io.VirtualFilenameFilter#accept(VirtualFile, String)} メソッドが
	 * 呼び出されたときに <tt>true</tt> の値が返される場合だけとなる。
	 * @param filter	ファイルフィルタ
	 * @return	このノードが示すディレクトリ内のファイルおよびディレクトリを示すファイルオブジェクトの配列。
	 * 			配列は、ディレクトリが空の場合は空になる。このノードがディレクトリを示さない場合、
	 * 			または入出力エラーが発生した場合は <tt>null</tt>
	 */
	public VirtualFile[] listFiles(VirtualFilenameFilter filter);
	/**
	 * このノードが示すディレクトリにあるファイルおよびディレクトリの中で、
	 * 指定されたフィルタの基準を満たすもののファイルオブジェクトの配列を取得する。
	 * このメソッドの動作は、{@link #listFiles()} メソッドと同じだが、
	 * 返された配列内のファイルオブジェクトはフィルタの基準を満たす必要がある。
	 * 指定された <em>filter</em> が <tt>null</tt> の場合、すべてのパス名が受け入れられる。
	 * そうでない場合、ファイルオブジェクトがフィルタの基準を満たすのは、このフィルタの {@link ssac.util.io.VirtualFileFilter#accept(VirtualFile)} メソッドが
	 * 呼び出されたときに <tt>true</tt> の値が返される場合だけとなる。
	 * @param filter	ファイルフィルタ
	 * @return	このノードが示すディレクトリ内のファイルおよびディレクトリを示すファイルオブジェクトの配列。
	 * 			配列は、ディレクトリが空の場合は空になる。このモデルがディレクトリを示さない場合、
	 * 			または入出力エラーが発生した場合は <tt>null</tt>
	 */
	public VirtualFile[] listFiles(VirtualFileFilter filter);
}
