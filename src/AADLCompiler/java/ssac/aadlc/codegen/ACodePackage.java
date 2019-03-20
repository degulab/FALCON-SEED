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
 * @(#)ACodePackage.java	1.30	2009/12/02
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadlc.codegen;

import java.util.List;

import org.antlr.runtime.Token;

import ssac.aadlc.AADLMessage;
import ssac.aadlc.CompileException;
import ssac.aadlc.analysis.AADLAnalyzer;
import ssac.aadlc.analysis.type.AADLJavaAction;

/**
 * AADLパッケージ宣言
 * 
 * @version 1.30	2009/12/02
 * 
 * @since 1.30
 */
public class ACodePackage extends AADLCode
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private String packagePath = null;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	protected ACodePackage() {
		super(AADLJavaAction.instance);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	static public ACodePackage buildPackageDeclaration(AADLAnalyzer analyzer, Token keyToken, Token pathToken) {
		ACodePackage retCode = new ACodePackage();
		retCode.genPackageDeclaration(analyzer, keyToken, pathToken);
		return retCode;
	}
	
	static public ACodePackage buildPackageDeclaration(AADLAnalyzer analyzer, Token keyToken, List<Token> tokens) {
		ACodePackage retCode = new ACodePackage();
		retCode.genPackageDeclaration(analyzer, keyToken, tokens.toArray(new Token[tokens.size()]));
		return retCode;
	}
	
	public String getPackagePath() {
		return packagePath;
	}

	//------------------------------------------------------------
	// generate codes
	//------------------------------------------------------------
	
	private void genPackageDeclaration(AADLAnalyzer analyzer, Token keyToken, Token...pathTokens) {
		if (pathTokens.length <= 0) {
			String msg = AADLMessage.noPackagePath();
			throw new CompileException(keyToken.getLine(), (keyToken.getCharPositionInLine()+keyToken.getText().length()), msg);
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append(pathTokens[0].getText());
		for (int i = 1; i < pathTokens.length; i++) {
			sb.append('.');
			sb.append(pathTokens[i].getText());
		}
		packagePath = sb.toString();
		
		jlb.appendLine(keyToken.getLine(), "package ", packagePath, ";");
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
}
