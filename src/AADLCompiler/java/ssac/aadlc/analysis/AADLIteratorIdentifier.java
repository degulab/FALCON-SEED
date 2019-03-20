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
 * @(#)AADLIteratorIdentifier.java	1.50	2010/09/27
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadlc.analysis;

/**
 * AADLにおけるイテレータ識別子。
 * このクラスは、内包記述におけるイテレータ固有のユニークなIDを保持する。
 * このクラスは不変である。
 * 
 * @version 1.50	2010/09/27
 * 
 * @since 1.50
 */
public class AADLIteratorIdentifier implements Comparable<AADLIteratorIdentifier>
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** 次の内包記述ブロックの ID **/
	static private int nextItNo = 1;

	/** ID **/
	private final int itNo;
	/** 変数名 **/
	private final String itName;
	/** ラベル名 **/
	private final String itLabel;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	public AADLIteratorIdentifier() {
		this.itNo = nextItNo++;
		this.itName = makeIteratorName(this.itNo);
		this.itLabel = makeIteratorLabel(this.itNo);
	}
	
	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public final int nextNumber() {
		return nextItNo;
	}
	
	public final int number() {
		return itNo;
	}
	
	public final String name() {
		return itName;
	}
	
	public final String label() {
		return itLabel;
	}

	public int compareTo(AADLIteratorIdentifier obj) {
		int thisVal = this.itNo;
		int anotherVal = obj.itNo;
		return (thisVal<anotherVal ? -1 : (thisVal==anotherVal ? 0 : 1));
	}

	@Override
	public int hashCode() {
		return itNo;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		
		if (obj instanceof AADLIteratorIdentifier) {
			if (((AADLIteratorIdentifier)obj).itNo == this.itNo)
				return true;
		}
		
		return false;
	}

	@Override
	public String toString() {
		return String.format("[%d, %s, %s]", itNo, itName, itLabel);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	static private final String makeIteratorName(int no) {
		return String.format("iterator$%d", no);
	}
	
	static private final String makeIteratorLabel(int no) {
		return String.format("l_iterator$%d", no);
	}
}
