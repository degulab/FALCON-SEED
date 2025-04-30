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
