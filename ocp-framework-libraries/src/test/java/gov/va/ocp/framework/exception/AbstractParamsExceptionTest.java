package gov.va.ocp.framework.exception;

import org.junit.Assert;
import org.junit.Test;

import gov.va.ocp.framework.exception.AbstractParamsException;

public class AbstractParamsExceptionTest {


	@SuppressWarnings("serial")
	class InnerException extends AbstractParamsException{

		public InnerException(String message, Throwable cause) {
			super(message, cause);
	
		}
				
	}

	@Test
	public void instantiateException() throws Exception {
		AbstractParamsException abstractParamsException = new AbstractParamsException("Test", null);

		Assert.assertNotNull(abstractParamsException);
	}


	@SuppressWarnings("deprecation")
	@Test
	public void instantiateParamsException() throws Exception {
		InnerException innerException = new InnerException("Test Inner", null);

        innerException.setParamCount(1);
        Assert.assertEquals(1, innerException.getParamCount().intValue());
        
        innerException.setParamNames(new String[] {"1"});
        Assert.assertEquals(new String[] {"1"}, innerException.getParamNames());
        
        innerException.setParamValues(new String[] {"1"});
        Assert.assertEquals(new String[] {"1"}, innerException.getParamValues());
	}
	
}
