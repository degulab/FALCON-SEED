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
 * @(#)VirtualFileFilter.java	1.14	2009/12/09
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.io;

/**
 * 抽象ファイルのフィルタ。
 * このインタフェースのインスタンスは、<code>VirtualFile</code> クラスの
 * <code>listFiles(VirutalFileFilter)</code> メソッドに渡すことができる。
 * 
 * @version 1.14	2009/12/09
 * @since 1.14
 */
public interface VirtualFileFilter {
	/**
	 * 指定された抽象パス名がパス名リストに含まれる必要があるかどうかを判定する。
	 * 
	 * @param pathname	テスト対象の抽象パス名
	 * @return	<em>pathname</em> が含まれる必要がある場合は <tt>true</tt>
	 */
	boolean accept(VirtualFile pathname);
}
