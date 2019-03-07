package gov.va.ocp.framework.rest.provider.aspect;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.event.Level;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockPart;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import gov.va.ocp.framework.AbstractBaseLogTester;
import gov.va.ocp.framework.audit.AuditEvents;
import gov.va.ocp.framework.audit.Auditable;
import gov.va.ocp.framework.exception.OcpRuntimeException;
import gov.va.ocp.framework.log.OcpLogger;
import gov.va.ocp.framework.messages.MessageSeverity;
import gov.va.ocp.framework.messages.ServiceMessage;
import gov.va.ocp.framework.rest.provider.bre.MessagesToHttpStatusRulesEngine;
import gov.va.ocp.framework.rest.provider.bre.rules.MessageSeverityMatchRule;
import gov.va.ocp.framework.service.DomainRequest;
import gov.va.ocp.framework.service.DomainResponse;

@Ignore
@RunWith(MockitoJUnitRunner.class)
public class RestProviderHttpResponseAspectTest extends AbstractBaseLogTester {

	private final OcpLogger restProviderLog = super.getLogger(RestProviderHttpResponseAspect.class);

	private RestProviderHttpResponseAspect restProviderHttpResponseAspect;
	@Mock
	private ProceedingJoinPoint proceedingJoinPoint;

	@Mock
	private ResponseEntity<DomainResponse> responseEntity;

	@Mock
	private DomainResponse domainResponse;

	@Mock
	private MethodSignature mockSignature;

	@InjectMocks
	private final RestProviderHttpResponseAspect requestResponseAspect = new RestProviderHttpResponseAspect();

	@InjectMocks
	private final LogAnnotatedMethodRequestResponseAspect logAnnotatedAspect = new LogAnnotatedMethodRequestResponseAspect();

	private final TestServiceRequest mockRequestObject = new TestServiceRequest();
	private final Object[] mockArray = { mockRequestObject };

	private final List<ServiceMessage> detailedMsg = new ArrayList<ServiceMessage>();

	@Before
	public void setUp() throws Exception {
		super.getAppender().clear();

		final MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();
		final MockHttpServletResponse httpServletResponse = new MockHttpServletResponse();

		httpServletRequest.setContentType("multipart/form-data");
		final MockPart userData = new MockPart("userData", "userData", "{\"name\":\"test aida\"}".getBytes());
		httpServletRequest.addPart(userData);

		httpServletRequest.addHeader("TestHeader", "TestValue");
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(httpServletRequest, httpServletResponse));

