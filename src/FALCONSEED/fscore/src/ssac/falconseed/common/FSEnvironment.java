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
 *  Copyright 2007-2012  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)FSEnvironment.java	1.20	2012/03/26
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)FSEnvironment.java	1.13	2012/02/15
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.common;

import java.io.File;

import ssac.util.properties.ExProperties;
import ssac.util.properties.JavaXmlPropertiesModel;

/**
 * FALCON-SEED 環境共通パラメータ
 * 
 * @version 1.20	2012/03/26
 * @since 1.13
 */
public class FSEnvironment
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static protected final String FILENAME_PROPERTIES = "falconseed.xml";
	
	static protected final String KEY_SYSTEM_NAME		= "system.name";
	static protected final String KEY_SYSTEM_VERSION	= "system.version";
	static protected final String KEY_SYSTEM_BUILD	= "system.build";
	static protected final String KEY_SYSTEM_TITLE	= "system.title";

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	static protected FSEnvironment	_instance;
	
	private final File				_rootDir;
	private final ExProperties		_envProps;
	
	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	static public void initialize(File fAppHomeDir) {
		// get environment root directory
		File fEnvRootDir = null;
		if (fAppHomeDir != null) {
			try {
				if (fAppHomeDir.exists() && fAppHomeDir.isDirectory()) {
					fEnvRootDir = new File(new File(fAppHomeDir, "lib"), "resources");
					if (!fEnvRootDir.exists() || !fEnvRootDir.isDirectory()) {
						fEnvRootDir = null;
					}
				}
			}
			catch (Throwable ignoreEx) {
				fEnvRootDir = null;
			}
		}
		
		// create instance
		_instance = new FSEnvironment(fEnvRootDir);
	}
	
	public FSEnvironment(File rootDir)
	{
		// デフォルトプロパティの初期化
		_envProps = new ExProperties(new JavaXmlPropertiesModel());
		_envProps.setProperty(KEY_SYSTEM_NAME, "FALCON-SEED");
		
		// プロパティのロード
		if (rootDir != null) {
			File propFile = new File(rootDir, FILENAME_PROPERTIES);
			try {
				_envProps.loadFile(propFile);
			}
			catch (Throwable ex) {
				FSStartupSettings.printLoadErrorMessage(propFile, ex);
				ex = null;
				rootDir = null;
			}
		}
		_rootDir = rootDir;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	static public FSEnvironment getInstance() {
		return _instance;
	}
	
	public File rootDirectory() {
		return _rootDir;
	}
	
	public String name() {
		return _envProps.getString(KEY_SYSTEM_NAME, "");
	}
	
	public String version() {
		return _envProps.getString(KEY_SYSTEM_VERSION, "");
	}
	
	public String buildNumber() {
		return _envProps.getString(KEY_SYSTEM_BUILD, "");
	}
	
	public String title() {
		return _envProps.getString(KEY_SYSTEM_TITLE, "");
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
