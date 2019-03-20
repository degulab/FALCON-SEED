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
 *  Copyright 2007-2015  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)IMExecDefFile.java	3.2.1	2015/07/08
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)IMExecDefFile.java	2.0.0	2012/11/02
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)IMExecDefFile.java	1.00	2010/12/20
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module.swing.tree;

import ssac.util.io.VirtualFile;
import ssac.util.io.VirtualFileFilter;

/**
 * モジュール実行定義ファイルの共通モデルとなるインタフェース。
 * 本アプリケーションでは、プロジェクト名やフォルダ名・ファイル名は Windows 環境を
 * 基準とし、大文字小文字を区別しないでチェックする。
 * <p>
 * 基本的に、このインタフェースを実装するクラスのインスタンスは、不変オブジェクトである。
 * 
 * @version 3.2.1
 */
public interface IMExecDefFile
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Interfaces
	//------------------------------------------------------------

	/**
	 * このモデルに関連付けられているファイルの実体が存在するかを検証する。
	 * @return	存在する場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean exists();

	/**
	 * このモデルに関連付けられているファイルが普通のファイルかどうかを判定する。
	 * ファイルは、それがディレクトリではなく、システムに依存するほかの基準を満たす場合に
	 * 「普通」のファイルと見なされる。
	 * @return	ファイルが存在し、さらにそれが普通のファイルである場合は <tt>true</tt>、
	 * 			そうでない場合は <tt>false</tt>
	 */
	public boolean isFile();

	/**
	 * このモデルに関連付けられているファイルがディレクトリかどうかを判定する。
	 * @return	ファイルが存在し、さらにそれがディレクトリである場合は <tt>true</tt>、
	 * 			そうでない場合は <tt>false</tt>
	 */
	public boolean isDirectory();

	/**
	 * このモデルに関連付けられているファイルがモジュール実行定義を示すディレクトリかどうかを判定する。
	 * これは、ディレクトリ直下にモジュール実行定義の情報ファイルが含まれている場合に、
	 * モジュール実行定義を示すディレクトリ（モジュール実行定義データ）と見なされる。
	 * @return	ファイルが存在し、さらにそれがモジュール実行定義情報を直下に持つディレクトリである場合は <tt>true</tt>、
	 * 			そうでない場合は <tt>false</tt>
	 */
	public boolean isExecDefData();

	/**
	 * このモデルに関連付けられているファイルがモジュール実行定義を示すディレクトリであり、
	 * フィルタマクロであるかどうかを判定する。
	 * これは、ディレクトリ直下にフィルタマクロ定義情報ファイルが含まれている場合に、
	 * フィルタマクロを示すディレクトリ（フィルタマクロ定義データ）と見なされる。
	 * @return	ファイルが存在し、さらにそれがフィルタマクロ定義情報を直下に持つディレクトリである場合は <tt>true</tt>、
	 * 			そうでない場合は <tt>false</tt>
	 * @since 2.0.0
	 */
	public boolean isExecDefFilterMacro();

	/**
	 * このモデルに関連付けられているファイルがモジュール実行定義を示すディレクトリであり、
	 * 汎用フィルタであるかどうかを判定する。
	 * これは、ディレクトリ直下に汎用フィルタ定義情報ファイルが含まれている場合に、
	 * 汎用フィルタを示すディレクトリ（汎用フィルタ定義データ)とみなされる。
	 * @return	ファイルが存在し、さらにそれが汎用フィルタ定義情報を直下に持つディレクトリである場合は <tt>true</tt>、
	 * 			そうでない場合は <tt>false</tt>
	 * @since 3.2.1
	 */
	public boolean isExecDefGenericFilter();

	/**
	 * このモデルに関連付けられているファイルの最終更新時刻を取得する。
	 * @return	ファイルが最後に変更された時刻を表す <code>long</code> 値。
	 * 			エポック (1970 年 1 月 1 日 0 時 0 分 0 秒、グリニッジ標準時) からミリ秒単位で測定。
	 * 			ファイルが存在しないか、入出力エラーが発生した場合は 0L
	 */
	public long lastModified();

	/**
	 * このモデルに関連付けられているファイルのパスを含まないファイル名を取得する。
	 * @return	パスを含まないファイル名
	 */
	public String getFilename();

	/**
	 * このモデルの表示名を取得する。
	 * @return	モデルの表示名
	 */
	public String getDisplayName();

	/**
	 * このモデルに関連付けられているファイルオブジェクトを取得する。
	 * @return	<code>VirtualFile</code> オブジェクト
	 */
	public VirtualFile getFile();

	/**
	 * このモデルに関連付けられているファイルオブジェクトを、
	 * 指定された抽象パスに置き換えた、新しいモデルを生成する。
	 * 基本的に、抽象パス以外の情報は複製される。
	 * @param newFile	新しい抽象パス
	 * @return	生成された <code>IMExecDefFile</code> オブジェクト
	 */
	public IMExecDefFile replaceFile(VirtualFile newFile);

	/**
	 * このモデルが示すディレクトリ内のファイルを示すファイルモデルの配列を取得する。
	 * このモデルがディレクトリを示さない場合、このメソッドは <tt>null</tt> を返す。
	 * 結果の配列は特定の順序にはならない。
	 * @return	このモデルが示すディレクトリ内のファイルおよびディレクトリを示すファイルモデルの配列。
	 * 			配列は、ディレクトリが空の場合は空になる。このモデルがディレクトリを示さない場合、
	 * 			または入出力エラーが発生した場合は <tt>null</tt>
	 */
	public IMExecDefFile[] listFiles();
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
	public IMExecDefFile[] listFiles(VirtualFileFilter filter);

	/**
	 * このモデルのハッシュコードを返す。
	 * @return	ハッシュコード
	 */
	public int hashCode();

	/**
	 * このモデルが指定されたオブジェクトと等しいかどうかを判定する。
	 * 引数が <tt>null</tt> ではなく、このモデルと同じファイルまたはディレクトリを示す抽象パス名である場合だけ <tt>true</tt> を返す。
	 * @param obj	このモデルと比較されるオブジェクト
	 * @return	2 つのオブジェクトが同じ場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean equals(Object obj);
}
