package service;

import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

import javax.xml.soap.*;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.util.Collections.*;

public class SendingService {

    public static final Logger LOGGER = Logger.getLogger(SendingService.class);

    public static final String SERVER_URI = "http://localhost:9000/ws";
    public static final String LOG_SERVER_URI = "http://localhost:8000/ws";

    public static final String NAMESPACE_URI = "http://spring.io/guides/gs-producing-web-service";

    private static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(4);

    public static String sendCodeToCompile(String source) {
        if (!StringUtils.hasText(source)) {
            return "You have no code to compile";
        }
        return sendMessage(source, SERVER_URI, "compileRequest", "source", UUID.randomUUID());
    }

    private static String sendMessage(String source, String serverUri, String methodName, String methodParam, UUID requestId) {
        SOAPConnection soapConnection = null;
        try {
            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
            // Create SOAP Connection
            soapConnection = soapConnectionFactory.createConnection();

            Map<String, String> params = new HashMap<String, String>();
            params.put(methodParam, source);
            params.put("requestId", requestId.toString());

            // Send SOAP Message to compiler service
            final SOAPMessage soapRequest = createSOAPRequest(methodName, params, serverUri);

            final String requestLogMessage = String.format("Sending request to compiler service (requestId = %s): %s",
                    requestId, messageToString(soapRequest));
            LOGGER.info(requestLogMessage);

            SOAPMessage logSoapRequest = createSOAPRequest("logRequest",
                    singletonMap("message", new Date() + " [CLIENT] " + requestLogMessage), LOG_SERVER_URI);
            EXECUTOR_SERVICE.submit(createLogMessageTask(logSoapRequest));

            // Receive SOAP response from compiler service
            SOAPMessage soapResponse = soapConnection.call(soapRequest, serverUri);

            String responseLogMessage = String.format("Receiving response from compiler service (requestId = %s): %s",
                    requestId, messageToString(soapResponse));

            SOAPMessage logSoapResponse = createSOAPRequest("logRequest",
                    singletonMap("message", new Date() + " [CLIENT] " + responseLogMessage), LOG_SERVER_URI);
            EXECUTOR_SERVICE.submit(createLogMessageTask(logSoapResponse));

            return soapResponse.getSOAPBody().getFirstChild().getFirstChild().getFirstChild().getTextContent();
        } catch (Throwable e) {
            LOGGER.error("" + e);
            return "Error occurred while sending SOAP Request to Server. Please contact technical staff.";
        } finally {
            if (soapConnection != null) {
                try {
                    soapConnection.close();
                } catch (SOAPException e) {
                }
            }
        }
    }

    /**
     * Creates task to log message on log server
     *
     * @param logSoapRequest request to log
     * @return
     */
    private static Runnable createLogMessageTask(final SOAPMessage logSoapRequest) {
        return new Runnable() {
            @Override
            public void run() {

                SOAPConnection soapConnection = null;
                try {
                    SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
                    soapConnection = soapConnectionFactory.createConnection();
                    soapConnection.call(logSoapRequest, LOG_SERVER_URI);
                } catch (Throwable e) {
                    LOGGER.error("" + e);
                } finally {
                    if (soapConnection != null) {
                        try {
                            soapConnection.close();
                        } catch (SOAPException e) {
                        }
                    }
                }
            }
        };
    }

    private static SOAPMessage createSOAPRequest(String methodName, Map<String, String> paramData, String serverUri) throws Exception {
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();

        // SOAP Envelope
        SOAPEnvelope envelope = soapPart.getEnvelope();
        envelope.addNamespaceDeclaration("gs", NAMESPACE_URI);

        // SOAP Body
        SOAPBody soapBody = envelope.getBody();
        SOAPElement root = soapBody.addChildElement(methodName, "gs");

        for (Map.Entry<String, String> methodParam : paramData.entrySet()) {
            SOAPElement source = root.addChildElement(methodParam.getKey(), "gs");
            source.addTextNode(methodParam.getValue());
        }

        MimeHeaders headers = soapMessage.getMimeHeaders();
        headers.addHeader("SOAPAction", serverUri + methodName);

        soapMessage.saveChanges();

        return soapMessage;
    }

    private static String messageToString(SOAPMessage soapResponse) throws Exception {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        Source sourceContent = soapResponse.getSOAPPart().getContent();

        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        transformer.transform(sourceContent, result);

        return writer.toString();
    }
}
