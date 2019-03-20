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
 *  Copyright 2007-2014  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)CommandLineParser.java	2.0.0	2014/03/18 : move 'ssac.util.*' to 'ssac.aadl.macro.util.*' (recursive) 
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)CommandLineParser.java	1.10	2009/12/02
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)CommandLineParser.java	1.00	2008/11/20
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.macro;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ssac.aadl.macro.util.Classes;
import ssac.aadl.macro.util.Strings;
import ssac.aadl.macro.util.io.ReportPrinter;

/**
 * マクロ実行エンジンのコマンドライン解析。
 * 
 * @version 2.0.0	2014/03/18
 * @since 1.00
 */
public class CommandLineParser
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	/**
	 * デバッグ・フラグ
	 */
	static private final int OPT_FLAG_DEBUG		= -1;

	/**
	 * バージョン情報フラグ
	 */
	static private final int OPT_SHOW_VERSION	= 11;
	/**
	 * ヘルプ・フラグ
	 */
	static private final int OPT_SHOW_HELP		= 12;
	/**
	 * 詳細表示フラグ
	 */
	static private final int OPT_FLAG_VERBOSE	= 13;
	/**
	 * 警告非表示フラグ
	 */
	static private final int OPT_FLAG_NOWARN		= 14;
	/**
	 * ECHOコマンド出力時の時刻非表示フラグ
	 */
	static private final int OPT_FLAG_NOTIME  	= 15;
	/**
	 * 正当性検証フラグ
	 */
	static private final int OPT_CHECK_ONLY		= 21;
	/**
	 * Macroファイル・エンコーディング・フラグ
	 */
	static private final int OPT_MACRO_ENCODING	= 22;
	/**
	 * CSVファイル・エンコーディング・フラグ
	 */
	static private final int OPT_AADL_CSV_ENCODING	= 23;
	/**
	 * TXTファイル・エンコーディング・フラグ
	 */
	static private final int OPT_AADL_TXT_ENCODING	= 24;
	/**
	 * AADL実行用クラスパス・フラグ
	 */
	static private final int OPT_AADL_LIBPATH	= 31;
	/**
	 * Java実行時の最大ヒープサイズ
	 */
	static private final int OPT_HEAP_MAX		= 32;
	/**
	 * Java実行時の追加VMオプション
	 */
	static private final int OPT_JAVAVM			= 33;

	/**
	 * ヒープサイズ指定文字列の検証用パターン
	 */
	static private final Pattern patHeapSize = Pattern.compile("\\A[1-9][0-9]*[kKmM]?\\z");

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	static private HashMap<String,Integer> optMap = new HashMap<String,Integer>();
	static {
		optMap.put("-debug",			OPT_FLAG_DEBUG);
		optMap.put("-version",			OPT_SHOW_VERSION);
		optMap.put("-?",				OPT_SHOW_HELP);
		optMap.put("-help",				OPT_SHOW_HELP);
		optMap.put("-verbose",			OPT_FLAG_VERBOSE);
		optMap.put("-nowarn",			OPT_FLAG_NOWARN);
		optMap.put("-notime",			OPT_FLAG_NOTIME);
		optMap.put("-macroencoding",	OPT_MACRO_ENCODING);
		optMap.put("-csvencoding",		OPT_AADL_CSV_ENCODING);
		optMap.put("-txtencoding",		OPT_AADL_TXT_ENCODING);
		optMap.put("-c",				OPT_CHECK_ONLY);
		optMap.put("-libpath",			OPT_AADL_LIBPATH);
		optMap.put("-heapmax",			OPT_HEAP_MAX);
		optMap.put("-javavm",			OPT_JAVAVM);
	}
	
	private boolean flgDebug;				// Flag : debug
	
	private boolean flgVersion;			// Flag : show version
	private boolean flgHelp;				// Flag : show help
	
	private boolean flgNoWarn;			// Flag : nowarn
	private boolean flgVerbose;			// Flag : verbose
	private boolean flgNoTime;			// Flag : notime
	private boolean flgCheckOnly;			// Flag : compile only
	
	//private String	strEncoding;			// Encoding charset name
	private String strMacroEncoding;		// Macro(amf/csv) file encoding
	private String strAadlCsvEncoding;		// AADL CSV file encoding
	private String strAadlTxtEncoding;		// AADL TXT file encoding
	private String strLibPath;				// AADL libraries path
	private String strLibClassPath;		// class path for AADL libraries
	private String strHeapMax;				// Java max heap size(same -Xmx option value)
	private String strJavaVMoptions;		// Java VM options(No check)
	private List<String> javaVMoptionList;	// Java VM option list
	private List<String> macroArgs;		// Macro arguments
	
	private String strFilename;			// マクロファイル名
	
	private boolean flgTrust;				// 正常フラグ
	
	private List<String> listOptions;		// 継承用オプションリスト

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public CommandLineParser() {
		super();
		initialize();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public boolean hasError() {
		return (!this.flgTrust);
	}
	
	public boolean isDebug() {
		return this.flgDebug;
	}
	
	public boolean isVersion() {
		return this.flgVersion;
	}
	
	public boolean isHelp() {
		return this.flgHelp;
	}
	
	public boolean isVerbose() {
		return this.flgVerbose;
	}
	
	public boolean isNoWarn() {
		return this.flgNoWarn;
	}
	
	public boolean isNoTime() {
		return this.flgNoTime;
	}
	
	public boolean isCheckOnly() {
		return this.flgCheckOnly;
	}
	
	public boolean hasMacroEncoding() {
		return (this.strMacroEncoding != null);
	}
	
	public boolean hasAadlCsvEncoding() {
		return (this.strAadlCsvEncoding != null);
	}
	
	public boolean hasAadlTxtEncoding() {
		return (this.strAadlTxtEncoding != null);
	}
	
	public boolean hasLibPath() {
		return (this.strLibPath != null);
	}
	
	public boolean hasLibClassPath() {
		return (this.strLibClassPath != null);
	}
	
	public boolean hasHeapMax() {
		return (this.strHeapMax != null);
	}
	
	public boolean hasJavaVMoptions() {
		return (this.strJavaVMoptions != null);
	}
	
	public String getMacroEncoding() {
		return this.strMacroEncoding;
	}
	
	public String getAadlCsvEncoding() {
		return this.strAadlCsvEncoding;
	}
	
	public String getAadlTxtEncoding() {
		return this.strAadlTxtEncoding;
	}
	
	public String getLibPath() {
		return this.strLibPath;
	}
	
	public String getLibClassPath() {
		return this.strLibClassPath;
	}
	
	public String getHeapMax() {
		return this.strHeapMax;
	}
	
	public String getJavaVMoptions() {
		return this.strJavaVMoptions;
	}
	
	public List<String> getJavaVMoptionList() {
		return this.javaVMoptionList;
	}
	
	public List<String> getMacroArguments() {
		return this.macroArgs;
	}
	
	public String getSourceFilename() {
		return this.strFilename;
	}
	
	public List<String> getInheritOptions() {
		return this.listOptions;
	}
	
	public synchronized void parse(String[] args) throws IllegalArgumentException
	{
		// initialize
		initialize();
		
		// parse
		boolean existMacroFile = false;
		ArrayList<String> argslist = new ArrayList<String>();
		for (int i = 0; i < args.length; i++) {
			Integer optID = optMap.get(args[i]);
			if (optID != null) {
				//--- options
				switch (optID.intValue()) {
					case OPT_FLAG_DEBUG :
						this.flgDebug = true;
						break;
					case OPT_SHOW_VERSION :
						this.flgVersion = true;
						//--- バージョン情報を表示しても停止しない
						break;
					case OPT_SHOW_HELP :
						this.flgHelp = true;
						return;
					case OPT_FLAG_VERBOSE :
						this.flgVerbose = true;
						break;
					case OPT_FLAG_NOWARN :
						this.flgNoWarn = true;
						break;
					case OPT_CHECK_ONLY :
						this.flgCheckOnly = true;
						break;
					case OPT_FLAG_NOTIME :
						this.flgNoTime = true;
						break;
					case OPT_MACRO_ENCODING :
						{
							String opt = args[i];
							String enc = getOptionParameter(args, i++);
							checkOptionEncoding(opt, enc);
							this.strMacroEncoding = enc;
						}
						break;
					case OPT_AADL_CSV_ENCODING :
						{
							String opt = args[i];
							String enc = getOptionParameter(args, i++);
							checkOptionEncoding(opt, enc);
							this.strAadlCsvEncoding = enc;
						}
						break;
					case OPT_AADL_TXT_ENCODING :
						{
							String opt = args[i];
							String enc = getOptionParameter(args, i++);
							checkOptionEncoding(opt, enc);
							this.strAadlTxtEncoding = enc;
						}
						break;
					case OPT_AADL_LIBPATH :
						this.strLibPath = getOptionParameter(args, i++);
						//--- check Class path
						checkOptionLibPath();
						break;
					case OPT_HEAP_MAX :
						this.strHeapMax = getOptionParameter(args, i++);
						//--- check Max heap size
						checkOptionHeapMax();
						break;
					case OPT_JAVAVM :
						this.strJavaVMoptions = getOptionParameter(args, i++);
						updateJavaVMoptionList();
						break;
					default :
						this.flgTrust = false;
						throw new IllegalArgumentException("Illegal arg[" + i + "] option.");
				}
			}
			else if (existMacroFile) {
				//--- AADL Macro arguments
				argslist.add(args[i]);
			}
			else {
				//--- AADL Macro file path
				this.strFilename = args[i];
				existMacroFile = true;
			}
		}
		if (!argslist.isEmpty()) {
			this.macroArgs = argslist;
		}
		
		listOptions = makeOptionList();
		
		// Check
		//--- check AADL filename
		checkSourceFilename();
		
		// Completed!
	}
	
	static public void showVersion(ReportPrinter out) {
		out.println(AADLMacroEngine.VERSION_TEXT);
	}
	
	static public void showHelp(ReportPrinter out) {
		out.println("Usage: AADLMacroEngine [-options] macrofile [argument ...]");
		out.println();
		out.println("where options include:");
		out.println("    -version    print product version");
		out.println("    -? -help    print this help message and exit");
		out.println("    -c          check syntax only");
		out.println("    -macroencoding <encoding>");
		out.println("                Set the Macro csv file encoding name, such as");
		out.println("                EUCJIS/SJIS. If -macroencoding is not specified,");
		out.println("                the \"MS932\"(Microsoft enhancing of Shift-JIS) is used.");
		out.println("    -csvencoding <encoding>");
		out.println("                Set the csv file encoding name for AADL, such as");
		out.println("                EUCJIS/SJIS. If -csvencoding is not specified,");
		out.println("                the platform default converter is used.");
		out.println("    -txtencoding <encoding>");
		out.println("                Set the text file encoding name for AADL, such as");
		out.println("                EUCJIS/SJIS. If -txtencoding is not specified,");
		out.println("                the platform default converter is used.");
		out.println("    -libpath <classpath>");
		out.println("                Specify a list of directories, JAR archives,");
		out.println("                and ZIP archives to search for class files for");
		out.println("                AADL libraries. Class path entries are separated");
		out.println("                by semicolons (;). Specifying -libpath overrides");
		out.println("                any setting of the AADLCLASSPATH environment variable.");
		out.println("                If -libpath is not used and AADLCLASSPATH is not set,");
		out.println("                the user class path consists of the current directory (.). ");
		out.println("    -heapmax <n>");
		out.println("                Specify the maximum size, in bytes, of the JAVA VM");
		out.println("                memory allocation pool. This value must a multiple");
		out.println("                of 1024 greater than 2MB. Append the letter k or K");
		out.println("                to indicate kilobytes, or m or M to indicate megabytes.");
		out.println("                The default value is 64MB. Examples: ");
		out.println("                    -heapmax 83886080");
		out.println("                    -heapmax 81920k");
		out.println("                    -heapmax 80m");
		out.println("                When this option specified, ignore java heap option(-Xmx / -Xms)");
		out.println("                on 'java' and '&' macro commands.");
		out.println("    -nowarn     Disable warning messages.");
		out.println("    -notime     Disable timestamp with macro engine messages.");
		out.println("    -verbose    Verbose output.");
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	private void initialize() {
		flgTrust = true;
		
		flgVersion = false;
		flgHelp = false;
		
		flgDebug = false;
		flgNoWarn = false;
		flgVerbose = false;
		flgNoTime = false;
		flgCheckOnly = false;
		
		strMacroEncoding = null;
		strAadlCsvEncoding = null;
		strAadlTxtEncoding = null;
		strLibPath  = null;
		strLibClassPath = null;
		strHeapMax  = null;
		strJavaVMoptions = null;
		javaVMoptionList = Collections.emptyList();
		macroArgs = Collections.emptyList();
		
		strFilename = null;
		
		listOptions = Collections.emptyList();
	}
	
	private List<String> makeOptionList() {
		ArrayList<String> list = new ArrayList<String>();
		//****************************************************************
		// -version, -help, -c は継承しない
		//****************************************************************
		//--- Debug
		if (flgDebug)
			list.add("-debug");
		//--- NoWarn
		if (flgNoWarn)
			list.add("-nowarn");
		//--- Verbose
		if (flgVerbose)
			list.add("-verbose");
		//--- NoTime
		if (flgNoTime)
			list.add("-notime");
		//--- encoding
		if (hasMacroEncoding()) {
			list.add("-macroencoding");
			list.add(strMacroEncoding);
		}
		if (hasAadlCsvEncoding()) {
			list.add("-csvencoding");
			list.add(strAadlCsvEncoding);
		}
		if (hasAadlTxtEncoding()) {
			list.add("-txtencoding");
			list.add(strAadlTxtEncoding);
		}
		//--- libpath
		if (hasLibPath()) {
			list.add("-libpath");
			list.add(strLibPath);
		}
		//--- heapmax
		if (hasHeapMax()) {
			list.add("-heapmax");
			list.add(strHeapMax);
		}
		//--- java VM options
		if (hasJavaVMoptions()) {
			list.add("-javavm");
			list.add(strJavaVMoptions);
		}
		// finished
		if (list.isEmpty()) {
			return Collections.emptyList();
		} else {
			list.trimToSize();
			return list;
		}
	}
	
	private String getOptionParameter(String[] args, int optpos) throws IllegalArgumentException
	{
		String retValue;
		int prmpos = optpos + 1;
		if (prmpos < args.length && args[prmpos].length() > 0) {
			retValue = args[prmpos];
		}
		else {
			retValue = null;
			String msg = String.format("There is no parameter of '%s'.", args[optpos]);
			throw new IllegalArgumentException(msg);
		}
		return retValue;
	}

	// 'encoding' option
	private void checkOptionEncoding(String optionName, String encoding) throws IllegalArgumentException
	{
		if (encoding == null || encoding.length() <= 0) {
			String msg = String.format("'%s' option is not specified encoding.", optionName);
			throw new IllegalArgumentException(msg);
		}
		
		// encoding check
		try {
			Charset.forName(encoding);
		}
		catch (Exception ex) {
			String msg = String.format("'%s' is not supported encoding at '%s' option.", encoding, optionName);
			throw new IllegalArgumentException(msg, ex);
		}
	}

	// 'libpath' option
	private void checkOptionLibPath() throws IllegalArgumentException
	{
		if (strLibPath == null) {
			strLibClassPath = null;
			return;
		}
		
		// class path check
		String[] paths = strLibPath.split(AADLMacroEngine.MACRO_CLASSPATH_SEPARATOR);
		ArrayList<String> pathList = new ArrayList<String>(paths.length);
		for (String path : paths) {
			boolean isExist;
			try {
				File cp = new File(path).getAbsoluteFile();
				isExist = cp.exists();
				pathList.add(cp.getAbsolutePath());
			} catch (Exception ex) {
				isExist = false;
			}
			if (!isExist) {
				// Error
				String msg = String.format("AADL Library path '%s' is not found!", path);
				throw new IllegalArgumentException(msg);
			}
		}
		
		// update libpath for absolute paths
		if (pathList.isEmpty()) {
			strLibPath = null;
			strLibClassPath = null;
		} else {
			strLibPath = Classes.toClassPathString(AADLMacroEngine.MACRO_CLASSPATH_SEPARATOR, pathList);
			strLibClassPath = Classes.toClassPathString(null, pathList);
		}
	}
	
	// 'heapmax' option
	private void checkOptionHeapMax() throws IllegalArgumentException
	{
		if (this.strHeapMax == null)
			return;
		
		// heap size check
		Matcher mc = patHeapSize.matcher(strHeapMax);
		if (!mc.matches()) {
			// Error
			String msg = String.format("Maximum heap size '%s' is not correct for JAVA VM.", strHeapMax);
			throw new IllegalArgumentException(msg);
		}
	}
	
	// 'javavm' option
	private void updateJavaVMoptionList() {
		if (this.strJavaVMoptions != null && this.strJavaVMoptions.length() > 0) {
			// VM引数を分解
			String[] vmArgs = Strings.splitCommandLineWithDoubleQuoteEscape(this.strJavaVMoptions);
			this.javaVMoptionList = Arrays.asList(vmArgs);
		} else {
			this.javaVMoptionList = Collections.emptyList();
		}
	}
	
	// Macro source file check
	private void checkSourceFilename() throws IllegalArgumentException
	{
		if (this.strFilename == null || this.strFilename.length() <= 0) {
			// Error
			throw new IllegalArgumentException("AADL Macro file name is nothing!");
		}
		
		boolean isExist, isFile;
		try {
			File asf = new File(this.strFilename);
			isExist = asf.exists();
			isFile = asf.isFile();
		} catch (Exception ex) {
			isExist = false;
			isFile = false;
		}
		//--- check exists
		if (!isExist) {
			// Error
			String msg = String.format("AADL Macro file '%s' is not found!", this.strFilename);
			throw new IllegalArgumentException(msg);
		}
		//--- check is file
		if (!isFile) {
			// Error
			String msg = String.format("AADL Macro file '%s' is not file!", this.strFilename);
			throw new IllegalArgumentException(msg);
		}
	}
}
