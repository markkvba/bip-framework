package gov.va.ocp.framework.service.aspect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;

import gov.va.ocp.framework.audit.AuditLogger;
import gov.va.ocp.framework.log.OcpLogger;
import gov.va.ocp.framework.log.OcpLoggerFactory;
import gov.va.ocp.framework.messages.MessageSeverity;
import gov.va.ocp.framework.messages.ServiceMessage;
import gov.va.ocp.framework.rest.provider.aspect.BaseHttpProviderAspect;
import gov.va.ocp.framework.service.DomainResponse;
import gov.va.ocp.framework.validation.Validatable;
import gov.va.ocp.framework.validation.ViolationMessageParts;

/**
 * TODO This aspect needs pointcut on the controller, not the service api.
 * TODO So ServiceMessage and DomainResponse need to change to Message and ProviderResponse.
 *
 * The Class ServiceValidationToMessageAspect is an aspect that performs validation (i.e. JSR 303) on the
 * standard service methods which are validatable, converting validation errors into ServiceMessage objects in a consistent way.
 *
 * Standard service operations which are validatable are those which are...
 * (1) public
 * (2) return a DomainResponse and
 * (3) have a single input that is of the type Validatable.
 *
 * @See gov.va.ocp.framework.service.DomainResponse
 * @see gov.va.ocp.framework.validation.Validatable
 *
 * @author jshrader
 */
@Aspect
@Order(-9998)
public class ServiceValidationToMessageAspect extends BaseServiceAspect {

	private static final OcpLogger LOGGER = OcpLoggerFactory.getLogger(ServiceValidationToMessageAspect.class);

	/**
	 * Around advice for{@link BaseServiceAspect#serviceImpl()} pointcut.
	 * <p>
	 * This method will execute JSR-303 validations on any {@link Validatable} parameter objects in the method signature.<br/>
	 * Any failed validations is added to the method's response object, and is audit logged.
	 *
	 * @param joinPoint
	 * @return Object
	 * @throws Throwable
	 */
	@Around("publicStandardServiceMethod() && serviceImpl()")
	public Object aroundAdvice(final ProceedingJoinPoint joinPoint) throws Throwable {

		DomainResponse domainResponse = null;

		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("ServiceValidationToMessageAspect executing around method:" + joinPoint.toLongString());
			}

			// fetch the request
			List<Object> serviceRequest = null;
			if (joinPoint.getArgs().length > 0) {
				serviceRequest = Arrays.asList(joinPoint.getArgs());
			}
			if (serviceRequest == null) {
				serviceRequest = new ArrayList<>();
			}

			// start creating the response
			final Map<String, List<ViolationMessageParts>> messages = new LinkedHashMap<>();

			if (joinPoint.getArgs().length > 0) {
				final MethodSignature methodSignature = (MethodSignature) joinPoint.getStaticPart().getSignature();
				domainResponse = validateRequest(methodSignature, serviceRequest, messages);
			}

			// proceed with the actual method
			if (domainResponse == null) {
				domainResponse = (DomainResponse) joinPoint.proceed();
			}
		} catch (final Throwable throwable) {
			LOGGER.error("ServiceValidationToMessageAspect encountered " + throwable.getClass().getName()
					+ ": " + throwable.getMessage(), throwable);
			throw throwable;
		} finally {
			LOGGER.debug("ServiceValidationToMessageAspect after method was called.");
		}

		return domainResponse;

	}

	/**
	 * Convert map to messages. This is exposed so services can call directly if they desire.
	 *
	 * @param domainResponse the service response
	 * @param messages the messages
	 */
	protected static void convertMapToMessages(final DomainResponse domainResponse,
			final Map<String, List<ViolationMessageParts>> messages) {
		for (final Entry<String, List<ViolationMessageParts>> entry : messages.entrySet()) {
			for (final ViolationMessageParts fieldError : entry.getValue()) {
				domainResponse.addMessage(MessageSeverity.ERROR, fieldError.getNewKey(), fieldError.getText(), HttpStatus.BAD_REQUEST);
			}
		}
		Collections.sort(domainResponse.getMessages(), Comparator.comparing(ServiceMessage::getKey));
	}

	/**
	 * Validate any validatable objects on the serviceRequest list.
	 *
	 * @param methodSignature
	 * @param serviceRequest
	 * @param messages
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	private DomainResponse validateRequest(MethodSignature methodSignature, List<Object> serviceRequest,
			Map<String, List<ViolationMessageParts>> messages)
			throws InstantiationException, IllegalAccessException {
		DomainResponse domainResponse = null;
		for (final Object objValidatable : serviceRequest) {
			if (objValidatable != null && objValidatable instanceof Validatable) {
				((Validatable) objValidatable).validate(messages);
			}
		}
		if (!messages.isEmpty()) {
			domainResponse = (DomainResponse) methodSignature.getMethod().getReturnType().newInstance();
			convertMapToMessages(domainResponse, messages);
			AuditLogger.error(BaseHttpProviderAspect.getDefaultAuditableInstance(methodSignature.getMethod()),
					domainResponse.getMessages().stream().map(ServiceMessage::toString).reduce("", String::concat), null);
		}
		return domainResponse;
	}
}