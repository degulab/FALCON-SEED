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
 * @(#)VirtualFilenameFilter.java	1.14	2009/12/09
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.io;

/**
 * このインタフェースを実装するクラスのインスタンスは、
 * ファイル名にフィルタをかけるために使用される。
 * これらのインスタンスは、<code>VirtualFile</code> インタフェースの
 * <code>list</code> メソッドによるディレクトリリストをフィルタ処理するために
 * 使われる。
 * 
 * @version 1.14	2009/12/09
 * @since 1.14
 */
public interface VirtualFilenameFilter {
	/**
	 * 指定されたファイルをファイルリストに含めるかどうかをテストする。
	 * @param dir	ファイルが見つかったディレクトリ
	 * @param name	ファイルの名前
	 * @return	名前をファイルリストに含める場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	boolean accept(VirtualFile dir, String name);
}
