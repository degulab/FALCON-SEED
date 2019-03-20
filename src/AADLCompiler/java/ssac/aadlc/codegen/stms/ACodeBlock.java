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
