package logger;

import io.spring.guides.gs_producing_web_service.LogRequest;
import org.apache.log4j.Logger;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class LogEndpoint {

	private static final String NAMESPACE_URI = "http://spring.io/guides/gs-producing-web-service";

	public static final Logger LOGGER = Logger.getLogger(LogEndpoint.class);

	/**
	 * Simply logging requests via log4j now
	 * @param request request to log
	 */
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "logRequest")
	@ResponsePayload
	public void logRequest(@RequestPayload LogRequest request) {
		LOGGER.info(request.getMessage());
	}
}
