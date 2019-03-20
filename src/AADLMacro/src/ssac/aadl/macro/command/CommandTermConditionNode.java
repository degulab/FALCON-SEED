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
 * @(#)CommandTermConditionNode.java	1.00	2008/11/14
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.macro.command;

/**
 * AADLマクロコマンドの終了条件式のルートノード。
 * 
 * @version 1.00	2008/11/14
 *
 * @since 1.00
 */
public class CommandTermConditionNode extends CommandNode implements Cloneable
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public CommandTermConditionNode(CommandToken token) {
		super(token);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public boolean isTermExitCode(int exitCode) {
		CommandNode condNode = getChild(0);
		CommandNode valNode = condNode.getChild(0);
		CommandToken token = condNode.getToken();
		
		switch (token.getType()) {
			case CommandToken.EQUAL :
				return (exitCode == getIntegerValue(null, valNode));
			case CommandToken.NOTEQUAL :
				return (exitCode != getIntegerValue(null, valNode));
			case CommandToken.GREATER :
				return (exitCode > getIntegerValue(null, valNode));
			case CommandToken.GREATEREQUAL :
				return (exitCode >= getIntegerValue(null, valNode));
			case CommandToken.LESS :
				return (exitCode < getIntegerValue(null, valNode));
			case CommandToken.LESSEQUAL :
				return (exitCode <= getIntegerValue(null, valNode));
			default :
				throw new AssertionError();
		}
	}

	@Override
	public CommandTermConditionNode clone() throws CloneNotSupportedException
	{
		try {
			CommandTermConditionNode n = (CommandTermConditionNode)super.clone();
			return n;
		}
		catch (CloneNotSupportedException ex) {
			throw new InternalError();
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
}