		super.getAppender().clear();
		restProviderLog.setLevel(Level.DEBUG);
		try {
			Mockito.lenient().when(proceedingJoinPoint.getArgs()).thenReturn(mockArray);
			Mockito.lenient().when(proceedingJoinPoint.getSignature()).thenReturn(mockSignature);
			Mockito.lenient().when(mockSignature.getMethod()).thenReturn(myMethod());

			final ServiceMessage msg = new ServiceMessage(MessageSeverity.FATAL, "FatalKey", "Fatal ServiceMessage", null);
			detailedMsg.add(msg);
			Mockito.lenient().when(proceedingJoinPoint.proceed()).thenReturn(responseEntity);
			Mockito.lenient().when(responseEntity.getBody()).thenReturn(domainResponse);
			Mockito.lenient().when(domainResponse.getMessages()).thenReturn(detailedMsg);
		} catch (final Throwable e) {

		}

	}

	@Override
	@After
	public void tearDown() {
	}

	// TODO
	@Ignore
	@Test
	public void testMultipartFormData() {

		final MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();
		final MockHttpServletResponse httpServletResponse = new MockHttpServletResponse();

		httpServletRequest.setContentType("multipart/form-data");
		final MockPart userData = new MockPart("userData", "userData", "{\"name\":\"test aida\"}".getBytes());
		httpServletRequest.addPart(userData);
		httpServletRequest.addHeader("TestHeader", "TestValue");
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(httpServletRequest, httpServletResponse));

		restProviderHttpResponseAspect = new RestProviderHttpResponseAspect();
		Object returnObject = null;
		try {
			Mockito.lenient().when(proceedingJoinPoint.proceed()).thenReturn(domainResponse);
			Mockito.lenient().when(proceedingJoinPoint.getSignature()).thenReturn(mockSignature);
			Mockito.lenient().when(mockSignature.getMethod()).thenReturn(myMethod());
			Mockito.lenient().when(proceedingJoinPoint.getTarget()).thenReturn(new TestClass());

//TODO			returnObject = restProviderHttpResponseAspect.aroundAdvice(proceedingJoinPoint);
		} catch (final Throwable throwable) {

		}
		assertNotNull(returnObject);
	}

	// TODO
	@Ignore
	@Test
	public void testMultipartmixed() {

		final MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();
		final MockHttpServletResponse httpServletResponse = new MockHttpServletResponse();

		httpServletRequest.setContentType("multipart/mixed");
		final MockPart userData = new MockPart("userData", "userData", "{\"name\":\"test aida\"}".getBytes());
		httpServletRequest.addPart(userData);
		httpServletRequest.addHeader("TestHeader", "TestValue");
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(httpServletRequest, httpServletResponse));

		restProviderHttpResponseAspect = new RestProviderHttpResponseAspect();
		Object returnObject = null;
		try {
			Mockito.lenient().when(proceedingJoinPoint.proceed()).thenReturn(domainResponse);
			Mockito.lenient().when(proceedingJoinPoint.getSignature()).thenReturn(mockSignature);
			Mockito.lenient().when(mockSignature.getMethod()).thenReturn(myMethod());
			Mockito.lenient().when(proceedingJoinPoint.getTarget()).thenReturn(new TestClass());

//TODO			returnObject = restProviderHttpResponseAspect.aroundAdvice(proceedingJoinPoint);
		} catch (final Throwable throwable) {

		}
		assertNotNull(returnObject);
	}

	// TODO
	@Ignore
	@Test
	public void testServiceResponseReturnType() {
		restProviderHttpResponseAspect = new RestProviderHttpResponseAspect();
		Object returnObject = null;
		try {
			Mockito.lenient().when(proceedingJoinPoint.proceed()).thenReturn(domainResponse);
			Mockito.lenient().when(proceedingJoinPoint.getSignature()).thenReturn(mockSignature);
			Mockito.lenient().when(mockSignature.getMethod()).thenReturn(myMethod());
			Mockito.lenient().when(proceedingJoinPoint.getTarget()).thenReturn(new TestClass());

			// TODO returnObject = restProviderHttpResponseAspect.aroundAdvice(proceedingJoinPoint);
		} catch (final Throwable throwable) {

		}
		assertNotNull(returnObject);
	}

	@Ignore
	@Test
	public void testServiceResponseReturnTypes() {
		restProviderHttpResponseAspect = new RestProviderHttpResponseAspect();
		Object returnObject = null;
		try {
			final DomainResponse serviceResp = new DomainResponse();
			serviceResp.addMessage(MessageSeverity.FATAL, "Test KEY", "Test Error", HttpStatus.INTERNAL_SERVER_ERROR);
			Mockito.lenient().when(proceedingJoinPoint.proceed()).thenReturn(serviceResp);
			Mockito.lenient().when(proceedingJoinPoint.getSignature()).thenReturn(mockSignature);
			Mockito.lenient().when(mockSignature.getMethod()).thenReturn(myMethod());
			Mockito.lenient().when(proceedingJoinPoint.getTarget()).thenReturn(new TestClass());

			// TODO returnObject = restProviderHttpResponseAspect.aroundAdvice(proceedingJoinPoint);
		} catch (final Throwable throwable) {

		}
		assertNotNull(returnObject);
	}

	// TODO
	@Ignore
	@Test
	public void testConstructorWithParam() {
		final MessagesToHttpStatusRulesEngine ruleEngine = new MessagesToHttpStatusRulesEngine();
		ruleEngine.addRule(new MessageSeverityMatchRule(MessageSeverity.FATAL, HttpStatus.INTERNAL_SERVER_ERROR));
		ruleEngine.addRule(new MessageSeverityMatchRule(MessageSeverity.ERROR, HttpStatus.BAD_REQUEST));
//TODO		restProviderHttpResponseAspect = new RestProviderHttpResponseAspect(ruleEngine);
		assertNotNull(restProviderHttpResponseAspect);

	}

	// TODO
	@Ignore
	@Test
	public void testAroundAdvice() {
		try {
			restProviderHttpResponseAspect = new RestProviderHttpResponseAspect();
			// TODO assertNull(restProviderHttpResponseAspect.aroundAdvice(proceedingJoinPoint));
		} catch (final Throwable e) {

		}

	}

	// TODO
	@Ignore
	@Test
	public void testAroundAdviceCatchReferenceExceptionLogging() {
		super.getAppender().clear();

		restProviderLog.setLevel(Level.ERROR);
		restProviderHttpResponseAspect = new RestProviderHttpResponseAspect();
		Object returnObject = null;
		try {
			Mockito.lenient().when(proceedingJoinPoint.proceed()).thenThrow(new OcpRuntimeException());
			Mockito.lenient().when(proceedingJoinPoint.getSignature()).thenReturn(mockSignature);
			Mockito.lenient().when(mockSignature.getMethod()).thenReturn(myMethod());
			Mockito.lenient().when(proceedingJoinPoint.getTarget()).thenReturn(new TestClass());

			// TODO returnObject = restProviderHttpResponseAspect.aroundAdvice(proceedingJoinPoint);
		} catch (final Throwable throwable) {

		}
		assertTrue(((DomainResponse) returnObject).getMessages().size() > 0);
	}

	// TODO
	@Ignore
	@Test
	public void testAroundAdviceCatchExceptionLogging() {
		super.getAppender().clear();
		restProviderLog.setLevel(Level.ERROR);

		restProviderHttpResponseAspect = new RestProviderHttpResponseAspect();
		Object returnObject = null;
		try {
			Mockito.lenient().when(proceedingJoinPoint.proceed())
					.thenThrow(new Throwable("Unit Test Throwable converted to ReferenceRuntimException"));
			Mockito.lenient().when(proceedingJoinPoint.getSignature()).thenReturn(mockSignature);
			Mockito.lenient().when(mockSignature.getMethod()).thenReturn(myMethod());
			Mockito.lenient().when(proceedingJoinPoint.getTarget()).thenReturn(new TestClass());
			// TODO returnObject = restProviderHttpResponseAspect.aroundAdvice(proceedingJoinPoint);
		} catch (final Throwable throwable) {

		}

		assertTrue(((DomainResponse) returnObject).getMessages().size() > 0);
	}

	@Test
	public void testAnnotatedMethodRequestResponse() {
		Object obj;
		try {
			Mockito.lenient().when(proceedingJoinPoint.proceed()).thenReturn(domainResponse);
			Mockito.lenient().when(proceedingJoinPoint.getSignature()).thenReturn(mockSignature);
			Mockito.lenient().when(mockSignature.getMethod()).thenReturn(myAnnotatedMethod());
			Mockito.lenient().when(proceedingJoinPoint.getTarget()).thenReturn(new TestClass());

			restProviderHttpResponseAspect = new RestProviderHttpResponseAspect();
			obj = logAnnotatedAspect.logAnnotatedMethodRequestResponse(proceedingJoinPoint);
			assertNotNull(obj);
		} catch (final Throwable throwable) {
			assertTrue(throwable instanceof RuntimeException);
		}
	}

	@Test
	public void testAnnotatedMethodRequestResponseRunTimeException() {

		try {
			final Object[] array = { null, new Object() };
			Mockito.lenient().when(proceedingJoinPoint.getArgs()).thenReturn(array);
			Mockito.lenient().when(proceedingJoinPoint.proceed()).thenThrow(new RuntimeException("Unit Test Exception"));
			restProviderHttpResponseAspect = new RestProviderHttpResponseAspect();
			logAnnotatedAspect.logAnnotatedMethodRequestResponse(proceedingJoinPoint);
		} catch (final Throwable throwable) {
			assertTrue(throwable instanceof RuntimeException);
		}

	}

	@Test
	public void testAnnotatedMethodRequestResponseRunTimeExceptionArrayZero() {

		try {
			final Object[] array = new Object[0];
			Mockito.lenient().when(proceedingJoinPoint.getArgs()).thenReturn(array);
			Mockito.lenient().when(proceedingJoinPoint.proceed()).thenThrow(new RuntimeException("Unit Test Exception"));
			restProviderHttpResponseAspect = new RestProviderHttpResponseAspect();
			logAnnotatedAspect.logAnnotatedMethodRequestResponse(proceedingJoinPoint);
		} catch (final Throwable throwable) {
			assertTrue(throwable instanceof RuntimeException);
		}

	}

	@Test
	public void testGetReturnResponse() {
		final RestProviderHttpResponseAspect aspect = new RestProviderHttpResponseAspect();
		Method method = null;
		Object retval = null;
		try {
			method = aspect.getClass().getDeclaredMethod("getReturnResponse", boolean.class, Object.class);
			method.setAccessible(true);
			retval = method.invoke(aspect, Boolean.TRUE, new ResponseEntity<DomainResponse>(HttpStatus.valueOf(200)));
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			e.printStackTrace();
			fail("Should not have exception here");
		}
		assertNull(retval);

		try {
			retval = method.invoke(aspect, Boolean.FALSE, "hello");
		} catch (SecurityException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			e.printStackTrace();
			fail("Should not have exception here");
		}
		assertNotNull(retval);
		assertTrue("hello".equals(retval));
	}

	public Method myMethod() throws NoSuchMethodException {
		return getClass().getDeclaredMethod("someMethod");
	}

	public void someMethod() {
		// do nothing
	}

	public Method myAnnotatedMethod() throws NoSuchMethodException {
		return getClass().getDeclaredMethod("annotatedMethod");
	}

	@Auditable(event = AuditEvents.REQUEST_RESPONSE, activity = "testActivity")
	public void annotatedMethod() {
		// do nothing
	}

	class TestClass {

	}
}

class TestServiceRequest extends DomainRequest {
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 8736731329416969081L;
	private String text;

	public String getText() {
		return text;
	}

	public void setText(final String text) {
		this.text = text;
	}
}
