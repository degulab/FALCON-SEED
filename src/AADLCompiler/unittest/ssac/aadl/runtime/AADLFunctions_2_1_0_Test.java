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
 *  Copyright 2007-2014  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)AADLFunctions_2_1_0_Test.java	2.1.0	2014/05/29
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.runtime;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;
import ssac.aadl.runtime.mqtt.MqttCsvParameter;
import exalge2.ExAlgeSet;
import exalge2.ExBase;
import exalge2.ExBasePattern;
import exalge2.ExBasePatternSet;
import exalge2.ExBaseSet;
import exalge2.ExTransfer;
import exalge2.Exalge;
import exalge2.TransDivideRatios;
import exalge2.TransMatrix;
import exalge2.TransTable;

/**
 * {@link ssac.aadl.runtime.AADLFunctions} クラスのテスト。
 * 
 * @version 2.1.0	2014/05/29
 * 
 * @author Yasunari Ishizuka (PieCake,Inc.)
 * 
 * @since 2.1.0
 */
public class AADLFunctions_2_1_0_Test extends TestCase
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
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Test Cases for 2.1.0
	//------------------------------------------------------------
	
	public void testZeroProjExalge() {
		Exalge alge = new Exalge(new Object[]{
				new ExBase("hoge", ExBase.NO_HAT), new BigDecimal(0),
				new ExBase("age", ExBase.HAT), new BigDecimal(3),
		});
		Exalge ans = new Exalge(new Object[]{
				new ExBase("hoge", ExBase.NO_HAT), new BigDecimal(0),
		});
		
		Exalge result = AADLFunctions.zeroProj(alge);
		assertEquals(ans, result);
	}
	
	public void testZeroProjExAlgeSet() {
		ExAlgeSet algeset = new ExAlgeSet(Arrays.asList(new Exalge[]{
				new Exalge(new Object[]{
						new ExBase("hoge", ExBase.NO_HAT), new BigDecimal(0),
						new ExBase("age", ExBase.HAT), new BigDecimal(3),
				}),
				new Exalge(new Object[]{
						new ExBase("hoge2", ExBase.NO_HAT), new BigDecimal(0),
						new ExBase("age2", ExBase.HAT), new BigDecimal(0),
				}),
				new Exalge(new Object[]{
						new ExBase("hoge3", ExBase.NO_HAT), new BigDecimal(123),
						new ExBase("age3", ExBase.HAT), new BigDecimal(987),
				}),
		}));
		ExAlgeSet ansset = new ExAlgeSet(Arrays.asList(new Exalge[]{
				new Exalge(new Object[]{
						new ExBase("hoge", ExBase.NO_HAT), new BigDecimal(0),
				}),
				new Exalge(new Object[]{
						new ExBase("hoge2", ExBase.NO_HAT), new BigDecimal(0),
						new ExBase("age2", ExBase.HAT), new BigDecimal(0),
				}),
		}));
		
		ExAlgeSet result = AADLFunctions.zeroProj(algeset);
		assertEquals(ansset, result);
	}
	
	public void testNotZeroProjExalge() {
		Exalge alge = new Exalge(new Object[]{
				new ExBase("hoge", ExBase.NO_HAT), new BigDecimal(0),
				new ExBase("age", ExBase.HAT), new BigDecimal(3),
		});
		Exalge ans = new Exalge(new Object[]{
				new ExBase("age", ExBase.HAT), new BigDecimal(3),
		});
		
		Exalge result = AADLFunctions.notzeroProj(alge);
		assertEquals(ans, result);
	}
	
	public void testNotZeroProjExAlgeSet() {
		ExAlgeSet algeset = new ExAlgeSet(Arrays.asList(new Exalge[]{
				new Exalge(new Object[]{
						new ExBase("hoge", ExBase.NO_HAT), new BigDecimal(0),
						new ExBase("age", ExBase.HAT), new BigDecimal(3),
				}),
				new Exalge(new Object[]{
						new ExBase("hoge2", ExBase.NO_HAT), new BigDecimal(0),
						new ExBase("age2", ExBase.HAT), new BigDecimal(0),
				}),
				new Exalge(new Object[]{
						new ExBase("hoge3", ExBase.NO_HAT), new BigDecimal(123),
						new ExBase("age3", ExBase.HAT), new BigDecimal(987),
				}),
		}));
		ExAlgeSet ansset = new ExAlgeSet(Arrays.asList(new Exalge[]{
				new Exalge(new Object[]{
						new ExBase("age", ExBase.HAT), new BigDecimal(3),
				}),
				new Exalge(new Object[]{
						new ExBase("hoge3", ExBase.NO_HAT), new BigDecimal(123),
						new ExBase("age3", ExBase.HAT), new BigDecimal(987),
				}),
		}));
		
		ExAlgeSet result = AADLFunctions.notzeroProj(algeset);
		assertEquals(ansset, result);
	}
	
	public void testExalgeCsvString() {
		Exalge alge = new Exalge(new Object[]{
				new ExBase("hoge", ExBase.NO_HAT), new BigDecimal(0),
				new ExBase("age", ExBase.HAT), new BigDecimal(3),
		});

		String csv = AADLFunctions.toCsvString(alge);
		
		Exalge result = AADLFunctions.newExalgeFromCsvString(csv);
		
		assertEquals(alge, result);
	}
	
	public void testExAlgeSetCsvString() {
		ExAlgeSet algeset = new ExAlgeSet(Arrays.asList(new Exalge[]{
				new Exalge(new Object[]{
						new ExBase("hoge", ExBase.NO_HAT), new BigDecimal(0),
						new ExBase("age", ExBase.HAT), new BigDecimal(3),
				}),
				new Exalge(new Object[]{
						new ExBase("hoge2", ExBase.NO_HAT), new BigDecimal(0),
						new ExBase("age2", ExBase.HAT), new BigDecimal(0),
				}),
				new Exalge(new Object[]{
						new ExBase("hoge3", ExBase.NO_HAT), new BigDecimal(123),
						new ExBase("age3", ExBase.HAT), new BigDecimal(987),
				}),
		}));

		String csv = AADLFunctions.toCsvString(algeset);
		
		ExAlgeSet result = AADLFunctions.newExAlgeSetFromCsvString(csv);
		
		assertEquals(algeset, result);
	}
	
	public void testExBaseSetCsvString() {
		ExBaseSet baseset = new ExBaseSet(Arrays.asList(new ExBase[]{
				new ExBase("hoge1", ExBase.NO_HAT),
				new ExBase("hoge2", ExBase.HAT),
				new ExBase("hoge3", ExBase.HAT),
		}));
		
		String csv = AADLFunctions.toCsvString(baseset);
		
		ExBaseSet result = AADLFunctions.newExBaseSetFromCsvString(csv);
		
		assertEquals(baseset, result);
	}
	
	public void testExBasePatternSetCsvString() {
		ExBasePatternSet baseset = new ExBasePatternSet(Arrays.asList(new ExBasePattern[]{
				new ExBasePattern("*", ExBasePattern.WILDCARD),
				new ExBasePattern("pat1", ExBase.HAT, "pat2", "pat3"),
				new ExBasePattern("hoge*hoge", ExBase.NO_HAT, "time*time"),
		}));
		
		String csv = AADLFunctions.toCsvString(baseset);
		
		ExBasePatternSet result = AADLFunctions.newExBasePatternSetFromCsvString(csv);
		
		assertEquals(baseset, result);
	}
	
	public void testTransTableCsvString() {
		TransTable table = new TransTable();
		table.put(new ExBasePattern("from1", ExBasePattern.WILDCARD), new ExBasePattern("to1", ExBasePattern.WILDCARD));
		table.put(new ExBasePattern("hoge*", ExBasePattern.WILDCARD), new ExBasePattern("test1", ExBasePattern.WILDCARD, "test2"));
		
		String csv = AADLFunctions.toCsvString(table);
		
		TransTable result = AADLFunctions.newTransTableFromCsvString(csv);
		
		assertEquals(table, result);
	}
	
	public void testTransMatrixCsvString() {
		TransMatrix table = new TransMatrix();
		TransDivideRatios ratios = new TransDivideRatios();
		ratios.put(new ExBasePattern("test1", ExBasePattern.WILDCARD), new BigDecimal(0));
		ratios.put(new ExBasePattern("test2", ExBasePattern.WILDCARD), new BigDecimal(3));
		ratios.put(new ExBasePattern("test3", ExBasePattern.WILDCARD), new BigDecimal(5));
		table.put(new ExBasePattern("from1", ExBasePattern.WILDCARD), ratios);
		table.put(new ExBasePattern("from2", ExBasePattern.WILDCARD), ratios);
		table.put(new ExBasePattern("from3", ExBasePattern.WILDCARD), ratios);
		table.updateTotalRatios();
		
		String csv = AADLFunctions.toCsvString(table);
		
		TransMatrix result = AADLFunctions.newTransMatrixFromCsvString(csv);
		
		assertEquals(table, result);
	}
	
	public void testExTransferCsvString() {
		ExTransfer table = new ExTransfer();
		table.putAggregate(new ExBasePattern("from1", ExBasePattern.WILDCARD), new ExBasePattern("to1", ExBasePattern.WILDCARD));
		table.putAggregate(new ExBasePattern("hoge*", ExBasePattern.WILDCARD), new ExBasePattern("test1", ExBasePattern.WILDCARD, "test2"));
		
		String csv = AADLFunctions.toCsvString(table);
		
		ExTransfer result = AADLFunctions.newExTransferFromCsvString(csv);
		
		assertEquals(table, result);
	}
	
	public void testMqttCsvParameter() {
		String csv1 = "localhost,/#,hoge/+/hoge";
		String csv2 = "localhost,hoge,hoge/hoge/hoge";
		String ansServerURI = "tcp://localhost:1883";
		List<String> ans1 = Arrays.asList("/#", "hoge/+/hoge");
		List<String> ans2 = Arrays.asList("hoge", "hoge/hoge/hoge");
		MqttCsvParameter param;
		
		try {
			AADLFunctions.parseMqttCsvParameter(csv1, true, new BigDecimal("1234012341233455423234289654323452345"));
			fail("Expecting IllegalArgumentException.");
		} catch (IllegalArgumentException ex) {
			assertTrue(true);
		}
		param = AADLFunctions.parseMqttCsvParameter(csv1, true, new BigDecimal(2));
		assertEquals(ansServerURI, AADLFunctions.getMqttServerURI(param));
		assertNull(AADLFunctions.getMqttClientID(param));
		assertNotNull(AADLFunctions.getAvailableMqttClientID(param));
		assertEquals(new BigDecimal(2), AADLFunctions.getMqttQOS(param));
		assertEquals(ans1, AADLFunctions.getMqttTopicList(param));
		
		try {
			AADLFunctions.parseMqttCsvParameter(csv1, false);
			fail("Expecting IllegalArgumentException.");
		} catch (IllegalArgumentException ex) {
			assertTrue(true);
		}
		param = AADLFunctions.parseMqttCsvParameter(csv2, false);
		assertEquals(ansServerURI, AADLFunctions.getMqttServerURI(param));
		assertNull(AADLFunctions.getMqttClientID(param));
		assertNotNull(AADLFunctions.getAvailableMqttClientID(param));
		assertEquals(new BigDecimal(1), AADLFunctions.getMqttQOS(param));
		assertEquals(ans2, AADLFunctions.getMqttTopicList(param));
		
		try {
			AADLFunctions.parseMqttCsvParameter(csv1, new BigDecimal("1234012341233455423234289654323452345"));
			fail("Expecting IllegalArgumentException.");
		} catch (IllegalArgumentException ex) {
			assertTrue(true);
		}
		param = AADLFunctions.parseMqttCsvParameter(csv1, new BigDecimal(2));
		assertEquals(ansServerURI, AADLFunctions.getMqttServerURI(param));
		assertNull(AADLFunctions.getMqttClientID(param));
		assertNotNull(AADLFunctions.getAvailableMqttClientID(param));
		assertEquals(new BigDecimal(2), AADLFunctions.getMqttQOS(param));
		assertEquals(ans1, AADLFunctions.getMqttTopicList(param));
		
		param = AADLFunctions.parseMqttCsvParameter(csv1);
		assertEquals(ansServerURI, AADLFunctions.getMqttServerURI(param));
		assertNull(AADLFunctions.getMqttClientID(param));
		assertNotNull(AADLFunctions.getAvailableMqttClientID(param));
		assertEquals(new BigDecimal(1), AADLFunctions.getMqttQOS(param));
		assertEquals(ans1, AADLFunctions.getMqttTopicList(param));
		
		String anscsv = "tcp://localhost:1883|qos=1,/#,hoge/+/hoge";
		String result = AADLFunctions.toCsvString(param);
		assertEquals(anscsv, result);
	}
}
