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
 *  Copyright 2012  SOARS Project.
 *  <author> Hiroshi Deguchi(SOARS Project.)
 *  <author> Yasunari Ishizuka(PieCake.inc,)
 */
/*
 * @(#)DtalgeIOTestHelper.java	1.70	2012/05/30
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.runtime;

import java.math.BigDecimal;
import java.util.ArrayList;

import dtalge.DtBase;
import dtalge.Dtalge;

public class DtalgeIOTestHelper
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	// 書き込み用ランダム順序のテストデータ(空文字列も含む)
	static public final Object[] writeRandomAlgeData =
	{
		new DtBase("基底08", "string" , "#", "#"), "文字列08",
		new DtBase("基底08", "decimal", "#", "#"), new BigDecimal("-0.2108"),
		new DtBase("基底08", "boolean", "#", "#"), false,
		new DtBase("基底02", "string" , "#", "#"), "!%!%!%文字列02",
		new DtBase("基底02", "decimal", "#", "#"), new BigDecimal("123.02"),
		new DtBase("基底02", "boolean", "#", "#"), false,
		new DtBase("基底0d", "string" , "#", "#"), "!%",
		new DtBase("基底0d", "decimal", "#", "#"), new BigDecimal("-1.13"),
		new DtBase("基底0d", "boolean", "#", "#"), false,
		new DtBase("基底06", "string" , "#", "#"), " !%文字列06",
		new DtBase("基底06", "decimal", "#", "#"), new BigDecimal("-1006"),
		new DtBase("基底06", "boolean", "#", "#"), false,
		new DtBase("基底04", "string" , "#", "#"), "文字列!%04",
		new DtBase("基底04", "decimal", "#", "#"), new BigDecimal("-975.04"),
		new DtBase("基底04", "boolean", "#", "#"), false,
		new DtBase("基底0c", "string" , "#", "#"), "!%n",
		new DtBase("基底0c", "decimal", "#", "#"), new BigDecimal("1.12"),
		new DtBase("基底0c", "boolean", "#", "#"), true,
		new DtBase("基底0b", "string" , "#", "#"), "",
		new DtBase("基底0b", "decimal", "#", "#"), BigDecimal.ZERO,
		new DtBase("基底0b", "boolean", "#", "#"), false,
		new DtBase("基底0a", "string" , "#", "#"), null,
		new DtBase("基底0a", "decimal", "#", "#"), null,
		new DtBase("基底0a", "boolean", "#", "#"), null,
		new DtBase("基底03", "string" , "#", "#"), null,
		new DtBase("基底03", "decimal", "#", "#"), null,
		new DtBase("基底03", "boolean", "#", "#"), null,
		new DtBase("基底09", "string" , "#", "#"), "文字列49",
		new DtBase("基底09", "decimal", "#", "#"), null,
		new DtBase("基底09", "boolean", "#", "#"), false,
		new DtBase("基底07", "string" , "#", "#"), null,
		new DtBase("基底07", "decimal", "#", "#"), null,
		new DtBase("基底07", "boolean", "#", "#"), null,
		new DtBase("基底05", "string" , "#", "#"), "文字列45",
		new DtBase("基底05", "decimal", "#", "#"), null,
		new DtBase("基底05", "boolean", "#", "#"), false,
		new DtBase("基底01", "string" , "#", "#"), null,
		new DtBase("基底01", "decimal", "#", "#"), null,
		new DtBase("基底01", "boolean", "#", "#"), null,
	};

	// 読み込み用ランダム順序のテストデータ(空文字列はnull)
	static public final Object[] readRandomAlgeData =
	{
		new DtBase("基底08", "string" , "#", "#"), "文字列08",
		new DtBase("基底08", "decimal", "#", "#"), new BigDecimal("-0.2108"),
		new DtBase("基底08", "boolean", "#", "#"), false,
		new DtBase("基底02", "string" , "#", "#"), "!%!%!%文字列02",
		new DtBase("基底02", "decimal", "#", "#"), new BigDecimal("123.02"),
		new DtBase("基底02", "boolean", "#", "#"), false,
		new DtBase("基底0d", "string" , "#", "#"), "!%",
		new DtBase("基底0d", "decimal", "#", "#"), new BigDecimal("-1.13"),
		new DtBase("基底0d", "boolean", "#", "#"), false,
		new DtBase("基底06", "string" , "#", "#"), " !%文字列06",
		new DtBase("基底06", "decimal", "#", "#"), new BigDecimal("-1006"),
		new DtBase("基底06", "boolean", "#", "#"), false,
		new DtBase("基底04", "string" , "#", "#"), "文字列!%04",
		new DtBase("基底04", "decimal", "#", "#"), new BigDecimal("-975.04"),
		new DtBase("基底04", "boolean", "#", "#"), false,
		new DtBase("基底0c", "string" , "#", "#"), "!%n",
		new DtBase("基底0c", "decimal", "#", "#"), new BigDecimal("1.12"),
		new DtBase("基底0c", "boolean", "#", "#"), true,
		new DtBase("基底0b", "string" , "#", "#"), null,
		new DtBase("基底0b", "decimal", "#", "#"), BigDecimal.ZERO,
		new DtBase("基底0b", "boolean", "#", "#"), false,
		new DtBase("基底0a", "string" , "#", "#"), null,
		new DtBase("基底0a", "decimal", "#", "#"), null,
		new DtBase("基底0a", "boolean", "#", "#"), null,
		new DtBase("基底03", "string" , "#", "#"), null,
		new DtBase("基底03", "decimal", "#", "#"), null,
		new DtBase("基底03", "boolean", "#", "#"), null,
		new DtBase("基底09", "string" , "#", "#"), "文字列49",
		new DtBase("基底09", "decimal", "#", "#"), null,
		new DtBase("基底09", "boolean", "#", "#"), false,
		new DtBase("基底07", "string" , "#", "#"), null,
		new DtBase("基底07", "decimal", "#", "#"), null,
		new DtBase("基底07", "boolean", "#", "#"), null,
		new DtBase("基底05", "string" , "#", "#"), "文字列45",
		new DtBase("基底05", "decimal", "#", "#"), null,
		new DtBase("基底05", "boolean", "#", "#"), false,
		new DtBase("基底01", "string" , "#", "#"), null,
		new DtBase("基底01", "decimal", "#", "#"), null,
		new DtBase("基底01", "boolean", "#", "#"), null,
	};

	// 読み込み用ランダム順序のテストデータ(空文字列はnull)
	static public final Object[] readSortedAlgeData =
	{
		new DtBase("基底01", "string" , "#", "#"), null,
		new DtBase("基底01", "decimal", "#", "#"), null,
		new DtBase("基底01", "boolean", "#", "#"), null,
		new DtBase("基底02", "string" , "#", "#"), "!%!%!%文字列02",
		new DtBase("基底02", "decimal", "#", "#"), new BigDecimal("123.02"),
		new DtBase("基底02", "boolean", "#", "#"), false,
		new DtBase("基底03", "string" , "#", "#"), null,
		new DtBase("基底03", "decimal", "#", "#"), null,
		new DtBase("基底03", "boolean", "#", "#"), null,
		new DtBase("基底04", "string" , "#", "#"), "文字列!%04",
		new DtBase("基底04", "decimal", "#", "#"), new BigDecimal("-975.04"),
		new DtBase("基底04", "boolean", "#", "#"), false,
		new DtBase("基底05", "string" , "#", "#"), "文字列45",
		new DtBase("基底05", "decimal", "#", "#"), null,
		new DtBase("基底05", "boolean", "#", "#"), false,
		new DtBase("基底06", "string" , "#", "#"), " !%文字列06",
		new DtBase("基底06", "decimal", "#", "#"), new BigDecimal("-1006"),
		new DtBase("基底06", "boolean", "#", "#"), false,
		new DtBase("基底07", "string" , "#", "#"), null,
		new DtBase("基底07", "decimal", "#", "#"), null,
		new DtBase("基底07", "boolean", "#", "#"), null,
		new DtBase("基底08", "string" , "#", "#"), "文字列08",
		new DtBase("基底08", "decimal", "#", "#"), new BigDecimal("-0.2108"),
		new DtBase("基底08", "boolean", "#", "#"), false,
		new DtBase("基底09", "string" , "#", "#"), "文字列49",
		new DtBase("基底09", "decimal", "#", "#"), null,
		new DtBase("基底09", "boolean", "#", "#"), false,
		new DtBase("基底0a", "string" , "#", "#"), null,
		new DtBase("基底0a", "decimal", "#", "#"), null,
		new DtBase("基底0a", "boolean", "#", "#"), null,
		new DtBase("基底0b", "string" , "#", "#"), null,
		new DtBase("基底0b", "decimal", "#", "#"), BigDecimal.ZERO,
		new DtBase("基底0b", "boolean", "#", "#"), false,
		new DtBase("基底0c", "string" , "#", "#"), "!%n",
		new DtBase("基底0c", "decimal", "#", "#"), new BigDecimal("1.12"),
		new DtBase("基底0c", "boolean", "#", "#"), true,
		new DtBase("基底0d", "string" , "#", "#"), "!%",
		new DtBase("基底0d", "decimal", "#", "#"), new BigDecimal("-1.13"),
		new DtBase("基底0d", "boolean", "#", "#"), false,
	};

	// 読み込み用ランダム順序のテストデータ(空文字列はnull)
	static public final Object[] readHalfSortedAlgeData =
	{
		new DtBase("基底01", "string" , "#", "#"), null,
		new DtBase("基底01", "decimal", "#", "#"), null,
		new DtBase("基底01", "boolean", "#", "#"), null,
		new DtBase("基底02", "string" , "#", "#"), "!%!%!%文字列02",
		new DtBase("基底02", "decimal", "#", "#"), new BigDecimal("123.02"),
		new DtBase("基底02", "boolean", "#", "#"), false,
		new DtBase("基底03", "string" , "#", "#"), null,
		new DtBase("基底03", "decimal", "#", "#"), null,
		new DtBase("基底03", "boolean", "#", "#"), null,
		new DtBase("基底04", "string" , "#", "#"), "文字列!%04",
		new DtBase("基底04", "decimal", "#", "#"), new BigDecimal("-975.04"),
		new DtBase("基底04", "boolean", "#", "#"), false,
		new DtBase("基底05", "string" , "#", "#"), "文字列45",
		new DtBase("基底05", "decimal", "#", "#"), null,
		new DtBase("基底05", "boolean", "#", "#"), false,
		new DtBase("基底06", "string" , "#", "#"), " !%文字列06",
		new DtBase("基底06", "decimal", "#", "#"), new BigDecimal("-1006"),
		new DtBase("基底06", "boolean", "#", "#"), false,
		new DtBase("基底07", "string" , "#", "#"), null,
		new DtBase("基底07", "decimal", "#", "#"), null,
		new DtBase("基底07", "boolean", "#", "#"), null,
		new DtBase("基底08", "string" , "#", "#"), "文字列08",
		new DtBase("基底08", "decimal", "#", "#"), new BigDecimal("-0.2108"),
		new DtBase("基底08", "boolean", "#", "#"), false,
		new DtBase("基底09", "string" , "#", "#"), "文字列49",
		new DtBase("基底09", "decimal", "#", "#"), null,
		new DtBase("基底09", "boolean", "#", "#"), false,
		new DtBase("基底0d", "string" , "#", "#"), "!%",
		new DtBase("基底0d", "decimal", "#", "#"), new BigDecimal("-1.13"),
		new DtBase("基底0d", "boolean", "#", "#"), false,
		new DtBase("基底0c", "string" , "#", "#"), "!%n",
		new DtBase("基底0c", "decimal", "#", "#"), new BigDecimal("1.12"),
		new DtBase("基底0c", "boolean", "#", "#"), true,
		new DtBase("基底0b", "string" , "#", "#"), null,
		new DtBase("基底0b", "decimal", "#", "#"), BigDecimal.ZERO,
		new DtBase("基底0b", "boolean", "#", "#"), false,
		new DtBase("基底0a", "string" , "#", "#"), null,
		new DtBase("基底0a", "decimal", "#", "#"), null,
		new DtBase("基底0a", "boolean", "#", "#"), null,
	};

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	static public boolean equalElementSequence(Dtalge alge, Object...elements) {
		boolean result;
		
		if (alge.isEmpty()) {
			if (elements != null && elements.length > 0) {
				System.err.println("equalElementSequence : Dtalge("
						+ alge.getNumElements()
						+ ") != elements("
						+ (elements != null ? String.valueOf(elements.length) : "null")
						+ ")");
				result = false;
			} else {
				result = true;
			}
		}
		else if (elements == null || (elements.length%2 != 0) || alge.getNumElements() != (elements.length/2)) {
			System.err.println("equalElementSequence : Dtalge("
					+ alge.getNumElements()
					+ ") != elements("
					+ (elements != null ? String.valueOf(elements.length) : "null")
					+ ")");
			result = false;
		}
		else {
			int index = 0;
			result = true;
			for (Dtalge entryAlge : alge) {
				Object eBase = elements[index];
				Object eValue = elements[index+1];
				DtBase aBase = entryAlge.getOneBase();
				Object aValue = entryAlge.getOneValue();
				if (!aBase.equals(eBase)) {
					System.err.println("equalElementSequence : not equal DtBase : "
							+ String.valueOf(aValue) + aBase.toString()
							+ " : base[" + String.valueOf(index)
							+ "]=" + String.valueOf(eBase));
					result = false;
				}
				if (aValue==null || eValue==null) {
					if (aValue != eValue) {
						System.err.println("equalElementSequence : not equal value : "
								+ String.valueOf(aValue) + aBase.toString()
								+ " : value[" + String.valueOf(index+1)
								+ "]=" + String.valueOf(eValue));
						result = false;
					}
				}
				else if (aValue instanceof BigDecimal) {
					if (((BigDecimal)aValue).compareTo((BigDecimal)eValue) != 0) {
						System.err.println("equalElementSequence : not equal value : "
								+ String.valueOf(aValue) + aBase.toString()
								+ " : value[" + String.valueOf(index+1)
								+ "]=" + String.valueOf(eValue));
						result = false;
					}
				}
				else if (!aValue.equals(eValue)) {
					System.err.println("equalElementSequence : not equal value : "
							+ String.valueOf(aValue) + aBase.toString()
							+ " : value[" + String.valueOf(index+1)
							+ "]=" + String.valueOf(eValue));
					result = false;
				}
				index+=2;
			}
		}
		
		return result;
	}
	
	static public boolean equalNoNullElementSequence(Dtalge alge, Object...elements) {
		if (elements == null || elements.length < 1) {
			return (alge==null ? true : alge.isEmpty());
		}
		
		ArrayList<Object> list = new ArrayList<Object>(elements.length);
		for (int index = 0; index < elements.length; index+=2) {
			Object eBase  = elements[index];
			Object eValue = ((index+1)<elements.length ? elements[index+1] : null);
			if (eValue != null) {
				list.add(eBase);
				list.add(eValue);
			}
		}
		Object[] noNullElements = list.toArray(new Object[list.size()]);
		
		return equalElementSequence(alge, noNullElements);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
