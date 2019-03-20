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
 * @(#)MqttCsvParameter.java	0.4.0	2014/05/29
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.runtime.mqtt;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import ssac.aadl.runtime.nio.csv.CsvFieldDecoder;
import ssac.aadl.runtime.nio.csv.CsvUtil;

/**
 * CSV 形式で記述された MQTT 接続パラメータを入力とする MQTT 接続パラメータを保持するクラス。
 * 主に、接続先サーバーURI、QoS、トピックリストを保持する。
 * <p>なお、このオブジェクトは不変オブジェクトである。
 * 
 * @version 0.4.0	2014/05/29
 * @since 0.4.0
 * 
 * @author Yasunari Ishizuka (PieCake,Inc.)
 * @author Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 * @author Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
public class MqttCsvParameter
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	/** ServerURI と パラメータ文字列との区切り文字 **/
	static protected final char		MQTTPARAM_DELIMITER_CHAR	= '|';
	static protected final String	MQTTPARAM_DELIMITER_STR		= "|";
	static protected final String	REGEXP_MQTTPARAM_DELIMITER	= "\\|";

	/** 標準の <code>QoS</code> **/
	static protected final int		DEFAULT_QOS	= 1;

	static protected final char		MQPARAM_SEPARATOR		= '=';
	static protected final String	MQPARAM_KEY_QOS			= "qos";
	static protected final String	MQPARAM_KEY_CLIENTID	= "clientID";
	
	static protected final char		TOPIC_WILDCARD_MULTI	= '#';
	static protected final char		TOPIC_WILDCARD_SINGLE	= '+';
	static protected final char		TOPIC_LEVEL_SEPARATOR	= '/';

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** サーバーURI を示す文字列(not null) **/
	protected String		_serverURI;
	/** クライアントID を示す文字列、設定されていない場合は <tt>null</tt> **/
	protected String		_clientID;
	/** QoS **/
	protected int			_qos;
	/** 変更不可能なトピックのリスト(not null) **/
	protected List<String>	_topics;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * デフォルトの <code>QoS</code> を 1 として、指定された CSV 形式 で記述された MQTT 接続パラメータを読み込み、新しいインスタンスを生成する。
	 * CSV 形式の MQTT 接続パラメータは 1 レコード(1行)で記述し、第 1 列(先頭フィールド)に接続先サーバー情報、
	 * 第 2 列(第 2 フィールド)以降にトピックを記述する。トピックは記述を省略できる。<br>
	 * 接続先サーバー情報には、接続先サーバー URI に続けて、'|' で区切ることにより接続オプションを <code>名前＝値</code> の
	 * 形式で指定できる。接続オプションは省略できるが、値のみの省略は許可されない。
	 * また、接続オプションの名前は、大文字小文字は区別されない。指定可能な接続オプションは、次の通り。
	 * <dl>
	 * <dt><b>qos=<i>値</i></b></dt>
	 * <dd>MQTT の伝送品質(QoS)を 0、1、2 のどれかから指定する。</dd>
	 * <dt><b>clientID=<i>値</i></b></dt>
	 * <dd>接続に使用する任意のクライアントID。</dd>
	 * </dl>
	 * 
	 * <p>このコンストラクタは、次の場合に例外をスローする。
	 * <ul>
	 * <li>接続先サーバー情報の接続先サーバー URI の書式が正しくない場合
	 * <li>接続先サーバー情報の接続先サーバー URI に含まれるスキーマがサポートされていない場合、またはサーバー URI に含まれるポート番号が範囲外の場合
	 * <li>接続先サーバー情報の接続オプションに、無効な項目が含まれている場合
	 * <li>接続先サーバー情報の接続オプションに含まれる <code>QoS</code> の値が、0、1、2 以外の場合
	 * <li>接続先サーバー情報の接続オプションに含まれる <code>ClientID</code> が 23 文字よりも長い場合
	 * <li>トピックの書式が正しくない場合
	 * </ul>
	 * なお、トピックにはワイルドカードの指定も許可する。
	 * 
	 * @param csvString	<code>MQTT</code> パラメータが記述された CSV レコードフォーマットの文字列
	 * @throws NullPointerException	<em>csvString</em> が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	記述内容が正しくない場合
	 */
	public MqttCsvParameter(String csvString) {
		this(csvString, true, DEFAULT_QOS);
	}
	
	/**
	 * デフォルトの <code>QoS</code> を 1 として、指定された CSV 形式 で記述された MQTT 接続パラメータを読み込み、新しいインスタンスを生成する。
	 * CSV 形式の MQTT 接続パラメータは 1 レコード(1行)で記述し、第 1 列(先頭フィールド)に接続先サーバー情報、
	 * 第 2 列(第 2 フィールド)以降にトピックを記述する。トピックは記述を省略できる。<br>
	 * 接続先サーバー情報には、接続先サーバー URI に続けて、'|' で区切ることにより接続オプションを <code>名前＝値</code> の
	 * 形式で指定できる。接続オプションは省略できるが、値のみの省略は許可されない。
	 * また、接続オプションの名前は、大文字小文字は区別されない。指定可能な接続オプションは、次の通り。
	 * <dl>
	 * <dt><b>qos=<i>値</i></b></dt>
	 * <dd>MQTT の伝送品質(QoS)を 0、1、2 のどれかから指定する。</dd>
	 * <dt><b>clientID=<i>値</i></b></dt>
	 * <dd>接続に使用する任意のクライアントID。</dd>
	 * </dl>
	 * 
	 * <p>このコンストラクタは、次の場合に例外をスローする。
	 * <ul>
	 * <li>接続先サーバー情報の接続先サーバー URI の書式が正しくない場合
	 * <li>接続先サーバー情報の接続先サーバー URI に含まれるスキーマがサポートされていない場合、またはサーバー URI に含まれるポート番号が範囲外の場合
	 * <li>接続先サーバー情報の接続オプションに、無効な項目が含まれている場合
	 * <li>接続先サーバー情報の接続オプションに含まれる <code>QoS</code> の値が、0、1、2 以外の場合
	 * <li>接続先サーバー情報の接続オプションに含まれる <code>ClientID</code> が 23 文字よりも長い場合
	 * <li>トピックの書式が正しくない場合
	 * </ul>
	 * なお、<em>allowTopicWildcard</em> に <tt>false</tt> を指定した場合、トピックにワイルドカードが含まれている場合にも例外をスローする。
	 * 
	 * @param csvString			CSV 形式の MQTT 接続パラメータを示す文字列
	 * @param allowTopicWildcard	トピックにワイルドカードの指定を許可する場合は <tt>true</tt>
	 * @throws NullPointerException	<em>csvString</em> が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	記述内容が正しくない場合
	 */
	public MqttCsvParameter(String csvString, boolean allowTopicWildcard) {
		this(csvString, allowTopicWildcard, DEFAULT_QOS);
	}
	
	/**
	 * デフォルトの <code>QoS</code> を指定して、指定された CSV 形式 で記述された MQTT 接続パラメータを読み込み、新しいインスタンスを生成する。
	 * CSV 形式の MQTT 接続パラメータは 1 レコード(1行)で記述し、第 1 列(先頭フィールド)に接続先サーバー情報、
	 * 第 2 列(第 2 フィールド)以降にトピックを記述する。トピックは記述を省略できる。<br>
	 * 接続先サーバー情報には、接続先サーバー URI に続けて、'|' で区切ることにより接続オプションを <code>名前＝値</code> の
	 * 形式で指定できる。接続オプションは省略できるが、値のみの省略は許可されない。
	 * また、接続オプションの名前は、大文字小文字は区別されない。指定可能な接続オプションは、次の通り。
	 * <dl>
	 * <dt><b>qos=<i>値</i></b></dt>
	 * <dd>MQTT の伝送品質(QoS)を 0、1、2 のどれかから指定する。</dd>
	 * <dt><b>clientID=<i>値</i></b></dt>
	 * <dd>接続に使用する任意のクライアントID。</dd>
	 * </dl>
	 * 
	 * <p>このコンストラクタは、次の場合に例外をスローする。
	 * <ul>
	 * <li>接続先サーバー情報の接続先サーバー URI の書式が正しくない場合
	 * <li>接続先サーバー情報の接続先サーバー URI に含まれるスキーマがサポートされていない場合、またはサーバー URI に含まれるポート番号が範囲外の場合
	 * <li>接続先サーバー情報の接続オプションに、無効な項目が含まれている場合
	 * <li>接続先サーバー情報の接続オプションに含まれる <code>QoS</code> の値が、0、1、2 以外の場合
	 * <li>接続先サーバー情報の接続オプションに含まれる <code>ClientID</code> が 23 文字よりも長い場合
	 * <li>トピックの書式が正しくない場合
	 * </ul>
	 * なお、トピックにはワイルドカードの指定も許可する。
	 * 
	 * @param csvString			CSV 形式の MQTT 接続パラメータを示す文字列
	 * @param defaultQOS			接続オプションの <code>QoS</code> が省略された場合の <code>QoS</code> のデフォルト値
	 * 
	 * @throws NullPointerException	<em>csvString</em> が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	記述内容が正しくない場合、もしくは <em>defaultQOS</em> が 0、1、2 以外の場合
	 */
	public MqttCsvParameter(String csvString, int defaultQOS) {
		this(csvString, true, defaultQOS);
	}
	
	/**
	 * デフォルトの <code>QoS</code> を指定して、指定された CSV 形式 で記述された MQTT 接続パラメータを読み込み、新しいインスタンスを生成する。
	 * CSV 形式の MQTT 接続パラメータは 1 レコード(1行)で記述し、第 1 列(先頭フィールド)に接続先サーバー情報、
	 * 第 2 列(第 2 フィールド)以降にトピックを記述する。トピックは記述を省略できる。<br>
	 * 接続先サーバー情報には、接続先サーバー URI に続けて、'|' で区切ることにより接続オプションを <code>名前＝値</code> の
	 * 形式で指定できる。接続オプションは省略できるが、値のみの省略は許可されない。
	 * また、接続オプションの名前は、大文字小文字は区別されない。指定可能な接続オプションは、次の通り。
	 * <dl>
	 * <dt><b>qos=<i>値</i></b></dt>
	 * <dd>MQTT の伝送品質(QoS)を 0、1、2 のどれかから指定する。</dd>
	 * <dt><b>clientID=<i>値</i></b></dt>
	 * <dd>接続に使用する任意のクライアントID。</dd>
	 * </dl>
	 * 
	 * <p>このコンストラクタは、次の場合に例外をスローする。
	 * <ul>
	 * <li>接続先サーバー情報の接続先サーバー URI の書式が正しくない場合
	 * <li>接続先サーバー情報の接続先サーバー URI に含まれるスキーマがサポートされていない場合、またはサーバー URI に含まれるポート番号が範囲外の場合
	 * <li>接続先サーバー情報の接続オプションに、無効な項目が含まれている場合
	 * <li>接続先サーバー情報の接続オプションに含まれる <code>QoS</code> の値が、0、1、2 以外の場合
	 * <li>接続先サーバー情報の接続オプションに含まれる <code>ClientID</code> が 23 文字よりも長い場合
	 * <li>トピックの書式が正しくない場合
	 * </ul>
	 * なお、<em>allowTopicWildcard</em> に <tt>false</tt> を指定した場合、トピックにワイルドカードが含まれている場合にも例外をスローする。
	 * 
	 * @param csvString			CSV 形式の MQTT 接続パラメータを示す文字列
	 * @param allowTopicWildcard	トピックにワイルドカードの指定を許可する場合は <tt>true</tt>
	 * @param defaultQOS			接続オプションの <code>QoS</code> が省略された場合の <code>QoS</code> のデフォルト値
	 * 
	 * @throws NullPointerException	<em>csvString</em> が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	記述内容が正しくない場合、もしくは <em>defaultQOS</em> が 0、1、2 以外の場合
	 */
	public MqttCsvParameter(String csvString, boolean allowTopicWildcard, int defaultQOS) {
		if (defaultQOS < 0 || defaultQOS > 2) {
			throw new IllegalArgumentException("Default QoS is not 0, 1 or 2 : " + defaultQOS);
		}
		_qos = defaultQOS;
		//--- parse
		parseCsvString(csvString, allowTopicWildcard);
	}

	/**
	 * 指定されたパラメータで、新しいインスタンスを生成する。
	 * <p>このコンストラクタは、次の場合に例外をスローする。
	 * <ul>
	 * <li><em>serverURI</em> が <tt>null</tt> の場合、<em>serverURI</em> の書式が正しくない場合
	 * <li><em>serverURI</em> に含まれるスキーマがサポートされていない場合、または <em>serverURI</em> に含まれるポート番号が範囲外の場合
	 * <li><em>clientID</em> が <tt>null</tt> でも空文字でもなく、<code>ClientID</code> として正当ではない場合
	 * <li><em>qos</em> もしくは <em>defaultQOS</em> の値が 0、1、2 以外の場合
	 * <li><em>topiclist</em> に含まれるトピックの書式が正しくない場合
	 * </ul>
	 * なお、<em>arrowTopicWildcard</em> が <tt>false</tt> の場合、トピックにワイルドカードが含まれている場合も例外をスローする。
	 * @param serverURI	接続先サーバーURIを示す文字列
	 * @param clientID		クライアントIDとする文字列
	 * @param qos			<code>QoS</code>
	 * @param topiclist	トピックを示す文字列のコレクション
	 * @param allowTopicWildcard	トピックにワイルドカードを含めることを許可する場合は <tt>true</tt>
	 * @throws NullPointerException	<em>serverURI</em> が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	指定された内容が正しくない場合
	 */
	public MqttCsvParameter(String serverURI, String clientID, int qos, Collection<String> topiclist, boolean allowTopicWildcard) {
		// Server URI の解析
		URI uri = MqttConnectionParams.parseServerURI(serverURI);
		_serverURI = uri.toString();

		// qos
		if (qos < 0 || qos > 2) {
			throw new IllegalArgumentException("QoS is not 0, 1 or 2 : " + String.valueOf(qos));
		}
		_qos = qos;
		
		// ClientID
		if (clientID != null && clientID.length() > 0) {
			if (clientID.length() > MqttUtil.MAX_CLIENTID_LENGTH)
				throw new IllegalArgumentException("Client ID is greater than " + MqttUtil.MAX_CLIENTID_LENGTH + " characters : " + clientID);
			else if (clientID.indexOf(MQTTPARAM_DELIMITER_CHAR) >= 0)
				throw new IllegalArgumentException("Client ID must not contain '|' character : " + clientID);
			_clientID = clientID;
		} else {
			_clientID = null;
		}
		
		// トピックの解析
		if (topiclist != null) {
			ArrayList<String> newlist = new ArrayList<String>(topiclist.size());
			for (String strTopic : topiclist) {
				if (strTopic == null || strTopic.length() <= 0)
					continue;	// empty
				validTopicString(strTopic, allowTopicWildcard);
				newlist.add(strTopic);
			}
			newlist.trimToSize();
			_topics = Collections.unmodifiableList(newlist);
		}
		else {
			_topics = Collections.<String>emptyList();
		}
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * サーバーURL を取得する。
	 * @return	サーバーURL を示す文字列
	 */
	public String getServerURI() {
		return _serverURI;
	}

	/**
	 * 指定されたクライアントIDを取得する。
	 * @return	クライアントIDが指定されていればその文字列、なければ <tt>null</tt>
	 */
	public String getClientID() {
		return _clientID;
	}

	/**
	 * 利用可能なクライアントIDを取得する。
	 * このオブジェクトにクライアントIDが指定されていない場合、ランダムに生成されたクライアントIDを返す。
	 * @return	クライアントID を示す文字列
	 * @see ssac.aadl.runtime.mqtt.MqttUtil#generateClientID()
	 */
	public String getAvailableClientID() {
		if (_clientID == null) {
			return MqttUtil.generateClientID();
		} else {
			return _clientID;
		}
	}

	/**
	 * <code>QoS</code> を取得する。
	 * @return	<code>QoS</code>
	 */
	public int getQOS() {
		return _qos;
	}

	/**
	 * トピックのリストを取得する。
	 * トピックが指定されていない場合は、要素が空のリストを返す。
	 * なお、このメソッドが返すリストは、変更できない。
	 * @return	変更不可能なトピックのリスト
	 */
	public List<String> getTopics() {
		return _topics;
	}

	/**
	 * このオブジェクトのパラメータを、CSV 形式の文字列として出力する。
	 * <code>QoS</code> の値も、出力に含まれる。
	 * @return	CSVレコードフォーマットの文字列
	 */
	public String toCsvString() {
		StringBuilder sb = new StringBuilder();
		
		// first field
		String strFirstField = _serverURI;
		strFirstField = strFirstField.concat(String.format("|%s=%s", MQPARAM_KEY_QOS, String.valueOf(_qos)));
		if (_clientID != null) {
			strFirstField = strFirstField.concat(String.format("|%s=%s", MQPARAM_KEY_CLIENTID, _clientID));
		}
		appendCsvField(strFirstField, sb);
		
		// topics
		for (String strTopic : _topics) {
			sb.append(CsvUtil.CSV_DELIMITER_CHAR);
			appendCsvField(strTopic, sb);
		}
		
		return sb.toString();
	}
	
	@Override
	public int hashCode() {
		int h = 0;
		h = 31 * h + _serverURI.hashCode();
		h = 31 * h + (_clientID==null ? 0 : _clientID.hashCode());
		h = 31 * h + _qos;
		h = 31 * h + _topics.hashCode();
		return h;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		
		if (obj instanceof MqttCsvParameter) {
			MqttCsvParameter aParam = (MqttCsvParameter)obj;
			if (isEqualObjects(aParam._serverURI, this._serverURI)
				&& isEqualObjects(aParam._clientID, this._clientID)
				&& aParam._qos == this._qos
				&& isEqualObjects(aParam._topics, this._topics))
			{
				return true;
			}
		}
		
		return false;
	}

	/**
	 * このオブジェクトのパラメータを含むこのオブジェクトの文字列を表現を返す。
	 * このメソッドが出力する内容は、{@link #toCsvString()} の出力とは異なる。
	 * @return	このオブジェクトの文字列表現
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getName());
		sb.append("[");
		//--- server URI
		sb.append("uri=\"");
		sb.append(_serverURI);
		sb.append("\"");
		//--- QoS
		sb.append(", QoS=");
		sb.append(_qos);
		//--- ClientID
		sb.append(", clientID=");
		if (_clientID == null) {
			sb.append("null");
		} else {
			sb.append("\"");
			sb.append(_clientID);
			sb.append("\"");
		}
		//--- topics
		sb.append(", topics=");
		sb.append(_topics);
		sb.append("]");
		
		return sb.toString();
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	private void parseCsvString(String csvString, boolean arrowWildcard) {
		// CSV フィールドの抽出
		String[] csvFields = parseCsvFields(csvString);
		if (csvFields.length < 1 || csvFields[0].length() <= 0) {
			throw new IllegalArgumentException("Server URI is empty.");
		}
		
		// 設定パラメータの抽出
		String[] mqParams = csvFields[0].split(REGEXP_MQTTPARAM_DELIMITER);
		if (mqParams.length < 1 || mqParams[0].length() <= 0) {
			throw new IllegalArgumentException("Server URI is empty.");
		}
		
		// Server URI の解析
		URI uri = MqttConnectionParams.parseServerURI(mqParams[0]);
		_serverURI = uri.toString();
		
		// 設定パラメータの解析
		parseMqttConfigParams(mqParams, 1);
		
		// トピックの解析
		ArrayList<String> topiclist = new ArrayList<String>(csvFields.length - 1);
		for (int i = 1; i < csvFields.length; ++i) {
			String strTopic = csvFields[i];
			if (strTopic == null || strTopic.length() <= 0)
				continue;	// empty
			validTopicString(strTopic, arrowWildcard);
			topiclist.add(strTopic);
		}
		topiclist.trimToSize();
		_topics = Collections.unmodifiableList(topiclist);
	}

	private void validTopicString(String strTopic, boolean arrowWildcard) {
		int len = strTopic.length();
		if (len <= 0)
			throw new IllegalArgumentException("A topic must be at least one character long.");

		char prev = 0;
		for (int pos = 0; pos < len; ++pos) {
			char ch = strTopic.charAt(pos);
			if (ch == TOPIC_WILDCARD_MULTI) {
				if (!arrowWildcard)
					throw new IllegalArgumentException("A topic must not contain wildcard characters : " + strTopic);
				else if (prev != 0 && prev != TOPIC_LEVEL_SEPARATOR)
					throw new IllegalArgumentException("'#' wildcard can be specified only on its own or next to the '/' : " + strTopic);
			}
			else if (ch == TOPIC_WILDCARD_SINGLE) {
				if (!arrowWildcard)
					throw new IllegalArgumentException("A topic must not contain wildcard characters : " + strTopic);
				else if (prev != 0 && prev != TOPIC_LEVEL_SEPARATOR)
					throw new IllegalArgumentException("'+' wildcard can be specified only on its own or next to the '/' : " + strTopic);
			}
			else if (ch == TOPIC_LEVEL_SEPARATOR) {
				if (prev == TOPIC_LEVEL_SEPARATOR)
					throw new IllegalArgumentException("'/' can not be specified after '/' : " + strTopic);
			}
			else {
				if (prev == TOPIC_WILDCARD_MULTI || prev == TOPIC_WILDCARD_SINGLE) 
					throw new IllegalArgumentException("A character can not be specified after wildcard : " + strTopic);
			}
			prev = ch;
		}
		// check last character
		if (prev == TOPIC_LEVEL_SEPARATOR) {
			throw new IllegalArgumentException("'/' can not be specified at the end of the topic : " + strTopic);
		}
	}
	
	private void parseMqttConfigParams(String[] params, int beginpos) {
		String clientID = null;
		
		for (int i = beginpos; i < params.length; ++i) {
			String strParam = params[i];
			if (strParam.length() <= 0)
				continue;	// skip
			int eqPos = strParam.indexOf(MQPARAM_SEPARATOR);
			if (eqPos < 0) {
				// error
				throw new IllegalArgumentException("Invalid MQTT parameter : " + strParam);
			}
			String strKey = strParam.substring(0, eqPos);
			String strVal = strParam.substring(eqPos+1);
			if (MQPARAM_KEY_QOS.equalsIgnoreCase(strKey)) {
				// QOS
				int qos;
				try {
					qos = Integer.parseInt(strVal);
				} catch (Throwable ex) {
					throw new IllegalArgumentException("QoS is not 0, 1 or 2 : " + strParam);
				}
				if (qos < 0 || qos > 2) {
					throw new IllegalArgumentException("QoS is not 0, 1 or 2 : " + strParam);
				}
				_qos = qos;
			}
			else if (MQPARAM_KEY_CLIENTID.equalsIgnoreCase(strKey)) {
				// ClientID
				if (strVal == null || strVal.length() <= 0)
					throw new IllegalArgumentException("Client ID is empty : " + strParam);
				else if (strVal.length() > MqttUtil.MAX_CLIENTID_LENGTH)
					throw new IllegalArgumentException("Client ID is greater than " + MqttUtil.MAX_CLIENTID_LENGTH + " characters : " + strParam);
				clientID = strVal;
			}
			else {
				// Undefined key
				throw new IllegalArgumentException("Undefined MQTT parameter : " + strParam);
			}
		}
		
		// update client ID
		_clientID = clientID;
	}

	/**
	 * CSVレコードフォーマットの文字列を、フィールドに分解する。
	 * @param csvString	文字列
	 * @return	フィールドの配列
	 * @throws NullPointerException	<em>csvString</em> が <tt>null</tt> の場合
	 */
	static protected String[] parseCsvFields(String csvString) {
		StringBuilder sb = new StringBuilder(csvString);
		CsvFieldDecoder csvDecoder = new CsvFieldDecoder(sb);
		csvDecoder.decode();
		csvDecoder.flush();
		return csvDecoder.fields();
	}
	
	static protected boolean isEqualObjects(Object obj1, Object obj2) {
		if (obj1 == null)
			return (obj2 == null);
		else
			return obj1.equals(obj2);
	}
	
	static protected void appendCsvField(String value, StringBuilder appendBuffer) {
		// フィールド区切りは追加しない
		if (value != null && value.length() > 0) {
			boolean enquoted = false;
			int spos = appendBuffer.length();
			int len = value.length();
			int pos = 0;
			for (; pos < len; ++pos) {
				char ch = value.charAt(pos);
				appendBuffer.append(ch);
				if (ch == CsvUtil.CSV_DELIMITER_CHAR || ch == CsvUtil.TSV_DELIMITER_CHAR || ch == CsvUtil.CR || ch == CsvUtil.LF) {
					enquoted = true;
					appendBuffer.insert(spos, CsvUtil.CSV_QUOTE_CHAR);
					++pos;
					break;
				}
				else if (ch == CsvUtil.CSV_QUOTE_CHAR) {
					enquoted = true;
					appendBuffer.insert(spos, CsvUtil.CSV_QUOTE_CHAR);
					appendBuffer.append(CsvUtil.CSV_QUOTE_CHAR);
					++pos;
					break;
				}
			}
			if (enquoted) {
				for (; pos < len; ++pos) {
					char ch = value.charAt(pos);
					appendBuffer.append(ch);
					if (ch == CsvUtil.CSV_QUOTE_CHAR) {
						appendBuffer.append(ch);
					}
				}
				appendBuffer.append(CsvUtil.CSV_QUOTE_CHAR);	// end of enquot
			}
		}
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
