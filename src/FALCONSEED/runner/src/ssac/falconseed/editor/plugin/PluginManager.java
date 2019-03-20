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
 *  Copyright 2007-2010  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)PluginManager.java	1.00	2010/12/20
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.editor.plugin;

import java.io.File;

import ssac.falconseed.editor.plugin.csv.CsvFileComponentManager;
import ssac.util.Strings;

/**
 * プラグインを管理するマネージャ。
 * 
 * @version 1.00	2010/12/20
 */
public class PluginManager
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/**
	 * プラグインマネージャのアプリケーション内唯一のインスタンス
	 */
	static private final PluginManager instance = new PluginManager();
	/**
	 * プラグインのリスト
	 */
	private IComponentManager[] pluginList = new IComponentManager[]{};

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	private PluginManager() {}

	/**
	 * プラグインマネージャの唯一のインスタンスを取得する。
	 * @return	プラグインマネージャのインスタンス
	 */
	static protected final PluginManager getInstance() {
		return instance;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * プラグインマネージャを初期化する。
	 */
	static public void setupPlugins() {
		instance.setup();
	}

	/**
	 * このマネージャの先頭に登録されているプラグインを返す。
	 * @return	マネージャ先頭のプラグインを返す。プラグインが一つも
	 * 			定義されていない場合は <tt>null</tt> を返す。
	 */
	static public IComponentManager getDefaultPlugin() {
		if (instance.size() > 0)
			return instance.getByIndex(0);
		else
			return null;
	}

	/**
	 * このマネージャにプラグインが一つも登録されていない場合に <tt>true</tt> を返す。
	 * @return このマネージャがプラグインを保持していない場合は <tt>true</tt>
	 */
	static public boolean isEmpty() {
		return (instance.size() <= 0);
	}

	/**
	 * このマネージャが保持しているプラグイン数を返す。
	 * @return	プラグイン数
	 */
	static public int getPluginCount() {
		return instance.size();
	}

	/**
	 * 指定された位置にあるプラグインを取得する。
	 * 
	 * @param index	プラグインが格納されているインデックス
	 * @return	プラグイン
	 * 
	 * @throws ArrayIndexOutOfBoundsException インデックスが範囲外の場合
	 */
	static public IComponentManager getPlugin(int index) {
		return instance.getByIndex(index);
	}

	/**
	 * 指定された識別子を持つプラグインを取得する。
	 * 
	 * @param id	識別子
	 * @return	指定IDのプラグインを返す。プラグインがない場合は <tt>null</tt> を返す。
	 */
	static public IComponentManager findPlugin(String id) {
		return instance.getByID(id);
	}

	/**
	 * 指定されたファイルをサポートしているプラグインを取得する。
	 * サポートされていないファイルの場合は <tt>null</tt> を返す。
	 * @param targetFile	判定するファイル
	 * @return	ファイルをサポートしているプラグインを返す。
	 * 			どのプラグインでもサポートしていない場合は <tt>null</tt> を返す。
	 */
	static public IComponentManager findSupportedPlugin(File targetFile) {
		return instance.getByFile(targetFile);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	/**
	 * このマネージャが保持しているプラグイン数を返す。
	 * @return	プラグイン数
	 */
	private int size() {
		return pluginList.length;
	}

	/**
	 * 指定された位置にあるプラグインを取得する。
	 * 
	 * @param index	プラグインが格納されているインデックス
	 * @return	プラグイン
	 * 
	 * @throws ArrayIndexOutOfBoundsException インデックスが範囲外の場合
	 */
	private IComponentManager getByIndex(int index) {
		return pluginList[index];
	}

	/**
	 * 指定された識別子を持つプラグインを取得する。
	 * 
	 * @param id	識別子
	 * @return	指定IDのプラグインを返す。プラグインがない場合は <tt>null</tt> を返す。
	 */
	private IComponentManager getByID(String id) {
		if (Strings.isNullOrEmpty(id))
			return null;	// id is empty
		
		for (IComponentManager plugin : pluginList) {
			if (id.equals(plugin.getID())) {
				return plugin;
			}
		}
		
		// not found
		return null;
	}

	/**
	 * 指定されたファイルをサポートしているプラグインを取得する。
	 * サポートされていないファイルの場合は <tt>null</tt> を返す。
	 * @param targetFile	判定するファイル
	 * @return	ファイルをサポートしているプラグインを返す。
	 * 			どのプラグインでもサポートしていない場合は <tt>null</tt> を返す。
	 */
	private IComponentManager getByFile(File targetFile) {
		if (targetFile == null)
			return null;	// targetFile is null
		
		for (IComponentManager plugin : pluginList) {
			if (plugin.isSupportedFileType(targetFile)) {
				return plugin;
			}
		}
		
		// not supported
		return null;
	}

	/**
	 * 適用する全てのプラグインを収集し、マネージャに登録する。
	 */
	private synchronized void setup() {
		pluginList = new IComponentManager[]{
				new CsvFileComponentManager(),
		};
	}

	/**
	 * プラグインマネージャに、プラグインを登録する。
	 * 
	 * @param plugin	登録するプラグイン
	 */
	/*
	protected synchronized void regist(IComponentManager plugin) {
		if (plugin == null) {
			return;	// not regist
		}

		IComponentManager[] srcList = pluginList;
		if (srcList.length > 0) {
			int len = srcList.length;
			IComponentManager[] dstList = new IComponentManager[len+1];
			System.arraycopy(srcList, 0, dstList, 0, len);
			dstList[len]= plugin;
			pluginList = dstList;
		}
		else {
			pluginList = new IComponentManager[]{plugin};
		}
	}
	*/
}
