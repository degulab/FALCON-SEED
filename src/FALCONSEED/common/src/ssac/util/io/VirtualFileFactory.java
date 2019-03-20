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
 * @(#)VirtualFileFactory.java	1.14	2009/12/09
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.io;

import java.net.URI;

/**
 * ファイルオブジェクトを生成するためのファクトリーインタフェース。
 * 
 * @version 1.14	2009/12/09
 * @since 1.14
 */
public interface VirtualFileFactory
{
	/**
	 * 指定されたパス名文字列を抽象パス名に変換して、
	 * 新しい <code>VirtualFile</code> のインスタンスを生成する。
	 * 指定された文字列が空の文字列の場合、結果は空の抽象パス名になる。
	 * <p>
	 * パス名が絶対パスの場合、そのパスを許可するかどうかは、この
	 * インタフェースの実装に依存する。
	 * @param pathname	パス名文字列
	 * @return	生成されたインスタンス
	 * @throws NullPointerException	<em>pathname</em> が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	<em>pathname</em> がパス名として正しくない場合
	 */
	public VirtualFile newFile(String pathname);
	/**
	 * 親パス名文字列および子パス名文字列から、
	 * 新しい <code>VirtualFile</code> のインスタンスを生成する。
	 * <p>
	 * <em>parent</em> が <tt>null</tt> の場合、新しい <code>VirtualFile</code> の
	 * インスタンスは、指定された <em>child</em> パス名文字列のみで生成される。<br>
	 * そうでない場合、<em>parent</em> パス名文字列はディレクトリを示し、
	 * <em>child</em> パス名文字列はディレクトリまたはファイルを示す。
	 * <em>parent</em> もしくは <em>child</em> が絶対パスを表す場合、
	 * そのパスを許可するかどうか、パスをどのように合成するかどうかは、
	 * このインタフェースの実装に依存する。
	 * 
	 * @param parent	親パス名文字列
	 * @param child		子パス名文字列
	 * @return	生成されたインスタンス
	 * @throws NullPointerException	<em>child</em> が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	引数がパス名として正しくない場合
	 */
	public VirtualFile newFile(String parent, String child);
	/**
	 * 親抽象パス名および子パス名文字列から、
	 * 新しい <code>VirtualFile</code> のインスタンスを生成する。
	 * <p>
	 * <em>parent</em> が <tt>null</tt> の場合、新しい <code>VirtualFile</code> の
	 * インスタンスは、指定された <em>child</em> パス名文字列のみで生成される。<br>
	 * そうでない場合、<em>parent</em> 抽象パス名はディレクトリを示し、
	 * <em>child</em> パス名文字列はディレクトリまたはファイルを示す。
	 * <em>child</em> が絶対パスを表す場合、そのパスを許可するかどうか、
	 * パスをどのように合成するかどうかは、このインタフェースの実装に依存する。
	 * 
	 * @param parent	親抽象パス名
	 * @param child		子パス名文字列
	 * @return	生成されたインスタンス
	 * @throws NullPointerException	<em>child</em> が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	引数がパス名として正しくない場合
	 * @throws ClassCastException			<em>parent</em> のクラスが、
	 * 										このインタフェースの実装ではサポートされない場合
	 */
	public VirtualFile newFile(VirtualFile parent, String child);
	/**
	 * 指定された <em>uri</em> を抽象パス名に変換して、
	 * 新しい <code>VirtualFile</code> のインスタンスを生成する。
	 * <p>
	 * URI がどのように抽象パスに変換されるかは、このインタフェースの実装に依存する。
	 * @param uri	位置を示す URI
	 * @return	生成されたインスタンス
	 * @throws NullPointerException	<em>uri</em> が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	URI がこのインタフェースの実装ではサポートされない場合
	 */
	public VirtualFile newFile(URI uri);
}
