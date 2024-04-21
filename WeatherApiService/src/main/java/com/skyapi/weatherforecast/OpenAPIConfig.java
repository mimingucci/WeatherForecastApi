package com.skyapi.weatherforecast;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class OpenAPIConfig {

	  @Value("${mimingucci.openapi.dev-url}")
	  private String devUrl;

//	  @Value("${mimingucci.openapi.prod-url}")
//	  private String prodUrl;

	  @Bean
	  public OpenAPI myOpenAPI() {
	    Server devServer = new Server();
	    devServer.setUrl(devUrl);
	    devServer.setDescription("Server URL in Development environment");

//	    Server prodServer = new Server();
//	    prodServer.setUrl(prodUrl);
//	    prodServer.setDescription("Server URL in Production environment");

	    Contact contact = new Contact();
	    contact.setEmail("gtvvunguye@gmail.com");
	    contact.setName("mimingucci");
	    contact.setUrl("https://github.com/mimingucci");
	    License mitLicense = new License().name("MIT License").url("https://choosealicense.com/licenses/mit/");

	    Info info = new Info()
	        .title("Weather Forecast Apis")
	        .version("1.0")
	        .contact(contact)
	        .description("This API exposes endpoints to see upcoming weather at specific locations").termsOfService("https://github.com/mimingucci")
	        .license(mitLicense);

	    return new OpenAPI().info(info).servers(List.of(devServer));
	  }
}
