package com.coding.exercise.bankapp.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI 3.x metadata and server configuration for the BankApp REST API.
 *
 * <p>This configuration is discovered automatically by {@code springdoc-openapi}
 * via classpath scanning. It enriches the generated OpenAPI document with:
 * <ul>
 *   <li>Human-friendly API metadata (title, description, version, contact, license).</li>
 *   <li>The correct server base URL that includes the servlet context path
 *       ({@code /bank-api}).</li>
 * </ul>
 *
 * <p>Exposed documentation endpoints (with {@code server.servlet.context-path=/bank-api}):
 * <ul>
 *   <li>OpenAPI JSON: {@code /bank-api/v3/api-docs}</li>
 *   <li>Swagger UI: {@code /bank-api/swagger-ui/index.html}
 *       (with {@code /bank-api/swagger-ui.html} redirect support)</li>
 * </ul>
 *
 * <p>Security configuration (see {@link com.coding.exercise.bankapp.config.SecurityConfig})
 * permits these documentation endpoints without authentication.
 */
@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "BankApp API",
        description = "RESTful Spring Boot banking API providing customer, account, and transaction "
                    + "management backed by an in-memory H2 database.",
        version = "1.0.0",
        contact = @Contact(
            name = "BankApp Team"
        ),
        license = @License(
            name = "MIT License",
            url = "https://opensource.org/licenses/MIT"
        )
    ),
    servers = {
        @Server(
            url = "/bank-api",
            description = "BankApp API base path (servlet context-path)"
        )
    }
)
public class OpenApiConfig {
    /*
     * No explicit @Bean definitions are required here. The presence of this
     * configuration class and its {@link OpenAPIDefinition} annotation is
     * sufficient for springdoc-openapi to augment the generated OpenAPI document
     * with the provided metadata and server information.
     */
}
