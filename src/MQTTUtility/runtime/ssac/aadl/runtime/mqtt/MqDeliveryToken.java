/*
 * @(#)MqDeliveryToken.java	0.3.1	2013/07/05
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MqDeliveryToken.java	0.3.0	2013/06/30
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.runtime.mqtt;

/**
 * MQTT 送信データの待機オブジェクト。
 * 
 * @version 0.3.1	2013/07/05
 * @since 0.3.0
 * 
 * @author Yasunari Ishizuka (PieCake,Inc.)
 * @author Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 * @author Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
public interface MqDeliveryToken
{
	/**
	 * 処理が完了するまで待機する。
	 * <p>このメソッドでは例外は <code>MqttException</code> オブジェクトとしてスローされる。
	 * より詳細な例外要因がある場合、<code>MqttException.getCause()</code> が要因となった例外オブジェクトを返す。
	 * なお、待機中に割り込みが発生した場合、<code>MqttException.getCause()</code> では <code>InterruptedException</code> が返される。
	 * @throws MqttException	待機中にエラーが発生した場合
	 */
	public void waitForCompletion() throws MqttException;

	/**
	 * 処理が完了するまで、もしくは指定されたタイムアウト時間が経過するまで待機する。
	 * <p>このメソッドでは例外は <code>MqttException</code> オブジェクトとしてスローされる。
	 * より詳細な例外要因がある場合、<code>MqttException.getCause()</code> が要因となった例外オブジェクトを返す。
	 * なお、待機中に割り込みが発生した場合、<code>MqttException.getCause()</code> では <code>InterruptedException</code> が返される。
	 * @param timeout	待機する時間をミリ秒で指定する。0 を指定した場合は待機せず即座に処理を返す。
	 * 					また、負の値を指定した場合は、処理が完了するまで待機する。
	 * @throws MqttTimedOutException	処理完了前に待機時間が経過した場合
	 * @throws MqttException			待機中にエラーが発生した場合
	 */
	public void waitForCompletion(long timeout) throws MqttException;

	/**
	 * 処理が正常に完了している場合に <tt>true</tt> を返す。
	 * エラーなどで処理が中断された場合は <tt>false</tt> を返す。
	 * @return	正常完了なら <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean isCompleted();
}
