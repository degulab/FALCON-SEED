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
 *  Copyright 2007-2013  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
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
	 */
	public boolean isCompleted();
}
