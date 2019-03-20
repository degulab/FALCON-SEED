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
 * @(#)CommandLineArgs.java	1.30	2009/12/02
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)CommandLineArgs.java	1.10	2008/05/20
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)CommandLineArgs.java	1.00	2007/11/29
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadlc.compile;

import java.io.File;
import java.nio.charset.Charset;
import java.util.HashMap;

import ssac.aadlc.AADLConstants;
import ssac.aadlc.io.ReportPrinter;

/**
 * AADLコンパイラーのコマンドライン引数解析クラス
 * 
 * @version 1.30	2009/12/02
 */
public class CommandLineArgs
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	static private final int OPT_AADLC_DEBUG		= -1;
	
	static private final int OPT_SHOW_VERSION	= 11;
	static private final int OPT_SHOW_HELP		= 12;
	static private final int OPT_FLAG_VERBOSE	= 13;
	static private final int OPT_FLAG_NOWARN		= 14;
	static private final int OPT_FLAG_NOMANIFEST	= 15;
	static private final int OPT_CHECK_ONLY		= 21;
	static private final int OPT_ENCODING		= 22;
	static private final int OPT_USER_CLASSPATH	= 31;
	static private final int OPT_SRCDEST_PATH	= 32;
	static private final int OPT_DEST_NAME		= 33;
	static private final int OPT_MANIFEST		= 34;
	static private final int OPT_JARPROPFILE		= 41;
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	static private HashMap<String,Integer> optMap = new HashMap<String,Integer>();
	static {
		optMap.put("-debug",		OPT_AADLC_DEBUG);
		optMap.put("-version",		OPT_SHOW_VERSION);
		optMap.put("-?",			OPT_SHOW_HELP);
		optMap.put("-help",			OPT_SHOW_HELP);
		optMap.put("-verbose",		OPT_FLAG_VERBOSE);
		optMap.put("-nowarn",		OPT_FLAG_NOWARN);
		optMap.put("-encoding",		OPT_ENCODING);
		optMap.put("-c",			OPT_CHECK_ONLY);
		optMap.put("-classpath",	OPT_USER_CLASSPATH);
		optMap.put("-sd",			OPT_SRCDEST_PATH);
		optMap.put("-d",			OPT_DEST_NAME);
		optMap.put("-m",			OPT_MANIFEST);
		optMap.put("-manifest",		OPT_MANIFEST);
		optMap.put("-M",			OPT_FLAG_NOMANIFEST);
		optMap.put("-nomanifest",	OPT_FLAG_NOMANIFEST);
		optMap.put("-jarpropfile",	OPT_JARPROPFILE);
	}
	
	private boolean flgDebug;				// Flag : debug
	
	private boolean flgVersion;			// Flag : show version
	private boolean flgHelp;				// Flag : show help
	
	private boolean flgNoWarn;			// Flag : nowarn
	private boolean flgVerbose;			// Flag : verbose
	private boolean flgCheckOnly;			// Flag : compile only
	private boolean flgNoManifest;		// Flag : no manifest
	
	private String	strEncoding;			// Encoding charset name
	private String strClassPath;			// user class path
	private String strSourceDestPath;		// source file output path
	private String strDestName;			// dest file name(with path)
	private String strManifest;			// Manifest file name(with path)
	private String strJarPropPath;			// Jar module properties(with path)
	
	private String strAADLName;			// AADL source file name(with path)
	
	private boolean flgTrust;				// 正常フラグ

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public CommandLineArgs() {
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
	
	public boolean isCheckOnly() {
		return this.flgCheckOnly;
	}
	
	public boolean isNoManifest() {
		return this.flgNoManifest;
	}
	
	public boolean hasEncoding() {
		return (this.strEncoding != null);
	}
	
	public boolean hasClassPath() {
		return (this.strClassPath != null);
	}
	
	public boolean hasSourceDestPath() {
		return (this.strSourceDestPath != null);
	}
	
	public boolean hasDestFilename() {
		return (this.strDestName != null);
	}
	
	public boolean hasManifest() {
		return (this.strManifest != null);
	}
	
	public boolean hasJarPropertiesPath() {
		return (this.strJarPropPath != null);
	}
	
	public String getEncoding() {
		return this.strEncoding;
	}
	
	public String getClassPath() {
		return this.strClassPath;
	}
	
	public String getSourceDestPath() {
		return this.strSourceDestPath;
	}
	
	public String getDestFilename() {
		return this.strDestName;
	}
	
	public String getManifest() {
		return this.strManifest;
	}
	
	public String getJarPropertiesPath() {
		return this.strJarPropPath;
	}
	
	public String getAADLFilename() {
		return this.strAADLName;
	}
	
	public void parse(String[] args) throws IllegalArgumentException
	{
		// initialize
		initialize();
		
		// parse
		for (int i = 0; i < args.length; i++) {
			Integer optID = optMap.get(args[i]);
			if (optID != null) {
				//--- options
				switch (optID.intValue()) {
					case OPT_AADLC_DEBUG :
						this.flgDebug = true;
						break;
					case OPT_SHOW_VERSION :
						this.flgVersion = true;
						//@@@ modified by Y.Ishizuka : 2008/05/20
						//--- バージョン情報を表示しても停止しないように変更
						break;
						//return;
						//@@@ end of modified.
					case OPT_SHOW_HELP :
						this.flgHelp = true;
						return;
					case OPT_FLAG_VERBOSE :
						this.flgVerbose = true;
						break;
					case OPT_FLAG_NOWARN :
						this.flgNoWarn = true;
						break;
					case OPT_FLAG_NOMANIFEST :
						this.flgNoManifest = true;
						break;
					case OPT_CHECK_ONLY :
						this.flgCheckOnly = true;
						break;
					case OPT_ENCODING :
						this.strEncoding = getOptionParameter(args, i++);
						//--- check Encoding
						checkOptionEncoding();
						break;
					case OPT_USER_CLASSPATH :
						this.strClassPath = getOptionParameter(args, i++);
						//--- check Class path
						checkOptionClassPath();
						break;
					case OPT_SRCDEST_PATH :
						this.strSourceDestPath = getOptionParameter(args, i++);
						//--- check source dest path
						checkOptionSourceDestPath();
						break;
					case OPT_DEST_NAME :
						this.strDestName = getOptionParameter(args, i++);
						//--- check dest filename
						checkOptionDestFilename();
						break;
					case OPT_MANIFEST :
						this.strManifest = getOptionParameter(args, i++);
						//--- check manifest
						checkOptionManifestFilename();
						break;
					case OPT_JARPROPFILE :
						this.strJarPropPath = getOptionParameter(args, i++);
						//--- check properties path
						checkOptionJarPropertiesPath();
						break;
					default :
						this.flgTrust = false;
						throw new IllegalArgumentException("Illegal arg[" + i + "] option.");
				}
			}
			else {
				//--- AADL file path
				if ((i+1) != args.length) {
					// error
					throw new IllegalArgumentException("Two or more files cannot be specified.");
				}
				this.strAADLName = args[i];
			}
		}
		
		// Check
		/*
		//--- check Encoding
		checkOptionEncoding();
		//--- check Class path
		checkOptionClassPath();
		//--- check manifest
		checkOptionManifestFilename();
		//--- check source dest path
		checkOptionSourceDestPath();
		//--- check dest filename
		checkOptionDestFilename();
		*/
		//--- check AADL filename
		checkAADLFilename();
		
		// Completed!
	}
	
	static public void showVersion(ReportPrinter out) {
		out.println(AADLConstants.VERSION_TEXT);
	}
	
	static public void showHelp(ReportPrinter out) {
		out.println("Usage: aadlc [-options] aadlfile");
		out.println();
		out.println("where options include:");
		//@@@ modified by Y.Ishizuka : 2008/05/20
		//--- バージョン情報を表示しても停止しないように変更
		out.println("    -version    print product version");
		//out.println("    -version    print product version and exit");
		//@@@ end of modified.
		out.println("    -? -help    print this help message and exit");
		out.println("    -c          check syntax only");
		out.println("    -encoding <encoding>");
		out.println("                Set the source file encoding name, such as");
		out.println("                EUCJIS/SJIS. If -encoding is not specified,");
		out.println("                the platform default converter is used.");
		out.println("    -classpath <classpath>");
		out.println("                A ; separated list of directories, JAR archives,");
		out.println("                and ZIP archives to search for class files.");
		out.println("    -d <jarfile>");
		out.println("                Set the destination Jar file name.");
		out.println("                The destination Jar file already exist; aadlc");
		out.println("                overwrites the file. If -d is not specified,");
		out.println("                aadlc creates Jar file by using the AADL program");
		out.println("                name.");
		out.println("    -sd <dir>   Set the destination directory for JAVA source");
		out.println("                file converted from AADL. The destination");
		out.println("                directory must already exist; aadlc will not");
		out.println("                create the destination directory.");
		out.println("                This option can be used with '-c'.");
		out.println("    -m <manifest>");
		out.println("    -manifest <manifest>");
		out.println("                Manifest information is taken from the specified");
		out.println("                manifest file.");
		out.println("    -M -nomanifest");
		out.println("                Do not create a manifest file entry in Jar file.");
		out.println("                If this option specified, ignore '-m(-manifest)'.");
		out.println("    -jarpropfile <propfile>");
		out.println("                Set the AADL JAR properties file name.");
		out.println("    -nowarn     Disable warning messages.");
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
		flgCheckOnly = false;
		flgNoManifest = false;
		
		strEncoding = null;
		strClassPath = null;
		strSourceDestPath = null;
		strDestName = null;
		strManifest = null;
		strJarPropPath = null;
		
		strAADLName = null;
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
	private void checkOptionEncoding() throws IllegalArgumentException
	{
		if (this.strEncoding == null)
			return;
		
		// encoding check
		try {
			Charset.forName(this.strEncoding);
		}
		catch (Exception ex) {
			String msg = String.format("'%s' is not supported encoding.", this.strEncoding);
			this.strEncoding = null;
			throw new IllegalArgumentException(msg, ex);
		}
	}

	// 'classpath' option
	private void checkOptionClassPath() throws IllegalArgumentException
	{
		if (this.strClassPath == null)
			return;
		
		// class path check
		String[] paths = this.strClassPath.split(";");
		for (String path : paths) {
			boolean isExist;
			try {
				File cp = new File(path);
				isExist = cp.exists();
			} catch (Exception ex) {
				isExist = false;
			}
			if (!isExist) {
				// Error
				String msg = String.format("Class path '%s' is not found!", path);
				throw new IllegalArgumentException(msg);
			}
		}
	}

	// source dest path option
	private void checkOptionSourceDestPath() throws IllegalArgumentException
	{
		if (this.strSourceDestPath == null)
			return;
		
		// source dest path check
		boolean isExist, isDirectory;
		try {
			File sd = new File(this.strSourceDestPath);
			isExist = sd.exists();
			isDirectory = sd.isDirectory();
		} catch (Exception ex) {
			isExist = false;
			isDirectory = false;
		}
		//--- check exists
		if (!isExist) {
			// Error
			String msg = String.format("Source destination directory '%s' is not found!", this.strSourceDestPath);
			throw new IllegalArgumentException(msg);
		}
		//--- check is directory
		if (!isDirectory) {
			// Error
			String msg = String.format("Source destination '%s' is not directory!", this.strSourceDestPath);
			throw new IllegalArgumentException(msg);
		}
	}

	// dest filename option
	private void checkOptionDestFilename() {
		if (this.strDestName == null)
			return;
		
		// dest filename check
		//--- no check
	}
	
	// Jar properties path option
	private void checkOptionJarPropertiesPath() {
		if (this.strJarPropPath == null)
			return;
		
		// path check
		boolean isExist = false;
		boolean isFile  = false;
		boolean canRead = false;
		try {
			File f = new File(this.strJarPropPath);
			isExist = f.exists();
			isFile  = f.isFile();
			canRead = f.canRead();
		} catch (Exception ex) {}
		//--- check exists
		if (!isExist) {
			// Error
			String msg = String.format("Jar properties file '%s' is not found!", this.strJarPropPath);
			throw new IllegalArgumentException(msg);
		}
		//--- check is file
		if (!isFile) {
			// Error
			String msg = String.format("Jar properties file '%s' is not File!", this.strJarPropPath);
			throw new IllegalArgumentException(msg);
		}
		//--- check can read
		if (!canRead) {
			// Error
			String msg = String.format("Jar properties file '%s' cannot read!", this.strJarPropPath);
			throw new IllegalArgumentException(msg);
		}
	}
	
	// manifest filename option
	private void checkOptionManifestFilename() throws IllegalArgumentException
	{
		if (this.strManifest == null)
			return;
		
		// manifest
		boolean isExist, isFile;
		try {
			File mf = new File(this.strManifest);
			isExist = mf.exists();
			isFile = mf.isFile();
		} catch (Exception ex) {
			isExist = false;
			isFile = false;
		}
		//--- check exists
		if (!isExist) {
			// Error
			String msg = String.format("Manifest '%s' is not found!", this.strManifest);
			throw new IllegalArgumentException(msg);
		}
		//--- check is file
		if (!isFile) {
			// Error
			String msg = String.format("Manifest '%s' is not file!", this.strManifest);
			throw new IllegalArgumentException(msg);
		}
	}
	
	// AADL source file check
	private void checkAADLFilename() throws IllegalArgumentException
	{
		if (this.strAADLName == null || this.strAADLName.length() <= 0) {
			// Error
			throw new IllegalArgumentException("AADL file name is nothing!");
		}
		
		boolean isExist, isFile;
		try {
			File asf = new File(this.strAADLName);
			isExist = asf.exists();
			isFile = asf.isFile();
		} catch (Exception ex) {
			isExist = false;
			isFile = false;
		}
		//--- check exists
		if (!isExist) {
			// Error
			String msg = String.format("AADL source file '%s' is not found!", this.strAADLName);
			throw new IllegalArgumentException(msg);
		}
		//--- check is file
		if (!isFile) {
			// Error
			String msg = String.format("AADL source file '%s' is not file!", this.strAADLName);
			throw new IllegalArgumentException(msg);
		}
	}
}
