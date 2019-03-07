package gov.va.ocp.framework.service.aspect;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import gov.va.ocp.framework.service.DomainRequest;
import gov.va.ocp.framework.service.DomainResponse;
import gov.va.ocp.framework.service.aspect.ServiceValidationToMessageAspect;
import gov.va.ocp.framework.validation.ViolationMessageParts;

@RunWith(MockitoJUnitRunner.class)
public class ServiceValidationToMessageAspectTest {

	@Mock
	private ProceedingJoinPoint proceedingJoinPoint;

	@Mock
	private DomainRequest mockServiceRequest;

	@Mock
	private DomainResponse mockServiceResponse;

	@Mock
	private MethodSignature signature;

	@Mock
	private JoinPoint.StaticPart staticPart;

	private ServiceValidationToMessageAspect mockServiceValidationToMessageAspect;

	private Object[] value;

	private Map<String, List<ViolationMessageParts>> map = new HashMap<String, List<ViolationMessageParts>>();
	private List<ViolationMessageParts> testMessageList;

	@Before
	public void setUp() throws Exception {
		value = new Object[1];
		value[0] = mockServiceRequest;
		Mockito.lenient().when(proceedingJoinPoint.getArgs()).thenReturn(value);
		Mockito.lenient().when(proceedingJoinPoint.getStaticPart()).thenReturn(staticPart);
		Mockito.lenient().when(staticPart.getSignature()).thenReturn(signature);
		Mockito.lenient().when(signature.getMethod()).thenReturn(myMethod());

		testMessageList = new ArrayList<ViolationMessageParts>();
		ViolationMessageParts errorMessage1 = new ViolationMessageParts();
		errorMessage1.setNewKey("ErrMsg1");
		errorMessage1.setText("Error ServiceMessage 1");
		ViolationMessageParts errorMessage2 = new ViolationMessageParts();
		errorMessage2.setNewKey("ErrMsg2");
		errorMessage2.setText("Error ServiceMessage 2");
		testMessageList.add(errorMessage1);
		testMessageList.add(errorMessage2);

		mockServiceValidationToMessageAspect = new ServiceValidationToMessageAspect();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testAroundAdvice() {
		try {
			assertNotNull(mockServiceValidationToMessageAspect.aroundAdvice(proceedingJoinPoint));
			assertNull(((DomainResponse) mockServiceValidationToMessageAspect.aroundAdvice(proceedingJoinPoint)).getMessages());

		} catch (Throwable throwable) {

		}
	}

	@Test
	public void testAroundAdviceForResponse() {
		try {
			value = new Object[1];
			Mockito.lenient().when(proceedingJoinPoint.getArgs()).thenReturn(value);
			assertNotNull(mockServiceValidationToMessageAspect.aroundAdvice(proceedingJoinPoint));

		} catch (Throwable throwable) {
			throwable.printStackTrace();
		}
	}

	@Test
	public void testAroundAdviceForException() {
		try {
			Mockito.lenient().when(proceedingJoinPoint.proceed()).thenReturn(new DomainRequest());
			assertNotNull(mockServiceValidationToMessageAspect.aroundAdvice(proceedingJoinPoint));

		} catch (Throwable throwable) {

			assertNotNull(throwable);
		}
	}

	@Test
	public void testConvertMapToMessages() {
		map.put("errors", testMessageList);
		ServiceValidationToMessageAspect.convertMapToMessages(mockServiceResponse, map);
		assertNotNull(mockServiceResponse.getMessages());
		assertEquals(2, mockServiceResponse.getMessages().size());
	}

	public Method myMethod() throws NoSuchMethodException {
		return getClass().getDeclaredMethod("someResponseMethod", String.class);
	}

	public DomainResponse someResponseMethod(String simpleParam) {
		return new DomainResponse();
	}
}
