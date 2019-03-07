package gov.va.ocp.framework.rest.provider.bre.rules;

import java.util.Set;

import org.springframework.http.HttpStatus;

import gov.va.ocp.framework.rest.provider.Message;

/**
 * The Interface MessagesToHttpStatusRule is the rule interface used in the MessagesToHttpStatusRulesEngine.
 *
 * @author jshrader
 */
@FunctionalInterface
public interface MessagesToHttpStatusRule {

	/**
	 * Eval.
	 *
	 * @param messagesToEval the messages to eval
	 * @return the HttpStatus
	 */
	HttpStatus eval(Set<Message> messagesToEval);
}
