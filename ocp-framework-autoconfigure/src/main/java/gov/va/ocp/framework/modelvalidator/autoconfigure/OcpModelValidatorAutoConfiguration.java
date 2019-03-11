package gov.va.ocp.framework.modelvalidator.autoconfigure;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.context.MessageSourceAutoConfiguration;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import gov.va.ocp.framework.validation.ModelValidator;


/**
 * Created by rthota on 8/24/17.
 */

@Configuration
@AutoConfigureBefore(MessageSourceAutoConfiguration.class)
public class OcpModelValidatorAutoConfiguration {

    /**
     * Model validator.
     *
     * @return the model validator
     */
    @Bean
    @ConditionalOnMissingBean
    public ModelValidator modelValidator(){
        return new ModelValidator();
    }
    
    /**
     * Validator.
     *
     * @param messageSource the message source
     * @return the local validator factory bean
     */
    @Bean
    @ConditionalOnMissingBean
    public LocalValidatorFactoryBean validator(MessageSource messageSource) {
        LocalValidatorFactoryBean validatorFactoryBean = new LocalValidatorFactoryBean();
        validatorFactoryBean.setValidationMessageSource(messageSource);
        return validatorFactoryBean;
    }
}


