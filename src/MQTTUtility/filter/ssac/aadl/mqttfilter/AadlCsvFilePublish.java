package ssac.aadl.mqttfilter;

public class AadlCsvFilePublish extends ssac.aadl.runtime.AADLModule
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

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
		AadlCsvFilePublish module = new AadlCsvFilePublish();
		int ret = module.aadlRun(args);
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
	** 指定されたサーバーのトピックに、指定のファイルを単一メッセージとしてパブリッシュする。
	** $1[STR]	- 接続先
	** $2[STR]	- トピック
	** $3[STR]	- 伝送品質[QoS] (0 | 1 | 2)
	** $4[STR]	- サーバーにメッセージを残す[retained] (true | false)
	** $5[IN]	- 入力ファイル(CSV)
	** $6[STR]	- 送信メッセージタイプ (binary | default | platform | 文字コード)
	*/
	@Override
	protected int aadlRun(String[] args) {
		if (args.length < 6) {
			System.err.println("'CsvFileSort' module needs 6 arguments! : " + args.length);
			return (1);
		}
		
		final String strServerURI   = args[0];	// $1[STR]
		final String strTopic       = args[1];	// $2[STR]
		final String strQOS         = args[2];	// $3[STR]
		final String strRetained    = args[3];	// $4[STR]
		final String inFile         = args[4];	// $5[IN]
		final String strMsgEncoding = args[5];	// $6[STR]

		// check server URI
		ssac.aadl.runtime.mqtt.internal.AADLMqttFunctions.validNotEmptyString(strServerURI, "Server name or address is empty.");

		// check topic
		ssac.aadl.runtime.mqtt.internal.AADLMqttFunctions.validNotEmptyString(strTopic, "Topic is empty.");

		// check QoS
		int qos = ssac.aadl.runtime.mqtt.internal.AADLMqttFunctions.validQosParam(strQOS, null);

		// retained
		boolean retained = ssac.aadl.runtime.mqtt.internal.AADLMqttFunctions.toBoolean(strRetained);

		// check message encoding
		String msgEncoding;
		msgEncoding = ssac.aadl.runtime.mqtt.internal.AADLMqttFunctions.validMessageEncoding(strMsgEncoding, "Invalid message type");

		// check required in file
		ssac.aadl.runtime.mqtt.internal.AADLMqttFunctions.validNotEmptyString(inFile, "Input file name is empty.");

		// publish
		//@{
			ssac.aadl.runtime.mqtt.internal.AADLMqttFilePublisher apub = new ssac.aadl.runtime.mqtt.internal.AADLMqttFilePublisher(strServerURI);
			apub.setVerboseMode(true);
			apub.publishFile(strTopic, qos, retained, msgEncoding,
				new java.io.File(inFile),
				ssac.aadl.runtime.AADLFunctions.getDefaultCsvEncoding());

			// check error
			if (apub.isError()) {
				String errmsg = apub.getLastErrorMessage();
				if (errmsg == null || errmsg.length() <= 0)
					errmsg = "Failed to publish!";
				throw new RuntimeException(errmsg, apub.getLastErrorCause());
			}
		//}@;
			
		// completed
		return 0;
	}

	//------------------------------------------------------------
	// AADL functions
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
