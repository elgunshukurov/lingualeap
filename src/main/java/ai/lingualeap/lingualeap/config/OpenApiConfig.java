package ai.lingualeap.lingualeap.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI linguaLeapOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("LinguaLeap API")
                        .description("AI-powered language learning assistant API documentation")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("LinguaLeap Team")
                                .email("support@lingualeap.ai")
                                .url("https://lingualeap.ai"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://www.apache.org/licenses/LICENSE-2.0.html")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Development server"),
                        new Server().url("https://api.lingualeap.ai").description("Production server")
                ));
    }
}
