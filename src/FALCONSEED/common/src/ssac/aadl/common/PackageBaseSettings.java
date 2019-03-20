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
 * @(#)PackageBaseSettings.java	1.14	2009/12/09
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.common;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import ssac.aadl.module.setting.AbstractSettings;

/**
 * モジュールマネージャ用接続先リスト設定
 * 
 * @version 1.14	2009/12/09
 * 
 * @since 1.14
 */
public class PackageBaseSettings extends AbstractSettings
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static private final String PROPERTY_FILENAME = "packbases.prefs";
	
	static private final String KEY_PREFS_LAST_BASE = "packbase.latest";
	static private final String KEY_PREFS_BASE_LIST = "packbase.baselist";

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	static private PackageBaseSettings instance = null;
	
	private URI[] baseURIs = EMPTY_URI_ARRAY;
	private URI lastBaseURI = null;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	static public PackageBaseSettings getInstance() {
		if (instance == null) {
			instance = new PackageBaseSettings(getFile());
		}
		return instance;
	}
	
	static private File getFile() {
		return StartupSettings.getConfigFile(PROPERTY_FILENAME);
	}
	
	private PackageBaseSettings(File targetFile) {
		super();
		loadForTarget(targetFile);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public boolean isSpecifiedLastPackageBase() {
		return (lastBaseURI != null);
	}
	
	public URI getLastPackageBase() {
		return lastBaseURI;
	}
	
	public void setLastPackageBase(URI uri) {
		lastBaseURI = uri;
	}
	
	public URI[] getPackageBases() {
		return baseURIs;
	}
	
	public void setPackageBases(URI...bases) {
		if (bases != null && bases.length > 0) {
			baseURIs = new URI[bases.length];
			System.arraycopy(bases, 0, baseURIs, 0, bases.length);
		} else {
			baseURIs = EMPTY_URI_ARRAY;
		}
	}

	//------------------------------------------------------------
	// Implement AbstractSettings interfaces
	//------------------------------------------------------------

	@Override
	public void commit() throws IOException {
		// 最後に選択されたベースURIの保存
		setAbsoluteURIProperty(KEY_PREFS_LAST_BASE, lastBaseURI);
		
		// ベースURIリストの保存
		setAbsoluteURIsProperty(KEY_PREFS_BASE_LIST, baseURIs);
		
		// プロパティの保存
		super.commit();
	}

	@Override
	public void rollback() {
		// プロパティの読み込み
		super.rollback();
		
		// ベースURIリストの復元
		baseURIs = getAbsoluteURIsProperty(KEY_PREFS_BASE_LIST, EMPTY_URI_ARRAY);
		
		// 最後に選択されたベースURIの保存
		lastBaseURI = getAbsoluteURIProperty(KEY_PREFS_LAST_BASE, null);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
}
