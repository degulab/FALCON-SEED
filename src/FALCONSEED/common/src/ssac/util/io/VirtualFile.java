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
 * @(#)VirtualFile.java	1.14	2009/12/09
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.io;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

/**
 * ファイル操作のための抽象化インタフェース。
 * 基本的に、{@link java.io.File} と同様のインタフェースを提供する。
 * <p>
 * このインタフェースは、{@link java.io.File} とは異なる実装を行うための
 * 抽象化インタフェースとなる。
 * 
 * @version 1.14	2009/12/09
 * @since 1.14
 */
public interface VirtualFile extends Comparable<VirtualFile>
{
	/**
	 * このオブジェクトを生成した <code>VirtualFileFactory</code> オブジェクトを返す。
	 */
	public VirtualFileFactory getFactory();

	/**
	 * この抽象パスが示すファイルの入力ストリームを取得する。
	 * @return	この抽象パスにアクセス可能な <code>InputStream</code> オブジェクト
	 * @throws FileNotFoundException	ファイルが存在しないか、
	 * 									普通のファイルではなくディレクトリであるか、
	 * 									または何らかの理由で開くことができない場合
	 * @throws SecurityException		セキュリティマネージャが存在し、
	 * 									<code>checkRead</code> メソッドがファイルへの
	 * 									読み込みアクセスを拒否する場合 
	 */
	public InputStream getInputStream() throws FileNotFoundException;
	/**
	 * この抽象パスが示すファイルへの出力ストリームを取得する。 
	 * @return	この抽象パスへ出力可能な <code>OutputStream</code> オブジェクト
	 * @throws FileNotFoundException	ファイルは存在するが、普通のファイルではなくディレクトリである場合、
	 *									ファイルは存在せず作成もできない場合、
	 *									または何らかの理由で開くことができない場合
	 * @throws SecurityException		セキュリティマネージャが存在し、
	 * 									<code>checkWrite</code> メソッドがファイルへの
	 * 									書き込みアクセスを拒否する場合 
	 */
	public OutputStream getOutputStream() throws FileNotFoundException;
	/**
	 * この抽象パスが示すファイルへの出力ストリームを取得する。
	 * @param append	<tt>true</tt> の場合、バイトはファイルの先頭ではなく最後に書き込まれる  
	 * @return	この抽象パスへ出力可能な <code>OutputStream</code> オブジェクト
	 * @throws FileNotFoundException	ファイルは存在するが、普通のファイルではなくディレクトリである場合、
	 *									ファイルは存在せず作成もできない場合、
	 *									または何らかの理由で開くことができない場合
	 * @throws SecurityException		セキュリティマネージャが存在し、
	 * 									<code>checkWrite</code> メソッドがファイルへの
	 * 									書き込みアクセスを拒否する場合 
	 */
	public OutputStream getOutputStream(boolean append) throws FileNotFoundException;

	/**
	 * <em>ancestor</em> がこの抽象パスの上位に位置するものかを判定する。
	 * 実装が異なる抽象パスの場合は <tt>false</tt> を返す。
	 * <p><b>(注)</b>
	 * <blockquote>
	 * このメソッドは正規化された抽象パスによって判定を行うものであり、
	 * ファイルシステム上の実体を考慮するものではない。
	 * </blockquote>
	 * @param ancestor	上位かどうかを判定する抽象パス
	 * @return	<em>ancestor</em> がこの抽象パスの上位であれば <tt>true</tt> を返す。
	 * 			<em>ancestor</em> が自身と等しい場合も <tt>true</tt> を返す。
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	public boolean isDescendingFrom(VirtualFile ancestor);
	/**
	 * <em>basepath</em> からの相対パスを表す文字列を返す。
	 * 相対関係ではない場合は、絶対パスを表す文字列を返す。
	 * @param basepath	相対パスの基準となる抽象パス
	 * @return	相対パスが生成できた場合は相対パスを表す文字列を返す。
	 * 			そうでない場合は絶対パスを表す文字列を返す。
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	引数の実装がこのオブジェクトの実装と異なる場合
	 */
	public String relativePathFrom(VirtualFile basepath);
	/**
	 * <em>basepath</em> からの相対パスを表す文字列を返す。
	 * 相対関係ではない場合は、絶対パスを表す文字列を返す。
	 * このメソッドが返すパス文字列の名前区切り文字は、<em>separator</em> に指定された文字となる。
	 * ただし、<em>separator</em> が実装において有効な文字ではない場合、実装標準の名前区切り文字となる。
	 * @param basepath	相対パスの基準となる抽象パス
	 * @param separator	任意の名前区切り文字
	 * @return	相対パスが生成できた場合は相対パスを表す文字列を返す。
	 * 			そうでない場合は絶対パスを表す文字列を返す。
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	引数の実装がこのオブジェクトの実装と異なる場合
	 */
	public String relativePathFrom(VirtualFile basepath, char separator);
	/**
	 * <em>basepath</em> からの相対パスを返す。
	 * 相対関係ではない場合は、絶対パスを返す。
	 * @param basepath	相対パスの基準となる抽象パス
	 * @return	相対パスが生成できた場合は相対パスを返す。
	 * 			そうでない場合は絶対パスを返す。
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	引数の実装がこのオブジェクトの実装と異なる場合
	 */
	public VirtualFile relativeFileFrom(VirtualFile basepath);
	/**
	 * <em>basepath</em> からの相対パスを返す。
	 * 相対関係ではない場合は、絶対パスを返す。
	 * このメソッドが返す抽象パスの名前区切り文字は、<em>separator</em> に指定された文字となる。
	 * ただし、<em>separator</em> が実装において有効な文字ではない場合、実装標準の名前区切り文字となる。
	 * @param basepath	相対パスの基準となる抽象パス
	 * @param separator	任意の名前区切り文字
	 * @return	相対パスが生成できた場合は相対パスを返す。
	 * 			そうでない場合は絶対パスを返す。
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	引数の実装がこのオブジェクトの実装と異なる場合
	 */
	public VirtualFile relativeFileFrom(VirtualFile basepath, char separator);
	
	public String getName();
	public String getPath();
	public String getParentPath();
	public VirtualFile getParentFile();
	public VirtualFile getChildFile(String child);
	public String getAbsolutePath();
	public VirtualFile getAbsoluteFile();
	public String getNormalizedPath();
	public String getNormalizedPath(char separator);
	public VirtualFile getNormalizedFile();
	public VirtualFile getNormalizedFile(char separator);
	
	public URL toURL() throws MalformedURLException;
	public URI toURI();
	
	public boolean isAbsolute();
	public boolean isNormalized();
	
	public boolean canRead();
	public boolean canWrite();

	public boolean isDirectory();
	public boolean isFile();
	public boolean isHidden();
	public boolean exists();
	
	public long length();
	public long lastModified();
	public boolean setLastModified(long time);
	public boolean setReadOnly();
	
	public boolean createNewFile() throws IOException;
	public boolean mkdir();
	public boolean mkdirs();
	public boolean delete();
	public boolean renameTo(VirtualFile dest);
	
	public String[] list();
	public String[] list(VirtualFilenameFilter filter);
	public VirtualFile[] listFiles();
	public VirtualFile[] listFiles(VirtualFilenameFilter filter);
	public VirtualFile[] listFiles(VirtualFileFilter filter);
	public int countFiles();
	public int countFiles(VirtualFilenameFilter filter);
	public int countFiles(VirtualFileFilter filter);
	
	public int compareTo(VirtualFile pathname);

	public int hashCode();
	public boolean equals(Object obj);
	public String toString();
}
