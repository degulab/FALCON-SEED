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
 *  Copyright 2007-2016  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)EtcConvertProgressHandler.java	3.3.0	2016/05/10
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.excel2csv;

/**
 * <code>[Excel to CSV]</code> 変換実行時の進捗状況更新用ハンドラ。
 * 
 * @version 3.3.0
 * @since 3.3.0
 */
public class EtcConvertProgressHandler
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** 進捗状況カウントの最大値 **/
	private long	_maxValue;
	/** 進捗状況カウントの現在値 **/
	private long	_curValue;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public EtcConvertProgressHandler() {
		this(100L);
	}
	
	public EtcConvertProgressHandler(long maxValue) {
		if (maxValue < 0L)
			throw new IllegalArgumentException("Maximum value is negative : " + maxValue);
		_maxValue = maxValue;
		_curValue = 0L;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public boolean isCanceled() {
		return false;
	}
	
	public long getMaxValue() {
		return _maxValue;
	}
	
	public void setMaxValue(long newMaxValue) {
		if (newMaxValue < 0L)
			throw new IllegalArgumentException("Maximum value is negative : " + newMaxValue);
		_maxValue = newMaxValue;
		if (_curValue > _maxValue) {
			_curValue = _maxValue;
		}
	}
	
	public long getCurrentValue() {
		return _curValue;
	}
	
	public void incrementCurrentValue() {
		setCurrentValue(_curValue + 1L);
	}
	
	public void addCurrentValue(long append) {
		setCurrentValue(_curValue + append);
	}
	
	public void setCurrentValue(long curValue) {
		if (curValue < 0L)
			_curValue = 0L;
		else if (curValue > _maxValue)
			_curValue = _maxValue;
		else
			_curValue = curValue;
	}
	
	public double getProgressRate() {
		if (_maxValue > 0L)
			return ((double)_curValue / _maxValue * 100.0);
		else
			return 0.0;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
