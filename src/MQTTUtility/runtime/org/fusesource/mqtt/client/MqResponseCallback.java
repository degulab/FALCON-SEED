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
 * @(#)MqResponseCallback.java	0.3.1	2013/07/05
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package org.fusesource.mqtt.client;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import ssac.aadl.runtime.mqtt.MqDeliveryToken;
import ssac.aadl.runtime.mqtt.MqttException;
import ssac.aadl.runtime.mqtt.MqttRuntimeException;
import ssac.aadl.runtime.mqtt.MqttTimedOutException;

/**
 * MQTT response callback class for MQTT client operations.
 * 
 * @version 0.3.1	2013/07/05
 * @since 0.3.0
 * 
 * @author Yasunari Ishizuka (PieCake,Inc.)
 * @author Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 * @author Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
public class MqResponseCallback<T> implements Callback<T>, MqDeliveryToken
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	protected final CountDownLatch	_syncDone = new CountDownLatch(1);
	protected Throwable	_failedReason = null;
	protected boolean		_completed = false;
	protected T			_responseValue;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 正常終了したときに渡された値を返す。
	 */
	public T getValue() {
		synchronized (this) {
			return _responseValue;
		}
	}

	/**
	 * 正常終了の場合に <tt>true</tt> を返す。
	 */
	public boolean isSucceeded() {
		synchronized (this) {
			return (_completed && _failedReason==null);
		}
	}

	/**
	 * エラーが発生している場合に <tt>true</tt> を返す。
	 */
	public boolean hasError() {
		synchronized (this) {
			return (_failedReason != null);
		}
	}

	/**
	 * このオブジェクトが保持しているエラーを返す。
	 * エラーがない場合は <tt>null</tt> を返す。
	 */
	public Throwable getError() {
		synchronized (this) {
			return _failedReason;
		}
	}

	/**
	 * 指定された時間、応答があるまで待機する。
	 * @param timeout	待機する時間をミリ秒で指定する。0 を指定した場合は待機せず即座に処理を返す。
	 * 					また、負の値を指定した場合は、応答するまで待機する。
	 * @return	応答があった場合は <tt>true</tt>、タイムアウト時間が経過した場合は <tt>false</tt>
	 * @throws InterruptedException	待機中のスレッドに対し割り込みが発生した場合
	 */
	public boolean await(long timeout) throws InterruptedException
	{
		boolean result = false;
		if (timeout < 0L) {
			// wait infinite
			_syncDone.await();
			result = true;
		} else {
			// wait until specified time
			result = _syncDone.await(timeout, TimeUnit.MILLISECONDS);
		}
		
		return result;
	}

	//------------------------------------------------------------
	// ssac.aadl.runtime.mqtt.MqDeliveryToken interfaces
	//------------------------------------------------------------
	
	/**
	 * 処理が正常に完了している場合に <tt>true</tt> を返す。
	 * エラーなどで処理が中断された場合は <tt>false</tt> を返す。
	 */
	public boolean isCompleted() {
		synchronized (this) {
			return _completed;
		}
	}
	/**
	 * 処理が完了するまで待機する。
	 * <p>このメソッドでは例外は <code>MqttException</code> オブジェクトとしてスローされる。
	 * より詳細な例外要因がある場合、<code>MqttException.getCause()</code> が要因となった例外オブジェクトを返す。
	 * なお、待機中に割り込みが発生した場合、<code>MqttException.getCause()</code> では <code>InterruptedException</code> が返される。
	 * @throws MqttException	待機中にエラーが発生した場合
	 */
	public void waitForCompletion() throws MqttException
	{
		waitForCompletion(-1);
	}

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
	public void waitForCompletion(long timeout) throws MqttException
	{
		boolean result;
		try {
			result = await(timeout);
		}
		catch (InterruptedException ex) {
			throw new MqttException(ex);
		}
		
		// check result
		synchronized (this) {
			Throwable error = _failedReason;
			if (error instanceof MqttRuntimeException) {
				throw (MqttRuntimeException)error;
			}
			else if (error instanceof MqttException) {
				throw (MqttException)error;
			}
			else if (error != null) {
				throw new MqttException(error);
			}
			else if (!result && !_completed) {
				throw new MqttTimedOutException();
			}
		}
	}

	//------------------------------------------------------------
	// org.fusesource.mqtt.client.Client<T> interfaces
	//------------------------------------------------------------
	
	@Override
	public void onSuccess(T value) {
		synchronized (this) {
			_responseValue = value;
			_completed = true;
		}
		_syncDone.countDown();
	}
	
	@Override
	public void onFailure(Throwable value) {
		synchronized (this) {
			if (_failedReason == null) {
				// 最初のエラーを保存
				_failedReason = value;
			}
		}
		_syncDone.countDown();
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
}
