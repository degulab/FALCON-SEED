/*
 * @(#)FindReplaceInterface.java	1.00	2010/12/20
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.editor.view.dialog;

/**
 * テキストの検索／置換機能を提供するインタフェース。
 * 
 * @version 1.00	2010/12/20
 */
public interface FindReplaceInterface
{
	/**
	 * 検索操作を許可するかどうかを判定する。
	 * @return	検索操作を許可する場合は <tt>true</tt>、そうでない場合は <tt>false</tt> を返す。
	 */
	public boolean allowFindOperation();
	/**
	 * 置換操作を許可するかどうかを判定する。
	 * @return	置換操作を許可する場合は <tt>true</tt>、そうでない場合は <tt>false</tt> を返す。
	 */
	public boolean allowReplaceOperation();
	
	public boolean isIgnoreCase();
	public void setIgnoreCase(boolean ignore);
	public void putKeywordString(String strKeyword);
	public void putReplaceString(String strKeyword);
	public String getKeywordString();
	public String getReplaceString();
	public boolean findNext();
	public boolean findPrev();
	public boolean replaceNext();
	public boolean replaceAll();
	public int getLastReplacedCount();
}
