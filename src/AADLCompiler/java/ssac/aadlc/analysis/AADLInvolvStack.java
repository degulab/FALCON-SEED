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
 * @(#)AADLInvolvStack.java	1.30	2009/12/02
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadlc.analysis;

import java.util.Stack;

/**
 * 内包記述ブロック識別子のスタック
 * 
 * @version 1.30	2009/12/02
 * 
 * @since 1.30
 */
public class AADLInvolvStack
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private final Stack<AADLInvolvIdentifier> idStack = new Stack<AADLInvolvIdentifier>();

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public AADLInvolvStack() {
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public boolean isEmpty() {
		return idStack.isEmpty();
	}
	
	public int getLevelCount() {
		return idStack.size();
	}
	
	public void clear() {
		idStack.clear();
	}
	
	public boolean contains(AADLInvolvIdentifier id) {
		return idStack.contains(id);
	}
	
	public boolean containsNumber(int number) {
		for (AADLInvolvIdentifier id : idStack) {
			if (id.number() == number) {
				return true;
			}
		}
		return false;
	}
	
	public boolean containsName(String name) {
		for (AADLInvolvIdentifier id : idStack) {
			if (id.name().equals(name)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean containsLabel(String label) {
		for (AADLInvolvIdentifier id : idStack) {
			if (id.label().equals(label)) {
				return true;
			}
		}
		return false;
	}
	
	public AADLInvolvIdentifier peek() {
		if (!idStack.isEmpty())
			return idStack.peek();
		else
			return null;
	}
	
	public AADLInvolvIdentifier push() {
		AADLInvolvIdentifier id = new AADLInvolvIdentifier();
		return idStack.push(id);
	}
	
	public AADLInvolvIdentifier pop() {
		if (!idStack.isEmpty())
			return idStack.pop();
		else
			return null;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
}
