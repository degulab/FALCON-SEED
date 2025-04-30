/*
 * @(#)ACodeBlock.java	1.00	2007/11/29
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadlc.codegen.stms;

import java.util.List;

import org.antlr.runtime.tree.CommonTree;

import ssac.aadlc.analysis.AADLAnalyzer;

/**
 * AADL ブロック文
 *
 * 
 * @version 1.00	2007/11/29
 */
public class ACodeBlock extends ACodeStatement
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	protected ACodeBlock() {
		super();
	}
	
	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 文のブロック展開
	 */
	static public ACodeBlock inBlock(AADLAnalyzer analyzer, CommonTree astTree,
										ACodeStatement...statements)
	{
		ACodeBlock retCode = new ACodeBlock();
		retCode.genWithBlock(analyzer, astTree, statements);
		return retCode;
	}

	/**
	 * 文のブロック展開
	 */
	static public ACodeBlock inBlock(AADLAnalyzer analyzer, CommonTree astTree,
										List<ACodeStatement> statements)
	{
		ACodeStatement[] stms;
		if (statements != null)
			stms = statements.toArray(new ACodeStatement[statements.size()]);
		else
			stms = new ACodeStatement[0];
		return inBlock(analyzer, astTree, stms);
	}

	/**
	 * 文の展開(ブロックに含まない)
	 */
	static public ACodeBlock withoutBlock(AADLAnalyzer analyzer, CommonTree astTree,
											ACodeStatement...statements)
	{
		ACodeBlock retCode = new ACodeBlock();
		retCode.genWithoutBlock(analyzer, astTree, statements);
		return retCode;
	}
	
	/**
	 * 文の展開(ブロックに含まない)
	 */
	static public ACodeBlock withoutBlock(AADLAnalyzer analyzer, CommonTree astTree,
											List<ACodeStatement> statements)
	{
		ACodeStatement[] stms;
		if (statements != null)
			stms = statements.toArray(new ACodeStatement[statements.size()]);
		else
			stms = new ACodeStatement[0];
		return withoutBlock(analyzer, astTree, stms);
	}
	
	//------------------------------------------------------------
	// Code generators
	//------------------------------------------------------------
	
	private void genWithBlock(AADLAnalyzer analyzer, CommonTree astTree, ACodeStatement...statements) {
		// '{' '}' で囲む
		this.jlb.appendLine("{", astTree.getLine());
		for (ACodeStatement stm : statements) {
			if (stm != null) {
				this.jlb.add(stm.getJavaLineBuffer());
			}
		}
		this.jlb.appendLine("}", astTree.getLine());
	}
	
	private void genWithoutBlock(AADLAnalyzer analyzer, CommonTree astTree, ACodeStatement...statements) {
		// '{' '}' で囲まない
		for (ACodeStatement stm : statements) {
			if (stm != null) {
				this.jlb.add(stm.getJavaLineBuffer());
			}
		}
	}
	
	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

}
