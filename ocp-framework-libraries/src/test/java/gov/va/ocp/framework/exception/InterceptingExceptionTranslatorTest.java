package gov.va.ocp.framework.exception;

import org.junit.Rule;
import org.junit.rules.ExpectedException;

import gov.va.ocp.framework.AbstractBaseLogTester;

public class InterceptingExceptionTranslatorTest extends AbstractBaseLogTester {

	@Rule
	public ExpectedException exceptions = ExpectedException.none();

//	/** Underlying logger implementation of OcpLogger */
//	private OcpLogger LOG = super.getLogger(InterceptingExceptionTranslator.class);
//
//	@Test
//	public void testReferenceRunTimeExceptionDefault() throws Exception {
//		InterceptingExceptionTranslator interceptingExceptionTranslator = new InterceptingExceptionTranslator();
//		interceptingExceptionTranslator.setDefaultExceptionType(OcpRuntimeException.class);
//
//		OcpRuntimeException throwable =
//				new OcpRuntimeException("", "Cause Unit Test",
//						MessageSeverity.ERROR, HttpStatus.BAD_REQUEST);
//
//		exceptions.expect(OcpRuntimeException.class);
//		exceptions.expectMessage("Cause Unit Test");
//
//		interceptingExceptionTranslator.afterThrowing(
//				this.getClass().getMethod("testReferenceRunTimeExceptionDefault"),
//				null, null, throwable);
//	}
//
//	@Test
//	public void testReferenceRunTimeExceptionMapNullAndDefaultExceptionTypeNull() {
//		super.getAppender().clear();
//
//		InterceptingExceptionTranslator interceptingExceptionTranslator = new InterceptingExceptionTranslator();
//
//		Throwable throwable = new Throwable("Cause Unit Test");
//
//		try {
//			interceptingExceptionTranslator.afterThrowing(
//					this.getClass().getMethod("testReferenceRunTimeExceptionMapNullAndDefaultExceptionTypeNull"), null, null,
//					throwable);
//		} catch (Throwable e) {
//			assertTrue(OcpRuntimeException.class.isAssignableFrom(e.getClass()));
//		}
//
//		Assert.assertTrue(super.getAppender().get(0).getMessage().startsWith(
//				"InterceptingExceptionTranslator caught exception, handling it as configured.  "
//						+ "Here are details [java.lang.Throwable thrown by "
//						+ "gov.va.ocp.framework.exception.InterceptingExceptionTranslatorTest.testReferenceRunTimeExceptionMapNullAndDefaultExceptionTypeNull]"
//						+ " args [null]."));
//	}
//
//	@Test
//	public void testExcludeThrowableDebugStatementException() throws Exception {
//		super.getAppender().clear();
//
//		InterceptingExceptionTranslator interceptingExceptionTranslator = new InterceptingExceptionTranslator();
//		interceptingExceptionTranslator.setDefaultExceptionType(OcpRuntimeException.class);
//
//		Set<String> exclusion = new HashSet<>();
//		exclusion.add("java.lang.Throwable");
//		interceptingExceptionTranslator.setExclusionSet(exclusion);
//
//		Throwable throwable = new Throwable("Cause Unit Test");
//
//		interceptingExceptionTranslator.afterThrowing(this.getClass().getMethod("testExcludeThrowableDebugStatementException"), null,
//				null, throwable);
//
//		Assert.assertEquals("InterceptingExceptionTranslator is configured to ignore exceptions of type [java.lang.Throwable]"
//				+ " - not translating this exception.", super.getAppender().get(0).getMessage());
//
//	}
//
//	@Test
//	public void testExcludeThrowableExceptionNoDebugStatement() throws Exception {
//		super.getAppender().clear();
//
//		// Setup
//		LOG.setLevel(Level.ERROR);
//		InterceptingExceptionTranslator interceptingExceptionTranslator = new InterceptingExceptionTranslator();
//		interceptingExceptionTranslator.setDefaultExceptionType(OcpRuntimeException.class);
//
//		Set<String> exclusion = new HashSet<>();
//		// Test OR statement by matching on package name
//		exclusion.add("java.lang");
//		interceptingExceptionTranslator.setExclusionSet(exclusion);
//		Throwable throwable = new Throwable("Cause Unit Test");
//
//		// Test
//		interceptingExceptionTranslator.afterThrowing(this.getClass().getMethod("testExcludeThrowableExceptionNoDebugStatement"), null,
//				null, throwable);
//
//		// Nothing should have been logged because exception was excluded
//		Assert.assertTrue(super.getAppender().isEmpty());
//
//	}
//
//	@Test
//	public void testResolvableException() throws Exception {
//		InterceptingExceptionTranslator interceptingExceptionTranslator = new InterceptingExceptionTranslator();
//		interceptingExceptionTranslator.setDefaultExceptionType(OcpRuntimeException.class);
//
//		OcpRuntimeException throwable = new OcpRuntimeException("", "Cause Unit Test",
//				null, null, new Throwable("Test cause"));
//
//		try {
//			interceptingExceptionTranslator.afterThrowing(
//					this.getClass().getMethod("testResolvableException"),
//					null, null, throwable);
//			fail("Should have thrown exception");
//		} catch (Exception e) {
//			assertTrue(OcpRuntimeException.class.isAssignableFrom(throwable.getClass()));
//			assertTrue(e.getCause().getClass().getName().equals(Throwable.class.getName()));
//		}
//
//	}
}
