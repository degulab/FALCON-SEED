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
 *  Copyright 2007-2009  SOARS Project.
 *  <author> Hiroshi Deguchi(SOARS Project.)
 *  <author> Li Hou(SOARS Project.)
 *  <author> Yasunari Ishizuka(PieCake.inc,)
 */
package exalge2;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

public class TransMatrixTest extends TestCase
{
	static private final String from1 = "りんご-*-*-*-*";
	static private final String from2 = "みかん-*-*-*-*";
	static private final String from3 = "ばなな-*-*-*-*";
	static private final String from4 = "メロン-*-*-*-*";
	static private final String from5 = "マンゴー-*-*-*-*";
	static private final String from6 = "まぐろ-*-*-*-*";
	static private final String from7 = "かつお-*-*-*-*";
	
	static private final String pattern11 = "name11-*-*-*-*";
	static private final String pattern12 = "name12-*-*-*-*";
	static private final String pattern13 = "name13-*-*-*-*";
	static private final String pattern14 = "name14-*-*-*-*";
	
	static private final String pattern21 = "name21-*-unit-*-*";
	static private final String pattern22 = "name22-*-unit-*-*";
	static private final String pattern23 = "name23-*-unit-*-*";
	static private final String pattern24 = "name24-*-unit-*-*";
	
	static private final String pattern31 = "name31-*-unit-time-*";
	static private final String pattern32 = "name32-*-unit-time-*";
	static private final String pattern33 = "name33-*-unit-time-*";
	static private final String pattern34 = "name34-*-unit-time-*";
	
	static private final String pattern41 = "name41-*-unit-time-subject";
	static private final String pattern42 = "name42-*-unit-time-subject";
	static private final String pattern43 = "name43-*-unit-time-subject";
	static private final String pattern44 = "name44-*-unit-time-subject";
	
	static private final String pattern51 = "name51-HAT-unit-time-subject";
	static private final String pattern52 = "name52-HAT-unit-time-subject";
	static private final String pattern53 = "name53-HAT-unit-time-subject";
	static private final String pattern54 = "name54-HAT-unit-time-subject";
	
	static private final ExBasePattern[] fromPatterns = new ExBasePattern[]{
		new ExBasePattern(from1),
		new ExBasePattern(from2),
		new ExBasePattern(from3),
		new ExBasePattern(from4),
		new ExBasePattern(from5),
		new ExBasePattern(from6),
		new ExBasePattern(from7),
	};
	
	static private final Object[] ratiosData1 = new Object[]{
		pattern11, 10,
		pattern12, 20,
		pattern13, 30,
		pattern14, 40,
	};
	static private final Object[] ratiosData2 = new Object[]{
		pattern21, 10,
		pattern22, 20,
		pattern23, 30,
		pattern24, 40,
	};
	static private final Object[] ratiosData3 = new Object[]{
		pattern31, 10,
		pattern32, 20,
		pattern33, 30,
		pattern34, 40,
	};
	static private final Object[] ratiosData4 = new Object[]{
		pattern41, 10,
		pattern42, 20,
		pattern43, 30,
		pattern44, 40,
	};
	static private final Object[] ratiosData5 = new Object[]{
		pattern51, 10,
		pattern52, 20,
		pattern53, 30,
		pattern54, 40,
	};
	
	static TransDivideRatios makeDivideRatiosData(Object...objects) {
		if (objects==null)
			throw new NullPointerException();
		if ((objects.length % 2)!=0)
			throw new IllegalArgumentException("objects(" + objects.length + ") is not pairs.");

		TransDivideRatios ratios = new TransDivideRatios();
		for (int i = 0; i < objects.length; i+=2) {
			Object objPattern = objects[i];
			Object objValue   = objects[i+1];
			ExBasePattern pattern;
			BigDecimal    value;
			
			if (objPattern instanceof String) {
				pattern = new ExBasePattern((String)objPattern);
			} else {
				pattern = (ExBasePattern)objPattern;
			}
			
			if (objValue instanceof Number) {
				value = new BigDecimal(((Number)objValue).toString());
			} else if (objValue instanceof String) {
				value = new BigDecimal((String)objValue);
			} else {
				value = (BigDecimal)objValue;
			}
			
			ratios.put(pattern, value);
		}
		
		return ratios;
	}
	
