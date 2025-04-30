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
