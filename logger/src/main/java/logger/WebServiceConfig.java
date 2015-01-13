package logger;

import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.XsdSchema;

@EnableWs
@Configuration
@ComponentScan
public class WebServiceConfig extends WsConfigurerAdapter {
	@Bean
	public ServletRegistrationBean dispatcherServlet(ApplicationContext applicationContext) {
		MessageDispatcherServlet servlet = new MessageDispatcherServlet();
		servlet.setApplicationContext(applicationContext);
		servlet.setTransformWsdlLocations(true);

		return new ServletRegistrationBean(servlet, "/ws/*");
	}

	@Bean(name = "wsdlDefault")
	public DefaultWsdl11Definition defaultWsdl11Definition(XsdSchema loggerSchema) {
		DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
		wsdl11Definition.setPortTypeName("LoggerPort");
		wsdl11Definition.setLocationUri("/ws");
		wsdl11Definition.setTargetNamespace("http://test.org/logger");
		wsdl11Definition.setSchema(loggerSchema);
		return wsdl11Definition;
	}

	@Bean
	public XsdSchema loggerSchema() {
		return new SimpleXsdSchema(new ClassPathResource("logger.xsd"));
	}
}
