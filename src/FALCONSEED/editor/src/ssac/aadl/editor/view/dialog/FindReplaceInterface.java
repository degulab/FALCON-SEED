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
 *  Copyright 2007-2010  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)FindReplaceInterface.java	1.16	2010/09/27
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)FindReplaceInterface.java	1.04	2008/08/18
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.editor.view.dialog;

/**
 * テキストの検索／置換機能を提供するインタフェース。
 * 
 * @version 1.16	2010/09/27
 * 
 * @since 1.04
 */
public interface FindReplaceInterface
{
	/**
	 * 検索操作を許可するかどうかを判定する。
	 * @return	検索操作を許可する場合は <tt>true</tt>、そうでない場合は <tt>false</tt> を返す。
	 * @since 1.16
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
