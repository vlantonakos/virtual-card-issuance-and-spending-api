package com.cardplatform.integration.manager;

import com.cardplatform.infrastructure.web.exception.ErrorResponse;
import lombok.AllArgsConstructor;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

@AllArgsConstructor
public class CardRequestManager {

    private final String baseUrl;
    private final TestRestTemplate testRestTemplate;

    /**
     * Makes a POST request to create a new card.
     *
     * @param dto The card DTO to be created
     * @param <T> The type of the DTO
     * @return ResponseEntity containing the created card's ID as a String
     */
    public <T> ResponseEntity<String> makePostRequest(final T dto) {
        HttpEntity<T> request = new HttpEntity<>(dto, createJsonHeaders());
        return testRestTemplate.exchange(baseUrl, HttpMethod.POST, request, String.class);
    }

    /**
     * Makes a POST request expecting error response.
     */
    public <T> ResponseEntity<ErrorResponse> makePostRequestError(final T dto) {
        HttpEntity<T> request = new HttpEntity<>(dto, createJsonHeaders());
        return testRestTemplate.exchange(
                baseUrl,
                HttpMethod.POST,
                request,
                ErrorResponse.class
        );
    }

    /**
     * Makes a GET request to fetch a card by ID.
     *
     * @param cardId The card identifier
     * @param typeReference Type reference for response deserialization
     * @param <T> The type of the response
     * @return ResponseEntity containing the card
     */
    public <T> ResponseEntity<T> makeGetRequest(final UUID cardId, final ParameterizedTypeReference<T> typeReference) {
        return testRestTemplate.exchange(
                baseUrl + "/" + cardId,
                HttpMethod.GET,
                null,
                typeReference
        );
    }

    /**
     * Makes a GET request with query parameters.
     */
    public <T> ResponseEntity<T> makeGetRequestWithParams(String path, String params,
                                                          ParameterizedTypeReference<T> typeReference) {
        return testRestTemplate.exchange(
                baseUrl + "/" + path + "?" + params,
                HttpMethod.GET,
                null,
                typeReference
        );
    }

    /**
     * Makes a GET request with path and type reference.
     */
    public <T> ResponseEntity<T> makeGetRequest(String path, ParameterizedTypeReference<T> typeReference) {
        return testRestTemplate.exchange(
                baseUrl + "/" + path,
                HttpMethod.GET,
                null,
                typeReference
        );
    }

    /**
     * Makes a POST request for card transactions (spend/topup).
     *
     * @param cardId The card identifier
     * @param dto The transaction DTO
     * @param operation The operation (spend/topup)
     * @param typeReference Type reference for response deserialization
     * @param <T> The type of the DTO
     * @param <R> The type of the response
     * @return ResponseEntity containing the updated card
     */
    public <T, R> ResponseEntity<R> makeTransactionRequest(final UUID cardId, final T dto, final String operation,
                                                           final ParameterizedTypeReference<R> typeReference) {
        HttpEntity<T> request = new HttpEntity<>(dto, createJsonHeaders());
        return testRestTemplate.exchange(
                baseUrl + "/" + cardId + "/" + operation,
                HttpMethod.POST,
                request,
                typeReference
        );
    }

    /**
     * Makes a PUT request to update card status (block/activate).
     *
     * @param cardId The card identifier
     * @param operation The operation (block/activate)
     * @param typeReference Type reference for response deserialization
     * @param <T> The type of the response
     * @return ResponseEntity containing the updated card
     */
    public <T> ResponseEntity<T> makeStatusUpdateRequest(final UUID cardId, final String operation,
                                                         final ParameterizedTypeReference<T> typeReference) {
        HttpEntity<Void> request = new HttpEntity<>(createJsonHeaders());
        return testRestTemplate.exchange(
                baseUrl + "/" + cardId + "/" + operation,
                HttpMethod.PUT,
                request,
                typeReference
        );
    }

    /**
     * Creates HTTP headers for JSON requests.
     *
     * @return HttpHeaders object with configured headers
     */
    private HttpHeaders createJsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

}