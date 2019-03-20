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
 *  Copyright 2007-2015  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)FSGenericFilter.java	1.0.0	2015/05/29
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.basicfilter.generic;

/**
 * 汎用フィルタ。
 * 
 * @version 1.0.0	2015/05/29
 * 
 * @author H.Deguchi(SOARS Project.)
 * @author Y.Ishizuka(PieCake.inc,)
 */
public class FSGenericFilter extends ssac.aadl.runtime.AADLModule
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static protected final String ARGTYPE_IN	= "[IN]";
	static protected final String ARGTYPE_OUT	= "[OUT]";
	static protected final String ARGTYPE_STR	= "[STR]";
	static protected final String ARGTYPE_PUB	= "[PUB]";
	static protected final String ARGTYPE_SUB	= "[SUB]";

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		FSGenericFilter aadlpg = new FSGenericFilter();
		int ret = aadlpg.aadlRun(args);
		System.exit(ret);
	}

	//------------------------------------------------------------
	// Implement ssac.aadl.runtime.AADLModule interfaces
	//------------------------------------------------------------

	@Override
	protected int _aadl$getAADLLineNumber(int javaLineNo) {
		return 0;
	}

	/*
	 * $1[IN] 対象の汎用フィルタ(Jar)
	 * $2 以降は、汎用フィルタ(Jar)に含まれる定義に基づく
	 */
	@Override
	protected int aadlRun(String[] args) {
		
		// completed
//		println("Converted Exalges, output " + toString(retrec) + " records.");
		
		// completed
		return 0;
	}

	//------------------------------------------------------------
	// AADL functions
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
	
	static public class FSGenericFieldAttribute
	{
	}
	
	static public class FSGenericFilterNode
	{
		
	}
	
	static public class FSGenericFilterInputNode extends FSGenericFilterNode
	{
		
	}
	
	static public class FSGenericFilterOutputNode extends FSGenericFilterNode
	{
		
	}

	/**
	 * 汎用フィルタにおけるフィルタ定義データを保持するオブジェクト。
	 */
	static public class FSGenericFilterDefs
	{
		private String	_name;
		private String	_title;
		private String	_desc;
		private FSGenericFilterArgList	_args;
		
		public FSGenericFilterDefs(String filterName, String filterTitle, String filterDesc) {
			_name  = filterName;
			_title = filterTitle;
			_desc  = filterDesc;
			_args  = new FSGenericFilterArgList();
		}
		
		public String getName() {
			return _name;
		}
		
		public String getTitle() {
			return _title;
		}
		
		public String getDescription() {
			return _desc;
		}
		
		public FSGenericFilterArgList getArguments() {
			return _args;
		}
	}
	
	static public class FSGenericFilterArgList extends java.util.ArrayList<FSGenericFilterArgDefs>
	{
		public FSGenericFilterArgList() {
			super();
		}
		
		public FSGenericFilterArgList(int capacity) {
			super(capacity);
		}
	}

	/**
	 * 汎用フィルタにおける引数定義を保持するオブジェクト。
	 */
	static public class FSGenericFilterArgDefs
	{
		private String	_type;
		private String	_desc;
		
		public FSGenericFilterArgDefs(String argType, String argDesc) {
			_type = argType;
			_desc = argDesc;
		}
		
		public String getType() {
			return _type;
		}
		
		public String getDescription() {
			return _desc;
		}
	}
}
