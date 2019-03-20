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
 * @(#)MExecArgsModel.java	3.1.0	2014/05/14
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MExecArgsModel.java	1.00	2010/12/20
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module.swing;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ssac.aadl.module.ModuleArgData;
import ssac.aadl.module.ModuleArgType;
import ssac.falconseed.module.args.IMExecArgParam;
import ssac.falconseed.module.args.MExecArgPublish;
import ssac.falconseed.module.args.MExecArgString;
import ssac.falconseed.module.args.MExecArgSubscribe;
import ssac.falconseed.module.args.MExecArgTempFile;
import ssac.falconseed.module.setting.MExecDefSettings;
import ssac.util.io.VirtualFile;

/**
 * モジュール実行時引数のデータモデル。
 * このデータモデルは、モジュール実行定義 {@link MexecDefSettings} と、
 * 一引数の定義データモデル {@link MExecArgItemModel} の複数の
 * オブジェクトを格納する。
 * 
 * @version 3.1.0	2014/05/14
 */
public class MExecArgsModel
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** 現在のエディタのドキュメントの種類を示す <code>IMExecArgParam</code> のインスタンス **/
	private final IMExecArgParam			_activeDocType;

	/** このモデルの基礎となるモジュール実行定義 **/
	private final MExecDefSettings			_settings;
	/** このモデルを構成する要素 **/
	private final List<MExecArgItemModel>	_itemlist;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public MExecArgsModel(IMExecArgParam activeDocType, MExecDefSettings settings) {
		if (settings == null)
			throw new NullPointerException("The specified Module Execution Definition settings is null.");
		this._activeDocType = activeDocType;
		this._settings = settings;
		this._itemlist = new ArrayList<MExecArgItemModel>();
		initItemList(settings);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 現在のエディタドキュメントの種類を示すパラメータ
	 * @return	アクティブなエディタのドキュメントに対応する <code>IMExecArgParam</code> のインスタンスを返す。
	 * 			アクティブなエディタが存在しない場合は <tt>null</tt> を返す。
	 */
	public IMExecArgParam getActiveDocumentType() {
		return _activeDocType;
	}

	/**
	 * 指定されたパラメータが、現在のアクティブエディタのドキュメントを利用可能な
	 * パラメータかを判定する。引数が <tt>null</tt> の場合、このメソッドは <tt>false</tt> を返す。
	 * @param param	判定対象のパラメータ
	 * @return	指定されたパラメータがアクティブなエディタドキュメントを利用可能であれば <tt>true</tt> を返す。
	 * 			利用できない場合やアクティブなエディタが存在しない場合は <tt>false</tt> を返す。
	 */
	public boolean canUseActiveDocument(IMExecArgParam param) {
		if (_activeDocType == null || param == null) {
			return false;
		}
		
		return (param == _activeDocType);
	}

	/**
	 * このモデルの関連付けられているモジュール実行定義情報を取得する。
	 * @return	モジュール実行定義情報
	 */
	public MExecDefSettings getSettings() {
		return _settings;
	}

	/**
	 * 引数設定が存在しないかを判定する。
	 * @return	引数設定が一つも存在しない場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean isItemEmpty() {
		return _itemlist.isEmpty();
	}

	/**
	 * 設定されている引数の総数を返す。
	 * @return	引数の総数
	 */
	public int getNumItems() {
		return _itemlist.size();
	}

	/**
	 * 指定された位置の引数設定を取得する。
	 * @param index	取得する位置を示すインデックス
	 * @return	<code>MExecArgItemModel</code> オブジェクト
	 * @throws ArrayIndexOutOfBoundsException	インデックスが無効な場合
	 */
	public MExecArgItemModel getItem(int index) {
		return _itemlist.get(index);
	}

	/**
	 * 指定された位置の引数設定が編集可能かを判定する。
	 * @param index	判定する引数設定の位置を示すインデックス
	 * @return	引数設定が編集可能なら <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws ArrayIndexOutOfBoundsException	インデックスが無効な場合
	 */
	public boolean isItemEditable(int index) {
		return getItem(index).isEditable();
	}

	/**
	 * 指定された位置の引数設定について、編集可能かどうかを設定する。
	 * <p><b>注意：</b>
	 * <blockquote>
	 * この設定は表示には反映されない。
	 * </blockquote>
	 * @param index	設定対象の引数設定の位置を示すインデックス
	 * @param editable	編集可能とする場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws ArrayIndexOutOfBoundsException	インデックスが無効な場合
	 */
	public void setItemEditable(int index, boolean editable) {
		getItem(index).setEditable(editable);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	/**
	 * 指定されたモジュール実行定義情報から、引数設定のモデルリストを
	 * 初期化する。
	 * @param settings	モジュール実行定義情報
	 */
	protected void initItemList(final MExecDefSettings settings) {
		// リストのクリア
		_itemlist.clear();
		
		// 引数設定の生成
		int num = settings.getNumArguments();
		for (int i = 0; i < num; i++) {
			ModuleArgData argdata = settings.getArgument(i);
			IMExecArgParam param = null;
			Object value = argdata.getValue();
			if (value instanceof IMExecArgParam) {
				param = (IMExecArgParam)value;
				value = null;
			}
			MExecArgItemModel item = createItemModel(argdata.getType(), argdata.getDescription(), param, false, value);
			_itemlist.add(item);
		}
	}

	/**
	 * 指定されたパラメータで、新しいアイテムモデルを生成する。
	 * このパラメータから、編集の可不可も自動的に設定される。
	 * @param type		引数属性
	 * @param desc		引数説明
	 * @param param		引数指定のパラメータ
	 * @param option	オプションフラグ
	 * @param value		引数値
	 * @return	生成された <code>MExecArgItemModel</code> オブジェクト
	 * @throws IllegalArgumentException	<em>type</em> が無効な場合
	 */
	protected MExecArgItemModel createItemModel(ModuleArgType type, String desc, IMExecArgParam param, boolean option, Object value) {
		boolean editable;
		switch (type) {
			case IN :
				if (param instanceof MExecArgTempFile) {
					// テンポラリファイル指定なら編集不要
					editable = false;
				}
				else if (param==null) {
					// パラメータ指定がない
					if (value instanceof VirtualFile) {
						// ファイルが存在するなら、編集不要
						try {
							editable = !((VirtualFile)value).exists();
						} catch (Throwable ex) {
							editable = true;
						}
					}
					else if (value instanceof File) {
						// ファイルが存在するなら、編集不要
						try {
							editable = !((VirtualFile)value).exists();
						} catch (Throwable ex) {
							editable = true;
						}
					}
					else {
						// ファイル以外が指定されているなら、編集可能
						editable = true;
					}
				}
				else {
					// その他の条件では編集可能
					editable = true;
				}
				break;
			case OUT :
				if (param instanceof MExecArgTempFile) {
					// テンポラリファイル指定なら編集不要
					editable = false;
				}
				else if (param==null && ((value instanceof VirtualFile) || (value instanceof File))) {
					// パラメータ指定がなく、ファイルが指定されていれば、編集不要
					editable = false;
				}
				else {
					// その他の条件では編集可能
					editable = true;
				}
				break;
			case STR :
				if (!(param instanceof MExecArgString) && value != null && value.toString().length() > 0) {
					// 文字列入力パラメータではなく、文字列が有効なら編集不要
					editable = false;
				}
				else {
					// その他の条件では、編集可能
					editable = true;
				}
				break;
			case PUB :
				if (!(param instanceof MExecArgPublish) && value != null && value.toString().length() > 0) {
					// 宛先入力パラメータではなく、文字列が有効なら編集不要
					editable = false;
				}
				else {
					// その他の条件では、編集可能
					editable = true;
				}
				break;
			case SUB :
				if (!(param instanceof MExecArgSubscribe) && value != null && value.toString().length() > 0) {
					// 宛先入力パラメータではなく、文字列が有効なら編集不要
					editable = false;
				}
				else {
					// その他の条件では、編集可能
					editable = true;
				}
				break;
			default :
				throw new IllegalArgumentException("The specified argument type[" + type.name() + "] is invalid.");
		}
		MExecArgItemModel item = new MExecArgItemModel(type, desc, param, false, value);
		item.setEditable(editable);
		return item;
	}

	/*
	 * @deprecated	このメソッドはテスト専用。
	 * 引数設定のリストが 99 個となるまで、テストデータを追加する。
	 *
	private void initTestItemList() {
		int index = 0;
		while (_itemlist.size() < 99) {
			MExecArgItemModel item = createItemModel((ModuleArgType)testdata[index][0],
													(String)testdata[index][1],
													(IMExecArgParam)testdata[index][2],
													(Boolean)testdata[index][3],
													testdata[index][4]);
			_itemlist.add(item);
			index = (index + 1) % testdata.length;
		}
	}
	
	static private final String DESC_SHORT	= "引数の説明(Short)";
	static private final String DESC_LONG		= "引数の説明の Long Version。"
												+ "この引数の説明は、ユーザーが入力した内容を表示するものだが、ユーザーが設定した引数が長い場合もその内容がすべて表示されたほうが見やすいと思う。"
												+ "したがって、引数の説明が長い場合でも、その内容はすべて表示するためのデザインが、このコンポーネントの特徴である。";
	
	static private final String STR_SHORT		= "短い文字列の例";
	static private final String STR_MIDDLE	= "世界でいちばん初めに切手ができた国は、イギリスです。"
												+ "それは、今から二百年近く前のことでした。"
												+ "当時、イギリスにはすでに郵便の制度はありましたが、手紙の重さと送る距離によって、値段が変わっていました。"
												+ "つまり、重いものを遠くへ送るほどたくさんのお金を払わなければならなかったのです。";
	static private final String STR_LONG		= "日本には至るところに桜の木が植えてあり、日本人は桜の花が大好きです。"
												+ "春、私たちの目を楽しませてくれる桜は、夏には次の年の花芽の準備にかかります。"
												+ "けれど、すぐに花は咲かず、秋冬を過ごし、春になってやっと花が咲くのには、理由があります。"
												+ "開花ホルモンという指令物質が夏には働いていないのです。"
												+ "開花ホルモンは、気温が〇度近くまで一度下がり、その後だんだん暖かくなって初めて、「花を咲かせなさい」と桜の木に指令を出します。"
												+ "そのため、ずっと気温が高い南の島には、桜はありません。"
												+ "四季のある地域だからこその花なのです。"
												+ "この開花ホルモンは、葉も関係していると考えられています。"
												+ "桜の代表的な種類であるソメイヨシノの場合、花が散ると葉っぱだけの葉桜になります。"
												+ "葉から花芽の成長を遅らせる物質が出ているので、葉がある間、花芽は開きません。"
												+ "冬が終わり春になりかけるころの桜には、葉が全くなくなり、それからやっと花芽がふくらみます。";
	
	static private final VirtualFile FILE_SHORT_PATH = new DefaultFile("../..").getAbsoluteFile().getNormalizedFile();
	static private final VirtualFile FILE_LONG_PATH = new DefaultFile("../Exalge/testdata/CSV/NormalExBasePatternSet_UTF8.csv").getAbsoluteFile().getNormalizedFile();
	
	static public final VirtualFile TESTFILE_BASEPATH = new DefaultFile("../Dtalge/testdata").getAbsoluteFile().getNormalizedFile();
	static private final VirtualFile TESTFILE_RELATIVE = new DefaultFile("../Dtalge/testdata/tokuho/surveyAlge.csv").getAbsoluteFile().getNormalizedFile();
	
	static private final Object[][] testdata = {
		{ModuleArgType.STR, DESC_SHORT, null, false, null},
		{ModuleArgType.STR, DESC_SHORT, null, false, STR_SHORT},
		{ModuleArgType.STR, DESC_SHORT, null, false, STR_MIDDLE},
		{ModuleArgType.STR, DESC_SHORT, null, false, STR_LONG},
		{ModuleArgType.STR, DESC_LONG, null, false, null},
		{ModuleArgType.STR, DESC_LONG, null, false, STR_SHORT},
		{ModuleArgType.STR, DESC_LONG, null, false, STR_MIDDLE},
		{ModuleArgType.STR, DESC_LONG, null, false, STR_LONG},
		{ModuleArgType.STR, DESC_SHORT, MExecArgString.instance, false, null},
		{ModuleArgType.STR, DESC_SHORT, MExecArgString.instance, false, STR_SHORT},
		{ModuleArgType.STR, DESC_SHORT, MExecArgString.instance, false, STR_MIDDLE},
		{ModuleArgType.STR, DESC_SHORT, MExecArgString.instance, false, STR_LONG},
		{ModuleArgType.STR, DESC_LONG, MExecArgString.instance, false, null},
		{ModuleArgType.STR, DESC_LONG, MExecArgString.instance, false, STR_SHORT},
		{ModuleArgType.STR, DESC_LONG, MExecArgString.instance, false, STR_MIDDLE},
		{ModuleArgType.STR, DESC_LONG, MExecArgString.instance, false, STR_LONG},
		
		{ModuleArgType.IN, DESC_SHORT, null, false, null},
		{ModuleArgType.IN, DESC_SHORT, null, false, FILE_SHORT_PATH},
		{ModuleArgType.IN, DESC_SHORT, null, false, FILE_LONG_PATH},
		{ModuleArgType.IN, DESC_SHORT, null, false, TESTFILE_RELATIVE},
		{ModuleArgType.IN, DESC_SHORT, MExecArgTempFile.instance, false, null},
		{ModuleArgType.IN, DESC_SHORT, MExecArgTempFile.instance, false, FILE_SHORT_PATH},
		{ModuleArgType.IN, DESC_SHORT, MExecArgTempFile.instance, false, FILE_LONG_PATH},
		{ModuleArgType.IN, DESC_SHORT, MExecArgTempFile.instance, false, TESTFILE_RELATIVE},
		{ModuleArgType.IN, DESC_SHORT, MExecArgCsvFile.instance, false, null},
		{ModuleArgType.IN, DESC_SHORT, MExecArgCsvFile.instance, false, FILE_SHORT_PATH},
		{ModuleArgType.IN, DESC_SHORT, MExecArgCsvFile.instance, false, FILE_LONG_PATH},
		{ModuleArgType.IN, DESC_SHORT, MExecArgCsvFile.instance, false, TESTFILE_RELATIVE},
		{ModuleArgType.IN, DESC_SHORT, MExecArgXmlFile.instance, false, null},
		{ModuleArgType.IN, DESC_SHORT, MExecArgXmlFile.instance, false, FILE_SHORT_PATH},
		{ModuleArgType.IN, DESC_SHORT, MExecArgXmlFile.instance, false, FILE_LONG_PATH},
		{ModuleArgType.IN, DESC_SHORT, MExecArgXmlFile.instance, false, TESTFILE_RELATIVE},
		{ModuleArgType.IN, DESC_SHORT, MExecArgTextFile.instance, false, null},
		{ModuleArgType.IN, DESC_SHORT, MExecArgTextFile.instance, false, FILE_SHORT_PATH},
		{ModuleArgType.IN, DESC_SHORT, MExecArgTextFile.instance, false, FILE_LONG_PATH},
		{ModuleArgType.IN, DESC_SHORT, MExecArgTextFile.instance, false, TESTFILE_RELATIVE},
		
		{ModuleArgType.OUT, DESC_SHORT, null, false, null},
		{ModuleArgType.OUT, DESC_SHORT, null, false, FILE_SHORT_PATH},
		{ModuleArgType.OUT, DESC_SHORT, null, false, FILE_LONG_PATH},
		{ModuleArgType.OUT, DESC_SHORT, null, false, TESTFILE_RELATIVE},
		{ModuleArgType.OUT, DESC_SHORT, MExecArgTempFile.instance, false, null},
		{ModuleArgType.OUT, DESC_SHORT, MExecArgTempFile.instance, false, FILE_SHORT_PATH},
		{ModuleArgType.OUT, DESC_SHORT, MExecArgTempFile.instance, false, FILE_LONG_PATH},
		{ModuleArgType.OUT, DESC_SHORT, MExecArgTempFile.instance, false, TESTFILE_RELATIVE},
		{ModuleArgType.OUT, DESC_SHORT, MExecArgCsvFile.instance, false, null},
		{ModuleArgType.OUT, DESC_SHORT, MExecArgCsvFile.instance, false, FILE_SHORT_PATH},
		{ModuleArgType.OUT, DESC_SHORT, MExecArgCsvFile.instance, false, FILE_LONG_PATH},
		{ModuleArgType.OUT, DESC_SHORT, MExecArgCsvFile.instance, false, TESTFILE_RELATIVE},
		{ModuleArgType.OUT, DESC_SHORT, MExecArgXmlFile.instance, false, null},
		{ModuleArgType.OUT, DESC_SHORT, MExecArgXmlFile.instance, false, FILE_SHORT_PATH},
		{ModuleArgType.OUT, DESC_SHORT, MExecArgXmlFile.instance, false, FILE_LONG_PATH},
		{ModuleArgType.OUT, DESC_SHORT, MExecArgXmlFile.instance, false, TESTFILE_RELATIVE},
		{ModuleArgType.OUT, DESC_SHORT, MExecArgTextFile.instance, false, null},
		{ModuleArgType.OUT, DESC_SHORT, MExecArgTextFile.instance, false, FILE_SHORT_PATH},
		{ModuleArgType.OUT, DESC_SHORT, MExecArgTextFile.instance, false, FILE_LONG_PATH},
		{ModuleArgType.OUT, DESC_SHORT, MExecArgTextFile.instance, false, TESTFILE_RELATIVE},
		
		{ModuleArgType.IN, DESC_LONG, null, false, null},
		{ModuleArgType.IN, DESC_LONG, null, false, FILE_SHORT_PATH},
		{ModuleArgType.IN, DESC_LONG, null, false, FILE_LONG_PATH},
		{ModuleArgType.IN, DESC_LONG, null, false, TESTFILE_RELATIVE},
		{ModuleArgType.IN, DESC_LONG, MExecArgTempFile.instance, false, null},
		{ModuleArgType.IN, DESC_LONG, MExecArgTempFile.instance, false, FILE_SHORT_PATH},
		{ModuleArgType.IN, DESC_LONG, MExecArgTempFile.instance, false, FILE_LONG_PATH},
		{ModuleArgType.IN, DESC_LONG, MExecArgTempFile.instance, false, TESTFILE_RELATIVE},
		{ModuleArgType.IN, DESC_LONG, MExecArgCsvFile.instance, false, null},
		{ModuleArgType.IN, DESC_LONG, MExecArgCsvFile.instance, false, FILE_SHORT_PATH},
		{ModuleArgType.IN, DESC_LONG, MExecArgCsvFile.instance, false, FILE_LONG_PATH},
		{ModuleArgType.IN, DESC_LONG, MExecArgCsvFile.instance, false, TESTFILE_RELATIVE},
		{ModuleArgType.IN, DESC_LONG, MExecArgXmlFile.instance, false, null},
		{ModuleArgType.IN, DESC_LONG, MExecArgXmlFile.instance, false, FILE_SHORT_PATH},
		{ModuleArgType.IN, DESC_LONG, MExecArgXmlFile.instance, false, FILE_LONG_PATH},
		{ModuleArgType.IN, DESC_LONG, MExecArgXmlFile.instance, false, TESTFILE_RELATIVE},
		{ModuleArgType.IN, DESC_LONG, MExecArgTextFile.instance, false, null},
		{ModuleArgType.IN, DESC_LONG, MExecArgTextFile.instance, false, FILE_SHORT_PATH},
		{ModuleArgType.IN, DESC_LONG, MExecArgTextFile.instance, false, FILE_LONG_PATH},
		{ModuleArgType.IN, DESC_LONG, MExecArgTextFile.instance, false, TESTFILE_RELATIVE},
		
		{ModuleArgType.OUT, DESC_LONG, null, false, null},
		{ModuleArgType.OUT, DESC_LONG, null, false, FILE_SHORT_PATH},
		{ModuleArgType.OUT, DESC_LONG, null, false, FILE_LONG_PATH},
		{ModuleArgType.OUT, DESC_LONG, null, false, TESTFILE_RELATIVE},
		{ModuleArgType.OUT, DESC_LONG, MExecArgTempFile.instance, false, null},
		{ModuleArgType.OUT, DESC_LONG, MExecArgTempFile.instance, false, FILE_SHORT_PATH},
		{ModuleArgType.OUT, DESC_LONG, MExecArgTempFile.instance, false, FILE_LONG_PATH},
		{ModuleArgType.OUT, DESC_LONG, MExecArgTempFile.instance, false, TESTFILE_RELATIVE},
		{ModuleArgType.OUT, DESC_LONG, MExecArgCsvFile.instance, false, null},
		{ModuleArgType.OUT, DESC_LONG, MExecArgCsvFile.instance, false, FILE_SHORT_PATH},
		{ModuleArgType.OUT, DESC_LONG, MExecArgCsvFile.instance, false, FILE_LONG_PATH},
		{ModuleArgType.OUT, DESC_LONG, MExecArgCsvFile.instance, false, TESTFILE_RELATIVE},
		{ModuleArgType.OUT, DESC_LONG, MExecArgXmlFile.instance, false, null},
		{ModuleArgType.OUT, DESC_LONG, MExecArgXmlFile.instance, false, FILE_SHORT_PATH},
		{ModuleArgType.OUT, DESC_LONG, MExecArgXmlFile.instance, false, FILE_LONG_PATH},
		{ModuleArgType.OUT, DESC_LONG, MExecArgXmlFile.instance, false, TESTFILE_RELATIVE},
		{ModuleArgType.OUT, DESC_LONG, MExecArgTextFile.instance, false, null},
		{ModuleArgType.OUT, DESC_LONG, MExecArgTextFile.instance, false, FILE_SHORT_PATH},
		{ModuleArgType.OUT, DESC_LONG, MExecArgTextFile.instance, false, FILE_LONG_PATH},
		{ModuleArgType.OUT, DESC_LONG, MExecArgTextFile.instance, false, TESTFILE_RELATIVE},
	};
	/**/
}
