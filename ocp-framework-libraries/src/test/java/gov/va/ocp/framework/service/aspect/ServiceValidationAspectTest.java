package gov.va.ocp.framework.service.aspect;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import gov.va.ocp.framework.service.DomainResponse;
import gov.va.ocp.framework.service.aspect.validators.TestRequestValidator;

@RunWith(MockitoJUnitRunner.class)
public class ServiceValidationAspectTest {

	@Mock
	private ProceedingJoinPoint proceedingJoinPoint;

	@Mock
	private MethodSignature signature;

	ServiceValidationAspect aspect = new ServiceValidationAspect();

	@Before
	public void setUp() throws Exception {
		assertNotNull(aspect);

		when(proceedingJoinPoint.toLongString()).thenReturn("ProceedingJoinPointLongString");
	}

	@After
	public void tearDown() throws Exception {
	}

	DomainResponse testMethodOneArg(TestRequest test) {
		DomainResponse dr = null;
		return dr;
	}

	DomainResponse testMethodNoArg() {
		DomainResponse dr = null;
		return dr;
	}

	DomainResponse testMethodSad(TestRequest test) {
		DomainResponse dr = null;
		return dr;
	}

	@Test
	public final void testAroundAdviceOneArgHappy() {
		Object[] args = new Object[1];
		args[0] = new TestRequest();

		DomainResponse returned = null;

		when(proceedingJoinPoint.getArgs()).thenReturn(args);
		when(proceedingJoinPoint.toLongString()).thenReturn("ProceedingJoinPointLongString");
		when(proceedingJoinPoint.getSignature()).thenReturn(signature);
		when(signature.getName()).thenReturn("testMethodOneArg");
		when(signature.getDeclaringType()).thenReturn(this.getClass());

		try {
			returned = (DomainResponse) aspect.aroundAdvice(proceedingJoinPoint);
		} catch (ClassCastException cce) {
			cce.printStackTrace();
			fail("Could not cast Object from aroundAdvice to DomainResponse: "
					+ cce.getClass().getSimpleName() + " - " + cce.getMessage());
		}

		assertNull(returned);
	}

	@Test
	public final void testAroundAdviceNoArgsHappy() {
		DomainResponse returned = null;

		when(proceedingJoinPoint.getArgs()).thenReturn(new Object[] {});
		when(proceedingJoinPoint.toLongString()).thenReturn("ProceedingJoinPointLongString");

		try {
			returned = (DomainResponse) aspect.aroundAdvice(proceedingJoinPoint);
		} catch (ClassCastException cce) {
			cce.printStackTrace();
			fail("Could not cast Object from aroundAdvice to DomainResponse: "
					+ cce.getClass().getSimpleName() + " - " + cce.getMessage());
		}

		assertNull(returned);
	}

	@Test
	public final void testAroundAdviceOneArgSad() {
		Object[] args = new Object[1];
		args[0] = new TestRequest();

		DomainResponse returned = null;

		when(proceedingJoinPoint.getArgs()).thenReturn(args);
		when(proceedingJoinPoint.toLongString()).thenReturn("ProceedingJoinPointLongString");
		when(proceedingJoinPoint.getSignature()).thenReturn(signature);
		when(signature.getName()).thenReturn("testMethodSad");
		when(signature.getDeclaringType()).thenReturn(this.getClass());

		try {
			returned = (DomainResponse) aspect.aroundAdvice(proceedingJoinPoint);
		} catch (ClassCastException cce) {
			cce.printStackTrace();
			fail("Could not cast Object from aroundAdvice to DomainResponse: "
					+ cce.getClass().getSimpleName() + " - " + cce.getMessage());
		}

		assertNotNull(returned);
		assertNotNull(returned.getMessages());
		assertTrue(returned.getMessages().size() == 1);
		assertTrue(TestRequestValidator.SEVERITY.equals(
				returned.getMessages().get(0).getSeverity()));
		assertTrue(TestRequestValidator.KEY.equals(
				returned.getMessages().get(0).getKey()));
		assertTrue(TestRequestValidator.TEXT.equals(
				returned.getMessages().get(0).getText()));
		assertTrue(TestRequestValidator.STATUS.equals(
				returned.getMessages().get(0).getHttpStatus()));
	}
}