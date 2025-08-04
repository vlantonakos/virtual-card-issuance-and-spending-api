package com.cardplatform.integration;

import com.cardplatform.CardPlatformApplication;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("qa")
@Getter
@Setter
@SpringBootTest(classes = CardPlatformApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class BaseCardIntegrationTest {

    /**
     * The base URL for API endpoints.
     *
     * <p>This should be set by concrete test classes in their setup methods
     * to point to the appropriate API endpoint base path. Typically constructed
     * using the server port and API version path.</p>
     *
     * <p><strong>Example:</strong> {@code "http://localhost:8080/api/v1/cards"}</p>
     */
    private String baseUrl;

    /**
     * The randomly assigned server port for the test application.
     *
     * <p>Spring Boot automatically injects the actual port number that the
     * embedded server is running on. This is used to construct the base URL
     * for making HTTP requests to the application during tests.</p>
     *
     * <p>The random port allocation prevents conflicts when running tests
     * in parallel or in CI/CD environments where multiple test suites
     * might be executing simultaneously.</p>
     */
    @LocalServerPort
    private int serverPort;

    /**
     * TestRestTemplate for making HTTP requests during integration tests.
     *
     * <p>This is Spring Boot's test-specific HTTP client that is pre-configured
     * to work with the test application context. It provides methods for making
     * REST API calls (GET, POST, PUT, DELETE) and handles request/response
     * serialization automatically.</p>
     *
     * <p><strong>Key Features:</strong></p>
     * <ul>
     *   <li>Automatic JSON serialization/deserialization</li>
     *   <li>Built-in error handling and status code management</li>
     *   <li>Support for parameterized type references for generic types</li>
     *   <li>Integration with Spring Boot test configuration</li>
     * </ul>
     *
     * <p><strong>Usage Example:</strong></p>
     * <pre>{@code
     * ResponseEntity<CardDTO> response = getTestRestTemplate().exchange(
     *     baseUrl + "/cards/" + cardId,
     *     HttpMethod.GET,
     *     null,
     *     CardDTO.class
     * );
     * }</pre>
     */
    @Autowired
    private TestRestTemplate testRestTemplate;

    /**
     * Jackson ObjectMapper for JSON processing in tests.
     *
     * <p>This is the same ObjectMapper instance that the application uses,
     * ensuring consistent JSON serialization/deserialization behavior between
     * the application and tests. It's particularly useful for:</p>
     *
     * <ul>
     *   <li>Parsing JSON response strings into JsonNode objects</li>
     *   <li>Extracting specific values from JSON responses</li>
     *   <li>Converting between DTOs and JSON for test data preparation</li>
     *   <li>Validating JSON structure and content in assertions</li>
     * </ul>
     *
     * <p><strong>Usage Example:</strong></p>
     * <pre>{@code
     * String jsonResponse = response.getBody();
     * JsonNode jsonNode = getObjectMapper().readTree(jsonResponse);
     * String cardId = jsonNode.get("id").asText();
     * }</pre>
     */
    @Autowired
    private ObjectMapper objectMapper;

}