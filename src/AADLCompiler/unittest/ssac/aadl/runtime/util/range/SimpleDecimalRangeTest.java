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
 *  Copyright 2007-2011  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)SimpleDecimalRangeTest.java	1.70	2011/05/17
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.runtime.util.range;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.NoSuchElementException;

import junit.framework.TestCase;

/**
 * {@link ssac.aadl.runtime.util.range.SimpleDecimalRange} クラスのテスト。
 * 
 * @version 1.70	2011/05/17
 * 
 * @author Yasunari Ishizuka (PieCake,Inc.)
 * 
 * @since 1.70
 */
public class SimpleDecimalRangeTest extends TestCase
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static public final VerifyRangePattern[] rangePatterns = {
		// [0]
		new VerifyRangePattern(true,
			toDecimal(0), toDecimal(10), toDecimal(1), toDecimal(0), toDecimal(10),
			//--- iterations
			new BigDecimal[]{
				toDecimal(0), toDecimal(1), toDecimal(2), toDecimal(3), toDecimal(4),
				toDecimal(5), toDecimal(6), toDecimal(7), toDecimal(8), toDecimal(9),
				toDecimal(10),
			},
			//--- contains
			new Object[]{
				toDecimal(0), (short)1, 2, 3L, 4.0F,
				5.0, 6.0, toDecimal(7), toDecimal(8), toDecimal(9),
			},
			//--- not contains
			new Object[]{
				0.1, 0.2F, 10.0000001, (short)11, -1L,
				toDecimal("-0.00000000000000000000000000000001"), toDecimal("0.00000000000000000000000000000001"),
				Short.MIN_VALUE, Short.MAX_VALUE,
				Integer.MIN_VALUE, Integer.MAX_VALUE,
				Long.MIN_VALUE, Long.MAX_VALUE,
				Float.MIN_VALUE, Float.MAX_VALUE,
				Double.MIN_VALUE, Double.MAX_VALUE,
				toDecimal("-9223372036854775812"), toDecimal("9223372036854775812"),
				toDecimal("-9223372036854775812.000000000000001"), toDecimal("9223372036854775812.0000000000000001"),
				"0", "hoge",
			}
		),
		// [1]
		new VerifyRangePattern(false,
			toDecimal(0), toDecimal(10), toDecimal(1), toDecimal(0), toDecimal(9),
			//--- iterations
			new BigDecimal[]{
				toDecimal(0), toDecimal(1), toDecimal(2), toDecimal(3), toDecimal(4),
				toDecimal(5), toDecimal(6), toDecimal(7), toDecimal(8), toDecimal(9),
			},
			//--- contains
			new Object[]{
				toDecimal(0), (short)1, 2, 3L, 4.0F,
				5.0, 6.0, toDecimal(7), toDecimal(8), toDecimal(9),
			},
			//--- not contains
			new Object[]{
				0.1, 0.2F, 10.0000001, (short)11, -1L, 10,
				toDecimal("-0.00000000000000000000000000000001"), toDecimal("0.00000000000000000000000000000001"),
				Short.MIN_VALUE, Short.MAX_VALUE,
				Integer.MIN_VALUE, Integer.MAX_VALUE,
				Long.MIN_VALUE, Long.MAX_VALUE,
				Float.MIN_VALUE, Float.MAX_VALUE,
				Double.MIN_VALUE, Double.MAX_VALUE,
				toDecimal("-9223372036854775812"), toDecimal("9223372036854775812"),
				toDecimal("-9223372036854775812.000000000000001"), toDecimal("9223372036854775812.0000000000000001"),
				"0", "hoge",
			}
		),
		// [2]
		new VerifyRangePattern(true,
			toDecimal(-10L), toDecimal(10L), toDecimal(2L), toDecimal(-10L), toDecimal(10L),
			//--- iterations
			new BigDecimal[]{
				toDecimal(-10L), toDecimal(-8L), toDecimal(-6L), toDecimal(-4L), toDecimal(-2L),
				toDecimal(0L), toDecimal(2L), toDecimal(4L), toDecimal(6L), toDecimal(8L), toDecimal(10L)
			},
			//--- contains
			new Object[]{
				-10L, -8, (short)-6, -4.0f, -2.0,
				0.0, 2.0f, (short)4, 6, 8L, toDecimal(10),
			},
			//--- not contains
			new Object[]{
				-11L, toDecimal(11), 4.5, -3.5f,
				Short.MIN_VALUE, Short.MAX_VALUE,
				Integer.MIN_VALUE, Integer.MAX_VALUE,
				Long.MIN_VALUE, Long.MAX_VALUE,
				Float.MIN_VALUE, Float.MAX_VALUE,
				Double.MIN_VALUE, Double.MAX_VALUE,
				toDecimal("-9223372036854775812"), toDecimal("9223372036854775812"),
				toDecimal("-9223372036854775812.000000000000001"), toDecimal("9223372036854775812.0000000000000001"),
				"0", "hoge",
			}
		),
		// [3]
		new VerifyRangePattern(false,
			toDecimal(-10L), toDecimal(10L), toDecimal(2L), toDecimal(-10L), toDecimal(8L),
			//--- iterations
			new BigDecimal[]{
				toDecimal(-10L), toDecimal(-8L), toDecimal(-6L), toDecimal(-4L), toDecimal(-2L),
				toDecimal(0L), toDecimal(2L), toDecimal(4L), toDecimal(6L), toDecimal(8L),
			},
			//--- contains
			new Object[]{
				-10L, -8, (short)-6, -4.0f, -2.0,
				0.0, 2.0f, (short)4, 6, 8L,
			},
			//--- not contains
			new Object[]{
				-11L, toDecimal(11), 4.5, -3.5f, 10L,
				Short.MIN_VALUE, Short.MAX_VALUE,
				Integer.MIN_VALUE, Integer.MAX_VALUE,
				Long.MIN_VALUE, Long.MAX_VALUE,
				Float.MIN_VALUE, Float.MAX_VALUE,
				Double.MIN_VALUE, Double.MAX_VALUE,
				toDecimal("-9223372036854775812"), toDecimal("9223372036854775812"),
				toDecimal("-9223372036854775812.000000000000001"), toDecimal("9223372036854775812.0000000000000001"),
				"0", "hoge",
			}
		),
		// [4]
		new VerifyRangePattern(true,
			toDecimal(10L), toDecimal(-10L), toDecimal(-2L), toDecimal(-10L), toDecimal(10L),
			//--- iterations
			new BigDecimal[]{
				toDecimal(10L), toDecimal(8L), toDecimal(6L), toDecimal(4L), toDecimal(2L), toDecimal(0L),
				toDecimal(-2L), toDecimal(-4L), toDecimal(-6L), toDecimal(-8L), toDecimal(-10L)
			},
			//--- contains
			new Object[]{
				-10L, -8, (short)-6, -4.0f, -2.0,
				0.0, 2.0f, (short)4, 6, 8L, toDecimal(10),
			},
			//--- not contains
			new Object[]{
				-11L, toDecimal(11), 4.5, -3.5f,
				Short.MIN_VALUE, Short.MAX_VALUE,
				Integer.MIN_VALUE, Integer.MAX_VALUE,
				Long.MIN_VALUE, Long.MAX_VALUE,
				Float.MIN_VALUE, Float.MAX_VALUE,
				Double.MIN_VALUE, Double.MAX_VALUE,
				toDecimal("-9223372036854775812"), toDecimal("9223372036854775812"),
				toDecimal("-9223372036854775812.000000000000001"), toDecimal("9223372036854775812.0000000000000001"),
				"0", "hoge",
			}
		),
		// [5]
		new VerifyRangePattern(false,
			toDecimal(10L), toDecimal(-10L), toDecimal(-2L), toDecimal(-8L), toDecimal(10L),
			//--- iterations
			new BigDecimal[]{
				toDecimal(10L), toDecimal(8L), toDecimal(6L), toDecimal(4L), toDecimal(2L), toDecimal(0L),
				toDecimal(-2L), toDecimal(-4L), toDecimal(-6L), toDecimal(-8L),
			},
			//--- contains
			new Object[]{
				-8, (short)-6, -4.0f, -2.0,
				0.0, 2.0f, (short)4, 6, 8L, toDecimal(10),
			},
			//--- not contains
			new Object[]{
				-11L, toDecimal(11), 4.5, -3.5f, -10,
				Short.MIN_VALUE, Short.MAX_VALUE,
				Integer.MIN_VALUE, Integer.MAX_VALUE,
				Long.MIN_VALUE, Long.MAX_VALUE,
				Float.MIN_VALUE, Float.MAX_VALUE,
				Double.MIN_VALUE, Double.MAX_VALUE,
				toDecimal("-9223372036854775812"), toDecimal("9223372036854775812"),
				toDecimal("-9223372036854775812.000000000000001"), toDecimal("9223372036854775812.0000000000000001"),
				"0", "hoge",
			}
		),
		// [6]
		new VerifyRangePattern(true,
			toDecimal("-2"), toDecimal("2"), toDecimal("0.5"), toDecimal("-2.0"), toDecimal("2.0"),
			//--- iterations
			new BigDecimal[]{
				toDecimal("-2"), toDecimal("-1.5"), toDecimal("-1"), toDecimal("-0.5"),
				toDecimal("0"), toDecimal("0.5"), toDecimal("1"), toDecimal("1.5"), toDecimal("2")
			},
			//--- contains
			new Object[]{
				-2.0, toDecimal("-1.5"), (short)-1, toDecimal("-0.5"),
				0.0f, toDecimal("0.5"), 1L, toDecimal("1.5"), 2,
			},
			//--- not contains
			new Object[]{
				-3L, toDecimal(3), 1.25f, -0.25,
				Short.MIN_VALUE, Short.MAX_VALUE,
				Integer.MIN_VALUE, Integer.MAX_VALUE,
				Long.MIN_VALUE, Long.MAX_VALUE,
				Float.MIN_VALUE, Float.MAX_VALUE,
				Double.MIN_VALUE, Double.MAX_VALUE,
				toDecimal("-9223372036854775812"), toDecimal("9223372036854775812"),
				toDecimal("-9223372036854775812.000000000000001"), toDecimal("9223372036854775812.0000000000000001"),
				"0", "hoge",
			}
		),
		// [7]
		new VerifyRangePattern(false,
			toDecimal("-2"), toDecimal("2"), toDecimal("0.5"), toDecimal("-2.0"), toDecimal("1.5"),
			//--- iterations
			new BigDecimal[]{
				toDecimal("-2"), toDecimal("-1.5"), toDecimal("-1"), toDecimal("-0.5"),
				toDecimal("0"), toDecimal("0.5"), toDecimal("1"), toDecimal("1.5"),
			},
			//--- contains
			new Object[]{
				-2.0, toDecimal("-1.5"), (short)-1, toDecimal("-0.5"),
				0.0f, toDecimal("0.5"), 1L, toDecimal("1.5"), 
			},
			//--- not contains
			new Object[]{
				-3L, toDecimal(3), 1.25f, -0.25, 2,
				Short.MIN_VALUE, Short.MAX_VALUE,
				Integer.MIN_VALUE, Integer.MAX_VALUE,
				Long.MIN_VALUE, Long.MAX_VALUE,
				Float.MIN_VALUE, Float.MAX_VALUE,
				Double.MIN_VALUE, Double.MAX_VALUE,
				toDecimal("-9223372036854775812"), toDecimal("9223372036854775812"),
				toDecimal("-9223372036854775812.000000000000001"), toDecimal("9223372036854775812.0000000000000001"),
				"0", "hoge",
			}
		),
		// [8]
		new VerifyRangePattern(true,
			toDecimal("2"), toDecimal("-2"), toDecimal("-0.5"), toDecimal("-2.0"), toDecimal("2.0"),
			//--- iterations
			new BigDecimal[]{
				toDecimal("2"), toDecimal("1.5"), toDecimal("1"), toDecimal("0.5"), toDecimal("0"),
				toDecimal("-0.5"), toDecimal("-1"), toDecimal("-1.5"), toDecimal("-2")
			},
			//--- contains
			new Object[]{
				-2.0, toDecimal("-1.5"), (short)-1, toDecimal("-0.5"),
				0.0f, toDecimal("0.5"), 1L, toDecimal("1.5"), 2,
			},
			//--- not contains
			new Object[]{
				-3L, toDecimal(3), 1.25f, -0.25,
				Short.MIN_VALUE, Short.MAX_VALUE,
				Integer.MIN_VALUE, Integer.MAX_VALUE,
				Long.MIN_VALUE, Long.MAX_VALUE,
				Float.MIN_VALUE, Float.MAX_VALUE,
				Double.MIN_VALUE, Double.MAX_VALUE,
				toDecimal("-9223372036854775812"), toDecimal("9223372036854775812"),
				toDecimal("-9223372036854775812.000000000000001"), toDecimal("9223372036854775812.0000000000000001"),
				"0", "hoge",
			}
		),
		// [9]
		new VerifyRangePattern(false,
			toDecimal("2"), toDecimal("-2"), toDecimal("-0.5"), toDecimal("-1.5"), toDecimal("2.0"),
			//--- iterations
			new BigDecimal[]{
				toDecimal("2"), toDecimal("1.5"), toDecimal("1"), toDecimal("0.5"), toDecimal("0"),
				toDecimal("-0.5"), toDecimal("-1"), toDecimal("-1.5"),
			},
			//--- contains
			new Object[]{
				toDecimal("-1.5"), (short)-1, toDecimal("-0.5"),
				0.0f, toDecimal("0.5"), 1L, toDecimal("1.5"), 2,
			},
			//--- not contains
			new Object[]{
				-3L, toDecimal(3), 1.25f, -0.25, -2,
				Short.MIN_VALUE, Short.MAX_VALUE,
				Integer.MIN_VALUE, Integer.MAX_VALUE,
				Long.MIN_VALUE, Long.MAX_VALUE,
				Float.MIN_VALUE, Float.MAX_VALUE,
				Double.MIN_VALUE, Double.MAX_VALUE,
				toDecimal("-9223372036854775812"), toDecimal("9223372036854775812"),
				toDecimal("-9223372036854775812.000000000000001"), toDecimal("9223372036854775812.0000000000000001"),
				"0", "hoge",
			}
		),
		// [10]
		new VerifyRangePattern(true,
			toDecimal(-32768L), toDecimal(32766L), toDecimal(13107L), toDecimal(-32768L), toDecimal(19660L),
			//--- iterations
			new BigDecimal[]{
				toDecimal(-32768L), toDecimal(-19661L), toDecimal(-6554L), toDecimal(6553L), toDecimal(19660L),
			},
			//--- contains
			new Object[]{
				-32768L, -19661, (short)-6554, 6553.0f, 19660.0
			},
			//--- not contains
			new Object[]{
				Short.MAX_VALUE, -32767L, toDecimal("-19660"), (short)-6553, 6552.0f, 19661.0,
				Integer.MIN_VALUE, Integer.MAX_VALUE,
				Long.MIN_VALUE, Long.MAX_VALUE,
				Float.MIN_VALUE, Float.MAX_VALUE,
				Double.MIN_VALUE, Double.MAX_VALUE,
				toDecimal("-9223372036854775812"), toDecimal("9223372036854775812"),
				toDecimal("-9223372036854775812.000000000000001"), toDecimal("9223372036854775812.0000000000000001"),
				"0", "hoge",
			}
		),
		// [11]
		new VerifyRangePattern(false,
			toDecimal(-32768L), toDecimal(32767L), toDecimal(13107L), toDecimal(-32768L), toDecimal(19660L),
			//--- iterations
			new BigDecimal[]{
				toDecimal(-32768L), toDecimal(-19661L), toDecimal(-6554L), toDecimal(6553L), toDecimal(19660L),
			},
			//--- contains
			new Object[]{
				-32768L, -19661, (short)-6554, 6553.0f, 19660.0
			},
			//--- not contains
			new Object[]{
				Short.MAX_VALUE, -32767L, toDecimal("-19660"), (short)-6553, 6552.0f, 19661.0,
				Integer.MIN_VALUE, Integer.MAX_VALUE,
				Long.MIN_VALUE, Long.MAX_VALUE,
				Float.MIN_VALUE, Float.MAX_VALUE,
				Double.MIN_VALUE, Double.MAX_VALUE,
				toDecimal("-9223372036854775812"), toDecimal("9223372036854775812"),
				toDecimal("-9223372036854775812.000000000000001"), toDecimal("9223372036854775812.0000000000000001"),
				"0", "hoge",
			}
		),
		// [12]
		new VerifyRangePattern(true,
			toDecimal(2147483647L), toDecimal(-2147483647L), toDecimal(-858993459L), toDecimal(-1288490189L), toDecimal(2147483647L),
			//--- iterations
			new BigDecimal[]{
				toDecimal(2147483647L), toDecimal(1288490188L), toDecimal(429496729L), toDecimal(-429496730L), toDecimal(-1288490189L)
			},
			//--- contains
			new Object[]{
				2147483647L, 1288490188.0, 429496729.0, -429496730L, toDecimal(-1288490189L)
			},
			//--- not contains
			new Object[]{
				Integer.MIN_VALUE, 2147483646, 1288490187L, 429496728.0, toDecimal(-429496731L), toDecimal(-1288490190L),
				Long.MIN_VALUE, Long.MAX_VALUE,
				Float.MIN_VALUE, Float.MAX_VALUE,
				Double.MIN_VALUE, Double.MAX_VALUE,
				toDecimal("-9223372036854775812"), toDecimal("9223372036854775812"),
				toDecimal("-9223372036854775812.000000000000001"), toDecimal("9223372036854775812.0000000000000001"),
				"0", "hoge",
			}
		),
		// [13]
		new VerifyRangePattern(false,
			toDecimal(2147483647L), toDecimal(-2147483648L), toDecimal(-858993459L), toDecimal(-1288490189L), toDecimal(2147483647L),
			//--- iterations
			new BigDecimal[]{
				toDecimal(2147483647L), toDecimal(1288490188L), toDecimal(429496729L), toDecimal(-429496730L), toDecimal(-1288490189L)
			},
			//--- contains
			new Object[]{
				2147483647L, 1288490188.0, 429496729.0, -429496730L, toDecimal(-1288490189L)
			},
			//--- not contains
			new Object[]{
				Integer.MIN_VALUE, 2147483646, 1288490187L, 429496728.0, toDecimal(-429496731L), toDecimal(-1288490190L),
				Long.MIN_VALUE, Long.MAX_VALUE,
				Float.MIN_VALUE, Float.MAX_VALUE,
				Double.MIN_VALUE, Double.MAX_VALUE,
				toDecimal("-9223372036854775812"), toDecimal("9223372036854775812"),
				toDecimal("-9223372036854775812.000000000000001"), toDecimal("9223372036854775812.0000000000000001"),
				"0", "hoge",
			}
		),
		// [14]
		new VerifyRangePattern(true,
			toDecimal(-9223372036854775808L), toDecimal(9223372036854775806L), toDecimal(3689348814741910323L), toDecimal(-9223372036854775808L), toDecimal(5534023222112865484L),
			//--- iterations
			new BigDecimal[]{
				toDecimal(-9223372036854775808L), toDecimal(-5534023222112865485L), toDecimal(-1844674407370955162L), toDecimal(1844674407370955161L), toDecimal(5534023222112865484L),
			},
			//--- contains
			new Object[]{
				toDecimal(-9223372036854775808L), -5534023222112865485L, toDecimal(-1844674407370955162L), toDecimal("1844674407370955161.0"), toDecimal(5534023222112865484L),
			},
			//--- not contains
			new Object[]{
				Long.MAX_VALUE, -9223372036854775807L, toDecimal(-5534023222112865484L), -1844674407370955161.0, toDecimal("1844674407370955162.0"), toDecimal(5534023222112865485L),
				toDecimal("-9223372036854775809"), toDecimal("-9223372036854775808.0000000001"),
				toDecimal("-9223372036854775807.9999999999"), toDecimal("9223372036854775806.9999999999"),
				toDecimal("9223372036854775807.0000000001"), toDecimal("9223372036854775808"),
				Float.MIN_VALUE, Float.MAX_VALUE,
				Double.MIN_VALUE, Double.MAX_VALUE,
				toDecimal("-9223372036854775812"), toDecimal("9223372036854775812"),
				toDecimal("-9223372036854775812.000000000000001"), toDecimal("9223372036854775812.0000000000000001"),
				"0", "hoge",
			}
		),
		// [15]
		new VerifyRangePattern(false,
			toDecimal(-9223372036854775808L), toDecimal(9223372036854775807L), toDecimal(3689348814741910323L), toDecimal(-9223372036854775808L), toDecimal(5534023222112865484L),
			//--- iterations
			new BigDecimal[]{
				toDecimal(-9223372036854775808L), toDecimal(-5534023222112865485L), toDecimal(-1844674407370955162L), toDecimal(1844674407370955161L), toDecimal(5534023222112865484L),
			},
			//--- contains
			new Object[]{
				toDecimal(-9223372036854775808L), -5534023222112865485L, toDecimal(-1844674407370955162L), toDecimal("1844674407370955161.0"), toDecimal(5534023222112865484L),
			},
			//--- not contains
			new Object[]{
				Long.MAX_VALUE, -9223372036854775807L, toDecimal(-5534023222112865484L), -1844674407370955161.0, toDecimal("1844674407370955162.0"), toDecimal(5534023222112865485L),
				toDecimal("-9223372036854775809"), toDecimal("-9223372036854775808.0000000001"),
				toDecimal("-9223372036854775807.9999999999"), toDecimal("9223372036854775806.9999999999"),
				toDecimal("9223372036854775807.0000000001"), toDecimal("9223372036854775808"),
				Float.MIN_VALUE, Float.MAX_VALUE,
				Double.MIN_VALUE, Double.MAX_VALUE,
				toDecimal("-9223372036854775812"), toDecimal("9223372036854775812"),
				toDecimal("-9223372036854775812.000000000000001"), toDecimal("9223372036854775812.0000000000000001"),
				"0", "hoge",
			}
		),
		// [16]
		new VerifyRangePattern(true,
			toDecimal(0), toDecimal(0), toDecimal(0), null, null,
			//--- iterations
			new BigDecimal[]{},
			//--- contains
			new Object[]{},
			//--- not contains
			new Object[]{
				"0", (short)0, 0, 0L, 0.0F, 0.0,
				Short.MIN_VALUE, Short.MAX_VALUE,
				Integer.MIN_VALUE, Integer.MAX_VALUE,
				Long.MIN_VALUE, Long.MAX_VALUE,
				Float.MIN_VALUE, Float.MAX_VALUE,
				Double.MIN_VALUE, Double.MAX_VALUE,
			}
		),
		// [17]
		new VerifyRangePattern(false,
			toDecimal(0), toDecimal(0), toDecimal(0), null, null,
			//--- iterations
			new BigDecimal[]{},
			//--- contains
			new Object[]{},
			//--- not contains
			new Object[]{
				"0", (short)0, 0, 0L, 0.0F, 0.0,
				Short.MIN_VALUE, Short.MAX_VALUE,
				Integer.MIN_VALUE, Integer.MAX_VALUE,
				Long.MIN_VALUE, Long.MAX_VALUE,
				Float.MIN_VALUE, Float.MAX_VALUE,
				Double.MIN_VALUE, Double.MAX_VALUE,
				"0", "hoge",
			}
		),
		// [18]
		new VerifyRangePattern(true,
			toDecimal(-32768L), toDecimal(32766L), toDecimal(-13107L), null, null,
			//--- iterations
			new BigDecimal[]{},
			//--- contains
			new Object[]{},
			//--- not contains
			new Object[]{
				-32768L, -19661, (short)-6554, 6553.0f, 19660.0,
				Short.MAX_VALUE, -32767L, toDecimal("-19660"), (short)-6553, 6552.0f, 19661.0,
				Integer.MIN_VALUE, Integer.MAX_VALUE,
				Long.MIN_VALUE, Long.MAX_VALUE,
				Float.MIN_VALUE, Float.MAX_VALUE,
				Double.MIN_VALUE, Double.MAX_VALUE,
				toDecimal("-9223372036854775812"), toDecimal("9223372036854775812"),
				toDecimal("-9223372036854775812.000000000000001"), toDecimal("9223372036854775812.0000000000000001"),
				"0", "hoge",
			}
		),
		// [19]
		new VerifyRangePattern(false,
			toDecimal(-32768L), toDecimal(32767L), toDecimal(-13107L), null, null,
			//--- iterations
			new BigDecimal[]{},
			//--- contains
			new Object[]{},
			//--- not contains
			new Object[]{
				-32768L, -19661, (short)-6554, 6553.0f, 19660.0,
				Short.MAX_VALUE, -32767L, toDecimal("-19660"), (short)-6553, 6552.0f, 19661.0,
				Integer.MIN_VALUE, Integer.MAX_VALUE,
				Long.MIN_VALUE, Long.MAX_VALUE,
				Float.MIN_VALUE, Float.MAX_VALUE,
				Double.MIN_VALUE, Double.MAX_VALUE,
				toDecimal("-9223372036854775812"), toDecimal("9223372036854775812"),
				toDecimal("-9223372036854775812.000000000000001"), toDecimal("9223372036854775812.0000000000000001"),
				"0", "hoge",
			}
		),
		// [20]
		new VerifyRangePattern(true,
			toDecimal(2147483647L), toDecimal(-2147483647L), toDecimal(858993459L), null, null,
			//--- iterations
			new BigDecimal[]{},
			//--- contains
			new Object[]{},
			//--- not contains
			new Object[]{
				2147483647L, 1288490188.0, 429496729.0, -429496730L, toDecimal(-1288490189L),
				Integer.MIN_VALUE, 2147483646, 1288490187L, 429496728.0, toDecimal(-429496731L), toDecimal(-1288490190L),
				Long.MIN_VALUE, Long.MAX_VALUE,
				Float.MIN_VALUE, Float.MAX_VALUE,
				Double.MIN_VALUE, Double.MAX_VALUE,
				toDecimal("-9223372036854775812"), toDecimal("9223372036854775812"),
				toDecimal("-9223372036854775812.000000000000001"), toDecimal("9223372036854775812.0000000000000001"),
				"0", "hoge",
			}
		),
		// [21]
		new VerifyRangePattern(false,
			toDecimal(2147483647L), toDecimal(-2147483648L), toDecimal(858993459L), null, null,
			//--- iterations
			new BigDecimal[]{},
			//--- contains
			new Object[]{},
			//--- not contains
			new Object[]{
				2147483647L, 1288490188.0, 429496729.0, -429496730L, toDecimal(-1288490189L),
				Integer.MIN_VALUE, 2147483646, 1288490187L, 429496728.0, toDecimal(-429496731L), toDecimal(-1288490190L),
				Long.MIN_VALUE, Long.MAX_VALUE,
				Float.MIN_VALUE, Float.MAX_VALUE,
				Double.MIN_VALUE, Double.MAX_VALUE,
				toDecimal("-9223372036854775812"), toDecimal("9223372036854775812"),
				toDecimal("-9223372036854775812.000000000000001"), toDecimal("9223372036854775812.0000000000000001"),
				"0", "hoge",
			}
		),
	};

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	static protected BigDecimal toDecimal(long value) {
		return BigDecimal.valueOf(value);
	}
	
	static protected BigDecimal toDecimal(String value) {
		return new BigDecimal(value);
	}
	
	static protected int toHashCode(VerifyRangePattern pat) {
		//--- isIncludeRangeLast
		int h = pat.includeLast ? 1231 : 1237;
		//--- rangeFirst
		h = 31 * h + (pat.from.stripTrailingZeros().hashCode());
		//--- rangeLast
		h = 31 * h + (pat.to.stripTrailingZeros().hashCode());
		//--- rangeStep
		h = 31 * h + (pat.step.stripTrailingZeros().hashCode());
		
		return h;
	}
	
	static protected String toString(VerifyRangePattern pat) {
		StringBuilder sb = new StringBuilder();
		sb.append('[');
		sb.append(pat.from.stripTrailingZeros().toPlainString());
		sb.append(" -> ");
		sb.append(pat.to.stripTrailingZeros().toPlainString());
		sb.append(", step=");
		sb.append(pat.step.stripTrailingZeros().toPlainString());
		sb.append(", includeLast=");
		sb.append(pat.includeLast);
		sb.append(']');
		return sb.toString();
	}
	
	static public class VerifyRangePattern {
		public VerifyRangePattern(boolean includeLast, BigDecimal vf, BigDecimal vt, BigDecimal vs,
				BigDecimal min, BigDecimal max,
				BigDecimal[] ite, Object[] con, Object[] noc)
		{
			this.includeLast = includeLast;
			this.from = vf;
			this.to = vt;
			this.step = vs;
			this.rangemin = min;
			this.rangemax = max;
			this.iterations = ite;
			this.contains = con;
			this.notcontains = noc;
		}

		public final boolean   includeLast;
		public final BigDecimal from;
		public final BigDecimal to;
		public final BigDecimal step;
		public final BigDecimal rangemin;
		public final BigDecimal rangemax;
		public final BigDecimal[] iterations;
		public final Object[] contains;
		public final Object[] notcontains;
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	//------------------------------------------------------------
	// Test Cases for 1.70
	//------------------------------------------------------------

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.SimpleDecimalRange#SimpleDecimalRange(java.math.BigDecimal)}.
	 */
	public void testSimpleDecimalRangeBigDecimal() {
		// check null
		try {
			new SimpleDecimalRange(null);
			fail("Must be throw NullPointerException.");
		} catch (NullPointerException ex) {}
		
		// check new instances
		for (int i = 0; i < rangePatterns.length; i++) {
			String msg = String.format("Test params : [%d] ", i);
			VerifyRangePattern pat = rangePatterns[i];
			SimpleDecimalRange range = new SimpleDecimalRange(pat.to);
			assertEquals(msg, false, range._includeLast);
			assertEquals(msg, 0, BigDecimal.ZERO.compareTo(range._impl.getDecimalFromValue()));
			assertEquals(msg, 0, pat.to  .compareTo(range._last));
			if (pat.to.signum() >= 0) {
				assertEquals(msg, 0, BigDecimal.ONE.compareTo(range._step));
			} else {
				assertEquals(msg, 0, BigDecimal.ONE.negate().compareTo(range._step));
			}
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.SimpleDecimalRange#SimpleDecimalRange(java.math.BigDecimal, boolean)}.
	 */
	public void testSimpleDecimalRangeBigDecimalBoolean() {
		// check null
		try {
			new SimpleDecimalRange(null, true);
			fail("Must be throw NullPointerException.");
		} catch (NullPointerException ex) {}
		
		// check new instances
		for (int i = 0; i < rangePatterns.length; i++) {
			String msg = String.format("Test params : [%d] ", i);
			VerifyRangePattern pat = rangePatterns[i];
			SimpleDecimalRange range = new SimpleDecimalRange(pat.to, pat.includeLast);
			assertEquals(msg, pat.includeLast, range._includeLast);
			assertEquals(msg, 0, BigDecimal.ZERO.compareTo(range._impl.getDecimalFromValue()));
			assertEquals(msg, 0, pat.to  .compareTo(range._last));
			if (pat.to.signum() >= 0) {
				assertEquals(msg, 0, BigDecimal.ONE.compareTo(range._step));
			} else {
				assertEquals(msg, 0, BigDecimal.ONE.negate().compareTo(range._step));
			}
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.SimpleDecimalRange#SimpleDecimalRange(java.math.BigDecimal, java.math.BigDecimal)}.
	 */
	public void testSimpleDecimalRangeBigDecimalBigDecimal() {
		// check null
		try {
			new SimpleDecimalRange(null, null);
			fail("Must be throw NullPointerException.");
		} catch (NullPointerException ex) {}
		try {
			new SimpleDecimalRange(null, BigDecimal.ONE);
			fail("Must be throw NullPointerException.");
		} catch (NullPointerException ex) {}
		try {
			new SimpleDecimalRange(BigDecimal.ZERO, null);
			fail("Must be throw NullPointerException.");
		} catch (NullPointerException ex) {}
		
		// check new instances
		for (int i = 0; i < rangePatterns.length; i++) {
			String msg = String.format("Test params : [%d] ", i);
			VerifyRangePattern pat = rangePatterns[i];
			SimpleDecimalRange range = new SimpleDecimalRange(pat.from, pat.to);
			assertEquals(msg, false, range._includeLast);
			assertEquals(msg, 0, pat.from.compareTo(range._impl.getDecimalFromValue()));
			assertEquals(msg, 0, pat.to  .compareTo(range._last));
			if (pat.to.subtract(pat.from).signum() >= 0) {
				assertEquals(msg, 0, BigDecimal.ONE.compareTo(range._step));
			} else {
				assertEquals(msg, 0, BigDecimal.ONE.negate().compareTo(range._step));
			}
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.SimpleDecimalRange#SimpleDecimalRange(java.math.BigDecimal, java.math.BigDecimal, boolean)}.
	 */
	public void testSimpleDecimalRangeBigDecimalBigDecimalBoolean() {
		// check null
		try {
			new SimpleDecimalRange(null, null, true);
			fail("Must be throw NullPointerException.");
		} catch (NullPointerException ex) {}
		try {
			new SimpleDecimalRange(null, BigDecimal.ONE, false);
			fail("Must be throw NullPointerException.");
		} catch (NullPointerException ex) {}
		try {
			new SimpleDecimalRange(BigDecimal.ZERO, null, true);
			fail("Must be throw NullPointerException.");
		} catch (NullPointerException ex) {}
		
		// check new instances
		for (int i = 0; i < rangePatterns.length; i++) {
			String msg = String.format("Test params : [%d] ", i);
			VerifyRangePattern pat = rangePatterns[i];
			SimpleDecimalRange range = new SimpleDecimalRange(pat.from, pat.to, pat.includeLast);
			assertEquals(msg, pat.includeLast, range._includeLast);
			assertEquals(msg, 0, pat.from.compareTo(range._impl.getDecimalFromValue()));
			assertEquals(msg, 0, pat.to  .compareTo(range._last));
			if (pat.to.subtract(pat.from).signum() >= 0) {
				assertEquals(msg, 0, BigDecimal.ONE.compareTo(range._step));
			} else {
				assertEquals(msg, 0, BigDecimal.ONE.negate().compareTo(range._step));
			}
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.SimpleDecimalRange#SimpleDecimalRange(java.math.BigDecimal, java.math.BigDecimal, java.math.BigDecimal)}.
	 */
	public void testSimpleDecimalRangeBigDecimalBigDecimalBigDecimal() {
		// check null
		try {
			new SimpleDecimalRange(null, null, null);
			fail("Must be throw NullPointerException.");
		} catch (NullPointerException ex) {}
		try {
			new SimpleDecimalRange(BigDecimal.ZERO, BigDecimal.ONE, null);
			fail("Must be throw NullPointerException.");
		} catch (NullPointerException ex) {}
		try {
			new SimpleDecimalRange(null, BigDecimal.ONE, BigDecimal.ONE);
			fail("Must be throw NullPointerException.");
		} catch (NullPointerException ex) {}
		try {
			new SimpleDecimalRange(BigDecimal.ZERO, null, BigDecimal.ONE);
			fail("Must be throw NullPointerException.");
		} catch (NullPointerException ex) {}
		
		// check new instances
		for (int i = 0; i < rangePatterns.length; i++) {
			String msg = String.format("Test params : [%d] ", i);
			VerifyRangePattern pat = rangePatterns[i];
			SimpleDecimalRange range = new SimpleDecimalRange(pat.from, pat.to, pat.step);
			assertEquals(msg, false, range._includeLast);
			assertEquals(msg, 0, pat.from.compareTo(range._impl.getDecimalFromValue()));
			assertEquals(msg, 0, pat.to  .compareTo(range._last));
			assertEquals(msg, 0, pat.step.compareTo(range._step));
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.SimpleDecimalRange#SimpleDecimalRange(java.math.BigDecimal, java.math.BigDecimal, java.math.BigDecimal, boolean)}.
	 */
	public void testSimpleDecimalRangeBigDecimalBigDecimalBigDecimalBoolean() {
		// check null
		try {
			new SimpleDecimalRange(null, null, null, true);
			fail("Must be throw NullPointerException.");
		} catch (NullPointerException ex) {}
		try {
			new SimpleDecimalRange(BigDecimal.ZERO, BigDecimal.ONE, null, false);
			fail("Must be throw NullPointerException.");
		} catch (NullPointerException ex) {}
		try {
			new SimpleDecimalRange(null, BigDecimal.ONE, BigDecimal.ONE, true);
			fail("Must be throw NullPointerException.");
		} catch (NullPointerException ex) {}
		try {
			new SimpleDecimalRange(BigDecimal.ZERO, null, BigDecimal.ONE, false);
			fail("Must be throw NullPointerException.");
		} catch (NullPointerException ex) {}
		
		// check new instances
		for (int i = 0; i < rangePatterns.length; i++) {
			String msg = String.format("Test params : [%d] ", i);
			VerifyRangePattern pat = rangePatterns[i];
			SimpleDecimalRange range = new SimpleDecimalRange(pat.from, pat.to, pat.step, pat.includeLast);
			assertEquals(msg, pat.includeLast, range._includeLast);
			assertEquals(msg, 0, pat.from.compareTo(range._impl.getDecimalFromValue()));
			assertEquals(msg, 0, pat.to  .compareTo(range._last));
			assertEquals(msg, 0, pat.step.compareTo(range._step));
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.SimpleDecimalRange#isEmpty()}.
	 */
	public void testIsEmpty() {
		for (int i = 0; i < rangePatterns.length; i++) {
			String msg = String.format("Test params : [%d] ", i);
			VerifyRangePattern pat = rangePatterns[i];
			SimpleDecimalRange range = new SimpleDecimalRange(pat.from, pat.to, pat.step, pat.includeLast);
			assertEquals(msg, pat.includeLast, range._includeLast);
			assertEquals(msg, 0, pat.from.compareTo(range._impl.getDecimalFromValue()));
			assertEquals(msg, 0, pat.to  .compareTo(range._last));
			assertEquals(msg, 0, pat.step.compareTo(range._step));
			
			if (pat.rangemin != null) {
				assertFalse(msg, range.isEmpty());
			} else {
				assertTrue(msg, range.isEmpty());
			}
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.SimpleDecimalRange#isIncludeRangeLast()}.
	 */
	public void testIsIncludeRangeLast() {
		for (int i = 0; i < rangePatterns.length; i++) {
			String msg = String.format("Test params : [%d] ", i);
			VerifyRangePattern pat = rangePatterns[i];
			SimpleDecimalRange range = new SimpleDecimalRange(pat.from, pat.to, pat.step, pat.includeLast);
			assertEquals(msg, pat.includeLast, range._includeLast);
			assertEquals(msg, 0, pat.from.compareTo(range._impl.getDecimalFromValue()));
			assertEquals(msg, 0, pat.to  .compareTo(range._last));
			assertEquals(msg, 0, pat.step.compareTo(range._step));
			
			assertEquals(msg, pat.includeLast, range.isIncludeRangeLast());
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.SimpleDecimalRange#isIncremental()}.
	 */
	public void testIsIncremental() {
		for (int i = 0; i < rangePatterns.length; i++) {
			String msg = String.format("Test params : [%d] ", i);
			VerifyRangePattern pat = rangePatterns[i];
			SimpleDecimalRange range = new SimpleDecimalRange(pat.from, pat.to, pat.step, pat.includeLast);
			assertEquals(msg, pat.includeLast, range._includeLast);
			assertEquals(msg, 0, pat.from.compareTo(range._impl.getDecimalFromValue()));
			assertEquals(msg, 0, pat.to  .compareTo(range._last));
			assertEquals(msg, 0, pat.step.compareTo(range._step));

			if (pat.step.signum() >= 0) {
				assertTrue(range.isIncremental());
			} else {
				assertFalse(range.isIncremental());
			}
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.SimpleDecimalRange#containsValue(java.lang.Object)}.
	 */
	public void testContainsValue() {
		for (int i = 0; i < rangePatterns.length; i++) {
			String msg = String.format("Test params : [%d] ", i);
			VerifyRangePattern pat = rangePatterns[i];
			SimpleDecimalRange range = new SimpleDecimalRange(pat.from, pat.to, pat.step, pat.includeLast);
			assertEquals(msg, pat.includeLast, range._includeLast);
			assertEquals(msg, 0, pat.from.compareTo(range._impl.getDecimalFromValue()));
			assertEquals(msg, 0, pat.to  .compareTo(range._last));
			assertEquals(msg, 0, pat.step.compareTo(range._step));

			Object[] con = pat.contains;
			Object[] ni  = pat.notcontains;
			//--- includes
			for (Object v : con) {
				assertTrue(msg + " value=" + v, range.containsValue(v));
			}
			//--- not includes
			for (Object v : ni) {
				assertFalse(msg + " value=" + v, range.containsValue(v));
			}
			assertFalse(msg + " value=null", range.containsValue(null));
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.SimpleDecimalRange#rangeFirst()}.
	 */
	public void testRangeFirst() {
		for (int i = 0; i < rangePatterns.length; i++) {
			String msg = String.format("Test params : [%d] ", i);
			VerifyRangePattern pat = rangePatterns[i];
			SimpleDecimalRange range = new SimpleDecimalRange(pat.from, pat.to, pat.step, pat.includeLast);
			assertEquals(msg, pat.includeLast, range._includeLast);
			assertEquals(msg, 0, pat.from.compareTo(range._impl.getDecimalFromValue()));
			assertEquals(msg, 0, pat.to  .compareTo(range._last));
			assertEquals(msg, 0, pat.step.compareTo(range._step));

			assertEquals(msg, 0, pat.from.compareTo(range.rangeFirst()));
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.SimpleDecimalRange#rangeLast()}.
	 */
	public void testRangeLast() {
		for (int i = 0; i < rangePatterns.length; i++) {
			String msg = String.format("Test params : [%d] ", i);
			VerifyRangePattern pat = rangePatterns[i];
			SimpleDecimalRange range = new SimpleDecimalRange(pat.from, pat.to, pat.step, pat.includeLast);
			assertEquals(msg, pat.includeLast, range._includeLast);
			assertEquals(msg, 0, pat.from.compareTo(range._impl.getDecimalFromValue()));
			assertEquals(msg, 0, pat.to  .compareTo(range._last));
			assertEquals(msg, 0, pat.step.compareTo(range._step));

			assertEquals(msg, 0, pat.to.compareTo(range.rangeLast()));
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.SimpleDecimalRange#rangeStep()}.
	 */
	public void testRangeStep() {
		for (int i = 0; i < rangePatterns.length; i++) {
			String msg = String.format("Test params : [%d] ", i);
			VerifyRangePattern pat = rangePatterns[i];
			SimpleDecimalRange range = new SimpleDecimalRange(pat.from, pat.to, pat.step, pat.includeLast);
			assertEquals(msg, pat.includeLast, range._includeLast);
			assertEquals(msg, 0, pat.from.compareTo(range._impl.getDecimalFromValue()));
			assertEquals(msg, 0, pat.to  .compareTo(range._last));
			assertEquals(msg, 0, pat.step.compareTo(range._step));

			assertEquals(msg, 0, pat.step.compareTo(range.rangeStep()));
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.SimpleDecimalRange#rangeMin()}.
	 */
	public void testRangeMin() {
		for (int i = 0; i < rangePatterns.length; i++) {
			String msg = String.format("Test params : [%d] ", i);
			VerifyRangePattern pat = rangePatterns[i];
			SimpleDecimalRange range = new SimpleDecimalRange(pat.from, pat.to, pat.step, pat.includeLast);
			assertEquals(msg, pat.includeLast, range._includeLast);
			assertEquals(msg, 0, pat.from.compareTo(range._impl.getDecimalFromValue()));
			assertEquals(msg, 0, pat.to  .compareTo(range._last));
			assertEquals(msg, 0, pat.step.compareTo(range._step));

			if (pat.rangemin != null) {
				assertEquals(msg, 0, pat.rangemin.compareTo(range.rangeMin()));
			} else {
				assertNull(msg, range.rangeMin());
			}
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.SimpleDecimalRange#rangeMax()}.
	 */
	public void testRangeMax() {
		for (int i = 0; i < rangePatterns.length; i++) {
			String msg = String.format("Test params : [%d] ", i);
			VerifyRangePattern pat = rangePatterns[i];
			SimpleDecimalRange range = new SimpleDecimalRange(pat.from, pat.to, pat.step, pat.includeLast);
			assertEquals(msg, pat.includeLast, range._includeLast);
			assertEquals(msg, 0, pat.from.compareTo(range._impl.getDecimalFromValue()));
			assertEquals(msg, 0, pat.to  .compareTo(range._last));
			assertEquals(msg, 0, pat.step.compareTo(range._step));

			if (pat.rangemax != null) {
				assertEquals(msg, 0, pat.rangemax.compareTo(range.rangeMax()));
			} else {
				assertNull(msg, range.rangeMax());
			}
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.SimpleDecimalRange#equals(java.lang.Object)}.
	 */
	public void testEqualsObject() {
		for (int i = 0; i < rangePatterns.length; i++) {
			String msg = String.format("Test params : [%d] ", i);
			VerifyRangePattern pat = rangePatterns[i];
			SimpleDecimalRange range = new SimpleDecimalRange(pat.from, pat.to, pat.step, pat.includeLast);
			assertEquals(msg, pat.includeLast, range._includeLast);
			assertEquals(msg, 0, pat.from.compareTo(range._impl.getDecimalFromValue()));
			assertEquals(msg, 0, pat.to  .compareTo(range._last));
			assertEquals(msg, 0, pat.step.compareTo(range._step));
			
			for (int j = 0; j < rangePatterns.length; j++) {
				String amsg = String.format(".equals[%d] ", j);
				VerifyRangePattern apat = rangePatterns[j];
				SimpleDecimalRange arange = new SimpleDecimalRange(apat.from, apat.to, apat.step, apat.includeLast);

				if (i == j) {
					assertEquals(msg + amsg, range, arange);
					assertEquals(msg + amsg, arange, range);
					assertEquals(msg + amsg, range.hashCode(), arange.hashCode());
				} else {
					assertFalse(msg + amsg, range.equals(arange));
					assertFalse(msg + amsg, arange.equals(range));
				}
			}
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.SimpleDecimalRange#iterator()}.
	 */
	public void testIterator() {
		for (int i = 0; i < rangePatterns.length; i++) {
			String msg = String.format("Test params : [%d] ", i);
			VerifyRangePattern pat = rangePatterns[i];
			SimpleDecimalRange range = new SimpleDecimalRange(pat.from, pat.to, pat.step, pat.includeLast);
			assertEquals(msg, pat.includeLast, range._includeLast);
			assertEquals(msg, 0, pat.from.compareTo(range._impl.getDecimalFromValue()));
			assertEquals(msg, 0, pat.to  .compareTo(range._last));
			assertEquals(msg, 0, pat.step.compareTo(range._step));

			BigDecimal[] con = pat.iterations;
			Iterator<BigDecimal> it = range.iterator();
			if (con.length > 0) {
				for (BigDecimal v : con) {
					String vmsg = msg + " value=" + v;
					assertTrue(vmsg, it.hasNext());
					assertEquals(vmsg, 0, v.compareTo(it.next()));
				}
			}
			assertFalse(msg, it.hasNext());
			try {
				it.next();
				fail(msg + " Must be throw NoSuchElementException.");
			} catch (NoSuchElementException ex) {
				assertTrue(true);
			}
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.AbstractRange#hashCode()}.
	 */
	public void testHashCode() {
		for (int i = 0; i < rangePatterns.length; i++) {
			String msg = String.format("Test params : [%d] ", i);
			VerifyRangePattern pat = rangePatterns[i];
			SimpleDecimalRange range = new SimpleDecimalRange(pat.from, pat.to, pat.step, pat.includeLast);
			assertEquals(msg, pat.includeLast, range._includeLast);
			assertEquals(msg, 0, pat.from.compareTo(range._impl.getDecimalFromValue()));
			assertEquals(msg, 0, pat.to  .compareTo(range._last));
			assertEquals(msg, 0, pat.step.compareTo(range._step));

			assertEquals(msg, toHashCode(pat), range.hashCode());
		}
	}

	/**
	 * Test method for {@link ssac.aadl.runtime.util.range.AbstractRange#toString()}.
	 */
	public void testToString() {
		for (int i = 0; i < rangePatterns.length; i++) {
			String msg = String.format("Test params : [%d] ", i);
			VerifyRangePattern pat = rangePatterns[i];
			SimpleDecimalRange range = new SimpleDecimalRange(pat.from, pat.to, pat.step, pat.includeLast);
			assertEquals(msg, pat.includeLast, range._includeLast);
			assertEquals(msg, 0, pat.from.compareTo(range._impl.getDecimalFromValue()));
			assertEquals(msg, 0, pat.to  .compareTo(range._last));
			assertEquals(msg, 0, pat.step.compareTo(range._step));

			assertEquals(msg, toString(pat), range.toString());
		}
	}
}
