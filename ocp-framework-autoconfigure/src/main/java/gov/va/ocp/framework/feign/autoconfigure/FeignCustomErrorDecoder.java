package gov.va.ocp.framework.feign.autoconfigure;

import java.io.IOException;
import java.io.Reader;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import feign.Response;
import feign.codec.ErrorDecoder;
import gov.va.ocp.framework.exception.OcpFeignRuntimeException;

/**
 * The Class FeignCustomErrorDecoder.
 */
public class FeignCustomErrorDecoder implements ErrorDecoder {

	private final ErrorDecoder defaultErrorDecoder = new Default();

	/* (non-Javadoc)
	 * @see feign.codec.ErrorDecoder#decode(java.lang.String, feign.Response)
	 */
	@Override
	public Exception decode(String methodKey, Response response) {
		if (response.status() >= 400 && response.status() <= 499) {

			StringBuffer strBuffer = new StringBuffer();
			try {

				Reader inputReader = response.body().asReader();
				int data = inputReader.read();
				while(data != -1) {
					strBuffer.append((char)data);
					data = inputReader.read();
				}

			} catch (IOException e) {
				return defaultErrorDecoder.decode(methodKey, response);
			}

			try {
				JSONObject messageObjects = new JSONObject(strBuffer.toString());
				JSONArray jsonarray = messageObjects.getJSONArray("messages");
				JSONObject messageObject = jsonarray.getJSONObject(0);
				return new OcpFeignRuntimeException(messageObject.getString("key"), messageObject.getString("text"),
						messageObject.getString("status"), messageObject.getString("severity"));

			} catch (JSONException e) {
				return defaultErrorDecoder.decode(methodKey, response);
			}

		}
		return defaultErrorDecoder.decode(methodKey, response);
	}

}