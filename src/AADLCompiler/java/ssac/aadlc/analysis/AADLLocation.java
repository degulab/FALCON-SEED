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
 * @(#)AADLLocation.java	1.30	2009/12/02
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadlc.analysis;


/**
 * AADLコード位置情報
 * 
 * @version 1.30	2009/12/02
 * 
 * @since 1.30
 */
public final class AADLLocation implements Cloneable, Comparable<AADLLocation>
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** 行番号 **/
	private final int lineNo;
	/** 行先頭からの文字位置 **/
	private final int charPosInLine;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public AADLLocation() {
		this(0, 0);
	}
	
	public AADLLocation(int lineNo, int charPosInLine) {
		if (lineNo < 0)
			throw new IllegalArgumentException("lineNo must be greater than or equal to zero : " + lineNo);
		if (charPosInLine < 0)
			throw new IllegalArgumentException("charPosInLine must be greater than or equal to zero : " + charPosInLine);
		this.lineNo = lineNo;
		this.charPosInLine = charPosInLine;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 行番号を返す。
	 */
	public int line() {
		return lineNo;
	}

	/**
	 * 行の先頭からの文字位置を返す。
	 */
	public int charPositionInLine() {
		return charPosInLine;
	}

	/**
	 * 2つの <code>AADLLocation</code> を比較し、小さいほうを返す。
	 * 引数のどちらかが <tt>null</tt> の場合は、<tt>null</tt> ではないインスタンスを返す。
	 * 引数のどちらも <tt>null</tt> の場合は <tt>null</tt> を返す。
	 * 
	 * @param l1	比較する <code>AADLLocation</code>
	 * @param l2	比較するもう一方の <code>AADLLocation</code>
	 * @return	引数 <code>l2</code> が引数 <code>l1</code> よりも小さい場合は <code>l2</code>、
	 * 			引数 <code>l1</code> が引数 <code>l2</code> よりも小さい場合、もしくは等しい場合は <code>l1</code> を返す。
	 * 			どちらかが <tt>null</tt> の場合は、<tt>null</tt> ではないインスタンスを返す。
	 * 			どちらも <tt>null</tt> の場合は <tt>null</tt> を返す。
	 */
	static public AADLLocation min(AADLLocation l1, AADLLocation l2) {
		if (l1 == null)
			return l2;
		if (l2 == null)
			return l1;
		
		if (l2.compareTo(l1) < 0)
			return l2;
		else
			return l1;
	}
	
	/**
	 * 2つの <code>AADLLocation</code> を比較し、大きいほうを返す。
	 * 引数のどちらかが <tt>null</tt> の場合は、<tt>null</tt> ではないインスタンスを返す。
	 * 引数のどちらも <tt>null</tt> の場合は <tt>null</tt> を返す。
	 * 
	 * @param l1	比較する <code>AADLLocation</code>
	 * @param l2	比較するもう一方の <code>AADLLocation</code>
	 * @return	引数 <code>l2</code> が引数 <code>l1</code> よりも大きい場合は <code>l2</code>、
	 * 			引数 <code>l1</code> が引数 <code>l2</code> よりも大きい場合、もしくは等しい場合は <code>l1</code> を返す。
	 * 			どちらかが <tt>null</tt> の場合は、<tt>null</tt> ではないインスタンスを返す。
	 * 			どちらも <tt>null</tt> の場合は <tt>null</tt> を返す。
	 */
	static public AADLLocation max(AADLLocation l1, AADLLocation l2) {
		if (l1 == null)
			return l2;
		if (l2 == null)
			return l1;
		
		if (l2.compareTo(l1) > 0)
			return l2;
		else
			return l1;
	}

	//------------------------------------------------------------
	// Implement Comparable interfaces
	//------------------------------------------------------------

	/**
	 * 2つの <code>AADLLocation</code> を数値的に比較する。
	 * 
	 * @param aLocation	比較対象の <code>AADLLocation</code>
	 * @return	この <code>AADLLocation</code> が引数 <code>AADLLocation</code> と等しい場合には 0 を返す。
	 * 			この <code>AADLLocation</code> の行番号が引数 <code>AADLLocation</code> の行番号より小さい場合には、0 より小さい値を返す。
	 * 			この <code>AADLLocation</code> の行番号が引数 <code>AADLLocation</code> の行番号より大きい場合には、0 より大きい値を返す。
	 * 			2つの <code>AADLLocation</code> で行番号が等しい場合、
	 * 			この <code>AADLLocation</code> の文字位置が引数 <code>AADLLocation</code> の文字位置より小さい場合には 0 より小さい値、
	 * 			この <code>AADLLocation</code> の文字位置が引数 <code>AADLLocation</code> の文字位置より大きい場合には 0 より大きい値を返す。
	 */
	public int compareTo(AADLLocation aLocation) {
		if (this.lineNo < aLocation.lineNo)
			return (-1);
		if (this.lineNo > aLocation.lineNo)
			return (1);
		if (this.charPosInLine < aLocation.charPosInLine)
			return (-1);
		if (this.charPosInLine > aLocation.charPosInLine)
			return (1);
		
		return 0;	// equal
	}

	//------------------------------------------------------------
	// Implement Object interfaces
	//------------------------------------------------------------

	@Override
	protected AADLLocation clone() throws CloneNotSupportedException {
		try {
			AADLLocation v = (AADLLocation)super.clone();
			return v;
		}
		catch (CloneNotSupportedException ex) {
			throw new InternalError();
		}
	}

	@Override
	public int hashCode() {
		return (lineNo ^ charPosInLine);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		
		if (obj instanceof AADLLocation) {
			AADLLocation aLoc = (AADLLocation)obj;
			if (aLoc.lineNo==this.lineNo && aLoc.charPosInLine==this.charPosInLine) {
				return true;
			}
		}
		
		return false;
	}

	@Override
	public String toString() {
		return ("[" + lineNo + ":" + charPosInLine + "]");
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
}