	static TransDivideRatios[] makeDivideRatiosArray(boolean updateTotalRatio) {
		TransDivideRatios[] ratios = new TransDivideRatios[]{
				makeDivideRatiosData(ratiosData1),
				makeDivideRatiosData(ratiosData2),
				makeDivideRatiosData(ratiosData3),
				makeDivideRatiosData(ratiosData4),
				makeDivideRatiosData(ratiosData5),
		};
		if (updateTotalRatio) {
			for (TransDivideRatios r : ratios) {
				r.updateTotalRatio();
			}
		}
		return ratios;
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	/**
	 * {@link exalge2.TransMatrix#TransMatrix()} のためのテスト・メソッド。
	 */
	public void testTransMatrix() {
		TransMatrix matrix = new TransMatrix();
		assertTrue(matrix.getTransMap().isEmpty());
		assertFalse(matrix.isTotalRatioUsed());
	}

	/**
	 * {@link exalge2.TransMatrix#TransMatrix(boolean)} のためのテスト・メソッド。
	 */
	public void testTransMatrixBoolean() {
		TransMatrix matrix1 = new TransMatrix(true);
		assertTrue(matrix1.getTransMap().isEmpty());
		assertTrue(matrix1.isTotalRatioUsed());
		
		TransMatrix matrix2 = new TransMatrix(false);
		assertTrue(matrix2.getTransMap().isEmpty());
		assertFalse(matrix2.isTotalRatioUsed());
	}

	/**
	 * {@link exalge2.TransMatrix#isEmpty()} のためのテスト・メソッド。
	 */
	public void testIsEmpty() {
		TransMatrix matrix = new TransMatrix();
		assertTrue(matrix.getTransMap().isEmpty());
		assertTrue(matrix.isEmpty());
		
		matrix.put(new ExBasePattern(from1), makeDivideRatiosData(ratiosData1));
		assertFalse(matrix.getTransMap().isEmpty());
		assertFalse(matrix.isEmpty());
		
		matrix.clear();
		assertTrue(matrix.getTransMap().isEmpty());
		assertTrue(matrix.isEmpty());
	}

	/**
	 * {@link exalge2.TransMatrix#size()} のためのテスト・メソッド。
	 */
	public void testSize() {
		TransDivideRatios[] divideRatios = makeDivideRatiosArray(false);
		
		TransMatrix matrix = new TransMatrix();
		assertEquals(0, matrix.getTransMap().size());
		assertEquals(matrix.getTransMap().size(), matrix.size());
		
		for (int i = 0; i < divideRatios.length; i++) {
			matrix.put(fromPatterns[i], divideRatios[i]);
			assertEquals(i+1, matrix.getTransMap().size());
			assertEquals(matrix.getTransMap().size(), matrix.size());
		}
		
		matrix.clear();
		assertEquals(0, matrix.getTransMap().size());
		assertEquals(matrix.getTransMap().size(), matrix.size());
	}

	/**
	 * {@link exalge2.TransMatrix#clear()} のためのテスト・メソッド。
	 */
	public void testClear() {
		TransDivideRatios[] divideRatios = makeDivideRatiosArray(false);
		
		TransMatrix matrix = new TransMatrix();
		assertEquals(0, matrix.getTransMap().size());
		assertEquals(matrix.getTransMap().size(), matrix.size());
		
		for (int i = 0; i < divideRatios.length; i++) {
			matrix.put(fromPatterns[i], divideRatios[i]);
			assertEquals(i+1, matrix.getTransMap().size());
			assertEquals(matrix.getTransMap().size(), matrix.size());
		}
		
		matrix.clear();
		assertEquals(0, matrix.getTransMap().size());
		assertEquals(matrix.getTransMap().size(), matrix.size());
	}

	/**
	 * {@link exalge2.TransMatrix#isTotalRatioUsed()} のためのテスト・メソッド。
	 */
	public void testIsTotalRatioUsed() {
		TransMatrix matrix1 = new TransMatrix(true);
		assertTrue(matrix1.isTotalRatioUsed());
		
		TransMatrix matrix2 = new TransMatrix(false);
		assertFalse(matrix2.isTotalRatioUsed());
	}

	/**
	 * {@link exalge2.TransMatrix#setUseTotalRatio(boolean)} のためのテスト・メソッド。
	 */
	public void testSetUseTotalRatio() {
		TransMatrix matrix1 = new TransMatrix(true);
		assertTrue(matrix1.isTotalRatioUsed());
		matrix1.setUseTotalRatio(false);
		assertFalse(matrix1.isTotalRatioUsed());
		
		TransMatrix matrix2 = new TransMatrix(false);
		assertFalse(matrix2.isTotalRatioUsed());
		matrix2.setUseTotalRatio(true);
		assertTrue(matrix2.isTotalRatioUsed());

		TransDivideRatios[] divideRatios = makeDivideRatiosArray(false);
		TransMatrix matrix3 = new TransMatrix(false);
		assertFalse(matrix3.isTotalRatioUsed());
		for (int i = 0; i < divideRatios.length; i++) {
			matrix3.put(fromPatterns[i], divideRatios[i]);
			assertEquals(BigDecimal.ZERO, divideRatios[i].getTotalRatio());
		}
		matrix3.setUseTotalRatio(true);
		for (TransDivideRatios tdr : matrix3.getTransMap().values()) {
			assertEquals(BigDecimal.ZERO, tdr.getTotalRatio());
		}
	}
	
	/**
	 * {@link exalge2.TransMatrix#updateTotalRatios()} のためのテスト・メソッド。
	 */
	public void testUpdateTotalRatios() {
		TransDivideRatios[] divideRatios = makeDivideRatiosArray(false);
		
		TransMatrix matrix = new TransMatrix();
		matrix.updateTotalRatios();
		
		for (int i = 0; i < divideRatios.length; i++) {
			matrix.put(fromPatterns[i], divideRatios[i]);
			assertEquals(BigDecimal.ZERO, divideRatios[i].getTotalRatio());
		}
		matrix.updateTotalRatios();
		for (TransDivideRatios tdr : matrix.getTransMap().values()) {
			assertTrue(BigDecimal.ZERO.compareTo(tdr.getTotalRatio())!=0);
		}
	}

	/**
	 * {@link exalge2.TransMatrix#containsTransFrom(exalge2.ExBasePattern)} のためのテスト・メソッド。
	 */
	public void testContainsTransFrom() {
		TransMatrix matrix = new TransMatrix();
		assertTrue(matrix.isEmpty());
		for (ExBasePattern pat : fromPatterns) {
			assertFalse(matrix.containsTransFrom(pat));
		}
		
		TransDivideRatios[] divideRatios = makeDivideRatiosArray(false);
		for (int i = 0; i < divideRatios.length; i++) {
			matrix.put(fromPatterns[i], divideRatios[i]);
		}
		assertFalse(matrix.isEmpty());
		
		for (int i = 0; i < fromPatterns.length; i++) {
			if (i < divideRatios.length) {
				assertTrue(matrix.containsTransFrom(fromPatterns[i]));
			} else {
				assertFalse(matrix.containsTransFrom(fromPatterns[i]));
			}
		}
	}

	/**
	 * {@link exalge2.TransMatrix#containsTransTo(exalge2.ExBasePattern)} のためのテスト・メソッド。
	 */
	public void testContainsTransTo() {
		TransDivideRatios[] resultRatios = makeDivideRatiosArray(false);
		
		TransMatrix matrix = new TransMatrix();
		assertTrue(matrix.isEmpty());
		for (TransDivideRatios tdr : resultRatios) {
			for (ExBasePattern pat : tdr.patterns()) {
				assertFalse(matrix.containsTransTo(pat));
			}
		}
		
		TransDivideRatios[] divideRatios = makeDivideRatiosArray(false);
		int maxLen = divideRatios.length / 2;
		for (int i = 0; i < maxLen; i++) {
			matrix.put(fromPatterns[i], divideRatios[i]);
		}
		assertFalse(matrix.isEmpty());
		
		for (int i = 0; i < resultRatios.length; i++) {
			for (ExBasePattern pat : resultRatios[i].patterns()) {
				if (i < maxLen) {
					assertTrue(matrix.containsTransTo(pat));
				} else {
					assertFalse(matrix.containsTransTo(pat));
				}
			}
		}
	}

	/**
	 * {@link exalge2.TransMatrix#transFromPatterns()} のためのテスト・メソッド。
	 */
	public void testTransFromPatterns() {
		TransDivideRatios[] resultRatios = makeDivideRatiosArray(false);
		Set<ExBasePattern> resultSet = new HashSet<ExBasePattern>();
		for (int i = 0; i < resultRatios.length; i++) {
			resultSet.add(fromPatterns[i]);
		}
		
		TransMatrix matrix = new TransMatrix();
		assertTrue(matrix.isEmpty());
		assertFalse(matrix.getTransMap().keySet().equals(resultSet));
		assertFalse(matrix.transFromPatterns().equals(resultSet));
		
		TransDivideRatios[] divideRatios = makeDivideRatiosArray(false);
		int maxLen = resultRatios.length;
		for (int i = 0; i < maxLen; i++) {
			matrix.put(fromPatterns[i], divideRatios[i]);
		}
		assertFalse(matrix.isEmpty());
		assertTrue(matrix.getTransMap().keySet().equals(resultSet));
		assertTrue(matrix.transFromPatterns().equals(resultSet));
	}

	/**
	 * {@link exalge2.TransMatrix#transToRatios()} のためのテスト・メソッド。
	 */
	public void testTransToRatios() {
		TransDivideRatios[] resultRatios = makeDivideRatiosArray(false);
		List<TransDivideRatios> resultSet = new ArrayList<TransDivideRatios>(Arrays.asList(resultRatios));
		
		TransMatrix matrix = new TransMatrix();
		assertTrue(matrix.isEmpty());
		assertFalse(matrix.getTransMap().values().equals(resultSet));
		assertFalse(matrix.transToRatios().equals(resultSet));
		
		TransDivideRatios[] divideRatios = makeDivideRatiosArray(false);
		int maxLen = resultRatios.length;
		for (int i = 0; i < maxLen; i++) {
			matrix.put(fromPatterns[i], divideRatios[i]);
		}
		assertFalse(matrix.isEmpty());
		assertEquals(resultSet.size(), matrix.size());
		assertTrue(matrix.getTransMap().values().containsAll(resultSet));
		assertTrue(matrix.transToRatios().containsAll(resultSet));
	}

	/**
	 * {@link exalge2.TransMatrix#getTransTo(exalge2.ExBasePattern)} のためのテスト・メソッド。
	 */
	public void testGetTransTo() {
		TransDivideRatios[] resultRatios = makeDivideRatiosArray(false);
		
		TransMatrix matrix = new TransMatrix();
		assertTrue(matrix.isEmpty());
		for (int i = 0; i < fromPatterns.length; i++) {
			TransDivideRatios tdr1 = matrix.getTransMap().get(fromPatterns[i]);
			TransDivideRatios tdr2 = matrix.getTransTo(fromPatterns[i]);
			assertTrue(tdr1 == null);
			assertTrue(tdr2 == null);
		}
		assertTrue(matrix.getTransMap().get(null) == null);
		assertTrue(matrix.getTransTo(null) == null);
		
		TransDivideRatios[] divideRatios = makeDivideRatiosArray(false);
		int maxLen = resultRatios.length;
		for (int i = 0; i < maxLen; i++) {
			matrix.put(fromPatterns[i], divideRatios[i]);
		}
		assertFalse(matrix.isEmpty());
		assertTrue(matrix.getTransMap().get(null) == null);
		assertTrue(matrix.getTransTo(null) == null);
		
		for (int i = 0; i < fromPatterns.length; i++) {
			TransDivideRatios tdr1 = matrix.getTransMap().get(fromPatterns[i]);
			TransDivideRatios tdr2 = matrix.getTransTo(fromPatterns[i]);
			if (i < maxLen) {
				assertEquals(tdr1, resultRatios[i]);
				assertEquals(tdr2, resultRatios[i]);
				assertSame(tdr1, tdr2);
			} else {
				assertTrue(tdr1 == null);
				assertTrue(tdr2 == null);
			}
		}
	}

	/**
	 * {@link exalge2.TransMatrix#put(exalge2.ExBasePattern, exalge2.TransDivideRatios)} のためのテスト・メソッド。
	 */
	public void testPut() {
		TransDivideRatios[] resultRatios = makeDivideRatiosArray(false);
		
		TransMatrix matrix = new TransMatrix();
		assertTrue(matrix.isEmpty());
		TransDivideRatios[] divideRatios = makeDivideRatiosArray(false);
		int maxLen = resultRatios.length;
		for (int i = 0; i < maxLen; i++) {
			matrix.put(fromPatterns[i], divideRatios[i]);
		}
		assertFalse(matrix.isEmpty());
		assertEquals(maxLen, matrix.size());
		int size = matrix.size();
		
		// null
		boolean coughtException;
		try {
			matrix.put(null, divideRatios[0]);
			coughtException = false;
		} catch(NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			matrix.put(fromPatterns[0], null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			matrix.put(null, null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		
		// overwrite
		for (int i = 0; i < maxLen; i++) {
			matrix.put(fromPatterns[i], divideRatios[i]);
		}
		assertFalse(matrix.isEmpty());
		assertEquals(size, matrix.size());
		assertEquals(maxLen, matrix.size());
		
		// check data
		for (int i = 0; i < fromPatterns.length; i++) {
			TransDivideRatios tdr1 = matrix.getTransTo(fromPatterns[i]);
			if (i < maxLen) {
				assertEquals(tdr1, resultRatios[i]);
				assertTrue(matrix.containsTransFrom(fromPatterns[i]));
			} else {
				assertTrue(tdr1 == null);
				assertFalse(matrix.containsTransFrom(fromPatterns[i]));
			}
		}
	}

	/**
	 * {@link exalge2.TransMatrix#remove(exalge2.ExBasePattern)} のためのテスト・メソッド。
	 */
	public void testRemove() {
		TransDivideRatios[] resultRatios = makeDivideRatiosArray(false);
		
		TransMatrix matrix = new TransMatrix();
		assertTrue(matrix.isEmpty());
		
		// remove from empty
		matrix.remove(null);
		assertTrue(matrix.isEmpty());
		for (int i = 0; i < fromPatterns.length; i++) {
			matrix.remove(fromPatterns[i]);
			assertTrue(matrix.isEmpty());
		}
		
		TransDivideRatios[] divideRatios = makeDivideRatiosArray(false);
		int maxLen = resultRatios.length;
		for (int i = 0; i < maxLen; i++) {
			matrix.put(fromPatterns[i], divideRatios[i]);
		}
		assertFalse(matrix.isEmpty());
		assertEquals(maxLen, matrix.size());
		
		// remove
		matrix.remove(null);
		assertFalse(matrix.isEmpty());
		assertEquals(maxLen, matrix.size());
		for (int i = fromPatterns.length - 1; i >= 0; i--) {
			matrix.remove(fromPatterns[i]);
		}
		assertTrue(matrix.isEmpty());
	}

	/**
	 * {@link exalge2.TransMatrix#machesTransFrom(exalge2.ExBase)} のためのテスト・メソッド。
	 */
	public void testMachesTransFrom() {
		ExBase base1 = new ExBase(ExalgeTest.hatAppleYen);
		ExBase base2 = new ExBase(ExalgeTest.nohatOrangeNum);
		ExBase base3 = new ExBase(ExalgeTest.hatMangoNum);
		ExBase base4 = new ExBase(ExalgeTest.nohatCashYen);
		
		TransMatrix matrix = new TransMatrix();
		assertTrue(matrix.isEmpty());
		assertTrue(matrix.machesTransFrom(null) == null);
		assertTrue(matrix.machesTransFrom(base1) == null);
		assertTrue(matrix.machesTransFrom(base2) == null);
		assertTrue(matrix.machesTransFrom(base3) == null);
		assertTrue(matrix.machesTransFrom(base4) == null);
		
		TransDivideRatios[] divideRatios = makeDivideRatiosArray(false);
		for (int i = 0; i < divideRatios.length; i++) {
			matrix.put(fromPatterns[i], divideRatios[i]);
		}
		assertFalse(matrix.isEmpty());
		assertTrue(matrix.machesTransFrom(null) == null);
		assertTrue(matrix.machesTransFrom(base1).equals(fromPatterns[0]));
		assertTrue(matrix.machesTransFrom(base2).equals(fromPatterns[1]));
		assertTrue(matrix.machesTransFrom(base3).equals(fromPatterns[4]));
		assertTrue(matrix.machesTransFrom(base4) == null);
	}

	/**
	 * {@link exalge2.TransMatrix#transform(exalge2.ExBase)} のためのテスト・メソッド。
	 */
	public void testTransformExBase() {
		ExBase base1 = new ExBase(ExalgeTest.hatAppleYen);
		ExBase base2 = new ExBase(ExalgeTest.nohatOrangeNum);
		ExBase base3 = new ExBase(ExalgeTest.hatMangoNum);
		ExBase base4 = new ExBase(ExalgeTest.nohatCashYen);
		
		TransMatrix matrix1 = new TransMatrix(false);
		TransMatrix matrix2 = new TransMatrix(true);
		assertTrue(matrix1.isEmpty());
		assertTrue(matrix2.isEmpty());
		boolean coughtException;
		try {
			matrix1.transform((ExBase)null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			matrix2.transform((ExBase)null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		
		ExBaseSet ansSet01 = new ExBaseSet(Arrays.asList(base1));
		ExBaseSet ansSet02 = new ExBaseSet(Arrays.asList(base2));
		ExBaseSet ansSet03 = new ExBaseSet(Arrays.asList(base3));
		ExBaseSet ansSet04 = new ExBaseSet(Arrays.asList(base4));
		ExBaseSet ansSet11 = new ExBaseSet(Arrays.asList(
				new ExBase("name11-HAT-円-#-#"),
				new ExBase("name12-HAT-円-#-#"),
				new ExBase("name13-HAT-円-#-#"),
				new ExBase("name14-HAT-円-#-#")
		));
		ExBaseSet ansSet12 = new ExBaseSet(Arrays.asList(
				new ExBase("name21-NO_HAT-unit-#-#"),
				new ExBase("name22-NO_HAT-unit-#-#"),
				new ExBase("name23-NO_HAT-unit-#-#"),
				new ExBase("name24-NO_HAT-unit-#-#")
		));
		
		assertEquals(matrix1.transform(base1), ansSet01);
		assertEquals(matrix1.transform(base2), ansSet02);
		assertEquals(matrix1.transform(base3), ansSet03);
		assertEquals(matrix1.transform(base4), ansSet04);
		assertEquals(matrix2.transform(base1), ansSet01);
		assertEquals(matrix2.transform(base2), ansSet02);
		assertEquals(matrix2.transform(base3), ansSet03);
		assertEquals(matrix2.transform(base4), ansSet04);
		
		matrix1.put(fromPatterns[0], makeDivideRatiosData(ratiosData1));
		matrix1.put(fromPatterns[1], makeDivideRatiosData(ratiosData2));
		matrix1.put(fromPatterns[2], makeDivideRatiosData(ratiosData3));
		matrix1.put(fromPatterns[3], makeDivideRatiosData(ratiosData4));
		matrix1.put(fromPatterns[4], makeDivideRatiosData());
		matrix2.put(fromPatterns[0], makeDivideRatiosData(ratiosData1));
		matrix2.put(fromPatterns[1], makeDivideRatiosData(ratiosData2));
		matrix2.put(fromPatterns[2], makeDivideRatiosData(ratiosData3));
		matrix2.put(fromPatterns[3], makeDivideRatiosData(ratiosData4));
		matrix2.put(fromPatterns[4], makeDivideRatiosData());
		
		assertEquals(matrix1.transform(base1), ansSet11);
		assertEquals(matrix1.transform(base2), ansSet12);
		try {
			matrix1.transform(base3);
			coughtException = false;
		} catch (IllegalStateException ex) {
			System.out.println("TransMatrixTest:testTransformExBase:IllegalStateException:" + ex.getLocalizedMessage());
			coughtException = true;
		}
		assertTrue(coughtException);
		assertEquals(matrix1.transform(base4), ansSet04);
		assertEquals(matrix2.transform(base1), ansSet11);
		assertEquals(matrix2.transform(base2), ansSet12);
		try {
			matrix2.transform(base3);
			coughtException = false;
		} catch (IllegalStateException ex) {
			System.out.println("TransMatrixTest:testTransformExBase:IllegalStateException:" + ex.getLocalizedMessage());
			coughtException = true;
		}
		assertTrue(coughtException);
		assertEquals(matrix2.transform(base4), ansSet04);
	}

	/**
	 * {@link exalge2.TransMatrix#transform(exalge2.ExBaseSet)} のためのテスト・メソッド。
	 */
	public void testTransformExBaseSet() {
		ExBase base1 = new ExBase(ExalgeTest.hatAppleYen);
		ExBase base2 = new ExBase(ExalgeTest.nohatOrangeNum);
		ExBase base3 = new ExBase(ExalgeTest.hatMangoNum);
		ExBase base4 = new ExBase(ExalgeTest.nohatCashYen);
		
		TransMatrix matrix1 = new TransMatrix(false);
		TransMatrix matrix2 = new TransMatrix(true);
		assertTrue(matrix1.isEmpty());
		assertTrue(matrix2.isEmpty());
		boolean coughtException;
		try {
			matrix1.transform((ExBaseSet)null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			matrix2.transform((ExBaseSet)null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		
		ExBaseSet set1 = new ExBaseSet(Arrays.asList(base1, base2, base3, base4));
		ExBaseSet set2 = new ExBaseSet(Arrays.asList(base1, base2, base4));
		ExBaseSet ansSet = new ExBaseSet(Arrays.asList(
				new ExBase("name11-HAT-円-#-#"),
				new ExBase("name12-HAT-円-#-#"),
				new ExBase("name13-HAT-円-#-#"),
				new ExBase("name14-HAT-円-#-#"),
				new ExBase("name21-NO_HAT-unit-#-#"),
				new ExBase("name22-NO_HAT-unit-#-#"),
				new ExBase("name23-NO_HAT-unit-#-#"),
				new ExBase("name24-NO_HAT-unit-#-#"),
				base4
		));
		assertTrue(matrix1.transform(new ExBaseSet()).isEmpty());
		assertTrue(matrix2.transform(new ExBaseSet()).isEmpty());
		assertEquals(matrix1.transform(set1), set1);
		assertEquals(matrix2.transform(set2), set2);
		
		matrix1.put(fromPatterns[0], makeDivideRatiosData(ratiosData1));
		matrix1.put(fromPatterns[1], makeDivideRatiosData(ratiosData2));
		matrix1.put(fromPatterns[2], makeDivideRatiosData(ratiosData3));
		matrix1.put(fromPatterns[3], makeDivideRatiosData(ratiosData4));
		matrix1.put(fromPatterns[4], makeDivideRatiosData());
		matrix2.put(fromPatterns[0], makeDivideRatiosData(ratiosData1));
		matrix2.put(fromPatterns[1], makeDivideRatiosData(ratiosData2));
		matrix2.put(fromPatterns[2], makeDivideRatiosData(ratiosData3));
		matrix2.put(fromPatterns[3], makeDivideRatiosData(ratiosData4));
		matrix2.put(fromPatterns[4], makeDivideRatiosData());
		
		assertTrue(matrix1.transform(new ExBaseSet()).isEmpty());
		assertTrue(matrix2.transform(new ExBaseSet()).isEmpty());
		try {
			matrix1.transform(set1);
			coughtException = false;
		} catch (IllegalStateException ex) {
			System.out.println("TransMatrixTest:testTransformExBaseSet:IllegalStateException:" + ex.getLocalizedMessage());
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			matrix2.transform(set1);
			coughtException = false;
		} catch (IllegalStateException ex) {
			System.out.println("TransMatrixTest:testTransformExBaseSet:IllegalStateException:" + ex.getLocalizedMessage());
			coughtException = true;
		}
		assertTrue(coughtException);
		assertEquals(matrix1.transform(set2), ansSet);
		assertEquals(matrix2.transform(set2), ansSet);
	}

	/**
	 * {@link exalge2.TransMatrix#transfer(exalge2.ExBase, java.math.BigDecimal)} のためのテスト・メソッド。
	 */
	public void testTransfer() {
		ExBase base1 = new ExBase(ExalgeTest.hatAppleYen);
		ExBase base2 = new ExBase(ExalgeTest.nohatOrangeNum);
		ExBase base3 = new ExBase(ExalgeTest.hatMangoNum);
		ExBase base4 = new ExBase(ExalgeTest.nohatCashYen);
		
		TransMatrix matrix = new TransMatrix(false);
		assertTrue(matrix.isEmpty());
		boolean coughtException;
		try {
			matrix.transfer(base1, null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			matrix.transfer(null, new BigDecimal(100));
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			matrix.transfer(null, null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		assertTrue(matrix.transfer(base1, new BigDecimal(120)) == null);
		assertTrue(matrix.transfer(base2, new BigDecimal(120)) == null);
		assertTrue(matrix.transfer(base3, new BigDecimal(120)) == null);
		assertTrue(matrix.transfer(base4, new BigDecimal(120)) == null);
		
		matrix.put(fromPatterns[0], makeDivideRatiosData(ratiosData1));
		matrix.put(fromPatterns[1], makeDivideRatiosData(ratiosData2));
		matrix.put(fromPatterns[2], makeDivideRatiosData(ratiosData3));
		matrix.put(fromPatterns[3], makeDivideRatiosData(ratiosData4));
		matrix.put(fromPatterns[4], makeDivideRatiosData());
		
		try {
			matrix.transfer(base1, null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			matrix.transfer(null, new BigDecimal(100));
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			matrix.transfer(null, null);
			coughtException = false;
		} catch (NullPointerException ex) {
			coughtException = true;
		}
		assertTrue(coughtException);

		Exalge ansAlge11 = ExalgeTest.makeAlge(
				new ExBase("name11-HAT-円-#-#"), new BigDecimal(1200),
				new ExBase("name12-HAT-円-#-#"), new BigDecimal(2400),
				new ExBase("name13-HAT-円-#-#"), new BigDecimal(3600),
				new ExBase("name14-HAT-円-#-#"), new BigDecimal(4800)
		);
		Exalge ansAlge12 = ExalgeTest.makeAlge(
				new ExBase("name21-NO_HAT-unit-#-#"), new BigDecimal(1200),
				new ExBase("name22-NO_HAT-unit-#-#"), new BigDecimal(2400),
				new ExBase("name23-NO_HAT-unit-#-#"), new BigDecimal(3600),
				new ExBase("name24-NO_HAT-unit-#-#"), new BigDecimal(4800)
		);
		assertEquals(matrix.transfer(base1, new BigDecimal(120)), ansAlge11);
		assertEquals(matrix.transfer(base2, new BigDecimal(120)), ansAlge12);
		try {
			matrix.transfer(base3, new BigDecimal(120));
			coughtException = false;
		} catch (IllegalStateException ex) {
			System.out.println("TransMatrixTest:testTransfer:IllegalStateException:" + ex.getLocalizedMessage());
			coughtException = true;
		}
		assertTrue(coughtException);
		assertTrue(matrix.transfer(base4, new BigDecimal(120)) == null);
		
		matrix.setUseTotalRatio(true);
		try {
			matrix.transfer(base2, new BigDecimal(120));
			coughtException = false;
		} catch (ArithmeticException ex) {
			System.out.println("TransMatrixTest:testTransfer:ArithmeticException:" + ex.getLocalizedMessage());
			coughtException = true;
		}
		assertTrue(coughtException);
		try {
			matrix.transfer(base3, new BigDecimal(120));
			coughtException = false;
		} catch (IllegalStateException ex) {
			System.out.println("TransMatrixTest:testTransfer:IllegalStateException:" + ex.getLocalizedMessage());
			coughtException = true;
		}
		assertTrue(coughtException);
		assertTrue(matrix.transfer(base4, new BigDecimal(120)) == null);
		
		Exalge ansAlge21 = ExalgeTest.makeAlge(
				new ExBase("name11-HAT-円-#-#"), new BigDecimal(12),
				new ExBase("name12-HAT-円-#-#"), new BigDecimal(24),
				new ExBase("name13-HAT-円-#-#"), new BigDecimal(36),
				new ExBase("name14-HAT-円-#-#"), new BigDecimal(48)
		);
		Exalge ansAlge22 = ExalgeTest.makeAlge(
				new ExBase("name21-NO_HAT-unit-#-#"), new BigDecimal(12),
				new ExBase("name22-NO_HAT-unit-#-#"), new BigDecimal(24),
				new ExBase("name23-NO_HAT-unit-#-#"), new BigDecimal(36),
				new ExBase("name24-NO_HAT-unit-#-#"), new BigDecimal(48)
		);
		matrix.updateTotalRatios();
		assertEquals(matrix.transfer(base1, new BigDecimal(120)), ansAlge21);
		assertEquals(matrix.transfer(base2, new BigDecimal(120)), ansAlge22);
		try {
			matrix.transfer(base3, new BigDecimal(120));
			coughtException = false;
		} catch (IllegalStateException ex) {
			System.out.println("TransMatrixTest:testTransfer:IllegalStateException:" + ex.getLocalizedMessage());
			coughtException = true;
		}
		assertTrue(coughtException);
		assertTrue(matrix.transfer(base4, new BigDecimal(120)) == null);
	}

	/**
	 * {@link exalge2.TransMatrix#clone()} のためのテスト・メソッド。
	 */
	public void testClone() {
		TransDivideRatios[] divideRatios = makeDivideRatiosArray(false);
		
		TransMatrix m1 = new TransMatrix(false);
		assertTrue(m1.isEmpty());
		assertFalse(m1.isTotalRatioUsed());
		TransMatrix m2 = (TransMatrix)m1.clone();
		assertTrue(m2.isEmpty());
		assertFalse(m2.isTotalRatioUsed());
		m1.put(fromPatterns[0], divideRatios[0]);
		m1.setUseTotalRatio(true);
		assertFalse(m1.isEmpty());
		assertTrue(m2.isEmpty());
		assertTrue(m1.isTotalRatioUsed());
		assertFalse(m2.isTotalRatioUsed());
		assertTrue(m1.containsTransFrom(fromPatterns[0]));
		assertFalse(m2.containsTransFrom(fromPatterns[0]));
		
		m1 = new TransMatrix(true);
		m1.put(fromPatterns[0], divideRatios[0]);
		m1.put(fromPatterns[1], divideRatios[1]);
		assertFalse(m1.isEmpty());
		assertTrue(m1.isTotalRatioUsed());
		assertEquals(2, m1.size());
		m2 = (TransMatrix)m1.clone();
		assertFalse(m2.isEmpty());
		assertTrue(m2.isTotalRatioUsed());
		assertEquals(2, m2.size());
		m1.put(fromPatterns[2], divideRatios[2]);
		m1.setUseTotalRatio(false);
		assertFalse(m1.isEmpty());
		assertFalse(m2.isEmpty());
		assertFalse(m1.isTotalRatioUsed());
		assertTrue(m2.isTotalRatioUsed());
		assertEquals(3, m1.size());
		assertEquals(2, m2.size());
		assertTrue(m1.containsTransFrom(fromPatterns[0]));
		assertTrue(m1.containsTransFrom(fromPatterns[1]));
		assertTrue(m1.containsTransFrom(fromPatterns[2]));
		assertTrue(m2.containsTransFrom(fromPatterns[0]));
		assertTrue(m2.containsTransFrom(fromPatterns[1]));
		assertFalse(m2.containsTransFrom(fromPatterns[2]));
	}

	/**
	 * {@link exalge2.TransMatrix#hashCode()} のためのテスト・メソッド。
	 */
	public void testHashCode() {
		TransMatrix matrix = new TransMatrix(false);
		assertEquals(Boolean.FALSE.hashCode(), matrix.hashCode());
		matrix = new TransMatrix(true);
		assertEquals(Boolean.TRUE.hashCode(), matrix.hashCode());
		
		TransDivideRatios[] divideRatios = makeDivideRatiosArray(false);
		TransMatrix m1 = new TransMatrix(false);
		TransMatrix m2 = new TransMatrix(true);
		LinkedHashMap<ExBasePattern, TransDivideRatios> map = new LinkedHashMap<ExBasePattern,TransDivideRatios>();
		for (int i = 0; i < divideRatios.length; i++) {
			map.put(fromPatterns[i], divideRatios[i]);
			m1.put(fromPatterns[i], divideRatios[i]);
			m2.put(fromPatterns[i], divideRatios[i]);
		}
		assertEquals((Boolean.FALSE.hashCode()+map.hashCode()), m1.hashCode());
		assertEquals((Boolean.TRUE.hashCode()+map.hashCode()), m2.hashCode());
		assertTrue(m1.hashCode() != m2.hashCode());
		m1.setUseTotalRatio(true);
		assertEquals(m1.hashCode(), m2.hashCode());
	}

	/**
	 * {@link exalge2.TransMatrix#equals(java.lang.Object)} のためのテスト・メソッド。
	 */
	public void testEqualsObject() {
		TransDivideRatios[] dr1 = makeDivideRatiosArray(false);
		TransDivideRatios[] dr2 = makeDivideRatiosArray(true);

		TransMatrix m1 = new TransMatrix(false);
		TransMatrix m2 = new TransMatrix(true);
		assertTrue(m1.isEmpty());
		assertTrue(m2.isEmpty());
		assertFalse(m1.equals(m2));
		assertFalse(m2.equals(m1));
		m2.setUseTotalRatio(false);
		assertEquals(m1, m2);
		assertEquals(m2, m1);
		assertEquals(m1.hashCode(), m2.hashCode());
		
		for (int i = 0; i < dr1.length; i++) {
			m1.put(fromPatterns[i], dr1[i]);
			m2.put(fromPatterns[i], dr2[i]);
		}
		assertEquals(dr1.length, m1.size());
		assertEquals(dr2.length, m2.size());
		assertEquals(m1.size(), m2.size());
		assertFalse(m1.equals(m2));
		assertFalse(m2.equals(m1));
		
		m1.updateTotalRatios();
		assertEquals(m1, m2);
		assertEquals(m2, m1);
		assertEquals(m1.hashCode(), m2.hashCode());
		
		m1.setUseTotalRatio(true);
		assertFalse(m1.equals(m2));
		assertFalse(m2.equals(m1));
	}

	/**
	 * {@link exalge2.TransMatrix#toString()} のためのテスト・メソッド。
	 */
	public void testToString() {
		TransMatrix matrix = new TransMatrix(false);
		assertEquals("useTotalRatio(false)[]", matrix.toString());
		matrix.setUseTotalRatio(true);
		assertEquals("useTotalRatio(true)[]", matrix.toString());
		
		TransDivideRatios[] resultRatios = makeDivideRatiosArray(false);
		TransDivideRatios[] divideRatios = makeDivideRatiosArray(false);
		for (int i = 0; i < divideRatios.length; i++) {
			matrix.put(fromPatterns[i], divideRatios[i]);
		}
		
		// check-1
		StringBuilder sb = new StringBuilder();
		sb.append("useTotalRatio(" + Boolean.TRUE + ")[\n");
		for (int i = 0; i < resultRatios.length; i++) {
			sb.append(fromPatterns[i]);
			sb.append(" -> ");
			sb.append(resultRatios[i]);
			sb.append("\n");
		}
		sb.append(']');
		assertEquals(sb.toString(), matrix.toString());
		
		// check-2
		matrix.updateTotalRatios();
		matrix.setUseTotalRatio(false);
		sb = new StringBuilder();
		sb.append("useTotalRatio(" + Boolean.FALSE + ")[\n");
		for (int i = 0; i < resultRatios.length; i++) {
			resultRatios[i].updateTotalRatio();
			sb.append(fromPatterns[i]);
			sb.append(" -> ");
			sb.append(resultRatios[i]);
			sb.append("\n");
		}
		sb.append(']');
		assertEquals(sb.toString(), matrix.toString());
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
}
