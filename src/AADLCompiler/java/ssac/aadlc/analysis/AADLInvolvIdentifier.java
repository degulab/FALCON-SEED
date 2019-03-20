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
 * @(#)AADLInvolvIdentifier.java	1.30	2009/12/02
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadlc.analysis;

/**
 * 内包記述ブロック識別子。
 * このクラスは、内包記述ブロック固有のユニークなIDを保持する。
 * このクラスは不変である。
 * 
 * @version 1.30	2009/12/02
 * 
 * @since 1.30
 */
public class AADLInvolvIdentifier implements Comparable<AADLInvolvIdentifier>
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** 次の内包記述ブロックの ID **/
	static private int nextInvNo = 1;

	/** ID **/
	private final int invNo;
	/** 変数名 **/
	private final String invName;
	/** ラベル名 **/
	private final String invLabel;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	public AADLInvolvIdentifier() {
		this.invNo = nextInvNo++;
		this.invName = makeInvolvName(this.invNo);
		this.invLabel = makeInvolvLabel(this.invNo);
	}
	
	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public final int nextNumber() {
		return nextInvNo;
	}
	
	public final int number() {
		return invNo;
	}
	
	public final String name() {
		return invName;
	}
	
	public final String label() {
		return invLabel;
	}

	public int compareTo(AADLInvolvIdentifier obj) {
		int thisVal = this.invNo;
		int anotherVal = obj.invNo;
		return (thisVal<anotherVal ? -1 : (thisVal==anotherVal ? 0 : 1));
	}

	@Override
	public int hashCode() {
		return invNo;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		
		if (obj instanceof AADLInvolvIdentifier) {
			if (((AADLInvolvIdentifier)obj).invNo == this.invNo)
				return true;
		}
		
		return false;
	}

	@Override
	public String toString() {
		return String.format("[%d, %s, %s]", invNo, invName, invLabel);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	static private final String makeInvolvName(int no) {
		return String.format("involv$%d", no);
	}
	
	static private final String makeInvolvLabel(int no) {
		return String.format("l_involv$%d", no);
	}
}
