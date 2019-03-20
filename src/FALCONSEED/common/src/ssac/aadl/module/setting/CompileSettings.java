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
 * @(#)CompileSettings.java	1.14	2009/12/09
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)CompileSettings.java	1.10	2009/01/28
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)CompileSettings.java	1.00	2008/03/24
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.module.setting;

import java.io.File;
import java.io.IOException;


/**
 * コンパイル設定情報を保持するクラス。
 * ビルドオプションダイアログのコンパイル・オプション・パネルにより設定される
 * 情報を操作するための機能を提供する。
 * 
 * @version 1.14	2009/12/09
 */
public class CompileSettings extends ExecSettings
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	static public final String SECTION = "Compile";
	
	static public final String KEY_NEXT_REVISION = SECTION + ".revision.next";
	
	//static public final String GROUP_JAVAC	= SECTION + ".JavaCompiler";
	static public final String GROUP_ENCODING	= SECTION + ".Encoding";
	static public final String GROUP_OPTIONS	= SECTION + ".Options";
	
	//static public final String KEY_JAVAC_SPECIFY	= GROUP_JAVAC + ".specify";
	//static public final String KEY_JAVAC_JAR		= GROUP_JAVAC + ".jar";
	
	//static public final String KEY_ENCODING_SPECIFY	= GROUP_ENCODING + ".specify";
	//static public final String KEY_ENCODING_NAME		= GROUP_ENCODING + ".name";
	
	static public final String KEY_OPTIONS_DEST			= GROUP_OPTIONS + ".-d";
	static public final String KEY_OPTIONS_SRCOUT			= GROUP_OPTIONS + ".-sd";
	static public final String KEY_OPTIONS_NOMANIFEST		= GROUP_OPTIONS + ".-nomanifest";
	static public final String KEY_OPTIONS_MANIFEST		= GROUP_OPTIONS + ".-manifest";
	static public final String KEY_OPTIONS_COMPILEONLY	= GROUP_OPTIONS + ".-c";
	static public final String KEY_OPTIONS_NOWARN			= GROUP_OPTIONS + ".-nowarn";
	static public final String KEY_OPTIONS_VERBOSE		= GROUP_OPTIONS + ".-verbose";
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private final TextEncodingSettings	propEncoding;

	//private File javacFile;
	private File destFile;
	private File srcOutDir;
	private File manifest;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public CompileSettings() {
		super();
		this.propEncoding = new TextEncodingSettings(props, GROUP_ENCODING);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	//
	// Next revision
	//
	
	public int getCurrentRevision() {
		return (getNextRevision() - 1);
	}
	
	public int getNextRevision() {
		return props.getInteger(KEY_NEXT_REVISION, Integer.valueOf(1));
	}
	
	public void incrementNextRevision() {
		props.setInteger(KEY_NEXT_REVISION, (getNextRevision() + 1));
	}
	
	//--- KEY_JAVAC_SPECIFY
	
	//public boolean isSpecifiedJavaCompiler() {
	//	return this.props.getBooleanValue(KEY_JAVAC_SPECIFY);
	//}
	
	//public void setJavaCompilerSpecified(boolean toSpecify) {
	//	this.props.setBooleanValue(KEY_JAVAC_SPECIFY, toSpecify);
	//}
	
	//--- KEY_JAVAC_PATH
	
	//public File getTargetJavaCompilerFile() {
	//	if (isSpecifiedJavaCompiler() && javacFile != null && javacFile.exists()) {
	//		return javacFile;
	//	}
	//	
	//	return AppSettings.getInstance().getCurrentJavaCompilerFile();
	//}
	
	//public String getTargetJavaCompilerPath() {
	//	if (isSpecifiedJavaCompiler() && javacFile != null && javacFile.exists()) {
	//		return javacFile.getPath();
	//	}
	//	
	//	return AppSettings.getInstance().getCurrentJavaCompilerPath();
	//}
	
	//public File getJavaCompilerFile() {
	//	return javacFile;
	//}
	
	//public String getJavaCompilerPath() {
	//	if (javacFile != null) {
	//		return javacFile.getPath();
	//	} else {
	//		return null;
	//	}
	//}
	
	//public void setJavaCompilerFile(File jarFile) {
	//	if (jarFile != null) {
	//		javacFile = jarFile.getAbsoluteFile();
	//	} else {
	//		javacFile = null;
	//	}
	//}
	
	//public void setJavaCompilerPath(String jarPath) {
	//	if (jarPath != null && jarPath.length() > 0) {
	//		setJavaCompilerFile(new File(jarPath));
	//	} else {
	//		javacFile = null;
	//	}
	//}
	
	//--- KEY_LAST_ENCODING
	
	public String getLastEncodingName() {
		return propEncoding.getLastEncodingName();
	}
	
	public void setLastEncodingName(String name) {
		propEncoding.setLastEncodingName(name);
	}
	
	//--- KEY_ENCODING_SPECIFY
	
	public boolean isSpecifiedEncoding() {
		return propEncoding.isSpecifiedEncoding();
	}
	
	public void setEncodingSpecified(boolean toSpecify) {
		propEncoding.setEncodingSpecified(toSpecify);
	}
	
	//--- KEY_ENCODING_NAME
	
	public String getTargetEncodingName(String defaultName, boolean ignoreLastEncoding) {
		return propEncoding.getTargetEncodingName(defaultName, ignoreLastEncoding);
	}
	
	//public String getTargetEncodingName() {
	//	String strEncoding = getEncodingName();
	//	if (isSpecifiedEncoding() && !Strings.isNullOrEmpty(strEncoding)) {
	//		return strEncoding;
	//	}
	//	
	//	return AppSettings.getInstance().getAadlSourceEncodingName();
	//}
	
	public String getEncodingName() {
		return propEncoding.getEncodingName();
	}
	
	public void setEncodingName(String name) {
		propEncoding.setEncodingName(name);
	}
	
	//--- KEY_OPTIONS_DEST
	
	public File getDestinationFile() {
		return destFile;
	}
	
	public String getDestinationPath() {
		if (destFile != null) {
			return destFile.getPath();
		} else {
			return null;
		}
	}
	
	public void setDestinationFile(File file) {
		if (file != null) {
			destFile = file.getAbsoluteFile();
		} else {
			destFile = null;
		}
	}
	
	public void setDestinationPath(String path) {
		if (path != null && path.length() > 0) {
			setDestinationFile(new File(path));
		} else {
			destFile = null;
		}
	}
	
	//--- KEY_OPTIONS_SRCOUT

	public File getSourceOutputDirFile() {
		return srcOutDir;
	}
	
	public String getSourceOutputDirPath() {
		if (srcOutDir != null) {
			return srcOutDir.getPath();
		} else {
			return null;
		}
	}
	
	public void setSourceOutputDirFile(File file) {
		if (file != null) {
			srcOutDir = file.getAbsoluteFile();
		} else {
			srcOutDir = null;
		}
	}
	
	public void setSourceOutputDirPath(String path) {
		if (path != null && path.length() > 0) {
			setSourceOutputDirFile(new File(path));
		} else {
			srcOutDir = null;
		}
	}
	
	//--- KEY_OPTIONS_NOMANIFEST
	
	public boolean isDisabledManifest() {
		return this.props.getBooleanValue(KEY_OPTIONS_NOMANIFEST);
	}
	
	public void setDisableManifest(boolean toDisable) {
		setOptionsFlag(KEY_OPTIONS_NOMANIFEST, toDisable);
	}
	
	//--- KEY_OPTIONS_MANIFEST
	
	public File getUserManifestFile() {
		return manifest;
	}
	
	public String getUserManifestPath() {
		if (manifest != null) {
			return manifest.getPath();
		} else {
			return null;
		}
	}
	
	public void setUserManifestFile(File file) {
		if (file != null) {
			manifest = file.getAbsoluteFile();
		} else {
			manifest = null;
		}
	}
	
	public void setUserManifestPath(String path) {
		if (path != null && path.length() > 0) {
			setUserManifestFile(new File(path));
		} else {
			manifest = null;
		}
	}
	
	//--- KEY_OPTIONS_COMPILEONLY
	
	public boolean isSpecifiedCompileOnly() {
		return this.props.getBooleanValue(KEY_OPTIONS_COMPILEONLY);
	}
	
	public void setCompileOnlySpecified(boolean toSpecify) {
		setOptionsFlag(KEY_OPTIONS_COMPILEONLY, toSpecify);
	}
	
	//--- KEY_OPTIONS_NOWARN
	
	public boolean isDisabledWarning() {
		return this.props.getBooleanValue(KEY_OPTIONS_NOWARN);
	}
	
	public void setWarningDisabled(boolean toDisable) {
		setOptionsFlag(KEY_OPTIONS_NOWARN, toDisable);
	}
	
	//--- KEY_OPTIONS_VERBOSE
	
	public boolean isVerbose() {
		return this.props.getBooleanValue(KEY_OPTIONS_VERBOSE);
	}
	
	public void setVerbose(boolean toEnable) {
		setOptionsFlag(KEY_OPTIONS_VERBOSE, toEnable);
	}

	//------------------------------------------------------------
	// Implement ExecSettings interfaces
	//------------------------------------------------------------

	@Override
	public void commit() throws IOException {
		// ファイルを相対パスに変換し、プロパティに保存
		//setAbsoluteJavaFileProperty(KEY_JAVAC_JAR, javacFile);
		setAbsoluteJavaFileProperty(KEY_OPTIONS_DEST, destFile);
		setAbsoluteJavaFileProperty(KEY_OPTIONS_SRCOUT, srcOutDir);
		setAbsoluteJavaFileProperty(KEY_OPTIONS_MANIFEST, manifest);
		
		// 使用しないオプションは破棄
		props.clearProperty(KEY_OPTIONS_DEST);
		props.clearProperty(KEY_OPTIONS_MANIFEST);
		props.clearProperty(KEY_OPTIONS_NOMANIFEST);

		// プロパティの保存
		super.commit();
	}

	@Override
	public void rollback() {
		// プロパティの読み込み
		super.rollback();
		
		// プロパティからファイル情報を取得する
		//--- プロパティのファイル情報は、相対パスで保存されている場合がある
		//--- 相対パスの場合は、このプロパティファイルからの相対とする
		//javacFile = getAbsoluteJavaFileProperty(KEY_JAVAC_JAR, null);
		destFile  = getAbsoluteJavaFileProperty(KEY_OPTIONS_DEST, null);
		srcOutDir = getAbsoluteJavaFileProperty(KEY_OPTIONS_SRCOUT, null);
		manifest  = getAbsoluteJavaFileProperty(KEY_OPTIONS_MANIFEST, null);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected void setOptionsFlag(String key, boolean flag) {
		if (flag) {
			this.props.setBooleanValue(key, true);
		} else {
			this.props.clearProperty(key);
		}
	}
}
