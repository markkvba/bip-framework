package gov.va.bip.framework.log.logback;

import ch.qos.logback.classic.spi.ILoggingEvent;
import com.fasterxml.jackson.core.JsonGenerator;
import net.logstash.logback.composite.JsonWritingUtils;
import net.logstash.logback.composite.loggingevent.MessageJsonProvider;

import java.io.IOException;

/**
 * The Class BipMaskingMessageProvider.
 */
public class BipMaskingMessageProvider extends MessageJsonProvider {

	/** The rules. */
	private BipMaskRules rules;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.logstash.logback.composite.loggingevent.MessageJsonProvider#writeTo(
	 * com.fasterxml.jackson.core.JsonGenerator,
	 * ch.qos.logback.classic.spi.ILoggingEvent)
	 */
	@Override
	public void writeTo(JsonGenerator generator, ILoggingEvent event) throws IOException {
		JsonWritingUtils.writeStringField(generator, getFieldName(), rules.apply(event.getFormattedMessage()));
	}

	/**
	 * Sets the rules.
	 *
	 * @param rules
	 *            the new rules
	 */
	public void setRules(BipMaskRules rules) {
		this.rules = rules;
	}
}
