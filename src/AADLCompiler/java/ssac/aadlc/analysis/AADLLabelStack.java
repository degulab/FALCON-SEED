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
