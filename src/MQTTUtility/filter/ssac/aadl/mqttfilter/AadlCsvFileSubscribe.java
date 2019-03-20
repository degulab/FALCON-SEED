package ssac.aadl.mqttfilter;

public class AadlCsvFileSubscribe extends ssac.aadl.runtime.AADLModule
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
		AadlCsvFileSubscribe module = new AadlCsvFileSubscribe();
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
	** 指定されたサーバーのトピックに対しサブスクライブし、受信した単一メッセージをファイルへ保存する。
	** $1[STR]	- 接続先
	** $2[STR]	- トピック
	** $3[STR]	- 伝送品質[QoS] (0 | 1 | 2)
	** $4[STR]	- 受信メッセージタイプ (binary | default | platform | 文字コード)
	** $5[OUT]	- 出力先ファイル(CSV)
	*/
	@Override
	protected int aadlRun(String[] args) {
		if (args.length < 5) {
			System.err.println("'CsvFileSort' module needs 5 arguments! : " + args.length);
			return (1);
		}
		
		final String strServerURI   = args[0];	// $1[STR]
		final String strTopic       = args[1];	// $2[STR]
		final String strQOS         = args[2];	// $3[STR]
		final String strMsgEncoding = args[3];	// $4[STR]
		final String outFile        = args[4];	// $5[OUT]

		// check server URI
		ssac.aadl.runtime.mqtt.internal.AADLMqttFunctions.validNotEmptyString(strServerURI, "Server name or address is empty.");

		// check topic
		ssac.aadl.runtime.mqtt.internal.AADLMqttFunctions.validNotEmptyString(strTopic, "Topic is empty.");

		// check QoS
		int qos = ssac.aadl.runtime.mqtt.internal.AADLMqttFunctions.validQosParam(strQOS, null);

		// check message encoding
		String msgEncoding;
		msgEncoding = ssac.aadl.runtime.mqtt.internal.AADLMqttFunctions.validMessageEncoding(strMsgEncoding, "Invalid message type");

		// check required out file
		ssac.aadl.runtime.mqtt.internal.AADLMqttFunctions.validNotEmptyString(outFile, "Output file name is empty.");

		// subscribe
		//@{
			ssac.aadl.runtime.mqtt.internal.AADLMqttFileSubscriber asub = new ssac.aadl.runtime.mqtt.internal.AADLMqttFileSubscriber(strServerURI);
			asub.setVerboseMode(true);
			asub.subscribeFile(strTopic, qos, msgEncoding,
				new java.io.File(outFile),
				ssac.aadl.runtime.AADLFunctions.getDefaultCsvEncoding());

			// check error
			if (asub.isError()) {
				String errmsg = asub.getLastErrorMessage();
				if (errmsg == null || errmsg.length() <= 0)
					errmsg = "Failed to subscribe!";
				throw new RuntimeException(errmsg, asub.getLastErrorCause());
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
