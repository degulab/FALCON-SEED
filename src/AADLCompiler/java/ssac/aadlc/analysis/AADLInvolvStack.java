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
