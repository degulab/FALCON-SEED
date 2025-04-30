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
