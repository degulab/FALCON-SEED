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
