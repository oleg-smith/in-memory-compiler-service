package service;

import compiler.InMemoryCompiler;
import io.spring.guides.gs_producing_web_service.CompileRequest;
import io.spring.guides.gs_producing_web_service.CompileResponse;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class CompilerEndpoint {

	private static final String NAMESPACE_URI = "http://spring.io/guides/gs-producing-web-service";

	public static final Logger LOGGER = Logger.getLogger(CompilerEndpoint.class);

	@Autowired
	private InMemoryCompiler compiler;

	@Autowired
	private SendingService sendingService;

	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "compileRequest")
	@ResponsePayload
	public CompileResponse compileSource(@RequestPayload CompileRequest request) {

		String requestLogMessage = String.format("Receiving request from client (requestId = %s): %s",
				request.getRequestId(), request.getSource());
		LOGGER.info(requestLogMessage);

		sendingService.sendLogMessage(requestLogMessage);

		String compileResults = compiler.compile(request.getSource());

		String responseLogMessage = String.format("Sending response to client (requestId = %s): %s",
				request.getRequestId(), request.getSource());
		LOGGER.info(responseLogMessage);

		sendingService.sendLogMessage(responseLogMessage);

		CompileResponse response = new CompileResponse();
		response.setResult(compileResults);

		return response;
	}
}
