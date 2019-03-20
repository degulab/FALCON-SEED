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
 *  Copyright 2007-2010  SOARS Project.
 *  <author> Hiroshi Deguchi(SOARS Project.)
 *  <author> Yasunari Ishizuka(PieCake.inc,)
 */
/*
 * @(#)DtDataTypesTest.java	0.20	2010/02/19
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)DtDataTypesTest.java	0.10	2008/09/02
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge;

import java.math.BigDecimal;
import java.math.BigInteger;

import junit.framework.TestCase;
import dtalge.exception.IllegalValueOfDataTypeException;
import dtalge.exception.ValueFormatOfDataTypeException;
import dtalge.util.DtDataTypes;

/**
 * <code>DtDataTypes</code> のユニットテスト。
 * このテストケースでは、ファイル入出力のテストは含まない。
 * 
 * @author Y.Ishizuka(PieCake,Inc.)
 */
public class DtDataTypesTest extends TestCase
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static protected final String[] normalBooleanTypes = {
		"boolean", "BOOLEAN", "bOoLeAn"
	};
	//static protected final String[] normalIntegerTypes = {
	//	"integer", "INTEGER", "InTeGeR"
	//};
	static protected final String[] normalDecimalTypes = {
		"decimal", "DECIMAL", "dEcImAl"
	};
	static protected final String[] normalStringTypes = {
		"string", "STRING", "StRiNg"
	};
	static protected final String[] illegalTypes = {
		"boorean", "integra", "desimal", "strong",
	};
	
	static protected final Object[] values = {
		null, true, false, Byte.valueOf((byte)0x1), Short.valueOf((short)2),
		Integer.valueOf(3), Long.valueOf(4), Float.valueOf(5.5f), Double.valueOf(6.6),
		new BigInteger("7"), new BigDecimal("8.8"), "文字列",
	};
	
	static protected final String[] strvals = {
		null, "true", "false", "0x1", "2", "-3", "4", "5.5f", "-6.6", "+7", "+8.888", "文字列",
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

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	//------------------------------------------------------------
	// Test cases
	//------------------------------------------------------------

	/**
	 * {@link dtalge.DtDataTypes#fromName(java.lang.String)} のためのテスト・メソッド。
	 */
	public void testFromName() {
		//assertNotSame(DtDataTypes.BOOLEAN, DtDataTypes.INTEGER);
		assertNotSame(DtDataTypes.BOOLEAN, DtDataTypes.DECIMAL);
		assertNotSame(DtDataTypes.BOOLEAN, DtDataTypes.STRING);
		//assertNotSame(DtDataTypes.INTEGER, DtDataTypes.DECIMAL);
		//assertNotSame(DtDataTypes.INTEGER, DtDataTypes.STRING);
		assertNotSame(DtDataTypes.DECIMAL, DtDataTypes.STRING);
		//assertFalse(DtDataTypes.BOOLEAN.equals(DtDataTypes.INTEGER));
		assertFalse(DtDataTypes.BOOLEAN.equals(DtDataTypes.DECIMAL));
		assertFalse(DtDataTypes.BOOLEAN.equals(DtDataTypes.STRING));
		//assertFalse(DtDataTypes.INTEGER.equals(DtDataTypes.DECIMAL));
		//assertFalse(DtDataTypes.INTEGER.equals(DtDataTypes.STRING));
		assertFalse(DtDataTypes.DECIMAL.equals(DtDataTypes.STRING));
		
		//--- boolean
		for (String strname : normalBooleanTypes) {
			String ret = DtDataTypes.fromName(strname);
			assertEquals(DtDataTypes.BOOLEAN, ret);
			assertSame(DtDataTypes.BOOLEAN, ret);
		}
		
		//--- integer
		//for (String strname : normalIntegerTypes) {
		//	String ret = DtDataTypes.fromName(strname);
		//	assertEquals(DtDataTypes.INTEGER, ret);
		//	assertSame(DtDataTypes.INTEGER, ret);
		//}
		
		//--- decimal
		for (String strname : normalDecimalTypes) {
			String ret = DtDataTypes.fromName(strname);
			assertEquals(DtDataTypes.DECIMAL, ret);
			assertSame(DtDataTypes.DECIMAL, ret);
		}
		
		//--- string
		for (String strname : normalStringTypes) {
			String ret = DtDataTypes.fromName(strname);
			assertEquals(DtDataTypes.STRING, ret);
			assertSame(DtDataTypes.STRING, ret);
		}
		
		//--- null
		try {
			String ret = DtDataTypes.fromName(null);
			fail("null->\"" + String.valueOf(ret) + "\" must be throw NullPointerException.");
		}
		catch (NullPointerException ex) {
			System.out.println("Valid exception : " + ex.toString());
		}
		catch (Exception ex) {
			fail("Unknown exception : " + ex.toString());
		}
		
		//--- errors
		for (String strname : illegalTypes) {
			try {
				String ret = DtDataTypes.fromName(strname);
				fail("\"" + String.valueOf(strname) + "\"->\"" + String.valueOf(ret) + "\" must be throw IllegalArgumentException.");
			}
			catch (IllegalArgumentException ex) {
				System.out.println("Valid exception : " + ex.toString());
			}
			catch (Exception ex) {
				fail("Unknown exception : " + ex.toString());
			}
		}
	}

	/**
	 * {@link dtalge.DtDataTypes#classFromName(java.lang.String)} のためのテスト・メソッド。
	 */
	public void testClassFromName() {
		//assertNotSame(DtDataTypes.BooleanClass, DtDataTypes.IntegerClass);
		assertNotSame(DtDataTypes.BooleanClass, DtDataTypes.DecimalClass);
		assertNotSame(DtDataTypes.BooleanClass, DtDataTypes.StringClass);
		//assertNotSame(DtDataTypes.IntegerClass, DtDataTypes.DecimalClass);
		//assertNotSame(DtDataTypes.IntegerClass, DtDataTypes.StringClass);
		assertNotSame(DtDataTypes.DecimalClass, DtDataTypes.StringClass);
		//assertFalse(DtDataTypes.BooleanClass.equals(DtDataTypes.IntegerClass));
		assertFalse(DtDataTypes.BooleanClass.equals(DtDataTypes.DecimalClass));
		assertFalse(DtDataTypes.BooleanClass.equals(DtDataTypes.StringClass));
		//assertFalse(DtDataTypes.IntegerClass.equals(DtDataTypes.DecimalClass));
		//assertFalse(DtDataTypes.IntegerClass.equals(DtDataTypes.StringClass));
		assertFalse(DtDataTypes.DecimalClass.equals(DtDataTypes.StringClass));
		
		//--- boolean
		for (String strname : normalBooleanTypes) {
			Class<?> ret = DtDataTypes.classFromName(strname);
			assertEquals(DtDataTypes.BooleanClass, ret);
			assertSame(DtDataTypes.BooleanClass, ret);
		}
		
		//--- integer
		//for (String strname : normalIntegerTypes) {
		//	Class<?> ret = DtDataTypes.classFromName(strname);
		//	assertEquals(DtDataTypes.IntegerClass, ret);
		//	assertSame(DtDataTypes.IntegerClass, ret);
		//}
		
		//--- decimal
		for (String strname : normalDecimalTypes) {
			Class<?> ret = DtDataTypes.classFromName(strname);
			assertEquals(DtDataTypes.DecimalClass, ret);
			assertSame(DtDataTypes.DecimalClass, ret);
		}
		
		//--- string
		for (String strname : normalStringTypes) {
			Class<?> ret = DtDataTypes.classFromName(strname);
			assertEquals(DtDataTypes.StringClass, ret);
			assertSame(DtDataTypes.StringClass, ret);
		}
		
		//--- null
		try {
			Class<?> ret = DtDataTypes.classFromName(null);
			fail("null->\"" + String.valueOf(ret) + "\" must be throw NullPointerException.");
		}
		catch (NullPointerException ex) {
			System.out.println("Valid exception : " + ex.toString());
		}
		catch (Exception ex) {
			fail("Unknown exception : " + ex.toString());
		}
		
		//--- errors
		for (String strname : illegalTypes) {
			try {
				Class<?> ret = DtDataTypes.classFromName(strname);
				fail("\"" + String.valueOf(strname) + "\"->\"" + String.valueOf(ret) + "\" must be throw IllegalArgumentException.");
			}
			catch (IllegalArgumentException ex) {
				System.out.println("Valid exception : " + ex.toString());
			}
			catch (Exception ex) {
				fail("Unknown exception : " + ex.toString());
			}
		}
	}

	/**
	 * {@link dtalge.DtDataTypes#validDataType(java.lang.String, java.lang.Object)} のためのテスト・メソッド。
	 */
	public void testValidDataType() {
		//--- null
		for (Object val : values) {
			try {
				DtDataTypes.validDataType(null, val);
				fail("<null>[" + String.valueOf(val) + "] must be throw NullPointerException.");
			}
			catch (NullPointerException ex) {
				System.out.println("Valid exception : " + ex.toString());
			}
			catch (Exception ex) {
				fail("Unknown exception : " + ex.toString());
			}
		}
		
		//--- errors
		for (String strname : illegalTypes) {
			for (Object val : values) {
				try {
					DtDataTypes.validDataType(strname, val);
					fail("<" + String.valueOf(strname) + ">[" + String.valueOf(val) + "] must be throw IllegalArgumentException.");
				}
				catch (IllegalArgumentException ex) {
					System.out.println("Valid exception : " + ex.toString());
				}
				catch (Exception ex) {
					fail("Unknown exception : " + ex.toString());
				}
			}
		}
		
		//--- boolean
		for (String strname : normalBooleanTypes) {
			for (Object val : values) {
				if (val == null || val instanceof Boolean) {
					try {
						DtDataTypes.validDataType(strname, val);
					} catch (Exception ex) {
						fail("Unknown exception : " + ex.toString());
					}
				} else {
					try {
						DtDataTypes.validDataType(strname, val);
						fail("<" + String.valueOf(strname) + ">[" + String.valueOf(val) + "] must be throw IllegalValueOfDataTypeException.");
					}
					catch (IllegalValueOfDataTypeException ex) {
						System.out.println("Valid exception : " + ex.toString());
					}
					catch (Exception ex) {
						fail("Unknown exception : " + ex.toString());
					}
				}
			}
		}
		
		//--- integer
		//for (String strname : normalIntegerTypes) {
		//	for (Object val : values) {
		//		if (val == null || val instanceof BigInteger) {
		//			try {
		//				DtDataTypes.validDataType(strname, val);
		//			} catch (Exception ex) {
		//				fail("Unknown exception : " + ex.toString());
		//			}
		//		} else {
		//			try {
		//				DtDataTypes.validDataType(strname, val);
		//				fail("<" + String.valueOf(strname) + ">[" + String.valueOf(val) + "] must be throw IllegalValueOfDataTypeException.");
		//			}
		//			catch (IllegalValueOfDataTypeException ex) {
		//				System.out.println("Valid exception : " + ex.toString());
		//			}
		//			catch (Exception ex) {
		//				fail("Unknown exception : " + ex.toString());
		//			}
		//		}
		//	}
		//}
		
		//--- decimal
		for (String strname : normalDecimalTypes) {
			for (Object val : values) {
				if (val == null || val instanceof BigDecimal) {
					try {
						DtDataTypes.validDataType(strname, val);
					} catch (Exception ex) {
						fail("Unknown exception : " + ex.toString());
					}
				} else {
					try {
						DtDataTypes.validDataType(strname, val);
						fail("<" + String.valueOf(strname) + ">[" + String.valueOf(val) + "] must be throw IllegalValueOfDataTypeException.");
					}
					catch (IllegalValueOfDataTypeException ex) {
						System.out.println("Valid exception : " + ex.toString());
					}
					catch (Exception ex) {
						fail("Unknown exception : " + ex.toString());
					}
				}
			}
		}
		
		//--- string
		for (String strname : normalStringTypes) {
			for (Object val : values) {
				if (val == null || val instanceof String) {
					try {
						DtDataTypes.validDataType(strname, val);
					} catch (Exception ex) {
						fail("Unknown exception : " + ex.toString());
					}
				} else {
					try {
						DtDataTypes.validDataType(strname, val);
						fail("<" + String.valueOf(strname) + ">[" + String.valueOf(val) + "] must be throw IllegalValueOfDataTypeException.");
					}
					catch (IllegalValueOfDataTypeException ex) {
						System.out.println("Valid exception : " + ex.toString());
					}
					catch (Exception ex) {
						fail("Unknown exception : " + ex.toString());
					}
				}
			}
		}
	}

	/**
	 * {@link dtalge.DtDataTypes#valueOf(java.lang.String, java.lang.String)} のためのテスト・メソッド。
	 */
	public void testValueOf() {
		//--- null
		for (String val : strvals) {
			try {
				Object ret = DtDataTypes.valueOf(null, val);
				fail("<null>[" + String.valueOf(val) + "]=[" + String.valueOf(ret) + "] must be throw NullPointerException.");
			}
			catch (NullPointerException ex) {
				System.out.println("Valid exception : " + ex.toString());
			}
			catch (Exception ex) {
				fail("Unknown exception : " + ex.toString());
			}
		}
		
		//--- errors
		for (String strname : illegalTypes) {
			for (String val : strvals) {
				try {
					Object ret = DtDataTypes.valueOf(strname, val);
					fail("<" + String.valueOf(strname) + ">[" + String.valueOf(val) + "]=[" + String.valueOf(ret) + "] must be throw IllegalArgumentException.");
				}
				catch (IllegalArgumentException ex) {
					System.out.println("Valid exception : " + ex.toString());
				}
				catch (Exception ex) {
					fail("Unknown exception : " + ex.toString());
				}
			}
		}
		
		//--- boolean
		for (String strname : normalBooleanTypes) {
			for (String val : strvals) {
				if (val == null) {
					try {
						Object ret = DtDataTypes.valueOf(strname, val);
						assertNull(ret);
					} catch (Exception ex) {
						fail("Unknown exception : " + ex.toString());
					}
				} else {
					try {
						Object ret = DtDataTypes.valueOf(strname, val);
						assertEquals(Boolean.valueOf(val), ret);
					} catch (Exception ex) {
						fail("Unknown exception : " + ex.toString());
					}
				}
			}
		}
		
		//--- integer
		//for (String strname : normalIntegerTypes) {
		//	for (String val : strvals) {
		//		if (val == null) {
		//			try {
		//				Object ret = DtDataTypes.valueOf(strname, val);
		//				assertNull(ret);
		//			} catch (Exception ex) {
		//				fail("Unknown exception : " + ex.toString());
		//			}
		//		} else {
		//			BigInteger ans;
		//			try {
		//				ans = new BigInteger(val);
		//			} catch (Throwable ex) {
		//				ans = null;
		//			}
		//			if (ans != null) {
		//				try {
		//					Object ret = DtDataTypes.valueOf(strname, val);
		//					assertEquals(ans, ret);
		//				} catch (Exception ex) {
		//					fail("Unknown exception : " + ex.toString());
		//				}
		//			} else {
		//				try {
		//					Object ret = DtDataTypes.valueOf(strname, val);
		//					fail("<" + String.valueOf(strname) + ">[" + String.valueOf(val) + "]=[" + String.valueOf(ret) + "] must be throw ValueFormatOfDataTypeException.");
		//				}
		//				catch (ValueFormatOfDataTypeException ex) {
		//					System.out.println("Valid exception : " + ex.toString());
		//				}
		//				catch (Exception ex) {
		//					fail("Unknown exception : " + ex.toString());
		//				}
		//			}
		//		}
		//	}
		//}
		
		//--- decimal
		for (String strname : normalDecimalTypes) {
			for (String val : strvals) {
				if (val == null) {
					try {
						Object ret = DtDataTypes.valueOf(strname, val);
						assertNull(ret);
					} catch (Exception ex) {
						fail("Unknown exception : " + ex.toString());
					}
				} else {
					BigDecimal ans;
					try {
						ans = new BigDecimal(val);
					} catch (Throwable ex) {
						ans = null;
					}
					if (ans != null) {
						try {
							Object ret = DtDataTypes.valueOf(strname, val);
							assertEquals(ans, ret);
						} catch (Exception ex) {
							fail("Unknown exception : " + ex.toString());
						}
					} else {
						try {
							Object ret = DtDataTypes.valueOf(strname, val);
							fail("<" + String.valueOf(strname) + ">[" + String.valueOf(val) + "]=[" + String.valueOf(ret) + "] must be throw ValueFormatOfDataTypeException.");
						}
						catch (ValueFormatOfDataTypeException ex) {
							System.out.println("Valid exception : " + ex.toString());
						}
						catch (Exception ex) {
							fail("Unknown exception : " + ex.toString());
						}
					}
				}
			}
		}
		
		//--- string
		for (String strname : normalStringTypes) {
			for (String val : strvals) {
				try {
					Object ret = DtDataTypes.valueOf(strname, val);
					assertEquals(val, ret);
				} catch (Exception ex) {
					fail("Unknown exception : " + ex.toString());
				}
			}
		}
	}
}
