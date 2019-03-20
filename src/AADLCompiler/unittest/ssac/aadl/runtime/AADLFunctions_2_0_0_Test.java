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
 * @(#)AADLFunctions_2_0_0_Test.java	2.1.0	2014/05/29
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AADLFunctions_2_0_0_Test.java	2.0.0	2014/03/23
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.runtime;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLClassLoader;

import exalge2.ExBase;
import exalge2.Exalge;
import junit.framework.TestCase;

/**
 * {@link ssac.aadl.runtime.AADLFunctions} クラスのテスト。
 * 
 * @version 2.1.0	2014/05/29
 * 
 * @author Yasunari Ishizuka (PieCake,Inc.)
 * 
 * @since 2.0.0
 */
public class AADLFunctions_2_0_0_Test extends TestCase
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static private final File	AADLMACRO_JAR	= new File("testlib/AADLMacroEngine.jar");
	
	static private final ExBase	base1	= new ExBase("e1", ExBase.NO_HAT);
	static private final ExBase	base2	= new ExBase("e2", ExBase.NO_HAT);
	static private final ExBase	base3	= new ExBase("e3", ExBase.NO_HAT);
	static private final ExBase	base4	= new ExBase("e4", ExBase.NO_HAT);
	static private final ExBase	base5	= new ExBase("e5", ExBase.NO_HAT);
	static private final ExBase	base6	= new ExBase("e6", ExBase.NO_HAT);
	static private final ExBase	base7	= new ExBase("e7", ExBase.NO_HAT);
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected void setTerminateFlag(boolean flag) {
		URLClassLoader sysloader = (URLClassLoader)ClassLoader.getSystemClassLoader();
		Class<?> sysclass = URLClassLoader.class;
		try {
			// append class path
			Method clMethod = sysclass.getDeclaredMethod("addURL", URL.class);
			clMethod.setAccessible(true);
			clMethod.invoke(sysloader, AADLMACRO_JAR.getAbsoluteFile().toURI().toURL());
			// create instance for InterruptibleJavaMainProcessImpl
			Class<?> implclass = Class.forName("ssac.aadl.macro.process.InterruptibleJavaMainProcessImpl");
			Constructor<?> implconst = implclass.getDeclaredConstructor(File.class, File.class);
			implconst.setAccessible(true);
			Object implinst = implconst.newInstance((File)null, (File)null);
			// set instance to static field
			Field implfld = implclass.getDeclaredField("_instance");
			implfld.setAccessible(true);
			implfld.set(null, implinst);
			// modified terminate request flag
			if (flag) {
				Field flgtermreq = implclass.getDeclaredField("_flgTerminateRequested");
				flgtermreq.setAccessible(true);
				flgtermreq.set(implinst, true);
			}
		}
		catch (Throwable ex) {
			fail("Failed to set termination flag : " + ex.toString());
		}
	}
	
	protected boolean isAcceptTermination() {
		try {
			// get instance from static field
			Class<?> implclass = Class.forName("ssac.aadl.macro.process.InterruptibleJavaMainProcessImpl");
			Field implfld = implclass.getDeclaredField("_instance");
			implfld.setAccessible(true);
			Object implinst = implfld.get(null);
			if (implinst == null) {
				return false;
			}
			// get accept termination flag
			Field flgacceptterm = implclass.getDeclaredField("_flgAcceptTermination");
			flgacceptterm.setAccessible(true);
			return flgacceptterm.getBoolean(implinst);
		}
		catch (Throwable ex) {
			fail("Failed to get accept termination flag : " + ex.toString());
			return false;
		}
	}

	//------------------------------------------------------------
	// Test Cases for 2.0.0
	//------------------------------------------------------------

	public void testCheckAndAcceptTerminateRequest() {
		// initial
		try {
			AADLFunctions.checkAndAcceptTerminateRequest();
		} catch (Throwable ex) {
			fail("Failed to check termination request : " + ex.toString());
		}
		
		// true
		setTerminateFlag(true);
		try {
			AADLFunctions.checkAndAcceptTerminateRequest();
			fail("Failed to throw AADLTerminationException.");
		} catch (AADLTerminatedException ex) {
			assertTrue(true);
		} catch (Throwable ex) {
			fail("Failed to check termination request : " + ex.toString());
		}
		assertTrue(isAcceptTermination());
		
		// false
		setTerminateFlag(false);
		try {
			AADLFunctions.checkAndAcceptTerminateRequest();
		} catch (Throwable ex) {
			fail("Failed to check termination request : " + ex.toString());
		}
		assertFalse(isAcceptTermination());
	}

	public void testAcceptTerminateReqest() {
		// initial
		assertFalse(AADLFunctions.acceptTerminateRequest());
		
		// true
		setTerminateFlag(true);
		assertTrue(AADLFunctions.acceptTerminateRequest());
		assertTrue(isAcceptTermination());
		
		// false
		setTerminateFlag(false);
		assertFalse(AADLFunctions.acceptTerminateRequest());
		assertFalse(isAcceptTermination());
	}

	public void testIsTerminateRequested() {
		// initial
		assertFalse(AADLFunctions.isTerminateRequested());
		
		// true
		setTerminateFlag(true);
		assertTrue(AADLFunctions.isTerminateRequested());
		assertFalse(isAcceptTermination());
		
		// false
		setTerminateFlag(false);
		assertFalse(AADLFunctions.isTerminateRequested());
		assertFalse(isAcceptTermination());
	}

	public void testStrictBar() {
		Exalge srcAlge, orgAlge, ansAlge;
		Exalge retAlge;
		
		// empty
		srcAlge = new Exalge();
		orgAlge = srcAlge.copy();
		ansAlge = new Exalge();
		retAlge = AADLFunctions.strictBar(srcAlge);
		assertTrue(retAlge.isEmpty());
		assertEquals(orgAlge, srcAlge);
		assertEquals(ansAlge, retAlge);
		
		// no pairs
		srcAlge = new Exalge(new Object[]{
				base1,			new BigDecimal("11"),
				base2.hat(),	new BigDecimal("12"),
				base3,			new BigDecimal("13"),
				base4.hat(),	new BigDecimal("14"),
				base5,			new BigDecimal("0"),
				base6.hat(),	new BigDecimal("0"),
		});
		orgAlge = srcAlge.copy();
		ansAlge = new Exalge(new Object[]{
				base1,			new BigDecimal("11"),
				base2.hat(),	new BigDecimal("12"),
				base3,			new BigDecimal("13"),
				base4.hat(),	new BigDecimal("14"),
				base5,			new BigDecimal("0"),
				base6.hat(),	new BigDecimal("0"),
		});
		retAlge = AADLFunctions.strictBar(srcAlge);
		assertFalse(retAlge.isEmpty());
		assertEquals(orgAlge, srcAlge);
		assertEquals(ansAlge, srcAlge);
		assertEquals(ansAlge, retAlge);
		
		// has pairs
		srcAlge = new Exalge(new Object[]{
				base1,			new BigDecimal("11"),
				base2,			new BigDecimal("16"),
				base2.hat(),	new BigDecimal("12"),
				base3,			new BigDecimal("13"),
				base3.hat(),	new BigDecimal("17"),
				base4,			new BigDecimal("14"),
				base4.hat(),	new BigDecimal("14"),
				base5,			new BigDecimal("0"),
				base6.hat(),	new BigDecimal("0"),
				base7,			new BigDecimal("0"),
				base7.hat(),	new BigDecimal("0"),
		});
		orgAlge = srcAlge.copy();
		ansAlge = new Exalge(new Object[]{
				base1,			new BigDecimal("11"),
				base2,			new BigDecimal("4"),
				base3.hat(),	new BigDecimal("4"),
				base5,			new BigDecimal("0"),
				base6.hat(),	new BigDecimal("0"),
		});
		retAlge = AADLFunctions.strictBar(srcAlge);
		assertFalse(retAlge.isEmpty());
		assertEquals(orgAlge, srcAlge);
		assertFalse(ansAlge.equals(srcAlge));
		assertEquals(ansAlge, retAlge);
	}

	public void testStrictBarLeaveZero() {
		Exalge srcAlge, orgAlge, ansAlge;
		Exalge retAlge;
		
		// empty
		srcAlge = new Exalge();
		orgAlge = srcAlge.copy();
		ansAlge = new Exalge();
		retAlge = AADLFunctions.strictBarLeaveZero(srcAlge, true);
		assertTrue(retAlge.isEmpty());
		assertEquals(orgAlge, srcAlge);
		assertEquals(ansAlge, srcAlge);
		assertEquals(ansAlge, retAlge);
		retAlge = AADLFunctions.strictBarLeaveZero(srcAlge, false);
		assertTrue(retAlge.isEmpty());
		assertEquals(orgAlge, srcAlge);
		assertEquals(ansAlge, srcAlge);
		assertEquals(ansAlge, retAlge);
		
		// no pairs
		srcAlge = new Exalge(new Object[]{
				base1,			new BigDecimal("11"),
				base2.hat(),	new BigDecimal("12"),
				base3,			new BigDecimal("13"),
				base4.hat(),	new BigDecimal("14"),
				base5,			new BigDecimal("0"),
				base6.hat(),	new BigDecimal("0"),
		});
		orgAlge = srcAlge.copy();
		ansAlge = new Exalge(new Object[]{
				base1,			new BigDecimal("11"),
				base2.hat(),	new BigDecimal("12"),
				base3,			new BigDecimal("13"),
				base4.hat(),	new BigDecimal("14"),
				base5,			new BigDecimal("0"),
				base6.hat(),	new BigDecimal("0"),
		});
		retAlge = AADLFunctions.strictBarLeaveZero(srcAlge, true);
		assertFalse(retAlge.isEmpty());
		assertEquals(orgAlge, srcAlge);
		assertEquals(ansAlge, srcAlge);
		assertEquals(ansAlge, retAlge);
		retAlge = AADLFunctions.strictBarLeaveZero(srcAlge, false);
		assertFalse(retAlge.isEmpty());
		assertEquals(orgAlge, srcAlge);
		assertEquals(ansAlge, srcAlge);
		assertEquals(ansAlge, retAlge);
		
		// has pairs
		srcAlge = new Exalge(new Object[]{
				base1,			new BigDecimal("11"),
				base2,			new BigDecimal("16"),
				base2.hat(),	new BigDecimal("12"),
				base3,			new BigDecimal("13"),
				base3.hat(),	new BigDecimal("17"),
				base4,			new BigDecimal("14"),
				base4.hat(),	new BigDecimal("14"),
				base5,			new BigDecimal("0"),
				base6.hat(),	new BigDecimal("0"),
				base7,			new BigDecimal("0"),
				base7.hat(),	new BigDecimal("0"),
		});
		orgAlge = srcAlge.copy();
		//--- true
		ansAlge = new Exalge(new Object[]{
				base1,			new BigDecimal("11"),
				base2,			new BigDecimal("4"),
				base3.hat(),	new BigDecimal("4"),
				base4,			new BigDecimal("0"),
				base5,			new BigDecimal("0"),
				base6.hat(),	new BigDecimal("0"),
				base7,			new BigDecimal("0"),
		});
		retAlge = AADLFunctions.strictBarLeaveZero(srcAlge, true);
		assertFalse(retAlge.isEmpty());
		assertEquals(orgAlge, srcAlge);
		assertFalse(ansAlge.equals(srcAlge));
		assertEquals(ansAlge, retAlge);
		//--- false
		ansAlge = new Exalge(new Object[]{
				base1,			new BigDecimal("11"),
				base2,			new BigDecimal("4"),
				base3.hat(),	new BigDecimal("4"),
				base4.hat(),	new BigDecimal("0"),
				base5,			new BigDecimal("0"),
				base6.hat(),	new BigDecimal("0"),
				base7.hat(),	new BigDecimal("0"),
		});
		retAlge = AADLFunctions.strictBarLeaveZero(srcAlge, false);
		assertFalse(retAlge.isEmpty());
		assertEquals(orgAlge, srcAlge);
		assertFalse(ansAlge.equals(srcAlge));
		assertEquals(ansAlge, retAlge);
	}
}
