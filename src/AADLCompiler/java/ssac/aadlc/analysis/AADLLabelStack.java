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
 * @(#)AADLLabelStack.java	1.30	2009/12/02
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadlc.analysis;

import java.util.Stack;

/**
 * 制御文用ラベルのスコープスタック
 * 
 * @version 1.30	2009/12/02
 * 
 * @since 1.30
 */
public class AADLLabelStack
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private final Stack<String> labelStack = new Stack<String>();

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public AADLLabelStack() {
		
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public boolean isEmpty() {
		return labelStack.isEmpty();
	}
	
	public int getLevelCount() {
		return labelStack.size();
	}
	
	public void clear() {
		labelStack.clear();
	}
	
	public boolean contains(String label) {
		return labelStack.contains(label);
	}
	
	public String peek() {
		if (!labelStack.isEmpty())
			return labelStack.peek();
		else
			return null;
	}
	
	public void push(String label) {
		if (label != null && label.length() > 0)
			labelStack.push(label);
		else
			labelStack.push(AADLAnalyzer.ANONYMOUS_LABEL);
	}
	
	public String pop() {
		if (!labelStack.isEmpty())
			return labelStack.pop();
		else
			return null;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
}
